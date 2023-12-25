# 어노테이션

지금까지 함수와 클래스에 대한 많은 feature를 보았지만, 이들은 전부 클래스나 함수의 이름을 명확하게 명시할 것을 요구한다. 예를 들어 함수를 호출하려면 해당 함수가 정의된 클래스나 이름, 또는 파라미터 타입을 알아야 한다. 그러나 `Annotations`과 `reflection`를 사용하면 이러한 제약에서 벗어나 미리 알고있지 않은 임의의 클래스를 다룰 수 있게 해준다.

* 애노테이션: 라이브러리가 요구하는 의미를 클래스에게 부여할 수 있음
* 리플렉션: 실행(runtime) 시점에 컴파일러 내부 구조를 분석할 수 있음

### 애노테이션 선언과 적용 <a href="#ec-a-eb-b-ed-c-ec-d-b-ec-ec-a-ec-b-ea-b-bc-ec-a-ec-a-a" id="ec-a-eb-b-ed-c-ec-d-b-ec-ec-a-ec-b-ea-b-bc-ec-a-ec-a-a"></a>

애노테이션의 핵심 컨셉은 코틀린에서도 비슷하다. 애노테이션은 추가적인 `메타데이터`를 선언할 때 사용할 수 있게 해준다. 메타데이터는 소스코드나 컴파일된 클래스 파일, 또는 런타임 때 접근가능하다.

### 애노테이션 적용 <a href="#ec-a-eb-b-ed-c-ec-d-b-ec-ec-a-ec-a-a" id="ec-a-eb-b-ed-c-ec-d-b-ec-ec-a-ec-a-a"></a>

애노테이션을 적용하기 위해서는 선언 시 함수나 클래스 이름 앞에 `@` 캐릭터를 넣으면 된다.

```kotlin
import org.junit.*
class MyTest {
	@Test fun testTrue() { //애노테이션 적용
		Assert.assertTrue(true)
	}
}
```

애노테이션은 다음과 같은 파라미터를 가질 수 있다: 원시 타입의 값, 문자열, enum, 클래스 참조, 다른 애노테이션 클래스, 그리고 지금까지 말한 요소들로 이뤄진 배열이 있다.

애노테이션 인자를 지정하는 문법은 자바와 약간 다르다.

* 클래스를 애노테이션 인자로 지정할 때: `@MyAnnotation(MyClass::class)`처럼 `::class`를 클래스 이름 뒤에 뒤에 넣어야 한다.
* 다른 애노테이션을 인자로 지정할 때: 인자로 들어가는 애노테이션의 이름 앞에 `@`를 넣지 않아야 한다.
* 배열을 인자로 지정할 때: `@RequestMapping(path = arrayOf("/foo", "/bar"))`처럼 `arrayOf` 함수를 사용한다. 하지만 자바에서 선언한 애노테이션 클래스를 사용하는 경우라면 `value`라는 이름의 파라미터가 자동으로 가변 길이 인자로 변환되게 된다. 따라서 이 경우에는 `arrayOf` 함수를 쓰지 않아도 된다.

애노테이션 인자는 컴파일 시점에 인지 가능해야 한다. 따라서 임의의 프로퍼티를 인자로 지정할 수는 없다. 프로퍼티를 애노테이션 인자로 사용하려면 그 앞에 `const` 변경자를 붙여야 한다.

```kotlin
const val TEST_TIMEOUT = 100L
@Test(timeout = TEST_TIMEOUT) fun testMethod() { ... }
```

만약 일반적인 프로퍼티를 사용하고 애노테이션 인자로 사용한다면, `““Only ‘const val’ can be used in constant expressions.”`에러를 얻게 된다.

애노테이션 타겟

일반적으로, 코틀린에서 하나의 선언은 자바에서 여러 개의 선언과 상응한다. 따라서 이들 중 어떤 요소가 애노테이션이 되어야 하는지 알기 어렵다.

애노테이트 되어야 하는 특정한 요소는 `use-site target` 선언으로 명시할 수 있다. 이 선언은 `@` 캐릭터와 애노테이션 이름 사이에 위치하며, `:`으로 분리되어 보인다. 다음 그림에서는 `get`이 use-site target이 된다.

<figure><img src="https://blog.kakaocdn.net/dn/bor9on/btrXqs6yvrB/ANIGEevJr725rdyRYfQK41/img.png" alt=""><figcaption></figcaption></figure>

만약 프로퍼티를 애노테이트해서 자바에서 선언했다면, 이는 상응하는 필드에 기본값으로 적용된다. 코틀린은 또한 애노테이션을 직접 프로퍼티에 적용할 수 있게 해준다.

