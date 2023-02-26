# Web Server

Web server는 Http 요청을 처리하고 응답을 제공한다.\
웹서버는 Http\&TCP 처리를 구현한 것이다.\
Http 프로토콜을 구현하고, 웹 리소스를 관리한다

<img src="../../.gitbook/assets/file.excalidraw (2).svg" alt="" class="gitbook-drawing">

웹서버는 역방향 DNS를 사용하여 클라이언트의 IP 주소를 클라이언트의 호스트 명으로 변환하도록 설정되어있다.

{% hint style="info" %}
ident :  서버에서 어떤 사용자가 HTTP 커넥션을 초기화 했는지 찾을 수 있게 해준다.\
113번 포트를 사용하며 로깅에 유용하지만, 공공 인터넷에서는 여러가지 이유로 잘 사용하지 않는다.
{% endhint %}

#### &#x20;HTTP REQUEST

Connection에 데이터가 도착하면 웹서버는 데이터를 파싱하여 요청 메시지를 구성한다.\
요청 메서드, 지정된 리소스의 식별자, 번호를 찾는다.\
헤더를 읽는다 (CRLF)\
요청 본문을 읽어들인다

#### HTTP RESPONSE

MIME타입을 지정한다.\
성공 메시지 대신 Redirect를 하는 경우도 있다.

