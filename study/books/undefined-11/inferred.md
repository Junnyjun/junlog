# inferred 타입으로 리턴하지 말라

코틀린 타입 추론을 사용할 때는 몇 가지 위험한 부분이 있는데, 이러한 위험한 부분을 피하려면 할당 시에 inferred 타입은 정확하게 오른쪽에 있는 피연산에 맞게 설정된다는 것을 기억해야함

절대 수퍼 클래스 또는 인터페이스로는 설정되지 않음

```kotlin
open class Animal
class Zebra: Animal()

fun main() {
    var animal = Zebra()
    animal = Animal()
}
```

명시적으로 지정

```kotlin
open class Animal
class Zebra: Animal()

fun main() {
    var animal: Animal = Zebra()
    animal = Animal()
}
```

그러나 직접 라이브러리를 조작할 수 없는 경우에는 이런 문제를 간단하게 해결할 수 없고 이러한 경우에서 inferred 타입을 노출하면 위험한 일이 발생할 수 있음

```kotlin
val DEFAULT_CAR: Car = Flat126P()

interface CarFactory {
    fun produce() = DEFAULT_CAR 
}
```

위와 같이 CarFactory 인터페이스에서 DEFAULT\_CAR를 리턴하는 메소드가 있다고 가정

DEFAULT\_CAR 는 Car로 명시적으로 지정되어 있어서 메소드 리턴 타입을 제거

그러나 이후 다른 사람이 코드를 보다가 DEFAULT\_CAR 는 타입 추론에 의해 자동으로 타입이 지정될 것이므로 명시적으로 지정한 코드를 제거 하게 된다면 CarFactory는 Flat126P 이외의 자동차를 생산할 수 없게 됨

만약 인터페이스를 우리가 직접 만들었다면 문제를 쉽게 찾을 수 있지만 외부 API라면 쉽게 해결할 수 없기 때문에 리턴 타입을 명시적으로 지정해주는게 좋음

#### 정리

타입을 확실하게 지정해야 하는 경우에는 명시적으로 타입을 지정

안전을 위해 외부 API를 만들 때는 반드시 타입을 지정하고 이렇게 지정한 타입을 특별한 이유와 확실한 확인 없이 제거하지 않는 것이 좋음

inferred 타입은 프로젝트가 진전될 때, 제헌이 너무 많아지거나 예측하지 못하는 결과를 낼 수 있음
