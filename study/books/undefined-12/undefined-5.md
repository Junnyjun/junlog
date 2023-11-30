# 함수형 타입

## **함수형 언어의 정적 타입 시스템**

정적 타입 시스템의 컴파일러는 컴파일 전 코드에 대해 많은 것을 알고 있고 이러한 정보를 이용해 리플렉션, 타입 추론과 같은 고도화 된 기능을 제공할 수 있음

함수형 언어에서는 객체뿐 아니라 표현식도 타입으로 취급함

### **곱 타입과 한계**

곱 타입은 하나의 자료구조에 여러 타입을 한번에 정의 하는 것(ex 튜플, 레코드) 두 개 이상의 타입을 AND로 결합한 형태이며 대부분의 언어에서 사용됨

**코틀린으로 구현한 레코드의 예**

```kotlin
class Circle(val name: String, val x: Float, val y: Float)
```

### **곱 타입의 한계**

AND로 자료구조간 계층 구조를 표현하려면 상속을 사용해야 하는데, \
신규 계층이 추가 될 때 복잡성이 증가하고 else에 대한 처리를 반드시 해야 함

```kotlin
var toggle: Boolean = false

fun main() {

    toggle = true
    toggle = false

    caseLanguageEnum(Language.KOTLIN)
}

interface LanguageInterface

class Java : LanguageInterface
class Kotlin : LanguageInterface
class Scala : LanguageInterface
class Haskell : LanguageInterface

private fun caseLanguageInterface(language: LanguageInterface) = when (language) {
    is Java -> {
        // doSomething
    }
    is Kotlin -> {
        // doSomething
    }
    is Scala -> {
        // doSomething
    }
    else -> {
        throw IllegalArgumentException("invalid type : $language")
    }
}

```

**합 타입을 사용한 OR 결합**

합 타입은 곱 타입과 달리 두 개 이상의 타입을 OR로 결합한다. \
코틀린은 sealed class를 사용해서 합 타입을 만듬

함수형 프로그래밍에서는 패턴 매칭이 쉽고, 타입의 사이드이펙트(else 처리)를 신경 쓰지 않아도 됨(이는 컴파일 타임에 실수를 미리 예방 할 수 있게 됨)

```kotlin
var toggle: Boolean = false

fun main() {

    toggle = true
    toggle = false

    caseLanguageEnum(Language.KOTLIN)
}

private fun caseLanguageEnum(language: Language) = when (language) {
    Language.JAVA -> {
        // doSomething
    }
    Language.KOTLIN -> {
        // doSomething
    }
    Language.SCALA -> {
        // doSomething
    }
    Language.HASKELL -> {
        // doSomething
    }
}

enum class Language {
    JAVA, KOTLIN, SCALA, HASKELL
}
```

## **타입의 구성요소**

### **타입 변수**

C++의 템플릿 T와 동치

```kotlin
fun <T> head(list: List<T>): T = list.first()
```

타입 변수를 갖는 함수를 다형 함수(polymorphic function)라 함

### **값 생성자**

타입의 값을 반환하는 것

### **타입 생성자, 타입 매개변수**

새로운 타입을 생성하기 위해 매개변수화 된 타입을 전달 받는 것

**인터페이스(interface) vs 트레이트(trait) vs 추상 클래스(abstract class) vs 믹스인(mixin)**

**인터페이스** 객체지향 프로그래밍에서 인터페이스는 클래스의 기능 명세

**트레이트** 인터페이스와 유사하지만, 구현부를 포함한 메서드를 정의 할 수 있음(코틀린의 interface는 trait임)

**추상 클래스** 상속 관계에서의 추상적인 객체를 모델링 하기 위해 사용, 다중 상속 불가

**믹스인** 클래스들 간에 어떤 프로퍼티나 메서드를 결합하는 것으로, 재사용성이 높고 유연하며 다중 상속에서 발생하는 모호성을 해결 할 수 있음

```kotlin
interface Developer {
    val language: String

    fun writeCode() {
        println("write $language")
    }
}

interface Backend : Developer {
    fun operateEnvironment(): String {
        return "operateEnvironment"
    }

    override val language: String
        get() = "Haskell"
}

interface Frontend : Developer {
    fun drawUI(): String {
        return "drawUI"
    }

    override val language: String
        get() = "Elm"
}

class FullStack : Frontend, Backend {
    override val language: String
        get() = super<Frontend>.language + super<Backend>.language
}

fun main() {
    val frontend = object : Frontend {}
    val backend = object : Backend {}

    frontend.writeCode()    // write Elm
    backend.writeCode()     // write Haskell

    val fullStack = FullStack()

    fullStack.writeCode()  // write ElmHaskell
    println(fullStack.drawUI())     // drawUI
    println(fullStack.operateEnvironment()) // operateEnvironment
}
```

### **타입 클래스, 타입 클래스의 인스턴스**

하스켈에서 타입의 행위를 선언하는 방법을 타입 클래스라 함 객체지향의 클래스와 유사한 이름이나 다른 것이며 코틀린의 인터페이스와 유사함

* 행위에 대한 선언
* 필요시, 행위의 구현부도 포함 가능

타입 클래스와 코틀린의 인터페이스는 다르지만 아래에서 코틀린의 인터페이스를 타입 클래스와 유사한 방법으로 사용할 것임

```kotlin
interface Eq<in T> {
    fun equal(other: T): Boolean = this == other
    fun notEqual(other: T): Boolean = this != other
}

sealed class TrafficLight: Eq<TrafficLight>, Print
object Red: TrafficLight() {
    override fun print() = print("Red")
}
object Yellow: TrafficLight() {
    override fun print() = print("Yellow")
}
object Green: TrafficLight() {
    override fun print() = print("Green")
}

fun main() {
    println(Red.equal(Yellow))     // false
    println(Red.notEqual(Yellow))  // true

    Red.print()     // Red
    Yellow.print()  // Yellow
    Green.print()   // Green
}
```

### **재귀적 자료구조**

sealed class를 사용해 대수적 데이터 타입을 만들어 활용하면, 재귀적 자료구조를 만들 수 있다 앞서 살펴본 FunList가 재귀적 자료구조의 대표적인 예임

대수적 데이터타입에서 구성하는 값 생성자의 필드에 자기 자신을 포함하는 구조를 재귀적인 자료구조라 함

```kotlin
fun main() {
    val reversed = reverse(Cons(1, Cons(2, Cons(3, Nil))), Nil)
    printFunList(reversed)
}

fun <T> reverse(list: FunList<T>, acc: FunList<T>): FunList<T> = when (list) {
    Nil -> acc
    is Cons -> reverse(list.tail, acc.addHead(list.head))
}
```
