# ByteBuf

### 5.1 ByteBuf API

ByteBuf는 Netty에서 제공하는 데이터 컨테이너로, JDK의 ByteBuffer에 비해 많은 이점을 제공합니다. ByteBuf는 다양한 데이터 작업을 효율적으로 수행할 수 있는 API를 제공합니다.&#x20;

주요 메서드로는 읽기, 쓰기, 검색 등이 있으며, 이를 통해 데이터를 효율적으로 처리할 수 있습니다. ByteBuf는 투명한 제로-카피와 용량 확장성 등의 기능을 제공하여, 네트워크 프로그래밍에서의 성능을 최적화합니다.

### 5.2 Class ByteBuf—Netty의 데이터 컨테이너

모든 네트워크 통신은 바이트 시퀀스의 이동을 포함하므로 효율적이고 사용하기 쉬운 데이터 구조가 필요합니다. Netty의 ByteBuf 구현은 이러한 요구 사항을 충족시킵니다.&#x20;

ByteBuf는 데이터를 접근하기 쉽게 하기 위해 readerIndex와 writerIndex라는 두 개의 별도 인덱스를 유지합니다. 데이터를 읽을 때마다 readerIndex가 증가하고, 데이터를 쓸 때마다 writerIndex가 증가합니다.

#### 5.2.1 작동 방식

ByteBuf는 읽기와 쓰기를 위한 두 개의 별도 인덱스를 유지합니다. ByteBuf에서 읽을 때마다 readerIndex가 증가하고, 쓸 때마다 writerIndex가 증가합니다.&#x20;

예를 들어, readerIndex가 writerIndex에 도달하면 읽을 수 있는 데이터의 끝에 도달한 것입니다.&#x20;

```java
ByteBuf buffer = ...;
while (buffer.isReadable()) {
    System.out.println(buffer.readByte());
}
```

#### 5.2.2 ByteBuf 사용 패턴

Netty를 사용할 때 여러 가지 일반적인 ByteBuf 사용 패턴이 있습니다.

**힙 버퍼**

힙 공간에 데이터를 저장하는 가장 자주 사용되는 ByteBuf 패턴입니다. \
이 패턴은 풀링이 사용되지 않는 상황에서 빠른 할당과 해제를 제공합니다.&#x20;

```java
ByteBuf heapBuf = ...;
if (heapBuf.hasArray()) {                    
    byte[] array = heapBuf.array();                            
    int offset = heapBuf.arrayOffset() + heapBuf.readerIndex();  
    int length = heapBuf.readableBytes();           
    handleArray(array, offset, length);   
}
```

**직접 버퍼**

직접 버퍼는 네이티브 호출을 통해 메모리를 할당하여 네트워크 데이터 전송에 이상적입니다. \
직접 버퍼의 주요 단점은 힙 기반 버퍼보다 할당 및 해제 비용이 더 많이 든다는 것입니다.

```java
ByteBuf directBuf = ...;
if (!directBuf.hasArray()) {              
    int length = directBuf.readableBytes();               
    byte[] array = new byte[length];                              
    directBuf.getBytes(directBuf.readerIndex(), array);  
    handleArray(array, 0, length);        
}
```

**컴포지트 버퍼**

컴포지트 버퍼는 여러 ByteBuf의 집합적 뷰를 제공하며, 필요에 따라 ByteBuf 인스턴스를 추가 및 삭제할 수 있습니다. CompositeByteBuf는 여러 ByteBuf를 하나로 결합하여 단일 버퍼처럼 작동하게 합니다. \
이는 분산된 데이터를 단일 ByteBuf로 처리해야 하는 경우에 유용합니다.

CompositeByteBuf는 성능을 최적화하고 메모리 복사를 최소화하는 방법을 제공합니다. \
CompositeByteBuf의 주요 메서드로는 addComponent(), removeComponent(), numComponents() 등이 있으며, 이를 통해 버퍼를 동적으로 관리할 수 있습니다.

```java
CompositeByteBuf compBuf = Unpooled.compositeBuffer();
ByteBuf headerBuf = ...;
ByteBuf bodyBuf = ...;
compBuf.addComponents(headerBuf, bodyBuf);
```

CompositeByteBuf의 또 다른 중요한 기능은 슬라이스(slices)를 지원한다는 것입니다. \
슬라이스는 원래 버퍼의 일부분을 참조하는 ByteBuf 인스턴스로, 메모리 복사 없이도 원래 데이터에 접근할 수 있습니다. 이는 메모리 효율성을 높이는 데 매우 유용합니다.

