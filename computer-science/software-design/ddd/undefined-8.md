# 암묵적인 개념을 명시적으로 만들기

깊은 모델링은 매우 유용하지만, 실제로 어떻게 해야 할까? 깊은 모델은 사용자 활동, 문제, 솔루션의 핵심 개념과 추상화를 포함하여 유연하게 표현할 수 있는 능력을 갖춘다. 첫 번째 단계는 도메인의 필수 개념을 어떤 형태로든 모델에 표현하는 것이다. 세부화는 지식 축적과 리팩토링의 연속적인 반복 후에 이루어진다. 중요한 개념이 인식되고 모델과 설계에서 명시적으로 표현될 때 이 과정은 진정으로 시작된다.

도메인 모델과 해당 코드의 많은 변형은 암시적으로 존재하는 개념을 인식하고 이를 모델에서 하나 이상의 명시적인 객체나 관계로 표현하는 형태를 취한다. 이러한 변화는 종종 중요한 돌파구가 되어 깊은 모델로 이어진다. 그러나 대부분의 경우, 돌파구는 모델에 중요한 개념들이 명시된 후, 여러 번의 리팩토링을 통해 책임이 반복적으로 조정되고, 다른 객체와의 관계가 변경되고, 이름이 몇 번 변경된 후에 발생한다. 최종적으로 명확한 형태로 드러나지만, 이는 암시된 개념을 인식하는 것으로 시작된다.

이러한 인식은 팀의 언어를 경청하고, 설계의 어색함과 전문가들의 진술에서 모순점을 면밀히 조사하며, 도메인 문헌을 탐구하고, 많은 실험을 통해 이루어진다.

#### 언어를 경청하라

다음과 같은 경험이 있을 수 있다. 사용자들이 보고서의 어떤 항목에 대해 항상 이야기해왔다. 해당 항목은 애플리케이션이 다양한 상황에서 수집한 데이터 집합으로부터 작성된다. 다른 객체의 속성이나 직접적인 데이터베이스 쿼리. 데이터를 모아 무언가를 보고하거나 도출하는 것이다. 그러나 객체가 필요하다고 생각해 본 적이 없을 것이다. 아마도 사용자가 의미하는 바를 진정으로 이해하지 못했거나, 그것이 중요하다는 것을 깨닫지 못했을 것이다.

그러다 갑자기 머릿속에 불이 켜진다. 당신은 새로운 통찰력에 대해 전문가들과 흥분 속에 이야기한다. 그들은 당신이 마침내 이해한 것에 대해 안도감을 보이거나, 이미 당연하게 여겼던 것일 수 있다. 어느 쪽이든, 당신은 이제 모델 다이어그램을 그리기 시작하고, 사용자들이 새로운 모델이 어떻게 연결되는지에 대한 세부 사항을 수정한다. 대화의 질이 변화했음을 알 수 있다. 당신과 사용자는 서로를 더 정확하게 이해하고 있으며, 특정 시나리오를 해결하기 위한 모델 상호작용의 시연이 더 자연스러워졌다. 도메인 모델의 언어가 더 강력해졌다. 코드를 리팩토링하여 새로운 모델을 반영하면 더 깔끔한 설계를 얻을 수 있다.

도메인 전문가들이 사용하는 언어를 들어보라. 간결하게 복잡한 내용을 말하는 용어가 있는가? 당신의 단어 선택을 수정하는가? 특정 용어를 사용하면 당혹스러운 표정이 사라지는가? 이러한 것은 모델에 도움이 될 수 있는 개념의 힌트다.

이는 '명사는 객체'라는 오래된 개념이 아니다. 새로운 단어를 듣는 것은 대화를 통해 탐구하고 지식 축적을 통해 깨끗하고 유용한 개념을 추구하는 단서가 될 뿐이다. 사용자나 도메인 전문가가 디자인에 없는 용어를 사용할 때, 이는 경고 신호다. 개발자와 도메인 전문가 모두가 디자인에 없는 용어를 사용할 때는 두 배로 강한 경고 신호다.

또는 이를 기회로 볼 수 있다. **UBIQUITOUS LANGUAGE**는 말, 문서, 모델 다이어그램, 코드에 퍼져 있는 어휘로 구성된다. 용어가 디자인에 없으면, 이를 포함하여 모델과 디자인을 개선할 수 있는 기회가 있다.

#### 예제: 배송 모델에서 누락된 개념 인식하기

팀은 이미 화물을 예약할 수 있는 작동하는 애플리케이션을 개발했다. 이제 원산지, 목적지 및 선박 간의 환승에서 화물의 하역 작업 주문을 관리하는 '운영 지원' 애플리케이션을 구축하려고 한다.

예약 애플리케이션은 화물의 여행을 계획하기 위해 라우팅 엔진을 사용한다. 여정의 각 구간은 화물이 탑재될 선박의 항해 ID(특정 선박의 특정 항해를 나타내는 고유 식별자), 탑재 위치 및 하역 위치를 나타내는 데이터베이스 테이블의 행으로 저장된다.

