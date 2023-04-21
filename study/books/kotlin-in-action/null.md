# Null

그동안은 코틀린의 문법 중 넓은 범위의 것들을 살펴보았다. 이제 가장 중요한 부분을 배워보자: 바로 타입 시스템이다. 코틀린에서는 새롭게 만들어진 기능, 즉 nullable 타입과 read-only collection 등을 지원한다.

### Nullability <a href="#nullability" id="nullability"></a>

코틀린 타입 시스템에서는 자바에서 흔히 볼 수 있는 `java.lang.NullPointerException`를 피할 수 있기 위해서 컴파일 시점에 null 에러를 파악할 수 있도록 명시적으로 지원한다.

<pre class="language-arduino"><code class="lang-arduino">/* Java */
int strLen(String s) {
<strong>return s.length();
</strong>}
</code></pre>

자바에서는 위와 같은 함수에서 String이 null일 경우 NullPointerException 에러를 만들지만, 코틀린의 경우 String 타입이 반드시 넘겨지도록 강제하기 때문에 null이 포함된 매개변수를 넘겨 줄 수 없다. 만약 코틀린에서 null을 넘겨주고 싶다면 이를 `앨비스 연산자 ?`를 사용하여 보여줘야 한다.

```kotlin
fun strLen(s: String) = s.length
>>> strLen(null)
ERROR: Null can not be a value of a non-null type String

//명시적으로 null 타입을 넘겨주기 때문에 ? 사용
un strLenSafe(s: String?) = ...
```

null 타입을 명시한 후에는 할 수 있는 일이 제한되어 있다.

1. 더 이상 `fun strLenSafe(s: String?) = s.length()`처럼 메소드 호출 불가
2. null 타입이 아닌 값에게 `val x: String? = null`처럼 null 할당 불가
3. null 타입이 아닌 파라미터를 가진 함수에게 null 타입 pass 불가

단, if를 통해 null을 체크해 준 다음에는 컴파일러가 컴파일 하는 것이 가능하다.

```kotlin
fun strLenSafe(s: String?): Int =
if (s != null) s.length else 0
```

### 타입이란 무엇인가? <a href="#ed-ec-e-ec-d-b-eb-e-eb-ac-b-ec-ec-d-b-ea-b-f" id="ed-ec-e-ec-d-b-eb-e-eb-ac-b-ec-ec-d-b-ea-b-f"></a>

타입이란 `“해당 타입에 대해 가능한 값들을 집합으로 모아 놓은 분류”`이다.

자바에서는 String 타입에서 String 값과 null 값 둘 중에 하나를 가질 수 있다. 따라서 이런 경우 추가적인 타입 체크가 필요하다.

> 📌 자바에서도 `@Nullable`나 `@NotNull`를 활용하여 null 타입 체크가 가능하지만, 이는 별로 유용하지 않다. 또 다른 해결법은 `Optional class`를 활용하여 null 타입을 감싸는 것이지만, 이는 더 복잡한 코드를 생성하게 된다.

이러한 자바의 문제점을 코틀린은 Nullable 타입을 제공함으로써 손쉽게 해결 가능하다. `Nullable`과 `None-null`을 구분함으로 인해 어떠한 값이 어떤 계산을 할 수 있는지 명확하게 이해할 수 있다.

### Safe call operator: “?.” <a href="#safe-call-operator-a-e-c-f-e-d" id="safe-call-operator-a-e-c-f-e-d"></a>

해당 연산자는 null 체크와 동시에 메소드를 호출하는 역할을 한다.

```reasonml
s?.toUpperCase()
//같은 의미
if (s != null) s.toUpperCase() else null.
```

이때 주의할 점은, 비록 String.toUpperCase()의 호출 결과가 String이어야 하지만, s가 null일 때 함수의 호출 결과는 `String?`이 된다.

```kotlin
class Employee(val name: String, val manager: Employee?)
fun managerName(employee: Employee): String? = employee.manager?.name
```

위와 같이 ?.을 활용할 경우, manager가 존재하지 않는 경우에 대해서 추가적인 체크가 없이 한 줄로 바로 해결이 가능하다. 또한 다음과 같이 여러 개의 Safe call operator를 체인으로 만들어 각각의 값을 null인지 체크할 수 있다.

