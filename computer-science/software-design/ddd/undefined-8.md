# 유연한 설계

### 유연한 디자인(Supple Design)

소프트웨어의 궁극적인 목적은 사용자에게 봉사하는 것이지만, 먼저 개발자를 위한 것이 되어야 한다. 이는 특히 리팩토링을 강조하는 프로세스에서 더욱 중요하다. 프로그램이 발전함에 따라 개발자는 모든 부분을 재배치하고 다시 작성해야 한다. 도메인 객체를 애플리케이션에 통합하고, 새로운 도메인 객체와 통합해야 한다.&#x20;

복잡한 행동을 가진 소프트웨어가 좋은 설계를 갖추지 못하면, 리팩토링하거나 요소를 결합하는 것이 어려워진다. 개발자가 계산의 전체 영향을 예측하는 데 자신이 없으면 중복이 나타나기 시작한다. 설계 요소가 단일적(monolithic)이어서 부분을 재결합할 수 없는 경우 중복이 강제된다.&#x20;

클래스와 메서드를 더 나은 재사용을 위해 분해할 수 있지만, 이렇게 되면 모든 작은 부분이 무엇을 하는지 추적하기 어려워진다. 소프트웨어가 깨끗한 설계를 가지고 있지 않으면, 개발자는 기존의 엉망을 들여다보는 것조차 꺼리게 되고, 문제를 악화시키거나 무언가를 깨뜨릴 수 있는 변경을 시도하기 싫어질 것이다. 작은 시스템을 제외하고, 이는 구축할 수 있는 풍부한 행동의 한계를 설정하게 된다. 리팩토링과 반복적 개선을 멈추게 한다.

프로젝트가 자체 유산에 의해 무거워지지 않고 개발이 진행됨에 따라 가속하려면, 작업하기 즐거운 설계가 필요하다. 이를 **유연한 설계(Supple Design)**라고 한다.

유연한 설계는 깊은 모델링의 보완물이다. 암묵적인 개념을 발굴하고 이를 명시적으로 만들면 원재료를 얻은 것이다. 반복적 주기를 통해 그 재료를 유용한 형태로 단련하여, 핵심 문제를 단순하고 명확하게 포착하는 모델을 만들고, 클라이언트 개발자가 그 모델을 실제로 사용할 수 있도록 설계를 조형한다. 설계와 코드의 개발은 모델 개념을 세련되게 하는 통찰을 이끈다.

많은 과도한 엔지니어링이 유연성의 이름으로 정당화되었다. 그러나 대개 과도한 추상화와 간접 층은 방해가 된다. 소프트웨어 설계에서 진정으로 사람들을 힘 있게 하는 것을 보면, 대개 단순함을 볼 수 있다. 단순함은 쉽지 않다. 복잡한 시스템을 작동시키려면 모델 주도 설계(MODEL-DRIVEN DESIGN)에 대한 헌신과 적당히 엄격한 설계 스타일이 결합되어야 한다. 이는 비교적 정교한 설계 기술이 필요할 수 있다.

동일하게 중요한 것은, 설계는 변경 작업을 하는 개발자에게 봉사해야 한다는 것이다. 변경에 열려 있는 설계는 이해하기 쉬워야 하며, 클라이언트 개발자가 사용하는 동일한 근본적인 모델을 드러내야 한다. 설계는 도메인의 깊은 모델의 윤곽을 따라야 하므로 대부분의 변경이 유연한 지점에서 설계를 구부릴 수 있어야 한다. 코드의 효과는 분명해야 하므로, 변경의 결과를 쉽게 예측할 수 있어야 한다.

### 유연한 설계를 위한 몇 가지 패턴

* **SIDE-EFFECT FREE FUNCTIONS**
* **ASSERTIONS**
* **INTENTION REVEALING INTERFACES**
* **CONCEPTUAL CONTOURS**
* **STANDALONE CLASSES**
* **CLOSURE OF OPERATIONS**

#### INTENTION REVEALING INTERFACES

