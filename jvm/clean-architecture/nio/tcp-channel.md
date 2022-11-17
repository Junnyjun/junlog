# TCP Channel

<mark style="color:orange;">NIO의 tcp는 blocking, non-blocking, asyncronized은 서로 구현방식이 전혀 다릅니다.</mark>

## Blocking Socket

### SocketChannel

ServerSocketChannel : buffer X  blocking 방식

SocketChannel : buffer O, blocking, non-blocking 방식&#x20;

<img src="../../../.gitbook/assets/file.drawing (10).svg" alt="" class="gitbook-drawing">

### Connection

#### Server

ServerSocket을 생성한 뒤 blocking으로 설정해줍니다.

```java
ServerSocketChannel serverSocketChannel = ServerSocketChannel.open(INET);
serverSocketChannel.configureBlocking(true);
serverSocketChannel.bind(new InetSocketAddress(8877));
```

클라이언트의 연결을 승인하기 위한 accept()를 실행합니다.

```java
SocketChannel accept = serverSocketChannel.accept();
```

{% hint style="info" %}
(InetSocketAddress) socketChannel.getRemoteAddress();  를사용하면\
클라이언트의 정보를 볼 수 있습니다.
{% endhint %}

연결을 종료하고 싶은 경우 close를 실행해 줍니다.

```java
serverSocketChannel.close();
```

#### Client

Socket을 생성한 뒤 blocking으로 설정해줍니다.

```java
SocketChannel socketChannel = SocketChannel.open(INET);
socketChannel.configureBlocking(true);
```

서버에 연결 요청을 보내기 위해 connet()를 실행합니다.

```java
socketChannel.connect(new InetSocketAddress("localhost", 8877));
```

연결 종료는 close를 실행해 줍니다.

```java
if (socketChannel.isConnected()) {
    socketChannel.close();
}
```

#### how do code?

#### Server

```java
public class Main {
    public static void main(String[] args) throws IOException{
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open(INET);
        serverSocketChannel.configureBlocking(true);
        serverSocketChannel.bind(new InetSocketAddress(8877));

        while (true) {
            SocketChannel socketChannel = serverSocketChannel.accept();
            InetSocketAddress inetSocketAddress = (InetSocketAddress) socketChannel.getRemoteAddress();

            System.out.println("CONNECTED !!! " + inetSocketAddress.getHostName());
        }
    }
}
```

#### Client

```java
public class Main {
    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open(INET);
        socketChannel.configureBlocking(true);

        socketChannel.connect(new InetSocketAddress("localhost", 8877));
        System.out.println("COMPLETE CONNECT");
        if (socketChannel.isConnected()) {
            socketChannel.close();
            System.out.println("COMPLETE CLOSE");
        }
    }
}
```

### Data Communication

Client의 연결 요청이 Server에서 수락되었다면,  read(), write()를 이용해 통신할 수 있습니다.

모두 버퍼를 가지고 있기 때문에 버퍼를 사용하여 데이터를 교환합니다.

#### Write

데이터 전송은 write를 사용하여 전송한다.

바이트코드로 전송하면된다.

```java
socketChannel.write(Charset.defaultCharset().encode("This is junny land"));
```

#### Read

전송된 데이터를 읽어오는 것은 Read를 사용한다.

바이트 코드로 들어온 값을 읽으면 된다.

```java
ByteBuffer buffer = ByteBuffer.allocate(1024);

socketChannel.read(buffer);
buffer.flip();
String result = Charset.defaultCharset().decode(buffer).toString();
```

read를 호출한 시점부터 상대방이 데이터를 보내주기전 까지는 항상 Blocking상태가 되는데,

블로킹이 해제되고 응답이 오는 경우는 세가지 입니다.

| reason      | response    |
| ----------- | ----------- |
| 데이터 전송받음    | 바이트 수       |
| 상대측 close() | -1          |
| 비정상적 종료     | IOException |



### Thread  Pool?

동기 방식의 서버 채널의 read는 응답을 기다리는 동안 blocking되기 때문에 작업이 뎌디다.

이런 문제는병렬 프로그래밍 방식 중 Tread pool을 이용하여 해결할 수 있다.

<img src="../../../.gitbook/assets/file.drawing (1) (1).svg" alt="" class="gitbook-drawing">

```java
this.executorService = newFixedThreadPool(getRuntime().availableProcessors());
```

executorService에 스레드풀 의 크기를 지정해 준뒤

```java
Runnable runnable = () -> {
    CharBuffer answer = Charset.defaultCharset().decode(BUFFER);
    System.out.println("READ ="+ answer);
};
executorService.submit(runnable);
```

Runnable에 실행할 작업을 작성한 뒤, executorService에 등록해준다



## Non-Blocking Socket

블로킹 방식은 Client가 연결 요청을 할 때마다 accept()에서 블로킹이 됩니다.

이와 반대로 non-blocking 방식은 connect(), accept(), read(), write()에서 블로킹이 되지 않습니다.



하지만 Blocking이 되지 않으므로 무한정 read,write를 실행하여 CPU 사용률을 증가 시킬수도 있기 때문에, event Listener인 Selector를 사용하여 특정 이벤트를 감지합니다.

<img src="../../../.gitbook/assets/file.drawing (1) (7).svg" alt="Selector Architecture" class="gitbook-drawing">

Selector는 일종의 멀티 플렉서 입니다.

{% hint style="info" %}
[멀티플렉서](https://ko.wikipedia.org/wiki/%EB%A9%80%ED%8B%B0%ED%94%8C%EB%A0%89%EC%84%9C) ? : 여러 입력중 하나를 선택해주는&#x20;
{% endhint %}

채널이 Selector에 등록 요청을 할때, 작업을 키로 설정한 뒤 관심 키셋(interest key)에 저장 시킵니다

관심 키셋에 등록된 키 중 작업 준비가 된 키는 key Set(selected keySet)에 저장한 뒤, 하나씩 꺼내어 채널작업을 처리하게 됩니다.



### Create Selector

#### 첫 단계로 Channel을 생성해 줍니다.

server) ServerSocetChannel

```java
ServerSocketChannel channel = ServerSocketChannel.open();
channel.configureBlocking(false);
channel.bind(new InetSocketAddress("localhost", 8080));
```

client) SocketChannel

```java
SocketChannel channel = SocketChannel.open();
channel.configureBlocking(false);
```

#### 두번째로 register()로 Selector에 등록합니다.

```java
Selector selector = Selector.open();
SelectionKey selectionKey = open.register(selector, {OPTION});
```

| Option                                       | Description                                                  |
| -------------------------------------------- | ------------------------------------------------------------ |
| <mark style="color:green;">OP\_ACCEPT</mark> | <mark style="color:green;">ServerSocketChannel의 연결 수락</mark> |
| OP\_CONNECT                                  | SocketChannel의 서버 연결                                         |
| OP\_READ                                     | SocketChannel의 데이터 읽기                                        |
| OP\_WRITE                                    | SocketChannel의 데이터 쓰기                                        |

