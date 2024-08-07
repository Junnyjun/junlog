# 모델과 구현의 연계

소프트웨어 개발 프로젝트에서 모델과 구현의 밀접한 결합이 왜 중요한지를 설명하는 사례를 다룹니다.

**모델과 구현의 분리 문제**

프로젝트 첫날, 도착하자마자 거대한 클래스 다이어그램이 벽을 가득 채우고 있는 모습을 보았습니다. 스마트한 사람들이 몇 달 동안 신중하게 연구하여 도메인 모델을 개발했지만, 모델과 코드는 완전히 분리되어 있었습니다.&#x20;

**첫 번째 문제:** 모델의 복잡한 연관성은 인간 분석가가 탐색할 수 있었지만, 저장하고 검색할 수 있는 단위로 번역되지 않았습니다. 이는 모델이 구현을 안내하지 못한다는 것을 의미했습니다.

**두 번째 문제:** 모델과 코드 간의 피드백이 간접적이었습니다. 기술 플랫폼에서 비효율적인 모델의 특정 측면은 몇 달이 지나서야 발견되었고, 이미 개발자들은 모델 없이 소프트웨어를 작성하기 시작했습니다. 이로 인해 모델은 데이터 구조로 축소되었고, 실제로 사용되지 않았습니다.

**모델 기반 설계의 필요성**

모델 기반 설계는 초기 분석뿐만 아니라 설계의 기초가 되는 모델을 요구합니다. 이는 코드를 통해 모델을 직접 표현해야 한다는 것을 의미합니다. 분석 모델과 설계 모델의 분리를 없애고, 하나의 모델이 두 목적을 모두 충족하도록 요구합니다.

**모델 기반 설계의 핵심:**

* **하나의 모델 사용:** 분석과 설계 모두에 유용한 단일 모델을 찾아야 합니다.
* **구현과의 밀접한 연관:** 설계의 일부가 도메인 모델을 직접 반영하도록 설계해야 합니다.
* **도메인 전문가와의 협업:** 개발자는 모델에 대한 논의에 참여하고, 도메인 전문가와 긴밀히 협력해야 합니다.

**객체 지향 설계와 모델 기반 설계**

객체 지향 프로그래밍은 모델 기반 설계를 지원하는 도구입니다. 모델의 개념을 코드로 직접 구현함으로써, 설계와 모델 간의 연관성을 강화할 수 있습니다.&#x20;

```java
abstract class AbstractNet {
    private Set<LayoutRule> rules;
    
    void assignRule(LayoutRule rule) {
        rules.add(rule);
    }

    Set<LayoutRule> assignedRules() {
        return rules;
    }
}

class Net extends AbstractNet {
    private Bus bus;
    
    Set<LayoutRule> assignedRules() {
        Set<LayoutRule> result = new HashSet<>();
        result.addAll(super.assignedRules());
        result.addAll(bus.assignedRules());
        return result;
    }
}
```

이러한 접근 방식은 코드와 모델의 일관성을 유지하고, 모델 기반 설계의 이점을 최대한 활용할 수 있게 합니다.

**모델링 패러다임과 도구 지원**

모델 기반 설계를 효과적으로 구현하려면 모델과 설계 간의 일관성이 필요합니다. 이를 위해 객체 지향 프로그래밍과 같은 모델링 패러다임을 지원하는 도구와 언어를 사용하는 것이 중요합니다.&#x20;

모델 기반 설계는 분석과 설계를 통합하여 개발 프로세스를 개선하고, 도메인의 핵심 개념을 정확하게 반영하는 소프트웨어를 개발할 수 있게 합니다. 이는 복잡한 도메인에서도 효과적인 솔루션을 제공할 수 있는 강력한 접근 방식입니다.

**모델과 구현의 결합**

성공적인 모델 기반 설계를 위해서는 모델과 구현이 밀접하게 결합되어야 합니다. 이를 위해서는 다음과 같은 접근이 필요합니다:

* **모델을 코드로 직접 표현:** 모델의 개념을 코드로 직접 구현하여 설계와 모델의 일관성을 유지합니다.
* **반복적 개선:** 모델과 설계를 반복적으로 개선하여 실용적이고 효율적인 모델을 만듭니다.
* **도구와 언어의 지원:** 객체 지향 프로그래밍과 같은 모델링 패러다임을 지원하는 도구와 언어를 사용합니다.

모델 기반 설계는 초기 분석과 설계를 통합하여 개발 프로세스를 개선하고, 도메인의 핵심 개념을 정확하게 반영하는 소프트웨어를 개발할 수 있게 합니다.&#x20;

***

#### 핸즈온 모델러

소프트웨어 개발에서 모델러와 프로그래머의 역할이 어떻게 통합되어야 하는지 설명합니다.&#x20;

**모델러와 프로그래머의 분리 문제**

한 프로젝트에서 제 역할은 여러 애플리케이션 팀을 조율하고 설계를 주도하는 도메인 모델을 개발하는 것이었습니다. 하지만 경영진은 모델러가 모델링만 해야 한다고 생각해 코딩 작업을 금지했습니다. 처음에는 도메인 전문가와 개발 리더들과 함께 핵심 모델을 잘 정리해나갔지만, 그 모델은 실제로 활용되지 못했습니다.

**첫 번째 이유:** 모델의 의도가 전달 과정에서 손실되었습니다. 모델의 전반적인 효과는 세부 사항에 매우 민감합니다. UML 다이어그램이나 일반적인 논의만으로는 충분하지 않습니다. 직접 코드를 작성하며 예시를 제공하고, 개발자들과 긴밀하게 협력했다면 팀이 모델의 추상화를 제대로 이해하고 활용할 수 있었을 것입니다.

**두 번째 이유:** 모델과 구현, 기술의 상호작용에서 오는 피드백이 간접적이었습니다. 예를 들어, 모델의 특정 부분이 기술 플랫폼에서 비효율적이었지만, 그 영향을 몇 달 후에야 알게 되었습니다.&#x20;

**핸즈온 모델러의 필요성과 역할**

프로젝트의 조건이 모델러에게 유리했음에도 불구하고, 모델러가 구현 과정에서 분리되면 구현의 제약 조건을 이해하지 못하게 됩니다. 모델 기반 설계의 기본 조건은 모델이 효과적인 구현을 지원하고 도메인의 핵심 통찰을 추상화하는 것입니다.&#x20;

하지만 코드를 작성하는 사람들이 모델에 책임감을 느끼지 않으면 모델과 소프트웨어는 무관해집니다. 또한 코드 변경이 모델을 변화시킨다는 인식이 없으면 리팩토링은 모델을 약화시킵니다.

모델러는 코드를 직접 다뤄야 합니다. 모델에 기여하는 모든 기술자는 프로젝트의 주요 역할과 상관없이 코드를 다뤄야 합니다.

**역할과 중요성**

성공적인 모델 기반 설계를 위해서는 모델러가 직접 코드를 다루며 구현 과정에 적극적으로 참여해야 합니다.&#x20;

* **모델을 코드로 직접 표현:** 모델의 개념을 코드로 직접 구현하여 설계와 모델의 일관성을 유지합니다.
* **반복적 개선:** 모델과 설계를 반복적으로 개선하여 실용적이고 효율적인 모델을 만듭니다.
* **도구와 언어의 지원:** 객체 지향 프로그래밍과 같은 모델링 패러다임을 지원하는 도구와 언어를 사용합니다.
