# 요소의 가시성을 최소화하라

### API를 설계할 때 가능한 한 간결한 API를 선호한다

작은 인터페이스는 배우기 쉽고 유지하기 쉬움

&#x20; \=> 눈에 보이는 요소가 적을 수록 유지하고 테스트 해야할 것이 적음

변경을 가할 때 기존의 것을 숨기는 것보단 새로운 것을 노출하는 것이 쉬움

&#x20; \=> public 으로 공개해놓은 API는 외부에서 사용되는데 요소를 변경하는 경우에 모든 곳에서 업데이트 해야하기 때문에 가기성을 제한하는 것이 훨씬 더 어려워 각 용도를 신중하게 고려해 대안을 제공하는 것이 좋음

클래스의 상태를 외부에서 직접 변경할 수 있다면 클래스는 자산의 상태를 보장할 수 없음

&#x20; \=> 불변성이 깨질 수 있는 가능성이 있기 때문에 위험함

가시성이 제한될수록 클래스의 변경을 쉽게 추적할 수 있음

&#x20; \=> 프로퍼티의 상태를 더 쉽게 이해할 수 있고 동시성 처리에 용이함

### 가시성 한정자 사용하기

#### 접근제어자

public : 모든 곳에서 사용 가능

private : 클래스 내에서만 사용 가능

protected : 해당 클래스와 하위 클래스에서만 사용 가능

internal : 모듈 내부에서만 사용 가능

#### Top-level 요소

public : 모든 곳에서 사용 가능

private : 같은 파일 내부에서만 사용 가능

internal : 모듈 내부에서만 사용 가능

### 정리

인터페이스가 작을수록 학습과 유지보수에 쉬움

최대한 제한이 되어야 변경하기 쉬움

클래스의 상태를 나타내는 프로퍼티가 노출되어 있다면 클래스가 자신의 상태를 책일질 수 없음

가시성이 제한되면 변경을 쉽게 추적할 수 있음