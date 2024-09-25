# Bean Scopes

## Bean Scopes (빈 스코프)

Bean 정의를 생성할 때, 해당 Bean 정의에 의해 정의된 클래스의 실제 인스턴스를 생성하기 위한 "레시피"를 만드는 것입니다. Bean 정의가 레시피라는 개념은 중요하며, 이는 클래스와 마찬가지로 단일 레시피에서 여러 객체 인스턴스를 생성할 수 있음을 의미합니다.

Bean 정의에서 특정 Bean 정의로 생성된 객체의 다양한 의존성과 구성 값을 제어할 수 있을 뿐만 아니라, 특정 Bean 정의로 생성된 객체의 범위(scope)도 제어할 수 있습니다. 이 접근 방식은 객체의 범위를 Java 클래스 수준에서 고정하지 않고 구성(configuration)을 통해 선택할 수 있기 때문에 강력하고 유연합니다.&#x20;

Spring Framework는 여섯 가지 범위를 지원하며, 그 중 네 가지는 웹 인식(ApplicationContext) 환경에서만 사용할 수 있습니다. 또한, 사용자 정의 범위를 생성할 수도 있습니다.

### **Bean Scopes**

| **Scope**       | **Description**                                                                                                                 |
| --------------- | ------------------------------------------------------------------------------------------------------------------------------- |
| **singleton**   | (기본값) 각 Spring IoC 컨테이너당 단일 객체 인스턴스로 스코프를 지정합니다.                                                                                |
| **prototype**   | 단일 Bean 정의를 여러 객체 인스턴스로 스코프를 지정합니다.                                                                                             |
| **request**     | 단일 HTTP 요청의 생명주기에 Bean을 스코프합니다. 즉, 각 HTTP 요청마다 단일 Bean 정의를 기반으로 새로운 인스턴스가 생성됩니다. 이는 웹 인식 Spring ApplicationContext 환경에서만 유효합니다. |
| **session**     | HTTP 세션의 생명주기에 Bean을 스코프합니다. 이는 웹 인식 Spring ApplicationContext 환경에서만 유효합니다.                                                     |
| **application** | ServletContext의 생명주기에 Bean을 스코프합니다. 이는 웹 인식 Spring ApplicationContext 환경에서만 유효합니다.                                              |
| **websocket**   | WebSocket의 생명주기에 Bean을 스코프합니다. 이는 웹 인식 Spring ApplicationContext 환경에서만 유효합니다.                                                   |

**Thread scope**는 사용할 수 있지만 기본적으로 등록되어 있지 않습니다. 이 또는 기타 사용자 정의 스코프를 등록하는 방법에 대해서는 **Custom Scope 사용하기**를 참조하세요.

### Singleton Scope&#x20;

싱글톤 범위의 Bean은 단일 공유 인스턴스만 관리하며, 해당 Bean 정의와 일치하는 ID를 가진 모든 요청에 대해 Spring 컨테이너가 동일한 Bean 인스턴스를 반환합니다.

다시 말해, Bean 정의를 싱글톤으로 스코프하면 Spring IoC 컨테이너는 해당 Bean 정의에 의해 정의된 객체의 정확히 하나의 인스턴스만 생성합니다. 이 단일 인스턴스는 싱글톤 Bean 캐시에 저장되며, 이후의 모든 요청과 참조는 캐시된 객체를 반환합니다.&#x20;

<img src="../../../.gitbook/assets/file.excalidraw (56).svg" alt="" class="gitbook-drawing">

Spring의 싱글톤 개념은 Gang of Four(GoF) 패턴 책에서 정의된 싱글톤 패턴과 다릅니다. GoF 싱글톤은 ClassLoader당 하나의 인스턴스만 생성하도록 객체의 범위를 하드코딩합니다. 반면 Spring 싱글톤의 범위는 컨테이너 및 Bean당 하나로 설명할 수 있습니다.&#x20;

이는 단일 Spring 컨테이너에 특정 클래스의 Bean을 하나 정의하면, 그 Bean 정의에 의해 정의된 클래스의 인스턴스가 하나만 생성됨을 의미합니다. 싱글톤 스코프는 Spring에서 기본 스코프입니다. XML에서 싱글톤으로 Bean을 정의하는 예는 다음과 같습니다:

```xml
<bean id="accountService" class="com.something.DefaultAccountService"/>
<bean id="accountService" class="com.something.DefaultAccountService" scope="singleton"/>
```

