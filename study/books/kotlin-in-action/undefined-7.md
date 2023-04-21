# 제너릭

### 제네릭 타입 파라미터 <a href="#ec-a-c-eb-a-eb-a-a-d-ed-ec-e-ed-c-c-eb-d-bc-eb-af-b-ed-b" id="ec-a-c-eb-a-eb-a-a-d-ed-ec-e-ed-c-c-eb-d-bc-eb-af-b-ed-b"></a>

제네릭은 타입 파라미터를 정의내릴 수 있도록 도와준다. 인스턴스가 생성되는 순간, 타입 파라미터는 타입 인수로 변경된다. 예를 들어, `Map<K, V>`라고 선언한 후 `Map<String, Person>`와 같이 특정한 인자로 변경할 수 있다.

```reasonml
//string을 인자로 넘겨주기 때문에 자동으로 List<String>
val authors = listOf("Dmitry", "Svetlana")
//빈 리스트를 생성하기 때문에 List<String>라는 것을 명시적으로 표시
val readers: MutableList<String> = mutableListOf()
val readers = mutableListOf<String>()
```

> 📌 코틀린은 반드시 제네릭 타입의 타입 인자를 정의해야한다:\
> 자바의 경우 최초에는 제네릭이라는 개념이 없었고, 이후 업데이트를 통해 1.5에서 처음 제네릭이 나왔다. 이로 인해 자바에서는 컬렉션을 선언할 때 원소 타입을 지정하지 않아도 컬렉션을 생성할 수 있었다. 그러나 코틀린은 처음부터 제네릭을 도입했기 때문에 반드시 제네릭 타입의 인자를 정의해주어야(프로그래머가 정의하든, 타입 추론에 의해 정의되든) 사용할 수 있다.

#### 제네릭 함수와 프로퍼티 <a href="#ec-a-c-eb-a-eb-a-a-d-ed-a-ec-ec-ed-eb-a-c-ed-d-bc-ed-b-b" id="ec-a-c-eb-a-eb-a-a-d-ed-a-ec-ec-ed-eb-a-c-ed-d-bc-ed-b-b"></a>

리스트를 사용하는 함수이지만 그 어떤 타입의 리스트인 간에 상관없이 사용할 수 있게 만들고 싶다면 `제너릭 함수`를 만들어야 한다. 제너릭 함수는 자신만의 타입 파라미터를 가지고 있다. 이러한 타입 파라미터는 함수가 깨어날 때 특정한 인자로 변경되어야 한다.

<figure><img src="https://blog.kakaocdn.net/dn/t6B2X/btrXbrzBKGf/f4Nn37H2ZEYX5iMUpYXLwK/img.png" alt=""><figcaption></figcaption></figure>

위의 slice 함수를 호출할 때는 타입 인자를 명시적으로 표현해야 한다. 그러나 많은 경우 컴파일러가 이를 추론하기 때문에 그럴 필요가 없다. 예를 들어, `letters.slice(10..13)`와 같이 호출한다면 컴파일러는 타입 파라미터 T가 Char라는 것을 자동으로 추론한다. 즉, `letters.slice<Char>(0..2)`처럼 직접 명시해 줄 필요가 없다.

```kotlin
val authors = listOf("Dmitry", "Svetlana")
val readers = mutableListOf<String>(/* ... */)
fun <T> List<T>.filter(predicate: (T) -> Boolean): List<T>
>>> readers.filter { it !in authors }
```

위의 예시에서, `it`은 `String`이 된다. 컴파일러는 이를 추론하여 `제너릭 타입 T`가 `String`이 된다는 것을 알아낼 수 있다.

### 제너릭 클래스 선언 <a href="#ec-a-c-eb-eb-a-a-d-ed-b-eb-e-ec-a-a-ec-a-ec-b" id="ec-a-c-eb-eb-a-a-d-ed-b-eb-e-ec-a-a-ec-a-ec-b"></a>

