# Covariance

코틀린의 제네릭을 다루다 보면 '공변성(Covariance)', '반공변성(Contravariance)', '불변성(Invariance)'이라는 용어를 마주하게 됩니다. 처음 들으면 어렵게 느껴질 수 있지만, 이 개념들은 타입 안전성을 확보하고 유연한 코드를 작성하는 데 매우 중요합니다.&#x20;

***

#### 타입 시스템의 기본 개념: 왜 공변성, 반공변성이 필요할까?

자바와 코틀린 같은 객체지향 언어에서는 \*\*다형성(Polymorphism)\*\*이 핵심 원리 중 하나입니다. 예를 들어, `Dog` 클래스가 `Animal` 클래스를 상속받는다고 해봅시다. 이때 `Dog` 타입의 객체는 `Animal` 타입의 변수에 할당할 수 있습니다.

```
open class Animal
class Dog : Animal()

fun main() {
    val myAnimal: Animal = Dog() // 문제 없음!
}
```

이것이 바로 객체지향의 자연스러운 동작입니다. `Dog`는 `Animal`의 일종이므로, `Animal`이 필요한 곳에 `Dog`를 사용할 수 있습니다.

그런데 제네릭 타입은 어떨까요? `List<Dog>`를 `List<Animal>` 타입의 변수에 할당할 수 있을까요?

```
val dogs: List<Dog> = listOf(Dog(), Dog())
val animals: List<Animal> = dogs // 컴파일 에러!
```

코틀린은 이 코드를 허용하지 않습니다. 왜냐하면, `List<Dog>`는 `List<Animal>`의 하위 타입이 아니기 때문입니다. 만약 이 할당이 허용된다면, 다음과 같은 문제가 발생할 수 있습니다.

```
fun addCat(list: MutableList<Animal>) {
    list.add(Cat()) // Cat은 Animal의 하위 클래스라고 가정
}

val dogs: MutableList<Dog> = mutableListOf(Dog())
addCat(dogs) // 만약 허용된다면, dogs 리스트에 Cat이 들어가는 문제가 발생!
val myDog: Dog = dogs[0] // 런타임 오류 가능성 (Cat을 Dog으로 캐스팅)
```

이처럼 제네릭 타입은 기본적으로 \*\*타입 불변성(Invariance)\*\*을 가집니다. 즉, `List<T>` 타입은 `T`가 아무리 하위/상위 클래스 관계라 해도 서로 다른 타입으로 취급됩니다. 이러한 문제를 해결하고 더 유연한 코드를 작성하기 위해 공변성과 반공변성이 필요합니다.

***

#### 1. 불변성(Invariance)

불변성은 제네릭 타입의 기본 동작입니다. `A`가 `B`의 하위 타입이라 하더라도 `Container<A>`와 `Container<B>`는 서로 아무런 관계가 없습니다.

* 키워드: 코틀린에서는 별도의 키워드가 필요 없습니다. `class Box<T> { ... }`처럼 선언된 제네릭 클래스는 기본적으로 불변성을 가집니다
* 특징: 가장 안전하지만, 타입 간의 관계를 전혀 허용하지 않아 유연성이 떨어집니다.

```
// 기본적으로 Invariant (불변성)
class Box<T>(val item: T)

val catBox: Box<Cat> = Box(Cat())
val animalBox: Box<Animal> = catBox // 컴파일 에러!
```

***

#### 2. 공변성(Covariance)

공변성은 `A`가 `B`의 하위 타입일 때, `Container<A>`를 `Container<B>`로 취급할 수 있도록 하는 성질입니다. 즉, 타입 계층 관계가 컨테이너에도 그대로 적용됩니다.

* 키워드: `out`
* 의미: '생산자(Producer)' 역할을 하는 타입에 사용됩니다. 즉, T 타입의 객체를 '생산'하거나 '반환'만 할 수 있습니다. `out` 키워드가 붙은 타입 파라미터 `T`는 함수 반환 타입(`out`)으로만 사용될 수 있고, 파라미터 타입(`in`)으로는 사용될 수 없습니다.

