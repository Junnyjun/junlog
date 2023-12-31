# 성능, 확장성

#### 성능을 높인다?&#x20;

&#x20;더 적은 자원으로 더 많은 일을 하도록 한다.\
&#x20;CPU, 메모리, 네트웍 속도, 디스크 속도, DB 속도, 디스크 용량 …

#### 멀티 스레드에 내제된 성능상 비용

```
스레드 간 작업 조율 시 오버헤드 (락 걸기, 신호 보내기, 메모리 동기화)
컨텍스트 스위칭
스레드 생성 및 제거
여러 스레드를 효율적 스케줄링
```

더 나은 성능을 위해 병렬 프로그래밍 한다면

```
프로그램이 확보할 수 있는 자원 최대한 활용 가능한가
남는 자원이 생길 때, 그 자원 역시 최대한 활용 가능한가 
```

#### 성능 대 확장성

얼마나 빠르냐, 얼마나 많은 양의 일을 하냐 두 가지 관점에서 생각하기

```
얼마나 빠르냐 > 서비스 시간, 대기 시간
얼마나 많은 > 용량, 처리량
```

추가적인 장비를 사용해 처리량, 용량을 쉽게 키울 수 있는가?\
&#x20;작업 병렬화로 시스템 가용 자원을 최대로 활용하며 더 많은 일을 처리할 수 있도록 하는 방법

#### 성능 트레이드 오프 측정

최적화 기법을 성급하게 적용하지 말기, 제대로 동작하게 만들고 최적화하기, 예상보다 심각하게 성능이 떨어지는 경우에만 최적화하기

성능과 관련된 설계 시 해야할 질문

```
빠르다의 의미?
 ㄴ대기시간? 서비스 시간? 처리 용량?
어떤 조건일 때 이 방법이 빠를 것인가?
 ㄴ부하가 적을 때? 부하 걸릴 때? 데이터 많을 때 적을 때?
얼마나 빈번히 발생하는 케이스인가
 ㄴ 실용적으로 품을 들일만한 가치가 있는가?
개발 비용, 유지 보수 비용 증가가 어느정도 되는가?
 ㄴ그런 비용을 감수하면서 성능 개선 작업을 해야 하는가?
```

성능을 높이기 위해 안전성을 떨어뜨리는 것은 최악\
성능 튜닝은 항상 성능 목표에 대한 명확한 요구 사항이 있어야한다.\
실제와 같은 사용자 부하의 특성을 동일하게 할 수 있는 성능 측정 도구가 있어야 한다.\
추측하지 말고, 실제로 측정해보자.

### 암달의 법칙

&#x20;병렬 작업, 순차 작업의 비율에 따라 하드웨어 추가 투입 시 성능이 얼마나 개선될 지 예측하는 이론

```
순차 작업이 많으면 프로세서의 개수가 늘어도 속도 증가는 미비하다.
모든 병렬 프로그램에는 항상 순차적으로 실행되는 부분이 존재한다.
```

락 잡기, 공유 변수 접근 등\
순차적 실행 구조가 얼마나 효율적으로 동기화 되어있나? 가 중요한 지표 가 된다.

`ConcurrentLinkedQueue vs synchronizedLinkedList`

### 스레드와 비용

병렬로 실행하며 얻는 이득이 병렬로 실행하느라 드는 비용을 넘어야 한다.

#### 컨텍스트 스위칭

스레드가 실행되다 다른 스레드가 실행되는 순간 발생

```
현재 스레드 실행 상태 보관
다음 스레드 실행 상태 읽기
```

#### 컨텍스트 스위칭의 비용

운영체제와 JVM 내부의 공용 자료 구조를 다뤄야 한다\
다른 스레드로 변경됬을 때, 기존에 사용하던 데이터가 프로세서의 캐시에 없을 수 있다.

스레드가 실행하다 락을 확보하기 위해 대기하면, 일반적으로 JVM은 해당 스레드를 일시 정지하고 다른 스레드를 실행한다. (락 확보는 컨텍스트 스위칭을 유발한다.)

#### 메모리 동기화

메모리 배리어, 캐시를 플러시하거나 무효화하고, 쓰기 버퍼 플러시, 실행 파이프 라인 늦추기\
명령어 재배치 할 수 없어 성능 문제\
JVM 내부적으로 과도한 락 사용을 방지하기도 한다.

```
유출 분석, 락 생략 >
 로컬 변수가 외부로 공개된 적이 없다면, 해당 변수가 내부 스레드에서만 사용된다. 
 Vector 등에 접근할 때 동기화 필요 없다.

락 확장 > 
 연달아 붙어 있는 여러 개의 동기화 블록을 하나의 락으로 묶는 방법
```

#### 블로킹

JVM에서 스레드를 대기 상태에 두는 방법

```
스핀 대기(spin waiting) >
 락을 확보할 때 까지 재시도
 대기 시간 짧을 때 효과적
운영체제가 제공하는 기능 활용 >
 스레드 실제 대기 상태
 대기 시간이 긴 경유 효율적이라고 함
```

대부분의 경우 운영체제 기능 호출하는 편

### 락 경쟁 줄이기

작업을 순차적으로 처리하면 확장성을 놓치고 (scalability)\
작업을 병렬로 처리하면 컨텍스트 스위칭에서 성능에 악영향을 준다. (performance)

락을 놓고 경쟁하는 상황 > 일을 순차적으로 처리 + 컨텍스트 스위칭 증가\
확장성에서 가장 큰 위협은 특정 자원을 독점적으로 사용하도록 제한하는 락이다.

락 경쟁의 원인

```
락을 얼마나 빈번하게 확보하려 하나?
한 번 확보하면 얼마나 오래 사용하나?
```

따라서 락 경쟁 조건 줄이기 위해서

```
락 확보 유지 시간 최소화
락 확보 요청 횟수 최소화
독점적인 락 대신 병렬성을 높여주는 여러 방법을 활용
```

#### 락 구역 좁히기

락을 유지하는 시간을 줄이는 방법\
락이 영향 미치는 구역을 좁히면 락 유지하는 시간을 줄인다.\
공유 상태 변수가 하나인 경우, 스레드 안전성 위임 방법을 통해서 개선할 수도 있다.

#### 락 정밀도 높이기

스레드에서 해당 락을 덜 사용하도록 변경하는 방법

```
락 분할 (splitting)
락 스트라이핑 (striping)
```

서로 다른 락을 사용해 여러 독립적인 상태 변수를 각자 묶는 방법\
락의 개수가 많아질수록 데드락 발생 위험

`락 스트라이핑` > 독립적인 객체를 여러 가지 크기 단위로 묶고 묶인 블록 단위로 락 나누기

#### 핫 필드 최소화

모든 연산 수행 시 한 번씩 사용하는 카운터 변수와 같은 부분\
공용 변수인 핫 필드에 접근하기 위해서 확장성을 읽게된다.

#### 독점적인 락을 최소화하는 다른 방법

좀더 높은 병렬성으로 변수 관리하기

```
병렬 컬렉션 사용하기
ReadWriteLock 사용하기
불변 객체 사용하기
단일 연산 변수 사용하기 (e.g. AtomicLong)
```

ReadWriteLock

```
여러 개의 reader가 있고 writer는 하나인 상황으로 문제 압축
읽기 연산이 대부분을 차지하는 데이터 구조에 적용
읽기 전용 데이터 구조라면 불변 클래스 형태 유지만 해도 동기화 코드 필요 없다.
```