```java
CompositeByteBuf compBuf = Unpooled.compositeBuffer();
ByteBuf headerBuf = ...;
ByteBuf bodyBuf = ...;
compBuf.addComponents(true, headerBuf, bodyBuf);

ByteBuf slice = compBuf.slice(0, headerBuf.readableBytes());
```

## ByteBuf

### 5.3 Byte-level operations

ByteBuf는 기본 읽기 및 쓰기 작업 외에도 데이터를 수정하기 위한 다양한 메서드를 제공합니다.&#x20;

#### 5.3.1 Domain access indexing

ByteBuf는 특정 영역 내에서 데이터에 접근할 수 있는 메서드를 제공합니다. \
이를 통해 데이터의 부분 영역에 대해 읽기와 쓰기 작업을 수행할 수 있습니다.&#x20;

getBytes(int index, byte\[] dst, int dstIndex, int length)와 같은 메서드를 사용하여 특정 인덱스부터 시작하여 데이터를 읽거나 쓸 수 있습니다.

```java
ByteBuf buffer = ...;
byte[] dst = new byte[buffer.readableBytes()];
buffer.getBytes(buffer.readerIndex(), dst, 0, dst.length);
```

#### 5.3.2 Sequential (Linear) access indexing

ByteBuf는 readerIndex와 writerIndex를 사용하여 순차적으로 데이터를 읽고 쓸 수 있습니다. \
readByte()와 writeByte(byte value) 메서드를 사용하여 순차적으로 데이터를 읽거나 쓸 수 있습니다.

```java
ByteBuf buffer = ...;
while (buffer.isReadable()) {
    System.out.println(buffer.readByte());
}
buffer.writeByte((byte) 'a');
```

#### 5.3.3 Discardable bytes

ByteBuf는 읽기 작업이 완료된 바이트를 버릴 수 있는 메서드를 제공합니다. \
discardReadBytes() 메서드는 readerIndex 이전의 바이트를 제거하여 메모리를 회수합니다.

```java
ByteBuf buffer = ...;
buffer.readBytes(new byte[buffer.readableBytes()]);
buffer.discardReadBytes();
```

#### 5.3.4 Readable bytes

ByteBuf의 읽기 가능한 바이트 영역은 실제 데이터를 저장하는 곳입니다.&#x20;

새로 할당된, 래핑된, 또는 복사된 버퍼의 기본 readerIndex 값은 0입니다. 이름이 read 또는 skip으로 시작하는 모든 작업은 현재 readerIndex에서 데이터를 가져오거나 건너뛰고, 읽은 바이트 수만큼 readerIndex를 증가시킵니다.

만약 호출된 메서드가 ByteBuf 인수를 쓰기 대상(buffer)으로 사용하고, 대상 인덱스 인수가 없는 경우, 대상 버퍼의 writerIndex도 증가합니다. 예를 들어:

```java
readBytes(ByteBuf dest);
```

읽을 수 있는 바이트가 소진된 상태에서 버퍼에서 읽으려 하면, IndexOutOfBoundsException이 발생합니다.

```java
ByteBuf buffer = ...;
while (buffer.isReadable()) {
    System.out.println(buffer.readByte());
}
```

#### 5.3.5 Writable bytes

ByteBuf의 쓰기 가능한 바이트 영역은 정의되지 않은 내용의 메모리 영역으로, 쓰기 작업을 위해 준비된 상태입니다.&#x20;

새로 할당된 버퍼의 기본 writerIndex 값은 0입니다. 이름이 write로 시작하는 모든 작업은 현재 writerIndex에서 데이터를 쓰기 시작하고, 쓴 바이트 수만큼 writerIndex를 증가시킵니다.&#x20;

만약 쓰기 작업의 대상이 ByteBuf이고, 소스 인덱스가 지정되지 않은 경우, 소스 버퍼의 readerIndex도 동일한 양만큼 증가합니다.

다음은 랜덤 정수 값으로 버퍼의 쓰기 가능한 바이트를 채우는 예제입니다.&#x20;

```java
ByteBuf buffer = ...;
while (buffer.writableBytes() >= 4) {
    buffer.writeInt(random.nextInt());
}
```

#### 5.3.6 Index management

JDK의 InputStream은 현재 위치를 표시하고 해당 위치로 스트림을 재설정하기 위해 mark(int readlimit) 및 reset() 메서드를 정의합니다.&#x20;

이와 유사하게 ByteBuf에서도 markReaderIndex(), markWriterIndex(), resetReaderIndex(), resetWriterIndex() 메서드를 호출하여 readerIndex와 writerIndex를 설정하고 재설정할 수 있습니다.&#x20;

