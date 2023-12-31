# 시스템을 연결하는 네트워크 구조

## 계층 구조

계층 구조에서는 데이터나 기능 호출 흐름에 따라 계층 간 역할이 나누어진다는 특징이 있다.

역할이 나누어져 있기 때문에 각 층은 자신이 담당하는 일만 책임을 지며, 다른 일은 다른 계층이 책임을 진다.

상호 연결돼 있는 계층들에서는 교환 방법, 즉 인터페이스만 정해 두면 된다.\
계층 구조로 나눔으로써 게층 간에 서로 영향을 주지 않고 독립적으로 동작할 수 있다.

상호 간에 내부 처리를 은폐하고 있기 때문에 인터페이스만 바꾸지 않으면 각 계층이 내부적인 처리를 마음대로 바꾸어도 문제 없다.

단점으로는 작업 효율을 희생해야 한다는 점을 들 수 있다. 교대할 때는 작업 인계를 위한 오버헤드가 발생한다.

## OSI 7계층 모델

OSI(Open Systems Interconnection)라는 통신 규격을 만들 때 고안된 것으로, OSI 통신 기능을 7개의 계층으로 나눈 것이다.

OSI 자체는 현재 사용되고 있지는 않지만, 다양한 분야에서 공통적으로 참조할 수 있는 “참조 모델"로써 현재도 사용 중이다.

### 7계층

```
애플리케이션 계층 - 애플리케이션 처리
프레젠테이션 계층 - 데이터 표현 방법
세션 계층 - 통신 시작과 종료 순서
전송 계층 - 네트워크의 통신 관리
네트워크 계층 - 네트워크 통신 경로 선택
데이터링크 계층 - 직접 접속돼 있는 기기 간 처리
물리 계층 - 전기적인 접속
```

### 네트워크 외의 계층 구조

시스템을 구성하는 서버 한 대의 내부를 살펴봐도 계층 구조로 되어 있음을 알 수 있다.\
애플리케이션이나 OS, 하드웨어 조합도 계층 구조라고 할 수 있다.

## 프로토콜

프로토콜(Protocol)은 컴퓨터가 서로 소통하기 위해 정한 규약을 말한다.\
떨어진 곳에 있는 두 개의 장비는 사전에 절차를 정해 두지 않으면 서로 통신할 수 없다.

브라우저로 웹 페이지를 볼 때 HTTP라고 불리는 프로토콜을 사용해서 서버에게 웹 페이지를 달라고 요청한다. 또한 이 통신은 전기 신호나 전파를 이용해서 전달된다.

프로토콜은 같은 계층 간의 약속이라고 할 수 있다.\
다른 제조사에서 만들어진 컴퓨터가 서로 통신하기 위해서는 프로토콜을 일치시켜야한다.

서버 내부의 장비들 간에도 서로 통신을 하기위해서는 프로토콜이 필요하다. (USB 프로토콜, SCSI 프로토콜)

### TCP/IP를 이용하고 있는 현재의 네트워크

인터넷을 포함해서 현재 네트워크를 지탱하는 것은 TCP/IP 및 관련 프로토콜이다. 이들 프로토콜 집합을 모아서 TCP/IP Protocol Suite라고 한다.

## TCP/IP 계층 구조

TCP/IP 프로토콜 집합은 명칭 그대로 TCP와 IP의 두 가지 프로토콜을 주축으로 한 프로토콜 집합이다.

OSI 참조 모델에서는 네트워크를 7계층으로 분할했지만, TCP/IP에서는 4계층 모델로 불리며 OSI 7계층의 1\~2계층을 모아서 링크 계층, 5\~7계층을 모아서 애플리케이션 계층으로 취급하기도 한다.

### TCP/IP 4계층 모델과 시스템 대응 관계

TCP/IP가 가지고 있는 계층 구조가 실제 서버에서 어떤 식으로 나누어져 있는지 보자.

* HTTP는 애플리케이션 프로토콜이기 때문에 httpd 프로세스를 사용한다.
* HTTP 통신 데이터를 상대방에게 보내기 위해서 TCP에 데이터를 건네지만, 여기부터 이더넷 계층까지는 OS 커널이 담당한다.
* 커널 내에서 TCP, IP, 이더넷을 담당하는 기능이 필요한 정보를 데이터에 부여해서 최종적으로 이더넷 프레임이 생성된다.
* 이것이 NIC에 전달돼서 이더넷 케이블 등을 통해 인접 노드를 경유해서 최종 위치까지 전달된다.

