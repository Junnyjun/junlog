# 설계와 아키텍처란?

* 설계와 아키텍처의 차이점\
  \
  설계 - 저수준의 구조와 결정사항을 의미하는 용어로 사용되어 진다.\
  아키텍처 - 고수준의 구조와 결정사항을 의미하는 용어로 사용되어 진다.\
  \
  하지만 저수준과 고수준의 구조를 마땅히 구분할 만한 경계가 없다.\
  \
  &#xNAN;_&#xC989;, 설계와 아키텍처 사이에는 아무런 차이가 없다._\
  \\
* 소프트웨어 아키텍처의 목표 - 필요한 시스템을 만들고 유지보수하는 데 투입되는 인력을 최소화하는 데 있다.

시스템을 급하게 만들거나, 결과물의 총량을 순전히 프로그래머 수만으로 결정하거나, 코드와 설계의 구조를 깔끔하게 만들려는 생각을 전혀 하지 않으면, 파국으로 치닫는 이 비용 곡선에 올라타게 된다.

<figure><img src="https://blog.kakaocdn.net/dn/bBxMGo/btqZYl6gkDm/d4oYbGNwUluYUA74BTTQk1/img.png" alt=""><figcaption><p>릴리즈에 따른 생산성</p></figcaption></figure>

###

### '토끼와 거북이'

> "급할수록 돌아가라."

토끼와 거북이 우화는 지나친 과신이 가진 어리석음을 말해준다.

현대의 개발자는 "코드는 나중에 정리하면 돼. 당장은 시장에 출시하는 게 먼저야" 라는 흔해 빠진 거짓말에 속는다.

결국에 개발자는 절대로 태세를 전환하지 않는다. 이전에 작성한 코드로 돌아가 정리하는 일은 일어나지 않는다.

~~_"지저분한 코드를 작성하면 단기간에는 빠르게 갈 수 있고, 장기적으로 볼 때만 생산성이 낮아진다."_~~

개발자는 위 거짓말을 받아들이고, 나중에 기회가 되면 엉망이 된 코드를 정리하는 태세로 전환할 수 있다고 자신의 능력을 과신하게 된다.

하지만 진실은 다음과 같다.

> 엉망으로 만들면 깔끔하게 유지할 때 보다 항상 더 느리다.

아래는 제이슨 고먼(Jason Gorman)이 수행한 실험결과이다.

실험은 6일에 걸쳐 진행하였다.

그는 정수를 로마 숫자로 변환하는 프로그램을 완성하였다.

그는 TDD(테스트 주도 개발)을 첫째 날, 셋째 날, 다섯째 날에 적용했다. 나머지 3일에는 TDD를 사용하지 않은 채 코드를 작성했다.

결과적으로 TDD를 적용한 날 중 가장 느렸던 날이 TDD를 적용하지 않고 가장 빨리 작업한 날보다도 더 빨랐다.

<figure><img src="https://blog.kakaocdn.net/dn/cZ5Cbi/btqZ0sDxyyN/N4Tq24XY9NC41NWJ8UT550/img.png" alt=""><figcaption><p>이터레이션별 걸린 시간과 TDD 적용 여부</p></figcaption></figure>

> 빨리가는 유일한 방법은 제대로 가는 것이다.

### 결론

어떤 경우라도 개발 조직이 할 수 있는 최고의 선택지는 조직에 스며든 과신을 인지하여 방지하고, 소프트웨어 아키텍처의 품질을 심각하게 고민하기 시작하는 것이다.

소프트웨어 아키텍처를 심각하게 고려할 수 있으려면 좋은 소프트웨어 아키텍처가 무엇인지 이해해야 한다.

비용은 최소화하고 생산성은 최대화할 수 있는 설계와 아키텍처를 가진 시스템을 만들려면, 이러한 결과로 이끌어줄 시스템 아키텍처가 지닌 속성을 알고 있어야 한다.
