# 클래스, 객체, 인터페이스

해당 챕터에서는 코틀린에서 class를 다루는 법에 대해 더 깊은 이해를 가질 것이다. 코틀린의 class와 interface는 자바의 그것과 약간은 다르다. 이제부터 그 차이점을 알아보도록 하자.

### Interface <a href="#interface" id="interface"></a>

코틀린의 인터페이스는 추상 메소드의 구현을 필요로 한다는 점에서 자바 8의 것과 비슷한데, 단지 상태를 가지고 있지 않다는 점이 다르다.

```kotlin
//인터페이스
interface Clickable {
	fun click()
}

//인터페이스 구현
class Button : Clickable {
	override fun click() = println("I was clicked")
}
>>> Button().click()
I was clicked
```

또한 코틀린에서는 인터페이스를 구현하기 위해서 implements나 extends를 사용하는 대신 콜론(:)을 사용한다.

### Override <a href="#override" id="override"></a>

자바와는 다르게 코틀린에서는 override 키워드를 사용하는 것이 필수적이다. 이는 실수로 메소드를 오버라이드하는 상황을 방지해준다. 또한, 인터페이스의 메소드는 default 구현이 가능하다. 이때 자바는 default 키워드로 이를 명시해야 하지만, 코틀린은 그럴 필요가 없다.

```kotlin
//인터페이스
interface Clickable {
	fun click() //반드시 구현해야 함
	fun showOff() = println("I'm clickable!") //디폴트 메소드가 있으므로, 그대로 사용 가능
}
```

이때 다른 인터페이스(Focusable이라고 하겠다)에서도 showOff 메소드를 가지게 될 경우 컴파일 오류가 발생할 수 있다. 따라서 이 경우에는 자바와 마찬가지로, super 키워드를 사용해야 한다. 이때 super의 앞에 인터페이스 이름을 적는 자바와 달리, <>안에 인터페이스 이름을 적어야 한다.

```kotlin
//showOff 메소드의 구현
class Button : Clickable, Focusable {
	override fun click() = println("I was clicked")
	override fun showOff() {
		super<Clickable>.showOff()
		super<Focusable>.showOff()
	}
}
```

### Open, final, abstract <a href="#open-c-final-c-abstract" id="open-c-final-c-abstract"></a>

자바의 경우 모든 클래스의 서브 클래스를 생성하는 것이 가능하고, 모든 메소드의 오버라이딩이 가능하다(final로 선언되지 않은 이상). 그런데 이는 편리하기도 하지만 한편으로는 문제를 일으킬 수 있다. 자바에서는 가끔씩 취약한 기반 클래스(fragile base class) 문제가 발생한다. 이는 다음과 같은 문제다.

> 하위 클래스가 기반 클래스에 대해 가졌던 가정이 기반 클래스를 변경함으로써 깨져버린 경우에 생긴다. 어떤 클래스가 자신을 상속하는 방법에 대해 정확한 규칙을 제공하지 않는다면 그 클래스의 클라이언트는 기반 클래스를 작성한 사람의 의도와 다른 방식으로 메서드를 오버라이드할 위험이 있다.

이를 막기 위해서 자바를 사용할 때는 “상속을 위한 설계와 문서를 갖추거나, 그럴 수 없다면 상속을 금지”하는 것이 올바른 방식이다. 코틀린도 이와 같은 철학을 따른다. 코틀린의 매소트는 디폴트로 final이기 때문이다. 만약 어떤 클래스가 서브클래스의 생성을 허용한다면, 이를 open으로 명시해야 한다.

```kotlin
//서브 클래스가 상속 가능함을 명시함.
open class RichButton : Clickable {
	fun disable() {} //디폴트가 final이므로, 오버라이드 불가
	open fun animate() {} //서브 클래스에서 오버라이드함을 명시함.
	override fun click() {}
	final override fun click() {} //final이 없는 오버라이드는 기본으로 open임으로, final을 명시해야 함
}
```

> 📌 이전에 스마트 캐스팅을 위해선 클래스의 프로퍼티가 val이면서 커스텀 접근자를 구현하지 않아야 한다. 클래스도 마찬가지로 만약 클래스가 open이라면 스마트 캐스트는 불가하다.

만약 abstract로 선언한 클래스가 있다면, 자바와 마찬가지로, 이는 인스턴스화하는 것이 불가능하다. 추상 클래스의 추상 맴버는 반드시 오버라이드 되어야 하며, 따라서 항상 open이다. (이를 꼭 명시할 필요는 없다.)

