# Client\&Cookie

#### 개별 접촉

현대의 웹 사이트들은 개인화된 서비스를 제공하고 싶어한다. Amazon.com의 경우, 여러가지 방식으로 사이트를 개인화시켜서 사용자에게 제공한다.

```
개별 인사 - 사용자가 접속하면 환영 메시지를 띄우는 것
사용자 맞춤 추천 - 고객의 생일이나 고객의 흥미를 학습해 특별한 제품 제시
저장한 사용자 정보 - 고객의 주소와 신용카드 정보 저장
세션 추적 - 장바구니 기능을 위해 사용자의 상태를 저장
```

#### &#x20;사용자 식별 기술

```
사용자 식별 관련 정보를 전달하는 HTTP 헤더들
클라이언트 IP주소 추적으로 알아낸 IP 주소로 사용자를 식별
사용자 로그인 인증을 통한 사용자 식별
URL에 식별자를 포함하는 기술인 뚱뚱한(fat) URL
식별 정보를 지속해서 유지하는 강력하면서도 효율적인 기술인 쿠키
```

### HTTP 헤더

사용자에 대한 정보를 전달하는 HTTP 헤더

| 헤더 이름            | 헤더 타입  | 설명                     |
| ---------------- | ------ | ---------------------- |
|  From            | 요청     | 사용자의 이메일 주소            |
|  User-Agent      | 요청     | 사용자의 브라우저              |
|  Referer         | 요청     | 사용자가 현재 링크를 타고온 근원 페이지 |
|  Authorization   | 요청     | 사용자 이름과 비밀번호           |
|  Client-ip       | 확장(요청) | 클라이언트 IP주소             |
|  X-Forwarded-For | 확장(요청) | 클라이언트 IP주소             |
|  Cookie          | 확장(요청) | 서버가 생성한 ID 라벨          |

#### &#x20;

#### 클라이언트 IP 주소

초기 웹 선구자들은 사용자 식별에 클라이언트 IP 주소를 사용하려 했다.

하지만 IP주소로 사용자를 식별하는 방식은 다음과 같은 약점이 있어 인트라넷 같이 제한된 영역에서는 사용하기도 하지만, 그 외에는 사용하지 않는다

#### &#x20;IP 주소로 사용자를 식별할 수 없는 경우&#x20;

```
여러 사용자가 같은 컴퓨터를 사용 할 때
인터넷 서비스 제공자(ISP)가 사용자가 로그인할 때 동적으로 IP 주소를 할당하는 경우
사용자가 네트워크 주소 변환(NAT)방화벽을 사용할 경우
웹 서버가 클라이언트 IP주소 대신 프락시 서버의 IP주소를 볼 경우 
```

#### 사용자 로그인

웹 서버는 사용자 이름과 비밀번호로 인증(로그인)할 것을 요구해서 사용자에게 명시적으로 식별 요청을 할 수 있다.

#### &#x20;사용자 식별 요청

```
브라우저는 서버로 사이트 접근 요청을 보낸다
사이트는 401 Login Required HTTP 응답 코드와 WWW-Authenticate 헤더를 반환하여 로그인하라고 요청한다
사용자가 입력 후, 브라우저는 기존 요청을 다시 보내서 사용자 식별을 시도한다(Authenticate 헤더 사용)
서버는 사용자의 식별 정보를 안다
이 시점 이후, 브라우저는 서버로부터 사용자 식별 정보를 요청 받으면 자동으로 사용자 이름과 비밀번호를 전달한다.
```

#### 뚱뚱한 URL

사용자의 상태 정보를 포함하고 있는 URL을 뚱뚱한 URL이라도 부른다. 예를들어 Amazon.com에서는 웹 상점을 돌아다니는 사용자에게 할당된 식별번호를 각 URL뒤에 붙여 사용자를 추적한다.

ex) 식별번호 : 002-1145265-8016838

```xml
<a href="/exec/obidos/tg/browse/-/229220/ref=gr_gifts/002-1145265-8016838"> All Fifts </a><br>
```

&#x20;뚱뚱한 URL 기술

```
URL 이 사용자에게 혼돈을 준다
공유하게 되면 개인정보 유출의 위험이 있다
URL이 달라지므로 기존 캐시에 접근할 수 없다
뚱뚱한 URL을 위한 HTML페이지를 다시 그려야 한다
다른 사이트로 이동하면 뚱뚱한 URL 세션에서 이탈할 수 있다. 이 경우 장바구니같은 것들이 초기화된다.
로그아웃 할 시 모든 정보를 잃는다
```

### 쿠키

쿠키는 사용자를 식별하고 세션을 유지하는 방식 중에서 현재까지 가장 널리 사용하는 방식이다

#### 쿠키의 타입

쿠키의 타입은 파기되는 시점에 따라 나뉘어진다.\
세션 쿠키는 사이트를 탐색 할 때 관련한 설정과 선호 사항들을 저장하는 임시 쿠키다.\
지속 쿠키는 사이트에 대한 설정 정보나 로그인 이름을 디스크에 저장해 정보를 유지한다. \
Discard 파라미터가 설정되어 있거나, Expires, Max-age 파라미터가 없으면 세션 쿠키가 된다.&#x20;

