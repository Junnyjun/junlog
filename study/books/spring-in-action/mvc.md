# 스프링 MVC 시작하기

#### 스프링 MVC를 이용한 요청 추적

\- 웹브라우저에서 링크 클릭 혹은 폼을 서브밋할 때, 요청을 처리하기 위한 작업이 수행됨.&#x20;

<figure><img src="https://blog.kakaocdn.net/dn/bCg5Jn/btqzAWXTBmu/n47sJz1DsGPTRKUl05KyVk/img.png" alt=""><figcaption><p>요청 처리 정보</p></figcaption></figure>

1\. 요청이 브라우저에서 떠나면서  사용자의 요구 내용 전달 -> DispatcherServelet

2\. DispatcherServlet에서, 다음 요청이 가야할 곳을 찾기위해 핸들러 매핑에게 도움 요청 -> 컨트롤러 선택

3\. 선택된 컨트롤러에, DispatcherServlet가 요청을 보냄 -> 요청은 페이로드로 떨굼 -> 이후 컨트롤러의 처리 시간동안 대기 -> 브라우저용 정보로 반환(모델) -> 이 정보들을, 사용자가 보기 편한 형태로 바꾸는 뷰가 필요(Like JSP)

4\. 모델과 뷰의 이름을 확인한 후, 함께 DispatcherServelet으로 요청을 반환

5\. DispatcherServlet은 뷰리졸버에게 전달 받은, 뷰의 이름(논리적으로 주어진?....)과 실제로 구현된 뷰를 매핑해줄 것을  요청 -> 렌더링을 위한 뷰가 어떤 것인지 알게 됨

6\. 일반적인 JSP로 모델 데이터를 전달해주는 뷰의 구현

7\. 모델데이터로 렌더링 후, 응답 객체로 전달

&#x20;

#### 스프링 MVC 설정

**DispatcherServelet 설정**

\* 스프링 MVC의 핵심, 과거에는 web.xml을 사용했으나, 이제는 Java로 설정 가능

```
package cafe.config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class CafeWebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer{

    @Override
    // DispatcherServelet을 '/'에 매핑
    // 매핑 되기 위한, 하나 혹은 여러개의 패스를 지정한다, '/'은 애플리케이션으로 오는 모든 요청을 처리
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[] {RootConfig.class };
    }

    @Override
    // 설정 클래스를 명시
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[] { WebCofig.class };
    }


}
```

&#x20;

#### DispatcherServlet과 ContextLoaderLisnter란?

위의 DispatcherServlet의 getRootConfigClasses()와 getServletConfigClasses() 메소드를 이해하려면, 서블릿 리스너 사이의 관계에 대한 이해가 필요함

\* DispatcherServlet은 컨트롤러, 뷰리졸버, 핸들러 매핑 등 웹 컴포넌트가 포함된 빈을 로딩할 것으로 생각

\* 반면, ContextLoaderListner는 애플리케이 내의 그 외 빈을 로딩할 것으로 생각(중간계층, 데이터 계층 컴포넌트)

&#x20;

AbstractAnnotationConfigDispatcherServletInitializer는 DispatcherServlet과 ContextLoaderListner를 생성...  ==> web.xml의 대안.. 아파치 톰캣7, 서블릿 3.0 이상에서만 지원

\* getServletConfigClasses()에 리턴된 @Configuration 클래스는 DispatcherServlet에 매핑

\* getRootConfigClasses()에 리턴된 @Configuration 클래스는 ContextLoaderListner에 매핑

&#x20;

**스프링 MVC 활성화 하기**

