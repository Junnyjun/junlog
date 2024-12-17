# DDD를 시작하며

### 고품질 소프트웨어 모델을 향한 여정

소프트웨어 개발에서 \*\*도메인 주도 설계(Domain-Driven Design, DDD)\*\*는 단순히 버그가 적은 소프트웨어를 만드는 것을 넘어, 비즈니스 목표를 명확하게 반영하는 고품질의 소프트웨어 모델을 설계하는 데 중요한 역할을 합니다. 이번 포스트에서는 DDD의 기본 개념과 이를 효과적으로 구현하는 방법에 대해 깊이 있게 탐구해보겠습니다.

#### 왜 DDD를 시작해야 하는가?

소프트웨어 개발에서 **품질**은 중요한 목표 중 하나입니다. 우리는 테스트를 통해 치명적인 버그를 피하고, 가능한 한 오류가 없는 소프트웨어를 제공하려고 노력합니다. 그러나 버그가 없는 소프트웨어가 반드시 고품질의 소프트웨어 모델을 의미하지는 않습니다. 소프트웨어 모델은 비즈니스 목표를 정확하게 반영해야 하며, 이를 위해서는 단순히 버그를 피하는 것 이상이 필요합니다.

\*\*도메인 주도 설계(DDD)\*\*는 이러한 고품질 소프트웨어 모델을 설계하는 데 도움을 주는 접근 방식입니다. 올바르게 구현되면 DDD는 소프트웨어가 어떻게 작동하는지와 설계가 일치하도록 만들어줍니다. 이 책은 여러분이 DDD를 올바르게 구현할 수 있도록 돕는 것을 목표로 합니다.

#### DDD 도입을 위한 로드맵

이 장에서는 다음과 같은 주제를 다룹니다:

* DDD가 프로젝트와 팀에 어떤 도움을 줄 수 있는지 이해하기
* 프로젝트가 DDD에 투자할 가치가 있는지 평가하기
* DDD의 일반적인 대안과 그 한계 이해하기
* DDD의 기초를 이해하고 프로젝트에 첫 발을 내딛기
* DDD를 경영진, 도메인 전문가, 기술 팀에게 설득하는 방법
* DDD 사용 시 직면할 도전과 이를 극복하는 방법
* DDD를 구현하는 팀의 학습 과정 보기

#### DDD를 적용할 수 있는가?

**DDD를 구현할 수 있는 조건**은 다음과 같습니다:

* **탁월한 소프트웨어를 만들고자 하는 열정**과 그 목표를 달성할 끈기
* **학습과 개선에 대한 열망** 및 필요성을 인정할 수 있는 용기
* **소프트웨어 패턴을 이해하고 올바르게 적용할 수 있는 능력**
* **검증된 애자일 방법론을 사용하여 설계 대안을 탐색할 수 있는 기술과 인내심**
* **현상 유지에 도전할 용기**
* **세부 사항에 주의를 기울이고 실험하며 발견하려는 욕구와 능력**
* **더 똑똑하고 더 나은 코드를 작성하려는 추진력**

DDD는 기술 그 자체에 중점을 두기보다는 **논의, 경청, 이해, 발견, 비즈니스 가치**를 중심으로 합니다. 비즈니스 도메인을 이해할 수 있다면 최소한 소프트웨어 모델 발견 과정에 참여하여 \*\*유비쿼터스 랭귀지(Ubiquitous Language)\*\*를 만들 수 있습니다. 이는 DDD를 성공적으로 구현할 수 있는 발판을 마련해 줍니다.

#### 도메인 전문가와의 협업

DDD의 큰 장점 중 하나는 **도메인 전문가와 개발자가 함께 협력**하여 소프트웨어를 설계한다는 점입니다. 도메인 전문가는 해당 비즈니스 도메인에 대해 가장 잘 알고 있는 사람들이며, 이들과의 긴밀한 협업을 통해 더 정확하고 유용한 소프트웨어 모델을 구축할 수 있습니다.

도메인 전문가와의 협업을 통해 다음과 같은 이점을 얻을 수 있습니다:

* **비즈니스와 기술 간의 소통 개선:** 도메인 전문가와 개발자가 같은 언어를 사용함으로써 소통의 효율성을 높입니다.
* **비즈니스 지식의 중앙화:** 소프트웨어가 비즈니스 지식을 중앙에 두어, 특정 개인에게만 지식이 국한되지 않도록 합니다.
* **지속적인 학습과 발전:** 팀원들이 함께 비즈니스 도메인에 대해 배우고, 이를 소프트웨어 모델에 반영함으로써 지속적으로 발전할 수 있습니다.

#### 애너믹 도메인 모델의 문제점

\*\*애너믹 도메인 모델(Anemic Domain Model)\*\*은 도메인 객체가 데이터만을 보유하고 비즈니스 로직을 포함하지 않는 설계를 말합니다. 이는 레오 프레이먼(Leo F.)이 처음 언급했을 때부터 큰 문제로 지적되어 왔습니다. 많은 개발자들이 이를 정상으로 여기고 있는 실정입니다.

애너믹 도메인 모델의 주요 문제점은 다음과 같습니다:

1. **비즈니스 로직의 집중 분산:** 도메인 객체가 비즈니스 로직을 포함하지 않기 때문에, 서비스 계층에 로직이 집중되며 이는 유지보수성을 저하시킵니다.
2. **객체의 의미 상실:** 도메인 객체가 단순한 데이터 홀더로 전락하여, 객체의 비즈니스적 의미가 희석됩니다.
3. **테스트와 확장성의 문제:** 비즈니스 로직이 도메인 객체에 포함되지 않으면, 이를 테스트하고 확장하는 과정에서 어려움이 발생합니다.

**예시: 애너믹 도메인 모델 vs. 행동 중심 모델**

애너믹 도메인 모델의 예시:

```java
public class Customer extends Entity {
    private String customerId;
    private String firstName;
    private String lastName;
    // ... getters and setters ...
}
```

서비스 계층에서의 사용:

```java
@Transactional
public void saveCustomer(String customerId, String firstName, String lastName) {
    Customer customer = customerDao.readCustomer(customerId);
    if (customer == null) {
        customer = new Customer();
        customer.setCustomerId(customerId);
    }
    customer.setFirstName(firstName);
    customer.setLastName(lastName);
    customerDao.saveCustomer(customer);
}
```

행동 중심 모델의 예시:

```java
public class Customer extends Entity {
    private String customerId;
    private String firstName;
    private String lastName;

    public void changeName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        DomainEventPublisher.instance().publish(new CustomerNameChanged(this.customerId));
    }
}
```

서비스 계층에서의 사용:

```java
@Transactional
public void changeCustomerName(String customerId, String firstName, String lastName) {
    Customer customer = customerRepository.findById(customerId);
    if (customer == null) {
        throw new IllegalStateException("Customer does not exist.");
    }
    customer.changeName(firstName, lastName);
}
```

#### Ubiquitous Language의 중요성

\*\*Ubiquitous Language(유비쿼터스 랭귀지)\*\*는 도메인 전문가와 개발자가 공유하는 공통된 언어입니다. 이는 프로젝트 팀 내에서 일관되게 사용되며, 소프트웨어 모델과 소스 코드에 직접적으로 반영됩니다. Ubiquitous Language의 주요 특징은 다음과 같습니다:

* **공유된 언어:** 도메인 전문가와 개발자 모두가 이해하고 사용하는 언어입니다.
* **모델 중심:** 소프트웨어 모델에 직접적으로 반영되어, 코드와 자연스럽게 연결됩니다.
* **지속적인 발전:** 팀의 협업을 통해 지속적으로 발전하고, 비즈니스 도메인의 변화를 반영합니다.

**Ubiquitous Language의 실천 방법:**

1. **비즈니스 용어의 정의:** 도메인 전문가와 함께 비즈니스 용어를 정의하고, 이를 소프트웨어 모델에 반영합니다.
2. **정기적인 회의:** 도메인 전문가와 개발자가 정기적으로 만나 언어와 모델을 검토합니다.
3. **문서화:** 초기 단계에서 간단한 용어 사전을 작성하고, 지속적으로 업데이트합니다.
4. **코드 반영:** 모델의 모든 클래스와 메서드 이름이 Ubiquitous Language를 반영하도록 합니다.

