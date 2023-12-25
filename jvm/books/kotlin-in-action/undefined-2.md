# 함수

해당 챕터에서는 선언 및 호출 기능을 코틀린이 어떻게 개선했는지, 자바 라이브러리를 코틀린 스타일로 어떻게 변환하는지 알아보자. 이를 위해서 코틀린 컬렉션, 문자열 및 정규 표현에 초점을 맞추어 보도록 하자.

## Collection <a href="#collection" id="collection"></a>

먼저, 컬렉션을 생성하는 법은 다음과 같다.

```kotlin
val set = hashSetOf(1, 7, 53) //set 생성
val list = arrayListOf(1, 7, 53) //list 생성
val map = hashMapOf(1 to "one", 7 to "seven", 53 to "fifty-three") //map 생성
```

코틀린의 컬렉션과 관련하여 매우 다행인 점은 자바의 컬랙션 클래스를 그대로 사용한다는 점이다. 따라서 자바 코드와 상호 작용하기 편리하다.

```kotlin
>>> println(set.javaClass)
class java.util.HashSet //kolin의 set은 java의 hashset class과 일치한다.
```

자바 컬렉션과 같은 클래스를 사용하지만, 코틀린은 더 많은 기능을 사용할 수 있다. 예를 들어, 함수를 통해 list에서 가장 큰 값(max)을 찾아내거나 가장 마지막 원소(last)를 가져올 수 있다. 이러한 함수를 사용하기 전에, 우선 코틀린의 함수 선언이라는 새로운 개념을 배워보자.

### 호출하기 쉬운 함수 <a href="#ed-b-ec-b-c-ed-ea-b-b-ec-ac-ec-a-b-ed-a-ec" id="ed-b-ec-b-c-ed-ea-b-b-ec-ac-ec-a-b-ed-a-ec"></a>

리스트에 있는 item을 출력하고 싶은데, 디폴트 형식 \[1, 2, 3]이 아니라 (1; 2; 3) 형식으로 출력하고 싶다면 어떻게 할까? 장황한 프로그래밍을 사용해 볼 수도 있다. 예를 들어, 다음과 같은 방식으로 joinToString 함수를 프로그래밍해보자.

* 먼저 “(” 를 출력하고, index 범위를 통해 아이템 사이에만 “;”를 출력하고, 마지막 아이템 뒤에는 “)”을 출력하는 방식

위의 함수는 joinToString(list, "; ", "(", ")")과 같이 파라미터를 넘겨주어야 한다. 그런데 이때 다음과 같은 문제가 발생한다.

***

**1. 가독성 문제**

joinToString(collection, " ", " ", ".")와 같이 호출될 경우, String과 파라미터를 연결 짓기 어려워진다. 그런데 코틀린에서는 다음과 같은 표현이 가능하다.

```kotlin
//각 요소의 이름을 명시
joinToString(collection, separator = " ", prefix = " ", postfix = ".")
```

따라서 자바에 비해 가독성이 높다. (슬프게도, JDK나 Andriod framework가 포함된 메소드에서는 해당 기능을 사용할 수 없다고 한다🙁)

**2. 메소드의 과잉 오버로드**

자바에서는 종종 하위 호환성을 위해 오버로드가 발생한다. 예를 들어, java.lang.Thread에는 8개의 생성자가 존재한다. (이전 버전과의 호환을 위해 계속 생성자가 추가되었기 때문이다.)

<figure><img src="https://blog.kakaocdn.net/dn/b7ZXvb/btrWx6CPycN/AhwcDlIGkXnAVHYTVREMg1/img.png" alt=""><figcaption><p>8개의 생성자 종류</p></figcaption></figure>

그런데 코틀린에서는 위와 같은 문제가 생기지 않는데, 파라미터를 디폴트 값으로 명시할 수 있기 때문이다.

```kotlin
//함수 선언시 디폴트 값 선언
fun <T> joinToString(
	collection: Collection<T>,
	separator: String = ", ",
	prefix: String = "",
	postfix: String = ""
): String

//어떤 값을 넣든 간에 함수 사용 가능
>>> joinToString(list, ", ", "", "")
1, 2, 3
>>> joinToString(list)
1, 2, 3
>>> joinToString(list, "; ")
1; 2; 3
>>> joinToString(list, suffix = ";", prefix = "# ")
# 1, 2, 3;
```

> 📌 자바에서는 기본 매개변수 개념이 없기 때문에, Java에서 기본 파라미터 값으로 Kotlin 함수를 호출할 때 모든 파라미터 값을 명시적으로 지정해야 한다. 이를 쉽게 하기 위해 @Jvm Overloads로 주석을 달면 컴파일러가 파라미터의 개수가 각기 다른 자바 오버로드 메소드들을 생성한다.