이 메서드들은 InputStream의 호출과 유사하지만, 표시가 무효화되는 시점을 지정하는 readlimit 매개변수는 없습니다.

또한 readerIndex(int) 또는 writerIndex(int) 메서드를 호출하여 인덱스를 지정된 위치로 이동할 수 있습니다. \
인덱스를 유효하지 않은 위치로 설정하려고 하면 IndexOutOfBoundsException이 발생합니다.

clear() 메서드를 호출하여 readerIndex와 writerIndex를 모두 0으로 설정할 수 있습니다. \
이는 메모리 내용을 지우지는 않지만 인덱스를 재설정합니다. \
clear() 메서드는 메모리를 복사하지 않고 인덱스를 재설정하므로 discardReadBytes()보다 훨씬 저렴합니다.

#### 5.3.7 Search operations

ByteBuf에서 지정된 값의 인덱스를 결정하는 몇 가지 방법이 있습니다. 가장 간단한 방법은 indexOf() 메서드를 사용하는 것입니다.&#x20;

더 복잡한 검색은 ByteBufProcessor 인수를 사용하는 메서드를 통해 수행할 수 있습니다. 이 인터페이스는 하나의 메서드 boolean process(byte value)를 정의하며, 입력 값이 찾고자 하는 값인지 여부를 보고합니다.

다음 예제는 캐리지 리턴 문자(\r)를 찾기 위해 ByteBufProcessor를 사용하는 방법을 보여줍니다.

```java
ByteBuf buffer = ...;
int index = buffer.forEachByte(ByteBufProcessor.FIND_CR);
```

#### 5.3.8 Derived buffers

파생 버퍼는 ByteBuf의 내용을 특수한 방식으로 나타내는 뷰를 제공합니다. \
이러한 뷰는 다음 메서드들을 통해 생성됩니다:

* duplicate()
* slice()
* slice(int, int)
* Unpooled.unmodifiableBuffer(...)
* order(ByteOrder)
* readSlice(int)

각 메서드는 자체 reader, writer 및 마커 인덱스를 가진 새로운 ByteBuf 인스턴스를 반환합니다. \
내부 저장소는 JDK ByteBuffer와 마찬가지로 공유됩니다.&#x20;

이는 파생 버퍼를 생성하는 비용이 저렴하다는 것을 의미하지만, 내용을 수정하면 원본 인스턴스도 수정된다는 점에 주의해야 합니다.

다음 예제는 slice(int, int)을 사용하여 ByteBuf 세그먼트를 작업하는 방법을 보여줍니다.

```java
Charset utf8 = Charset.forName("UTF-8");
ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", utf8);
ByteBuf sliced = buf.slice(0, 14);
System.out.println(sliced.toString(utf8));
buf.setByte(0, (byte)'J');
assert buf.getByte(0) == sliced.getByte(0);
```

이제 ByteBuf 세그먼트의 복사본이 원본과 어떻게 다른지 보겠습니다.

```java
Charset utf8 = Charset.forName("UTF-8");
ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", utf8);
ByteBuf copy = buf.copy(0, 14);
System.out.println(copy.toString(utf8));
buf.setByte(0, (byte)'J');
assert buf.getByte(0) != copy.getByte(0);
```

#### 5.3.9 Read/write operations

읽기/쓰기 작업에는 두 가지 범주가 있습니다:

* 지정된 인덱스에서 시작하여 인덱스를 변경하지 않는 get() 및 set() 작업
* 지정된 인덱스에서 시작하여 접근한 바이트 수만큼 인덱스를 조정하는 read() 및 write() 작업

다음 예제는 get() 및 set() 메서드의 사용을 보여주며, 이 메서드들이 읽기 및 쓰기 인덱스를 변경하지 않는다는 것을 보여줍니다.

```java
Charset utf8 = Charset.forName("UTF-8");
ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", utf8);
System.out.println((char)buf.getByte(0));
int readerIndex = buf.readerIndex();
int writerIndex = buf.writerIndex();
buf.setByte(0, (byte)'B');
System.out.println((char)buf.getByte(0));
assert readerIndex == buf.readerIndex();
assert writerIndex == buf.writerIndex();
```

이제 read() 및 write() 작업을 살펴보겠습니다. 이 메서드들은 현재 readerIndex 또는 writerIndex에서 작동합니다. 이러한 메서드는 ByteBuf를 스트림처럼 읽기 위해 사용됩니다.&#x20;

