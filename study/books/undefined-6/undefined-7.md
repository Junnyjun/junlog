# 스레드 풀 활용

### 작업과 실행 정책 간의 보이지 않는 연결 관계

일정한 조건을 갖춘 실행 정책이 필요한 작업

작업이 실행 정책에 의존성을 가진 경우가 있다.

#### 의존성 있는 작업

```
이 경우 스레드 부족 데드락(thread starvation deadlock)이 발생할 수 있다. 
이런 작업을 수행하는 경우에 실행 정책을 섬세하게 선택해야 한다.
```

#### 스레드 한정 기법을 사용하는 작업

```
같은 스레드를 사용한다고 가정한 작업은, Executor가 단일 스레드로 동작하지 않을 때
스레드 안전성을 쉽게 잃는다.
```

#### 응답 시간이 민감한 작업

```
단일 스레드 Executor에 오랫동안 실행될 작업 등록
서너개의 스레드가 동작하는 풀에 실행 시간이 긴 작업을 몇 개 등록
해당 Executor를 중심으로 응답성이 크게 떨어지게된다.
```

#### ThreadLocal을 사용하는 작업

```
작업 실행 중 스레드를 대치하기도 한다.
따라서 ThreadLocal을 사용해 작업 간에 값 전달하는 용도로 사용하면 안된다.
```

스레드 풀은\
서로 독립적인 다수의 작업을 실행할 때 좋다\
크기가 제한된 스레드 풀에 의존성 있는 작업을 등록하면 deallock\
작업은 실행 정책을 요구하거나, 특정 실행 정책 아래에서는 실행되지 않는 경우가 있다.

#### 스레드 부족 데드락 (thread starvation deadlock)

스레드 풀에서 다른 작업에 의존성 가진 작업 실행 > 데드락 걸릴 가능성 ⤴️

#### 스레드 부족 데드락

이전 작업에서 추가한 작업이 실행되어 결과를 알려주기 기다리는 상황이라면, 이전 작업이 스레드를 점유하고 있기 때문에 추가한 작업이 실행되지 않아서 데드락에 빠지게 된다. 스레드 풀을 사용해도 모든 작업이 아직 실행되지 않은 작업의 결과를 기다릴 때도 발생할 수 있다.

완전히 독립적이지 않은 작업을 Executor에 등록할 때는 항상 스레드 부족 데드락에 주의하자.

#### 오래 실행되는 작업

```
특정 작업이 예상보다 긴 시간동안 종료되지 않고 실행된다면 스레드 풀의 응답 속도에 문제가 생긴다.
제한 없이 기다리는 기능 대신 일정 시간 대기하는 메소드 사용을 고려할 수 있다.
```

### 스레드 풀 크기 조절

풀의 가장 이상적 크기는 다음에 따라 다르다.

```
실행할 작업 종류
스레드 풀 활용 애플리케이션 특성
```

스레드 풀의 설정을 하드코딩해 고정하는 건 그다지 좋은 방법이 아니다.

설정 파일이나, Runtime.availableProcessors등 메소드 결과에 따라 동적으로 지정하자.

```
스레드가 너무 크다 > CPU, 메모리 자원 경쟁 ⤴️ > 리소스 부족
스레드가 너무 작다 > 작업은 쌓이는데 CPU, 메모리는 남음 > 작업 처리 속도 ⤵️
```

작업의 속성에 따라 풀을 나눌 수 있다

```
CPU 많이 사용하는 작업
IO 작업 및 기타 블로킹 작업
리소스(메모리, 파이 핸들, 소켓 핸들, 데이터베이스 연결) 사용하는 작업
```

스레드 풀 크기와 자원 풀 크기가 서로에게 영향을 미친다.

### ThreadPoolExecutor 설정

ThreadPoolExecutor instance&#x20;

```
newCachedThreadPool, newFixedThreadPool, newScheduledThreadPool 같은 팩토리 메소드
ThreadPoolExecutor 생성 메소드 직접 호출
```

#### 스레드 생성과 제거

core, maximum, keep-alive를 설정할 수 있다.

core > 풀에 실행할 작업이 없더라도 코어 크기 만큼은 유지, 단 최초에 모든 스레드를 생성하는 게 아니라 core 까지는 요청 받고 생성

maximum > 작업 큐가 가득 차서 스레드를 추가할 때 최대 스레드 수

keep-alive > 작업 없이 이 시간이 지나고 스레드가 core 갯수보다 많다면 제거 대상

#### 큐에 쌓인 작업 관리

