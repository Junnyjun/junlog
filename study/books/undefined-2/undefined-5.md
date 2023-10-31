# 네트워크 계층

### 01 IPv6 프로토콜

&#x20;\- 32비트의 주소 공간을 지원하는 현재의 IP 프로토콜은 최대 2의 32 승개의 호스트를 수용할 수 있지만 주소가 부족해 IPv6 버전을 사용

* 주소 공간 확장
* 헤더 구조 단순화
* 흐름 제어 기능 지원(Flow Label)

#### &#x20;1. IPv6 헤더 구조

<figure><img src="https://blog.kakaocdn.net/dn/cvwe1A/btrdr3CQDw0/Ri8U4ISLBzTMITmQAEdAp0/img.png" alt="" height="301" width="371"><figcaption></figcaption></figure>

&#x20;\- 총 40바이트 중에서 32바이트는 주소 공간으로 할당되고, 8바이트만 프로토콜 기능을 위해 사용.

&#x20; IPv6 기본 헤더 바로 뒤에 확장 헤더를 하나 이상 둘 수 있는데, 확장 헤더의 종류는 다음과 같다.

* Hop-by-Hop Options Header
* Routing Header : IPv4의 소스 라우팅과 유사한 기능 제공, 특정 노드를 경유하여 전송되도록 한다.
* Fragment Header : IPv4 프로토콜 헤더에 정의된 Offset, Identification, MF 필드처럼 패킷 분할과 관련된 정보
* 등등..

&#x20;

&#x20; **1.1 DS/ECN 필드**

&#x20;\- 차등 서비스가 도입되면서 6비트의 DS 필드와 2비트의 ECN 필드가 정의되었다. 이 공간은 원래 4비트의 Priority 필드의 앞부분 4비트의 공간으로 사용되던 곳.

&#x20; **1.2 Flow Label 필드**

&#x20;\- IPc4에서 생성하는 패킷은 라우터가 중개할 때 동일한 기준을 적용하여 처리한다. IPv6에서는 특정 송수신 호스트 사이에 전송되는 데이터를 하나의 흐름으로 정의해 중간 라우터가 이 패킷을 특별한 기준으로 처리할 수 있도록 지원.

&#x20; : 음성이나 영상 데이터처럼 실시간 서비스가 필요한 응용 환경에서 사용하며 기본원칙은 아래와 같다.

* 필드를 지원하지 않은 호스트는 IPv6 패킷을 생성할 때 반드시 0으로 지정해야 한다.
* 필드의 값이 0 이외의 동일한 번호로 부여받은 패킷은 모두 동일하게 지정해야 한다.
* 필드 값은 최대 범위 내에서 랜덤하게 선택되는데 동일 번호가 부여되지 않도록 해야 한다.

&#x20;

&#x20; **1.3 기타 필드**

* Version Number&#x20;
* Payload Length : 헤더를 제외한 패킷의 크기(바이트)
* Next Header(다음 헤더) : 기본 헤더 다음에 이어지는 헤더의 유형을 수신 호스트에 알려준다.
* Hop Limit(홉 제한) : TTL(Time To Live) 필드와 동일한 역할을 수행. 패킷이 라우터에 의해 중개될 때마다 감소되며, 0이 되면 해당 패킷은 네트워크에서 사라진다.

&#x20;

#### &#x20;2. IPv6 주소

&#x20; **2.1 주소 표현**

<figure><img src="https://blog.kakaocdn.net/dn/bcmOfD/btrdptpeXjH/he08Miyeagbcrbs97PLac0/img.png" alt="" height="167" width="463"><figcaption></figcaption></figure>

&#x20;\- 길기때문에 표기가 불편하므로 IPv4와 함께 사용하는 환경에서 주소를 캡슐화해 사용하기도 한다.

&#x20;ex) X:X:X:..:d.d.d.d -> X는 16비트이므로 총 96비트이고, d.d.d.d는 8비트로 총 32비트다 = 128비트

