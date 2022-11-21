# TCP Channel (Non-Blocking)

## Non-Blocking Socket

블로킹 방식은 Client가 연결 요청을 할 때마다 accept()에서 블로킹이 됩니다.

이와 반대로 Non-blocking 방식은 connect() accept() read() write()에서 블로킹이 되지 않습니다.



하지만 Blocking이 되지 않으므로 무한정 read,write를 실행하여 CPU 사용률을 증가 시킬수도 있기 때문에, event Listener인 Selector를 사용하여 특정 이벤트를 감지합니다.

<img src="../../../../.gitbook/assets/file.drawing (1) (7).svg" alt="Selector Architecture" class="gitbook-drawing">

Selector는 일종의 멀티 플렉서 입니다.

{% hint style="info" %}
[멀티플렉서](https://ko.wikipedia.org/wiki/%EB%A9%80%ED%8B%B0%ED%94%8C%EB%A0%89%EC%84%9C) ?  여러 입력중 하나를 선택
{% endhint %}

채널이 Selector에 등록 요청을 할때, 작업을 키로 설정한 뒤 관심 키셋(interest key)에 저장 시킵니다

관심 키셋에 등록된 키 중 작업 준비가 된 키는 key Set(selected keySet)에 저장한 뒤, 하나씩 꺼내어 채널작업을 처리하게 됩니다.

넌블로킹 방식은 스레드풀을 이용하여 적은양의 스레드로도 많은 작업을 처리할 수 있습니다.



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

#### 두번째 단계계로 register()로 Selector에 등록합니다.

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

동일한 소켓 채널은 한가지의 작업 유형(option)만 처리할 수 있습니다.

읽기를 등록한 뒤 쓰기로 변경하기 위해선 기존 selectionKey를 수정해야 합니다

{% code title="연결 후 옵션 수정" %}
```java
SelectionKey selectionKey = client.register(clientSelector, SelectionKey.OP_CONNECT);
selectionKey.interestOps(SelectionKey.OP_READ);
selectionKey.interestOps(SelectionKey.OP_WRITE);
```
{% endcode %}

#### 세번째 단계로 Select를 호출합니다.

등록된 SelectionKey에서 하나 이상의 채널의 작업 준비 응답을 기다립니다.

{% code title="select()는 blocking입니다" %}
```java
clientSelector.select(); // 응답 대기
clientSelector.select(1000L); // 1초 대기
clientSelector.selectNow(); // 즉시 호출 없으면 0
```
{% endcode %}

select()는채널의 준비 완료 응답, Selector의 wakeUp(), Thread Interrupt에 의해 리턴됩니다.

```java
Selector clientSelector = Selector.open();
SelectionKey selectionKey = client.register(clientSelector, SelectionKey.OP_CONNECT);
selectionKey.interestOps(SelectionKey.OP_WRITE);
clientSelector.wakeup();
```



### Channel  Process

키셋에서 SelectionKey들을 받아와 유형별로 작업을 처리합니다.

```java
public class SelectorWorker extends Thread {
    private final Selector selector;

    public SelectorWorker(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void run() {
        while (true) {
                if (isSelectActive()) continue;
                findOption();
        }
    }

    private boolean isSelectActive(){
        try {
            if (selector.select() == 0) {
                return true;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    private void findOption() {
        selector.selectedKeys().forEach(selectionKey -> {
            switch (selectionKey.readyOps()) {
                case SelectionKey.OP_ACCEPT -> System.out.println("OPTION = OP_ACCEPT");
                case SelectionKey.OP_CONNECT -> System.out.println("OPTION = OP_CONNECT");
                case SelectionKey.OP_READ -> System.out.println("OPTION = OP_READ");
                case SelectionKey.OP_WRITE -> System.out.println("OPTION = OP_WRITE");
                default -> System.out.println("OPTION = UNKNOWN");
            }
        });
    }
}
```

채널 객체는 selectionKey에서 가져올 수 있습니다.

```java
ServerSocketChannel channel = (ServerSocketChannel) selectionKey.channel();
```

채널 객체 외에 다른 객체도 selectionKey에 주입할 수 있는데, 객체를 등록한 뒤 selectionKey에서 꺼내서 사용하면 됩니다.

```java
SelectionKey selectionKey = client.register(clientSelector, SelectionKey.OP_CONNECT);
selectionKey.attach(new Attached());

selector.selectedKeys().forEach(selectionKey -> {
Attached attached = (Attached) selectionKey.attachment();
});
```
