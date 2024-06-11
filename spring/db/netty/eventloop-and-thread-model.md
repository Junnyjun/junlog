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
