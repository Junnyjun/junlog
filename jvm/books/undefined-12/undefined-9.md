# 모나드

### **모나드 타입 클래스**

펑터는 어떤 컨텍스트 내의 값을 변경 할 때 사용된다.&#x20;

펑터 타입 클래스를 사용해서 다양한 타입에서 fmap 함수를 적용 할 수 있었다. 또한 애플리케이티브 펑터는 apply 함수를 구현하여 컨텍스트 안에 있는 함수를 다룰 수 있다.&#x20;

그러나 **어떤 값이 포함된 컨텍스트를 일반적인 값을 받아서 컨텍스트를 반환하는 함수의 입력으로 넣으려면** 다른 방법이 필요하다.

모나드는 펑터이자 애플리케이티브 펑터다. 추가로 모나드는 flatMap이라는 함수가 추가로 제공된다. 따라서 모나드는 애플리케이티브 펑터의 확장으로 볼 수 있다.

```kotlin
interface Monad<out A> : Functor<A> {

    fun <V> pure(value: V): Monad<V>

    override fun <B> fmap(f: (A) -> B): Monad<B> = flatMap { a -> pure(f(a)) }

    infix fun <B> flatMap(f: (A) -> Monad<B>): Monad<B>

    infix fun <B> leadTo(m: Monad<B>): Monad<B> = flatMap { m }
}
```

flatMap 함수는 Monad를 (A) -> Monad 함수에 적용해서 Monad를 반환 한다. \
이와 같은 동작은 모나드 컨텍스트에 있는 값을 일반값처럼 다룰 수 있게 한다.

flatMap 함수의 또 다른 기능으로는 중첩된 컨텍스트를 하나의 컨텍스트로 펼쳐서 매핑하는 것이다. 그러나 중첩을 펼치는 것은 flatMap 함수의 한가지 기능일 뿐, 모나드를 더 깊게 이해하고 활용하려면 flatMap 함수는 **컨텍스트에 값을 마치 일반 값처럼 다룰 수 있게 해준다**는 것을 명심하자.

### **메이비 모나드**

옵셔널 개념을 적용한 모나드

```kotlin
fun main() {
    // fmap test
    println(Just(10).fmap { it + 10 })   // Just(20)
    println(Nothing.fmap { x: Int -> x + 10 })  // Nothing

    // pure test
    println(Maybe.pure(10))  // Just(10)
    println(Maybe.pure { x: Int -> x * 2 })  // Just((kotlin.Int) -> kotlin.Int)

    // apply test
    println(Maybe.pure { x: Int -> x * 2 } apply Just(10))  // Just(20)
    println(Maybe.pure { x: Int -> x * 2 } apply Nothing)   // Nothing
    println(Maybe.pure({ x: Int, y: Int -> x * y }.curried())
            apply Just(10)
            apply Just(20)
    )   // Just(200)

    println(Maybe.pure({ x: Int, y: Int, z: Int -> x * y + z }.curried())
            apply Just(10)
            apply Just(20)
            apply Just(30)
    )   // Just(230)

    // leadTo test
    println(Just(10).leadTo(Nothing))   // Nothing
    println(Nothing.leadTo(Just(10)))   // Nothing
    println(Just(10).leadTo(Just(20)))   // Just(20)

    // flatMap test
    println(Just(10).flatMap { x -> Maybe.pure(x * 2) })    // Just(20)
    println(Nothing.flatMap { x: Int -> Maybe.pure(x * 2) })    // Nothing
    println(Just(Just(10)).flatMap { m -> m.fmap { x -> x * 2 } })  // Just(20)
}

sealed class Maybe<out A> : Monad<A> {

    companion object {
        fun <V> pure(value: V) : Maybe<V> = Just(0).pure(value)
    }

    override fun <V> pure(value: V): Maybe<V> = Just(value)

    override fun <B> fmap(f: (A) -> B): Maybe<B> = super.fmap(f) as Maybe<B>

    override infix fun <B> flatMap(f: (A) -> Monad<B>): Maybe<B> = when (this) {
        is Just -> try { f(value) as Maybe<B> } catch (e: ClassCastException) { Nothing }
        is Nothing -> Nothing
    }
}

data class Just<out A>(val value: A) : Maybe<A>() {

    override fun toString(): String = "Just($value)"
}

object Nothing : Maybe<kotlin.Nothing>() {

    override fun toString(): String = "Nothing"
}

infix fun <A, B> Maybe<(A) -> B>.apply(f: Maybe<A>): Maybe<B> = when (this) {
    is Just -> f.fmap(value)
    is Nothing -> Nothing
}

private fun <P1, P2, R> ((P1, P2) -> R).curried(): (P1) -> (P2) -> R =
        { p1: P1 -> { p2: P2 -> this(p1, p2) } }

private fun <P1, P2, P3, R> ((P1, P2, P3) -> R).curried(): (P1) -> (P2) -> (P3) -> R =
        { p1: P1 -> { p2: P2 -> { p3: P3 -> this(p1, p2, p3) } } }
```

