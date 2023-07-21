# Logback

### Logback에서의 설정

> Logback configuration 설명에 앞서, Joran이라는 이름이 자주 나올 것입니다. Joran은 Logback이 사용하는 configuration 프레임워크입니다.

또한, 중간 정도의 크기의 애플리케이션이라도 코드에 수천 개의 로깅 문을 포함하기에 그것들을 효율적이고 간편하게 관리하기 위한 도구가 필요했습니다.

Logback은 프로그래밍으로 또는 XML이나 Groovy 포맷의 설정 스크립트 파일을 통해서 설정할 수 있습니다. Logback이 스스로 설정을 찾는 스텝은 다음과 같습니다.

```
classpath에서 logback-test.xml 파일을 찾습니다.
(1) 과정에서 파일을 찾지 못했다면, classpath에서 logback.groovy 파일을 찾습니다.
(2) 과정에서 파일을 찾지 못했다면, classpath에서 logback.xml 파일을 찾습니다.
(3) 과정에서 파일을 찾지 못했다면, JDK 1.6의 service-provider loading facility (Service Loader)에 의해 com.qos.logback.classic.spi.Configurator 인터페이스의 구현체를 찾습니다. 탐색 위치는 classpath에서 META-INF\services\ch.qos.logback.classic.spi.Configurator 입니다.
위 과정에서 성공한 경우가 없다면, logback은 콘솔에 출력하는 BasicConfigurator으로 설정합니다.
```

당신이 특별한 Logback 설정을 하지 않을 것이라면 자동으로 (5) 번의 BasicConfigurator로 설정될 것입니다. 하지만, Joran이 Logback Conguration 파일을 파싱 하는데 100 밀리세컨드 정도의 시간이 소요됩니다. 이 정도의 몇 밀리세컨드라도 줄여 애플리케이션이 더 빠르게 시작되길 바란다면 (1) \~ (4) 스텝을 생략할 수 있도록 직접 BasicConfigurator를 작성할 수 있습니다.&#x20;

### Logback 자동 설정

logback을 설정하는 가장 쉬운 방법은 그냥 아무 설정을 없이 BasicConfigurator에게 맡기는 것입니다. 이렇게 지정할 경우 console에 로그 메시지가 출력됨을 확인할 수 있습니다. \
BasicConfigurator를 통해 설정할 경우 최소한으로 설정된 ConsoleAppender가 루트 로거에 부착되게 됩니다. 출력 포맷은 아래와 같이 지정되었으며 PatternLayoutEncoder에 등록되어있고, 루트 로거의 레벨은 DEBUG로 지정됩니다.

> %d{HH:mm:ss.SSS} \[%thread] %-5level %logger{36} - %msg%n.

예시)

```
package chapters.configuration;
  
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
   
public class Foo {
  static final Logger logger = LoggerFactory.getLogger(Foo.class);
  
  public void doIt() {
    logger.debug("Did it again!");
  }
}
```

```
16:06:09.046 [main] DEBUG chapters.configuration.Foo - Did it again!
```

### logback-test.xml / logback.xml을 이용한 Logback 자동 설정

앞서 설정 과정에서 알아보았다시피, Logback은 설정 파일을 찾는 순서가 존재합니다. 이번에는 classpath에 logback.xml 파일을 생성하고 아래와 같이 Logback 설정을 작성해봅시다. 아래의 설정은 BasicConfigurator와 동일한 설정입니다. 이후 동작시켜보면 위의 결과와 같은 결과를 보임을 확인할 수 있습니다.

```
<configuration>

  <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <!-- encoders are assigned the type
         ch.qos.logback.classic.encoder.PatternLayoutEncoder by default -->
    <encoder>
      <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
    </encoder>
  </appender>

  <root level="debug">
    <appender-ref ref="STDOUT" />
  </root>
</configuration>
```

Configuration 파일을 분석하는 동안 경고 또는 에러가 발생하면, Logback은 내장 상태 시스템에 의해 콘솔에 내부 상태 데이터(Status Data)를 자동으로 출력합니다. 만약 사용자가 상태 수신 코드를 명시적으로 등록할 경우 중복을 제거하기 위해 Logback의 자동 상태 출력 기능은 비활성화됩니다.

