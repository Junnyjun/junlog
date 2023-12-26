# 고차 함수

## **고차 함수**

함수형 프로그래밍에서는 아래 두 가지 조건 중 하나 이상을 만족하는 함수를 고차 함수라 한다

* 함수를 매개변수로 받는 함수
* 함수를 반환하는 함수

**고차함수는 코드의 재사용성을 높임**

객체지향 계산기 vs 고차함수 계산기

#### 객체 지향 계산기

상속을 사용하며 기능 추가시 중복 코드가 많아짐

```kotlin
fun main() {
    // OOP 예제
  val calcSum = Sum()
    val calcMinus = Minus()
  val calcProduct = Product()
    val calcTwiceSum = TwiceSum()

    println(calcSum.calc(1, 5))     // 6
    println(calcMinus.calc(5, 2))   // 3
    println(calcProduct.calc(4, 2)) // 8
  println(calcTwiceSum.calc(8, 2)) //20
}

interface Calcable {
    fun calc(x: Int, y: Int): Int
}

class Sum : Calcable {
    override fun calc(x: Int, y: Int): Int {
        return x + y
    }
}

class Minus : Calcable {
    override fun calc(x: Int, y: Int): Int {
        return x - y
    }
}

class Product : Calcable {
    override fun calc(x: Int, y: Int): Int {
        return x * y
    }
}

class TwiceSum : Calcable {
    override fun calc(x: Int, y: Int): Int {
        return (x + y) * 2
    }
}
```

#### 고차 함수 계산기

비즈니스 기능을 함수로 모듈화

```kotlin
fun main() {
    // 고차함수를 사용한 예
  val sum: (Int, Int) -> Int = { x, y -> x + y }
    val product: (Int, Int) -> Int = { x, y -> x * y }
    val minus: (Int, Int) -> Int = { x, y -> x - y }
    val twiceSum: (Int, Int) -> Int = { x, y -> (x + y) * 2 }

    println(higherOrder(sum, 1, 5))     // 6
    println(higherOrder(minus, 5, 2))   // 3
    println(higherOrder(product, 4, 2)) // 8
    println(higherOrder(twiceSum, 8, 2))   // 20
}

private fun higherOrder(func: (Int, Int) -> Int, x: Int, y: Int): Int = func(x, y)
```

**코드 작성이 간결해짐**

입력 리스트의 값을 두 배로 증가시키고 10보다 큰 수를 반환하는 예제

```kotlin
fun main() {
    val ints: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

    // 명령형 프로그래밍 예
    val over10Values: ArrayList<Int> = ArrayList()

    for (element in ints) {
        val twiceInt = element * 2
        if(twiceInt > 10){
            over10Values.add(twiceInt)
        }
    }

    println(over10Values)   // [12, 14, 16, 18, 20]

    // 고차함수를 사용한 예
    val result = ints
            .map { it * 2 }
            .filter { it > 10 }

    println(result)            // [12, 14, 16, 18, 20]
}
```

### **부분 함수**

* 허용되지 않는 입력값으로 함수를 호출 할 때, 일반적인 프로그래밍에서는 예외를 던지거나 특정값을 리턴하도록 처리
* 함수형 프로그래밍에서는 이러한 처리를 ‘부분 함수’를 통해 처리
* 부분 함수란 **모든 가능한 입력 중, 일부 입력에 대한 결과만 정의한 함수**를 의미

```kotlin
class PartialFunction<in P, out R>(
    private val condition: (P) -> Boolean,
    private val f: (P) -> R)
    : (P) -> R {

    override fun invoke(p: P): R = when {
        condition(p) -> f(p)
        else -> throw IllegalArgumentException("$p isn't supported.")
    }

    fun isDefinedAt(p: P): Boolean = condition(p)
}
```

* 부분함수를 사용할 경우 호출하는 쪽에서 호출하기 전 함수가 정상적으로 동작하는지 isDefinedAt과 같은 방법을 제공함으로써 미리 확인 할 수 있다.
* 호출자가 함수가 던지는 예외나 오류값에 대해서 알지 못하여도 된다.
* 부분 함수의 조합으로 부분 함수 자체를 재사용 할 수도 있고, 확장 할 수도 있다.

### **부분 적용 함수**

* 부분 적용 함수는 부분 함수와 이름이 비슷하지만 관계는 없음
* 부분 적용 함수란 전달 받은 매개변수를 가변적으로 사용하여 함수 내부에서 원 함수와 다른 매개변수를 이용하는 함수를 말함
* 전달하는 매개변수는 가변적임
* 부분 적용 함수는 코드를 재사용 하기 위해 쓸 수도 있지만, 커링 함수(curried functions)를 구현하기 위해 필요한 개념임

```kotlin
fun main() {
    val func = { a: String, b: String -> a + b }

    val partiallyAppliedFunc1 = func.partial1("Hello")
    val result1 = partiallyAppliedFunc1("World")

    println(result1)  // Hello World

    val partiallyAppliedFunc2 = func.partial2("World")
    val result2 = partiallyAppliedFunc2("Hello")

    println(result2)  // Hello World
}

fun <P1, P2, R> ((P1, P2) -> R).partial1(p1: P1): (P2) -> R {
    return { p2 -> this(p1, p2) }
}

fun <P1, P2, R> ((P1, P2) -> R).partial2(p2: P2): (P1) -> R {
    return { p1 -> this(p1, p2) }
}
```

### **커링 함수**

커링이란 여러 개의 매개변수를 받는 함수를 분리하여, 단일 매개변수를 받는 부분 적용 함수의 체인으로 만드는 방법임

여러 매개변수를 받는 함수

```kotlin
private fun multiThree(a: Int, b: Int, c: Int): Int = a * b * c
```

한개의 매개변수를 전달받는 체인으로 구성된 커링 함수

```kotlin
private fun multiThree(a: Int) = { b: Int -> { c: Int -> a * b * c } }
```

두 함수의 호출 결과는 같으나 호출 방법이 다름

```kotlin
    println(partial3) // 6
    println(multiThree(1)(2)(3)) // 6, 함수를 커링으로 쪼갰기 때문에 이러한 형태의 호출이 가능
```

**함수형 프로그래밍에서 복잡해 보이는 커링을 사용하는 이유**

* 부분 적용 함수를 다양하게 재사용 할 수 있음
* 마지막 매개변수가 입력될 때까지 함수의 실행을 늦출 수 있음

**코틀린용 커링 함수 추상화하기**

코틀린에서는 기본 함수로 커링을 제공하지 않음, 매개변수가 한개인 부분 적용 함수의 체인을 만들기 위해서는 복잡하게 함수를 정의 해야 함 커링을 일반화하여 커링 함수를 쉽게 만들 수 있도록 다음과 같은 방법으로 추상화가 가능함

```kotlin
private fun <P1, P2, P3, R> ((P1, P2, P3) -> R).curried(): (P1) -> (P2) -> (P3) -> R =
        { p1: P1 -> { p2: P2 -> { p3: P3 -> this(p1, p2, p3) } } }

private fun <P1, P2, P3, R> ((P1) -> (P2) -> (P3) -> R).uncurried(): (P1, P2, P3) -> R =
        { p1: P1, p2: P2, p3: P3 -> this(p1)(p2)(p3) }

fun main() {
    val multiThree = { a: Int, b: Int, c: Int -> a * b * c }
    val curried = multiThree.curried()
    println(curried(1)(2)(3))   // 6

    val uncurried = curried.uncurried()
    println(uncurried(1, 2, 3)) // 6
}
```

### **합성 함수**

합성 함수란 고차 함수를 이용해서 두개의 함수를 결합하는 것을 의미함

(f o g)(x) = f(g(x)) 이며 (f o g)(x)는 g 함수가 x를 매개변수로 호출한 결과를 f 함수의 매개변수로 전달한 결과와 같음

```kotlin
infix fun <F, G, R> ((F) -> R).compose(g: (G) -> F): (G) -> R {
    return { gInput: G -> this(g(gInput)) }
}

fun main() {
    println(composed(3))    // 9
}

private fun composed(i: Int) = addThree(twice(i))

private fun addThree(i: Int) = i + 3

private fun twice(i: Int) = i * 2
```

**여러 개의 매개변수를 갖는 함수를 합성하는 방법**

```kotlin
import kotlin.math.abs

fun main() {
    val powerOfTwo = { x: Int -> power(x.toDouble(), 2).toInt() }
    val gcdPowerOfTwo = { x1: Int, x2: Int -> gcd(powerOfTwo(x1), powerOfTwo(x2)) }

    println(gcdPowerOfTwo(25, 5))   // 25

    val curriedGcd1 = ::gcd.curried()
    // 잘못된 합성의 예
    val composedGcdPowerOfTwo1 = curriedGcd1 compose powerOfTwo

    println(composedGcdPowerOfTwo1(25)(5))   // 5

    val curriedGcd2 = { m: Int, n: Int -> gcd(m, powerOfTwo(n)) }.curried()
    // 적절한 합성의 예
    val composedGcdPowerOfTwo2 = curriedGcd2 compose powerOfTwo

    println(composedGcdPowerOfTwo2(25)(5))   // 25
}

private tailrec fun gcd(m: Int, n: Int): Int = when (n) {
    0 -> m
    else -> gcd(n, m % n)
}

private tailrec fun power(x: Double, n: Int, acc: Double = 1.0): Double = when (n) {
    0 -> acc
    else -> power(x, n - 1, x * acc)
}
```
