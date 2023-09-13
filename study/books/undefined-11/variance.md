# 제네릭 타입과 variance 한정자를 활용하라

class Cup\<T> : variance 한정자가 없으므로 기본적으로 invariant(불공변성)

```kotlin
Cup<Int>, Cup<Number>, Cup<Nothing> 은 어떠한 관련성이 없음

class Cup<out T> : 타입 파라미터를 convariant(공변성)으로 만듦
  A가 B의 서브 타입일 때, Cup<A>가 Cup<B>의 서브타입이라는 의미

Class Cup<in T> : 타입 파라미터를 contravariant(반변성)으로 만듦
  A가 B의 서브 타입일 때, Cup<A>가 Cup<B>의 슈퍼타입
```

함수 타입은 파라미터 유형과 리턴 타입에 따라서 서로 어떤 관계를 갖는다.

```kotlin
(Int) -> Any 타입의 함수는 아래와 같은 함수로도 동작한다.
(Int) -> Number
(Number) -> Any
(Number) -> Number
(Number) -> Int
```

코틀린 함수 타입의 모든 파라미터 타입은 contravariant, 모든 리턴 타입은 covariant\
함수 타입을 사용할 때는 자동으로 variance 한정자가 사용됨

### Variance 한정자의 안정성

자바의 Array는 covariant 속성을 갖기 때문에 문제가 발생할 수 있음

```kotlin
Integer[] numbers = {1,2,3,4};
Object[] objects = numbers;
ojects[2] = "B" //ArrayStoreException 발생
```

#### 코틀린은 public in 한정자 위치에 covariant 타입 파리미터(out 한정자)가 오는 것을 금지한다.

코틀린은 public out 한정자 위치(함수 리턴 타입, 프로퍼티 타입) 에 contravariant 타입 파라미터 (in 한정자)가 오는 것을 금지한다.&#x20;

#### public out 위치는 암묵적으로 업캐스팅을 허용, 하지만 이는 contravariant(in 한정자) 에 맞는 동작이 아님

Box안에 어떤 타입이 들어 있는지 확실하게 알 수 없기때문 만약 프로퍼티를 var 로 선언하면 해당 프로퍼티의 타입은 invariant 인 것으로 보인다.

Variance 한정자의 위치는 크게 두 위치에서 사용할 수 있다.

#### 선언 부분

클래스와 인터페이스 선언에 한정자가 적용\
클래스와 인터페이스가 사용되는 모든 곳에 영향

#### 클래스와 인터페이스를 활용하는 위치

특정한 변수에만 variance 한정자가 적용\
특정 인스턴스에만 적용해야 할 때 이런 코드를 사용

```kotlin
interface Dog
interface Cutie
data class Puppy(val name: String): Dog, Cutie
data class Hound(val name: String): Dog
data class Cat(val name: String): Cutie


fun main() {
    val dogs = mutableListOf<Dog>(Hound("pluto"))

    fillWithPuppies(dogs)
    println(dogs)

    val animals = mutableListOf<Cutie>(Cat("felix"))
    fillWithPuppies(animals)

    println(animals)

}

fun fillWithPuppies(list: MutableList<in Puppy>) {
    list.add(Puppy("jim"))
    list.add(Puppy("amy"))
}
```

#### Variance 한정자를 사용하면 위치가 제한될 수 있다.

MutableList\<out T> 가 있다면, get으로 요소를 추출했을 때 T 타입이 나올 것이지만, set은 Nothing 타입의 아규먼트가 전달될 거라 예상되므로 사용불가능하다.

`모든 타입의 서브타입을 가진 리스트(Nothing 리스트) 가 존재할 가능성이 있기 때문`

MutalbeList\<in T> 가 있다면, get set을 모두 사용할 수 있지만, 전달되는 자료형은 Any?가 된다

`모든 타입의 슈퍼타입을 가진 리스트 (Any 리스트)가 존재할 가능성이 있기 때문`
