# 오늘날의 보안

안타깝게도 소프트웨어 작성 초기부터 보안을 신중하게 고려하는건 현재 일반적인 관행이 아니다.\
기술을 익히는 초기 단계부터 이런 관행의 문제점이 잘 드러나지 않는것은 문제가 있다.

소프트웨어 시스템을 다룰때는 비 기능적 측면을 고려해야 한다

## 스프링이란 <a href="#h_1" id="h_1"></a>

스프링은 스프링 애플리케이션 컨텍스트 이라는 컨테이너 제공하는데 \
이는 애플리케이션 컴포넌트들을 생성하고 관리한다

애플리케이션 컴포넌트 또는 빈bean들은 스프링 애플리케이션 컨텍스트 내부에서 서로 연결되어 \
완전한 애플리케이션을 만든다.

빈의 상호 연결은 DI(의존성 주입) 기반으로 수행된다\
즉 애플리케이션 컴포넌트는 스프링 컨텍스트에 의해 관리되고 상호 주입된다

### @Configuration <a href="#h_2" id="h_2"></a>

최신 버전의 스프링에서는 xml 보다는 자바 기반 configuration 을 사용하여 빈을 상호연결한다

```java
@Configuration
public class ServiceConfiguration {
    @Bean
    public InventoryService inventoryService() {
        return new InventoryService();
    }
    @Bean
    public ProductService productService() {
        return new ProductService(inventoryService());
    }
}
```

`@Configuration` 은 각 Bean 을 추가해야 된다는 것 알려준다

### 자동-구성 <a href="#h_3" id="h_3"></a>

* Autowiring과 Component Scanning 기반으로 한다
* Component Scanning 을 통해 스프링은 자동으로 애플리케이션의 classpath에 지정된 컴포넌트를 찾은 후 컨텍스트의 빈으로 생성한다
* Autowiring 을 통해 의존관계에 있는 컴포넌트를 자동으로 다른 빈에 주입
* 스프링 부트는 자동 구성 기능이 막강!!!

### 1.2 스프링 애플리케이션 초기 설정하기 <a href="#h_4" id="h_4"></a>

* [https://start.spring.io/](https://start.spring.io/)

<figure><img src="https://blog.kakaocdn.net/dn/ubNqL/btq8vQJR3fx/GegDNVj6QqF9lROdT0p9ck/img.jpg" alt=""><figcaption></figcaption></figure>

#### 1.2.2 스프링 프로젝트 구조 살펴 보기 <a href="#h_5" id="h_5"></a>

**주요 항목**

1. mvnw, mvnw.cmd\
   메이븐 래퍼 스크립트\
   메이븐이 설치 되어 있지 않더라도 이 스크립트로 프로젝트 빌드 가능
2. pom.xml\
   메이블 빌드 명세
3. TacoCloudApplication.java\
   스프링부트 메인 클래스
4. application.properties\
   구성 속성 지정
5. static\
   브라우저에 제공할 정적인 콘텐츠(이미지, 스타일시트 등) 저장 폴더
6. templates\
   브라우제 콘텐츠를 보여주는 템플릿 파일 폴더

**빌드 명세 살펴보기**

1. Spring boot starter 의존성 관리

* Spring Web, thymeleaf, Test 의존성 항목은 spring-boot-starter를 에 포함한다
* 필요로 하는 모든 라이브러리 의존성을 선언하지 않아도 되므로 관리가 쉽다
* 라이브러리 이름이 아닌 기능 관점으로 의존성 생각 가능해준다\
  ex) 웹은 웹 설정을 추가 하자
* 라이브러리들의 버전 걱정을 하지 않아도 된다.

**애플리케이션의 부트스트랩(구동)**

부트 스트랩 클래스(TacoCloudApplication)가 실행 가능 JAR 파일에서 애플리케이션을 실행한다

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // 스프링부트 애플리케이션
public class TacoCloudApplication {

