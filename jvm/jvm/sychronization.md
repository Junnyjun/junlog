# Sychronization

JAVA는 동시사용자를 처리하기 위해 굉장히 많은 Thread를 사용한다.

Thread가 같은 자원을 사용할 때는 경합이 발생하고, Dead Lock이 발생할 수도 있다.

### Syncronized

Thread가 공유 자원을 사용할 때, 정합성을 보장하려면 동기화 장치로 하나의 Thread만 접근할 수 있도록 해야한다. Java에서는 Monitor를 사용해 Thread를 동기화 한다.

모든 자바 객체는 Monitor를 하나 씩 가지고 있다.\
특정 Thread가 소유한 Monitor를 다른 Thread에서 획득 하려면 해당 Monitor를 소유하고 있는 Thread가 해제될 때 까지 Wait Queue에서 대기해야 한다.

### Mutal Exclusion& Critical Section

공유 데이터가 다수의 Thread가 동시에 접근해 작업하면 메모리 Corruption이 발생할 수 있다.

공유 데이터의 접근은 한번에 한 Thread씩 순차적으로 이루어 져야 한다.

<img src="../../.gitbook/assets/file.drawing (12) (2).svg" alt="" class="gitbook-drawing">

Object Lock은 한번에 한 Thread만 Object를 사용할 수 있도록 내부적으로 Mutex를 활용한다.\
JVM이 Class file을 Load 할 때는 Heap에는 Java class의 Instance가 생성되며 Object Lcock은 instance에 동기화 작업을 한다.

Object의 Lock은 중복하여 획득할 수 있다.\
특정 Object의 Critical Section에 진입할 때마다 Lock을 획득하는 작업을 다시 수행할 수 있다는 뜻이다.

Object의 Header에는 Lock Counter를 가지고 있는데, Lock을 획득하면 1씩 증&감 한다.\
Critical Section은 Object Reference와 연계해 동기화를 수행한다.\
떠날 때 Lock은 자동으로 해지되며, 명시적인 작업은 불필요 하다.

다른 Thread가 점유된 자원에 접근하려고 할땐 Monitor의 Wait Set에서 대기하게 된다.

### How do Java?

#### synchronized

사용중일 경우 다른 Thread의 접근을 제한 한다.\
이 경우 다른 접근하려는 모든 Thread는 MONITORENTER, MONITOREXIT라는 바이트 코드 상태를 가진다

```java
Integer data = 0;
public synchronized void receive(){
  data++;
}
```

#### Wait& Notify&#x20;

&#x20;wait은 현재 Thread 점유를 멈추고, Notify는 Thread를 점유한다.

<img src="../../.gitbook/assets/file.drawing (7).svg" alt="" class="gitbook-drawing">

Thread가 Entry Set으로 진입하면 Monitor Lock 획득을 시도한다. \
다른 Thread가 Lock을 획득했을 경우에는, 후발 Thread는 다시 Entry Set에서 대기해야 한다

Wait Set에 들어온 Thread가 Critical Section을 벗어나는 방법은 Monitor를 다시 획득해 Lock을 놓고 나가는 방법 이외에는 없다.

### Thread

Thread는 daemon& non-daemon Thread로 나눌 수 있다.

daemon Thread는 다른 Thread가 없으면 동작을 중지한다.

<img src="../../.gitbook/assets/file.drawing (15).svg" alt="" class="gitbook-drawing">

#### Dump

java에서 동기화 문제를 해결하는 가장 기본적인 방법이다. \
현재 Thread의 상태와 stack Trace를 출력하고 JVM의 종류에 따라 풍부한 정보를 제공한다.

