# 성능 테스트 패턴 및 안티패턴

성능 테스트나 팀에 문제를 일으킬 수 있는 일반적인 안티패턴 몇 가지를 개략적으로 설명하고, \
이러한 문제가 팀에 문제가 되지 않도록 방지하는 리팩토링된 해결책을 설명할 것입니다.

### 성능 테스트의 유형

성능 테스트는 종종 잘못된 이유로 또는 잘못 수행되어 집니다. 그 이유는 매우 다양하지만, 성능 분석의 본질을 이해하지 못하거나 "뭔가 하는 것이 아무 것도 하지 않는 것보다 낫다"는 믿음에서 비롯되는 경우가 많습니다. 우리가 반복해서 보게 될 것처럼, 이 믿음은 기껏해야 위험한 반진실인 경우가 많습니다.

더 흔한 실수 중 하나는 "성능 테스트"에 대해 일반적으로 이야기하면서 구체적인 사항에 참여하지 않는 것입니다. 실제로 시스템에서 수행할 수 있는 다양한 대규모 성능 테스트 유형이 많이 있습니다.

**참고 사항**

좋은 성능 테스트는 정량적입니다. 이는 실험 결과로 처리되고 통계 분석의 대상이 될 수 있는 숫자 답변을 생성하는 질문을 던집니다.

우리가 이 책에서 논의할 성능 테스트 유형은 대체로 독립적이지만 약간 겹치는 목표를 가지고 있으므로, 특정 테스트의 도메인을 생각할 때 주의해야 합니다. 성능 테스트를 계획할 때 좋은 경험 법칙은 테스트가 답하려는 정량적 질문과 그것이 테스트 대상 애플리케이션에 왜 중요한지를 단순히 적어보고 하는 것입니다.

```
지연 시간 테스트 (Latency test)
끝에서 끝까지의 트랜잭션 시간은 얼마인가?

처리량 테스트 (Throughput test)
현재 시스템 용량으로 몇 개의 동시 트랜잭션을 처리할 수 있는가?

부하 테스트 (Load test)
시스템이 특정 부하를 처리할 수 있는가?

스트레스 테스트 (Stress test)
시스템의 한계점은 어디인가?

지속성 테스트 (Endurance test)
시스템을 장기간 실행할 때 어떤 성능 이상이 발견되는가?

용량 계획 테스트 (Capacity planning test)
추가 자원을 추가할 때 시스템이 예상대로 확장되는가?

감쇠 테스트 (Degradation test)
시스템이 부분적으로 실패할 때 어떻게 되는가?
```

### 모범 사례 입문

성능 튜닝 연습에서 노력을 집중할 곳을 결정할 때, 유용한 지침을 제공할 수 있는 세 가지 황금 규칙이 있습니다:

1. **중요한 것을 식별하고 그것을 측정하는 방법을 알아내라.**
2. **최적화할 것은 중요한 것을 최적화하고, 쉽게 최적화할 수 있는 것을 최적화하지 마라.**
3. **큰 포인트를 먼저 다루라.**

두 번째 포인트에는 반대가 있는데, 이는 측정하기 쉬운 어떤 관찰치에 너무 많은 중요성을 부여하지 말라는 것입니다. 모든 관찰치가 비즈니스에 중요하지는 않지만, 올바른 측정을 하기보다 쉬운 측정에 대해 보고하는 유혹에 빠지기 쉽습니다.

### 톱-다운 성능

Java 성능의 많은 측면을 처음 접하는 엔지니어들이 놓치는 것 중 하나는, Java 애플리케이션의 대규모 벤치마킹이 코드의 작은 부분에 대한 정확한 숫자를 얻으려고 하는 것보다 보통 더 쉽다는 점입니다. 우리는 이것을 5장에서 자세히 논의할 것입니다.

#### 테스트 환경 구축

테스트 환경 설정은 대부분의 성능 테스트 팀이 수행해야 할 첫 번째 작업 중 하나입니다. 가능한 한, 이는 모든 측면에서 프로덕션 환경과 정확히 동일한 복제본이어야 합니다. 여기에는 애플리케이션 서버(서버는 동일한 수의 CPU, 동일한 버전의 OS 및 Java 런타임 등을 가져야 함)뿐만 아니라 웹 서버, 데이터베이스, 로드 밸런서, 네트워크 방화벽 등도 포함됩니다. (복제하기 어렵거나 프로덕션에 상응하는 부하를 처리할 충분한 QA 용량이 없는) 모든 서비스는 대표적인 성능 테스트 환경을 위해 모킹(mocking)해야 합니다.

