# HikariCP의 동시성 처리

HikariCP의 놀라운 성능 비결 중 하나는 특별히 설계된 고성능 데이터 구조들입니다. HikariCP의 심장부라 할 수 있는 ConcurrentBag 클래스를 분석하며, 어떻게 멀티스레드 환경에서 데이터베이스 커넥션을 효율적으로 관리하는지 알아보겠습니다.

### ConcurrentBag의 역할

ConcurrentBag은 HikariCP에서 가장 중요한 자료구조 중 하나로, 다음과 같은 핵심적인 역할을 담당합니다:

* 데이터베이스 커넥션 풀의 관리
* 동시 접근 환경에서 커넥션의 대여(borrow)와 반환(return) 처리
* 커넥션 상태 추적 및 관리
* 성능 최적화된 동시성 제어

ConcurrentBag은 일반적인 컬렉션 인터페이스를 구현하지 않는 특별한 목적의 자료구조입니다. \
대신 HikariCP의 요구사항에 특화되어 설계되었습니다.

### 핵심 개념과 인터페이스

ConcurrentBag은 크게 두 가지 핵심 인터페이스를 중심으로 설계되어 있습니다

#### IConcurrentBagEntry 인터페이스

이 인터페이스는 ConcurrentBag에 저장되는 모든 항목이 구현해야 하는 계약을 정의합니다.&#x20;

```java
public interface IConcurrentBagEntry {
   boolean compareAndSet(int expectState, int newState);
   int getState();
}
```

이 인터페이스를 통해 ConcurrentBag은 원자적 방식으로 항목의 상태를 변경하고 추적할 수 있습니다.

#### ConcurrentBag의 핵심 메서드

ConcurrentBag은 다음과 같은 핵심 메서드를 제공합니다:

* `borrow()`: 사용 가능한 항목을 대여하여 반환합니다
* `requite(T bagEntry)`: 항목을 풀에 반환합니다
* `add(T bagEntry)`: 새 항목을 풀에 추가합니다
* `remove(T bagEntry)`: 항목을 풀에서 제거합니다

### 내부 구조와 구현

ConcurrentBag의 내부는 여러 최적화된 데이터 구조를 사용하여 구현되어 있습니다:

#### 상태 추적

ConcurrentBag은 다음과 같은 상태 값을 사용하여 각 항목의 상태를 추적합니다:

* : 항목이 유휴 상태이며 사용 가능함 `STATE_NOT_IN_USE`
* : 항목이 현재 사용 중임 `STATE_IN_USE`
* : 항목이 제거되었음 `STATE_REMOVED`
* : 항목이 예약됨 (다른 스레드가 곧 사용할 예정) `STATE_RESERVED`

#### 내부 컬렉션

ConcurrentBag은 여러 내부 컬렉션을 사용하여 항목을 효율적으로 관리합니다:

```java
private final CopyOnWriteArrayList<T> sharedList;
private final ThreadLocal<List<Object>> threadList;
private final ConcurrentBag.IBagStateListener listener;
private final AtomicInteger waiters;
private final SynchronousQueue<T> handoffQueue;
```

* : 모든 항목을 저장하는 스레드 안전한 리스트 `sharedList`
* `threadList`: 스레드 로컬 캐시로, 각 스레드가 자신이 사용한 항목을 추적
* : 현재 대기 중인 스레드 수를 추적하는 원자적 카운터 `waiters`
* : 항목을 대기 중인 스레드에게 직접 전달하기 위한 동기화 큐 `handoffQueue`

### 핵심 알고리즘: borrow()

ConcurrentBag의 가장 중요한 메서드인 `borrow()`는 다음과 같은 최적화된 알고리즘을 통해 구현됩니다:

```java
public T borrow(long timeout, final TimeUnit timeUnit) throws InterruptedException {
   // 1. 스레드 로컬 리스트에서 먼저 사용 가능한 항목을 찾음
   final var threadLocalList = threadList.get();
   for (int i = threadLocalList.size() - 1; i >= 0; i--) {
      final var entry = (T) threadLocalList.get(i);
      if (entry.compareAndSet(STATE_NOT_IN_USE, STATE_IN_USE)) {
         return entry;
      }
   }

   // 2. 공유 리스트에서 사용 가능한 항목을 찾음
   final var waiting = waiters.incrementAndGet();
   try {
      for (T entry : sharedList) {
         if (entry.compareAndSet(STATE_NOT_IN_USE, STATE_IN_USE)) {
            return entry;
         }
      }

      // 3. 항목을 찾지 못했다면, 제한 시간 동안 대기
      listener.addBagItem(this);
      timeout = timeUnit.toNanos(timeout);
      do {
         final var start = currentTime();
         final T bagEntry = handoffQueue.poll(timeout, NANOSECONDS);
         if (bagEntry != null || (timeout = timeout - (currentTime() - start)) <= 0) {
            return bagEntry;
         }
      } while (waiters.get() > 0);
   } finally {
      waiters.decrementAndGet();
   }

   return null;
}
```

이 알고리즘은 다음과 같은 최적화를 포함합니다:

1. **지역성 최적화**: 스레드 로컬 리스트를 먼저 확인하여 CPU 캐시 히트율을 높입니다.
2. **대기 최소화**: 사용 가능한 항목을 찾기 위해 적극적으로 탐색합니다.
3. **직접 전달 메커니즘**: 를 통해 반환된 항목을 대기 중인 스레드에게 직접 전달합니다. `handoffQueue`

### requite(): 항목 반환 최적화

항목을 풀에 반환하는 `requite()` 메서드도 성능 최적화에 중점을 두고 있습니다:

```java
public void requite(final T bagEntry) {
   bagEntry.setState(STATE_NOT_IN_USE);
   
   // 대기 중인 스레드가 있다면 직접 항목을 전달
   if (waiters.get() > 0) {
      handoffQueue.offer(bagEntry);
   }
   
   // 스레드 로컬 리스트에 항목 추가 (재사용을 위해)
   threadList.get().add(bagEntry);
}
```

이 메서드의 주요 최적화 포인트는:

1. **직접 전달**: 대기 중인 스레드가 있으면 항목을 즉시 전달하여 대기 시간을 최소화합니다.
2. **지역성 활용**: 반환된 항목을 스레드 로컬 리스트에 추가하여 동일한 스레드가 재사용할 가능성을 높입니다.

### ConcurrentBag의 성능 이점

ConcurrentBag은 다음과 같은 이유로 다른 동시성 컬렉션보다 훨씬 뛰어난 성능을 제공합니다:

#### 락 사용 최소화

ConcurrentBag은 항목의 상태 변경에 무거운 동기화 블록 대신 과 같은 원자적 연산을 사용합니다. 이는 경합을 크게 줄이고 멀티코어 시스템에서의 확장성을 향상시킵니다. `compareAndSet`

#### 스레드 지역성 활용

스레드 로컬 캐시를 사용하여 항목의 스레드 친화도(thread affinity)를 높입니다. 이는 CPU 캐시의 효율성을 향상시키고 메모리 접근 비용을 줄입니다.

#### 적극적 핸드오프 메커니즘

반환된 항목을 대기 중인 스레드에게 직접 전달하는 핸드오프 메커니즘은 스레드 간 항목 전달의 지연 시간을 최소화합니다.

#### 상태 머신 접근 방식

각 항목을 상태 머신으로 모델링하고 원자적 상태 전이를 통해 일관성을 유지하면서도 높은 처리량을 제공합니다.

### 실제 사용 시나리오

HikariCP에서 ConcurrentBag의 실제 사용 시나리오를 살펴보겠습니다:

#### 커넥션 풀 관리

ConcurrentBag은 HikariPool 클래스에서 데이터베이스 커넥션 풀을 관리하는 데 사용됩니다:

```java
private final ConcurrentBag<PoolEntry> connectionBag = new ConcurrentBag<>(this);
```

#### 커넥션 획득 및 반환

애플리케이션이 커넥션을 요청하면 HikariPool은 ConcurrentBag의 borrow() 메서드를 사용하여 사용 가능한 커넥션을 찾아 반환합니다:

```java
public Connection getConnection(long timeout) throws SQLException {
   final var poolEntry = connectionBag.borrow(timeout, MILLISECONDS);
   if (poolEntry == null) {
      throw new SQLTransientConnectionException("Connection is not available");
   }
   
   return poolEntry.createProxyConnection();
}
```

커넥션이 닫히면 HikariPool은 ConcurrentBag의 requite() 메서드를 사용하여 커넥션을 풀에 반환합니다.

### ConcurrentBag vs 기존 동시성 컬렉션

ConcurrentBag을 다른 동시성 컬렉션과 비교해보겠습니다:

#### ConcurrentBag vs BlockingQueue

* **BlockingQueue** : 생산자-소비자 패턴에 최적화되어 있으며, FIFO(선입선출) 시맨틱을 제공합니다.&#x20;
* **ConcurrentBag** : 특정 순서가 없으며, 스레드 지역성과 최소 대기 시간에 최적화되어 있습니다.&#x20;

#### ConcurrentBag vs ConcurrentLinkedQueue

* **ConcurrentLinkedQueue**: 락 없는 FIFO 큐로, 추가와 제거 작업에 최적화되어 있습니다.
* **ConcurrentBag** : 부하가 높은 환경에서 더 효율적인 항목 재사용과 직접 핸드오프를 제공합니다.&#x20;

### 성능 최적화 기법 요약

ConcurrentBag에 사용된 주요 성능 최적화 기법들을 정리해보면:

#### 비차단 동시성 제어

락 대신 원자적 연산(AtomicInteger, compareAndSet)을 사용하여 경합을 최소화합니다.

#### 데이터 지역성 최적화

ThreadLocal을 사용하여 CPU 캐시 활용도를 높이고 메모리 접근 패턴을 개선합니다.

#### 지연 시간 감소 전략

대기 중인 스레드에게 직접 항목을 전달하는 핸드오프 메커니즘과 능동적인 폴링을 통해 지연 시간을 최소화합니다.

#### 상태 관리 효율화

상태 전이를 원자적으로 관리하고 불필요한 상태 체크를 최소화합니다.
