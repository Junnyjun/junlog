# JMX

스프링의 DI는 빈 프로퍼티를 설정하는 방법중 하나이다. 그러나 일단 배포가 돼서 실행중이면, DI만으로는 설정을 변경할 수 없다.

\* 이 때 실행중인 앱의 설정을 바꾸기 위해, JMX(Java Management Extenstison)을 사용할 수 있다.

관리빈(Management Bean, 이하 MBean) : JMX를 이용한 관리 목적에 특화되어 있는 구성요소. 관리 인터페이스를 정의하는 메소드를 노출하는 자바 빈 => 네가지 타입의 MBean이 정의

* 표준 MBean : 고정된 자바 인터페이스의 리플렉션에 의해 관리 인터페이스가, 결정되는 MBean
* 동적 MBean : 실행시에 DynamicMBean 인터페이스의 메소드 호출에 의해 관리 인터페이스가 결정, 실행시마다 달라질 수 있음
* 오픈 MBean : 특별한 동적MBean으로, 애트리뷰트와 오퍼레이션이 프리미티브 타입, 프리미티브 타입용 클래스 래퍼, 프리미티브나 프리미티드 래퍼로 분해될 수 있는 타입으로 제한
* 모델 MBean : 관리 인터페이스를 관리 리소스로 넘기는 특이한 동적MBean. 선언되는만큼 작성되지는 않음. 일반적으로 메타정보를 이용하여 관리 인터페이스를 조립하는 팩토리에 의해 만들어짐

\=> 스프링의 JMX 모듈을 이용하여, 스프링 애플리케이션의 빈을 관리할 수 있다

***

### 1 스프링 빈을 MBean으로 익스포트하기

JMX를 사용하기 위해, 프로퍼티를 추가

```
public static final int DEFAULT_SPITTLES_PER_PAGE = 25; // 실행시점에 변경할 프로퍼티
private int spittlesPerPage = DEFAULT_SPITTLES_PER_PAGE; 

public void setSpittlesPerPage(int spittlesPerPage) { // 빌드 시점에 JMX를 이용하여 실행할 메소드
  this.spittlesPerPage = spittlesPerPage;
}

public int getSpittlesPerPage() {
  return spittlesPerPage;
}
```

\=> 이것만으로,  외부에서 설정은 불가능, 다른 프로퍼티와 마찬가지로 빈의 프로퍼티일 뿐

\=> Controller 빈을, MBean으로 노출해야 함 => 그러면 프로퍼티가 관리 애트리뷰트로 노출

```
// MBeanExporter로, MBean 서버에서 스프링 관리 빈을, 모델MBean으로 익스포트
@Bean
public MBeanExporter mbeanExporter(SpittleController spittleController) {
  MBeanExporter exporter = new MBeanExporter();
  Map<String, Object> beans = new HashMap<String, Object>();
  beans.put("spitter:name=SpittleController", spittleController); // Cotroller를, MBean으로 노출
  exporter.setBeans(beans);
  return exporter;
}
```

\==> 이 경우의 MBean - pair.spitter:name=SpittleController

<figure><img src="https://blog.kakaocdn.net/dn/cl37kI/btqAXRBmJoU/twjh6syCNyQN89rpOdePm0/img.png" alt=""><figcaption><p>JMX Bean으로, 익스포트파면 JConsole등의 JMX 기반 관리도구를 이용하여, 내부를 들여다보며 프로퍼티 메소드를 호출한다</p></figcaption></figure>

<figure><img src="https://blog.kakaocdn.net/dn/cFwEJN/btqAYpkkB0F/xKckbHnlXItYCSGWiSDJxK/img.png" alt=""><figcaption></figcaption></figure>

\=> MBeanExporter 설정 후, SpittleController가 관리 서버에 모델MBean으로 익스포트 됨. 모든 public 멤버는 MBean 오퍼레이션과 애트리뷰트로 익스포트.&#x20;

\=> 하지만  spittlesPerPage 프로퍼티 설정을 제외하고는, 굳이 다른 부분을 볼 필요가 없다(spittles, getSpittlesPerPage emd)

\=> 이러한 MBean의 애트리뷰트와 오페레이션 제어를 위한 몇가지 옵션이 있음&#x20;

* 이름을 사용하여 노출, 무시하는 빈 메소드 선언
* 인터페이스로 구성하여 노출 메소드  선택
* 빈의 애너테이션을 적용하여 관리 애트리뷰트와 오퍼레이션 지정

***

#### 1.1 이름으로 메소드 노출시키기

MBean 인포 어셈블러 : MBean에서 익스포트 시킬 오퍼레이션과, 애트리뷰트를 제한하는데 핵심적인 역할

\=> MethoodNameBasedMBeanInfoAssembler&#x20;