때때로 팀은 기존 QA 환경을 재사용하거나 시간 공유하려고 합니다. 이는 소규모 환경이나 일회성 테스트의 경우 가능할 수 있지만, 관리 오버헤드와 스케줄링 및 물류 문제를 과소평가해서는 안 됩니다.

#### 성능 요구 사항 식별

"A Simple System Model"에서 만난 단순한 시스템 모델을 다시 떠올려 봅시다. 이는 시스템의 전체 성능이 애플리케이션 코드만으로 결정되지 않는다는 것을 명확하게 보여줍니다. 컨테이너, 운영 체제, 하드웨어 모두가 역할을 합니다.

따라서 성능을 평가하는 데 사용할 메트릭은 코드 측면만 고려해서는 안 됩니다. 대신, 전체 시스템과 고객 및 경영진에게 중요한 관찰 가능한 양을 고려해야 합니다. 이는 일반적으로 \*\*성능 비기능 요구 사항 (Performance Nonfunctional Requirements, NFRs)\*\*이라고 하며, 우리가 최적화하고자 하는 주요 지표입니다.

이러한 목표를 정확히 측정하고 달성할 수 있도록 이해관계자들과의 개방적인 논의가 필수적입니다. 이상적으로는 이 논의가 성능 연습의 첫 번째 킥오프 미팅의 일부를 형성해야 합니다.



### Java 특정 문제

성능 분석의 과학 대부분은 모든 현대 소프트웨어 시스템에 적용 가능하지만, JVM의 특성상 성능 엔지니어가 인지하고 신중히 고려해야 할 추가적인 복잡성이 있습니다. 이는 메모리 영역의 동적 튜닝과 같은 JVM의 동적 자체 관리 기능에서 주로 비롯됩니다.

특히 중요한 Java 특정 통찰력 중 하나는 JIT 컴파일과 관련이 있습니다. 현대 JVM은 어떤 메서드가 실행되고 있는지를 분석하여 최적화된 기계 코드로 JIT 컴파일할 후보를 식별합니다. 이는 메서드가 JIT 컴파일되지 않는다면 다음 두 가지 중 하나라는 것을 의미합니다:

1. 메서드가 충분히 자주 실행되지 않아 컴파일할 가치가 없음.
2. 메서드가 너무 크거나 복잡하여 컴파일을 위해 분석할 수 없음.

두 번째 조건은 첫 번째 조건보다 훨씬 드물지만, JVM 기반 애플리케이션의 초기 성능 연습 중 하나는 중요한 애플리케이션의 주요 코드 경로에 있는 중요한 메서드가 컴파일되고 있는지 확인하기 위해 간단한 메서드 컴파일 로그를 켜는 것입니다.

### 소프트웨어 개발 생명주기의 성능 테스트

일부 회사와 팀은 성능 테스트를 가끔 하는 일회성 활동으로 생각하는 것을 선호합니다. 그러나 보다 정교한 팀은 지속적인 성능 테스트, 특히 성능 회귀 테스트를 소프트웨어 개발 생명주기(SDLC)의 필수적인 부분으로 만드는 경향이 있습니다. 이는 테스트 환경에서 어떤 코드 버전이 현재 존재하는지를 제어하기 위해 개발자와 인프라 팀 간의 협업을 필요로 하며, 전용 테스트 환경 없이는 거의 불가능합니다.

성능을 위한 가장 일반적인 모범 사례 중 일부를 논의한 후, 이제 팀이 빠질 수 있는 함정과 안티패턴에 주목하겠습니다.

### 성능 안티패턴 소개

안티패턴은 소프트웨어 프로젝트나 팀에서 관찰되는 바람직하지 않은 행동입니다. 많은 프로젝트에서 발생하는 빈도로 인해, 일부 기본 요인이 바람직하지 않은 행동을 유발하는 것으로 결론지어지거나 의심됩니다. 일부 안티패턴은 처음에는 정당해 보일 수 있으며, 비이상적인 측면이 즉각적으로 명확하지 않을 수 있습니다. 다른 안티패턴은 시간이 지남에 따라 천천히 축적된 부정적인 프로젝트 관행의 결과일 수 있습니다.

