# Bootstrapping

### 8.1 부트스트랩 클래스

부트스트랩 클래스 계층 구조는 추상 부모 클래스와 두 개의 구체적인 부트스트랩 하위 클래스로 구성됩니다.&#x20;

즉, 서버는 클라이언트의 연결을 수락하고 그들과 통신하기 위해 자식 채널을 생성하는 부모 채널을 사용하지만, 클라이언트는 대부분의 네트워크 상호작용을 위해 단일 비부모 채널만 필요로 합니다.&#x20;

클라이언트 또는 서버에 특정한 단계를 제외하고, 부트스트랩 과정의 공통 단계는 `AbstractBootstrap`에서 처리되고, 각각 `Bootstrap` 또는 `ServerBootstrap`에서 처리됩니다.

### 8.2 클라이언트와 비연결형 프로토콜 부트스트래핑

`Bootstrap`은 클라이언트 또는 비연결형 프로토콜을 사용하는 애플리케이션에서 사용됩니다.&#x20;

| 이름                                                       | 설명                                                      |
| -------------------------------------------------------- | ------------------------------------------------------- |
| `Bootstrap group(EventLoopGroup)`                        | 채널의 모든 이벤트를 처리할 `EventLoopGroup`을 설정합니다.                |
| `Bootstrap channel(Class<? extends C>)`                  | 채널 구현 클래스를 지정합니다.                                       |
| `Bootstrap channelFactory(ChannelFactory<? extends C>)`  | `bind()`에 의해 호출될 팩토리 클래스를 지정합니다.                        |
| `Bootstrap localAddress(SocketAddress)`                  | 채널이 바인드될 로컬 주소를 지정합니다.                                  |
| `<T> Bootstrap option(ChannelOption<T> option, T value)` | 새로 생성된 채널의 `ChannelConfig`에 적용될 `ChannelOption`을 설정합니다. |
| `<T> Bootstrap attr(Attribute<T> key, T value)`          | 새로 생성된 채널의 속성을 지정합니다.                                   |
| `Bootstrap handler(ChannelHandler)`                      | 이벤트 알림을 받을 `ChannelHandler`를 설정합니다.                     |
| `Bootstrap clone()`                                      | 현재 `Bootstrap`의 복제본을 생성합니다.                             |
| `Bootstrap remoteAddress(SocketAddress)`                 | 원격 주소를 설정합니다.                                           |
| `ChannelFuture connect()`                                | 원격 피어에 연결하고 연결 작업이 완료되면 `ChannelFuture`를 반환합니다.         |
| `ChannelFuture bind()`                                   | 채널을 바인드하고 바인드 작업이 완료되면 `ChannelFuture`를 반환합니다.          |

#### 8.2.1 클라이언트 부트스트래핑

`Bootstrap` 클래스는 클라이언트 및 비연결형 프로토콜을 사용하는 애플리케이션에서 채널을 생성하는 책임을 집니다.&#x20;

```kotlin
fun main() {
    val group: EventLoopGroup = NioEventLoopGroup()
    try {
        val bootstrap = Bootstrap()
        bootstrap.group(group)
            .channel(NioSocketChannel::class.java)
            .handler(object : SimpleChannelInboundHandler<ByteBuf>() {
                override fun channelRead0(ctx: ChannelHandlerContext, msg: ByteBuf) {
                    println("Received data")
                }
            })

        val future = bootstrap.connect(InetSocketAddress("www.example.com", 80))
        future.syncUninterruptibly()
    } finally {
        group.shutdownGracefully()
    }
}
```

#### 8.2.2 채널과 EventLoopGroup 호환성

Netty에서는 `EventLoopGroup`과 `Channel` 구현이 호환되어야 합니다. \
`NIO` 및 `OIO` 전송을 위한 관련 `EventLoopGroup`과 `Channel` 구현이 있습니다.

```kotlin
fun main() {
    val group = NioEventLoopGroup()
    try {
        val bootstrap = Bootstrap()
        bootstrap.group(group)
            .channel(NioSocketChannel::class.java)
            // .channel(OioSocketChannel::class.java) // 호환되지 않는 구현
            .handler(object : SimpleChannelInboundHandler<ByteBuf>() {
                override fun channelRead0(ctx: ChannelHandlerContext, msg: ByteBuf) {
                    println("Received data")
                }
            })
        val future = bootstrap.connect(InetSocketAddress("www.example.com", 80))
        future.syncUninterruptibly()
    } finally {
        group.shutdownGracefully()
    }
}
```