계층 구조로 나누어져 있어서 통신하고 싶은 애플리케이션은 독자적으로 통신 구조를 만들 필요 없이 TCP/IP에게 위임할 수 있다. 또한, 각 계층을 담당하고 있는 것도 쉽게 변경할 수 있다.

## TCP/IP 각 계층의 명칭

TCP/IP는 4계층이라고 하지만, 실제 현장에서 계층을 숫자로 부를 때는 OSI 참조 모델의 7계층 방식으로 부르는 경우가 많다.

```
링크(이더넷) 계층 - 레이어 2 or L2
IP 계층 - 레이어 3 or L3
전송 계층(TCP 계층) - 레이어 4 or L4
애플리케이션 계층 - 애플리케이션 레이어 or L7
```

TCP/IP에서는 L5, L6, L7을 모아서 애플리케이션 계층으로 취급함.

## \[레이어 7] 애플리케이션 계층의 프로토콜 HTTP

애플리케이션 계층 프로토콜은 자신이 통신하는 것이 아니라 통신 자체는 모두 OS, 즉 TCP/IP에 맡긴다.

웹 시스템에서 가장 중요한 애플리케이션 계층 프로토콜인 HTTP에 대해 살펴본다.\
HTTP의 사양은 RFC2616에서 정의하고 있다.

### HTTP의 처리 흐름

```
브라우저에 URL을 입력한다.
요청이 전송된다.
요청 내용이 해석된다
파일이나 이미지를 응답으로 반환한다.
파일 내부를 해석해서 이미지 등이 포함돼 있으면 다시 요청을 한다.
```

이와 같이 클라이언트와 웹서버는 HTTP를 통해서 몇 번이고 요청과 응답을 주고 받는다.

### 요청과 응답의 구체적인 내용

* 요청 시 중요한 것은 서버에 던지는 명령 == Method (GET은 파일 요구, POST는 데이터를 전송함을 의미)
* 헤더 부분에 다양한 부가 정보가 들어가며 세밀한 제어를 위해 사용 (User-Agent는 브라우저 식별 정보를 가지고, Cookie는 세션 식별자로 이용)
* 응답은 요청에 대하니 결과와 그에 대한 상태 정보를 가지고 있음. 또한 메시지 바디에 실제 데이터를 저장함

→ HTTP는 하위 계층인 IP나 유선을 통해 명령을 보내거나 통신 제어를 하지 않음. 때문에 HTTP 요청은 지정된 명령만으로 구성돼서 매우 간단한 구조라는 특징을 가진다.

### 애플리케이션 프로토콜은 사용자 공간을 처리

* 애플리케이션 프로토콜은 기본적으로 애플리케이션 프로세스 내부에서 모두 구현된다.
* 애플리케이션 계층 프로토콜은 필요한 데이터를 소켓에 기록만 하며, 통신은 모두 TCP/IP에 위임한다.
* 요청은 TCP/IP를 통해 상대 서버에 전송되고, 서버는 소켓을 통해 통신 상대가 보낸 데이터를 읽어서 요청을 처리.

### 소켓 이하는 커널 공간에서 처리

* 애플리케이션 프로세스가 네트워크 통신을 하는 경우, 커널에 ‘TCP/IP로 통신하고 싶으니까 상대방 애플리케이션과 통신할 수 있는 회선을 열어줘'라고 의뢰(시스템 콜)함
* 커널은 소켓을 만들고, TCP를 사용함과 IP주소 및 포트 번호 정보를 시스템 콜 경유로 커널에 전달하면 상대 서버와의 사이에 가상 경로(버추얼 서킷)가 생성된다.
* 실제 데이터는 물리적인 통신 케이블을 통해 전달되는 것이지만, 프로세스 관점에서는 데이터가 소켓으로 들어가 가상 경로를 통해 상대 통신 소켓으로 나오는 것이다.

## \[레이어 4] 전송 계층 프로토콜 TCP

소켓에 기록된 애플리케이션 데이터는 커널 내에서 통신 대상에게 전달하기 위한 준비를 시작한다. 제일 먼저 임우를 수행하는 것이 전송 계층 프로토콜인 TCP다.

### TCP의 역할

