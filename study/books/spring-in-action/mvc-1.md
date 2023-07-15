# 고급 스프링 MVC

### 다양한 스프링 MVC 설정

기본적인 AbstratAnnotationConfigDispatcherServeleteInitializer의 설정이 요구사항을 만족하지 못할 때, 제공하는 방법



#### DispatcherServelet 설정 사용자 정의하기

추가설정을 위해, 오버라이드 할 수 있는 메소드들(getServletMappings(), getRootConfigClasses(), getServeletConfigClsses() 제외)이 존재함.&#x20;

\* customizedRegistration() : AbstratAnnotationConfigDispatcherServeleteInitializer가 서블릿 컨테이너와 DispatcherServelet을 등록한 후, ServletRegistration안에 전달하면서, 호출.. DispatcherServlet에 추가적인 설정 적용

```
@Override
procted void customizeRegistration(Dynamic registration){
  registration.setMultipartConfig(new MultipartConfigElement("/tmp/coffee/uploads"));
}
```

\=> 멀티파트 요청 등록(파일 위치는 /tmp/coffee/uploads)

\=> customizedRegistration()에서 넘겨지는 ServletRegistration.Dynamic으로 setLoadOnStartup()을 호출하여, load-on-startup의 우선순위를 설정, setInitParameter()를 호출하여, 초기 인자를 설정하고, 멀티파트 지원 설정을 위해 setMultipartConfig()를 호출하는 등의 기능을 수행함... (??)



서블릿 필터 추가하기

\* AbstratAnnotationConfigDispatcherServeleteInitializer에서 DispatcherServlet, ContextLoaderListner외의 \[서블릿, 필터, 리스너]를 추가적으로 등록하려면?

\* 웹컨테이너의 추가적인 컴포넌트를 등록한다면, 새 초기화 클래스를 생성하면 됨. 가장 쉬운 방법은 WebApplicationInitializer 구현

```
public class MyServletInitializer implements WebApplicationInitializer {
  @Override
  public void onStartup(servletContext servletContext) throws ServletException {
    Dynamic myServlet = servletContext.addServlet("myServlet", Myservlet.class); // 서블릿 등록
    myServlet.addMappintg("/custom//**"); // 서블릿 매핑
  }
}
```

\=> 서블릿-등록 초기화 클래스&#x20;

```
@Override
public void onStartup(servletContext servletContext) throws ServletException {
  javax.servlet.FilterRegstration.Dynamic filter = servletContext.addFilter("myFilter", MyFilter.class); // 필터 등록
  filter.addMappingForUrlPatterns(null, false, "/custom/*"); // 필터 매핑 추가
}
```

\=> 필터 등록

\==> 하지만, 필터를 등록 후, 단순히 DispatcherServlet엣에 매핑한다면, AbstratAnnotationConfigDispatcherServeleteInitializer에서 getServletFilsters() 메소드를 오버라이드 하면 됨.

```
@Override
protected Filter[] getServletFilters() {
  return new Filter[] { new MyFilter() };
}
```



#### &#x20;

#### 멀티파트 폼 데이터 처리하기

\* 이미지 업로딩의 경우, 헤더는 텍스트이지만 바디의 경우 바이너리 데이터로 이루어져 전달하는 경우가 대부분

\* 스프링 MVC 컨트롤러에선 매우 간단히, 이런 멀티파트 요청을 읽고, 처리할 수 있다.



**멀티파트 리졸버 설정하기**

\* DispatcherServlet에는 멀티파트 요청 파싱 로직이 구현되지 않음. 대신 MultipartResolver 전략 인터페이스 구현부에 위임

\-> 2가지의 구현이 있음. CommonsMultipartResolver, StandardServletMultipartResolver(주로 사용)

```
@Bean
public MultipartResolver multipartResolver() throws IOException {
  return new StandardServletMultipartResolver();
}
```

\=> 리졸버에, 설정을 하는대신, 서블릿 설정에서 멀티파트설정을 명시해주어야함, 최소 파일 저장 임시패스도 없다면 정상적으로 동작하지 않음.

```
@Override
procted void customizeRegistration(Dynamic registration){
  registration.setMultipartConfig(new MultipartConfigElement("/tmp/coffee/uploads"));
}
```

\=> 위에 설정해 주었던.. 최소한의 멀티파트 설정

\* 멀티파트설정에는, 업로드의 크기, 파트의 개수나 개별파트에 관련없는 요청의 최대 크기, 임시저장을 사용하지 않는 최대 크기를 정할 수 있음

