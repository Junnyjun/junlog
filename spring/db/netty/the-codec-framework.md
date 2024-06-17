# The codec framework

### 10.1 코덱이란 무엇인가?

모든 네트워크 애플리케이션은 피어 간에 전송되는 원시 바이트를 대상 프로그램의 데이터 형식으로 변환하는 방법을 정의해야 합니다.&#x20;

이 변환 로직은 코덱에 의해 처리되며, 코덱은 각각 한 형식의 바이트 스트림을 다른 형식으로 변환하는 인코더와 디코더로 구성됩니다. 인코더는 송신 데이터를 처리하고 디코더는 수신 데이터를 처리합니다.

### 10.2 디코더

이 클래스는 두 가지 사용 사례를 다룹니다:

* 바이트를 메시지로 디코딩하는 `ByteToMessageDecoder`와 `ReplayingDecoder`
* 하나의 메시지 형식을 다른 메시지 형식으로 디코딩하는 `MessageToMessageDecoder`

디코더는 수신 데이터를 한 형식에서 다른 형식으로 변환하는 책임을 지기 때문에, Netty의 디코더는 `ChannelInboundHandler`를 구현합니다.

#### **10.2.1 추상 클래스 `ByteToMessageDecoder`**

바이트를 메시지로 또는 다른 바이트 시퀀스로 디코딩하는 것은 매우 일반적인 작업이기 때문에, Netty는 이를 위한 추상 기본 클래스인 `ByteToMessageDecoder`를 제공합니다.&#x20;

```kotlin
class ToIntegerDecoder : ByteToMessageDecoder() {
    override fun decode(ctx: ChannelHandlerContext, `in`: ByteBuf, out: List<Any>) {
        if (`in`.readableBytes() >= 4) {
            out + `in`.readInt()
        }
    }
}
```

**10.2.2 추상 클래스 `ReplayingDecoder`**

`ReplayingDecoder`는 `ByteToMessageDecoder`를 확장하여 `readableBytes()`를 호출할 필요를 없앱니다.&#x20;

이는 들어오는 `ByteBuf`를 사용자 정의 `ByteBuf` 구현인 `ReplayingDecoderBuffer`로 래핑하여 내부적으로 이 호출을 실행함으로써 달성됩니다.

```kotlin
abstract class ReplayingDecoder<S> : ByteToMessageDecoder()
```

여기서 매개변수 `S`는 상태 관리를 위해 사용할 형식을 지정합니다. \
상태 관리를 수행하지 않을 경우 `Void`를 사용합니다.

```kotlin
class ToIntegerDecoder2 : ReplayingDecoder<Void>() {
    @Throws(Exception::class)
    override fun decode(ctx: ChannelHandlerContext, `in`: ByteBuf, out: MutableList<Any>) {
        out.add(`in`.readInt()) // 4바이트를 읽어 정수로 변환 후 `out` 리스트에 추가
    }
}
```

`ReplayingDecoder`를 사용하면 `ByteToMessageDecoder`보다 코드가 단순해지지만, 약간의 오버헤드가 발생합니다.&#x20;

따라서 복잡성이 크게 증가하지 않는 한 `ByteToMessageDecoder`를 사용하는 것이 좋습니다.

**10.3.2 추상 클래스 `MessageToMessageEncoder`**

이제 메시지 형식을 변환하는 `MessageToMessageEncoder`를 살펴보겠습니다. \
이 클래스는 주로 한 형식의 메시지를 다른 형식의 메시지로 변환하는 데 사용됩니다.&#x20;

```kotlin
abstract class MessageToMessageEncoder<I> : ChannelOutboundHandlerAdapter()
```

```kotlin
class IntegerToStringEncoder : MessageToMessageEncoder<Int>() {
    @Throws(Exception::class)
    override fun encode(ctx: ChannelHandlerContext, msg: Int, out: MutableList<Any>) {
        out.add(msg.toString()) // 정수를 문자열로 변환하여 `out` 리스트에 추가
    }
}
```

#### 10.4 추상 코덱 클래스

지금까지 디코더와 인코더를 별도의 엔티티로 다루어 왔지만, 때로는 하나의 클래스에서 인바운드 및 아웃바운드 데이터와 메시지의 변환을 모두 관리하는 것이 유용할 수 있습니다.&#x20;

Netty의 추상 코덱 클래스는 이러한 목적으로 유용하며, 디코더와 인코더 쌍을 함께 묶어 두 가지 작업을 처리할 수 있습니다.&#x20;

**10.4.1 추상 클래스 `ByteToMessageCodec`**

`ByteToMessageCodec`은 바이트를 메시지로 디코딩하고, 메시지를 다시 바이트로 인코딩하는 작업을 처리합니다.&#x20;

| 메서드            | 설명                         |
| -------------- | -------------------------- |
| `decode()`     | 바이트를 메시지로 디코딩하는 메서드입니다.    |
| `decodeLast()` | 채널이 비활성 상태가 될 때 한 번 호출됩니다. |
| `encode()`     | 메시지를 바이트로 인코딩하는 메서드입니다.    |

**10.4.2 추상 클래스 `MessageToMessageCodec`**

`MessageToMessageCodec`은 한 메시지 형식을 다른 메시지 형식으로 디코딩하고, 다시 원래 형식으로 인코딩하는 작업을 처리합니다.&#x20;

```kotlin
abstract class MessageToMessageCodec<INBOUND_IN, OUTBOUND_IN> : ChannelDuplexHandler()
```

