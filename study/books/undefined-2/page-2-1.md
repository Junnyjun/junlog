# 전송 계층

### 01 UDP 프로토콜

(UDP, User Datagram Protocol) - 비연결형 서비스, 헤더와 전송 데이터에 대한 체크섬 기능 제공, Best Effort 전달 방식 지원

\- 상위 계층에서 받은 데이터를 IP 프로토콜에 전달하지만, 전송한 데이터그램이 목적지까지 제대로 도착했는지 확인하지 않아 신뢰성이 떨어지지만 빠르다.

#### 1. UDP 헤더 구조

<figure><img src="https://blog.kakaocdn.net/dn/XzZEY/btreJxR6XPJ/SEzhfDDMPrt44Pq5dpJpZK/img.png" alt="" height="162" width="526"><figcaption></figcaption></figure>

* Source Port/Destination Port : 송수신 프로세스에 할당된 네트워크 포트 번호이다. 호스트에서 실행되는 프로세스를 구분하는데 이용. 호스트는 IP 프로토콜의 IP 주소로 구분하므로, 인터넷에서 실행되는 네트워크 프로세스의 교유 구분자는 호스트의 IP 주소와 프로세스 포트 번호의 조합이다. \[UDP 포트 번호는 TCP 포트 번호와 독립적으로 관리]
* Length : 프로토콜 헤더를 포함한 UDP 데이터그램의 전체 크기이다. 단위는 바이트 헤더의 크기가 8바이트이므로 최소 값은 8이다.
* Checksum : 프로토콜 헤더와 데이터에 대한 체크섬 값을 제공하여 수신 프로세스가 데이터그램 변형 오류를 감지할 수 있다. IP 프로토콜은 헤더만 체크섬을 계산하지만, UDP는 데이터까지 체크섬을 계산한다.

\- Length 필드는 크기가 16비트이므로 65,535 바이트지만 응용 프로그램을 포함한 다른 계층과의 연관성 문제를 고려하여 일반적으로 8192 바이트를 넘지 않게 사용

#### 2. UDP의 데이터그램 전송

\- UDP 비연결형 서비스를 이용하여 데이터그램을 전송하며, 각 데이터그램은 전송 과정에서 독립적으로 중개되고 데이터그램이 목적지까지 도착할 수 있도록 최선을 다하지만 반드시 목적지에 도착하는 것을 보장하지 않는다.

슬라이딩 윈도우 프로토콜 같은 흐름 제어 기능도 없어 버퍼 오버플로에 의한 데이터 분실 오류가 발생할 수 있다.

**2.1 UDP에서의 데이터그램 분실**

\- 송신 프로세스가 전송한 데이터그램 4개가 첫 라우터에서 두 번째 라우터로 전송되는 과정에서 3번 데이터그램에 오류가 발생해 다음 라우터에 도착하지 못했다.

<figure><img src="https://blog.kakaocdn.net/dn/c2nkTj/btrePVDt6uf/8xvu6sOH5ODkamFiLOFsEk/img.png" alt="" height="180" width="518"><figcaption></figcaption></figure>

\-> 1,2,4만 도착(3번 분실)

**2.2 UDP에서의 데이터그램 도착 순서 변경**

<figure><img src="https://blog.kakaocdn.net/dn/b0dJV5/btrePVQ2QDj/pnuB15dKoE7PjPx6ED3vB0/img.png" alt="" height="187" width="457"><figcaption></figcaption></figure>

\->도착 순서 변경 오류(순서 번호 기능 필요)

### 02 RTP 프로토콜

\- 음성, 영상 정보를 인터넷에서 실시간으로 서비스하면 데이터그램 변형이나 분실 오류를 복구하는 기능이 상대적으로 덜 중요해진다. 대신 데이터그램의 도착 순서, 수신화 패킷의 지연 간격(지터) 분포의 균일성과 데이터 압축에 의한 전송 정보량의 최소화가 중요하다.