```
package cafe.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@EnableWebMvc   // Spring MVC 활성화
@ComponentScan("cafe.web")  // 컴포넌트 스캐닝 활성화
public class WebConfig extends WebMvcConfigurerAdapter {

    // 뷰 리졸버 생성
    public ViewResolver viewResolver(){
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        // 기본 뷰 리졸버 = JSP를 뷰로 사용할 때 사용

        resolver.setPrefix("/WEB-INF/views/"); // 프리픽스 지정
        resolver.setSuffix(".jsp"); // 서픽스 지정
        resolver.setExposeContextBeansAsAttributes(true); // 스프링 빈을, ApplicationContext에서 접근 가능하게 설정 ${..} 플레이스 홀더 사용 가능
        return resolver;
    }

    @Override
    // 정적 콘텐츠 처리  설정
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable(); // 고정적인 리소스에 대한 요청을 직접 처리하지 않고, 서블릿 컨테이너의 디폴트 서블릿 전달 요청
    }
}
```

\* WebConfig : 컴포넌트 스캔을 통해, 컨트롤러를 가져올 수 있다, 이로인해 내부에 컨트롤러를 선언할 필요가 없음,&#x20;

```
// 컴포넌트 스캐닝을 하는 기본 RootConfig
@Configuration
@ComponentScan(basePackages = {"spittr"},
    excludeFilters = {
            @ComponentScan.Filter(type= FilterType.ANNOTATION, value = EnableWebMvc.class)
    })
public class RootConfig {
}
```

\* RootConfig : 해당 클래스는 컴포넌트 스캐닝을  한다는 점 이외에는, 다음에 다뤄보기로 함.

&#x20;

#### 간단한 컨트롤러  작성하기

@RequestMapping : 컨트롤러가 처리할, 요청의 종류를 정의하는 애너테이션 ( value로 요청 패스를, method로 요청 메소드를..)

```
@Controller // @Component에 기반을 둔, 정형화된 애너테이션, 컴포넌트 스캔 가능
public class HomeController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    // 요청패스 "/"에 관하여, Http Get 요청을 함
    public String home() {
        return "home"; // 뷰의 이름이 home
        // view는, webConfig에서 /WEB-INF/views 프리픽스, .jsp 서픽스를 지정했으므로, /WEB-INF/views/home.jsp 가 해당 뷰
    }
}


==> 혹은 이렇게 클래스 레벨 요청으로 처리 가능
@Controller
@RequestMapping({"/", "/hompage"}) // "/"와 "/hompage"에 관하여 클레스 레벨로 처리
public class HomeController {

    @RequestMapping(method = RequestMethod.GET)
    public String home() {
        return "home"; // 뷰의 이름이 home
      }
}
```

&#x20;

```
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>home</title>
</head>
<body>
home
</body>
</html>
```

\==> /WEB-INF/views/home.jsp

<figure><img src="https://blog.kakaocdn.net/dn/dmMCvM/btqzHUSn5tk/e1W1MyCr3m4cYh2gtKhuuk/img.png" alt=""><figcaption><p>간단한 home화면</p></figcaption></figure>

&#x20;

&#x20;

&#x20;

**컨트롤러 테스팅**

일반적인, POJO 테스트로는, 리턴되는 home이 뷰에관한 리턴이 아닌, 단순히 String인 "home"이 리턴된다.

따라서, MVC를 위한 컨트롤러 테스팅이 필요함

```
public class HomeControllerTest {

    @Test
    public void testHomePage() throws Exception{
        HomeController controller = new HomeController();
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
		// mockMvc 관련 셋업... 
        
        mockMvc.perform(get("/")).andExpect(view().name("home"));
        // "/"요청에 관한 get을 수행하는 뷰의 이름이, "home"인지 체크
    }
}
```

&#x20;

#### 뷰에 모델 데이터 전달하기

\* 데이터에 엑세스를 위한 저장소를 정의해주어야 함. 실제 구현은 나중에

```
public interface CoffeeRepository {
    List<Coffee> findCoffees(int min, int max);
}
```

\-> 커피를 찾기 위한, 저장소 인터페이스( 최소가격, 최대가격)

```
public class Coffee {
    private final String name;
    private final int  price;
    ... 
    생성자, getter 생략
}
```

\==> Coffee 클래스, 커피의 id번호, 이름, 샷, 물, 우유의 양 저장

**Coffee 데이터를 처리할  수 있는 컨트롤러 작성**

