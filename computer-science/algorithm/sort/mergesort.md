# MergeSort

다음으로 조금 더 복잡한 정렬 알고리즘인 병합 정렬 (Merge Sort)을 코틀린으로 구현해보겠습니다.&#x20;

병합 정렬은 분할 정복 알고리즘으로, 배열을 반으로 나누고 각각을 재귀적으로 정렬한 다음 병합하는 방식으로 작동합니다.

```kotlin
fun mergeSort(arr: IntArray): IntArray {
    if (arr.size <= 1) {
        return arr
    }

    val middle = arr.size / 2
    val left = arr.sliceArray(0 until middle)
    val right = arr.sliceArray(middle until arr.size)

    return merge(mergeSort(left), mergeSort(right))
}

fun merge(left: IntArray, right: IntArray): IntArray {
    var leftIndex = 0
    var rightIndex = 0
    val result = IntArray(left.size + right.size)

    for (i in result.indices) {
        if (rightIndex >= right.size || (leftIndex < left.size && left[leftIndex] <= right[rightIndex])) {
            result[i] = left[leftIndex]
            leftIndex++
        } else {
            result[i] = right[rightIndex]
            rightIndex++
        }
    }

    return result
}

fun main() {
    val arr = intArrayOf(38, 27, 43, 3, 9, 82, 10)
    val sortedArray = mergeSort(arr)
    println(sortedArray.joinToString(", "))
}
```

**병합 정렬**은 다음과 같은 방식으로 동작합니다:

1. 배열을 반으로 나누고, 재귀적으로 각각을 정렬.
2. 각각의 배열을 정렬한 후, 두 배열을 병합.
3. 병합할 때는 두 배열의 앞부분부터 비교하여 작은 값을 결과 배열에 넣고, 나머지를 뒤에 붙입니다.

병합 정렬은 O(n log n)의 시간 복잡도를 가지고 있으며, 안정적인 정렬 알고리즘입니다.
