# GnomeSort

**Gnome Sort**와 같은 복잡한 정렬 알고리즘을 먼저 시도해볼 수 있습니다.&#x20;

Gnome Sort는 버블 정렬과 비슷하지만, 조건이 충족되지 않을 때 다시 뒤로 돌아가는 방식으로 동작하는 알고리즘입니다.&#x20;

```kotlin
fun gnomeSort(arr: IntArray) {
    var index = 0
    while (index < arr.size) {
        if (index == 0 || arr[index] >= arr[index - 1]) {
            index++
        } else {
            val temp = arr[index]
            arr[index] = arr[index - 1]
            arr[index - 1] = temp
            index--
        }
    }
}

fun main() {
    val arr = intArrayOf(34, 2, 78, 1, 56, 44, 3, 99)
    gnomeSort(arr)
    println(arr.joinToString(", "))
}
```

작은 값이 나올 때마다 뒤로 돌아가면서 그 앞과 비교해 위치를 교체하는 방식으로 동작합니다.&#x20;

이 알고리즘은 일반적인 O(n^2) 시간 복잡도를 가지며, 특히 큰 배열에서는 비효율적일 수 있지만, 구체적인 동작을 이해하는 데 도움을 줍니다.