&#x20;

&#x20; **2.2 주소 공간**

<figure><img src="https://blog.kakaocdn.net/dn/bUU6e2/btrdpMIQQML/l5iso7KHRdAKQQOOPZFBD0/img.png" alt="" height="373" width="528"><figcaption></figcaption></figure>

### 02 이동 IP 프로토콜

&#x20;\- 현재 진행되는 인터넷 환경 변화와 관련해 가장 많이 연구되는 분야 중 하나는 이동하는 사용자가 서비스 중단 없이 인터넷에 접속할 수 있는 이동 환경 서비스를 수용하는 문제이다.

#### &#x20;1. 터널링 원리

&#x20;\- 이동 호스트가 자신의 고유 주소를 유지하면서 인터넷 서비스를 받으려면 계속 이동하는 송수신 호스트 간의 데이터 라우팅 처리가 중요하다.

&#x20; **1.1 상이한 전송 수단**

<figure><img src="https://blog.kakaocdn.net/dn/AJurm/btrdoohHqIM/kLL7v90g3cJzFhU0sYkGeK/img.png" alt="" height="434" width="397"><figcaption></figcaption></figure>

&#x20;\-> 홍길동이 (a)->(d)로 가며 (b), (c)를 거친다. IP 프로토콜을 이용하며 버스, 배만 바뀔 뿐 홍길동은 같다.

&#x20;문제는 버스에서 배를 갈아탈 때 스스로 IP 프로토콜을 교체하는 작업이 추가로 이뤄져야 한다.

&#x20;

&#x20; **1.2 터널링 방식**

\- 위의 문제를 간단히 해결하는 법으로 터널링 기능으로 최종 사용자인 홍길동은 IP 프로토콜의 교체 과정에 개입하지 않는다.

<figure><img src="https://blog.kakaocdn.net/dn/d8pp71/btrdrlKwQxv/lXm6rjxs8UFLmfXaPV5G60/img.png" alt="" height="469" width="402"><figcaption></figcaption></figure>

&#x20;\-> 버스에 배라는 캡을 씌운 느낌

&#x20;

#### &#x20;2. IP 터널링

&#x20;\- 컴퓨터 네트워크 환경에서 두 개의 호스트를 연결해 통신하려면 조건이 필요

* 통신할 상대방을 다른 상대자와 구분하는 것

&#x20;

<figure><img src="https://blog.kakaocdn.net/dn/62bwd/btrdo8FD4pj/fNxwtaEfN5dJkUe5K4UeJ0/img.png" alt="" height="266" width="515"><figcaption></figcaption></figure>

&#x20;\- 무선 호스트가 이동할 때 발생하는 데이터 경로 문제를 해결하기 위한 이동 IP 프로토콜로 먼저 이동 호스트의 위치가 바뀌면 새로운 위치를 관장하는 포린 에이전트(FAnew)로부터 COA(Care of Address)를 얻는다. 이 주소는 이동 호스트의 홈 에이전트 HA에 등록되어 FAnew와 HA 사이에 터널을 형성하는 데 사용된다. HA로 라우팅 된 패킷을 이동 호스트에 전달하려면 새로 형성된 터널을 통해 FAnew로 전달해야 한다.

&#x20;\- 이동 호스트는 IP 프로토콜 헤더의 Source Address 필드에 표기된 주소를 자신의 홈 주소로 설정해 패킷을 전송한다. 패킷을 수신할 때는 반대로 Destination Address 필드

&#x20;\- COA는 이동 호스트가 위치를 변경할 때 새로 이동한 지역에서 일시적으로 할당된 IP 주소여서 호스트가 이동할 때마다 새로운 COA가 할당되고 기존 COA는 회수되는 과정이 반복.

<figure><img src="https://blog.kakaocdn.net/dn/mHMIx/btrdr3CRPO3/CKhG4A0JZKs7Pih6k7WgKk/img.png" alt="" height="339" width="475"><figcaption></figcaption></figure>

