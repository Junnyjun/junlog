# CSRF\&CORS 적용

## CSRF
`CSRF`는 `Cross-Site Request Forgery`의 약자로, 사용자가 의도하지 않은 요청을 보내는 공격을 막기 위한 방어 기술이다.\
`CSRF` 공격은 사용자가 의도하지 않은 요청을 보내는 공격으로, 사용자가 의도하지 않은 요청을 보내는 것을 막기 위해 `CSRF` 토큰을 사용한다.

스프링 시큐리티에서는 `CSRF` 공격을 방어하기 위해 `CsrfFilter`를 사용한다. \
`CsrfFilter`는 `CSRF` 토큰을 생성하고, 요청이 유효한지 확인한다. \
`CsrfFilter`는 `CsrfConfigurer`를 통해 설정할 수 있다.

사용자가 처음 웹 페이지를 열때 `CSRF` 토큰을 생성하고, 이 토큰을 쿠키에 저장한다. \
이후 사용자가 요청을 보낼 때마다 `CSRF` 토큰을 함께 보내고, 서버에서는 이 토큰을 검증한다.

```kotlin
class CustomCsrfFilter : Filter {
    private val logger = LoggerFactory.getLogger(CustomFilter::class.java)
    override fun doFilter(req: ServletRequest, res: ServletResponse, filter: FilterChain) {
        val csrf = req.getAttribute("_csrf")
        filter.doFilter(req, res)
    }
}
```

## CSRF 맞춤 구현

<details markdown="1">
  <summary> TokenRepository(JPA) </summary>

```kotlin
@Entity
class Token(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    val token: UUID,
    val identifier: String,
) {
    constructor() : this(0, UUID.randomUUID(), "JUNNYLAND")
}
///
interface TokenRepository :JpaRepository<Token, String>{}
```
</details>

<details markdown="1">
  <summary> CsrfRepository </summary>

```kotlin
class CsrfTokenRdbmsRepository(
    private val csrfTokenRepository: TokenRepository
) : CsrfTokenRepository {
    override fun generateToken(request: HttpServletRequest): CsrfToken =
        DefaultCsrfToken("X-CSRF-TOKEN", "_csrf","${UUID.randomUUID()}")

    override fun saveToken(token: CsrfToken, request: HttpServletRequest, response: HttpServletResponse) {
        csrfTokenRepository.findByIdentifier(request.getHeader("X-IDENTIFIER"))
            ?.let { csrfTokenRepository.save((Token(token = UUID.fromString(token.token), identifier = "JUNNYLAND"))) }
            ?: csrfTokenRepository.save(Token(token = UUID.fromString(token.token), identifier = "JUNNYLAND"))
    }
    override fun loadToken(request: HttpServletRequest): CsrfToken? =
        csrfTokenRepository.findByIdentifier(request.getHeader("X-IDENTIFIER"))
            ?.let { DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", it.token.toString()) }
}
```
</details>

## CORS
`CORS`는 `Cross-Origin Resource Sharing`의 약자로, 다른 도메인 간에 자원을 공유하기 위한 방법이다.\
허용된 도메인에서만 요청을 받아들이고, 허용된 도메인에서만 응답을 보내는 방식이다.

스프링 시큐리티 에서는 `Access-Control-Allow-Origin` 헤더를 사용하여 `CORS`를 설정할 수 있다.\
`CORS` 설정은 `CorsConfigurer`를 통해 설정할 수 있다.

<details markdown="1">
  <summary> @CrossOrigin </summary>

```kotlin
@RestController
@CrossOrigin(origins = ["junnyland.com"])
class HelloController {

    @GetMapping("/hello")
    fun hello(): String = "Hello World!"
}
```

</details>

<details markdown="1">
  <summary> CorsConfigurer </summary>

```kotlin
@Configuration
class CorsConfig : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:8080")
            .allowedMethods("GET", "POST", "PUT", "DELETE")
    }
}
```