경우에 따라 이러한 행동은 사회적 또는 팀의 제약에 의해, 또는 일반적인 잘못 적용된 관리 기술에 의해, 또는 단순한 인간(및 개발자)의 본성에 의해 유도될 수 있습니다. 이러한 바람직하지 않은 특징을 분류하고 범주화함으로써, 우리는 이를 논의하고 프로젝트에서 제거하는 데 도움이 되는 "패턴 언어"를 개발합니다.

성능 튜닝은 항상 매우 객관적인 프로세스로 다뤄져야 하며, 계획 단계 초기에 정확한 목표가 설정되어야 합니다. 이는 말처럼 쉽지 않습니다: 팀이 압박을 받거나 합리적인 상황에서 운영되지 않는 경우, 이는 단순히 무시될 수 있습니다.

많은 독자는 새로운 클라이언트가 라이브로 전환되거나 새로운 기능이 출시되는 상황을 보았을 것이며, 예를 들어 사용자 수락 테스트(UAT)에서 운이 좋으면, 하지만 종종 프로덕션에서 예상치 못한 중단이 발생합니다. 팀은 보통 성능 테스트가 수행되지 않았거나, 팀의 "닌자(ninja)"가 가정을 하고 사라졌기 때문에 원인을 찾고 수정하는 데 서두르는 상황에 놓이게 됩니다(닌자는 이를 잘 수행합니다).

이러한 방식으로 작업하는 팀은 좋은 성능 테스트 관행을 따르고 개방적이고 합리적인 대화를 나누는 팀보다 안티패턴에 더 자주 빠질 가능성이 높습니다. 많은 개발 문제와 마찬가지로, 종종 기술적인 측면보다는 의사소통 문제와 같은 인간적인 요소가 애플리케이션에 문제를 일으키는 원인이 됩니다.

Carey Flichel이 작성한 블로그 게시물 "왜 개발자들이 잘못된 기술 선택을 계속하는가(Why Developers Keep Making Bad Technology Choices)"에서 분류의 흥미로운 가능성을 제공했습니다. 이 게시물은 개발자가 잘못된 선택을 하게 되는 다섯 가지 주요 이유를 특별히 지적합니다. 각 이유를 차례로 살펴보겠습니다.

#### 권태

대부분의 개발자는 역할에서 권태를 경험했을 것이며, 일부는 매우 짧은 기간 동안 새로운 도전이나 역할을 찾기 위해 회사를 떠나거나 다른 곳으로 이동할 것입니다. 그러나 조직 내에서 다른 기회가 없거나 다른 곳으로 이동하는 것이 불가능할 수 있습니다.

많은 독자들이 단순히 권태를 견디고 있으며, 더 쉬운 삶을 추구하는 개발자를 만난 적이 있을 것입니다. 그러나 권태에 빠진 개발자는 여러 가지 방식으로 프로젝트에 해를 끼칠 수 있습니다.&#x20;

#### 이력서 패딩

때때로 기술의 과잉 사용은 권태와 연결되지 않고, 개발자가 이력서(CV)에 특정 기술 경험을 늘리기 위해 기회를 활용하는 것을 나타냅니다. 이 시나리오에서 개발자는 재직 시장에 다시 진입하려고 할 때 잠재적인 급여 및 시장성을 높이기 위해 적극적으로 노력하고 있습니다. 이는 잘 운영되는 팀 내에서는 많은 사람들이 이를 용인하지 않을 가능성이 높지만, 여전히 프로젝트를 불필요한 경로로 이끄는 선택의 근원이 될 수 있습니다.

#### 동료 압력

동료 압력은 우려 사항이 제기되거나 선택이 이루어질 때 논의되지 않을 때 기술적 결정이 최악의 상태에 도달하는 경우가 많습니다. 이는 몇 가지 방식으로 나타날 수 있습니다

#### 이해 부족&#x20;

개발자는 현재 도구의 전체 기능을 인식하지 못하기 때문에 문제를 해결하기 위해 새로운 도구를 도입하려고 할 수 있습니다. 새로운 기술 구성 요소를 사용하는 것은 종종 한 가지 특정 작업을 수행하는 데 뛰어나기 때문에 유혹적입니다. 그러나 더 많은 기술적 복잡성을 도입하는 것은 현재 도구가 실제로 할 수 있는 것과 균형을 이루어야 합니다.