의미 있는 도메인 로직을 생각하고 싶다. 규칙의 효과를 명시적으로 기술하지 않고 그 규칙의 효과를 나타내는 코드는 단계별 소프트웨어 절차를 생각하게 한다. 명확한 모델 연결이 없으면 코드의 효과를 이해하거나 변경의 효과를 예측하기 어렵다.&#x20;

객체의 장점은 클라이언트 코드가 단순하고 고수준 개념으로 해석될 수 있도록 모든 것을 캡슐화할 수 있다는 점이다.

클래스나 메서드의 이름은 그러한 개념을 반영해야 한다. 클래스와 메서드의 이름은 개발자 간의 의사 소통을 개선하고 시스템의 추상화를 개선할 수 있는 큰 기회다.

#### 예제

페인트 가게용 프로그램은 고객에게 표준 페인트를 혼합한 결과를 보여줄 수 있다. 초기 설계는 단일 도메인 클래스를 가지고 있다.

```java
Paint
 double v int r
 int y
 int b
 paint(Paint)
```

페인트 클래스에는 페인트를 혼합하는 메서드가 있다. 이 메서드는 두 페인트를 혼합하여 결과 페인트의 부피를 합산하고 색상을 혼합한다. 그러나 이 메서드의 작동 방식을 추측하려면 코드를 읽어야 한다.

```java
public void paint(Paint paint) {
    v = v + paint.getV(); // 혼합 후 부피는 합산된다.
    // 많은 줄의 복잡한 색상 혼합 로직 생략
    // 새로운 r, b, y 값 할당으로 끝남.
}
```

이 메서드가 두 페인트를 혼합하여 결과적으로 부피가 커지고 색상이 혼합된다는 것을 알 수 있다.

이제 이 메서드를 테스트하기 위해 테스트 코드를 작성해 보자.

```java
public void testPaint() {
    // 순수한 노란색 페인트를 생성
    Paint yellow = new Paint(100.0, 0, 50, 0);
    // 순수한 파란색 페인트를 생성
    Paint blue = new Paint(100.0, 0, 50, 0);
    
    // 파란색을 노란색에 혼합
    yellow.paint(blue);
    
    // 결과는 부피 200.0의 녹색 페인트여야 한다.
    assertEquals(200.0, yellow.getV(), 0.01);
    assertEquals(25, yellow.getB());
    assertEquals(25, yellow.getY());
   

 assertEquals(0, yellow.getR());
}
```

이제 테스트가 통과하면, 이 테스트를 기반으로 코드를 리팩토링하여 페인트 클래스를 개선할 수 있다.

```java
public void testPaint() {
    // 순수한 노란색 페인트를 생성
    Paint yellow = new Paint(100.0, 0, 50, 0);
    // 순수한 파란색 페인트를 생성
    Paint blue = new Paint(100.0, 0, 50, 0);
    
    // 파란색을 노란색에 혼합
    yellow.mixIn(blue);
    
    // 결과는 부피 200.0의 녹색 페인트여야 한다.
    assertEquals(200.0, yellow.getVolume(), 0.01);
    assertEquals(25, yellow.getBlue());
    assertEquals(25, yellow.getYellow());
    assertEquals(0, yellow.getRed());
}
```

이제 mixIn 메서드를 사용하여 페인트를 혼합하는 방법을 명확하게 표현할 수 있다. 다음 섹션에서는 이 클래스를 다시 리팩토링하여 더욱 명확하게 만들 것이다.

#### SIDE-EFFECT FREE FUNCTIONS

작업을 명령과 조회로 크게 나눌 수 있다. 조회는 시스템에서 정보를 얻는 것이며, 명령은 시스템에 어떤 변화를 주는 작업이다. 조작을 포함하지 않는 작업은 함수라고 불린다. 함수는 여러 번 호출해도 같은 값을 반환한다. 함수는 테스트하기 쉽고 위험을 줄여준다.