스레드 풀에서 작업을 List\<Runnable>로 관리하는 것이 매번 스레드를 만드는 것보다 자원을 덜 소모한다.

하지만 여전히 작업이 많이 들어올 때 시스템 자원이 모자란 것은 맞다.\
처리하는 속도보다 빠른 속도록 작업이 추가될 때 속도 조절 기능을 사용해 메모리가 가득 차는 현상을 막아야 한다.

#### 큐 크기에 제한을 두는 방식

크기 제한 ❌

```
newFixedThreadPool, newSingleThreadExecutor
크기 제한 하지 않는 LinkedBlockingQueue 사용
작업이 추가되는 속도가 빠를 때 큐에 끝없이 작업이 쌓일 수 있다.
```

크기 제한 ⭕️

```
ArrayBlockingQueue, 크기 제한된 LinkedBlockingQueue, 크기 제한된 PriorityBlockingQueue 사용
큐가 가득 찼을 때 새로운 작업 등록 상황 처리 어떻게 할지 설정 해야한다. 
(집중 대응 정책, saturation policy)
```

큐에 안 쌓음

```
newCachedThreadPool
SynchronousQueue 사용 > 단지 스레드간 작업 넘겨주는 기능
스레드 개수 제한이 없는 상태, 작업을 마음대로 거부할 수 있는 상황에서 적용할 만 하다.
```

다른 작업에 의존성 갖는 작업 실행은 스레드 큐 크기 제한되면 안되므로 newCachedThreadPool 과 같은 스레드 풀 사용해야 한다.

#### 집중 대응 정책 (saturation policy)

집중 대응 정책 (saturation policy) > 크기가 제한된 큐에 작업이 가득찰 때 스레드 풀의 대응 정책\
RejectedExecutionHandler로 표현할 수 있다.

중단 정책 (abort policy) > 작업을 추가할 수 없음을 에러를 통해 알린다.\
ThreadPoolExecutor.AbortPolicy 로 표현

제거 정책 (discard policy) > 추가하려는 작업을 아무 반응 없이 제거한다.\
ThreadPoolExecutor.DiscardPolicy 로 표현

오래된 항목 제거 정책 (discard oldest policy) > 큐에서 실행 예정된 작업 제거하고 새로 받은 일 추가\
ThreadPoolExecutor.DiscardOldestPolicy 로 표현\
FIFO 큐인 경우에는 오래된 항목이 제거되고, Priority 큐인 경우에는 가장 우선순위 높은게 제거된다. 따라서 Priority 큐와는 궁합이 좋지 않다.

호출자 실행 정책 (caller runs policy) > 작업 프로듀서 스레드가 작업을 처리하도록 넘긴다.\
ThreadPoolExecutor.CallerRunsPolicy 로 표현\
execute 호출자 스레드에서 실행한다는 의미\
작업 추가 스레드에 처리를 넘기기 때문에 작업의 추가 속도를 늦출 수 있다.

부하가 점점 밖으로 드러나는 형태

웹 서버 내부 스레드 풀 부하 > 서버 동작하는 TCP 계층 부하 > 클라이언트가 느끼는 부하

#### 스레드 팩토리

스레드 풀에서 새로운 스레드는 스레드 팩토리를 통해서 생성

E.g

```
스레드에 UncaughtExecptionHandler 직접 지정이 필요한 경우
Thread 클래스를 상속받은 다른 스레드를 생성하려는 경우 (e.g. 디버그 메시지 출력 기능 추가 등)
[권장 ❌] 스레드 실행 우선 순위 조절 (c.f. 10.3.1)
[권장 ❌] 데몬 상태 직접 지정 (c.f. 7.4.2)
스레드 풀 사용하는 스레드마다 의미 있는 이름 지정, 로그 남기기 등…
디버깅 할 때 요긴하게 사용할 수 있을 것 같다.
```

ThreadPoolExecutor 생성 이후 설정 변경 > Executors.unconfigurableExecutorService(executor) 를 활용해 더이상 설정 변경 못하게 만들 수 있다.

ThreadPoolExecutor 상속 > beforeExecute, afterExecute는 같은 Thread에서 실행되기에 두 작업 사이에 값을 전달하고 싶으면 ThreadLocal을 활용할 수 있다.

### 재귀 함수 병렬화

다음 케이스가 모두 만족하는 경우 병렬화하기 좋은 대상

```
복잡한 연산을 수행 or 블로킹 IO 메소드 호출
반복 작업이 이전 회차와 독립적
```