```kotlin
class Address(val streetAddress: String, val zipCode: Int, val city: String, val country: String)
class Company(val name: String, val address: Address?)
class Person(val name: String, val company: Company?)
fun Person.countryName(): String {
	val country = this.company?.address?.country
	return if (country != null) country else "Unknown"
}
```

### 앨비스 연산자: “?:” <a href="#ec-a-eb-b-ec-a-a-ec-b-ec-b-ec-e-a-e-c-f-a-e-d" id="ec-a-eb-b-ec-a-a-ec-b-ec-b-ec-e-a-e-c-f-a-e-d"></a>

코틀린에서는 null 대신 넣어줄 수 있는 디폴트 값을 처리하는 앨비스 연산자가 있다. 만약 값이 null이 아닐 경우에는 그 값을 활용하고, null일 경우에는 디폴트 값을 넣어준다. 이는 종종 `?.`와 같이 활용되기도 한다.

```kotlin
fun strLenSafe(s: String?): Int = s?.length ?: 0
>>> println(strLenSafe("abc"))
3
>>> println(strLenSafe(null))
0

fun Person.countryName() =
	company?.address?.country ?: "Unknown"
```

또한 코틀린에서는 앨비스 연산자를 `throw`나 `return`과 함께 사용할 수 있어 더욱 편리하다.

```angelscript
data class Address(val city: String, val country: String)

data class Company(val name: String, val address: Address?)

data class Employee(val name: String, val company: Company?)

fun print(employee: Employee) {
  // 엘비스 연산자로 throw도 가능
  val address = employee?.company?.address ?: throw IllegalArgumentException("Need Address")
  with(address) {
    print("city: $city, countyL $country")
  }
}
```

위의 코드에서 만약 address가 존재하지 않는다면, NullPointerException을 던지지 않고 그 대신 의미 있는 에러를 보여준다.

### 안전한 캐스팅: “as?” <a href="#ec-ec-a-ed-c-ec-ba-ec-a-a-ed-c-a-e-cas-f-e-d" id="ec-ec-a-ed-c-ec-ba-ec-a-a-ed-c-a-e-cas-f-e-d"></a>

자바에서는 캐스트하려는 값이 존재하지 않을 경우 타입 캐스트 중에 `ClassCastException`이 발생할 수 있지만, 코틀린은 `as?`를 통해 해당 에러가 발생하지 않도록 할 수 있다. 이러한 타입 캐스팅은 앨비스 연산자를 함께 활용할 수 있는데, 이는 equals 메소드를 사용하는 과정에서 잘 드러난다.

```kotlin
class Person(val firstName: String, val lastName: String) {
	override fun equals(o: Any?): Boolean {
		val otherPerson = o as? Person ?: return false
		return otherPerson.firstName == firstName &&
			otherPerson.lastName == lastName
	}
	override fun hashCode(): Int =
		firstName.hashCode() * 37 + lastName.hashCode()
}
```

위에서 볼 수 있듯이, 이러한 패턴을 활용하면 파라미터가 적절한 타입을 가지고 있는지 확인하고, 캐스팅하고, 적절하지 않을 경우 false를 돌려보낼 수 있다.

그러나 가끔씩은 컴파일러에게 null이 아니라는 것을 명시해줌으로써 null을 다룰 수도 있다.

### null 아님 단언: “!!” <a href="#null-ec-eb-b-eb-b-a-ec-b-a-e-c-e-d" id="null-ec-eb-b-eb-b-a-ec-b-a-e-c-e-d"></a>

```kotlin
fun ignoreNulls(s: String?) {
	val sNotNull: String = s!!
	println(sNotNull.length)
}
```

만약 s가 null일 경우에는 어떤 일이 생길까? NullPointerException와 같은 에러를 런타임에 던지는 등의 에러가 발생한다. 따라서 null 아님 단언은 NPE와 같은 에러를 감수할 때만 사용할 수 있다.

그러나 null 아님 단언을 유용하게 활용할 수 있는 상황도 있는데, 예를 들어 한 함수에서 이미 null이 아님을 체크했고 그 값을 다른 함수에서 활용할 때, 컴파일러는 해당 값이 null이 아니라는 것을 모른다. 따라서 명시적으로 표현해주어야 된다.

