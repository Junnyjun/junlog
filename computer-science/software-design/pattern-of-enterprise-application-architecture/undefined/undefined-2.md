# 관계형 데이터베이스 접근

#### 데이터 소스 계층의 역할

데이터 소스 계층은 애플리케이션이 작업을 수행하는 데 필요한 다양한 인프라와 통신하는 역할을 합니다. 이 문제의 중요한 부분은 데이터베이스와의 통신이며, 오늘날 구축되는 대부분의 시스템에서는 관계형 데이터베이스를 사용합니다.

#### 관계형 데이터베이스와 SQL

관계형 데이터베이스가 성공한 주요 이유 중 하나는 데이터베이스 통신을 위한 거의 표준화된 언어인 SQL의 존재입니다. SQL은 벤더별로 복잡한 특정 확장이 많지만, 핵심 구문은 공통적이고 잘 이해되고 있습니다.

#### SQL과 애플리케이션 개발

SQL을 사용하는 데에는 몇 가지 문제가 있습니다. 많은 애플리케이션 개발자가 SQL을 잘 이해하지 못하고, 결과적으로 효과적인 쿼리와 명령을 정의하는 데 어려움을 겪습니다. SQL을 프로그래밍 언어에 포함시키는 다양한 기술이 있지만, 모두 다소 어색합니다. 데이터에 접근하는 메커니즘이 애플리케이션 개발 언어와 잘 맞아야 합니다.

#### 아키텍처 패턴

첫 번째 패턴 세트는 아키텍처 패턴으로, 도메인 로직이 데이터베이스와 통신하는 방식을 결정합니다. 이 선택은 설계에 광범위한 영향을 미치므로 주의를 기울여야 하며, 나중에 리팩터링하기 어려운 선택입니다.

**Gateway 패턴**

SQL 접근을 도메인 로직과 분리하고 별도의 클래스에 배치하는 것이 현명합니다. 이러한 클래스를 데이터베이스의 테이블 구조를 기반으로 구성하면 각 데이터베이스 테이블당 하나의 클래스가 생기게 됩니다. 이러한 클래스는 데이터베이스 테이블에 대한 게이트웨이를 형성하여 애플리케이션의 나머지 부분이 SQL에 대해 아무것도 알 필요가 없게 합니다.

1. **Row Data Gateway (152)**:
   * 쿼리 결과로 반환된 각 행마다 하나의 인스턴스를 가지는 접근 방식.
   * 객체 지향적인 데이터 처리 방식에 자연스럽게 맞음.
2. **Table Data Gateway (144)**:
   * 데이터베이스의 각 테이블당 하나의 인스턴스를 가지는 접근 방식.
   * GUI 도구에서 많이 사용되는 레코드 세트(Record Set)와 잘 맞음.

**Active Record (160)**

도메인 객체가 데이터베이스 테이블과 상호 작용하는 방법을 알고 있는 경우. 도메인 로직이 복잡해질 경우 이 접근 방식은 한계가 있으며, 더 많은 간접 접근 방식을 필요로 할 수 있습니다.

**Data Mapper (165)**

도메인 모델을 데이터베이스와 완전히 분리시키는 더 나은 방법. 이 패턴은 데이터베이스와 도메인 객체 간의 모든 로드 및 저장 작업을 처리하여 두 계층이 독립적으로 변화할 수 있도록 합니다.

#### 행위 문제

객체가 데이터베이스에 자신을 로드하고 저장하는 방법을 관리하는 것이 중요합니다. 여러 객체를 메모리에 로드하고 수정할 때, 어떤 객체가 수정되었는지 추적하고 모두 데이터베이스에 올바르게 기록해야 합니다.

**Unit of Work (184)**

데이터베이스에서 읽어들인 모든 객체와 수정된 모든 객체를 추적합니다. 데이터베이스에 대한 모든 업데이트를 처리하고, 응용 프로그램 프로그래머가 명시적인 저장 메서드를 호출하는 대신 단위 작업을 커밋하도록 합니다.

