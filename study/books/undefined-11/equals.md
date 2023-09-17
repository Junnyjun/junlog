# equals의 규약을 지켜라

#### 코틀린의 Any에는 잘 설정된 규약을 가진 아래 메서드들이 있다.

```
equals
hashCode
toString 
```

이런 메서드들의 규약은 주석, 문서에 잘 설명돼 있다. 아이템 32에서 설명한 것처럼 Any 클래스를 상속받는 모든 메서드는 이런 규약을 잘 지키는 게 좋다.

### 동등성(equality)&#x20;

#### 코틀린에는 2가지 동등성이 있다.

구조적 동등성 : equals 메서드와 이를 기반으로 만들어진 !=, == 연산자로 확인하는 동등성이다. a가 nullable이 아니면 a == b는 a.equals(b)로 변환되고, a가 nullable이면 a?.equals(b) ?: (b === null)로 변환된다

레퍼런스적 동등성 : !==, === 연산자로 확인하는 동등성이다. 두 피연산자가 같은 객체를 가리키면 true를 리턴한다

equals는 모든 클래스의 슈퍼클래스인 Any에 구현돼 있으므로 모든 객체에서 쓸 수 있다. 다만 연산자를 써서 다른 타입의 두 객체를 비교하는 건 허용되지 않는다.

```kotlin
fun main() {
    // 오류 : 두 클래스 사이에는 ==, === 연산자를 쓸 수 없다
    Animal() == Book()
    Animal() === Book()
}

open class Animal
class Book
```

같은 타입을 비교하거나 둘이 상속 관계를 가질 땐 비교할 수 있다.

```kotlin
fun main() {
    // Cat은 Animal의 서브클래스기 때문에 == === 모두 쓸 수 있다
    Animal() == Cat()
    Animal() === Cat()
}

open class Animal
class Cat: Animal()
```

#### equals가 필요한 이유&#x20;

equals는 디폴트로 ===처럼 두 인스턴스가 완전히 같은 객체인지 비교한다. 이는 모든 객체는 디폴트로 유일한 객체라는 걸 의미한다.

```kotlin
fun main() {
    val name1 = Name("김")
    val name2 = Name("김")
    val name1Ref = name1

    println(name1 == name1)     // true
    println(name1 == name2)     // false
    println(name1 === name1Ref) // true
}

class Name(val name: String)
```

이런 동작은 DB 연결, 리포지토리, 쓰레드 등의 활동 요소(active element)를 활용할 때 굉장히 유용하다.\
하지만 동등성을 약간 다른 형태로 표현해야 하는 객체가 있다.&#x20;

예를 들어 두 객체가 기본 생성자의 프로퍼티가 같다면 같은 객체로 보는 형태가 있을 수 있다. \
data 한정자를 붙여서 데이터 클래스로 정의하면 자동으로 이런 동등성으로 동작한다.

```kotlin
fun main() {
    val name1 = FullName("김", "철수")
    val name2 = FullName("김", "철수")
    val name3 = FullName("이", "영희")

    println(name1 == name1)     // true
    println(name1 == name2)     // true (데이터가 같기 때문)
    println(name1 == name3)     // false

    println(name1 === name1)    // true
    println(name1 === name2)    // false
    println(name1 === name3)    // false
}

data class FullName(val name: String, val surname: String)
```

데이터 클래스는 내부에 어떤 값을 가졌는지가 중요하므로 이렇게 동작하는 게 좋다.&#x20;

그래서 일반적으로 데이터 모델을 표현할 때는 data 한정자를 붙인다. \
데이터 클래스의 동등성은 모든 프로퍼티가 아닌 일부 프로퍼티만 비교해야 할 때도 유용하다.&#x20;

날짜, 시간을 표현하는 객체를 살펴본다. 이 객체는 동등성 확인 때 검사되지 않는 asStringCache와 changed라는 프로퍼티를 갖는다.&#x20;

