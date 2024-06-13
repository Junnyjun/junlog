# Bootstrapping

### 8.1 부트스트랩 클래스

부트스트랩 클래스 계층 구조는 추상 부모 클래스와 두 개의 구체적인 부트스트랩 하위 클래스로 구성됩니다.&#x20;

즉, 서버는 클라이언트의 연결을 수락하고 그들과 통신하기 위해 자식 채널을 생성하는 부모 채널을 사용하지만, 클라이언트는 대부분의 네트워크 상호작용을 위해 단일 비부모 채널만 필요로 합니다.&#x20;

이전에 공부한 Netty 구성 요소 중 여러 가지가 부트스트랩 과정에 참여하며, 이들 중 일부는 클라이언트와 서버 모두에서 사용됩니다. 클라이언트 또는 서버에 특정한 단계를 제외하고, 부트스트랩 과정의 공통 단계는 `AbstractBootstrap`에서 처리되고, 각각 `Bootstrap` 또는 `ServerBootstrap`에서 처리됩니다.

### 8.2 클라이언트와 비연결형 프로토콜 부트스트래핑

`Bootstrap`은 클라이언트 또는 비연결형 프로토콜을 사용하는 애플리케이션에서 사용됩니다. 다음 표는 이 클래스의 개요를 보여줍니다.

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

```java
public class ClientBootstrapExample {
    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                                System.out.println("Received data");
                            }
                        });
                    }
                });

            ChannelFuture future = bootstrap.connect(new InetSocketAddress("www.example.com", 80));
            future.addListener((ChannelFuture f) -> {
                if (f.isSuccess()) {
                    System.out.println("Connection established");
                } else {
                    System.err.println("Connection attempt failed");
                    f.cause().printStackTrace();
                }
            });
            future.sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
```

#### 8.2.2 채널과 EventLoopGroup 호환성

Netty에서는 `EventLoopGroup`과 `Channel` 구현이 호환되어야 합니다. `NIO` 및 `OIO` 전송을 위한 관련 `EventLoopGroup`과 `Channel` 구현이 있습니다.

**호환 가능한 `EventLoopGroup` 및 `Channel` 구현 예제:**

```java
java코드 복사EventLoopGroup group = new NioEventLoopGroup();
Bootstrap bootstrap = new Bootstrap();
bootstrap.group(group)
    .channel(NioSocketChannel.class)
    .handler(new SimpleChannelInboundHandler<ByteBuf>() {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
            System.out.println("Received data");
        }
    });

ChannelFuture future = bootstrap.connect(new InetSocketAddress("www.example.com", 80));
future.syncUninterruptibly();
```

**호환되지 않는 `EventLoopGroup` 및 `Channel` 구현 예제:**

```java
java코드 복사EventLoopGroup group = new NioEventLoopGroup();
Bootstrap bootstrap = new Bootstrap();
bootstrap.group(group)
    .channel(OioSocketChannel.class) // 호환되지 않는 구현
    .handler(new SimpleChannelInboundHandler<ByteBuf>() {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
            System.out.println("Received data");
        }
    });

ChannelFuture future = bootstrap.connect(new InetSocketAddress("www.example.com", 80));
future.syncUninterruptibly(); // IllegalStateException 발생
```

이 코드는 `NioEventLoopGroup`과 `OioSocketChannel`을 혼합하여 사용하려고 시도하기 때문에 `IllegalStateException`을 발생시킵니다.

부트스트래핑 시, `bind()` 또는 `connect()`를 호출하기 전에 다음 메서드를 호출하여 필요한 구성 요소를 설정해야 합니다.

* `group()`
* `channel()` 또는 `channelFactory()`
* `handler()`

이 작업을 수행하지 않으면 `IllegalStateException`이 발생합니다. 특히 `handler()` 호출은 `ChannelPipeline`을 구성하는 데 필요하므로 매우 중요합니다.
