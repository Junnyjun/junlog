# 📘 이펙티브 코틀린

자바의 필드와 코틀린의 프로퍼티는 완전 다른 개념이다.

### **프로퍼티의 특징**

#### 프로퍼티는 사용자 정의 세터와 게터를 가질 수 있다.

field 식별자 : 프로퍼티의 데이터를 저장해두는 백킹 필드(backing field)에 대한 레퍼런스 \
&#x20;\> 세터와 게터의 디폴트 구현에 사용

#### 파생 프로퍼티 (derrived property)

var을 사용해서 만든 읽고 쓸 수 있는 프로퍼티는 게터와 세터 정의가 가능하다.\
val로 읽기 전용 프로퍼티를 만들때는 field가 만들어지지 않는다

#### 프로퍼티는 필드가 필요가 없다.&#x20;

개념적으로 접근자를 나타낸다 (val 는 게터, var는 게터와 세터)

그래서 인터페이스에서 프로퍼티 정의가 가능

프로퍼티 위임 (property delegation)이 가능하다

확장 프로퍼티를 만들 수도 있다. (프로퍼티는 본질적으로 함수이므로)

&#x20;

**프로퍼티를 구분하는 방법**

원칙적으로 프로퍼티는 상태를 나타내거나 설정하기 위한 목적으로만 사용하는 것이 좋고, 다른 로직등을 포함하지 않아야 한다.\
\
기준 : “이 프로퍼티를 함수로 정의할 경우, 접두사로 get또는 set을 붙일 것인가?”

프로퍼티는 상태 집합 / 함수는 행동

**프로퍼티 대신 함수를 사용하는 것이 좋은 경우**

```
연산 비용이 높거나 복잡도가 O(1)보다 큰 경우
비즈니스 로직(애플리케이션 동작)을 포함하는 경우
결정적이지 않은 경우
변환의 경우
게터에서 프로퍼티의 상태 변경이 일어나야 하는 경우
```