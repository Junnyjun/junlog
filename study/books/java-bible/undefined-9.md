# 람다와 스트림

### 1. 람다식 <a href="#1" id="1"></a>

메서드를 하나의 식으로 표현한 것

모든 메서드는 클래스에 포함되어야 하므로 클래스도 새로 만들어야 하고, 객체도 생성해야만 비로소 이 메서드를 호출할 수 있다. 그러나 람다식은 이 모든 과정없이 오직 람다식 자체만으로도 이 메서드의 역할을 대신할 수 있다.

ex) 최댓값 반환\
`(a, b) -> a > b ? a: b`

#### 함수형 인터페이스 <a href="#undefined" id="undefined"></a>

람다식을 다루기 위한 인터페이스

함수형 인터페이스에는 오직 하나의 추상 메서드만 정의되어 있어야 한다.

람다식은 익명클래스와 동일하다.\
함수형 인터페이스를 구현한 익명 개체의 메서드와 람다식의 매개변수의 타입과 개수 그리고 반환값이 일치하기 때문에 함수형 인터페이스를 구현한 익명 개체를 람다식으로 대체가 가능하다.

```java
MyFunction f = new MyFunction() {
		public int max(int a, int b) {
       		return  a > b ? a : b;
    	}
	};
    
MyFunction f = (int a, int b) -> a > b ? a : b;
```

```java
@FunctionalInterface
interface MyFunction {
	public abstract int max(int a, int b);
}
```

#### 함수형 인터페이스 타입의 매개변수와 반환타입 <a href="#undefined" id="undefined"></a>

메서드의 매개변수의 타입이 함수형 인터페이스타입라면, 이 메서드를 호출할 때 람다식을 참조하는 참조변수를 매개변수로 지정해야한다.

메서드의 반환타입이 함수형 인터페이스타입이라면, 이 함수형 인터페이스의 추상메서드와 동등한 람다식을 가리키는 참조변수를 반환하거나 람다식을 직접 반환할 수 있다.

#### 람다식의 타입과 형변환 <a href="#undefined" id="undefined"></a>

함수형 인터페이스로 람다식을 참조할 수 있는 것일 뿐, 람다식의 탕비이 함수형 인터페이스의타입과 일치하는 것은 아니다.

람다식은 익명 객체이고 익명 객체는 타입이 없다.

람다ㅣ식은 오직 함수형 인터페이스로만 형변환이 가능하다.

#### 외부 변수를 참조하는 람다식 <a href="#undefined" id="undefined"></a>

람다식 내에서 참조하는 지역변수는 FINAL이 붙지 않았어도 상수로 간주된다.

외부 지역변숙와 같은 이름의 람다식 매개변수는 허용되지 않는다.

#### Java.util.function패키지 <a href="#javautilfunction" id="javautilfunction"></a>

일반적으로 자주 쓰이는 형식의 메서드를 함수형 인터페이스로 미리 정의해 놓은 패키지

| 함수형 인터페이스          | 메서드                    | 설명                                      |
| ------------------ | ---------------------- | --------------------------------------- |
| java.lang.Runnable | void run()             | 매개변수도 없고, 반환값도 없음                       |
| Supplier\<T>       | T get()                | 매개변수는 없고, 반환값만 있음                       |
| Consumer\<T>       | void accept(T t)       | 매개변수만 있고, 반환값이 없음                       |
| Function\<T,R>     | R apply(T t)           | 일반적인 함수. 하나의 매개변수를 받아서 결과를 반환           |
| Predicate\<T>      | boolean test(T t)      | 조건식을 표현하는데 사용됨. 매개변수는 하나, 반환타입은 boolean |
| BiConsumer\<T,U>   | void accept(T t, U u)  | 두개의 매개변수만 있고, 반환값이 없음                   |
| BiFunction\<T,U,R> | R apply(T t, U u)      | 두 개의 매개변수를 받아서 결과를 반환                   |
| BiPredicate\<T,U>  | boolean test(T t, U u) | 조건식을 표현하는데 사용됨. 매개변수는 둘, 반환타입은 boolean  |

**UnaryOperator와 BinaryOperator**

| 함수형 인터페이스          | 메서드               | 설명                                               |
| ------------------ | ----------------- | ------------------------------------------------ |
| UnaryOperator\<T>  | T apply(T t)      | Function의 자손. Function과 달리 매개변수와 결과의 타입이 같다.     |
| BinaryOperator\<T> | T apply(T t, T t) | BiFunction의 자손. BiFunction과 달리 매개변수와 결과의 타입이 같다. |

#### Function의 합성 <a href="#function" id="function"></a>

람다식을 합성해서 새로운 람다식을 만들 수 있다.

함수 f,g가 있을때, f.andThen(g)는 함수 f를 먼저 적용하고, 그 다음 함수 g를 적용한다.\
f.compose(g)는 반대로 g를 먼저 적용하고 f를 적용한다.

#### Predicate의 결합 <a href="#predicate" id="predicate"></a>

and(), or(), negate()로 연결해서 하나의 새로운 Predicate로 결합할 수 있다.

#### 메서드 참조 <a href="#undefined" id="undefined"></a>

하나의 메서드만 호출하는 람다식은 `클래스이름::메서드이름` 또는 `참조변수::메서드이름`으로 바꿀 수 있다.

