# 실전 : 책임의 분리
지금까지 배운 스프링 시큐리티의 내용을 실전으로 옮기고,
`JWT`를 사용하여 인증과 권한 부여를 구현한다

구현 시나리오는 다음과 같다
- 클라이언트 : `CURL`을 사용하여 요청을 보낸다
- 인증 서버 : `SMS OTP`로 인증을 수행한다
- 비즈니스 서버 :  시큐리티의 보호 대상인 서버이다.

1. `/login`을 호출해 OTP값을 받는다
2. `/login`에 OTP값을 전달하여 인증을 수행한다
3. 토큰을 요청 헤더에 추가하고 다른 엔드포인트를 호출한다.

## 토큰의 구현과 이용
토큰을 이용하면 클라이언트가 서버에 요청을 보낼 때마다 인증을 수행할 필요가 없다.
토큰은 클라이언트가 서버에 요청을 보낼 때마다 요청 헤더에 추가되어 전달된다.
서버는 토큰을 검증하고, 토큰에 포함된 정보를 이용하여 인증과 권한 부여를 수행한다.

## JWT
`JWT`는 `JSON Web Token`의 약자로, `RFC 7519`에 정의되어 있다.
`JWT`는 `Header`, `Payload`, `Signature` 세 부분으로 구성되어 있다.

- `Header` : 토큰의 타입과 해싱 알고리즘을 지정한다.
- `Payload` : 토큰에 포함될 정보를 지정한다.
- `Signature` : `Header`와 `Payload`를 인코딩한 후, 비밀키로 해싱한 값을 지정한다.


## 인증서버 구현
인증 서버는 `SMS OTP`로 인증을 수행한다.
`/user/auth`을 호출하면 `SMS OTP`를 전송하고, `/login`에 `OTP`값을 전달하여 인증을 수행한다.

- `/user/add` : 사용자를 추가한다
- `/user/auth` : 사용자를 인증한다
- `/otp/check` : `OTP`값을 검증한다

<details markdown="1">
  <summary> USER ENTITY</summary>

```kotlin
@Entity
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    @Column(unique = true)
    val username: String,
    val password: String,

) {}

interface UserRepository: JpaRepository<User, Long> {
    fun findByUsername(username: String): User?
}
```
</details>

<details markdown="1">
  <summary> OTP ENTITY</summary>

```kotlin
@Entity
class Otp(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    val value: String,
    val username: String
) {}

interface OtpRepository: JpaRepository<Otp, Long> {
    fun findByUsername(username: String): Otp?
}
```
</details>
<details markdown="1">
  <summary> Security Config</summary>

```kotlin
@EnableWebSecurity
class SecurityConfig(
) {

    @Bean
    fun configure():HttpSecurity {
        return HttpSecurity {
            it
                .authorizeHttpRequests {
                    it
                        .antMatchers("/user/add").permitAll()
                        .antMatchers("/user/auth").permitAll()
                        .antMatchers("/otp/check").permitAll()
                        .anyRequest().authenticated()
                }
                .formLogin()
        }
    }
}
```
</details>

생략 ...

## 논리 서버 구현
논리 서버는 `JWT`를 사용하여 인증과 권한 부여를 수행한다.

<details markdown="1">
  <summary> Authentication </summary>

```kotlin
class UsernamePasswordAuthentication (
    principal: Any,
    credentials: Any,
    authorities: MutableCollection<out GrantedAuthority>?
): UsernamePasswordAuthenticationToken(principal, credentials, authorities) {
}
```