* TCP(Transmission Control Protocol)는 명칭 그대로 전송을 제어하는 프로토콜로 신뢰도 높은 데이터 전송을 가능하게 함
* 애플리케이션이 보낸 데이터를 그 형태 그대로 상대방에게 확실히 전달하는 역할. 단, 가능한 한 주변에는 민페를 끼치지 않으면서. (신뢰도가 낮은 인터넷에서 사용하기 위함)
* TCP의 담당 범위는 서버가 송신할 때와 서버가 수신한 후 애플리케이션에게 전달하는 것까지. 상대 서버까지 전송하는 부분은 하위 계층인 IP에 위임.
* TCP없이 IP로만 통신하면, 데이터가 상대방에게 확실히 전달됐는지, 도착 순서는 보장하는지를 확인할 수 없다.

#### \<TCP 중요한 기능>

* 포트 번호를 이용해서 데이터 전송
* 연결 생성
* 데이터 보증과 재전송 제어
* 흐름 제어와 폭주 제어(혼잡 제어)

### 커널 공간의 TCP 처리 흐름

1. 애플리케이션 프로세스가 소켓에 데이터를 기록한다
2. 소켓에 기록된 데이터는 큐를 경유해서 커널 내 네트워크 처리 부분에 전달
3. 커널에 전달된 데이터는 ‘소켓 버퍼'라는 메모리 영역에서 처리됨
4. 데이터에 TCP 헤더를 붙여서 TCP 세그먼트를 생성. TCP 헤더에는 목적 애플리케이션의 포트 번호 신호를 포함해 TCP 기능에 필요한 다양한 정보 기록
5. MSS를 초과한 데이터는 자동적으로 분할돼서 복수의 TCP 세그먼트가 생성됨. (MSS - 하나의 TCP 세그먼트로 전송할 수 있는 최대 크기)
6. 최종적으로 링크 계층을 사용해서 데이터를 전송 (MSS는 링크 계층의 MTU에 의존)

### 포트 번호를 이용한 데이터 전송

* 상대 서버에 데이터가 도착해서 어떤 애플리케이션용 데이터인지 알 수 있도록 포트 번호를 이용하여 판단한다.
* TCP 포트 번호는 0-65535까지의 숫자를 이용한다.

### 연결 생성

1. 서버 측 소켓은 자신이 지정한 포트 번호에 통신이 오는지를 기다렸다가 받는 상태로, 이 상태를 ‘포트를 리슨(LISTEN)’하고 있다고 한다.
2. 커널 내 TCP 계층에서 통신 상대 서버에게 가상 경로를 열어 줄 것을 의뢰한다.
3. 통신을 받는 측은 열어도 된다고 응답한다
4. 마지막으로 다시 한번 확인했다는 메시지를 보내면 가상 경로가 생성된다. 실제로는 송신 측에도 자동적으로 포트번호가 설정된 소켓이 열린다.(클라이언트 측에서는 사용할 포트 번호를 지정할 수 없음)

→ 이 세 번의 대화를 TCP/IP의 3-way handshaking이라고 한다.

* TCP 통신을 시작할 때 상대 서버에 포트 번호와 연결을 열어달라고 부탁할 뿐 특별한 일을 하지 않음
* 데이터 전송 자체도 IP에 위임했기 때문에 실제 물리적인 경로가 막히거나 서버가 갑작스런 장애로 전원이 꺼져도 가상적인 경로인 TCP 연결이 끊어지진 않는다.
* 애플이케이션이 OS에게 연결 절단 의뢰를 하거나 통신 대상이 에러를 보내오지 않는 이상 TCP 연결 자체가 유지되기 때문에 주의 필요

### 데이터 보증과 재전송 제어

TCP에는 데이터가 확실히 전달되도록 보증하는 기능이 있다.

#### 데이터 손실을 방지하는 구조

* 수신 측에 TCP 세그먼트가 도착하면 수신 측은 송신 측에에 도착했다는 것을 알림 → ACK
* 송신 측은 TCP 헤더에 ACK가 돌아오는 것을 보고 전송한 세그먼트가 무사히 도착했음을 알 수 있음
* ACK가 돌아오지 않으면 세그먼트의 유실로 판단하고 재전송하여 손실 방지 (언제든지 재전송 가능하도록 ACK가 돌아오기까지는 데이터를 세그먼트를 소켓 버퍼에 남겨둠)

