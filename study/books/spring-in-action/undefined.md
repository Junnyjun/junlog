# 고급 와이어링

## &#x20;고급와이어링

### 환경과 프로파일

\- 같은 dataSource를 지정하는 빈이어도, dev환경, cbr환경, 상용환경이 다를 수 있음

\- 스프링은 이러한 환경에 맞는 프로파일을 지정 가능함

&#x20;

환경과 프로파일에 따른 빈설정

@Profile - 빈이 속한 프로파일을 지정하는 어노테이션

```
@Configuration
@Profile("dev")
public class DevConfig {
  @Bean
  public DataSource dataSource() {
    return new ....
  }
}
=====> dev 프로파일에서 사용하는 dataSource용 컨피그 파일
===> dev프로파일이 활성화 되지 않을경우, 해당 @Bean 메소드는은 무시된다

@Configuration
@Profile("prod")
public class ProdConfig {
  @Bean
  public DataSource dataSource() {
    return new ....
  }
}
=====> prod 프로파일에서 사용하는 dataSource용 컨피그 파일
```

## 조건부 빈 설정

@Conditionl - 조건부 빈을 생성하는 어노테이션

\- 소정의 조건을 통해, 그것이 true이면 빈을 생성한다

```
@Bean
@Conditional(BobExistsCondition.class)
public Bob bob() {
  return new bob();
}
===> BobExistsCondition 해당 클래스가 구현하는 Condition 인터페이스가, bob 빈의 생성을 결정

public class BobExistsCondition {
  public boolean matchs(ConditionalConetxt context, AnnotatedTypeMetadata metadata) {
    Environment env = context.getEnvironment
    return env.containsProperty("bob")
  }
}
===> bob 프로퍼티가 없다면, 위의 bob 빈이 생성되지 않음
==> ConditionalConetxt : 빈 정의를 확인, 빈 존재를 확인 후 빈 프로퍼티 발굴, Environment 변수값 확인
==> AnnotatedTypeMetadata : @Bean 메소드의 어노테이션 검사 기회를 제공(isAnnotated)
```

### 오토와이어링의 모호성

\- 일치하는 빈이 여럿일때의 모호함 때문에, 프로퍼티, 생성자 인수, 메소드 파라미터의 오토와이어링은 어려움

\==> 따라서 이러한 모호성을 해결할 방법이 존재 ex) Caffelatte, Capuccino, Americano 클래스의 addShot 메소드

기본 빈 지정

```
@Component
@Primary
public class Americano implements Coffee { ... }

혹은
자바 설정에서

@Bean
@Primary
public Coffee americano() {
  return new Americano();
}

==> 카페에서 제일 잘팔리는(잘쓰이는) 아메리카노를, 기본 빈으로 지정하여, Coffe 모호성을 없앨 수 있음

@Component
@Primary
public class Caffelatte implemnets Coffee { .. }
===> 하지만 이렇게, Coffee에 관한 기본 빈이 2개(Americano, Caffelatte)라면, 새로운 모호성이 생기며, 사실상 기본 후보가 없음
```

&#x20;

오토와이어링 빈의 자격

@Qualifier : 수식자를 사용하는 방법 -> 주입 지점에서 @Autowired와 사용

\=> @Primary의 한계점인 하나의 명백한 옵션 선택이 불가능 한 것을 보안하는 것

```
@Autowired
@Qualifier("americano")
public void addShot(Coffee coffee) {
  this.shot++;
}

===> 해당 "americano" 수식자를 통해 addShot은 americano에 적용 받음
```

리팩토링을 위해, @Qualifier의 수식자에 다른 값도 줄 수있음

```
@Component
@Qualifier("dutch")
public void Coldbrew implements Coffe { ... }

==> 이런식으로 Coldebrew를 "dutch"로 수식자를 정할경우, Coldbrew를 나중에, DutchCoffe로 바꾸든      ColdeBrewCoffe로 바꾸든, 상관이 없다 beanId가 dutch이기 때문에
```

필요한 수식자가 많은 경우 ====> 두가지 이상의 Qualifier는 불가능

```
@Component
@Qualifier("espresso")
@Qualifier("water")
public void Americano implements Coffee { ... }

@Component
@Qualifier("espresso")
@Qualifier("milk")
public void Caffelatee implements Coffee { ... }
```

@Qualifier가 많이 필요할 경우, 명시적으로 정의된 맞춤형 어노테이션을 만들 수도 있다

```
@Target(~~)
@Retention(~~~)
@Qualifier
public @interface Esspresso {}

@Target(~~)
@Retention(~~~)
@Qualifier
public @interface Water {}

==> 이렇게 명시적으로 어노테이션을 만든 후

@Component
@Esspresso
@Water
public void Americano implements Coffee { ... }
```

&#x20;

## 빈 범위

\- 기본적으로 스프링의 빈은 모두 싱글톤

\- 하지만, 명시적으로 선언하여 이변성(mubtable) 클래스를 빈으로 정의할 수 있음

&#x20;

싱글톤 : 전체 애플리케이션을 위해 생성되는 빈 중 하나

프로토타입 : 빈이 주입될때마다 생성 or 스프링 애플리케이션 컨텍스트에서 얻는 빈 인스턴스 하나

세션 : 웹에서 각각의 세션용 빈 인스턴스 하나

요청 : 웹에서 각각의 요청용 빈 인스턴스 하나

&#x20;

@Scope 어노테이션 사용

```
@Bean
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public Americano americano() {
  return new Americano();
}
==> 프로토 타입

@Bean
@Scope(value = WebApllicationContext.SCOPE_SESSION
, proxyMode=ScopedProxyMode.INTERFACES)
public SECafe cafe() { ... }

==> 세션... 각 세션마다 생성되지만, 각 세션에 관해서는 싱글톤
==> 범위프록시(ScopedProxyMode.INTERFACES)를 지정하고, 이를 세션/요청 범위 빈에 위임
```

&#x20;

## 런타임 주입

\- 빈 아이어링을 할 때, 하드코딩된 값을 주입하는것을 방지하고, 값이 런타임 시에 결정되길 원할 때 이용

방식 1. 프로퍼티 플레이스 홀더, 2. 스프링 표현 언어

&#x20;

외부 값 주입

```
@Configuration
@PropertySource("classpath:/com/cafe/app.properties") 
public class AppConfig {
  @Autowired
  Enviroment env;
  
  @Bean
  public EmptyCafe cafe() {
    return new Cafe(env.getProperty("cafe.name"));
  }
}

==> app.properties 참조하여, getProperty()로 호출
```

프로퍼티 플레이스홀더

\- 플레이스홀더 : ${..}표현식을 사용하여 properties파일 혹은 yaml파일에서 가져올 수 있음

```
public EmptyCafe(
  @Value("${cafe.name}") String name {
    this.name = name;
}

===> 이처럼 @Value 어노테이션을 이용하여, 플레이스홀더값(${...})을 사용한다
```

&#x20;

스프링 표현식 와이어링

\- 런타임에 평가하는 표현식을 도입(SpEL)

\- #{...} 표현식 사용

```
public EmptyCafe(
  @Value("#{systemProperties['cafe.name']}") String name {
    this.name = name;
}
==> SpEL로 systemProperties 객체 사용

#{americano}
==> 빈ID로 다른 빈을 와이어링 가능

이 외
#{cafeSelector.selectCafe()} 메소드 참조
#{cafe[4].name} 컬렉션 사용
#{admin.email matchs '[a-zA-Z0-9._&+-]} 정규 표현식 사용
```
