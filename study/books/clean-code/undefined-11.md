# 동시성

동시성과 깔끔한 코드는 양립하기 어렵다.

스레드를 하나만 실행하는 코드는 짜기가 쉽다. 겉으로 보기에는 멀쩡하지만 깊숙한 곳에 문제가 있는 다중 스레드 코드도 짜기가 쉽다.

이런 코드는 시스템이 부하를 받기 전까지는 멀쩡하게 돌아간다.

이번 장에서는 여러 스레드를 동시에 돌리는 이유, 어려움, 깨끗한 코드를 작성하는 방법, 동시성을 테스트 하는 방법과 문제점을 설명한다.

#### 1. 동시성이 필요한 이유.

동시성은 결합을 없애는 전략이다. **무엇** 과 **언제** 를 분리하는 전략이다.

**무엇** 과 **언제** 를 분리하면 애플리케이션 구조와 효율이 극적으로 나아진다.

예를 들어, 매일 수많은 웹 사이트에서 정보를 가져와 요약하는 정보 수집기가 있다고 가정한다.

수집기가 단일 스레드라면 한 번에 한 사이트를 끝내야 다음 사이트로 넘어간다. 한 번에 한 사이트를 방문하는 대신

다중 스레드 알고리즘을 이용하면 수집기 성능을 높일 수 있다.

**미신과 오해**

반드시 동시성이 필요한 상황이 존재한다. 하지만 동시성은 **어렵다.**

다음은 동시성과 관련한 일반적인 미신과 오해다.

* 동시성은 항상 성능을 높여준다.\
  동시성은 **때로** 성능을 높혀준다.
* 동시성을 구현해도 설계는 변하지 않는다.\
  일반적으로 **무엇** 과 **언제** 를 분리하면 시스템 구조가 크게 달라진다.
* 웹 또는 EJB 컨테이너를 사용하면 동시성을 이해할 필요가 없다.\
  컨테이너가 어떻게 동작하는지, 어떻게 동시 수정, 데드락 등과 같은 문제를 피할 수 있는지를 알아야한다.

다음은 동시성에 대한 타당한 생각 몇가지다.

* 동시성은 다소 부하를 유발한다.\
  성능 측면에서 부하가 걸리며, 코드도 더 짜야한다.
* 동시성은 복잡하다.\
  간단한 문제라도 동시성은 복잡하다.
* 일반적으로 동시성 버그는 재현하기 어렵다.\
  그래서 진짜 결함으로 간주되지 않고 일회성 문제로 여겨 무시하기쉽다.
* 동시성을 구현하려면 흔히 근본적인 설계 전략을 재고해야 한다.

#### 2. 난관.

동시성을 구현하기 어려운 이유는 무엇일까?

두 스레드가 자바 코드 한 줄을 거쳐가는 경로는 수없이 많다.

그 중에 일부 경로가 잘못된 결과를 내놓기 때문이다.

바이트 코드만 고려했을 때, 두 스레드가 하나의 메서드를 실행하는 잠재적인 경로는 최대 **12,870** 개에 달한다.

물론 대다수 경로는 올바른 결과를 내놓는다.

문제는 **잘못된 결과를 내놓는 일부 경로이다.**

#### 3. 동시성 방어 원칙.

**단일 책임 원칙**

**SRP는 주어진 메서드 / 클래스 / 컴포넌트를 변경할 이유가 한여야 한다는 원칙이다.**

동시성은 복잡성 하나만으로도 따로 분리할 이유가 충분하다.

동시성을 구현할 때는 다음 몇 가지를 고려한다.

1. 동시성 코드는 독자적인 개발, 변경, 조율 주기가 있다.
2. 동시성 코드에는 독자적인 난관이 있다.
3. 잘못 구현한 동시성 코드는 별의별 방식으로 실패한다.

* 권장사항: 동시성코드는 다른 코드와 분리하라.

**자료 범위를 제한하라.**

공유 객체를 사용하는 코드 내 **임계 영역** 을 **synchronized** 키워드로 보호하라고 권장한다.

