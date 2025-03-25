# Proxy

Proxy 패턴은 클라이언트와 실제 객체(Real Subject) 사이에 대리자(Proxy)를 두어,\
클라이언트가 직접 실제 객체에 접근하지 않고 Proxy를 통해 접근하도록 하는 패턴입니다.

```
       ┌─────────┐
       │ Client  │
       └────┬────┘
            │
            ▼
    ┌────────────────┐
    │    Proxy       │
    │ (부가 기능 적용)│
    └────┬────┬─────┘
         │    │
         │    │ 위임 (Delegation)
         ▼    ▼
  ┌─────────┐  ┌────────────┐
  │ Real    │  │ 부가 기능  │
  │ Subject │  │ (로깅, 캐싱 등) │
  └─────────┘  └────────────┘

```

이를 통해 접근 제어나 추가 기능(로깅, 캐싱, 권한 검사 등)을 쉽게 구현할 수 있습니다.

***

### How do code

{% tabs %}
{% tab title="JAVA" %}
```java
// Specification: Proxy 패턴은 실제 객체에 대한 접근을 제어하거나 부가 기능을 추가하기 위해 사용됩니다.

// Service 인터페이스
public interface Service {
    String operation();
}

// 실제 객체 (Real Subject)
public class RealService implements Service {
    @Override
    public String operation() {
        return "실제 서비스의 동작 결과";
    }
}

// Proxy 객체
public class ServiceProxy implements Service {
    private Service realService;

    public ServiceProxy() {
        // 실제 객체를 생성하거나 주입합니다.
        this.realService = new RealService();
    }

    @Override
    public String operation() {
        // 호출 전 부가 기능 (예: 로깅)
        System.out.println("Proxy: operation() 호출 전");

        // 실제 객체에 위임
        String result = realService.operation();

        // 호출 후 부가 기능 (예: 결과 캐싱)
        System.out.println("Proxy: operation() 호출 후");

        return result;
    }
}
```
{% endtab %}

{% tab title="KOTLIN" %}
```kotlin
// Service 인터페이스
interface Service {
    fun operation(): String
}

// 실제 객체 (Real Subject)
class RealService : Service {
    override fun operation(): String {
        return "실제 서비스의 동작 결과"
    }
}

// Proxy 객체
class ServiceProxy(private val realService: Service = RealService()) : Service {
    override fun operation(): String {
        // 호출 전 부가 기능
        println("Proxy: operation() 호출 전")

        // 실제 객체에 위임
        val result = realService.operation()

        // 호출 후 부가 기능
        println("Proxy: operation() 호출 후")

        return result
    }
}
```
{% endtab %}
{% endtabs %}

이와 같이 Proxy 패턴은 클라이언트와 실제 객체 사이에서 중개자 역할을 수행하며,\
추가적인 기능(예: 로깅, 캐싱, 보안 검사 등)을 쉽게 적용할 수 있도록 돕습니다.
