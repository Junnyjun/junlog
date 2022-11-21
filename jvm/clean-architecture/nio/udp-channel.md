# UDP Channel

UDP를 사용하기 위해선 `DatagramChannel` 을 이용해 주면 됩니다.

```java
DatagramChannel datagramChannel = DatagramChannel.open(StandardProtocolFamily.INET);
```



### How do send?

채널을 연뒤, send()를 이용하여 buffer를 전송할 수 있다.

```java
public class Main {
    public static void main(String[] args) throws IOException {
        DatagramChannel datagramChannel = DatagramChannel.open(StandardProtocolFamily.INET);

        System.out.println("Server started");

        ByteBuffer buffer = Charset.defaultCharset().encode("Hello Junny");
        datagramChannel.send(buffer, new InetSocketAddress("localhost", 7788));
        System.out.println("Send to server ");

        System.out.println("Close to server");
        datagramChannel.close();
    }
}
```

### How to receive?

채널을 연뒤, `bind()` 로 채널의 포트를 지정해줍니다.

receive는 Blocking이므로 응답이 올 때까지 기다립니다.

```java
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        DatagramChannel datagramChannel = DatagramChannel.open(StandardProtocolFamily.INET);

        datagramChannel.bind(new InetSocketAddress(7788));

        new Thread(() -> {
            System.out.println("Start Server");

            System.out.println("Waiting Client");
            while (true) {
                try {
                    ByteBuffer byteBuffer = ByteBuffer.allocate(10000);
                    SocketAddress receive = datagramChannel.receive(byteBuffer);
                    System.out.println("Client Message: " + UTF_8.decode(byteBuffer));
                } catch (IOException e) {
                    System.out.println("Close Server");
                    throw new RuntimeException(e);
                }
            }
        }).start();


        Thread.sleep(1000000L);
        datagramChannel.close();
    }
}
```

