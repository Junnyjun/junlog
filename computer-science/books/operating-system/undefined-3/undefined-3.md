# 암시적 스레딩

다음은 운영체제 이론서의 **Implicit Threading** 절에 대한 상세 정리입니다. 원문 구조에 맞춰 **섹션 단위로 순차적 흐름**을 유지했으며, 모든 키 포인트와 예시를 세부적으로 설명하였습니다.

***

#### Implicit Threading

멀티코어 환경이 일반화되면서 수백\~수천 개의 스레드를 포함하는 애플리케이션 설계가 요구된다. 그러나 이처럼 많은 수의 스레드를 **프로그래머가 명시적으로 관리하는 방식은 비효율적이고 오류 가능성이 높다**. 이러한 문제를 해결하기 위해 등장한 것이 **암시적 스레딩 (Implicit Threading)** 이다.

**개념 요약:**

* **암시적 스레딩**은 스레드 생성과 관리를 **개발자가 아닌 컴파일러 또는 런타임 라이브러리**가 담당하는 방식이다.
* 개발자는 \*\*"스레드"가 아닌 "작업(task)"\*\*을 정의하면, 런타임 시스템이 해당 작업을 스레드에 자동 매핑함.
* 주로 **many-to-many 스레드 모델**을 사용.

***

### 주요 암시적 스레딩 기술 5가지

***

#### 1. Thread Pools

**개요:**

* **스레드를 요청할 때마다 새로 생성**하는 방식은 **지연 및 리소스 낭비** 문제가 있다.
* 대신, **초기 시작 시 일정 개수의 스레드를 생성하여 풀(pool)에 저장**하고, 작업이 오면 기존 스레드를 재사용.

**장점:**

1. **스레드 생성 비용 절감** – 재사용하므로 빠름.
2. **동시 실행 스레드 수 제한** – 자원 고갈 방지.
3. **작업 생성과 실행 분리** – 스케줄링 전략 다양화 가능 (지연 실행, 주기 실행 등).

**예시 – Java:**

```java
ExecutorService pool = Executors.newFixedThreadPool(4);
pool.execute(new Task());
pool.shutdown();
```

**예시 – Windows:**

```c
QueueUserWorkItem(&PoolFunction, NULL, 0);
```

***

#### 2. Fork-Join

**개요:**

* 작업을 **작게 분할(fork)** → 하위 작업에서 각각 처리 → 결과를 **합침(join)**.
* 분할 정복 알고리즘(Quicksort, Mergesort)과 함께 사용됨.
* Java 1.7부터 `ForkJoinPool`, `RecursiveTask`, `RecursiveAction` 등 제공.

**Java 구조 예시:**

```java
ForkJoinPool pool = new ForkJoinPool();
int result = pool.invoke(new SumTask(0, SIZE - 1, array));
```

```java
// compute() 내부
if (작은 작업) {
  직접 계산
} else {
  left.fork(); right.fork();
  return left.join() + right.join();
}
```

**특징:**

* 내부적으로 **작업 큐와 work stealing 기법**을 사용해 부하 분산.
* 병렬성 확보는 `THRESHOLD` 크기 조절로 최적화.

***

#### 3. OpenMP

**개요:**

* C, C++, FORTRAN에서 사용하는 **컴파일러 지시어 기반 병렬화 라이브러리**.
* OpenMP는 **공유 메모리 환경**에서 병렬화 코드를 쉽게 작성 가능하게 함.

**문법 예시:**

```c
#pragma omp parallel
{
    printf("I am a parallel region.");
}
```

```c
#pragma omp parallel for
for (int i = 0; i < N; i++) {
    c[i] = a[i] + b[i];
}
```

**특징:**

* 지시어 기반이므로 코드 변경 없이 병렬화 추가 가능.
* **스레드 수, 데이터 공유 범위, 스케줄링 방식** 설정 가능.

***

#### 4. Grand Central Dispatch (GCD) – macOS/iOS

**개요:**

* Apple이 개발한 **작업 기반 비동기 병렬 처리 기술**.
* **큐 기반 작업 스케줄링 시스템**: 작업을 큐에 넣으면 스레드 풀에서 적절히 실행.

**큐 종류:**

* **Serial Queue**: 작업을 FIFO 순서로 순차 실행
* **Concurrent Queue**: 작업을 병렬로 동시에 실행

**QoS (Quality-of-Service) 클래스:**

* `USER_INTERACTIVE`: UI 반응성 필요
* `USER_INITIATED`: 사용자 트리거 동작
* `UTILITY`: 시간 오래 걸리는 작업 (예: 데이터 가져오기)
* `BACKGROUND`: 사용자 비가시적 작업 (예: 인덱싱, 백업)

**Swift 예시:**

```swift
let queue = dispatch_get_global_queue(QOS_CLASS_USER_INITIATED, 0)
dispatch_async(queue) {
    print("I am a closure.")
}
```

***

#### 5. Intel Thread Building Blocks (TBB)

**개요:**

* **C++ 병렬 애플리케이션 개발을 위한 템플릿 기반 라이브러리**.
* 캐시 친화적이며, 작업-스레드 매핑 및 부하 분산 자동 수행.

**특징:**

* 병렬 루프, 원자 연산, 락, 동시 자료구조 제공 (concurrent queue, map, vector 등).
* 플랫폼 독립적으로 스레드 개수나 하드웨어 변경 시 코드 재작성 불필요.

**예시 – `parallel_for`:**

```cpp
parallel_for(size_t(0), n, [=](size_t i) {
    apply(v[i]);
});
```

* 반복 구간을 여러 청크로 나눠 스레드가 병렬 처리.
