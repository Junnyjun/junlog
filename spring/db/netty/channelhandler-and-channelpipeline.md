# ChannelHandler\&ChannelPipeline

## ChannelHandler and ChannelPipeline

### 6.1 The ChannelHandler family

ChannelHandler는 Netty의 구성 요소 모델의 일부로, 데이터 처리 로직을 체인 형태로 구성할 수 있습니다. 이 섹션에서는 ChannelHandler의 주요 인터페이스와 그 동작 방식을 다룹니다.

#### 6.1.1 The Channel lifecycle

Channel은 EventLoop에 등록된 후 다양한 상태를 가집니다.

<img src="../../../.gitbook/assets/file.excalidraw (48).svg" alt="" class="gitbook-drawing">

* **ChannelUnregistered**: Channel이 생성되었지만 EventLoop에 등록되지 않은 상태.
* **ChannelRegistered**: Channel이 EventLoop에 등록된 상태.
* **ChannelActive**: Channel이 활성화되어 원격 피어와 연결된 상태로, 데이터 송수신이 가능.
* **ChannelInactive**: Channel이 원격 피어와 연결되지 않은 상태.

이 상태 변화는 ChannelPipeline 내의 ChannelHandlers로 전달되어 처리됩니다.

#### 6.1.2 The ChannelHandler lifecycle

ChannelHandler의 생명주기 메서드는 ChannelPipeline에 추가되거나 제거될 때 호출됩니다. \
각 메서드는 ChannelHandlerContext 인수를 받습니다.

* **handlerAdded**: ChannelPipeline에 ChannelHandler가 추가될 때 호출됩니다.
* **handlerRemoved**: ChannelPipeline에서 ChannelHandler가 제거될 때 호출됩니다.
* **exceptionCaught**: ChannelPipeline에서 처리 중 오류가 발생하면 호출됩니다.

Netty는 ChannelHandler의 두 가지 중요한 하위 인터페이스를 정의합니다:

**ChannelInboundHandler**: 모든 종류의 수신 데이터 및 상태 변경을 처리합니다.\
**ChannelOutboundHandler**: 송신 데이터와 모든 작업을 가로챌 수 있습니다.

#### 6.1.3 Interface ChannelInboundHandler

ChannelInboundHandler는 데이터가 수신되거나 \
Channel의 상태가 변경될 때 호출되는 메서드를 정의합니다.&#x20;

```
channelRegistered: Channel이 EventLoop에 등록되었을 때 호출됩니다.
channelUnregistered: Channel이 EventLoop에서 등록 해제되었을 때 호출됩니다.
channelActive: Channel이 활성화되었을 때 호출됩니다.
...
```

`channelRead()` 메서드를 재정의할 때는 풀링된 ByteBuf 인스턴스와 연결된 메모리를 명시적으로 해제해야 합니다. 이를 위해 Netty는 `ReferenceCountUtil.release()` 메서드를 제공합니다.

```java
@Sharable
public class DiscardHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ReferenceCountUtil.release(msg);
    }
}
```

#### 6.1.4 Interface ChannelOutboundHandler

ChannelOutboundHandler는 송신 데이터와 작업을 처리합니다. 주요 메서드는 다음과 같습니다:

```
bind: Channel을 로컬 주소에 바인딩할 때 호출됩니다.
connect: Channel을 원격 피어에 연결할 때 호출됩니다.
disconnect: Channel을 원격 피어와 연결 해제할 때 호출됩니다.
...
```

이 메서드들은 대부분 `ChannelPromise` 인수를 받아 작업 완료 시 통지를 제공합니다.

#### 6.1.5 ChannelHandler adapters

`ChannelInboundHandlerAdapter`와 `ChannelOutboundHandlerAdapter`는 기본 구현을 제공하여 자신의 ChannelHandlers를 작성하는 데 사용할 수 있습니다.&#x20;

이들 어댑터 클래스는 공통의 추상 클래스 `ChannelHandlerAdapter`를 확장합니다.

`ChannelHandlerAdapter`는 유틸리티 메서드 `isSharable()`을 제공하여 구현이 Sharable로 주석 처리되어 여러 ChannelPipeline에 추가될 수 있는지 여부를 반환합니다.