지원되는 `use-site targets`의 전체 목록은 다음과 같다:

* `property`—Java annotations can’t be applied with this use-site target.
* `field`—프로퍼티에 의해 생성된 필드.
* `get`—프로퍼티 getter.
* `set`—프로퍼티 setter.
* `receiver`—확장 함수나 프로퍼티의 수신 파라미터.
* `param`—생성자 파라미터.
* `setparam`—프로퍼티 setter 파라미터.
* `delegate`—Field storing the delegate instance for a delegated property.
* `file`—Class containing top-level functions and properties declared in the file.

`file` 을 타겟으로 하는 애노테이션은 항상 파일의 `top level`에 놓여야 한다. 즉, `package` 지정 전에 있어야 한다.

#### JSON 직렬화를 맞춤화하기 위해 애노테이션 사용 <a href="#json-ec-a-eb-a-ac-ed-eb-a-bc-eb-a-e-ec-b-a-ed-ed-ea-b-b-ec-c-ed-b-ec-a-eb-b-ed-c-ec-d-b-ec-ec-ac-ec" id="json-ec-a-eb-a-ac-ed-eb-a-bc-eb-a-e-ec-b-a-ed-ed-ea-b-b-ec-c-ed-b-ec-a-eb-b-ed-c-ec-d-b-ec-ec-ac-ec"></a>

애노테이션이 사용되는 클래식한 케이스는 객체 직렬화를 맞춤화하는 상황이다. `직렬화(serialization)`란 객체를 저장하거나 네트워크로 보내기 위해 binary 또는 text 표현법으로 변환하는 과정을 의미한다. 이와 정반대의 과정을 `역직렬화(deserialization)`라고 한다. 이 과정에서 가장 일반적으로 쓰이는 포멧이 `JSON`이다.

이 챕터에서는 직렬화와 역직렬화를 위한 순수 코틀린 라이브러리, 즉 `JKid`의 구현를 알아보고자 한다.

간단한 예시로 `Person` 클래스의 직렬화와 역직렬화를 위한 코드가 있다. 이 코드에서는 `serialize` 함수에 인스턴스를 넘기고, 리턴값으로 JSON 표현법으로 표현된 string을 받는다.

```angelscript
data class Person(val name: String, val age: Int)
>>> val person = Person("Alice", 29)
>>> println(serialize(person))
{"age": 29, "name": "Alice"}
```

JSON 표현법은 `"age": 29`처럼 `key/value` 쌍을 가지는 객체이다. JSON 표현법에서 객체를 다시 얻기 위해서는 deserialize 함수를 사용할 수 있다.

```lisp
>>> val json = """{"name": "Alice", "age": 29}"""
>>> println(deserialize<Person>(json))
Person(name=Alice, age=29)
```

다음 그림은 JSON 표현법과 객체 간의 직렬화/역직렬화 전환 관계를 보여준다.

<figure><img src="https://blog.kakaocdn.net/dn/p5Rd4/btrXn5RK4fv/zr8bC7XjuBg8dytgHmeqU0/img.png" alt=""><figcaption></figcaption></figure>

애노테이션은 객체가 직렬화/역직렬화되는 방식을 맞춤형으로 만들기 위해 사용될 수 있다. 객체를 JSON으로 직렬화할 때, 라이브러리는 기본적으로 모든 프로퍼티 이름을 키로 만드는 방식으로 프로퍼티를 직렬화한다. 애노테이션은 이러한 기본값을 변경할 수 있다. 이때 `@JsonExclude`와 `@JsonName`라는 두 가지 애노테이션을 사용할 수 있다.

* @JsonExclude 애노테이션: 직렬화/역직렬화에서 제외되어야 하는 애노테이션을 명시한다.
* @JsonName 애노테이션: key/value 쌍에서 키로 주어진 string이 사용되어야 함을 명시한다(프로퍼티 이름이 기본적으로 키 값이 되지만, 이러한 기본 설정이 아니라 다른 키 값을 명시).

```less
data class Person(
	@JsonName("alias") val firstName: String, //firstName 프로퍼티의 키 이름은 alias
	@JsonExclude val age: Int? = null //age 프로퍼티는 직렬화/역직렬화에서 제외
)
```

JKid의 대부분의 기능, 즉 `serialize()`, `deserialize()`, `@JsonName`, 그리고 `@JsonExclude`를 살펴보았다. 이제 애노테이션 선언에 대해 알아보자.

### 애노테이션 선언 <a href="#ec-a-eb-b-ed-c-ec-d-b-ec-ec-a-ec-b" id="ec-a-eb-b-ed-c-ec-d-b-ec-ec-a-ec-b"></a>

