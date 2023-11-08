# 시큐리티

### 어노테이션을 활용하는 시큐리티 메소드

스프링에서 가장 일반적으로 사용되는 보안방식은, 어노테이션을 이용하여 보안을 적용하는 방식

* 스프링 시큐리티 - @Secured
* JSR-250 - @RolesAllowed
* Expression-driven 어노테이션 - @PreAuthorize, @PostAuthorize, @PreFilter, @PostFilter

\-> @Secured, @RolesAllowed는 간단한 어노테이션, 이 외의 @PreAuthorize, @PostAuthorize, @PreFilter, @PostFilter는 보안 규칙에 더 많은 유연성을 보장함

&#x20;



#### @Secured 어노테이션을 통한 메소드 보안

@EnableGlobalMethodSecurity : 스프링 시큐리티의 메소드 어노테이션 기반 시큐리티를 활성화 하기 위해서 필요

```
@Configuration
@EnableGlobalMethodSecurity(securedEnabled=true)
public class MethodSecurityConfig
       extends GlobalMethodSecurityConfiguration {
}
```

\* GlobalMethodSecurityConfiguration -> 9장의  WebSecurityConfigurerAdapter와 비슷한 포지션.. 해당 클래스를 확장하여 다양한 설정을 할 수 있다. - ex) configure(AuthoenticationManagerBuilder auth)

\* securedEnabled=true => 포인트커트 생성, @Secured로 애너테이션 되는 빈 메소드를 감싼다.

```
@Secured({"ROLE_USER", "ROLE_ADMIN"})
public void addUser(User user) {
    ~~~~
}
```

\* user, admin 권한이 있을 때만, 해당 메소드를 호출 할 수 있다

\* 인증되지 않은 사용자는, 스프링 시큐리티 예외를 발생

\* 단점은, 스프링에만 특화된 애너테이션이라는 점

&#x20;



#### JSR-250의 @RolesAllowed

@RolesAllowd는 @Secured와 거의 모든 면에서 같음. 차이점은 자바 표준 애너테이션

```
@Configuration
@EnableGlobalMethodSecurity(jsr250Enabled=true)
public class MethodSecurityConfig
       extends GlobalMethodSecurityConfiguration {
}
```

\* jsr250Enabled를 true로 설정  - 동작 방식이 @Secured와 동일

\* 자바 표준 애너테이션이기 때문에, 다른 프레임워크나 API컨텍스트에서 사용될 때에도 사용이 가능한 이점

\* @Secured와 공통적인 단점 : 메소드 실행을, 권한만이 관리하며, 다른 팩터들이 없다는 한계점

&#x20;



#### 메소드 레벨 시큐리티를 위한 표현식 사용

@Secured와 @RolesAllowed가, 권한 없는 사용자의 접근을 제한하는 정도의 동작만 함. 보안 제약 사항을 더 자세하게 제공하기 위해, 스프링 시큐리티는 아래 네개의 애너테이션을 제공한다.

<figure><img src="https://blog.kakaocdn.net/dn/dB2MLC/btqAkT62KO6/fSm8XmG7ULOUo7uVRxqkck/img.png" alt=""><figcaption></figcaption></figure>

&#x20;

```
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {}
```

\* 해당 Confgiruation을 설정 후, 계속 살펴보았던애너테이션의 prePostEnabled 애트리뷰트를 true로 설정한다.

&#x20;



#### 메소드 액세스 규칙 표현하기

스프링은 표현식 검증에 기반을 둔, 매세도 액세스 제한 애너테이션인 @PreAuthorize와, @PostAuthorize를 통해서, 유연성을 추가한다.



**사전 권한 부여 PREAUTHORIZING**&#x20;

@PreAuthorize : SpEL을 쓰지 않으면, 기존 @Secured와 @RolesAllowed와 동일하게 동작하는 것처럼 보이지만, SpEL을 사용하여, 유연하게, 사전권한을 부여할 수 있다.

```
@PreAuthorize("(hasRole('ROLE_SPITTER') and #spittle.text.length() >= 140 or hasRole('ROLE_PREMIUM')")
public void addSpittle(Spittle spittle) {
  ~~~~
}
```

\* SPITTER권한이 있는 사람 중에, spittle.text가 140 이상이거나, PREMIUM 권한이 있는 지 체크 먼저 하고 권한 부여&#x20;



**사후 권한 부여 POSTAUTHORIZING**

@PostAuthorize : 다소 명확하지 않은데, 먼저 메소드를 실행한 후에 객체를 기준으로, 보안 적용 여부를 결정한다.&#x20;

```
@PostAuthorize("returnObject.spitter.username == principal.username")
public Spittle getSpittleById(long id) {
    ~~~~        
}
```