일반적으로 캐시를 위한 객체는 캐시에 영향을 주지 않는 프로퍼티가 복사되지 않는 게 좋다. 그래서 equals 내부에서 asStringCache와 changed를 비교하지 않는다. \
data 한정자를 써도 같은 결과가 나온다.&#x20;

```kotlin
class DateTime(
    private var millis: Long = 0L,
    private var timeZone: TimeZone? = null
) {
    private var asStringCache = ""
    private var changed = false

    override fun equals(other: Any?): Boolean =
        other is DateTime &&
                other.millis == millis &&
                other.timeZone == timeZone
}
```

이렇게 코드를 작성하면 기본 생성자에 선언되지 않은 프로퍼티는 copy로 복사되지 않는다.&#x20;

기본 생성자에 선언되지 않은 프로퍼티까지 복사하는 건 의미없는 일이라서 기본 생성자에 선언되지 않은 걸 복사하지 않는 동작은 올바른 동작이라고 할 수 있다.

data 한정자를 기반으로 동등성의 동작을 조작할 수 있으므로 일반적으로 코틀린에선 equals를 직접 구현할 필요가 없다. 다만 상황에 따라 equals를 직접 구현해야 하는 경우가 있을 수 있다. 또한 일부 프로퍼티만 같은지 확인해야 하는 경우 등이 있을 수 있다. 아래 User 클래스는 id만 같으면 같은 객체라고 판단한다.

```kotlin
class User(
    val id: Int,
    val name: String,
    val surname: String
) {
    override fun equals(other: Any?): Boolean =
        other is User && other.id == id

    override fun hashCode(): Int = id
}
```

#### equals를 직접 구현해야 하는 경우는 아래와 같다

기본 제공되는 동작과 다른 동작을 해야 할 때

일부 프로퍼티만으로 비교해야 할 때

data 한정자를 붙이는 걸 원하지 않거나 비교해야 하는 프로퍼티가 기본 생성자에 없을 때

### equals의 규약&#x20;

코틀린 1.4.31 기준 equals에는 아래 주석이 달려 있다.

어떤 다른 객체가 이 객체와 같은지(equal to) 확인할 때 사용한다. \
구현은 반드시 아래 요구사항을 충족해야 한다

```
- 반사적(reflexive) 동작 : x가 null이 아닌 값이면 x.equals(x)는 true를 리턴해야 한다
- 대칭적(symmetric) 동작 : x, y가 null이 아닌 값이면 x.equals(y)는 y.equals(x)와 같은 결과를 출력해야 한다
- 연속적(transitive) 동작 : x, y, z가 null이 아닌 값이고 x.equals(y)와 y.equals(z)가 true라면 x.equals(z)도 true여야 한다
- 일관적(consistent) 동작 : x, y가 null이 아닌 값이면 x.equals(y)는 비교에 쓰이는 프로퍼티를 변경한 게 아니라면 여러 번 실행하더라도 항상 같은 결과를 반환해야 한다
- null과 관련된 동작 : x가 null이 아닌 값이면 x.equals(null)은 항상 false를 리턴해야 한다
```

요구사항은 모두 중요하다. 자바 때부터 정의됐으며 코틀린에서도 처음부터 정의된 내용이다. 따라서 수많은 객체가 이런 동작에 의존해서 만들어졌다. 이제 각 내용을 확인한다.

#### &#x20;equals는 반사적 동작을 해야 한다

크게 문제없는 확실한 동작으로 보이지만 코드를 실수로 작성해서 이런 동작을 위반할 수도 있다. \
현재 시간을 나타내고 밀리초로 시간을 비교하는 Time 객체를 아래처럼 만들었다고 가정한다.

```kotlin
// 이렇게 하지 마라
fun main() {
    val now = Time(isShow = true)
    now = now
    List(100_000) { now }.all { it == now }
}

class Time(
    val millisArg: Long = -1,
    val isShow: Boolean = false
) {
    val millis: Long
        get() = if (isShow) System.currentTimeMillis() else millisArg

    override fun equals(other: Any?): Boolean =
        other is Time && millis == other.millis
}
```

