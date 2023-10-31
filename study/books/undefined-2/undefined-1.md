# 네트워크 개념

[ ](https://aeunhi99.tistory.com/126#%C-%A-)**1 계층구조의 개념**

1\. 계층적 모듈구조

1\) 모듈화 : 복잡한 시스템을 기능별로 모듈화 하면 시스템 구조가 단순해짐

\* 프로그래밍 언어에서는 함수 개념을 사용해 전체 프로그램을 모듈화 함

&#x20;

2\) 계층 구조 : 특정 모듈이 다른 모듈에 서비스를 제공하는 형식의 계층 구조

\- 네트워크에서도 독립적인 고유기능을 수행하는 모듈들이 상하위의 계층 구조로 연결되어 동작함

\- 계층 구조의 장점

* 전체 시스템을 이해하기 쉽고, 설계 및 구현이 용이
* 모듈간의 표준 인터페이스가 단순해지면 모듈의 독립성을 향상, 시스템 구조를 단순화함
* 대칭 구조에서는 프로토콜을 단순화 시킬 수 있음
* 내부기능의 변화가 전체 시스템의 동작에 영향을 미치지 않음

&#x20;

2\. 프로토콜 설계 시 주의사항

1\) 주소 표현

\- 주소의 역할 : 서로를 구분해줌

\- 주소의 활용도를 높이기 위해 구조적(계층적) 정보를 포함함

ex) 전화번호 : 국가코드-지역코드-번호

\- 1 : 다 통신을 지원함

* 브로드캐스팅 : 모든 호스트에게 데이터 전달 (1:all)
* 멀티캐스팅 : 특정 호스트에게 데이터 전달(1:n)
* 유니캐스트 : 1대 1&#x20;

2\) 오류 제어

* 데이터 변형 오류 : 데이터가 깨져서 도착
* 데이터 분실 오류 : 데이터가 도착하지 못함
* 오류제어기능은 통신 프로토콜의 가장 기본적인 기능이다.

3\) 흐름제어

\- 수신 호스트의 버퍼 처리 속도보다 송신 호스트가 데이터를 전송하는 속도가 빠르면 데이터 분실 오류가 발생가능

\=> 송신 호스트의 전송 속도를 조절하는 흐름 제어기능 필요

&#x20;

4\) 데이터 전달 방식

* 단방향 : 데이터를 한쪽 방향으로만 전송
* 전이중 : 데이터를 양쪽에서 동시에 전송
* 반이중 : 양방향으로 전송할 수 있지만, 특정 시점에서는 한쪽 방향으로만 전송

&#x20;

3\. 서비스 프리미티브

1\) 의미 : 계층 구조 프로토콜에서 하위 계층이 상위 계층에 제공하는 서비스 종류

2\) 종류&#x20;

ⓐ 연결형 서비스 : 3단계로 구성

<figure><img src="https://blog.kakaocdn.net/dn/Y8vjB/btrhzRejoWz/lgvUklGJgBAZQMJ0mX5C21/img.png" alt=""><figcaption></figcaption></figure>

ⓑ 비연결형 서비스 : 전송할 데이터가 있으면 각 데이터를 독립적으로 목적지 호스트로 전송

3\) 기능

<figure><img src="https://blog.kakaocdn.net/dn/bFnxjH/btrhjYl3GdX/GraraXP5AKwBcfa3JGoI31/img.png" alt=""><figcaption></figcaption></figure>

&#x20;

\- Request : 연결 설정 요청(CONNECT.Request), 데이터 전송 요청(DATA.Request), 연결 해제 요청(DISCONNECT.Request)

\- Indication : 연결 설정, 데이터 전송, 연결 해제에 대해 CONNECT.Indication, DATA.Indication, DISCONNECT.Indication 순으로 사용&#x20;

\- Response : 연결 설정 요청은 CONNECT.Response, 데이터는 DATA.Response, 연결 해제는 DISCONNECT.Response로 전달