**3. 무의미한 static 메소드/프로퍼티**

자바에서는 static 메소드를 포함한 무의미한 클래스(예를 들어, Collections 클래스)가 많이 만들어지고는 한다. 코틀린에서는 함수를 top level, 즉 클래스 바깥에 두기 때문에 함수를 패키지에 직접 넣을 수 있다.

```kotlin
package strings
fun joinToString(...): String { ... }
```

해당 함수를 Java에서 컴파일하면 class가 함수를 감싸게 되고, 함수는 static 메소드로 바뀐다.

> 📌 top level 함수가 사용된 Kolin 코드를 Java로 바꾸려면 다음과 같이 선언하면 된다.

```kotlin
@file:JvmName("StringFunctions") //문서의 맨 위에 선언
package strings
fun joinToString(...): String { ... }
```

프로퍼티 또한 마찬가지다. 자바에서는 클래스 내에서만 변수가 사용 가능하지만, Kolin에서는 top level에 프로퍼티를 두는 것이 가능하다.

```kotlin
const var opCount = 0 //top level property
fun performOperation() {
	opCount++
	// ...
}
```

이때 const 키워드를 사용하여 자바에서의 public static final임을 명시할 수 있다.

***

### 함수와 프로퍼티의 확장 <a href="#ed-a-ec-ec-ed-eb-a-c-ed-d-bc-ed-b-b-ec-d-ed-ec-e-a" id="ed-a-ec-ec-ed-eb-a-c-ed-d-bc-ed-b-b-ec-d-ed-ec-e-a"></a>

코틀린을 자바 프로젝트로 합칠 때, 코틀린으로 변환이 필요 없는 코드는 재작성하지 않는 것이 효율적이다. 이를 위해서 extension function이 사용되는데, 이는 class의 맴버이면서 class 바깥에 선언된 함수를 뜻한다. 또한, 이클립스에서 불러와서 사용할 수 있다.

```kotlin
fun String.lastChar(): Char = this.get(this.length - 1)
	//리시버 타입   //리시버 객체

>>> println("Kotlin".lastChar())
n
```

extension 함수를 사용하면 확장하고 싶은 클래스의 메소드나 프로퍼티에 직접적으로 접근할 수 있다. 그러나 extension 함수가 캡슐화를 깨뜨리는 것은 아니다.

### Import extension function <a href="#import-extension-function" id="import-extension-function"></a>

extension 함수는 다른 class나 함수와 마찬가지로 import가 되어야 한다(네임 충돌을 막기 위해).

```kotlin
import strings.lastChar //extension 함수 사용을 위해 import하기
val c = "Kotlin".lastChar()

import strings.lastChar as last //키워드 사용하여 import하기
val c = "Kotlin".last()
```

### 자바에서 extension functions 호출 <a href="#ec-e-eb-b-ec-ec-c-extension-functions-ed-b-ec-b-c" id="ec-e-eb-b-ec-ec-c-extension-functions-ed-b-ec-b-c"></a>

extension 함수는 리시버 객체를 첫 인자로 받는 함수이므로, 호출이 런타임 오버해드를 만들지는 못한다. 따라서 위의 함수는 lastChar("Java")와 같이 호출 가능하다(심지어 코틀린 코드보다도 간단하다!).

### Back to (1; 2; 3) <a href="#back-to-b-b" id="back-to-b-b"></a>

결과적으로, 맨 위에서 언급한 기능은 extension 함수를 사용하여 다음과 같이 호출 가능하다!

```kotlin
>>> val list = arrayListOf(1, 2, 3)
>>> println(list.joinToString(" "))
1 2 3
```

### 오버라이딩 불가 <a href="#ec-a-eb-b-eb-d-bc-ec-d-b-eb-a-eb-b-ea-b" id="ec-a-eb-b-eb-d-bc-ec-d-b-eb-a-eb-b-ea-b"></a>

일반적인 맴버 함수가 아니라 extension 함수의 경우에는 class의 맴버가 아니기 때문에 오버라이드가 불가능하다. 즉, 확장 함수는 확장하는 클래스를 실제로 수정하여 함수를 추가하는 것이 아니기 때문에 오버라이드를 할 수 없다.

```kotlin
fun View.showOff() = println("I'm a view!")
fun Button.showOff() = println("I'm a button!")
>>> val view: View = Button()
>>> view.showOff()
I'm a view!
```

위의 코드를 보면 실제 타입은 Button일지라도, View에 응답하는 showOff가 호출되어 출력값이 ‘I'm a view!’으로 나오는 것을 확인할 수 있다. 즉, 코틀린은 extension 함수를 static으로 처리한다.

> 📌 맴버 함수와 이름이 같은 extestion 함수가 있으면 항상 맴버 함수가 우선한다.

