# Container Context

## Spring IoC 컨테이너 소개

Spring IoC(Inversion of Control) 컨테이너는 애플리케이션의 객체(bean)를 생성하고 관리하는 역할을 합니다. `ApplicationContext` 인터페이스는 Spring의 IoC 컨테이너를 나타내며, bean의 인스턴스화, 설정, 조립을 담당합니다.&#x20;

컨테이너는 **구성 메타데이터**를 읽어 어떤 컴포넌트를 인스턴스화하고 설정하며, 어떻게 조립할지에 대한 지시를 받습니다. 이 메타데이터는 애노테이션이 달린 컴포넌트 클래스, @Configuration 애노테이션이 붙은 설정 클래스, XML 파일, 또는 Groovy 스크립트 등으로 정의할 수 있습니다.

#### 주요 ApplicationContext 구현체

스프링의 `ApplicationContext` 인터페이스에는 여러 가지 구현체가 있습니다. 그 중에서도 자주 사용되는 것은 `AnnotationConfigApplicationContext`와 `ClassPathXmlApplicationContext`입니다.

* **AnnotationConfigApplicationContext**: 자바 기반 설정을 사용하여 애플리케이션 컨텍스트를 설정할 때 사용.
* **ClassPathXmlApplicationContext**: XML 파일 기반 설정을 사용하여 애플리케이션 컨텍스트를 설정할 때 사용.

#### Spring Boot에서 ApplicationContext 생성

Spring Boot에서는 별도의 설정 없이 기본적인 설정 규칙에 따라 애플리케이션 컨텍스트가 자동으로 생성됩니다. 별도의 사용자 코드로 컨테이너를 생성할 필요가 없으며, Spring Boot의 자동 설정이 이를 처리해줍니다.

#### 구성 메타데이터

구성 메타데이터는 Spring IoC 컨테이너에 애플리케이션의 컴포넌트를 어떻게 인스턴스화하고 설정하며 조립할지를 알려줍니다. 구성 메타데이터는 자바 기반, 애노테이션 기반, 또는 XML 기반으로 작성할 수 있으며, 이 메타데이터를 기반으로 컨테이너가 필요한 bean을 관리합니다.

**자바 기반 설정**

자바 기반 설정에서는 `@Configuration` 클래스와 `@Bean` 애노테이션을 사용하여 bean을 정의합니다. 이 설정은 외부의 자바 클래스를 사용하여 애플리케이션의 객체를 정의하고 관리합니다.

```java
@Configuration
public class AppConfig {

    @Bean
    public MyService myService() {
        return new MyServiceImpl();
    }
}
```

**XML 기반 설정**

XML 기반 설정에서는 `<bean>` 태그를 사용하여 bean을 정의합니다. XML 파일에 각 bean의 id와 클래스 정보를 정의하고, 의존 관계를 설정할 수 있습니다.

```xml
<beans xmlns="http://www.springframework.org/schema/beans">
    <bean id="myService" class="com.example.MyServiceImpl">
        <property name="dependency" ref="someDependency"/>
    </bean>

    <bean id="someDependency" class="com.example.SomeDependency"/>
</beans>
```

#### Groovy 기반 설정

Groovy DSL을 사용해도 bean을 정의할 수 있습니다. 이 방식은 Grails 프레임워크에서 흔히 사용되며, XML과 유사한 방식으로 외부 설정을 정의할 수 있습니다.

```groovy
beans {
    myService(MyServiceImpl) {
        dependency = someDependency
    }
    someDependency(SomeDependency)
}
```

#### ApplicationContext를 사용한 Bean 접근

`ApplicationContext`는 다양한 bean을 등록하고, 필요에 따라 이를 검색할 수 있는 메커니즘을 제공합니다. 예를 들어, XML 기반 설정에서 `ClassPathXmlApplicationContext`를 사용해 설정 파일을 로드하고, 등록된 bean을 검색할 수 있습니다.

```java
ApplicationContext context = new ClassPathXmlApplicationContext("services.xml", "daos.xml");
PetStoreService service = context.getBean("petStore", PetStoreService.class);
List<String> userList = service.getUsernameList();
```

이 방식으로 Spring IoC 컨테이너가 관리하는 bean을 가져와 사용할 수 있습니다. Groovy 기반 설정에서도 유사한 방식으로 사용할 수 있으며, `GenericApplicationContext`를 통해 여러 형식의 설정 파일을 조합해 사용할 수 있습니다.