\- Confirm : 연결 설정은 CONNECT.Confirm, 데이터는 DATA.Confirm, 연결 해제는 DISCONNECT.Confirm로 전달

&#x20;

&#x20;

**2 OSI 참조 모델**

1\. OSI 7계층 모델

1\) 헤더 정보

\- 송신 호스트 : 데이터가 상위 계층에서 하위 계층으로 갈수록 헤더 추가

\- 수신 호스트 : 데이터가 하위 계층에서 상위 계층으로 갈수록 헤더 제거

&#x20;

<figure><img src="https://blog.kakaocdn.net/dn/cs25k4/btrhqfhb9Oe/1gtBpLC0D7X3bJPKFGSVs0/img.png" alt=""><figcaption></figcaption></figure>

2\) 계층별 기능

① 물리 계층(Physical Layer)

• 전송 매체의 물리적 인터페이스에 관한 사항을 기술

• 데이터 전송 속도, 송수신 호스트 사이의 클록동기화 방법, 물리적 연결 형태 등

② 데이터 링크 계층(Data Link Layer)

• 데이터의 물리적 전송 오류를 해결

• 프레임 : 전송 데이터의 명칭&#x20;

③ 네트워크 계층(Network Layer)

• 송신 호스트가 전송한 데이터가 어떤 경로를 통해 수신 호스트에 전달되는지를 결정하는 라우팅 문제를 처리

• 호스트 구분을 위한 주소 개념 필요 (예: IP 주소)

• 패킷 : 전송 데이터의 명칭

• 혼잡 제어 : 데이터 전송 경로의 선택에 따라 네트워크 혼잡에 영향을 미침

④ 전송 계층(Transport Layer)

• 송신 프로세스와 수신 프로세스를 직접 연결하는 단대단 통신 기능 제공

⑤ 세션 계층(Session Layer)

• 송수신 호스트 사이의 대화 제어를 비롯 상호 배타적인 동작을 제어하기 위한 토큰 제어, 일시적인 전송 장애를 해결하기 위한 동기기능 등 제공

⑥ 표현 계층(Presentation Layer)

• 데이터의 의미와 표현 방법을 처리, 데이터를 코딩하는 문제를 다룸

⑦ 응용 계층(Application Layer)

• 최상위, 다양하게 존재하는 응용 환경에서 공통으로 필요한 기능을 다룸

• 대표적인 인터넷 서비스: FTP, Telnet, 전자 메일

&#x20;

[ ](https://aeunhi99.tistory.com/126#%C-%A-)

**3 TCP/IP 모델**

1\. 구현 환경

<figure><img src="https://blog.kakaocdn.net/dn/UJPwl/btrhyjidVqW/Qsk0qed51xSDqLqZH52clK/img.png" alt=""><figcaption></figcaption></figure>

1\) 시스템 공간 (계층 1\~4)

\- TCP(연결형 서비스 제공)와 UDP(비연결형 서비스 제공)는 시스템 운영체제인 커널 내부에 구현됨

\- 네트워크 계층은 IP로 구현, 전송 패킷의 올바른 경로 선택 기능을 제공

2\) 사용자 공간 (계층 5\~7)

\- 사용자 프로그램으로 구현

\- 전송 계층의 기능을 제공하는 소켓 시스템 콜을 호출해 TCP와 UDP 기능을 사용

&#x20;

2\. 프로토콜&#x20;

1\) TCP/IP 계층 구조

\- TCP/UDP : 사용자 데이터를 전송하는 전송 계층 프로토콜

\- IP : 사용제 데이터를 전송하는 네트워크 계층 프로토콜&#x20;

\- ARP : IP 주소를 MAC주소로 변환 / RARP :  MAC주소를 IP 주소로 변환

\- ICMP : 오류 메시지를 전송하는 프로토콜, IP 프로토콜에 캡슐화 되어 전송된다.
