# Customize Bean

Spring Framework는 **Bean의 특성**을 맞춤 설정하기 위해 사용할 수 있는 여러 **인터페이스**를 제공합니다. 이 섹션에서는 이를 몇 가지로 그룹화하여 설명합니다:

1. **Lifecycle Callbacks (생명주기 콜백)**
2. **ApplicationContextAware 및 BeanNameAware**
3. **기타 Aware 인터페이스**

## **Lifecycle Callbacks**

Spring 컨테이너에서 관리하는 Bean의 생명주기(lifecycle)와 상호 작용하려면, `InitializingBean`과 `DisposableBean` 인터페이스를 구현할 수 있습니다.

* `InitializingBean` 인터페이스는 `afterPropertiesSet()` 메서드를 제공하여 Bean이 초기화 작업을 할 수 있도록 합니다.
* `DisposableBean` 인터페이스는 `destroy()` 메서드를 통해 Bean이 파괴될 때 작업을 수행할 수 있습니다.

`@PostConstruct`와 `@PreDestroy` 애노테이션을 사용하는 것이 모던한 Spring 애플리케이션에서 생명주기 콜백을 받는 **최선의 방법**으로 권장됩니다. 이를 사용하면 Bean이 Spring에 종속되지 않으므로 더 유연하게 사용할 수 있습니다.

### **InitializingBean**

**Spring에서 초기화 작업을 수행하려면** `InitializingBean` 인터페이스를 구현하고, `afterPropertiesSet()` 메서드를 오버라이드합니다. 이 메서드는 Spring 컨테이너가 해당 Bean에 필요한 모든 속성을 설정한 후에 호출됩니다.

```xml
<bean id="exampleInitBean" class="examples.ExampleBean" init-method="init"/>
```

```java
public class ExampleBean {
    public void init() {
        // Bean 초기화 작업 수행
    }
}
```

위의 예시는 `InitializingBean`을 직접 구현한 예시와 거의 동일한 역할을 합니다.

```java
public class AnotherExampleBean implements InitializingBean {
    @Override
    public void afterPropertiesSet() {
        // Bean 초기화 작업 수행
    }
}
```

> 주의: `@PostConstruct`와 초기화 메서드는 Bean이 **완전히 초기화된 후**에 실행됩니다. 이 단계에서는 외부 Bean과의 상호작용을 하기 전에 내부적인 초기화만 수행해야 하며, 그렇지 않으면 \*\*초기화 교착 상태(deadlock)\*\*가 발생할 수 있습니다.

**비용이 많이 드는 후속 초기화 작업**

비용이 많이 드는 초기화 작업(예: 비동기 데이터베이스 준비)을 트리거해야 할 경우에는 `SmartInitializingSingleton.afterSingletonsInstantiated()` 메서드를 구현하거나 `ContextRefreshedEvent`를 처리하는 **ApplicationListener**를 구현하여 모든 싱글톤 초기화가 끝난 후에 작업을 처리하는 것이 좋습니다.

***

### **DisposableBean**

**Bean이 파괴될 때**(애플리케이션 종료 시 등) 특정 작업을 수행하려면, `DisposableBean` 인터페이스를 구현하고, `destroy()` 메서드를 오버라이드하여 작업을 정의할 수 있습니다.

```xml
<bean id="exampleDestructionBean" class="examples.ExampleBean" destroy-method="cleanup"/>
```

```java
public class ExampleBean {
    public void cleanup() {
        // Bean 파괴 작업 수행 (예: 리소스 해제)
    }
}
```

이 예시는 `DisposableBean` 인터페이스를 구현한 다음의 예시와 동일한 역할을 합니다:

```java
public class AnotherExampleBean implements DisposableBean {
    @Override
    public void destroy() {
        // Bean 파괴 작업 수행
    }
}
```

> **주의**: Bean을 파괴할 때도 `@PreDestroy` 애노테이션이나 `destroy-method` 속성을 사용하는 것이 권장되며, Spring에 종속되지 않는 방식으로 구현할 수 있습니다.

***

## **Default Initialization and Destroy Methods**

**기본 초기화/파괴 메서드 설정**: Spring에서는 Bean에 대해 일관된 라이프사이클 콜백 메서드 이름을 지정할 수 있습니다.&#x20;