<pre class="language-kotlin"><code class="lang-kotlin">@Component
class HomeController(
    var data: String = "hello spring"
) {
<strong>}
</strong>------------------------------------------------------------------
<strong>@ExtendWith(SpringExtension::class)
</strong><strong>@ContextConfiguration("file:web/WEB-INF/spring/applicationContext.xml")
</strong>class BeanScopeTest {
    @Autowired
    lateinit var homeController: HomeController

    @Autowired
    lateinit var applicationContext:ApplicationContext

    @Test
    fun test() {
        homeController.data = "new one"

        val bean = applicationContext.getBean("homeController") as HomeController
        assertEquals("new one", bean.data) // 변경된 값 유지
    }
}
</code></pre>

### Prototype Scope

프로토타입 스코프는 Bean 정의당 새로운 Bean 인스턴스를 생성하여 반환합니다. 즉, Bean을 주입받거나 `getBean()` 메서드를 통해 Bean을 요청할 때마다 새로운 인스턴스가 생성됩니다.&#x20;

일반적으로 상태를 가지는(Stateful) Bean에는 프로토타입 스코프를, 무상태(Stateless) Bean에는 싱글톤 스코프를 사용하는 것이 좋습니다.

<img src="../../../.gitbook/assets/file.excalidraw (56).svg" alt="" class="gitbook-drawing">

XML에서 프로토타입으로 Bean을 정의하는 예는 다음과 같습니다

```xml
<bean id="accountService" class="com.something.DefaultAccountService" scope="prototype"/>
```

<pre class="language-kotlin"><code class="lang-kotlin">@Component
class HomeController(
    var data: String = "hello spring"
) {
<strong>}
</strong>------------------------------------------------------------------
<strong>@ExtendWith(SpringExtension::class)
</strong><strong>@ContextConfiguration("file:web/WEB-INF/spring/applicationContext.xml")
</strong>class BeanScopeTest {
    @Autowired
    lateinit var homeController: HomeController

    @Autowired
    lateinit var applicationContext:ApplicationContext

    @Test
    fun test() {
        homeController.data = "new one"

        val bean = applicationContext.getBean("homeController") as HomeController
        assertEquals("hello spring", bean.data) // 인스턴스 재생성
    }
}
</code></pre>

다른 스코프와 달리 Spring은 프로토타입 Bean의 전체 생명주기를 관리하지 않습니다. 컨테이너는 프로토타입 객체를 인스턴스화, 설정 및 조립한 후 클라이언트에 전달하며, 그 이후로는 해당 프로토타입 인스턴스에 대한 기록을 유지하지 않습니다.&#x20;

따라서 초기화 생명주기 콜백 메서드는 모든 객체에 대해 호출되지만, 소멸 생명주기 콜백은 호출되지 않습니다. 클라이언트 코드는 프로토타입 스코프의 객체를 정리하고, 프로토타입 Bean이 보유한 비용이 많이 드는 리소스를 해제해야 합니다. 프로토타입 스코프의 Bean이 보유한 리소스를 해제하려면, 참조를 유지하고 정리해야 하는 Bean을 보유하는 사용자 정의 Bean Post-Processor를 사용하는 것이 좋습니다.

어떤 면에서는 Spring 컨테이너가 프로토타입 스코프 Bean에 대해 수행하는 역할이 Java의 `new` 연산자를 대체하는 것입니다. 그 이후의 모든 생명주기 관리는 클라이언트가 처리해야 합니다.

### Singleton Beans with Prototype-bean Dependencies

싱글톤 스코프 Bean이 프로토타입 스코프 Bean에 의존성을 가질 때, 의존성은 인스턴스화 시점에 해결됩니다. 따라서 싱글톤 Bean에 프로토타입 스코프 Bean을 의존성 주입하면, 새로운 프로토타입 Bean 인스턴스가 생성되어 싱글톤 Bean에 주입됩니다. 이 프로토타입 인스턴스는 싱글톤 스코프 Bean에 제공되는 유일한 인스턴스입니다.

<img src="../../../.gitbook/assets/file.excalidraw (57).svg" alt="" class="gitbook-drawing">

그러나 싱글톤 스코프 Bean이 런타임에 프로토타입 스코프 Bean의 새로운 인스턴스를 반복적으로 획득하려는 경우, 프로토타입 스코프 Bean을 싱글톤 Bean에 의존성 주입할 수 없습니다. 왜냐하면 이러한 주입은 Spring 컨테이너가 싱글톤 Bean을 인스턴스화하고 의존성을 해결할 때 단 한 번만 발생하기 때문입니다.&#x20;

## Request, Session, Application, and WebSocket Scopes&#x20;

