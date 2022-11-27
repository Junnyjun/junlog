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

<img src="../../.gitbook/assets/file.drawing (8).svg" alt="" class="gitbook-drawing">

Object Lock은 한번에 한 Thread만 Object를 사용할 수 있도록 내부적으로 Mutex를 활용한다.\
JVM이 Class file을 Load 할 때는 Heap에는 Java class의 Instance가 생성되며 Object Lcock은 instance에 동기화 작업을 한다.

Object의 Lock은 중복하여 획득할 수 있다.\
특정 Object의 Critical Section에 진입할 때마다 Lock을 획득하는 작업을 다시 수행할 수 있다는 뜻이다.

{% embed url="https://codepen.io/Junnyjun/pen/MWXXQNd" %}
