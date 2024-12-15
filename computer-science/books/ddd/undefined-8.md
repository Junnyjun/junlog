# 암묵적인 개념을 명시적으로 만들기

깊은 모델링은 매우 유용하지만, 실제로 어떻게 해야 할까? 깊은 모델은 사용자 활동, 문제, 솔루션의 핵심 개념과 추상화를 포함하여 유연하게 표현할 수 있는 능력을 갖춘다. 첫 번째 단계는 도메인의 필수 개념을 어떤 형태로든 모델에 표현하는 것이다.&#x20;

도메인 모델과 해당 코드의 많은 변형은 암시적으로 존재하는 개념을 인식하고 이를 모델에서 하나 이상의 명시적인 객체나 관계로 표현하는 형태를 취한다. 이러한 변화는 종종 중요한 돌파구가 되어 깊은 모델로 이어진다. \
그러나 대부분의 경우, 돌파구는 모델에 중요한 개념들이 명시된 후, 여러 번의 리팩토링을 통해 책임이 반복적으로 조정되고, 다른 객체와의 관계가 변경되고, 이름이 몇 번 변경된 후에 발생한다.&#x20;

최종적으로 명확한 형태로 드러나지만, 이는 암시된 개념을 인식하는 것으로 시작된다.

### 언어를 경청하라

다음과 같은 경험이 있을 수 있다. 사용자들이 보고서의 어떤 항목에 대해 항상 이야기해왔다. 해당 항목은 애플리케이션이 다양한 상황에서 수집한 데이터 집합으로부터 작성된다. 다른 객체의 속성이나 직접적인 데이터베이스 쿼리. 데이터를 모아 무언가를 보고하거나 도출하는 것이다.&#x20;

**UBIQUITOUS LANGUAGE**는 말, 문서, 모델 다이어그램, 코드에 퍼져 있는 어휘로 구성된다. 용어가 디자인에 없으면, 이를 포함하여 모델과 디자인을 개선할 수 있는 기회가 있다.

#### 예제

팀은 이미 화물을 예약할 수 있는 작동하는 애플리케이션을 개발했다. 이제 원산지, 목적지 및 선박 간의 환승에서 화물의 하역 작업 주문을 관리하는 '운영 지원' 애플리케이션을 구축하려고 한다.

예약 애플리케이션은 화물의 여행을 계획하기 위해 라우팅 엔진을 사용한다. 여정의 각 구간은 화물이 탑재될 선박의 항해 ID(특정 선박의 특정 항해를 나타내는 고유 식별자), 탑재 위치 및 하역 위치를 나타내는 데이터베이스 테이블의 행으로 저장된다.

**여정 객체로 리팩토링의 이점**

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

#### 예제

다음 이야기는 상업 대출 및 기타 이자 수익 자산에 투자하는 가상의 금융 회사에서 일어난다. 애플리케이션은 이러한 투자를 추적하고 그로부터 얻은 수익을 계산하는 기능을 점진적으로 추가하면서 발전해왔다. 매일 밤, 배치 스크립트가 실행되어 모든 이자 및 수수료 수익을 계산하고 회사의 회계 소프트웨어에 적절히 기록했다.

### **어색한 모델**

모델 측면에서, 야간 배치 스크립트는 각 자산을 반복하면서 해당 날짜의 날짜에 대해 `calculateInterestForDate()`를 호출했다. 스크립트는 반환 값을 받아 특정 원장에 게시할 금액과 함께 이를 회계 프로그램의 공개 인터페이스에 전달했다. 이 소프트웨어는 금액을 명명된 원장에 게시했다. 스크립트는 각 자산의 당일 수수료를 가져와 다른 원장에 게시하는 유사한 과정을 거쳤다.

한 개발자는 이자 계산의 복잡성이 증가함에 따라 어려움을 겪고 있었다. 그녀는 작업에 더 적합한 모델을 찾을 기회를 포착하기 시작했다. 이 개발자(DEV)는 그녀의 좋아하는 도메인 전문가(EXP)에게 문제 영역을 탐색하는 데 도움을 요청했다.

