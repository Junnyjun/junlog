# 웹 애플리케이션 보안

**웹 보안의 3요소**

* Principal - 사용자 : 현재 서비스에 접근하기 위한 주체
* Authentication - 인증 : 사용자를 확인하는 과정
* Authorization - 인가 : 확인 된 사용자에 대한 권한 검사 및 부여\
  → Spring Security는 이 3가지를 쉽게 사용가능하며, 확장성 또한 매우 높음

[https://bob-full.tistory.com/6](https://bob-full.tistory.com/6)



### 스프링 시큐리티 시작하기

**모듈**

<figure><img src="https://blog.kakaocdn.net/dn/lP8rG/btqzWYnLQSy/r0uVLqCLNnXEUnNLjVhg00/img.png" alt=""><figcaption></figcaption></figure>

&#x20;



**웹요청 필터**

\* 스프링 시큐리티에는 다양한 보안의 관점을 제공하기 위한 서블릿 필터들을 제공

\* DelegatingFilterProxy : 스플링 애플리케이션 컨텍스트의 위임된 필터 빈을 처리하기 위한 프록시 필터

<figure><img src="https://blog.kakaocdn.net/dn/bpMNgT/btqzXhOdGb5/AknGF0pFFFZ7wuKrfSLtD1/img.png" alt=""><figcaption></figcaption></figure>

```
public class SecurityWebInitializer extends AbstractSecurityWebApplicationInitializer {}

// ===> AbstractSecurityWebApplicationInitializer를 상속받으면 DelegatingFilterProxy가 자동 등록 됨.
```



**간단한 보안 설정**

```
@Configuration
@EnableWebSecurity // 웹 보안 활성
public class SecurityConfig extends WebSecurityConfigurerAdapter {
}

// WebSecurityConfigurerAdapter는 스프링 시큐리티 웹 보안 설정의 기본이라고 할 수 있음
```

\=> WebSecurityConfigurer를 직접 구현하거나, WebSecurityConfigurerAdapter를 확장한 빈으로 설정해야함

```
@Configuration
@EnableWebMvcSecurity // 스프링MVC에 관련한 보안 활성
public class SecurityConfig extends WebSecurityConfigurerAdapter {
}
```

\=> 스프링MVC 보안 활성, 스프링 MVC의 argument resolver를 설정하여, @AuthenticationPrincipal의 주체를 받을 수 있음,,, 폼바인딩 태그 라이브러리 사용하는 빈도 설정

&#x20;

**WebSecurityConfigurerAdapter의 메소드**

* configure(AuthenticationManagerBuilder) : 사용자 세부서비스 + AuthenticationProvider 설정
* configure(WebSecurity) : 필터 연결을 설정, 전역 보안에 영향을 주는 구성 설정
* configure(HttpSecurity) : 인터셉터가 요청하는 URL 경로를 보호해야하는지에 대한 정의, 자신만의 인증 매커니즘 정의



#### 시큐리티 상세 설정

#### 인메모리 사용자 저장소 작업

\* WebSecurityConfigurerAdapter에서 가장 쉬운 설정 방법은 configure(AuthenticationManagerBuilder) 오버라이딩

```
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
  auth
  .inMemoryAuthentication() // 인메모리 저장소 활성화
  .withUser("user").password("pwd").roles("USER").and()
  //.with ~~~ .pas~~ .auhorities("ROLE_USER") 와 상동
  .withUser("admin").password("pwd").roles("USER", "ADMIN");
  //.with ~~~ .pas~~ .auhorities("ROLE_USER", "ROLE_ADMIN") 와 상동
}
// 인메모리 저장소를 활용하여, user, admin에 각각 권한을 주는 함수
```

<figure><img src="https://blog.kakaocdn.net/dn/UjZKK/btqzWrYp0fe/5pyOXkFF5Ej7uqvnKxfKJk/img.png" alt=""><figcaption></figcaption></figure>

authorities()의 축약형 -> roles()



#### 데이터베이스 테이블로 인증

**기본사용자 쿼리  오버라이딩**

\* JDBC 지원 사용자 저장소에 인증하기위한 메소드 - jdbcAuthentcation()을 사용한다&#x20;

```
@Autowired 
DataSource dataSource;

@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
  auth
  .jdbcAuthentication()
  .dataSoruce(dataSoucre)
  .userBuySernameQuery(
    "select username, password, true" +
    "from People where username=?")
  .authoritiesByUsernameQuery(
    "select username, 'ROLE_USER' from People where username=?");
}
// 오토와이어링 된, dataSource를 인증의 수단으로 사용 => 자동연결
```

인증과 기본 권한에 대한 쿼리를 오버라이딩한, jdbcAuthentication()... (정확히 모르겠음..)



**부호화된 암호**

\* 사용자 암호는 암호화된 데이터로 저장 -> 일반텍스트로는 인증을 할  수 없음 -> 이를위해 passwordEncoder()로 암호 복호화가 필요

\* 스프링시큐리티의 암호화 모듈은, BCrpytPasswordEncoder, NoOpPasswordEncoder, StandardPasswordEncoder가 있음

```
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
  auth
  .jdbcAuthentication()
  .dataSoruce(dataSoucre)
  .userBuySernameQuery(~~~~)
  .authoritiesByUsernameQuery(~~~~);
  .passwordEncoder(new StandardPasswordEncoder("53cr3t");
  // 인코더를 선언해서, 패스워드를 복호화할 수 있음(인코더 없이 자동으로 복호화 X)
}

////////////

//PasswordEncoder 인터페이스, 해당 인터페이스를 구현하여 커스텀 인코더도 만들 수 있다.
public interface PasswordEncoder {
  String encode(CharSequence rawPassword);
  boolean matches(CharSequence rawPassword, String encodedPassword);
}
```



#### LDAP기반 적용

LDAP : 경량 디렉토리 액세스 프로토콜 - TCP/IP위에서 디렉토리 서비스를 조회, 응용하는 프로토콜

\* ldapAuthentication() 메소드 사용 : jdbcAuthentication()의 LDAP버전

```
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
  auth
  .ldapAuthentication()
  .userSearchBase("ou=people") // 사용자를 찾는 기본 쿼리
  .userSearchFilter("(uid={0})") // 사용자를 찾는 기본 LDAP 쿼리 필터
  .groupSearchBase("ou=groups") // 그룹 찾는 기본 쿼리
  .groupSearchFilter("members={0}"); // 그룹 찾는 쿼리 필터
}
```



**암호비교설정**

passwordCompare() : 인증 시, 암호에 관한 비교 명령을 실행할 수 있다.&#x20;

```
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
  auth
  .ldapAuthentication()
  .userSearchBase(~~~)
  .userSearchFilter(~~~)
  .groupSearchBase(~~~)
  .groupSearchFilter(~~~)
  .passwordCompare()
  .passwordEncoder(~~~)
  .passwordAttribute("passCode") 
  // 패스워드 attribue에 저장된 애트리뷰트를 인코딩하여, userPassword와 비교한다.
}
```



**원격 LDAP 조회**

기본설정은 localhost:3389에 있을 것을 설정돼있지만, 원한다면 LDAP서버를 설정 가능하다.

\=> contextSource() 이용

```
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
  auth
  .ldapAuthentication()
  .userSearchBase(~~~)
  .userSearchFilter(~~~)
  .groupSearchBase(~~~)
  .groupSearchFilter(~~~)
  .contextSource()
  .url("ldap://~~~.com:389//~`");
}
```



#### 사용자 정의 서비스 설정

\* UserDetailService 인터페이스 구현&#x20;

```
public interface UserDetailService {
	UserDetails loadUserByUsername(Sring  username) throws UsernameNotFoundException;
    // 유저의 이름으로, 정보를 가져오는 메소드
}
```

\=> 간단한 구현 예시

```
public class CafeUserService implements UserDetailService {