\- TCP는 패킷의 순서와 신뢰성이 너무 강조되어 재전송 기능과 복잡한 흐름 제어 기능으로, UDP는 도착 순서를 보장 못해서 실시간 서비스를 지원하지 못한다.

\- RTP(Real Time Protocol) : UDP에 순서 번호 기능을 추가한 방식

* RTP특징

1\) 불규칙하게 수신되는 데이터의 순서를 정렬하기 위해 타임스탬프 방식(Timestamp)을 사용

2\) 프로토콜의 동작이 응용 프로그램의 라이브러리 형태로 구현되는 ALF(Application Level Framing) 방식을 사용해 프로토콜에 내부에 위치하는 버퍼의 크기를 각 응용 프로그램마다 별도로 관리하기 용이하다.

3\) 실시간에 유용하지만 자원 예약이나 QoS 보장과 같은 기능은 제공하지 못해 실시간 동영상 지원 힘듦

#### 1. 실시간 요구 사항

\- 일반 고속 통신 서비스는 파일 전송, 전자 메일 같은 전통적인 인터넷 서비스 환경에서 가장 중요한 건 신뢰성이고 네트워크 시스템의 성능과 지연 문제를 다룬다

\- 실시간 서비스는 전송 시간이 중요하고 송신 프로세스가 전송한 데이터의 전송 간격이 수신 프로세스에 그대로 유지되도록 하는 것이 중요하며, 대부분 특정 데이터가 정해진 시간 안에 반드시 도착하도록 요구한다.

**1.1 버퍼의 역할**

<figure><img src="https://blog.kakaocdn.net/dn/oG0Hd/btreJRQre8B/4zsb9XUODLIQDzKzMK8Oxk/img.png" alt="" height="371" width="509"><figcaption></figcaption></figure>

\-> 송신 프로세스가 전송한 데이터는 데이터그램 사이의 시간 간격이 일정하다가 인터넷을 거쳐 수신 프로세스에 전달되는 과정에서 간격이 불규칙하게 변한다. 수신 측에서는 시간 간격이 가변적인 데이터를 수신 프로세스에 즉시 전송하지 않고, 지연 버퍼를 사용해 데이터의 시간 간격을 일정하게 보정한다.

이때 수신 프로세스에 도착한 데이터의 시간이 실시간 재생에서 요구하는 일정 범위보다 늦으면 해당 데이터를 버린다.

\- 주어진 시간 안에 데이터 전송을 완료하기 위한 조건은 전송 대역폭을 충분히 확보하는 것이고 송수신 프로세스 사이의 지연 시간(Latency)에 관한 문제라 표현하기도 한다.

지연 시간 : 송신 프로세스가 전송한 데이터의 출발 시간과 수신 프로세스에 도착한 시간의 차이로 대역폭, 네트워크 구조, 라우팅 방식, 전송 프로토콜의 종류 등 여러 가지가 영향을 끼친다.

**1.2 지터(Jitter)**

: 송수신 프로세스 사이의 데이터그램 간격 차이(b)

\- 지터 분포 : 데이터그램의 도착 시간을 측정하였을 때 각 데이터그램의 도착 시간이 일정하지 않고 불규칙적으로 도착하는 정도

<figure><img src="https://blog.kakaocdn.net/dn/cJUKHh/btreJQYgjoS/7bip4emym7V4511kxtkok1/img.png" alt="" height="327" width="596"><figcaption></figcaption></figure>

(a) - 전송 간격이 균일했던 송신 데이터그램이 수신 프로세스에 도착할 때 간격이 일정하지 않음

#### 2. RTP의 데이터 전송

<figure><img src="https://blog.kakaocdn.net/dn/sTy8L/btreLgu9J9J/n4UnRchwn0TmU6KWWFxQzk/img.png" alt="" height="311" width="412"><figcaption></figcaption></figure>

\-> RTP의 동작 원리로 실시간 서비스를 제공하기 위해 작고 빠른 UDP로 구현된다.

