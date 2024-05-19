# Echo 서버 작성

Echo 서버는 클라이언트로부터 메시지를 받아 그대로 반환하는 서버입니다.

**2.1 ChannelHandler 구현**

먼저 클라이언트의 요청을 처리하는 `ChannelHandler`를 구현합니다. 이 핸들러는 수신된 메시지를 그대로 반환합니다.

```kotlin
kotlin코드 복사import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

class EchoServerHandler : ChannelInboundHandlerAdapter() {
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        ctx.write(msg) // 받은 메시지를 그대로 씀
        ctx.flush() // 즉시 플러시
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace() // 예외가 발생하면 스택 트레이스를 출력
        ctx.close() // 채널을 닫음
    }
}
```

**2.2 서버 부트스트랩 설정**

서버를 설정하고 시작하기 위한 부트스트랩 코드를 작성합니다.

```kotlin
kotlin코드 복사import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel

class EchoServer(private val port: Int) {
    fun start() {
        val bossGroup: EventLoopGroup = NioEventLoopGroup()
        val workerGroup: EventLoopGroup = NioEventLoopGroup()
        try {
            val b = ServerBootstrap()
            b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel::class.java)
                .childHandler(object : ChannelInitializer<SocketChannel>() {
                    @Override
                    public override fun initChannel(ch: SocketChannel) {
                        ch.pipeline().addLast(EchoServerHandler())
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)

            val f = b.bind(port).sync()
            f.channel().closeFuture().sync()
        } finally {
            workerGroup.shutdownGracefully()
            bossGroup.shutdownGracefully()
        }
    }
}

fun main() {
    val port = 8080
    EchoServer(port).start()
}
```

#### 3. Echo 클라이언트 작성

Echo 서버와 통신할 클라이언트를 작성해보겠습니다.

**3.1 ChannelHandler 구현**

클라이언트의 `ChannelHandler`는 서버로부터 수신된 데이터를 처리합니다.

```kotlin
kotlin코드 복사import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

class EchoClientHandler : ChannelInboundHandlerAdapter() {
    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        println("Received from server: $msg")
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        ctx.close()
    }
}
```

**3.2 클라이언트 부트스트랩 설정**

클라이언트 부트스트랩 코드는 서버에 연결하고 메시지를 전송하는 작업을 처리합니다.

```kotlin
kotlin코드 복사import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel

class EchoClient(private val host: String, private val port: Int) {
    fun start() {
        val group: EventLoopGroup = NioEventLoopGroup()
        try {
            val b = Bootstrap()
            b.group(group)
                .channel(NioSocketChannel::class.java)
                .handler(object : ChannelInitializer<SocketChannel>() {
                    @Override
                    public override fun initChannel(ch: SocketChannel) {
                        ch.pipeline().addLast(EchoClientHandler())
                    }
                })

            val f = b.connect(host, port).sync()
            f.channel().closeFuture().sync()
        } finally {
            group.shutdownGracefully()
        }
    }
}

fun main() {
    val host = "localhost"
    val port = 8080
    EchoClient(host, port).start()
}
```

#### 4. 애플리케이션 실행

이제 서버와 클라이언트를 실행하여 Netty 애플리케이션을 테스트해보겠습니다.

1. 먼저 `EchoServer`의 `main` 메서드를 실행하여 서버를 시작합니다.
2. 그런 다음 `EchoClient`의 `main` 메서드를 실행하여 클라이언트를 시작합니다.



클라이언트는 서버에 메시지를 전송하고, 서버는 그 메시지를 그대로 클라이언트에 반환합니다.
