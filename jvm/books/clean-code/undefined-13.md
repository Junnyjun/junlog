# 냄새와 휴리스틱

**Refactoring** 에서 마틴 파울러는 다양한 **코드 냄새** 를 거론한다.

아래 소개하는 목록에서 마틴이 맡은 냄새에 저자가 맡은 냄새를 추가해 정리해봤다.

#### 1. 주석.

**C1: 부적절한 정보**

다른 시스템에(소스 코드 관리 시스템, 버그 추적 시스템 등등) 저장할 정보는 주석으로 적절하지 못하다.

일반적으로 작성자, 최종 수정일, SPR 번호 등과 같은 메타 정보만 주석으로 넣는다.

주석은 코드와 설계에 기술적인 설명을 부연하는 수단이다.

**C2: 쓸모 없는 주석**

오래된 주석, 엉뚱한 주석, 잘못된 주석은 더 이상 쓸모가 없다.

쓸모 없어진 주석은 재빨리 삭제하는 편이 가장 좋다.

**C3: 중복된 주석**

코드만으로 충분한데 구구절절 설명하는 주석이 중복된 주석이다.

**C4: 성의 없는 주석**

작성할 가치가 있는 주석은 잘 작성할 가치도 있다.

주석을 달 참이라면 시간을 들여 최대한 멋지게 작성한다.

**C5: 주석 처리된 코드**

주석으로 처리된 코드는 얼마나 오래된 코드인지, 중요한 코드인지 아닌지 알 길이 없다.

누군가에게 필요하거나 다른 사람이 사용할 코드라 생각하고 아무도 삭제하지 않는다.

소스 코드 관리 시스템이 코드를 기억하기 때문에 주석으로 처리된 코드를 발견하면 즉각 지워버려라.

주석으로 처리된 코드를 내버려 두지 마라.

#### 2. 환경.

**E1: 여러 단계로 빌드해야 한다.**

빌드는 간단히 한 단계로 끝나야 한다.

한 명령으로 전체를 체크아웃해서 한 명령으로 빌드할 수 있어야 한다.

**E2: 여러 단계로 테스트해야 한다.**

모든 단위 테스트는 한 명령으로 돌려야 한다.

IDE에서 버튼 하나로 모든 테스트를 돌린다면 가장 이상적이다.

아무리 열악한 환경이라도 셸에서 명령 하나로 가능해야 한다.

모튼 테스트를 한 번에 실행하는 능력은 아주 근본적이고 아주 중요하다.

그 방법이 빠르고, 쉽고, 명백해야 한다.

#### 3. 함수.

**F1: 너무 많은 인수.**

함수에서 인수 개수는 작을수록 좋다.

인수가 넷 이상이라면 그 가치가 아주 의심스러우므로 최대한 피한다.

**F2: 출력 인수.**

출력 인수는 직관을 정면으로 위배한다.

함수에서 뭔가의 상태를 변경해야 한다면 출력 인수를 쓰지말고

함수가 속한 객체의 상태를 변경한다.

**F3: 플래그 인수.**

Boolean 인수는 함수가 여러 기능을 수행한다는 명백한 증거다.

플래그 인수는 혼란을 초래하므로 피해야 한다.

**F4: 죽은 함수.**

아무도 호출하지 않는 함수는 삭제한다.

소스 코드 관리 시스템이 모두 기억하므로 걱정할 필요없이 삭제하도록 한다.

#### 4. 일반.

**G1: 한 소스 파일에 여러 언어를 사용한다.**

어떤 자바 소스 파일은 `XML, HTML. YAML. Javadpc, Javascript` 등을 포함한다.

좋게 말하면 혼란스럽고 나쁘게 말하면 조잡하다.

현실적으로는 여러 언어가 불가피하므로 각별한 노력을 기울여 소스 파일에 언어의 수와 범위를 최대한 줄이도록 애써야 한다.