각각의 응용 서비스의 종류에 따라 요구 조건이 다른 기능들이 추가되는 형식(기능들이 개별적으로 구현)으로 완전한 RTP 모듈이 완성된다.

\- RTP는 믹서와 트랜슬레이터라는 두 종류의 RTP 릴레이를 지원한다.

릴레이(Relay) : 데이터 전송 과정에서 송수신 프로세스가 데이터를 직접 전송할 수밖에 없는 상황이 발생했을 때, 데이터를 중개하는 기능

* 믹서(Mixer) - 여러 송신 프로세스로부터 RTP 데이터그램 스트림을 받아 이들을 적절히 조합해 새로운 데이터그램 스트림을 생성한다. 이 과정에서 데이터 형식이 변하거나 믹싱 기능이 수행될 수 있고 여러 송신 프로세스로부터 수신한 데이터의 시간 관계가 적절하게 조절되지 않을 수 있기 때문에 조합된 데이터그램 스트림에 시간 정보를 제공한다.(시간 정보(Synchronization)를 제공했음을 표시해야 함)
* 트랜슬레이터(Translator) - 입력된 각 RTP 데이터그램을 하나 이상의 출력용 RTP 데이터그램으로 만들어 주는 장치로 이 과정에서 데이터 형식이 변할 수 있다.

ex) 임의의 수신자 그룹에서 특정 수신 프로세스가 고해상도 비디오 신호를 처리할 능력이 없으면 트랜슬레이터가 저해상도 신호로 변환해 처리할 수 있도록 도와준다.

\- 믹서는 데이터그램 스트림(데이터그램을 연속으로 전송하는 것)의 믹싱에 관한 문제를 다루고 / 트랜슬레이터는 스트림에 관심 없고 변환 작업을 수행

#### 3. RTP 헤더 구조

<figure><img src="https://blog.kakaocdn.net/dn/c6MsBr/btreKKDikeO/fq85WNcj3BkaImM3od9Ygk/img.png" alt="" height="235" width="513"><figcaption></figcaption></figure>

\- 멀티캐스트 전송이 가능해 멀티캐스트 그룹에서 누가 데이터를 전송했는지를 확인하는 송신 구분자(Source Identifier) 필드가 존재한다. 또한 수신 프로세스에서 지연 버퍼를 사용해 타이밍 관계를 조절할 수 있도록 Timestamp 필드도 지원된다.

* Version - 버전 번호
* Padding - RTP 페이로드의 마지막에 패딩 데이터가 존재하는지 여부를 나타낸다. 응용 환경에서 페이로드의 크기가 특정 크기의 배수가 되어야 할 때 사용
* Extension - 확장 헤더
* CSRC Count - 개수
* Marker - 임의의 표식을 위해 이용하므로, 페이로드 유형에 따라 값의 의미가 결정된다. 보통 데이터 스트림의 경계점을 표시하는 데 사용. \[비디오 페이로드에서는 프레임의 마지막을 표시하기 위해 1로 지정]
* Payload Type(페이로드 유형) - 헤더 다음에 이어지는 RTP 페이로드의 유형을 나타낸다.
* Sequence Number - Timestamp 필드 값이 동일한 페이로드에 대해 패킷 손실이나 순서 변경과 같은 오류를 검출할 수 있도록 한다. 일반적으로 동시에 생성된 일련의 연속 패킷들은 동일한 Timestamp 값을 가지며, 순서 번호는 RTP 패킷 단위로 1씩 증가한다.
* Timestamp - 페이로드에 포함된 데이터의 생성 시기를 나타낸다. 시간 단위는 페이로드의 종류에 영향을 받으며, 송신 프로세스에서 사용하는 클록에 의해 발생한다.
* SSRC Identifier - 임의의 세션 내에서 페이로드의 발신지가 어디인지를 구분하는 고유 번호로, 랜덤 하게 생성되는 32비트 숫자

