# 개요

### 작업을 동시에 실행하는 일에 대한 간략한 역사

운영체제: 여러 개의 프로그램을 각자의 프로세스 내에서 동시에 실행\
프로세스: 각자가 서로 격리된 채, 독립적으로 실행되는 프로그램

운영체제가 프로세스마다 메모리, 파일 핸들, 보안 권한등의 자원을 할당한다.\
프로세스간 통신: 소켓, 시그널 핸들러, 공유 메모리, 세마포어, 파일 등으로 통신

프로그램 동시 실행으로 해결하는 문제들

* 자원 활용
  * IO 대기와 같이 유용하지 않은 일을 하며 자원을 점유하고 있으면 비효율적이다. 차라리 다른 프로그램을 사용하는 게 바람직하다.
* 공정성
  * 한 프로그램을 모두 끝내야 다른 프로그램을 시작하는 것 보다는 더 작은 단위로 컴퓨터를 공유하는 게 바람직하다.
* 편의성
  * 여러 작업을 전부 처리하는 하나의 프로그램을 작성하는 것보다 각기 일을 처리하고 필요할 때 프로그램 간 조율하는 프로그램을 작성하는 편이 더 쉽고 바람직하다.

스레드도 프로세스와 같은 동기를 가지고 고안되었다.

스레드와 프로세스의 차이

* 스레드는 메모리, 파일 핸들과 같이 프로세스에 할당된 자원을 공유한다.
* 각기 별도의 프로그램 카운터, 스택, 지역 변수를 갖는다.
* 한 프로그램 내 여러 스레드를 동시에 여러 개의 CPU에 할당해 실행시킬 수 있다.

한 스레드는 다른 스레드와 상관 없이 비동기적으로 실행된다.\
스레드는 프로세스의 메모리 주소 공간을 공유하기에, 한 프로세스 내 모든 스레드는 같은 변수에 접근하고 같은 힙에 객체를 할당한다.

* 이로 인해서 프로세스보다 더 세밀한 단위로 데이터를 공유할 수 있다.
* 하지만 동시에 공유된 데이터에 접근하는 과정을 적절하게 동기화해야한다.

### 스레드의 이점

#### 멀티프로세서 활용

프로세서 스케줄링의 기본 단위는 스레드이다. 따라서 하나의 스레드로 동작하는 프로그램은 한 번에 최대 하나의 프로세서만 사용한다. (Q? 어느 CPU 얘기인가?)

활성 상태인 스레드가 여러 개인 프로그램은 여러 프로세서에서 동시에 실행될 수 있다.

#### 단순한 모델링

각 작업마다 스레드를 하나씩 할당하면 마치 순차적인 작업처럼 처리할 수 있고, 스케줄링, 교차 실행되는 작업, 비동기 IO, 자원 대기 등의 세부적인 부분과 상위의 비즈니스 로직을 분리하기 쉽다.

#### 단순한 비동기 이벤트 처리

서버 연결 마다 스레드를 할당하고 동기 IO를 사용하도록 하면 개발 작업이 쉬워진다. (Q? 넌블락 IO랑은 다른가)

#### 더 빨리 반응하는 사용자 인터페이스

화면을 그리고 사용자 이벤트를 받아 큐잉하는 메인 이벤트 루프 스레드에서 오래 걸리는 작업이 일어나면 사용자가 빠른 피드백을 받지 못한다.

시간이 오래 걸릴 작업을 별도 스레드에서 실행했다면? UI 이벤트 스레드는 여유롭게 사용자 입력에 반응하고 UI가 더 빨리 반응할 수 있을 것이다.

### 스레드 사용의 위험성

#### 안정성 위해 요소

경쟁 조건

스레드는 서로 같은 메모리 주소 공간을 공유하고 동시에 실행되므로 다른 스레드가 사용 중일지도 모르는 변수를 읽거나 수정할 수도 있다.

* 다른 스레드 간 통신 박식보다 데이터 공유가 쉬워서 좋다.
* 하지만, 여러 스레드가 같은 변수를 읽고 수정하면, 동작 과정을 추론하기 어려워질 수 있다.

#### 활동성 위험

활동성(liveness): 원하는 일이 결국 일어난다.

활동성 장애 유형 (10장 주제)

* 데드락 (deadlock)
* 소모상태 (starvation)
* 라이브락 (livelock)

#### 성능 문제

```
형편없는 서비스 시간, 반응성, 처리율, 자원 소모, 규모에 따른 확장성 등 문제를 포괄
동기화 수단을 사용할 때 컴파일러 최적화를 방해, 메모리 캐시를 지우거나 무효화 하기도 한다.
손실을 분석할 수 있어야하고, 최소화하는 방법을 다룰 것
```

현실에서는 거의 모든 자바 프로그램이 멀티스레드로 동작한다