#### 데이터 순서를 보장하는 구조

* 각 TCP 세그먼트에 시퀀스(Sequence) 번호를 붙여서 구현. TCP 헤더에 기록
* 해당 TCP 세그먼트가 가지고 있는 데이터가 전송 데이터 전체 중 몇 바이트째부터 시작하는 부분인지를 가리킴
* 수신 측은 이 시퀀스 번호를 사용해서 원래 순서대로 데이터를 조립함

#### TCP 재전송 제어

* 순차적 조합을 위해 수신 측은 ACK를 반환할 때 다음에 필요한 TCP 세그먼트 시퀀스 번호도 ACK 번호로 전달함

#### 재전송 시점은 언제인가?

* 타임 아웃 - 일정 시간 내에 ACK가 돌아오지 않으면 재전송
* 중복 ACK - 한번 받은 ACK 번호와 같은 것이 3회 중복돼서 도착한 경우 그 번호에 해당하는 TCP 세그먼트가 도착하지 않았다고 간주하고 재전송 (지연으로 인해 도착 순서 변경이 있을 수 있기 때문에 3회까지 기다림)
* SACK(Selective ACK) 옵션 - SACK 옵션을 사용하면 이미 도착했다는 것을 정보로 전달할 수 있음. 이를 통해 도착하지 않은 TCP 세그먼트만 선택해서 재전송

### 흐름 제어와 폭주 제어

#### 흐름 제어

* 동기로 통신을 하면 효율이 나쁘기 때문에 ACK를 기다리지 않고 다음 세그먼트를 전송한다.
* TCP는 어느 정도의 세그먼트 수라면 ACK를 기다리지 않고 전송하는 윈도우라는 개념을 가지고 있으며, ACK를 기다리지 않고 전송 가능한 데이터 크기를 윈도우 크기라고 한다.
* 윈도우에는 수식 측의 윈도우와 송신 측의 윈도우로 2가지가 있다.
* ACK가 오면 해당 TCP 세그먼트는 재전송할 필요가 없기 때문에 송신용 소켓 버퍼에서 삭제하고 송신 윈도우를 다음으로 이동한다. → 슬라이딩 윈도우(Sliding window)
* 수신 측은 수신용 소켓 버퍼가 넘쳐서 더 이상 수신이 불가능하게 되면 수신 윈도우 크기를 작게 만들고 이 사실을 송신 측에 알린다. 송신 측은 수신 윈도우 크기 이상의 데이터는 ACK없이 보낼 수 없게 된다. → 이것이 TCP 흐름제어!

#### 폭주 제어(혼잡 제어)

* 송신 측 윈도우 크기를 네트워크 폭주 상태(혼잡 상태)에 맞추어 변경시킴
* 네트워크가 혼잡하면 송신 윈도우 크기를 작게 해서 전송 데이터 양을 줄인다.
* 슬로우 스타트(slow start) : 통신 시작 시 1세그먼트에 설정. 통신이 문제 없이 수신 측에 도착하면 ACK 반환시마다 윈도우 크기를 지수 함수적으로 늘려가는 방식. 송신이 실패하면, 윈도우 크기를 작게 해서 송신량을 줄이고, 다시 윈도우 크기를 키워나감.

## \[레이어 3] 네트워크 계층의 프로토콜 IP

TCP 세그먼트가 만들어지면 다음은 IP처리가 시작됨. IP에는 프로토콜 종류에 따라 다른 버전이 있으며, 현재 폭넓게 사용되고 있는 것은 IPv4. IPv4를 중심으로 설명한다.

### IP의 역할

지정한 대상 서버까지 전달받은 데이터를 전해 주는 것. 단, IP에서는 반드시 전달된다고 보장하지 않음. 중요 기능은 다음과 같다.

* IP 주소를 이용해서 최종 목적지에 데이터 전송
* 라우팅(Routing)

### 커널 공간의 IP 처리 흐름

* TCP 세그먼트에 IP 헤더를 붙여서 IP 패킷을 생성
* 대상 서버까지는 이 IP 패킷 형태로 네트워크를 경유해서 도달
* IP 헤더에는 최종 목적지 서버의 IP 주소, 저장하고 있는 데이터 길이, 프로토콜 종류, 헤더 체크섬 등이 기록
* TCP 헤더의 크기는 20byte, IP 헤더(IPv4)의 크기도 20byte → (세그먼트에 들어간) 데이터 크기 + 40byte