<figure><img src="https://blog.kakaocdn.net/dn/bfDMyw/btreJPZoH5e/QqSkWr2zWMVATkXyyAak3K/img.png" alt="" height="351" width="542"><figcaption></figcaption></figure>

#### 4. RTP 제어 프로토콜

RTCP(RTP Control Protocol) : RTP 제어 프로토콜을 RTP 데이터 전송 프로토콜과 구분하기 위해 부르며 RTP처럼 UDP를 하부 전송 계층으로 사용하며, RTCP 패킷을 모든 멤버에게 주기적으로 전송

\- 기능

* QoS와 혼잡 제어 - RTCP는 세션에서의 데이터 분배 과정에서 발생하는 서비스 품질에 관한 피드백 기능을 지원한다. 즉 멀티캐스팅 과정에서 세션 멤버의 데이터 송수신 과정이 어떻게 이뤄졌는지를 판단하는데 이를 위해 송수신 프로세스가 관련 보고서를 작성한다. \[송신 프로세스 보고서에는 전송률, 수신 프로세스 보고서는 수신 과정에서 발생하는 패킷 분실이나 지터 등의 정보가 포함]
* Idnetification(구분자) - RTCP 패킷에 송신 프로세스에 관한 구분자 정보가 포함되며, 서로 다른 세션에서 발신된 스트림 정보들을 서로 연관시키는 근거를 제공한다.

### 03 OSI TP 프로토콜

\- OSI에서 TP(Transport Protocol)는 다섯 개의 클래스로 서비스를 분류하여 지원한다.

<figure><img src="https://blog.kakaocdn.net/dn/DkrTX/btreJoAKA7U/Lk233Y2qpFXY23BhZOLzYK/img.png" alt="" height="247" width="350"><figcaption></figcaption></figure>

\-> 0,1은 단일 포트를 지원하는 프로토콜\[0 : 오류 검출 기능 없이 기본 전송 기능만 제공/1 : 패킷 손실 확인과 같은 간단한 오류 복구 기능]

2,3,4는 목적지에서 다수 포트를 지원하는 멀티플렉싱 기능이 있다.

#### 1. OSI TP의 서비스 프리미티브

\- TP가 상위 계층에 제공하는 전송 서비스에는 연결, 비연결형이 있다.

연결형 서비스를 이용하기 위한 연결 설정과 연결 해제는 T-CONNECT와 T-DISCONNECT로 정의

데이터는 일반 데이터를 의미하는 T-DATA와 긴급 데이터를 의미하는 T-EXPEDITED-DATA로 정의

전송 계층의 연결형 프로토콜은 상위 계층에 전송 오류가 없는 서비스를 제공하기 때문에 데이터에 대한 긍정, 부정응답 프레임은 정의되지 않는다.

<figure><img src="https://blog.kakaocdn.net/dn/cMqI4N/btreMD4vec9/eBnvL7J2hyXTt70dWqd9QK/img.png" alt="" height="260" width="579"><figcaption></figcaption></figure>

#### 2. OSI TP의 데이터 전송

<figure><img src="https://blog.kakaocdn.net/dn/0Zuzh/btreLf37jav/8mZmm2TNFy18WCUuLWKTK0/img.png" alt="" height="235" width="589"><figcaption></figcaption></figure>

\-> 서비스 프리미티브를 이용한 연결형 서비스의 동작 과정으로

\- T-CONNECT 요구는 연결이 정상적으로 설정되는 경우를 가장한 것으로 연결 설정 요구를 받은 오른쪽에서 연결을 거부하면 연결이 설정되지 않는다.

\- T-DISCONNECT를 이용한 연결 해제는 한쪽의 요구에 의해 연결이 해제되며 양쪽에서 동시에 연결 해제를 요구하는 경우에도 연결이 해제된다. 또한 송수신 프로세스의 의도와 상관없이 네트워크 내부에 특별한 상황\[네트워크의 특정 부분에 혼잡이 발생한 경우]이 발생하여 연결 해제 요구가 발생할 수도 있다.
