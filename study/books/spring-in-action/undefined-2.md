# 웹 뷰 렌더링

#### 뷰 리졸루션 이해하기

\* 컨트롤러에서는, 직접적으로 브라우저에 렌더링하는 HTML을 생성해 내는 메소드가 없음. 대신, 렌더링을 위한 데이터를 모델에 담아 뷰에 전달하는 역할을 함. 뷰의 논리적 이름만 전달

\* 이렇게 컨트롤러의 요청+처리로직 // 뷰의 뷰-렌더링 분리는 스프링 MVC의 중요한 기능&#x20;

\* 실제로 스프링이 모델을 렌더링하여 구현되는 뷰를 결정하는 것이 '뷰 리졸버(View Resolver)'

ViewResolver & View인터페이스

```
public interface ViewResolver {
  View resolveViewName(String viewName, Locale locale) throws Exception;
}

public interface View {
  String getContentType();
  Void render(Map<String, ?> model, HttpServletRequest request,
              HttpservletResponse reponse) throws Exception
}
```

\=> ViewResolver는 뷰의 이름과 Locale을 넘겨 받고 View를 리턴해줌

\=> View는, 모델을 전달 받은 후, 서블릿 요청과 응답 객체를 받아 결과를 렌더링 해줌

\* 스프링은 논리적 뷰 이름을 물리적인 뷰의 구현으로 변환하기 위한 13개의 뷰 리졸버를 제공한다..

#### JSP뷰 만들기

**뷰 리졸버 설정**

\* ResourceBundleViewResolver 로 직접 논리적인 뷰 이름을 특정 구현과 매핑시킬 수있음

\* InternalResourceViewResolver는 간접적으로 접근, 접두사와 접미사를 뷰 이름에 붙이는 규칙을 따름(5장에서 사용)

\=>InternalResourceViewResolver를 일반적으로 사용하며, 5장에서 다루었으므로 설정 방식은 넘어감

```
    @Bean
    public ViewResolver viewResolver(){
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        // 기본 뷰 리졸버 = JSP를 뷰로 사용할 때 사용

        resolver.setPrefix("/WEB-INF/views/"); // 프리픽스 지정
        resolver.setSuffix(".jsp"); // 서픽스 지정
        resolver.setExposeContextBeansAsAttributes(true); // 스프링 빈을, ApplicationContext에서 접근 가능하게 설정 ${..} 플레이스 홀더 사용 가능
        return resolver;
    }
```

\=>5장의 코드

**스프링의 JSP 라이브러리 사용하기**

\*간단하게만 살펴보도록 함

폼에 모델 바인딩하기

```
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf"%>
```

폼바폼 바인딩 태그를 사용하기 위한 선언 ( sf는 spring form의 약어)

```
<sf:form method="POST" commandName="coffee">
        Coffee name : <sf:input path="name" /> <br/>
        Coffee price : <sf:input path="price"/> <br/>
        <input type="submit" value="register">
</sf:form>
```

\=> commandName 애트리뷰트에 지정되는 모델 객체의 컨텍스트를 설정

\=> 장점 :  표준 HTML태그를 사용하는 것보다, 좀 더 개선된 점을 제공.. 검증 실패후 폼이 이미 입력된 값으로 채워져 있음..

&#x20;

오류표시하기

\* \<sf:errors>를 사용하여, 표시해 줄 에러들을 추출함

```
Coffee name : <sf:input path="name" /> <sf:errors path="name" cssClass="error"/> <br/>
```

\=> 폼에 저장된 값이 에러일 경우. 검증 내용을 cssClass="error"을 통해 알려준다.

```
span.error{
 color: red;
 }
```

\=> 에러가 있을 경우, 에러가 빨간색으로 바뀜

&#x20;

스프링의 일반 태그 라이브러리

스프링은 더 일반적인 JSP 태그 라이브러리를 제공함 ... 많은 태그들은, 자주 사용되진 않음

```
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
```

&#x20;