#### 6.1.6 Resource management

`ChannelInboundHandler.channelRead()` 또는 `ChannelOutboundHandler.write()`를 호출할 때 리소스 누수가 발생하지 않도록 해야 합니다.&#x20;

Netty는 풀링된 ByteBuf를 처리하기 위해 참조 카운팅을 사용하므로, ByteBuf 사용을 마친 후 참조 카운트를 조정해야 합니다.&#x20;

Netty는 잠재적인 문제를 진단하는 데 도움을 주기 위해 `ResourceLeakDetector` 클래스를 제공합니다.&#x20;

```
LEAK: ByteBuf.release() was not called before it's garbage-collected. Enable
advanced leak reporting to find out where the leak occurred. To enable
advanced leak reporting, specify the JVM option
'-Dio.netty.leakDetectionLevel=ADVANCED' or call
ResourceLeakDetector.setLevel().
```

누수 감지 레벨은 다음과 같습니다:

* **DISABLED**: 누수 감지를 비활성화합니다. 철저한 테스트 후에만 사용하세요.
* **SIMPLE**: 기본 샘플링 비율(1%)을 사용하여 누수를 보고합니다. 대부분의 경우에 적합합니다.
* **ADVANCED**: 누수가 발생한 위치를 보고합니다. 기본 샘플링 비율을 사용합니다.
* **PARANOID**: 모든 접근을 샘플링합니다. 성능에 큰 영향을 미치므로 디버깅 단계에서만 사용하세요.

**Inbound 메시지를 소비하고 해제**

```java
@Sharable
public class DiscardInboundHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ReferenceCountUtil.release(msg);
    }
}
```

**Outbound 데이터 폐기 및 해제**

```java
@Sharable
public class DiscardOutboundHandler extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        ReferenceCountUtil.release(msg);
        promise.setSuccess();
    }
}
```

채널에서 발생하는 예외는 `exceptionCaught()` 메서드를 통해 처리할 수 있습니다. 이 메서드는 ChannelPipeline에서 처리 중 오류가 발생하면 호출됩니다. 이를 통해 적절한 예외 처리를 구현할 수 있습니다.

```java
@Override
public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    ctx.close();
}
```

### 6.2 Interface ChannelPipeline

ChannelPipeline은 ChannelHandler 인스턴스의 체인으로, 채널을 통해 흐르는 인바운드 및 아웃바운드 이벤트를 가로챕니다.&#x20;

새로운 채널이 생성될 때마다 새로운 ChannelPipeline이 할당되며, 이는 채널의 생명주기 동안 변경되지 않습니다.

#### ChannelHandlerContext

ChannelHandlerContext는 ChannelHandler가 자신의 ChannelPipeline 및 다른 핸들러와 상호작용할 수 있도록 합니다.&#x20;

핸들러는 이를 통해 다음 ChannelHandler에게 알리거나 ChannelPipeline을 동적으로 수정할 수 있습니다. \
ChannelHandlerContext는 이벤트 처리 및 I/O 작업을 위한 다양한 API를 제공합니다.

### 6.2.1 Modifying a ChannelPipeline

ChannelHandler는 ChannelPipeline의 레이아웃을 실시간으로 수정할 수 있습니다. 이는 다른 ChannelHandler를 추가, 제거 또는 교체할 수 있는 중요한 기능입니다.

#### ChannelPipeline 메서드

```kotlin
addFirst: ChannelPipeline의 처음에 ChannelHandler를 추가합니다.
addBefore: 지정된 핸들러 앞에 ChannelHandler를 추가합니다.
addAfter: 지정된 핸들러 뒤에 ChannelHandler를 추가합니다.
...

ChannelPipeline pipeline = ..;
FirstHandler firstHandler = new FirstHandler();
pipeline.addLast("handler1", firstHandler);
pipeline.addFirst("handler2", new SecondHandler());
pipeline.addLast("handler3", new ThirdHandler());
...
```

#### ChannelHandler execution and blocking