&#x20;\- 홈 에이전트와 이동 에이전트 사이에 설정되는 터널로 IP 패킷을 목적지까지 전송하기 위한 중간 단계의 새로운 경로이고 송수신 호스트 사이에서 동작하는 IP 프로토콜과는 별도로  패킷을 중개해야 한다.

&#x20;터널 구간을 지나는 과정에서 라우팅 처리가 필요한데 여기서 IP 프로토콜을 사용하며 패킷을 데이터로 취급하는 새로운 형태의 IP 캡슐 패킷이 구성되어 전달된다.

&#x20;

### 03 기타 네트워크 계층 프로토콜

&#x20;\- IP 프로토콜은 응용 계층에서 생성된 사용자 데이터를 전송하기 위해 사용된다. 전송 과정이 올바르게 이뤄지려면 다양한 제어 프로토콜이 필요.(오류-ICMP, ARP/RARP, IGMP)

&#x20;

#### 1. ARP 프로토콜

\- 네트워크 환경에서 임의의 호스트가 다른 호스트에 데이터를 전송하려면 수신 호스트의 IP 주소뿐 아니라, MAC 주소도 알아야 한다. 수신 호스트의 IP 주소는 보통 응용 프로그램 사용자가 프로그램을 실행하는 과정에서 직접 입력하므로 IP주소로부터 수신 호스트의 MAC 주소를 얻는 작업이 추가로 필요.

&#x20;

&#x20;**1.1 MAC 주소**

<figure><img src="https://blog.kakaocdn.net/dn/wDS7v/btrdo9dv2rw/B9Cvkyrqif3cK9DRFuo0mk/img.png" alt="" height="328" width="576"><figcaption></figcaption></figure>

&#x20;\- 송신 호스트가 물리 계층을 통해 데이터를 전송하는 과정에서 필요한 주소를 설명하며 송신 호스트의 IP 주소는 자신의 파일시스템에 보관되어있고 수신 호스트의 IP 주소는 일반 사용자가 접속하고자 하는 호스트의 IP 주소를 지정해준다. 사용자는 일반적으로 도메인 이름을 입력하는데, 도메인 이름은 DNS 서비스를 통해 IP 주소로 쉽게 변환할 수 있다.

\-송신 호스트의 MAC 주소는 자신의 LAN 카드에 내장되므로 이 값을 읽으면 된다.&#x20;

수신 호스트의 MAC 주소는 수신 호스트의 IP 주소를 매개변수로 ARP 기능을 통해 얻어야 한다.

&#x20;\--> ARP request 라는 특수 패킷을 브로드 캐스팅하여 호스트 B만 IP 주소와 동일함을 인지해 B는 ARP reply 패킷을 사용해 자신의 MAC 주소를 A에 회신한다.

&#x20;\- 데이터를 전송할 때마다 ARP를 사용하면 네트워크 트래픽이 증가하기 때문에 가장 최근에 얻은 IP 주소와 MAC 주소 매핑 값을 보관하는 캐시 정보를 이용한다.

&#x20;

&#x20;**1.2 RARP 프로토콜의 필요성**

(Reverse Address Resolution Protocol) : MAC 주소를 이용해 IP 주소를 제공

\- 디스크가 존재하지 않는 시스템이나 X 윈도우 터미널 등에서는 자신의 LAN 카드 정보를 읽어 MAC 주소를 얻을 수 있지만 , 파일 시스템이 없으므로 IP 주소를 보관할 방법이 없다. 이런 경우 자신의 MAC 주소와 IP 주소의 매핑 값을 보관하는 서버 호스트로부터 IP 주소를 얻어야 한다.

<figure><img src="https://blog.kakaocdn.net/dn/dDAqb7/btrdswSwqsQ/NzRW86LjIxwgkt01OoJmx0/img.png" alt="" height="318" width="549"><figcaption></figcaption></figure>

\-> 위의 경우로 IP 주소를 얻고자 하는 호스트는 MAC 주소를 매개변수로 패킷을 브로드 캐스팅한다.