**G2: 당연한 동작을 구현하지 않는다.**

최소 놀람의 원칙에 의거해 함수나 클래스는 다른 프로그래머가 당연하게 여길 만한 동작과 기능을 제공해야 한다.

**G3: 경계를 올바로 처리하지 않는다.**

흔히 개발자들은 머릿속에서 코드를 돌려보고 끝낸다.

모든 경계 조건, 모든 구석진 곳, 모든 기벽, 모든 예외 등등

모든 경계 조건을 테스트하는 테스트 케이스를 작성하라.

**G4: 안전 절차 무시.**

실패하는 테스트 케이스를 일단 제껴두고 나중으로 미루는 태도는 신용카드가 공짜 돈이라 생각하는 만큼 위험하다.

**G5: 중복.**

코드에서 중복을 발견할 때마다 추상화할 기회로 간주하라.

중복된 코드를 하위 루틴이나 다른 클래스로 분리하라.

똑같은 코드가 여러 차례 나오는 중복이라면 간단한 함수로 교체한다.

여러 모듈에서 일련의 `switch / case, if / else` 문으로 똑같은 조건을 거듭 확인하는 중복이라면 다형성으로 대체해야 한다.

알고리즘이 유사하거나 코드가 서로 다른 중복이라면 **TEMPLATE METHOD 패턴** 이나 **STRATEGY 패턴** 으로 중복을 제거한다.

**어디서든 중복을 발견하면 없애라**

**G6: 추상화 수준이 올바르지 못하다.**

추상화로 개념을 분리할 떄는 철저해야 한다.

모든 저차원 개념은 파생 클래스에 넣고, 모든 고차원 개념은 기초 클래스에 넣는다.

기초 클래스는 구현 정보에 무지해야 한다.

소스 파일, 컴포넌트, 모듈도 마찬가지다.

어느 경우든 고차원 개념과 저차원 개념을 섞어서는 안 된다.

**G7: 기초 클래스가 파생 클래스에 의존한다.**

기초 클래스가 파생 클래스를 사용한다면 뭔가 문제가 있다는 말이다/

간혹 파생 클래스 갯수가 확실히 고정되어 있다면 기초 클래스에 파생 클래스를 선택하는 코드가 들어가기도 한다.

일반적으로 기초 클래스와 파생 클래스를 다른 `jar` 파일로 배포하는 편이 좋다.

**G8: 과도한 정보.**

잘 정의된 모듈은 인터페이스가 아주 작다.

부실하게 정의된 모듈은 인터페이스가 구질구질하다.

클래스가 제공하는 메서드 수는 작을수록 좋다.

함수가 아는 변수 수도 작을수록 좋다.

클래스에 들어있는 인스턴스 변수 수도 작을수록 좋다.

자료를 숨겨라.

하위 클래스에서 필요하다는 이유로 `protected` 변수나 함수를 마구 생성하지 마라.

매우 깐깐하게 정보를 제한해 결합도를 낮춰라.

**G9: 죽은 코드.**

죽은 코드란 실행되지 않는 코드를 가리킨다.

불가능한 조건을 확인하는 if문과 throw 문이 없는 try 문에서 catch 블록이 좋은 예다.

죽은 코드를 발견하면 시스템에서 제거하도록 하자.

**G10: 수직 분리.**

변수와 함수는 사용되는 위치에 가깝게 정의한다.

* 지역 변수는 처음으로 사용하기 직전에 선언하며 수직으로 가까운 곳에 위치해야 한다.
* 비공개 함수는 처음으로 호출한 직후에 정의한다.

**G11: 일관성 부족.**

어떤 개념을 특정 방식으로 구현했다면 유사한 개념도 같은 방식으로 구현한다.

간단한 일관성 만으로도 코드를 읽고 수정하기가 대단히 쉬워진다.

**G12: 잡동사니.**