위에서 언급한 `@JsonExclude`과 `@JsonName`의 선언 방식에 대해 알아보자.

```crystal
annotation class JsonExclude //파라미터를 가지지 않는JsonExclude 
annotation class JsonName(val name: String) //파라미터를 명시한 JsonName

/* Java에서 같은 애노테이션을 선언하는 방법 */
public @interface JsonName {
	String value();
}
```

선언 방식은 일반적인 클래스와 비슷해보인다. class 키워드 앞에 annotation이 추가된다는 것만 다르다.

자바에서는 위의 코드와 같이 value라는 메소드를 호출하는데, 코틀린에서는 `name` 프로퍼티만을 가지고 있다. 자바에서 value 메소드는 특별하다: 애노테이션을 적용할 때 value를 제외한 모든 애트리뷰트 이름을 명시해야 하기 때문이다. 반면 코틀린 애노테이션은 일반적인 생성자 호출과 마찬가지 방법으로 애노테이션을 적용할 수 있다. `@JsonName(name = "first_name")`은 `@JsonName("first_name")`과 같은 의미를 가진다.

이제 애노테이션 사용을 어떻게 컨트롤하는지 알아보자.

### 메타 애노테이션: 애노테이션을 처리하는 방법 제어 <a href="#eb-a-ed-ec-a-eb-b-ed-c-ec-d-b-ec-a-ec-a-eb-b-ed-c-ec-d-b-ec-ec-d-ec-b-eb-a-ac-ed-eb-a-eb-b-a-eb-b-ec" id="eb-a-ed-ec-a-eb-b-ed-c-ec-d-b-ec-a-ec-a-eb-b-ed-c-ec-d-b-ec-ec-d-ec-b-eb-a-ac-ed-eb-a-eb-b-a-eb-b-ec"></a>

자바와 마찬가지로, 코틀린 애노테이션 클래스는 스스로를 애노테이트할 수 있다. 애노테이션 클래스에 적용할 수 있는 애노테이션을 메타-애노테이션이라고 부른다.

표준 라이브러리에서 가장 흔하게 사용되는 메타-애노테이션은 `@Target`이다.

```crystal
@Target(AnnotationTarget.PROPERTY)
annotation class JsonExclude
```

@Target 메타-에노테이션은 애노테이션이 적용되는 요소의 타입을 명시한다. 만약 명시하지 않는다면 모든 선언에 애노테이션이 적용된다.

> 📌 @Retention 애노테이션\
> 자바에서 `@Retention`은 정의 중인 애노테이션 클래스를 .class 파일에 저장할지, 실행 시점에 reflection을 사용해 접근할 수 있게 할 지를 지정하는 메타-애노테이션이다. 코틀린에서는 애노테이션을 실행 시점에 보유하도록 하기 때문에 해당 Retention을 명시할 필요가 없다.

### 애노테이션 파라미터로 클래스 활용 <a href="#ec-a-eb-b-ed-c-ec-d-b-ec-ed-c-c-eb-d-bc-eb-af-b-ed-b-eb-a-c-ed-b-eb-e-ec-a-a-ed-c-ec-a-a" id="ec-a-eb-b-ed-c-ec-d-b-ec-ed-c-c-eb-d-bc-eb-af-b-ed-b-eb-a-c-ed-b-eb-e-ec-a-a-ed-c-ec-a-a"></a>

클래스 레퍼런스를 파라미터로 가지는 애노테이션 클래스를 만들 수도 있다. JKid 라이브러리에서 이는 `@DeserializeInterface` 애노테이션을 사용하여 만들 수 있다. 이는 인터페이스 타입을 가지는 프로퍼티 타입에 대해 역직렬화를 하도록 만든다. 간단한 예시로는 다음과 같은 것이 있다:

```angelscript
interface Company {
	val name: String
}
data class CompanyImpl(override val name: String) : Company
data class Person(
	val name: String,
	@DeserializeInterface(CompanyImpl::class) val company: Company
)
```

JKid가 Person 인스턴스에서 company 객체를 읽으면 CompanyImpl 인스턴스를 생성하고 역직렬화를 한 후, company 프로퍼티에 저장한다. 이를 명시하기 위해서 `@DeserializeInterface` 애노테이션에 `CompanyImpl::class`를 인자로 사용할 수 있다.

애노테이션이 선언되는 방식은 다음과 같다.

```crystal
annotation class DeserializeInterface(val targetClass: KClass<out Any>)
```

