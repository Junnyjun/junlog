# 스레드와 동시성

\*\*스레드(Thread)\*\*는 프로세스 내에서 실행되는 \*\*경량 프로세스(lightweight process)\*\*입니다.\
전통적인 프로세스는 하나의 실행 단위(단일 스레드)였지만, 현대 운영체제는 **하나의 프로세스 안에 여러 개의 스레드**를 동시에 실행할 수 있게 설계되었습니다.

**멀티스레딩의 주요 장점 (Benefits of Multithreading)**

1. **응답성 향상 (Responsiveness)**\
   한 스레드가 긴 I/O 작업이나 네트워크 대기를 하고 있어도, 다른 스레드가 계속 실행되어 사용자에게 빠른 응답을 제공합니다. 특히 GUI 프로그램과 웹 서버에서 매우 중요합니다.
2. **자원 공유 (Resource Sharing)**\
   같은 프로세스에 속한 스레드들은 **코드 섹션, 데이터 섹션, 열린 파일, 신호 처리기** 등을 공유합니다. 프로세스 간 공유보다 훨씬 효율적입니다.
3. **경제성 (Economy)**\
   프로세스를 새로 생성하는 것보다 스레드를 생성하는 비용이 훨씬 적습니다.\
   Solaris 시스템에서 프로세스 생성은 스레드 생성보다 약 **30배** 느립니다. 메모리와 자원도 공유하므로 메모리 사용량도 절감됩니다.
4. **확장성 (Scalability)**\
   멀티코어 또는 멀티프로세서 시스템에서 여러 스레드가 서로 다른 코어에서 동시에 실행되어 \*\*진정한 병렬 처리(true parallelism)\*\*를 실현할 수 있습니다.

**스레드와 프로세스의 차이점 (Figure 4.1)**

* 프로세스: 독립된 주소 공간, 별도의 PCB, 자원
* 스레드: 같은 프로세스 내에서 \*\*스택과 레지스터(프로그램 카운터 포함)\*\*만 독립적으로 가짐. 나머지는 공유

#### 4.2 Multicore Programming (멀티코어 프로그래밍)

오늘날 대부분의 CPU는 \*\*멀티코어(multicore)\*\*입니다. 하나의 물리적 칩 안에 여러 개의 독립된 코어가 존재하며, 각 코어가 동시에 명령어를 실행할 수 있습니다.

**멀티코어 프로그래밍의 주요 과제**:

* **병렬성 식별**: 어떤 작업을 동시에 실행할 수 있는가?
* **데이터 분할**: 데이터를 어떻게 여러 코어에 나누어줄 것인가?
* **데이터 종속성 관리**: 한 스레드의 결과가 다른 스레드에 영향을 미치는 경우
* **부하 균형 (Load Balancing)**: 코어별 작업량을 균등하게 유지
* **캐시 일관성 (Cache Coherence)**: 각 코어가 가진 캐시 사이의 일관성 유지

**암달의 법칙 (Amdahl’s Law)**\
전체 작업 중 병렬화 가능한 비율이 p이고, 코어 수가 n개일 때 최대 속도 향상은\
**Speedup ≤ 1 / ((1−p) + p/n)** 입니다.\
병렬화 비율이 높을수록 코어 수 증가에 따른 성능 향상이 커집니다.

#### 4.3 Multithreading Models (멀티스레딩 모델)

운영체제가 \*\*사용자 수준 스레드(user threads)\*\*와 \*\*커널 수준 스레드(kernel threads)\*\*를 어떻게 매핑하는지에 따라 세 가지 모델로 나뉩니다.

1. **Many-to-One Model**\
   여러 사용자 스레드가 하나의 커널 스레드에 매핑됩니다.\
   장점: 구현이 간단하고 오버헤드가 적음\
   단점: 한 스레드가 블록되면 전체 프로세스가 블록됨 (현대 시스템에서는 거의 사용되지 않음)
2. **One-to-One Model**\
   각 사용자 스레드가 하나의 커널 스레드에 1:1로 매핑됩니다.\
   장점: 한 스레드가 블록되어도 다른 스레드는 계속 실행 가능, 진정한 병렬 처리 가능\
   단점: 스레드 생성 시 오버헤드가 상대적으로 큼\
   → Windows, Linux, macOS가 채택한 모델입니다.