```kotlin
class CopyRowAction(val list: JList<String>) : AbstractAction() {
	override fun isEnabled(): Boolean =
		list.selectedValue != null
	override fun actionPerformed(e: ActionEvent) {
		val value = list.selectedValue!!
		// copy value to clipboard
	}
}
```

만약 위의 상황에서 `!!`를 사용하지 않는다면, `val value = list.selectedValue ?: return`를 통해 non-null 타입을 확보할 수 있다.

컴파일러는 에러가 발생하면 발생한 line을 추적하지 명령어를 체크하지 않기 때문에, 한 줄에 !!를 두 개 이상 사용하는 것은 바람직하지 못하다.

* `person.company!!.address!!.country //바람직하지 못한 코드`

### let 함수 <a href="#let-ed-a-ec" id="let-ed-a-ec"></a>

let 함수는 nullable을 다루기 더 쉽게 만들어준다. 주로 null이 불가능한 함수의 파라미터로 nullable한 타입의 값을 넘기려고 할 때 let을 활용한다.

```kotlin
fun sendEmailTo(email: String) { /*...*/ }
>>> val email: String? = ...
>>> sendEmailTo(email)
ERROR: Type mismatch: inferred type is String? but String was expected

//솔루션 중 하나: null이 아님을 체크한다.
if (email != null) sendEmailTo(email)
```

<figure><img src="https://blog.kakaocdn.net/dn/bH6WXW/btrWIOWkvME/DzgkPOZ5MHYfrUIS7k9la0/img.png" alt=""><figcaption></figcaption></figure>

let 함수를 활용하면 위의 문제를 해결할 수 있다. null이 아닐 때는 자동으로 생성된 `it`으로 활용 가능하다.

여러 값을 null인지 아닌지 체크해야 할 때는 중첩 let을 활용하여 할 수 있다. 그러나 이럴 경우 굉장히 코드가 복잡해질 수 있기 때문에, 평범한 if 조건문을 활용하는 것이 더욱 권장된다.

### null 불가능 타입의 지연 초기화 <a href="#null-eb-b-ea-b-eb-a-a-ed-ec-e-ec-d-ec-a-ec-b-ec-b-ea-b-b-ed" id="null-eb-b-ea-b-eb-a-a-ed-ec-e-ec-d-ec-a-ec-b-ec-b-ea-b-b-ed"></a>

많은 경우 초기화 메소드는 인스턴스가 생성된 직후 진행된다. 평범한 경우, 코틀린은 값을 생성자에서 초기화할 것을 권장하고 프로퍼티가 non-null일 경우에 non-null 초기화 값을 제공해야 한다. 그렇지 않을 경우에는 nullable 타입을 대신 사용해야 한다.

```kotlin
class MyService {
	fun performAction(): String = "foo"
}
class MyTest {
	private lateinit var myService: MyService
	@Before fun setUp() {
		myService = MyService()
	}
	@Test fun testAction() {
		Assert.assertEquals("foo", myService.performAction())
	}
}
```

지연 초기화의 프로퍼티는 항상 `var`이라는 것을 명시하자(읽기와 쓰기 가능). lateinit을 활용하면 null이 불가능한 타입을 사용할 수 있지만, 프로퍼티 초기화전에 접근할 경우에는 `“lateinit property myService has not been initialized”`와 같은 에러가 발생한다.

### nullable 타입의 확장 <a href="#nullable-ed-ec-e-ec-d-ed-ec-e-a" id="nullable-ed-ec-e-ec-d-ed-ec-e-a"></a>

```kotlin
//확장 함수를 호출하는 경우
fun verifyUserInput(input: String?) {
	if (input.isNullOrBlank()) {
		println("Please fill in the required fields")
	}
}
```

nullable 타입으로 확장 함수를 호출하는 경우, nullable 타입으로 해당 함수를 부를 수 있게 된다. 또한 null 타입을 가질 수 있기 때문에 이를 명시적으로 체크해줘야 한다.

### 타입 파라미터와 nullable <a href="#ed-ec-e-ed-c-c-eb-d-bc-eb-af-b-ed-b-ec-nullable" id="ed-ec-e-ed-c-c-eb-d-bc-eb-af-b-ed-b-ec-nullable"></a>