코틀린은 자바와 마찬가지로 꺽쇠 기호 `<>`를 사용하여 클래스나 인터페이스를 제네릭하게 만들 수 있다. 이렇게 정의내린 후에는 타입 파라미터를 클래스의 본문에서 사용할 수 있다. 예시로 코틀린의 List 인터페이스를 살펴보자.

```kotlin
interface List<T> { //타입 파라미터 T를 정의내림
	operator fun get(index: Int): T //T는 레귤러 타입과 마찬가지로 인터페이스나 클래스에서 사용 가능
	// ...
}
```

기본적으로, 코틀린의 제네릭은 자바와 매우 비슷하다. 이제부터는 다른 점을 살펴보도록 하자.

### 타입 파라미터 제약 <a href="#ed-ec-e-ed-c-c-eb-d-bc-eb-af-b-ed-b-ec-a-c-ec-bd" id="ed-ec-e-ed-c-c-eb-d-bc-eb-af-b-ed-b-ec-a-c-ec-bd"></a>

`type paramter constraint`는 클래스나 함수에 사용할 수 있는 타입 인자를 제한하는 기능이다. 예를 들어, 리스트에 있는 원소를 모두 더하는 함수가 있다고 가정하자. 이 함수는 `List<Int>`나 `List<Double>`에 사용될 수 있지만, `List<String>`에는 사용 불가능하다. 이를 표현하기 위해서 타입 파라미터는 숫자만 될 수 있다는 것을 명시해야 한다.

어떤 타입을 제네릭 타입의 타입 파라미터에 대한 상한(upper bound)으로 지정하면 그 제네릭 타입을 인스턴화할 때 사용하는 타입 인자는 반드시 그 상한 타입이거나 그 상한 타입의 하위 타입이어야 한다.

<figure><img src="https://blog.kakaocdn.net/dn/bW40PG/btrXdNIRFmI/UyLgJltiKvCL2hNLLBkXkK/img.png" alt=""><figcaption></figcaption></figure>

이때 제한을 명시하기 위해 타입 파라미터 뒤에 `:`를 표시하여 상한을 보여줄 수 있다. 이렇게 한계를 명시하면 함수는 그 한계를 만족하는 클래스가 생성될 때만 깨어난다. 다음은 최대값을 찾는 함수로, 서로 비교 가능한 값에 한해서만 사용할 수 있는 함수다.

```kotlin
fun <T: Comparable<T>> max(first: T, second: T): T {
	return if (first > second) first else second
}
>>> println(max("kotlin", "java"))
kotlin

/* 비교 불가능한 값에 대해 호출될 때는 에러 발생 */
>>> println(max("kotlin", 42))
ERROR: Type parameter bound for T is not satisfied:
inferred type Any is not a subtype of Comparable<Any>
```

위에서 보이는 상한은 `Comparable<T>`이다. String 클래스는 `Comparable<String>`를 상속하기 때문에 상한에 걸리지 않는다.

#### 타입 파라미터 제약이 둘 이상인 경우 <a href="#ed-ec-e-ed-c-c-eb-d-bc-eb-af-b-ed-b-ec-a-c-ec-bd-ec-d-b-eb-ec-d-b-ec-ec-d-b-ea-b-bd-ec-a-b" id="ed-ec-e-ed-c-c-eb-d-bc-eb-af-b-ed-b-ec-a-c-ec-bd-ec-d-b-eb-ec-d-b-ec-ec-d-b-ea-b-bd-ec-a-b"></a>

드물지만 두 개 이상의 제약을 두어야 하는 경우도 있다. 다음 코드에는 타입 인자가 `CharSequence`와 `Appendable` 인터페이스를 둘 다 구현해야 한다. 이 말은 data를 접근하는 연산자와 데이터를 변경하는 연산자가 모두 사용 가능한 타입이어야 한다는 것이다.

```kotlin
fun <T> ensureTrailingPeriod(seq: T)
	where T : CharSequence, T : Appendable {
	if (!seq.endsWith('.')) {
		seq.append('.')
	}
}
>>> val helloWorld = StringBuilder("Hello World")
>>> ensureTrailingPeriod(helloWorld)
>>> println(helloWorld)
Hello World.
```

