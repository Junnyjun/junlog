# 인라인 클래스의 사용을 고려하라

### 중요한 점

inline 키워드는 Deprecated 됨. 대신 value 키워드와 @JvmInline 어노테이션 사용

하나의 값을 보유하는 객체 또한 inline으로 만들 수 있다

기본 생성자 프로퍼티가 하나인 클래스 앞에 inline을 붙이면, 해당 객체를 사용하는 위치가 모두 해당 프로퍼티로 교체

inline 클래스의 메소드는 모두 정적 메소드로 만들어진다.

```kotlin
@JvmInline
value class Name(private val value: String) {
    fun greet() {
        print("Hello, $value")
    }
}
```

인라인 클래스는 다른 자료형을 wrapping 해서 새로운 자료형을 만들 때 많이 사용

측정 단위를 표현하거나 타입 오용으로 발생하는 문제를 막을 때 사용

**측정 단위를 표현할 때**

```kotlin
interface Timer {
    fun callAfter(time: Int, callback: () -> Unit)
}
```

callAfter 함수의 time 파라미터는 어떤 단위를 받는지 알 수 없음

밀리초를 받는지, 초를 받는지, 분을 받는지 알 수 없어 여러 문제가 발생할 수 있음

파라미터 이름에 측정 단위를 붙여줌으로서 해결할 수 있지만, 함수를 사용할 때 이름이 노출되지 않을 수 있으므로 문제가 발생할 가능성이 있음

또한 리턴 값에는 이름을 붙일 수 없음

타입에 제한을 걸면 이러한 오류를 해결할 수 있고 inline 클래스를 활용하면 더 효율적으로 만들 수 있다

```kotlin
@JvmInline
value class Minutes(val minutes: Int) {
    fun toMillis(): Millis = Millis(minutes * 60 * 1000)
}

@JvmInline
value class Millis(val milliseconds: Int) {
}

interface User {
    fun decideAboutTime(): Minutes
    fun wakeUp()
}

interface Timer {
    fun callAfter(time: Millis, callback: () -> Unit)
}
```

프론트엔드 개발에서는 px, mm, dp 등 다양한 단위를 사용하는데 이러한 단위를 제한할 때 활용하면 좋다

또한 객체 생성을 위해서 DSL-like 확장 프로퍼티를 만들어 두어도 좋다

**타입 오용으로 발생하는 문제를 막을 때**

어떠한 Entity가 여러 ID를 가지고 있을 때 모두 같은 Int 자료형이라면, 실수로 잘못된 값을 넣을 수 있다.

이럴 경우 Int 자료형의 값을 inline 클래스로 만들어 활용하여 해결할 수 있다.

```kotlin
@JvmInline
value class StudentId(val studentId: Int)

@JvmInline
value class TeacherId(val teacherId: Int)

@JvmInline
value class SchoolId(val schoolId: Int)

class Grades(
        val studentId: StudentId,
        val teacherId: TeacherId,
        val schoolId: SchoolId
) {

}
```

**인라인 클래스와 인터페이스**

인라인 클래스도 인터페이스를 구현할 수 있다

```kotlin
package chapter7.item47

interface TimeUnit {
    val millis: Long
}

@JvmInline
value class MinutesWithInterface(private val minutes: Long): TimeUnit {
    override val millis: Long
        get() = minutes * 60 * 1000
}

@JvmInline
value class MillisWithInterface(private val milliseconds: Long): TimeUnit {
    override val millis: Long
        get() = milliseconds
}

fun setUpTimerWithInterface(time: TimeUnit) {
    val millis = time.millis
}

fun main() {
    setUpTimerWithInterface(MinutesWithInterface(1000))
    setUpTimerWithInterface(MillisWithInterface(1000))
}
```

하지만 이 코드는 클래스가 inline으로 동작하지 않는다.&#x20;

따라서 해당 코드는 클래스를 inline으로 만들었을 때 얻을 수 있는 장점이 없다

인터페이스를 통해서 타입을 나타내려면, 객체를 래핑해서 사용해야하기 때문이다.

**typealias**

typealias 를 사용하면, 타입에 새로운 이름을 붙여 줄 수 있음

```kotlin
typealias nanos = Int
typealias hours = Int

val nano: nanos = 10
val hour: hours = 10
```

하지만 두 typealias 모두 단순하게 Int를 나타내기 때문에 실수로 둘을 혼용해서 잘못입력할 수 있음

따라서 단위 등을 표현하려면, 파라미터 이름 또는 클래스, 인라인 클래스를 이용하는게 바람직하다
