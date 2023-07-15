# 원격 서비스

### 1. 스프링 리모팅 개요

리모팅 : 클라이언트 애플리케이션과 서비스 간의 대화. 클라이언트 측에서 기능이 필요하면, 애플리케이션이 그 기능을 제공 가능한 다른 시스템에 접촉을 시도

다른 애플리케이션과 서비스의 통신은, 클라이언트에서 호출하는 원격 프로시저 호출(RPC, Remote Procedure Call)로 시작 됨. 표면적으로 메소드 호출과 유사함.

<figure><img src="https://blog.kakaocdn.net/dn/bag9CO/btqAy0Z4h5E/4tVKgnx6UWeKNK7iOCpkfk/img.png" alt=""><figcaption><p>Spitter라는 서비스에서, 서드파티 클라이언트와 상호작용</p></figcaption></figure>

로컬메소드 호출과는, 인접성에서 차이점을 보임 -> 근거리(대화) vs 원거리(전화통화)



스프링은 여러 RPC 모델에 대해, 리모팅을 지원해줌

<figure><img src="https://blog.kakaocdn.net/dn/baMrUi/btqAz7KX7FQ/n3sgY0mAqYUiDHluIOk9V0/img.png" alt=""><figcaption><p>스프링이 지원해주는 RPC모델</p></figcaption></figure>

\* 해당 RPC모델에 관계 없이, 모든 모델에 대해 지원 기능에 한 가지 공통된 테마가 있음(템플릿과 유사)

<figure><img src="https://blog.kakaocdn.net/dn/dTcTVg/btqAyF2UotH/7DUJnJ6SReqyOgrpSMrZ7k/img.png" alt=""><figcaption><p>스프링에서 원격서비스는 마치 다른 스프링빈인것처럼 와이어링 될 수 있도록 프록시 생성</p></figcaption></figure>

클라이언트는 마치, 프록시가 해당 서비스를 제공하는 것처럼 호출한다. 그러면 프록시가 클라이언트를 대신하여 원격 서비스와 통신. -> 그 이후 연결에 관련된  세부사항을 처리하고, 원격 서비스를  호출

호출의 결과로 RemoteException이 발생하면, 예외를 처리하여 프록시는, 비검사형인 RemoteAccessException으로 던짐. 따라서 클라이언트는 강제적이 아니라, 선택적으로 예외를 처리할 수 있다.&#x20;

<figure><img src="https://blog.kakaocdn.net/dn/bIlkES/btqAz6LYX7m/5AYdkvGrtNq1bq141DYM00/img.png" alt=""><figcaption><p>원격 익스포터를 이용해 원격 서비스로 익스포트 됨</p></figcaption></figure>

원격 서비스를 소비하는 코드를 개발하거나, 구현하는 코드를 개발하든... 원격 서비스를 이용하는 작업은 단순히 설정상의 문제 -> 리모팅을 위해 자바 코드를 작성할 필요가 없음. 그리고 서비스 빈은 그 빈이 RPC에 참여하는지도 몰라도 괜찮다.



#### &#x20;

#### 2. RMI 활용

RMI(Remote Mehod Invocation, 원격 메소드 호출) : 자바 프로그램 간의 통신을 수행하는 수단

\* 과거에는 RMI서비스를 개발하고, 액세스 작업또한 거쳐야 했음. 하지만 스프링은 RMI를 로컬 JavaBeans인 것처럼 연결 해주는 프록시 팩토리 빈을 제공하여 RMI모델을 단순화함



**2.1 RMI 서비스 익스포트**

\- 기존 RMI의 개발작업을 단순화(5단계의 복잡한 방법)

스프링에서 RMI 서비스 구성하기

RemoteException(전통적인 방법)을 던지는 메소드를 갖는 클래스를 작성하는 대신, 서비스의 기능을 수행하는 POJO만 작성하면 됨

```
public interface SpitterService {
          List<Spittle> getRecentSpittles(int count);
          void saveSpittle(Spittle spittle);
          void saveSpitter(Spitter spitter);
          Spitter getSpitter(long id);
          void startFollowing(Spitter follower, Spitter followee);
          List<Spittle> getSpittlesForSpitter(Spitter spitter);
          List<Spittle> getSpittlesForSpitter(String username);
          Spitter getSpitter(String username);
          Spittle getSpittleById(long id);
          void deleteSpittle(long id);
          List<Spitter> getAllSpitters();
}
```