#### 새로운 모델의 이점

1. UBIQUITOUS LANGUAGE에 '발생'이라는 용어를 추가하여 용어를 풍부하게 함.
2. 발생과 지급을 분리.
3. 스크립트에서 도메인 계층으로 도메인 지식 이동 (어느 원장에 게시할지 등).
4. 수수료와 이자를 비즈니스에 맞게 결합하고 코드 중복 제거.
5. 발생 스케줄로 새로운 수수료와 이자 변형을 쉽게 추가할 수 있는 경로 제공.

개발자는 필요한 새로운 개념을 찾기 위해 깊이 파고들었다. 그녀는 이자 계산의 어색함을 인식하고, 더 깊은 해답을 찾기 위해 헌신적인 노력을 기울였다. 그녀는 은행 전문가와 함께 작업하는 행운이 있었다. 덜 적극적인 전문가와 작업했을 경우, 그녀는 더 많은 실수를 하고, 다른 개발자들과의 브레인스토밍에 더 많이 의존했을 것이다. 진행 속도는 더 느렸겠지만, 여전히 가능했을 것이다.

#### 모순을 숙고하라

다양한 도메인 전문가들이 경험과 필요에 따라 사물을 다르게 본다. 동일한 사람도 신중히 분석하면 논리적으로 일관되지 않은 정보를 제공할 수 있다. 이러한 모순은 종종 더 깊은 모델에 대한 단서가 될 수 있다.

#### 책을 읽어라

모델 개념을 찾을 때 분명한 것을 간과하지 말라. 많은 분야에서 기본 개념과 관습적인 지혜를 설명하는 책을 찾을 수 있다. 여전히 도메인 전문가와 협력하여 문제에 관련된 부분을 추출하고 객체 지향 소프트웨어에 적합하게 압축해야 한다. 그러나 시작점으로서 일관되고 깊이 생각된 관점을 찾을 수 있다.

#### 시도하고, 또 시도하라

이 예제들은 많은 시행착오가 포함되어 있음을 전달하지 않는다. 나는 대화에서 유용하고 명확한 모델을 찾기 전에 여러 단서를 따를 것이다. 그 후에도 더 나은 아이디어를 제공하는 추가 경험과 지식 축적으로 인해 그 모델을 한 번 이상 교체하게 될 것이다. 모델러/디자이너는 자신의 아이디어에 집착할 여유가 없다.

이러한 방향 전환은 단순한 몸부림이 아니다. 각 변화는 모델에 더 깊은 통찰을 내포한다. 각 리팩토링은 설계를 더 유연하게 만들어 다음 번 변경이 더 쉬워지고, 필요할 때 구부릴 준비가 된 상태로 만든다.

어쨌든 실험은 배울 수 있는 방법이다. 설계에서 실수를 피하려고 하면 경험이 적은 상태에서 결과물이 나올 수 있다. 그리고 빠른 실험 시리즈보다 시간이 더 오래 걸릴 수도 있다.

#### 덜 명백한 개념 범주 표현하기

객체 지향 패러다임은 특정 종류의 개념을 찾고 발명하게 만든다. 매우 추상적인 것조차도 '명사와 동사'는 대부분의 객체 모델의 핵심이다. 그러나 모델에서도 명시적으로 표현할 수 있는 다른 중요한 개념 범주가 있다.

나는 객체 지향을 시작할 때 명백하지 않았던 세 가지 범주를 논의할 것이다. 각 범주를 배울 때마다 내 설계가 더욱 날카로워졌다.

#### 명시적 제약

제약은 특히 중요한 모델 개념 범주를 이룬다. 종종 암묵적으로 나타나며, 이를 명시적으로 표현하면 설계를 크게 개선할 수 있다.\
때때로 제약은 객체나 메서드에서 자연스럽게 자리를 잡는다. '버킷' 객체는 용량을 초과하지 않는 불변성을 보장해야 한다.

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

