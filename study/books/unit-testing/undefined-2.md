# 단위 테스트 구조

### &#x20;단위 테스트를 구성하는 방법 ( AAA 패턴 잘 사용하는 방법)

AAA 패턴 예제

<figure><img src="https://blog.kakaocdn.net/dn/cpQl3h/btrxieNuEct/48v0NhiT2MN0VHSM0QhQ8k/img.png" alt="" height="283" width="600"><figcaption></figcaption></figure>

**AAA 패턴의 장점**

\- 테스트 코드의 일관성

\- 익숙해지면 쉽게 읽을 수 있다.

\- 테스트 스위트의 유지 보수 비용 줄여줌

&#x20;

&#x20;

**AAA 안티 패턴**

1\. 준비, 실행, 검증 구절이 여러개 있는 테스트

\-> 더 이상 단위 테스트가 아니라 통합 테스트이다. 단위 테스트는 간단하고, 빠르며, 이해하기 쉬워야 한다.

<figure><img src="https://blog.kakaocdn.net/dn/chZwvJ/btrxiezYTTK/IkZ2mVvBDywhopwkHQzMY1/img.png" alt="" height="441" width="600"><figcaption></figcaption></figure>

2\. 테스트 내에 if문

\-> 단윈 테스트든 통합 테스트든 테스트는 분기가 없는 간단한 일련의 과정이어야 한다. if문 테스트가 한 번에 너무 많은 것을 검증한다는 표시이다. 이점은 없고, 추가 유지비만 늘어난다.

&#x20;

3\. 각 구절의 크기를 고려하지 않은 코드

준비 단계 : 가장 큰 구절, 실행과 검증을 합친 것 보다 클 수도 있다. 너무 크면 별도의 팩토리 클래스로 도출하는 게 좋다. 참고 (Object Mother, Test Data builder)

실행 구절 : 반드시 한 줄이어야 한다. 반드시!

검증 구절 : 단위 테스트의 가장 작은 단위를 평가한다. 단위 테스트의 단위는 동작의 단위이지 코드의 단위가 아니다.

종료 구절 : DB 연결 종료나 할당된 자원을 해제하는 등 부작용이 남지 않도록 하는것. (종료 구절은 보통 통합 테스트의 영역이다.)

&#x20;

실행 구절 안티 패턴 예제

실행 구절이 2줄인 API는 캡슐화가 깨져있고 데이터 베이스와 연결되어 있다면 데이터 베이스 정합성이 깨져있을 수 있다. (그런데도 과정 하나 하나 다 검증 하려고 하는 런던파 놈들 쯧쯧쯧.. )

<figure><img src="https://blog.kakaocdn.net/dn/baWUpn/btrxiFKPtSa/8wy3b66F3h0ZvbKq18YY61/img.png" alt="" height="296" width="600"><figcaption><p>실행 줄이 하나인 코드</p></figcaption></figure>

<figure><img src="https://blog.kakaocdn.net/dn/H1lAu/btrxkL5pOZ6/IAa75QIQK5fVaIJqjEj8nk/img.png" alt="" height="299" width="600"><figcaption><p>실행 줄이 2줄이 코드. API가 잘못 설계 되어있음을 알 수 있다.</p></figcaption></figure>

&#x20;

**테스트 대상 시스템(System Under Test : sut)  구별하기**

말 그래로 단위 테스트의 대상이라는 의미이다.

단위 테스트 대상은 굳이 클래스 이름 따라 인스턴스를 만들지 말고 sut로 명명해서 명확하게 구별해주자.

<figure><img src="https://blog.kakaocdn.net/dn/7Vj7k/btrxlrljy4S/KEIUm7pVgjft5oOSQvWy0k/img.png" alt="" height="314" width="600"><figcaption></figcaption></figure>

### 3.2 xUnit 테스트 프레임워크 살펴보기

스킵.. Junit, Spock을 이미 쓰고 있으면 당연한 기술들이 나열되어 있음.

&#x20;

### 3.3 테스트 간 테스트 픽스처 재사용

