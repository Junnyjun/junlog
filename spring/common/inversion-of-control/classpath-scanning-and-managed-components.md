# Classpath Scanning and Managed Components

Spring 프레임워크에서 **Classpath Scanning**과 **Managed Components**를 사용하면 XML 없이도 애플리케이션 컴포넌트를 자동으로 감지하고 컨테이너에 등록할 수 있습니다.&#x20;

이를 통해 개발자는 애노테이션을 사용하여 자동으로 Bean을 등록하거나 의존성을 주입할 수 있습니다. 이러한 방식은 코드의 간결성을 높이고 설정을 간소화하는 데 기여합니다.

## **Component 및 관련 애노테이션**

Spring은 여러 계층에서 사용되는 클래스들을 적절하게 식별할 수 있는 몇 가지 **Stereotype 애노테이션**을 제공합니다

* **@Component**: 모든 Spring 관리 컴포넌트에 사용할 수 있는 일반적인 스테레오타입 애노테이션입니다.
* **@Repository**: 영속성 계층에서 DAO(Data Access Object)를 나타내며, 예외 번역 등의 추가 기능이 제공됩니다.
* **@Service**: 비즈니스 로직을 처리하는 서비스 계층을 나타내는 스테레오타입입니다.
* **@Controller**: 프레젠테이션 계층에서 MVC 패턴의 컨트롤러 역할을 수행하는 클래스에 사용됩니다.

이러한 스테레오타입 애노테이션들은 **@Component**의 특수화로, 각 계층에 적합한 추가적인 의미나 도구에 의한 처리 가능성을 제공합니다.

### **Meta-Annotations 및 Composed Annotations**

Spring의 애노테이션들은 다른 애노테이션에 메타 애노테이션으로 사용될 수 있습니다.\
또한, 여러 메타 애노테이션을 결합하여 **Composed Annotation**을 만들 수 있습니다.&#x20;

예를 들어, **@RestController**는 **@Controller**와 **@ResponseBody**를 결합한 애노테이션입니다. Composed Annotation은 메타 애노테이션의 속성을 재정의하여 커스터마이즈할 수도 있습니다.

### **Classpath에서 Bean을 자동으로 감지하고 등록하기**

Spring은 클래스 경로를 스캔하여 특정 조건에 맞는 클래스들을 자동으로 감지하고, 해당 클래스를 **BeanDefinition**으로 등록할 수 있습니다. 이 방식은 XML로 Bean을 등록하는 대신, 컴포넌트 애노테이션을 사용해 자동으로 Bean을 감지합니다.

#### **서비스 클래스와 레포지토리 클래스**

```java
@Service
public class SimpleMovieLister {
    private MovieFinder movieFinder;

    public SimpleMovieLister(MovieFinder movieFinder) {
        this.movieFinder = movieFinder;
    }
}

@Repository
public class JpaMovieFinder implements MovieFinder {
    // 구현 생략
}
```

#### **Configuration 클래스에서 Component 스캔 사용**

```java
@Configuration
@ComponentScan(basePackages = "org.example")
public class AppConfig {
    // 설정 메서드 생략
}
```

또는 XML을 사용하여 같은 설정을 할 수 있습니다:

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="...">
    <context:component-scan base-package="org.example"/>
</beans>
```

이 구성에서는 **context:component-scan**이 **context:annotation-config** 기능을 자동으로 활성화하므로, 두 요소를 함께 사용할 필요가 없습니다.

### **스캔 커스터마이징: 필터 사용**

기본적으로 Spring은 **@Component**, **@Repository**, **@Service**, **@Controller**, **@Configuration** 애노테이션이 붙은 클래스만을 감지하여 Bean으로 등록합니다.&#x20;

그러나 필터를 사용하면 이 동작을 커스터마이징할 수 있습니다. **includeFilters** 또는 **excludeFilters** 속성을 사용하여 필터를 추가하거나 XML 설정에서 **context:include-filter** 또는 **context:exclude-filter** 요소를 사용할 수 있습니다.

예를 들어, **@Repository** 애노테이션이 있는 클래스를 제외하고 **Stub** 레포지토리만 포함하는 설정은 다음과 같습니다:

```java
@Configuration
@ComponentScan(basePackages = "org.example",
               includeFilters = @Filter(type = FilterType.REGEX, pattern = ".*Stub.*Repository"),
               excludeFilters = @Filter(Repository.class))
