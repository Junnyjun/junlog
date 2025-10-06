# Type Bounds

타입 경계는 제네릭 타입 파라미터 `T`가 가질 수 있는 타입을 제한하는 기능입니다. 제네릭 타입 파라미터가 특정 클래스나 인터페이스를 상속하거나 구현하도록 강제하여, 함수나 클래스 내에서 해당 타입이 가진 기능을 안전하게 사용할 수 있도록 보장합니다.

**1. 상한 경계 (Upper Bound)**

가장 일반적으로 사용되는 경계 설정입니다. 타입 파라미터 `T`가 특정 타입 이하의 범위만 허용하도록 제한합니다.

* 키워드: `:` (콜론)
* 의미: `T`는 지정된 타입이나 그 하위 타입이어야 합니다.

Kotlin

```
// T는 Number 클래스나 그 하위 클래스(Int, Double 등)여야 한다.
fun <T : Number> sumOfList(list: List<T>): Double {
    // T가 Number의 하위 타입이므로, Double 값을 반환하는 toDouble() 메서드를 안전하게 사용할 수 있다.
    return list.sumOf { it.toDouble() }
}

fun main() {
    val integers = listOf(1, 2, 3)
    sumOfList(integers) // 허용: Int는 Number의 하위 타입

    // val strings = listOf("a", "b")
    // sumOfList(strings) // 컴파일 에러! String은 Number의 하위 타입이 아님
}
```

참고: 타입 경계를 명시하지 않으면, 기본적으로 \*\*`Any?`\*\*가 경계로 설정됩니다.

**2. 다중 경계 (Multiple Bounds)**

코틀린은 하나의 클래스에 대해 하나의 클래스와 여러 개의 인터페이스를 경계로 설정할 수 있습니다. 이때는 `where` 절을 사용합니다.

* 키워드: `where`
* 의미: `T`는 지정된 모든 조건을 만족해야 합니다.

Kotlin

```
// T는 Animal 클래스를 상속하는 동시에, Serializable 인터페이스도 구현해야 한다.
fun <T> processData(data: T) 
    where T : Animal,
          T : java.io.Serializable {
    
    // Animal의 기능을 사용하고
    data.makeSound()
    
    // Serializable 관련 처리도 가능
    // ...
}

open class Animal { fun makeSound() = println("Sound") }
class Dog : Animal(), java.io.Serializable 

fun main() {
    processData(Dog()) // 허용
    // processData(Animal()) // 에러! Serializable을 구현하지 않음
}
```

#### 왜 타입 경계를 사용해야 할까?

타입 경계를 사용하면 제네릭 코드의 타입 안정성과 유용성이 동시에 향상됩니다.

1. 안전성: 컴파일러가 `T` 타입의 객체가 특정 메서드(예: `sumOfList`의 `toDouble()`)를 가지고 있음을 확신할 수 있게 됩니다.
2. 가독성: 이 함수가 어떤 종류의 타입에 대해서만 작동하는지 명확하게 알려줍니다.

타입 경계는 공변성, 반공변성과 함께 코틀린 제네릭 시스템을 견고하게 만드는 중요한 축 중 하나입니다.