대부분의 read() 메서드는 대응하는 write() 메서드를 가지고 있으며, ByteBuf에 데이터를 추가하는 데 사용됩니다.&#x20;

다음 예제는 read() 및 write() 메서드의 사용을 보여줍니다.

```java
Charset utf8 = Charset.forName("UTF-8");
ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", utf8);
System.out.println((char)buf.readByte());
int readerIndex = buf.readerIndex();
int writerIndex = buf.writerIndex();
buf.writeByte((byte)'?');
assert readerIndex == buf.readerIndex();
assert writerIndex != buf.writerIndex();
```

#### 5.3.10 More operations

다음 표는 ByteBuf에서 제공하는 추가적인 유용한 작업들을 나열합니다.

| Name            | Description                                                                        |
| --------------- | ---------------------------------------------------------------------------------- |
| isReadable()    | 최소한 하나의 바이트를 읽을 수 있으면 true를 반환합니다.                                                 |
| isWritable()    | 최소한 하나의 바이트를 쓸 수 있으면 true를 반환합니다.                                                  |
| readableBytes() | 읽을 수 있는 바이트 수를 반환합니다.                                                              |
| writableBytes() | 쓸 수 있는 바이트 수를 반환합니다.                                                               |
| capacity()      | ByteBuf가 보유할 수 있는 바이트 수를 반환합니다.                                                    |
| maxCapacity()   | ByteBuf가 보유할 수 있는 최대 바이트 수를 반환합니다.                                                 |
| hasArray()      | ByteBuf가 바이트 배열로 지원되면 true를 반환합니다.                                                 |
| array()         | ByteBuf가 바이트 배열로 지원되면 바이트 배열을 반환하고, 그렇지 않으면 UnsupportedOperationException을 발생시킵니다. |

### 5.4 Interface ByteBufHolder

우리는 종종 실제 데이터 페이로드 외에도 다양한 속성 값을 저장해야 할 필요가 있습니다. \
HTTP 응답은 좋은 예로, 바이트로 표현된 콘텐츠 외에도 상태 코드, 쿠키 등이 있습니다.

Netty는 이러한 일반적인 사용 사례를 처리하기 위해 ByteBufHolder를 제공합니다. ByteBufHolder는 또한 Netty의 고급 기능인 버퍼 풀링을 지원하며, 필요한 경우 ByteBuf를 풀에서 빌려오고 자동으로 해제할 수 있습니다.

ByteBufHolder는 기본 데이터와 참조 카운팅에 접근하기 위한 몇 가지 메서드를 제공합니다. Table 5.6은 ReferenceCounted에서 상속받은 메서드들을 제외한 ByteBufHolder의 메서드들을 나열합니다.

| Name        | Description                                            |
| ----------- | ------------------------------------------------------ |
| content()   | ByteBufHolder가 보유한 ByteBuf를 반환합니다.                     |
| copy()      | 포함된 ByteBuf의 데이터를 포함하여 이 ByteBufHolder의 깊은 복사본을 반환합니다. |
| duplicate() | 포함된 ByteBuf의 데이터를 공유하는 얕은 복사본을 반환합니다.                  |

ByteBufHolder는 페이로드를 ByteBuf에 저장하는 메시지 객체를 구현하려는 경우 좋은 선택입니다.

### 5.5 ByteBuf allocation

이 섹션에서는 ByteBuf 인스턴스를 관리하는 방법을 설명합니다.

#### 5.5.1 On-demand: interface ByteBufAllocator

메모리 할당 및 해제의 오버헤드를 줄이기 위해 Netty는 ByteBufAllocator 인터페이스를 통해 풀링을 구현합니다. 풀링의 사용은 애플리케이션별 결정이며 ByteBuf API에 아무런 영향을 미치지 않습니다.

Channel(각 Channel은 고유한 인스턴스를 가질 수 있음)이나 ChannelHandler에 바인딩된 ChannelHandlerContext에서 ByteBufAllocator를 참조할 수 있습니다.&#x20;

```java
Channel channel = ...;
ByteBufAllocator allocator = channel.alloc();
....
ChannelHandlerContext ctx = ...;
ByteBufAllocator allocator2 = ctx.alloc();
```

Netty는 ByteBufAllocator의 두 가지 구현을 제공합니다

PooledByteBufAllocator는 ByteBuf 인스턴스를 풀링하여 성능을 개선하고 메모리 단편화를 최소화합니다. 이 구현은 여러 최신 운영 체제에서 채택된 메모리 할당 방법인 jemalloc을 사용합니다.&#x20;