테스트 픽스처의 의미 : 테스트 실행 대상 객체. 이 객체는 정규 의존성이다. 예를 들면 DB나 하드 디스크의 파일이 그런 것들이다. 이런 객체는 각 테스트의 실행 전에 알려진 고정 상태로 유지하기 때문에 동일한 결과를 생성한다.

&#x20;

올바르지 않은 재사용 방법

테스트 간 결합도가 높고,

가독성이 떨어진다.

<figure><img src="https://blog.kakaocdn.net/dn/cRTXf8/btrxr4WB5mH/Y1j1mTg7Qu8sWKN4tiP2ek/img.png" alt="" height="515" width="600"><figcaption><p>테스트 픽처스를 수정하면 모든 테스트가 영향을 받는다.</p></figcaption></figure>

테스트간 높은 결합도는 안티 패턴이다.

&#x20;

예를들어,&#x20;

```
_store.AddInventory(Product.Shampoo, 10);
```

위의 코드를 아래와 같이 바꾸는 경우를 생각해보라.

```
_store.AddInventory(Product.Shampoo, 15);
```

&#x20;

&#x20;

그리고 테스트 코드만 보고는 더 이상 전체 그림을 볼 수 없기 때문에 가독성이 떨어진다고 할 수 있다.

&#x20;

&#x20;

더 나은 테스트 픽스처 재사용법

최선은 아니지만 두번째 방법으로는 비공개 팩토리 메서드를 두는 것이다.

코드를 짧게 유지하면서 가독성이 떨어지지 않는다.

<figure><img src="https://blog.kakaocdn.net/dn/cGUCKx/btrxieNwckN/XaJ7JiVKmu66ZC7nXLcEVk/img.png" alt="" height="126" width="600"><figcaption></figcaption></figure>

<figure><img src="https://blog.kakaocdn.net/dn/cmSjD8/btrxiFqBgao/XzXOEbpT9sefXeAJb7c9zk/img.png" alt="" height="581" width="600"><figcaption></figcaption></figure>

하지만 예외도 있다. DB 연결는 통합 테스트가 여기에 해당한다. 모든 테스트마다 DB에 연결이 필요한 경우 최초에 한번 하는게 났다.

이런 경우에 기초 클래스(bass class)를 둬서 DB 연결을 초기화 하는 것이 합리적이다.

<figure><img src="https://blog.kakaocdn.net/dn/zM9Zx/btrxj8lwadV/zVaWsltWmWAksyatzxAGb1/img.png" alt="" height="407" width="600"><figcaption></figcaption></figure>

&#x20;

### 3.4 단위 테스트 명명법

올바른 명칭은 테스트가 검증하는 내용과 기본 시스템의 동작을 이해하는 데 도움이 된다.

&#x20;

안티 패턴

\[테스트 대상 메서드]\_\[시나리오]\_\[예상 결과]

\-> 쉬운 영어 구문이 훨씬 이해하기 쉽다.

&#x20;

예제

<figure><img src="https://blog.kakaocdn.net/dn/MmBKy/btrxsEX3yX2/UxJVPVvbBtf1N2VY95Cxxk/img.png" alt="" height="373" width="600"><figcaption><p>쉬운 영어 표현</p></figcaption></figure>

<figure><img src="https://blog.kakaocdn.net/dn/bbnel3/btrxjHWPf0F/ZDoRkNk21R19FmCGthLBH0/img.png" alt="" height="70" width="600"><figcaption><p>[테스트 대상 메서드]_[시나리오]_[예상 결과] 따른 예</p></figcaption></figure>

&#x20;

프로그래머가 수수께끼 같은 이름을 잘 이해하는 건 사실이지만, 누구에게나 이런 이름을 이해하는 건 부담스럽다.

&#x20;

&#x20;

단위 테스트 명명 지침

* 엄격한 명명 정책을 따르지 않는다. 표현의 자유를 허용하자.
* 도메인에 익숙한 비개발자들에게 시나리오 설명하는 것처럼 테스트 이름을 짓자. (PM, PO, 비지니스 분석가, UX/UI 디자이너)
* 단어를 밑줄(\_)표시로 구분한다.

테스트 클래스 이름을 지을 때 CalculatorTest라고 짓는 경우가 많은데.. Calculator 클래스만 테스트 한다는 생각을 버리자. \[클래스명]Test인 클래스를 동작의 단위로 검증하고 API의 진입점이라고 여기자.