`request`, `session`, `application`, `websocket` 스코프는 웹 인식 Spring ApplicationContext 구현체(`XmlWebApplicationContext` 등)에서만 사용할 수 있습니다. 이러한 스코프를 일반 Spring IoC 컨테이너(`ClassPathXmlApplicationContext` 등)와 함께 사용하면, 알 수 없는 Bean 스코프에 대한 `IllegalStateException`이 발생합니다.

### **초기 웹 설정**

요청, 세션, 애플리케이션, 웹소켓 수준에서 Bean의 스코핑을 지원하기 위해 일부 초기 설정이 필요합니다.&#x20;

<details>

<summary>설정 방법</summary>

**Spring Web MVC 사용 시**: `DispatcherServlet`을 통해 처리되는 요청 내에서 스코프된 Bean을 접근하면 특별한 설정이 필요 없습니다. `DispatcherServlet`이 이미 관련 상태를 노출하기 때문입니다.

**Servlet 웹 컨테이너 사용 시** (`JSF` 등에서 `DispatcherServlet` 외부에서 요청을 처리할 때):

`org.springframework.web.context.request.RequestContextListener` `ServletRequestListener`를 등록해야 합니다. 이는 `WebApplicationInitializer` 인터페이스를 사용하여 프로그래밍 방식으로 수행하거나, 웹 애플리케이션의 `web.xml` 파일에 다음과 같이 선언할 수 있습니다:

```xml
<web-app>
    ...
    <listener>
        <listener-class>
            org.springframework.web.context.request.RequestContextListener
        </listener-class>
    </listener>
    ...
</web-app>
```

또는 `RequestContextFilter`를 사용하는 것을 고려할 수 있습니다. 필터 매핑은 주변 웹 애플리케이션 구성에 따라 달라지므로, 적절하게 변경해야 합니다. 웹 애플리케이션의 필터 부분 예시는 다음과 같습니다:

```xml
<web-app>
    ...
    <filter>
        <filter-name>requestContextFilter</filter-name>
        <filter-class>org.springframework.web.filter.RequestContextFilter</filter-class>
    </filter>
    <filter-mapping>
        <filter-name>requestContextFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
    ...
</web-app>
```

`DispatcherServlet`, `RequestContextListener`, `RequestContextFilter`는 모두 동일한 작업을 수행합니다. 즉, HTTP 요청 객체를 해당 요청을 처리하는 스레드(Thread)에 바인딩합니다. 이를 통해 요청 및 세션 스코프의 Bean이 호출 체인의 더 아래에서 사용 가능해집니다.

</details>

초기 설정 방법은 특정 Servlet 환경에 따라 다릅니다.

### **Request Scope**

다음은 Bean 정의를 요청 스코프로 설정한 XML 구성 예시입니다:

```xml
<bean id="loginAction" class="com.something.LoginAction" scope="request"/>
```

Spring 컨테이너는 각 HTTP 요청마다 `loginAction` Bean 정의를 사용하여 새로운 `LoginAction` Bean 인스턴스를 생성합니다. 즉, `loginAction` Bean은 HTTP 요청 수준에서 스코프됩니다.&#x20;

생성된 인스턴스의 내부 상태를 변경해도, 동일한 `loginAction` Bean 정의로부터 생성된 다른 인스턴스들은 이러한 상태 변경을 보지 못합니다. 이는 각 인스턴스가 개별 요청에 특화되어 있기 때문입니다. 요청 처리가 완료되면, 요청 스코프에 속한 Bean은 폐기됩니다.

애노테이션 기반 구성이나 Java 설정을 사용할 때는 `@RequestScope` 애노테이션을 사용하여 컴포넌트를 요청 스코프로 지정할 수 있습니다. 예시는 다음과 같습니다:

```java
@RequestScope
@Component
public class LoginAction {
    // ...
}
```

**Session Scope**

다음은 Bean 정의를 세션 스코프로 설정한 XML 구성 예시입니다:

```xml
<bean id="userPreferences" class="com.something.UserPreferences" scope="session"/>
```

Spring 컨테이너는 `userPreferences` Bean 정의를 사용하여 단일 HTTP 세션 동안 새로운 `UserPreferences` Bean 인스턴스를 생성합니다.&#x20;

다시 말해, `userPreferences` Bean은 HTTP 세션 수준에서 스코프됩니다. 요청 스코프와 마찬가지로, 생성된 인스턴스의 내부 상태를 변경해도 동일한 `userPreferences` Bean 정의로부터 생성된 다른 세션 인스턴스들은 이러한 상태 변경을 보지 못합니다. 이는 각 세션 인스턴스가 개별 세션에 특화되어 있기 때문입니다. HTTP 세션이 최종적으로 폐기되면, 해당 세션에 스코프된 Bean도 폐기됩니다.