서버에는 RARP 기능을 전담으로 수행하는 서버가 하나 이상 존재하기 때문에 모든 호스트가 RARP 변환 요청을 받아도 해당 정보를 보관하는 RARP 서버만 응답해서 자신의 IP 주소를 얻고, 정해진 호스트로부터 자신의 부트 이미지를 다운로드한다.&#x20;

&#x20;

#### 2. ICMP 프로토콜

(Internet Control Message Protocol) : 인터넷 환경에서 오류에 관한 처리를 지원한다. IP 프로토콜은 데이터 전송 과정에서 패킷 폐기 등의 오류가 발생해도 보고하는 기능이 없어 오류가 발생한 IP 패킷에 대하여 그 원인을 송신 호스트에 전달.

&#x20;

&#x20;**2.1 ICMP 메시지**

\- 종류는 Type 필드 값에 따라 오류 보고 메시지와 질의 메시지로 나뉜다.

오류보고 메시지(Error-Reporting Message) - IP 패킷을 전송하는 과정에서 발생하는 문제를 보고하는 것이 목적이며 IP 패킷을 전송한 송신 호스트에 전달(ICMP는 오류 발생 사실을 통보하는 것 오류 해결을 상위 계층의 몫)

* DESTINATION UNREACHABLE(DU) - 수신 호스트가 존재하지 않거나 존재해도 필요한 프로토콜이나 포트 번호 등이 없어 수신 호스트에 접근이 불가능한 경우 발생(IP 헤더의 DF 필드가 설정된 패킷을 라우터가 분할해야 하는 경우에도 해당 패킷을 버리고 이 메시지를 회신)
* SOURCE QUENCH(SQ) - 네트워크에 필요한 자원이 부족하여 패킷이 버려지는 경우 발생(이 메시지를 이용해 송신 호스트에 혼잡 가능성을 경고)
* TIME EXVEEDED(TE) - 패킷의 TTL필드 값이 0이 되어 패킷이 버려진 경우 발생

질의 메시지(Query Message) - 라우터 혹은 다른 호스트들의 정보를 획득하려는 목적으로 사용

* ECHO REQUEST, ECHO REPLY - 유닉스의 PING 프로그램에서 네트워크의 신뢰성을 검증하기 위해 ECHO REQUEST 메시지를 전송하고 이를 수신한 호스트는 REPLY를 전송해 응답해 특정 호스트가 인터넷에서 활성화되어 동작하는지 확인 가능
* TIMESTAMP REQUEST, TIMESTAMP REPLY - 두 호스트 간의 네트워크 지연을 계산하는 용도로 사용

&#x20;

&#x20;**2.2 ICMP 헤더 형식**

<figure><img src="https://blog.kakaocdn.net/dn/ODtHI/btrdqhV8tsm/BCSid4OkCV32cyqAWKoCX0/img.png" alt="" height="230" width="516"><figcaption></figcaption></figure>

* Type(유형) : 1바이트 크기로 메시지의 종류를 구분.(DU, SQ, TE..)
* Code : 메시지 내용에 대한 자세한 정보를 제공하는 매개변수 값
* 체크섬

<figure><img src="https://blog.kakaocdn.net/dn/LquiO/btrdpsDQdq2/GOphHknA7egEodRdJHl8yk/img.png" alt="" height="184" width="474"><figcaption></figcaption></figure>

&#x20;**2.3 ICMP 메시지 전송**

\- ICMP는 기능적으로 IP 프로토콜과 같은 네트워크 계층의 역할을 수행하지만 2 계층에 바로 전달되지 않고, IP 패킷에 캡슐화된 후에 전달된다

<figure><img src="https://blog.kakaocdn.net/dn/dzwCTl/btrdrv7o0t6/VtDl4sDQKIuAclf1CgYd0K/img.png" alt="" height="228" width="493"><figcaption></figcaption></figure>

&#x20;

