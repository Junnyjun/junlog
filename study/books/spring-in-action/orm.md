# ORM

애플리케이션이 복잡해짐에 따라, 퍼시스턴스와 관련된 요구사항도 복잡해짐 ==> 더 복잡하고 정교한 기능이 필요

\* 지연 로딩(lazy loading) : 관련성 있는 객체 전체를 한번에 가져오고 싶지 않을 때, 필요로 하는 실제 데이터만 불러오는 기능. ex) 객체 안의 내부객체는 제외하고 불러오기

\* 조기 인출(eager fetching) : 지연 로딩의 반대 개념. 한번의 쿼리로 전체 객체를 가져오는 기능 // ex) 불러오기 한번으로, 객체와 내부객체의 정보를 모두 가져옴

\* 캐스케이딩(cascading) : 때때로 데이터베이스 테이블을 변경했을 때, 다른 테이블의 값도 변경해야 하는 기능 // ex) 객체A를 삭제할 때, 연관된 B객체도 같이 삭제

\=> 이러한 서비스를 제공하는 프레임 워크를 '객체 관계 매핑(ORM, Object-Relational Mapping, 이후 ORM)'이라고 함.&#x20;

\=> 애플리케이션 퍼시스턴스 계층에 ORM이라고 함. 종류로는 하이버네이트, iBATIS, myBATIS, JPA등이 포함된다.

\* 부가적인 기능 : 선언적 트랜잭션에 대한 통합 지원, 투명한 예외 처리, 스레드 안정성을 갖춘 경량의 템플릿, DAO 지원, 자원 관리

***

#### 스프링과 하이버네이트 통합

하이버네이트 : 오픈소스 퍼시스턴스 프레임워크, 제대로 된 ORM이 갖춰야할 기능을 모두 제공(캐시, 지연 로딩, 조기 인출, 분산 캐시 등)

***

**하이버네이트 세션 팩토리 선언**

org..hibernate.Session : 가장 중심인 인터페이스,, 객체를 저장, 업데이트, 삭제, 로드하는 기본적인 액세스 기능을 제공

스프링에서 하이버네이트 SessionFactory를 얻는 방법은, 세션팩토리 빈을 통함 -> org.springframework.orm.hibernate4.LocalSessionFactoryBean (하이버네이트3은 생략)

하이버네이트4를 기준으로, 3의 AnnotationSesstionFactoryBean의 매시업과 같음.

```
@Bean
public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
  LocalSessionFactoryBean sfb = new LocalSessionFactoryBean();
  sfb.setDataSource(dataSource); // 데이터베이스 커넥션 및 종류 확인
  sfb.setPackagesToScan(new String[] { "com.bobfull.cafe.domain" }); // 퍼시스턴스 애너테이션이 적용된 도메인 클래스(@Entity, @MappedSuperClass 포함)
  Properties props = new Properties();
  props.setProperty("dialect", "org.hiebernate.dialect.H2Dialect"); 
  sfb.setHibernateProperties(props); // 데이터베이스 커넥션 및 종류 확인
  return sfb;
}


```

***

**스프링으로부터 해방된 하이버네이트 구성**

하이버네이스 상황세션을 이용하여, HibernateTemplate이 필요 없이, SessionFactory를 직접 저장소에 와이어링 할 수 있다.

```
@Repository
public class HibrernateCoffeeRepository implements CoffeeRepository{

    private SessionFactory sessionFactory 
    
    @Inject
    public HibernateCoffeeRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory; //세션팩토리 주입
    }

    private Session currentSession() {
        return sessionFactory.getCurrentSession(); // 현재 세션 추출
    }
    
    public long count() {
        return findAll().size();
    }
    public Coffee save(Coffee coffee) {
        Serializable id = currentSession().save(Coffee); // 현재 세션 사용
        return new Coffee((Long) id,
                Coffee.getName(),
                Coffee.getWater(),
                Coffee.getShot());
    }

    public Coffee findOne(long id) {
        return (Coffee) currentSession().get(Coffee.class, id);
    }

    public Coffee findByName(String name) {
        return (Coffee) currentSession()
                .createCriteria(Coffee.class)
                .add(Restrictions.eq("name", name))
                .list().get(0);
    }

    public List<Coffee> findAll() {
        return (List<Coffee>) currentSession()
                .createCriteria(Coffee.class).list();
  } 
}
```

\=> sessionFactory 프로퍼티에서, SessionFactory를 자동 주입한 후, currentSession() 메소드에서 이 SessionFactory를 이용해서 현재 트랜잭션의 세션을 얻음

\=> @Repository 애너테이션을 적용하여, 컴포넌트 스캐닝에 스캔됨(HibernateCoffeeRepository 빈이 필요 없음), 또한 명시적 설정을 줄이는데에 도움을 준다