자바빈  규칙에 의해, spittlesPerPage 프로퍼티는 set\~(설정자), get\~(접근자)를 가짐, MBean의 노출을 제한하기위해, 어셈블러에게 인터페이스에 있는메소드만 포함하라고 알려주어야 함

```
// 어셈블러에, 관리할 메소드만 등록(set,get)
@Bean 
public MethodNameBasedMBeanInfoAssembler assembler() {
  MethodNameBasedMBeanInfoAssembler assembler = new MethodNameBasedMBeanInfoAssembler();
  assembler.setManagedMethods(new String[] {"getSpittlesPerPage", "setSpittlesPerPage" }); // 관리할 메소드를 선택
  return assembler;
}


// 익스포터에, 해당 어셈블러 등록
@Bean
public MBeanExporter mbeanExporter(SpittleController spittleController, MBeanInfoAssembler assembler) {
  MBeanExporter exporter = new MBeanExporter();
  Map<String, Object> beans = new HashMap<String, Object>();
  beans.put("spitter:name=SpittleController", spittleController);
  exporter.setBeans(beans);
  exporter.setAssembler(assembler); // 어셈블러 등록
  return exporter;
}
```

\=> 위 사진에 있던 spittles()가 노출되지 않게 된다.

<figure><img src="https://blog.kakaocdn.net/dn/dxQw1N/btqAXoGiBYh/AdJtfXsEwfhQHSKgaZIqik/img.png" alt=""><figcaption></figcaption></figure>

\* 또 다른 메소드 이름 기반 어셈블러로는, MethodExclusionMBeanInfoAssembler가 있음&#x20;

\=> 위의 MethodNamedBasedMBeanInfoAssembler와는 반대로, 드러내지 않는 메소드의 목록을 부여함

```
@Bean
public MethodExclusionMBeanInfoAssembler assembler() {
  MethodExclusionMBeanInfoAssembler assembler = new MethodExclusionMBeanInfoAssembler();
  assembler.setIgnoredMethods(new String[] {"spittles"});
  
  return assembler;
}
```

\* 동작은 위의 사진과 같다(spittles를 제한했기 때문)

***

#### 1.2 인터페이스를 이용한 MBean 오퍼레이션과 애트리뷰트 정의

InrterfaceBasedMeanInfoAssembler는, 인터페이스를  이용해 MBean 관리 오퍼레이션으로 익스포트할 빈의 메소드를 선택하는 어셈블러, 인터페이스를 나열한다는 점 제외하고는 MethodNamedBasedMBeanInfoAssembler와 유사하다

```
// 인터페이스
public interface SpittleControllerManagedOperations {
  int getSpittlesPerPage();
  void setSpittlesPerPage(int spittlesPerPage);
}

// 인터페이스 기반의 어셈블러 => 위의 인터페이스를 관리하게 설정
@Bean
public InterfaceBasedMBeanInfoAssembler assembler() {
  InterfaceBasedMBeanInfoAssembler assembler =  new InterfaceBasedMBeanInfoAssembler();
  assembler.setManagedInterfaces(new Class<?>[] { SpittleControllerManagedOperations.class });
  return assembler;
}
```

\* 인터페이스를 기준으로, MBean을 관리할 수 있게 된다.

\* 인터페이스로 관리를 하면, 수십 개의 메소드도 몇 개의 인터페이스로 합칠 수 있으며, 복수의 MBean을 익스포트하는 경우에도 스프링 설정이 깔끔하게 유지된다

\* 그러나, 관리 오퍼레이션의 선언은 코드에서 중복으로 나타난다(단순히 MBeanExporter를 위해) => 이와 같은 중복을 제거하는 방법은 애너테이션을 사용하느 것이 좋다

***

#### 1.3 애너테이션 주도의 MBean을 이용한 작업

MetadataMBeanIfoAssembler : 애너테이션을  이용해, 오퍼레이션과 애트리뷰트를 설정할 수 있는 인포 어셈블러

\=> 해당 어셈블러를 수동으로 와이어링 하는 작업은 부담스럽고, 단지 애너테이션을 위해 그런 작업을 할 필요가 없음

\=> 간단한 방법 제공, context 설정 네임스페이스이용(사용하던 MBeanExporter대신)

```
<context:mbean-export server="mbeanServer" />
```

\=> 이제 스프링 빈을 변환, @ManagedResource를 적용하고, 메소드는 @ManagedOperation, @ManagedAttribute 적용

```
@Controller
@ManagedResource(objectName="spitter:name=SpittleController") // MBean으로 익스포트
public class SpittleController {

  @ManagedAttribute  // 관리 애트리뷰트
  public void setSpittlesPerPage(int spittlesPerPage) {
    this.spittlesPerPage = spittlesPerPage;
  }
  
  @ManagedAttribute // 관리 애트리뷰트
  public int getSpittlesPerPage() {
    return spittlesPerPage;
  }
}
```