#### 3. IGMP 프로토콜

&#x20;멀티캐스팅(Multicasting) : 특정 그룹에 속하는 모든 호스트에 메시지를 전송하는 방식

이때 필요한 라우팅 알고리즘을 멀티캐스트 라우팅(Multicast Routing)이라 한다.

&#x20;

&#x20;**3.1 그룹 관리**

&#x20;\- 멀티캐스트 라우팅에서는 다수의 호스트를 논리적인 하나의 단위로 관리하기 위한 그룹 관리 기능이 필요.

목적지 주소가 멀티캐스트 그룹 주소로 지정된 패킷을 그룹의 모든 호스트에 전달하려면 라우터가 입력 패킷을 호스트의 수만큼 복사해 전달하는 기능을 수행해야 한다.

* 다중 수신 호스트를 표시하는 멀티캐스트 그룹 주소 표기 방법 통일
* 라우터에서 IP 멀티캐스트 주소와 이 그룹에 속하는 멤버 호스트의 네트워크 주소 사이의 연관성
* 멀티캐스트 라우팅 알고리즘은 그룹에 속한 모든 멤버에게 도달하는 가장 짧은 경로를 선택하는 기능을 제공

&#x20;

&#x20;**3.2 IGMP 헤더 구조**

(Internet Group Management Protocol) : 임의의 호스트가 멀티캐스트 주소로 정의된 멀티캐스트 그룹에 가입하거나 탈퇴할 때 사용하는 프로토콜. 또한 멀티캐스트 그룹에 가입한 호스트와 라우터 사이에 멤버 정보를 교환하는 용도로도 사용

<figure><img src="https://blog.kakaocdn.net/dn/bbd4MV/btrdo35nLYg/uIZjXSRz3CYUPjdTvp1DX1/img.png" alt="" height="155" width="570"><figcaption></figcaption></figure>

* Type : 세 가지 값으로 0x11(멀티캐스트 라우터가 전송한 질의 메시지), 0x16(호스트가 전송하는 보고 메시지), 0x17(그룹 탈퇴에 관한 메시지)로 특정 그룹에 소속된 마지막 멤버의 탈퇴와 관련
* Max Response Time(최대 응답 시간) : 질의 메시지에서만 사용
* 체크섬 : 오류 검출용
* Group Address : 질의 메시지는 0으로 채우고, 보고 메시지에는 호스트가 가입을 원하는 그룹 주소를 표기

&#x20;

&#x20;**3.3 IGMP 동작 과정**

&#x20;\- 자신이 IGMP 메시지에 표시된 멀티캐스트 주소의 멤버임을 다른 호스트와 라우터에 알리기 위한 용도로 IGMP를 사용(IGMP 헤더의 Group Address 필드에 가입을 원하는 멀티캐스트 주소를 기록)

<figure><img src="https://blog.kakaocdn.net/dn/ZFsHX/btrdr3v7fBH/1s3J17WIyWjPHTr5bJ9n20/img.png" alt="" height="350" width="449"><figcaption></figcaption></figure>

&#x20;\- 멀티캐스트 라우터가 그룹에 속한 멤버 목록을 유효하게 관리하려면 IGMP 질의 메시지를 사용해 주기적으로 확인하는 과정이 필요

&#x20;개별 호스트가 자신의 그룹 멤버를 유지하려면 위와 같이 해야 하며 응답이 이뤄지지 않으면 탈퇴한 것으로 간주

&#x20;

&#x20;**3.4 IGMP 메시지의 전송**

<figure><img src="https://blog.kakaocdn.net/dn/bKBnHE/btrdmCN0Qs7/rXvUmWKkmy3WFc7xiqqaWK/img.png" alt="" height="253" width="503"><figcaption></figcaption></figure>

&#x20;\- IP 프로토콜과 동등한 계층의 기능을 수행하지만 데이터 링크 계층에 바로 전달하지 않고 IP 패킷에 캡슐화되어 보내진다.