이때 KClass 타입은 자바에서 `java.lang.Class`와 상응한다. 여기서 `out` 키워드는 `Any`를 확장한 클래스를 모두 인자로 허용할 수 있게 만들어준다.

### 애노테이션 파라미터로 제네릭 클래스 활용 <a href="#ec-a-eb-b-ed-c-ec-d-b-ec-ed-c-c-eb-d-bc-eb-af-b-ed-b-eb-a-c-ec-a-c-eb-a-eb-a-a-d-ed-b-eb-e-ec-a-a-ed" id="ec-a-eb-b-ed-c-ec-d-b-ec-ed-c-c-eb-d-bc-eb-af-b-ed-b-eb-a-c-ec-a-c-eb-a-eb-a-a-d-ed-b-eb-e-ec-a-a-ed"></a>

기본적으로 JKid는 nonprimitive 타입의 프로퍼티를 중첩 객체로 직렬화한다. 그렇지만 직접 개인적인 직렬화 로직을 생성하는 것으로 바꿀 수도 있다.

`@CustomSerializer` 애노테이션은 커스텀 직렬화 클래스를 인자로 가진다. 직렬화 클래스는 `ValueSerializer` 인터페이스를 구현해야 한다.

```kotlin
interface ValueSerializer<T> {
	fun toJsonValue(value: T): Any?
	fun fromJsonValue(jsonValue: Any?): T
}
```

`@CustomSerializer` 애노테이션을 선언하는 방식은 다음과 같다. 이때 애노테이션에 사용될 프로퍼티 타입을 알 수 없기 때문에 스타 프로젝션(`*`)을 사용할 수 있다.

```angelscript
data class Person(
	val name: String,
	@CustomSerializer(DateSerializer::class) val birthDate: Date //CustomSerializer 애노테이션
)

annotation class CustomSerializer( //직접 코드로 구현한 CustomSerializer 애노테이션
	val serializerClass: KClass<out ValueSerializer<*>>
)
```

<figure><img src="https://blog.kakaocdn.net/dn/cxpTVm/btrXomTifmA/NkWudfZLfZ8tEblqTM4xjK/img.png" alt=""><figcaption></figcaption></figure>

이러한 방식은 조금 까다롭지만, 좋은 소식은 클래스를 애노테이션 인자로 사용하고 싶은 때 같은 패턴을 사용할 수 있다는 점이다.

### 리플랙션: 실행 시점에 코틀린 객체 내부 관찰 <a href="#eb-a-ac-ed-c-eb-e-ec-a-ec-b-a-ed-ec-b-c-ec-a-ec-ec-bd-ed-b-eb-a-b-ea-b-d-ec-b-b-eb-b-eb-b-ea-b-ec-b" id="eb-a-ac-ed-c-eb-e-ec-a-ec-b-a-ed-ec-b-c-ec-a-ec-ec-bd-ed-b-eb-a-b-ea-b-d-ec-b-b-eb-b-eb-b-ea-b-ec-b"></a>

리플렉션이란 런타임에 동적으로 프로그램의 클래스를 조사하기 위해서 사용되는 방법이다. 가끔씩 아무 타입의 객체로든 적용 가능한 코드나, 런타임에만 매소드/프로퍼티의 이름을 파악 가능한 경우가 생긴다. 이때 리플렉션을 통해 프로그램이 실행중일 때 인스턴스 등을 통해 객체의 내부 구조 등을 파악하게 만들 수 있다.

코틀린에서 리플렉션을 사용하려면 두 가지 서로 다른 리플렉션 API를 다룰 줄 알아야 한다. 첫 번째는 자바가 `java.lang.reflect` 패키지를 통해 제공하는 표준 리플렉션이다. 코틀린 클래스는 일반 자바 바이트코드로 컴파일되므로 자바 리플렉션 API도 코틀린 클래스를 컴파일한 바이트코드를 완벽히 지원한다.

두 번째 API는 코틀린이 `kotlin.reflect` 패키지를 통해 제공하는 코틀린 리플렉션 API다. 이 API는 자바에는 없는 프로퍼티나 nullable 타입과 같은 코틀린 고유 개념에 대한 리플렉션을 제공한다.

이 섹션에서는 JKid가 어떻게 리플렉션 API를 사용하는지 알아보도록 하자.

### 코틀린 리플렉션 API: KClass, KCallable, KFunction, KProperty <a href="#ec-bd-ed-b-eb-a-b-eb-a-ac-ed-c-eb-a-ec-api-a-kclass-c-kcallable-c-kfunction-c-kproperty" id="ec-bd-ed-b-eb-a-b-eb-a-ac-ed-c-eb-a-ec-api-a-kclass-c-kcallable-c-kfunction-c-kproperty"></a>

