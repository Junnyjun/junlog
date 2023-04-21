# 테스트

스프링의 핵심인 `IoC`와 `DI`는 오브젝트의 설계와 생성, 관계, 사용에 관한 기술이다.

애플리케이션은 계속 변하고 복잡해져 간다. 그 변화에 대응하는 첫 번째 전략이 `확장`과 `변화`를 고려한 `객체지향적 설계`와 그것을 효과적으로 담아낼 수 있는 `IoC/DI` 같은 기술이라면, 두 번째 전략은 만들어진 코드를 확신할 수 있게 해주고 변화에 유연하게 대처할 수 있는 자신감을 주는 `테스트 기술`이다.

**스프링으로 개발을 하면서 테스트를 만들지 않는다면 이는 스프링이 지닌 가치의 절반을 초기하는 셈이다.**

## UserDaoTest 다시 보기

### UserDaoTest의 특징

```java
public class UserDaoTest {
	public static void main(String[] args) throws SQLException {
		ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");

		UserDao dao = context.getBean("userDao", UserDao.class);

		User user = new User();
		user.setId("user);
		user.setName("백기선");
		uset.setPassword("married");

		dao.add(user);

		System.out.println(user.getId() + "등록 성공");

		User user2 = dao.get(user.getId());
		System.out.println(user2.getName());
		System.out.println(user2.getPassword());

		System.out.println(user2.getId() + "조회 성공");
	}
}
```

이 테스트 코드의 내용을 정리해보면 다음과 같다.

* `main()` 메소드를 이용한다.
* `UserDao`의 오브젝트를 가져와 메소드를 호출한다.
* 테스트에 사용할 입력 값(`User 오브젝트`)을 직접 코드에서 만들어 넣어준다.
* 테스트의 결과를 콘솔에 `출력`해준다.
* 각 단계의 작업이 에러 없이 끝나면 `콘솔`에 성공 메시지로 출력해준다.

### 웹을 통한 DAO 테스트 방법의 문제점

이렇게 웹 화면을 통해 값을 입력하고, 기능을 수행하고 결과를 확인하는 방법은 가장 흔히 쓰이는 방법이지만 `DAO`에 대한 테스트로서는 `단점`이 너무 많다. 모든 `레이어`의 기능을 다 만들고 나서야 테스트가 가능하다는 점이 가장 큰 문제다. 하나의 테스트를 수행하는 데 참여하는 `클래스`와 `코드`가 너무 많기 때문이다.

```java
if (!user.getName().equals(user2.getName())) {
	System.out.println("테스트 실패 (name)");
} else if (!user.getPassword().equals(user2.getPassword())) {
	System.out.println("테스트 실패 (name)");
} else {
	System.out.println("조회 테스트 성공");
} 
```

### 작은 단위의 테스트

**테스트는 가능하면 작은 단위로 쪼개서 집중해서 할 수 있어야 한다.** `관심사의 분리`라는 원리가 여기에도 적용된다. 이렇게 작은 단위의 코드에 대해 테스트를 수행한 것을 단위 테스트(`Unit Test`)라고 한다. 충분히 하나의 관심에 집중해서 효율적으로 테스트할 만한 범위의 단위라고 보면 된다.

때로는 `단위 테스트` 없이 바로 이런 긴 테스트만 하는 경우도 있다. **이때는 문제의 원인을 찾기가 매우 힘들다.**

### UserDaoTest의 문제점

#### 수동 확인 작업의 번거로움

`콘솔`에 나온 값을 보고 `등록`과 `조회`가 성공적으로 되고 있는지를 확인하는 건 `사람의 책임`이다. UserDaoTest는 간단하지만 검증해야 하는 필드 양이 많고 복잡해지면 역시 `불편함`을 느낄 수밖에 없다.

#### 실행 작업의 번거로움

만약 `DAO`가 `수백 개`가 되고 그에 대한 `main()` 메소드도 그만큼 만들어진다면, 전체 기능을 테스트해보기 위해 `main()` 메소드를 수백 번 실행하는 수고가 필요하다.

## UserDaoTest 개선

### 테스트의 효율적인 수행과 결과 관리

#### JUnit 테스트로 전환

`프레임워크`는 개발자가 만든 클래스에 대한 제어 권한을 넘겨받아서 `주도적`으로 애플리케이션의 흐름을 제어한다. 개발자가 만든 클래스의 `오브젝트`를 생성하고 실행하는 일은 프레임워크에 의해 진행된다.



#### 테스트 메소드 전환

* 메소드를 `public`으로 선언돼어야 한다.
*   `@Test`라는 애노테이션을 붙여주어야 한다.

    ```
    public class UserDaoTest {
    	@Test
    	public void addAndGet() throws SQLException {
    		...
    	}
    }
    ```
*   main 메소드에 Test 실행 클래스 정보를 넣어준다.

    ```
    import org.junit.runner.JUnitCore;
    ...
    public static void main(String[] args) {
    	JUnitCore.main("springbookuser.dao.UserDaoTest");
    }
    ```

