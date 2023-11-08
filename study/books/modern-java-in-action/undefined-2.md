# 병렬 데이터 처리

#### 병렬 스트림

병렬 스트림이란 각각의 스레드에서 처리할 수 있도록 스트림 요소를 여러 청크로 분할한 스트림이다.

&#x20;

숫자 n을 인수로 받아서 1부터 n까지의 모든 숫자의 합계를 반환하는 메서드를 구현한다고 해보자.

```
public long sequentialSum(long n) {
  return Stream.iterate(1L, i -> i + 1)
    .limit(n)
    .reduce(0L, Long::sum);
  }
```

n이 커져서 병렬로 처리해야되는 상황이라면 무엇부터 건드려야할까?

결과 변수는 어떻게 동기화해야할까? 몇 개의 스레드를 사용해야 할까?

숫자는 어떻게 생성할까? 생성된 숫자는 누가 더할까?

**7.1.1 순차 스트림을 병렬 스트림으로 변환하기**

순차 스트림에 parallel 메서드를 호출하면 기존의 함수형 리듀싱 연산이 병렬로 처리된다.

```
public long sequentialSum(long n) {
  return Stream.iterate(1L, i -> i + 1)
    .limit(n)
    .parallel() //병렬 스트림으로 변환
    .reduce(0L, Long::sum);
  }
```

스트림이 여러 청크로 분할되어 각각 리듀싱 연산을 수행한 후 다시 리듀싱 연산으로 합쳐져 결과를 도출한다.

반대로 sequential로 병렬을 순차 스트림으로 바꿀 수 있다.

이 두 메서드들을 통해 병렬로 실행할 연산과순차로 실행할 연산을 제어할 수 있다.

```
stream.parallel()
  .filter(...)
  .sequential()
  .map(...)
  .parallel()
  .reduce();
```

**7.1.2 스트림 성능 측정**

성능 측정을 위하여 자바 마이크로벤치마크 하니스(JMH) 라이브러리를 통해 벤치마크를 구현해보자.

```
@BenchmarkMode(Mode.AverageTime) //벤치마크 대상 메서드를 실행하는데 걸린 평균 시간 측정
@OutputTimeUtil(TimeUnit.MILLISECONDS) //벤치마크 결과를 ms 단위로 출력
@Fork(2, jvmArgs = {"-Xms4G", "-Xmx4G"}) //4GB의 힙 공간을 제공한 환경에서 2번의 벤치마크를 수행
public class ParallelStreamBenchmark {
  private static final long N = 10_000_000L;
  
  @Benchmark
  public long sequentialSum() {
    return Stream.iterate(1L, i -> i + 1).limit(N)
      .reduce(0L, Long::sum);
    }
    
    @TearDown(Level.Invocation) //매 벤치마크 실행한 후에 가비지 컬렉터 동작 시도
    public void tearDown() {
      System.gc();
    }
  }
```

전통적인 for 루프를 사용해 반복하는 방법이 더 저수준으로 동작할 뿐 아니라 기본값을 박싱하거나 언박싱할 필요가 없으므로 더 빠를 것이라 예상할 수 있다.

```
@Benchmark
public long iterativeSum() {
  long result = 0;
  for (long i = 1L; i <= N; i++) {
    result += i;
  }
  return result;
}
```

벤치마크 결과도 역시 순차적 스트림을 사용할 때보다 4배 이상 빠르게 측정되었다.

&#x20;

하지만 병렬스트림의 벤치마크 결과는 순차스트림에 비해 5배나 느린 결과가 나왔다. 어떤 문제가 있을까?

* 반복 결과로 박싱된 객체가 만들어지므로 숫자를 더하려면 언박싱을 해야한다.
* 반복 작업은 병렬로 수행할 수 있는 독립 단위로 나누기가 어렵다.

이전 연산의 결과에 따라 다음 함수의 입력이 달라지기 때문에 iterate 연산을 청크로 분할하기가 어렵다.

스트림이 병렬로 처리되도록 지시했고 각각의 합계가 다른 스레드에서 수행되었지만, 결국 순차처리 방식과 크게 다른점이 없으므로 스레드를 할당하는 오버헤드만 증가한 것이다.

&#x20;

