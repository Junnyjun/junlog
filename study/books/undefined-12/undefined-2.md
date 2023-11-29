# 재귀

**함수형 프로그래밍에서 재귀가 갖는 의미**

복잡한 프로그램을 명령형 프로그래밍 방식으로 풀기 위해서는 명제를 간단한 문제로 나누어 접근하는 방식이 좋음, 이러한 방식을 동적 계획법이라 함

* 동적 계획법을 이용한 피보나치 수열 구현
  * 이전 값을 메모리에 기억하면서 루프 반복으로 원하는 항을 구함

```kotlin
fun main() {
    println(fiboDynamic(10, IntArray(100)))    // 55
}

private fun fiboDynamic(n: Int, fibo: IntArray): Int {
    fibo[0] = 0
    fibo[1] = 1

    for (i in 2..n) {
        fibo[i] = fibo[i - 1] + fibo[i - 2]
    }

    return fibo[n]
}
```

* 재귀적 구현
  * 메모리 할당이나 값 변경 없이 스택 메모리와 재귀 호출을 통해 원하는 항을 구함

```kotlin
fun main() {
    println(fiboRecursion(10))    // 55
}

private fun fiboRecursion(n: Int): Int = when (n) {
    0 -> 0
    1 -> 1
    else -> fiboRecursion(n - 2) + fiboRecursion(n - 1)
}
```

함수형 프로그래밍에서는 어떻게 값을 계산할 수 있을지 선언하는 대신 무엇을 선언할지 고민해야 함

반복문(for, while 등)은 구조적으로 어떻게 동작해야 하는지 명령하는 구문인데, 함수형 프로그래밍에서는 루프를 사용하여 해결하던 문제들을 재귀적으로 해결해야하며 모든 반복문은 재귀로 구현 할 수 있음

재귀는 반복문에 비하여 복잡한 알고리즘을 간결하게 표현 할 수 있지만 2가지 문제점을 갖음

* 동적 계획법에 비해 성능이 느림
* 스택 오버플로 가능성이 존재

이같은 문제점을 극복하는 방법이 존재함

**재귀를 설계하는 방법**

재귀가 무한루프에 빠지지 않으려면 재귀에서 빠져 나오는 종료조건(edge condition)이 반드시 한 개 이상 존재해야 하고, 이 재귀적 반복이 진행 될 수록 종료조건에 수렴해야 함

* 종료 조건은 항등값을 이용(empty 상태의 자료구조, 0, 1 등등)
* 함수의 입력 분할은 수학적 귀납법과 동일한 패턴(어떤 구성요소와 나머지 구성요소로 최종 결괏값을 만들기 위한 함수의 동작을 구현)
* 매 호출시에는 종료조건(항등값)에 수렴하도록(가까워지도록) 처리

　

**재귀가 수행되는 흐름 관찰**

1부터 5까지 더하는 프로그램 : 시간 복잡도 O(n^2)

```kotlin
fun main() {
    println(func(5))    // 15
}

fun func(n: Int): Int = when {
    n < 0 -> 0
    else -> n + func(n - 1)
}
```

종료조건 : n이 0보다 작음

매 호출마다 n이 1씩 줄어들도록 설계 -> 종료조건에 수렴되도록

```
1 : func(5) 호출
2 : 5 + func(4)
3 : 5 + (4 + func(3))
3 : 5 + (4 + (3 + func(2)))
4 : 5 + (4 + (3 + (2 + func(1))))
5 : 5 + (4 + (3 + (2 + (1 + func(0))))
6 : 5 + (4 + (3 + (2 + (1 + 0)))
```

종료 조건을 만날 때까지 함수가 반복 호출된 후 거꾸로 뒤에서부터 값이 더해짐

　

**메모이제이션으로 재귀 피보나치 성능 개선하기**