\* 스프링의 RmiServiceExporter를 사용하면, 해당 클래스의 메소드들에 RemoteException을 던져주지 않아도 됨

```
@Bean
public RmiServiceExporter rmiExporter(SpitterService spitterService) {
  RmiServiceExporter rmiExporter = new RmiServiceExporter();
  rmiExporter.setService(spitterService);
  rmiExporter.setServiceName("SpitterService");
  rmiExporter.setServiceInterface(SpitterService.class);
  return rmiExporter;
}
```

\* 빈을 어댑터 클래스 안에 래핑하는 방식으로 작동 -> SpitterService -> 이 어댑터 클래스는, 바인딩된 서비스(SpitterServicelmpl)에 대한 요청을 프록시함

\* 기본적 로컬 머신의 1099포트에 레지스트리 바인드를 시도

```
@Bean
public RmiServiceExporter rmiExporter(SpitterService spitterService) {
  RmiServiceExporter rmiExporter = new RmiServiceExporter();
  rmiExporter.setService(spitterService);
  rmiExporter.setServiceName("SpitterService");
  rmiExporter.setServiceInterface(SpitterService.class);
  rmiExporter.setRegistryHost("rmi.spitter.com");
  rmiExporter.setRegistryPort(1199);
  return rmiExporter;
}
```

\* 바인드하는 레지스트리 포트를 변경하는 방법(1199)

<figure><img src="https://blog.kakaocdn.net/dn/c0iuuK/btqACHj993U/orHvr3HHCgtxtT7DT9DRq1/img.png" alt=""><figcaption><p>RmiSerivceExporter는 POJO를 어댑터 안에, 래핑 후 그 어댑터를 RMI레지스트리에 바인딩한다.</p></figcaption></figure>



**2.2 RMI 서비스 와이어링**

전통적인 방법으로, RMI 레지스트리에서 서비스를 검색하려면 API Naming 클래스를 사용해야함

```
try {
      String serviceUrl = "rmi:/spitter/SpitterService";
      SpitterService spitterService = (SpitterService) Naming.lookup(serviceUrl);
... }
catch (RemoteException e) { ... }
catch (NotBoundException e) { ... }
catch (MalformedURLException e) { ... }
```

\==> 각 Exception이 치명적이며, 회복 불가능하다. 이러한 예외가 나올경우, 사실상 어플리케이션을 재시작하여야함. try/catch를 사실상 할 필요가 없다...

\==> DI를 정면으로 위반한다. 이 서비스는 RMI서비스이기에, 다른 서비스에서 구현을 제공할 기회가 없음. 고로 객체도 주입할 수없다.&#x20;

\==> 스프링은 이러한 단점을 극복할 수 있는 RmiProxyFactoryBean으로 RMI 서비스에 대한 프록시를 생성하는 팩토리빈을 제공

```
@Bean
public RmiProxyFactoryBean spitterService() {
  RmiProxyFactoryBean rmiProxy = new RmiProxyFactoryBean();
  rmiProxy.setServiceUrl("rmi://localhost/SpitterService");
  rmiProxy.setServiceInterface(SpitterService.class);
  return rmiProxy;
}
```

\* 서비스 URL은 RmiProxyFactoryBean의 serviceUrl 프로퍼티를 통해 설정

<figure><img src="https://blog.kakaocdn.net/dn/Nrb9P/btqABRUTdBx/QgZK18GRshVUHERK7JzDe1/img.png" alt=""><figcaption><p>Rmi서비스로 통하는, 프록시 객체를 생성. 클라이언트는 해당 프록시를 통해, 서비스가 마치 로컬 POJO인 것처럼 통신한다</p></figcaption></figure>

&#x20;

스프링 관리 빈으로 RMI서비스를 선언했으므로, 로컬빈처럼 다른 빈에 종속객체로 연결됨

```
@Autowired // 서비스 프록시를 클라이언트에 연결
SpitterService spitterService;
```