3. **Many-to-Many Model**\
   여러 사용자 스레드를 여러 커널 스레드에 매핑합니다.\
   가장 유연하지만 구현이 복잡합니다. Solaris 과거 버전에서 사용되었습니다.

#### 4.4 Thread Libraries (스레드 라이브러리)

프로그래머가 스레드를 쉽게 만들고 관리할 수 있도록 제공되는 API입니다.

**1. Pthreads (POSIX Threads) – 가장 널리 사용되는 표준**

Linux, macOS, UNIX 계열에서 지원.

**기본 코드 예시**:

```c
#include <pthread.h>

void* thread_function(void* arg) {
    // 스레드가 수행할 작업
    return NULL;
}

int main() {
    pthread_t tid;
    pthread_create(&tid, NULL, thread_function, NULL);
    pthread_join(tid, NULL);   // 스레드 종료 대기
    return 0;
}
```

**2. Windows Threads**

Win32 API 사용.

**기본 코드 예시**:

```c
#include <windows.h>

DWORD WINAPI thread_function(LPVOID param) {
    // 스레드가 수행할 작업
    return 0;
}

int main() {
    HANDLE thread = CreateThread(NULL, 0, thread_function, NULL, 0, NULL);
    WaitForSingleObject(thread, INFINITE);
    return 0;
}
```

**3. Java Threads**

Java 언어 자체에서 스레드를 지원합니다.

**기본 코드 예시** (Runnable 인터페이스):

```java
public class MyThread implements Runnable {
    public void run() {
        // 스레드가 수행할 작업
    }
    public static void main(String[] args) {
        Thread t = new Thread(new MyThread());
        t.start();
    }
}
```

#### 4.5 Implicit Threading (묵시적 스레딩)

프로그래머가 직접 스레드를 생성하지 않고, **라이브러리나 컴파일러**가 자동으로 스레드를 만들어주는 기법입니다. (10판에서 가장 강조된 부분)

1. **Thread Pools**\
   미리 여러 스레드를 생성해 풀에 보관 → 작업 요청 시 풀에서 하나를 재사용.
2. **OpenMP**\
   C/C++/Fortran에서 `#pragma omp parallel for` 지시어로 병렬 루프 자동 생성.
3. **Grand Central Dispatch (GCD)** – Apple macOS/iOS\
   `dispatch_async(dispatch_get_global_queue(...), ^{ /* 작업 */ });`\
   시스템이 자동으로 스레드 수와 코어를 관리 (Swift에서도 동일하게 사용)
4. **Fork-Join Parallelism** (Java) `ForkJoinPool`과 `RecursiveTask`를 사용해 작업을 재귀적으로 분할(fork)하고 결과를 합침(join).
5. **Intel Thread Building Blocks (TBB)**\
   C++ 라이브러리로 `parallel_for`, `parallel_reduce`, `parallel_pipeline` 등을 제공.

#### 4.6 Threading Issues (스레딩 이슈)

1. **신호 처리 (Signal Handling)**\
   시그널을 어느 스레드가 처리할지 결정해야 합니다.
2. **스레드 취소 (Thread Cancellation)**
   * Asynchronous cancellation: 즉시 강제 종료 (위험)
   * Deferred cancellation: 취소 지점에서만 종료 (안전, Pthreads 기본)
3. **스레드 로컬 저장소 (Thread-Local Storage)**\
   각 스레드마다 독립적인 전역 변수를 가질 수 있음 (`__thread` 키워드 또는 `ThreadLocal` 클래스)
4. **스케줄러 활성화 (Scheduler Activations)**\
   커널과 사용자 스레드 라이브러리가 협력하여 최적의 스레드 수를 유지

#### 4.7 Operating-System Examples

* **Linux**: NPTL (Native POSIX Thread Library) – One-to-One 모델, 커널 수준 스레드
* **Windows**: One-to-One 모델, `CreateThread()` 사용
* **macOS/iOS**: GCD + XNU 커널의 하이브리드 스레드 지원