더 특화된 메서드 사용

멀티코어 프로세서를 활용해서 효과적으로 병렬 연산을 실행하려면 어떻게 해야할까?

5장에서 LongStream.rangeClosed라는 메서드를 소개했다.&#x20;

&#x20;

LongStream.rangeClosed의 장점

* 기본형 long을 직접 사용하므로 박싱과 언박싱 오버헤드가 사라진다.
* 쉽게 청크로 분할할 수 있는 숫자 범위를 생산한다. 예를들어 1-20 범위의 숫자를 각각 1-5, 6-10, 11-15, 16-20 범위의 숫자로 분할한다.

```
  @Benchmark
  public long parallelRangedSum() {
    return LongStream.rangeClosed(1, N)
      .parallel()
      .reduce(0L, Long::sum);
    }
```

벤치마크 결과 순차로 수행했을때보다 병렬로 수행했을때 더 높은 성능을 보였다.

올바른 자료구조를 선택해야 병렬 실행도 최적의 성능을 발휘한다는 사실을 알수있다.

**7.1.3 병렬 스트림의 올바른 사용법**

공유된 상태를 바꾸는 알고리즘을 병렬 스트림으로 사용하면 문제가 발생한다.

```
public long sideEffectSum(long n) {
  Accumlator accumulator = new Accumulator();
  LongStream.rangeClosed(1, n).forEach(accumulator::add);
  return acculator.total;
}

public class Accumulator {
  public long total = 0;
  public void add(long value) { total += value; }
}
```

위 코드를 병렬로 실행하게되면 total 값에 접근할 때마다 데이터 레이스 문제가 일어난다.

```
public long sideEffectParallelSum(long n) {
  Accumulator accumulaor = new Accumulator();
  LongStream.rangeClosed(1, n).parallel().forEach(accumultor::add);
  return accumulator.total;
}
```

메서드의 성능 뿐만 아니라 결과값도 올바르게 나오지 않는다.

병렬 스트림이 올바르게 동작하기 위해서는 공유된 가변 상태를 피해야한다.

**7.1.4 병렬 스트림 효과적으로 사용하기**

* 확신이 서지 않을때는 순차 스트림과 병렬 스트림 구현 시의 성능을 직접 측정한다.
* 자동 박싱과 언박싱은 성능을 크게 저하시킬 수 있는 요소이므로 주의해서 사용해야 하며, 기본형 특화 스트림(IntStream, LongStream, DoubleStream)을  사용하는 것이 좋다.
* limit이나 findFirst처럼 요소의 순서에 의존하는 연산은 병렬 스트림에서 성능이 더 떨어진다. 요소의 순서가 상관없다면 unordered를 호출해서 비정렬된 스트림을 얻은 후 limit을 호출하는 것이 더 효율적이다.
* 스트림에서 수행하는 전체 파이프라인 연산 비용을 고려하자. 요소 수가 많고 요소 당 연산 비용이 높다면 병렬 스트림으로 성능을 개선할 여지가 있다.
* 병렬화 과정의 부가 비용을 상쇄하지 못할 정도의 소량의 데이터에서는 병렬스트림이 도움되지 않는다.
* 스트림을 구성하는 자료구조가 적절한지 확인한다. ArrayList는 요소를 탐색하지 않고도 분할할 수 있지만 LinkedList는 모든 요소를 탐색해야 분할할 수 있다.\
  range 팩토리 메서드로 만든 기본형 스트림이나 커스텀 Spliterator를 구현하면 쉽게 분해할 수 있다.
* 스트림의 특성과 파이프라인 중간 연산이 스트림의 특성을 어떻게 바꾸는지에 따라 분해 과정의 성능이 달라질 수 있다. SIZED 스트림은 정확히 같은 크기의 두 스트림으로 분할할 수 있으므로 효과적으로 병렬 처리가 가능하다. 반면 필터 연산이 있으면 스트림의 길이를 예측할 수 없으므로 병렬 처리가 어려워진다.
* 최종 연산의 병합 과정 비용이 비싸다면 병렬 스트림으로 얻은 이익이 상쇄될 수 있다.

스트림 소스와 분해성 (병렬화 친밀도)

