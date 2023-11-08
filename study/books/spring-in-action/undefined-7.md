# 이메일

### 1. 이메일 전송을 위해 스프링 설정하기

MailSender 인터페이스 : 스프링의 이메일 추상화 핵심. 해당 인터페이스의 구현체는 이메일 서버에 연결하여 이메일을 전송한다

<figure><img src="https://blog.kakaocdn.net/dn/y3bdG/btqAQmoq2DW/mVKCBZLcltTDl3KKJkQAo1/img.png" alt=""><figcaption></figcaption></figure>

JavaMailSenderImpl : MailSender의 구현체, JavaMail API를 사용한다



#### 1.1 메일 전송자 설정하기

```
// JavaMailSenderImpl은 @Bean 메소드의 몇개 라인만으로 설정됨
@Bean
public MailSender mailSender(Environment env) {
  JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
  mailSender.setHost(env.getProperty("mailserver.host"));
  mailSender.setPort(env.getProperty("mailserver.port"));
  mailSender.setUsername(env.getProperty("mailserver.username"));
  mailSender.setPassword(env.getProperty("mailserver.password"));
  return mailSender;
}
```

\* host, port, username, password 등을 설정해 줄 수 있다 => 자신의 메일 세션을 생성하기 위해 설정 => 기본적으로 포트는 25(SMTP 포트)를 쓸 것으로 가정

\=> 만약 MailSession을 이미 JNDI에 구성해 놓았거나, 서버에 의해 JNDI가 구성돼 있다면, 서버의 상세 설정을 JavaMailSenderImpl에 설정하는 것은 좋은 선택이 아님

\=> 데이터 소스와 마찬가지로, JNDI로 검색이 가능

```
// 세션 검색 및 등록
@Bean
public JndiObjectFactoryBean mailSession() {
  JndiObjectFactoryBean jndi = new JndiObjectFactoryBean();
  jndi.setJndiName("mail/Session");
  jndi.setProxyInterface(MailSession.class);
  jndi.setResourceRef(true);
  return jndi;
}


// 메일센더에 세션 등록
@Bean
public MailSender mailSender(MailSession mailSession) {
  JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
  mailSender.setSession(mailSession);
  return mailSender;
}

```

\=> 명시적 서버 설정을 교체하였고, 서버의 설정은 JNDI에서 관리하게 됨

\=> 이 경우, JavaMailSenderImpl은 서버의 설정보다는, 메일 발송에 초점을 맞출수 있게 된다.



#### 1.2 메일 서버를 와이어링하고 사용하기

```
// 메일서버를 오토와이어링하고, 발송자에 사용할 수 있음
@Autowired
JavaMailSender mailSender;

// 메일을 보내는 메소드
public void sendSimpleSpittleEmail(String to, Spittle spittle) {
  SimpleMailMessage message = new SimpleMailMessage(); // 메시지 구성
  
  String spitterName = spittle.getSpitter().getFullName();
  
  message.setFrom("noreply@spitter.com"); // 발송자
  message.setTo(to);
  message.setSubject("New spittle from " + spitterName);
  
  message.setText(spitterName + " says: " + spittle.getText());
  
  mailSender.send(message); // 메시지를 세팅후, mailSender는 해당 메시지를 보낸다
}
```

\* setFrom() 메소드를 통해 메시지의 발송자를 지정하고,, mailSender의 send() 메소드로 메시지를 발송한다..



#### 2. 이메일 메시지를 풍부하게 꾸미기

#### 2.1 첨부파일 추가하기

첨부파일을 보내기 위하여, 멀티파트(multiPart)메시지를 생성한다

SimpleMailMessage의 경우, 첨부파일을 보내기에는 너무 간단하다. 이를 위해서 MIME(Multipurpose  Internet Mail Extensions) 메시지를 생성해야 한다

```
MimeMessage message = mailSender.createMimeMessage();
```

