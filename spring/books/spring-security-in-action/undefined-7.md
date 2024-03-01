# 제한 적용

Endpoint 별로 제한을 걸어서 사용자의 요청을 제한할 수 있다.\
제한을 걸 수 있는 대상은 IP, 사용자, 요청 횟수, 시간 등이 있다.\
제한을 걸어서 사용자의 요청을 제한하는 방법은 다음과 같다.

<details markdown="1">
  <summary> MVC matcher </summary>

```kotlin
@Bean
fun securityFilterChain(http: HttpSecurity): SecurityFilterChain = http
        .authorizeHttpRequests { it
                .mvcMatchers("/write").hasAuthority("WRITE")
                .mvcMatchers("/read").hasAnyAuthority("READ", "WRITE")
                .mvcMatchers("/admin").access("hasAuthority('ADMIN') and !hasAuthority('READ')")
        }
        .authenticationProvider(authenticationProvider)
        .build()
```
</details>
MVC matcher를 이용하여 Endpoint 별로 제한을 걸 수 있다.\
MVC matcher는 MVC 구문으로 경로를 지정하며, `@RequestMapping` , `@GetMapping` , `@PostMapping` 등을 이용하여 경로를 지정할 수 있다.

<details markdown="1">
  <summary> Ant matcher </summary>

```kotlin
@Bean
fun securityFilterChain(http: HttpSecurity): SecurityFilterChain = http
        .authorizeHttpRequests { it
                .antMatchers("/**/write").hasAuthority("WRITE")
        }
        .authenticationProvider(authenticationProvider)
        .build()
```
</details>
Ant matcher를 이용하여 경로를 지정할 수 있다.\
Ant matcher는 경로를 지정할 때 와일드카드를 이용하여 경로를 지정할 수 있다.

<details markdown="1">
  <summary> Reqex matcher </summary>

```kotlin
@Bean
fun securityFilterChain(http: HttpSecurity): SecurityFilterChain = http
        .authorizeHttpRequests { it
                .regexMatchers("/[0-9]+").hasAuthority("WRITE")
        }
        .authenticationProvider(authenticationProvider)
        .build()
```
</details>
Regex matcher를 이용하여 경로를 지정할 수 있다.\
Regex matcher는 정규식을 이용하여 경로를 지정할 수 있다.