	private final ConsumerRepository consumerRepository;
    
    ~~ 생성자 ~~
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Consumer consumer = consumerRepository.findByUsername(username);
        
        if(consumer != null) {
        	List<GrantAuthority> authorities = new ArrayList<GrantAuthority>(); // 권한 리스트
            authorities.add(new SimpleGrantedAuthority("ROLE_COFFEE");
        
        return new User(
        	consumer.getUsername(),
            consumer.getPassword(),
            authorities);
        }
        
        throew new UsernameNotFoundException ~~
}
```

\=> Consumer의 객체를 얻어와서, User 객체를 만든 후 반환한다.

&#x20;

\* 해당 서비스를 사용자 인증에 사용하기 위해서, 보안 설정을 해주어야 한다

```
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
  auth
  .userDetailService(new CafeUserService(consumerRepository);
}
```



#### 요청가로채기

\* 애플리케이션에서, 모든 요청에 대한 보안관리가 있지는 않다(로그인 페이지), 몇몇 요청은 특정 권한만 가능하다(어드민 메뉴 등). 이러한 각각의 요청에 대해, 따로 보안이 필요한 경우를, 스프링 시큐리티는 대비할 수 있다.

configure(HttpSecurity) : 웹 요청(URL path)에 관한 설정용 메소드

```
@Override
protected void configure(HttpSecurity http) throws Exception {
    http
    .authorizeRequest()
    .antMatchers("/consumer/**").authenticated() // consumer/밑으로는 모두 인증
    .antMatchers(HttpMethod.POST, "/search").hasRole("CONSUMER") // /search에서 POST, Consumer 롤을 가진사람만 가능
    .anyRequest().permitAll(); // 위의 두 경우를 제외하곤, 모두 허용
    
```

\=> antMatcher : Ant 스타일 동작이기 때문에, \*\*사용 ( regexMatcher는 .\* 사용)

<figure><img src="https://blog.kakaocdn.net/dn/lukcV/btqzZcsF9in/s6G6YDdAKqhlZ9GIY5fuiK/img.png" alt=""><figcaption><p>패스에 보안을 적용하기위한 메소드</p></figcaption></figure>

**스프링 표현식 보안**

위의, access() 메소드를 통해, SpEL을 사용하여 더 높은 수준의 보안을 구현할 수 있다.

```
.antMatchers("/consumer/me").access("hasRole('ROLE_CONSUMER') and hasIpAddres('192.168.1.2')")
// ROLE과 IP주소에 관한 access 메소드
```

<figure><img src="https://blog.kakaocdn.net/dn/yqk0a/btqzZ3vinA2/0rbgfr3auwNjhVbdGtzLK0/img.png" alt=""><figcaption></figcaption></figure>

<figure><img src="https://blog.kakaocdn.net/dn/bFzUen/btqzYUsiALD/gzsH5IJYiQA3Rbym2t3oEk/img.png" alt=""><figcaption><p>스프링 보안 특성의 표현으로 SpEL을 확장한다.</p></figcaption></figure>



**채널 보안 적용**

등록폼에, HTTPS를 적용할 수 있다 --- HTTPS - 데이터를 주고 받는 과정에 '보안'요소가 추가, 서버간 통신 데이터가  전부 암호화

```
@Override
protected void configure(HttpSecurity http) throws Exception {
	http
    .authorizeRequest()
    .antMatchers("/consumer/me").authenticated()
    .antMatchers(HttpMethod.POST, "/search").hasRole("CONSUMER")
    .anyRequest().permitAll();
    .requiredChannel()
    .antMatchers("/consumer/form").requiresSecure(); // HTTPS 요구
```



**사이트간 요청 위조 방지**

CSRF를 방지 할 수 있다. --- CSRF : 사이트간 요청 위조, 특정 사이트에서 사용자에게 다른 서버에  좋지 않은 결과를 야기시키는 요청을 제출하도록 위조.

스프링 3.2부터는 CSRF 보안은 기본으로 활성화. 동기화 장치 토큰으로  구현된다. 요청에 CSRF 토큰이 없거나, 서버와 일치하지 않으면 CsrfException 발생

Thymeleaf에선 \<form> \~ \</form> 사이에, 숨겨진 \_csrf 필드르 사용

스프링 폼 바인딩 태그를 이용하면, \<sf:form> 에서 자동으로 붙여줌.

JSP를 사용한다면, {$\_csrf.paramteterName} 등으로  사용해 줄 수 있다.

\=> CSRF 보호를 비활성 할수도 있다(권장하지 않음)

```
protected void configure(HttpSecurity http) throws Exception {
    http
    ~~~
    .csrf().disable();
}
```



#### 사용자 인증하기

기존 HttpSecurity에, formLogin을 추가하면, 단순한 형태의 기본 페이지를 스프링시큐리티가 제공해준다.

```
protected void configure(HttpSecurity http) throws Exception {
    http
    .formLogin() // 간단한 로그인 폼 제공
    .and()
    .antMatchers( ~~ )
    . ~~~
}
```

<figure><img src="https://blog.kakaocdn.net/dn/ckxh58/btqzYCMpLE3/4u9Xg9V1ELQaV274DzoxY1/img.png" alt=""><figcaption></figcaption></figure>



**사용자 정의 로그인 페이지 추가하기**

Thymeleaf를 이용하여, 간단한 로그인 페이지를 개발할 수 있다. 이ㄷ 때 중요한 점은, \<form>이 제출되는 부분에 CSRF토큰이 포함되어 있다는 것이다.

```
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:th="http://www.thymeleaf.org">
  <head>
    <title>Spitter</title>
    <link rel="stylesheet"
          type="text/css"
          th:href="@{/resources/style.css}"></link>
  </head>
  <body onload='document.f.username.focus();'>
    <div id="header" th:include="page :: header"></div>
    <div id="content">
      <form name='f' th:action='@{/login}' method='POST'>
        <table>
          <tr>
            <td>User:</td>
            <td><input type='text' name='username' value='' /></td>
          </tr>
          <tr>
          <td>Password:</td>
          <td><input type='password' name='password'/></td>
        </tr>
        <tr>
        <td colspan='2'><input name="submit" type="submit" value="Login"/></td></tr>
      </table>
    </form>
  </div>
  <div id="footer" th:include="page :: copy"></div>
</body>
</html>
```

\=> 이 템플릿 역시 /login페이지로  제출 할 것이다 (\<form> 부분)



**HTTP 기본 인증 활성화**

HTTP 기본 인증은, 사용자를 직접 애플리케이션에 HTTP 요청을 하여 인증한다.&#x20;

[http://iloveulhj.github.io/posts/http/http-basic-auth.html ](http://iloveulhj.github.io/posts/http/http-basic-auth.html) ===> HTTP 기본인증

HTTP 기본 인증 활성화 또한, HttpSecurity에서 가능하다

```
protected void configure(HttpSecurity http) throws Exception {
    http
    .formLogin()
    .loginPage("/login") // 로그인 페이지
    .and()
    .httpBasic()
    .realmName("Cafe") // 원하는대로 범위를 명시할 수 있다
    .and()
    ~~~
}
```

\=> 대게 httpBasic정도만 필요한 편...



**기억하기 기능**

HttpSecurity의 rememberMe()를 호출해준다.

```
protected void configure(HttpSecurity http) throws Exception {
    http
    .formLogin()
    .loginPage("/login") // 로그인 페이지
    .and()
    .rememberMe()
    .tokenValidatySeconds(2419200) // 기본 값은 2주이지만, 4주까지 유효하도록 명시
    .key("consumerKey") // 해당 키로 부호화하여 저장된다 (MD5 해시)
    ~~~
  
}
```



**로그아웃**

HttpSecurity의 logout()을 호출

```
protected void configure(HttpSecurity http) throws Exception {
    http
    .formLogin()
    .loginPage("/login")
    .and()
    .logout()
    .logoutSuccessUrl("/") // 로그아웃 성공 시 url
}
```

=> 만약 LogoutFilter가 가로채기위한 기본 설정패스를 오버라이딩 한다면?

```
protected void configure(HttpSecurity http) throws Exception {
    http
    .formLogin()
    .loginPage("/login")
    .and()
    .logout()
    .logoutUrl("/signOut") // 로그아웃 필터를 위한, 기본 설정패스 오버라이딩
}
```



#### 뷰 보안하기

.. 뷰는 자세히 다루지는 않겠으나, JSP 태그 라이브러리와, Thymeleaf를 이용할 수 있다.

**JSP 태그 라이브러리**

<figure><img src="https://blog.kakaocdn.net/dn/ZGA5f/btqz0Cxv4mK/7P9sKDk2QYJjMC8zawWA30/img.png" alt=""><figcaption><p>해당건 3건만, 포함</p></figcaption></figure>

\=> \<security:authentication>에서 , authorities(권한), credentials(자격 - 암호), details(추가정보 - ip, 세션id, 인증서 번호), principal(사용자) 접근 가능

\=> 간단한 예시

```
<security:authorize
   access="isAuthenticated() and principal.username=='habuma'"> #인증이 됐고, habuma인 유저만
  <a href="/admin">Administration</a>
</security:authorize>

```

&#x20;

**Thymeleaf의 스프링 시큐리티 언어**

스프링 시큐리티 태그 라이브러리와 동일한 애트리뷰트 제공

<figure><img src="https://blog.kakaocdn.net/dn/c8zISA/btqzYUy9jU6/6I1CDy0iKbJ3GzzCBLhSH0/img.png" alt=""><figcaption></figcaption></figure>

\-> 스프링 시큐리티 언어 등록

```
@Bean
public SpringTemplateEngine templateEngine(TemplateResolver templateResolver) {
  SpringTemplateEngine templateEngine = new SpringTemplateEngine();
  templateEngine.setTemplateResolver(templateResolver);
  templateEngine.addDialect(new SpringSecurityDialect()); // 시큐리티 언어 등록
  return templateEngine;
}
```

\===> 간단한 Thymeleaf 스프링 시큐리티 언어 사용 예제

```
<div sec:authorize="isAuthenticated()">
  Hello <span sec:authentication="name">someone</span>
</div>
```