\* 먼저, 메소드를 실행한 후에 returnObject의 값을 비교하여, 같은 사용자일 때만 권한 부여

\* @PreAuthorize와 달리, 애너테이션이 적용 된 메소드를 수행 후, 나중에 권한을 부여한다는 점을 기억 -> 의도하지 않는 결과 발생에 주의(예외 처리 등)

&#x20;



#### 필터링

메소드 액세스를 제한하는 것은 매우 무거운 작업이다. 필요한 것의 범위를 좁히고 반환되는 컬렉션을 필터링 하는 방법을 사용해아할 경우가 있음. 이럴 경우에 스프링 시큐리티는 필터링 어노테이션 메소드를 제공



**사후필터링 POSTFILTERING**

@PostFiilter : 값 파라미터로 SpEL 표현식을 가짐. 메소드 엑세스 제한 대신, 각 컬렉션 값 멤버에 대한 표현식이 메소드로부터 반환되는지 검증 후, 결과가 false이면 제거

```
@PreAuthorize("hasAnyRole({'ROLE_SPITTER', 'ROLE_ADMIN'})")
@PostFilter( "hasRole('ROLE_ADMIN') || filterObject.spitter.username == principal.name")
public List<Spittle> getOffensiveSpittles() {
     ~~~~~ 
}
```

\* 사전 권한 부여 후, 메소드가 실행되면. 해당 리턴된 Spittle 리스트를 필터링해서, 허용된 Spittle만이, 사용자가 볼 수 있다.

\* filterObject는 반환 된, List의 개별 요소를 참조 -> false면 해당 객체 삭제



**사전 필터링 PREFILTERING**

@PreFilter : @PostFilter와 반대로, 사전에 필터링을 한 후에 적용하게 된다.

```
@PreAuthorize("hasAnyRole({'ROLE_SPITTER', 'ROLE_ADMIN'})")
@PreFilter( "hasRole('ROLE_ADMIN') || targetObject.spitter.username == principal.name")
public void deleteSpittles(List<Spittle> spittles) { 
    ~~~~
}
```

\* Spittle들을 삭제하는 메소드

\* 사전 권한 부여를 통해, 메소드를 호출한 후에. 메소드 실행전에 파라미터(spittles)를 검증한다. 검증된 아이템들이 true인 것을 필터링하여 메소드를 진행함



**권한 평가자 정의**

@PreFilter @PostFilter의 표현식은 복잡하지 않으나, 길어질 여지가 있다. 이 경우에 다루기 불편하고, 테스트가 복잡한 문제점이 있음

\=> 권한 평가자(Permission Evaluator)를 통해 간단한 표현식으로 바꿀 수 있다.

```
@PreAuthorize("hasAnyRole({'ROLE_SPITTER', 'ROLE_ADMIN'})")
@PreFilter("hasPermission(targetObject, 'delete')")
public void deleteSpittles(List<Spittle> spittles) { ... }
```

\* 해당 코드의 'hasPermission() 메소드는, SpEL에 관한 스프링 시큐리티 제공 확장자이며, 검증 시에 수행할 로직을 플러그인하는 기회를 제공

```
public class SpittlePermissionEvaluator implements PermissionEvaluator {
  
  private static final GrantedAuthority ADMIN_AUTHORITY =
      new GrantedAuthorityImpl("ROLE_ADMIN");
  
  public boolean hasPermission(Authentication authentication,
          Object target, Object permission) {
      if (target instanceof Spittle) {
          Spittle spittle = (Spittle) target;
          String username = spittle.getSpitter().getUsername();
          if ("delete".equals(permission)) {
              return isAdmin(authentication) ||
                username.equals(authentication.getName());
          } 
      }
      throw new UnsupportedOperationException(
              "hasPermission not supported for object <" + target
                      + "> and permission <" + permission + ">");
  }
  
  public boolean hasPermission(Authentication authentication,
      Serializable targetId, String targetType, Object permission) {
      throw new UnsupportedOperationException();
  }
  
  private boolean isAdmin(Authentication authentication) {
      return authentication.getAuthorities().contains(ADMIN_AUTHORITY);
  } 
}
```

\* 스프링 시큐리티의 PermissionEvaluator 인터페이스  구현 - 두개의 hasPermission() 메소드....

\* Object target이, Object Permission를 평가하여 동작하는 메소드를 구현

```
@Override
protected MethodSecurityExpressionHandler createExpressionHandler() {
    DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
    expressionHandler.setPermissionEvaluator(new SpittlePermissionEvaluator());
    return expressionHandler;
}
```

\* 구현 후에, ExpressionHandller에서, PermissionEvaluator를 설정해주면 됨
