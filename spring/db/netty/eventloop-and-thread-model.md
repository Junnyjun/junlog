# Eventloop& Thread Model

### 7.1 Threading model overview

스레드 모델은 OS, 프로그래밍 언어, 프레임워크, 또는 애플리케이션의 맥락에서 스레드 관리의 중요한 측면을 지정합니다. 스레드가 생성되는 방식과 시기는 애플리케이션 코드 실행에 큰 영향을 미치므로, 개발자는 다양한 모델의 장단점을 이해해야 합니다.&#x20;

Netty의 스레드 모델은 강력하면서도 사용하기 쉽고, 애플리케이션 코드를 단순화하고 성능과 유지보수성을 최대화하는 것을 목표로 합니다.&#x20;

#### 7.1.1 스레드 모델 개요

이 섹션에서는 일반적인 스레드 모델을 소개한 후 Netty의 과거 및 현재 스레드 모델에 대해 논의하며 각 모델의 장단점을 검토할 것입니다.

스레드 모델은 코드가 실행되는 방식을 지정합니다. 동시 실행의 가능한 부작용을 항상 경계해야 하므로, 적용되는 모델의 영향을 이해하는 것이 중요합니다

다중 코어 또는 CPU를 가진 컴퓨터가 보편화됨에 따라 대부분의 현대 애플리케이션은 시스템 자원을 효율적으로 사용하기 위해 정교한 멀티스레딩 기법을 사용합니다.&#x20;

기본적인 스레드 풀링 패턴은 다음과 같이 설명할 수 있습니다:

* 스레드 풀의 자유 목록에서 스레드가 선택되어 제출된 작업(Runnable 구현)을 실행합니다.
* 작업이 완료되면 스레드는 목록으로 반환되어 재사용 가능해집니다.

### 7.2 Interface EventLoop

연결의 수명 동안 발생하는 이벤트를 처리하는 작업을 실행하는 것은 모든 네트워킹 프레임워크의 기본 기능입니다.

이와 관련된 프로그래밍 구조는 종종 이벤트 루프라고 불리며, \
Netty는 이 용어를`io.netty.channel.EventLoop` 인터페이스로 채택하고 있습니다.

이벤트 루프의 기본 아이디어는 다음 코드 목록에서 설명됩니다.&#x20;

```java
while (!terminated) {
    List<Runnable> readyEvents = blockUntilEventsReady();
    for (Runnable ev: readyEvents) {
        ev.run();
    }
}
```

Netty의 EventLoop는 두 가지 기본 API(동시성 및 네트워킹)를 사용하는 협력적 설계의 일부입니다.

* io.netty.util.concurrent 패키지는 JDK 패키지 java.util.concurrent를 기반으로 스레드 실행기를 제공
* io.netty.channel 패키지의 클래스는 Channel 이벤트와 인터페이스를 확장합니다.

이 모델에서 EventLoop는 변경되지 않는 하나의 스레드에 의해 구동되며, 즉시 또는 예약된 실행을 위해 EventLoop 구현에 직접 제출할 수 있는 작업(Runnable 또는 Callable)을 실행합니다.&#x20;

구성과 사용 가능한 코어에 따라 여러 EventLoop가 생성될 수 있으며, 단일 EventLoop가 여러 Channel을 서비스할 수 있습니다.

```java
public interface EventLoop extends EventExecutor, EventLoopGroup {
    @Override
    EventLoopGroup parent();
}
```

### 7.3 Task scheduling

때때로 작업을 나중에(지연된) 또는 주기적으로 실행하도록 예약해야 할 필요가 있습니다. \
일반적인 사용 사례는 원격 피어에 하트비트 메시지를 보내 연결이 여전히 살아 있는지 확인하는 것입니다.&#x20;

#### 7.3.1 JDK scheduling API

