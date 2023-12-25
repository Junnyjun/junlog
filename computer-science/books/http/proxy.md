# Proxy

웹 프락시 서버는 클라이언트 입장에서 트랜잭션을 수행하는 중개인이다.\
Cient는 HTTP 서버와 통신하지 않고 Proxy와 통신한다.

<img src="../../../.gitbook/assets/file.excalidraw (4) (1) (1).svg" alt="" class="gitbook-drawing">

하나의 클라이언트만을 위한 프록시를 개인 프록시라고 부르며, 여러 클라이언트가 사용하는 프록시를 공용 프록시라고 부른다.

{% hint style="info" %}
proxy : gateway

proxy는 같은 프로토콜을 사용하는 어플리케이션을 연결하고,\
gateway는 서로 다른 프로토콜을 사용하는 둘 이상을 연결해주는 차이가 존재한다.
{% endhint %}

성인컨텐츠 등을 차단하는 어린이 필터, 보안등급이 있는 문서에 대한 접근 제어, 보안 방화벽, 웹 캐시, Reverse Proxy, Contents Router, TransCoder, Anonymizer 등으로  사용된다.&#x20;

#### 요청 처리방식

Proxy Server들은 origin server에 가까운 Proxy Server를 parent로 둔다. 일반적으로 자신의 parent proxy 혹은 origin server에게 라우팅하는데, contents router, reverse proxy 등의 경우처럼 동적으로 parent를 선정하는 경우가 있다. 이 경우 현재 부모들의 작업량 수준에 근거하여 선택(Load Balancing), 지리적 인접성에 근거하여 선택, 특정 URI의 요청을 처리하는 프락시 서버를 선택, 유료 서비스 가입자를 위한 라우팅 등의 방법이 있다.&#x20;

웹 브라우저에서 Proxy를 사용하도록 설정하여 의도적으로 origin server가 아닌 Proxy로 보낼 수 있다. 그리고 트래픽을 가로채어 Proxy로 리다이렉트하는 방식(Transparent Proxy)과 웹 서버에서 HTTP Redirect 명령을 클라이언트에게 돌려줌으로써 클라이언트의 요청을 Proxy로 리다이렉트할 수있다. 그 외에 Reverse Proxy는 웹서버의 이름과 IP주소를 자신이 직접 사용하여 모든 요청을 서버 대신 자신이 받는다.

{% hint style="info" %}
Proxy가 없는 경우엔 기본스킴으로 'http://' 기본포트로 80 기본 경로를 '/'로 간주한다. 실패할 경우, DNS resolve 를 활용하여 도메인을 검색한다.  웹 브라우저는 Proxy에 요청할 경우 부분 호스트명을 자동확장하지 않는다.
{% endhint %}

#### 추적

보안과 비용 절감을 위해 인터넷 접속시 Cache Proxy Server를 사용하며, 많은 대형 ISP들이 성능 개선과 기능 구현을 위해 Proxy Cache를 사용한다. \
동시에 성능상의 이유로 세계 곳곳에 흩어져 있는 대리 Cache 저장고에 Contents를 복제해두는 방식이 점점 더 흔해지고 있다.

Proxy가 점점 더 흔해지면서, 서로 다른 스위치나 라우터를 넘나드는 IP 패킷의 흐름을 추적하는 것 못지 않게 Proxy를 넘나드는 메시지의 흐름을 추적하고 문제점을 찾아내는 것도 필요한 일이 되었다. Via 헤더 필드는 메시지가 지나는 각 중간 노드(Proxy나 Gateway)의 정보를 나열한다. _(Via: 1.1 proxy-62.irenes-isp.net_[_,_](https://brainbackdoor.tistory.com/proxy-62.irenes-isp.net,) _1.0 cache.joes-hardware.com)_

&#x20;Via 헤더 필드는 메시지의 전달을 추적하고, 메시지 루프를 진단하고, 요청을 보내고 그에 대한 응답을 돌려주는 과정에 관여하는 모든 메시지 발송자들의 프로토콜을 다루는 능력을 알아보기 위해 사용된다.
