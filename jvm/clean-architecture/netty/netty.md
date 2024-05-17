# Netty 등장의 이유

### Java 네트워크 프로그래밍의 역사

Java는 네트워크 프로그래밍을 위해 초기에는 `java.net` 패키지를 제공했습니다.&#x20;

이 패키지는 차단(blocking) 입출력(I/O) 방식을 사용했습니다. 차단형 I/O는 한 번에 하나의 연결만 처리할 수 있어, 여러 클라이언트가 동시에 연결되면 각 연결마다 새로운 스레드를 생성해야 했습니다.&#x20;

이는 시스템 자원의 낭비를 초래할 수 있습니다.

다음은 차단형 I/O를 사용한 간단한 서버 예제입니다:

```java
ServerSocket serverSocket = new ServerSocket(8080);
Socket clientSocket = serverSocket.accept();
BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

String request, response;
while ((request = in.readLine()) != null) {
    if ("Done".equals(request)) {
        break;
    }
    response = processRequest(request);
    out.println(response);
}
```

위 코드는 클라이언트의 요청을 처리하고 응답을 반환하는 단순한 서버입니다. \
하지만 이 방식은 많은 클라이언트를 동시에 처리하기에는 비효율적입니다.

### 비차단형 I/O와 셀렉터(Selectors)

Java는 `java.nio` 패키지를 통해 비차단형(non-blocking) I/O를 도입했습니다. 비차단형 I/O는 네트워크 호출이 완료될 때까지 기다리지 않고 즉시 반환합니다. 이를 통해 단일 스레드로 여러 연결을 효율적으로 관리할 수 있습니다.

비차단형 I/O의 핵심은 셀렉터(Selector)입니다. 셀렉터는 여러 채널을 감시하고, 어느 채널이 I/O 작업을 수행할 준비가 되었는지 알려줍니다. 다음은 셀렉터를 사용하는 예제입니다:

```java
Selector selector = Selector.open();
ServerSocketChannel serverSocket = ServerSocketChannel.open();
serverSocket.bind(new InetSocketAddress(8080));
serverSocket.configureBlocking(false);
serverSocket.register(selector, SelectionKey.OP_ACCEPT);

while (true) {
    selector.select();
    Set<SelectionKey> selectedKeys = selector.selectedKeys();
    Iterator<SelectionKey> iter = selectedKeys.iterator();
    while (iter.hasNext()) {
        SelectionKey key = iter.next();
        if (key.isAcceptable()) {
            register(selector, serverSocket);
        }
        if (key.isReadable()) {
            answerWithEcho(selector, key);
        }
        iter.remove();
    }
}
```

위 코드는 비차단형 I/O와 셀렉터를 사용하여 여러 연결을 단일 스레드로 관리하는 예제입니다.

### Netty 소개

Netty는 비차단형 네트워크 애플리케이션을 쉽게 개발할 수 있도록 돕는 프레임워크입니다. \
Netty의 주요 특징은 다음과 같습니다:

* 다양한 전송 유형에 대한 통합된 API 제공
* 강력한 스레드 모델
* 완전한 비동기 I/O 지원
* 낮은 지연시간과 높은 처리량

Netty를 사용하면 네트워크 애플리케이션의 성능을 극대화할 수 있으며, \
코드를 간결하고 유지보수하기 쉽게 작성할 수 있습니다.

### Netty의 주요 컴포넌트

Netty는 다음과 같은 주요 컴포넌트를 제공합니다:

#### **채널(Channel)**

채널은 네트워크 연결을 추상화한 객체로, 데이터의 입출력 작업을 처리합니다. 이는 Java NIO의 `java.nio.channels.Channel` 인터페이스를 기반으로 합니다. 채널은 비차단형 I/O 작업을 수행하며, 네트워크 소켓이나 파일과 같은 다양한 I/O 소스를 추상화합니다.

#### **콜백(Callback)**

콜백은 특정 이벤트가 발생했을 때 호출될 메서드를 제공하는 방식입니다. Netty는 이벤트가 발생할 때 호출되는 여러 가지 콜백 메서드를 제공합니다. 예를 들어, 클라이언트가 서버에 연결되면 `channelActive` 메서드가 호출됩니다.

#### **퓨처(Future)**

퓨처는 비동기 작업의 결과를 나타내며, 작업이 완료되면 결과를 제공합니다. Netty는 `ChannelFuture` 인터페이스를 사용하여 비동기 작업의 결과를 처리합니다. 이는 비동기 작업이 완료되었을 때 알림을 받거나 결과를 확인할 수 있는 방법을 제공합니다.

#### **이벤트와 핸들러(Event and Handler)**

이벤트는 상태 변화나 작업 완료를 알리는 역할을 합니다. 핸들러는 이러한 이벤트를 처리하는 로직을 구현합니다. Netty는 다양한 이벤트를 처리할 수 있는 `ChannelHandler` 인터페이스를 제공합니다. 예를 들어, `ChannelInboundHandler`는 수신된 데이터를 처리하고, `ChannelOutboundHandler`는 송신할 데이터를 처리합니다.
