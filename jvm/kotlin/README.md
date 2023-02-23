# 🔮 KOTLIN

##

코틀린은 매우 강력한 언어이며, 사용하기 쉽고 재미있는 기능들이 많이 있습니다. 여기에는 그 중 일부가 나열되어 있습니다.

### 1. 확장 함수

확장 함수는 클래스에 기능을 추가할 수 있습니다. 다른 언어에서는 인터페이스를 구현해야하지만, 코틀린에서는 클래스의 기능을 확장할 수 있습니다.

```
fun String.addExclamation(): String {
    return "$this!"
}

```

위의 코드에서는 String 클래스에 addExclamation()이라는 함수를 추가합니다. 그러므로 아래와 같은 코드가 가능해집니다.

```
val greeting = "Hello"
println(greeting.addExclamation()) // 출력: Hello!

```

### 2. Null 안정성

코틀린은 컴파일 시간에 null 안전성을 검사합니다. 이는 NullPointerException을 방지하는 데 도움이 됩니다. 예를 들어 다음과 같은 코드가 있습니다.

```
var name: String? = null

```

위의 코드에서 변수 name은 null을 가질 수 있습니다. 이 변수를 사용할 때는 null 체크를 해주어야 합니다.

```
val message = "My name is ${name ?: "Unknown"}"

```

위의 코드에서는 엘비스 연산자를 사용하여 null 체크를 합니다. 만약 name이 null이면 "Unknown"이 출력됩니다.

### 3. 데이터 클래스

코틀린은 데이터 클래스를 지원합니다. 데이터 클래스는 자동으로 toString(), equals(), hashCode() 등을 생성해줍니다.

```
data class Person(val name: String, val age: Int)

```

위의 코드에서는 Person 클래스를 생성합니다. 이 클래스는 name과 age를 가지며, 자동으로 toString(), equals(), hashCode() 등을 생성합니다.

```
val person1 = Person("Alice", 25)
val person2 = Person("Bob", 30)

println(person1) // 출력: Person(name=Alice, age=25)
println(person1 == person2) // 출력: false

```

### 4. 함수형 프로그래밍

코틀린은 함수형 프로그래밍을 지원합니다. 람다식, 고차 함수 등을 지원하여 코드량을 줄이고 가독성을 높일 수 있습니다.

```
val numbers = listOf(1, 2, 3, 4, 5)

val evenNumbers = numbers.filter { it % 2 == 0 }

println(evenNumbers) // 출력: [2, 4]

```

위의 코드에서는 리스트 내부의 짝수를 필터링합니다. 이를 위해 람다식을 사용합니다.

### 5. 코루틴

코루틴은 비동기 프로그래밍을 지원하는 기능입니다. 이를 사용하면 비동기 코드를 작성하기가 쉬워집니다. 코루틴은 suspend 함수와 함께 사용됩니다.

```
suspend fun fetchUserData(userId: String): User {
    // ...
}

val user = runBlocking {
    val userId = "123"
    fetchUserData(userId)
}

```

위의 코드에서는 runBlocking 함수를 사용하여 코루틴을 실행합니다. 이를 통해 fetchUserData 함수를 호출하고 반환된 값을 user 변수에 저장합니다.

이외에도 많은 재미있는 기능들이 코틀린에 있습니다. 코틀린을 사용해보면 더 많은 기능들을 발견할 수 있을 것입니다!
