# 불필요한 객체 생성을 피하라

#### 불필요한 객체 생성을 피하는 것은 최적화의 관점에서 좋다.

JVM에서는 하나의 가상 머신에서 동일한 문자열을 처리하는 코드가 여러개 있다면, \
기존의 문자열을 재사용

Interger와 Long 처럼 박스화한 기본 자료형도 작은 경우에 재사용 EX ) Int 캐싱\
nullable 타입은 int 자료형 대신 Integer 자료형을 사용하게 강제

```
Int를 사용하면 일반적으로 기본 자료형 int 로 컴파일
하지만 nullable로 만들거나, 타입 아규먼트로 사용할 경우에는 Interger로 컴파일된다.
```

어떠한 객체를 wrap 할 경우 크게 세 가지의 비용이 발생한다.

```
객체는 더 많은 용량을 차지
요소가 캡슐화되어 있다면, 접근에 추가적인 함수 호출이 필요
객체는 생성되어야하고, 메모리 영역에 할당되고, 이에 대한 레퍼런스를 만드는 등의 작업이 필요
```

### **객체 선언**

객체를 재사용하는 간단한 방법은 객체 선언을 사용하는 것(싱글톤)

```kotlin
sealed class LinkedList<T>

class Node<T>(
        val head: T,
        val tail: LinkedList<T>
): LinkedList<T>()

class Empty<T>: LinkedList<T>()

fun main() {
    val list: LinkedList<Int> =
            Node(1, Node(2, Node(3, Empty())))
    val list2: LinkedList<String> =
            Node("a", Node("b", Empty()))
}
```

위 예제의 문제점은 매번 Empty 인스턴스를 새로 만들어야 한다는 점

Empty 인스턴스를 미리 하나만 만들고, 다른 리스트에서 활용할 수 있게 한다하여도 제네릭 타입이 일치하지 않아서 문제가 될 수 있다

```kotlin
sealed class LinkedList<out T>

class Node<T>(
        val head: T,
        val tail: LinkedList<T>
): LinkedList<T>()

object EmptyWithNothing: LinkedList<Nothing>()

fun main() {
    val list1: LinkedList<Int> =
            Node(1, Node(2, EmptyWithNothing))

    val list2: LinkedList<String> =
            Node("a", EmptyWithNothing)
}
```

빈 리스트는 다른 모든 타입의 서브타입이어야 함.\
따라서 Nothing 리스트를 만들어서 사용하면 해결

Nothing은 모든 타입의 서브타입이므로 리스트가 covariant(out 한정자)면, LinkedList\<Nothing>은 모든 LinkedList의 서브타입이 됨

이러한 방식은 immutable sealed 클래스를 정의할 때 자주 사용

### **캐시를 활용하는 팩토리 함수**

팩토리 함수는 캐시를 가질 수 있다, 따라서 팩토리 함수는 항상 같은 객체를 리턴하게 만들 수도 있다

코루틴의 Dispatchers.Default 는 쓰레드 풀을 가지고 있으며, 어떤 처리를 시작하라고 명령하면 사용하고 있지 않은 쓰레드 하나를 사용해 명령을 수행 => 데이터베이스도 비슷한 형태로 커넥션 풀 사용

객체 생성이 무겁거나, 동시에 여러 mutable 객체를 사용해야 하는 경우에는 객체 풀을 사용하는 것이 도움이 됨

#### Parameterized 팩토리 메소드도 캐싱을 활용할 수 있음

```kotlin
class ParameterizedFactory {
    private val connections = mutableMapOf<String, String>()
    
    fun getConnection(host: String) =
            connections.getOrPut(host) { "createConnection" }
}
```

모든 순수 함수는 캐싱을 활용할 수 있음. 이를 메모이제이션(memoization)이라고 부름

하지만 캐시는 더 많은 메모리를 사용한다는 단점이 있음. \
&#x20; \=> 메모리 문제로 크래시가 생긴다면 메모리를 해제해 주면 된다

#### SoftReference

SoftReference는 가비지 컬렉터가 값을 정리할 수도 있고, 정리하지 않을 수도 있음

메모리가 부족해서 추가로 필요한 경우에만 정리

캐시를 만들 때는 SoftReference를 사용하는 것이 좋다.

#### WeakReference

WeakReference는 가비지 컬렉터가 값을 정리하는 것을 막지 않는다.

따라서 다른 레퍼런스가 이를 사용하지 않으면 곧바로 제거

### **무거운 객체를 외부 스코프로 보내기**

컬렉션 처리에서 이루어지는 무거운 연산은 컬렉션 처리 함수 내부에서 외부로 빼는 것이 좋다

```kotlin
fun <T: Comparable<T>> Iterable<T>.countMax(): Int = 
	count { it == this.max() }
```

위 코드는 매 반복마다 max 값을 확인하므로 성능에 좋지 않기에 이를 외부로 빼야한다

```kotlin
fun <T: Comparable<T>> Iterable<T>.countMax(): Int {
	val max = this.max()
	return count { it == max }
}
```

**지연 초기화**

만약 A 라는 클래스에 B,C,D 라는 무거운 인스턴스가 필요하다고 가정하면, A를 생성하는 과정이 굉장히 무거워질 것.

내부에 있는 인스턴스들을 지연 초기화 하면, A라는 객체를 생성하는 과정을 가볍게 만들 수 있다.

```kotlin
class A {
    val b by lazy { B() }
    val c by lazy { C() }
    val d by lazy { D() }
}
```

**기본 자료형 사용하기**

기본적인 요소를 나타내기 위한 특별한 기본 내장 자료형

nullable 타입을 연산할 때나 타입을 제네릭으로 사용할 때 기본 자료형을 wrap 한 자료형이 사용\
굉장히 큰 컬렉션을 처리할 때 기본 자료형과 wrap한 자료형의 성능차이가 크다