### IP 주소를 이용한 최종 목적지로의 데이터 전송

* IP 주소는 32비트로 표현된 숫자 집합. 원래는 컴퓨터가 처리하는 이진수를 써야하지만, 사람이 읽기 쉽도록 8비트 단위로 마침표를 찍어서 표현
* IP 주소는 네트워크부와 호스트부로 나뉜다. 네트워크부는 어떤 네트워크인지를 가리키고, 호스트부는 해당 네트워크 내에 있는 컴퓨터를 가리킴. → IP주소로 어디의 누구인지 알 수 있음
* 어디까지가 네트워크부인지 표시를 위해 ‘/24’같은 CIDR(사이더) 표기를 사용 or 서브넷 마스크(Subnet Mask)로 표현
* 같은 네트워크 내에 있다면 동일한 네트워크 주소가 사용됨.
* 서브넷 마스크는 몇 번째 자리까지가 네트워크 주소인지를 구별하기 위한 표시
* 호스트부의 비트가 모두 0인 것을 네트워크 주소, 모두 1인 것을 브로드캐스트(Broadcast) 주소라고 함. 이들은 호스트에 할당해서는 안되는 특별한 IP주소임.
* 참고로 브로드캐스트 주소로 보낸 패킷은 같은 네트워크의 모든 호스트에 전달됨

### IP주소 고갈과 IPv6

* 현재 사용되고 있는 IPv4는 주소를 32비트로 표현하고 있음.
* 약 43억 개의 IP주소를 만들수 있지만, 고갈되고 있음.
* 이 문제를 위해 IP주소를 128비트로 늘린 것이 IPv6다.
* 하지만 IPv6는 IPv4와 호환성을 가지고 있지 않음. (IPv4를 전제로한 네트워크 장비 및 애플리케이션은 IPv6를 사용할 수 없음)
* 조금씩 IPv6 대응 환경으로 바뀌어나가고 있고, 사용할 준비가 거의 끝났다고 할 수 있음 (2012.06.06 World IPv6 Launch)
* TCP 및 그 윗 단계의 HTTP는 양쪽 버전을 모두 지원하기 때문에 의식할 필요 없음(계층 구조의 장점)

### 사설 네트워크와 IP 주소

IP주소는 왜 192.168로 시작하는 것일까?

* IP주소는 목적지를 식별하기 위한 것이기 때문에, 다른 곳에 위치한 두 대의 컴퓨터가 동일한 IP를 사용하면 패킷을 어느 컴퓨터에 전달해야할지 알 수 없다.
* 모든 컴퓨터에 고유한 IP 주소를 할당해야하지만, 사용할 수 있는 IP 주소가 무한대로 있는 것이 아니다.
* 인터넷에 사용되는 IP는 특정 범위는 특정 조직이 사용하는 방식으로 지정되어있기 때문에 원하는대로 설정할 수 없다.
* 사설(private) 네트워크에서 사용할 수 있는 주소도 RFC 1918에서 정의되어 있다.
  * 10.0.0.0/8(10.0.0.0\~10.255.255.255 범위)
  * 172.16.0.0/12(172.16.0.0\~172.31.255.255 범위)
  * 192.168.0.0/16(192.168.0.0\~192.168.255.255 범위)
* 사설 주소의 반대되는 개념으로, 인터넷상에서 통신이 가능한 IP 주소를 공공(Public) IP 주소라고 한다.
* 사설 주소는 자유롭게 사용할 수 있어 편리하지만, 인터넷 상의 호스트 등과 통신이 불가능하다.
* 공공 주소와 사설 주소가 모두 할당된 호스트를 준비하고, 사설 주소만 있는 호스트는 해당 호스트를 경유해서 인터넷 세상과 통신하도록 한다.

### 라우팅(Routing)

IP 주소를 이용해 대상 서버를 지정할 수 있지만, 대상 서버가 항상 같은 네트워크 내에 있는 것은 아님. 다른 네트워크에 있는 경우 최종 목적지에 도착할 때까지 목적지를 알고 있는 라우터에 전송을 부탁하는 것임!

* 서버나 라우터는 자신이 알고 있는 목적지 정보를 라우팅 테이블이라 하는 형태로 목록화함
* IP 패킷을 받은 라우터는 해당 IP 패킷의 헤더에서 목적지를 확인해서 어디로 보내야 할지 확인 후 전송

