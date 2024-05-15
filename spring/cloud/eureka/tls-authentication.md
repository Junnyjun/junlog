# TLS Authentication

Eureka 서버는 클라이언트가 서버에 연결할 때 인증을 요구할 수 있습니다.\
이를 위해 Eureka 서버는 클라이언트가 제공하는 인증서를 검증합니다. 

## 키 생성
```bash
$ keytool -genkeypair -alias eureka -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore eureka.p12 -validity 3650
$ keytool -list -v -keystore eureka.p12

```

## Eureka 서버 설정
```yaml
server:
  port: 8761
  ssl:
    key-store: classpath:eureka.p12
    key-store-password: password
    key-store-type: PKCS12
    key-alias: eureka
    client-auth: need
    enabled: true
```

## 클라이언트 설정
```yaml
eureka:
  client:
    service-url:
      defaultZone: https://localhost:8761/eureka/
  instance:
    hostname: localhost
    secure-port: 8761
    secure-port-enabled: true
    non-secure-port-enabled: false
    home-page-url: https://localhost:8761/
    status-page-url: https://localhost:8761/actuator/info
    health-check-url: https://localhost:8761/actuator/health
    prefer-ip-address: false
    metadata-map:
      instanceId: ${spring.application.name}:${spring.application.instance_id:${random.value}}
```

