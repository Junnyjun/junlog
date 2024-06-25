# Provided ChannelHandlers and codecs

### Netty를 이용한 다양한 프로토콜 처리

Netty는 다양한 일반적인 프로토콜을 처리하기 위한 코덱과 핸들러를 제공합니다. 이러한 도구들을 사용하면 SSL/TLS 및 WebSocket 지원과 같은 기능을 구현하는데 소요되는 시간과 노력을 줄일 수 있습니다.

#### 11.1 SSL/TLS로 Netty 애플리케이션 보호하기

오늘날 데이터 프라이버시는 중요한 문제이며, 개발자로서 우리는 이를 해결할 준비가 되어 있어야 합니다. 최소한 SSL과 TLS와 같은 암호화 프로토콜에 대해 익숙해져야 합니다.&#x20;

Netty는 이러한 프로토콜을 지원하기 위해 `javax.net.ssl` 패키지를 활용하여 암호화 및 복호화를 간단하게 구현할 수 있습니다. Netty는 내부적으로 `SSLEngine`을 사용하는 `SslHandler`를 제공하여 이를 구현합니다.

**Netty의 OpenSSL/SSLEngine 구현**

Netty는 OpenSSL 도구를 사용하는 `OpenSslEngine` 구현을 제공합니다. 이 클래스는 JDK의 `SSLEngine` 구현보다 더 나은 성능을 제공합니다. OpenSSL 라이브러리가 사용 가능할 경우 Netty 애플리케이션은 기본적으로 `OpenSslEngine`을 사용할 수 있습니다. 그렇지 않으면 JDK 구현으로 대체됩니다.

**데이터 흐름**

SSL/TLS 핸들러를 사용한 데이터 흐름은 다음과 같습니다:

* 암호화된 수신 데이터는 `SslHandler`에 의해 가로채어져 복호화된 후 내부로 전달됩니다.
* 발신 데이터는 `SslHandler`를 통해 암호화된 후 외부로 전달됩니다.

**SSL/TLS 지원 추가하기**

다음 코드는 `SslHandler`를 `ChannelPipeline`에 추가하는 방법을 보여줍니다. `ChannelInitializer`는 `Channel`이 등록될 때 `ChannelPipeline`을 설정하는 데 사용됩니다.

```java
public class SslChannelInitializer extends ChannelInitializer<Channel> {
    private final SslContext context;
    private final boolean startTls;

    public SslChannelInitializer(SslContext context, boolean startTls) {
        this.context = context;
        this.startTls = startTls;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        SSLEngine engine = context.newEngine(ch.alloc());
        ch.pipeline().addFirst("ssl", new SslHandler(engine, startTls));
    }
}
```

#### 11.2 Netty HTTP/HTTPS 애플리케이션 구축

HTTP/HTTPS는 가장 일반적인 프로토콜 중 하나이며, Netty는 이 프로토콜을 사용하기 위한 다양한 인코더와 디코더를 제공합니다.

**HTTP 디코더, 인코더 및 코덱**

```java
public class HttpPipelineInitializer extends ChannelInitializer<Channel> {
    private final boolean client;

    public HttpPipelineInitializer(boolean client) {
        this.client = client;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (client) {
            pipeline.addLast("decoder", new HttpResponseDecoder());
            pipeline.addLast("encoder", new HttpRequestEncoder());
        } else {
            pipeline.addLast("decoder", new HttpRequestDecoder());
            pipeline.addLast("encoder", new HttpResponseEncoder());
        }
    }
}
```

**HTTP 메시지 집계**

HTTP 요청 및 응답은 여러 부분으로 구성될 수 있습니다. Netty는 메시지 부분을 `FullHttpRequest` 및 `FullHttpResponse` 메시지로 병합하는 집계기를 제공합니다.