&#x20;`NioEventLoopGroup`과 `OioSocketChannel`을 혼합하여 사용하려고 시도하기 때문에 `IllegalStateException`을 발생시킵니다.

부트스트래핑 시, `bind()` 또는 `connect()`를 호출하기 전에 다음 메서드를 호출하여 필요한 구성 요소를 설정해야 합니다.

* `group()`
* `channel()` 또는 `channelFactory()`
* `handler()`

이 작업을 수행하지 않으면 `IllegalStateException`이 발생합니다. 특히 `handler()` 호출은 `ChannelPipeline`을 구성하는 데 필요하므로 매우 중요합니다.

#### 8.3 서버 부트스트래핑

서버 부트스트래핑을 개요로 ServerBootstrap API를 설명한 후, 서버 부트스트래핑에 필요한 단계와 관련된 몇 가지 주제를 설명합니다. 여기에는 서버 채널에서 클라이언트를 부트스트랩하는 특별한 경우도 포함됩니다.

#### 8.3.1 ServerBootstrap 클래스

다음 표는 ServerBootstrap 클래스의 메서드를 나열합니다.

| Name           | Description                                                                                       |
| -------------- | ------------------------------------------------------------------------------------------------- |
| group          | ServerBootstrap에서 사용할 EventLoopGroup을 설정합니다. 이 EventLoopGroup은 ServerChannel과 수락된 채널의 I/O를 처리합니다. |
| channel        | 인스턴스화할 ServerChannel 클래스를 설정합니다.                                                                  |
| channelFactory | Channel을 기본 생성자를 통해 생성할 수 없는 경우 ChannelFactory를 제공합니다.                                            |
| localAddress   | ServerChannel이 바인딩할 로컬 주소를 지정합니다. 지정하지 않으면 OS에서 무작위로 할당됩니다.                                       |
| option         | 새로 생성된 ServerChannel의 ChannelConfig에 적용할 ChannelOption을 지정합니다.                                    |
| childOption    | 수락된 채널의 ChannelConfig에 적용할 ChannelOption을 지정합니다.                                                  |
| attr           | ServerChannel에 대한 속성을 지정합니다. bind() 호출 후에는 변경할 수 없습니다.                                            |
| childAttr      | 수락된 채널에 대한 속성을 적용합니다. 이후 호출은 효과가 없습니다.                                                            |
| handler        | ServerChannel의 ChannelPipeline에 추가할 ChannelHandler를 설정합니다.                                        |
| childHandler   | 수락된 채널의 ChannelPipeline에 추가할 ChannelHandler를 설정합니다.                                               |
| clone          | 원본 ServerBootstrap과 동일한 설정으로 다른 원격 피어에 연결하기 위해 ServerBootstrap을 복제합니다.                            |
| bind           | ServerChannel을 바인딩하고, 연결 작업이 완료되면 ChannelFuture를 반환합니다.                                           |

#### 8.3.2 서버 부트스트래핑

서버 채널은 `bind()`가 호출될 때 생성됩니다.&#x20;

새로운 채널은 ServerChannel이 연결을 수락할 때 생성됩니다.

```kotlin
fun main() {
    val group = NioEventLoopGroup()
    try {
        val bootstrap = ServerBootstrap()
        bootstrap.group(group)
            .channel(NioServerSocketChannel::class.java)
            .childHandler(object : ChannelInitializer<SocketChannel>() {
                override fun initChannel(ch: SocketChannel) {
                    ch.pipeline().addLast(object : SimpleChannelInboundHandler<ByteBuf>() {
                        override fun channelRead0(ctx: ChannelHandlerContext, msg: ByteBuf) {
                            println("Received data")
                        }
                    })
                }
            })

        val future: ChannelFuture = bootstrap.bind(InetSocketAddress(8080))
        future.sync()
    } finally {
        group.shutdownGracefully()
    }
}          
```

#### 8.5 부트스트랩 중 여러 ChannelHandler 추가

Netty는 ChannelPipeline에 여러 ChannelHandler를 추가할 수 있는 기능을 제공합니다.&#x20;

부트스트랩 과정에서 `handler()` 또는 `childHandler()`를 호출하여 단일 ChannelHandler를 추가하는 것 외에, 여러 ChannelHandler를 추가하려면 `ChannelInitializer` 클래스를 사용합니다.

