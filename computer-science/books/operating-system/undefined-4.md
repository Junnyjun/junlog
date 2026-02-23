# 동기화도구

현대 운영체제는 여러 프로세스(또는 스레드)가 동시에 실행됩니다.\
이때 \*\*공유 데이터(shared data)\*\*에 동시에 접근하면 \*\*불일치(inconsistency)\*\*가 발생할 수 있습니다.

**예시**: 두 프로세스가 같은 변수 `counter`를 증가시키는 경우

* 프로세스 A: `counter++` (레지스터에 읽기 → 증가 → 쓰기)
* 프로세스 B: 동시에 같은 작업 수행 → 최종 값이 예상보다 작아짐 (Race Condition)

**Race Condition (경쟁 조건)**: 여러 프로세스가 공유 데이터를 동시에 접근하고, 최종 결과가 접근 순서에 따라 달라지는 상황입니다.

**Critical-Section Problem (임계 구역 문제)**:\
여러 프로세스가 공유 데이터를 접근할 때, \*\*임계 구역(critical section)\*\*이라고 불리는 코드 영역에서 한 번에 하나의 프로세스만 실행되도록 보장해야 합니다.

**임계 구역 문제 해결을 위한 3가지 요구 조건** (모두 만족해야 함):

1. **Mutual Exclusion (상호 배제)**: 한 프로세스가 임계 구역에 있으면 다른 프로세스는 들어갈 수 없다.
2. **Progress (진행)**: 임계 구역에 들어갈 프로세스가 없으면, 들어가고자 하는 프로세스 중 하나는 반드시 들어갈 수 있어야 한다.
3. **Bounded Waiting (한정된 대기)**: 한 프로세스가 임계 구역에 들어가기 위해 대기하는 횟수에 상한이 있어야 한다 (기아 방지).

#### 6.2 The Critical-Section Problem (임계 구역 문제)

임계 구역 문제는 **소프트웨어 솔루션**, **하드웨어 지원**, **추상화 도구**로 해결할 수 있습니다.

**소프트웨어 솔루션의 어려움**:

* 초기에는 `turn` 변수, `flag` 배열 등을 사용했으나, **progress**나 **bounded waiting** 조건을 모두 만족시키기 매우 어려웠습니다.

#### 6.3 Peterson’s Solution (피터슨의 해결책)

두 프로세스만 있는 경우에 대한 **고전적인 소프트웨어 솔루션**입니다.

**알고리즘** (C 언어 스타일):

```c
do {
    flag[i] = true;          // 내가 들어가고 싶다
    turn = j;                // 상대방에게 양보
    while (flag[j] && turn == j);   // 상대방이 들어가고 싶고 내 차례가 아니면 대기
    
    // Critical Section
    ...
    
    flag[i] = false;         // 나갔다
    // Remainder Section
} while (true);
```

**특징**:

* Mutual Exclusion, Progress, Bounded Waiting 모두 만족
* **단점**: Busy Waiting (spinlock) → CPU를 계속 사용 (낭비)

#### 6.4 Hardware Support for Synchronization (하드웨어 지원)

소프트웨어 솔루션은 실용적이지 않습니다. 현대 시스템은 **하드웨어 수준**에서 원자적(atomic) 연산을 제공합니다.

**6.4.1 Test-and-Set Instruction**

```c
boolean test_and_set(boolean *target) {
    boolean rv = *target;
    *target = true;
    return rv;
}
```

* 원자적으로 실행됨
* Mutex 구현에 사용

**6.4.2 Compare-and-Swap (CAS) Instruction**

가장 중요한 현대 하드웨어 명령어입니다.

```c
int compare_and_swap(int *value, int expected, int new_value) {
    int temp = *value;
    if (temp == expected)
        *value = new_value;
    return temp;
}
```

**Lock-free 알고리즘**과 **wait-free 알고리즘**의 기초가 됩니다.

**메모리 모델과 재정렬 문제** (10판 새 내용):

* 현대 CPU는 **instruction reordering**과 **delayed writes**를 수행
* **Memory Barrier (메모리 장벽)** 명령어가 필요
* **Sequential Consistency** vs **Relaxed Memory Models**

#### 6.5 Mutex Locks (뮤텍스 락)

가장 간단한 동기화 도구입니다.

```c
// Acquire
while (test_and_set(&lock));   // spinlock

// Critical Section

// Release
lock = false;
```

**장점**: 간단\
**단점**: Busy Waiting (CPU 낭비) → Spinlock

현대 시스템은 **Adaptive Mutex**를 사용 (스핀하다가 일정 시간 후 sleep)

#### 6.6 Semaphores (세마포어)

\*\*세마포어(Semaphore)\*\*는 정수 변수 S로, 두 가지 원자적 연산으로만 접근합니다.

* `wait()` (P 연산, down)
* `signal()` (V 연산, up)

**이진 세마포어 (Binary Semaphore)**: 0 또는 1 → Mutex와 동일\
**카운팅 세마포어 (Counting Semaphore)**: 자원 개수 관리

**고전적인 구현** (busy waiting):

```c
wait(S) {
    while (S <= 0);   // busy wait
    S--;
}

signal(S) {
    S++;
}
```

**세마포어로 해결하는 고전 문제** (다음 장에서 상세):

* Producer-Consumer Problem
* Readers-Writers Problem
* Dining Philosophers Problem

#### 6.7 Monitors (모니터)

\*\*모니터(Monitor)\*\*는 고수준 추상화 도구입니다.\
조건 변수(condition variable) `condition x;`와 `x.wait()`, `x.signal()`을 가집니다.

**Java에서의 모니터**:

```java
public synchronized void method() {
    while (!condition)
        wait();
    ...
    notifyAll();
}
```

**장점**: 상호 배제와 조건 대기를 언어 수준에서 자동 제공\
**단점**: 모든 언어가 지원하지 않음

#### 6.8 Liveness (생존성)

**Liveness**는 프로세스가 결국 진행될 것이라는 보장입니다.

**문제 유형**:

* **Deadlock**: 서로 기다리며 영원히 진행 불가
* **Starvation**: 낮은 우선순위 프로세스가 영원히 대기
* **Livelock**: 상태는 계속 변하지만 유용한 작업은 진행되지 않음

#### 6.9 Evaluation (평가)

* **Busy Waiting** vs **Blocking** (sleep/wakeup)
* **Spinlock**의 장점 (짧은 임계 구역에서 유리)
* **Lock-free & Wait-free 알고리즘**의 장점 (progress 보장)
* **성능 vs 복잡도 trade-off**