```
// out 키워드로 T를 공변성 타입으로 만듦
interface Producer<out T> {
    fun produce(): T // T를 반환하는 함수 (생산자)
    // fun consume(item: T) // T를 파라미터로 받는 함수는 불가능!
}

open class Fruit
class Apple : Fruit()

val appleProducer: Producer<Apple> = object : Producer<Apple> {
    override fun produce(): Apple = Apple()
}

// AppleProducer는 FruitProducer로 취급될 수 있음 (공변성)
val fruitProducer: Producer<Fruit> = appleProducer
val fruit: Fruit = fruitProducer.produce() // 안전함!
```

`out T`는 `T`를 소비하는(함수 인자로 사용하는) 행위를 막음으로써 타입 안전성을 보장합니다. 만약 `consume(item: T)`가 허용된다면, `fruitProducer`에 `Banana`를 전달하는 일이 가능해지고, 실제로는 `AppleProducer`가 `Banana`를 처리해야 하는 모순적인 상황이 발생합니다.

***

#### 3. 반공변성(Contravariance)

반공변성은 공변성과 정반대입니다. `A`가 `B`의 하위 타입일 때, `Container<B>`를 `Container<A>`로 취급할 수 있도록 하는 성질입니다. 타입 계층 관계가 거꾸로 적용됩니다.

* 키워드: `in`
* 의미: '소비자(Consumer)' 역할을 하는 타입에 사용됩니다. 즉, T 타입의 객체를 '소비'하거나 '받기'만 할 수 있습니다. `in` 키워드가 붙은 타입 파라미터 `T`는 함수 파라미터 타입(`in`)으로만 사용될 수 있고, 반환 타입(`out`)으로는 사용될 수 없습니다.

```
// in 키워드로 T를 반공변성 타입으로 만듦
interface Consumer<in T> {
    fun consume(item: T) // T를 파라미터로 받는 함수 (소비자)
    // fun produce(): T // T를 반환하는 함수는 불가능!
}

val animalConsumer: Consumer<Animal> = object : Consumer<Animal> {
    override fun consume(item: Animal) {
        println("동물을 소비합니다.")
    }
}

// AnimalConsumer는 DogConsumer로 취급될 수 있음 (반공변성)
val dogConsumer: Consumer<Dog> = animalConsumer
dogConsumer.consume(Dog()) // 안전함!
```

`in T`는 `T`를 반환하는(생산하는) 행위를 막음으로써 타입 안전성을 보장합니다. `Animal`을 소비할 수 있는 객체는 `Dog`도 당연히 소비할 수 있습니다. 따라서 `Consumer<Animal>`을 `Consumer<Dog>` 자리에 사용하는 것은 논리적으로 타당하며 안전합니다.

***

#### 정리 및 한눈에 보기

| 성질   | 키워드   | 역할  | 설명                                                        | 예시                                    |
| ---- | ----- | --- | --------------------------------------------------------- | ------------------------------------- |
| 불변성  | 없음    | -   | `Container<A>`와 `Container<B>`는 무관.                       | `Box<Cat>`는 `Box<Animal>`과 다름.        |
| 공변성  | `out` | 생산자 | `A`가 `B`의 하위 타입이면, `Container<A>`를 `Container<B>`로 사용 가능. | `Producer<Apple>`는 `Producer<Fruit>`. |
| 반공변성 | `in`  | 소비자 | `A`가 `B`의 하위 타입이면, `Container<B>`를 `Container<A>`로 사용 가능. | `Consumer<Animal>`는 `Consumer<Dog>`.  |

가장 간단하게 기억하는 방법은 이렇습니다.

* `out` (`출력`) -> 타입 T를 \*\*반환(return)\*\*만 할 수 있는 '생산자' 역할. `out`은 `T`의 상위 타입으로 변환될 수 있습니다.
* `in` (`입력`) -> 타입 T를 \*\*인자(argument)\*\*로만 받을 수 있는 '소비자' 역할. `in`은 `T`의 하위 타입으로 변환될 수 있습니다.
