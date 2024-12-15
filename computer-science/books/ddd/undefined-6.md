# 언어의 사용

### 도메인 객체의 생명주기

도메인 객체의 생명주기는 객체가 생성되고, 상태 변화를 거쳐, 삭제되거나 보관되는 일련의 과정을 포함합니다. 이 생명주기를 관리하기 위해서는 AGGREGATES, FACTORIES, REPOSITORIES와 같은 패턴을 사용하여 모델 기반 설계(Model-Driven Design)를 실현할 수 있습니다.

#### AGGREGATES

AGGREGATES는 객체 간의 관계를 간소화하고 무결성을 유지하는 데 중요한 역할을 합니다. AGGREGATE는 데이터 변경의 단위로 취급되는 관련된 객체들의 집합입니다.&#x20;

각 AGGREGATE는 루트 엔티티와 경계를 가지며, 내부 객체 간의 참조는 허용되지만 외부 객체는 루트 엔티티를 통해서만 참조할 수 있습니다.

AGGREGATE를 설계하는 주요 원칙은 다음과 같습니다:

* **루트 엔티티**: AGGREGATE의 루트 엔티티는 전역 식별자를 가지며, 외부에서 참조할 수 있는 유일한 객체입니다.
* **경계**: AGGREGATE는 경계를 가지고 있으며, 이 경계 내의 객체들은 루트 엔티티를 통해서만 접근할 수 있습니다. 이는 AGGREGATE의 무결성을 유지하는 데 도움을 줍니다.
* **불변성**: AGGREGATE의 상태 변경은 트랜잭션 단위로 이루어져야 하며, 트랜잭션이 완료될 때까지 AGGREGATE의 모든 불변 조건이 충족되어야 합니다.

#### FACTORIES

FACTORIES는 객체 생성의 복잡성을 캡슐화하여 클라이언트가 객체 생성의 내부 구조를 알 필요 없게 합니다. FACTORY는 객체나 AGGREGATE를 생성할 때 사용되며, 객체의 초기 상태를 설정하고 생성 과정에서 모든 불변 조건이 충족되도록 합니다.

FACTORY를 설계하는 주요 원칙은 다음과 같습니다:

* **캡슐화**: FACTORY는 객체 생성의 모든 복잡성을 캡슐화하여 클라이언트가 생성 과정에 대해 알 필요 없게 합니다.
* **불변 조건**: FACTORY는 생성된 객체의 모든 불변 조건을 충족시켜야 합니다. 이를 위해 필요한 모든 데이터를 받아 객체를 생성합니다.
* **추상화**: FACTORY는 생성할 객체의 타입을 추상화하여 클라이언트가 구체적인 클래스에 의존하지 않도록 합니다.

#### REPOSITORIES

REPOSITORIES는 객체의 저장, 검색, 삭제와 관련된 복잡성을 캡슐화하여 클라이언트가 간단한 인터페이스를 통해 객체를 조작할 수 있게 합니다.&#x20;

REPOSITORY는 특정 타입의 모든 객체를 개념적으로 모은 집합체로, 객체를 저장하고 검색하는 기능을 제공합니다.

REPOSITORY를 설계하는 주요 원칙은 다음과 같습니다:

* **저장소 추상화**: REPOSITORY는 객체의 저장소를 추상화하여 클라이언트가 저장소의 세부 구현에 대해 알 필요 없게 합니다.
* **간단한 인터페이스**: REPOSITORY는 객체를 저장, 검색, 삭제하는 간단한 인터페이스를 제공하여 클라이언트가 쉽게 사용할 수 있게 합니다.
* **객체 수명주기 관리**: REPOSITORY는 객체의 전체 수명주기를 관리하며, 객체의 생성, 보관, 삭제를 담당합니다.

### 화물 운송 시스템의 예제

화물 운송 시스템의 모델을 통해 모델 기반 설계를 어떻게 실현할 수 있는지 살펴보겠습니다. \
이 시스템은 다음과 같은 세 가지 기본 기능을 요구합니다:

1. 고객 화물의 주요 처리 단계 추적
2. 사전 화물 예약
3. 화물이 특정 처리 단계에 도달했을 때 고객에게 자동으로 송장 발송

#### 초기 모델

초기 모델은 다음과 같은 클래스 다이어그램으로 표현됩니다.

```
Cargo
---------
+ trackingID: String
+ deliverySpecification: DeliverySpecification
+ deliveryHistory: DeliveryHistory

DeliverySpecification
----------------------
+ destination: Location
+ arrivalDate: Date

DeliveryHistory
---------------
+ handlingEvents: List<HandlingEvent>

HandlingEvent
-------------
+ completionTime: Date
+ type: String
+ cargo: Cargo

CarrierMovement
---------------
+ scheduleID: String
+ from: Location
+ to: Location

Location
--------
+ portCode: String

Customer
--------
+ name: String
+ customerID: String
```

