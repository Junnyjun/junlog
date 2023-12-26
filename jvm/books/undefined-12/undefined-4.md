# 컬렉션으로 데이터 다루기

## **함수형 List 구현**

순수한 함수형 언어의 리스트는 기본적으로 lazy evaluation이나, \
코틀린이나 스칼라의 리스트는 lazy evaulation 되는 list를 별도로 제공한다.

기본적으로 리스트는 한 개 이상의 Construct로 연결되어 있거나, \
아무 연결이 없는 Nil로 구성되어 있다.

```kotlin
sealed class FunList<out T> {
    object Nil : FunList<Nothing>()
    data class Cons<out T>(val head: T, val tail: FunList<T>) : FunList<T>()
}

// List 생성 예
val list: FunList<Int> = Cons(1, Cons(2, Nil))
```

### **addHead 함수 만들기**

맨 앞에 값을 추가 후 나머지 원본 리스트는 tail : O(1)

```kotlin
fun <T> FunList<T>.addHead(head: T): FunList<T> = FunList.Cons(head, this)
```

### **appendTail 함수 만들기**

```kotlin
tailrec fun <T> FunList<T>.appendTail(value: T, acc: FunList<T> = FunList.Nil): FunList<T> = when (this) {
    FunList.Nil -> FunList.Cons(value, acc).reverse()
    is FunList.Cons -> tail.appendTail(value, acc.addHead(head))
}
```

## **컬렉션 데이터 걸러 내기**

### **명령형 방식**

if, for, while 등으로 컬렉션을 순회하며 필터링

```kotlin
fun imperativeFilter(numList: List<Int>): List<Int> {
    val newList = mutableListOf<Int>()
    for (num in numList) {
        if (num % 2 == 0) {
            newList.add(num)
        }
    }

    return newList
}
```

### **함수형 방식**

고차 함수 사용

```kotlin
fun functionalFilter(numList: List<Int>): List<Int> =
   numList.filter { it % 2 == 0 }
```

### **명령형 방식과 함수형 방식의 성능 비교**

명령형 vs lazy evaluation 사용하지 않은 함수형

* 압도적으로 명령형 방식이 빠름

명령형 vs lazy evaluation 사용한 함수형(Sequence 사용)

* 두 방식은 비슷한 성능

성능에 민감하고, 컬렉션이 무한하거나 매우 클 경우 lazy evauluation \
혹은 명령형 방식으로 처리 해야 함

```kotlin
fun main() {
    println(imperativeWay(listOf(1, 2, 3, 4, 5)))   // 1
    println(functionalWay(listOf(1, 2, 3, 4, 5)))    // 1

    val bigIntList = (1..10000000).toList()

    var start = System.currentTimeMillis()
    imperativeWay(bigIntList)
    println("${System.currentTimeMillis() - start} ms")    // 0 ms

    start = System.currentTimeMillis()
    functionalWay(bigIntList)
    println("${System.currentTimeMillis() - start} ms")    // 2349 ms

    start = System.currentTimeMillis()
    realFunctionalWay(bigIntList)
    println("${System.currentTimeMillis() - start} ms")    // 10 ms
}

private fun imperativeWay(intList: List<Int>): Int {
    for (value in intList) {
        val doubleValue = value * value
        if (doubleValue < 10) {
            return doubleValue
        }
    }

    throw NoSuchElementException("There is no value")
}

private fun functionalWay(intList: List<Int>): Int =
    intList
        .map { n -> n * n }
        .filter { n -> n < 10 }
        .first()

private fun realFunctionalWay(intList: List<Int>): Int =
    intList.asSequence()
        .map { n -> n * n }
        .filter { n -> n < 10 }
        .first()
```

**Lazy evaulation 컬렉션 만들어보기**

입력 매개변수를 람다로 전달 받으며, Cons 생성 시점에 매개 변수가 평가 되지 않음(실제 평가 시점은 그 값이 필요 할 때)

```kotlin
sealed class FunStream<out T> {
    object Nil : FunStream<Nothing>()
    data class Cons<out T>(val head: () -> T, val tail: () -> FunStream<T>) : FunStream<T>() {
        override fun equals(other: Any?): Boolean =
            if (other is Cons<*>) {
                if (head() == other.head()) {
                    tail() == other.tail()
                } else {
                    false
                }
            } else {
                false
            }

        override fun hashCode(): Int {
            var result = head.hashCode()
            result = 31 * result + tail.hashCode()
            return result
        }
    }
}

fun <T> generateFunStream(seed: T, generate: (T) -> T): FunStream<T> =
    FunStream.Cons({ seed }, { generateFunStream(generate(seed), generate) })
```

이러한 컬렉션에 값을 추가 하여도, 즉시 평가 되지 않기 때문에 equals과 같은 비교 함수를 만들어야하며, 비교시 모든 값이 즉시 평가됨

**FunStream으로 무한대 값 만들기**

```kotlin
fun <T> generateFunStream(seed: T, generate: (T) -> T): FunStream<T> =
    FunStream.Cons({ seed }, { generateFunStream(generate(seed), generate) })
```