#### 쿠키는 어떻게 동작하는가

사용자가 웹 사이트에 방문하면 웹 서버는 사용자에 대해 모른다. \
다시 돌아왔을 때, 해당 사용자를 식별하기 위한 유일한 값을 쿠키에 할당한다. \
쿠키는 임의의 이름=값 형태의 리스트를 가지고, 리스트는 Set-Cookie같은 http 응답헤더에 기술되어 사용자에게 전달된다.

쿠키는 어떤 정보든 포함될 수 있다. \
사용자가 미래에 같은 사이트를 방문하면, 브라우저는 서버가 이 사용자에게 할당했던 쿠키를 Cookie 요청헤더에 기술해 전송한다.

#### 쿠키 상자 : 클라이언트 측 상태

브라우저는 쿠키 정보를 저장할 책임이 있는데, 이 시스템을 '클라이언트 측 상태'라고 한다. \
공식적인 이름은 'HTTP 상태 관리 체계' 이다

#### 구클 크롬 쿠키&#x20;

구글 크롬은 Cookies라는 SQLite 파일에 쿠키를 저장한다.\
한 행이 쿠키 한개에 해당하며, 총 13개의 필드가 잇는데 주요 필드는 다음과 같다

```
creation_utc :  쿠키가 생성된 사점. Jan 1, 1970 00:00:00 GMT이후 초단위
host_key : 쿠키의 도메인
name : 쿠키의 이름이다
value : 쿠키의 값이다
path : 쿠키와 관련된 도메인에 있는 경로다
expire_utc : 쿠키의 파기 시점을 알려주는데, Jan 1, 1970 00:00:00 GMT 로부터 파기된 시간을 초 단위로 기술한다
secure : 이 쿠키를 SSL 커넥션일 경우에만 보낼지를 가리킨다.
```

#### &#x20;마이크로소프트 인터넷 익스플로러 쿠키&#x20;

인터넷 익스플로러 쿠키는 캐시 디렉터리에 개별 텍스트 파일로 저장된다. \
각 쿠키는 여러 행으로 기술되어 있다.

#### 사이트마다 각기 다른 쿠키들

브라우저는 쿠키를 생성한 서버에게만 쿠키에 담긴 정보를 전달한다.&#x20;

서버는 쿠키를 생성할 때, Set-Cookie 응답 헤더에 Domain속성을 기술해서 어떤 사이트가 그 쿠키를 읽을 수 있는지 제어할 수 있다.

```xml
Set-Cookie: user="mary17"; domain="airtravelbargains.com"
```

위와 같은 겅우에 http://www.[airtravelbargains.com](http://airtravelbargains.com/)/specials.html에 접근하면 다음과 같은 쿠키를 얻는다

Cookie: user="mary17"

```xml
Set-Cookie: pref=compact; domain="airtravelbargains.com"; path=/autos/
```

위처럼 특정 경로에 쿠키를 지정하면 다음과 같은 쿠키를 받는다

Cookie: user="mary17"

Cookie: pref=compact

#### 쿠키 구성요소

현재 사용되는 쿠키 명세에는 Version 0 쿠키(넷스케이프 쿠키)와 Version 1쿠키(RFC 2965)가 있다. \
Version 1 쿠키는 Version 0 쿠키의 확장으로 널리 쓰이지는 않는다

#### Version 0 (넷스케이프) 쿠키

&#x20;Set-Cookie 헤더&#x20;

| Set-Cookie 속성 | 예                                                             |
| ------------- | ------------------------------------------------------------- |
| 이름=값          | Set-Cookie: customer=Mary                                     |
| Expires       | Set-Cookie: foo=bar; expires=Wendsday, 09-Nov-99 23:12:40 GMT |
| Domain        | Set-Cookie: SHIPPING=REDEX; domain="joes-hardware.com"        |
| Path          | Set-Cookie: lastorder=00183; path:/orders                     |
| Secure        | Set-Cookie: private\_id=519; secure                           |

&#x20;Cookie 헤더

Cookie: session-id=002-1145265-8016838; session-id-time=1007884800

Version 1 (RFC 2965) 쿠키

쿠키의 확장된 버전은 RFC2965에 정의되어 있다. \
Version 1 표준은 Set-Cookie2와 Cookie2 헤더를 소개해고 있으며, Version0 시스탬과도 호환된다.

아직 모든 브라우저나 서버가 완전히 지원하지는 않는다

쿠키의 Set-Cookie2 필터 중에서, 현재의 웹 사이트에 들어맞는 필터 정보에 달러문자를 붙여서 쿠키와 함께 전송한다

#### 쿠키와 캐싱

문서가 Set-Cookie헤더를 제외하고캐시를 해도 될 경우라면 그 문서에 명시적으로 Cache-Control: no-cache="Set-Cookie"를 기술해서 명확히 표시한다. \
또한, 캐시를 해도 되는 문서에 Cache-Control: public을 사용한다.