```java
public class HttpAggregatorInitializer extends ChannelInitializer<Channel> {
    private final boolean isClient;

    public HttpAggregatorInitializer(boolean isClient) {
        this.isClient = isClient;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (isClient) {
            pipeline.addLast("codec", new HttpClientCodec());
        } else {
            pipeline.addLast("codec", new HttpServerCodec());
        }
        pipeline.addLast("aggregator", new HttpObjectAggregator(512 * 1024));
    }
}
```

**HTTP 압축**

Netty는 데이터 압축을 지원하는 `ChannelHandler` 구현을 제공합니다.

```java
public class HttpCompressionInitializer extends ChannelInitializer<Channel> {
    private final boolean isClient;

    public HttpCompressionInitializer(boolean isClient) {
        this.isClient = isClient;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (isClient) {
            pipeline.addLast("codec", new HttpClientCodec());
            pipeline.addLast("decompressor", new HttpContentDecompressor());
        } else {
            pipeline.addLast("codec", new HttpServerCodec());
            pipeline.addLast("compressor", new HttpContentCompressor());
        }
    }
}
```

#### 11.3 WebSocket 지원

Netty는 WebSocket 지원을 위한 광범위한 도구를 제공합니다.&#x20;

```java
public class WebSocketServerInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast(
            new HttpServerCodec(),
            new HttpObjectAggregator(65536),
            new WebSocketServerProtocolHandler("/websocket"),
            new TextFrameHandler(),
            new BinaryFrameHandler(),
            new ContinuationFrameHandler()
        );
    }
}
```

#### 11.4 유휴 연결 및 타임아웃

Netty는 유휴 연결 및 타임아웃을 감지하기 위한 여러 `ChannelHandler` 구현을 제공합니다.

**IdleStateHandler 예제**

```java
public class IdleStateHandlerInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new IdleStateHandler(0, 0, 60, TimeUnit.SECONDS));
        pipeline.addLast(new HeartbeatHandler());
    }

    public static final class HeartbeatHandler extends ChannelInboundHandlerAdapter {
        private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(
            Unpooled.copiedBuffer("HEARTBEAT", CharsetUtil.ISO_8859_1));

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                ctx.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate()).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            } else {
                super.userEventTriggered(ctx, evt);
            }
        }
    }
}
```

#### 11.5 대용량 데이터 쓰기

Netty는 NIO의 제로 복사 기능을 활용하여 파일의 내용을 네트워크로 전송할 때 복사 단계를 제거합니다.&#x20;

```java
FileInputStream in = new FileInputStream(file);
FileRegion region = new DefaultFileRegion(in.getChannel(), 0, file.length());
channel.writeAndFlush(region).addListener(new ChannelFutureListener() {
    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
        if (!future.isSuccess()) {
            Throwable cause = future.cause();
            // 오류 처리
        }
    }
});
```

#### 11.6 데이터 직렬화

Netty는 다양한 데이터 직렬화 옵션을 제공합니다.

**JDK 직렬화**

```java
public class JdkSerializationInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new ObjectEncoder());
        pipeline.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
    }
}
```

**JBoss Marshalling**

```java
public class MarshallingInitializer extends ChannelInitializer<Channel> {
    private final MarshallerProvider marshallerProvider;
    private final UnmarshallerProvider unmarshallerProvider;

    public MarshallingInitializer(UnmarshallerProvider unmarshallerProvider, MarshallerProvider marshallerProvider) {
        this.unmarshallerProvider = unmarshallerProvider;
        this.marshallerProvider = marshallerProvider;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new MarshallingDecoder(unmarshallerProvider));
        pipeline.addLast(new MarshallingEncoder(marshallerProvider));
        pipeline.addLast(new ObjectHandler());
    }
}
```

**프로토콜 버퍼**

```java
public class ProtoBufInitializer extends ChannelInitializer<Channel> {
    private final MessageLite lite;

    public ProtoBufInitializer(MessageLite lite) {
        this.lite = lite;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new ProtobufVarint32FrameDecoder());
        pipeline.addLast(new ProtobufEncoder());
        pipeline.addLast(new ProtobufDecoder(lite));
        pipeline.addLast(new ObjectHandler());
    }
}
```