\=>  @ManagedAttribute를 get에다만 적용하면, JMX에서 spittlePerPage는 읽기전용 프로퍼티가 됨

\=>  @ManagedAttribute를 적용하면, spittlePerPage 프로퍼티도, 관리 애트리뷰트로 노출시킴 => 만약 프로퍼티 자체(spittlesPerPage)를 노출시키고 싶지 않다면, @ManagedOPeration 어노테이션을 사용한다

&#x20;

***

#### 1.4 MBean 충돌 처리

\* 만약 같은 이름의 MBean이 이미 존재하여, 충돌이 발생한다면 어떻게 해야할까? => 기본적으로, MBeanExporter는 InstanceAlreadyExistException을 던짐

\=> 하지만, 익스포터의 registrationBehaviorName 프로퍼티나, \<contex:mbean=export>의 registration 애트리뷰트를 이용하여, 충돌을 처리할 수 있다

```
@Bean
public MBeanExporter mbeanExporter(SpittleController spittleController, MBeanInfoAssembler assembler) {
  MBeanExporter exporter = new MBeanExporter();
  Map<String, Object> beans = new HashMap<String, Object>(); beans.put("spitter:name=SpittleController", spittleController); exporter.setBeans(beans);
  exporter.setAssembler(assembler); 
  exporter.setRegistrationPolicy(RegistrationPolicy.IGNORE_EXISTING); // 충돌을 무시하고, 새 빈을 등록하지 않음
  return exporter;
}
```

* FAIL\_ON\_EXISTING : 실패 (default)
* IGNORE\_EXISTING : 충돌 무시하고, 새 빈 등록하지 않음
* REPLACING\_EXISTING : 기존 빈을, 새로운 빈으로 대체

***

### 2. MBean 리모팅

원격 JMX의 표준이 필요해지며, JSR-160, 즉 JMX 원격 API 명세가 만들어짐. 최소 RMI 바인딩을 필수로 요구하며, 선택적으로 JMX 메시징 프로토콜(JMXMP)을 요구

***

2.1 원격으로 MBean 노출하기

MBean을 원격 객체로 이용하기 위해, 스프링의 ConnectionServerFactoryBean 구성

```
@Bean
public ConnectorServerFactoryBean connectorServerFactoryBean() {
  return new ConnectorServerFactoryBean();
}
```

해당 팩토리빈은 JMXConnectorServer를 생성하고 시작 => 9875포트 -> service:jmx:jmxmp://localhost:9875

\* JMX 구현체에 따라, RMI, SOAP, Hessian/Burlap, IIO를 포함하여, 몇 가지의 원격 프로토콜 옵션이 존재

&#x20;

\==> RMI를 이용하여, MBean을 이용하기

```
// serviceURL을 RMI를 사용
@Bean
public ConnectorServerFactoryBean connectorServerFactoryBean() {
  ConnectorServerFactoryBean csfb = new ConnectorServerFactoryBean();
  csfb.setServiceUrl("service:jmx:rmi://localhost/jndi/rmi://localhost:1099/spitter"); // RMI 레지스트리에 바인딩(1099)
  return csfb;
}

// RMI 레지스트리 팩토리 빈을 통해, MBean이용
@Bean
public RmiRegistryFactoryBean rmiRegistryFB() {
  RmiRegistryFactoryBean rmiRegistryFB = new RmiRegistryFactoryBean();
  rmiRegistryFB.setPort(1099);
  return rmiRegistryFB;
}
```

***

#### 2.2 원격 MBean에 액세스

원격 MBean서버에 액세스하려면, MBeanServerConnectionFactoryBean을 설정해야 함

```
// 위에서 만든 rmi 서버에 액세스하는, 팩토리빈 선언
@Bean
public MBeanServerConnectionFactoryBean connectionFactoryBean() {
  MBeanServerConnectionFactoryBean mbscfb = new MBeanServerConnectionFactoryBean();
  mbscfb.setServiceUrl("service:jmx:rmi://localhost/jndi/rmi://localhost:1099/spitter");
  return mbscfb;
}

// 다 빈과  마찬가지로, 빈 프로퍼티로 연결
@Bean
public JmxClient jmxClient(MBeanServerConnection connection) {
  JmxClient jmxClient = new JmxClient();
  jmxClient.setMbeanServerConnection(connection);
  return jmxClient;
}
```

&#x20;

\* MBeanServerConnection에서는 원격 MBean 서버를 쿼리하며, 포함된 메소드를 호출할 수 있는 몇 가지 메소드가 포함됨

