# 추상화 규약을 지켜라

* 규약은 개발자들의 단순한 합의이기 때문에 한쪽에서 규약을 위반할 수 있음

```
class Employee {
    private val id: Int = 2
    override fun toString() = "User(id=$id)"
    private fun privateFunction() {
        println("Private function called")
    }
}

fun callPrivateFunction(employee: Employee) {
    employee::class.declaredFunctions
        .first { it.name == "privateFunction" }
        .apply { isAccessible == true }
        .call(employee)
}

fun changeEmployeeId(employee: Employee, newId: Int) {
    employee::class.java.getDeclaredField("id")
        .apply { isAccessible = true }
        .set(employee, newId)
}

fun main() {
    val employee = Employee()
    callPrivateFunction(employee)
    changeEmployeeId(employee, 1)
    println(employee)
} 
```

* 위와 같이 리플렉션을 활용하면 우리가 원하는 것을 열고 사용할 수 있는데 이런 코드는 private 프로퍼티와 private 함수의 이름과 같은 세부적인 정보에 매우 크게 의존하고 있기 때문에 변경에 매우 취약할 수 있음

#### 상속된 규약

* 클래스르 상속받거나 인터페이스를 확장할때는 규약을 지키는 것은 더더욱 중요
* 예를 들어 equals와 hashCode 메서드를 가진 Any 클래스를 상속 받는데 이러한 메소드는 모두 우리가 반드시 존중하고 지켜야하는 규약이 있음
  * 만약 규약을 지키지 않는다면 객체가 제대로 동작 안할 수 있음
  * 아래와 같이 equals가 제대로 구현되지 않는다면 중복을 허용하게 되어 제대로 동작하지 않음

```
class Id(val id: Int) {
    override fun equals(other: Any?): Boolean = other is Id && other.id == id
}

fun main() {
    val set = mutableSetOf(Id(1))
    set.add(Id(1))    
    set.add(Id(1))
    println(set.size) // 3
}
```

#### 정리

* 프로그램 안정성을 위해 규약을 최대한 지키는 것이 좋음
* 만약 규약을 깨야한다면 문서화를 잘해두는 것이 좋음
*