```
@Override
protected void customizeRegistration(Dynamic registration){
  registration.setMultipartConfig(new MultipartconfigElement("/tmp/coffee/uploads",
  						2097152, 4194304, 0));
}
```

\=> 업로드 파일 2MB, 전체 요청의 크기 4MB, 모든파일을 디스크에 쓰기

\==> 이외에, 서블릿3.0 미만 스펙에서는 CommonsMultipartResolver를 사용해야함



멀티파트 요청 처리하기

\* @RequestPart 애너테이션 : type이 file인 \<input>을 받을 수 있다(바이너리 데이터)

```
@RequestMapping(value="/register", method=POST)
public String processingRegistration(
		@RequestPart("profilePircture") byte[] profilePicture,
        @Valid Coffee coffee,
        Errors errors) {
     ....
}
```

\=> profilePricture 애트리뷰트에, byte 배열을 넘겨준다. 파일이 없다면 null, 메소드 내에서 처리를 해준다.



**MultipartFile 받기**

스프링은 멀티파트 데이터를 처리하는 다기능 객체를 얻기위해 MultipartFile을 제공한다.&#x20;

```
// MultipartFile 인터페이스
public interface MultipartFile extends InputStreamSource {
    String getName();
    String getOriginalFilename();
    String getContentType();
    boolean isEmpty();
    long getSize();
    byte[] getBytes() throws IOException;
    InputStream getInputStream() throws IOException; // 파일 데이터를 스트림 형태로 읽어오기
    void transferTo(File var1) throws IOException, IllegalStateException; // 업로드된 파일을 파일시스템에 쓰는 메소드
}
```

&#x20;

\=> 위의 processRegistration()에 추가 가능

```
@RequestMapping(value="/register", method=POST)
public String processingRegistration(
		@RequestPart("profilePircture") byte[] profilePicture,
        @Valid Coffee coffee,
        Errors errors) {
  profilePicture.transferTo(new File("/data/coffee/" + profilePicture.getOriginalFilename())); // 로컬 파일시스템에, 해당 파일을 저장
}
```

&#x20;

&#x20;

\=> 이외에, MultipartFile은 Part인터페이스로 대체가 가능하다

```
// 기본적으로 MultiplePart와 메소드 명이 거의 동일, StandardServletMultiplepartResolver가 없어도 동작
public interface Part {
    InputStream getInputStream() throws IOException;
    String getContentType();
    String getName();
    long getSize();
    void write(String var1) throws IOException;
    void delete() throws IOException;
    String getHeader(String var1);
    Collection<String> getHeaders(String var1);
    Collection<String> getHeaderNames();
}
```



#### 예외 처리하기

\* 스프링은 예외를 응답으로 변환 시키는 유용한 방법들을 제공한다.

\=> 자동으로 명시된 HTTP 상태코드로 매핑 //예외는 @ResponseStatuas 애너테이션 // 메소드는 @ExceptionHandler 애너테이션



**예외를 HTTP상태 코드에 매핑하기**

<figure><img src="https://blog.kakaocdn.net/dn/TWnyt/btqzOxLp2VB/SaP5JA1DhtGn2KwqQZ8dtk/img.png" alt=""><figcaption><p>Http 상태 코드로 매핑되는 스프링 예외</p></figcaption></figure>

\=> 기본적으로 해당 상태코드에, 각 Exception이 매핑되어 있음.

이러한 예외들 이외에도, @ResponseState를 통해, 매핑할 수 있다.

```
@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="coffee Not Found")
public class CoffeeNotFoundException extends RuntimeException{
}
```

\=> 커피를 찾았는데 보이지 않는, 404 NOT FOUND에러에 맞는 코드를 매핑한다. 이후, 해당 에러가 발생하 경우에는, 404상태 코드를 가짐



**예외처리 메소드 작성하기**

단순히 에러를 나타내는 것 뿐아니라, 예외의 몇가지를 요청을 다루는 방식과 비슷하게 처리할 수 있다.

예) 이미 생성한 커피 메뉴를, 다시 생성했을 때 예외처리

```
@RequestMapping(method=RequestMethod.POST)
public String saveCoffee(~~~) {
  try{
    coffeeRepsitory.save(~~)
    return "redirect:/coffees";
  } catch(DuplicateCoffeeException e) { // 중복 에러 잡기
    return "error/duplicate";
  }
}
     
```

\=> 기본적인 자아 에제, 메소드가 약간 복잡하지만 동작은 잘한다

\=> 해당 내용에서 예외-처리 코드를 걷어내고, @ExceptionHandler를 통해, 간단한 코드로 바꿀 수 있다.

