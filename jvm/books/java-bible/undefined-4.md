# 예외처리

> **컴파일 에러** 컴파일 시에 발생하는 에러\
> **런타임 에러** 실행 시에 발생하는 에러\
> **논리적 에러** 실행은 되지만, 의도와 다르게 동작하는 것

> **에러(error)** 프로그램 코드에 의해서 수습될 수 없는 심각한 오류\
> **예외(exception)** 프로그램 코드에 의해서 수습될 수 있는 다소 미약한 오류

#### 예외 클래스의 계층구조 <a href="#undefined" id="undefined"></a>

* **Exception클래스들**\
  사용자의 실수와 같은 외적인 요인에 의해 발생하는 예외
* **RuntimeException클래스들**\
  프로그래머의 실수로 발생하는 예외

#### 예외 처리하기 <a href="#undefined" id="undefined"></a>

예외처리(exception handling)의

* 정의\
  프로그램 실행 시 발생할 수 있는 예외에 대비한 코드를 작성하는 것
* 목적\
  츠로그램의 비정상 종료를 막고, 정상적인 실행상태를 유지하는 것

#### 메서드에 예외 선언하기 <a href="#undefined" id="undefined"></a>

자바에서는 메서드를 작성할 때 메서드 내에서 빌생할 가능성이 있는 예외를 메서드의 선언부에 명시하여 이 메서드를 사용하는 쪽에서는 이에 대한 처리를 하도록 강요하기 때문에, 프로그래머들의 짐을 덜어 주는 것은 물론이고 보다 견고한 프로그램 코드를 작성할 수 있도록 도와준다.

```java
void method() throw Exception1, Exception2 {
	//메서드 내용
}
```

#### finally블럭 <a href="#finally" id="finally"></a>

try-catch문과 함께 예외의 발생여부에 상관없이 실행되어야할 코드를 포함시킬 목적으로 사용된다.

try블럭이나 catch블럭에서 return문이 실행되는 경우에도 finally블럭의 문장들이 먼저 실행된 후에, 현재 실행 중인 메서드를 종료한다.

#### 자원 자동 반환 <a href="#undefined" id="undefined"></a>

주로 입출력에 사용되는 클래스를 사용할 때 유용하다.

#### 예외 되던지기(exception re-throwing) <a href="#exception-re-throwing" id="exception-re-throwing"></a>

이 방법은 하나의 예외에 대해서 예외가 발생한 메서드와 이를 호출한 메서드 양쪽 모구에서 처리해줘야 할 작업이 있을 때 사용된다.

이 때 주의할 점은 예외가 발생할 메서드에서는 메서드의 선언부에 발생할 예외를 throws에 지정해줘야 한다.