상태 데이터는 StatusPrinter를 이용하여 출력하는 방법이 있는 반면, logback.xml 파일을 통해 설정할 수 있습니다. 방법은 configuration디렉티브에 debug="true" 속성을 추가하는 것입니다.

```
<configuration debug="true"> <!-- 상태 데이터 출력 -->

  <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender"> 
    <!-- encoders are  by default assigned the type
         ch.qos.logback.classic.encoder.PatternLayoutEncoder -->
    <encoder>
      <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
    </encoder>
  </appender>

  <root level="debug">
    <appender-ref ref="STDOUT" />
  </root>
</configuration>
```

또 다른 방법은 StatusListener를 등록하는 것입니다. debug="true" 속성은 OnConsoleStatusListener를 등록하는 것과 완전히 동일하게 동작합니다.

```
<configuration>
  <statusListener class="ch.qos.logback.core.status.OnConsoleStatusListener" />  

  ... the rest of the configuration file  
</configuration>
```

### 시스템 변수를 통한 Logback Configuration 경로 지정

Logback이 스텝을 따라 설정을 찾는 방법 이외 logback.configuration 이름의 시스템 변수로 경로를 지정해줄 수 있습니다. 경로의 logback 설정 파일의 확장자는 항상 ".xml" 혹은 ".groovy"이어야만 합니다. 이 시스템 설정은 java 실행 command 옵션으로 지정할 수 있으며 또한 Application Code에서 추가해 줄 수 있습니다.&#x20;

```
java -Dlogback.configurationFile=/path/to/config.xml chapters.configuration.MyApp1
```

```
import ch.qos.logback.classic.util.ContextInitializer;

public class ServerMain {
    public static void main(String args[]) throws IOException, InterruptedException {
       // must be set before the first call to  LoggerFactory.getLogger();
       // ContextInitializer.CONFIG_FILE_PROPERTY is set to "logback.configurationFile"
       System.setProperty(ContextInitializer.CONFIG_FILE_PROPERTY, "/path/to/config.xml");
       ...
    }   
}
```

### 자동 Configuration 재구성

logback-classic 모듈은 주기적으로 configuration 파일을 읽어 Logback의 설정을 재구성할 수 있습니다. 이는 Application이 동작 중일 때 재시작 없이 설정 파일을 수정하고 적용할 수 있는 장점이 있습니다. 자동으로 Logback 설정 재구성을 위해 configuration 디렉티브에 scan="true" 속성을 추가합니다. 기본으로 적용되는 scan 주기는 1분입니다. scan주기는 scanPeriod 속성을 이용하여 지정해줄 수 있습니다.

```
<configuration scan="true" scanPeriod="30 seconds" > 
  ...
</configuration> 
```

scanPeriod의 값은 시간과 시간의 단위("milliseconds", "seconds", "minutes", "hours")를 조합하여 기술합니다.&#x20;

### 패키지 데이터 로깅

Logback을 이용하여 사용하는 패키지 데이터를 로깅할 수 있습니다. 1.1.4 버전 이후 이 설정은 기본값이 disable 되었기 때문에 패키지 데이터 로깅을 위해서는 configuration 디렉티브에 packagingData="true" 속성을 추가해야 합니다. 이는 versioning 이슈에 대한 문제를 파악하는데 큰 도움이 됩니다. 하지만 패키징 데이터를 검사하고 로깅하는 과정은 비교적 큰 비용을 갖고 있기 때문에 성능에 따라 사용 여부를 결정하여야 합니다. 이 설정은 LoggerContext에서도 지정 가능합니다.

```
<configuration packagingData="true">
  ...
</configuration>
```

```
LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
lc.setPackagingDataEnabled(true);
```

### JoranConfigurator 직접 이용하기

Logback은 logback-core 모듈 내 존재하는 Joran이라는 configuration 라이브러리에 의존합니다. 기본 Configuration 메커니즘으로 Logback은 classpath에서 찾은 기본 configuration 파일에서 JoranConfigurator를 호출합니다. 만약 어떠한 이유로 기본 로그백의 configuration 메커니즘을 오버라이드하고 싶다면 직접 JoranConfigurator를 직접 호출하여 사용해야 합니다.

