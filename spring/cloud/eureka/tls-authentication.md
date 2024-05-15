# TLS Authentication

Eureka 서버는 클라이언트가 서버에 연결할 때 인증을 요구할 수 있습니다.\
이를 위해 Eureka 서버는 클라이언트가 제공하는 인증서를 검증합니다.

## 키 생성

```bash
$ keytool -genkeypair -alias eureka-server -keyalg RSA -keysize 2048 -storetype JKS -keystore keystore.jks -validity 3650
$ keytool -export -alias eureka-server -file eureka-server.cer -keystore keystore.jks
```

## Eureka 설정

### 의존성 추가

```groovy
ext {
    set('springCloudVersion', "2023.0.1")
}

dependencies {
    implementation 'org.jetbrains.kotlin:kotlin-reflect'
    implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-server'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

dependencyManagement {
    imports {
        mavenBom "org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}"
    }
}
```

### 서버 설정

```yaml
spring:
  application:
    name: eureka-server

server:
  port: 8761

eureka:
  server:
    enable-self-preservation: true

  client:
    tls:
      enabled: true
      key-store: classpath:keystore.jks
      key-store-password: password
      key-store-type: JKS
      key-alias: eureka-server
      
    service-url:
      defaultZone: http://localhost:8761/eureka/
    register-with-eureka: false
    fetch-registry: false
```

### 클라이언트 설정

```yaml
spring:
  application:
    name: eureka-client

server:
  ssl:
    key-store: classpath:keystore.jks
    key-store-password: 132435!
    key-store-type: JKS
    key-password: 132435!
    client-auth: need

eureka:
  client:
    service-url:
      defaultZone: https://localhost:8761/eureka/
    tls:
      key-store: classpath:keystore.jks
      key-store-password: 132435!
      key-store-type: JKS
      key-password: 132435!
      enabled: true
    register-with-eureka: true

  instance:
    hostname: localhost
    secure-port-enabled: true
    prefer-ip-address: false
```

> TIP \
> 이와 같은 에러 발생시 인증서 확인 
> PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException