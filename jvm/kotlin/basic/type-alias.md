# Type Alias

새로운 이름을 사용하여 기존 타입을 정의하는 데 사용되는 키워드입니다. \
typealias를 사용하면 코드의 가독성을 높일 수 있으며, 복잡한 타입을 간결하게 표현할 수 있습니다.

```kotlin
typealias Username = String
typealias Password = String

fun login(username: Username, password: Password) {
    // ...
}

fun main() {
    val username: Username = "myusername"
    val password: Password = "mypassword"
    login(username, password)
}

```

복잡한 표현을 더 간결하게 표현할때 유용합니다.

```kotlin
typealias UserPrefs = Pair<List<Map<String, Any?>>, Set<String>>

val userPrefs: UserPrefs = listOf(
    mapOf("key1" to 1, "key2" to "value2"),
    mapOf("key3" to true, "key4" to 4.5f)
) to setOf("preference1", "preference2")

```