아무도 사용하지 않는 변수, 아무도 호출하지 않는 함수, 정보를 제공하지 못하는 주석 등등

코드만 복잡하게 만드는 요소들은 제거해야 한다.

**G13: 인위적 결합.**

서로 무관한 개념을 인위적으로 결합하지 않는다.

함수, 상수, 변수를 선언할 때는 시간을 들여 올바른 위치를 고민한다.

**G14: 기능 욕심.**

클래스 메서드는 자기 클래스의 변수와 함수에 관심을 가져야지 다른 클래스의 변수와 함수에 관심을 가져서는 안된다.

메서드가 다른 객체의 참조자와 변경자를 사용해 그 객체 내용을 조작한다면

메서드가 그 객체 클래스의 범위를 욕심낸다는 뜻이다.

기능 욕심은 한 클래스의 속사정을 다른 클래스에 노출하므로, 별다른 문제가 없다면, 제거하는 편이 좋다.

하지만 때로는 어쩔 수 없는 경우도 생기므로 신중하게 판단하도록 한다.

**G15: 선택자 인수.**

함수 호출 끝에 달리는 `false`만큼이나 밉살스런 코드도 없다.

선택자 인수는 목적을 기억하기 어려울 뿐 아니라 각 선택자 인수가 여러 함수를 하나로 조합한다.

선택자 인수는 큰 함수를 작은 함수 여럿으로 쪼개지 않으려닌 게으름의 소산이다.

Boolean 뿐만 아니라 enum, int 등 함수 동작을 제어하려는 인수는 하나 같이 바람직하지 않다.

**G16: 모호한 의도.**

코드를 짤 때는 의도를 최대한 분명히 밝힌다.

행을 바꾸지 않고 표현한 수식, 헝가리식 표기법, 매직 넘버 등은 모두 저자의 의도를 흐린다.

독자에게 의도를 분명히 표현하도록 시간을 투자할 가치가 있다.

**G17: 잘못 지운 책임.**

소프트웨어 개발자가 내리는 가장 중요한 결정 중 하나가 코드를 배체하는 위치다.

코드는 독자가 자연스럽게 기대할 위치에 배치한다.

**G18: 부적절한 static 함수.**

일반적으로 static 함수보다 인스턴스 함수가 더 좋다.

조금이라도 의심스럽다면 인스턴스 함수로 정의한다.

반드시 static 함수로 정의해야겠다면 재정의할 가능성은 없는지 꼼꼼히 따져본다.

**G19: 서술적 변수.**

프로그램 가독성을 높이는 가장 효과적인 방법 중 하나가 계산을 여러 단계로 나누고

중간 값으로 서술적인 변수 이름을 사용하는 방법이다.

```
Matcher match = headerPattern.matcher(line);
if (match.find()) {
    String key = match.group(1);
    String value = match.group(2);
    headers.put(key.toLowerCase(), value);
}
```

위의 코드를 보면 서술적인 변수 이름을 사용한 탓에 첫 번째로 일치하는 그룹이 **key** 이고

두번째로 일치하는 그룹이 **value** 라는 사실이 명확히 드러난다.

서술적인 변수 이름은 많을수록 더 좋다.

계산을 몇 단계로 나누고 중간 값에 좋은 변수 이름만 붙여도 해독하기 어렵던 모듈이 순식간에 읽기 쉬운 모듈로 탈바꿈한다.

**G20: 이름과 기능이 일치하는 함수.**

이름만으로 분명하지 않기에 구현을 살피거나 문서를 뒤적여야 한다면 더 좋은 이름으로 바꾸거나 아니면 더 좋은 이름을

붙이기 쉽도록 기능을 정리해야 한다.

**G21: 알고리즘을 이해하라.**

구현이 끝났다고 선언하기 전에 함수가 돌아가는 방식을 확실히 이해하는지 확인하라.

테스트 케이스를 모두 통과한다는 사실만으로 부족하다.