| 소스              | 분해성 |
| --------------- | --- |
| ArrayList       | 훌륭함 |
| LinkedList      | 나쁨  |
| IntStream.range | 훌륭함 |
| Stream.iterate  | 나쁨  |
| HashSet         | 좋음  |
| TreeSet         | 좋음  |

#### &#x20;포크/조인 프레임워크

병렬화할 수 있는 작업을 재귀적으로 작은 작업으로 분할하여 서브태스크로 처리한 뒤, 각각의 결과를 합쳐서 전체 결과로 만드는 방식

**7.2.1 RecursiveTask 활용**

스레드풀을 이용하려면 RecursiveTask\<R>의 서브클래스를 만들어야하고, 추상메서드 compute를 구현해야 한다.

```
protected abstract R compute();
```

&#x20;

compute 메서드 구현 형식은 분할 정복 알고리즘의 병렬화 버전을 사용한다.

```
if(태스크가 충분히 작거나 더이상 분할할 수 없으면) {
  순차적으로 태스크 계산
} else {
  태스크를두 서브태스크로 분할
  태스크가 다시 서브태스크로 분할되도록 메시지를 재귀적으로 호출
  모든 서브태스크의 연산이 왑료될때까지 대기
  각 서브태스크의 결과를 합침
}
```

&#x20;

**7.2.2 포크/조인 프레임워크를 제대로 사용하는 방법**

* join 메서드를 태스크에 호출하면 태스크가 생산하는 결과가 준비될 때까지 호출자를 블록시킨다. 따라서 두 서브태스크가 모두 시작된 다음에 join을 호출하지 않으면, 각각의 서브태스크가 다른 서브태스크를 기다리는 일이 발생할 수 있다.
* RecursiveTask 내에서는 compute나 fork 메서드를 사용하며, 순차코드에서 병렬 계산을 시작할때만 ForkJoinPool의 invoke 메서드를 사용해야 한다.
* 서브태스크에 fork 메서드를 호출해서 ForkJoinPool의 일정을 조절할 수 있다. 한쪽 작업에만 fork를 호출하고 다른쪽에는 compute를 호출하면, 한 태스크에는 같은 스레드를 재사용할 수 있으므로 불필요한 오버헤드를 피할 수 있다.
* 포크/조인 프레임워크의 병렬 계산은 디버깅하기 어렵다. fork라 불리는 스레드에서 compute를 호출하므로 스택 트레이스가 도움이 되지 않는다.
* 병렬 처리로 성능을 개선하려면 태스크를 여러 독립적인 서브태스크로 분할할 수 있어야 하며, 각 서브태스크의 실행 시간은 새로운 태스크를 포킹하느데 드는 시간보다 길어야한다.

**7.2.3 작업 훔치기**

이론적으로는 CPU의 코어 개수만큼 병렬화된 태스크로 작업부하를 분할하면 모든 코어에서 태스크를 실행할 것이고, 같은 시간에 종료될 것이라고 생각할 수 있다. 하지만 다양한 이유로 각각의 서브태스크의 작업완료 시간이 크게 달라질 수 있다.

포크/조인 프레임워크에서는 작업훔치기(work stealing)라는 기법으로 이 문제를 해결한다.

각각의 스레드는 자신에게 할당된 작업이 모두 끝나면 다른 스레드의 큐에서 작업을 가져와 처리한다. 따라서 태스크의 크기를 작게 나누어야 스레드 간의 작업부하를 비슷한 수준으로 유지할 수 있다. 실제 코어 수 보다 더 잘게 나누는 이유이다.



#### 7.3 Spliterator 인터페이스

Spliterator는 '분할할 수 있는 반복자'라는 의미이다. Iterator처럼 소스의 요소 탐색 기능을 제공하며, 병렬 작업에 특화되어 있다.

```
public interface Spliterator<T> {
  boolean tryAdvance(Consumer<? super T> action);
  Spliterator<T> trySplit();
  long estimateSize();
  int characteristics();
}
```

tryAdvace - Spliterator의 요소를 순차적으로 소비하면서 탐색해야할 요소가 있으면 참을 반환

trySplit - Spliterator의 일부 요소(자신이 반환한 요소)를 분할해서 두 번째 Spliterator를 생성

estimateSize - 탐색해야할 요소 수