이 모델은 도메인 지식을 조직화하고 팀을 위한 언어를 제공합니다. 예를 들어, 다음과 같은 문장을 만들 수 있습니다:

* 여러 고객이 하나의 화물에 관여하며, 각기 다른 역할을 맡는다.
* 화물의 배송 목표가 명시되어 있다.
* 배송 목표를 충족시키기 위해 일련의 운송 단계가 필요하다.

**주요 클래스와 개념**

* **Cargo**: 화물 객체로, 추적 ID를 가집니다.
* **Handling Event**: 화물의 처리 이벤트로, 화물을 선박에 적재하거나 세관을 통과하는 등의 작업을 나타냅니다.
* **Delivery Specification**: 화물의 배송 목표를 정의하며, 도착지와 도착 시간을 포함할 수 있습니다.
* **Carrier Movement**: 특정 운송 수단(예: 트럭, 선박)이 특정 장소에서 다른 장소로 이동하는 것을 나타냅니다.
* **Delivery History**: 화물의 실제 이동 내역을 반영합니다.
* **Customer**: 고객 객체로, 이름과 고객 ID를 가집니다.

#### 도메인 모델의 개선

도메인 모델을 더 잘 설계하고, 실용적인 구현을 위해 일부 변경이 필요합니다.&#x20;

이는 `Delivery Specification`이 `Cargo`의 상세 의미를 담당하게 하여 복잡성을 줄이고, 나중에 쉽게 변경할 수 있도록 합니다.

#### 객체 식별 및 값 객체 구분

각 객체를 고려하여 식별을 추적해야 하는지 또는 기본 값을 나타내는지 확인합니다.

* **Customer**: 고객은 명확하게 식별이 필요하므로 엔티티입니다.
* **Cargo**: 동일한 상자가 두 개 있어도 구별할 수 있어야 하므로 엔티티입니다.
* **Handling Event** 및 **Carrier Movement**: 각각의 사건과 이동은 고유하므로 엔티티입니다.
* **Location**: 같은 이름을 가진 장소도 서로 다르므로 엔티티입니다.
* **Delivery History**: `Cargo`와 일대일 관계를 가지며, `Cargo`에 종속된 엔티티입니다.
* **Delivery Specification**: 목표 상태를 나타내는 값 객체입니다.
* **Role**: 역할을 나타내는 값 객체입니다.

#### AGGREGATE 경계 정의

* **Customer**, **Location**, **Carrier Movement**는 독립적인 AGGREGATE 루트입니다.
* **Cargo**는 명백한 AGGREGATE 루트이며, 그 경계 내에 `Delivery History`와 `Delivery Specification`을 포함합니다.

AGGREGATE 경계를 정의하면 다음과 같은 이점이 있습니다:

* **무결성 유지**: AGGREGATE 내부의 객체들은 트랜잭션 단위로 일관성을 유지할 수 있습니다.
* **참조 관리**: 외부 객체는 AGGREGATE의 루트 엔티티를 통해서만 내부 객체에 접근할 수 있으므로, 객체 참조를 체계적으로 관리할 수 있습니다.
* **변경 관리**: AGGREGATE의 경계를 통해 객체 변경의 영향을 국한시킬 수 있습니다.

#### REPOSITORIES 선정

다섯 개의 엔티티가 AGGREGATE 루트이므로, REPOSITORY를 이들에 대해서만 제공합니다.

* **Customer Repository**: 고객을 검색하기 위한 메서드를 제공합니다.
* **Location Repository**: 위치를 검색하기 위한 메서드를 제공합니다.
* **Carrier Movement Repository**: 운송 이동을 검색하기 위한 메서드를 제공합니다.
* **Cargo Repository**: 화물을 검색하기 위한 메서드를 제공합니다.

REPOSITORY를 선정하고 구현하면 다음과 같은 이점이 있습니다:

* **저장소 추상화**: 클라이언트가 저장소의 세부 구현을 몰라도 되므로, 코드가 간단해지고 유지보수가 용이해집니다.
* **객체 수명주기 관리**: 객체의 저장, 검색, 삭제를 체계적으로 관리할 수 있습니다.
* **성능 최적화**: 저장소 접근 방법을 최적화하여 시스템 성능을 개선할 수 있습니다.

#### 새로운 기능: 화물의 목적지 변경

고객이 화물의 목적지를 변경하고자 할 때, `Delivery Specification`을 값 객체로 처리하여 간단히 새로운 값을 설정할 수 있습니다.

