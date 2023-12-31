# 컴퓨터 네트워크

## 컴퓨터 네트워크 <a href="#01" id="01"></a>

### 컴퓨터 네트워크의 종류 <a href="#0101" id="0101"></a>

여러 대의 컴퓨터를 서로 연결하여 서로 데이터를 주고받을 수 있도록 망이 사전에 구축된 것

![](https://velog.velcdn.com/cloudflare/iseeu95/2796e79a-5ae5-40cb-b94c-983adef6803d/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202022-04-08%20%EC%98%A4%ED%9B%84%208.23.23.png)

인터넷은 여러 네트워크 중에서도 가장 규모가 크다.

***

### 서버와 클라이언트 <a href="#0103" id="0103"></a>

서버와 클라이언트의 역할은 하드웨어의 성능으로 구분하는 게 아니라 어떤 역할의 프로그램을 설치하느냐에 따라 결정된다.

![](https://velog.velcdn.com/cloudflare/iseeu95/69f1cdd4-bf04-46d1-bd84-f642dc2581ab/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202022-04-08%20%EC%98%A4%ED%9B%84%208.28.15.png)

#### 피어 투 피어(P2P) <a href="#p2p" id="p2p"></a>

네트워크에 연결된 두 대의 컴퓨터가 클라이언트와 서버의 역할을 동시에 할 수 있어서 서로에게 서비스를 주거나 받을 수 있는 통신 방식 주로 개인 컴퓨터 간의 파일 공유나 인터넷 전화(VoIP, Voice over IP)에 활용

***

### 패킷 교환 방식 <a href="#0104" id="0104"></a>

컴퓨터 네트워크는 패킷 교환 방식을 이용하여 여러 대의 컴퓨터와 혼선 없이 데이터를 주고받을 수 있다.

#### 패킷 교환 방식이란? <a href="#undefined" id="undefined"></a>

컴퓨터 네트워크에서는 이메일, 파일과 같은 데이터를 패킷이라는 작은 단위로 분할한 후 주고받는다.\
데이터를 패킷 단위로 작게 잘라서 네트워크에 흘려 보냄 \
패킷은 자신이 어디로 전달되어야 하는지 알 수 있도록 어드레스 정보를 가지고 있다.

#### 회선 교환 방식과 패킷 교환 방식 <a href="#undefined" id="undefined"></a>

아날로그 방식의 유선 전화나 3G 방식의 휴대전화는 회선 교환 방식을 사용

**회선교환 방식이란?**

통신하려는 양측을 연결하기 위해 하나의 통신 경로를 점유한 후 통신하는 방식이라서 기본적으로 일대일 통신만 할수 있다.

**패킷 교환 방식이란?**

주고받을 데이터를 작게 쪼갠 후 _**다른 데이터의 조각들과 통신 경로를 공유**_하며 전송하는 방식이라 여러 상대와 통신할 때 효과적

***

### 컴퓨터 네트워크과 계층 모델 <a href="#0105" id="0105"></a>

#### 컴퓨터 네트워크를 구성하는 계층 <a href="#undefined" id="undefined"></a>

![](https://velog.velcdn.com/cloudflare/iseeu95/8b807f11-4f0c-4a2e-99c3-9a28d97d277a/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202022-04-08%20%EC%98%A4%ED%9B%84%208.29.43.png)

4개 계층 중 서비스의 내용을 결정하는 것은 애플리케이션 계층뿐.\
나머지 3개 계층은 데이터를 전달하는 통신 기능을 담당.

#### 각 계층을 통과하는 데이터의 형태 <a href="#undefined" id="undefined"></a>

통신 과정에서 **각 계층을 지나는 데이터는 패킷 단위로 작게 쪼개지고 목적지 정보와 같은 부가 정보가 헤더의 형태**로 덧붙여지게 된다.

### OSI 참조 모델 <a href="#osi" id="osi"></a>

여기서 소개한 것은 인터넷에서 사용되는 TCP/IP의 계층 모델이다.

네트워크 관련 문서를 보면 OSI 참조 모델이 언급되기도 하는데, OSI 참조 모델은 TCP/IP 계층 모델보다 더 세분화된 7개의 계층으로 구성되어 있다.

#### TCP/IP는 프로토콜의 집합 <a href="#tcpip" id="tcpip"></a>

TCP/IP는 하나의 프로토콜을 지칭하는 말이 아니라 인터넷에서 사용되는 각종 표준 프로토콜을 한데 모아

흔히 TCP/IP라고 부르는 이유는 TCP와 IP가 이들 프로토콜 중 가장 대표적인 프로토콜이기 때문.\
각각의 개별 프로토콜을 일컫는 말이 아니라, 인터넷 프로토콜 집합의 의미로 굳이 구분해야 할 때는 TCP/IP 프로토콜 슈트(Suite)라고 한다.