\* 하이버네이트 템플릿이 아닌, 하이버네이트 저장소를 사용할  경우의 예외 변환은 아래와 같이 이용 가능하다

```
// 템플릿이 없는 하이버네이트 저장소에 예외 변환 추가
@Bean
public BeanPostProcessor perstistenceTeanslation() {
  return new PersistenceExceptionTeanslationPostProcessor(); //빈의 후 처리기, @Repository에 추가하여, 플랫폼에 특화된 모든 예외를 잡은 후 비검사형 예외로 던짐 
}
```

&#x20;

***

**스프링과 자바  퍼시스턴스 API**

JPA(java persitence API)는 POJO 기반의 퍼시스턴스  메커니즘,, 스프링 2.0부터 사용 가능하다.

***

엔티티 관리자 팩토리 설정

JPA로 만든 애플리케이션은, EntityManagerFactory의 구현객체를 이용해서, EntityManager의 인스턴스를 획득해야한다.

엔티티 관리자의 종류

* 애플리케이션 관리형\_Application-managed : 애플리케이션이 엔티티 관리자 팩토리에서 직접 요청하여, 생성되는 유형. 직접 open부터 close까지 신경써야하므로 , 자바 EE컨테이너가 없이 실행되는, 독립형 애플리케이션에 적합하다.
* 컨테이너 관리형\_container-managed : 자바 EE 컨테이너에 의해 생성되고 관리, 애플리케이션은 직접 엔티티관리자와 상호작용 하지 않음. 종속객체 주입 혹은 JNDI를 통해 획득. 이 유형은 단순히 persistnece.xml로 지정한 것 이상의 자바 EE컨테이너를 제공하고자 할 때 사용

\=> 스프링에서는, 개발자에게는 큰 의미가 없음.. 어떤 유형을 사용해도, 스프링이 관리를 해줌... JpaTemplate이 세부사항을 감춰줌

***

**애플리케이션 관리형 JPA 구성**

\- 대부분 persitence.xml 설정파일로 가져옴(classpath:META-INF 디렉토리) -> 하나 이상의 퍼시스턴스 유닛(하나의 데이터소스를 공유하는 퍼시스턴스 클래스의 집합) 선언이 목표

```
 <persistence xmlns="http://java.sun.com/xml/ns/persistence"version="1.0">
    <persistence-unit name="cafePU">
      <class>com.bobfull.cafe.domain.Coffee</class>
      <class>com.bob.full.cafe.domain.CafeUser</class>
      <properties>
        <property name="toplink.jdbc.driver"
            value="org.hsqldb.jdbcDriver" />
        <property name="toplink.jdbc.url" value=
            "jdbc:hsqldb:hsql://localhost/cafe/coffee" />
        <property name="toplink.jdbc.user"
            value="sa" />
        <property name="toplink.jdbc.password"
            value="" />
      </properties>
    </persistence-unit>
  </persistence>
```

\=> 여러개의 퍼시스턴스 클래스와, 매핑파일 정보

위의 파일을 가지고, LocalEntityManagerFactoryBean을 선언해준다.

```
@Bean
public LocalEntityManagerFactoryBean entityManagerFactoryBean() {
  LocalEntityManagerFactoryBean emfb
      = new LocalEntityManagerFactoryBean();
  emfb.setPersistenceUnitName("cafePU");
  return emfb;
}
```

\==> 애플리케이션 관리형의 경우 \<persistence.xml>에 필요한 내용을 넣어두어야, PersistenceProvider가 가져올 수 있다. 그런데 스프링을 이용하면, JpaTemplate이 PersistenceProvider와 상호작용을 하기 때문에, 실제로 설정정보를 persistence.xml에 넣는 것은 현명하지 않다.

***

**컨테이너 관리형 JPA 구성**

\* EntityManagerFactory는 컨테이너(스프링)이 제공하는 정보를 바탕으로 생성

```
@Bean
public LocalContainerEntityManagerFactoryBean entityManagerFactory(
        DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {
  LocalContainerEntityManagerFactoryBean emfb = new LocalContainerEntityManagerFactoryBean();
  emfb.setDataSource(dataSource);
  emfb.setJpaVendorAdapter(jpaVendorAdapter);
  emfb.setPackagesToScan("com.bobfull.cafe.domain"); // @Entity로 애너테이션된 클래스를 위한 패키지 스캔 -> xml에 명시적으로 선언할 필요가 없음
  return emfb;
}
```

\* dataSource를 스프링 컨텍스트에서 지정가능 하다

\* persistence.xml이 필요가 없음

