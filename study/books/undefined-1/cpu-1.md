# CPU 성능 향상 기법

## 빠른 CPU를 위한 설계 기법 <a href="#ch05-cpu" id="ch05-cpu"></a>

#### 클럭 <a href="#undefined" id="undefined"></a>

클럭 속도: 헤르츠(Hz) 단위로 측정한다. 1초에 몇 번 클럭 신호가 발생하는지를 나타낸다.

#### 코어와 멀티코어 <a href="#undefined" id="undefined"></a>

코어(Core): CPU 내부에 존재하는 연산장치.

멀티코어(Multi-Core): 하나의 CPU에 여러 개의 코어가 존재하는 것.

#### 스레드와 멀티스레드 <a href="#undefined" id="undefined"></a>

스레드(Thread): 프로세스 내에서 실행되는 흐름의 단위.

하드웨어적 스레드(Hardware Thread): 하나의 코어가 동시에 처리하는 명령어 단위. 하나의 코어로 여러 명령어를 동시에 처리하는 CPU를 멀티스레드 CPU라고 한다. 논리 프로세서(Logical\
Processor)라고도 한다.

소프트웨어적 스레드(Software Thread): 하나의 프로그램에서 독립적으로 실행되는 단위. 하나의 프로그램을 여러 개의 스레드로 분할하여 동시에 실행하는 것을 멀티스레드 프로그래밍이라고 한다.

멀티코어 프로세서(Multi-Core Processor): 하나의 CPU에 여러 개의 코어가 존재하는 CPU.

멀티스레드 프로세서(Multi-Thread Processor): 하나의 프로세서가 여러 개의 스레드를 동시에 처리하는 CPU.

### 명령어 병렬 처리 기법 <a href="#52" id="52"></a>

#### 명령어 파이프라인 <a href="#undefined" id="undefined"></a>

명령어가 처리되는 과정을 비슷한 시간 간격으로 나누면 다음과 같다.

1. 명령어 인출(Instruction Fetch): 명령어를 메모리에서 가져오는 과정
2. 명령어 해석(Instruction Decode): 명령어를 해석하는 과정
3. 명령어 실행(Instruction Execute): 명령어를 실행하는 과정
4. 결과 저장(Write Back): 명령어의 실행 결과를 저장하는 과정

CPU는 같은 단계가 겹치지만 않으면 명령어를 동시에 처리할 수 있다. 이를 명령어 파이프라인이라고 한다.

명령어 파이프라인(Pipeline): 명령어가 처리되는 과정을 나누어서 동시에 처리하는 기법.

하지만, 파이프라인 위험이 따른다.

데이터 위험(Data Hazard): 명령어의 실행 결과가 다음 명령어의 실행에 영향을 미치는 경우. 명령어 간의 의존성에 의해 발생한다.\
제어 위험(Control Hazard): 프로그램 카운터의 갑작스러운 변화에 의해 발생한다. 분기 예측(Branch Prediction)으로 해결한다.\
구조적 위험(Structural Hazard): 서로 다른 명령어가 같은 CPU 부품(ALU, 레지스터 등)을 사용하는 경우.

#### 슈퍼스칼라 <a href="#undefined" id="undefined"></a>

명령어 파이프라인을 여러 개 사용하는 기법. 명령어를 여러 개의 파이프라인으로 나누어서 동시에 처리한다.(멀티 스레드 프로세서와 유사)

#### 비순차적 명령어 처리(OoOE, Out-of-Order Execution) <a href="#oooe-out-of-order-execution" id="oooe-out-of-order-execution"></a>

명령어를 순차적으로 처리하는 것이 아니라, 명령어의 실행 결과가 필요한 명령어를 먼저 처리하는 기법.

### CISR와 RISC <a href="#53-cisr-risc" id="53-cisr-risc"></a>

#### 명령어 집합 <a href="#undefined" id="undefined"></a>

명령어 집합(Instruction Set): CPU가 처리할 수 있는 명령어의 집합. 명령어 집합 구조(Instruction Set Architecture, ISA)라고도 한다.

현대 CPU의 명령어 집합은 크게 CISC와 RISC로 나뉜다.

#### CISC <a href="#cisc" id="cisc"></a>

CISC(Complex Instruction Set Computer): 복잡한 명령어 집합. 다양하고 강력한 명령어를 활용한다. 상대적으로 적은 수의 명령어로 복잡한 작업을 처리할 수 있다.

명령어 파이프라이닝이 불리한 단점이 있다.

#### RISC <a href="#risc" id="risc"></a>

RISC(Reduced Instruction Set Computer): 간단한 명령어 집합. 명령어의 수가 적고, 각 명령어의 기능이 단순하다. 명령어 파이프라이닝이 용이하다.

load-store 구조를 사용한다. 메모리 접근을 최소화 하고 레지스터를 많이 사용한다.
