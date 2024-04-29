# 오류 처리

오류 처리는 프로그램에 반드시 필요한 요소 중 하나일 뿐이다.

**뭔가 잘못될 가능성은 늘 존재한다.**

뭔가 잘못되면 바로 잡을 책임은 **프로그래머** 에게 있다.

깨끗한 코드와 오류 처리는 연관성이 있다. 오류 처리 코드로 인해 프로그램 논리를 이해하기 어려워진다면 깨끗한 코드라 부르기 어렵다.

#### 1. 오류 코드보다 예외를 사용하라.

예외를 지원하지 ㅇ낳는 언어는 오류를 처리하고 보고하는 방법이 제한적이었다. 오류 플래그를 설정하거나 호출자에게 오류 코드를 반환하는 방법이 전부였다.

이 방법들을 사용하게되면 호출자 코드가 함수를 호출한 즉시 오류를 확인해야하기 때문에 복잡해지게 된다.

때문에 오류가 발생하면 예외를 던지는 편이 낫다.

#### 2. Try - Catch - Finally 문 부터 작성하라.

어떤 면에서 try블록은 트랜잭션과 비슷하다.

try 블록에서 무슨 일이 생기든지 catch 블록은 프로그램 상태를 일관성 있게 유지해야 한다.

책에서는 강제로 예외를 일으키는 테스트 케이스를 작성한 후 테스트를 통과하게 코드를 작성하는 방법을 권장한다.

#### 3. 미확인 예외를 사용하라.

여러 해 동안 자바 프로그래머들은 확인된 (Checked) 예외의 장단점을 놓고 논쟁을 벌여왔다.

지금은 안정적인 소프트웨어를 제작하는 요소로 확인된 예외가 반드시 필요하지는 않다는 사실이 분명해졌다.

그러므로 우리는 확인된 오류가 치르는 비용에 상응하는 이익을 제공하는지 따져봐야 한다.

**확인된 예외는 OCP를 위반한다.**

메서드에서 확인된 예외를 던졌는데 catch블록이 세 단계 위에 있다면 그 사이 메서드의 선언부에 해당 예외를 정의해야 한다.

만약 이런 변경이 최하단의 메서드에서 일어났다면 해당 메서드를 호출하는 함수 모두가

1. catch 블록에서 새로운 예외를 처리하거나
2. 선언부에 throw 절을 추가해야 한다.

때로는 확인된 예외가 유용할 때가 있다.

아주 중요한 라이브러리를 작성한다면 모든 예외를 잡아야한다.

하지만 일반적인 어플리케이션은 의존성이라는 비용이 이익보다 크다.

#### 4. 예외에 의미를 제공하라.

예외를 던질 때는 전후 상황을 충분히 덧붙인다.

예외의 호출 스택만으로는 부족하다.

오류 메시지에 정보를 담아 예외와 함께 던진다. 실패한 연산 이름과 실패 유형도 언급한다.

#### 5. 호출자를 고려해 예외 클래스를 정의하라.

오류를 분류하는 방법은 수없이 많지만 오류를 정의할 때 가장 중요한 관심하는 **오류를 잡아내는 방법** 이 되어야 한다.

다음은 오류를 형편없이 분류한 사례다.

외부 라이브러리를 호출하는 try - catch - finally 문을 포함한 코드로, 라이브러리가 던질 예외를 모두 잡아 낸다.

```
ACMEPort port = new ACMEPort(12);

try {
    port.open();
} catch (DeviceResponseException e) {
    reportPortError(e);
    logger.log("Device response exception", e)
} catch (ATM1212UnlockedException e) {
    reportPortError(e);
    logger.log("Unlock excepion", e)
} catch (GMXError e) {
    reportPortError(e);
    logger.log("Device response exception")
} finally {
    ...
}
```

대다수 상황에서 우리가 오류를 처리하는 방식은

1. 오류를 기록한다.
2. 프로그램을 계속 수행해도 좋은지 확인한다.

이렇게 두가지이다.

위의 코드는 호출하는 라이브러리 API를 감싸면서 예외 유형 하나만 반환하게 하여 간결하게 고칠 수 있다.

```
public class LocalPort {
    private ACMEPort innerPort;

    public LocalPort(int portNumber) {
        innerPort = new ACMEPort(portNumber);
    }

    public void open() {
        try {
            innerPort.open();
        } catch (DeviceResponseException e) {
            throw new PortDeveiceFailure(e);
        } catch (ATM1212UnlockedException e) {
            throw new PortDeveiceFailure(e);
        } catch (GMXError e) {
            throw new PortDeveiceFailure(e);
        } finally {
            ...
        }
    }
 }
```