#### 도메인 객체로서 프로세스 표현하기

먼저, 절차를 모델의 주요 측면으로 만들고 싶지 않다는 것을 동의하자. 객체는 절차를 캡슐화하여 그 목표나 의도를 생각하게 해야 한다.\
여기서 이야기하는 것은 도메인에 존재하는 프로세스로, 모델에서 표현해야 하는 것이다. 이러한 프로세스가 등장하면 객체 설계를 어색하게 만드는 경향이 있다.

프로세스를 수행하는 방법이 여러 가지인 경우, 또 다른 접근 방식은 알고리즘 자체 또는 그 일부를 독립된 객체로 만드는 것이다. 프로세스 간의 선택은 이러한 객체 간의 선택이 된다.

제약과 프로세스는 객체 지향 언어로 프로그래밍할 때 떠오르지 않는 모델 개념의 두 가지 광범위한 범주다. 그러나 이를 모델 요소로 생각하기 시작하면 설계가 훨씬 더 날카로워질 수 있다.

다음 범주는 훨씬 더 구체적이지만 매우 일반적이다. **SPECIFICATION**은 특정 종류의 규칙을 간결하게 표현하고, 이를 조건 논리에서 분리하여 모델에서 명시적으로 만든다.

### SPECIFICATION

다양한 애플리케이션에서 부울 테스트 메서드는 실제로 작은 규칙의 일부다. 간단할 때는 `Iterator.hasNext()` 또는 `Invoice.isOverdue()`와 같은 테스트 메서드로 처리한다.

```java
public boolean isOverdue() {
    Date currentDate = new Date();
    return currentDate.after(dueDate);
}
```

그러나 모든 규칙이 그렇게 간단하지는 않다. 같은 `Invoice` 클래스에서 다른 규칙인 `anInvoice.isDelinquent()`은 아마도 `Invoice`가 연체되었는지 테스트하는 것부터 시작할 것이다. `Invoice`는 기본 의미를 지원하지 않는 도메인 클래스 및 하위 시스템에 대한 모든 종류의 종속성을 개발할 것이다.

이 시점에서, 개발자는 종종 규칙 평가 코드를 애플리케이션 계층으로 이동시키려고 할 것이다. 이제 규칙은 도메인 계층에서 완전히 분리되어 데이터 객체로 남게 된다. 이러한 규칙은 도메인 계층에 있어야 하지만, 도메인 객체의 책임에 맞지 않는다. 평가 메서드는 조건 논리로 인해 부피가 커지고, 규칙을 읽기 어렵게 만든다.

#### 예제

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

### 쿼리하기

유효성 검사는 개별 객체를 평가하여 기준을 충족하는지 확인하는 것이다. 또 다른 일반적인 필요는 컬렉션에서 기준에 따라 하위 집합을 선택하는 것이다. 이 개념은 여전히 적용되지만, 구현 문제가 다르다.

예를 들어, 연체 송장을 가진 모든 고객의 목록을 표시해야 한다면, 앞서 정의한 연체 송장 규격이 여전히 사용될 수 있지만, 구현은 아마도 변경되어야 한다. 먼저, 인메모리에서 작업할 수 있다고 가정해보자.&#x20;

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

이 설계에는 몇 가지 문제가 있다. 가장 중요한 것은 테이블 구조의 세부 정보가 도메인 계층에 누출된다는 점이다. 이는 유지 보수성과 수정 가능성에 영향을 미칠 수 있다.&#x20;

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

이렇게 하면 SQL이 리포지토리에 남아 있으면서도 명세가 사용할 쿼리를 제어할 수 있다. \
이는 명세에 규칙을 더 깔끔하게 수집하지는 않지만, 연체의 본질을 선언하는 것이 있다.

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
