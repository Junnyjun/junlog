# 명시적인 락

ReentrantLock 은 자바5.0 에서 추가됐으며 암묵적인 락으로 할 수 없는 고급 기능을 가지고 있다.

#### &#x20;Lock 과 ReentrantLock

Lock 인터페이스는 조건없는 락, 폴링 락, 타임아웃이 있는 락, \
&#x20;락 확보 대기상태에 인터럽트를 걸 수 있는 기능을 가진다.

모든 작업이 명시적이다.

```java
public interface Lock {
       void lock();
       void lockInterruptibly() throws InterruptedException();
       boolean tryLock();
       boolean tryLock( long timeout, TimeUnit unit() throws InterruptedException();
       void unlock();
       Condition newCondition();
}
```

ReentrantLock 은 Lock 인터페이스를 구현한다. synchronized 구문과 동일한 메모리 가시성과 상호 배제 기능을 제공한다.

```java
Lock lock = new ReentrantLock();
lock.lock();
try {
     // do something           
} finally {
     lock.unlock();
}
```

ReentrantLock 은 finally 구문에서 반드시 락을 해제해 주어야 한다.

#### 폴링과 시간 제한이 있는 락 확보 방법

락을 확보할 때 시간제한을 두거나 폴링 방법(trylock)을 사용하면 락을 확보하지 못하는 상황에도 통제권을 다시 얻을 수 있다.

```
public boolean trySendOnSharedLine(String message, long timeout, TimeUnit unit) throws InterruptedException {
      long nanoToLock = unit.toNanos(timeout) - estimateNanosToSend(message);
      if (!lock.tryLock(nanosToLock, NANOSECONDS))
             return false;
      try {
             return sendOnSharedLine(message);
      } finally {
             lock.unlock();
      }
}
```

#### 인터럽트 걸 수 있는 락 확보 방법        &#x20;

```
public boolean sendOnSharedLine(String message) throws InteruptedException {
      lock.lockInterruptibly();
 
      try {
             return cancellableSendOnSharedLine(message);
      } finally {
             lock.unlock();
      }
}
```

#### 성능에 대한 고려

자바 5.0과 함께 ReentrantLock이 처음 소개됐을 때 암묵적인 락에 비해 훨씬 나은 성능을 보여줬다.

하지만 자바 6.0에서는 암묵적인 락과 ReentrantLock 의 성능 차이가 많이 줄었다.

#### 공정성

ReentrantLock 은 두 종류의 락 공정성 설정을 지원한다. \
&#x20; 불공정 unfair 방법 과 공정 fair 방법이다.

&#x20;생성자에서 Boolean 인자로 공정성 지정(디폴트는 불공정)

공정한 방법은 락을 확보하고자 하는 스레드는 항상 큐의 맨끝 대기열에 들어간다.\
&#x20; 불공정 방법은 락을 확보하려는 시점에 락이 사용중이라면 대기열에 들어가게 되고,\
&#x20; 확보하려는 찰나에 락이 해제되었다면 대기열에 대기중인 스레드를 뛰어넘어 락을 확보하게 된다.

대부분의 경우 공정하게 처리해서 얻는 장점보다 불공정하게 처리해서 얻는 성능상 이점이 더 크다. \
&#x20; 왜냐하면 락을 원하는 스레드가 있을 때 바로 확보시키는 것이 대기 중인 스레드를 찾아\
&#x20; 확보시키는 것보다 빠르기 때문에.

#### synchronized 또는 ReentrantLock 선택&#x20;

그렇다면 ReentrantLock만 사용하고 synchronized는 사용할 필요가 없는 것일까?



**synchronized의 장점**

```
코드가 익숙하고 간결함
finally에서 락을 해제하지 않을 가능성이 없다.
```

ReentrantLock 은 암묵적인 락만으로는 해결할 수 없는 복잡한 상황에서 사용하기 위한 고급 동기화 기능이다.

&#x20;

다음과 같은 고급 동기화 기법을 사용해야 하는 경우에만 ReentrantLock을 사용하도록 하자

```
- 락을 확보할 때 타임아웃을 지정해야 하는 경우
- 폴링의 형태로 락을 확보하고자 하는 경우
- 락을 확보하느라 대기 상태에 들어가 있을 때 인터럽트를 걸 수 있어야 하는 경우
- 대기 상태 큐 처리 방법을 공정하게 해야 하는 경우
- 코드가 단일 블록의 형태를 넘어서는 경우
```

그 외의 경우에는 synchronized 블록을 사용하도록 하자!\
단순히 성능이 나아지길 기대하며 ReentrantLock을 사용하는 것은 좋지 않음.

#### &#x20;읽기-쓰기 락

읽기 작업은 여러 개를 한꺼번에 처리할 수 있지만 \
&#x20; 쓰기 작업은 혼자만 동작할 수 있는 구조의 동기화를 처리해 주는 \
&#x20; 락이 읽기-쓰기 락 read-write lock 이다.

대다수의 작업은 데이터 변경이 아닌 읽기 작업이다. \
&#x20; 이런 상황에서는 락의 조건을 풀어 읽기 연산은 여러 스레드에서 동시에\
&#x20; 실행할 수 있도록 해주면 성능을 크게 향상시킬 수 있다.

단, 데이터를 읽는 거나 쓰는 중에는 쓰지 못한다.\
읽기 락과 쓰기 락이 연관성없이 동작하는 것이 아니다.

```
public interface ReadWriteLock {
     Lock readLock();
     Lock writeLock();
}
```

멀티 프로세서 시스템에서 읽기 작업이 많고 쓰기 작업이 적은 구조에 사용하면 성능을 크게 높일 수 있음\
ReentrantReadWriteLock 클래스를 통해 읽기 락, 쓰기 락 모두 재진입 가능한 락 기능을 제공함.