#### 잘못 이해된/존재하지 않는 문제

개발자는 문제 공간 자체를 충분히 조사하지 않고 특정 문제를 해결하기 위해 기술을 사용하는 경우가 많습니다. 성능 값을 측정하지 않고는 특정 솔루션의 성공을 이해하는 것은 거의 불가능합니다. 종종 이러한 성능 메트릭을 수집하면 문제에 대한 더 나은 이해를 가능하게 합니다.

안티패턴을 피하기 위해서는 기술적 문제에 대한 의사소통이 팀의 모든 참가자에게 개방적이고 적극적으로 장려되어야 하며, 불분명한 사항이 있는 경우 사실적 증거를 수집하고 프로토타입을 작업하여 팀의 결정을 이끌어내는 데 도움이 될 수 있습니다. 기술은 매력적으로 보일 수 있지만, 프로토타입이 기대에 미치지 못하면 팀은 더 정보에 입각한 결정을 내릴 수 있습니다.



### 성능 안티패턴 카탈로그

목록은 결코 완전하지 않으며, 확실히 아직 발견되지 않은 많은 것들이 있을 것입니다.

**Shiny에 현혹되다**

가장 최신이거나 멋진 기술이 종종 첫 번째 튜닝 대상이 됩니다. 이는 최신 기술을 탐구하는 것이 레거시 코드에 파고드는 것보다 더 흥미로울 수 있기 때문입니다.&#x20;

또한, 새로운 기술 구성 요소와 함께 제공되는 코드가 더 잘 작성되고 유지 관리하기 쉬울 수 있습니다. 이러한 사실은 개발자를 새로운 애플리케이션 구성 요소를 먼저 살펴보도록 이끕니다.

이는 종종 목표 지향적인 튜닝이나 애플리케이션을 측정하는 노력보다는 단순히 추측에 불과합니다. 개발자가 새로운 기술을 아직 완전히 이해하지 못했으며, 문서를 검토하지 않고 장난치게 됩니다

이 안티패턴은 새로 구성되었거나 경험이 부족한 팀에서 흔합니다. 자신을 증명하려 하거나, 레거시 시스템에 묶이고 싶지 않아 종종 새로운, "더 뜨거운" 기술을 옹호합니다—이는 우연히도 새로운 역할에서 급여 상승을 가져올 수 있는 정확히 그런 기술일 수 있습니다.

1. 병목 현상의 실제 위치를 결정하기 위해 측정합니다.
2. 새로운 구성 요소 주위에 충분한 로깅을 보장합니다.
3. 모범 사례뿐만 아니라 단순화된 데모를 살펴봅니다.
4. 팀이 새로운 기술을 이해하고 팀 전체에서 일정 수준의 모범 사례를 확립하도록 합니다.

**단순한 것에 현혹되다**

팀은 일반적으로 애플리케이션의 작은 부분을 프로파일링하고 전반적인 애플리케이션을 프로파일링하여 객관적으로 고충 지점을 찾는 대신, 시스템의 가장 단순한 부분을 먼저 목표로 삼습니다.&#x20;

원래 개발자는 시스템의 해당 부분을 튜닝하는 방법을 이해하고 있습니다. 지식 공유나 페어 프로그래밍이 없었기 때문에 단일 전문가가 된 것입니다.

특히, 이 두 안티패턴은 모두 미지의 것에 대한 반응으로 인한 것입니다. Shiny에 현혹되다에서는 개발자(또는 팀)가 더 많이 배우고 이점을 얻기 위해 새로운 기술을 탐구하려는 욕구로 나타납니다

1. 병목 현상의 실제 위치를 결정하기 위해 측정합니다.
2. 익숙하지 않은 구성 요소에서 문제가 발생한 경우 도메인 전문가에게 도움을 요청합니다.
3. 개발자가 시스템의 모든 구성 요소를 이해하도록 보장합니다.

**성능 튜닝 마법사**

경영진은 "외로운 천재" 해커의 할리우드 이미지를 구매하고, 자신의 인식된 우수한 성능 튜닝 기술을 사용하여 모든 성능 문제를 해결할 수 있는 누군가를 고용했습니다.