### 타입 파라미터를 널이 될 수 없는 타입으로 한정 <a href="#ed-ec-e-ed-c-c-eb-d-bc-eb-af-b-ed-b-eb-a-bc-eb-ec-d-b-eb-a-ec-ec-eb-a-ed-ec-e-ec-c-bc-eb-a-c-ed-c-ec" id="ed-ec-e-ed-c-c-eb-d-bc-eb-af-b-ed-b-eb-a-bc-eb-ec-d-b-eb-a-ec-ec-eb-a-ed-ec-e-ec-c-bc-eb-a-c-ed-c-ec"></a>

타입 상한을 정하지 않은 타입 파라미터는 `Any?`를 상한으로 정한 파라미터라고 할 수 있다.

```crystal
class Processor<T> {
	fun process(value: T) {
		value?.hashCode()
	}
}
```

예를 들어서 위의 코드에서 `T` 뒤에 `?`가 붙어 있지는 않지만, `value`는 널이 될 수 있다. 왜냐하면 상한을 정하지 않았기 때문에 `Any?`가 자동으로 상한이 되기 때문이다. 만약 타입 파라미터를 널이 될 수 없는 값으로 한정하고 싶다면, 상한을 `Any`로 설정하면 된다.

### 런타임 시 제네릭스의 동작 : erased와 reified type parameters <a href="#eb-f-b-ed-ec-e-ec-b-c-ec-a-c-eb-a-eb-a-a-d-ec-a-a-ec-d-eb-f-ec-e-a-erased-ec-reified-type-parameters" id="eb-f-b-ed-ec-e-ec-b-c-ec-a-c-eb-a-eb-a-a-d-ec-a-a-ec-d-eb-f-ec-e-a-erased-ec-reified-type-parameters"></a>

JVM의 제네릭스는 보통 타입 소거를 사용해 구현되는데, 이는 런타임 시점에 제네릭 클래스의 인스턴스에 타입 인자 정보가 들어있지 않다는 것을 의미한다.

> 📌 Generic Type erasure란?\
> 소거란 원소 타입을 컴파일 타입에만 검사하고 런타임에는 해당 타입 정보를 알 수 없다는 말이다. 즉, 컴파일 타임에만 타입 제약 조건을 정의하고, 런타임에는 타입을 제거한다는 뜻이다.

### 실행 시점의 제네릭 : 타입 검사와 캐스트 <a href="#ec-b-a-ed-ec-b-c-ec-a-ec-d-ec-a-c-eb-a-eb-a-a-d-a-ed-ec-e-ea-b-ec-ac-ec-ec-ba-ec-a-a-ed-a-b" id="ec-b-a-ed-ec-b-c-ec-a-ec-d-ec-a-c-eb-a-eb-a-a-d-a-ed-ec-e-ea-b-ec-ac-ec-ec-ba-ec-a-a-ed-a-b"></a>

자바와 마찬가지로 코틀린 제네릭 타입의 인자 정보는 런타임에 지워진다. 이는 제네릭 클래스 인스턴스가 그 인스턴스를 생성할 때 쓰인 타입 인자에 대한 정보를 유지하지 않는다는 뜻이다.

예를 들어, List\<String>를 생성하고 문자열을 이에 집어넣는다면, 런타임에는 이를 오직 List로만 보게 되고, 그 안의 원소가 어떤 타입인지를 구별하는 것이 불가능하다.

<figure><img src="https://blog.kakaocdn.net/dn/kOFqF/btrXaCuR2I1/BbBV2vU2Byb1etRzv0ipc1/img.png" alt=""><figcaption></figcaption></figure>

컴파일러는 두 개의 리스트가 다른 타입이라는 것을 알지만, 실행 시간에는 두 개가 서로 같게 보인다. 물론 List\<String>에는 오직 문자열만 있어야 하며, List\<Int>에는 오직 정수만 있어야 한다. 왜냐하면 컴파일러는 올바른 타입인 인자만 리스트에 저장되도록 만들기 때문이다.

