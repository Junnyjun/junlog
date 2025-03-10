# MVC

Spring Framework로 웹 개발을 할 땐 기본적으로 MVC 패턴을 따른다.

MVC 패턴은 Model, View, Controller 이 3가지로 나뉘어 역할을 분할하여 처리한다.

역할을 나누어 처리하기 때문에 서로의 결합도가 낮아져서 좋은 코드가 되며 유지보수도 하기 편해진다.

Spring MVC Framework 구조를 공부하기 앞서 MVC 패턴의 방식과 개념에 대해 간략히 알아보도록 하자.

### MVC 패턴

MVC 패턴은 Model, View, Controller 개념이 합쳐지면서 생긴 방식으로 소프트웨어 공학에서 사용되는 디자인 패턴이다.

<figure><img src="https://blog.kakaocdn.net/dn/ynM2n/btqyD8jSH6R/YVJGZvslTOrzARChhfTxp1/img.png" alt=""><figcaption></figcaption></figure>

처리 순서는 다음과 같다.

```
1. 사용자의 Request를 Controller가 받는다.
2. Controller는 Business Logic을 처리를 Service와 같이 처리한 후 결과를 Model에 담는다.
3. Model에 저장된 결과를 바탕으로 시각처리를 담당하는 View를 제어하여 사용자에게 전달한다.
```

`Controller`:사용자가 접근 한 URL에 따라 요청을 파악한다. URL에 맞는 Method를 호출하여 Service와 함께 Business Logic을 처리한다. 최종적으로 나온 결과는 Model에 저장을 하고, View에 던져준다.

`Model`: Model은 Controller에서 받은 데이터를 저장하는 역할을 한다.

`View` : Controller로 부터 받은 Model 데이터를 바탕으로 사용자에게 표현해준다. 일반적으로 HTML, JSP... 에 해당한다.

MVC 패턴에는 크게 2가지 방식으로 나눠진다.

### Model 1

<figure><img src="https://blog.kakaocdn.net/dn/bPSoZU/btqyDCFxtTo/dKlnKPqIz6NRn3wydUUgvK/img.png" alt=""><figcaption></figcaption></figure>

Model1 방식은 Java파일과 \<Tag>를 HTMl에 모두 작성하여 개발한다. 즉 JSP가 모든 요청을 다 처리한다.

이러한 방식을 통해 개발을 하면 빨리 개발을 할 수 있는 장점이 있다.

그러나 프로젝트 규모가 커질수록 코드가 복잡해져 유지보수가 힘들어지는 단점이 존재한다.

### Model2

<figure><img src="https://blog.kakaocdn.net/dn/blPTBS/btqyBzvIQhg/bAYZmdxgD7mUGCnYiKlku0/img.png" alt=""><figcaption></figcaption></figure>

Model 2 구조는 처리해야할 역할을 Controller, View, Model이 모두 나눠 처리한다.

Controller는 RequestMapping을 통해 URL을 확인하여 바로 View에 던져줄지, Service로 들어가 추가적인 Business Login을 할지 결정한다. 이렇게 역할을 나눔으로써 HTML과 Java를 분리하여 처리하기 때문에 Model 1에 비해 확장성도 좋고 유연하며 유지보수도 하기 쉬워진다.

### Spring MVC Framework

Spring MVC Framework는 MVC 패턴을 따르면서 Spring 만의 독자적인 Class를 통해 처리를 한다.

동작 순서는 다음과 같다.

1. Client로부터 요청이 들어오면 DispatcherServlet이 호출된다.
2. DispatcherServlet은 받은 요청을 HandlerMapping에게 던져준다. 요청받은 URL을 분석하여 HandlerMapping 적합한 Controller를 선택하여 반환한다.
3. DispatcherServlet는 다음으로 HandlerAdapter를 호출한다. HandlerApdater는 해당하는 Controller 중 요청한 URL에 맞는 적합한 Method를 찾아준다.
4. Controller는 Business Logic을 처리하고, 해당하는 결과를 View에 전달할 객체를 Model에 저장한다.
5. Controller는 View name을 DispatcherServlet에게 리턴한다.
6. DispatcherServlet은 ViewResolver를 호출하여 Controller가 리턴한 View name을 기반으로 적합한 View를 찾아준다.
7. DispatcherServlet은 View 객체에 처리결과를 넘겨 최종 결과를 보여주도록 요청한다.
8. View 객체는 해당하는 View를 호출하며, View는 Model 객체에서 화면 표시에 필요한 객체를 가져와 화면 표시를 처리하고 Client에게 넘겨준다.

이러한 설정은 web.xml에 기술되어 있다.

Spring Project를 생성하면 src 밑에 web.xml, servlet-context.xml, root-context.xml이 존재한다.

ContextLoaderLoaderListener와 DispatcherServlet은 각자 WebApplicationContext를 생성한다. ContextLoaderListener가 생성한 Context가 Root Context가 되고 DispatcherServlet이 생성한 인스턴스는 Root Context를 부모로 하는 자식 Context가 된다.

자식 Context들은 Root Context의 설정 Bean을 사용할 수 있다.\
따라서 ContextLoaderListener를 공통으로 사용하는 Bean을 설정한다.

이러한 context의 특징을 알고 Bean을 설정할 때 component-scan을 통한 등록을 주의해야한다.

양쪽 Context설정에서 component scan을 통해 Bean을 등록할 때 controller와 service, reposity의 각 Bean들이 등록되어야 하는데 context에 맞게 등록이 되도록 해야한다.

단순히 base-package에 대한 설정만으로 Scan을 하면 같은 Bean이 양쪽 context에 모두 등록되어 불필요한 Bean등록이 발생한다.

따라서 component scan을 통한 각 context에 대한 Bean 등록시 exclude, include를 적절히 사용하여 불필요한 중복 Bean이 등록되지 않도록 해야한다.

<figure><img src="https://blog.kakaocdn.net/dn/z4j6X/btqyBzWRGmf/KckWH9keSUSUOXXywOanMK/img.png" alt=""><figcaption></figcaption></figure>