\* jpaVendorAdapter의 경우, 특정 JPA 구현체에 대한 정보를  제공하기 위함이다. EclipseLinkJpaVendorAdpater, HibernateJpaVendorAdapter, OpenJpaVendorAdapter가 있다.

```
@Bean 
public JpaVendorAdapter jpaVendorAdapter() {
  HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter(); // 구현체가 하이버네이트를 사용하므로, 해당 어댑터로 결정
  adapter.setDatabase("HSQL");
  adapter.setShowSql(true);
  adapter.setGenerateDdl(false);
  adapter.setDatabasePlatform("org.hibernate.dialect.HSQLDialect");
  return adapter;
}
```

***

**JNDI에서 EntityManagerFactory 가져오기**

서버에 배포하는 경우, EntityManagerFactory는 이미 생성 됐고, 검색을 위해 JNDI에서 대기. 이 경우 스프링의 jee 네임스페이스가 \<jee:jndi-lookup> 요소를 사용하여 레퍼런스를 가진다.

```
<jee:jndi-lookup id="emf" jndi-name="persistence/cafePU" />
```

&#x20;

이러한 설정을 가지고, 빈을 설정

```
@Bean
public JndiObjectFactoryBean entityManagerFactory() {}
JndiObjectFactoryBean jndiObjectFB = new JndiObjectFactoryBean();
  jndiObjectFB.setJndiName("jdbc/CafeDS");
  return jndiObjectFB;
}
```

***

#### JPA 기반 저장소 작성

스프링JPA 통합은 , JpaTemplate과, JpaDaoSupprot클래스에 포함됨. 그럼에도 템플릿 기반의 JPA는 순수 JPA 접근 방식을 위해 별도로 남겨두었다.&#x20;

**스프링에 해방된 JPA**

```
@Repository
@Transactional
public class JpaCafeRepository implements CafeRepository {
    @PersistenceUnit
    private EntityManagerFactory emf; // EntityManagerFactory 주입

    public void addCoffee(Coffee Coffee) {
      emf.createEntityManager().persist(Coffee); // EntityManager 생성 및  사용
    }
    
    public Coffee getCoffeeByName(String name) {
      return emf.createEntityManager().find(Coffee.class, name);
    }
    public void saveCoffee(Coffee coffee) {
      emf.createEntityManager().merge(coffee);
    } 
    ~~~~
}
```

\=> 어떠한, 스프링 템플릿도 사용하지 않음. @PersistencUnit으로, EntityManagerFactory가 애너테이션 되고, 스프링은 이를 저장소에 주입한다. 이후 JpaCafeRepository는 해당 EntityManager를 생성 후 사용한다.

\=> EntityManager가 계속 생성되는 문제가 있음. 스레드 세이프하지 않고, 저장소와 같이 공유 싱글톤 빈으로 주입되지 않기 때문..

***

프록시를 가지는 저장소를 EntityManager에 주입

```
@Repository
@Transactional
public class JpaCafeRepository implements CafeRepository {
    
    @PersistenceContext
    private EntityManager em; // EntityManager 주입

    public void addCoffee(Coffee Coffee) {
      emf.createEntityManager().persist(Coffee); // EntityManager   사용
    }
    
    public Coffee getCoffeeByName(String name) {
      return emf.createEntityManager().find(Coffee.class, name);
    }
    public void saveCoffee(Coffee coffee) {
      emf.createEntityManager().merge(coffee);
    } 
    ~~~~
}
```

\* EntityManager에 직접 주어지기 때문에, 각 메소드를 계속 생성할 필요가 없다.

@PersistenceContext는 EntityManager를 주입하지 않는 대신, 저장소에 실제 EntityManager를 주고, 그것의 프록시를 제공한다. 연관된거나 존재하지 않을 경우에만 새로 생성 => 스레드 세이프

@PersistenceUnit, @PersistenceContext는 모두 JPA스펙에서 제공 => 스프링이 EntityManagerFactory, EntityManager를 이해하고 주입하기 위해서는, PersistenceAnnotationBeanPostProcessor를 구성해야 한다.

```
@Bean
public PersistenceAnnotationBeanPostProcessor paPostProcessor() {
  return new PersistenceAnnotationBeanPostProcessor();
}
```

마지막으로 , 예외 변환을 하기 위해서는 하이버네이트와 마찬가지로 예외변환 클래스도 설정해주어야 한다.

```
@Bean
public BeanPostProcessor persistenceTranslation() {
  return new PersistenceExceptionTranslationPostProcessor();
}
```

***

&#x20;

#### 스프링 데이터를 사용한  자동 JPA 저장소

스프링 데이터를 사용하여, 동일한 저장소 구현을 없애고 저장소 인터페이스 작성을 멈출 수 있다.

```
public interface CafeRepository extends JpaRepository<Cafe, Long> {}
```