```
public List<Spittle> getSpittles(String userName) {
  Spitter spitter = spitterService.getSpitter(userName);
  return spitterService.getSpittlesForSpitter(spitter);
}
```

해당 코드처럼 로컬 빈처럼 연결 가능하다

\=> 장점은, 클라이언트 코드가 RMI 서비스를 처리한다는 것을 아예 몰라도 되며, 단지 주입된 Serivce 객체만을 받을 뿐



RMI는 원격 서비스 통신으로는 훌륭하지만 여러 단점이 있다.

* 방화벽을 넘어 작업하는 환경에서는 한계가 있음(대체로 임의 포트를 사용)...
* 인트라넷은 상관없지만, 인터넷상에서는 문제가 된다(터널링 작업이 까다로움)
* RMI는 자바기반이기 때문에, 클라이언트 서비스 둘 다 자바로 작성해야함(자바의 직렬화를 사용하므로, 양쪽의 객체타입이 동일해야함)

\===> 이러한 단점을 해결하기 위해 Hessian과 Burlap 을 이용한다.



#### &#x20;

#### 3. Hessian과 Burlap을 이용한 리모트 서비스 노출

Hessian, Burlap은 Http를 통해, 가벼운 원격서비스를 가능케 한다. 웹서비스 단순화를 목표로 함

* Hessian : RMI와 유사하게, 클라이언트와 서비스간에, 바이너리 메시지를 이용해 통신함 - PHP, Python, C++등 자바 외의 언어에 이식됨
* Burlap : XML기반 리모팅 기술, XML을 파싱 가능한 언어라면, 자동적으로 이식 가능하다. 메시지 구조가 매우 간단

\-> 두가지 기술은 대부분 동일함. 바이너리메시지와, XML의 차이이다.



**3.1 Hessian과 Burlap을 이용한 빈 기능 노출**

Hessian 서비스 익스포트

스프링에서 Hessian 서비를 익스포트하는 것은, RMI와 비슷함. HessianServiceExporter를 사용하면 됨 => 서비스에 대해서는 RmiServiceExporter와 동일한 기능 ==> 그러나, 방식은 약간 다르다

<figure><img src="https://blog.kakaocdn.net/dn/dqqIVs/btqABC4WBDK/7KGHluWkZXwW6TCViKeTp0/img.png" alt=""><figcaption><p>익스포터를 통해, 서비스 메소드를 바로 호출</p></figcaption></figure>

HessianServiceExporter는 Hessian의 요청을 받아, POJO의 메소드 호출로 변환하는 스프링 MVC 컨트롤러

```
@Bean
public HessianServiceExporter hessianExportedSpitterService(SpitterService service) {
  HessianServiceExporter exporter = new HessianServiceExporter();
  exporter.setService(service); // * RmiSerivceExporter와 마찬가지로, 서비스로 구현하는 빈에 대한 레퍼런스가 연결 
  exporter.setServiceInterface(SpitterService.class);
  return exporter;
}
```

\* 레지스트리를 갖지 않으므로, serviceName 프로퍼티는 필요가 없음



Hessian 컨트롤러 구성하기

RMI와 Hessian의 주된 차이점은, Hessian이 HTTP기반이기에, HessianServiceExporter가 스프링 MVC로 구현된다는 점.. 따라서 두가지의 설정이 필요하다.

\* DispatcherServlet 설정 -> Hessian 서비스를 URL을 적젌한 서비스 빈으로 디스패치하도록 URL 핸들러 설정

```
/* WebApplicationInitalizer 사용 시 */

ServletRegistration.Dynamic dispatcher = container.addServlet(
        "appServlet", new DispatcherServlet(dispatcherServletContext));
    dispatcher.setLoadOnStartup(1);
    dispatcher.addMapping("/");
    dispatcher.addMapping("*.service");  // Hessian서비스를 처리하기 위해 *.service URL패턴 서블릿 매핑 추가
    
    
/* AbstractDispatcherServletInitializer 또는 AbstractAnnotationConfigDispatcherServletInitializer 사용 시 */

@Override
protected String[] getServletMappings() {
  return new String[] { "/", "*.service" }; // URL패턴 서블릿 매핑 추가
} 
```