![Cargo Booking Table](https://via.placeholder.com/400)

```
| Cargo_ID | Voyage_ID | Load_Loc | Unload_Loc |
```

다음은 개발자(DEV)와 배송 전문가(EXP) 간의 대화 (대폭 축약된) 일부다.

**DEV**: 운영 애플리케이션이 필요한 모든 데이터를 '화물 예약' 테이블에 포함하고 싶습니다.

**EXP**: 운영팀은 화물의 전체 일정이 필요할 것입니다. 지금 어떤 정보가 있습니까?

**DEV**: 화물 ID, 선박 항해, 각 구간의 탑재 항구 및 하역 항구입니다.

**EXP**: 날짜는 어떻게 하나요? 운영팀은 예상 시간을 기준으로 작업을 계약해야 합니다.

**DEV**: 음, 그건 선박 항해 일정에서 유도할 수 있습니다. 테이블 데이터는 정규화되어 있습니다.

**EXP**: 예, 날짜가 필요할 것입니다. 운영팀은 이러한 일정을 사용하여 다가오는 작업을 계획합니다.

**DEV**: 네... 알겠습니다. 운영 관리 애플리케이션은 각 하역 작업의 날짜와 함께 전체 탑재 및 하역 시퀀스를 제공할 수 있습니다. 일종의 '여정'이라고 할 수 있겠죠.

**EXP**: 좋습니다. 여정이 운영팀이 필요로 하는 주요 정보입니다. 사실, 예약 애플리케이션에는 고객에게 여정을 인쇄하거나 이메일로 보낼 수 있는 메뉴 항목이 있습니다. 그걸 어떻게 사용할 수 있나요?

**DEV**: 그건 단지 보고서일 뿐입니다. 운영 애플리케이션을 기반으로 할 수는 없습니다. \[DEV는 생각에 잠김.]

**DEV**: 그래서 이 여정은 예약과 운영을 연결하는 고리군요.

**EXP**: 예, 일부 고객 관계도 포함됩니다.

**DEV**: \[화이트보드에 다이어그램을 그리며.] 그렇다면 이렇게 생긴 건가요?

![Routing Service Diagram](https://via.placeholder.com/400)

**EXP**: 네, 기본적으로 맞습니다. 각 구간에서 선박 항해, 탑재 위치 및 하역 위치와 시간을 보고 싶습니다.

**DEV**: 이 객체를 생성하면 선박 항해 일정에서 시간을 유도할 수 있습니다. 이 객체를 운영 애플리케이션과의 주요 접점으로 만들 수 있습니다. 그리고 예약 애플리케이션의 여정 보고서를 이것을 사용하도록 다시 작성하여 도메인 로직을 도메인 레이어로 되돌릴 수 있습니다.

**EXP**: 모두 이해하지는 못했지만, 여정의 두 가지 주요 사용 용도가 예약 보고서와 운영 애플리케이션에 있다는 것은 맞습니다.

**DEV**: 좋습니다! 라우팅 서비스 인터페이스가 데이터베이스 테이블 대신 여정 객체를 반환하도록 할 수 있습니다. 이렇게 하면 라우팅 엔진이 우리의 테이블을 알 필요가 없게 됩니다.

**EXP**: 무슨 말인지 잘 모르겠지만요.

**DEV**: 라우팅 엔진이 여정을 반환하도록 하겠습니다. 그런 다음 나머지 예약이 저장될 때 예약 애플리케이션이 데이터베이스에 저장할 수 있습니다.

**EXP**: 지금은 그렇게 되어 있지 않다는 말인가요?!

이 시점에서 개발자는 라우팅 프로세스에 관여하는 다른 개발자들과 대화를 나누었다. 모델 변경 사항과 설계에 미치는 영향을 논의하고 필요할 때 배송 전문가에게 문의했다. 그들은 다음과 같은 결과를 도출했다.

![Itinerary Model Diagram](https://via.placeholder.com/400)

개발자는 배송 전문가가 '여정' 개념을 얼마나 중요한지 인식할 수 있을 만큼 면밀히 경청하고 있었다. 물론, 모든 데이터가 이미 수집되고 있었으며, 여정 보고서에 암시적으로 포함되어 있었지만, 모델의 일부로서 명시적인 여정은 새로운 기회를 열어주었다.

**여정 객체로 리팩토링의 이점**:

1. 라우팅 서비스 인터페이스의 표현력 향상.
2. 예약 데이터베이스 테이블로부터 라우팅 서비스의 분리.
3. 예약 애플리케이션과 운영 지원 애플리케이션 간의 명확한 관계 (여정 객체 공유).
4. 중복 감소, 여정이 예약 보고서와 운영 지원 애플리케이션 모두에 대한 하역 시간을 도출.
5. 도메인 로직을 예약

보고서에서 격리된 도메인 레이어로 이동. 6. UBIQUITOUS LANGUAGE 확장, 개발자와 도메인 전문가 및 개발자 간의 모델 및 설계에 대한 더 정확한 논의 가능.

#### 어색함을 조사하라

필요한 개념은 항상 표면에 떠오르지 않는다. 설계의 가장 어색한 부분을 조사해야 할 수도 있다. 절차가 복잡한 작업을 수행하는 부분. 새로운 요구 사항이 복잡성을 더하는 부분.

때로는 누락된 개념이 있는지조차 인식하기 어렵다. 객체가 모든 작업을 수행하지만, 일부 책임이 어색하다고 느낄 수 있다. 또는 누락된 개념을 인식하더라도 모델 솔루션이 쉽게 떠오르지 않을 수 있다.

이제 도메인 전문가와 적극적으로 협력해야 한다. 운이 좋다면, 그들은 아이디어를 가지고 모델을 실험하는 것을 즐길 수 있다. 그렇지 않다면, 도메인 전문가를 검증자로 사용하고, 그들의 얼굴에서 불편함이나 인식을 주의 깊게 살펴보아야 한다.

#### 예제: 어려운 방법으로 이자 벌기

다음 이야기는 상업 대출 및 기타 이자 수익 자산에 투자하는 가상의 금융 회사에서 일어난다. 애플리케이션은 이러한 투자를 추적하고 그로부터 얻은 수익을 계산하는 기능을 점진적으로 추가하면서 발전해왔다. 매일 밤, 배치 스크립트가 실행되어 모든 이자 및 수수료 수익을 계산하고 회사의 회계 소프트웨어에 적절히 기록했다.

**어색한 모델**

![Awkward Model](https://via.placeholder.com/400)

모델 측면에서, 야간 배치 스크립트는 각 자산을 반복하면서 해당 날짜의 날짜에 대해 `calculateInterestForDate()`를 호출했다. 스크립트는 반환 값을 받아 특정 원장에 게시할 금액과 함께 이를 회계 프로그램의 공개 인터페이스에 전달했다. 이 소프트웨어는 금액을 명명된 원장에 게시했다. 스크립트는 각 자산의 당일 수수료를 가져와 다른 원장에 게시하는 유사한 과정을 거쳤다.

한 개발자는 이자 계산의 복잡성이 증가함에 따라 어려움을 겪고 있었다. 그녀는 작업에 더 적합한 모델을 찾을 기회를 포착하기 시작했다. 이 개발자(DEV)는 그녀의 좋아하는 도메인 전문가(EXP)에게 문제 영역을 탐색하는 데 도움을 요청했다.

**DEV**: 이자 계산기가 손을 쓸 수 없을 정도로 복잡해지고 있어요.

**EXP**: 그 부분은 복잡하죠. 여전히 더 많은 사례가 있는데 보류하고 있었습니다.

**DEV**: 알아요. 다른 이자 유형을 추가하기 위해 다른 이자 계산기를 대체할 수 있지만, 지금 가장 어려운 것은 일정에 맞춰 이자를 지급하지 않을 때 발생하는 모든 특수 사례입니다.

**EXP**: 그것들은 정말 특수 사례가 아닙니다. 사람들이 언제 지급할지에 대한 유연성이 많아요.

**DEV**: 이자 계산기를 자산에서 분리했을 때 많이 도움이 되었죠. 더 분리해야 할 수도 있겠어요.

**EXP**: 좋아요.

**DEV**: 이자 계산에 대해 이야기할 방법이 있을까요?

**EXP**: 무슨 말인가요?

**DEV**: 예를 들어, 우리는 회계 기간 동안 지급되지 않은 이자를 추적하고 있습니다. 그에 대한 이름이 있나요?

**EXP**: 실제로는 그렇게 하지 않아요. 이자 수익과 지급은 별개의 게시입니다.

**DEV**: 그렇다면 그 숫자가 필요하지 않다는 말인가요?

**EXP**: 때로는 확인할 수도 있지만, 그건 우리가 사업을 하는 방식은 아닙니다.

**DEV**: 좋아요, 그러면 지급과 이자를 분리해서 모델링하는 게 맞을 것 같아요. 이렇게 하면 어떨까요? \[화이트보드에 그림을 그리며]

**EXP**: 일리 있네요. 하지만 그냥 한 곳에서 다른 곳으로 옮긴 것 같은데요.

**DEV**: 이제 이자 계산기는 이자 수익만 추적하고, 지급은 그 숫자를 별도로 유지합니다. 많이 단순화되지는 않았지만, 귀사의 비즈니스 관행을 더 잘 반영하나요?

**EXP**: 아, 이해했습니다. 이자 기록도 있으면 좋겠네요. 지급 기록처럼요.

**DEV**: 네, 새로운 기능으로 요청되었습니다. 그러나 원래 디자인에 추가할 수도 있었을 것입니다.

**EXP**: 아. 이자와 지급 기록이 분리된 것을 보고 이자를 더 잘 조직하려는 것 같았습니다. 발생주의 회계를 알고 있나요?

**DEV**: 설명해 주세요.

**EXP**: 매일 또는 일정이 요구하는 대로, 우리는 원장에 게시되는 이자 발생을 가지고 있습니다. 지급은 다른 방식으로 게시됩니다. 여기 있는 집합은 조금 어색합니다.

**DEV**: 각 날짜 또는 기간 동안 하나의 발생만 계산되면 이자 계산이 더 간단해집니다. 그리고 그것들을 모두 유지할 수 있습니다. 이렇게 하면 어떨까요?

**EXP**: 네, 좋아 보입니다. 왜 이게 더 쉬운지 잘 모르겠지만요. 기본적으로 어떤 자산이든 이자, 수수료 등을 발생시킬 수 있는 것이 그 가치를 결정합니다.

**DEV**: 수수료도 같은 방식으로 작동하나요? 다른 원장에 게시된다고 했죠?

![Refactored Model](https://via.placeholder.com/400)

새로운 모델의 이점:

1. UBIQUITOUS LANGUAGE에 '발생'이라는 용어를 추가하여 용어를 풍부하게 함.
2. 발생과 지급을 분리.
3. 스크립트에서 도메인 계층으로 도메인 지식 이동 (어느 원장에 게시할지 등).
4. 수수료와 이자를 비즈니스에 맞게 결합하고 코드 중복 제거.
5. 발생 스케줄로 새로운 수수료와 이자 변형을 쉽게 추가할 수 있는 경로 제공.

개발자는 필요한 새로운 개념을 찾기 위해 깊이 파고들었다. 그녀는 이자 계산의 어색함을 인식하고, 더 깊은 해답을 찾기 위해 헌신적인 노력을 기울였다. 그녀는 은행 전문가와 함께 작업하는 행운이 있었다. 덜 적극적인 전문가와 작업했을 경우, 그녀는 더 많은 실수를 하고, 다른 개발자들과의 브레인스토밍에 더 많이 의존했을 것이다. 진행 속도는 더 느렸겠지만, 여전히 가능했을 것이다.

#### 모순을 숙고하라

다양한 도메인 전문가들이 경험과 필요에 따라 사물을 다르게 본다. 동일한 사람도 신중히 분석하면 논리적으로 일관되지 않은 정보를 제공할 수 있다. 이러한 모순은 종종 더 깊은 모델에 대한 단서가 될 수 있다.

갈릴레오가 한 번 역설을 제기한 적이 있다. 감각의 증거는 지구가 정지해 있다는 것을 명확히 나타낸다. 그러나 코페르니쿠스는 지구가 태양 주위를 매우 빠르게 움직이고 있다고 설득력 있게 주장했다. 이를 조화시키면 자연이 어떻게 작동하는지에 대한 중요한 통찰을 얻을 수 있다.

갈릴레오는 사고 실험을 고안했다. 만약 기수가 달리는 말에서 공을 떨어뜨리면 어디로 떨어질까? 물론, 공은 말과 함께 움직여 말의 발치에 떨어질 것이다. 이로부터 그는 초기 형태의 관성 좌표계를 유도하여 역설을 해결하고, 운동 물리학에 대한 훨씬 유용한 모델로 이어졌다.

우리의 모순은 보통 그다지 흥미롭지 않으며, 그 함축도 그렇게 깊지 않다. 그럼에도 불구하고, 이와 같은 사고 패턴은 문제 도메인의 피상적인 층을 뚫고 더 깊은 통찰로 이어질 수 있다.

#### 책을 읽어라

모델 개념을 찾을 때 분명한 것을 간과하지 말라. 많은 분야에서 기본 개념과 관습적인 지혜를 설명하는 책을 찾을 수 있다. 여전히 도메인 전문가와 협력하여 문제에 관련된 부분을 추출하고 객체 지향 소프트웨어에 적합하게 압축해야 한다. 그러나 시작점으로서 일관되고 깊이 생각된 관점을 찾을 수 있다.

#### 예제: 이자 수익 다시 살펴보기

앞서 논의된 투자 추적 애플리케이션에 대해 다른 시나리오를 상상해 보자. 이전과 마찬가지로, 이야기는 개발자가 설계가 번잡해지고 특히 이자 계산기가 복잡해지는 것을 깨닫는 것에서 시작된다. 그러나 이 시나리오에서는 도메인 전문가의 주요 책임이 다른 곳에 있으며, 그는 소프트웨어 개발 프로젝트에 큰 관심이 없다. 이 시나리오에서 개발자는 실종된 개념이 표면 아래에 숨어 있다고 의심하면서도, 도메인 전문가에게 도움을 요청할 수 없

었다.

대신, 그녀는 서점에 갔다. 조금 둘러본 후, 그녀는 좋아하는 회계 입문서를 찾아 훑어보았다. 그녀는 잘 정의된 개념 체계를 발견했다. 특히 그녀의 사고를 자극한 발췌문:

"발생주의 회계. 이 방법은 소득이 발생했을 때 인식하며, 지급되지 않았더라도 이를 기록한다. 모든 비용도 발생했을 때 기록되며, 이를 나중에 지불하든 청구하든 간에 이를 반영한다. 모든 의무, 포함 세금은 비용으로 표시된다."

그녀는 더 이상 회계를 재발명할 필요가 없었다. 다른 개발자와의 브레인스토밍 후, 그녀는 모델을 만들었다.

![Deeper Model Based on Book Learning](https://via.placeholder.com/400)

그녀는 자산이 수익 생성기라는 통찰을 얻지 못했지만, 여전히 '계산기'가 있다. 원장에 대한 지식도 여전히 애플리케이션에 포함되어 있으며, 도메인 레이어에 속하지 않는다. 그러나 그녀는 수익의 발생 문제를 지급 문제와 분리하여 복잡성의 주요 원인을 제거하고, '발생'이라는 용어를 모델과 UBIQUITOUS LANGUAGE에 도입했다. 이후 반복을 통해 추가적인 세부화가 이루어질 수 있었다.

그녀가 도메인 전문가와 대화할 기회를 가졌을 때, 그는 매우 놀랐다. 처음으로 프로그래머가 그가 하는 일에 관심을 보였다. 그의 책임 할당 방식으로 인해, 그는 이전 시나리오에서처럼 그녀와 협력하지 않았다. 그러나 그녀의 지식 덕분에 더 나은 질문을 할 수 있었고, 그는 앞으로 질문에 신속히 답변하고 그녀의 말을 더 주의 깊게 들었다.

물론, 이는 양자택일의 문제가 아니다. 도메인 전문가의 충분한 지원이 있어도, 이론을 이해하기 위해 문헌을 보는 것이 유익하다. 대부분의 비즈니스는 회계나 금융 수준으로 정제된 모델을 가지고 있지 않지만, 많은 분야에서 비즈니스의 일반 관행을 조직하고 추상화한 사상가들이 있다.

또한, 그녀는 \[Fowler 1997]의 'Analysis Patterns: Reusable Object Models'와 같은 것을 읽을 수도 있었다. 이는 그녀를 전혀 다른 방향으로 이끌었을 수도 있다. 이러한 모델들은 그녀에게 맞춤형 솔루션을 제공하지는 않겠지만, 자신의 실험을 위한 새로운 출발점을 제공하고, 이전에 이와 같은 소프트웨어를 작성한 사람들의 축적된 경험을 제공했을 것이다. 이를 통해 그녀는 바퀴를 재발명하는 일을 피할 수 있었다. 11장에서 '분석 패턴 적용'에 대해 더 깊이 다룰 것이다.

#### 시도하고, 또 시도하라

이 예제들은 많은 시행착오가 포함되어 있음을 전달하지 않는다. 나는 대화에서 유용하고 명확한 모델을 찾기 전에 여러 단서를 따를 것이다. 그 후에도 더 나은 아이디어를 제공하는 추가 경험과 지식 축적으로 인해 그 모델을 한 번 이상 교체하게 될 것이다. 모델러/디자이너는 자신의 아이디어에 집착할 여유가 없다.

이러한 방향 전환은 단순한 몸부림이 아니다. 각 변화는 모델에 더 깊은 통찰을 내포한다. 각 리팩토링은 설계를 더 유연하게 만들어 다음 번 변경이 더 쉬워지고, 필요할 때 구부릴 준비가 된 상태로 만든다.

어쨌든 실험은 배울 수 있는 방법이다. 설계에서 실수를 피하려고 하면 경험이 적은 상태에서 결과물이 나올 수 있다. 그리고 빠른 실험 시리즈보다 시간이 더 오래 걸릴 수도 있다.

#### 덜 명백한 개념 범주 표현하기

객체 지향 패러다임은 특정 종류의 개념을 찾고 발명하게 만든다. 매우 추상적인 것조차도 '명사와 동사'는 대부분의 객체 모델의 핵심이다. 그러나 모델에서도 명시적으로 표현할 수 있는 다른 중요한 개념 범주가 있다.

나는 객체 지향을 시작할 때 명백하지 않았던 세 가지 범주를 논의할 것이다. 각 범주를 배울 때마다 내 설계가 더욱 날카로워졌다.

#### 명시적 제약

제약은 특히 중요한 모델 개념 범주를 이룬다. 종종 암묵적으로 나타나며, 이를 명시적으로 표현하면 설계를 크게 개선할 수 있다.

때때로 제약은 객체나 메서드에서 자연스럽게 자리를 잡는다. '버킷' 객체는 용량을 초과하지 않는 불변성을 보장해야 한다.

![Bucket Constraint](https://via.placeholder.com/400)

이 간단한 불변성은 내용물을 변경할 수 있는 각 작업에서 조건 논리를 사용하여 강제할 수 있다.

```java
class Bucket {
    private float capacity;
    private float contents;

    public void pourIn(float addedVolume) {
        if (contents + addedVolume > capacity) {
            contents = capacity;
        } else {
            contents = contents + addedVolume;
        }
    }
}
```

이는 너무 간단해서 규칙이 명백하다. 그러나 더 복잡한 클래스에서는 이 제약이 쉽게 잊혀질 수 있다. 이를 별도의 메서드로 분리하여 제약의 중요성을 명확히 표현하는 것이 좋다.

```java
class Bucket {
    private float capacity;
    private float contents;

    public void pourIn(float addedVolume) {
        float volumePresent = contents + addedVolume;
        contents = constrainedToCapacity(volumePresent);
    }

    private float constrainedToCapacity(float volumePlacedIn) {
        if (volumePlacedIn > capacity) return capacity;
        return volumePlacedIn;
    }
}
```

두 코드 모두 제약을 강제하지만, 두 번째 버전은 모델과의 명확한 관계를 만든다. 이 매우 간단한 규칙은 원래 형태에서도 이해할 수 있었지만, 강제되는 규칙이 더 복잡해지면 이를 별도의 메서드로 분리하는 것이 좋다. 이를 통해 제약을 명시적으로 표현하고, 이를 논의할 수 있는 명명된 개념으로 만든다. 또한 제약이 복잡해질 수 있는 공간을 제공한다.

이 별도의 메서드는 제약에 공간을 제공하지만, 단일 메서드에 잘 맞지 않는 제약도 많다. 또는 메서드가 단순하더라도 객체가 기본 책임을 위해 필요하지 않은 정보를 호출할 수 있다. 제약이 기존 객체에 좋은 자리가 없을 수도 있다. 제약이 호스트 객체의 설계를 왜곡하는 경고 신호는 다음과 같다:

1. 제약을 평가하기 위해 객체 정의에 맞지 않는 데이터가 필요하다.
2. 관련 규칙이 여러 객체에 나타나 중복이나 상속을 강제한다.
3. 설계 및 요구 사항 대화가 주로 제약에 초점을 맞추고 있지만, 구현에서는 절차 코드에 숨겨져 있다.

제약이 객체의 기본 책임을 가리고 있거나, 도메인에서 눈에 띄지만 모델에서는 눈에 띄지 않는 경우, 이를 명시적인 객체로 분리하거나 객체와 관계의 집합으로 모델링할 수 있다.

#### 예제: 오버부킹 정책

1장에서 다룬 예제에서, 한 해운 회사는 운송량을 초과하여 10% 더 예약하는 비즈니스 관행을 가지고 있었다. 이는 마지막 순간 취소를 보상하여 선박이 거의 꽉 찬 상태로 항해할 수 있게 한다.

이 제약은 새로운 클래스를 추가하여 명시적으로 표현되었다.

![Overbooking Policy](https://via.placeholder.com/400)

#### 도메인 객체로서 프로세스 표현하기

먼저, 절차를 모델의 주요 측면으로 만들고 싶지 않다는 것을 동의하자. 객체는 절차를 캡슐화하여 그 목표나 의도를 생각하게 해야 한다.

여기서 이야기하는 것은 도메인에 존재하는 프로세스로, 모델에서 표현해야 하는 것이다. 이러한 프로세스가 등장하면 객체 설계를 어색하게 만드는 경향이 있다.

5장에서 도메인 서비스를 설명하는 예제로, 화물을 경로 지정하는 해운 시스템을 설명했다. 이 라우팅 프로세스는 비즈니스에서 수행하는 것이다. 서비스는 이러한 프로세스를 명시적으로 표현하는 한 방법이며, 복잡한 알고리즘을 캡슐화할 수 있다.

프로세스를 수행하는 방법이 여러 가지인 경우, 또 다른 접근 방식은 알고리즘 자체 또는 그 일부를 독립된 객체로 만드는 것이다. 프로세스 간의 선택은 이러한 객체 간의 선택이 된다.

제약과 프로세스는 객체 지향 언어로 프로그래밍할 때 떠오르지 않는 모델 개념의 두 가지 광범위한 범주다. 그러나 이를 모델 요소로 생각하기 시작하면 설계가 훨씬 더 날카로워질 수 있다.

다음 범주는 훨씬 더 구체적이지만 매우 일반적이다. **SPECIFICATION**은 특정 종류의 규칙을 간결하게 표현하고, 이를 조건 논

리에서 분리하여 모델에서 명시적으로 만든다.

이제 **SPECIFICATION** 패턴에 대해 논의할 것이다. 이는 모델링 문제에 대한 해결책을 제공하며, 모델 중심 설계를 유지할 수 있다.

#### SPECIFICATION

다양한 애플리케이션에서 부울 테스트 메서드는 실제로 작은 규칙의 일부다. 간단할 때는 `Iterator.hasNext()` 또는 `Invoice.isOverdue()`와 같은 테스트 메서드로 처리한다. `isOverdue()`의 코드는 규칙을 평가하는 알고리즘이다.

```java
public boolean isOverdue() {
    Date currentDate = new Date();
    return currentDate.after(dueDate);
}
```

그러나 모든 규칙이 그렇게 간단하지는 않다. 같은 `Invoice` 클래스에서 다른 규칙인 `anInvoice.isDelinquent()`은 아마도 `Invoice`가 연체되었는지 테스트하는 것부터 시작할 것이다. 그러나 그것이 전부는 아니다. 고객 계좌의 상태에 따라 유예 기간 정책이 다를 수 있다. 일부 연체된 송장은 두 번째 알림을 보낼 준비가 되었고, 다른 송장은 수금 대행사에 보낼 준비가 되었을 것이다. 고객의 결제 이력, 회사의 다른 제품 라인에 대한 정책... `Invoice`의 명확성은 곧 규칙 평가 코드의 양으로 인해 상실될 것이다. `Invoice`는 기본 의미를 지원하지 않는 도메인 클래스 및 하위 시스템에 대한 모든 종류의 종속성을 개발할 것이다.

이 시점에서, 개발자는 종종 규칙 평가 코드를 애플리케이션 계층으로 이동시키려고 할 것이다. 이제 규칙은 도메인 계층에서 완전히 분리되어 데이터 객체로 남게 된다. 이러한 규칙은 도메인 계층에 있어야 하지만, 도메인 객체의 책임에 맞지 않는다. 평가 메서드는 조건 논리로 인해 부피가 커지고, 규칙을 읽기 어렵게 만든다.

논리 프로그래밍 패러다임에서 작업하는 개발자는 이를 다르게 처리할 것이다. 이러한 규칙은 '서술자'로 표현된다. 서술자는 '참' 또는 '거짓'으로 평가되는 함수이며, 이를 결합하여 복잡한 규칙을 표현할 수 있다. 서술자가 있다면, 규칙을 명시적으로 선언하고 `Invoice`와 함께 사용할 수 있다. 논리 패러다임에 있으면 좋겠지만, 그렇지 않다.

이에 따라 객체로 논리 규칙을 구현하려는 시도가 있었다. 일부 시도는 매우 정교했고, 다른 일부는 순진했다. 일부는 야심차게, 일부는 소박하게 시도했다. 일부는 유용했고, 일부는 실패한 실험으로 버려졌다. 몇몇은 프로젝트를 탈선시키기도 했다. 한 가지는 명확하다. 객체로 논리를 완전히 구현하는 것은 주요 작업이다.

다행히도 우리는 큰 이익을 얻기 위해 이를 완전히 구현할 필요가 없다. 대부분의 규칙은 몇 가지 특수한 경우에 해당한다. 우리는 서술자의 개념을 차용하여 부울로 평가되는 특수 목적의 객체를 만들 수 있다. 이러한 테스트 메서드가 넘쳐날 때, 이를 별도의 값 객체로 분리할 수 있다. 이 새로운 객체는 다른 객체를 평가하여 서술된 기준을 충족하는지 여부를 확인할 수 있다.

#### 예제: 연체 송장 규칙을 SPECIFICATION으로 분리

```java
public class DelinquentInvoiceSpecification {
    private Date currentDate;

    public DelinquentInvoiceSpecification(Date currentDate) {
        this.currentDate = currentDate;
    }

    public boolean isSatisfiedBy(Invoice candidate) {
        int gracePeriod = candidate.customer().getPaymentGracePeriod();
        Date firmDeadline = DateUtility.addDaysToDate(candidate.dueDate(), gracePeriod);
        return currentDate.after(firmDeadline);
    }
}
```

이제 클라이언트 클래스에서 연체 송장을 확인하는 메서드를 작성할 수 있다.

```java
public boolean accountIsDelinquent(Customer customer) {
    Date today = new Date();
    Specification delinquentSpec = new DelinquentInvoiceSpecification(today);
    Iterator it = customer.getInvoices().iterator();
    while (it.hasNext()) {
        Invoice candidate = (Invoice) it.next();
        if (delinquentSpec.isSatisfiedBy(candidate)) return true;
    }
    return false;
}
```

#### 쿼리하기

유효성 검사는 개별 객체를 평가하여 기준을 충족하는지 확인하는 것이다. 또 다른 일반적인 필요는 컬렉션에서 기준에 따라 하위 집합을 선택하는 것이다. 이 개념은 여전히 적용되지만, 구현 문제가 다르다.

예를 들어, 연체 송장을 가진 모든 고객의 목록을 표시해야 한다면, 앞서 정의한 연체 송장 규격이 여전히 사용될 수 있지만, 구현은 아마도 변경되어야 한다. 먼저, 인메모리에서 작업할 수 있다고 가정해보자. 이 경우, 유효성 검사에 사용된 직관적인 구현이 여전히 유효하다. 송장 리포지토리는 특정 조건을 만족하는 송장을 선택하는 일반화된 메서드를 가질 수 있다.

```java
public Set selectSatisfying(InvoiceSpecification spec) {
    Set results = new HashSet();
    Iterator it = invoices.iterator();
    while (it.hasNext()) {
        Invoice candidate = (Invoice) it.next();
        if (spec.isSatisfiedBy(candidate)) results.add(candidate);
    }
    return results;
}
```

이렇게 하면 클라이언트는 단일 코드 문장으로 모든 연체 송장의 컬렉션을 얻을 수 있다.

```java
Set delinquentInvoices = invoiceRepository.selectSatisfying(new DelinquentInvoiceSpecification(currentDate));
```

이제 송장 객체가 아마도 인메모리에 없을 것이다. 수천 개의 송장이 있을 수 있다. 일반적인 비즈니스 시스템에서 데이터는 관계형 데이터베이스에 있을 가능성이 높다. 그리고 앞서 언급했듯이, 모델의 초점은 이러한 다른 기술과의 교차점에서 종종 잃어진다.

관계형 데이터베이스는 강력한 검색 기능을 제공한다. 이 문제를 효율적으로 해결하기 위해 SQL을 사용하는 방법을 모색해 보자. SQL은 명세를 작성하는 자연스러운 방법이다.

다음은 동일한 클래스에서 쿼리를 캡슐화한 매우 간단한 예다. `InvoiceSpecification`에 단일 메서드를 추가하고 `DelinquentInvoiceSpecification` 하위 클래스에서 구현했다.

```java
public String asSQL() {
    return "SELECT *" +
           " FROM INVOICE, CUSTOMER" +
           " WHERE INVOICE.CUST_ID = CUSTOMER.ID" +
           " AND TO_DAYS(INVOICE.DUE_DATE) + CUSTOMER.GRACE_PERIOD" +
           " < TO_DAYS(" + SQLUtility.dateAsSQL(currentDate) + ")";
}
```

이제, 리포지토리는 명세를 사용하여 데이터베이스에서 조건을 만족하는 객체를 선택할 수 있다.

![Repository and Specification Interaction](https://via.placeholder.com/400)

이 설계에는 몇 가지 문제가 있다. 가장 중요한 것은 테이블 구조의 세부 정보가 도메인 계층에 누출된다는 점이다. 이는 유지 보수성과 수정 가능성에 영향을 미칠 수 있다. 그러나 이는 규칙을 한 곳에 유지하는 간단한 방법을 보여준다. 일부 객체-관계 매핑 프레임워크는 이러한 쿼리를 모델 객체 및 속성으로 표현할 수 있는 수단을 제공하여 실제 SQL을 인프라 계층에서 생성한다.

인프라가 도움을 주지 않는다면, SQL을 리포지토리에서 분리하여 더 일반적인 방식으로 쿼리를 표현하는 방법이 있다. 이렇게 하면 규칙을 캡처하지 않고 결합하거나 컨텍스트에서 사용하여 규칙을 도출할 수 있다.

```java
public class InvoiceRepository {
    public Set selectWhereGracePeriodPast() {
        String sql = whereGracePeriodPast_SQL();
        ResultSet queryResultSet = SQLDatabaseInterface.instance().executeQuery(sql);
        return buildInvoicesFromResultSet(queryResultSet);
    }

    public String whereGracePeriodPast_SQL() {
        return "SELECT *" +
               " FROM INVOICE, CUSTOMER" +
               " WHERE INVOICE.CUST_ID = CUSTOMER.ID" +
               " AND TO_DAYS(INVOICE.DUE_DATE) + CUSTOMER.GRACE_PERIOD" +
               " < TO_DAYS(" + SQLUtility.dateAsSQL(currentDate) + ")";
    }

    public Set selectSatisfying(InvoiceSpecification spec) {
        return spec.satisfyingElementsFrom(this);
    }
}
```

이제 `InvoiceSpecification`의 `asSql()` 메서드는 `satisfyingElementsFrom(InvoiceRepository)`로 대체되며, `DelinquentInvoiceSpecification`에서는 다음과 같이 구현된다.

```java
public class DelinquentInvoiceSpecification {
    // 기본 DelinquentInvoiceSpecification 코드

    public Set satisfyingElementsFrom(InvoiceRepository repository) {
        return repository.selectWhereGracePeriodPast();
    }
}
```

이렇게 하면 SQL이 리포지토리에 남아 있으면서도 명세가 사용할 쿼리를 제어할 수 있다. 이는 명세에 규칙을 더 깔끔하게 수집하지는 않지만, 연체의

본질을 선언하는 것이 있다.

리포지토리는 이제 이 경우에만 사용될 매우 특화된 쿼리를 가지고 있다. 이는 받아들일 수 있지만, 연체된 송장보다 기한이 지난 송장이 상대적으로 더 많다면, 리포지토리 메서드를 더 일반적으로 유지하면서 성능을 개선할 수 있다.

```java
public class InvoiceRepository {
    public Set selectWhereDueDateIsBefore(Date aDate) {
        String sql = whereDueDateIsBefore_SQL();
        ResultSet queryResultSet = SQLDatabaseInterface.instance().executeQuery(sql);
        return buildInvoicesFromResultSet(queryResultSet);
    }

    public String whereDueDateIsBefore_SQL() {
        return "SELECT *" +
               " FROM INVOICE" +
               " WHERE TO_DAYS(INVOICE.DUE_DATE)" +
               " < TO_DAYS(" + SQLUtility.dateAsSQL(currentDate) + ")";
    }

    public Set selectSatisfying(InvoiceSpecification spec) {
        return spec.satisfyingElementsFrom(this);
    }
}
```

```java
public class DelinquentInvoiceSpecification {
    // 기본 DelinquentInvoiceSpecification 코드

    public Set satisfyingElementsFrom(InvoiceRepository repository) {
        Collection pastDueInvoices = repository.selectWhereDueDateBefore(currentDate);
        Set delinquentInvoices = new HashSet();
        Iterator it = pastDueInvoices.iterator();
        while (it.hasNext()) {
            Invoice anInvoice = (Invoice) it.next();
            if (this.isSatisfiedBy(anInvoice)) {
                delinquentInvoices.add(anInvoice);
            }
        }
        return delinquentInvoices;
    }
}
```

이는 성능 저하를 초래할 수 있다. 이는 상황에 따라 다르다. 여러 가지 방식으로 SPECIFICATIONS와 REPOSITORIES 간의 상호작용을 구현할 수 있으며, 이는 개발 플랫폼에 맞추어 선택할 수 있다.

때로는 성능을 개선하거나 보안을 강화하기 위해 쿼리가 서버에서 저장 프로시저로 구현될 수 있다. 이 경우 SPECIFICATION은 저장 프로시저가 허용하는 매개변수만 전달할 수 있다. 이러한 경우를 제외하면, 모델과 일치하는 한 어떤 구현도 자유롭게 선택할 수 있다.

#### 주문 제작 (생성)

펜타곤이 새로운 전투기를 원할 때, 사양을 작성한다. 이 사양은 전투기가 마하 2에 도달해야 하며, 1800마일의 범위를 가져야 하고, 5천만 달러 이하의 비용이 들어야 한다고 요구할 수 있다. 하지만, 사양은 설계도 아니고, 전투기 자체도 아니다. 항공우주 엔지니어링 회사는 이 사양을 바탕으로 하나 이상의 설계를 만든다. 경쟁 회사들은 서로 다른 설계를 제시할 수 있지만, 모두 원래 사양을 충족해야 한다.

많은 컴퓨터 프로그램은 무언가를 생성해야 하며, 이러한 생성된 객체를 명시해야 한다. 워드 프로세서에 그림을 삽입할 때, 텍스트는 그 주위를 흐른다. 그림의 위치와 텍스트 흐름 스타일을 지정하면, 워드 프로세서는 이러한 제약을 충족하도록 단어의 정확한 배치를 계산한다.

SPECIFICATION을 사용하면 생성기를 명확하게 정의하고, 생성된 객체를 명시적으로 제약할 수 있다.

**예제: 화학 창고 포장기**

화학 물질이 보관된 대형 용기(박스카와 유사한)로 쌓인 창고가 있다. 일부 화학 물질은 관성적이어서 거의 어디에나 저장할 수 있다. 일부는 휘발성이 있어 특별히 환기된 용기에 저장해야 한다. 일부는 폭발성이 있어 특별히 장갑된 용기에 저장해야 한다. 용기 내의 조합에 대한 규칙도 있다.

이제 소프트웨어를 작성하여 화학 물질을 용기에 안전하고 효율적으로 넣는 방법을 찾아야 한다.

![Chemical Warehouse Model](https://via.placeholder.com/400)

```java
public class ContainerSpecification {
    private ContainerFeature requiredFeature;

    public ContainerSpecification(ContainerFeature requiredFeature) {
        this.requiredFeature = requiredFeature;
    }

    boolean isSatisfiedBy(Container aContainer) {
        return aContainer.getFeatures().contains(requiredFeature);
    }
}
```

이제 폭발성 화학 물질을 설정하는 샘플 클라이언트 코드를 작성할 수 있다.

```java
tnt.setContainerSpecification(new ContainerSpecification(ARMORED));
```

`Container`에서 각 드럼이 요구 사항을 충족하는지 확인하는 메서드를 작성할 수 있다.

```java
public boolean isSafelyPacked() {
    ContainerSpecification packingSpec = getDrum().getContainerSpecification();
    Iterator it = contents.iterator();
    while (it.hasNext()) {
        Drum drum = (Drum) it.next();
        if (!drum.containerSpecification().isSatisfiedBy(this)) {
            return false;
        }
    }
    return true;
}
```

이제 모니터링 애플리케이션을 작성하여 재고 데이터베이스를 가져와 안전하지 않은 상황을 보고할 수 있다.

```java
Iterator it = containers.iterator();
while (it.hasNext()) {
    Container container = (Container) it.next();
    if (!container.isSafelyPacked()) {
        unsafeContainers.add(container);
    }
}
```

이 소프트웨어는 우리가 작성해야 하는 것이 아니다. 그러나 이를 통해 포장기를 테스트할 수 있다. 이 도메인 이해와 SPECIFICATION 기반 모델은 규칙을 명시적으로 표현하는 간단한 인터페이스를 정의할 수 있게 한다.

```java
public interface WarehousePacker {
    public void pack(Collection containersToFill, Collection drumsToPack) throws NoAnswerFoundException;
}
```

이제 제약 해결 책임을 가진 서비스 인터페이스를 정의할 수 있다.

```java
public class PrototypePacker implements WarehousePacker {
    public void pack(Collection containers, Collection drums) throws NoAnswerFoundException {
        Iterator it = drums.iterator();
        while (it.hasNext()) {
            Drum drum = (Drum) it.next();
            Container container = findContainerFor(containers, drum);
            container.add(drum);
        }
    }

    public Container findContainerFor(Collection containers, Drum drum) throws NoAnswerFoundException {
        Iterator it = containers.iterator();
        while (it.hasNext()) {
            Container container = (Container) it.next();
            if (container.canAccommodate(drum)) {
                return container;
            }
        }
        throw new NoAnswerFoundException();
    }
}
```

이것은 많은 것을 남기지만, 규칙을 따른다. 복잡한 구성 요소의 프로토타입을 작동시키는 간단한 예를 제공한다.

이 모델을 통해 복잡한 구성 요소의 작동 프로토타입을 몇 줄의 코드로 구현할 수 있다. 덜 모델 중심적인 접근 방식은 이해하기 어렵고, 업그레이드하기 더 어려우며, 프로토타입을 만드는 데 더 오래 걸릴 것이다.