```
// MBean의 수를 가져오는 메소드
int mbeanCount = mbeanServerConnection.getMBeanCount

// 원격서버에 모든 MBean의 이름을 쿼리함
java.util.Set mbeanNames = mbeanServerConnection.queryNames(null, null);

// 이 외에도, MBean 애트리뷰트에 액세스도 가능(getAttribute)
String cronExpression = mbeanServerConnection.getAttribute(new ObjectName("spitter:name=SpittleController"), "spittlesPerPage");

// 마찬가지로 값 변경도 가능하다(setAttribute)
mbeanServerConnection.setAttribute(new ObjectName("spitter:name=SpittleController"), new Attribute("spittlesPerPage", 10));

// 오퍼레이션을 호출할 수 있음(invoke), setSpittlesPerPage 사용
mbeanServerConnection.invoke(
    new ObjectName("spitter:name=SpittleController"),
    "setSpittlesPerPage",
    new Object[] { 100 },
    new String[] {"int"}
 );
```

\=> invoke() 메소드의 경우, 일일이 파라미터를 전달해줘야 하는데, 이러한 점은 일반적인 메소드 호출만큼 직관적이지 않다

\=> 프록시를 만듬

***

#### 2.3 MBean 프록시 만들기

MBeanProxyFactoryBean : 15장의 리모팅 프록시 빈들과 같은 성질의 프록시 팩토리 빈

```
// 앞에서 구현한 적이 있는, SpittleControllerManagedOperations 인터페이스를 프록시로 이용
@Bean
public MBeanProxyFactoryBean remoteSpittleControllerMBean(MBeanServerConnection mbeanServerClient) {
  MBeanProxyFactoryBean proxy = new MBeanProxyFactoryBean();
  proxy.setObjectName("");
  proxy.setServer(mbeanServerClient);
  proxy.setProxyInterface(SpittleControllerManagedOperations.class);
  return proxy;
}
```

&#x20;

<figure><img src="https://blog.kakaocdn.net/dn/cUrEqE/btqA0NLs1Mm/OEQm3Sj71iPGQrIknwek11/img.png" alt=""><figcaption><p>원격 MBean에 대한 프록시 생성. 이때 프록시의 클라이언트는 설쩡된 POJO인 것처럼 원격MBean과 상호작용</p></figcaption></figure>

***

### 3. 통지처리

어플리케이션의 중요한 이벤트를 파악 후, 통지를 하기위해 스프링의 NotificicationPublisherAware 인터페이스로 통지 전송을 할 수 있음

\=> 일단, 전송하고자 하는 MBean을 모두 이 인터페이스를 구현해야함

<figure><img src="https://blog.kakaocdn.net/dn/bQ503C/btqAXnUWG2Y/xdm0Tcw4VDLcGfOtUX4OY0/img.png" alt=""><figcaption><p>JMX 통지를 통해, 외부와 활발하게 통신</p></figcaption></figure>

```
@Component 
@ManagedResource("spitter:name=SpitterNotifier") 
@ManagedNotification(notificationTypes="SpittleNotifier.OneMillionSpittles", name="TODO")
public class SpittleNotifierImpl implements NotificationPublisherAware, SpittleNotifier { // 퍼블리셔어웨어 구현
  
  private NotificationPublisher notificationPublisher;
 
  public void setNotificationPublisher(NotificationPublisher notificationPublisher) { // 퍼블리셔 주입
    this.notificationPublisher = notificationPublisher;
  }
  public void millionthSpittlePosted() { // 통지 전송
    notificationPublisher.sendNotification(new Notification("SpittleNotifier.OneMillionSpittles", this, 0));
  }
}

```

\*  요구가 많은 인터페이스가 아닌 setNotificationPublisher만 구현하면 됨 =>> 자동주입되는 NotificationPublisher를 이용

\* setNotification() 메소드가 호출 되면, 통지가 어디론가 가고 있다 => 리스너가 반응하도록 설정

***

#### 3.1 통지 듣기

통지를 받는표준 방식은 NotificationListener의 구현

```
public class PagingNotificationListener implements NotificationListener {
  public void handleNotification(Notification notification, Object handback) {
    ~~~~~        
  }
}
```

\* PaginNotificationListener는 전형적인 JMX 통지 리스너,. 통지를 받으면 통지에 반응하기 위해 handleNotification() 메소드가 호출된다.

\* 실제 구현은 책에 없음.. ~~책도 이제 귀찮은듯~~

\* 마지막으로 MBeanExporter로 해당 리스너를 등록한다

```
@Bean
public MBeanExporter mbeanExporter() {
  
  MBeanExporter exporter = new MBeanExporter();
  Map<?, NotificationListener> mappings =
  new HashMap<?, NotificationListener>();
  
  mappings.put("Spitter:name=PagingNotificationListener", new PagingNotificationListener());
  exporter.setNotificationListenerMappings(mappings);
  
  return exporter;
}
```

\* notificationListenerMappings 프로퍼티는 통지 리스너를 해당 MBean에 매핑