다국어 메시지 표시하기

```
<h1><s:message code="cafe.welcome"/></h1>
```

\=> cafe.welcome이라는 프로퍼티에서 가져와 렌더링한다.

\=> 따라서 스프링 내에서, 프로퍼티를 환경별로 다양하게 만들어둔다면, 해당 내용이 바뀐다.

```
message_en.properties
===
cafe.welcome=Welcome


message_ko.properties
===
cafe.welcome=반갑습니다
```

\=> en 프로퍼티를 쓸경우와, ko프로퍼티를 쓸경우의 값이 달라짐 => 따라서 뷰의 렌더링도 달라짐

&#x20;

URL 만들기

\<s:url> : 일반적인 기능 태그, URL을 변수로 할당하거나, 응답 내부 렌더링

```
<s:url href="/coffees/{name}" var="coffeeUrl">
  <s:param name="name" value="americano" />
</s:url>
```

\=> param에 명시된, 파라미터와 대응하는 플레이스 홀더라면, 파라미터는 플레이스홀더를 대체한다. 하지만 없다면, 쿼리 파라미터로 사용

\=> 대체가능한 패스 파라미터를 포함한다는 점에서, \<c:url>과의 차이점이 있음

&#x20;

콘텐트 이스케이핑

\<s:escapeBody> : 바디 내부에서 이스케이핑이 필요한 내용에 대해 렌더링 해주는 이스케이프 태그

```
<s:escapeBody htmlEscape="true">
<h1>Hello</h1>
</s:escapeBody>
```

\=> 해당 내용을 렌더링하면,, \&lt;h1%gt;Hello\&lt;/h1%gt; 로 렌더링 된다 -> 브라우저에서는 \<h1>Hello\</h1>로 다시 렌더링함

&#x20;

#### 아파치 타일즈 뷰로 레아이웃 정의하기

\* 여태까지, 웹페이지의 레이아웃에 대해서는 고려하지 않음. 모든 페이지에 공통적인 헤더와 꼬리말을 추가할 때. 아파치 타일즈와 같은 레이아웃 엔진을 사용하여, 모든 페이지에 적요될 공통 페이지 레이아웃을 정의할 수 있음.

**타일 뷰 리졸버 설정하기**

\* TileConfigurer빈은 타일 정의의 위치를 정하고 ,불러오고 ,배치하는 역할을 한다.&#x20;

\* TileViewResolver빈은 논리적인 뷰 이름으로  타일을 정의한다.

```
    @Bean
    public TilesConfigurer tilesConfigurer() {
        TilesConfigurer tiles = new TilesConfigurer();
        // 타일 정의
        tiles.setDefinitions(new String[] { "/WEB-INF/layout/tiles.xml" });
        // 타일의 위치 명시
        tiles.setCheckRefresh(true); // 리프레시가 가능하게 설정
        return tiles;
    }
```

&#x20;

```
  @Bean
  public ViewResolver viewResolver() {
    return new TilesViewResolver();
  }
```

&#x20;

**타일 정의하기**

\* 아파치 타일즈는 XML 파일에 타일 정의를 명시하기 위한 문서 타입 정의를 제공한다. 각각의 정의는 \<definition> 요소로 구성 되어있고, 하나 이상의 \<put-attribute>를 포함한다.&#x20;

\* 간단하게만 알아보도록 한다

```
... 생략
<definition name="base" template="/WEB-INF/layout/page.jsp">
  <put-attribute name ="header" value="/WEB-INF/layout/header.jsp"/>
  <put-attribute name ="footer" value="/WEB-INF/layout/footer.jsp"/>
</definition>

<definition name="home" extends="base">
  <put-attribute name="body" value="/WEB-INF/views/home.jsp"/>
</definition>
```

\=> 참조되는 템플릿은, page.jsp이고, 기본적으로 헤더는 header.jsp, 꼬리는 footer.jsp를 참조한다.'

\=> home으로 base를 확장했고, body영역은 home.jsp로 사용 가능하다.