진정한 성능 튜닝 전문가와 회사는 있지만, 대부분의 사람들은 문제를 측정하고 조사해야 한다는 데 동의할 것입니다. 특정 기술의 모든 사용에 동일한 솔루션이 적용될 가능성은 낮습니다.

이 안티패턴은 팀 내에서 자신이 성능 문제를 다룰 만큼 충분하지 않다고 여기는 개발자를 소외시킬 수 있습니다. 이는 걱정스러운데, 많은 경우 프로파일러 기반의 소량 최적화가 좋은 성능 향상을 이끌어낼 수 있기 때문입니다

이는 특정 기술에 도움을 줄 수 있는 전문가가 없다는 것을 의미하지는 않지만, 모든 성능 문제를 처음부터 이해할 수 있는 외로운 천재가 있다는 생각은 터무니없습니다. 많은 성능 전문가들은 측정과 그 측정을 기반으로 한 문제 해결에 특화된 전문가들입니다.

1. 결론을 서두르려는 압력을 견뎌냅니다.
2. 정상적으로 분석을 수행합니다.
3. 분석 결과를 모든 이해관계자에게 전달하여 문제의 원인에 대한 보다 정확한 그림을 장려합니다.

**전통적인 방식으로 튜닝**

성능 문제를 해결하기 위해 필사적으로 노력하는 동안, 팀원은 웹사이트에서 "마법 같은" 구성 매개변수를 발견합니다. 팀은 해당 매개변수를 테스트하지 않고 프로덕션에 적용합니다. 왜냐하면 그 사람이 인터넷에서 사용했을 때 정확히 개선되었기 때문입니다

“Stack Overflow에서 좋은 팁을 찾았어요. 이게 모든 것을 바꿉니다.”

개발자는 성능 팁의 맥락이나 근거를 이해하지 못하며, 실제 영향은 알 수 없습니다. 이는 특정 시스템에 대해 작동했을 수 있지만, 다른 시스템에서는 이득을 얻지 못하거나 더 나쁘게 만들 수 있습니다.

성능 팁은 알려진 문제에 대한 우회 해결책입니다—본질적으로 문제를 찾는 해결책입니다. 성능 팁은 유통 기한이 있으며 보통 소프트웨어나 플랫폼의 이후 릴리스에서 이 팁을 무용지물로 만드는 해결책이 나옵니다(예: 관리 매뉴얼에서 제공되는 일반적인 조언은 맥락이 없기 때문에 성능에 해로울 수 있습니다).

Java 플랫폼의 성능은 특정 맥락에서 발생하며, 많은 기여 요인이 있습니다. 이 맥락을 제거하면 JVM의 복잡한 실행 환경으로 인해 무엇이 남았는지 이해하기 거의 불가능해집니다.

1. 잘 테스트되고 잘 이해된 기술만 적용합니다.
2. UAT에서 매개변수를 찾아보고 시도해 보되, 모든 변경에 대해 이득을 증명하고 프로파일링하는 것이 중요합니다.
3. 다른 개발자 및 운영 팀 또는 DevOps와 구성에 대해 검토하고 논의합니다.

**비난 당하는 당나귀**

특정 구성 요소는 항상 문제의 원인으로 지목되지만, 실제로는 문제가 없었던 경우가 많습니다.\
충분한 분석이 이루어지지 않았습니다. 보통의 용의자는 조사에서 유일한 용의자입니다.\
팀은 문제의 진정한 원인을 찾기 위해 더 넓은 범위를 조사하려 하지 않습니다.

이 안티패턴은 종종 경영진이나 비즈니스에서 나타나는데, 이는 많은 경우 기술 스택을 완전히 이해하지 못하고 인지적 편향을 인식하지 못하기 때문에 패턴 매칭을 통해 진행됩니다. 그러나 기술자들도 이에 면역이 아닙니다.

기술자들은 종종 코드베이스나 일반적으로 비난되는 라이브러리 외의 코드를 잘 이해하지 못할 때 이 안티패턴에 빠집니다. 시스템의 일부를 지명하여 문제를 해결하려는 대신, 새로운 조사를 수행하는 것이 더 어렵습니다. Hibernate는 이에 대한 완벽한 예시입니다

1. 결론을 서두르려는 압력을 저항합니다.
2. 정상적으로 분석을 수행합니다.
3. 분석 결과를 모든 이해관계자에게 전달하여 문제의 원인에 대한 보다 정확한 그림을 장려합니다.

