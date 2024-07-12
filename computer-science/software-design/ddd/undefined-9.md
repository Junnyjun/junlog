# 유연한 설계

### 유연한 디자인(Supple Design)

소프트웨어의 궁극적인 목적은 사용자에게 봉사하는 것이지만, 먼저 개발자를 위한 것이 되어야 한다. 이는 특히 리팩토링을 강조하는 프로세스에서 더욱 중요하다. 프로그램이 발전함에 따라 개발자는 모든 부분을 재배치하고 다시 작성해야 한다. 도메인 객체를 애플리케이션에 통합하고, 새로운 도메인 객체와 통합해야 한다.&#x20;

복잡한 행동을 가진 소프트웨어가 좋은 설계를 갖추지 못하면, 리팩토링하거나 요소를 결합하는 것이 어려워진다. 개발자가 계산의 전체 영향을 예측하는 데 자신이 없으면 중복이 나타나기 시작한다. 설계 요소가 단일적(monolithic)이어서 부분을 재결합할 수 없는 경우 중복이 강제된다.&#x20;

프로젝트가 자체 유산에 의해 무거워지지 않고 개발이 진행됨에 따라 가속하려면, 작업하기 즐거운 설계가 필요하다. 이를 **유연한 설계(Supple Design)**라고 한다.

유연한 설계는 깊은 모델링의 보완물이다. 암묵적인 개념을 발굴하고 이를 명시적으로 만들면 원재료를 얻은 것이다.\
설계와 코드의 개발은 모델 개념을 세련되게 하는 통찰을 이끈다.

동일하게 중요한 것은, 설계는 변경 작업을 하는 개발자에게 봉사해야 한다는 것이다. 변경에 열려 있는 설계는 이해하기 쉬워야 하며, 클라이언트 개발자가 사용하는 동일한 근본적인 모델을 드러내야 한다.&#x20;

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

이 메서드가 두 페인트를 혼합하여 결과적으로 부피가 커지고 색상이 혼합된다는 것을 알 수 있다.\
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

이제 mixIn 메서드를 사용하여 페인트를 혼합하는 방법을 명확하게 표현할 수 있다. \
다음 섹션에서는 이 클래스를 다시 리팩토링하여 더욱 명확하게 만들 것이다.

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

디자인 요소의 의미와 작업 실행의 결과를 이해하기 위해서는 내부를 들여다보지 않고도 명확한 방법이 필요하다. INTENTION REVEALING INTERFACES는 어느 정도 도움이 되지만, 비공식적인 의도 설명만으로는 충분하지 않다.&#x20;

디자인 계약(Design by Contract) 방법은 클래스와 메서드에 대해 개발자가 보장하는 '사후 조건'과 '불변 조건'을 명시한다.

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

그러나 이러한 접근 방식은 단순화된 규칙이며, 일반적인 규칙으로는 잘 작동하지 않는다. \
도메인에는 논리적 일관성이 있다. 반복적인 리팩토링을 통해 유연성이 생기게 된다.&#x20;

#### STANDALONE CLASSES

의존성은 모델과 디자인을 이해하기 어렵게 만든다. 모듈과 애그리게이트는 이러한 의존성을 제한하는 데 목표를 둔다.&#x20;

독립형 클래스(STANDALONE CLASS)는 의존성을 제거하여, 해당 클래스만으로도 완전히 이해할 수 있게 만든다. 이는 모듈을 이해하는 부담을 크게 줄여준다.

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

### 선언적 설계

우리는 이전에 주장과 비공식적인 테스트 방법에 대해 논의했다. 이것들은 훨씬 더 나은 설계를 이끌어낼 수 있지만, 직접 작성한 소프트웨어에서는 진정한 보장을 할 수 없다.&#x20;

ASSERTIONS를 회피하는 방법 중 하나는 명시적으로 배제되지 않은 추가적인 부작용이 있을 수 있다는 것이다. 아무리 MODEL-DRIVEN 설계라 하더라도 개념적 상호 작용의 효과를 생성하기 위해 절차를 작성해야 한다.&#x20;

사람마다 다르게 해석되지만, 일반적으로 프로그램이나 그 일부를 일종의 실행 가능한 명세로 작성하는 방법을 의미한다. 매우 정밀한 속성 설명이 실제로 소프트웨어를 제어한다.&#x20;

1. 선언 언어가 필요한 모든 것을 표현하기에 충분하지 않지만, 자동화된 부분을 넘어 소프트웨어를 확장하는 것이 매우 어려운 프레임워크.
2. 코드 생성 기술이 수동으로 작성된 코드에 병합되어 재생성이 매우 파괴적인 방식으로 반복 주기를 저해하는 것.

아이러니하게도 이러한 선언적 설계 시도는 종종 모델과 애플리케이션의 단순화로 끝난다. 프레임워크의 한계에 갇힌 개발자는 무언가를 전달하기 위해 설계를 절충하게 된다.