이렇게 타입 정보를 지움으로써 생기는 제약을 알아보자. 타입 인자는 저장되지 않기 때문에 이를 확인할 수 없다. 따라서 다음과 같이 `is`를 사용해 확인할 수 없다.

```routeros
>>> if (value is List<String>) { ... }
ERROR: Cannot check for instance of erased type
```

이렇게 제너릭 타입 인자 정보를 지우는 것은 장점이 있다. 저장되어야 할 정보가 적어지기 때문에 사용해야 할 메모리의 양을 줄기 때문이다.

코틀린에서는 타입 인자를 명시하지않고 제네릭 타입을 사용할 수 없다. 그러면 특정 값이 집합이나 맵이 아니라 리스트라는 사실을 어떻게 확인할 수 있을까? 이는 바로 `star projection`를 사용하면 가능하다.

```ceylon
if (value is List<*>) { ... }
```

타입이 가진 모든 타입 파라미터에 대해서 `*`를 포함해야 한다. 이에 대해서는 뒤에서 좀 더 자세하게 배우도록 하고, 지금 당장으로써는 인자를 알 수 없는 제네릭 타입을 표현할 때 `*`를 사용한다는 것만 알도록 하자.

또한, `as`나 `as?` 캐스팅에도 제네릭 타입을 사용할 수 있다.

```kotlin
fun printSum(c: Collection<*>) {
	val intList = c as? List<Int>
		?: throw IllegalArgumentException("List is expected")
	println(intList.sum())
}
>>> printSum(listOf(1, 2, 3))
6
```

만약 타입에 대한 정보를 컴파일러가 알고 있다면, `is`를 사용해서 체크하는 것이 가능하다.

```kotlin
fun printSum(c: Collection<Int>) {
	if (c is List<Int>) {
		println(c.sum())
	}
}
>>> printSum(listOf(1, 2, 3))
6
```

여기서는 List\<Int>타입을 체크하는 것이 가능한데, 그 이유는 컴파일 타입에 해당 컬렉션이 정수를 가지고 있다는 것을 알 수 있기 때문이다.

### 실체화한 타입 파라미터를 사용한 함수 선언 <a href="#ec-b-a-ec-b-b-ed-ed-c-ed-ec-e-ed-c-c-eb-d-bc-eb-af-b-ed-b-eb-a-bc-ec-ac-ec-a-a-ed-c-ed-a-ec-ec-a-ec" id="ec-b-a-ec-b-b-ed-ed-c-ed-ec-e-ed-c-c-eb-d-bc-eb-af-b-ed-b-eb-a-bc-ec-ac-ec-a-a-ed-c-ed-a-ec-ec-a-ec"></a>

코틀린에서는 컴파일 타임에 제네릭 타입의 인자 정보가 지워짐으로 인해 제네릭 클래스의 인스턴스가 있어도 그 인스턴스를 만들 때 사용한 타입 인자를 알아낼 수가 없는데, 이는 제네릭 함수도 예외가 아니다.

```smali
>>> fun <T> isA(value: Any) = value is T
Error: Cannot check for instance of erased type: T
```

일반적으로는 그렇지만, 만약 `inline` 함수를 사용한다면 이러한 제약이 생기지 않는다. 그 이유는 인라인 함수의 타입 파라미터는 실체화되므로 실행 시점에 인라인 함수의 타입 인자를 알 수 있기 때문이다.

위의 함수를 인라인으로 만들고 타입 파라미터를 `reified`로 지정하면 value 타입이 T의 인스턴스인지를 실행 시점에 검사할 수 있다.

```kotlin
inline fun <reified T> isA(value: Any) = value is T
>>> println(isA<String>("abc"))
true
>>> println(isA<String>(123))
false
```