모든 함수의 타입 파라미터는 nullable일 수 있다. `?`로 끝나지 않아도 타입 파라미터는 null 타입을 허용한다.

```kotlin
// 타입 파라미터는 유일하게 **Any?**로 추론되므로 nullable하다.
fun <T> some1(): T = TODO()

// 상한을 두어 null이 불가능하게 할 수도 있다.
fun <T : Any> some2(): T = TODO()
```

### 자바와 nullable <a href="#ec-e-eb-b-ec-nullable" id="ec-e-eb-b-ec-nullable"></a>

코틀린에서와 달리 자바에서는 nullable 타입을 지원하지 않는다. 그렇다면 자바와 코틀린을 같이 사용할 때는 어떻게 될까?

첫째, 자바에서는 가끔씩 `@Nullable String`등으로 null에 대한 정보를 표시해준다. 이는 코틀린의 `String?`과 대치된다.

그런데 이렇게 nullable을 명시해주는 주석이 없는 경우는 `플랫폼 타입`을 사용한다.

### 플랫폼 타입 <a href="#ed-c-eb-e-ab-ed-f-bc-ed-ec-e" id="ed-c-eb-e-ab-ed-f-bc-ed-ec-e"></a>

코틀린이 null에 대한 정보를 알수 없는 타입일 경우, 처리를 개발자에게 전적으로 맡기게 된다.

```arduino
/* Java */
public class Person {
	private final String name;
	public Person(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
}
```

getName은 null을 반환할까, 아니면 반환하지 않을까? 코틀린 컴파일러는 이 경우에 null인지 아닌지 모르기 때문에 개발자가 스스로 해결해야 한다. 값이 null이 아니라는 것을 확신한다면 추가적인 확인 없이 사용할 수도 있다. 코틀린에서는 위의 자바 코드를 다음과 같이 다룰 수 있다.

```kotlin
fun yellAtSafe(person: Person) {
	println((person.name ?: "Anyone").toUpperCase() + "!!!")
}
>>> yellAtSafe(Person(null))
ANYONE!!!
```

### 상속 <a href="#ec-ec-d" id="ec-ec-d"></a>

자바 메소드를 코틀린에서 오버라이딩 한 경우, 파라미터와 리턴타입을 nullable 또는 non-null로 할 지 결정할 수 있다.

```kotlin
/* Java */
interface StringProcessor {
void process(String value);
}
**//두 가지 오버라이딩 모두 허용 가능**
class StringPrinter : StringProcessor {
	override fun process(value: String) {
		println(value)
	}
}
class NullableStringPrinter : StringProcessor {
	override fun process(value: String?) {
		if (value != null) {
			println(value)
		}
	}
}
```

이때 자바 메소드를 오버라이드하고, 메소드 변수가 null 불가능한 타입으로 선언되었을 경우 컴파일러는 null 아님을 단언하는 확인을 자동으로 추가해준다.

### 원시 타입 <a href="#ec-b-ec-b-c-ed-ec-e" id="ec-b-ec-b-c-ed-ec-e"></a>

자바는 원시 타입과 레퍼런스 타입의 명확한 구분을 가진다. 원시 타입은 그것의 값을 바로 가지고 있고, 레퍼런스 타입의 경우 해당 객체를 가지는 메모리 위치를 가지고 있다. 원시 타입의 경우 해당 값을 저장하거나 넘겨주기 더 편하지만, 컬렉션에 저장하거나 메소드를 호출할 수는 없기 때문에 자바에서는 특별한 wrapper 타입(Integer와 같은)을 제공한다. 따라서 컬렉션을 사용하기 위해서는 `Collection<Integer>`과 같이 사용해야 한다.

그런데 코틀린은 원시 타입과 래퍼 타입의 구분이 없다. 코틀린은 내부적으로 런타임에 가장 효율적인 방식으로 처리한다. 아래와 같이 더 편리하게 사용할 수 있다.

```reasonml
val i: Int = 1
val list: List<Int> = listOf(1, 2, 3)
```

자바의 원시 타입과 일치하는 타입 리스트는 다음과 같다.

* Integer types—Byte, Short, Int, Long
* Floating-point number types—Float, Double
* Character type—Char
* Boolean type—Boolean

### Number conversions <a href="#number-conversions" id="number-conversions"></a>