특정 설계의 단순화와 오류를 줄이는 데 유용한 방법 중 하나는 좁은 범위의 프레임워크가 설계의 특정 지루하고 오류가 많은 측면을 자동화하는 것이다.&#x20;

#### 사이드바: 규칙 기반 프로그래밍

규칙 기반 프로그래밍은 선언적 설계의 이상을 목표로 하며, 도메인 주도 설계에 대한 매력적인 접근 방식을 제공한다. 그러나 이는 미묘한 문제가 될 수 있다.&#x20;

원칙적으로 규칙 기반 프로그램은 선언적이지만, 대부분의 시스템은 성능 조정을 허용하기 위해 '제어 서술자'를 추가했다.&#x20;

#### 도메인 특화 언어

흥미로운 접근 방식 중 하나는 '도메인 특화 언어'(DSL)이다. 클라이언트 코드는 특정 도메인의 특정 모델에 맞춘 프로그래밍 언어로 작성된다.&#x20;

이러한 언어에서 프로그램은 매우 표현적일 수 있으며, UBIQUITOUS LANGUAGE와 가장 강력한 연결을 가질 수 있다. 그러나 객체 지향 기술을 기반으로 한 접근 방식에서는 몇 가지 단점이 있다.

모델을 개선하려면 언어를 수정할 수 있어야 한다. 여기에는 문법 선언 및 기타 언어 해석 기능을 수정하는 것뿐만 아니라 기본 클래스 라이브러리를 수정하는 것도 포함된다. 물론 누군가는 리팩토링 문제에 대한 기술적 해결책을 제시할 수 있다.

이 기술은 성숙한 모델, 특히 클라이언트 코드가 다른 팀에 의해 작성되는 경우에 가장 유용할 수 있다. 일반적으로 이러한 설정은 기술적으로 숙련된 프레임워크 구축자와 기술적으로 비숙련된 애플리케이션 구축자 사이의 유해한 구별을 초래할 수 있지만, 반드시 그렇게 될 필요는 없다.

#### 선언적 설계 스타일

설계가 INTENTION-REVEALING INTERFACES, SIDE-EFFECT-FREE FUNCTIONS 및 ASSERTIONS를 가지게 되면 선언적 설계 영역에 들어서게 된다. 선언적 설계의 많은 이점은 의미를 전달하는 결합 가능한 요소가 있을 때 얻을 수 있다.

#### SPECIFICATIONS의 선언적 스타일 확장

Chapter 9에서는 SPECIFICATION의 기본 개념, 프로그램에서 수행할 수 있는 역할 및 구현에 필요한 것들을 다루었다. 이제 몇 가지 유용한 벨과 휘슬을 살펴보자.

SPECIFICATION은 서술자의 공식화된 개념을 응용한 것이다. 서술자는 결합 및 수정할 수 있는 "and", "or" 및 "not" 연산을 가진다. 이러한 논리 연산은 서술자 아래에서 닫히므로, SPECIFICATION 조합은 연산의 폐쇄성을 보인다.

#### 논리 연산자를 사용한 SPECIFICATIONS 결합

SPECIFICATIONS를 사용하면 조합하고 싶은 상황이 자주 발생한다. SPECIFICATION은 서술자의 예이며, 서술자는 "and", "or" 및 "not" 연산으로 결합 및 수정할 수 있다. 이러한 논리 연산은 서술자 아래에서 닫히므로, SPECIFICATION 조합은 연산의 폐쇄성을 보인다.

#### 예제

```java
public interface Specification {
    boolean isSatisfiedBy(Object candidate);
}

public class ContainerSpecification implements Specification {
    private ContainerFeature requiredFeature;

    public ContainerSpecification(ContainerFeature requiredFeature) {
        this.requiredFeature = requiredFeature;
    }

    boolean isSatisfiedBy(Object candidate) {
        if (!(candidate instanceof Container)) return false;
        return ((Container) candidate).getFeatures().contains(requiredFeature);
    }
}
```

이제 Specification 인터페이스를 확장하여 세 가지 새로운 연산을 추가하자.

```java
public interface Specification {
    boolean isSatisfiedBy(Object candidate);
    Specification and(Specification other);
    Specification or(Specification other);
    Specification not();
}
```

Container Specifications을 확장한 예를 보자. 한 화학 물질이 휘발성이고 폭발성이 있는 경우 두 가지 요구 사항이 있을 수 있다. 이제 두 가지 SPECIFICATION을 조합할 수 있다.

```java
Specification ventilated = new ContainerSpecification(VENTILATED);
Specification armored = new ContainerSpecification(ARMORED);
Specification both = ventilated.and(armored);
```

이는 더 복잡한 Container Specification을 필요로 했을 것이며, 여전히 특수 목적의 것이었을 것이다. 이와 같은 방법으로 더 복잡한 규칙을 정의할 수 있다.