> 📌 인라인 함수에서만 reified type argument를 사용할 수 있는 이유\
> 컴파일러는 인라인 함수의 본문을 구현한 바이트코드를 그 함수가 호출되는 모든 시점에 삽입하는데, 이때 reified type argument를 통해 인라인 함수를 호출하는 각 부분의 정확한 타입을 모두 알아낼 수 있다. 따라서 컴파일러는 타입 인자로 쓰인 구체적인 클래스를 참조하는 바이트코드를 생성해 삽입할 수 있기 때문에, 실행 시점에 벌어지는 타입 소거의 영향을 받지 않는 바이트 코드가 생성된다.

### 실체화한 타입 파라미터로 클래스 참조 대체 <a href="#ec-b-a-ec-b-b-ed-ed-c-ed-ec-e-ed-c-c-eb-d-bc-eb-af-b-ed-b-eb-a-c-ed-b-eb-e-ec-a-a-ec-b-b-ec-a-b-eb-c" id="ec-b-a-ec-b-b-ed-ed-c-ed-ec-e-ed-c-c-eb-d-bc-eb-af-b-ed-b-eb-a-c-ed-b-eb-e-ec-a-a-ec-b-b-ec-a-b-eb-c"></a>

`java.lang.Class` 타입 인자를 파라미터로 받는 API에 대한 코틀린 어댑터를 구축하는 경우 `reified` 타입 파라미터를 자주 사용한다. `java.lang.Class`를 자주 사용하는 API의 예로는 JDK의 ServiceLoader가 있다. ServiceLoader는 어떤 추상 클래스나 인터페이스를 표현하는 `java.lang.Class`를 받아서 그 클래스나 인스턴스를 구현한 인스턴스를 반환한다.

```angelscript
val serviceImpl = ServiceLoader.load(Service::class.java)
/* reified 타입 파라미터를 사용 */
val serviceImpl = loadService<Service>()
```

### reified 타입 파라미터의 제약 <a href="#reified-ed-ec-e-ed-c-c-eb-d-bc-eb-af-b-ed-b-ec-d-ec-a-c-ec-bd" id="reified-ed-ec-e-ed-c-c-eb-d-bc-eb-af-b-ed-b-ec-d-ec-a-c-ec-bd"></a>

reified 타입 파라미터는 매우 유용한 도구이지만, 이에도 제약이 존재한다.

reified 타입 파라미터는 다음과 같은 경우 사용할 수 있다.

* 타입 검사와 캐스팅 (`is,!is,as,as?`)
* 10장에서 설명할 코틀린 리플렉션 API(`::class`)
* 코틀린 타입에 대응하는 `java.lang.Class` 얻기 (`::class.java`)
* 다른 함수를 호출할 때 타입 인자로 사용

하지만 다음과 같은 작업은 불가능

* 타입 파라미터 클래스의 인스턴스 생성하기
* 타입 파라미터 클래스의 동반 객체 메소드 호출하기
* reified 타입 파라미터를 요구하는 함수를 호출하면서 reified 하지 않은 타입 파라미터로 받은 타입을 타입 인자로 넘기기
* 클래스, 프로퍼티, 인라인 함수가 아닌 함수의 타입 파라미터를 reified로 지정하기

### Variance : 제네릭과 하위 타입 <a href="#variance-a-ec-a-c-eb-a-eb-a-a-d-ea-b-bc-ed-ec-c-ed-ec-e" id="variance-a-ec-a-c-eb-a-eb-a-a-d-ea-b-bc-ed-ec-c-ed-ec-e"></a>

변성(Variance)은 `List<String>`과 `List<Any>`의 기저 타입이 같고 타입 인자가 다른 여러 타입이 서로 어떤 관계가 있는지를 설명하는 개념이다.

#### Variance가 있는 이유 : 인자를 함수에 넘기기 <a href="#variance-ea-b-ec-e-eb-a-ec-d-b-ec-c-a-a-ec-d-b-ec-e-eb-a-bc-ed-a-ec-ec-eb-ea-b-b-ea-b-b" id="variance-ea-b-ec-e-eb-a-ec-d-b-ec-c-a-a-ec-d-b-ec-e-eb-a-bc-ed-a-ec-ec-eb-ea-b-b-ea-b-b"></a>

