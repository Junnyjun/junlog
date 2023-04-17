# STREAM2

#### 숫자형 스트림

reduce 메서드로 스트림 요소의 합을 구하는 방식은 다음과 같다.

```
int calories = menu.stream().map(Dish::getCalories).reduce(0, Integer::sum);
```

하지만 위 코드에선 합계를 계산하기 전에 Integer를 기본형으로 언박싱해야한다.

**5.7.1 기본형 특화 스트림**

스트림 API는 박싱 비용을 피할 수 있도록 IntStream, DoubleStream, LoingStream을 제공한다.

각각의 인터페이스는 sum, max와 같은 숫자 관련 리듀싱 연산 수행 메서드를 제공하며, 다시 객체 스트림으로 복원할 수 있는 기능도 제공한다.

&#x20;

숫자 스트림으로 매핑

```
int calories = menu.stream().mapToInt(Dish::getCalories).sum();
```

mapToInt 메서드는 각 요리에서 모든 칼로리(Interger형식)를 추출한 다음에 IntStream을(Stream\<Integer>가 아님) 반환한다. 스트림이 비어있으면 sum은 기본값 0을 반환한다.

&#x20;

객체 스트림으로 복원하기

boxed 메서드를 이용해서 특화 스트림을 일반 스트림으로 변환할 수 있다.

```
IntStream intStream = menu.stream().mapToInt(Dish::getCalories);
Stream<Integer> stream = intStream.boxed();
```

&#x20;

기본값: OptionalInt

Optional을 Integer, String 등의 참조 형식으로 파라미터화할 수 있으며, OptionalInt, OptionalDouble, OptionalLoing 세 가지 기본형 특화 스트림 버전도 제공한다.

```
OptionalInt maxCalories = menu.stream().mapToInt(Dish::getCalories).max();
```

**5.7.2 숫자 범위**

IntStream과 LongStream에서는 range와 rangeClosed라는 두 가지 정적 메서드를 제공한다. 두 메서드 모두 시작값과 종료값을 인수로 가진다.

range 메서드는 시작값과 종료값이 결과에 포함되지 않는 반면, rangeClosed는 시작값과 종료값이 결과에 포함된다.

```
IntStream evenNumbers = IntStream.rangeClosed(1, 100).filter(n -> n % 2 == 0);
```

**5.7.3 숫자 스트림 활용 : 피라고라스 수**

한 단계씩 문제를 해결해가며 숫자 스트림과 스트림 연산을 활용해 '피타고라스 수' 스트림을 만들어보자.

&#x20;

피타고라스 수

a x a + b x b = c x c

&#x20;

세 수 표현하기

int 배열을 먼저 정의하자. 예를 들어 (3,4,5)를 new int\[] {3, 4, 5}로 표현할 수 있다.

&#x20;

좋은 필터링 조합

피타고라스 정의를 만족하는 정수 조합을 찾아보자.

a와 b 두 수가 있을때 c가 정수인지 확인하기 위해 다음 코드를 사용할 수 있다.

Math.sqrt(a\*a + b\*b) % 1 == 0;

&#x20;

집합 생성

정수 조합을 필터링한 후에 map을 이용해 각 요소를 피타고라스 수로 변환할 수 있다.

```
stream.filter(b -> Math.sqrt(a*a + b*b) % 1 == 0)
   .map(b -> new int[]{a, b, (int)Math.sqrt(a * a + b * b)});
```

&#x20;

b값 생성

Stream.rangeClosed로 b 값의 범위를 생성해보자.

```
//case1
IntStream.rangeClosed(1, 100)
   .stream.filter(b -> Math.sqrt(a*a + b*b) % 1 == 0)
   .boxed()
   .map(b -> new int[]{a, b, (int)Math.sqrt(a * a + b * b)});

//case2
IntStream.rangeClosed(1, 100)
   .stream.filter(b -> Math.sqrt(a*a + b*b) % 1 == 0)
   .mapToObj(b -> new int[]{a, b, (int)Math.sqrt(a * a + b * b)});
```

IntStream의 map은 스트림의 각 요소로 int가 반환될 것을 기대한다. 따라서 boxed 메서드로 Stream\<Integer>형태로 변환한 후에 map 메서드를 사용하거나, IntStream의 mapToObj 메서드를 사용해야한다.

&#x20;

a값 생성

마지막으로 a값을 생성한다.

```
Stream<int[]> pythagoreanTriples = IntStream.rangeClose(1, 100).boxed()
   .flatMap(a -> IntStream.rangeClose(a, 100)
      .filter(b -> Math.sqrt(a*a + b*b) % 1 == 0)
      .mapToObj(b -> new int[]{a b, (int)Math.sqrt(a * a + b * b)})
```

스트림 a의 값을 매핑하면 스트림의 스트림이 만들어지므로 flatMap 메서드를 이용해 평준화한다.