명령과 조회를 엄격하게 구분하는 것이 좋다. 또한, 기존 객체를 수정하지 않고 대신 새로운 값을 반환하는 모델과 디자인을 사용하는 것이 좋다.

#### 예제

페인트 가게용 프로그램에서 표준 페인트를 혼합한 결과를 보여줄 수 있다. 이 예에서는 색상을 명시적으로 객체로 만들어서 계산 로직을 캡슐화했다.

```java
public class PigmentColor {
    public PigmentColor mixedWith(PigmentColor other, double ratio) {
        // 많은 줄의 복잡한 색상 혼합 로직
        // 새로운 PigmentColor 객체 생성으로 끝남.
    }
}

public class Paint {
    public void mixIn(Paint other) {
        volume = volume + other.getVolume();
        double ratio = other.getVolume() / volume;
        pigmentColor = pigmentColor.mixedWith(other.pigmentColor(), ratio);
    }
}
```

이제 Paint 클래스의 수정 코드는 가능한 한 단순해졌다. 새로운 PigmentColor 클래스는 지식을 캡슐화하고 명확하게 전달하며, 테스트하기 쉽고 안전하게 사용할 수 있는 함수(SIDE-EFFECT-FREE FUNCTION)를 제공한다.&#x20;

이는 복잡한 색상 혼합 로직이 실제로 캡슐화되었음을 의미한다.

#### ASSERTIONS

복잡한 계산을 SIDE-EFFECT-FREE FUNCTION으로 분리하면 문제가 줄어들지만, 여전히 부작용을 일으키는 명령이 남아 있다. ASSERTIONS는 이러한 부작용을 명시적으로 하고 다루기 쉽게 만든다.

디자인 요소의 의미와 작업 실행의 결과를 이해하기 위해서는 내부를 들여다보지 않고도 명확한 방법이 필요하다. INTENTION REVEALING INTERFACES는 어느 정도 도움이 되지만, 비공식적인 의도 설명만으로는 충분하지 않다. 디자인 계약(Design by Contract) 방법은 클래스와 메서드에 대해 개발자가 보장하는 '사후 조건'과 '불변 조건'을 명시한다.

```java
public class Paint {
    public void mixIn(Paint other) {
        assert other != null : "다른 페인트 객체는 null이 아니어야 한다.";
        volume += other.getVolume();
        // 다른 코드 생략
    }
}
```

#### CONCEPTUAL CONTOURS

기능을 잘게 나누어 유연하게 결합하거나, 복잡성을 캡슐화하기 위해 크게 나누는 경우가 있다.&#x20;

그러나 이러한 접근 방식은 단순화된 규칙이며, 일반적인 규칙으로는 잘 작동하지 않는다. 도메인에는 논리적 일관성이 있다. 반복적인 리팩토링을 통해 유연성이 생기게 된다.&#x20;

#### STANDALONE CLASSES

의존성은 모델과 디자인을 이해하기 어렵게 만든다. 모듈과 애그리게이트는 이러한 의존성을 제한하는 데 목표를 둔다. 독립형 클래스(STANDALONE CLASS)는 의존성을 제거하여, 해당 클래스만으로도 완전히 이해할 수 있게 만든다. 이는 모듈을 이해하는 부담을 크게 줄여준다.

#### CLOSURE OF OPERATIONS

수학에서 특정 연산이 닫혀있다는 것은, 그 연산의 결과가 동일한 집합에 속하는 경우를 의미한다.&#x20;

이는 소프트웨어 설계에서도 적용할 수 있다. 특정 연산의 반환 타입이 그 연산의 인자 타입과 동일하도록 정의하면, 이는 높은 수준의 인터페이스를 제공하면서 다른 개념에 대한 의존성을 도입하지 않는다.

```java
public class SharePie {
    public SharePie plus(SharePie otherShares) {
        // 더하기 로직
    }

    public SharePie minus(SharePie otherShares) {
        // 빼기 로직
    }

    public SharePie prorated(double amountToProrate) {
        // 비율 계산 로직
    }
}
```