만약 List\<Any> 타입이 있다고 가정하자. 이럴 경우 여기에 List\<String>를 넘기면 안전할까? 당연히 안전하다. 그러나 Any와 String이 List 인터페이스의 타입 인자로 들어가는 경우라면 안정성을 확신하기 힘들다.

```kotlin
fun add Answer(list: MutableList<Any>){
    list.add(42)
}

val strings = mutableListOf("abc","bac")
addAnswer(strings) // 이 줄이 컴파일 될 경우
println(strings.maxBy { it.length }) // 런타임에 여기서 예외가 발생
```

위에서 볼 수 있듯이 어떤 함수가 리스트에 원소를 추가하거나 변경한다면 타입 불일치가 발생할 수 있기 때문에 `List<Any>` 대신 `List<string>`을 넘길 수 없다. 하지만 이러한 경우가 아니라면 괜찮다.

### 클래스, 타입, 하위 타입 <a href="#ed-b-eb-e-ec-a-a-c-ed-ec-e-c-ed-ec-c-ed-ec-e" id="ed-b-eb-e-ec-a-a-c-ed-ec-e-c-ed-ec-c-ed-ec-e"></a>

어떤 타입 A의 값이 필요한 모든 장소에 어떤 타입 B의 값을 넣어도 아무 문제가 없다면 B는 A의 `subtype`이라고 한다. 반대의 경우는 `supertype`이다.

<figure><img src="https://blog.kakaocdn.net/dn/SvY1M/btrW9pbDJC8/uXWRKI1TB2xXa1DvDLGuKk/img.png" alt=""><figcaption></figcaption></figure>

그렇다면 이 사실이 왜 중요한 것일까? 다음과 같은 경우를 보자.

```kotlin
fun test(i: Int) {
	val n: Number = i // 컴파일 성공

	fun f(s: String) { /*...*/ }
	f(i) // 컴파일 실패
}
```

앞에 문제인 "`List<String>`타입의 값을 `List<Any>`를 파라미터로 받는 함수에 전달해도 괜찮을까?”를 다시 서술해보면 이는 "`List<String>`타입은 `List<Any>`의 subtype인가?”이다.

이때 인스턴스 타입 사이의 subtype 관계가 성립하지 않으면 그 제네릭 타입을 invariant(무공변)이라고 말한다. 만약 A가 B의 subtype이라면 List\<A>는 List\<B>의 subtype이다. 이런 클래스나 인터페이스를 covariant(공변적)이라고 말한다.

### covariant(공변성) : 하위 타입 관계 유지 <a href="#covariant-ea-b-b-eb-b-ec-b-a-ed-ec-c-ed-ec-e-ea-b-ea-b-ec-c-a-ec-a" id="covariant-ea-b-b-eb-b-ec-b-a-ed-ec-c-ed-ec-e-ea-b-ea-b-ec-c-a-ec-a"></a>

Producer\<T>를 예로 공변성 클래스를 설명해보자. A가 B의 하위 타입일 때 Producer\<A>가 Producer\<B>의 하위 타입이면 Peoducer는 공변적이다. 이를 하위 타입 관계가 유지된다고 한다.

```kotlin
interface Producer<out T> {
	fun produce(): T
}
```

클래스의 타입 파라미터를 공변적으로 만들면 함수 정의에 사용한 파라미터 타입과 타입 인자의 타입이 정확히 일치하지 않더라도 그 클래스의 인스턴스를 함수 인자나 반환값으로 사용할 수 있다.

그런데 아무 클래스나 공변적으로 만든다면 굉장히 불안정해질 것이다. 타입 안정성을 보장하기 위해서 클래스의 맴버를 `in`과 `out` 포지션으로 나누어야 한다.

<figure><img src="https://blog.kakaocdn.net/dn/bAz46E/btrXdLK2Z66/fiyKrBuctLrGEvpObxK5lk/img.png" alt=""><figcaption></figcaption></figure>