작성자가 알고리즘이 올바르다는 사실을 알아야 한다.

알고리즘이 올바르다는 사실을 확인하고 이해하려면 기능이 뻔히 보일 정도로

함수를 깔끔하고 명확하게 재구성하는 방법이 최고다.

**G22: 논리적 의존성은 물리적으로 드러내라.**

한 모듈이 다른 모듈에 의존한다면 물리적인 의존성도 있어야 한다.

의존하는 모듈이 상대 모듈에 대해 뭔가를 가정하면(논리적으로 의존하면) 안된다.

의존하는 모든 정보를 명시적으로 요청하는 편이 좋다.

**G23: if / else 혹인 switch / case 문보다 다형성을 사용하라.**

1. Switch 문을 선택하기 전에 다형성을 먼저 고려하라.
2. 유형보다 함수가 더 쉽게 변하는 경우는 극히 드물다. 그러므로 모든 Switch 문을 의심해야한다.

선택 유형 하나에는 switch문을 한 번만 사용한다.

같은 선택을 수행하는 다른 코드에서는 다형성 객체를 생성해 switch 문을 대신한다.

**G24: 표준 표기법을 따르라.**

팀은 업계 표준에 기반한 구현 표준을 따라야 한다.

구현 표준은 인스턴스 변수 이름을 선언하는 위치, 클래스 / 메서드 / 변수 이름을 정하는 방법,

괄호를 넣는 위치 등을 명시해야 한다.

팀이 정한 표준은 팀원들 모두가 따라야 한다.

**G25: 매직 숫자는 명명된 상수로 교체하라.**

일반적으로 코드에서 숫자를 사용하지 말라는 규칙이다.

숫자는 명명된 상수 뒤로 숨겨라.

**매직 숫자 라는 용어는 단시 숫자만 의미하지 않는다.**

의미가 분명하지 않은 토큰을 모두 가리킨다.

**G26: 정확하라.**

검색 결과 중 첫 번째 결과만 유일한 결과로 간주하는 행동은 순진하다.

갱신할 가능성이 희박하다고 잠금과 트랜잭션 관리를 건너뛰는 행동은 아무리 잘 봐줘도 게으름이다.

코드에서 뭔가를 결정할 때는 정확히 결정해야 한다.

**결정을 내리는 이유와 예외를 처리할 방법을 분명히 알아야 한다.**

호출하는 함수가 `null` 을 반환할지도 모른다면 반드시 `null` 을 점검한다.

**G27: 관례보다 구조를 사용하라.**

설계 결정을 강제할 때는 규칙보다 관례를 사용한다.

switch / case 문보다 추상 메서드가 있는 기초 클래스가 더 좋다.

switch / case 문을 매번 똑같이 구현하게 강제하기는 어렵지만,

파생 클래스는 추상 메서드를 모두 구현하지 않으면 안되기 때문이다.

**G28: 조건을 캡슐화하라.**

부울 논리는 이해하기 어렵다.

조건의 의도를 분명히 밝히는 함수로 표현하라.

```java
if (shouldBeDeleted(time))
```

이라는 코드는

```java
if (timer.hasExpired() && !timer.isRecurrent())
```

보다 좋다.

**G29: 부정 조건은 피하라.**

부정 조건은 긍정 조건보다 이해하기 어렵다.

가능하면 긍정 조건으로 표현한다.

**G30: 함수는 한 가지만 해야 한다.**

함수를 짜다보면 한 함수안에 여러 단락을 이어, 일련의 작업을 수행하고픈 유혹에 빠진다.

한가지만 수행하는 좀 더 작은 함수 여럿으로 나눠야 마땅하다.

**G31: 숨겨진 시간적인 결합.**

떄로는 시간적인 결합이 필요하다.

하지만 시간적인 결합을 숨겨서는 안된다.

함수를 짤 때는 함수 인수를 적절히 배치해 함수가 호출되는 순서를 명백히 드러낸다.