외부와 접속하는 네트워크는 보통 기본 게이트웨이(Default Gateway)라는 라우터가 설치돼 있음

* 라우팅 테이블에 목적지 정보가 없는 경우 기본 게이트웨이라 불리는 목적지에 패킷을 보냄
* 기본 게이트웨이는 외부 네트워크에 접속돼 있어서 외부 세계에 패킷을 보낼 수 있음
* 기본 게이트웨이의 라우터가 목적지 네트워크에 패킷을 전송

각 라우터는 라우팅 테이블을 기반으로 패킷을 전송하기 때문에 도중에 있는 라우터가 잘못된 라우팅 테이블을 가지고 있다면 목적지가 바뀔 수 있음

* 라우팅 테이블에 오류가 있어 네트워크에서 패킷이 계속 순회하게 되는 문제 발생할 수 있음
* 이러한 상태를 방지하기 위해 IP 헤더는 TTL(Time To Live)이라는 생존 시간 정보를 가지고 있음 (라우터를 하나 경유할 때마다 라우터가 TTL을 1씩 줄여, 0이 되면 패킷 파기)

### IP 헤더에서 체크섬이 사라진 날

* IPv4와 IPv6의 차이에는 주소 공간 외에도 체크섬 유무도 있음
* IPv4 헤더에는 체크섬이 포함. 헤더 정보가 파괴됐는지 여부를 확인할 수 있지만, 헤더 정보에 변경이 있으면 체크섬을 재계산해야함
* 즉, TTL이 줄어들 때마다(라우터를 경유할 때마다) 체크섬을 재계산해야함
* TCP 헤더에도 체크섬이 있으며, 이 체크섬 계산으로 IP 주소 부분을 포함한 일치성을 확인하기 때문에 IPv6에서는 체크섬이 제외됨

## \[레이어 2] 데이터 링크 계층의 프로토콜 이더넷

링크 계층에서 사용되는 대표적인 프로토콜은 이더넷(Ethernet)이다.

### 이더넷의 역할

* 링크 계층 프로토콜의 역할은 ‘동일 네트워크 내의 네트워크 장비까지 전달받은 데이터를 운반’하는 것
* TCP/IP 4계층 모델이서는 물리 계층과 함께 하나의 계층으로 취급되는만큼, 이더넷은 물리 계층과 밀접한 관계가 있음
* 이더넷은 케이블 통신에서 사용되기 때문에 이더넷 프레임은 전기 신호로 전송된다.
* 이더넷은 동일 네트워크 내, 즉 자신이 포함된 링크 내에서만 데이터를 전송할 수 있다. 이때 사용되는 주소가 MAC(맥) 주소다.

### 커널 공간의 이더넷 처리 흐름

* IP 계층에서 라우팅 테이블을 통해 어떤 링크(NIC)가 패키시을 보낼지는 정해져 있음
* 최종적인 통신 상대가 동일 네트워크에 있으면 해당 서버에 직접 전송, 다른 네트워크에 있으면 기본 게이트웨이에 패킷을 보내야함
* 이더넷 헤더 내의 MAC 주소라 불리는 링크 계층 주소를 사용해서 첫 번째 목적지로 보냄
* MAC 주소에는 ARP 테이블(MAC 테이블)이라 불리는 표가 있음. (동일 링크 내의 IP 주소에 대응하는 MAC 주소 정보가 기록)
* 인접한 장비의 MAC 주소를 헤더에 기록한 후 최종적으로 OS가 버스를 통해 NIC에 전달. NIC는 이것을 네트워크에 전송

IP 패킷이 이더넷 프레임에 저장되는 모습

* 이더넷 등 해당 링크 층에서 하나의 프레임으로 전송할 수 있는 최대 크기를 MTU(Maximum Transfer Unit)라고 함. (링크 종류, 설정에 따라 다르지만 일반적으로 1500byte)
* MTU에서 IP 및 TCP 헤더 크기를 뺀 것이 TCP의 MSS임 (MSS는 MTU 크기에 의해 변동)

### 동일 네트워크 내의 데이터 전송