\==> Spittle.service에 관한 매핑은 궁극적으로 hessianSpittleService빈(SpittleServiceImpl의 프록시)에 의해 처리

```
@Bean
public HandlerMapping hessianMapping() {
  SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
  Properties mappings = new Properties();
  mappings.setProperty("/spitter.service",
                       "hessianExportedSpitterService");
  mapping.setMappings(mappings);
  return mapping;
}
```

\* 해당 SimpleUrlHandlerMaping이 실제 URL매핑을 처리하게 된다.



Burlap 서비스 익스포트

BurlapServiceExporter는 XML을 처리하는 것 빼고는 Hessian과 모든 측면에서 동일

```
@Bean
public BurlapServiceExporter burlapExportedSpitterService(SpitterService service) {
    BurlapServiceExporter exporter = new BurlapServiceExporter();
    exporter.setService(service);
    exporter.setServiceInterface(SpitterService.class);
    return exporter;
}
```

\* 유일한 차이점은, 빈의 메소드와, exporter 클래스 ===> 컨트롤러 설정은 동일하다(생략)



**3.2 Hessian/Burlap 서비스에 액세스하기**

RmiProxyFactoryBean처럼 프록시를 이용하여, Spitter 서비스를 소비하는 클라이언트 코드가 Hessian/Burlap인지 모르게 할 수 있다.

HessianProxyFactoryBean / BurlapProxyFactoryBean 사용

```
/* Hessian */

@Bean
public HessianProxyFactoryBean spitterService() {
    HessianProxyFactoryBean proxy = new HessianProxyFactoryBean(); 
    proxy.setServiceUrl("http://localhost:8080/Spitter/spitter.service"); 
    proxy.setServiceInterface(SpitterService.class);
    return proxy;
}

/* Burlap */
@Bean
public BurlapProxyFactoryBean spitterService() {
    BurlapProxyFactoryBean proxy = new BurlapProxyFactoryBean(); 
    proxy.setServiceUrl("http://localhost:8080/Spitter/spitter.service"); 
    proxy.setServiceInterface(SpitterService.class);
    return proxy;
}
```

<figure><img src="https://blog.kakaocdn.net/dn/q17U3/btqABr3NJMx/ZbtGgk6au0p21qXJoelhI1/img.png" alt=""><figcaption><p>팩토리빈은 클라이언트가 HTTP요청으로, 원격서비스와 통신하는 프록시 객체를 생성한다</p></figcaption></figure>



\* Hessian/Burlap은 Http를 기반으로 하므로, 방화벽 문제를 겪지 않음

\* 하지만 복잡한 데이터모델의 경우, 직렬화 모델이 충분치 않을  수 있음(RMI가 우위)

\==> RMI(자바의 직렬화 사용)와 Hessian/Burlap(방화벽 문제 없음)의 장점을 섞은 스프링의 HTTP호출자가 존재



#### &#x20;

#### 4. 스프링의 HttpInvoker 사용하기

방화벽을 가로질러 사용 + 독자적인 객체 직렬화 매커니즘 => 스프링 Http 호출자(invoker) => HttpInvoker



**4.1 빈을 HTTP 서비스로 익스포트**

\* HttpInvokerServiceExporter를 이용

```
@Bean
public HttpInvokerServiceExporter httpExportedSpitterService(SpitterService service) {
  HttpInvokerServiceExporter exporter = new HttpInvokerServiceExporter();
  exporter.setService(service);
  exporter.setServiceInterface(SpitterService.class);
  return exporter;
}
```

\* 동작 자체도, HessianServiceExporter와 유사하다(스프링 MVC 컨트롤러)

<figure><img src="https://blog.kakaocdn.net/dn/dxrOTw/btqAB3BbKRx/iSUXLaR4pg0hXC92QEk2gK/img.png" alt=""><figcaption></figcaption></figure>

\==> 따라서, DispatcherServlet 매핑과, URL 핸들러를 설정해주어야 함 ==> 3.1의 컨트롤러 설정과 동일(생략)



**4.2 HTTP를 거쳐 서비스에 액세스하기**

\* 놀랍게도... 이 부분도 Hessian/Burlap과 거의 동일

<figure><img src="https://blog.kakaocdn.net/dn/ckJAGy/btqABrW2CLu/e8CpECmZRQfJiMzdSoGBMk/img.png" alt=""><figcaption></figcaption></figure>

... 프록시 팩토리 빈 설정(동일)

```
@Bean
public HttpInvokerProxyFactoryBean spitterService() {
  HttpInvokerProxyFactoryBean proxy = new HttpInvokerProxyFactoryBean(); 
  proxy.setServiceUrl("http://localhost:8080/Spitter/spitter.service"); 
  proxy.setServiceInterface(SpitterService.class);
  return proxy;
}
```



스프링 HttpInvoke는, Http통신의 단순함과, 자바에 내장된 객체 직렬화를 결합하여, 두가지의 장점을 모은 리모팅 솔루션

\=> 양쪽 모두가, 스프링 프레임워크를 사용해야 가능...&#x20;

\==> 앞에 서술한 모든 리모팅 모델은, 유비쿼터스 리모팅에 관한 웹서비스에는 사용 불가능하다....&#x20;

\===> 스프링에는 SOAP기반 웹서비스를 통해 리모팅을 구현 할 수 있다(처음부터 이걸 알려주던가..)



&#x20;

#### 5. 웹서비스 발행과 소비

SOA(서비스 지향 아키텍쳐) : 각 애플리케이션마다 동일한 기능을 구현하는 대신, 공통 된 핵심 서비스에 의거하도록 설계.

JAX-WS : 웹서비스를 생성하는 JAVA-API.. 어노테이션을 사용하여 쉽게 서버 클라이언트 및 서버 모듈의 개발 및 배포를 담당한다. 발전된 자바 진영의 노력의 산물

자바와 웹서비스는 오랜시간 다양한 옵션을 적용하여 사용 가능하게 개발됐음.&#x20;

스프링에서는, 일반적으로 알려진 XML 웹서비스나 JAX-XS를 이용하여, SOAP 웹서비스를 발행하고 소비 가능



**5.1 스프링을 사용할 수 있는 JAX-WS 엔드포인트 생성**

스프링은 JAX-WS 서비스 익스포터인 SimpleJaxWsServiceExporter를 제공한다.

2,3,4절의 방법는 사뭇 다름

다만, 이 방법이 모든 상황의 최선은 아님을 알아야한다. SimpleJaxWsServiceExporter는 JAX-WS 런타임이 특정한 주소에 대한 endpoint의 배포를 지원할 것을 요구한다(JDK 1.6 이상만 가능)



스프링에서의 JAX-WS 엔드포인트 오토와이어링

@WebService : 웹서비스 엔드포인트

@WebMethod : 웹 메소드 작업

\- SpringBeanAutowiringSupport를 상속하여, 엔드포인트 프로퍼티에 @Autowired 애너테이션을 적용하여, 종속성을 주입해야한다(그러면, JAX-WS 엔드포인트가 DI의 혜택을 받을 수 있다)

\- 스프링에 의해 관리되는 생명주기가 없을 때 사용하기 유용함.

```
@WebService(serviceName="SpitterService")
public class SpitterServiceEndpoint extends SpringBeanAutowiringSupport { // 오토 와이어링 활성화
  
  @Autowired
  SpitterService spitterService; // 서비스 오토와이어링
  
  @WebMethod
  public void addSpittle(Spittle spittle) {
    spitterService.saveSpittle(spittle); // SpitterService에 위임
  }
  
  @WebMethod
  public void deleteSpittle(long spittleId) {
    spitterService.deleteSpittle(spittleId); // SpitterService에 위임
  }
  
  @WebMethod
  public List<Spittle> getRecentSpittles(int spittleCount) {
    return spitterService.getRecentSpittles(spittleCount); // SpitterService에 위임
  }
  
  @WebMethod
  public List<Spittle> getSpittlesForSpitter(Spitter spitter) {
    return spitterService.getSpittlesForSpitter(spitter); // SpitterService에 위임
  }
}
```

\* 오토와이어링 활성화, Service 위임



**독립형 JAX-WS 엔드포인트 익스포트**