**G32: 일관성을 유지하라.**

코드 구조를 잡을 때는 이유를 고민하라.

구조에 일관성이 없어 보인다면 남들이 맘대로 바꿔도 괜찮다고 생각한다.

**G33: 경계 조건을 캡슐화하라.**

경계 조건은 빼먹거나 놓치기 쉽상이다.

경계 조건은 한 곳에서 별도로 처리한다.

다시말해 코드 여기저기에 +1, -1 등을 흩어놓지 않는다.

**G34: 함수는 추상화 수준을 한 단계만 내려가야 한다.**

함수 내 모든 문장은 추상화 수준이 동일해야 한다.

그 추상화 수준은 **함수 이름이 의미하는 작업보다 한 단계만 낮아야 한다.**

함수에서 추상화 수준을 분리하면 앞서 드러나지 않았던 새로운 추상화 수준이 드러나는 경우가 빈번하다.

**G35: 설정 정보는 최상위 단계에 둬라.**

추상화 최상위 단계에 둬야 할 기본값 상수나 설정 관련 상수를 저차원 함수에 숨겨서는 안된다.

대신 고차원 함수에서 저차원 함수를 호출할 때 인수로 넘긴다.

설정 관련 상수는 최상위 단계에 둔다.

그래야 변경하기도 쉽다.

저차원 함수에 상수 값을 정의하면 안 된다.

**G36: 추이적 탐색을 피하라.**

일반적으로 한 모듈은 주변 모듈을 모를수록 좋다.

**A가 B를 사용하고, B가 C를 사용한다 하더라도 A가 C를 알아야 할 필요는 없다.**

즉, `a.getB().getC().doSomething();` 은 바람직하지 않다.

#### 5. 자바.

**J1: 긴 import 목록을 피하고 와일드 카드를 사용하라.**

패키지에서 클래스를 둘 이상 사용한다면 와일드 카드를 사용해 패키지 전체를 가져오라.

**J2: 상수는 상속하지 않는다.**

어떤 프로그래머는 상수를 인터페이스에 넣은 다음 그 인터페이스를 상속해 해당 상수를 사용한다.

상속을 이렇게 사용하면 언어의 범위 규칙을 속이는 행위다.

대신 `static import` 를 사용하라.

**J3: 상수 대 Enum.**

자바 5는 enum 을 제공한다. 마음껏 활용하라.

메서드와 필드도 사용할 수 있기 때문에 int 보다 훨씬 더 유연하고 서술적인 강력한 도구다.

#### 6. 이름.

**N1: 서술적인 이름을 사용하라.**

이름은 성급하게 정하지 않는다.

서술적인 이름을 신중하게 고른다.

**소프트웨어 가독성의 90%는 이름이 결정한다.**

신중하게 선택한 이름은 추가 설명을 포함한 코드보다 강력하다.

**N2: 적절한 추상화 수준에서 이름을 선택하라.**

구현을 드러내는 이름은 피하라.

작업 대상 클래스나 함수가 위치하는 추상화 수준을 반영하는 이름을 선택하라.

코드를 살펴볼 때마다 추상화 수준이 너무 낮은 변수 이름을 발견하리라.

발견할 때마다 기회를 잡아 바꿔놓아야 한다.

안정적인 코드를 만들려면 지속적인 개선과 노력이 필요하다.

**N3: 가능하다면 표준 명명법을 사용하라.**

기존 명명법을 사용하는 이름은 이해하기 더 쉽다.

프로젝트에 유효한 의미가 담긴 이름을 많이 사용할수록 독자가 코드를 이해하기 쉬워진다.

**N4: 명확한 이름.**

함수나 변수의 목적을 명확히 밝히는 이름을 선택한다.

**N5: 긴 범위는 긴 이름을 사용하라.**

이름 길이는 범위 길이에 비례해야 한다.

