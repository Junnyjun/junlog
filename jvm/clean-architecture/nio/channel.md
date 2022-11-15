---
description: Nio의 읽기&쓰기
---

# Channel

## Synchronized Channel

FileChannel 은 open(), FileInputStream, FileOutputStream의 getChannel()를  사용하여  호출할 수  있습니다.

```java
FileChannel fileChannel = FileChannel.open(Paths.get("./junny.txt"), WRITE, READ, CREATE_NEW);
```



### How do Write?

fileChannel에 Write 옵션을 준 후 아래와 같이 사용 할 수 있습니다.

```java
Charset charset = Charset.defaultCharset();
ByteBuffer byteBuffer = charset.encode("Junny Land");
fileChannel.write(byteBuffer);
```

File에 작성되는 바이트는 ByteBuffer의 position\~limit 까지 작성이됩니다.



### How do Read?

fileChannel에 read 옵션을 준 후 아래와 같이 사용할 수 있습니다

```java
FileChannel fileChannel = FileChannel.open(Paths.get("./junny.txt"), WRITE, READ);
ByteBuffer buffer = ByteBuffer.allocate(1000);
Charset charset = Charset.defaultCharset();
StringBuffer stringBuffer = new StringBuffer();
while (true){
    int read = fileChannel.read(buffer);
    if(read == -1){
        break;
    }
    buffer.flip();
    stringBuffer.append(charset.decode(buffer));
    buffer.clear();
}
fileChannel.close();
```

allocate시 설정한 Buffer의 크기(예제 1000)만큼 읽어 옵니다

버퍼 보다 큰 사이즈를 읽어 올경우 flip으로 position을 0으로 변경해주어야 합니다.



### How do Copy?

ByteBuffer를 이용하여 읽기용 Channel과 쓰기용 Channel이 교대로 작업을 하면 됩니다.

<img src="../../../.gitbook/assets/file.drawing (2) (2) (2).svg" alt="" class="gitbook-drawing">

allocateDirect는 읽고 바로 쓰기 작업을 하는 경우 유용합니다

1\) Buffer를 사용하여 Copy

```java
try(FileChannel rawFile = FileChannel.open(Paths.get("./junny.txt"),READ);
    FileChannel newFile = FileChannel.open(Paths.get("./new.txt"), WRITE, CREATE_NEW);){
    ByteBuffer buffer = ByteBuffer.allocateDirect(1);
    while (true){
        buffer.clear();
        int read = rawFile.read(buffer);
        if(read == -1){
            break;
        }
        buffer.flip();
        newFile.write(buffer);
    }
}
```

2\) FILES의 copy를 사용하는 경우

```java
 Files.copy(Paths.get("./junny.txt"),Paths.get("./new.txt"));
```

{% hint style="info" %}
```
REPLACE_EXISTING : 존재하면 대체한다
COPY_ATTRIBUTES : 속성 까지 복사한다.
NOFOLLOW_LINKS : 링크 파일은 링크 파일만 복사한다 
```
{% endhint %}



## Asynchronized Channel

위에 서술된 read() , write()는 입출력 작업 동안 blocking 됩니다.

실직적인 입출력 작업 처리는 스레드 풀의 작업 스레드가 담당하고 있습니다.&#x20;

<img src="../../../.gitbook/assets/file.drawing (3).svg" alt="" class="gitbook-drawing">

각각의 Thread가 작업을 마치면 callback은 자동으로 호출되기 때문에,&#x20;

작업 완료후 실행해야할 코드가 있다면 콜백 메소드에 작성하면 됩니다.

```java
ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(
        Paths.get("./junny.txt"),
        Set.of(StandardOpenOption.CREATE, StandardOpenOption.WRITE),
        executorService
);
```

{% hint style="info" %}
`Runtime.getRuntime().availableProcessors() :` 코어의 수를 리턴합니다
{% endhint %}

### How do Write?

비동기의  파일 쓰기는 세가지 클래스로 구현했다.

| name            | description            |
| --------------- | ---------------------- |
| Main            | 파일에 쓰기위한 구현            |
| Callback        | 쓰기 성공&실패 유무 핸들링        |
| FileInformation | 성공시 출력 , 실패시 `FAIL` 출력 |



#### Main

```java
public class Main {
    public static void main(String[] args) throws IOException {
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        Path temp = Paths.get("./junny.txt");
        try(AsynchronousFileChannel fileChannel = open(temp, of(CREATE, WRITE), executorService)){
            fileChannel.write(Charset.defaultCharset().encode("Welcome to JunnyLand"), 0, new FileInformation(fileChannel, temp), new Callback());
        }
        executorService.shutdown();
    }
}
```

executorService.shutdown();를 선언해 주지않는다면 executorService가 종료되지 않는다.



#### FileInformation

```java
public record FileInformation(AsynchronousFileChannel fileChannel, Path path) {
    public void check() {
        try {
            List<String> strings = Files.readAllLines(path, Charset.defaultCharset());
            strings.forEach(System.out::println);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void fail() {
        try {
            System.out.println("FAIL !!!");
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void close(){
    try {
        fileChannel.close();
    } catch (IOException e) {
        throw new RuntimeException(e);
    }

}
```

FileChannel의 작업 시점과 맞춰주기 위해, callback시 file connection을 닫아준다.



#### Callback

```java
public class Callback implements CompletionHandler<Integer, FileInformation> {
    @Override
    public void completed(Integer result, FileInformation attachment) {
        attachment.check();
        System.out.println("COMPLETE!!!");
        attachment.close();
    }

    @Override
    public void failed(Throwable exc, FileInformation attachment) {
        attachment.fail();
        System.out.println("FAILED!!!");
        attachment.close();
    }
}
```

callback이 성공인지 실패인지에 따라 특정 메소드를 호출해준다



### How do Read?

비동기의 파일 읽기는 세가지 클래스로 구현된다.

| Method          | Description            |
| --------------- | ---------------------- |
| Main            | 파일을 읽기 위한 구현           |
| FileInformation | 버퍼를  읽거나, 채널을 종료하기     |
| Callback        | 성공시 출력 , 실패시 `FAIL` 출력 |

#### Main

```java
public class Main {
    public static void main(String[] args) throws IOException {
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        Path temp = Paths.get("./junny.txt");
        try (AsynchronousFileChannel fileChannel = open(temp, of(READ), executorService)) {
            ByteBuffer allocate = allocate(100);
            fileChannel.read(allocate, 0, new FileInformation(fileChannel, temp, allocate), new Callback());
        }

        executorService.shutdown();
    }
}
```

읽을 버퍼의 크기를 100으로 지정해 준 후, 위에서 작성한 junny.txt를 읽기 위한 구현

#### FileInformation

```java
public record FileInformation(AsynchronousFileChannel fileChannel, Path path, ByteBuffer buffer) {
    public void close() {
        try {
            fileChannel.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String read() {
        buffer.flip();
        return Charset.defaultCharset().decode(buffer).toString();
    }
}
```

#### Callback

```java
public class Callback implements CompletionHandler<Integer, FileInformation> {
    @Override
    public void completed(Integer result, FileInformation attachment) {
        String response = attachment.read();
        System.out.println("decode :: " + response  + "\nThread :: " + Thread.currentThread().getName());

        System.out.println("COMPLETE!!!");
        attachment.close();
    }

    @Override
    public void failed(Throwable exc, FileInformation attachment) {
        attachment.close();
        System.out.println("FAILED!!!");
        attachment.close();
    }
}
```