### Extension properties <a href="#extension-properties" id="extension-properties"></a>

extestion 함수와 마찬가지로 extestion 프로퍼티는 리시버 타입이 추가된 일반적인 프로퍼티라는 것을 알 수 있다.

```kotlin
val String.lastChar: Char
	get() = get(length - 1)

//맴버 프로퍼티와 함께 접근한다.
>>> println("Kotlin".lastChar)
n
>>> val sb = StringBuilder("Kotlin?")
>>> sb.lastChar = '!'
>>> println(sb)
Kotlin!
```

### collection <a href="#collection" id="collection"></a>

코틀린 collection을 위해 사용되는 함수는 다음과 같은 특징을 보여준다.

* 임의 개수의 인수를 사용하여 함수를 선언할 수 있는 vararg(가변인자) 키워드
* 형식 없이 단일 인수 함수를 호출할 수 있는 infix(중위) 표기법
* 단일 합성 값을 여러 변수로 압축 해제할 수 있는 구조 분해 선언

### Extending the Java Collections API <a href="#extending-the-java-collections-api" id="extending-the-java-collections-api"></a>

코틀린에서는 다음과 같이 자바의 라이브러리에 존재하지 않는 함수를 사용할 수 있다.

```kotlin
>>> val strings: List<String> = listOf("first", "second", "fourteenth")
>>> strings.last()
fourteenth
>>> val numbers: Collection<Int> = setOf(1, 14, 2)
>>> numbers.max()
14
```

그 이유는 무엇일까? 바로 last와 max가 extenstion 함수이기 때문이다!

### Varargs <a href="#varargs" id="varargs"></a>

자바에서는 배열로 argument를 패스하기 때문에 배열은 있는 그대로 전달되는 것과 다르게, 코틀린에서는 배열을 unpack한다. 이 기능은 spread operator를 통해 이용할 수 있다. 간단하게 말하자면 \* 캐릭터를 사용하면 된다.

```kotlin
fun main(args: Array<String>) {
	val list = listOf("args: ", *args)
	println(list)
} // * 문자는 배열의 값들을 하나의 call로 만든다.
```

### Infix <a href="#infix" id="infix"></a>

```kotlin
val map = mapOf(1 to "one", 7 to "seven", 53 to "fifty-three")

//중위 함수로 선언
infix fun Any.to(other: Any) = Pair(this, other)
```

위의 코드에서 to는 construct가 아니라 infix call, 즉 중위 호출이라는 것을 알 수 있다. 중위 호출은 extenstion 함수나 일반적인 메소드 모두에 활용 가능한데, 이를 위해서는 infix 키워드를 함수 앞에 붙이면 된다. to 함수는 Pair를 리턴한다. 예를 들어, 1 to “one”이라고 사용하면 결과로 (1, “one”)이 반환된다. 이러한 특성은 구조 분해 선언이라고 부른다(객체가 가지고 있는 여러 값을 분해해서 여러 변수에 한꺼번에 초기화하는 것). 구조 분해 선언은 pair 뿐만 아니라 map, loop 등등 여러 요소에 사용 가능하다.

### Splitting strings <a href="#splitting-strings" id="splitting-strings"></a>

자바에서는 ( . )을 아무 문자나 뜻하는 정규식으로 가지고 있기 때문에, 12.345-6.A".split(".")을 해해도 결과값으로 \[12, 345-6, A]이 반환되지 않는다. 따라서 빈 문자열이 반환된다.

코틀린에서는 이러한 혼란스러운 메소드를 숨기고 split을 사용하기 위해 정규식을 String이 아닌 Regex 타입으로 선언하였다. 따라서 코틀린에서는 와일드 카드를 입력 가능하다.

```kotlin
>>> println("12.345-6.A".split(".", "-"))
[12, 345, 6, A]
```

### 코드 단순화 <a href="#ec-bd-eb-c-eb-b-a-ec-c-ed" id="ec-bd-eb-c-eb-b-a-ec-c-ed"></a>

코드를 반복하지 말라는 유명한 규칙이 있다(Don’t Repeat Yourself, DRY). 코틀린은 이에 대한 솔루션을 제공하는데, 바로 local function을 제공하여 아무 필드나 validate하는 것이다.

```kotlin
class User(val id: Int, val name: String, val address: String)
	fun User.validateBeforeSave() {
		fun validate(value: String, fieldName: String) {
			if (value.isEmpty()) {
				throw IllegalArgumentException(
					"Can't save user $id: empty $fieldName")
				}
			}
		validate(name, "Name")
		validate(address, "Address")
}
fun saveUser(user: User) {
	user.validateBeforeSave()
	// Save user to the database
}
```

이런 식으로, 확장 기능으로 코드를 추출하는 것은 놀랄 만큼 유용하다.
