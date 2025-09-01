# reified

코틀린에서  키워드는 타입 소거(Type Erasure) 문제를 해결하기 위해 사용됩니다. 기본적으로 JVM은 컴파일 시간에 제네릭 타입 정보를 지워버립니다. 이 때문에 런타임에 `T`가 어떤 타입인지 알 수 없게 됩니다. `reified`는 이 타입 정보를 런타임에도 유지시켜 줍니다.

***

#### 타입 소거(Type Erasure)의 한계

자바와 코틀린에서 제네릭을 사용하는 주된 이유는 컴파일 시간에 타입 안전성을 확보하기 위함입니다. 하지만 JVM은 클래스 파일을 로드할 때 제네릭 타입 정보를 지워버리므로, 런타임에는 제네릭 타입에 대한 정보를 얻을 수 없습니다.

다음 코드를 보면 이 문제를 명확히 알 수 있습니다.

```
fun <T> isInstanceOf(item: Any): Boolean {
    // 런타임에 T의 타입 정보가 소거되어 is T를 사용할 수 없음
    return item is T // ⚠️ 컴파일 에러!
}
```

위 코드는 `T`가 `String`인지 `Int`인지 런타임에 알 수 없어 컴파일 에러가 발생합니다.

***

#### `reified`의 역할과 사용법

`reified`는 \*\*인라인 함수(inline function)\*\*의 타입 파라미터에만 사용할 수 있습니다. `reified` 키워드를 사용하면 컴파일러가 제네릭 타입 파라미터 `T`를 해당 타입으로 \*\*"실체화(reify)"\*\*해줍니다. 즉, 함수가 호출될 때 타입 `T`가 \*\*실제 타입(e.g., String, Int)\*\*으로 대체됩니다.

`reified`를 사용하여 위 코드를 수정하면 다음과 같습니다.

```
// reified 키워드로 T의 타입 정보를 유지
inline fun <reified T> isInstanceOf(item: Any): Boolean {
    return item is T
}

fun main() {
    val myString = "Hello"
    val myNumber = 123

    // 런타임에도 타입 T가 String, Int로 유지됨
    println(isInstanceOf<String>(myString)) // 출력: true
    println(isInstanceOf<Int>(myNumber))     // 출력: true
    println(isInstanceOf<String>(myNumber)) // 출력: false
}
```

이처럼 `reified`는 런타임에도 제네릭 타입을 활용해야 하는 상황에서 매우 강력한 도구가 됩니다.

***

#### `reified`의 활용 사례

`reified`는 특정 타입의 객체를 필터링하거나, 클래스 인스턴스를 동적으로 생성할 때 유용하게 쓰입니다.

**1. 타입별 리스트 필터링**

특정 타입의 요소만 걸러내고 싶을 때 `reified`를 사용하면 코드가 훨씬 간결해집니다.

```
inline fun <reified T> List<Any>.filterIsInstance(): List<T> {
    val result = mutableListOf<T>()
    for (item in this) {
        if (item is T) {
            result.add(item)
        }
    }
    return result
}

fun main() {
    val items = listOf("apple", 1, "banana", 2.5, "cherry")

    val strings = items.filterIsInstance<String>()
    println(strings) // 출력: [apple, banana, cherry]

    val doubles = items.filterIsInstance<Double>()
    println(doubles) // 출력: [2.5]
}
```

**2. 액티비티 시작**

안드로이드 개발에서 `startActivity`를 호출할 때 `reified`를 사용하면 클래스를 직접 넘기지 않아도 됩니다.

```
// Context 확장 함수
inline fun <reified T : Activity> Context.startActivity() {
    val intent = Intent(this, T::class.java)
    startActivity(intent)
}

// 사용 예시
// context.startActivity<MainActivity>()
```

`reified`는 코틀린의 타입 소거 한계를 극복하는 키워드로, 특히 인라인 함수와 함께 사용되어 런타임에도 제네릭 타입 정보를 유지할 수 있게 해줍니다. 이를 통해 더 안전하고 유연한 코드를 작성할 수 있으며, 특히 리플렉션과 같이 런타임 타입 정보가 필수적인 상황에서 매우 유용하게 활용됩니다. `reified`를 사용하면 코드가 더 간결해지고 가독성이 높아지는 장점도 있습니다.
