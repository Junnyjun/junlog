# Array

### 배열 생성

Kotlin에서 배열을 생성하려면, `arrayOf()` 함수를 사용합니다. `arrayOf()` 함수는 인자로 전달된 요소들을 가지고 새로운 배열을 생성합니다.

```kotlin
kotlinCopy codeval array = arrayOf(1, 2, 3, 4, 5)
```

위 코드에서는 `arrayOf()` 함수를 사용하여 `array` 변수에 정수형 배열을 대입하고 있습니다.

### 배열 접근

Kotlin에서 배열의 요소에 접근하려면, 인덱스를 사용합니다. 배열의 첫 번째 요소의 인덱스는 0이며, 이후 요소의 인덱스는 1씩 증가합니다.

```kotlin
kotlinCopy codeval array = arrayOf(1, 2, 3, 4, 5)
val element1 = array[0]
val element2 = array[1]
```



### 배열 변경

Kotlin에서는 배열 요소의 값을 변경할 수 있습니다.

```kotlin
kotlinCopy codeval array = arrayOf(1, 2, 3, 4, 5)
array[0] = 10
array[1] = 20
```



### 배열 반복

Kotlin에서는 `for` 문을 사용하여 배열의 요소들을 반복할 수 있습니다.

```kotlin
kotlinCopy codeval array = arrayOf(1, 2, 3, 4, 5)
for (element in array) {
    println(element)
}
```
