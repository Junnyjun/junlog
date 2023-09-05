# 일반적인 알고리즘 구현 시 제네릭을 써라

제네릭 함수 : 타입 아규먼트를 사용하는 함수 즉, 타입 파라미터를 갖는 함수를 얘기한다.

* 타입 파라미터는 컴파일러에 타입과 관련된 정보를 제공하여 컴파일러가 타입을 조금이라도 더 정확하게 추측할 수 있게 해준다.
* 컴파일 과정에서 타입 정보는 사라지지만 개발 중에는 특정 타입을 사용하게 강제할 수 있다.

제네릭 제한

* 타입 파라미터의 기능 중 하나로 구체적인 타입의 서브타입만 사용하게 타입을 제한한다.

```
public fun <T, C : MutableCollection<in T>> Iterable<T>.toCollection(destination: C): C {
    for (item in this) {
        destination.add(item)
    }
    return destination
}
```

* Any를 이용하여 nullable이 아닌 타입을 나타내는 케이스

```
public inline fun <T, R : Any> Iterable<T>.mapNotNull(transform: (T) -> R?): List<R> {
    return mapNotNullTo(ArrayList<R>(), transform)
}
```

※ where 키워드

java의 upper bounds 와 비슷한 기능,&#x20;

```
where T : CharSequence, T : Comparable<T>
// T 타입은 CharSequence와 Comparable을 모두 구현해야한다.
```
