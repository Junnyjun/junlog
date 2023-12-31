# 객체와 자료구조

변수를 비공개(private)로 정의하는 이유는 남들이 변수에 의존하지 않게 만들고 싶기 때문이다.

하지만 왜 수많은 프로그래머들이 get 함수와 set 함수를 당연하게 공개(public)해 비공개 변수를 외부에 노출할까?

#### 1. 자료 추상화.

다음 두 클래스는 모두 2차원 점을 표현한 클래스이다. 하나는 구현을 외부로 노출하고 다른 하나는 구현을 완전히 숨긴다.

```
public class Point {
    public double x;
    public double y;
}
```

```
public interface Point {
    double getX();
    double getY();
    void setCartesian(double x, double y);
}
```

첫번째 클래스는개별절으로 좌표값을 읽고 설정하게 강제한다.

만약 변수를 private으로 선언한다 해도 get함수와 set함수를 제공한다면 구현을 외부로 노출하는 셈이다.

두번째 클래스는 클래스 메서드가 접근 정책을 강제한다.

좌표를 읽을 때는 각 값을 개별적으로 읽어야 한다.

**변수 사이에 함수라는 계층을 넣는다고 구현이 저절로 감춰지지는 않는다.**

구현을 감추려면 추상화가 필요하다.

추상 인터페이스를 제공해 사용자가 구현을 모른 채 자료의 핵심을 조작할 수 있어야 진정한 의미의 클래스다.

자료를 세세하게 공개하기보다는 추상적인 개념으로 표현하는 편이 좋다.

아무 생각 없이 조회 / 설정 함수를 추가하는 방법이 가장 나쁘다.

#### 2. 자료 / 객체 비대칭.

**객체는 추상화 뒤로 자료를 숨긴 채 자료를 다루는 함수만 공개한다.**

**자료 구조는 자료를 그대로 공개하며 별다른 함수는 제공하지 않는다.**

자료와 객체구조는 근본적으로 양분된다.

* 자료구조를 사용하는 절차적인 코드는 기존 자료 구조를 변경하지 않으면서 새 함수를 추가하기 쉽다. 반면에 객체 지향 코드는 기존 함수를 변경하지 않으면서 새로운 클래스를 추가하기가 쉽다.
* 절차적인 코드는 새로운 자료 구조를 추가하기가 어렵고, 객체 지향 코드는 새로운 함수를 추가하기가 어렵다.

**객체 지향 코드에서 어려운 변경은 절차적인 코드에서는 쉽고, 절차적인 코드에서 어려운 변경은 객체 지향 코드에서는 쉽다.**

#### 3. 디미터 법칙.

> 디미터 법칙 : 모듈은 자신이 조작하는 객체의 속사정을 몰라야한다.

쉽게말해 **클래스 C의 메서드 f는 다음과 같은 객체의 메서드만 호출해야하는 법칙이다.**

1. 클래스 C
2. f가 생성한 객체
3. f에 인수로 넘어온 객체
4. C 인스턴스 변수에 저장된 객체

**기차 충돌**

흔히 다음과 같은 코드를 **기차 충돌** 이라 부른다.

다음 코드는 임시 파일을 생성하기 위한 임시 티렉터리의 절대 경로를 얻는 코드이다.

```
final String outputDir = ctxt.getOptions().getScratchDir().getAbsolutePath();
```

위의 코드는 여러 객체가 한줄로 이어진 기차처럼 보인다.

일반적으로 조잡하다 여겨지는 방식이기 때문에 피하는 편이 좋다.

위의 코드는 아래처럼 나누는 편이 좋다

```
Options opts = ctxt.getOptions();
File scratchDir = opts.getScratchDir();
final String outputDir = scratchDir.getAbsolutePath();
```

하지만 위의 코드 역시 디미터 법칙을 위반한다.

함수 하나가 아는 지식이 굉장히 많다.

반면, 자료 구조라면 디미터 법칙이 적용되지 않는다.

다음과 같이 구현했다면 디미터 법칙을 거론하지 않아도 된다.

```
final String outputDir = ctxt.options.scratchDir.absolutePath;
```

자료 구조는 무조건 함수 없이 공개 변수만 포함하고 객체는 비공개 변수와 공개 함수를 포함하게 한다.

그러나 간단한 자료 구조에도 조회 함수와 설정 함수 정의를 요구하는 프레임워크나 표준(ex: Bean)이 존재한다.

#### 4. 자료 전달 객체

자료 구조체의 전형적인 형태는 공개 변수만 있고 함수가 없는 클래스이다.

**이런 자료 구조체를 떄로는 자료 전달 객체(DTO)라 한다.**

좀 더 일반적인 형태로는 빈(Bean) 구조가 있다.

빈은 비공개 변수를 조회 / 설정 함수로 조작한다.

하지만 빈 구조는 별다른 이익을 제공하지는 않는다.

**활성 레코드**

활성 레코드는 DTO의 특수한 형태이다.

공개 변수가 있거나 비공개 변수에 조회 / 설정 함수가 있는 자료 구조지만, save나 find와 같은 탐색 함수도 제공한다.

활성 레코드는 데이터 베이스 테이블이나 다른 소스에서 자료를 직접 변환한 결과이다.

하지만 활성 레코드에 **비즈니스 규칙 메서드를 추가해 이런 자료 구조를 객체로 취급하는 건 바람직하지 않다.**

이렇게 되면 자료 구조도 아니고 객체도 아닌 잡종 구조가 나오기 때문이다.

**활성 레코드는 자료 구조로 취급한다.**

비즈니스 규칙을 담으면서 내부 자료를 숨기는 객체는 따로 생성한다.

이때 내부 자료는 활성 레코드의 인스턴스일 가능성이 높다.

#### 5. 결론

객체는 동작을 공개하고 자료를 숨긴다. 때문에 기존 동작을 변경하지 않으면서 새 객체 타입을 추가하기는 쉽지만 기존 객체에 새 동작을 추가하기는 어렵다.

자료 구조는 별다른 동작 없이 자료를 노출한다. 때문에 기존 자료 구조에 새 동작을 추가하기는 쉬우나, 기존 함수에 새 자료 구조를 추가하기는 어렵다.

우수한 소프트웨어 개발자는 편견없이 이 사실을 이해해 직면한 문제에 최적인 해결책을 선택한다.
