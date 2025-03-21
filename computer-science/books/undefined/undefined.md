# 개요

### 1. 코드 설계를 배우는 이유

#### 1.1 고품질 코드의 필요성

소프트웨어 개발에서 **코드 품질**은 성공적인 프로젝트의 핵심입니다. 모든 개발자는 "좋은 코드를 쓰고 싶다"고 생각하지만, 현실은 그리 녹록지 않습니다. 많은 개발자가 짧은 개발 일정과 기능 구현에 치중하느라 코드 품질을 뒷전으로 미루곤 합니다.&#x20;

먼저 "고품질 코드가 무엇인지" 정의해야 한다고 강조합니다. 많은 개발자가 오랜 경력에도 불구하고 코딩 스킬이 정체되는 이유는, 코드 품질에 대한 명확한 기준과 학습 없이 반복 작업에만 매달리기 때문입니다. 이 책은 그런 개발자들에게 코드 설계라는 '무기'를 쥐여 주고자 합니다.

#### 1.2 복잡한 코드 개발의 어려움

소프트웨어 개발에서 어려움은 크게 두 가지로 나뉩니다:

* **기술적 어려움**: 자율 주행, 고성능 메시지 대기열처럼 높은 기술력이 요구되는 경우.
* **복잡성**: 대규모 프로젝트에서 코드 양이 방대하고 비즈니스 로직이 얽혀 있는 경우.

Hello World 같은 간단한 프로그램은 누구나 작성할 수 있지만, 코드가 수십만 줄로 늘어나면 이야기가 달라집니다.&#x20;

단순히 작동하는 코드를 넘어, **이해하고 유지 보수할 수 있는 코드**가 필요해집니다. 제가 처음 대규모 프로젝트에 투입되었을 때, 수만 줄의 코드를 보고 어디서부터 분석해야 할지 몰라 당황했던 기억이 나네요. 그때 코드 설계의 중요성을 처음 실감했습니다.

#### 1.3 프로그래머의 기본 능력

프로그래머는 기술의 **넓이**와 **깊이**를 모두 갖춰야 합니다. 프레임워크나 미들웨어를 사용할 때, 단순히 API 호출법만 익히는 데 그치지 않고, 그 내부 작동 원리를 파고드는 태도가 필요합니다.&#x20;

저는 오픈소스 프로젝트(예: Spring Framework)의 소스 코드를 읽으며 많은 것을 배웠습니다. 처음에는 복잡한 클래스 관계와 호출 흐름을 따라가기 힘들었지만, 설계 원칙과 패턴을 익히면서 점점 "아, 이런 의도로 설계했구나!"라는 깨달음이 생기더군요.&#x20;

이런 경험은 코드 설계 학습이 단순히 이론이 아니라 실무 능력을 키우는 데 필수적임을 보여줍니다.

#### 1.4 경력 개발에 필요한 기술

주니어 개발자는 기본 도구와 언어를 익히는 데 집중하지만, **수석 엔지니어**로 성장하려면 코드 설계 같은 고급 스킬이 필수입니다.&#x20;

팀을 이끌고 프로젝트 품질을 책임지려면, 고품질 코드가 무엇인지 정의하고, 동료의 코드를 리뷰하며 개선 방향을 제시할 수 있어야 합니다.&#x20;

***

### 2. 코드 품질 평가 방법

코드 품질을 평가하려면 객관적이고 체계적인 기준이 필요합니다.&#x20;

다양한 평가 기준을 제시하지만, 그중 **7가지 핵심 기준**을 집중적으로 다룹니다. 아래에서 각 기준을 자세히 설명하고, 실무 예시를 곁들여 보겠습니다.

#### 2.1 유지 보수성 (Maintainability)

* **정의**: 코드 수정이나 기능 추가가 쉬운 정도.
* **예시**: 결제 시스템에서 새로운 결제 수단(예: 카카오페이)을 추가할 때, 기존 코드를 거의 건드리지 않고 모듈만 붙일 수 있다면 유지 보수성이 높습니다. 반대로, 제가 과거 경험한 프로젝트에서는 결제 로직이 한 클래스에 얽혀 있어 수정할 때마다 새로운 버그가 터졌습니다.
* **팁**: 계층화와 모듈화를 잘 해두면 유지 보수성이 올라갑니다.

#### 2.2 가독성 (Readability)

* **정의**: 다른 개발자가 코드를 쉽게 이해할 수 있는 정도.
* **예시**: 변수명을 cnt 대신 userCount로 짓고, 주석으로 "활성 사용자 수를 카운트"라고 달아놓으면 가독성이 높아집니다. 저는 코드 리뷰에서 주석 없는 코드를 보면 항상 "이게 뭐지?"라는 질문을 던지게 되더군요.
* **팁**: 의미 있는 네이밍과 적절한 주석은 필수입니다.