### **메이비 모나드 활용**

중첩된 널이 될 수 있는 객체 내의 값 접근하는 상황에서 일반적인 처리 예

```kotlin
class A1(val b: B1?)
class B1(val c: C1?)
class C1(val d: D1?)
class D1(val value: String?)

fun main() {
    val a = A1(B1(C1(D1("someValue"))))

    println(getValueOfD1(a))     // someValue
}

private fun getValueOfD1(a: A1): String {
    val b = a.b
    if (b != null) {
        val c = b.c
        if (c != null) {
            val d = c.d
            if (d != null) {
                if (d.value != null) {
                    return d.value
                } else {
                    return ""
                }
            }
        }
    }

    return ""
}
```

메이비를 사용한 값 접근 예

1번 상황에서 중첩된 if else 구조가 flatMap 함수로 대체

```kotlin
class A2(val b: Maybe<B2>)
class B2(val c: Maybe<C2>)
class C2(val d: Maybe<D2>)
class D2(val value: Maybe<String>)

fun main() {
    val a = A2(Just(B2(Just(C2(Just(D2(Just("someValue"))))))))
    val result = when (val maybe = getValueOfD2(a)) {
        is Just -> maybe.value
        Nothing -> ""
    }

    println(result)     // someValue
}

private fun getValueOfD2(a: A2): Maybe<String> = a.b
    .flatMap { it.c }
    .flatMap { it.d }
    .flatMap { it.value }
```

코틀린의 safe null 처리를 이용한 코드

안전한 널 처리를 언어차원에서 지원하는 코틀린은 메이비 모나드를 별도로 제공하지 않음 간단한 널 처리가 필요한 것이 아니라면 메이비 모나드를 직접 만들어 사용하는 것이 좋음

```kotlin
class A3(val b: B3?)
class B3(val c: C3?)
class C3(val d: D3?)
class D3(val value: String?)

fun main() {
    val a = A3(B3(C3(D3("someValue"))))

    println(a.b?.c?.d?.value ?: "")     // someValue
}
```

## **모나드의 법칙**

(애플리케이티브) 펑터와 같이 작성된 타입이 모나드의 법칙을 만족해야 안전하게 동작한다.

* 왼쪽 항등 법칙 : pure(x) flatMap f = f(x)
* 오른쪽 항등 법칙 : m flatMap pure = m
* 결합 법칙 : (m flatMap f) flatMap g = m flatMap { x -> f(x) flatMap g }

```kotlin
fun main() {
    val x = 10
    val f = { a: Int -> Just(a * 2) }
    val g = { a: Int -> Just(a + 1) }
    val h = { a: Int -> Just(a * 10) }
    val pure = { a: Int -> Just(a) }
    val m = Just(10)

    // Left Identity
    // pure(x) flatMap f = f(x)
    println(pure(x) flatMap f == f(x))  // true

    // Right Identity
    // m flatMap pure = m
    println(m flatMap pure == m)    // true

    // Associativity Law
    // (m flatMap f) flatMap g = m flatMap { x -> f(x) flatMap g }
    println((m flatMap f) flatMap g == m flatMap { a -> f(a) flatMap g } )  // true

    // identity compose f = f
    // f compose identity = f
    // (f compose g) compose h = f compose (g compose h)
    println((pure compose f)(10) == f(10))  // true
    println((f compose pure)(10) == f(10))  // true
    println(((f compose g) compose h)(10) == (f compose (g compose h))(10)) // true
}

private infix fun <F, G, R> ((F) -> Monad<R>).compose(g: (G) -> Monad<F>): (G) -> Monad<R> {
    return { gInput: G -> g(gInput) flatMap this }
}
```

