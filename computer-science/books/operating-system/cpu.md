# CPU 스케줄링

#### 5.1 Basic Concepts (기본 개념)

단일 CPU 코어 시스템에서는 한 번에 하나의 프로세스만 실행될 수 있습니다.\
다른 프로세스들은 CPU가 사용 가능해질 때까지 기다려야 합니다.\
**CPU-I/O Burst Cycle** (CPU-입출력 버스트 사이클): 프로세스 실행은 CPU 실행(CPU burst)과 I/O 대기(I/O burst)의 반복으로 이루어집니다.\
CPU burst가 끝나면 프로세스는 I/O를 요청하거나 종료하거나 다른 프로세스로 컨텍스트 전환됩니다.

**CPU Scheduler** (CPU 스케줄러, 단기 스케줄러)는 준비 큐(ready queue)에서 다음에 실행할 프로세스를 선택합니다.\
준비 큐는 일반적으로 \*\*링크드 리스트(linked list)\*\*로 구현되며, 각 PCB(Process Control Block)가 큐에 연결됩니다.

**Preemptive vs Non-preemptive Scheduling** (선점 vs 비선점 스케줄링):

* **비선점 스케줄링 (Non-preemptive Scheduling)**: 일단 CPU가 프로세스에 할당되면, 프로세스가 자발적으로 CPU를 반납할 때까지(종료하거나 I/O 대기 상태로 전환될 때까지) CPU를 유지합니다.\
  장점: 구현이 간단하고 오버헤드가 적음\
  단점: 응답 시간이 길어질 수 있음 (긴 CPU burst 프로세스가 짧은 프로세스를 오랫동안 지연시킬 수 있음)
* **선점 스케줄링 (Preemptive Scheduling)**: 실행 중인 프로세스가 CPU를 사용하고 있어도, 더 높은 우선순위 프로세스가 나타나거나 타이머 인터럽트가 발생하면 강제로 CPU를 빼앗습니다.\
  장점: 더 나은 응답 시간과 공정성\
  단점: 컨텍스트 스위칭 오버헤드가 크고, 공유 데이터 접근 시 동기화 문제(race condition)가 발생할 수 있음

**Dispatcher** (디스패처)는 CPU 스케줄러가 선택한 프로세스를 실제로 CPU에 할당하는 모듈입니다.\
Dispatcher는 **컨텍스트 스위칭(context switch)**, 사용자 모드 전환, 프로그램 카운터 점프 등의 작업을 수행합니다.\
**Dispatch Latency** (디스패치 지연 시간): 디스패처가 한 프로세스를 정지시키고 다른 프로세스를 시작하는 데 걸리는 시간입니다.\
이 지연 시간을 최소화하는 것이 CPU 스케줄링의 중요한 목표입니다.

#### 5.2 Scheduling Criteria (스케줄링 기준)

CPU 스케줄링 알고리즘을 비교하기 위한 기준(criteria)입니다.\
대부분의 기준은 \*\*평균값(average)\*\*으로 측정합니다.

1. **CPU Utilization (CPU 이용률)**\
   CPU가 바쁘게 일하는 시간의 비율. 목표: 최대화 (최대 100%에 가깝게)
2. **Throughput (처리량)**\
   단위 시간당 완료된 프로세스 수. 목표: 최대화
3. **Turnaround Time (반환 시간)**\
   프로세스가 시스템에 제출된 순간부터 완료되어 종료될 때까지 걸린 총 시간.\
   \= Waiting time + Execution time + I/O time 등\
   목표: 최소화
4. **Waiting Time (대기 시간)**\
   프로세스가 준비 큐에서 CPU를 기다리는 시간의 합.\
   목표: 최소화 (실행 시간은 알고리즘에 영향을 받지 않음)
5. **Response Time (응답 시간)**\
   요청이 제출된 순간부터 첫 번째 응답이 나올 때까지의 시간 (대화형 시스템에서 중요).\
   목표: 최소화

최적화 목표는 일반적으로 \*\*평균 대기 시간(average waiting time)\*\*을 최소화하는 것입니다.\
그러나 경우에 따라 \*\*최대 대기 시간(maximum waiting time)\*\*을 최소화하거나, **응답 시간**을 우선시하기도 합니다.

#### 5.3 Scheduling Algorithms (스케줄링 알고리즘)

**5.3.1 First-Come, First-Served Scheduling (FCFS, 선착순 스케줄링)**

