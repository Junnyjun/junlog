# associate

`associate` 함수는 Kotlin의 컬렉션 확장 함수 중 하나로, 리스트(List)나 다른 Iterable 객체를 `Map`으로 변환할 때 사용되는 매우 유용한 함수입니다. 이 함수는 리스트의 각 요소에 대해 키(Key)와 값(Value)을 생성하여 새로운 맵을 만들어 줍니다.

#### `associate` 함수의 기본적인 동작 구조

`associate` 함수는 하나의 람다(lambda) 함수를 인자로 받습니다. 이 람다 함수는 다음과 같은 역할을 수행합니다.

1. Iterable의 각 요소를 하나씩 순회합니다.
2. 각 요소에 대해 `Pair` 객체를 반환합니다. 이 `Pair` 객체는 `key`와 `value`로 이루어져 있습니다. 예를 들어, `item.id to item.name`과 같이 `to` 연산자를 사용하여 `Pair`를 생성할 수 있습니다.
3. `associate` 함수는 이 `Pair`들을 모아서 새로운 `Map<Key, Value>`를 생성합니다.

만약 동일한 키가 여러 번 생성된다면, 마지막에 생성된 키-값 쌍이 최종 맵에 포함됩니다. 이 점은 `associate` 함수를 사용할 때 특히 주의해야 할 부분입니다.

기본적인 형태: `fun <T, K, V> Iterable<T>.associate(transform: (T) -> Pair<K, V>): Map<K, V>`

* `Iterable<T>`: `associate` 함수를 호출하는 원본 컬렉션입니다. `T`는 컬렉션 요소의 타입입니다.
* `transform: (T) -> Pair<K, V>`: 각 요소 `T`를 받아서 `Pair<K, V>`를 반환하는 람다 함수입니다. `K`는 맵의 키 타입, `V`는 맵의 값 타입입니다.
* `Map<K, V>`: 최종적으로 반환되는 맵 객체입니다.

#### `associate` 함수의 실제 동작

다음은 `associate` 함수가 어떻게 동작하는지 보여주는 간단한 예제입니다. `User`라는 데이터 클래스가 있고, 이 리스트를 `id`를 키로 하는 맵으로 변환해 보겠습니다.

```
data class User(val id: Int, val name: String, val age: Int)

fun main() {
    val users = listOf(
        User(1, "Alice", 30),
        User(2, "Bob", 25),
        User(3, "Charlie", 35)
    )

    // users 리스트를 id를 키로, User 객체 전체를 값으로 하는 맵으로 변환
    val userMap: Map<Int, User> = users.associate { user ->
        user.id to user
    }

    // 결과 출력
    println(userMap)
    // 출력 결과: {1=User(id=1, name=Alice, age=30), 2=User(id=2, name=Bob, age=25), 3=User(id=3, name=Charlie, age=35)}

    // 맵에서 특정 id를 가진 유저 찾기
    val alice = userMap[1]
    println(alice)
    // 출력 결과: User(id=1, name=Alice, age=30)
}
```

이 예제에서 `users.associate { user -> user.id to user }` 부분이 핵심입니다.

1. `users` 리스트의 첫 번째 요소인 `User(1, "Alice", 30)`가 람다로 전달됩니다.
2. 람다는 `1 to User(...)`(`Pair(1, User(...))`)를 반환합니다.
3. `associate`는 이 `Pair`를 새로운 맵에 추가합니다. `1`이 키, `User(...)`가 값이 됩니다.
4. 다음 요소인 `User(2, "Bob", 25)`에 대해서도 동일한 과정이 반복됩니다.
5. 최종적으로 모든 요소에 대한 변환이 끝나면, `Map<Int, User>`가 완성됩니다.

#### `associate`와 유사한 함수들 비교

Kotlin에는 `associate`와 비슷해 보이는 다른 함수들도 있습니다. 이들을 구분하여 사용하는 것이 중요합니다.

**1. `associateBy`**

`associateBy`는 `associate`보다 더 간결하게 키만 생성하고, 원본 객체 전체를 값으로 사용하고 싶을 때 유용합니다. `associate`는 키와 값을 모두 직접 `Pair`로 만들어 반환해야 하지만, `associateBy`는 키만 반환하는 람다를 받습니다.

형태: `fun <T, K> Iterable<T>.associateBy(keySelector: (T) -> K): Map<K, T>`

```
val userMapBy: Map<Int, User> = users.associateBy { it.id }
// 결과는 위 associate 예제와 동일합니다.
```

**2. `associateWith`**

`associateWith`는 `associateBy`와 반대로, 원본 객체를 키로 사용하고, 값만 생성하고 싶을 때 유용합니다.

형태: `fun <T, V> Iterable<T>.associateWith(valueTransform: (T) -> V): Map<T, V>`

```
val userNameMap: Map<User, String> = users.associateWith { it.name }
// 결과: {User(...) -> "Alice", User(...) -> "Bob", User(...) -> "Charlie"}
```

***

Kotlin의 `associate` 함수는 리스트를 맵으로 변환하는 매우 유연하고 강력한 도구입니다. `associate`는 키와 값을 모두 직접 커스터마이징하여 `Pair`로 반환하는 방식이고, `associateBy`는 키만 지정하고 값을 원본 객체로 사용하며, `associateWith`는 키를 원본 객체로 사용하고 값만 지정하는 방식입니다.
