# 모노이드

## **모노이드란?**

모노이드는 연관 바이너리 함수와 항등값을 가진 대수적 타입으로 정의

**연관 바이너리 함수** (곱셈, 덧셈, 리스트를 합치는 함수 대상) 두 개의 매개변수와 리턴값의 타입이 동일한 바이너리 함수 곱셈, 덧셈, 리스트 결합은 결합 법칙을 만족함

모노이드는 곱셈이나, 덧셈, 리스트 결합하기의 예와 같은 몇 가지 동작을 일반화 할 수 있고 이러한 동작은 함수형 프로그래밍에서 다루는 대부분의 대수적 타입에서 사용된다.

　

**모노이드 타입 클래스 구현**

**간단한 모노이드 타입 클래스**

memty 함수 : 항등원 리턴 mappend 함수 : 바이너리 함수 리턴

```kotlin
interface Monoid<T> {

    fun mempty(): T

    fun mappend(m1: T, m2: T): T
}
```

두 함수는 모노이드를 반환하는 것이 아니라, 모노이드가 가진 값의 타입을 반환하며 코드 체이닝을 할 수 없음 체이닝을 가능하도록 처리 하려면 코드가 복잡해지며 대수 타입 이해 학습을 위해 위와 같은 방식으로 사용

**덧셈 타입 모노이드**

```kotlin
fun main() {
    val x = 1
    val y = 2
    val z = 3

    SumMonoid().run {
        println(mappend(mempty(), x) == x)  // true
        println(mappend(x, mempty()) == x)  // true
        println(mappend(mappend(x, y), z) == mappend(x, mappend(y, z)))  // true
    }

    println(SumMonoid().mconcat(funListOf(1, 2, 3, 4, 5)))      // 15
}

class SumMonoid : Monoid<Int> {

    override fun mempty(): Int = 0 // 덧셈의 항등원은 0

    override fun mappend(m1: Int, m2: Int): Int = m1 + m2
}
```

**곱셈 타입 모노이드**

```kotlin
fun main() {
    val x = 1
    val y = 2
    val z = 3

    ProductMonoid().run {
        println(mappend(mempty(), x) == x)  // true
        println(mappend(x, mempty()) == x)  // true
        println(mappend(mappend(x, y), z) == mappend(x, mappend(y, z)))  // true
    }

    println(ProductMonoid().mconcat(funListOf(1, 2, 3, 4, 5)))      // 120
}

class ProductMonoid : Monoid<Int> {

    override fun mempty(): Int = 1

    override fun mappend(m1: Int, m2: Int): Int = m1 * m2
}
```

**모노이드의 법칙**

모노이드는 항등값과 바이너리 함수를 갖으며, 항등 법칙과 결합법칙을 만족해야 함

```kotlin
mppend(menpty(), x) = x // 항등 법칙 만족
mppend(x, menpty()) = x // 결합 법칙 만족
mappend(mappend(x, y), z) = mappend(x, mappend(y, z))
```

**mconcat 함수 만들기**

입력 받은 리스트 요소를 하나의 값으로 출력하는 함수

```kotlin
fun <T> Monoid<T>.mconcat(list: FunList<T>): T = list.foldRight(mempty(), ::mappend)

fun main(args: Array<String>){
    prinln(ProductMonoid().mconcat(funListOf(1, 2, 3, 4, 5))) // 120
    prinln(SumMonoid().mconcat(funListOf(1, 2, 3, 4, 5))) // 15
}
```

**메이비 모노이드 만들기**

메이비 모노이드는 실패 할 수 있는 연산의 결과를 모노이드로 처리 할 때 사용된다.

```kotlin
fun main() {
    val x = Just(1)
    val y = Just(2)
    val z = Just(3)

    MaybeMonoid.monoid(ProductMonoid()).run {
        println(mappend(mempty(), x) == x)  // true
        println(mappend(x, mempty()) == x)  // true
        println(mappend(mappend(x, y), z) == mappend(x, mappend(y, z)))  // true
    }

    MaybeMonoid.monoid(SumMonoid()).run {
        println(mappend(mempty(), x) == x)  // true
        println(mappend(x, mempty()) == x)  // true
        println(mappend(mappend(x, y), z) == mappend(x, mappend(y, z)))  // true
    }
}

object MaybeMonoid {

    fun <T> monoid(inValue: Monoid<T>) = object : Monoid<Maybe<T>> {

        override fun mempty(): Maybe<T> = Nothing

        override fun mappend(m1: Maybe<T>, m2: Maybe<T>): Maybe<T> = when {
            m1 is Nothing -> m2
            m2 is Nothing -> m1
            m1 is Just && m2 is Just -> Just(inValue.mappend(m1.value, m2.value))
            else -> Nothing
        }
    }
}

sealed class Maybe<out T>
data class Just<T>(val value: T) : Maybe<T>()
object Nothing : Maybe<kotlin.Nothing>()
```

　

**모노이드를 통한 폴더블 이진 트리**

```kotlin
fun main() {
    val tree = Node(1,
            Node(2,
                    Node(3), Node(4)),
            Node(5,
                    Node(6), Node(7)))

    println(tree.foldLeft(0) { a, b -> a + b })    // 28
    println(tree.foldLeft(1) { a, b -> a * b })    // 5040

    println(tree.foldMap({ a -> a * 2 }, SumMonoid()))  // 56
    println(tree.foldMap({ a -> a + 1 }, ProductMonoid()))  // 40320
}

sealed class BinaryTree<out A> : Foldable<A> {

    override fun <B> foldLeft(acc: B, f: (B, A) -> B): B = when (this) {
        is EmptyTree -> acc
        is Node -> {
            val leftAcc = leftTree.foldLeft(acc, f)
            val rootAcc = f(leftAcc, value)
            rightTree.foldLeft(rootAcc, f)
        }
    }
}

data class Node<A>(val value: A, val leftTree: BinaryTree<A> = EmptyTree, val rightTree: BinaryTree<A> = EmptyTree) : BinaryTree<A>()
object EmptyTree : BinaryTree<kotlin.Nothing>()
```