임계영역의 수를 줄이는 기술이 중요하다.

공유 자료를 수정하는 위치가 많을수록 다음 가능성도 커진다.

* 보호할 임계영역을 빼먹는다.
* 모든 임계영역을 올바로 보호했는지 확인하느라 똑같은 노력과 수고를 반복한다.
* 그렇지 않아도 찾기 힘든 버그가 더 찾기 어려워진다.
* 권장사항: 자료를 캡슐화 하라. 공유 자료를 최대한 줄여라.

**자료 사본을 사용하라.**

공유 자료를 줄이려면 처음부터 공유하지 않는 방법이 제일 좋다.

어떤 경우에는 객체를 복사해 읽기 전용으로 사용하는 방법이 가능하다.

공유 객체를 피하는 방법이 있다면 코드가 문제를 일으킬 가능성도 아주 낮아진다.

사본으로 동기화를 피할 수 있다면 내부 잠금을 없애 절약한 수행 시간이 사본 생성과 가비지 컬렉션에 드는 부하를 상쇄활 가능성이 크다.

**스레드는 가능한 독립적으로 구현하라.**

자신만의 세상에 존재하는 스레드를 구현한다.

각 스레드는 클라이언트 요청 하나를 처리한다.

* 권장사항: 독자적인 스레드로, 가능하면 다른 프로세서에서, 돌려도 괜찮도록 자료를 독립적인 단위로 분할하라.

#### 4. 라이브러리를 이해하라.

자바 5는 동시성 측면에서 이전 버전보다 많이 나아졌다.

**스레드 환경에 안전한 컬렉션**

```java
java.util.concurrent
```

패키지가 제공하는 클래스는 다중 스레드 환경에서 사용해도 안전하며, 성능도 좋다.

* 권장사항: 언어가 제공하는 클래스를 검토하라.

#### 5. 실행 모델을 이해하라.

다중 스레드 애플리케이션을 분류하는 방식은 여러 가지다.

먼저 몇가지 용어부터 설명하면

* **한정된 자원**: 다중 스레드 환경에서 사용하는 자원으로, 크기나 숫자가 제한적이다. 데이터베이스 연결, 길이가 읽정한 읽기 / 쓰기 버퍼 등이 예다.
* **상호 배제** : 한 번에 한 스레드만 공유 자료나 공유 자원을 사용할 수 있는 경우를 가리킨다.
* **기아**: 한 스레드나 여러 스레드가 굉장히 오랫동안 혹은 영원히 자원을 기다린다.
* **데드락**: 여러 스레드가 서로가 끝나기를 기다린다. 모든 스레드가 각기 필요한 자원을 다른 스레드가 점유하는 바람에 어느 쪽도 더 이상 진행하지 못한다.
* **라이브락**: 락을 거는 단계에서 각 스레드가 서로를 방해한다. 스레드는 계속 진행하려 하지만, 공명으로 인해, 굉장히 오랫동안 혹은 영원히 진행하지 못한다.

**생산자 - 소비자**

하나 이상 생산자 스레드가 정보를 생성해 버퍼나 대기열에 넣는다.

하나 이상 소비자 스레드가 대기열에서 정보를 가져와 사용한다.

생산자 스레드와 소비자 스레드가 사용하는 대기열은 **한정된 자원이다.**

생산자 스레드는 대기열에 빈 공간이 있어야 정보를 채운다. 즉, 빈 공간이 생길 때까지 기다린다.

대기열을 올바로 사용하고자 생산자 스레드와 소비자 스레드는 서로에게 **시그널** 을 보낸다.

잘못하면 생산자 스레드와 소비자 스레드가 둘 다 진행 가능함에도 불구하고 동시에 서로에게서 시그널을 기다릴 가능성이 존재한다.

**읽기 - 쓰기**

읽기 스레드를 위한 주된 정보원으로 공유 자원을 사용하지만, 쓰기 스레드가 이 공유 자원을 이따금 갱신한다.

이런 경우 처리율이 문제의 핵심이다.

