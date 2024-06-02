# Oauth2가 작동하는 방법

## Oauth2 프레임워크

Oauth2는 클라이언트 애플리케이션이 사용자의 리소스에 접근할 수 있도록 권한을 부여하는 프레임워크 입니다.\
타사 서비스나, 자체 서비스에 대한 인증 및 권한 부여를 위해 Oauth2를 사용할 수 있습니다

<img src="../../../.gitbook/assets/file.excalidraw (4).svg" alt="" class="gitbook-drawing">

## Oauth2 구성 요소

oauth2는 다음과 같은 구성 요소로 구성되어 있습니다.

* `Resource Server` : 리소스에 대한 접근을 제어하는 서버
* `Resource Owner` : 리소스에 대한 접근을 허용하는 사용자
* `Client` : 리소스에 접근하는 애플리케이션
* `Authorization Server` : 클라이언트에게 접근 권한을 부여하는 서버

<img src="../../../.gitbook/assets/file.excalidraw (47).svg" alt="" class="gitbook-drawing">

### Grant

승인 토큰을 얻는 방법인 Grant는 얻는 여러 방법을 제공합니다.

* `Authorization Code` : 클라이언트가 사용자의 리소스에 접근하기 위해 사용자의 권한을 얻는 방법
* `Implicit` : 클라이언트가 사용자의 리소스에 접근하기 위해 사용자의 권한을 얻는 방법
* `Resource Owner Password Credentials` : 클라이언트가 사용자의 리소스에 접근하기 위해 사용자의 권한을 얻는 방법
* `Client Credentials` : 클라이언트가 사용자의 리소스에 접근하기 위해 사용자의 권한을 얻는 방법
