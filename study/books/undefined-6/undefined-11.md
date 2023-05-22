# 단일 연산 변수와 넌블로킹 동기화

단일 연산 변수와 대기 상태에 들어가지 않는 넌블로킹 동기화 기법에 대해서 살펴본다.\
넌블로킹 알고리즘은 락을 기반으로 하는 방법보다 설계와 구현 모두 훨씬 복잡함.\
대신 확장성과 활동성을 엄청나게 높여준다.

&#x20;여러 스레드가 동일한 자료를 놓고 경쟁하는 과정에서 대기 상태에 들어가는 일이 없어서 스케줄링 부하를 대폭 줄여준다.

&#x20;데드락이나 기타 활동성 문제가 발생할 위험도 없다.

### 락의 단점

락 확보 경쟁이 벌어지는 상황에서는 JVM 역시 운영체제 도움을 받는다.\
스레드가 락을 확보하기 위해 대기하고 있는 상태에서 대기 중인 스레드는 다른 작업을 전혀 못한다.

&#x20;이런 상태에서 락을 확보하고 있는 스레드의 작업이 지연되면 락 확보를 위해 대기하던 모든 스레드의 작업이 전부 지연된다.

스레드 실행을 중단했다가 다시 실행하는 방법은 상당한 부하 발생\
카운터 값 증가와 같은 세밀한 작업은 연산을 동기화하기에 락이 너무 무거운 방법임.

#### 병렬 연산을 위한 하드웨어적인 자원

&#x20;베타적인 락 방법은 보수적인 동기화 방법\
일단 값을 변경하고 다른 스레드의 간섭 없이 값이 제대로 변경되는 낙관적인 방법이 있다.

충돌 검출 방법을 통해 값을 변경하는 동안 다른 스레드의 간섭이 있었는지 확인하고,\
만약 간섭이 있었다면 연산 실패 후 재시도 혹은 재시도를 하지 않는다.

멀티프로세서 연산을 염두에 두고 만들어진 프로세서는 공유 변수 동시 접근 상황을 간단히 관리할 수 있도록 특별한 명령어를 제공함.(CAS, LL, SC 등)

#### 비교 후 치환(Compare And Swap)

&#x20;IA32, Sparc 같은 프로세서에서 채택하고 있는 방법은 비교 후 치환(CAS) 명령을 제공하는 방법이다.

&#x20;만약 여러 스레드가 동시에 CAS 연산을 사용해 한 변수의 값을 변경하려고 하면,\
스레드 가운데 하나만 성공적으로 값을 변경하고 나머지 스레드는 모두 실패한다.

&#x20;이와 같은 CAS 연산의 유연성 때문에 락 사용 시 발생할 수 있는 여러 가지 활동성 문제를 미연에 방지할 수 있음.

&#x20;

\[CAS 연산 예제]

```java
@ThreadSafe
public class SimulatedCAS {
    @GuardedBy("this")
    private int value;
   
    public synchronized int get() {
        return value;
    }
   
    public synchronized int compareAndSwap(int expectedValue, int newValue) {
        int oldValue = value;
       
        if (oldValue == expectedValue) {
            value = newValue;
        }
       
        return oldValue;
    }
   
    public synchronized boolean compareAndSet(int expectedValue, int newValue) {
        return (expectedValue == compareAndSwap(expectedValue, newValue));
    }

}
```

#### 넌블로킹 카운터

CAS 연산을 사용해 대기 상태에 들어가지 않으면서도 스레드 경쟁에 안전한 카운터 클래스이다.

```java
@ThreadSafe
public class CasCounter {
    private SimulatedCAS value;

    public SimulatedCAS getValue() {
        return value;
    }
   
    public int increment() {
        int v;
       
        do {
            v = value.get();
        } while (v != value.compareAndSwap(v, v + 1));
       
        return v + 1;
    }
}
```

&#x20;그냥 보기에는 CAS 기반의 카운터 클래스가 락 기반의 카운터보다 훨씬 성능이 떨어질 것 같지만 그렇지 않다.

&#x20;락 기반의 프로그램을 보면 언어적인 문법은 훨씬 간결하지만, JVM과 운영체제가 락 처리를 위한 작업은 간단하지 않다.