```
package chapters.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

public class MyApp3 {
  final static Logger logger = LoggerFactory.getLogger(MyApp3.class);

  public static void main(String[] args) {
    // assume SLF4J is bound to logback in the current environment
    LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
    
    try {
      JoranConfigurator configurator = new JoranConfigurator();
      configurator.setContext(context);
      // Call context.reset() to clear any previous configuration, e.g. default 
      // configuration. For multi-step configuration, omit calling context.reset().
      context.reset(); 
      configurator.doConfigure(args[0]);
    } catch (JoranException je) {
      // StatusPrinter will handle this
    }
    StatusPrinter.printInCaseOfErrorsOrWarnings(context);

    logger.info("Entering application.");

    Foo foo = new Foo();
    foo.doIt();
    logger.info("Exiting application.");
  }
}
```

예시 코드를 살펴봅시다. 우선 LoggerContext를 직접 가져오고, JoranConfigurator를 생성합니다. JoranConfigurator의 context로 이전 줄에서 가져온 LoggerContext를 지정합니다. context는 configuration 파일을 읽고 파싱 하는 동작을 위해 지정해야 합니다. 이후, LoggerContext를 초기화하고 main 메서드로 전달받은 configuration file 이름을 통해 설정하도록 합니다. JoranConfigurator에 context를 세팅한 후 StatusPrinter를 통해 내부 상태 또한 출력하도록 설정하여 주었습니다.

LoggerContext 초기화는 multi-step configuration 과정을 위해 반드시 호출되어야 하는 과정입니다.

### Logger의 상태 메시지 화면 설정

Logback은 LoggerContext를 통해 StatusManager라는 객체에 내부 상태 데이터들을 수집합니다. Logback에 지정된 default StatusManager는 메모리 적인 측면에서 필요한 정도의 양만 갖도록 하기 위해 두 (Head와 Tail) 부분으로 상태 데이터를 관리합니다. 글자 그대로 Header는 로그의 첫 부분을 의미하며 Tail은 끝 (최신) 부분을 의미합니다. Default 설정된 메모리에 유지할 로그의 개수는 150개입니다 (이는 추후 release에 따라 달라질 수 있습니다).

logback-classic에서는 이러한 상태 데이터를 시각화하여 볼 수 있도록 ViewStatusMessageServlet을 제공합니다. ViewStatusMessageServlet은 현재 사용 중인 LoggerContext에 해당하는 StatusManager 내용들을 HTML 테이블로 출력해줍니다. 사용을 위해 WEB-INF/web.xml 파일에 ViewStatusMessageServlet을 설정하여야 합니다.

```
  <servlet>
    <servlet-name>ViewStatusMessages</servlet-name>
    <servlet-class>ch.qos.logback.classic.ViewStatusMessagesServlet</servlet-class>
  </servlet>

  <servlet-mapping>
    <servlet-name>ViewStatusMessages</servlet-name>
    <url-pattern>/lbClassicStatus</url-pattern>
  </servlet-mapping>
```

ViewStatusMessage 서블릿의 동작을 확인하기 위해서 http://{host}/lbClassicStatus 로 접속합니다.\
(host는 동작한 Application의 host 주소로 입력합니다).\
\


<figure><img src="https://blog.kakaocdn.net/dn/UvOxQ/btqJWrfBYig/abqkOwsbknzZmxHhDCWE91/img.png" alt=""><figcaption><p>http://logback.qos.ch/manual/configuration.html - ViewStatusMessageServlet에서 띄워주는 상태 데이터 메시지 화면</p></figcaption></figure>

### 상태 메시지 리스너

StatutManager에 StatusListener를 붙여 상태 메시지에 대한 응답을 가져올 수 있습니다. Logback은 StatusListener의 구현체로 콘솔에 새로 들어오는 상태 메시지를 출력하는 OnConsolerStatusListener를 제공합니다.

```
   LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory(); 
   StatusManager statusManager = lc.getStatusManager();
   OnConsoleStatusListener onConsoleListener = new OnConsoleStatusListener();
   statusManager.add(onConsoleListener);
```

StatusListener는 등록된 후 새로 들어오는 상태 이벤트에 대한 메시지를 수신합니다. 하지만 리스너 등록 이전에 발송된 메시지들을 수신하지 않기에, Configuration 과정 중 StatusListener의 등록을 가장 앞에 두는 것이 좋은 등록 방법입니다.