코틀린과 자바의 중요한 차이는 숫자의 변환이다. 코틀린은 한 숫자 타입에서 다른 숫자 타입으로 변환해주지 않는다(변환되는 값이 더 크다고 하더라도). 예를 들어 다음과 같은 코드는 type mismatch 에러를 생성한다.

```kotlin
val i = 1
val l: Long = i
```

따라서 `i.toLong()`처럼 타입 변환을 명시적으로 보여줘야 한다. 이러한 타입 변환은 숫자 뿐만 아니라 `toByte(), toShort(), toChar()`처럼 다른 타입에도 적용된다. 이러한 함수는 작은 타입에서 큰 타입, 큰 타입에서 작은 타입 모두 지원한다.

> 📌 참고: 원시 타입 리터럴\
> Long: use the L suffix: 123L. \
> Double: use the standard representation of floating-point \
> numbers: 0.12, 2.0, 1.2e10, 1.2e-10. \
> Float: use the f or F suffix: 123.4f, .456F, 1e3f. \
> Hexadecimal literals: use the 0x or 0X prefix (0xCAFEBABL). \
> Binary literals: 0b or 0B prefix (0b000000101).

만약 이러한 숫자 리터럴을 사용한다면, 컴파일러가 타입 변환을 명시적으로 할 수 있다.

```kotlin
fun foo(l: Long) = println(l)
>>> val b: Byte = 1
>>> val l = b + 1L **//리터럴을 사용하여 타입 변환 가능**
>>> foo(42)
42
```

### “Any”와 “Any?”: 최상위 타입 <a href="#e-cany-e-d-ec-e-cany-f-e-d-a-ec-b-c-ec-ec-c-ed-ec-e" id="e-cany-e-d-ec-e-cany-f-e-d-a-ec-b-c-ec-ec-c-ed-ec-e"></a>

마치 객체에서 `Object`가 모든 클래스의 루트가 되는 것처럼, 타입의 최상위는 `Any`가 된다. 자바에서는 Wrapper로 감싼 타입의 루트가 Object가 되지만, 코틀린에서는 모든 타입의 최상위 타입이 Any가 된다는 차이가 있다.

### ”Unit”: 코틀린의 “Void” <a href="#e-dunit-e-d-a-ec-bd-ed-b-eb-a-b-ec-d-e-cvoid-e-d" id="e-dunit-e-d-a-ec-bd-ed-b-eb-a-b-ec-d-e-cvoid-e-d"></a>

코틀린의 `Unit`은 자바의 `void`와 같은 역할을 한다. 즉, 돌려줄 만한 리턴 타입이 딱히 없을 때 사용된다. Unit은 생략 가능하다.

```kotlin
fun f(): Unit { ... }
fun f(): { ... }
```

Unit과 void의 차이점은 바로 Unit이 본격적인 타입이라는 것이다. void와 달리, Unit은 타입 요소로 사용될 수 있다. 단, Unit 타입에 대해서는 단 하나의 값만 존재한다-바로 `implicitly`이다. 또한 `return Unit`을 작성할 필요가 없는데, 컴파일러에 의해 자동으로 추가되기 때문이다.

### Nothing 타입: 이 함수는 리턴하지 않음 <a href="#nothing-ed-ec-e-a-ec-d-b-ed-a-ec-eb-a-eb-a-ac-ed-b-ed-ec-a-ec-a-ec-d-c" id="nothing-ed-ec-e-a-ec-d-b-ed-a-ec-eb-a-eb-a-ac-ed-b-ed-ec-a-ec-a-ec-d-c"></a>

값을 리턴해준다는 개념은 함수가 성공적으로 끝나지 않을 경우에 사용할 수 없다. 이때 Nothing 타입을 사용해주면 함수가 정상적으로 끝나지 않음을 표현할 수 있다. 오직 반환 타입으로만 쓸 수 있으며, 어떠한 값도 가지지 않는다.

### 컬렉션과 배열 <a href="#ec-bb-ac-eb-a-ec-ea-b-bc-eb-b-b-ec-b" id="ec-bb-ac-eb-a-ec-ea-b-bc-eb-b-b-ec-b"></a>