자바에서 하드웨어 프로세서의 CAS 연산을 호출하려면 AtomicInteger와 같은 java.util.concurrent.atomic 패키지의 AtomicXxx 클래스를 사용하면 된다.

#### 단일 연산 변수 클래스

단일 연산 변수는 락보다 훨씬 가벼우면서 세밀한 구조를 가지고 있다.\
멀티프로세서에서 고성능 병렬 프로그램 작성 시 핵심적 역할을 한다.

&#x20;volatile 을 사용함으로 얻을 수 있었던 메모리 가시성을 단일 연산 변수 클래스를 사용함으로써도 얻을 수 있음.

\=> 더 나은 volatile 변수 역할을 할 수 있다.

```java
public class CasNumberRange {
    @Immutable
    private static class IntPair {
        final int lower; // 불변조건 : lower <= upper
        final int upper;

        public IntPair(int lower, int upper) {
            this.lower = lower;
            this.upper = upper;
        }
    }
    private final AtomicReference<IntPair> values = new AtomicReference<IntPair>(new IntPair(0, 0));

    public int getLower() {
        return values.get().lower;
    }

    public int Upper() {
        return values.get().upper;
    }

    public void setLower(int i) {
        while (true) {
            IntPair oldv = values.get();

            if (i > oldv.upper) {
                throw new IllegalArgumentException("Can't set lower to " + i + " > upper");
            }

            IntPair newv = new IntPair(i, oldv.upper);
            if (values.compareAndSet(oldv, newv)) {
                return;
            }
        }
        // setUpper 메소드도 setLower와 비슷하다.
    }
}
```

#### 성능 비교 : 락과 단일 연산 변수

```
- 경쟁이 많은 상황에서는 단일 연산 변수보다 락이 빠르게 처리됨.
- 하지만 실제적인 경쟁 상황에서는 단일 연산 변수가 락보다 성능이 좋음.
(단일 연산 변수는 일반적인 경쟁 상황에서 더 효율적으로 처리하게 때문)
- 경쟁이 적거나 보통일 경우 : 단일 연산 변수 , 경쟁 아주 많은 경우 : 락
- 하지만 스레드 별로 각자의 값을 사용하게 할 수 있어서 ThreadLocal을 사용한다면 그것이 가장 성능이 좋다.
```

#### 넌블로킹 알고리즘

&#x20;특정 스레드에서 작업이 실패하거나 대기 상태에 들어가는 경우에, 다른 어떤 스레드라도 그로 인해 실패하거나 대기 상태에 들어가지 않는 알고리즘.\
락 대신 CAS를 사용해서 구현한 알고리즘을 말한다고 보면 된다.

```java
AtomicReference<Node<E>> top = new AtomicReference<Node<E>>();
   
    public void push(E item) {
        Node<E> newHead = new Node<E>(item);
        Node<E> oldHead;
       
        do {
            oldHead = top.get();
            newHead.next = oldHead;
        } while (!top.compareAndSet(oldHead, newHead));
    }
   
    public E pop() {
        Node<E> oldHead;
        Node<E> newHead;
       
        do {
            oldHead = top.get();
           
            if (oldHead == null) {
                return null;
            }
           
            newHead = oldHead.next;
        } while (!top.compareAndSet(oldHead, newHead));
       
        return oldHead.item;
    }
   
    private static class Node<E> {
        public final E item;
        public Node<E> next;
       
        public Node(E item) {
            this.item = item;
        }
    }
}
```

### 단일 연산 필드 업데이터

#### &#x20;ABA 문제

CAS 연산 시 발생할 수 있는 문제\
현재 A인 상태를 확인하는 변수값이 A->B->A 이렇게 돌아온 경우.\
(참조형 변수의 경우 메모리 주소를 의미한다)

메모리 주소는 같지만 실제 다른 값으로 내용이 변경되었을 수 도 있다.\
일반적인 방법으로는 이런 경우를 확인할 수 없어서, AtomicStampedReference 클래스를 이용하면 된다.

&#x20;AtomicStampedReference는 객체 참조와 버전 번호를 같이 저장한다.