**더 큰 그림을 놓치다**

팀은 변경을 시도하거나 애플리케이션의 작은 부분을 프로파일링하는 데 집착하게 되며, 변경의 전체적인 영향을 충분히 인식하지 못합니다. 엔지니어들은 JVM 스위치를 튜닝하려고 시도할 수 있습니다

팀은 변경의 영향을 충분히 이해하지 못합니다. 팀은 새로운 JVM 설정에서 애플리케이션을 완전히 프로파일링하지 않았습니다. 마이크로벤치마크의 데이터 세트는 일반적인 애플리케이션 사용을 대표하지 않으므로 전체 시스템에 대한 영향을 결정할 수 없습니다.

JVM에는 실제로 수백 개의 스위치가 있습니다. 이는 매우 구성 가능한 런타임을 제공하지만, 또한 개발자가 모든 스위치의 집합적 영향을 올바르게 추론하기 어려운 유혹을 제공합니다. 더욱이 대부분의 경우, 스위치 값을 변경하는 효과는 개발자가 예상하는 것보다 작습니다

줄어든 생각(reductionist thinking)과 확인 편향(confirmation bias)의 조합으로 인해 Folklore에 의한 튜닝과 더 큰 그림을 놓치는 것은 복잡한 실행 환경의 별개의 하위 시스템(스레딩, GC, 스케줄링, JIT 컴파일 등)을 풀어내는 작업을 더욱 어렵게 만듭니다.&#x20;

1. 프로덕션에서 측정합니다.
2. UAT에서 한 번에 하나의 스위치를 변경합니다.
3. UAT 환경이 프로덕션과 동일한 스트레스 포인트를 가지고 있는지 확인합니다.
4. 프로덕션 시스템의 일반적인 부하를 대표하는 테스트 데이터가 있는지 확인합니다.
5. UAT에서 변경 사항을 테스트합니다.
6. UAT에서 재테스트합니다.
7. 누군가가 귀하의 추론을 재확인하도록 합니다.
8. 그들과 페어 프로그래밍하여 결론을 논의합니다.

**UAT는 내 데스크탑이다**

**설명**

UAT 환경은 종종 예상치 못한 방식으로 프로덕션과 크게 다릅니다. 많은 개발자가 저전력 데스크탑을 사용하여 고전력 프로덕션 서버용 코드를 작성하는 상황을 경험했을 것입니다. 그러나 점점 더 일반화되는 것은 개발자의 머신이 프로덕션에 배포된 작은 서버보다 훨씬 더 강력한 경우가 많다는 것입니다.&#x20;

**토론**

1. 중단 비용과 고객 또는 트랜잭션 손실과 관련된 기회 비용을 추적합니다.
2. 프로덕션과 동일한 UAT 환경을 구매합니다.
   * 대부분의 경우, 첫 번째 비용이 두 번째 비용보다 훨씬 큽니다. 때로는 경영진에게 올바른 사례를 제시해야 할 필요가 있습니다.

**프로덕션과 유사한 데이터는 어렵다**&#x20;

DataLite 안티패턴이라고도 알려진 이 안티패턴은 프로덕션과 유사한 데이터를 표현하려고 할 때 사람들이 겪는 몇 가지 일반적인 함정을 관련합니다. 예를 들어, 대형 은행의 거래 처리 플랜트가 수백만 개의 메시지를 처리하는 경우를 생각해 보십시오.

UAT의 데이터는 정확한 결과를 위해 프로덕션과 유사해야 합니다. 데이터가 보안상의 이유로 사용할 수 없는 경우, 유의미한 테스트에 사용할 수 있도록 스크램블링(마스킹 또는 난독화)을 해야 합니다. 또 다른 옵션은 UAT를 파티셔닝하여 개발자가 데이터를 보지 않고도 성능 테스트의 결과를 볼 수 있도록 하는 것입니다.

1. 데이터 도메인 전문가와 상담하고, 필요한 경우 데이터를 스크램블링하거나 난독화하여 프로덕션 데이터를 UAT로 마이그레이션하는 프로세스를 구축합니다.
2. 예상되는 고객이나 트랜잭션 볼륨이 높은 릴리스에 대해 과도하게 준비합니다.

### 인지적 편향과 성능 테스트