* MAC 주소는 네트워크 통신을 하는 하드웨어에 할당된 주소로, 원칙적으로는 세상의 모든 장비가 고유한 물리 주소를 가지고 있음
* MAC 주소는 48비트로 표현. 보틍 16진수로 표기.
* 서버 등이 보낸 이더넷 프레임으 L2 스위치에 도착하면 프레임을 받은 L2 스위치는 MAC 주소를 보고 적절한 포트에서 프레임을 꺼냄
* 다른 네트워크(L3 스위치나 라우터)를 거치는 경우는 MAC 주소를 사용한 통신이 불가능
* IP를 이용한 브로드캐스트 주소 통신은 이더넷상에서의 브로드캐스트 통신으로 전송. MAC 주소 FF-FF-FF-FF-FF-FF가 이더넷의 브로드캐스트에 해당.

### VLAN(Virtual LAN)

* 네트워크를 구축할 때는 통신이 도달하는 범위를 생각해야 함
* 네트워크 범위는 네트워크 스위치의 물리 구성에 의해 크게 좌우되기 때문에 유연하게 구성하기 어려움
* 물리 구성에 좌우되지 않고 설정만으로 네트워크를 나눌 수 있는 구조가 필요함
* VLAN은 물리 구성에 의존하지 않고 가상적인 네트워크를 나누는 구조다.
* 가상적으로 나눈 네트워크는 VLAN ID로 관리한다.
* VLAN의 중 자중 사용되는 것은 태그 VLAN으로, 이더넷 프레임에 해당 프레임이 소속된 VLAN ID의 태그를 붙여서 하나의 물리 링크 내에서도 복수의 네트워크 이더넷 프레임을 처리할 수 있는 구조다.
* 하나의 이더넷 케이블 내에서 다른 VLAN에 속하는 프레임을 전송할 수 있게 되어, 물리적으로 떨어져 있는 네트워크 스위치라도 동일 네트워크에 참가시키는 것이 가능하다.
* 네트워크 스위치의 포트별로 VLAN ID를 맵핑하여 하나의 L2 스위치라도 여러 네트워크를 다룰 수 있다.
* 같은 L2 스위치에 접속된 컴퓨터들이라도 각각 다른 VLAN ID에 설정된 포트를 사용하고 있는 경우는 별도의 L3 스위치나 라우터 없이는 통신할 수 없다.

### TCP/IP를 이용한 통신 이후

이더넷 프레임으로 NIC까지 전송된 프레임은 이후에 어떻게 처리되는 것일까?

#### 네트워크 스위치 중계 처리

* 스위치 내부에서도 헤더 등을 확인한다. L2 스위치에서는 이더넷 헤더를 확인해서 어떤 포트에 프레임을 보낼지 판단한다. L3 스위치나 라우터에서는 IP 헤더까지 확인해서 전송 위치를 확인한다. 여기서는 필요에 따라 헤더 내 데이터를 수정할 수도 있다.
* 전송된 이더넷 프레임은 제일 먼저 서버와 인접하고 있는 L2 스위치에 도착한다.
* L2 스위치는 이더넷 계층에서 처리하는 스위치로, 이더넷 헤더를 보고 대상 MAC 주소를 확인한 후 적절한 포트를 통해 프레임을 전송한다.
* 최종 목적지가 동일한 네트워크 서버라면 사이에 L2 스위치가 하나만 있지만, 다른 네트워크에 전송하는 경우는 L3 스위치나 라우터가 있을 수 있다.

#### 최종 목적지의 수신 확인

* L2 스위치나 L3 스위치를 경유해서 최종 목적지인 서버에 이더넷 프레임이 도착한다.
* NIC로 프레임이 도착하면 NIC 수신 큐에 저장해서 OS 인터럽트나 폴링을 이용해서 커널 내에 프레임을 복사한다.
* 이더넷 헤더와 푸터를 제거하고 IP 패킷을 꺼낸다. 여기서 IP 주소를 확인해서 자신에게 보낸 패킷이 맞는지 확인한다.
* 자신에게 보낸 패킷이 맞다면 IP 헤더를 제거하고 TCP 세그먼트를 꺼냄
* TCP 포트 번호를 확인해서 대응하는 소켓에 데이터를 전달 (데이터 보증을 위해 필요한 세그먼트가 도착할 때까지 버퍼에서 기다리는 경우있음)
* TCP 헤더를 제거하고 안에 있는 애플리케이션 데이터를 재구성하여 소켓을 통해 애플리케이션에 전달
