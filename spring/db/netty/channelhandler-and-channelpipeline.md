# ChannelHandler\&ChannelPipeline

## ChannelHandler and ChannelPipeline

### 6.1 The ChannelHandler family

ChannelHandler는 Netty의 구성 요소 모델의 일부로, 데이터 처리 로직을 체인 형태로 구성할 수 있습니다. 이 섹션에서는 ChannelHandler의 주요 인터페이스와 그 동작 방식을 다룹니다.

#### 6.1.1 The Channel lifecycle

Channel은 EventLoop에 등록된 후 다양한 상태를 가집니다. 다음은 Channel의 주요 상태와 설명입니다:

* **ChannelUnregistered**: Channel이 생성되었지만 EventLoop에 등록되지 않은 상태.
* **ChannelRegistered**: Channel이 EventLoop에 등록된 상태.
* **ChannelActive**: Channel이 활성화되어 원격 피어와 연결된 상태로, 데이터 송수신이 가능.
* **ChannelInactive**: Channel이 원격 피어와 연결되지 않은 상태.

이 상태 변화는 ChannelPipeline 내의 ChannelHandlers로 전달되어 처리됩니다.

#### 6.1.2 The ChannelHandler lifecycle

ChannelHandler의 생명주기 메서드는 ChannelPipeline에 추가되거나 제거될 때 호출됩니다. 각 메서드는 ChannelHandlerContext 인수를 받습니다.

* **handlerAdded**: ChannelPipeline에 ChannelHandler가 추가될 때 호출됩니다.
* **handlerRemoved**: ChannelPipeline에서 ChannelHandler가 제거될 때 호출됩니다.
* **exceptionCaught**: ChannelPipeline에서 처리 중 오류가 발생하면 호출됩니다.

Netty는 ChannelHandler의 두 가지 중요한 하위 인터페이스를 정의합니다:

* **ChannelInboundHandler**: 모든 종류의 수신 데이터 및 상태 변경을 처리합니다.
* **ChannelOutboundHandler**: 송신 데이터와 모든 작업을 가로챌 수 있습니다.

#### 6.1.3 Interface ChannelInboundHandler

ChannelInboundHandler는 데이터가 수신되거나 Channel의 상태가 변경될 때 호출되는 메서드를 정의합니다. 주요 메서드는 다음과 같습니다:

* **channelRegistered**: Channel이 EventLoop에 등록되었을 때 호출됩니다.
* **channelUnregistered**: Channel이 EventLoop에서 등록 해제되었을 때 호출됩니다.
* **channelActive**: Channel이 활성화되었을 때 호출됩니다.
* **channelInactive**: Channel이 비활성화되었을 때 호출됩니다.
* **channelReadComplete**: 읽기 작업이 완료되었을 때 호출됩니다.
* **channelRead**: 데이터가 읽혔을 때 호출됩니다.
* **channelWritabilityChanged**: Channel의 쓰기 가능 상태가 변경되었을 때 호출됩니다.
* **userEventTriggered**: 사용자 이벤트가 트리거되었을 때 호출됩니다.

`channelRead()` 메서드를 재정의할 때는 풀링된 ByteBuf 인스턴스와 연결된 메모리를 명시적으로 해제해야 합니다. 이를 위해 Netty는 `ReferenceCountUtil.release()` 메서드를 제공합니다.

**예제: 메시지 리소스 해제**

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

* **bind**: Channel을 로컬 주소에 바인딩할 때 호출됩니다.
* **connect**: Channel을 원격 피어에 연결할 때 호출됩니다.
* **disconnect**: Channel을 원격 피어와 연결 해제할 때 호출됩니다.
* **close**: Channel을 닫을 때 호출됩니다.
* **deregister**: Channel을 EventLoop에서 등록 해제할 때 호출됩니다.
* **read**: Channel에서 더 많은 데이터를 읽을 때 호출됩니다.
* **flush**: 대기 중인 데이터를 원격 피어로 플러시할 때 호출됩니다.
* **write**: 데이터를 Channel을 통해 원격 피어로 쓸 때 호출됩니다.

이 메서드들은 대부분 `ChannelPromise` 인수를 받아 작업 완료 시 통지를 제공합니다. `ChannelPromise`는 `ChannelFuture`의 하위 인터페이스로, `setSuccess()` 또는 `setFailure()`와 같은 쓰기 가능한 메서드를 정의합니다.

**예제: SimpleChannelInboundHandler 사용**

```java
@Sharable
public class SimpleDiscardHandler extends SimpleChannelInboundHandler<Object> {
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) {
        // 특별한 작업이 필요 없음
    }
}
```

#### 6.1.5 ChannelHandler adapters

`ChannelInboundHandlerAdapter`와 `ChannelOutboundHandlerAdapter`는 기본 구현을 제공하여 자신의 ChannelHandlers를 작성하는 데 사용할 수 있습니다. 이들 어댑터 클래스는 공통의 추상 클래스 `ChannelHandlerAdapter`를 확장합니다. `ChannelHandlerAdapter`는 유틸리티 메서드 `isSharable()`을 제공하여 구현이 Sharable로 주석 처리되어 여러 ChannelPipeline에 추가될 수 있는지 여부를 반환합니다.

#### 6.1.6 Resource management

`ChannelInboundHandler.channelRead()` 또는 `ChannelOutboundHandler.write()`를 호출할 때 리소스 누수가 발생하지 않도록 해야 합니다. Netty는 풀링된 ByteBuf를 처리하기 위해 참조 카운팅을 사용하므로, ByteBuf 사용을 마친 후 참조 카운트를 조정해야 합니다. Netty는 잠재적인 문제를 진단하는 데 도움을 주기 위해 `ResourceLeakDetector` 클래스를 제공합니다. 이는 애플리케이션 버퍼 할당의 약 1%를 샘플링하여 메모리 누수를 확인합니다.

누수가 감지되면 다음과 같은 로그 메시지가 생성됩니다:

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

#### 6.1.7 Example of releasing resources

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

### 6.2 Exception handling

채널에서 발생하는 예외는 `exceptionCaught()` 메서드를 통해 처리할 수 있습니다. 이 메서드는 ChannelPipeline에서 처리 중 오류가 발생하면 호출됩니다. 이를 통해 적절한 예외 처리를 구현할 수 있습니다.

```java
@Override
public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    ctx.close();
}
```