&#x20;

해당 내용을 사용하는 page.jsp

```
...생략
<body>
  <div id="header">
    <t:insterAttribute name="header"/> #헤더삽입
  </div>
  <div id="body">
    <t:insterAttribute name="body"/> #바디삽입
  </div>
  <div id="footer">
    <t:insterAttribute name="footer"/> #꼬리말 삽입
  </div>
```

\==> \<t:insertAttribute>를 통해, XML에 정의된 내용을 가져온다. => 따라서, 정의된 헤더, 푸터 + 내가 넣을 바디(base 확장)을 쓸 수 있다

\==> header.jsp, footer.jsp, page.jsp를 공통요소로서, 재사용이 가능하고, 유지보수가 간단해짐.

&#x20;

#### Thymeleaf로 작업하기

\* JSP의 단점(HTML도 아니고, XML도 아닌 모호함, 서블릿 스펙과 강력히 연결)을 보완하기 위해, 나온 템플릿엔진 -> [Thymeleaf란?](https://github.com/ihoneymon/spring-boot-orm-learn/blob/master/THYMELEAF\_TEMPLATE\_ENGINE.md)

\* JSP를 대체하기 위해, 타임리프를 사용할 수 있다. JSP가 사용될수 없는 곳에도 사용 가능하다

&#x20;

**타임리프 뷰 리졸버 설정하기**

\* 타임리프 템플릿 뷰를 정하는 ThymeleafViewResolver

\* 템플릿을 처리하고, 결과를 렌더링하는 SpringTemplateEngine

\* 타임리프 템플릿을 불러오는 TemplateResolver

```
    @Bean
    public ViewResolver viewResolver(SpringTemplateEngine templateEngine) {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        // 스프링 MVC의 ViewReslolver에 관한 구현, 최종적인 뷰는 Thymeleaf 템플릿
        viewResolver.setTemplateEngine(templateEngine);
        return viewResolver;
    }

    @Bean
    public TemplateEngine templateEngine(TemplateResolver templateResolver) {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        // 템플릿을 파싱하고, 해당 템플릿에 기반을 둔 결과를 렌더링하는 타임리프 엔진
        templateEngine.setTemplateResolver(templateResolver);

        return templateEngine;
    }

    @Bean
    public TemplateResolver templateResolver() {
        TemplateResolver templateResolver = new ServletContextTemplateResolver();
        // 최종적으로, 템플릿의 위치를 찾아낸다.
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML5");

        return templateResolver;
    }
```

&#x20;

**타임리프 템플릿 정의하기**

```
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:th="http://www.thymeleaf.org">   #타임리프 네임스페이스 선언          
... 생략
<head>
  <link th:href="@{/resource/style.css}"></link> #스타일 시트에 연결되는 th:href
... 생략
<body> 
  <a th:href="@{/coffees}">Coffees</a> # 페이지에 연결되는 th:href
  
  ...
```

특별한 태그를 사용하지 않음. 다만 사용자가지정한 네임스페이스 표준을 통해, Thymeleaf 애트리뷰트를 추가하는 것으로 동작

th:href빼고는, 일반적인 HTML파일... JSP처럼 특별한 처리과정 없이, 자연스러운 렌더링 및 편집이 가능하다.

&#x20;

**타임리프로 폼 바인딩 하기**

JSP는 \<sf:input>, \<sf:label> 등의, 태그를 통해 폼 바인딩을 하였다. 오류를 나타내기 위해, cssErrorClass도 사용하였다... 이를 Thymeleaf로 대체해본다..

```
<label th:class="${#fields.hasErrors('firstName')}? 'error'"> First Name</label>:
<input type="text" th:field="*{firstName}" th:class="${#fields.hasErrors('firstName')}? 'error'" /><br/>
```

\* cssClassName 애트리뷰트 대신, th:class를, 표준 HTML과 함께 사용. 오류가  있다면 error로 렌더링한다.

&#x20;
