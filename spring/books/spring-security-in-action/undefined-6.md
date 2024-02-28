# 액세스 제한

식별된 리소스가 요청된 리소스에 접근할 권한이 있는지를 확인하는 절차이다.

<img src="../../../.gitbook/assets/file.excalidraw (42).svg" alt="" class="gitbook-drawing">

## 권한&역할 접근 제한

`UserDetails`의 `GrantedAuthority`를 이용하여 권한을 확인한다.

```kotlin
class CustomUserDetails(
    private val user: User
) : UserDetails {
    override fun getAuthorities(): List<GrantedAuthority> = user.authorities
}
```

`UserDetails`의 `getAuthorities`를 이용하여 사용자의 권한을 확인한다.

### 권한 적용
- `hasAuthority` : 특정 권한을 가지고 있는지 확인
- `hasAnyAuthority` : 여러 권한 중 하나라도 가지고 있는지 확인
- `access` : SpEL을 이용하여 권한을 확인0

<details markdown="1">
  <summary> SecurityConfiguration </summary>

```kotlin
@Bean
fun securityFilterChain(http: HttpSecurity): SecurityFilterChain = http
        .authorizeHttpRequests { it
                .requestMatchers("/write").hasAuthority("WRITE")
                .requestMatchers("/read").hasAnyAuthority("READ", "WRITE")
                .requestMatchers("/admin").access("hasAuthority('ADMIN') and !hasAuthority('READ')")
        }
        .authenticationProvider(authenticationProvider)
        .build()
```
</details>

### 역할 적용
- `hasRole` : 특정 역할을 가지고 있는지 확인
- `hasAnyRole` : 여러 역할 중 하나라도 가지고 있는지 확인
- `access` : SpEL을 이용하여 역할을 확인

역할은 `ROLE_`로 시작하며 `GrantedAuthority`에 저장된다.

<details markdown="1">
  <summary> SecurityConfiguration </summary>

```kotlin
@Bean
fun securityFilterChain(http: HttpSecurity): SecurityFilterChain = http
        .authorizeHttpRequests { it
                .requestMatchers("/write").hasRole("WRITER")
                .requestMatchers("/read").hasAnyRole("READER", "WRITER")
                .requestMatchers("/admin").access("hasRole('ADMIN') and !hasRole('READER')")
        }
        .authenticationProvider(authenticationProvider)
        .build()
```
</details>