해당 인터페이스는 JpaRepository를 확장한다. 스프링 데이터에서 저장소 인터페이스를 발견할 때, CafeRepository구현을 진행한다(애플리케이션 컨텍스트와 비슷)

```
@Configuration
@EnableJpaRepositories(basePackages="com.bobfull.cafe.db")
public class JpaConfiguration {
... }
```

@EnableJpaRepositories() = \<jpa:repositories> - 스프링데이터 JPA저장소의 인터페이스를 확장하는 기본 패키지를 스캔한다.

\=> 일반 JPA동작을 위한 18개의 메소드를 제공함

***

**쿼리메소드 제공하기**

위의 18개 메소드 이외에, 더 많은 메소드가 필요할 때... 쿼리 메소드를 사용 할 수 있다.

스프링 데이터는 메소드 명에서 - 쿼리동사+대상+By+조건 을 통해 메소드를 만든다

* 쿼리 동사 - get, read, find, count 허용
* 대상 - 대부분 무시, JpaRespository를 어떻게  파라미터화 하느냐가 문제
* 조건 - 결과를 제한하는 한 개 이상의 조건을 찾을 수 있음. &#x20;

<figure><img src="https://blog.kakaocdn.net/dn/QlrWH/btqAaUj5vZt/1nKdBuIpSkK0Zlktk0J3F0/img.png" alt=""><figcaption><p>조건</p></figcaption></figure>

예시 : readByFirstnameOrLastname(String first, String last)&#x20;

\=> String프로퍼티 사용 시, 대소문자를 상관하지 않는 IgnoringCase를 포함한다. readByFirstnameIgnoringCaseOrLastnameIgnoringCase(String first, String last)&#x20;

\* 최종적으로 OrderBy로 정렬도 가능하다 readByFirstnameOrLastnameOrderByLastnameAsc(String first, String last)

&#x20;

\==> 사용자가 스프링 데이터의 명명규칙에 맞춰서 메소드의  확정 리스트를 제공하는 것은 불가능.

***

**맞춤형 쿼리 선언**

\* 데이터 명명규칙을 제외하고, 구현체를 만들기 위하여 @Query 애너테이션을 사용할 수 있다.

```
@Query("select * from Coffee c where c.name like '%latte'")
List<Coffee> findAllLattees();
```

\=> 해당 애너테이션을 통해, 직접 쿼리를 짜서 구현도 가능하다.. 하지만 아직은 메소드 구현은 할 수 없다. 단지 스프링 데이터 JPA에 메소드 구현에 대한 힌트를 주기 위한 쿼리를 수행했을 뿐

\* 명명규칙이 너무 복잡하거나, 명명규칙으로 수행이 불가능할 때 사용하면 좋다.

***

**맞춤형 기능 혼합**

\* 명명규칙과, 단일 쿼리(@Query)로도 힘든 저장소의 기능을 구현할 때 옛날 방식인, EntityManager를 직접 작성해야할 수 있다 ==> 실제로 스프링 데이터 JPA가 할수 없는 일을 할 때, 낮은 수준에서 JPA와 함께 동작하도록 해야한다.

\=> 스프링 데이터를 완전히 포기할 필요는 없음

스프링 JPA는 저장소 인터페이스 구현을 생성 후, Impl접미어를 가지는 클래스를 찾음. 해당 클래스와 JPA에 의해 생성된 방법과 병합

```
public class CoffeeRepositoryImpl implements CoffeePriceAdder {
  @PersistenceContext
  private EntityManager em;
  public int priceAdd() {
    String update =
        "UPDATE Coffee coffee " +
        "SET coffee.price = '5000' " +
        "WHERE coffee.price = '4000' " +
        "AND coffee.id IN (" +
        "SELECT c FROM CoffeeData c WHERE (" +
        "  SELECT COUNT(sellAmount) FROM s.coffees coffees) > 10000" +
        ")";
    return em.createQuery(update).executeUpdate();
  }
}
```

\=> 기주입 된, EntityManager를 사용. CoffeeRepository를 구현하지 않음!..&#x20;

```
// CoffeePriceAdder의 인터페이스
public interface CoffeePriceAdder{ int priceAdd(); }


// CafeRepository를 확장 - 코드 중복을 피하는 방법
public interface CafeRepository extends JpaRepository<Cafe, Long> {}, CoffeePriceAdder { ~~ }
```

&#x20;

\=> 둘중 하나의 방법을 사용

***

... 번외로 impl을 사용하고 싶지 않다면?

```
 @EnableJpaRepositories(
          basePackages="com.bobfull.cafe.db",
          repositoryImplementationPostfix="Helper")
```

\=> Postfix를 Helper로 지정한 케이스