```kotlin
fun main() {
    val group = NioEventLoopGroup()
    try {
        val bootstrap = ServerBootstrap()
        bootstrap.group(group)
            .channel(NioServerSocketChannel::class.java)
            .childHandler(object : ChannelInitializer<Channel>() {
                override fun initChannel(ch: Channel) {
                    val pipeline: ChannelPipeline = ch.pipeline()
                    pipeline.addLast(HttpClientCodec())
                    pipeline.addLast(HttpObjectAggregator(Int.MAX_VALUE))
                }
            })

        val future = bootstrap.bind(InetSocketAddress(8080))
        future.sync()
    } finally {
        group.shutdownGracefully()
    }
}
```

#### 8.6 Netty ChannelOptions 및 속성 사용하기

모든 채널을 수동으로 구성하는 것은 매우 번거로울 수 있습니다. 다행히도, 부트스트랩에서 `option()`을 사용하여 ChannelOptions을 적용할 수 있습니다.&#x20;

제공된 값들은 부트스트랩에서 생성된 모든 채널에 자동으로 적용됩니다. ChannelOptions에는 keep-alive나 timeout 속성 및 버퍼 설정과 같은 저수준의 연결 세부 사항이 포함됩니다.

```kotlin
fun main() {
    val id: AttributeKey<Int> = AttributeKey.valueOf("ID")
    val group = NioEventLoopGroup()

    try {
        val bootstrap = Bootstrap()
        bootstrap.group(group)
            .channel(NioSocketChannel::class.java)
            .handler(object : SimpleChannelInboundHandler<ByteBuf>() {
                override fun channelRegistered(ctx: ChannelHandlerContext) {
                    val idValue = ctx.channel().attr(id).get()
                    // ID 값으로 작업 수행
                }

                override fun channelRead0(ctx: ChannelHandlerContext, byteBuf: ByteBuf) {
                    println("Received data")
                }
            })

        bootstrap.option(ChannelOption.SO_KEEPALIVE, true)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
        bootstrap.attr(id, 123456)

        val future: ChannelFuture = bootstrap.connect(InetSocketAddress("www.junnyalnd.com", 80))
        future.syncUninterruptibly()
    } finally {
        group.shutdownGracefully()
    }
}
```

#### 8.7 DatagramChannels 부트스트래핑

이전 부트스트랩 코드 예제는 TCP 기반의 SocketChannel을 사용했지만, 부트스트랩은 비연결 프로토콜에도 사용할 수 있습니다.&#x20;

Netty는 이 목적을 위해 다양한 DatagramChannel 구현을 제공합니다. 유일한 차이점은 `connect()`를 호출하지 않고 `bind()`만 호출한다는 것입니다.

```kotlin
fun main() {
    val group = NioEventLoopGroup()
    try {
        val bootstrap = Bootstrap()
        bootstrap.group(group)
            .channel(OioDatagramChannel::class.java)
            .handler(object : SimpleChannelInboundHandler<DatagramPacket>() {
                override fun channelRead0(ctx: ChannelHandlerContext, msg: DatagramPacket) {
                    // 패킷 처리
                }
            })

        val future: ChannelFuture = bootstrap.bind(InetSocketAddress(0))
        future.sync()
    } finally {
        group.shutdownGracefully()
    }
}
```

#### 8.8 Shutdown

부트스트래핑을 통해 애플리케이션을 실행할 수 있지만, 결국에는 애플리케이션을 종료해야 하는 시점이 올 것입니다. 물론, JVM이 종료될 때까지 기다릴 수 있지만, 이는 자원을 깨끗하게 해제하는 것을 의미하는 '우아한 종료(graceful shutdown)'의 정의에 맞지 않습니다.&#x20;

우선, `EventLoopGroup`을 종료해야 합니다. 이는 모든 보류 중인 이벤트와 작업을 처리한 후 모든 활성 스레드를 해제합니다.&#x20;

```kotlin
fun main() {
    val group = NioEventLoopGroup()
    val bootstrap = Bootstrap()
    bootstrap.group(group)
        .channel(NioSocketChannel::class.java)
    
    // 부트스트랩을 설정하고 연결하는 로직을 여기에 추가

    val future = group.shutdownGracefully()
    // 그룹이 종료될 때까지 블록
    future.syncUninterruptibly()
}
```