| 종류               | 람다                         | 메서드 참조            |
| ---------------- | -------------------------- | ----------------- |
| static메서드 참조     | (x) -> ClassName.method(x) | ClassName::method |
| 인스턴스메서드 참조       | (obj, x) -> obj.method(x)  | ClassName::method |
| 특정 객체 인스턴스메서드 참조 | (x) -> obj.method(x)       | obj::method       |
| 생성자 참조           | () -> new ClassName()      | ClassName::new    |



### 2. 스트림(stream) <a href="#2-stream" id="2-stream"></a>

데이터 소스를 추상화하고, 데이터를 다루는데 자주 사용되는 메서드들을 정의해 놓았다.

스트림을 사용하면, 배열이나 컬렉션뿐만 아니라 파일에 저장된 데이터도 모두 같은 방식으로 다룰 수 있다.

#### 스트림의 특징 <a href="#undefined" id="undefined"></a>

* 스트림은 데이터 소스를 변경하지 않는다.
* 스트림은 일회용이다.
* 스트림은 작업을 내부 반복으로 처리한다.

#### 스트림의 연산 <a href="#undefined" id="undefined"></a>

* 중간 연산\
  연산결과가 스트림인 연산. 스트림에 연속해서 중간 연산할 수 있음.
* 최종 연산\
  연산결과가 스트림이 아닌 연산. 스트림의 요소를 소모하므로 단 한번만 가능.

#### 지연된 연산 <a href="#undefined" id="undefined"></a>

중간 연산을 호출하는 것은 단지 어떤 작업이 수행되어야하는지 지정해주는 것일 뿐이다. 최종 연산이 수행되어야 비로소 스트림의 요소들이 중간 연산을 거쳐 최종 연산에 소모된다.

#### 스트림 만들기 <a href="#undefined" id="undefined"></a>

* 컬렉션\
  컬렉션의 최고 조상인 Collection에 stream()이 정의되어 있다. 그래서 List와 Set을 구현한 클래스들은 모두 이 메서드로 스트림을 생성할 수 있다.
* 배열\
  배열을 소스로 하는 스트림을 생성하는 메서드는 Stream과 Arrays에 static메서드로 정의되어 있다.
* 특정 범위의 정수\
  IntStream과 LongStream은 지정된 범위의 연속된 정수를 스트림으로 생성해서 반환하는 range()와 rangeClosed()를 가지고 있다.
* 임의의 수\
  난수를 생성하는데 사용된느 Random크래스에는 인스턴스 메서드들이 포함되어 있다. 이 메서드들은 해당 타입의 난수들로 이루어진 스트림을 반환한다.\
  이 스트림은 크기가 정해지지 않은 '무한 스트림'이므로 limit()과 함께 사용하여 크기를 제한해주어야 한다.
* 람다식 - iterate(), generate()\
  iterate()는 씨앗값(seed)로 지정된 값부터 람다식에 의해 계산된 결과를 다시 seed값으로 계산을 반복하여 무한 스트림을 생성한다.\
  generate()는 람다식에 의해 계산된 값을 요소로 하는 무한 스트림을 생성해서 반환하지만, iterate와 달리 이전 결과를 이용하지는 않는다.
* 파일\
  list()는 지정된 디렉토리에 있는 파일의 목록을 소스로 하는 스트림을 생선한다.
* 빈스트림\
  요소가 하나도 없는 비어있는 스트림이다.
* 두 스트림의 연결\
  Stream의 static메서드인 concat()을 사용하면 두 스트림을 하나로 연결할 수 있다.

#### 중간연산 <a href="#undefined" id="undefined"></a>

* skip()\
  요소를 건너 뛴다.
* limit()\
  요소의 개수를 제한한다.
* filter()\
  주어진 조건(Predicate)에 맞지 않는 요소를 걸러낸다.\
  여러번 사용 가능하다.
* distinct()\
  스트림에서 중복된 요소들을 제거한다.
* sorted()\
  스트림을 정렬한다.\
  \-map()\
  스트림의 요소에 저장된 값 중에서 원하는 필드만 뽑아내거나 특정 형태로 변환해야 할 때 사용된다.
* peek()\
  연산과 연산 사이에 올바르게 처리되었는지 확인하고 싶을 떄 사용된다.

#### Optional <a href="#optional" id="optional"></a>

지네릭 클래스로 'T타입의 객체'를 감싸주는 래퍼 클래스이다.

if문으로 null체크 없이 정의된 메서드로 간결하고 안전한 코드를 작성할 수 있다.

#### 최종 연산 <a href="#undefined" id="undefined"></a>

* forEach()
* 조건검사 - allMatch(), anyMatch(), noneMatch(), findFirst(), findAny()
* 통계 - count(), sum(), average(), max(), min()
* 리듀싱 - reduce()
* collect

#### collect <a href="#collect" id="collect"></a>

> * collect()\
>   스트림의 최종연산. 배개변수로 컬렉터가 필요로 하다.
> * Collector\
>   인터페이스. 컬렉터는 이 인터페이스를 구현해야한다.
> * Collectors\
>   크래스. static메서드로 미리 작성된 컬렉터를 제공한다.

* 스트림을 컬렉션과 배열로 변환- toList(), toSet(), toMap(), toCollection(), toArray()
* 통계 - counting(), summingInt(), averagingInt(), maxBy(), minBy()
* 리듀싱 - reducing()
* 문자열 결합 - joining()
* 그룹화 분할 - groupingBy(), partitioningBy()