이렇게 외부 라이브러리를 감싼 클래스를 사용하여 코드를 작성하면 다음과 같다.

```
LocalPort port = new LocalPort(12);

try {
    port.open();
} catch (PortDeviceFailure e) {
    reportError(e);
    logger.log(e.getMessage(), e);
}
```

여기서 `LocalPort` 클래스는 단순히 `ACMEPort` 클래스가 던지는 예외를 잡아 변환하는 **감싸기(wrapper)** 클래스일 뿐이다.

감싸기 클래스는 매우 유용한다.

외부 api를 사용할 때는 감싸기 기법이 최선이다.

외부 api를 감싸면 외부 라이브러리와 프로그램 사이에 의존성이 크게 줄어든다.

또하나의 장점으로 감싸기 기법을 사용하면 특정 업체가 API를 설계한 방식에 발목잡히지 않는다는 점이 있다.

예외 클래스에 포함된 정보로 오류를 구분해도 괜찮은 경우에는 예외 클래스가 하나만 있어도 충분하다.

한 예외는 잡아내고 다른 예외는 무시해도 괜찮은 경우라면 여러 예외 클래스를 사용한다.

#### 6. 정상 흐름을 정의하라.

비즈니스 논리와 오류 처리가 잘 분리된 코드가 나오다보면 오류 감지가 프로그램 언저리로 밀려나게 된다.

외부 API를 감싸 독자적인 예외를 던지고, 코드 위에 처리기를 정의해 중단된 계산을 처리하는 방법이 멋진 처리 방식이지만

때로는 중단이 적합하지 않은 때도 있다.

다음 비용 청구 어플리케이션 예제 코드를 보도록하자.

```
try {
    MealExpenses expenses = expenseReportDAO.getMeals(employee.getID());
    m_total += expenses.getTotal();
} catch (MealExpensesNotFound e) {
    m_total += getMealPerDiem();
}
```

위의 코드는 식비를 비용으로 청구했다면 직원이 청구한 식비를 총계에 더하는 코드이다.

만약 식비를 비용으로 청구하지 않았다면 일일 기본 식비를 총계에 더한다.

그런데 예외가 논리를 따라가기 어렵게 만든다.

특수 상황을 처리할 필요가 없다면 코드가 훨씬 더 간결해진다.

```
MealExpenses expenses = expenseReportDAO.getMeals(employee.getID());
m_total += expenses.getTotal();
```

ExpenseReportDAO를 고쳐 언제나 MealExpense 객체를 반환하게 한다. 청구한 식비가 없다면 일일 기본 식비를 반환하는 MealExpense객체를 반환한다.

```
public class PerDiemMealExpenses implements MealExpense {
    public int getTotal() {
        // 기본값으로 일일 기본 식비를 반환한다.
    }
}
```

위와 같은 경우를 특수 사례 패턴이라고 한다.

클래스를 만들거나 객체를 조작해 특수 사례를 처리하는 방식이다.

클래스나 객체가 예외적인 상황을 캡슐화해서 처리하기 때문에 클라이언트 코드가 예외적인 상황을 처리할 필요가 없어진다.

#### 7. null을 반환하지 마라.

우리가 흔히 저지르는 바람에 오류를 유발하는 행위 중 하나가 바로 **null을 반환하는 습관이다.**

메서드에서 null을 반환하고픈 유혹이 든다면 그 대신 예외를 던지거나 특수 사례 객체를 반환한다.

만약 사용하려는 외부 api가 null을 반환한다면 감싸기 메서드를 구현해 예외를 던지거나 특수 사례 객체를 반환하는 방식을 고려한다.

많은 경우에 특수 사례 객체가 손쉬운 해결책이다.

#### 8. null을 전달하지 마라.

메서드에서 null을 반환하는 방식도 나쁘지만 null을 전달하는 방식은 더 나쁘다.

정상적인 인수로 null을 기대하는 api가 아니라면 메서드로 null을 전달하는 코드는 최대한 피한다.

대다수 프로그래밍 언어는 호출자가 실수로 null을 넘기는 것을 처리하는 방법이 ㅇ벗다.

그렇다면 애초에 null을 넘기지 못하도록 금지하는 정책이 합리적이다.

#### 9. 결론

깨끗한 코드는 읽기도 좋아야 하지만 안정성도 높아야 한다.

오류 처리를 프로그램 논리와 분리해 독자적인 사안으로 고려하면 튼튼하고 깨끗한 코드를 작성할 수 있다.

오류 처리를 프로그램 논리와 분리하면 독자적인 추론이 가능해지고, 코드 유지보수성도 크게 높아진다.