characteristics - Spliterator의 특성을 정의

**7.3.1 분할 과정**

스트림을 여러 스트림으로 분할하는 과정은 재귀적으로 일어난다.

1단계에서 첫 번째 Spliterator에서 trySplit을 호출하면 두 번째 Spliterator가 생성되고, 2단계에서 두 번째 Spliterator에서 trySplit을 호출하면 네 개의 Spliterator가 생성된다. 이는 trySplit가 null이 될때까지 반복한다.

&#x20;

Spliterator 특성

characteristics 추상 메서드로 정의하며, Spliterator 자체 특성 집항르 포함하는 int를 반환한다.

| 특성        | 의미                                                   |
| --------- | ---------------------------------------------------- |
| ORDERED   | 리스트처럼 정해진 순서가 있으므로 요소를 탐색하고 분할할 때 순서에 유의해야 함         |
| DISTINCT  | x, y 두 요소를 방문했을 때 x.equals(y)는 항상 false를 반환          |
| SORTED    | 탐색된 요소는 미리 정의된 정렬 순서를 따른다.                           |
| SIZED     | 크기가 알려진 소스로 생성했으므로 estimatedSize()는 정확한 값을 반환한다.     |
| NON-NULL  | 탐새갛느 모든 요소는 null이 아니다.                               |
| IMMUTABLE | 이 Spliterator의 소스는 불변이다. 요소를 탐색하는 동안 추가/삭제/수정할 수 없다. |
| CONCURRET | 동기화 없이 Spliterator의 소스를 여러 스레드에서 동시에 고칠 수 있다.        |
| SUBSIZED  | 이 Spliterator와 분할되는 모든 spliterator의 SIZED 특성을 갖는다.   |

**7.3.2 커스텀 Spliterator 구현하기**

반복형으로 단어 수를 세는 메서드

```
public int countWordsIteratively(String s) {
  int counter = 0;
  boolean lastSpace = true;
  for (char c : s.toCharArray()) { //문자열의 모든 문자를 하나씩 탐색
    if (Character.isWhiteSpace(c)) {
      lastSpace = true;
    } else {
      if (lastSpace) conter++; //공백문자 탐색 시 이전까지의 문자를 단어를 간주하여 단어 수 counting
      lastSpace = false;
    }
  }
  return counter;
}
```

&#x20;

함수형으로 단어 수를 세는 메서드

```
class WordCounter {
  private final int counter;
  private final boolean lastSpace;
  
  public WordCounter(int counter, boolean lastSpace) {
    this.counter = counter;
    this.lastSpace = lastSpace;
  }
  
  public WordCounter accumulate(Character c) {
    if (Character.isWhitespace(c)) {
      return lastSpace ? this : new WordCounter(counter, true);
    } else {
      return lastSpace ? new WordCounter(counter+1, false) : this;
    }
  }
  
  public WordCounter combine(WordCounter wordCounter) {
    return new WordCounter(counter + wordCounter.conter, wordCounter.lastSpace);
  }
  
  public int getConter() {
    return conter;
  }
}
```

스트림을 탐색하며 새로운 문자를 찾을때마다 accumulate를 호출한다.

accumulate 메서드는 새로운 WordCounter 클래스를 어떤 상태로 생성할 것인지 정의한다.&#x20;

combine은 문자열 서브스트림을 처리한 WordCounter의 결과를 합친다.

&#x20;

위 코드를 문자열 스트림의 리듀싱 연산으로 구현해보면,

```
private int countWord(Stream<Character> stream) {
  WordCounter wordCounter = stream.reduce(
    new WordCounter(0, true),
        WordCounter::accumulate,
        WordCounter::combine);
  return wordCounter.getCounter(); 
}
```

&#x20;

WordCounter 병렬로 수행하기

위 연산을 병렬 스트림으로 처리하면 원하는 결과가 나오지 않는다. 원래 문자열을 임의의 위치에서 둘로 나누다보니 하나의 단어를 둘로 계산하는 상황이 발생할 수 있기 때문이다.

따라서 문자열을 임의의 위치에서 분할하지 않고 단어가 끝나는 위치에서만 분할하도록 trySplit() 메서드를 구현해주면 된다.

\