이 코드는 실행할 때마다 결과가 달라질 수 있어서 일관적 동작도 위반한다. \
이처럼 equals 규약이 잘못되면 컬렉션 안에 해당 객체가 포함돼 있어도 contains() 등으로 포함돼 있는지 확인할 수 없다.&#x20;

결과에 일관성이 없으면 실행 결과가 제대로 된 것인지 알 수 없으므로 코드를 믿을 수 없다.

이 코드를 고치는 간단한 방법은 객체가 현재 시간을 나타내고 있는지 확인하고, 현재 시간을 나타내지 않으면 같은 타임스탬프를 갖고 있는지로 동등성을 확인하는 것이다. \
이는 태그 클래스의 고전적인 예시다. 따라서 클래스 계층 구조를 써서 해결하는 게 좋다.

```kotlin
sealed class Time
data class TimePoint(val millis: Long): Time()
object now: Time()
```

#### &#x20;equals는 대칭적 동작을 해야 한다

일반적으로 다른 타입과 동등성을 확인하려고 할 때 이런 동작이 위반된다. \
아래 코드는 복소수를 나타내는 Complex 클래스를 구현한 예시다.&#x20;

```kotlin
class Complex(
    val real: Double,
    val imaginary: Double
) {
    // 이렇게 하지 마라
    override fun equals(other: Any?): Boolean {
        if (other is Double) {
            return imaginary == 0.0 && real == other
        }
        return other is Complex && real == other.real && imaginary == other.imaginary
    }
}
```

코드만 보면 문제없어 보이지만 Double은 Complex와 비교할 수 없다. 따라서 요소 순서에 따라 결과가 달라진다.

```kotlin
fun main() {
    println(Complex(1.0, 0.0).equals(1.0))  // true
    println(1.0.equals(Complex(1.0, 0.0)))  // false
}
```

대칭적 동작을 못한다는 것은 contains()와 단위 테스트 등에서 예측 못한 동작이 발생할 수 있다는 것이다.

동등성 비교가 대칭적으로 동작하지 못하면 "x를 y와 비교", "y와 x를 비교"가 달라지기 때문에 결과를 믿을 수 없게 된다. 이것은 문서화되어 있는 내용은 아니다.&#x20;

일반적으로 모든 사람이 대칭적인 동작을 가정하고 있을 뿐이다. 객체가 대칭적인 동작을 못한다면 예상 못한 오류가 발생할 수 있으며 이것을 디버깅 중에 찾기는 정말 어렵다. \
따라서 동등성 구현 시에는 대칭성을 항상 고려해야 한다.

결론적으로 다른 클래스는 동등하지 않게 만드는 게 좋다. 1과 1.0은 다르고 1.0과 1.0F도 다르다. 타입이 다르므로 비교 자체가 안 된다.

#### equals는 연속적이어야 한다&#x20;

이런 연속적인 동작을 설계할 때 가장 큰 문제는 타입이 다른 경우다. \
아래처럼 Date, DateTime을 정의했다고 가정한다.&#x20;

```kotlin
open class Date(
    val year: Int,
    val month: Int,
    val day: Int
) {
    // 이렇게 하지 마라
    override fun equals(other: Any?): Boolean = when (other) {
        is DateTime -> this == other.date
        is Date -> other.day == day && other.month == month && other.year == year
        else -> false
    }
}

class DateTime(
    val date: Date,
    val hour: Int,
    val minute: Int,
    val second: Int
): Date(date.year, date.month, date.day) {
    // 이렇게 하지 마라
    override fun equals(other: Any?): Boolean = when (other) {
        is DateTime -> other.date == date && other.hour == hour && other.minute == minute && other.second == second
        is Date -> date == other
        else -> false
    }
}
```