memoization은 어떤 반복 연산을 수행시 이전 계산값을 캐싱하여 이미 계산된 연산을 회피하는 방법이다. 연산 횟수가 줄어, 속도가 개선되므로 동적 계획법의 핵심이 되는 기술임

메모이제이션을 사용한 피보나치 수열 : 시간 복잡도 O(N)

```kotlin
fun main() {
    println(fiboMemoization(6)) // 8
}

private var memo = Array(100) { -1 }

private fun fiboMemoization(n: Int): Int = when {
    n == 0 -> 0
    n == 1 -> 1
    memo[n] != -1 -> memo[n]
    else -> {
        println("fiboMemoization($n)")
        memo[n] = fiboMemoization(n - 2) + fiboMemoization(n - 1)
        memo[n]
    }
}
```

메모이제이션을 이용해 재귀의 효율성을 높이기는 하였으나, 순수한 함수의 요건을 위배하였음(memo 전역 변수 사용) 함수형 프로그래밍에서 메모이제이션을 사용하는 방법은 별도 메모리 캐싱을 사용하는 것이 아닌, 재귀 함수의 매개변수로 메모리 캐싱을 대체 할 수 있음

```kotlin
fun main() {
    println(fiboFunctional(6))  // 8
}

private fun fiboFunctional(n: Int): Int = fiboFP(n, 0, 1)

private tailrec fun fiboFunctional(n: Int, first: Int, second: Int): Int = when (n) {
    0 -> first
    1 -> second
    else -> fiboFP(n - 1, second, first + second)
}
```

　

**꼬리 재귀로 최적화하기**

tailrec은 언어 차원에서 제공하는 키워드로 꼬리 재귀 함수의 경우 컴파일러 최적화가 적용된다.

**꼬리 재귀 최적화란?**

* 꼬리 재귀란 어떤 함수가 직간접적으로 자기 자신을 호출하면서도 그 호출이 마지막 연산인 경우를 말함 마지막 연산인 호출을 꼬리 호출(tail call)이라 함
* 일반적인 재귀는 호출이 반복되며 너무 깊어질 경우 스택 오버플로가 발생 할 수 있으나, 꼬리 호출의 경우 스택 프레임을 컴파일러가 재사용하므로 스택 오버플로를 미연에 방지함
* 재귀가 꼬리 호출인 경우, 재귀의 마지막 결과가 재귀 호출 전체의 결과와 일치함, 컴파일러는 새로운 스택 프레임을 생성하지 않고 현재 스택 프레임에서 함수의 시작점으로 점프하여 재귀 호출 할 수 있음, 이 경우 재귀를 사용했지만 반복문을 사용한 것과 동일

**1.0에 코사인 함수를 값이 불변할 때 까지 수행하는 코드**

*   명령적 프로그래밍 방식

    ```kotlin
    private fun findFixPoint(): Double {
        var x = 1.0
        while (true) {
            val y = Math.cos(x)
            if (x == y) return x
            x = y
        }
    }
    ```
*   꼬리 재귀 방식

    ```kotlin
    private tailrec fun findFixPoint(x: Double = 1.0): Double = if (x == Math.cos(x)) x else findFixPoint(Math.cos(x))
    ```

　

**머리 재귀 vs 꼬리 재귀**

재귀 호출 위치가 함수 가장 먼저 vs 가장 나중에 따라 머리 재귀, 꼬리재귀라 하며 꼬리 재귀는 스택 프레임을 재사용 할 수 있어 컴파일러 최적화가 가능하다.

두 방식은 기능적으로 동일하지만, 언어 차원에서 꼬리재귀 컴파일러 최적화를 지원할 경우 가능하면 꼬리 재귀로 재귀를 구현한다.

```kotlin
private fun tailRecursion(n: Int): Int = when (n) {
    0 -> 0
    else -> tailRecursion(n - 1)
}

private fun headRecursion(n: Int): Int = when {
    n > 0 -> headRecursion(n - 1)
    else -> 0
}
```
