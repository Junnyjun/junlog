# TCP Channel

<mark style="color:orange;">NIO의 tcp는 blocking, non-blocking, asyncronized은 서로 구현방식이 전혀 다릅니다.</mark>

## Blocking Socket

### SocketChannel

ServerSocketChannel : buffer X  blocking 방식

SocketChannel : buffer O, blocking, non-blocking 방식&#x20;

<img src="../../../.gitbook/assets/file.drawing (5).svg" alt="" class="gitbook-drawing">

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



#### client

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

Server

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

Client

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