#### 2.3 확장성 (Extensibility)

* **정의**: 기존 코드를 수정하지 않고 새로운 기능을 추가할 수 있는 능력.
* **예시**: 데이터베이스 연결을 인터페이스로 추상화하면, MySQL에서 PostgreSQL로 바꿀 때 구현체만 교체하면 됩니다. 과거 제가 인터페이스 없이 하드코딩한 프로젝트는 DB 변경 시 몇 주를 고생했죠.
* **팁**: 개방-폐쇄 원칙(OCP)을 따르세요.

#### 2.4 유연성 (Flexibility)

* **정의**: 다양한 상황에 적응할 수 있는 능력.
* **예시**: API 엔드포인트를 설정 파일로 관리하면, 환경 변화(예: 개발/운영 서버)에도 코드 수정 없이 대응 가능합니다. 저는 유연성이 부족한 코드를 수정하느라 야근한 적이 많았습니다.
* **팁**: 추상화와 설정 관리를 활용하세요.

#### 2.5 간결성 (Simplicity)

* **정의**: 불필요한 복잡성을 피한 코드.
* **예시**: 간단한 데이터 조회 로직에 복잡한 디자인 패턴을 적용하는 대신, 직관적인 함수로 작성하는 게 낫습니다. 초보 시절 저는 "멋져 보이고 싶어서" 불필요한 패턴을 썼다가 나중에 후회했어요.
* **팁**: KISS 원칙을 기억하세요: "단순하게, 바보처럼!"

#### 2.6 재사용성 (Reusability)

* **정의**: 다른 프로젝트나 모듈에서 활용 가능한 코드.
* **예시**: 문자열 유틸리티 함수(예: trimAndCapitalize)를 만들어 두면 여러 프로젝트에서 재사용할 수 있습니다. 저는 이런 유틸리티를 라이브러리로 만들어 팀과 공유하곤 합니다.
* **팁**: DRY 원칙(반복하지 마세요)을 실천하세요.

#### 2.7 테스트 용이성 (Testability)

* **정의**: 단위 테스트를 쉽게 작성할 수 있는 정도.
* **예시**: 외부 의존성(DB 연결 등)이 없는 순수 함수는 테스트하기 쉽습니다. 반면, 제가 과거 작성한 의존성 높은 코드는 테스트 코드를 쓰기가 너무 힘들었어요.
* **팁**: 의존성 주입(DI)을 활용해 결합도를 낮추세요.

***

### 3. 고품질 코드를 작성하는 방법 (계속)

#### 3.2 설계 원칙 (계속)

설계 원칙은 코드 설계의 방향을 잡아주는 경험에서 나온 가이드라인입니다. 단순히 원칙을 외우는 것보다, **왜 필요한지**, **어떤 문제를 해결하는지**를 이해하는 것이 핵심입니다.&#x20;

다음은 주요 설계 원칙들입니다

* **단일 책임 원칙 (SRP)**: 클래스는 하나의 책임만 가져야 한다.
* **개방-폐쇄 원칙 (OCP, Open-Closed Principle)**: 확장에는 열려 있고, 수정에는 닫혀 있어야 한다.
* **리스코프 치환 원칙 (LSP, Liskov Substitution Principle)**: 자식 클래스는 부모 클래스를 대체할 수 있어야 한다.
* **인터페이스 분리 원칙 (ISP, Interface Segregation Principle)**: 클라이언트는 필요 없는 인터페이스에 의존하지 않아야 한다.
* **의존 역전 원칙 (DIP, Dependency Inversion Principle)**: 고수준 모듈은 저수준 모듈에 의존하지 않아야 한다.
* **KISS 원칙 (Keep It Simple, Stupid)**: 단순하게 유지하라.
* **YAGNI 원칙 (You Aren’t Gonna Need It)**: 필요 없는 기능을 미리 만들지 마라.
* **DRY 원칙 (Don’t Repeat Yourself)**: 중복을 피하라.
* **디미터 법칙 (LoD, Law of Demeter)**: 객체는 직접 아는 객체만 호출하라.

이 원칙들을 프로젝트에 맞게 적용하면 코드 품질이 크게 향상됩니다.&#x20;

#### 3.3 디자인 패턴

디자인 패턴은 **반복되는 설계 문제에 대한 검증된 해결책**입니다. 크게 생성, 구조, 행동 패턴으로 나뉘며, 적절히 사용하면 코드의 확장성과 유지 보수성을 높일 수 있습니다. 하지만 **과용하면 복잡성을 키울 수 있다**는 점을 주의해야 합니다.