### 가시성 변경자 <a href="#ea-b-ec-b-c-ec-b-eb-b-ea-b-bd-ec-e" id="ea-b-ec-b-c-ec-b-eb-b-ea-b-bd-ec-e"></a>

가시성 변경자는 코드 베이스의 컨트롤을 돕는다. 자바와 비슷하게, public, protected, 그리고 private 변경자가 있다. 그러나 디폴트 변경자가 다른데, 만약 명시하지 않으면 public이 되기 때문이다. 또한 자바가 package를 가시성 컨트롤을 위해 사용하는 것과 달리, 코틀린에서는 단순히 코드를 정돈하기 위해서 사용한다.

또한 코틀린은 모듈 내부에서만 사용할 수 있는 internal 접근자를 따로 제공한다. 이는 모듈 내부에서만 가시적이라는 뜻으로, 이때 모듈은 같이 컴파일되는 코틀린 파일 집합을 뜻한다. 이는 자바보다 캡슐화를 효과적으로 지원한다. 자바에서는 같은 패키지 안에 코드가 작성될 경우 캡슐화가 깨질 수 있기 때문이다.

<figure><img src="https://blog.kakaocdn.net/dn/bum3aF/btrWw6J7461/xSMt6TINDj7ekukwEJCSK1/img.png" alt=""><figcaption></figcaption></figure>

```kotlin
internal open class TalkativeButton : Focusable {
	private fun yell() = println("Hey!")
	protected fun whisper() = println("Let's talk!")
}

fun TalkativeButton.giveSpeech() { //에러: public 맴버가 internal을 노출시킴
	yell() //에러: yell은 TalkativeButton에서 private으로 선언됨
	whisper() //에러: whisper은 TalkativeButton에서 protected로 선언됨
}
	
```

> 📌 public, protected, private 변경자는 자바의 바이트 코드 안에서 그대로 유지되지만, internal 변경자는 자바에서 지원되지 않는 변경자이므로 바이트코드 상으론 public이 된다. 또한, private은 package-private(패키지 전용)으로 컴파일된다.

### 중첩 클래스 <a href="#ec-a-ec-b-a-ed-b-eb-e-ec-a-a" id="ec-a-ec-b-a-ed-b-eb-e-ec-a-a"></a>

코틀린의 중첩 클래스는 바깥 클래스의 인스턴스에 접근이 불가능하다.

```kotlin
interface State: Serializable //Serializable 인터페이스 구현
interface View { //
	fun getCurrentState(): State //State 리턴자를 가지는 getCurrentState
	fun restoreState(state: State) {} //State 매개변수를 가지는 restoreState
}

//코틀린의 중첩 클래스
class Button : View {
	override fun getCurrentState(): State = ButtonState()
	override fun restoreState(state: State) { /*...*/ }
	class ButtonState : State { /*...*/ }
}
```

코틀린의 중첩 클래스는 자바의 static 매소드처럼 명시적인 변경자가 없다. 그러나 내부 클래스는 inner라는 변경자를 사용한다. 정리하자면 다음과 같다.

<figure><img src="https://blog.kakaocdn.net/dn/cpkYBF/btrWx6CP8HJ/IlCb9ArultgJePvzKVK6F0/img.png" alt=""><figcaption></figcaption></figure>

<figure><img src="https://blog.kakaocdn.net/dn/GtouQ/btrWyL6mpZe/YCk6Kag90L7FHtuHJYGAKK/img.png" alt=""><figcaption></figcaption></figure>

위의 그림은 참조를 저장하지 않는 중첩 클래스(왼)와 참조를 저장하는 내부 클래스(오른)의 모습을 나타내고 있다.

한편, inner 클래스에서 외부 클래스를 접근하기 위해선 this@Outer를 사용하면 된다.

```kotlin
class Outer {
	inner class Inner {
		fun getOuterReference(): Outer = this@Outer
	}
}
```

### Sealed 클래스 <a href="#sealed-ed-b-eb-e-ec-a-a" id="sealed-ed-b-eb-e-ec-a-a"></a>

그렇다면 중첩 클래스와 내부 클래스는 코틀린에서 어떻게 사용될까? 이는 sealed 클래스를 생성하는 데 사용될 수 있다. sealed class를 사용하면, when에서 else 브랜치를 고려할 필요가 없다.