| Methods                                                                 | Description                                                                                                  |
| ----------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------ |
| `newScheduledThreadPool(int corePoolSize)`                              | 명령을 지연 후 실행하거나 주기적으로 실행할 수 있는 `ScheduledThreadExecutorService`를 생성합니다. 인수 `corePoolSize`를 사용하여 스레드 수를 계산합니다. |
| `newScheduledThreadPool(int corePoolSize, ThreadFactory threadFactory)` | 명령을 지연 후 실행하거나 주기적으로 실행할 수 있는 `ScheduledThreadExecutorService`를 생성합니다.                                       |
| `newSingleThreadScheduledExecutor()`                                    | 하나의 스레드를 사용하여 예약된 작업을 실행합니다.                                                                                 |
| `newSingleThreadScheduledExecutor(ThreadFactory threadFactory)`         | 하나의 스레드를 사용하여 예약된 작업을 실행합니다.                                                                                 |

`ScheduledExecutorService`를 사용하여 60초 후에 작업을 실행하는 방법

```kotlin
fun main(args: Array<String>) {
    val executor = Executors.newScheduledThreadPool(10)
    val future: ScheduledFuture<*> = executor.schedule(fun() = println("60 seconds later"), 60, TimeUnit.SECONDS)
    executor.shutdown()
}
```

`ScheduledExecutorService` API는 직관적이지만, 부하가 높은 상황에서는 성능 비용이 발생할 수 있습니다.&#x20;

#### 7.3.2 Scheduling tasks using EventLoop

`ScheduledExecutorService` 구현은 풀 관리의 일부로 추가 스레드를 생성하는 등의 한계가 있습니다.\
Netty는 채널의 EventLoop를 사용하여 이 문제를 해결합니다.

```kotlin
fun main(args: Array<String>) {
    val group = NioEventLoopGroup()
    val channel: Channel = NioSocketChannel()

    with(channel) {
        group.register(this)

        eventLoop().schedule(fun() = println("60 seconds later"), 60, TimeUnit.SECONDS)
        eventLoop().scheduleAtFixedRate(fun() = println("Run every 60 seconds"), 60, 60, TimeUnit.SECONDS)
    }
}
```

Netty의 EventLoop는 `ScheduledExecutorService`를 확장하므로, JDK 구현에서 사용할 수 있는 모든 메서드(`schedule()`, `scheduleAtFixedRate()` 등)를 제공합니다.&#x20;

비동기 작업마다 반환되는 `ScheduledFuture`를 사용하여 실행 상태를 취소하거나 확인할 수 있습니다.

### 7.4 Implementation details

#### 7.4.1 Thread management

Netty의 스레딩 모델의 뛰어난 성능은 현재 실행 중인 스레드의 식별에 달려 있습니다. \
즉, 호출 스레드가 현재 채널과 그 EventLoop에 할당된 스레드인지 여부를 확인합니다.&#x20;

호출 스레드가 EventLoop의 스레드인 경우, 해당 코드 블록이 직접 실행됩니다. \
EventLoop는 나중에 실행될 작업을 예약하고 내부 큐에 넣습니다. EventLoop가 다음 이벤트를 처리할 때 이 큐에 있는 작업을 실행합니다.&#x20;

각 EventLoop는 다른 EventLoop와 독립적인 자체 작업 큐를 갖습니다.

#### 7.4.2 EventLoop/thread allocation

채널을 위한 I/O 및 이벤트를 서비스하는 EventLoop는 EventLoopGroup에 포함됩니다. EventLoop가 생성되고 할당되는 방식은 전송 구현에 따라 다릅니다.

**ASYNCHRONOUS TRANSPORTS**

비동기 구현은 소수의 EventLoop(및 관련 스레드)만 사용하며, 현재 모델에서는 채널 간에 공유될 수 있습니다. 이는 각 채널에 스레드를 할당하는 대신, 가능한 최소한의 스레드 수로 많은 채널을 서비스할 수 있게 합니다.

그림 7.4는 고정 크기의 세 개의 EventLoop(각각 하나의 스레드로 구동되는)가 있는 EventLoopGroup을 보여줍니다. EventLoop(및 그 스레드)는 EventLoopGroup이 생성될 때 직접 할당되어 필요할 때 사용할 수 있도록 합니다.

EventLoopGroup은 새로 생성된 각 채널에 EventLoop를 할당할 책임이 있습니다. 현재 구현에서는 라운드 로빈 방식을 사용하여 균형 잡힌 분배를 달성하며, 동일한 EventLoop가 여러 채널에 할당될 수 있습니다. (이는 향후 버전에서 변경될 수 있습니다.)
