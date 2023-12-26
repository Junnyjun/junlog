# 태그 클래스보다는 클래스 계층을 사용하라

큰 규모의 프로젝트에선 상수(constant) 모드를 가진 클래스를 꽤 많이 볼 수 있다. \
이런 상수 모드를 태그라고 부르며 태그를 포함한 클래스를 태그 클래스라고 부른다.\
그런데 태그 클래스는 다양한 문제를 갖고 있다.&#x20;

이런 문제는 서로 다른 책임을 한 클래스에 태그로 구분해서 넣는다는 것에서 시작한다. \
아래 코드를 보면 테스트에 쓰이는 클래스로서 어떤 값이 기준에 만족하는지 확인하기 위해 쓰이는 클래스를 볼 수 있다

```kotlin
class ValueMatcher<T> private constructor(
    private val value: T? = null,
    private val matcher: Matcher
) {
    enum class Matcher {
        EQUAL,
        NOT_EQUAL,
        LIST_EMPTY,
        LIST_NOT_EMPTY
    }
    
    fun match(value: T?) = when (matcher) {
        Matcher.EQUAL -> value == this.value
        Matcher.NOT_EQUAL -> value != this.value
        Matcher.LIST_EMPTY -> value is List<*> && value.isEmpty()
        Matcher.LIST_NOT_EMPTY -> value is List<*> && value.isNotEmpty()
    }
    
    companion object {
        fun <T> equal(value: T) = ValueMatcher(value = value, matcher = Matcher.EQUAL)
        fun <T> notEqual(value: T) = ValueMatcher(value = value, matcher = Matcher.NOT_EQUAL)
        fun <T> emptyList() = ValueMatcher<T>(matcher = Matcher.LIST_EMPTY)
        fun <T> notEmptyList() = ValueMatcher<T>(matcher = Matcher.LIST_NOT_EMPTY)
    }
}
```

#### 이런 접근법에는 굉장히 많은 단점이 있다.

한 클래스에 여러 모드를 처리하기 위한 상용구(boilerplate)가 추가된다

여러 목적으로 써야 하므로 프로퍼티가 일관되지 않게 사용될 수 있으며 더 많은 프로퍼티가 필요하다. 위코드에서 value는 LIST\_EMPTY 또는 LIST\_NOT\_EMPTY일 때 아예 쓰이지 않는다

요소가 여러 목적을 가지고, 요소를 여러 방법으로 설정할 수 있는 경우 상태의 일관성, 정확성을 지키기 어렵다

팩토리 메서드를 써야 하는 경우가 많다. 그렇지 않으면 객체가 제대로 생성됐는지 확인하는 자체가 굉장히 어렵다&#x20;

그래서 코틀린은 일반적으로 태그 클래스보다 sealed 클래스를 많이 사용한다. \
한 클래스에 여러 모드를 만드는 방법 대신 각 모드를 여러 클래스로 만들고 타입 시스템, 다형성을 활용하는 것이다.&#x20;

그리고 이런 클래스에는 sealed 한정자를 붙여 서브클래스 정의를 제한한다.&#x20;

```kotlin
sealed class ValueMatcher<T> {
    abstract fun match(value: T): Boolean
    
    class Equal<T>(val value: T): ValueMatcher<T>() {
        override fun match(value: T): Boolean = value == this.value
    }
    
    class NotEqual<T>(val value: T): ValueMatcher<T>() {
        override fun match(value: T): Boolean = value != this.value
    }
    
    class EmptyList<T> : ValueMatcher<T>() {
        override fun match(value: T): Boolean = value is List<*> && value.isEmpty()
    }
    
    class NotEmptyList<T> : ValueMatcher<T>() {
        override fun match(value: T): Boolean = value is List<*> && value.isNotEmpty()
    }
}
```

이렇게 구현하면 책임이 분산되므로 훨씬 깔끔하다. 각 객체들은 자신에게 필요한 데이터만 있으며 적절한 파라미터만 갖는다. 이런 계층을 사용하면 태그 클래스의 단점을 모두 해소할 수 있다.

#### sealed 한정자

반드시 sealed 한정자를 써야 하는 건 아니다. abstract 한정자를 쓸 수도 있지만 sealed 한정자는 외부 파일에서 서브클래스를 만드는 행위 자체를 모두 제한한다. 외부에서 추가적인 서브클래스를 만들 수 없기 때문에 타입이 추가되지 않을 거라는 게 보장된다. 따라서 when을 쓸 때 else 브랜치를 따로 만들 필요가 없다.

when은 모드를 구분해서 다른 처리를 만들 때 편리하다. 어떤 처리를 각 서브클래스에 구현할 필요 없이 when을 활용하는 확장 함수로 정의하면 한 번에 구현할 수 있다.

```kotlin
fun <T> ValueMatcher<T>.reversed(): ValueMatcher<T> = when (this) {
    is ValueMatcher.EmptyList -> ValueMatcher.NotEmptyList()
    is ValueMatcher.NotEmptyList -> ValueMatcher.EmptyList()
    is ValueMatcher.Equal -> ValueMatcher.NotEqual(value)
    is ValueMatcher.NotEqual -> ValueMatcher.Equal(value)
}
```

abstract 키워드를 쓰면 다른 개발자가 새 인스턴스를 만들어 사용할 수도 있다. \
이 경우 함수를 abstract로 선언하고 서브클래스 안에 구현해야 한다. \
when을 쓰면 프로젝트 외부에서 새 클래스가 추가될 때 함수가 제대로 동작하지 않을 수 있기 때문이다.&#x20;

sealed 한정자를 쓰면 확장 함수를 써서 클래스에 새 함수를 추가하거나 클래스의 다양한 변경을 쉽게 처리할 수 있다. 클래스의 서브클래스를 제어하려면 sealed 한정자를 써야 한다.&#x20;

#### 태그 클래스와 상태 패턴의 차이

태그 클래스, 상태 패턴을 혼동하면 안 된다. \
상태 패턴은 객체 내부가 변화할 때 객체 동작이 변하는 소프트웨어 디자인 패턴이다.&#x20;

상태 패턴은 프론트엔드 컨트롤러, 프레젠터, 뷰모델을 설계할 때 많이 사용된다. \
아침 운동을 위한 앱을 만들 때 각각의 운동 전에 준비 시간이 있다고 한다. \
또한 운동을 모두 하고 나면 완료했다는 화면을 띄우려고 한다.

상태 패턴을 쓴다면 서로 다른 상태를 나타내는 클래스 계층 구조를 만들게 된다. \
그리고 현재 상태를 나타내기 위한 읽고 쓸 수 있는 프로퍼티도 만들게 된다.

```kotlin
sealed class WorkoutState

class PrepareState(val exercise: Exercise): WorkoutState()

class ExerciseState(val exercise: Exercise): WorkoutState()

object DoneState: WorkoutState()

fun List<Exercise>.toStates(): List<WorkoutState> = flatMap { exercise ->
    listOf(PrepareState(exercise), ExerciseState(exercise))
} + DoneState

class WorkoutPresenter(/*...*/) {
    private var state: WorkoutState = states.first()
}
```

#### 차이점은 아래와 같다.

상태는 더 많은 책임을 가진 클래스다

상태는 바꿀 수 있다



구체 상태(concrete state)는 객체를 활용해서 표현하는 게 일반적이며 태그 클래스보단 sealed class 계층으로 만든다. \
또한 이를 불변 객체로 만들고 바꿔야 할 때마다 state 프로퍼티를 변경하게 만든다. \
그리고 뷰에서 이런 state의 변화를 observe한다.
