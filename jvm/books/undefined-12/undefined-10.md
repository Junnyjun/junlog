# 로깅, 예외처리, 테스트, 디버깅

**함수형 프로그래밍에서 로그 남기기**

함수형 프로그래밍에서 많은 체이닝이 발생 할 때, 복잡한 데이터 변환 과정을 로그로 남기고 싶다면 어떻게 해야 할 까?

**1. 좋지 않은 방법 : 각 고차 함수 내에서 로그를 남김**

참조 투명성을 보장하지 않고, 하나의 함수가 여러 일을 함

```kotlin
private fun functionalSolution1(list: FunStream<Int>) = list
    .fmap {
        println("$it + 5")
        it + 5
    }
    .fmap {
        println("$it * $it")
        it * it
    }
    .fmap {
        println("$it > 50")
        it > 50
    }
```

**2. 확장 함수를 사용하여 개선**

로그를 남기는 확장함수를 사용하였으나 여전히 참조 투명성을 위배함

```kotlin
private fun functionalSolution2(list: FunStream<Int>) = list
    .fmap { addFive(it) withLog "$it + 5" }
    .fmap { square(it) withLog "$it * $it" }
    .fmap { isGreaterThan50(it) withLog "$it > 50" }

private fun addFive(it: Int) = it + 5

private fun square(it: Int) = it * it

private fun isGreaterThan50(it: Int) = it > 50

private infix fun <T> T.withLog(log: String): T {
    println(log)
    return this
}
```

**3. 모나드 활용**

게으른 평가를 이용하여 로그를 다른 컨텍스트 내부에 쌓아두고 이후 처리

```kotlin
fun main() {
    val result = functionalSolution4(funStreamOf(1, 2, 3))

    printFunStream(result.fmap { it.value })    // [false, false, true]
    printFunStream(
        result.flatMap { it.logs } as FunStream<*>)  // [1 + 5, 6 * 6, 36 > 50, 2 + 5, 7 * 7, 49 > 50, 3 + 5, 8 * 8, 64 > 50]
}

private fun functionalSolution4(list: FunStream<Int>) = list
    .fmap { addFive(it) withLog "$it + 5" }
    .fmap { it.flatMap { x -> square(x) withLog "$x * $x" } }
    .fmap { it.flatMap { x -> isGreaterThan50(x) withLog "$x > 50" } }

private infix fun <T> T.withLog(log: String): WriterMonad<T> = WriterMonad(this, funStreamOf(log))

data class WriterMonad<out T>(val value: T, val logs: FunStream<String>) : Monad<T> {

    companion object {
        fun <V> pure(value: V) = WriterMonad(0, mempty()).pure(value)
    }

    override fun <V> pure(value: V): WriterMonad<V> = WriterMonad(value, mempty())

    override fun <R> flatMap(f: (T) -> Monad<R>): WriterMonad<R> {
        val applied = f(this.value) as WriterMonad<R>
        return WriterMonad(applied.value, this.logs mappend applied.logs)
    }
}

private fun addFive(it: Int) = it + 5

private fun square(it: Int) = it * it

private fun isGreaterThan50(it: Int) = it > 50
```

**함수형 프로그래밍에서 예외처리하기**

lazy evaumation으로 처리되는 함수형 프로그래밍에서 예외가 발생할 가능성이 있는 람다 함수가 모나드의 함수 체인 중간에 입력되면 예외처리를 어떻게 하는 것이 좋을까?

**1. 메이비 모나드를 활용**

메이비는 실패할 가능성을 포함하는 컨텍스트로 이를 활용하여 중간에 예외가 발생할 경우 실패를 의미하는 Nothing을 반환하고, 성공하면 Just를 반환

```kotlin
fun main() {
    when(val result = divSubTenBy(5)) {
        is Nothing -> println("divSubTenBy(5) error")
        is Just -> println("divSubTenBy(5) returns ${result.value}")
    }   // divSubTenBy(5) returns 8

    when(val result = divSubTenBy(0)) {
        is Nothing -> println("divSubTenBy(0) error")
        is Just -> println("divSubTenBy(0) returns ${result.value}")
    }   // divSubTenBy(0) error
}

private fun divideTenBy(value: Int): Maybe<Int> = try {
    Just(10 / value)
} catch (e: Exception) {
    Nothing
}

private fun subtractTenBy(value: Int) = 10 - value

private fun divSubTenBy(value: Int) = divideTenBy(value).fmap { subtractTenBy(it) }
```

**2. 이더 모나드를 활용**

메이비를 사용하면 실패 여부를 정확하게 반환 할 수 있으나, 실패 원인까지 파악 할 수 없다. 실패 여부까지 포함 할 수 있는 컨텍스트인 이더를 모나드로 작성하여 예외처리에 활용 할 수 있다.