 public static void main(String[] args) {
  // 애플리케이션 실행
  SpringApplication.run(TacoCloudApplication.class, args); 
 }
}
```

`@SpringBootApplication` 은 3개의 어노테이션이 결합한 것이다

1. `@SpringBootConfiguration`\
   현재 클래스 (TacoCloudApplication) 를 구성 클래스로 지정한다
2. `@EnableAutoConfiguration`\
   스프링 부트 자동-구성을 활성화 한다
3. `@ComponentScan`\
   컴포넌트 검색을 활성화 한다\
   `@Component`, `@Controller`, `@Service` 등의 애노테이션과 함께 클래스를 선언할 수 있게 한다

**애플리케이션 테스트하기**

`@SpringBootTest` 가 스프링부트 기능으로 테스트 시작하라고 Junit 에게 알려준다

### 1.3 스프링 애플리케이션 작성하기 <a href="#h_6" id="h_6"></a>

#### 1.3.1 웹 요청 처리하기 <a href="#h_7" id="h_7"></a>

**컨트롤러**

* 웹 요청과 응답을 처리하는 컴포넌트
* 웹브라우저 요청을 상대할 경우 컨트롤러는 선택적으로 모델데이터를 채워 응답하며 브라우저에 반환되는 HTML 을 생성하기 위해 응답의 웹요청을 뷰에 전달

```kotlin
@Controller
public class HomeController {

    @GetMapping("/") // 루트 경로 / 의 웹 요청 처리
    public String home() {
        return "home"; // 뷰 이름 반환
    }
}
```

#### 1.3.2 뷰 정의하기 <a href="#h_8" id="h_8"></a>

src/main/resources/templates 에 html file 생성

#### 1.3.3 컨트롤러 테스트하기 <a href="#h_9" id="h_9"></a>

```java
@WebMvcTest(HomeController.class)
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc; // MockMvc 주입

    @Test
    public void testHomepage() throws Exception {
        mockMvc.perform(get("/")) // get 수행
            .andExpect(status().isOk()) // HTTP 200 OK 리턴
            .andExpect(view().name("home")) // home.html view 
            .andExpect(content().string(containsString("Welcome to..."))); // Welcome to... 텍스트 포함
    }
}
```

#### 1.3.5 스프링 부트 DevTools 알아보기 <a href="#h_10" id="h_10"></a>

개발자에게 편리한 도구 제공

* 코드 변경 시 자도으로 애플리케이션 다시 시작한다
* 브라우저로 전송되는 리소스가 변경될 때 자동으로 브라우저 새로고침한다
* 템플릿 캐시를 자동으로 비활성화 한다
* h2 데이터베이스를 사용한다면 자동으로 h2 콘솔을 활성화 한다

#### 1.3.6 리뷰하기 <a href="#h_11" id="h_11"></a>

**빌드 명세**

Web, Thymeleaf 의존성은 일부 다른 의존성도 포함 시킨다

* 스프링의 MVC 프레임워크
* 내장된 톰캣
* Thymeleaf, Thymeleaf레이아웃 dialect

**스프링부트 자동-구성 라이브러리 개입**

* 스프링 mvc 를 활성화하기 위해 컨텍스트에 관련된 빈들을 구성한다
* 내장된 톰캣 서버를 컨텍스트에 구성한다
* Thymeleaf 템플릿을 사용하는 스프링 MCS 뷰를 나타내기 위해 Thymeleaf 뷰 리졸버를 구서한다

### 1.4 스프링 살펴보기 <a href="#h_12" id="h_12"></a>

* 핵심 스프링 프레임워크
* 스프링 부트
* 스프링 데이터
* 스프링 시큐리티
* 스프링 통합과 배치
* 스프링 클라우드

***

### 📌 요약 <a href="#h_13" id="h_13"></a>

#### 1. 스프링의 목표 <a href="#h_14" id="h_14"></a>

* 웹 애플리케이션 생성, 데이터베이스 사용, 애플리케이션 보안, 마이크로서비스 등에서 개발자 노력을 줄여주자

#### 2. 스프링부트는 왜 쓰는가 <a href="#h_15" id="h_15"></a>

* 의존성관리, 자동-구성, 런타임 시의 애플리케이션 내부 작동 파악을 스프링에서 할수 있게 한다

#### 3. 스프링, 스프링부트 개요 <a href="#h_16" id="h_16"></a>

* 스프링 애플리케이션은 스프링 Initionalizer 를 사용해서 초기 설정 가능
* bean 이라고 하는 컴포넌트는 스프핑 애플리케이션 컨텍스트에서 자바나 xml 로 선언 가능하다
* 빈은 컴포넌트 탐색으로 찾거나 스프링 부트 자동-구성에서 자동으로 구성 가능하다
