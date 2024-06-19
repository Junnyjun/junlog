# Provided ChannelHandlers and codecs

#### 11.1 SSL/TLS로 Netty 애플리케이션 보안 설정

**Netty의 OpenSSL/SSLEngine 구현**

Netty는 OpenSSL 툴킷을 사용하는 SSLEngine 구현도 제공하며, 이는 JDK의 SSLEngine 구현보다 더 나은 성능을 제공합니다. Netty 애플리케이션은 OpenSslEngine을 기본적으로 사용할 수 있으며, OpenSSL 라이브러리가 없으면 JDK 구현으로 대체됩니다.

SSL API 및 데이터 흐름은 JDK의 SSLEngine이나 Netty의 OpenSslEngine을 사용할 때 동일합니다. \
다음은 `SslHandler`를 사용하는 데이터 흐름을 보여줍니다:

* 인바운드 데이터는 `SslHandler`에 의해 가로채어지고 복호화되어 인바운드 방향으로 전달됩니다.
* 아웃바운드 데이터는 `SslHandler`를 통해 암호화되어 아웃바운드 방향으로 전달됩니다.

```kotlin
class SslChannelInitializer(
    private val context: SslContext,
    private val startTls: Boolean
) : ChannelInitializer<Channel>() {
    override fun initChannel(ch: Channel) {
        val engine = context.newEngine(ch.alloc())
        ch.pipeline().addFirst("ssl", SslHandler(engine, startTls))
    }
}
```
