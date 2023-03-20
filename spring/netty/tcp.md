# TCP

<details>

<summary>gradle</summary>

```
repositories {
    maven { url 'https://repo.spring.io/milestone' }
    mavenCentral()
}

dependencies {
    testImplementation 'org.jetbrains.kotlin:kotlin-test'
    implementation platform('io.projectreactor:reactor-bom:2020.0.24')
    implementation 'io.projectreactor.netty:reactor-netty-core'
    implementation 'io.projectreactor.netty:reactor-netty-http'
}
```

</details>

## Server

### Execute

서버를 실행하기 위해서는 TcpServer를 이용하여 생성해준다.

<pre class="language-kotlin" data-title="" data-overflow="wrap"><code class="lang-kotlin">import io.netty.channel.ChannelOption
import reactor.netty.tcp.TcpServer

val server:DisposableServer = TcpServer.create()
<strong>    .host("localhost")
</strong>    .port(9988)
    .bindNow()
    
server.onDispose()
      .block()
</code></pre>

### Handle

들어온 요청을 처리하기 위해선 handle의 inbound, outbound를 활용할 수 있

```kotlin
.handle { inbound, outbound ->
    outbound.sendString(Mono.just("hello"))
    inbound.receive()
        .asString()
        .doOnNext { println(it) }
        .then()
}
```

Inbound 된 요청을 console로 찍고,\
Hello라는 응답을 내려주는 handler이다.

### Interceptor

연결의 Lifecycle을 활용하여 인터셉터를 구현할 수 있다.

```kotlin
TcpServer.create()
    .doOnBind { println("Bind :: ${it.bindAddress()}") }
    .doOnConnection { println("Connected :: ${it.address()}")  }
    .doOnBound { println("Bound :: ${it.address()}") }
    .doOnUnbound { println("Unbound :: ${it.address()}") }
    .bindNow()
    .onDispose()
    .block()
    
> Bind :: localhost/<unresolved>:9988
> Bound :: /127.0.0.1:9988
```

### Option

상세option을 지정해 줄수 있으며 상세 옵션은 링크를 [참조](https://docs.oracle.com/javase/8/docs/technotes/guides/net/socketOpt.html) \
`.option({OPTION}, {VALUE})` 로 지정할 수있다

### Logger

Peer들간 트래픽을 확인하기 위한 로깅은 `.wiretap(true)`  로 설정할 수 있다



## Event Loop

<img src="../../.gitbook/assets/file.excalidraw.svg" alt="" class="gitbook-drawing">

이벤트 루프는 Netty의 핵심 요소 중 하나입니다. 이벤트 루프는 Netty의 비동기 처리 및 다중 스레드 모델의 핵심입니다.

이 쓰레드는 이벤트 루프 스레드라고도 합니다. 이벤트 루프는 네트워크 이벤트를 처리하고 처리된 이벤트를 해당 채널 핸들러로 전달합니다. 이벤트 루프는 또한 다른 쓰레드에서 발생한 작업을 처리하는 데 사용됩니다.

이벤트 루프는 무한 루프를 실행하고 채널에 대한 이벤트를 처리합니다. 이벤트 루프는 이벤트 큐를 유지하고 이벤트가 발생할 때마다 이를 처리합니다. 이벤트 루프는 또한 네트워크 연결의 수명 주기를 관리합니다.

Netty의 이벤트 루프는 다중 스레드 모델을 지원합니다. 이벤트 루프는 여러 쓰레드에서 사용될 수 있으며, 이러한 쓰레드는 이벤트 루프에서 처리해야 할 작업을 제출할 수 있습니다.

```kotlin
val loop = LoopResources.create("event-loop", 1, 4, true)

val server = TcpServer.create()
    .runOn(loop)
    .bindNow()
    .onDispose()
    .block()
```



## Client