#### 실제 사례 연구: SaaSOvation의 DDD 도입

**SaaSOvation**은 SaaS(Software as a Service) 제품을 개발하는 가상의 회사입니다. 이 회사는 두 가지 주요 제품을 보유하고 있습니다:

1. **CollabOvation:** 기업용 협업 도구로, 포럼, 공유 캘린더, 블로그, 인스턴트 메시징 등 다양한 협업 기능을 제공합니다.
2. **ProjectOvation:** 애자일 프로젝트 관리 도구로, 스크럼 기반의 프로젝트 관리 기능을 포함하고 있습니다.

**Collaboration Context**

**Collaboration Context**는 다양한 협업 도구를 포함하는 바운디드 컨텍스트입니다. 초기에는 보안과 권한 관리가 협업 모델에 포함되어 설계가 혼란스러웠습니다. 이를 해결하기 위해 **Identity and Access Context**라는 별도의 바운디드 컨텍스트로 분리하였습니다. 이를 통해 협업 모델은 순수하게 협업 기능에 집중할 수 있게 되었습니다.

**Identity and Access Context**

**Identity and Access Context**는 사용자 인증과 권한 관리를 담당하는 바운디드 컨텍스트입니다. 이 컨텍스트는 독립적으로 운영되며, 다른 컨텍스트와의 통합은 명확한 인터페이스를 통해 이루어집니다. 이를 통해 보안 관련 로직이 협업 모델과 분리되어 유지보수가 용이해졌습니다.

**Agile Project Management Context**

**Agile Project Management Context**는 **ProjectOvation**의 핵심 도메인입니다. 이 컨텍스트는 스크럼 기반의 프로젝트 관리 기능을 포함하며, 제품 백로그, 스프린트, 릴리스 관리 등을 지원합니다. **Agile Project Management Context**는 **Collaboration Context**와 **Identity and Access Context**와의 명확한 통합을 통해 효율적인 프로젝트 관리를 지원합니다.

#### DDD 적용의 도전과제

DDD를 구현하면서 팀이 직면하게 되는 일반적인 도전과제는 다음과 같습니다:

* **Ubiquitous Language의 구축:** 팀 전체가 공유하는 언어를 만들기 위해 도메인 전문가와 지속적으로 협력해야 합니다.
* **도메인 전문가의 참여:** 프로젝트 초기에 도메인 전문가의 참여를 확보하고, 지속적으로 협력해야 합니다.
* **개발자의 사고방식 변화:** 개발자들이 기술적인 접근 방식에서 벗어나 도메인 중심의 사고방식으로 전환해야 합니다.
* **복잡성 관리:** DDD는 복잡한 도메인을 단순화하는 것을 목표로 하지만, 초기 설계 단계에서는 오히려 복잡해 보일 수 있습니다.

\*\*도메인 주도 설계(DDD)\*\*는 도메인, 서브도메인, 바운디드 컨텍스트를 명확히 정의하고 관리함으로써 복잡한 비즈니스 도메인을 효과적으로 다룰 수 있게 해줍니다. 전략적 설계를 통해 소프트웨어 아키텍처의 기반을 다지고, 각 바운디드 컨텍스트가 독립적으로 발전할 수 있도록 함으로써 유지보수성과 확장성을 높일 수 있습니다.

DDD를 성공적으로 구현하기 위해서는 도메인 전문가와 개발팀 간의 긴밀한 협력이 필요하며, Ubiquitous Language를 기반으로 한 명확한 모델링이 중요합니다. SaaSOvation의 사례처럼 초기에는 어려움이 있을 수 있으나, 전략적 설계를 통해 문제를 해결하고 더 나은 소프트웨어 모델을 구축할 수 있습니다.

다음 포스트에서는 **Context Mapping**에 대해 심도 있게 다루어, 바운디드 컨텍스트 간의 통합과 상호작용을 효과적으로 관리하는 방법을 살펴보겠습니다.