처리율을 강조하면 **기아** 현상이 생기거나 오래된 정보가 쌓인다.

갱신을 허용하면 처리율에 영향을 미친다.

따라서 읽기 스레드의 요구와 쓰기 스레드의 요구를 적절히 만족시켜 처리율도 적당히 높이고

기아도 방지하는 해법이 필요하다.

**식사하는 철학자들**

기업 애플리케이션은 여러 프로세스가 자원을 얻으려 경쟁한다.

주의해서 설계하지 않으면 데드락, 라이브락, 처리율 저하, 효율성 저하 등을 겪는다.

* 권장사항: 위에서 설명한 기본 알고리즘과 각 해법을 이해하라.

#### 6. 동기화하는 메서드 사이에 존재하는 의존성을 이해하라.

동기화하는 메서드 사이에 의존성이 존재하면 동시성 코드에 찾아내기 어려운 버그가 생긴다.

공유 클래스 하나에 동기화된 메서드가 여럿이라면 구현이 올바른지 다시 한 번 확인하기 바란다.

* 권장사항: 공유 객체 하나에는 메서드 하나만 사용하라.

공유 객체 하나에 여러 메서드가 필요한 상황이 생기면 다음 3가지 방법을 고려한다.

* 클라이언트에서 잠금: 클라이언트에서 첫 번째 메서드를 호출하기 전에 서버를 잠근다. 마지막 메서드를 호출할 때까지 잠금을 유지한다.
* 서버에서 잠금: 서버에다 "서버를 잠그고 모든 메서드를 호출한 후 잠금을 해제하는" 메서드를 구현한다. 클라이언트는 이 메서드를 호출한다.
* 연결 서버: 잠금을 수행하는 중간 단계를 생성한다. 서버에서 잠금 방식과 유사하지만 원래 서버는 변경하지 않는다.

#### 7. 동기화하는 부분을 작게 만들어라.

여기저기서 **synchronized** 문을 남발하는 코드는 바람직하지 않다.

반면, 임계영역은 반드시 보호해야 한다.

따라서, 코드를 짤 때는 임계영역 수를 최대한 줄여야 한다.

필요 이상으로 임계영역 크기를 키우면 스레드 간에 경쟁이 늘어나고 프로그램 성능이 떨어진다.

* 권장사항: 동기화하는 부분을 최대한 작게 만들어라.

#### 8. 올바른 종료 코드는 구현하기 어렵다.

깔끔하게 종료하는 코드는 올바로 구현하기 어렵다.

흔히 발생하는 문제가 **데드락** 이다.

깔끔하게 종료하는 다중 스레드 코드를 짜야 한다면 시간을 투자해 올바로 구현해야한다.

* 권장사항: 종료 코드를 개발 초기부터 고민하고 동작하게 초기부터 구현하라. 생각보다 어려우므로 이미 나온 알고리즘을 검토하라.

#### 9. 스레드 코드 테스트하기.

충분한 테스트는 위험을 낮춘다.

* 권장사항: 문제를 노출하는 테스트 케이스를 작성하라. 프로그램 설정과 시스템 설정과 부하를 바꿔가며 자주 돌려라. 테스트가 실패하면 원인을 추적하라. 다시 돌렸더니 통과하더라는 이유로 그냥 넘어가면 절대로 안된다.

이에 대해 몇가지 구체적인 지침을 제시한다.

**말이 안되는 실패는 잠정적인 스레드 문제로 취급하라.**

다중 스레드 코드는 때때로 **말이 안되는 오류** 를 일으킨다.

스레드 코드에 잠입한 버그는 수백만 번에 한 번씩 드러나기도 한다.

**일회성 문제란 존재하지 않는다** 라고 가정해야한다.

* 권장사항: 시스템 실패를 일회성이라 치부하지 마라.

**다중 스레드를 고려하지 않은 순차 코드부터 제대로 돌게 만들자.**

스레드 환경 밖에서 코드가 제대로 도는지 반드시 확인한다.

일반적인 방법으로, 스레드가 호출하는 **POJO** 를 만든다.