\=> 이후에는, 메일 주소, 본문, 첨부파일만 있으면 된다 => 하지만 MIME의 API들은 그리 간단하진 않음

\=> 스프링에선 쉽게  사용하기 위해 MimeMessageHelper를 제공한다

```
MimeMessageHelper helper = new MimeMessageHelper(message, true);
```

helper의 메소드들을 사용하면, 간단하게 첨부파일을 추가한 메일을 보낼 수 있음

```
public void sendSpittleEmailWithAttachment(String to, Spittle spittle) throws MessagingException {

  MimeMessage message = mailSender.createMimeMessage();
  MimeMessageHelper helper =
      new MimeMessageHelper(message, true); // 헬퍼에 메시지를 등록
   String spitterName = spittle.getSpitter().getFullName();
 
  helper.setFrom("noreply@spitter.com"); // 메시지 발송자
  helper.setTo(to);
  helper.setSubject("New spittle from " + spitterName);
  helper.setText(spitterName + " says: " + spittle.getText());

  FileSystemResource couponImage = new FileSystemResource("/collateral/coupon.png"); // 첨부할 파일 주소
   
  helper.addAttachment("Coupon.png", couponImage); // 헬퍼를 통해 이미지 등록
  
  mailSender.send(message);
}
```



#### 2.2 리치 콘텐츠를 이용한 이메일 전송

rich 이메일 전송은 텍스트와 크게 다르지 않다. 핵심은 HTML로 텍스트를 설정하는 것 -> 마찬가지로 MimeMessageHelper 사용

```
public void sendRichSpitterEmail(String to, Spittle spittle) throws MessagingException {
  MimeMessage message = mailSender.createMimeMessage();
  MimeMessageHelper helper = new MimeMessageHelper(message, true);
  
  helper.setFrom("noreply@spitter.com");
  helper.setTo("craig@habuma.com");
  helper.setSubject("New spittle from " +
        spittle.getSpitter().getFullName());
  
  // html로 텍스트 설정
  helper.setText("<html><body><img src='cid:spitterLogo'>" + 
               "<h4>" + spittle.getSpitter().getFullName() + " says...</h4>" +
               "<i>" + spittle.getText() + "</i>" +
               "</body></html>", true);
               
  // 리소스 참조
  ClassPathResource image = new ClassPathResource("spitter_logo_50.png");
  helper.addInline("spitterLogo", image);
  
  mailSender.send(message);
}
```



### 3. 템플릿을 사용하여 이메일 생성

일반적으로 문자열 결합으로, 메시지를 구상하면 실수할 확률이 높다. 또한 자바코드에 HTML을 섞으면  좋지 않다

결과적으로 HTML 메시지에 가까운 이메일 레이아웃을 표현하는 방법이 쓰임 => 아파치 Velocity, Thymeleaf로 템플릿 옵션을 선택할 수 있다



#### 3.1 Velocity를 사용하여 메시지 구성

Velocity : 아파치의 일반 용도 템플릿 엔진, JSP의 대용으로 쓰임

우선, VelocityEngine을 연결해야함 => VelocityEngineFactoryBean 제공

```
@Bean
public VelocityEngineFactoryBean velocityEngine() {
  VelocityEngineFactoryBean velocityEngine =
          new VelocityEngineFactoryBean();
  Properties props = new Properties();
  props.setProperty("resource.loader", "class");
  props.setProperty("class.resource.loader.class",
          ClasspathResourceLoader.class.getName());
  velocityEngine.setVelocityProperties(props);
  return velocityEngine;
}
```

\* 유일하게 설정해야하는 프로퍼티 : velocityProperties

\-> 이메일 전송에 연결

```
// 벨로시티엔진 오토와이어링
@Autowired
VelocityEngine velocityEngine;


Map<String, String> model = new HashMap<String, String>();
model.put("spitterName", spitterName);
model.put("spittleText", spittle.getText());
String emailText = VelocityEngineUtils.mergeTemplateIntoString(
          velocityEngine, "emailTemplate.vm", model ); // Velocity엔진을 통해, String으로 병합하는 작업을 간소화 한다
```