```
/// 해당 코드에서는, 예외처리 부분을 걷어냄
@RequestMapping(method=RequestMethod.POST)
public String saveCoffee(~~~) {
    coffeeRepsitory.save(~~)
    return "redirect:/coffees";
}

// 새로운 ExceptionHandler코드를 통해, 에러 핸들링 가능
@ExceptionHandler(DuplicateCoffeeException.class)
public String hanldeDuplicateCoffee() {
  return "error/duplicate";
}
```

\* 같은 컨트롤러 내의 모든 핸들러 메소드에서 발생한 예외를 처리한다. 잠재적으로 발생하는 모든 메소드 중복을 커버 가능



#### 어드바이징 컨트롤러

\* @ExceptionHandler 메소드가 동일 컨트롤러 클래스 내의 모든 메소드에서 발생하는 예외를 처리한다면, 모든 컨트롤러 메소드에서 발생하는 예외는 컨트롤러 어드바이스(Controller Advice)로 처리

@ControllerAdvice : @ExceptionHandler, @InitBinder, @ModelAttribute가 포함된 메소드를 모두 포함한다 + @RequestMapping이 붙은 메소드에도 전역적으로 사용된다.

```
// @Component가 자체적으로 붙어있음 -> 컴포넌트 스캐닝에 의해 선택
@ControllerAdvice
public class AppWideExceptionHandler {
  @ExceptionHandlder(DuplicateCoffeeException.class)
  public String duplicateCoffeeHandler() {
    return "error/duplicate";
  }
}
```



&#x20;

#### 리다이렉션 되는 요청간의 데이터 전달

리다이렉션되는 메소드에서, 리다이렉션을 처리하는 메소드에 데이터를 넘겨주는 방법

\* 데이터를 URL 템플릿을 사용하여, 패스변수나 쿼리인자로 전달

\* 데이터를 플래스 애트리뷰트에 넣어서 전달



**URL템플릿과 함께 리다이렉션 하기**&#x20;

\* 패스변수나 쿼리인자로 데이터를 넘기는 것은 매우 간단.... 단순히 "redirect:/coffees/{coffeeName}" 이런 식으로 String에 연결하면 된다. 사용자가 해야할 일은, 모델에 값을 설정해주는 것 뿐

```
@RequestMapping(value="/register", method=POST)
public String processRegistration(~~~~, Model model) {
	~~~~
    model.addAttribute("coffeName", coffee.getCoffeeName());
    return "redircet:/coffee/{coffeeName}";
}
```

\=> URL 템플릿의 플레이스홀더에 채워지므로, 안전하지 않은 문자는 빠짐..  => coffeeName이 아메리카노 일 경우 /coffees/americano로 매핑

```
@RequestMapping(value="/register", method=POST)
public String processRegistration(~~~~, Model model) {
	~~~~
    model.addAttribute("coffeName", coffee.getCoffeeName());
    model.addAttribute("shot", coffee.getShot());
    return "redircet:/coffee/{coffeeName}";
}
```

\=> 해당 경우에는, 모델의 애트리뷰트가 플레이스홀더에 매핑되지 않으므로, 리다이렉션에 자동적으로 쿼리 인자로 붙음

\=> coffeName = americano, shot =2 --> /coffees/americano?shot=2



**플래시 애트리뷰트로 작업하기**

\* 플래시 애트리뷰트란 : Session 기반으로 POST/Redirect/GET 상황에서 Controller간에 데이터를 전달할 수 있도록 Spring MVC에서 지원하는 기능

\* URL에 데이터를 넣는 방식은, 복잡한 인자일 경우 제한적이다. 해당 경우에는 플래시 애트리뷰트로 해결 가능

\* RedirectAttribute : 모델과 동일한 기능을 제공하고, 플래시 애트리뷰트를 위한 메소드도 제공한다.

<figure><img src="https://blog.kakaocdn.net/dn/eeBvTh/btqzQtgxExu/Zf0xGh3kis6f37K9VXwbB1/img.png" alt=""><figcaption><p>리다이렉션에도 불구하고 세션에 저장된 후 모델로 회수</p></figcaption></figure>

```
@RequestMapping(value="/register", method=POST)
public String processRegistration(~~~~, RedirectionAttirbutes model) {
	~~~~
    model.addAttribute("coffeeName", coffee.getCoffeeName());
    model.addFlashAttribute("coffee", coffee);
    // 키는 coffee이고 플래시어트리뷰트로 전달
    return "redircet:/coffee/{coffeeName}";
}
```

&#x20;
