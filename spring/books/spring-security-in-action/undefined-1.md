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

이로써, 기본 설정으로 스프링 시큐리티가 작동한것을 확인해 보았다.

## 기본 구성

<img src="../../../.gitbook/assets/file.excalidraw.svg" alt="" class="gitbook-drawing">

1. Authentication Filter는 인증 요청을 Authentication Manager에 위임후 Security Context구성
2. Authentication Manager는 Authentication Provider를 이용해 인증 처리
3. Authentication Provider는 인증 과정 구현
4. 구현 과정중 필요한 provider를 이용
5. 인증후 Security Context에 데이터 유지

### 기본 설정

User DetailService : 사용자에 관한 세부 정보를 등록하는 Bean이다.

PasswordEncoder : 암호를 인코딩한다, 암호가 서로 일치하는지 검증한다

#### HTTPS 설정

openssl을 이용한 자체 서명 인증서 생성

```bash
openssl req -newkey rsa:2048 -nodes -keyout key.pem -x509 -days 365 -out certificate.pem
openssl pkcs12 -export -in certificate.pem -inkey key.pem -out certificate.p12

---
Country Name (2 letter code) [AU]:KR
State or Province Name (full name) [Some-State]:korea
Locality Name (eg, city) []:Seoul
Organization Name (eg, company) [Internet Widgits Pty Ltd]:junnyland
Organizational Unit Name (eg, section) []:junny
Common Name (e.g. server FQDN or YOUR name) []:security
Email Address []:junnyland@test.com

Enter Export Password:
Verifying - Enter Export Password:
```

서버 설정

```yaml
server:
  ssl:
    key-store-type: PKCS12
    key-store: classpath:certificate.p12
    key-store-password: 1234
```

## 구성 재정의
