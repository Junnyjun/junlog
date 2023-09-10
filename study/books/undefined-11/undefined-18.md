# 변화로부터 코드를 보호하려면 추상화를 사용하라

추상화를 잘하면 세부 사항을 잘 알지 못하더라도 변경에 자유로워짐

#### 상수

리터럴을 상수 프로퍼티로 변경하게 되면 해당 값을 이름을 붙일 수 있고 값을 변경할때에 훨씬 쉽고 안전하게 변경이 가능하게됨

Before

```kotlin
fun isPasswordValid(text: String): Boolean {
    if (text.length < 7) return false
}
```

After

```kotlin
const val MIN_PASSWORD_LENGTH = 7

fun isPasswordValid(text: String): Boolean {
    if (text.length < MIN_PASSWORD_LENGTH) return false
}
```

이런식으로 상수로 빼면 장점

* 의미있는 이름 부여
* 값 변경에 안전함

#### 함수

사용자에게 토스트 메시지를 자주 출력해야하는 상황이 발생하는 경우 아래와 같이 간단한 확장 함수를 만들어 사용할 수 있음

Before

```kotlin
fun Context.toast(
    message: String,
    duration: Int = Toast.LENGTH_LONG
) {
    Toast.makeText(this, message, duration).show()
}
```

여기서 스낵바라는 다른 형태의 방식으로 출력해야하는 요구사항 발생

```
fun Context.snackbar(
    message: String,
    duration: Int = Snackbar.LENGTH_LONG
) {
   //
}
```

위와 같이 스낵바를 출력하는 확장함수를 만들고 기존의 Context.toast를 Context.snackbar로 변경

그러나 위와 같으 방법은 다른 모듈이 해당 함수를 의존하고 있을때 영향을 줄 수 있기 때문에 좋지 않음

After

```kotlin
fun Context.showMessage(
    message: String,
    duration: MessageLength = MessageLength.LONG,
) {
    val toastDuration = when (duration) {
        MessageLength.SHORT -> Length.LENGTH_LONG
        MessageLength.LONG -> Length.LENGTH_LONG
    }
    
    Toast.makeText(this, message, toastDuration).show()
}
```

위 요구사항의 경우 메시지 출력하고 싶다는 의도 자체가 중요하기 duration을 enum으로 관리 및 입력을 받고 함수명을 메시지 출력만 하는 역할로써 높은 레벨의 함수로 옮김

#### 클래스

클래스는 상태를 가질 수 있고 많은 함수를 가질 수 있기 때문에 함수보다 더욱 다양한 기능의 추상화 도구로 사용할 수 있음

```kotlin
class MessageDisplay(
    val context: Context
) {
    
    fun show(
        message: String,
        duration: MessageLength = MessageLength.LONG
    )  {
        val toastDuration = when (duration) {
            MessageLength.SHORT -> Length.LENGTH_LONG
            MessageLength.LONG -> Length.LENGTH_LONG
        }

        Toast.makeText(this.context, message, toastDuration).show()
    }
}

fun main() {
    val messageDisplay = MessageDisplay(Context())
    messageDisplay.show("message")
}
```

의존성 주입 프레임 워크를 사용해 클래스 생성 위임할 수 있음

```kotlin
@Inject lateinit var messageDisplay: MessageDisplay
```

mock 객체를 활용해 해당 클래스에 의존하는 다른 클래스의 기능을 테스트 할 수 있음

```kotlin
val messageDisplay: MessageDisplay = mockk()
messageDisplay.setChristmasMode(true)
```

하지만 더 많은 확장성을 얻기 위해서는 인터페이스를 활용하는 것이 좋음

#### 인터페이스

* 라이브러리의 경우 내부 클래스의 가시성을 제한하고, 인터페이스를 통해 노출하는 코드를 많이 사용
* 이렇게 하면 사용자가 클래스를 직접 사용하지 못해 라이브러리를 만드는 사람은 인터페이스만 유지하면 되기 때문에 사용자 측과의 결합을 줄일 수 있음
* 참고로 코틀린은 멀티 플랫폼 언어라 환경에 따라 다른 리스트를 리턴 및 플랫폼에 최적화를 시키고 있음

```kotlin
interface MessageDisplay {
    fun show(
        message: String,
        duration: MessageLength = MessageLength.LONG
    )
}

class ToastDisplay(
    val context: Context,
): MessageDisplay {
    override fun show(message: String, duration: MessageLength) {
        val toastDuration = when (duration) {
            MessageLength.SHORT -> Length.LENGTH_LONG
            MessageLength.LONG -> Length.LENGTH_LONG
        }

        Toast.makeText(this.context, message, toastDuration).show()
    }
}
```

* 인터페이스로 구성하면 해당 인터페이스의 의도만 제공하고 다양한 플랫폼에서 의도에 맞는 다양한 기능 직접 구현해 사용할 수 있음
* 추가로 테스트시에 인터페이스 faking이 클래스 mocking보다 간단하기 때문에 별도의 mocking 라이브러리를 사용하지 않아도됨
* 추가로 선언과 사용이 분리되어 있기 때문에 ToastDisplay 등의 실제 클래스를 자유롭게 변경할 수 있음

#### 추상화가 주는 자유

아래와 같이 추상화하는 방법을 통해 추상화를 진행하게 되면 변화에 대한 자유를 제공함

```
상수로 추출
동작을 함수로 래핑
함수를 클래스로 래핑
인터페이스 뒤에 클래스를 숨김
보편적인 객체를 특수한 객체로 래핑
```

#### 추상화의 문제

모든 것을 추상화하게 되면 불필요하게 너무 많은 것을 숨기게 되고 간단한 의도나 동작을 이해하기 더욱 어려워질 수 있음

그래서 팀의 크기나 도메인 복잡도 등을 고려해 적절한 추상화를 진행해야함

적절한 균형을 찾는 것은 거의 예술에 가까운 것이기 때문에 많은 경험이 필요한 영역

#### 정리

추상화는 단순하게 중복성을 제거해서 코드를 구성하기 위한 것이 아니고 코드를 변경해야할때 자유를 주는 것

하지만 너무 과한 추상화는 오히려 이해하기 힘든 구조로 변질될 수 있음