UnpooledByteBufAllocator는 ByteBuf 인스턴스를 풀링하지 않으며 호출될 때마다 새 인스턴스를 반환합니다.

Netty는 기본적으로 PooledByteBufAllocator를 사용하지만, ChannelConfig API를 통해 또는 애플리케이션 부트스트랩 시 다른 할당자를 지정하여 쉽게 변경할 수 있습니다.&#x20;

#### 5.5.2 Unpooled buffers

ByteBufAllocator에 대한 참조가 없는 상황도 있을 수 있습니다. 이 경우 Netty는 Unpooled라는 유틸리티 클래스를 제공하여 풀링되지 않은 ByteBuf 인스턴스를 생성하는 정적 헬퍼 메서드를 제공합니다.

| Name                                               | Description                                   |
| -------------------------------------------------- | --------------------------------------------- |
| buffer()                                           | 힙 기반 저장소를 가진 풀링되지 않은 ByteBuf를 반환합니다.          |
| buffer(int initialCapacity)                        | 초기 용량을 가진 힙 기반 풀링되지 않은 ByteBuf를 반환합니다.        |
| buffer(int initialCapacity, int maxCapacity)       | 초기 용량과 최대 용량을 가진 힙 기반 풀링되지 않은 ByteBuf를 반환합니다. |
| directBuffer()                                     | 직접 저장소를 가진 풀링되지 않은 ByteBuf를 반환합니다.            |
| directBuffer(int initialCapacity)                  | 초기 용량을 가진 직접 풀링되지 않은 ByteBuf를 반환합니다.          |
| directBuffer(int initialCapacity, int maxCapacity) | 초기 용량과 최대 용량을 가진 직접 풀링되지 않은 ByteBuf를 반환합니다.   |
| wrappedBuffer()                                    | 주어진 데이터를 래핑하는 ByteBuf를 반환합니다.                 |
| copiedBuffer()                                     | 주어진 데이터를 복사하는 ByteBuf를 반환합니다.                 |

Unpooled 클래스는 ByteBuf를 네트워크 프로젝트 외의 고성능 확장 가능한 버퍼 API가 필요한 프로젝트에서도 사용할 수 있게 합니다. 이러한 프로젝트는 Netty의 다른 구성 요소를 필요로 하지 않습니다.

#### 5.5.3 Class ByteBufUtil

ByteBufUtil은 ByteBuf를 조작하기 위한 정적 헬퍼 메서드를 제공합니다.&#x20;

가장 유용한 정적 메서드는 아마도 hexdump()일 것입니다. 이 메서드는 ByteBuf의 내용을 16진수로 출력합니다. 이는 디버깅 목적으로 ByteBuf의 내용을 로깅하는 상황 등에서 유용합니다. 16진수 표현은 바이트 값을 직접 나타내는 것보다 더 유용한 로그 항목을 제공하며, 실제 바이트 표현으로 쉽게 변환될 수 있습니다.

또한 boolean equals(ByteBuf, ByteBuf) 메서드는 두 ByteBuf 인스턴스의 동등성을 판단합니다. ByteBufUtil의 다른 메서드는 사용자 정의 ByteBuf 하위 클래스를 구현할 때 유용할 수 있습니다.

### 5.6 Reference counting

참조 카운팅은 객체가 다른 객체에 의해 더 이상 참조되지 않을 때 해당 객체가 보유한 리소스를 해제하여 메모리 사용과 성능을 최적화하는 기술입니다.&#x20;

참조 카운팅의 아이디어는 간단합니다. 특정 객체에 대한 활성 참조 수를 추적하는 것입니다. ReferenceCounted 구현 인스턴스는 일반적으로 1의 활성 참조 카운트로 시작합니다.&#x20;

참조 카운트가 0보다 큰 동안 객체는 해제되지 않습니다. 활성 참조 수가 0이 되면 인스턴스는 해제됩니다. \
참조 카운팅은 메모리 할당의 오버헤드를 줄이는 PooledByteBufAllocator와 같은 풀링 구현에 필수적입니다.

```java
Channel channel = ...;
ByteBufAllocator allocator = channel.alloc();
ByteBuf buffer = allocator.directBuffer();
assert buffer.refCnt() == 1;
```

```java
ByteBuf buffer = ...;
boolean released = buffer.release();
```

참조된 객체가 해제된 후에 접근하려고 하면 IllegalReferenceCountException이 발생합니다.\
특정 클래스는 자신의 방식으로 참조 카운팅 계약을 정의할 수 있습니다.&#x20;