중복된 수를 방지하기위해 b 값의 범위는 (a, 100)으로 변경한다.

&#x20;

개선할 점?

현재 코드에서는 제곱근을 두번 계산한다. 원하는 조건의 결과만 필터링하도록 수정해보자.

```
Stream<int[]> pythagoreanTriples = IntStream.rangeClose(1, 100).boxed()
   .flatMap(a -> IntStream.rangeClose(a, 100)
      .mapToObj(b -> new int[]{a b, (int)Math.sqrt(a * a + b * b)})
      .filter(t -> t[2] % 1 == 0));
```

***

#### 5.8 스트림 만들기

**5.8.1 값으로 스트림 만들기**

임의의 수를 인수로 받는 정적 메서드 Stream.of를 이용해서 스트림을 만들 수 있다.

```
Stream<String> stream = Stream.of("Modern", "Java", "In", "Action");
```

empty 메서드를 이용해서 스트림을 비울 수 있다.

```
Stream<String> emptyStream = Stream.empty();
```

**5.8.2 null이 될 수 있는 객체로 스트림 만들기**

ofNullable 메서드로 null이 될 수 있는 객체를 포함하는 스트림을 만들 수 있다.

이 패턴은 flatMap과 함께 사용하는 상황에서 유용하다.

```
Stream<String> stream = Stream.ofNullable(System.getProperty("home"));

Stream<String> values = Stream.of("config", "home", "user")
   .flatMap(key -> Stream.ofNullable(System.getProperty(key)));
```

**5.8.3 배열로 스트림 만들기**

배열을 인수로 받는 정적 메서드 Arrays.stream을 이용해서 스트림을 만들 수 있다.

```
int[] numbers = {2, 3, 5, 7, 11, 13};
int sum = Arrays.stream(numbers).sum();
```

**5.8.4 파일로 스트림 만들기**

파일을 처리하는 등의 I/O 연산에 사용하는 자바의 NIO API(비블록 I/O)도 스트림 API를 활용할 수 있다.

Files.lines로 파일의 각 행 요소를 반환하는 스트림을 얻을 수 있다.

Stream 인터페이스는 AutoCloseable 인터페이스를 구현하므로, try 블록 내의 자원은 자동으로 관리된다.

```
long uniqueWords = 0;
try(Stream<String> lines = Files.lines(Paths.get("data.txt"), Charset.defaultCharset())) {
  uniqueWords = lines.flatMap(line -> Arrays.stream(line.split(" ")))
    .distinct()
    .count();
} catch (IOException e) {
  //
}
```

&#x20;

**5.8.5 함수로 무한 스트림 만들기**

Stream.iterate와 Stream.generate를 이용해서 함수에서 스트림을 만들 수 있다.

두 연산을 이용하면 무한 스트림, 즉 고정된 컬렉션에서 고정된 크기로 스트림을 만들었던 것과 달리 크기가 고정되지 않은 스트림을 만들 수 있다.

&#x20;

iterate 메서드

```
Stream.iterate(0, n -> n + 2)
   .limit(10)
   .forEach(System.out::println);
```

iterate 메서드는 초깃값과 람다를 인수로 받아서 새로운 값을 끊임업이 생산할 수 있다.

예제에서는 람다 n -> n+2, 즉 이전 결과에 2를 더한 값을 반환한다.

iterate는 요청할 때마다 값을 생산할 수 있으며 끝이 없으므로 무한 스트림을 만든다. 이러한 스트림을 언바운드 스트림이라고 표현한다.

&#x20;

iterate 메서드는 프레디케이트를 지원한다. 두 번째 인수로 프레디 케이트를 받아 작업 중단의 기준으로 사용한다.

0에서 시작해서 100보다 크면 숫자 생성을 중단하는 코드를 다음처럼 구현할 수 있다.

```
IntStream.iterate(0, n -> n < 100, n -> n + 4)
   .forEach(System.out::println);
```

filter로도 같은 결과를 얻을수 있다고 생각할 수 있지만, filter 메서드는 언제 이 작업을 중단해야 하는지 알 수 없다.

이럴 때에는 talkWhile 메서드를 사용해야 한다.

```
// 불가
IntStream.iterate(0, n -> n + 4)
   .filter(n -> n < 100)
   .forEach(System.out::println);
   
// 가능
IntStream.iterate(0, n -> n + 4)
   .talkWhile(n -> n < 100)
   .forEach(System.out::println);
```

&#x20;

generate 메서드

iterate와 달리 generate는 생산된 각 값을 연속적으로 계산하지 않으며, Supplier\<T>를 인수로 받아서 새로운 값을 생산한다.

```
Stream.generate(Math::random)
  .limit(5)
  .forEach(System.out::println);
```

위 코드는 0에서 1 사이의 임의의 더블 숫자 5개를 만든다.
