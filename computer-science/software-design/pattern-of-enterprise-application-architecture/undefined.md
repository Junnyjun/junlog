# 계층화

## 레이어링: 복잡한 소프트웨어 시스템의 분해 기법

소프트웨어 설계에서 레이어링은 복잡한 시스템을 효율적으로 분해하고 관리하는데 중요한 역할을 합니다. 이 글에서는 레이어링의 개념과 그 장단점, 그리고 엔터프라이즈 애플리케이션에서의 레이어링의 진화를 중심으로 살펴보겠습니다.

### 레이어링이란?

레이어링은 시스템을 여러 개의 레이어로 나누어 각 레이어가 하위 레이어에 의존하지만, 하위 레이어는 상위 레이어를 인식하지 못하게 하는 구조를 의미합니다. 예를 들어, 네트워킹에서는 FTP가 TCP 위에, TCP는 IP 위에, IP는 이더넷 위에 위치합니다. 이러한 구조는 시스템을 이해하고 관리하는 데 많은 이점을 제공합니다.

#### 레이어링의 주요 이점

1. **이해 용이성**: 각 레이어를 별개의 전체로 이해할 수 있어, 다른 레이어의 세부 사항을 몰라도 시스템을 설계하고 구현할 수 있습니다.
2. **대체 구현 용이성**: 동일한 기본 서비스를 제공하는 대체 구현으로 레이어를 교체할 수 있습니다. 예를 들어, FTP 서비스는 이더넷, PPP 등 다양한 네트워크 프로토콜 위에서 동작할 수 있습니다.
3. **의존성 최소화**: 레이어 간의 의존성을 줄임으로써 특정 레이어의 변경이 다른 레이어에 미치는 영향을 최소화할 수 있습니다.
4. **표준화 촉진**: 레이어는 표준화하기 좋은 위치로, TCP와 IP는 레이어의 작동 방식을 정의하는 대표적인 표준입니다.
5. **재사용성**: 한 번 구축된 레이어는 여러 고급 서비스에서 재사용할 수 있습니다. 예를 들어, TCP/IP는 FTP, 텔넷, SSH, HTTP 등 다양한 프로토콜에서 사용됩니다.

#### 레이어링의 단점

1. **캡슐화 한계**: 레이어는 모든 것을 완벽하게 캡슐화하지 못하여 종종 연쇄적인 변경이 발생할 수 있습니다. 예를 들어, UI에 표시할 필드를 추가하려면 데이터베이스에서 UI까지 모든 레이어에 이를 반영해야 할 수 있습니다.
2. **성능 저하**: 추가 레이어는 성능을 저하시킬 수 있습니다. 각 레이어에서 데이터를 변환하는 과정에서 성능이 떨어질 수 있지만, 하위 기능을 최적화하여 성능 향상을 도모할 수 있습니다.

### 엔터프라이즈 애플리케이션에서의 레이어링 진화

초기 배치 시스템에서는 레이어링에 대해 별로 생각하지 않았습니다. 프로그램은 파일을 조작하는 단일 블록으로 구성되었으며, 레이어는 필요하지 않았습니다. 그러나 90년대 클라이언트-서버 시스템의 등장으로 레이어 개념이 더욱 분명해졌습니다.

#### 클라이언트-서버 시스템

클라이언트-서버 시스템은 두 개의 레이어로 구성되었습니다:

* **클라이언트**: 사용자 인터페이스와 애플리케이션 코드를 포함
* **서버**: 주로 관계형 데이터베이스를 보유

이 시스템은 데이터 집약적인 애플리케이션을 쉽게 구축할 수 있도록 했지만, 도메인 로직이 복잡해질수록 유지보수가 어려워졌습니다.

#### 3층 시스템

객체 지향 프로그래밍의 부상과 함께, 도메인 로직 문제를 해결하기 위해 3층 시스템이 등장했습니다:

* **프레젠테이션 레이어**: 사용자 인터페이스를 처리
* **도메인 레이어**: 비즈니스 로직을 포함
* **데이터 소스 레이어**: 데이터베이스와의 통신을 담당

이 구조는 도메인 로직을 UI에서 분리하여 객체로 구조화할 수 있게 했습니다.

#### 웹의 등장과 3층 시스템의 도입

웹의 등장은 클라이언트-서버 애플리케이션을 웹 브라우저로 배포하고자 하는 요구를 불러일으켰습니다. 이로 인해, 도메인 로직을 분리하여 새로운 프레젠테이션 레이어를 쉽게 추가할 수 있는 3층 시스템의 중요성이 더욱 커졌습니다.

### 세 가지 주요 레이어

#### 프레젠테이션 레이어

프레젠테이션 레이어는 사용자와 소프트웨어 간의 상호작용을 처리합니다. 이는 명령 줄 인터페이스부터 리치 클라이언트 그래픽 UI, HTML 기반 브라우저 UI까지 다양합니다.

#### 도메인 레이어

도메인 레이어는 비즈니스 로직을 포함합니다. 이는 입력 데이터와 저장된 데이터를 기반으로 한 계산, 데이터 검증, 데이터 소스와의 상호작용 등을 포함합니다.

#### 데이터 소스 레이어

데이터 소스 레이어는 데이터베이스, 메시징 시스템, 트랜잭션 관리자 등 다른 시스템과의 통신을 담당합니다. 이는 애플리케이션이 필요한 데이터를 저장하고 관리하는 역할을 합니다.
