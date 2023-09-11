# 예외를 활용해 코드에 제한을 걸어라

확실하게 어떤 형태로 동작해야 하는 코드가 있다면 예외를 활용해 제한을 걸어주는 것이 좋은데 코틀린에서는 코드의 동작에 제한을 걸 때 다음과 같은 방법을 사용할 수 있음

```
require 블록 : 아규먼트 제한
check 블록 : 상태와 관련된 동작 제한
assert 블록 : 어떤 것이 true인지 확인 가능 그러나 테스트 모드에서만 동작
return 도는 throw와 함께 활용하는 Elvis 연산자
```

EX )

```kotlin
class Stack<T>(
    var collection: List<T>
) {
    private val size = 3
    private val isOpen = false

    fun pop(num: Int = 1): List<T> {
        require(num <= size) {
            "Cannot remove more elements than current size"
        }
        check(isOpen) { "Cannot pop from closed stack" }
        val ret = collection.take(num)
        collection = collection.drop(num)
        assert(ret.size == num)
        return ret
    }
}
```

위 코드의 장점

```
제한을 걸면 문서를 읽지 않은 개발자도 문제 확인 가능
문제가 있을 경우에 함수가 예상하지 못한 동작을 하지 않고 throw함
  예상하지 못한 동작을 하는 것보단 throw 하는 것이 훨씬 안정적이고 관리하기 좋음
코드가 어느 정도 자체적으로 검사가 됨
스마트 캐스트 기능을 활용할 수 있게 되므로, 캐스트를 적게할 수 있음
```

#### 아규먼트

함수 정의할 때 타입 시스템을 활용해서 아규먼트에 제한을 거는 코드를 많이 사용

```kotlin
fun factorial(n: Int): Long {
    require(n >= 0)
    return if (n <= 1) 1 else factorial(n - 1) * n
}

fun findClusters(points: List<Point>): List<Cluster> {
    require(points.isNotEmpty())
    //...
    return points.map { Cluster() }
}

fun sendEmail(user: User, message: String) {
    requireNotNull(user.email)
    require(isValildEmail(user.email))
}

fun isValildEmail(email: String): Boolean {
    // ... 
    return false
}
```

이와 같은 형태의 입력 유효성 검사 코드는 함수의 가장 앞부분에 배치되므로 읽는 사람도 확인하기 쉬움

require 함수는 조건을 만족시키지 못할 때 무조건적으로 IllegalArgumentException 을 발생시키므로 제한을 무시할 수 없음

추가로 람다를 활용해 메시지 정의할 수 있음

#### 상태

어떤 구체적인 조건을 만족할 때만 함수를 사용할 수 있게 해야할 때가 있는데 이런 경우 check 함수를 통해 상태에 제한을 검

```kotlin
fun speak(text: String) {
    check(isInitialized)
    // ...
}
```

check 함수는 require과 비슷하지만 지정된 예측을 만족하지 못할 때 IllegalStateException 을 throw함

일반적으로 함수 전체에 대한 어떤 에측이 있을대는 require 블록 뒤에 배치

#### Assert 계열 함수 사용

```
fun pop(num: Int = 1): List<T> {
    assert(ret.size == num)
    return ret
}
```

Assert 계열의 함수는 코드를 자체 점검

특정 상황이 아닌 모든 상황에 대한 테스트 할 수 있음

실행 시점에 정확하게 어떻게 되는지 확인할 수 있음

#### nullability와 스마트 캐스팅

코틀린에서 require와 check 블록으로 어떤 조건을 확인해서 true가 나왔다면 해당 조건 이후로도 true일 것이라고 가정해 이를 활용해 타입 비교를 했을시 스마트 캐스트가 작동

뿐만 아니라 nullability 목적으로 오른쪽에 throw 또는 return을 두고 Elvis 연산자를 활용하는 경우도 많음

```kotlin
fun changeDress(person: Person) {
   require(person.outfit is Dress)
   val dress: Dress = person.outfit
   //...
}
```

nullability

```kotlin
fun sendEmail(person: Person, text: String) {
    val email: String = person.email ?: return
}
```

#### 정리

코틀린에서 코드에 제한을 걸때의 장점

```
제한을 훨씬 더 쉽게 확인할 수 있음
애플리케이션을 더 안정적으로 지킬 수 있음
코드를 잘못 쓰는 상황을 막을 수 있음
스마트 캐스팅 활용할 수 있음
```

코틀린에서 제공하는 제한 매커니즘

```
require 블록 : 아규먼트와 관련된 에측을 정의할 때 사용하는 범용적인 방법
check 블록 : 상태와 관련된 에측을 정의할 때 사용하는 범용적인 방법
assert 블록 : 테스트 모드에서 테스트할 때 사용하는 범용적인 방법
return과 throw 와 함께 Elvis 연산자 사용
```