public class AppConfig {
    // 설정 생략
}
```

XML로는 다음과 같이 표현할 수 있습니다:

```xml
<context:component-scan base-package="org.example">
    <context:include-filter type="regex" expression=".*Stub.*Repository"/>
    <context:exclude-filter type="annotation" expression="org.springframework.stereotype.Repository"/>
</context:component-scan>
```

#### **Bean 메타데이터 정의**

Spring 컴포넌트에서 **@Bean** 애노테이션을 사용하여 추가적인 Bean 메타데이터를 정의할 수 있습니다. 예를 들어, **@Component** 클래스에서 메서드를 사용하여 Bean을 정의하는 방법은 다음과 같습니다:

```java
@Component
public class FactoryMethodComponent {

    @Bean
    @Qualifier("public")
    public TestBean publicInstance() {
        return new TestBean("publicInstance");
    }

    @Bean
    protected TestBean protectedInstance(@Qualifier("public") TestBean spouse) {
        return new TestBean("protectedInstance");
    }
}
```

### **Autowired 및 Lazy 의존성 주입**

**@Autowired** 필드와 메서드에 대한 지원이 있으며, **@Lazy** 애노테이션을 사용하여 지연 로드할 수도 있습니다. 지연 로드된 Bean은 필요할 때까지 생성되지 않고, 더 나아가 **ObjectProvider**와 같은 도구를 사용하여 더 복잡한 상호작용을 처리할 수 있습니다.

#### **이름 자동 감지**

클래스가 스캔될 때 **BeanNameGenerator** 전략에 의해 이름이 자동 생성됩니다.&#x20;

기본적으로 **AnnotationBeanNameGenerator**가 사용됩니다. 스테레오타입 애노테이션에서 **value** 속성에 이름을 지정하면 그 이름이 Bean 이름으로 사용됩니다.

예를 들어, 다음과 같은 클래스는 각각 **myMovieLister**와 **movieFinderImpl**라는 이름의 Bean으로 등록됩니다:

```java
@Service("myMovieLister")
public class SimpleMovieLister {
    // ...
}

@Repository
public class MovieFinderImpl implements MovieFinder {
    // ...
}
```

Bean 이름이 충돌할 수 있는 경우, **BeanNameGenerator**를 커스터마이즈하여 처리할 수 있습니다. **FullyQualifiedAnnotationBeanNameGenerator**와 같은 전략을 사용할 수도 있습니다.

#### **Autodetected 컴포넌트의 범위 지정**

기본적으로 자동 감지된 컴포넌트는 **singleton** 범위를 가집니다. 그러나 **@Scope** 애노테이션을 사용하여 다른 범위를 지정할 수 있습니다. 예를 들어, **prototype** 범위로 설정된 Bean은 매번 새로운 인스턴스를 반환합니다:

```java
@Scope("prototype")
@Repository
public class MovieFinderImpl implements MovieFinder {
    // ...
}
```

#### **Qualifier 메타데이터 제공**

자동 감지된 컴포넌트에서 **@Qualifier** 애노테이션을 사용하여 세밀한 주입 제어를 할 수 있습니다. 이는 특정 Bean이 여러 개 있을 때, 어떤 Bean을 주입할지 지정하는 데 유용합니다:

```java
@Component
@Qualifier("Action")
public class ActionMovieCatalog implements MovieCatalog {
    // ...
}
```

또한 커스텀 애노테이션을 통해 더 구체적인 제어도 가능합니다.