예를 들어, 모든 Bean에서 초기화 메서드가 `init()`이고, 파괴 메서드가 `destroy()`라면, 각 Bean 정의마다 메서드를 명시할 필요 없이 `default-init-method`와 `default-destroy-method` 속성을 사용하여 이를 전역으로 적용할 수 있습니다.

```xml
<beans default-init-method="init" default-destroy-method="destroy">
    <bean id="myService" class="com.example.MyService"/>
</beans>
```

이 경우, `MyService` 클래스에 `init()`과 `destroy()` 메서드가 있다면, Bean이 생성되거나 파괴될 때 자동으로 호출됩니다.

***

## **ApplicationContextAware 및 BeanNameAware**

**ApplicationContextAware**: Bean이 **ApplicationContext**를 참조하도록 설정하려면 `ApplicationContextAware` 인터페이스를 구현하여 `setApplicationContext()` 메서드를 오버라이드합니다.

```java
public class MyBean implements ApplicationContextAware {
    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
```

*   **BeanNameAware**: Bean이 **자신의 이름**을 참조하도록 설정하려면 `BeanNameAware` 인터페이스를 구현하여 `setBeanName()` 메서드를 오버라이드합니다.

    ```java
    public class MyBean implements BeanNameAware {
        @Override
        public void setBeanName(String name) {
            System.out.println("Bean name: " + name);
        }
    }
    ```

이 인터페이스들은 Bean이 자신을 생성한 컨테이너나 자신에 대한 정보를 참조할 수 있게 해줍니다.

***

#### 3. **기타 Aware 인터페이스**

Spring은 추가적으로 다양한 **Aware** 인터페이스를 제공하여 Bean이 필요한 **인프라 종속성**을 컨테이너로부터 제공받을 수 있도록 합니다. 대표적인 Aware 인터페이스는 다음과 같습니다:

| **인터페이스**                   | **주입되는 의존성**                | **설명**                    |
| --------------------------- | --------------------------- | ------------------------- |
| **ApplicationContextAware** | ApplicationContext          | 컨테이너를 참조합니다.              |
| **BeanClassLoaderAware**    | Bean 클래스 로더                 | Bean 클래스 로더를 참조합니다.       |
| **BeanFactoryAware**        | BeanFactory                 | BeanFactory를 참조합니다.       |
| **ResourceLoaderAware**     | ResourceLoader              | 리소스를 참조하는 로더입니다.          |
| **MessageSourceAware**      | MessageSource               | 메시지 해결 전략을 주입받습니다.        |
| **ServletContextAware**     | ServletContext (웹 애플리케이션에서) | 현재 ServletContext를 참조합니다. |

이 인터페이스들은 주로 인프라에 가까운 Bean에서 사용되며, 일반 애플리케이션 코드에서는 Spring에 대한 의존성을 줄이기 위해 피하는 것이 좋습니다.

***

#### **Lifecycle 인터페이스를 활용한 Startup 및 Shutdown 콜백**

Spring에서는 **Lifecycle** 인터페이스를 구현하여 Bean이 애플리케이션 컨텍스트가 시작되거나 종료될 때 자동으로 시작/종료 메서드를 호출할 수 있습니다.

```java
public interface Lifecycle {
    void start();
    void stop();
    boolean isRunning();
}
```

이 인터페이스는 애플리케이션이 시작되거나 중지될 때 특정 작업을 수행해야 하는 객체에 적합합니다. 예를 들어, 백그라운드에서 실행되는 서비스가 있을 때 유용합니다.

* **SmartLifecycle** 인터페이스는 `Lifecycle`을 확장하며, 더 세밀하게 제어할 수 있는 기능을 제공합니다. 예를 들어, `getPhase()` 메서드를 통해 Bean이 시작되거나 종료될 **순서**를 지정할 수 있습니다.

```java
public interface SmartLifecycle extends Lifecycle, Phased {
    boolean isAutoStartup();
    void stop(Runnable callback);
}
```

`getPhase()` 메서드는 시작 순서와 종료 순서를 지정할 때 사용되며, 값이 낮을수록 먼저 시작되고 나중에 종료됩니다.
