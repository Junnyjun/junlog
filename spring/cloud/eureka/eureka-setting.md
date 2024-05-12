# Eureka setting

Eureka 는 자가 등록, 동적 발견 및 부하 분산을 담당하며 위의 서비스 디스커버리 패턴을 구현할 수 있도록 도와준다.

| Eureka Server                                                                                  | Eureka Client                                                                                                                                       |
| ---------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------- |
| 모든 마이크로서비스가 자신의 가용성을 등록하는 레지스트리                                                                | 등록된 마이크로서비스를 호출해서 사용할 때 Eureka Client 를 이용해서 필요한 서비스를 발견                                                                                            |
| <p>등록되는 정보는 서비스 ID와 URL 이 포함되는데,<br>마이크로서비스는 Eureka Client 를 이용해서 이 정보를 Eureka Server 에 등록</p> | <p>Eureka Server 는 Eureka Server 인 동시에 서로의 상태를 동기화하기 서로를 바라보는 Eureka Client 이기도 한다<br>이 점은 Eureka Server 의 고가용성을 위해 여러 대의 Eureka Server 운영 시 유용</p> |

## START EUREKA

### SERVER SETTING

**의존성 추가**\
springCloudVersion 버전 [참고](https://github.com/spring-cloud/spring-cloud-release/wiki/Supported-Versions#supported-releases)

{% code title="build.gradle" %}
```gradle
plugins {
  id 'java'
  id 'org.springframework.boot' version '3.2.0'
  id 'io.spring.dependency-management' version '1.1.4'
}

repositories {
  mavenCentral()
}

ext {
  set('springCloudVersion', "2023.0.0")
}

dependencyManagement {
  imports {
    mavenBom "org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}"
  }
}

```
{% endcode %}

**설정 활성화**

{% code title="mainApplication.kt" %}
```kotlin
@SpringBootApplication
@EnableEurekaServer
class EurekaApplication

fun main(args: Array<String>) {
	runApplication<EurekaApplication>(*args)
}
```
{% endcode %}

**설정 파일**

{% code title="application.yml" %}
```yaml
spring:
  application:
    name: eureka-server
  profiles:
    active: dev
server:
  port: 8761



eureka:
  server:
    enable-self-preservation: true
  instance:
    hostname: api-discovery.com
    secure-port: ${server.port}
  client:
    register-with-eureka: false
    fetch-registry: false
    service-url:
      defaultZone: http://api-discovery.com:8761/eureka
    disable-delta: true
```
{% endcode %}

을 지정해 준뒤 실행하면

<figure><img src="../../../.gitbook/assets/image (4).png" alt=""><figcaption></figcaption></figure>

### CLIENT SETTING

**의존성 추가**

{% code title="build.gradle" %}
```gradle
implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-client'
```
{% endcode %}

설정 활성화

```kotlin
@SpringBootApplication
@EnableEurekaClient
public class EurekaclientApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaclientApplication.class, args);
    }

}
```

설정 파일

```yaml
server:
  port: 8080

spring:
  application:
    name: user-Service
  profiles:
    active: dev


eureka:
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://localhost:8761/eureka
```

동록됫다는 로그를 확인할 수 있다.

```
2023-06-26 17:50:02.551  INFO 3560 --- [           main] .s.c.n.e.s.EurekaAutoServiceRegistration : Updating port to 8080
2023-06-26 17:50:02.626  INFO 3560 --- [nfoReplicator-0] com.netflix.discovery.DiscoveryClient    : DiscoveryClient_USER-SERVICE/host.docker.internal:user-Service:8080 - registration status: 204
2023-06-26 17:50:02.788  INFO 3560 --- [           main] git.io.apiuser.ApiUserApplicationKt      : Started ApiUserApplicationKt in 4.559 seconds (JVM running for 5.463)
```