### **IO 모나드**

순수한 함수형 언어에서 입출력(IO) 작업은 큰 골칫거리, 입출력 자체가외부와의 연결이 불가피하여 상태를 변경해야하고, 데이터의 순수성을 깨는 컨텍스트이기 때문

이와 같은 이유로 코틀린이나 스칼라 같은 하이브리드 언어도 입출력 작업은 명령형 프로그래밍 방식을 따른다.

그러나 하스켈의 경우 언어의 순수성을 지키기 위해 내부적으로 아주 복잡한 방법으로 입출력을 구현함

하스켈은 프로그램의 순수한 영역과, 상태를 변경해야하는 비순수 영역(사이드 이펙트 발생 가능한)을 완전히 분리하는 방법으로 입출력을 구현하였다.

#### **명령형 방식의 IO 및 함수 분리 예제**

함수 내부에서 파일 입출력 작업을 수행하기 때문에 순수한 함수가 아님\
(동일한 입력에 동일한 출력을 보장하지 않음)

```kotlin
fun main() {
    val filePath = ClassLoader.getSystemResource("someArticle.txt").path
    println(getFirstWord(filePath))     // Why

    val lines = getLines(filePath)
    println(getFirstWord2(lines))    // Why
}

private fun getFirstWord(filePath: String): String = getFirstLine(filePath).split(" ").first()

private fun getFirstLine(filePath: String): String = File(filePath).readLines().first()

private fun getFirstWord2(lines: List<String>): String = lines.first().split(" ").first()

private fun getLines(filePath: String): List<String> = File(filePath).readLines()
```

하스켈에서는 입출력 작업이 모나드 내에서만 가능하도록 강제하고, IO 모나드의 결괏값을 꺼내오는 바인딩 연산자(<-)를 제공하며, IO 모나드에서 값을 꺼낼 때 반드시 <-의 사용을 강제하여 두 작업의 분리를 유도한다.

```haskell
getFirstLine :: String -> IO String
getFirstLine :: [] = return ()
getFirstLine :: x = do
	handle <- openFile x ReadMode
	content <- hGetContents handle
	let firstLine = head . lines $ content
	return firstLine
```

### **리스트 모나드**

모나드를 이용해서 구현한 List

```kotlin
fun main(args: Array<String>) {
    val list1 = funListOf(1, 2, 3)
    val list2 = funListOf(5, 10, 15, 20)

    println(list1.mempty())   // []
    println(list1 mappend list2)    // [1, 2, 3, 5, 10, 15, 20]
    println(list1.fmap { x -> x * 2 })  // [2, 4, 6]
    println(FunList.pure(10))   // [10]

    val list3 = funListOf<(Int) -> Int>({ x -> x * 2 }, { x -> x + 1 }, { x -> x - 10 })
    println(list3 apply list1)  // [2, 4, 6, 2, 3, 4, -9, -8, -7]
    println(list3 apply list2)  // [10, 20, 30, 40, 6, 11, 16, 21, -5, 0, 5, 10]
    println(list1 _apply list3)  // [2, 4, 6, 2, 3, 4, -9, -8, -7]
    println(list2 _apply list3)  // [10, 20, 30, 40, 6, 11, 16, 21, -5, 0, 5, 10]

    println(Nil flatMap { x -> funListOf(x) })  // []
    println(list1 flatMap { x -> funListOf(x, -x) })    // [1, -1, 2, -2, 3, -3]
    println(funListOf(list1, list2).flatten())  // [1, 2, 3, 5, 10, 15, 20]

    println(funListOf(1, 2)
            .flatMap { x -> funListOf(x to 'a', x to 'c') }     // [(1, a), (1, c), (2, a), (2, c)]
            .fmap { x -> x.first to x.second.toUpperCase() }     // [(1, A), (1, C), (2, A), (2, C)]
            ._apply(funListOf<(Pair<Int, Char>) -> Char>({ x -> x.second }, { x -> x.second + x.first }))   // [A, B, C, D, A, C, C, E]
//            .foldLeft(Nil as FunList<Char>) { acc, x -> if (acc.contains(x)) acc else Cons(x, acc) }    // [E, D, C, B, A]
            .distinct()   // [E, D, C, B, A]
            .reverse()  // [A, B, C, D, E]
    )
}

sealed class FunList<out T> {
    companion object
}

object Nil : FunList<kotlin.Nothing>() {
    override fun toString(): String = "[]"
}

data class Cons<out T>(val head: T, val tail: FunList<T>) : FunList<T>() {
    override fun toString(): String = "[${foldLeft("") { acc, x -> "$acc, $x" }.drop(2)}]"
}

fun <T> funListOf(vararg elements: T): FunList<T> = elements.toFunList()

private fun <T> Array<out T>.toFunList(): FunList<T> = when {
    this.isEmpty() -> Nil
    else -> Cons(this[0], this.copyOfRange(1, this.size).toFunList())
}

fun <T> FunList<T>.mempty() = Nil

infix fun <T> FunList<T>.mappend(other: FunList<T>): FunList<T> = when (this) {
    is Nil -> other
    is Cons -> Cons(head, tail.mappend(other))
}

fun <T> FunList<FunList<T>>.flatten(): FunList<T> = foldRight(mempty()) { t, r: FunList<T> -> t mappend r }

fun <T> FunList.Companion.pure(value: T): FunList<T> = Cons(value, Nil)

infix fun <T, R> FunList<(T) -> R>.apply(f: FunList<T>): FunList<R> = when (this) {
    is Nil -> Nil
    is Cons -> f.fmap(head) mappend tail.apply(f)
}

infix fun <T, R> FunList<T>._apply(f: FunList<(T) -> R>): FunList<R> = when (this) {
    is Nil -> Nil
    is Cons -> f.fmap { it(head) } mappend tail._apply(f)
}

//infix fun <T, R> FunList<T>.flatMap(f: (T) -> FunList<R>): FunList<R> = when (this) {
//    is Nil -> Nil
//    is Cons -> f(head) mappend tail.flatMap(f)
//}

infix fun <T, R> FunList<T>.flatMap(f: (T) -> FunList<R>): FunList<R> = fmap(f).flatten()

infix fun <T, R> FunList<T>.fmap(f: (T) -> R): FunList<R> = when (this) {
    is Nil -> Nil
    is Cons -> Cons(f(head), tail.fmap(f))
}

//infix fun <T, R> FunList<T>.fmap(f: (T) -> R): FunList<R> = flatMap { x -> Cons(f(x), Nil) }

fun <T, R> FunList<T>.foldRight(acc: R, f: (T, R) -> R): R = when (this) {
    is Nil -> acc
    is Cons -> f(head, tail.foldRight(acc, f))
}

tailrec fun <T, R> FunList<T>.foldLeft(acc: R, f: (R, T) -> R): R = when (this) {
    is Nil -> acc
    is Cons -> tail.foldLeft(f(acc, head), f)
}

tailrec fun <T> FunList<T>.contains(element: T): Boolean = when (this) {
    is Nil -> false
    is Cons -> if (element == head) true else tail.contains(element)
}

fun <T> FunList<T>.distinct(): FunList<T> =
    foldLeft(Nil as FunList<T>) { acc, x -> if (acc.contains(x)) acc else Cons(x, acc) }

tailrec fun <T> FunList<T>.reverse(acc: FunList<T> = Nil): FunList<T> = when (this) {
    is Nil -> acc
    is Cons -> tail.reverse(Cons(head, acc))
}

fun <T> FunList<T>.filter(acc: FunList<T> = Nil, f: (T) -> Boolean): FunList<T> = when (this) {
    Nil -> acc.reverse()
    is Cons -> if (f(head)) {
        tail.filter(Cons(head, acc), f)
    } else {
        tail.filter(acc, f)
    }
}

fun<T> printFunList(list: FunList<T>) = println(list.toStringByFoldLeft())

private fun <T> FunList<T>.toStringByFoldLeft(): String = "[${foldLeft("") { acc, x -> "$acc, $x" }.drop(2)}]"
```