```java
public void changeDeliverySpecification(Cargo cargo, DeliverySpecification newSpec) {
    cargo.setDeliverySpecification(newSpec);
}
```

이 메서드는 새로운 `Delivery Specification` 객체를 생성하고, 기존의 `Cargo` 객체에 설정합니다. 이를 통해 객체의 상태를 일관되

게 유지할 수 있습니다.

#### 새로운 기능: 반복 비즈니스

반복 예약의 경우 기존 화물을 프로토타입으로 사용하여 새로운 화물을 생성할 수 있습니다. 이를 위해 PROTOTYPE 패턴을 사용합니다.

```java
public Cargo copyPrototype(String newTrackingID) {
    Cargo newCargo = new Cargo(newTrackingID);
    newCargo.setDeliverySpecification(this.deliverySpecification);
    newCargo.setCustomerRoles(new HashMap<>(this.customerRoles));
    // 기타 필요한 초기화 작업
    return newCargo;
}
```

이 메서드는 기존 `Cargo` 객체를 복사하여 새로운 `Cargo` 객체를 생성합니다. 이 과정에서 `Delivery Specification`과 `Customer Roles`를 복사하여 설정합니다.

#### Handling Event 추가

각 처리 이벤트를 기록할 때마다 `Handling Event`를 생성하여 `Delivery History`에 추가해야 합니다.

```java
public HandlingEvent createHandlingEvent(Cargo cargo, CarrierMovement movement, Date time) {
    HandlingEvent event = new HandlingEvent(cargo, movement, time);
    cargo.getDeliveryHistory().addHandlingEvent(event);
    return event;
}
```

이 메서드는 새로운 `Handling Event` 객체를 생성하고, 해당 이벤트를 `Cargo` 객체의 `Delivery History`에 추가합니다.

#### 최적화된 설계: Delivery History의 컬렉션 대체

`Delivery History`의 컬렉션을 쿼리로 대체하면, 처리 이벤트를 추가할 때 다른 AGGREGATE와의 충돌을 피할 수 있습니다. 예를 들어, `Delivery History`가 `Handling Event`를 직접 보관하는 대신, 데이터베이스에서 쿼리를 통해 필요한 이벤트를 가져올 수 있습니다.

```java
public List<HandlingEvent> getHandlingEvents(Cargo cargo) {
    // 데이터베이스 쿼리를 통해 Handling Events를 가져오는 로직
    return handlingEventRepository.findByCargo(cargo.getTrackingID());
}
```

이 메서드는 `Cargo` 객체의 추적 ID를 사용하여 데이터베이스에서 관련된 `Handling Event`를 가져옵니다.

#### MODULES 디자인

모델을 더 큰 모듈로 나누어 각 모듈이 도메인의 특정 부분을 다루도록 합니다. 예를 들어, 화물 운송 시스템을 다음과 같은 모듈로 나눌 수 있습니다:

* **Customer Module**: 고객 관련 클래스와 기능을 포함합니다.
* **Cargo Module**: 화물 관련 클래스와 기능을 포함합니다.
* **Handling Module**: 처리 이벤트 관련 클래스와 기능을 포함합니다.
* **Location Module**: 위치 관련 클래스와 기능을 포함합니다.

각 모듈은 도메인의 특정 부분을 다루며, 다른 모듈과의 의존성을 최소화하여 시스템을 더 이해하기 쉽게 만들고 유지보수를 용이하게 합니다.

이와 같은 모듈 분할은 시스템을 더 이해하기 쉽게 만들고, 유지보수를 용이하게 합니다.

#### 새로운 기능: 할당 검사

예약 시스템과 통합하여 할당 검사를 수행합니다. `Allocation Manager`라는 서비스를 도입하여 외부 시스템과의 통신을 캡슐화하고, 할당 관련 서비스를 제공합니다.

```java
public class AllocationManager {
    // 할당 관련 메서드 제공
    public boolean checkAllocation(Cargo cargo) {
        // 할당 검사 로직 구현
        return true;
    }
}
```

이 클래스는 외부 시스템과의 통신을 관리하고, 할당 검사를 수행하는 메서드를 제공합니다.

```java
public class BookingService {
    private AllocationManager allocationManager;
    private CargoRepository cargoRepository;

    public BookingService(AllocationManager allocationManager, CargoRepository cargoRepository) {
        this.allocationManager = allocationManager;
        this.cargoRepository = cargoRepository;
    }

    public void bookCargo(Cargo cargo) {
        if (allocationManager.checkAllocation(cargo)) {
            cargoRepository.save(cargo);
        } else {
            throw new IllegalStateException("할당 초과로 인해 예약 불가");
        }
    }
}
```

`BookingService` 클래스는 화물 예약을 처리하며, `Allocation Manager`를 통해 할당 검사를 수행합니다.