emailTemlate.vm ==> velocity 엔진의 파일로 존재 ==> 이렇게 하면, 굳이 코드 내에서 html을 섞어가며 쓸 필요가 없다

```
#자세한 내용은, velocity 문서를 참조
<html>
<body>
  <img src='cid:spitterLogo'>
  <h4>${spitterName} says...</h4>
  <i>${spittleText}</i>
</body>
</html>
```



#### 3.2 Thymeleaf 사용하기

Thymeleaf는 HTML을 위한 괜찮은 엔진. 하지만 JSP, Velocity와 달리, 특정 태그 라이브러리나 비일반적 마크업을 포함하지 않음

\=> 템플릿 디자이너는 쉽게 HTML툴을 사용 가능

```
// 완전한 형태
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<body>
  <img src="spitterLogo.png" th:src='cid:spitterLogo'>
  <h4><span th:text="${spitterName}">Craig Walls</span> says...</h4>
  <i><span th:text="${spittleText}">Hello there!</span></i>
</body>
</html>
```

&#x20;

\* Thymeleaf 엔진을 사용하기 위해, Thymeleaf context 인스턴스를 생성 후 모델 데이터로 채운다

```
// 모델 데이터를 채우는 동작 자체는, 벨로시티와 유사
Context ctx = new Context();
ctx.setVariable("spitterName", spitterName);
ctx.setVariable("spittleText", spittle.getText());
String emailText = thymeleaf.process("emailTemplate.html", ctx);

helper.setText(emailText, true);
mailSender.send(message);
```

&#x20;

\* Thymeleaf 엔진 자체는 6장에서 구성한, SpringTemplateEngine과 동일한 빈이다 => 다만 생성자 주입을 SpittleEmailServiceImpl로 주입

```
// 6장과 동일
@Autowired
private SpringTemplateEngine thymeleaf;

@Autowired
public SpitterEmailServiceImpl(SpringTemplateEngine thymeleaf) {
  this.thymeleaf = thymeleaf;
}
```

&#x20;

\* 6장의 템플릿엔진에서, 미세한 조정이 필요함 => 6장에선 서블릿 컨테스트 설정 => 이메일 템플릿은 클래스패스에서 해결

\==> ServletContextTemplateResolver뿐 아니라, ClassLoaderTemplateResolver가 필요(둘 다 필요)

```
//이메일 템플릿을, 클래스패스에서 해결하기 위한 리졸버
@Bean 
public ClassLoaderTemplateResolver emailTemplateResolver() {
  ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
  resolver.setPrefix("mail/"); // 메일의 접두사 프로퍼티
  resolver.setTemplateMode("HTML5"); 
  resolver.setCharacterEncoding("UTF-8"); 
  setOrder(1); // 순서1, ServletContextTemplateResolver는 순서 2로 지정
  return resolver;
}

// 일반적인  서블릿 컨텍스트 템플릿 리졸버
@Bean
public ServletContextTemplateResolver webTemplateResolver() {
  ServletContextTemplateResolver resolver =
          new ServletContextTemplateResolver();
  resolver.setPrefix("/WEB-INF/templates/");
  resolver.setTemplateMode("HTML5");
  resolver.setCharacterEncoding("UTF-8");
  setOrder(2); // 이메일 이후 2번
  return resolver;
}

// 리졸버가 2개이상일 때의 설정
@Bean 
public SpringTemplateEngine templateEngine(Set<ITemplateResolver> resolvers) {
  SpringTemplateEngine engine = new SpringTemplateEngine();
  engine.setTemplateResolvers(resolvers); // 2개의 복수 프로퍼티(resolvers)를 엔진에 지정할 수 있다
  return engine;
}
```