\* 위의 SpringBeanAutowiringSupport는, 프로퍼티가 주입된 객체가 스프링에 의해 관리되는 생명주기가 없을 때 유용

\* SimpleJaxWsServiceExporter는 스프링에서 관리하는 빈을 익스포트 할 때 사용. 15장 전체에 살펴본 다른 익스포터와 유사한 방식으로 동작. 다만 해당 익스포터는, 익스포트하는 빈에 대한 레퍼런스를 부여하지 않고, 애너테이션으로 대체된다.

```
@Bean
public SimpleJaxWsServiceExporter jaxWsExporter() {
  return new SimpleJaxWsServiceExporter();
}
```

\* 별다른 작업이 필요 없음. 스프링 애플리케이션이 시작될 때, @WebService 애너테이션이 적용된 빈을 찾는다

```
@Component
@WebService(serviceName="SpitterService") // 기본주소와함께 , JAX-WS 엔드포인트로 발행
public class SpitterServiceEndpoint { // 엔드포인트로 변환
  @Autowired
  SpitterService spitterService;
  
  @WebMethod
  public void addSpittle(Spittle spittle) {
    spitterService.saveSpittle(spittle);
  }
  
  @WebMethod
  public void deleteSpittle(long spittleId) {
    spitterService.deleteSpittle(spittleId);
  }
  
  @WebMethod
  public List<Spittle> getRecentSpittles(int spittleCount) {
    return spitterService.getRecentSpittles(spittleCount);
  }
  
  @WebMethod
  public List<Spittle> getSpittlesForSpitter(Spitter spitter) {
    return spitterService.getSpittlesForSpitter(spitter);
  }
}
```

\* 완전한 기능의 스프링 빈으로, 어떤 특별 지원 클래스 상속 없이도 오토와이어링 가능

\* 기본적으로 http://localhost:8080/SpitterService에 있는 웹서비스이다.

\==> 주소를 바꾸고 싶다면?

```
 @Bean
public SimpleJaxWsServiceExporter jaxWsExporter() {
    SimpleJaxWsServiceExporter exporter = new SimpleJaxWsServiceExporter();
    exporter.setBaseAddress("http://localhost:8888/services/");
}
```

\-> 기본 주소를 바꿔 줄 수 있다



**5.2 클라이언트 측에서 JAX-WS 프록시하기**

\* 스프링을 이용한 웹 서비스 발행 방법은, 다른 리모팅 모델의 방법과 달랐다. 하지만, 클라이언트 측 프록시의 경우에는 크게 다르지 않다.

<figure><img src="https://blog.kakaocdn.net/dn/c2oZeU/btqAB39qnzi/CtVRoJZYTy0m711MxK7yD0/img.png" alt=""><figcaption><p>위의 모델들과 거의 동일함을 볼 수  있다</p></figcaption></figure>

위의 그림과 같이, JaxWsPortProxyFactoryBean을 설정해준다.

```
@Bean
public JaxWsPortProxyFactoryBean spitterService() {
  JaxWsPortProxyFactoryBean proxy = new JaxWsPortProxyFactoryBean();
  proxy.setWsdlDocument("http://localhost:8080/services/SpitterService?wsdl");
  proxy.setServiceName("spitterService");
  proxy.setPortName("spitterServiceHttpPort");
  proxy.setServiceInterface(SpitterService.class);
  proxy.setNamespaceUri("http://spitter.com");
  return proxy;
}
```

\* 동작하기 위해선, 몇가지 프로퍼티를 설정해야 한다

\* wsdlDocument -> 웹 서비스의 정의 파일이 있는 위치.../// 나머지 세 값은, WSDL파일에 따라 결정

&#x20;

\* WSDL파일 예시

```
<wsdl:definitions targetNamespace="http://spitter.com">
...
  <wsdl:service name="spitterService">
    <wsdl:port name="spitterServiceHttpPort"
            binding="tns:spitterServiceHttpBinding">
...
    </wsdl:port>
  </wsdl:service>
</wsdl:definitions>
```

\==> 다수의 서비스 혹은 포트를 정의한다 ==> wdl:port, wsdl:service 등.. nameSpacesuri의 경우, wsdl:definition의 targetNameSpace에서 찾아 볼 수 있다.