### 검증 코드 전환

`if` 문장의 기능을 `JUnit`이 제공해주는 `assertThat`이라는 스태틱 메소드를 이용해 다음과 같이 변경할 수 있다.

```java
assertThat(user2.getName(), is(user.getName()));
```

### 테스트 결과의 일관성

`반복적`으로 테스트를 했을 때 테스트가 실패하기도 하고 성공하기도 한다면 이는 좋은 테스트라고 할 수가 없다. 가장 좋은 해결책은 `addAndGet()` 테스트를 마치고 나면 테스트가 등록한 사용자 정보를 삭제해서, 테스트를 수행하기 이전 상태로 만들어주는 것이다. (일일이 테스트 실행 후 롤백하는 코드를 구현하였다..ㅜㅜ)

## 테스트 주도 개발

`테스트 코드`를 먼저 만들고, 테스트를 `성공`하게 해주는 코드를 작성하는 방식의 개발 방법이 있다. 이를 테스트 주도 개발이라고 한다.

`TDD`는 아예 테스트를 먼저 만들고 그 테스트가 성공하도록 하는 코드만 만드는 식으로 진행하기 때문에 테스트를 빼먹지 않고 꼼꼼하게 만들어낼 수 있다.

### 테스트 코드 개선

`JUnit`은 `@Test`가 붙은 메소드를 실행하기 전과 후에 각각 `@Before`와 `@After`가 붙은 메소드를 자동으로 실행한다. 공통적인 준비 작업과 정리 작업은 `@Before`나 `@After`가 붙은 메소드에 넣어두면 `JUnit`이 자동으로 메소드를 실행해 준다.

```java
@Before
public void init() {
    LOG.info("startup");
    list = new ArrayList<>(Arrays.asList("test1", "test2"));
}

@After
public void finalize() {
    LOG.info("finalize");
    list.clear();
}
```

#### @BeforeClass, @AfterClass

테스트 클래스 실행 전과 실행 후 한번씩만 수행되는 메소드도 지원된다.

```
@BeforeClass
public static void setup() {
    LOG.info("startup - creating DB connection");
}

@AfterClass
public static void tearDown() {
    LOG.info("closing DB connection");
}
```

## 스프링 테스트 적용

#### @RunWith

`JUnit` 프레임워크의 테스트 실행 방법을 확장할 때 사용하는 애노테이션이다. `SpringJUnit4ClassRunner`라는 JUnit용 테스트 컨텍스트 프레임워크 확장 클래스를 지정해주면 `JUnit`이 테스트를 진행하는 중에 테스트가 사용할 `애플리케이션 컨텍스트`를 만들고 관리하는 작업을 진행해준다.

#### @ContextConfiguration

`@ContextConfiguration`은 자동으로 만들어줄 애플리케이션 컨텍스트의 설정파일 위치를 지정한 것이다.

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/application.xml")
public class UserDaoTest {
	@Autowired
	private ApplicationContext context;
	...

	@Before
	public void setUp() {
		this.dao = this.context.getBean("userDao", UserDao.class);
		...
	}

	...
}
```

### @DirtiesContext

테스트 메소드에서 애플리케이션 컨텍스트의 구성이나 상태를 변경한다는 것을 테스트 컨텍스트 프레임워크에 알려준다.

```java
@DirtiesContext
public class UserDaoTest {
	@Autowired
	UserDao dao;

	@Before
	public void setUp() {
		...
		DataSource dataSource = new SingleConnetionDataSource(
			"jdbc:mysql://localhost/testdb". "spring", "book", true);
	}
...
```

테스트에 필요한 빈 설정을 다르게 하고 싶다면 테스트용 applicationcontext.xml 파일을 생성하여 지정해줄 수 도 있다.

```java
@RunWith(SpringUnitClassRunner.class)
@ContextConfiguration(locations="/test-applicationContext.xml")
public class UserDaoTest {
	...
```

### 정리

* 테스트는 `자동화`돼야 하고, 빠르게 실행할 수 있어야 한다.
* `main()` 테스트 대신 `JUnit` 프레임워크를 이용한 테스트 작성이 편리하다.
* 테스트 결과는 `일관성`이 있어야 한다. 코드의 변경 없이 환경이나 테스트 실행 순서에 따라서 결과가 달라지면 안 된다.
* 테스트는 `포괄적`으로 작성해야 한다. `충분한 검증`을 하지 않는 테스트는 없는 것보다 나쁠수 있다.
* `코드 작성`과 `테스트 수행`의 간격이 짧을수록 효과적이다.
* 테스트하기 쉬운 코드가 좋은 코드다.
* 테스트를 먼저 만들고 테스트를 성공시키는 코드를 만들어가는 `테스트 주도 개발 방법`도 유용한다.
* `@Before`, `@After`를 사용해서 테스트 메소드들의 `공통 준비 작업`과 `정리 작업`을 처리할 수 있다.
* 오류가 발견될 경우 그에 대한 `버그 테스트`를 만들어두면 유용하다.
