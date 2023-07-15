# 소개

#### 단위 테스트란?

단위 테스트(Unit Test)는 프로그램의 기본 단위인 모듈(Module)을 테스트하는 것이다. \
구현 단계에서 각 모듈의 개발을 완료한 후 명세서의 내용대로 정확히 구현되었는지를 테스트하는 것이다

테스트가 가능한 최소 단위로 나눠서 테스트를 수행하며 개발 수명주기(Development LifeCycle)의 정황과 시스템에 의존적이면서도 시스템의 다른 부분에서 격리하여 독립적으로 수행해야 하는 테스트이다.&#x20;

단위테스트를 하기 위해서는 가짜 프로그램, 객체(Mock Object)를 만들어서 활용할 수 있으며, 정교하게 테스트 하기 위해서는 테스트 케이스(Test Case) 작성은 필수라 할 수 있다.

종류 : JUnit(Java), DBUnit(DB), CppUnit(C++), NUnit(.net), PyUnit(Python)

단위테스트의 장점

```
개발단계 초기에 문제를 발결할 수 있게 도와줌
리팩토링 또는 라이브러리 업그레이드 등에서 기능 확인을 도와줌(회귀 테스트)
기능에 대한 불확실성 감소
시스템에 대한 실제 문서 또는 예제로써 사용가능
빠른 피드백과 기능을 안전하게 보호 가능
```

단위테스트 수행 기법 비교

| -  | Black Box(통합 단위 테스트) | White Box(기능 단위 테스트) |
| -- | -------------------- | -------------------- |
| 정의 | 사용자 관점 IO 테스트        | 개발자 관점 Logic 테스트     |
| 장점 | 테스트용이                | 오류에 빠른 피드백           |
| 단점 | 내부 연관도 파악 곤란         | 누락된 Logic 찾기 곤란      |
| 종류 | 동등분할, 경계 값, 원인결과     | 구조, 루프 테스트           |

&#x20;

#### JUnit이란?

Java의 단위테스트를 수행해주는 대표적인 Testing Framework로써 JUnit4와 그 다음 버전인 JUnit5를 대게 사용한다. JUnit5는 다음과 같은 구조를 가지고 있다.

<figure><img src="https://blog.kakaocdn.net/dn/qVRFa/btqQIbDz6wo/G5KZcaH0VZ6ZeQUxSpScLk/img.png" alt=""><figcaption></figcaption></figure>

JUnit5 = JUnit Platform + JUnit Jupiter + JUnit Vintage

```
JUnit Platform : 테스트를 발견하고 테스트 계획을 생성하는 TestEngine 인터페이스를 가지고 있음. Platform은 TestEngine을 통해서 테스트를 발견하고 실행하고 결과를 보고.
JUnit Juptier : TestEngine의 실제 구현체는 별도 모듈인데 그중 하나. JUnit5에 새롭게 추가된 Jupiter API를 사용하여 테스트 코드를 발견하고 실행.
JUnit Vintage : 기존에 JUnit4 버전으로 작성한 테스트 코드를 실행할때 vintage-engine 모듈을 사용.
```
