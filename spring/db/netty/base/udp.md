# UDP

## Server

### Execute

서버를 실행하기 위해서는 UdpServer를 이용하여 생성해준다.

```kotlin
val server: Connection = UdpServer.create() // (1)
    .port(9988)
    .host("localhost")
    .bindNow(Duration.ofSeconds(30)) // (2)
    .onDispose()
    .block()
```

### Handle

들어온 요청을 처리하기 위해선 handle의 inbound, outbound를 활용할 수 있

```kotlin
.handle { inbound, outbound ->
    outbound.sendString(Mono.just("hello"))
    inbound.receiveObject()
        .map { data -> data as DatagramPacket }
        .map { packet -> String(packet.data) }
        .doOnNext { println(it) }
        .then()
}
```

Inbound 된 요청을 console로 찍고,\
Hello라는 응답을 내려주는 handler이다.

### Interceptor

연결의 Lifecycle을 활용하여 인터셉터를 구현할 수 있다.

```kotlin
.doOnBind { println("Bind :: ${it.bindAddress()}") }
.doOnBound { println("Bound :: ${it.address()}") }
.doOnUnbound { println("Unbound :: ${it.address()}") }
```

### Option

상세option을 지정해 줄수 있으며 상세 옵션은 링크를 [참조](https://docs.oracle.com/javase/8/docs/technotes/guides/net/socketOpt.html)\
`.option({OPTION}, {VALUE})` 로 지정할 수있다

### Logger

Peer들간 트래픽을 확인하기 위한 로깅은 `.wiretap(true)` 로 설정할 수 있다

## Client

구현체만 UdpClient로 바뀌고 Server랑 동일하게 사용이 가능하다.

```kotlin
val connetor =  TcpClient.create()
        .port(9988)
        .host("localhost")
        .connectNow()
```

![](../../../../.gitbook/assets/Written-By-Human-Not-By-AI-Badge-black.svg)
