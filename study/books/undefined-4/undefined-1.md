# 프로세스의 개념

일반적으로 프로세스는 실행 중인 프로그램으로 정의한다.\
프로그램은 디스크 상에 존재하는 실행을 위한 명령어와 정적 데이터의 묶음이다.\
운영체제가 이 명령어와 데이터 묶음을 읽고 실행하여 프로그램에 생명을 불어넣는다.

운영체제는 CPU를 가상화(Virtualization)하여 여러 개의 CPU가 존재하는 것처럼 만든다.\
시분할(Time Sharing)이라 불리는 이 기법은 원하는 수 만큼의 프로세스를 동시에 실행할 수 있게 한다.\
CPU를 공유하기 때문에 각 프로세스의 성능은 낮아진다.

운영체제에서 CPU 가상화를 잘 구현하기 위해 저수준의 도구와 고차원적인 지능이 필요하다.

#### 메커니즘(Mechanism) - 저수준 도구

```
- 메커니즘은 필요한 기능을 구현하는 방법이나 규칙을 의미
- CPU에서 프로그램 실행을 잠시 중단하고 다른 프로그램을 실행하는 것을 
    문맥 교환(Context Switch)라고 함
```

#### 정책(Policy) - 고차원

```
- 운영체제 내에서 어떤 결정을 내리기 위한 알고리즘
ex) 운영체제의 스케줄링 정책(Scheduling Policy)가 여러 프로그램 중 
    어느 프로그램을 실행할 지 결정
```

### 프로세스의 개념

운영체제는 실행 중인 프로그램의 개념을 제공하는데, 이를 프로세스(Process)라고 한다.

프로세스의 구성 요소를 이해하기 위해서 하드웨어 상태(Machine State)를 이해해야 한다.

#### 메모리

```
- 명령어는 메모리에 저장된다.
- 실행 프로그램이 읽고 쓰는 데이터도 메모리에 저장된다.
- 프로세스가 접근할 수 있는 메모리(주소 공간(Address Space))는 프로세스를 구성하는 요소이다.
```

#### 레지스터

```
- 프로그램 카운터(Program Counter, PC)는 프로그램의 어느 명령어가 실행 중인지 알려준다.
- 프로그램 카운터는 명령어 포인터(Instruction Pointer, IP)라고도 불린다.
- 스택 포인터(Stack Pointer)와 프레임 포인터(Frame Pointer)는 함수의 변수와 
  리턴 주소를 저장하는 스택을 관리할 때 사용하는 레지스터이다.
```

프로그램은 영구 저장장치(Persistent Storage)에 접근하기도 한다.\
이 입출력 정보는 프로세스가 현재 열어 놓은 파일 목록을 가지고 있다.

### 프로세스 API

운영체제가 반드시 API로 제공해야 하는 몇몇 기본 기능에 대해 간단히 살펴본다.

```
생성(Create): 운영체제는 새로운 프로세스를 생성할 수 있는 방법을 제공해야 한다.
제거(Destroy): 운영체제는 프로세스를 강제로 제거할 수 있는 방법을 제공해야 한다.
대기(Wait): 여러 종류의 대기 인터페이스가 제공된다.
각종 제어(Miscellaneous Control): 프로세스를 일시정지하거나 재개하는 기능을 제공한다.
상태(Status): 프로세스의 상태 정보를 얻어내는 인터페이스도 제공된다.
```

프로세스 생성: 좀 더 자세하게

<figure><img src="https://blog.kakaocdn.net/dn/nbTKS/btrhMQ1f9Vc/ZlbD1HNX52QBY0jWGqqOz0/img.png" alt="" width="375"><figcaption></figcaption></figure>

프로그램 실행을 위해 운영체제가 하는 작업

```
1) 프로그램 코드와 정적 데이터
   (Static Data, 예를 들어, 초기값을 가지는 변수)를 메모리, 프로세스의 주소 공간에 탑재

2) 일정량의 메모리가 프로그램의 실행시간 스택(Run-time Stack, 혹은 그냥 스택) 용도로 할당

3) 운영체제는 스택을 주어진 인자로 초기화

4) 운영체제는 프로그램의 힙(Heap)을 위한 메모리 영역을 할당

5) 운영체제는 입출력과 관계된 초기화 작업을 수행

6) 프로그램의 시작 지점(Entry Point), main() 루틴으로 분기
```

#### 프로세스 상태

<figure><img src="https://blog.kakaocdn.net/dn/cooT0n/btrhPCHJT5F/GitBL7TEEKo5NJ1TOw0yM0/img.png" alt="" height="229" width="310"><figcaption></figcaption></figure>

실행(Running): 프로세스는 명령어를 실행하고 있다.\
준비(Ready): 프로세스는 실행할 준비가 되어 있다.\
대기(Blocked): 프로세스가 다른 사건을 기다리는 동안 프로세스의 수행을 중단시키는 연산이다.

&#x20;

<figure><img src="https://blog.kakaocdn.net/dn/rjqT9/btrhMdWYfT2/qTyy3L3DBkYvN3iNyUWuF1/img.png" alt="" height="216" width="398"><figcaption></figcaption></figure>

<figure><img src="https://blog.kakaocdn.net/dn/cY30WR/btrhHurvJmT/zIxwtzbuxx7ids6tYsKNcK/img.png" alt="" height="241" width="399"><figcaption></figcaption></figure>

운영체제는 스케줄러를 통해 위와 같은 결정을 내린다.

자료 구조

운영체제도 일종의 프로그램이다.\
다른 프로그램들과 같이 다양한 정보를 유지하기 위한 자료 구조를 가지고 있다.

ex) 프로세스 리스트(Process List)