&#x20;

&#x20;

예제

<figure><img src="https://blog.kakaocdn.net/dn/pTREu/btrxoG2PjCO/cs397fMdPSvAyp9u2pCstk/img.png" alt="" height="186" width="600"><figcaption></figcaption></figure>

<figure><img src="https://blog.kakaocdn.net/dn/4CK5v/btrxoWLZA56/KuN7T3l0d43u9XmPaS5mWk/img.png" alt="" height="82" width="600"><figcaption><p>엄격한 명명 정책을 따르는 명명법</p></figcaption></figure>

&#x20;

조금 더 쉬운 영어로

<figure><img src="https://blog.kakaocdn.net/dn/0oAGC/btrxidgMyfI/zkqwTk38KD6PUAQc4Su8Ok/img.png" alt="" height="41" width="600"><figcaption></figcaption></figure>

배송 날짜가 무효하다는게 무슨 의미인가..? 난해하다.

<figure><img src="https://blog.kakaocdn.net/dn/RX4Cy/btrxt16x0Vi/ApWGGUdkuK1FIfrYTE8BXk/img.png" alt="" height="35" width="600"><figcaption></figcaption></figure>

should be 구문도 안티패턴이다. 테스트에 소망, 욕구가 들어가지 않도록 하자. should be는 is로 바꾸자.

<figure><img src="https://blog.kakaocdn.net/dn/bWnAmw/btrxoVGjeoD/RttdKrOghcK1zpMA589LR0/img.png" alt="" height="41" width="600"><figcaption></figcaption></figure>

&#x20;

### 3.5 매개변수화된 테스트 리팩터링하기

보통 테스트 하나로는 동작 단위를 완전하게 설명하기에 충분하지 않다. 이 단위는 일반적으로 여러 구성 요소를 포함하기 때문이다.

동작이 복잡할 수록, 이를 설명하는 데 테스트 수가 급격하히 증가할 수 있고 관리가 어렵다.

그래서 매개변수화된 테스트를 사용해 유사한 테스트를 묶을 수 있다.

<figure><img src="https://blog.kakaocdn.net/dn/b2zGNX/btrxsDkyrdP/LpTiI957OQsxqaz9WRG7i0/img.png" alt="" height="351" width="600"><figcaption><p>일반적으로 복잡도가 클수록, 충분히 설명하려면 더 많은 사실이 필요하다. 매개변수화된 테스트를 사용하면 유사한 사실을 단일한 테스트 메서드로 묶을 수 있다.</p></figcaption></figure>

&#x20;

&#x20;

가장 빠른 배송일이 오늘부터 이틀 후가 되도록 작동하는 배송 기능이 있다고 가정하자.

테스트 하기 위해선 아래의 테스트 들이 필요한다.

```
public void Delivery_with_a_past_date_is_invalid()
public void Delivery_for_today_is_invalid()
public void Delivery_for_tomorrow_is_invalid()
public void The_soonest_delivery_date_is_two_days_from_now()
```

<figure><img src="https://blog.kakaocdn.net/dn/cfMH5D/btrxicPJnDv/KSPLhaCiw6RWLUJrwSHIo0/img.png" alt="" height="432" width="600"><figcaption><p>매개변수화된 테스트, daysFromNow가 2일때만 expected가 true이다.</p></figcaption></figure>

&#x20;

불필요한 boolean 제거 (긍정 경우랑 부정 경우 나누자)

<figure><img src="https://blog.kakaocdn.net/dn/b3YYkS/btrxkJTkxwt/R2zd4wVeKzYPoexSLAuwD1/img.png" alt="" height="319" width="600"><figcaption></figcaption></figure>

&#x20;

### 3.6 검증된 라이브러리를 사용한 테스트 가독성 향상

크게 중요하지 않으니 예제만.

<figure><img src="https://blog.kakaocdn.net/dn/bdcmD1/btrxmAoQzMX/ZETsfn7JDl3CaYZ7fio471/img.png" alt="" height="479" width="600"><figcaption><p>Assert, Equal같은 표현보단. should, be같은 표현이 가독성이 좋다.</p></figcaption></figure>
