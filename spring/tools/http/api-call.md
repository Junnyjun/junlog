# RestTemplate Logging

효율적인 RestTemplate 요청/응답 로깅을 구현하는 방법

## BAD CASE

나쁜 케이스로는 특정 레벨을 디버그로 처리하는 방식입니다.

`logging.level.org.springframework.web.client.RestTemplate=DEBUG`

요청 URL및 상태가 찍히지만 정리되지 않고 여러 줄이 찍혀 어지러운 모습입니다.

```
o.s.w.c.RestTemplate - HTTP POST http://localhost:8082/spring-rest/persons
o.s.w.c.RestTemplate - Accept=[text/plain, application/json, application/*+json, */*]
o.s.w.c.RestTemplate - Writing [my request body] with org.springframework.http.converter.StringHttpMessageConverter
o.s.w.c.RestTemplate - Response 200 OK
```

또한 응답(body)는 확인할 수 없기에, 단점이 존재합니다

## GOOD CASE

`ClientHttpRequestInterceptor`를 사용해 로깅을 하는 방식입니다.

```kotlin
@Configuration
class RestTemplateConfig {
    @Bean
    fun restTemplate():RestTemplate = RestTemplate()
        .also {
            it.interceptors.add(LoggingRequestInterceptor(
                listOf(CONTENT_TYPE, ACCEPT),
                listOf("KEY"))
            )
        }

    class LoggingRequestInterceptor(
        private val valuableRequest :List<String>,
        private val valuableResponse :List<String>

    ) : ClientHttpRequestInterceptor {
        private val log: Logger = getLogger("REST API")

        override fun intercept(request: HttpRequest, body: ByteArray, execution: ClientHttpRequestExecution): ClientHttpResponse {
            log.info("[API REQUEST] URI: ${request.method} ${request.uri} HEADER : [${valuableRequestHeader(request.headers, valuableRequest)}] BODY : [${String(body)}]")
            val response = execution.execute(request, body)
            log.info("[API RESPONSE] URI: ${response.statusCode} ${response.statusText} HEADER : [${valuableResponseHeader(response.headers, valuableResponse)}] BODY : [${String(body)}]")
            return response
        }
        private fun valuableRequestHeader(httpHeaders: HttpHeaders,key:List<String>) = httpHeaders
            .filter { key.contains(it.key) }

        private fun valuableResponseHeader(httpHeaders: HttpHeaders,key:List<String>)= httpHeaders
            .filter { key.contains(it.key) }
    }
}
```

```
INFO REST API : [API REQUEST] URI: POST https://dummy.restapiexample.com/api/v1/create HEADER : [{Accept=[text/plain, application/json, application/*+json, */*], Content-Type=[text/plain;charset=ISO-8859-1]}] BODY : [{
    "status": "success",
    "data": {
    "name": "junnyland",
    "salary": "10000000",
    "age": "27",
    "id": 100
}]
INFO REST API : [API RESPONSE] URI: 200 OK OK HEADER : [{}] BODY : [{
    "status": "success",
    "data": {
    "name": "junnyland",
    "salary": "10000000",
    "age": "27",
    "id": 100
}]
```

이와같이 요청, 응답의 대한 로그를 정확하게 찍을 수 있습니다.