* 이더 모나드 구현

```kotlin
sealed class Either<out L, out R> : Monad<R> {

    companion object {
        fun <V> pure(value: V) = Right(0).pure(value)
    }

    override fun <V> pure(value: V): Either<L, V> = Right(value)

    override fun <R2> fmap(f: (R) -> R2): Either<L, R2> = when (this) {
        is Left -> Left(value)
        is Right -> Right(f(value))
    }

    override fun <B> flatMap(f: (R) -> Monad<B>): Monad<B> = when (this) {
        is Left -> Left(value)
        is Right -> f(value)
    }

//    override fun <R2> flatMap(f: (R) -> Monad<R2>): Either<L, R2> = when (this) {
//        is Left -> Left(value)
//        is Right -> try {
//            f(value) as Right<R2>
//        } catch (e: TypeCastException) {
//            Left(e.message) as Left<L>
//        }
//    }
}

data class Left<out L>(val value: L) : Either<L, Nothing>() {
    override fun toString(): String = "Left($value)"
}

data class Right<out R>(val value: R) : Either<Nothing, R>() {
    override fun toString(): String = "Right($value)"
}

infix fun <L, A, B> Either<L, (A) -> B>.apply(f: Either<L, A>): Either<L, B> = when (this) {
    is Left -> Left(value)
    is Right -> f.fmap(value)
}
```

* 이더 모나드를 활용한 예외처리 예제

```kotlin
fun main() {
    when(val result = divSubTenBy(5)) {
        is Left -> println("divSubTenBy(5) error by ${result.value}")
        is Right -> println("divSubTenBy(5) returns ${result.value}")
    }   // divSubTenBy(5) returns 8

    when(val result = divSubTenBy(0)) {
        is Left -> println("divSubTenBy(0) error by ${result.value}")
        is Right -> println("divSubTenBy(0) returns ${result.value}")
    }   // divSubTenBy(0) error by divide by zero exception
}

private fun divideTenBy(value: Int): Either<String, Int> = try {
    Right(10 / value)
} catch (e: ArithmeticException) {
    Left("divide by zero exception")
}

private fun subtractTenBy(value: Int) = 10 - value

private fun divSubTenBy(value: Int) = divideTenBy(value).fmap { subtractTenBy(it) }
```

**3. 트라이 모나드 활용**

이더 모나드는 try catch 문으로 예외를 받고, Left에 직접 메세지를 넣어 반환하였다. 트라이 모나드는 예외 자체를 컨텍스트에 담아서, 별도의 예외처리 없이 체이닝을 가능하게 한다.

* 트라이 모나드 구현

```kotlin
fun main() {
    // fmap test
    println(Success(10).fmap { it + 10 })     // Success(20)
    println(Failure(NullPointerException("NPE")).fmap { x: Int -> x + 10 })    // Failure(NPE)

    // pure test
    println(Try.pure(10))    // Success(10)
    println(Try.pure { x: Int -> x * 2 })   // Success((kotlin.Int) -> kotlin.Int)

    // apply test
    println(Try.pure { x: Int -> x * 2 } apply Failure(NullPointerException("NPE")))   // Failure(NPE)
    println(Try.pure { x: Int -> x * 2 } apply Success(10))   // Success(20)
    println(Try.pure({ x: Int, y: Int -> x * y }.curried())
            apply Failure(NullPointerException("NPE"))
            apply Success(10)
    )   // Failure(NPE)
    println(Try.pure({ x: Int, y: Int -> x * y }.curried())
            apply Success(10)
            apply Success(20)
    )   // Success(200)

    // flatMap test
    println(Success(10).flatMap { x -> Try.pure(x * 2) })  // Success(20)
    println(Failure(NullPointerException("NPE")).flatMap {  x: Int -> Try.pure(x * 2) } )   // Failure(NPE)
    println(Success(Success(10)).flatMap {  m -> m.fmap { x -> x * 2 }  })  // Success(20)
}

sealed class Try<out R> : Monad<R> {

    companion object {
        fun <V> pure(value: V) = Success(0).pure(value)
    }

    override fun <B> fmap(f: (R) -> B): Try<B> = super.fmap(f) as Try<B>

    override fun <V> pure(value: V): Try<V> = Success(value)

    override fun <R2> flatMap(f: (R) -> Monad<R2>): Try<R2> = when (this) {
        is Failure -> Failure(e)
        is Success -> try { f(value) as Try<R2> } catch (e: Throwable) { Failure(e) }
    }
}

data class Failure(val e: Throwable) : Try<Nothing>() {
    override fun toString(): String = "Failure(${e.message})"
}

data class Success<out R>(val value: R) : Try<R>() {
    override fun toString(): String = "Success($value)"
}

infix fun <T, R> Try<(T) -> R>.apply(f: Try<T>): Try<R> = when (this) {
    is Failure -> Failure(e)
    is Success -> f.fmap(value)
}

private fun <P1, P2, R> ((P1, P2) -> R).curried(): (P1) -> (P2) -> R =
        { p1: P1 -> { p2: P2 -> this(p1, p2) } }
```