```angelscript
//계층 확장 제한을 가능하게 한다.
sealed class Expr {
	class Num(val value: Int) : Expr()
	class Sum(val left: Expr, val right: Expr) : Expr()
}
fun eval(e: Expr): Int =
	when (e) {
		is Expr.Num -> e.value
		is Expr.Sum -> eval(e.right) + eval(e.left)
	}
```

<figure><img src="https://blog.kakaocdn.net/dn/co6j6N/btrWv7Qx9jf/0qKe41k7kkH5mWKhNixy7k/img.png" alt=""><figcaption></figcaption></figure>

sealed 클래스를 사용하면 같은 파일 안에서만 하위 클래스 선언 가능이 가능하고, sealed 클래스의 내부 클래스로만 하위 클래스 선언이 가능하다.

### 주 생성자와 초기화 블록 <a href="#ec-a-bc-ec-d-ec-b-ec-e-ec-ec-b-ea-b-b-ed-eb-b-eb-a-d" id="ec-a-bc-ec-d-ec-b-ec-e-ec-ec-b-ea-b-b-ed-eb-b-eb-a-d"></a>

```angelscript
//주 생성자(클래스 선언)
open class User (val name: String) {}

//(위를 풀어 쓴 것)클래스 이름 뒤의 constructor로 주 생성자 지정
class User constructor(_name: String) {
    val name: String
    init { //초기화 블록
        name = _name
    }
}

//프로퍼티를 생성자 파라미터로 초기화
//별다른 애노테이션이나 가시성 수식어가 없다면 constructor 키워드 생략 가능
class User(_nickname: String) { 
	val nickname = _nickname 
}

//파라미터로 프로퍼티를 바로 초기화
class User(val nickname: String)

//디폴트 값
class User(val nickname: String,
	val isSubscribed: Boolean = true)
```

constructor 키워드는 주 생성자 또는 부 생성자를 선언하는 역할을, init 키워드는 초기화 블록을 선언하는 역할을 한다. 이러한 초기화 코드는 클래스가 생성될 때 작용한다.

이때 슈퍼클래스가 있는 자식 클래스의 경우 슈퍼 클래스의 주 생성자 또한 초기화되어야 한다. 이는 다음과 같이 가능하다.

```angelscript
//슈퍼클래스인 user 또한 같이 초기화
class TwitterUser(nickname: String) : User(nickname) { ... }

//아무 생성자도 선언하지 않는 경우, 디폴트 생성자 생성됨
open class Button

//자식 클래스에서 파라미터가 없는 생성자 호출을 해야 함
class RadioButton: Button()
```

### 부 생성자 <a href="#eb-b-ec-d-ec-b-ec-e" id="eb-b-ec-d-ec-b-ec-e"></a>

여러 가지 방법으로 인스턴스를 초기화할 방법이 필요한 경우 부 생성자를 사용한다.

```delphi
open class View { //()괄호 없기 때문에 주 생성자 없음
	constructor(ctx: Context) { ... } //부 생성자
	constructor(ctx: Context, attr: AttributeSet) { ... } //부 생성자
} 

class MyButton : View {
	constructor(ctx: Context) //슈퍼 클래스의 생성자 호출
		: super(ctx) {
		// ...
	}
		constructor(ctx: Context, attr: AttributeSet)
		: super(ctx, attr) {
		// ...
	}
}
```

<figure><img src="https://blog.kakaocdn.net/dn/coHHz1/btrWxr1zDhn/KDb85bPjCPk2RKpkQoVy8k/img.png" alt=""><figcaption></figcaption></figure>

자바와 마찬가지로, 자기 자신의 생성자 호출을 위해서는 this()를, 슈퍼 클래스의 생성자 호출을 위해서는 super()를 사용한다.

### 인터페이스의 추상 프로퍼티와 구현 <a href="#ec-d-b-ed-b-ed-e-ec-d-b-ec-a-a-ec-d-ec-b-ec-ed-eb-a-c-ed-d-bc-ed-b-b-ec-ea-b-ac-ed" id="ec-d-b-ed-b-ed-e-ec-d-b-ec-a-a-ec-d-ec-b-ec-ed-eb-a-c-ed-d-bc-ed-b-b-ec-ea-b-ac-ed"></a>

