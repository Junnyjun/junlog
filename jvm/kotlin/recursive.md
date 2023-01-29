# Recursive

재귀는 스스로 호출하다 조건을 만나 종료를하는 함수를 의미합니다.

### 재귀 vs 꼬리재귀

return에 연산이 있으면 일반적인 재귀문입니다.

{% code title="재귀" %}
```kotlin
fun sum(n:Int):Int=
    if (n==1) 1
    else n+ sum(n-1)
```
{% endcode %}

return에 연산이 없으면 꼬리재귀입니다

```kotlin
fun sum(n:Int, sum:Int = n):Int=
    if (n==1) sum
    else sum(n-1, sum+n-1)
```

<img src="../../.gitbook/assets/file.excalidraw (1) (1).svg" alt="" class="gitbook-drawing">

재귀함수는 일반적으로 루프문과 다르게 계속 스택이 쌓이는것을 볼 수 있다.

#### Tailrec

코틀린에서는 tailrec으로 위와같은 꼬리재귀문을 쉽게 Loop를 이용한 코드로 작성할 수 있습니다.

```kotlin
tailrec fun rec(level:Long): Long {
    return if (level == 1L) 1
    else rec(level - 1)
}
```

{% code title="실제 동작하는 코" %}
```kotlin
fun recwhile(level:Long): Long {
    var level = level
    while (level != 1L) {
        level--
    }
    return 1
}
```
{% endcode %}

위 방식처럼 꼬리재귀를 없애고, 루프를 이용한 코드로 변환됩니다