위 구현은 "DateTime과 Date를 비교할 때"보다 "DateTime과 DateTime을 비교할 때"에 더 많은 프로퍼티를 확인한다는 문제가 있다.&#x20;

따라서 날짜가 같지만 시간이 다른 두 DateTime 객체를 비교하면 false가 나오지만 날짜가 같은 Date 객체를 비교하면 true가 나온다. 즉 불연속적인 관계를 갖는다.

```kotlin
fun main() {
    val o1 = DateTime(Date(1992, 10, 20), 12, 30, 0)
    val o2 = Date(1992, 10, 20)
    val o3 = DateTime(Date(1992, 10, 20), 14, 45, 30)

    println(o1 == o2)   // true
    println(o2 == o3)   // true
    println(o1 == o3)   // false
}
```

Date, DateTime이 상속 관계를 가지므로 같은 객체끼리만 비교하게 만드는 방법은 안 좋은 선택지다. 이렇게 구현하면 리스코프 치환 원칙을 위반한다. 따라서 처음부터 상속 대신 컴포지션을 쓰고 두 객체를 아예 비교 못 하게 만드는 게 좋다.

#### equals는 일관성을 가져야 한다

불변 객체라면 언제나 결과가 같아야 한다. 즉 equals는 반드시 비교 대상이 되는 두 객체에만 의존하는 순수 함수여야 한다.&#x20;

이전에 본 Time 클래스는 이런 원칙을 위반한다. 원칙을 위반하는 대표적인 예로 java.net.URL.equals()가 있다&#x20;

#### null 관련 동작

null과는 같을 수 없다. x가 null이 아닐 때 모든 x.equals(null)은 false를 리턴해야 한다. null은 유일한 객체이므로 절대 null과 같을 수는 없다

***

### &#x20;URL과 관련된 equals 문제

equals를 잘못 설계한 예로는 java.net.URL이 있다. java.net.URL 객체 2개를 비교하면 같은 IP 주소로 해석될 때는 true, 아닐 때는 false가 나온다. \
문제는 이 결과가 네트워크 상태에 따라 달라진다는 것이다. 문제점을 정리하면 아래와 같다.

* 동작이 일관되지 않다. 네트워크가 정상이면 두 URL이 같고 문제가 있으면 다르다. 주어진 호스트의 IP 주소는 시간과 네트워크 상황에 따라 다르다. 어떤 네트워크에선 두 URL이 같을 수 있지만 다른 네트워크에선 다를 수 있다.
* 일반적으로 equals, hashCode 처리는 빠를 거라 예상하지만 네트워크 처리는 느리다. URL이 어떤 리스트 내부에 있는지 확인하는 경우, 이런 작업을 할 때 각 요소(URL)에 대해 네트워크 호출이 필요할 것이다. 따라서 예상되는 속도보다 느리게 동작한다. 안드로이드 같은 일부 플랫폼에선 메인 쓰레드에서 네트워크 작업이 금지된다. 이런 환경에선 URL을 Set에 추가하는 기본 조작도 쓰레드를 나눠서 해야 한다
* 동작 자체에 문제가 있다. 같은 IP 주소를 갖는다고 같은 컨텐츠를 나타내는 건 아니다. 가상 호스팅을 한다면 관련 없는 사이트가 같은 IP 주소를 공유할 수도 있다.

#### &#x20;equals 구현하기

특별한 이유가 없는 이상 equals 직접 구현은 안 좋다. \
기본 제공되는 걸 그대로 쓰거나 데이터 클래스로 만들어서 쓰는 게 좋다.&#x20;

그래도 직접 구현해야 한다면 반사적, 대칭적, 연속적, 일관적 동작을 하는지 꼭 확인하라. 만약 상속한다면 서브클래스에서 equals의 작동 방식을 변경하면 안 된다는 걸 기억하라.&#x20;

상속을 지원하면서 완벽한 커스텀 equals를 만드는 건 거의 불가능하다.