```kotlin
fun readNumbers(reader: BufferedReader): List<Int?> {
	val result = ArrayList<Int?>()
	for (line in reader.lineSequence()) {
		try {
			val number = line.toInt()
			result.add(number)
		}
		catch(e: NumberFormatException) {
			result.add(null)
		}
	}
	return result
}
```

`List<Int?>`는 `Int?:` 타입을 가지고 있는 배열이다. `List<Int>?`와의 차이는 다음과 같다.

<figure><img src="https://blog.kakaocdn.net/dn/bfc0jn/btrWJetpOoy/H1cxUHdUE1QsPUpdEXbTbk/img.png" alt=""><figcaption></figcaption></figure>

### Read-only and mutable collections <a href="#read-only-and-mutable-collections" id="read-only-and-mutable-collections"></a>

기본적으로 코틀린이 가진 컬렉션은 전부 변경할 수 없는 컬렉션이다. 그런데 `MutableCollection`을 사용하면 컬렉션을 변경할 수 있는 컬렉션을 사용할 수 있다. val과 var을 분리한 것처럼, 읽기 전용 컬렉션과 변경 가능 컬렉션을 분리하면 프로그램 안의 데이터에 무슨 일이 일어나고 있는지 더 쉽게 이해할 수 있다. 만약 Collection을 사용한다면 데이터의 변경은 일어나고 있지 않음을, MutableCollection에 데이터를 넘겨준다면 변경을 의도하고 있는 것임을 알 수 있다.

```kotlin
fun <T> copyElements(source: Collection<T>, target: MutableCollection<T>) {
	for (item in source) {
		target.add(item) **//변경 가능한 컬렉션에 넘겨줌**
	}
}
>>> val source: Collectionthread-safe<Int> = arrayListOf(3, 5, 7)
>>> val target: MutableCollection<Int> = arrayListOf(1)
>>> copyElements(source, target)
>>> println(target)
[1, 3, 5, 7]
```

그렇다고 해서 collection이 가리키는 데이터가 항상 변경 불가능하다는 것은 아니다. 다른 레퍼런스는 변경 가능한 `MutableCollection`일 수도 있다! 그런데 다양한 레퍼런스가 하나의 데이터를 가리키고 있을 때 `ConcurrentModificationException`이 발생할 수 있으므로, 읽기 전용과 변경 가능을 구분해주는 것이 필수적이다.

### Kotlin collections and Java <a href="#kotlin-collections-and-java" id="kotlin-collections-and-java"></a>

코틀린과 자바 컬렉션은 서로 상호작용하는 인터페이스가 존재한다. 이 사이에는 변환이 필요 없으며, 데이터를 복사하거나 wrapper를 생성할 필요가 없다. 그런데 모든 자바 컬렉션에는 2개의 코틀린 컬렉션, 즉 읽기 전용과 변경 가능한 컬렉션이 존재한다.

자바는 읽기 전용과 변경 가능을 구분하지 않기 때문에 읽기 전용 컬렉션을 변경하게 될 수도 있다. 이때 코틀린은 읽기 전용 컬렉션을 자바가 변경하려는 경우 그 호출을 거절한다.

```arduino
/* Java */
// CollectionUtils.java
public class CollectionUtils {
	public static List<String> uppercaseAll(List<String> items) {
		for (int i = 0; i < items.size(); i++) {
			items.set(i, items.get(i).toUpperCase()); //변경 시도
		}
		return items;
	}
}
// Kotlin
// collections.kt
fun printInUppercase(list: List<String>) {
	println(CollectionUtils.uppercaseAll(list))
	println(list.first())
}
>>> val list = listOf("a", "b", "c")
>>> printInUppercase(list)
[A, B, C]
A
```

따라서 코틀린 컬렉션을 자바에게 넘겨줄 때는 정확한 파라미터 타입을 개발자가 작성해야 한다.

### 배열 <a href="#eb-b-b-ec-b" id="eb-b-b-ec-b"></a>

코틀린에서 배열을 생성하기 위해서는 다음과 같은 경우를 거칠 수 있다.

1. arrayOf 함수로 배열을 생성한다.
2. arrayOfNulls 함수로 null을 포함한 배열을 만든다.
3. Array 생성자로 배열과 람다를 만든다. 각 요소들은 람다로 초기화된다. 배열은 non-null 요소로 초기화한다.
