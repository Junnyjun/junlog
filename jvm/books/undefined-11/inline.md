# 함수 타입 파라미터를 갖는 함수에 inline 한정자를 붙여라

inline 한정자의 역할은 컴파일 시점에 '함수를 호출하는 부분'을 '함수의 본문으로 대체' 하는 것

### inline 한정자 사용의 장점

타입 아규면트에 reified 한정자를 붙여서 사용할 수 있음

함수 타입 파라미터를 가진 함수가 훨씬 빠르게 동작

비지역(non-local) 리턴을 사용할 수 있음

#### **타입 아규먼트를 reified로 사용할 수 있다**

제네릭의 경우 컴파일 타임에 타입정보가 사라짐\
따라서 타입 파라미터에 대한 연산에 오류가 발생

함수를 인라인으로 만들면, 이러한 제한을 무시할 수 있음\
함수 호출이 본문으로 대체되므로, reified 한정자를 지정하면, 타입 파라미터를 사용한 부분이 타입 아규먼트로 대체

**함수 타입 파라미터를 가진 함수가 훨씬 빠르게 동작한다**

inline 한정자를 붙이면 함수 호출과 리턴을 위해 점프하는 과정과 백스택을 추적하는 과정이 없기 때문에 조금 더 빠르게 동작하는 것

함수 파라미터를 가지지 않는 함수에서는 inline 한정자 유무에 따른 차이가 큰 차이를 발생시키지 않는다.

함수 리터럴을 사용해 만들진 종류의 객체는 어떤 방식으로든 저장되고 유지되어야 함

코틀린/JVM 에서는 JVM 익명 클래스 또한 일반 클래스를 기반으로, 함수를 객체로 만들어 낸다

```kotlin
class LambdaExample {
    val lambda: () -> Unit = {
        print("lambda")
    }
}
```

함수 본문을 객체로 wrapping 하기 때문에 코드의 속도가 느려진다.

***

인라인 함수와 인라인 함수가 아닌 함수의 중요한 차이는 함수 리터럴 내부에서 지역 변수를 캡쳐할 때 확인할 수 있다

인라인이 아닌 람다 표현식에서는 표현식 바깥의 지역 변수를 직접 사용할 수 없다.\
따라서 지역 변수는 컴파일 과정 중에 레퍼런스 객체로 랩핑되고, 람다 표현식 내부에서 이를 사용

```kotlin
class Repeat {
    private fun repeat(action: () -> Unit) {}

    fun test() {
        var l = 1L
        repeat {
            l += 1
        }
    }
}
```

**비지역적 리턴을 사용할 수 있다**

함수 리터럴이 컴파일될 때, 함수가 객체로 wrapping 되기 때문에 내부에서 리턴을 사용할 수 없다

함수가 다른 클래스에 위치하기때문에 return을 사용해서 main으로 돌아올 수 없기 때문

inline을 사용하면 함수가 main 함수 내부에 박히기 때문에 return을 사용할 수 있다.

**inline 한정자의 비용**

인라인 함수는 재귀적으로 동작할 수 없다

인라인 함수는 더 많은 가시성 제한을 가진 요소를 사용할 수 없다

public 인라인 함수 내부에서는 private과 internal 가시성을 가진 함수와 프로퍼티를 사용할 수 없다\
inline 한정자를 남용하면 코드의 크기가 쉽게 커진다

### **crossinline과 noinline**

함수를 인라인으로 만들고 싶지만, 어떠한 이유로 일부 함수 타입 파라미터는 inline으로 받고 싶지 않은 경우가 있을 수 있다

#### crossinline

아규먼트로 인라인 함수를 받지만, 비지역적 리턴을 하는 함수는 받을 수 없게 만든다

인라인으로 만들지 않은 다른 람다 표현식과 조합해서 사용할 때 문제가 바생하는 경우 활용

#### noinline

아규먼트로 인라인 함수를 받을 수 없게 만든다

인라인 함수가 아닌 함수를 아규먼트로 사용하고 싶을 때 활용

```kotlin
class InlineKeywordExample {
    inline fun requestNewToken(
            hasToken: Boolean,
            crossinline onRefresh: () -> Unit,
            noinline onGenerate: () -> Unit
    ) {
        if (hasToken) {
            httpCall("get-token", onGenerate)
        } else {
            httpCall("refresh-token") {
                onRefresh()
                onGenerate()
            }
        }
    }

    fun httpCall(url: String, callback: () -> Unit) {

    }
}
```