**생성 패턴**

객체 생성을 캡슐화해 유연성을 높입니다.

* **싱글턴 패턴**: 단일 인스턴스를 보장. (예: 로깅 시스템)
* **팩터리 패턴**: 객체 생성 로직을 분리. (예: 다양한 DB 연결 생성)
* **빌더 패턴**: 복잡한 객체를 단계적으로 생성. (예: 설정이 많은 객체)

**구조 패턴**

클래스나 객체를 조합해 더 큰 구조를 만듭니다.

* **프록시 패턴**: 객체 접근을 제어. (예: 지연 로딩)
* **데커레이터 패턴**: 객체에 동적으로 책임을 추가. (예: 스트림 기능 확장)
* **어댑터 패턴**: 호환되지 않는 인터페이스를 연결. (예: 레거시 시스템 통합)

**행동 패턴**

객체 간의 책임 분배와 협력을 다룹니다.

* **옵서버 패턴**: 상태 변화를 다른 객체에 알림. (예: GUI 이벤트)
* **전략 패턴**: 알고리즘을 캡슐화해 교체 가능하게. (예: 정렬 알고리즘 선택)
* **템플릿 메서드 패턴**: 알고리즘 골격을 정의하고 세부 단계를 위임.

처음 디자인 패턴을 배울 때 모든 문제를 패턴으로 해결하려 했던 적이 있습니다. 간단한 기능에도 복잡한 패턴을 적용해 코드를 불필요하게 어렵게 만들었죠.&#x20;

#### 3.4 코딩 규칙

코딩 규칙은 **코드 가독성**을 높이는 데 필수적입니다. 변수명, 함수명, 주석 작성법 등 구체적인 지침을 포함합니다. 예를 들어, 변수명은 `userName`처럼 의미를 명확히 하고, 함수는 하나의 역할만 하도록 작게 유지해야 합니다.&#x20;

* **네이밍**: 동사+명사로 함수명 짓기 (예: `getUserData`)
* **주석**: 코드 의도를 설명
* **함수 길이**: 한 화면에 보일 정도로 짧게
* **모듈화**: 관련 기능을 묶어 재사용성 높이기

또한, "나쁜 냄새" (긴 함수, 중복 코드 등)를 인식하고 리팩터링하는 것도 중요합니다.

#### 3.5 리팩터링 기술

리팩터링은 **기존 코드를 개선**하는 과정으로, 코드 품질을 유지하고 높이는 데 핵심입니다. 중복 로직을 함수로 추출하거나, 결합도가 높은 모듈을 인터페이스로 분리하는 등의 작업을 포함합니다.&#x20;

***

### 4. 과도한 설계를 피하는 방법

고품질 코드를 추구하다 보면 **과도한 설계**에 빠질 위험이 있습니다. 이는 불필요한 복잡성을 유발해 오히려 품질을 떨어뜨릴 수 있습니다.

#### 4.1 코드 설계의 원래 의도

설계의 목적은 **코드 품질 향상**입니다. 디자인 패턴을 사용할 때는 "이게 정말 가독성과 확장성을 개선하는가?"를 고민해야 합니다. 저는 과거 "멋져 보인다"는 이유로 패턴을 남용해 코드를 복잡하게 만든 적이 있습니다. 이제는 필요성을 먼저 따져봅니다.

#### 4.2 문제 중심의 설계

설계 전 **문제를 명확히 정의**해야 합니다. 예를 들어, "이 기능이 자주 변경될 가능성이 있는가?"를 고려해 유연성을 조절합니다. 불필요한 유연성은 복잡성만 키웁니다.

#### 4.3 복잡한 코드에만 설계 적용

디자인 패턴은 **복잡한 문제**를 해결할 때 유용합니다. 간단한 문제에 복잡한 패턴을 쓰면 오버엔지니어링이 됩니다. 프로젝트 규모에 따라 설계 깊이를 조절하는 것이 중요합니다.

#### 4.4 지속적인 리팩터링

**지속적인 리팩터링**은 과도한 설계를 방지합니다. 처음부터 완벽한 설계를 추구하기보다, 필요할 때 개선하는 방식을 추천합니다. 저는 팀에서 매주 "리팩터링 데이"를 운영합니다.

#### 4.5 상황에 맞는 설계

프로젝트 특성에 따라 설계 깊이가 달라집니다. 단기 프로젝트는 간소화하고, 장기 프로젝트는 초기 설계에 더 투자합니다.
