# Test Without @Autowire

#### `@TestConstructor`의 동작 원리

`@TestConstructor`는 Spring Boot에서 테스트 클래스의 생성자를 통해 의존성을 주입할 수 있도록 제공하는 애노테이션입니다.&#x20;

기본적으로 Spring Boot는 테스트 클래스에서 생성자 주입을 활성화하지 않습니다. 하지만 `@TestConstructor`를 사용하면 생성자 주입을 활성화하고, 이를 통해 의존성을 테스트 클래스에 전달할 수 있습니다.

**1. `AutowireMode`의 역할**

* `@TestConstructor`는 내부적으로 `AutowireMode`를 통해 의존성 주입 방식을 결정합니다.
* **`AutowireMode.ALL`**: 생성자의 모든 매개변수를 자동으로 주입합니다.
  * 테스트 컨텍스트에서 테스트 클래스의 생성자를 스캔하고, 필요한 의존성을 ApplicationContext에서 가져옵니다.
  * 스프링 컨테이너에서 관리되는 모든 Bean이 주입 가능합니다.

***

####

아래는 동작 과정을 코드로 설명한 예시입니다:

```java
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class ExampleTest {

    private final MyService myService;
    private final MyRepository myRepository;

    public ExampleTest(MyService myService, MyRepository myRepository) {
        this.myService = myService;
        this.myRepository = myRepository;
    }

    @Test
    void testService() {
        assertNotNull(myService);
        assertNotNull(myRepository);
        // MyService와 MyRepository는 스프링 컨테이너에서 주입됨
    }
}
```

**동작 흐름**

1. `TestContextManager`가 `ExampleTest`를 생성자 기반으로 초기화.
2. `SpringBootTestContextBootstrapper`가 `MyService`와 `MyRepository`를 스프링 컨텍스트에서 검색.
3. 생성자 호출 시 각 의존성을 주입한 객체를 생성.
4. 테스트 메서드(`testService`) 실행.

***

#### 왜 `@TestConstructor`가 유용한가?

1. **명시적 의존성**
   * 생성자 주입을 통해 의존성을 명시적으로 나타냄으로써 테스트 가독성과 유지보수성을 향상시킵니다.
2. **Mock 객체와의 통합 용이**
   * Spring Context 대신 Mock 객체를 주입하여 유닛 테스트를 수행하기에도 적합합니다.
3. **스프링 테스트의 유연성**
   * 기존의 `@Autowired`와 병행 사용 가능하며, 더 명확한 의존성 관리가 가능합니다.

***

#### TIP&#x20;

1. **Spring Boot 2.4 이상**
   * `@TestConstructor`는 Spring Boot 2.4 이상에서 사용할 수 있습니다.
2. **다른 의존성 주입 방식과의 충돌**
   * `@Autowired`를 통해 필드 주입이나 메서드 주입을 사용하는 경우와 혼용하면 혼란을 초래할 수 있으므로, 하나의 주입 방식을 일관되게 사용하는 것이 좋습니다.
3. **@TestConstructor 없이도 동작 가능**
   *   `application.properties`에 아래 설정을 추가하면 기본적으로 생성자 주입을 활성화할 수 있습니다:

       ```
       spring.test.constructor-autowire-mode=all
       ```