Configuration 파일에 하나 혹은 이상의 StatusListener를 지정할 수 있습니다. logback.xml에 StatusListener를 지정하는 방법은 다음과 같습니다.

```
<configuration>
  <statusListener class="ch.qos.logback.core.status.OnConsoleStatusListener" />  

  ... the rest of the configuration file  
</configuration>
```

&#x20;StatusListener는 시스템 변수로써 전달할 수도 있습니다. 변수의 "logback.statusListenerClass"으로 지정합니다. 기본으로 제공되는 StatusListener의 종류는 OnConsoleStatusListener, OnErrorConsoleStatusListener, NopStatusListener가 존재합니다.

```
java -Dlogback.statusListenerClass=ch.qos.logback.core.status.OnConsoleStatusListener ...
```

| package                    | StatusListener               | 동작 방식             |
| -------------------------- | ---------------------------- | ----------------- |
| ch.qos.logback.core.status | OnConsoleStatusListener      | 콘솔에 출력            |
| System.out                 | OnErrorConsoleStatusListener | System.err를 통해 출력 |
| System.err                 | NopStatusListener            | 상태 메시지 drop (무시)  |

&#x20;StatusListener가 지정되지 않으면 기본적으로 상태 메시지는 출력하지 않습니다. 이는 NopStatusListener를 리스너로 지정했을 때와 동일하게 동작합니다.

### logback-classic 중지시키기

logback-classic 모듈에 의해 사용되던 리소스를 해제시킬 가장 좋은 방법은 logback context를 중지시키는 것입니다. context를 중지시키면 context를 통해서 logger에 부착되었던 모든 appender들이 close 되며, active 상태의 스레드를 차례대로 중지시킵니다.

```
import org.sflf4j.LoggerFactory;
import ch.qos.logback.classic.LoggerContext;
...

// assume SLF4J is bound to logback-classic in the current environment
LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
loggerContext.stop();
```

위의 코드는 LoggerContext를 중지시키는 방법을 보여줍니다. loggerContext.stop() 메서드는 ServletContextListener의 contextDestroyed 메서드를 호출합니다. 그로 인해 logback-classic이 정지되며 사용하던 리소스들을 해제합니다.

다른 방법으로 JVM shutdown hook을 이용해서 logback-classic을 중지시킬 수 있습니다. 우선 configuration 파일에 shutdownHook 디렉티브를 추가해줍니다. shutdownHook 디렉티브에 class 속성으로 지정할 shutdown hook 클래스를 지정할 수 있습니다. 만약 아무 속성을 지정하지 않는다면 기본으로 DefaultShutdownHook 클래스로 설정됩니다.

```
<configuration debug="true">
   <!-- in the absence of the class attribute, assume 
   ch.qos.logback.core.hook.DefaultShutdownHook -->
   <shutdownHook/>
  .... 
</configuration>
```

DefaultShutdownHook은 hook 발생 후 곧바로(delay 0 by default) logback context를 중지시킵니다. context 중지에는 백그라운드에서 로그 파일 압축 작업이 일어나는 것을 대비해 최대 30초의 delay를 지정할 수 있습니다. JVM 종료 후에도 백그라운드로 동작하는 압축 작업등을 완료할 수 있도록 하기 위해선 단순히 \<shutdownHook/> 디렉티브만을 지정하면 됩니다. \
웹서버에서는 webShutdownHook이 자동으로 shutdownHook 디렉티브를 지정해주기 때문에 설정을 하지 않도록 합니다 (설정 중복 발생 가능).

servlet-api 3.x 이후에는 logback-classic이 자동으로 웹 서버에게 ServletContanierInitialzer를 implements 하는 LogbackServletContatinerInitializer를 설치합니다. LogbackServletContatinerInitializer는 LogbackServletContextListener를 자동으로 등록하는데 이 리스너를 통해 웹 서버가 stop 혹은 reload 될 때 자동으로 logback-classic context를 중지시켜줍니다.

만약 자동으로 리스너를 등록하는 것을 원치 않는다면 web.xml 파일에 logbackDisableServletContainerInitializer를 세팅함으로써 자동 등록을 방지할 수 있습니다.

```
<web-app>
    <context-param>
        <param-name>logbackDisableServletContainerInitializer</param-name>
        <param-value>true</param-value>
    </context-param>
    .... 
</web-app>
```

&#x20;