* 트라이 모나드를 활용한 예외처리 예제

```kotlin
fun main() {
    when(val result = divSubTenBy(5)) {
        is Failure -> println("divSubTenBy(5) error by ${result.e}")
        is Success -> println("divSubTenBy(5) returns ${result.value}")
    }   // divSubTenBy(5) returns 5

    when(val result = divSubTenBy(0)) {
        is Failure -> println("divSubTenBy(0) error by ${result.e}")
        is Success -> println("divSubTenBy(0) returns ${result.value}")
    }   // divSubTenBy(0) error by java.lang.ArithmeticException: / by zero
}

private fun divideTenBy(value: Int): Try<Int> = Try.pure(10).fmap { it / value }

private fun subtractTenBy(value: Int) = 10 / value

private fun divSubTenBy(value: Int) = divideTenBy(value).fmap { subtractTenBy(it) }
```

　

**함수형 프로그래밍에서 테스팅하기**

함수형 프로그래밍은 일반적인 프로그래밍 방식보다 테스트하기 좋은 코드가 만들어진다.

**테스트하기 좋은 코드만들기**

첫째, 불변 객체를 사용하라

* 순수한 함수형 언어에서 한번 생성된 객체는 변경할 수 없다. 불변 객체를 사용한 함수는 예측이 가능하다.

둘째, **동일한 입력에 동일한 출력을 보장하는 순수한 함수를 만들라**

셋째, **파일 / 네트워크 IO와 같은 부수효과를 발생하거나 상태를 가진 영역은 순수한 영역과 최대한 분리하라**

넷째, **널값의 사용을 피하라**

다섯째, **메이비와 이더를 적극적으로 활용하라**

위 다섯까지 명제의 방향성은 한가지이며 다음과 같다 : **부수효과를 최대한 결리하고 프로그램을 순수한 함수들로만 구성하라**

객체지향 프로그래밍에서는 부수효과를(DB 연결 등) 가진 모듈을 효과적으로 교체하기 위해 의존성 주입(DI) 패턴을 사용하며, 함수형 프로그래밍에서도 태그리스 파이널(Tagless final)이라는 패턴을 사용한다.

태그리스 파이널은 메이비, 리스트, 이더처럼 구체적인 타입을 사용하지 않은 타입 클래스에 행위들을 선언하고 실행 흐름을 선언하는 것이다. 이렇게 선언된 실행 흐름에는 구체적인 타입이 정해져 있지 않다. 실행 흐름이 선언된 상태에서 타입 클래스의 행위들을 구현해서 넘길 수 있는데, 구체적인 타입도 이때 정해진다. 태그리스 파이널 패턴을 사용하면 외부 시스템에 접근하는 모듈을 테스트 목적의 모의 객체로 손쉽게 교체할 수 있다.

　

**함수형 프로그래밍에서 디버깅하기**

명령형 프로그래밍은 라인별로 코드를 자겅하기 때문에 주로 중단점 디버거를 사용하지만, 함수형 프로그래밍은 여러 가지 고차 함수의 체인을 한 라인에 선언하고, 심지어 게으른 평가로 인해서 코드의 실행이 특정 라인에서 이루어진다고 보장하기 어렵다. 따라서 함수형 프로그래밍에서는 다른 도구를 사용해야 함

인텔리제이 디버거를 코틀린에서 활용하는 팁을 알아보자

**인텔리제이를 활용한 리스트 체인 디버깅**

인텔리제이 디버거는 함수에 입력되는 람다 함수 단위로 중단점을 찍을 수 있다.

중단점을 찍은 후 Set Breakpoint를 통해 라인, 람다 혹은 전체 등 중단점을 세밀하게 설정 할 수 있다.

**인텔리제이를 활용한 시퀀스 체인 디버깅**

게으른 평가를 수행하는 시퀀스는 실제 수행되는 시점에 중단점이 걸린다.

함수 체인이 평가되는 시점이 실제 로직과 아주 멀리 있거나, 시퀀스에 값이 매우 많다면, 특정 값에서 발생하는 오류를 찾기 쉽지 않은데 이럴 때는 람다 중단점에 조건을 서정 할 수 있다(해당 브레이크 포인트에 컨트롤을 누른상태로 클릭)