코틀린 리플렉션 API 코드의 메인 앤트리 포인트는 KClass이다. MyClass:class 코드를 작성하면 KClass의 인스턴스를 얻을 수 있다.

```ruby
class Person(val name: String, val age: Int)

>>> val person = Person("Alice", 29)
>>> val kClass = person.javaClass.kotlin
>>> println(kClass.simpleName)
Person
>>> kClass.memberProperties.forEach { println(it.name) }
age
name
```

위의 예제는 `.memberProperties`를 통해 클래스와 해당 클래스의 수퍼클래스에 정의된 비확장 프로퍼티를 모두 가져온다.

다음은 코틀린 리플렉션 API 인터페이스의 구조와 각 인터페이스의 역할을 나타낸 것이다.

<figure><img src="https://blog.kakaocdn.net/dn/IZK42/btrXoDgrIOs/sSSLNKU2BLlb4M4dJthRg0/img.png" alt=""><figcaption></figcaption></figure>

KClass

* java.lang.Class에 해당하는 것으로, 클래스를 표현하는 역할을 한다.
* 모든 선언 열거, 상위 클래스 얻기 등의 작업이 가능하다.

KCallable

* 함수, 프로퍼티의 공통 상위 인터페이스다.
* call 인터페이스를 제공해 가변 인자와 가변 반환을 할 수 있다.

KFunction

* 함수를 표현하는 역할을 한다.
* invoke 함수를 제공해서 컴파일 타임에 인자 개수와 타입에 대한 체크를 할 수 있다.
* KFunction1\<Int, Unit>의 형식으로 반환 값 타입 정보를 넣어 활용이 가능하다.

KProperty

* 프로퍼티를 표현한다. (단, 함수의 로컬 변수에는 접근할 수 없다)
* get 함수를 제공해서 프로퍼티 값을 얻을 수 있다.

### 리플렉션을 사용한 객체 직렬화 구현 <a href="#eb-a-ac-ed-c-eb-a-ec-ec-d-ec-ac-ec-a-a-ed-c-ea-b-d-ec-b-b-ec-a-eb-a-ac-ed-ea-b-ac-ed" id="eb-a-ac-ed-c-eb-a-ec-ec-d-ec-ac-ec-a-a-ed-c-ea-b-d-ec-b-b-ec-a-eb-a-ac-ed-ea-b-ac-ed"></a>

기본적으로 직렬화 함수는 객체의 모든 프로퍼티를 직렬화한다.

```kotlin
private fun StringBuilder.serializeObject(obj: Any) {
	val kClass = obj.javaClass.kotlin // 객체의 KClass를 얻는다.
	val properties = kClass.memberProperties // 클래스의 모든 프로퍼티를 얻는다.
	properties.joinToStringBuilder(
			this, prefix = "{", postfix = "}") { prop ->
		serializeString([prop.name](<http://prop.name/>)) // 프로퍼티 이름을 얻는다.
		append(": ")
		serializePropertyValue(prop.get(obj)) // 프로퍼티 값을 얻는다.
	}
}
```

위 함수의 구현은 명확하다. 클래스의 각 프로퍼티를 하나하나 직렬화하는데, 그 결과로는 { prop1: value1, prop2: value2 }와 같은 JSON이 생성된다.

### 애노테이션을 활용한 직렬화 커스텀 <a href="#ec-a-eb-b-ed-c-ec-d-b-ec-ec-d-ed-c-ec-a-a-ed-c-ec-a-eb-a-ac-ed-ec-bb-a-ec-a-a-ed" id="ec-a-eb-b-ed-c-ec-d-b-ec-ec-d-ed-c-ec-a-a-ed-c-ec-a-eb-a-ac-ed-ec-bb-a-ec-a-a-ed"></a>

챕터 초반에 우리는 @JsonExclude, @JsonName, 그리고 @CustomSerializer와 같은 애노테이션을 통해 JSON 직렬화를 맞춤화하는 법을 배웠다. 이제 이러한 애노테이션이 `serializeObject` 함수에 의해 어떻게 다루어지는 지 알도록 하자.

@JsonExclude나 @JsonName와 같은 애노테이션을 serializeObject 함수가 처리하는 방식은 다음과 같다. `findAnnotation` 함수를 사용하여 특정한 애노테이션이 현재 존재하는지 찾을 수 있다.

```kotlin
inline fun <reified T> KAnnotatedElement.findAnnotation(): T?
			= annotations.filterIsInstance<T>().firstOrNull()
```
