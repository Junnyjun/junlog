# Bean Overview

스프링 IoC 컨테이너에서 관리되는 **Bean**은 애플리케이션의 구성 요소를 나타내며, 이 Bean은 XML `<bean/>` 정의 또는 다른 형식의 메타데이터를 사용하여 생성됩니다.&#x20;

Spring은 이러한 bean 정의를 통해 **BeanDefinition** 객체를 내부적으로 사용해 각 bean의 메타데이터를 저장합니다. 이 BeanDefinition 객체에는 다음과 같은 주요 정보가 포함됩니다:

1. **클래스 이름**: 해당 bean을 구현하는 클래스의 패키지-포함 이름.
2. **Bean의 동작 설정**: 컨테이너에서 해당 bean의 동작 방식을 지정하는 요소(예: 범위, 라이프사이클 콜백 등).
3. **다른 Bean에 대한 참조**: 해당 bean이 작업을 수행하는 데 필요한 다른 bean에 대한 참조(의존성).
4. **기타 구성 요소**: 생성된 객체의 추가 설정 (예: 커넥션 풀의 연결 수 제한 등).

### Bean 정의 속성

스프링에서 관리하는 각 BeanDefinition은 여러 속성들로 구성됩니다:

| **속성**                | **설명**                              |
| --------------------- | ----------------------------------- |
| **클래스 (Class)**       | Bean의 구현 클래스를 지정합니다.                |
| **이름 (Name)**         | Bean의 고유 식별자입니다.                    |
| **범위 (Scope)**        | Bean의 라이프사이클을 정의합니다. (싱글톤, 프로토타입 등) |
| **생성자 인자**            | 의존성 주입을 위한 생성자 인자를 지정합니다.           |
| **프로퍼티 (Properties)** | 의존성 주입을 위한 프로퍼티를 지정합니다.             |
| **자동 연결 모드**          | 자동으로 의존성을 주입하는 방식을 지정합니다.           |
| **지연 초기화 모드**         | 해당 bean이 지연 초기화될지를 지정합니다.           |
| **초기화 메서드**           | bean의 초기화 메서드를 지정합니다.               |
| **소멸 메서드**            | bean의 소멸 메서드를 지정합니다.                |

#### Bean 등록 및 관리

일반적으로 `ApplicationContext`는 사전에 정의된 메타데이터를 사용하여 bean을 관리합니다. 하지만 컨테이너 외부에서 생성된 객체도 `registerSingleton()` 및 `registerBeanDefinition()` 메서드를 사용하여 수동으로 등록할 수 있습니다. 단, 이러한 객체나 메타데이터는 가능한 한 빨리 등록되어야 합니다. 그렇지 않으면 자동 와이어링이나 컨테이너가 bean을 관리하는데 문제가 발생할 수 있습니다.

```kotlin
@ExtendWith(SpringExtension::class)
@ContextConfiguration("file:web/WEB-INF/spring/applicationContext.xml")
class IocContainerTest {

    @Autowired
    lateinit var beanFactory: BeanFactory

    @Autowired
    lateinit var applicationContext:GenericApplicationContext
    @Test
    fun test() {
        applicationContext.registerBean<Any>("test")
    }
}

```

#### Bean 이름 지정

모든 bean은 고유한 식별자를 가져야 하며, XML 기반 메타데이터에서 `id` 또는 `name` 속성을 사용하여 이를 지정할 수 있습니다. `name` 속성은 여러 개의 별칭을 가질 수 있으며, 스페이스, 쉼표, 세미콜론으로 구분합니다. 별칭은 bean의 사용을 더욱 유연하게 만들어 줍니다.

#### Bean 오버라이딩

bean 오버라이딩은 동일한 식별자로 여러 bean을 등록할 때 발생합니다. 오버라이딩은 지원되지만, 향후 스프링 버전에서는 이 기능이 더 이상 지원되지 않을 예정입니다. `allowBeanDefinitionOverriding`을 `false`로 설정하여 오버라이딩을 금지할 수 있으며, 기본적으로 오버라이딩 시 INFO 레벨의 로그가 기록됩니다.

#### Bean 인스턴스화

Bean 정의는 객체를 생성하는 "레시피"와 같으며, 컨테이너가 `getBean()`을 호출할 때 이 레시피를 바탕으로 bean을 생성합니다. XML 기반 설정에서는 `<bean>` 태그의 `class` 속성으로 bean의 클래스를 지정합니다.

**생성자 인스턴스화**

일반적으로 기본 생성자를 사용하여 bean을 인스턴스화하지만, 경우에 따라 생성자 인자나 프로퍼티 값을 통해 객체를 생성할 수 있습니다.

**정적 팩토리 메서드**

정적 팩토리 메서드를 사용해 객체를 생성할 수도 있습니다. 이 경우 `factory-method` 속성을 사용해 팩토리 메서드를 지정합니다.

```xml
<bean id="clientService" class="examples.ClientService" factory-method="createInstance"/>
```

**인스턴스 팩토리 메서드**

기존에 등록된 bean의 인스턴스 메서드를 사용하여 새로운 bean을 생성할 수도 있습니다. 이 경우 `factory-bean` 속성을 사용하여 팩토리 메서드가 포함된 bean을 지정합니다.

```xml
<bean id="clientService" factory-bean="serviceLocator" factory-method="createClientServiceInstance"/>
```

#### Bean의 런타임 타입 확인

bean의 런타임 타입을 확인하려면 `BeanFactory.getType()` 메서드를 사용합니다. 이를 통해 실제 객체 타입을 확인할 수 있습니다.

위 내용을 바탕으로 Spring IoC 컨테이너는 객체 간의 의존성을 관리하고, 다양한 방법으로 bean을 정의, 생성 및 관리할 수 있습니다.