```
@Controller
@RequestMapping("/coffees")
public class CoffeeController {

    private CoffeeRepository coffeeRepository;

    @Autowired // coffeeReposi
    public CoffeeController(CoffeeRepository coffeeRepository){
        this.coffeeRepository = coffeeRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String coffees(Model model){
        model.addAllAttributes(
                coffeeRespository.findCoffees(1000, 5000)
        );
        // 해당 List<Coffee>를 모델에 추가함
        return "coffees"; 
        // 뷰 : /WEB-INF/views/coffees.jsp
    }
}
```

\==> Model에 직접, 뷰로 전달할 데이터를 넣어줄 수 있다 - 위 코드에선, 1000\~5000원 사이의 커피 목록

**테스팅**

```
 @Test
    public void testCoffee() throws Exception {
        List<Coffee> expectedCoffees = createCoffeeList(20);

        // mock 저장소
        CoffeeRepository mockRepository =
                mock(CoffeeRepository.class);
        when(mockRepository.findCoffees(1000, 5000)).thenReturn(expectedCoffees);

        CoffeeController controller = new CoffeeController(mockRepository);

        // mock 스프링 MVC
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setSingleView(new InternalResourceView("/WEB-INF/views/coffees.jsp"))
                .build();

        mockMvc.perform(get("/coffees"))
                .andExpect(view().name("coffees"))
                .andExpect(model().attributeExists("coffeeList"))
                .andExpect(model().attribute("coffeeList",
                        hasItems(expectedCoffees.toArray())));
                        // List<Coffee>이므로 coffeeList로 인식함.. 
    }

    private List<Coffee> createCoffeeList(int count) {
        List<Coffee> coffees = new ArrayList<Coffee>();

        for(int i = 0; i < count; i++){
            coffees.add(new Coffee("Coffee" + i, 2500));
        }

        return  coffees;
    }
```

\* mock 저장소를 만들고, 이를 mock 스프링 MVC에서 가져온 모델이 일치하는지 테스트

**coffees.jsp**

```
<body>
    <c:forEach items="${coffeeList}" var="coffee" >
        <li id ="coffee_<c:out value="coffee.name"/>">
            <div class="coffeePrice">
                <c:out value="${coffee.price}"/>
            </div>
        </li>
    </c:forEach>
</body>
```

\==> 모델로 받은 데이터 coffeeList를 간단하게 사용하는 jsp파일 // coffeeList를 그대로 사용 가능(플레이스 홀더)

### 요청입력받기

스프링 MVC는 클라이언트가 데이터를 전달해줄 몇 가지 방법을 제공함

\* 쿼리파라미터, 폼 파라미터, 패스 변수

&#x20;

#### 쿼리파라미터 입력받기

\* 이전 글에서 사용한 방식은, 코드 내에 1000이나 5000이 직접 적혀있었고, 클라이언트는 이를 바꾸지 못하는 구조였다. 만약 4천원\~ 5천원 사이인 커피의 목록을 알고싶은 사용자는 어떻게 해야할까? => 클라이언트의 GET 요청에서, min과 max의 파라미터값을 받으면 됨

```
@RequestMapping(method = RequestMethod.GET)
    public List<Coffee> coffees( @RequestParam(value="min", defaultValue = "1000") int min
                              ,  @RequestParam(value="max", defaultValue = "5000") int max
                            ){
        return coffeeRepository.findCoffees(min, max);
}
```

\=> @RequestParam 애너테이션을 통해, value 값, 기본값을 지정할  수 있다. 아무것도 입력하지 않으면 1000\~5000원 사이의 커피

\=> 가장 일반적인 방식 => \[url]/coffees?min=4000\&max=5000 ==> 4\~5천원 커피 찾기

**테스트**

```
// mock 저장소
        CoffeeRepository mockRepository =
                mock(CoffeeRepository.class);
        when(mockRepository.findCoffees(4000, 5000)).thenReturn(expectedCoffees);
		// 파라미터 4000, 5000으로 예상
... 생략

mockMvc.perform(get("/coffees?min=4000&max=5000"))
                .andExpect(view().name("coffees"))
                .andExpect(model().attributeExists("coffeeList"))
                .andExpect(model().attribute("coffeeList",
                        hasItems(expectedCoffees.toArray())));
```

