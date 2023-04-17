# CPU 스케줄링

### CPU 스케줄링 개요 <a href="#111-cpu" id="111-cpu"></a>

#### 프로세스 우선순위 <a href="#undefined" id="undefined"></a>

프로세스들은 우선순위를 가지고 있다. 우선순위가 높은 프로세스가 우선적으로 CPU를 할당받는다.

보통 입출력 작업이 많은 입출력 집중 프로세스(Interactive Process)의 우선순위가 CPU 집중 프로세스(CPU-Bound Process)보다 높다.

프로세스의 우선순위는 프로세스의 PCB(Process Control Block)에 저장된다.

#### 스케줄링 큐 <a href="#undefined" id="undefined"></a>

PCB에 우선순위가 저장되어 있지만, CPU를 사용할 다음 프로세스를 찾기 위해 운영체제가 모든 프로세스를 검색하는 것은 비효율적이다.

이를 해결하기 위해 운영체제는 스케줄링 큐(Scheduling Queue)를 사용한다. 반드시 FIFO 방식이지는 않다.

준비 큐는 프로세스가 CPU를 사용할 준비가 되었을 때, 대기 큐는 프로세스가 입출력을 기다리고 있을 때, 완료 큐는 프로세스가 종료되었을 때 사용된다.

대기큐는 같은 장치를 요구한 프로세스들은 같은 큐에 저장된다.

#### 선점형 스케줄링과 비선점형 스케줄링 <a href="#undefined" id="undefined"></a>

선점형 스케줄링(Preemptive Scheduling)은 프로세스가 CPU를 사용하고 있을 때, 운영체제가 강제로 프로세스를 중단시키고 다른 프로세스를 실행시키는 것이다.

비선점형 스케줄링(Non-Preemptive Scheduling)은 프로세스가 CPU를 사용하고 있을 때, 운영체제가 강제로 프로세스를 중단시키지 않는 것이다.

각각의 장단점이 있다.

### 11.2 CPU 스케줄링 알고리즘 <a href="#112-cpu" id="112-cpu"></a>

#### 선입 선처리 스케줄링(FCFS, First-Come First-Served Scheduling) <a href="#fcfs-first-come-first-served-scheduling" id="fcfs-first-come-first-served-scheduling"></a>

준비 큐에 있는 프로세스들을 도착한 순서대로 CPU를 할당하는 비선점형 스케줄링이다.

호위 효과(convoy effect)가 발생할 수 있다.

#### 최단 작업 우선 스케줄링(SJF, Shortest-Job-First Scheduling) <a href="#sjf-shortest-job-first-scheduling" id="sjf-shortest-job-first-scheduling"></a>

CPU 사용이 가장 짧은 프로세스를 우선적으로 CPU를 할당하는 비선점형 스케줄링이다. 선점형으로도 구현 가능하다.

#### 라운드 로빈 스케줄링(RR, Round-Robin Scheduling) <a href="#rr-round-robin-scheduling" id="rr-round-robin-scheduling"></a>

선입 선처리 스케줄링 + 타임 슬라이스(Time Slice)를 사용하는 선점형 스케줄링이다.

타임 슬라이스는 프로세스가 CPU를 사용할 수 있는 시간을 의미한다.

타임 슬라이스가 끝나면 프로세스는 준비 큐의 맨 뒤로 이동한다.

#### 최소 잔여 시간 우선 스케줄링(SRT, Shortest-Remaining-Time Scheduling) <a href="#srt-shortest-remaining-time-scheduling" id="srt-shortest-remaining-time-scheduling"></a>

라운드 로빈 스케줄링 + 최단 작업 우선 스케줄링을 사용하는 선점형 스케줄링이다.

#### 우선순위 스케줄링(Priority Scheduling) <a href="#priority-scheduling" id="priority-scheduling"></a>

프로세스마다 우선순위를 부여하고, 우선순위가 높은 프로세스를 우선적으로 CPU를 할당하는 비선점형 스케줄링이다.

기아 현상(starvation)이 발생할 수 있다. 이를 해결하기 위한 방법으로 에이징 기법(Aging)이 있다.

이는 오랫동안 CPU를 사용하지 못한 프로세스의 우선순위를 높여주는 방법이다.

#### 다단계 큐 스케줄링(Multi-Level Queue Scheduling) <a href="#multi-level-queue-scheduling" id="multi-level-queue-scheduling"></a>

우선순위 스케줄링의 발전된 형태이다.

우선순위별로 준비 큐를 여러 개 만들고, 우선순위가 높은 큐에 있는 프로세스를 우선적으로 CPU를 할당한다.

#### 다단계 피드백 큐 스케줄링(Multi-Level Feedback Queue Scheduling) <a href="#multi-level-feedback-queue-scheduling" id="multi-level-feedback-queue-scheduling"></a>

다단계 큐 스케줄링의 발전된 형태이다.

새로 준비 상태가 된 프로세스가 있다면 우선순위가 가장 높은 큐에 넣고, 타임 슬라이스동안 실행한다.

타임 슬라이스 동안 실행이 끝나지 않으면 우선순위를 낮추고, 다음 우선순위 큐에 넣는다.

CPU를 오래 사용해야 하는 프로세스는 점차 우선순위가 낮아진다.

에이징 기법을 사용하여 우선순위가 낮아진 프로세스의 우선순위를 높여서 기아 현상을 방지할 수 있다.