**Identity Map (195)**

중복된 객체 생성을 방지하고 올바른 업데이트를 보장하기 위해 읽어들인 각 행을 기록합니다.

**Lazy Load (200)**

필요할 때까지 실제 객체를 가져오지 않고 자리 표시자를 사용하여 데이터베이스 호출을 줄입니다.

#### 데이터 읽기

데이터를 읽을 때, SQL 선택문을 래핑한 메서드를 사용하는 것이 좋습니다. 메서드는 일반적으로 find(id) 또는 findForCustomer(customer)와 같은 형태를 취합니다.

**Finder Objects**

행 기반 클래스에서 데이터베이스 작업을 대체할 수 없기 때문에 별도의 Finder 객체를 사용하는 것이 좋습니다.

#### 성능 고려사항

1. 가능한 한 여러 행을 한 번에 가져오도록 합니다.
2. 조인을 사용하여 여러 테이블에서 데이터를 한 번에 가져옵니다.
3. 특정 상황에 맞춘 성능 프로파일링 및 튜닝을 실행합니다.

#### 구조적 매핑 패턴

객체와 관계형 데이터베이스의 링크 처리 방식의 차이로 인해 나타나는 문제를 해결하기 위해 다양한 매핑 패턴을 사용합니다.

**Foreign Key Mapping (236)**

단일 값 필드를 매핑하는 데 사용됩니다.

**Association Table Mapping (248)**

다대다 관계를 처리하기 위해 사용됩니다.

#### 상속 매핑

클래스 계층 구조를 처리하기 위해 세 가지 주요 옵션이 있습니다.

1. **Single Table Inheritance (278)**: 계층 구조의 모든 클래스를 하나의 테이블에 저장.
2. **Concrete Table Inheritance (293)**: 구체적인 클래스마다 하나의 테이블을 사용.
3. **Class Table Inheritance (285)**: 계층 구조의 각 클래스마다 하나의 테이블을 사용.

#### 매핑 빌드

관계형 데이터베이스에 매핑할 때 세 가지 상황이 있습니다.

1. 스키마를 직접 선택할 수 있는 경우.
2. 기존 스키마에 매핑해야 하지만 변경이 가능한 경우.
3. 기존 스키마에 매핑해야 하고 변경이 불가능한 경우.

**논리 데이터 저장소 스키마 사용**

같은 데이터를 여러 소스에서 가져와야 할 때, 두 단계 매핑 스키마를 사용하는 것이 좋습니다.

#### 메타데이터 사용

메타데이터를 사용하여 데이터베이스의 컬럼과 객체의 필드를 매핑하면 반복적인 코드를 피할 수 있습니다.

**Query Object (316)**

메모리 내 객체와 데이터를 기준으로 쿼리를 작성하고, 메타데이터를 사용하여 이를 SQL로 변환합니다.

**Repository (322)**

데이터베이스를 거의 보이지 않게 하여 모든 쿼리를 Repository를 통해 처리합니다.

#### 데이터베이스 연결 관리

데이터베이스 연결 객체는 애플리케이션 코드와 데이터베이스 간의 링크 역할을 합니다. 연결은 자원이므로 사용 후 반드시 닫아야 합니다.

1. **연결 풀링**: 연결 생성이 비싸다면 연결 풀을 사용하여 성능을 개선할 수 있습니다.
2. **명시적 닫기**: 명시적으로 연결을 닫는 것이 좋지만, 잊기 쉬우므로 주의해야 합니다.
3. **트랜잭션과 연결 관리**: 트랜잭션과 연결을 관리하는 것이 좋습니다.

#### 그 외 고려사항

1. `select *` 대신 명시적인 컬럼 이름을 사용하여 쿼리를 작성하는 것이 좋습니다.
2. 정적 SQL을 사용하여 성능을 개선할 수 있습니다.
3. 환경에 맞는 성능 최적화를 적용하는 것이 중요합니다.