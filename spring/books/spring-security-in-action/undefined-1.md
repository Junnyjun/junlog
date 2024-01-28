# 안녕! 스프링 시큐리티

스프링 부트는 미리 준비된 구성을 제공하므로, 자신이 원하는 구현만 따로 설정하면된다. 이를 **설정보다 관습** 이라 한다.

## 첫 번째 프로젝트 시작

첫번째 예시는 간단한 REST API 하나이다.

{% swagger method="get" path="" baseUrl="https://security.junnyland.com/hello" summary="https://github.com/Junnyjun/spring-jwt/blob/master/src/main/kotlin/git/io/kotlinjwt/security/application/in/HelloController.kt" %}
{% swagger-description %}

{% endswagger-description %}

{% swagger-response status="200: OK" description="Hello!" %}

{% endswagger-response %}

{% swagger-response status="401: Unauthorized" description="{ "status" : 401 ... }" %}

{% endswagger-response %}
{% endswagger %}

Spring boot web, security의존성을 설정하고 프로젝트를 시작하면 아래와 같이\
`Using generated security password: d09c0129-c06d-4ebe-9a94-fe48cc494b74`\
새 암호가 생성된다. Http basic 인증으로 endpoint를 호출하려면 이 암호를 이용하여 호출해야한다

```bash
> curl -u user:d09c0129-c06d-4ebe-9a94-fe48cc494b74 https://security.junnyland.com/hello
```

