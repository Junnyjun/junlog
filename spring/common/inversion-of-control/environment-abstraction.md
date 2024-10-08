# Environment Abstraction

Spring의 **Environment 인터페이스**는 두 가지 주요 측면을 추상화하여 애플리케이션 환경을 관리합니다: 프로파일(profiles)과 프로퍼티(properties입니다.&#x20;

**프로파일**은 특정 조건에서만 활성화되는 Bean 정의의 논리적 그룹을 의미하며, **프로퍼티**는 여러 출처에서 가져올 수 있는 설정 값들을 말합니다. 이 두 가지를 활용하여 Spring 애플리케이션의 환경에 따라 동적으로 Bean을 정의하거나 프로퍼티를 구성할 수 있습니다.

#### **Bean Definition Profiles**

프로파일을 사용하면 특정 환경에 맞게 Bean을 다르게 정의할 수 있습니다. 예를 들어, 개발 환경에서는 인메모리 데이터베이스를 사용하고, 운영 환경에서는 JNDI를 통해 데이터 소스를 설정하는 식으로 환경에 따라 Bean을 다르게 구성할 수 있습니다.

**프로파일을 사용하는 이유**

* 개발 환경에서 인메모리 데이터베이스를 사용하고, 운영 환경에서 JNDI를 통해 데이터 소스를 사용해야 할 때.
* 성능 모니터링을 테스트 또는 운영 환경에서만 활성화할 때.
* 특정 고객에게 맞춘 Bean을 등록해야 할 때.

**프로파일을 이용한 Bean 정의**

다음은 각 환경에서 사용할 DataSource를 다르게 구성하는 예입니다:

**개발 환경**:

```java
@Configuration
@Profile("development")
public class StandaloneDataConfig {

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.HSQL)
            .addScript("classpath:com/bank/config/sql/schema.sql")
            .addScript("classpath:com/bank/config/sql/test-data.sql")
            .build();
    }
}
```

**운영 환경**:

```java
@Configuration
@Profile("production")
public class JndiDataConfig {

    @Bean(destroyMethod = "") 
    public DataSource dataSource() throws Exception {
        Context ctx = new InitialContext();
        return (DataSource) ctx.lookup("java:comp/env/jdbc/datasource");
    }
}
```

**@Profile** 애노테이션을 사용하여 해당 프로파일이 활성화된 경우에만 해당 Bean이 등록됩니다. **"production"** 프로파일이 활성화된 경우 `JndiDataConfig` 클래스가 등록되고, **"development"** 프로파일이 활성화된 경우 `StandaloneDataConfig` 클래스가 등록됩니다.

**프로파일 표현식**

**@Profile** 애노테이션은 간단한 프로파일 이름뿐만 아니라 복잡한 논리식을 지원합니다. 예를 들어, "production & us-east"와 같은 방식으로 논리 AND를 사용할 수 있으며, 논리 OR은 "production | us-east"로 표현할 수 있습니다. 또한, **!** 연산자를 사용하여 특정 프로파일이 활성화되지 않았을 때만 Bean을 등록하는 조건을 만들 수 있습니다.

#### **@Profile을 이용한 메타 애노테이션**

**@Profile** 애노테이션은 메타 애노테이션으로도 사용할 수 있습니다. 예를 들어, @Profile("production")을 대신하여 **@Production**이라는 커스텀 애노테이션을 정의할 수 있습니다:

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Profile("production")
public @interface Production {
}
```

이제 **@Profile("production")** 대신 **@Production**을 사용할 수 있습니다.

#### **XML에서의 프로파일 정의**

XML에서는 요소의 **profile** 속성을 사용하여 프로파일을 정의할 수 있습니다. 아래는 XML에서 각 프로파일에 따라 데이터 소스를 다르게 설정하는 예시입니다:

**개발 환경**:

```xml
<beans profile="development"
       xmlns="http://www.springframework.org/schema/beans"
       xmlns:jdbc="http://www.springframework.org/schema/jdbc">
    <jdbc:embedded-database id="dataSource">
        <jdbc:script location="classpath:com/bank/config/sql/schema.sql"/>
        <jdbc:script location="classpath:com/bank/config/sql/test-data.sql"/>
    </jdbc:embedded-database>
</beans>
```

**운영 환경**:

```xml
<beans profile="production"
       xmlns="http://www.springframework.org/schema/beans"
       xmlns:jee="http://www.springframework.org/schema/jee">
    <jee:jndi-lookup id="dataSource" jndi-name="java:comp/env/jdbc/datasource"/>
</beans>
```

이렇게 XML에서도 **profile** 속성을 사용하여 환경에 맞는 Bean을 정의할 수 있습니다.

#### **프로파일 활성화**

프로파일을 활성화하려면 프로그램 코드에서 **Environment API**를 사용하거나 시스템 속성을 통해 지정할 수 있습니다. 다음은 프로그램 코드에서 프로파일을 활성화하는 예시입니다:

```java
AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
ctx.getEnvironment().setActiveProfiles("development");
ctx.register(SomeConfig.class, StandaloneDataConfig.class, JndiDataConfig.class);
ctx.refresh();
```

또는 시스템 속성을 통해 프로파일을 활성화할 수 있습니다:

```bash
-Dspring.profiles.active="development"
```

#### **디폴트 프로파일**

특정 프로파일이 활성화되지 않은 경우 사용할 기본 프로파일을 설정할 수 있습니다. "default"라는 이름의 기본 프로파일이 있으며, 이 프로파일을 사용하여 기본 Bean 구성을 제공할 수 있습니다. 기본 프로파일은 활성화된 다른 프로파일이 없을 때만 적용됩니다.

```java
@Configuration
@Profile("default")
public class DefaultDataConfig {

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.HSQL)
            .addScript("classpath:com/bank/config/sql/schema.sql")
            .build();
    }
}
```

#### **PropertySource 추상화**

Spring의 **Environment**는 다양한 **PropertySource** 객체를 관리하며, 이를 통해 여러 출처에서 설정 값을 가져올 수 있습니다. 기본적으로 Spring의 **StandardEnvironment**는 JVM 시스템 속성과 시스템 환경 변수를 관리합니다.

```java
ApplicationContext ctx = new GenericApplicationContext();
Environment env = ctx.getEnvironment();
boolean containsMyProperty = env.containsProperty("my-property");
System.out.println("Does my environment contain the 'my-property' property? " + containsMyProperty);
```

#### **@PropertySource 사용**

**@PropertySource** 애노테이션을 사용하면 프로퍼티 파일을 간편하게 로드할 수 있습니다. 예를 들어, **app.properties** 파일에서 **testbean.name=myTestBean**이라는 키-값 쌍을 정의한 경우 다음과 같이 사용할 수 있습니다:

```java
@Configuration
@PropertySource("classpath:/com/myco/app.properties")
public class AppConfig {

    @Autowired
    Environment env;

    @Bean
    public TestBean testBean() {
        TestBean testBean = new TestBean();
        testBean.setName(env.getProperty("testbean.name"));
        return testBean;
    }
}
```

#### **PropertySource의 순서와 우선순위**

Spring은 다양한 **PropertySource**를 계층적으로 관리하며, 기본적으로 시스템 속성이 환경 변수보다 높은 우선순위를 가집니다. 이를 변경하려면 직접 **PropertySource**를 정의하여 추가할 수 있습니다:

```java
ConfigurableApplicationContext ctx = new GenericApplicationContext();
MutablePropertySources sources = ctx.getEnvironment().getPropertySources();
sources.addFirst(new MyPropertySource());
```

#### **플레이스홀더 해석**

플레이스홀더는 **${property}** 형식으로 사용할 수 있으며, 이는 JVM 시스템 속성이나 환경 변수뿐만 아니라 Spring의 **Environment**를 통해 정의된 모든 **PropertySource**에서 해석됩니다.

```xml
<beans>
    <import resource="com/bank/service/${customer}-config.xml"/>
</beans>
```

위 예시에서는 **customer** 프로퍼티가 존재하는 경우 해당 값으로 대체되며, 존재하지 않으면 예외가 발생합니다.