인지적 편향과 성능 테스트는 밀접하게 관련되어 있습니다. 인간은 과거 경험과 유사한 상황에 직면했을 때도 빠르게 정확한 의견을 형성하는 데 서투를 수 있습니다.

인지적 편향은 인간의 뇌가 잘못된 결론을 도출하도록 하는 심리적 효과입니다. 이는 특히 해당 편향을 나타내는 사람이 이를 인지하지 못하고 합리적이라고 믿을 때 문제가 됩니다.

편향은 상호 보완적이거나 상반될 수 있습니다. 예를 들어, 일부 개발자는 문제가 전혀 소프트웨어 관련이 없다고 가정하고, 문제의 원인이 소프트웨어가 아닌 인프라스트럭처라고 가정할 수 있습니다.&#x20;

이는 "Works for Me" 안티패턴과 특징지을 수 있으며, 이는 "UAT에서는 잘 작동했으니 프로덕션 키트에 문제가 있는 것 같다"는 주장으로 특징지어집니다. 반대는 모든 문제가 소프트웨어에 의해 발생한다고 가정하는 것입니다.&#x20;

#### 환원주의적 사고&#x20;

이 인지적 편향은 시스템을 충분히 작은 조각으로 분해하면 구성 요소를 이해함으로써 전체를 이해할 수 있다고 주장하는 분석적 접근 방식에 기반합니다. 각 부분을 이해한다는 것은 잘못된 가정을 줄이는 것을 의미합니다.

문제는 복잡한 시스템에서는 이것이 사실이 아니라는 것입니다. 비정상적인 소프트웨어(또는 물리적) 시스템은 거의 항상 전체가 부분의 단순한 합보다 더 큰 의미를 가지는 출현적 행동(emergent behavior)을 나타냅니다.

#### 확인 편향&#x20;

확인 편향은 성능 테스트나 애플리케이션을 주관적으로 바라보는 데 있어 상당한 문제를 일으킬 수 있습니다. 확인 편향은 종종 잘못된 테스트 세트를 선택하거나 테스트 결과를 통계적으로 건전한 방식으로 분석하지 않을 때 도입됩니다. 확인 편향은 매우 어렵게 대처할 수 있는데, 왜냐하면 강력한 동기 부여나 감정적 요인이 작용하는 경우가 많기 때문입니다



**스위치 조작**

Folklore에 의한 튜닝과 더 큰 그림을 놓치는 것(Missing the Bigger Picture)은 환원주의적 사고와 확인 편향의 조합으로 인해 발생하는 안티패턴의 예입니다. 특히, Switches를 조작하는 것은 JVM의 매우 구성 가능한 특성에서 비롯된 숨겨진 인지적 함정 때문에 더욱 악명 높습니다.

VM이 감지된 하드웨어에 적합한 설정을 선택하려고 시도하지만, 일부 상황에서는 엔지니어가 코드를 튜닝하기 위해 플래그를 수동으로 설정해야 하는 경우가 있습니다. 이는 자체로는 해롭지 않지만, JVM의 명령줄 스위치가 매우 구성 가능하기 때문에 숨겨진 인지적 함정이 있습니다.

VM 플래그 목록을 보려면 다음 스위치를 사용하십시오:

```
-XX:+PrintFlagsFinal
```

Java 8u131부터는 이 스위치가 700개가 넘는 가능한 스위치를 생성합니다. 또한, 진단 모드에서만 사용할 수 있는 추가 튜닝 옵션도 있습니다. 이를 보려면 다음 스위치를 추가하십시오:

```
-XX:+UnlockDiagnosticVMOptions
```

이는 추가로 약 100개의 스위치를 잠금 해제합니다. 이러한 스위치의 가능한 조합의 집합적 영향을 올바르게 추론할 수 있는 방법은 없습니다. 더욱이, 대부분의 경우, 스위치 값을 변경하는 효과는 개발자가 예상하는 것보다 작습니다—종종 훨씬 작습니다.

#### 전쟁의 안개

#### 위험 편향

#### 엘스버그의 역설

확률을 이해하는 데 있어 인간이 얼마나 나쁜지 보여주는 엘스버그의 역설을 예로 들어보겠습니다. 이 역설은 유명한 미국 조사 기자이자 내부 고발자인 다니엘 엘스버그(Daniel Ellsberg)의 이름을 따서 명명되었습니다.&#x20;