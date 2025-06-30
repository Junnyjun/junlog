# 공유 메모리

공유 메모리 기반의 IPC (Interprocess Communication in Shared-Memory Systems)

공유 메모리 기반 IPC는 통신을 원하는 프로세스들이 메모리의 일부 구간을 공유하도록 설정하고, 해당 구역을 통해 데이터를 주고받는 방식이다. 일반적으로 이 공유 메모리는 이를 생성한 프로세스의 주소 공간에 위치하며, 다른 프로세스는 해당 영역을 자신의 주소 공간에 연결(attach)함으로써 접근한다.

운영체제는 보통 각 프로세스의 주소 공간을 격리시키는 것을 기본 정책으로 삼지만, 공유 메모리를 설정하면 이 제한을 양 당사자의 합의 하에 제거할 수 있다. 데이터의 형식과 위치, 접근 시점 등은 운영체제가 아닌 사용자 프로세스가 제어한다. 이로 인해 동기화 문제가 발생할 수 있으며, 동시에 같은 메모리 위치를 접근하지 않도록 프로그래머가 주의해야 한다.

#### 생산자-소비자 문제 예시

공유 메모리 기반 협력의 대표 예로 생산자-소비자 문제가 있다. 생산자는 데이터를 생성하고, 소비자는 이를 사용하는 구조이다. 이를 구현하려면 버퍼를 설정하고 생산자는 데이터를 버퍼에 추가하며, 소비자는 버퍼에서 데이터를 꺼내도록 구성한다.

버퍼는 고정 크기 순환 배열로 구현된다. 다음과 같은 구조체와 변수들이 공유 메모리에 위치한다:

```
#define BUFFER_SIZE 10
item buffer[BUFFER_SIZE];
int in = 0;
int out = 0;
```

* `in`: 다음으로 데이터를 쓸 위치
* `out`: 다음으로 데이터를 읽을 위치
* 버퍼가 가득 찬 조건: `(in + 1) % BUFFER_SIZE == out`
* 버퍼가 비어 있는 조건: `in == out`

생산자 및 소비자는 아래와 같은 코드 구조로 동작한다:

**생산자**

```
item next_produced;
while (true) {
    // next_produced 생성
    while (((in + 1) % BUFFER_SIZE) == out) ; // 버퍼 full 대기
    buffer[in] = next_produced;
    in = (in + 1) % BUFFER_SIZE;
}
```

**소비자**

```
item next_consumed;
while (true) {
    while (in == out) ; // 버퍼 empty 대기
    next_consumed = buffer[out];
    out = (out + 1) % BUFFER_SIZE;
    // next_consumed 소비
}
```

이 방식은 빠르지만, 동기화 문제를 자체적으로 해결해야 한다. 예를 들어 둘 이상의 프로세스가 동시에 버퍼에 접근하면 충돌이 발생할 수 있다. 이는 6장에서 다루는 동기화 기법으로 해결할 수 있다.

***

### 메시지 기반의 IPC (Interprocess Communication in Message-Passing Systems)

메시지 기반 IPC는 주소 공간을 공유하지 않고, 운영체제가 제공하는 시스템 콜 `send(message)`와 `receive(message)`를 통해 데이터를 주고받는 방식이다. 이 방식은 분산 시스템에서 효과적이며, 동기화 또한 메시지를 통한 통신 과정 내에서 해결할 수 있다.

메시지 크기는 고정되거나 가변일 수 있으며, 구현 난이도와 프로그래밍 편의성 사이의 트레이드오프가 존재한다. 메시지 전달을 위한 논리적 연결(Link)은 다음과 같은 세 가지 특성에 따라 구현된다:

#### 1. 통신 이름 지정 방식

* **직접 통신 (Direct Communication)**:
  * `send(P, message)` / `receive(Q, message)` 형태로, 수신자/송신자를 명시
  * 단점: 식별자가 코드 내에 하드코딩됨 → 모듈성 낮음
* **간접 통신 (Indirect Communication)**:
  * 메시지를 송수신하는 객체로 **mailbox(또는 포트)** 사용
  * `send(A, message)` / `receive(A, message)` → A는 mailbox 식별자
  * 두 프로세스는 같은 mailbox를 통해 통신하며, 하나의 mailbox에 여러 프로세스가 연결 가능
  * mailbox는 프로세스 소유 또는 운영체제 소유일 수 있음

#### 2. 동기화 방식

* **Blocking(동기)**:
  * `send`: 메시지가 수신될 때까지 송신자 대기
  * `receive`: 메시지가 도착할 때까지 수신자 대기
  * 두 프로세스가 동시에 멈추는 순간을 **rendezvous**라고 함
* **Non-blocking(비동기)**:
  * `send`: 메시지 전송 후 즉시 실행 재개
  * `receive`: 메시지 없으면 null 반환하고 실행 재개

#### 3. 버퍼링 방식

* **Zero capacity**:
  * 큐 크기 0 → 메시지 수신 준비 없으면 송신자는 블로킹됨
* **Bounded capacity (n)**:
  * 큐 최대 n개 메시지 저장 가능
  * n 초과 시 송신자는 대기
* **Unbounded capacity**:
  * 큐는 무한 크기로 간주
  * 송신자는 절대 블로킹되지 않음

#### 생산자-소비자 문제 예시 (메시지 방식)

**생산자**

```
message next_produced;
while (true) {
    // next_produced 생성
    send(next_produced);
}
```

**소비자**

```
message next_consumed;
while (true) {
    receive(next_consumed);
    // next_consumed 소비
}
```

메시지 기반 IPC는 공유 메모리에 비해 단순한 프로그래밍이 가능하며, 프로세스 간 결합도도 낮아진다. 하지만 커널 개입이 필요하므로 오버헤드가 크다. 반면, 공유 메모리는 빠르지만 프로세스 간 협력 로직과 동기화 구현 부담이 높다.
