# prepare Spring Framework 7

#### 1. 내장된 복원력 기능 <a href="#h3-0-1-uilt-in-esilience-eature" id="h3-0-1-uilt-in-esilience-eature"></a>

Spring Framework 7은 강력한 복원력 도구를 핵심에 직접 도입했습니다.

* **@Retryable:** 최대 시도 횟수, 지연, 지터, 백오프 등의 설정 가능한 옵션을 사용하여 실패한 메서드 호출을 재시도합니다. 또한 반응형 반환 유형을 지원합니다.
* **@ConcurrencyLimit:** 서비스와 리소스를 보호하기 위해 동시 메서드 호출을 제한합니다. 예를 들어, 단일 스레드에 대한 액세스를 제한합니다.
* **@EnableResilientMethods를** 사용하거나 특정 포스트 프로세서를 등록하여 두 애노테이션을 모두 활성화하세요 . 자세한 내용은 [여기를](https://docs.spring.io/spring-framework/docs/7.0.0-M7/javadoc-api/org/springframework/resilience/annotation/EnableResilientMethods.html) 참조하세요.

```
@Configuration
@EnableResilientMethods
public class ApplicationConfig {
}
```

**@Retryable** 및 **@ConcurrentLimit을** 사용한 서비스

```
@Configuration
@EnableResilientMethods
public class ApplicationConfig {
}
```

#### 2. Fluent JMS 클라이언트 API <a href="#h3-1-2-luent-lient" id="h3-1-2-luent-lient"></a>

Spring은 이제 **JdbcClient** 와 RestClient를 모델로 한 **JmsClient를 포함합니다. 개발자는 이제 유창한 빌더 스타일 API를 사용하여 메시지를 주고받을 수 있습니다.**&#x20;

**이 새로운 접근 방식은 기존 JMS 템플릿보다 더욱 우아하고 가독성이 뛰어난 대안입니다. JdbcClient** 가 **JdbcTemplate을** , **RestClient가 RestTemplate을** 대체하는 방식과 같습니다 .

#### 3. 강력한 API 버전 관리 <a href="#h3-2-3-obust-pi-ersioning" id="h3-2-3-obust-pi-ersioning"></a>

Spring Framework는 강력한 새로운 기능으로 API 버전 관리를 향상시킵니다.

* 미디어 유형을 통해 버전을 확인합니다.
* API 중단 알림 및 검증을 지원합니다.
* 고정된 버전 세트를 정의할 수 있습니다.

이러한 개선 사항은 Spring MVC와 Spring WebFlux 모두에서 적용됩니다.

#### 4. 통합 메시지 변환 <a href="#h3-3-4-nified-essage-onversion" id="h3-3-4-nified-essage-onversion"></a>

Spring은 새로운 _**HttpMessageConverters**_ 구성 클래스를 통해 메시지 변환을 간소화합니다. 이 통합된 접근 방식은 반응형 코덱에서 영감을 받아 HTTP 메시지의 직렬화 및 역직렬화 방식을 간소화합니다.

#### 5. 더 빠르고 스마트한 테스트 <a href="#h3-4-5-aster-and-marter-esting" id="h3-4-5-aster-and-marter-esting"></a>

Spring은 이제 **사용되지 않는 애플리케이션 컨텍스트를 일시 중지하여 테스트 성능을 최적화합니다.** 일시 중지된 컨텍스트는 프레임워크에서 중지되었다가 필요할 때 자동으로 다시 시작됩니다. 이를 통해 리소스 사용량을 줄이고 테스트 실행 속도를 크게 향상시킵니다.

#### 6. 현대 생태계 통합 <a href="#h3-5-6-odern-cosystem-ntegration" id="h3-5-6-odern-cosystem-ntegration"></a>

Spring Framework 7은 최신 플랫폼 및 표준에 맞춰져 있습니다.

* 코틀린 2.2
* 자카르타 EE 11 기준선
* GraalVM 24 지원

#### 7. Hibernate ORM 및 JPA 업그레이드 <a href="#h3-6-7-ibernate-and-pgrades" id="h3-6-7-ibernate-and-pgrades"></a>

**Spring은 Hibernate ORM 7.0** 및 **JPA 3.2** 와 통합되어 최신 지속성 표준과의 호환성을 제공합니다. 이전에는 EntityManager를 @PersistenceContext 애노테이션을 정의해야만 주입할 수 있었습니다. 하지만 이제 **EntityManagerFactory** 와 관련 공유 **EntityManager를 모두 @Inject 또는 @Autowired 애노테이션을** 사용하여 주입할 수 있으며 , 여러 개의 지속성 단위가 구성된 경우 특정 지속성 단위를 선택하는 한정자도 지원됩니다.

#### 8. HttpHeaders API 개편 <a href="#h3-7-8-verhauled-ttp-eaders" id="h3-7-8-verhauled-ttp-eaders"></a>

새로운 HttpHeaders API는 HTTP 헤더를 처리할 때 더 깔끔하고 일관된 개발자 경험을 제공합니다.

#### 9. Jackson 3.x 지원 <a href="#h3-8-9-upport-for-ackson-3-x" id="h3-8-9-upport-for-ackson-3-x"></a>

Spring Framework는 이제 **Jackson 3.x를** 지원 하고 더 이상 사용되지 않는 Jackson 기능에 대한 마이그레이션 지침을 제공하여 개발자가 원활하게 업그레이드할 수 있도록 돕습니다.

#### 10. JSpecify를 사용한 Null 안전성 <a href="#h3-9-10-ull-afety-using-pecify" id="h3-9-10-ull-afety-using-pecify"></a>

이전 애노테이션을 확실히 대체하는 null 안전성을 위한 **JSpecify를** 소개합니다

***

## 스프링 부트 관점

Spring Boot 4는 Spring 애플리케이션의 개발, 구성 및 배포 방식을 현대화하는 데 있어 중요한 도약입니다. 이 획기적인 변화는 더욱 모듈화되고 확장 가능하며 개발자 친화적인 프레임워크 버전의 시작을 의미합니다. Spring Boot 4의 주요 개선 사항과 이러한 개선 사항이 개발자에게 미치는 영향을 살펴보겠습니다.

#### 1. 모듈형 코드베이스 - 리팩토링된 아키텍처 <a href="#h3-10-1-odular-odebase-efactored-rchitecture" id="h3-10-1-odular-odebase-efactored-rchitecture"></a>

**내부 코드베이스를 더 작고 집중적인 모듈로 분할함으로써** 아키텍처에 큰 변화를 가져왔습니다 . 이전에는 Spring Boot가 크고 모놀리식 자동 구성 JAR 파일 형식에 의존했습니다. 버전 4에서는 자동 구성이 **모듈식 패키지로 리팩토링** 되어 프레임워크의 유지 관리 및 구성 가능성이 향상되었습니다.

각 모듈은 다음과 같은 전용 패키지로 시작합니다.

org.springframework . < 모듈 >​

모듈의 목적에 따라 다음이 포함될 수 있습니다.

* 공개 API
* 자동 구성 논리
* 액추에이터 관련 지원

#### &#x20;향상된 구성 속성 메타데이터 <a href="#h3-12-3-nhanced-onfiguration-roperties-etadata" id="h3-12-3-nhanced-onfiguration-roperties-etadata"></a>

새로운 어노테이션 **@ConfigurationPropertiesSource 를 도입합니다. 이를 통해 Spring Boot는 외부 모듈에 정의된 @ConfigurationProperties** 유형을 읽을 수 있게 되는데 , 이는 이전에는 불가능했던 기능입니다. 다음과 같은 이점이 있습니다. 1. 더욱 깔끔한 모듈형 디자인 2. 도구 지원 개선(IDE 자동 완성, 유효성 검사 등) 3. 공유 구성 라이브러리 관리 용이성 향상

#### SSL 상태 보고 개선 <a href="#h3-13-4-mprovements-in-ealth-eporting" id="h3-13-4-mprovements-in-ealth-eporting"></a>

Spring Boot의 SSL 상태 엔드포인트가 개선되어 더욱 정확하고 간소화된 보고가 제공됩니다.

**무엇이 바뀌었나요?**

* WILL\_EXPIRE\_SOON 상태가 제거되었습니다.
* 이제 인증서는 실제로 만료될 때까지 유효한 것으로 표시됩니다.
* **만료가** 임박한 인증서를 추적하는 데 도움이 되는 새로운 필드 _**expiringChains가**_ 추가되었습니다 .

이러한 변경으로 인해 팀은 잘못된 경보 없이 프로덕션 환경에서 SSL 인증서의 유효성을 더 쉽게 모니터링할 수 있습니다.

#### 여러 TaskDecorator Bean을 사용한 작업 스케줄링 <a href="#h3-14-5-ask-cheduling-with-ultiple-ask-ecorator-eans" id="h3-14-5-ask-cheduling-with-ultiple-ask-ecorator-eans"></a>

Spring Boot 4.0에서 가장 개발자 친화적인 업데이트 중 하나는 _**여러**_ **TaskDecorator** 빈을 지원한다는 것입니다. 4.0 이전 버전에서는 Spring Boot가 데코레이터를 하나만 지원했기 때문에 추적 및 로깅과 같은 여러 빈을 적용해야 할 때 수동으로 체이닝해야 했습니다.

**Spring Boot 4.0 이전:**

* Spring Boot에서는 하나만 허용했습니다.`TaskDecorator`
* 여러 데코레이터(예: 추적 및 로깅)의 경우 사용자 정의 데코레이터에서 수동으로 체인으로 연결해야 합니다.

```
@Bean
public TaskDecorator customTaskDecorator() {
    return runnable -> {
        Runnable decoratedWithTracing = tracingDecorator().decorate(runnable);
        return loggingDecorator().decorate(decoratedWithTracing);
    };
}

public TaskDecorator tracingDecorator() {
    return runnable -> () -> {
        System.out.println("Tracing Start");
        runnable.run();
        System.out.println("Tracing End");
    };
}

public TaskDecorator loggingDecorator() {
    return runnable -> () -> {
        System.out.println("Logging Start");
        runnable.run();
        System.out.println("Logging End");
    };
}
```

위의 코드 조각에서,

1. 체이닝은 수동으로 해야 해요
2. Spring 주석을 사용하여 여러 데코레이터를 주입하고 주문할 수 없습니다.

**Spring Boot 4.0에서:**

Spring Boot는 **@Order** 주석을 사용하여 지정된 순서대로 사용 가능한 모든 데코레이터를 연결하는 **CompositeTaskDecorator를** 자동으로 생성합니다

```
@Bean
@Order(1)
public TaskDecorator tracingDecorator() {
    return runnable -> () -> {
        System.out.println("Tracing Start");
        runnable.run();
        System.out.println("Tracing End");
    };
}

@Bean
@Order(2)
public TaskDecorator loggingDecorator() {
    return runnable -> () -> {
        System.out.println("Logging Start");
        runnable.run();
        System.out.println("Logging End");
    };
}
```

작업이 실행되면 Spring은 다음 순서대로 데코레이터를 적용합니다.

1. 추적 데코레이터
2. 로깅데코레이터
3. 그 다음 실제 작업

더 이상 데코레이터를 직접 구성할 필요가 없습니다. Spring Boot가 대신 처리해 줍니다.

#### 6. JdbcClient를 통한 JMS 지원 <a href="#h3-15-6-upport-via-dbc-lient" id="h3-15-6-upport-via-dbc-lient"></a>

Spring Boot 4.0은 Spring Framework 7에 도입된 JmsClient를 자동으로 구성합니다. 이는 JdbcClient 및 RestClient의 익숙한 패턴과 일치하며, JMS 메시징 작업을 위한 유연하고 빌더 패턴 스타일의 최신 API를 제공합니다. **JmsTemplate** 및 **JmsMessagingTemplate** 과도 여전히 공존합니다 .

이를 통해 JMS는 접근성이 뛰어나고 사용하기가 더 깔끔해집니다. 특히 메시징 시스템에 의존하는 마이크로서비스의 경우 더욱 그렇습니다.<br>