인터페이스에서 상태는 가질 순 없지만 추상 프로퍼티 정의는 가능하다. 이 뜻은 nickname이라는 value를 가질 수 있는 방법을 제공한다는 뜻이다.

```routeros
interface User {
	val nickname: String
}
```

인터페이스의 추상 프로퍼티는 지원 필드나 게터 등의 정보가 없으므로, 인터페이스를 구현한 하위 클래스에서 상태 저장을 위한 프로퍼티 등을 만들어야 한다.

```routeros
class Privateuser(override val nickname: String) : User

class SubscribingUser(val email: String) : User { 
	override val nickname: String get() = email.substringBefore('@')
}

class FacebookUser(val accountId: Int) : User { 
	override val nickname = getFBName(accountId)
}
```

### 인터페이스에서 게터와 세터 있는 프로퍼티 선언 <a href="#ec-d-b-ed-b-ed-e-ec-d-b-ec-a-a-ec-ec-c-ea-b-c-ed-b-ec-ec-b-ed-b-ec-e-eb-a-ed-eb-a-c-ed-d-bc-ed-b-b-e" id="ec-d-b-ed-b-ed-e-ec-d-b-ec-a-a-ec-ec-c-ea-b-c-ed-b-ec-ec-b-ed-b-ec-e-eb-a-ed-eb-a-c-ed-d-bc-ed-b-b-e"></a>

```julia
class User(val name: String) {
	var address: String = "unspecified"
	set(value: String) {
	println("""
		Address was changed for $name:
		"$field" -> "$value".""".trimIndent())
		field = value
	}
}
```

### 접근자 가시성 변경 <a href="#ec-a-ea-b-bc-ec-e-ea-b-ec-b-c-ec-b-eb-b-ea-b-bd" id="ec-a-ea-b-bc-ec-e-ea-b-ec-b-c-ec-b-eb-b-ea-b-bd"></a>

```kotlin
class LengthCounter {
	var counter: Int = 0
		private set //해당 프로퍼티를 클래스 바깥에서 변경할 수 없다
	fun addWord(word: String) {
		counter += word.length
	}
}
>>> val lengthCounter = LengthCounter()
>>> lengthCounter.addWord("Hi!")
>>> println(lengthCounter.counter)
3
```

접근자 가시성은 기본적으로 프로퍼티 가시성과 같다. 이때 get이나 set 앞에 가시성 수식어를 추가해 가시성 변경을 할 수 있다.

### equals(), hashCode(), toString() <a href="#equals-c-hashcode-c-tostring" id="equals-c-hashcode-c-tostring"></a>

```angelscript
data class Client(val name: String, val postalCode: Int)
```

위의 데이터 클래스는 다음의 메소드를 자동으로 생성한다.

* 인스턴스 간 비교를 위한 equals 예) println(client1 == client2) 코틀린에서 == 는 equals 메소드를 호출
* 각 필드를 선언 순서대로 표시하는 문자열 표현을 만들어주는 toString 기본적으로는 해당 객체의 레퍼런스를 만들어주기 때문에, 오버라이딩하여 쓸모 있는 함수로 바꾸어야 한다.

```kotlin
class Client(val name: String, val postalCode: Int) { 
	override fun toString() = "Client(name=$name, postalCode=$postalCode)" 
}
```

* 해시 기반 컨테이너에서 키로 사용할 수 있는 hashCode

```kotlin
class Client(val name: String, val postalCode: Int) { 
	... override fun hashCode(): Int = name.hashCode() * 31 + postalCode 
}
```

### copy() <a href="#copy" id="copy"></a>

코틀린의 데이터 클래스에서는 객체 복사를 편하게 해주는 copy﴾﴿ 메서드를 제공한다. 객체를 복사하면서 일부 프로퍼티를 바꿀 수 있게 해주는 것이다.

```lisp
val lee = Client("이재성", 41225)
println(lee.copy(postalCode = 4000))
```

### 클래스 위임 <a href="#ed-b-eb-e-ec-a-a-ec-c-ec-e" id="ed-b-eb-e-ec-a-a-ec-c-ec-e"></a>

> 참고: 위임이란?\
> 위임은 has a 관계로 클래스 내에서 위임 관계에 있는 클래스의 인스턴스를 가지고 있는 상태이다. 상속이 클래스 사이의 관계라면 위임은 인스턴스 사이의 관계라고 할 수 있다.