애노테이션 기반 구성이나 Java 설정을 사용할 때는 `@SessionScope` 애노테이션을 사용하여 컴포넌트를 세션 스코프로 지정할 수 있습니다. 예시는 다음과 같습니다:

```java
@SessionScope
@Component
public class UserPreferences {
    // ...
}
```

### **Application Scope**

다음은 Bean 정의를 애플리케이션 스코프로 설정한 XML 구성 예시입니다:

```xml
<bean id="appPreferences" class="com.something.AppPreferences" scope="application"/>
```

Spring 컨테이너는 `appPreferences` Bean 정의를 사용하여 전체 웹 애플리케이션 동안 하나의 `AppPreferences` Bean 인스턴스를 생성합니다.&#x20;

즉, `appPreferences` Bean은 ServletContext 수준에서 스코프되며, 일반 ServletContext 속성으로 저장됩니다. 이는 Spring의 싱글톤 Bean과 유사하지만 두 가지 중요한 점에서 다릅니다

1. ServletContext당 하나의 싱글톤으로, Spring ApplicationContext당 하나의 싱글톤이 아닙니다 (하나의 웹 애플리케이션에 여러 ApplicationContext가 있을 수 있음).
2. 실제로 ServletContext 속성으로 노출되어 보이므로, 외부에서 접근할 수 있습니다.

애노테이션 기반 구성이나 Java 설정을 사용할 때는 `@ApplicationScope` 애노테이션을 사용하여 컴포넌트를 애플리케이션 스코프로 지정할 수 있습니다. 예시는 다음과 같습니다:

```java
@ApplicationScope
@Component
public class AppPreferences {
    // ...
}
```

### **WebSocket Scope**

웹소켓 스코프는 WebSocket 세션의 생명주기와 연관되며, STOMP over WebSocket 애플리케이션에 적용됩니다.

### Scoped Beans as Dependencies

Spring IoC 컨테이너는 객체(Bean)의 인스턴스화뿐만 아니라 협력자(또는 의존성)의 와이어링도 관리합니다.&#x20;

예를 들어, HTTP 요청 스코프 Bean을 더 긴 생명주기(scope)를 가진 다른 Bean에 주입하려는 경우, 스코프된 Bean을 대체할 AOP 프록시를 주입할 수 있습니다. 즉, 스코프된 객체와 동일한 공개 인터페이스를 노출하지만, 관련 스코프(예: HTTP 요청)에서 실제 타겟 객체를 검색하고 메서드 호출을 위임할 수 있는 프록시 객체를 주입해야 합니다.

또한, `<aop:scoped-proxy/>`를 사용하여 스코프가 싱글톤인 Bean과 스코프가 세션이나 요청인 Bean 사이에 프록시를 삽입할 수 있습니다. 스코프가 프로토타입인 Bean의 경우, 공유 프록시의 모든 메서드 호출은 새로운 타겟 인스턴스를 생성하고 해당 인스턴스로 메서드 호출을 위임하게 됩니다.

스코프된 프록시는 짧은 스코프의 Bean을 생명주기 안전하게 접근하는 유일한 방법은 아닙니다. 또한 `ObjectFactory<MyTargetBean>` 또는 `ObjectProvider<MyTargetBean>`을 사용하여 매번 필요할 때 현재 인스턴스를 동적으로 가져올 수도 있습니다. JSR-330의 `Provider<MyTargetBean>`도 동일한 목적을 위해 사용됩니다.

## **프록시 유형 선택**

Spring 컨테이너가 `<aop:scoped-proxy/>` 요소로 표시된 Bean에 대해 프록시를 생성할 때, 기본적으로 CGLIB 기반의 클래스 프록시가 생성됩니다. CGLIB 프록시는 비공개 메서드를 인터셉트하지 않습니다. 따라서 이러한 프록시에서 비공개 메서드를 호출하면 실제 스코프된 타겟 객체로 위임되지 않습니다.

대안으로, `<aop:scoped-proxy/>` 요소의 `proxy-target-class` 속성 값을 `false`로 설정하여 표준 JDK 인터페이스 기반 프록시를 생성하도록 Spring 컨테이너를 구성할 수 있습니다. JDK 인터페이스 기반 프록시는 추가 라이브러리가 필요 없다는 장점이 있지만, 스코프된 Bean 클래스가 적어도 하나의 인터페이스를 구현해야 하며, 스코프된 Bean이 주입되는 모든 협력자는 해당 인터페이스를 통해 Bean을 참조해야 합니다. 다음 예시는 인터페이스 기반 프록시를 보여줍니다:

```xml
<!-- DefaultUserPreferences는 UserPreferences 인터페이스를 구현 -->
<bean id="userPreferences" class="com.stuff.DefaultUserPreferences" scope="session">
    <aop:scoped-proxy proxy-target-class="false"/>
</bean>

<bean id="userManager" class="com.stuff.UserManager">
    <property name="userPreferences" ref="userPreferences"/>
</bean>
```

### Injecting Request/Session References Directly

팩토리 스코프의 대안으로, Spring WebApplicationContext는 `HttpServletRequest`, `HttpServletResponse`, `HttpSession`, `WebRequest` 및 (JSF가 있는 경우) `FacesContext`와 `ExternalContext`를 Spring 관리 Bean에 타입 기반 자동 주입을 통해 직접 주입할 수 있습니다.&#x20;

Spring은 일반 Bean과 마찬가지로 이러한 요청 및 세션 객체의 프록시를 주입하여, 싱글톤 Bean 및 직렬화 가능한 Bean에서도 안전하게 접근할 수 있도록 합니다.

#### Custom Scopes (사용자 정의 스코프)

Spring의 Bean 스코핑 메커니즘은 확장 가능합니다. 기본적으로 제공되는 스코프 외에 **사용자 정의 스코프**를 정의하거나, 기존 스코프를 재정의할 수도 있지만, 후자는 권장되지 않으며 기본 제공되는 싱글톤과 프로토타입 스코프는 재정의할 수 없습니다.

**Creating a Custom Scope (사용자 정의 스코프 생성)**

사용자 정의 스코프를 Spring 컨테이너에 통합하려면, `org.springframework.beans.factory.config.Scope` 인터페이스를 구현해야 합니다. Spring Framework 자체에서 제공하는 Scope 구현체와 Scope javadoc을 참고하여 필요한 메서드를 구현할 수 있습니다.

`Scope` 인터페이스는 스코프에서 객체를 가져오고, 스코프에서 제거하며, 파괴할 수 있는 네 가지 메서드를 제공합니다.

예를 들어, 세션 스코프 구현은 세션에 바인딩된 세션 스코프 Bean을 반환합니다. 다음은 객체를 반환하는 메서드의 예시입니다:

```java
@Override
public Object get(String name, ObjectFactory<?> objectFactory) {
    // 스코프에서 객체를 가져오거나, 없으면 새로 생성
}
```

세션 스코프 구현은 스코프에서 Bean을 제거하는 메서드를 제공합니다:

```java
@Override
public Object remove(String name) {
    // 스코프에서 객체를 제거
}
```

파괴 콜백을 등록하는 메서드 예시:

```java
@Override
public void registerDestructionCallback(String name, Runnable destructionCallback) {
    // 객체가 파괴될 때 호출할 콜백을 등록
}
```

스코프의 대화 식별자를 얻는 메서드 예시:

```java
@Override
public String getConversationId() {
    // 스코프의 대화 식별자 반환
}
```

**Using a Custom Scope (사용자 정의 스코프 사용)**

사용자 정의 `Scope` 구현체를 작성하고 테스트한 후, Spring 컨테이너에 등록해야 합니다. 다음은 스코프를 등록하는 주요 메서드입니다:

```java
beanFactory.registerScope("thread", new SimpleThreadScope());
```

위 예시는 `SimpleThreadScope`를 등록하는 예시이며, 사용자 정의 스코프 구현체도 동일한 방식으로 등록할 수 있습니다.

XML 설정을 통해 사용자 정의 스코프를 선언적으로 등록할 수도 있습니다. 다음은 `CustomScopeConfigurer`를 사용한 예시입니다:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
                           https://www.springframework.org/schema/beans/spring-beans.xsd
                           http://www.springframework.org/schema/aop
                           https://www.springframework.org/schema/aop/spring-aop.xsd">

    <bean class="org.springframework.beans.factory.config.CustomScopeConfigurer">
        <property name="scopes">
            <map>
                <entry key="thread">
                    <bean class="org.springframework.context.support.SimpleThreadScope"/>
                </entry>
            </map>
        </property>
    </bean>

    <bean id="thing2" class="x.y.Thing2" scope="thread">
        <property name="name" value="Rick"/>
        <aop:scoped-proxy/>
    </bean>

    <bean id="thing1" class="x.y.Thing1">
        <property name="thing2" ref="thing2"/>
    </bean>

</beans>
```

위 예시에서 `<aop:scoped-proxy/>`는 `thing2` Bean이 `thread` 스코프를 따르도록 프록시를 생성합니다. `thing1` Bean은 `thing2` Bean을 참조할 때 프록시를 통해 접근하게 됩니다.