가장 간단한 알고리즘.\
준비 큐에 도착한 순서대로 CPU를 할당합니다 (FIFO 큐).

**예시** (4개의 프로세스, 도착 시간 0):

* P1: burst time 24
* P2: burst time 3
* P3: burst time 3

**Gantt Chart**:

```
P1    P2   P3
0    24   27   30
```

**평균 대기 시간 계산**:

* P1 대기 시간: 0
* P2 대기 시간: 24
* P3 대기 시간: 27
* 평균 대기 시간 = (0 + 24 + 27) / 3 = 17

**장점**:

* 구현이 매우 간단하고 공정함
* 비선점 스케줄링이므로 컨텍스트 스위칭 오버헤드가 적음

**단점**:

* **Convoy Effect** (호위 효과): 긴 burst time 프로세스가 먼저 도착하면 뒤에 있는 짧은 프로세스들이 오랫동안 기다림 → 평균 대기 시간 증가
* I/O-bound 프로세스와 CPU-bound 프로세스가 섞이면 전체 시스템 효율이 떨어짐

**5.3.2 Shortest-Job-First Scheduling (SJF, 최단 작업 우선 스케줄링)**

다음에 실행할 프로세스로 **CPU burst time이 가장 짧은 프로세스**를 선택합니다.

**비선점 SJF**와 **선점 SJF (Shortest-Remaining-Time-First, SRTF)** 두 가지가 있습니다.

**예시** (비선점 SJF, 도착 시간 모두 0):

* P1: 6
* P2: 8
* P3: 7
* P4: 3

**Gantt Chart**:

```
P4  P1   P3   P2
0   3    9    16   24
```

**평균 대기 시간** = (3 + 16 + 9 + 0) / 4 = 7 (FCFS보다 훨씬 짧음)

**장점**:

* 평균 대기 시간이 가장 짧음 (최적 알고리즘)
* 이론적으로 최적

**단점**:

* **CPU burst time을 미리 알아야 함** (실제로는 예측해야 함 → exponential averaging 사용)
* **Starvation (기아 현상)**: 긴 burst time 프로세스가 계속 밀려날 수 있음
* 선점 버전(SRTF)은 더 짧은 남은 시간 프로세스가 도착하면 현재 프로세스를 선점

**예측 기법 (Exponential Averaging)**: τ(n+1) = α·t(n) + (1-α)·τ(n)\
(τ: 다음 burst 예측값, t: 실제 burst 시간, α: 0\~1 사이 가중치)

**5.3.3 Priority Scheduling (우선순위 스케줄링)**

각 프로세스에 \*\*우선순위(priority number)\*\*를 부여하고, 가장 높은 우선순위 프로세스를 먼저 실행합니다.

* **내부 우선순위**: 시간 제한, 메모리 요구량, 프로세스 나이 등
* **외부 우선순위**: 중요도, 정치적 요인 등

**예시** (비선점):

* P1: priority 3, burst 10
* P2: priority 1, burst 1
* P3: priority 4, burst 2
* P4: priority 5, burst 1
* P5: priority 2, burst 5

**Gantt Chart**:

```
P2  P5   P1   P3   P4
0   1    6    16   18   19
```

**평균 대기 시간** 계산 후 설명

**단점**:

* **Starvation (기아 현상)**: 낮은 우선순위 프로세스가 영원히 기다릴 수 있음
* 해결 방법: **Aging (노화)** — 시간이 지날수록 우선순위를 점차 높여줌

**5.3.4 Round-Robin Scheduling (RR, 라운드 로빈 스케줄링)**

각 프로세스에게 \*\*시간 할당량(time quantum, q)\*\*을 주고, 순환하며 CPU를 할당합니다.

**예시** (q = 4):

* P1: 24
* P2: 3
* P3: 3

**Gantt Chart**:

```
P1  P2  P3  P1  P1  P1  P1  P1  P1
0   4   7   10  14  18  22  26  30
```

**평균 대기 시간** 계산

**장점**:

* 응답 시간이 좋음 (대화형 시스템에 적합)
* 공정함

**단점**:

* 시간 할당량(q)이 너무 크면 FCFS와 비슷해짐
* q이 너무 작으면 컨텍스트 스위칭 오버헤드가 커짐
* 최적 q 값은 보통 80%의 프로세스가 하나의 quantum 내에 끝나도록 설정