POJO는 스레드를 모른다. 따라서 스레드 환경 밖에서 테스트가 가능하다.

* 권장사항: 스레드 환경 밖에서 생기는 버그와 스레드 환경에서 생기는 버그를 동시에 디버깅하지 마라. 먼저 스레드 환경 밖에서 코드를 올바로 돌려라.

**다중 스레드를 쓰는 코드 부분을 다양한 환경에 쉽게 끼워 넣을 수 있게 스레드 코드를 구현하라.**

* 한 스레드로 실행하거나, 여러 스레드로 실행하거나, 실행 중 스레드 수를 바꿔본다.
* 스레드 코드를 실제 환경이나 테스트 환경에서 돌려본다.
* 테스트 코드를 빨리, 천천히, 다양한 속도로 돌려본다.
* 반복 테스트가 가능하도록 테스트 케이스를 작성한다.
* 권장사항: 다양한 설정에서 실행할 목적으로 다른 환경에 쉽게 끼워 넣을 수 있게 코드를 구현하라.

**다중 스레드를 쓰는 코드 부분을 상황에 맞게 조율할 수 있게 작성하라.**

처음부터 다양한 설정으로 프로그램의 성능 측정 방법을 강구한다.

스레드 개수를 조율하기 쉽게 코드를 구현한다.

프로그램이 돌아가는 도중에 스레드 개수를 변경하는 방법을 고려한다.

프로그램 처리율과 효율에 따라 스스로 스레드 개수를 조율하는 코드도 고민한다.

**프로세서 수보다 많은 스레드를 돌려보라.**

시스템이 스레드를 스와핑 할 때도 문제가 발생한다.

스와핑이 잦을수록 임계영역을 빼먹은 코드나 데드락을 일으키는 코드를 찾기 쉬워진다.

**다른 플랫폼에서 돌려보라.**

운영체제마다 스레드를 처리하는 정책이 다르다.

다중 스레드 코드는 플랫폼에 따라 다르게 돌아간다.

따라서 코드가 돌아갈 가능성이 있는 플랫폼 전부에서 테스트를 수행해야 마땅하다.

* 권장사항: 처음부터 그리고 자주 모든 목표 플랫폼에서 코드를 돌려라.

**코드에 보조 코드를 넣어 돌려라. 강제로 실패를 일으키게 해보라.**

스레드 코드는 오류를 찾기가 쉽지 않다.

간단한 테스트로는 버그가 드러나지 않는다.

스레드 버그가 산발적이고 우발적이고 재현이 어려운 이유는 코드가 실행되는 수천 가지 경로 중에 아주 소수만 실패하기 때문이다.

보조 코드를 추가해 코드가 실행되는 순서를 바꿔준다.

**Object.wait(), Object.sleep(), Object.yield()** 등과 같은 메서드를 추가해 코드를 다양한 순서로 실행한다.

보조 코드를 추가하는 방법은 두 가지다.

* 직접 구현하기
* 자동화
* 권장사항: 흔들기 기법을 사용해 오류를 찾아내라.

#### 10. 결론.

다중 스레드 코드는 올바로 구현하기 어렵다.

무엇보다 먼저, SRP를 준수한다. POJO를 사용해 스레드를 아는 코드와 모르는 코드를 분리한다.

스레드 코드는 최대한 집약되고 작아야 된다는 의미이다.

동시성 오류를 일으키는 잠정적인 원인을 철저히 이해한다.

어떻게든 문제는 생긴다. 초반에 드러나지 않는 문제는 일회성으로 치부해 무시하기 십상이다.

그러므로 스레드 코드는 많은 플랫폼에서 많은 설정으로 반복해서 계속 테스트해야 한다.

시간을 들여 보조 코드를 추가하면 오류가 드러날 가능성이 크게 높아진다.

스레드 코드는 출시하기 전까지 최대한 오랫동안 돌려봐야 한다.

깔끔한 접근 방식을 취한다면 코드가 올바로 돌아갈 가능성이 극적으로 높아진다.