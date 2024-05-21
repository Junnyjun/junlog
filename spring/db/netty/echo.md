# Echo 서버 작성

Echo 서버는 클라이언트로부터 메시지를 받아 그대로 반환하는 서버입니다.

> ```groovy
> implementation 'org.springframework.boot:spring-boot-starter-webflux'
> ```

**2.1 ChannelHandler 구현**

먼저 클라이언트의 요청을 처리하는 `ChannelHandler`를 구현합니다. 이 핸들러는 수신된 메시지를 그대로 반환합니다.

```kotlin
@Component
class EchoServerHandler : ChannelInboundHandlerAdapter() {
    private val logger =  LoggerFactory.getILoggerFactory().getLogger(EchoServerHandler::class.java.name)

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        logger.info("[Echo] Received: ${(msg as ByteBuf).toString(Charset.defaultCharset())}")
        ctx.write(msg)
    }

    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
            .addListener(CLOSE)
    }

    override fun channelActive(ctx: ChannelHandlerContext) {
        logger.info("[Echo] Connected to client")
    }
}
```

**2.2 서버 부트스트랩 설정**

서버를 설정하고 시작하기 위한 부트스트랩 코드를 작성합니다.

```kotlin
@Configuration
class EchoServer (
    @Value("\${netty.port:28080}")
    private val port: Int,
    private val echoServerHandler: EchoServerHandler,
) {
    private val logger: Logger = LoggerFactory.getLogger(EchoServer::class.java)
    private val group: NioEventLoopGroup = NioEventLoopGroup()
    private val serverBootstrap: ServerBootstrap = ServerBootstrap()

    @Bean
    fun start(): ServerBootstrap {
        logger.info("[Echo] Starting server on port $port")
        return serverBootstrap.group(group)
            .channel(NioServerSocketChannel::class.java)
            .localAddress(port)
            .childHandler(echoServerHandler)
            .also { it.bind().sync() }
    }

    @PreDestroy
    fun stop() {
        logger.info("[Echo] Stopping server")
        group.shutdownGracefully().sync()
    }
}
```

#### 3. Echo 클라이언트 작성

Echo 서버와 통신할 클라이언트를 작성해보겠습니다.

**3.1 ChannelHandler 구현**

클라이언트의 `ChannelHandler`는 서버로부터 수신된 데이터를 처리합니다.

```kotlin
class EchoClientHandler : SimpleChannelInboundHandler<ByteBuf>() {
    private val logger = getILoggerFactory().getLogger(EchoClientHandler::class.java.name)
    override fun channelRead0(p0: ChannelHandlerContext?, p1: ByteBuf?) {
        logger.info("[Echo] Received: ${p1?.toString(Charset.defaultCharset())}")
    }
    override fun channelActive(ctx: ChannelHandlerContext) {
        logger.info("[Echo] Connected to server")
        ctx.writeAndFlush(Unpooled.copiedBuffer("Hello, World!".toByteArray()))
    }
}
```

**3.2 클라이언트 부트스트랩 설정**

클라이언트 부트스트랩 코드는 서버에 연결하고 메시지를 전송하는 작업을 처리합니다.

```kotlin
@Test
fun echoPingTest() {
    val echoClientHandler = EchoClientHandler()
    val group = NioEventLoopGroup()
    val serverBootstrap = Bootstrap()
        .group(group)
        .channel(NioSocketChannel::class.java)
        .remoteAddress("localhost", 28080)
        .handler(object : ChannelInitializer<SocketChannel>() {
            override fun initChannel(ch: SocketChannel) {
                ch.pipeline().addLast(echoClientHandler)
            }
        })
    val channelFuture = serverBootstrap.connect().sync()
    channelFuture.channel().closeFuture().sync()
}
```

#### 4. 애플리케이션 실행

이제 서버와 클라이언트를 실행하여 Netty 애플리케이션을 테스트해보겠습니다.

1. 먼저 `EchoServer`의 `main` 메서드를 실행하여 서버를 시작합니다.
2. 그런 다음 `EchoClient`의 `main` 메서드를 실행하여 클라이언트를 시작합니다.



클라이언트는 서버에 메시지를 전송하고, 서버는 그 메시지를 그대로 클라이언트에 반환합니다.