범위가 작으면 아주 짧은 이름을 사용해도 괜찮다.

하지만 이름이 짧은 변수나 함수는 범위가 길어지면 의미를 잃는다.

그러므로 이름 범위가 길수록 이름을 정확하고 길게 짓는다.

**N6: 인코딩을 피하라.**

이름에 유형 정보나 범위 정보를 넣어서는 안 된다.

**오늘날 개발 환경에서는 m\_ 나 f 와 같은 접두어가 불필요하다.**

헝가리안 표기법의 오염에서 이름을 보호하라.

**N7: 이름으로 부수 효과를 설명하라.**

함수, 변수, 클래스가 하는 일을 모두 기술하는 이름을 사용한다.

실제로 여러 작업을 수행하는 함수에다 동사 하나만 사용하면 곤란하다.

```java
public ObjectOutputStream getOos() throws IOException {
    if (m_oos == null) {
        m_oos = new ObjectOutputStream(m_socket.getOutputStream());
    }

    return m_oos;
}
```

위 함수는 단순히 **oos만 가져오지 않는다.**

기존 `oos` 가 없으면 생성한다.

그러므로 **createOrReturnOos** 라는 이름이 더 좋다.

#### 7. 테스트.

**T1: 불충분한 테스트.**

테스트 케이스는 잠재적으로 꺠질 만한 부분을 모두 테스트해야 한다.

테스트 케이스가 확인하지 않는 조건이나 검증하지 않는 계산이 있다면 그 테스트는 불완전하다.

**T2: 커버리지 도구를 사용하라!.**

커버리지 도구는 테스트가 빠뜨리는 공백을 알려준다.

커버리지 도구를 사용하면 테스트가 불충분한 모듈, 클래스, 함수를 찾기가 쉬워진다.

대다수 IDE는 테스트 커버리지를 시각적으로 표현한다.

**T3: 사소한 테스트를 건너뛰지 마라.**

사소한 테스트는 짜기 쉽다. 사소한 테스트가 제공하는 문서적 가치는 구현에 드는 비용을 넘어선다.

**T4: 무시한 테스트는 모호함을 뜻한다.**

때로는 요구사항이 불분명하기에 프로그램이 돌아가는 방식을 확신하기 어렵다.

불분명한 요구사항은 테스트 케이스를 주석으로 처리하거나 `@Ignore` 를 붙여 표현한다.

**T5: 경계 조건을 테스트 하라.**

경계 조건은 각별히 신경 써서 테스트한다.

**T6: 버그 주변은 철저히 테스트하라.**

버그는 서로 모이는 경향이 있다.

한 함수에서 버그를 발견했다면 그 함수를 철저히 테스트하는 편이 좋다.

**T7: 실패 패턴을 살펴라.**

때로는 테스트 케이스가 실패하는 패턴으로 문제를 진단할 수 있다.

합리적인 순서로 정렬된 꼼꼼한 테스트 케이스는 실패 패턴을 드러낸다.

**T8: 테스트 커버리지 패턴을 살펴라.**

통과하는 테스트가 실행하거나 실행하지 않는 코드를 살펴보면 실패하는 테스트 케이스의 실패 원인이 드러난다.

**T9: 테스트틑 빨라야 한다.**

느린 테스트 케이스는 실행하지 않게 된다.

테스트 케이스가 빨리 돌아가게 최대한 노력한다.

#### 8. 결론.

이 장에서 소개한 휴리스틱과 냄새 목록이 완전하다 말하기는 어렵다.

여기서 소개한 목록은 가치 체계를 피력할 뿐이다.

일군의 규칙만 따른다고 깨끗한 코드가 얻어지지 않는다.

휴리스틱 목록을 익힌다고 소프트웨어 장인이 되지 못한다.

전문가 정신과 장인 정신은 가치에서 나온다.

그 가치에 기반한 규율과 절제가 필요하다.
