# 최대한 플랫폼 타입을 사용하지 마라

코틀린은 null-safety 매커니즘으로 인해 NPE를 거의 찾아보기 힘듬\
null-safety 매커니즘이 없는 자바, C 등의 프로그래밍 언어와 코틀린을 연결해서 사용할 때는 NPE 예외가 발생할 수 있음

```kotlin
public class JavaTest{ 
    public String giveName() { ... }
}
```

위 자바 코드로 반환된 타입을 사용할때에 @Nullable 어노테이션이 붙어 있다면 nullable로 추정하고 String?으로 변경하면 되는데 만약 붙어 있지 않다면 자바에서 모든 것이 nullable일 수 있으므로 최대한 안전하게 접근하기 위해 nullable로 가정하고 접근해야 함

### 제네릭 타입

```kotlin
public class UserRepo {
    public List<User> getUsers() { ...}
}

val users: List<User> = UserRepo().users!!.filterNotNull()
```

코틀린이 디폴트로 모든 타입을 nullable로 다룬다면, 이를 사용할 때 이러한 리스트와 리스트 내부의 User 객체들이 널 아니라는 것을 알아야 함

그래서 코틀린은 자바 등의 다른 프로그래밍 언어에서 넘어온 타입들을 특수하게 다루고 이러한 타입을 플랫폼 타입이라고 부름