클래스의 위임은 by 키워드를 통해 가능하다. 상속을 하지 않고 클래스에 새로운 동작을 추가하기 위해선 주로 데코레이터 패턴을 활용하는데, 데코레이터 패턴을 위해선 동일한 인터페이스를 구현해야하고, 관련되지 않은 모든 동작도 하나씩 위임해줘야 한다. 코틀린은 컴파일러의 자동 생성으로 이러한 위임을 간편히 할 수 있도록 제공한다.

```angelscript
//Collection<T> 타입에 대한 메서드 호출시 innerList에 위임
class DelegatingCollection<T>(
	innerList: Collection<T> = ArrayList<T>()
) : Collection<T> by innerList {}
```

### #object 키워드: 클래스 선언과 동시에 인스턴스 생성(싱글톤 생성) <a href="#object-ed-a-ec-b-c-eb-c-a-ed-b-eb-e-ec-a-a-ec-a-ec-b-ea-b-bc-eb-f-ec-b-c-ec-ec-d-b-ec-a-a-ed-b-ec-a" id="object-ed-a-ec-b-c-eb-c-a-ed-b-eb-e-ec-a-a-ec-a-ec-b-ea-b-bc-eb-f-ec-b-c-ec-ec-d-b-ec-a-a-ed-b-ec-a"></a>

object 키워드로 객체 선언을 시작할 경우, 클래스 선언과 동시에 인스턴스 생성이 가능하여 싱글톤을 보장할 수 있다.

> 참고: 싱클톤이란?\
> 싱글톤은 '하나'의 인스턴스만 생성하여 사용하는 디자인 패턴이다. 인스턴스가 필요할 때, 똑같은 인스턴스를 만들지 않고 기존의 인스턴스를 활용하는 것을 뜻한다.

```kotlin
object Payroll {
	val allEmployees = arrayListOf<Person>()
		fun calculateSalary() {
		for (person in allEmployees) {
			...
		}
	}
}
```

### 동반 객체(companion object) <a href="#eb-f-eb-b-ea-b-d-ec-b-b-companion-object" id="eb-f-eb-b-ea-b-d-ec-b-b-companion-object"></a>

동반 객체란 companion 키워드를 가진 클래스를 의미한다. 코틀린은 클래스 내부에서 static 메서드를 제공해주지 않는데, 그 대신 private 맴버를 접근하기 위해서 동반 객체를 사용한다.

동반 객체의 멤버를 사용하는 구문은 정적 메서드 호출이나 정적 필드 사용과 유사하다.

```kotlin
class A private constructor(val name: String) { // 클래스 A의 주 생성자는 private로 접근 제한
    companion object {
        fun bar() : A {
            return A("zero")	//companion object의 bar() 메서드를 통해 private 생성자 접근 가능
        }
    }
}
var a = A("TEST")	//에러, 주 생성자는 private 접근제한자로 설정되어 호출 불가능
var b = A.bar() //동반객체의 bar() 메서드를 통해 private 주생성자에 접근이 가능
```

### Companion objects as regular objects <a href="#companion-objects-as-regular-objects" id="companion-objects-as-regular-objects"></a>

동반 객체도 인터페이스 구현이나 클래스 확장을 할 수 있다.

```kotlin
//인터페이스 구현
interface JSONFactory<T> {
	fun fromJSON(jsonText: String): T
}
class Person(val name: String) {
	companion object : JSONFactory<Person> {
		override fun fromJSON(jsonText: String): Person = ...
	}
}

//클래스 확장
class Person(val firstName: String, val lastName: String) { 
	companion object {} 
} 
fun Person.Companion.fromJSON(json: String): Person { ... } 
val p = Person.fromJSON(json)
```

### 객체 식: 익명 내부 클래스 <a href="#ea-b-d-ec-b-b-ec-b-d-a-ec-d-b-eb-aa-eb-b-eb-b-ed-b-eb-e-ec-a-a" id="ea-b-d-ec-b-b-ec-b-d-a-ec-d-b-eb-aa-eb-b-eb-b-ed-b-eb-e-ec-a-a"></a>

```kotlin
fun countClicks(window: Window) {
	var clickCount = 0
	window.addMouseListener(object : MouseAdapter() {
		override fun mouseClicked(e: MouseEvent) {
			clickCount++
		}
	})
	// ...
}
```

object 키워드를 사용해서 익명 객체를 정의할 수 있다.