**5.3.5 Multilevel Queue Scheduling (다단계 큐 스케줄링)**

준비 큐를 여러 개의 별도 큐로 나눕니다 (예: foreground / background, 시스템 프로세스 / 사용자 프로세스 등).

각 큐마다 다른 스케줄링 알고리즘 적용 가능 (RR for interactive, FCFS for batch).

**고정 우선순위** 또는 **시간 슬라이스** 방식으로 큐 간 우선순위 결정.

**5.3.6 Multilevel Feedback Queue Scheduling (다단계 피드백 큐 스케줄링)**

Multilevel Queue의 가장 일반적인 형태.\
프로세스가 다른 큐로 이동할 수 있습니다 (feedback).

**예시** (3단계 큐):

* Q0: RR with q=8 (가장 높은 우선순위)
* Q1: RR with q=16
* Q2: FCFS (가장 낮은 우선순위)

새 프로세스는 Q0에 들어가고, quantum을 다 사용하면 다음 큐로 강등됩니다.\
짧은 burst 프로세스는 높은 우선순위 큐에서 빨리 끝나고, 긴 burst 프로세스는 낮은 큐로 이동합니다.

**장점**:

* 다양한 프로세스 유형에 적응 가능
* Starvation 방지 가능 (aging 기법 적용)

**단점**:

* 파라미터(q, 큐 수, 승격/강등 규칙)가 많아 튜닝이 어려움

#### 5.4 Thread Scheduling (스레드 스케줄링)

**프로세스 스케줄링** vs **스레드 스케줄링**:

* 프로세스: 커널이 프로세스를 스케줄링
* 스레드: 사용자 수준 스레드 vs 커널 수준 스레드

**Pthreads**에서 스레드 스케줄링 속성:

* `PTHREAD_SCOPE_PROCESS`: 프로세스 내에서 경쟁
* `PTHREAD_SCOPE_SYSTEM`: 시스템 전체에서 경쟁 (One-to-One 모델에 적합)

**Linux**는 커널 수준 스레드를 사용하므로 스레드 스케줄링은 프로세스 스케줄링과 동일합니다.

#### 5.5 Multi-Processor Scheduling (멀티프로세서 스케줄링)

**Symmetric Multiprocessing (SMP)**: 모든 프로세서가 동일한 준비 큐를 공유합니다.

**Load Balancing (부하 균형)**:

* **Push Migration**: 과부하 프로세서가 다른 프로세서로 프로세스를 밀어냄
* **Pull Migration**: 유휴 프로세서가 다른 프로세서에서 프로세스를 끌어옴

**NUMA (Non-Uniform Memory Access)** 시스템에서는 메모리 접근 시간이 위치에 따라 다릅니다.\
→ **NUMA-aware scheduling**: 프로세스를 메모리와 가까운 코어에 할당

**Heterogeneous Multiprocessing**: 성능이 다른 코어 (big.LITTLE 아키텍처)에서 작업 분배

#### 5.6 Real-Time CPU Scheduling (실시간 CPU 스케줄링)

**Hard Real-Time System**: 데드라인을 절대 놓치면 안 됨 (미사일 제어 등)\
**Soft Real-Time System**: 데드라인을 놓쳐도 괜찮지만 성능 저하 (멀티미디어 등)

**Rate Monotonic Scheduling (RMS)**: 주기가 짧은 태스크에 높은 우선순위 부여 (정적 우선순위)\
**Earliest Deadline First (EDF)**: 데드라인이 가장 가까운 태스크에 우선순위 부여 (동적 우선순위)

#### 5.7 Operating-System Examples

**Linux CFS (Completely Fair Scheduler)**:

* Red-Black Tree를 사용해 공정성을 유지
* vruntime (virtual runtime) 기준으로 스케줄링
* NUMA-aware 기능 포함

**Windows 10 Scheduling**:

* 우선순위 기반 (32단계)
* Foreground 프로세스에 약간의 boost
* Heterogeneous Multiprocessing 지원 (big.LITTLE)

#### 5.8 Algorithm Evaluation (알고리즘 평가)

* **평가 기준**: 대기 시간, 응답 시간, 처리량 등
* **평가 방법**:
  * **분석적 평가 (Analytic Evaluation)**: 수학적 모델링
  * **시뮬레이션 (Simulation)**: 실제 워크로드로 시뮬레이션
  * **구현 및 측정 (Implementation)**: 실제 시스템에 구현 후 측정