\=>  \[url]/coffees?min=4000\&max=5000 요청 부분을 수정한다

&#x20;

#### 패스파라미터 입력받기

커피를 찾는데, 주어진 '이름' 하나만 가지고 찾는다고 가정한다. 이를 위한 방법은 @RequestParam을 이용해 name을 쿼리 파라미터로 받는 것 일수 있다

```
 @RequestMapping(value = "/{coffeeName}", method = RequestMethod.GET)
    public String showCoffee(@PathVariable("coffeeName") String name, Model model)
    {
        model.addAttribute(coffeeRepository.findOne(name));
        return "coffee";
    }
```

\=> @PathVariable 애너테이션을 이용하여, /{coffeeName}에 관한 요청을 수행할 수 있다,

간단하지만.. 플레이스 홀더의 이름과 pathVarable의 이름이 꼭 일치해야하므로, 주의해주어야함

\=> \[url]/coffees/amerciano => 아메리카노 찾기.

**테스트**

```
mockMvc.perform(get("/coffees/americano"))
                .andExpect(view().name("coffees"))
                .andExpect(model().attributeExists("coffee"))
                .andExpect(model().attribute("coffee", expectedCoffee));
```

\=> => \[url]/americano => 요청 부분 및, list가 아니므로, 일치하는 부분 수정

&#x20;

#### 폼 처리하기

폼으로 처리하기 앞서서, 폼을 입력할 수 있는 컨트롤러와, 폼을 만든다

```
@RequestMapping(value = "/register", method = RequestMethod.GET)
    public String showRegistrationForm() {
        return "registerForm";
    }
```

&#x20;

```
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <h1>Register</h1>

    <form method="POST">
        Coffee name : <input type="text" name="name" /> <br/>
        Coffee price : <input type="text" name="price"/> <br/>
    </form>
</body>
</html>
```

\=> 등록 컨트롤러, 및 커피 등록 jsp

<figure><img src="https://blog.kakaocdn.net/dn/shOvX/btqzFVEGoaF/rfJ8SDBLyam8NP9Iy9sf9k/img.png" alt=""><figcaption><p>간단히 만든 regiser폼</p></figcaption></figure>

**폼 처리 컨트롤러 작성**

\* 폼처리 관련하여, POST로 받는 컨트롤러를 구현한다.

```
  @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String processionRegist(Coffee coffee) {
        coffeeRepository.save(coffee);

        return "redirect:/coffees/" + coffee.getName() ;
    }
```

\=> Register에서, name, price를 가져와 Coffee 클래스를 만든후 => save로 등록 => redirect로 등록된 커피를 볼 수 있는 메소드

\=> redirect를 하면 -> \[url]/coffees/커피이름 으로 연결되므로 => 위에서 만든 패스 파라미터로 처리 가능

**테스트**

```
mockMvc.perform(post("/coffees/register")
                        .param("name", "americano")
                        .param("price", "2500"))
                .andExpect(redirectedUrl("/coffees/americano"));
```

&#x20;

#### 폼 검증하기

\* 폼을 사용할때, 이름이 없거나 가격이 없는 경우에도, 값이 넘어갈 수 있는 점을 미연에 방지하기 위함

@NotNull 애너테이션 등이 주로 사용된다.

```
    @NotNull
    @Size(min=3, max=20)
    private final String name;
```

폼을 사용 할 때, name의 값을 null이 되지 못하며 5\~20 길이의 String임을 명시

@Valid 애너테이션을 통해, 검증한다

```
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String processionRegist(@Valid Coffee coffee, Errors errors) {
        if (errors.hasErrors()){
            return "registerForm";
        }
        
        coffeeRepository.save(coffee);

        return "redirect:/coffees/" + coffee.getName() ;
    }
```

\=> 만약 검증되지 않았다면, error가 생기고 바로 registerForm에 그대로 남게 됨
