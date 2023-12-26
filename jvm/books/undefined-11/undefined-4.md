# 사용자 정의 오류보다는 표준 오류를 사용하라

equire, check, assert 함수를 사용한다면, 대부분의 코틀린 오류를 처리할 수 있음

하지만 이외에도 예측하지 못한 상황을 나태내야 하는 경우가 있음

```kotlin
inline fun <reified T> String.readObject(): T {
    // ...
    val result: Int
    if (incorrectSign) {
        throw JsonParsingException()
    }
    
    return result
}
```

위와 같이 표준 라이브러리에서 나타내는 적절한 오류가 없는 경우 사용자 정의 오류를 사용하는 경우도 있지만 가급적 직접 오류를 정의하기보단 표준 라이브러리의 오류를 사용하는 것이 좋음

잘만들어진 규약을 재사용하면 다른 사람들이 API를 더 쉽게 배우고 이해할 수 잇음

#### 일반적으로 사용되는 예외

```
IllegalArgumentException, IllegalStateException
IndexOutOfBoundsException
ConcurrentModificationException : 동시 수정을 금지했는데 발생했을 경우
UnsupportedOperationException
NoSuchElementException
```