각 ChannelHandler는 EventLoop에 의해 이벤트를 처리합니다. 이 스레드를 블로킹하지 않는 것이 중요합니다.&#x20;

블로킹 API를 사용하는 레거시 코드를 인터페이스해야 할 경우, ChannelPipeline의 add() 메서드는 EventExecutorGroup을 인수로 받을 수 있습니다.&#x20;

### 6.2.2 Firing events

ChannelPipeline API는 인바운드 및 아웃바운드 작업을 호출하는 추가 메서드를 제공합니다.

#### 인바운드 작업

```
fireChannelRegistered: Channel이 EventLoop에 등록되었음을 알립니다.
fireChannelUnregistered: Channel이 EventLoop에서 등록 해제되었음을 알립니다.
fireChannelActive: Channel이 활성화되었음을 알립니다.
...
```

#### 아웃바운드 작업

```
bind: Channel을 로컬 주소에 바인딩합니다.
connect: Channel을 원격 주소에 연결합니다.
disconnect: Channel을 원격 피어와 연결 해제합니다.
close: Channel을 닫습니다.
...
```

### 6.3 Interface ChannelHandlerContext

ChannelHandlerContext는 ChannelHandler와 ChannelPipeline 간의 연결을 나타내며, ChannelHandler가 ChannelPipeline에 추가될 때마다 생성됩니다.&#x20;

#### ChannelHandlerContext API

```
bind: 주어진 SocketAddress로 바인딩하고 ChannelFuture를 반환합니다.
channel: 이 인스턴스에 바인딩된 Channel을 반환합니다.
close: Channel을 닫고 ChannelFuture를 반환합니다.
connect: 주어진 SocketAddress에 연결하고 ChannelFuture를 반환합니다.
deregister: 이전에 할당된 EventExecutor에서 등록 해제하고 ChannelFuture를 반환합니다.
disconnect: 원격 피어와의 연결을 끊고 ChannelFuture를 반환합니다.
executor: 이벤트를 디스패치하는 EventExecutor를 반환합니다.
..
```

**ChannelHandlerContext 참조 얻기**

```java
ChannelHandlerContext ctx = ..;
Channel channel = ctx.channel();
channel.write(Unpooled.copiedBuffer("Netty in Action", CharsetUtil.UTF_8));
```

### 6.4 Exception handling

예외 처리는 애플리케이션의 중요한 부분으로, Netty는 인바운드 및 아웃바운드 처리 중 발생하는 예외를 처리하기 위한 다양한 옵션을 제공합니다.

#### 6.4.1 Handling inbound exceptions

인바운드 이벤트 처리 중 예외가 발생하면 해당 예외는 발생한 ChannelInboundHandler에서부터 ChannelPipeline을 통해 흐르기 시작합니다. 예외를 처리하려면 ChannelInboundHandler 구현에서 다음 메서드를 재정의합니다:

```java
public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
```

**예제: 기본 인바운드 예외 처리**

```java
public class InboundExceptionHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
```

#### 6.4.2 Handling outbound exceptions

아웃바운드 작업의 정상 완료 및 예외 처리는 다음과 같은 알림 메커니즘을 기반으로 합니다:

**ChannelFuture**: 모든 아웃바운드 작업은 ChannelFuture를 반환합니다. ChannelFutureListeners는 작업 완료 시 성공 또는 오류를 알립니다.

**ChannelPromise**: ChannelOutboundHandler 메서드에 전달되는 ChannelPromise 인스턴스는 비동기 알림을 위해 리스너를 등록할 수 있습니다.

**예제: ChannelFuture에 ChannelFutureListener 추가**

```java
ChannelFuture future = channel.write(someMessage);
future.addListener(new ChannelFutureListener() {
    @Override
    public void operationComplete(ChannelFuture f) {
        if (!f.isSuccess()) {
            f.cause().printStackTrace();
            f.channel().close();
        }
    }
});
```

**예제: ChannelPromise에 ChannelFutureListener 추가**

```java
public class OutboundExceptionHandler extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        promise.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture f) {
                if (!f.isSuccess()) {
                    f.cause().printStackTrace();
                    f.channel().close();
                }
            }
        });
    }
}
```