즉, 클래스 멤버를 선언할 때 타입 파라미터를 사용할 수 있는 지점은 모두 `인(in)`과 `아웃(out)`위치로 나뉜다. 만약 T가 함수의 반환 타입에 쓰인다면 T는 `아웃` 위치에 있다. 그 함수는 T 타입의 값을 생산한다. T가 함수의 파라미터 타입에 쓰인다면 T는 `인` 위치에 있다.

### contravariance(반공변) : 뒤집힌 하위 타입 관계 <a href="#contravariance-eb-b-ea-b-b-eb-b-a-eb-a-ec-a-ed-e-c-ed-ec-c-ed-ec-e-ea-b-ea-b" id="contravariance-eb-b-ea-b-b-eb-b-a-eb-a-ec-a-ed-e-c-ed-ec-c-ed-ec-e-ea-b-ea-b"></a>

반공변 클래스의 하위 타입 관계는 공변 클래스의 경우와 반대다. Consumer\<T>를 예로 들어 설명하자면, 타입 B가 타입 A의 하위 타입인 경우 Consumer\<A>가 Consumer\<B>의 하위 타입인 관계가 성립하면 제네릭 클래스 Consumer\<T>는 타입 인자 T에 대해 반공변이다.

<figure><img src="https://blog.kakaocdn.net/dn/xG4JC/btrXaC2HzUQ/HsIDFGUnK39uJNVk4fERJk/img.png" alt=""><figcaption></figcaption></figure>

| covariant, 공변성                             | contravariance, 반공변성                       | invariant, 무공변   |
| ------------------------------------------ | ------------------------------------------ | ---------------- |
| Producer\<out T>                           | Consumer\<in T>                            | MutableList      |
| 타입 인자의 subtype 관계가 제네릭 타입에서도 유지            | 타입 인자의 subtype 관계가 제네릭 타입에서 역전             | subtype 관계 성립 안함 |
| Producer\<Cat>은 Producer\<Animal>의 subtype | Consumer\<Animal>은 Consumer\<Cat>의 subtype |                  |
| T를 out위치에만 사용 가능                           | T를 in위치에만 사용 가능                            | T를 아무데나 사용 가능    |

### Start Projection : 타입 인자 대신 \* 사용 <a href="#start-projection-a-ed-ec-e-ec-d-b-ec-e-eb-c-ec-b-a-ec-ac-ec-a-a" id="start-projection-a-ed-ec-e-ec-d-b-ec-e-eb-c-ec-b-a-ec-ac-ec-a-a"></a>

제네릭 타입 인자 정보가 없음을 표현하기 위해 Star Projection을 사용할 수 있고, 이는 `List<*>`와 같이 사용 가능하다.

첫째, `MutableList<>`는 `MutableList<Any?>`와 같지 않다. `MutableList<Any?>`는 모든 타입의 원소를 담을 수 있다는 사실을 알 수 있는 리스트다. 반면 `MutableList<*>`는 어떤 정해진 구체적인 타입의 원소만을 담는 리스트지만 그 원소의 타입을 정확히 모른다는 뜻이다.

```reasonml
>>> val list: MutableList<Any?> = mutableListOf('a', 1, "qwe")
>>> val chars = mutableListOf('a', 'b', 'c')
>>> val unknownElements: MutableList<*> =                
...         if (Random().nextBoolean()) list else chars
>>> unknownElements.add(42) // 컴파일러는 이 메소드 호출을 금지한다.                              
Error: Out-projected type 'MutableList<*>' prohibits
the use of 'fun add(element: E): Boolean'
>>> println(unknownElements.first()) // 원소를 가져와도 안전하다. first()는 Any? 타입의 원소를 반환한다. 
a
```

위의 예시에서 컴파일러는 `MutableList<*>`를 아웃 프로젝션 타입으로 인식하는데, 여기서의 `MutableList<*>`는 `MutableList<out Any?>`와 동일하게 동작한다. 어떤 리스트의 원소 타입을 모르더라도 그 리스트에서 안전하게 `Any?` 타입을 꺼내오는 것은 가능하지만, 타입을 모르는 리스트에 원소를 마음대로 넣는 것은 불가능하다.
