# compareTo의 규약을 지켜라

```kotlin
fun main() {
    obj1 > obj2 // obj1.compareTo(obj2) > 0
    obj1 < obj2 // obj1.compareTo(obj2) < 0
    obj1 >= obj2 // obj1.compareTo(obj2) >= 0
    obj1 <= obj2 // obj1.compareTo(obj2) <= 0
}
```

compareTo()는 Comparable\<T> 인터페이스에도 있다. 어떤 객체가 이 인터페이스를 구현하고 있거나 compareTo라는 연산자 메서드를 갖고 있다는 의미는 해당 객체가 어떤 순서를 갖고 있으므로 비교할 수 있다는 것이다. compareTo는 아래처럼 동작해야 한다.

```
비대칭적 동작 : a >= b고 b >= a면 a == b여야 한다. 비교와 동등성 비교에 어떤 관계가 있어야 하고 일관성이 있어야 한다
연속적 동작 : a >= b, b >= c면 a >= c여야 한다. 이런 동작을 하지 못하면 요소 정렬이 무한 반복에 빠질 수 있다
코넥스적 동작 : 두 요소는 어떤 확실한 관계를 갖고 있어야 한다. a >= b 또는 b >= a 중에 적어도 하나는 항상 true여야 한다. 두 요소 사이에 관계가 없으면 퀵 정렬, 삽입 정렬 등의 고전적인 정렬 알고리즘을 쓸 수 없다. 대신 위상 정렬 같은 정렬 알고리즘만 쓸 수 있다
```

#### compareTo를 따로 정의해야 하는가?

코틀린에서 compareTo를 따로 정의해야 하는 상황은 거의 없다. 일반적으로 어떤 프로퍼티 하나를 기반으로 순서를 지정하는 것으로 충분하기 때문이다. sortedBy를 쓰면 원하는 키로 컬렉션을 정렬할 수 있다.

```kotlin
fun main() {
    val names = listOf<User>(/*...*/)
    val sorted = names.sortedBy { it.surname }
}

class User(val name: String, val surname: String)
```

여러 프로퍼티를 기반으로 정렬해야 한다면 sortedWith 함수를 쓰면 된다. 이 함수는 compareBy를 활용해서 comparator를 만들어 사용한다.

```kotlin
val sorted = names.sortedWith(compareBy({ it.surname }, {it.name}))
```

문자열은 알파벳, 숫자 등의 순서가 있다. 따라서 내부적으로 Comparable\<String>을 구현하고 있다. 텍스트는 일반적으로 정렬해야 하는 경우가 많아서 유용하다. 하지만 단점도 있다. 두 문자열이 부등식으로 비교된 코드를 보면 이해하는 데 약간의 시간이 걸린다.

```kotlin
fun main() {
    val names = listOf<User>(/*...*/)
    val sorted = names.sortedWith(User.DISPLAY_ORDER)
}

class User(val name: String, val surname: String) {
    companion object {
        val DISPLAY_ORDER = compareBy(User::surname, User::name)
    }
}
```

&#x20;
