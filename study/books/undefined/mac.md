# MAC 계층

### 01 MAC 계층과 IEEE 802 시리즈

#### &#x20;1. MAC 계층

<figure><img src="https://blog.kakaocdn.net/dn/DrowV/btrcQeyuVqq/66Wpk98y2vtovuTXwBjZaK/img.png" alt="" height="274" width="432"><figcaption></figcaption></figure>

&#x20; \- LAN 환경에서는 네트워크 자원을 효율적으로 활용하기 위해 그림과 같이 LLC와 MAC 계층으로 나누어 처리한다.

&#x20;OSI 7 계층 모델에서 정의한 2 계층의 기본 기능은 LLC 계층에서, 물리적인 전송 선로의 특징과 매체 간의 연결 방식에 따른 제어 부분은 MAC 계층에서 처리한다.

&#x20;

&#x20; **1.1 MAC 계층**

&#x20; MAC(Medium Access Control) 계층은 전송 선로의 물리적인 특성을 반영하므로 LAN의 종류에 따라 특성이 구분된다.&#x20;

&#x20; 공유 버스 방식을 지원하는 이더넷과 링 구조를 지원하는 토큰 링 방식이 있다.

&#x20;이더넷에서는 데이터를 전송하기 전에 전송 선로를 먼저 확인해 다른 호스트가 데이터를 전송 중인지 여부를 파악해야 한다. 다른 호스트가 전송 선로를 사용하지 않으면 데이터를 전송할 수 있지만 사용하고 있다면 나중에 다시 시도한다.

&#x20;

&#x20;

&#x20; **1.2 LLC 계층**

&#x20; LLC(Local Link Control) 계층은 프레임 전송 과정에서 슬라이딩 윈도우 프로토콜을 사용.

#### &#x20;2. IEEE 802 시리즈

&#x20; : 국제 표준화 단체로 LAN 표준안을 제시.

802.1 : 표준안을 소개와 인터페이스 프리미티브에 대한 정의 / 802.2 : 2 계층의 상위 부분인 LLC 프로토콜에 대한 정의

802.3 : 물리 계층과 MAC 계층에 대한 내용 CSMA/CD 방식 / 802.4 : 토큰 버스 방식

802.5 : 토큰 링 방식..

<figure><img src="https://blog.kakaocdn.net/dn/348rg/btrcQeyvhXG/lPSput4PKvp0nv2OXhwQ21/img.png" alt="" height="316" width="395"><figcaption></figcaption></figure>

&#x20; **2.1 CSMA/CD(Carrier Sense Multiple Access / Collision Detetion)**

&#x20;: 다중 접근 채널(Multiple Access Channel) 방식을 이용해 공유 매체에 프레임을 전송하는 방식에서 충돌을 허용하는 방식

&#x20; \- CSMA/CD 방식에서는 충돌로 깨진  프레임을 복구하는 작업이 필요하기 때문에 프레임을 송신한 호스트에서 충돌을 감지하는 기능이 반드시 필요하다.

&#x20;충돌이 자주 발생하면 재전송도 많이 하기 때문에 네트워크 성능이 떨어지고 공유 매체의 길이가 길수록 프레임의 전송 지연이 증가하여 충돌이 발생할 가능성도 높아진다.

&#x20;

&#x20;

&#x20; **2.2 토큰 버스**

&#x20; : 물리적인 버스 구조로 연결되지만 논리적인 프레임 전달은 링 구조로 데이터 프레임 전송이 호스트 사이에 순차적으로 이루어지도록 토큰이라는 제어 프레임을 사용한다.

<figure><img src="https://blog.kakaocdn.net/dn/7hdBR/btrcIgyd9r3/Gsb3YPQW77NH4MRYZZ57b1/img.png" alt="" height="201" width="488"><figcaption></figcaption></figure>

&#x20; **2.3 토큰 링**

&#x20; \- 순환 구조의 링 동작은 대기 모드와 전송 모드가 있다.

&#x20;대기 모드는 입력으로 들어온 비트를 출력으로 즉시 내보내서 데이터 전송만 한다.

&#x20;전송 모드는 호스트가 토큰을 획득해 프레임을 전송할 수 있는 권한을 보유한 상태로 입력 단과 출력단의 논리적인 연결이 끊어지는 대신 호스트의 중개를 거쳐서 연결된다.

&#x20;호스트는 전송하고자 하는 프레임을 출력단을 통해 링으로 내보낼 수 있다.

<figure><img src="https://blog.kakaocdn.net/dn/poFFM/btrcOSCkGbo/TYIVHKSMn5vsZO80j2PcH0/img.png" alt="" height="282" width="264"><figcaption></figcaption></figure>

### 02 이더넷(IEEE 802.3)

#### &#x20;1. 이더넷과 신호 감지 기능

&#x20;  \-공유 버스에서 충돌을 방지하려면 다른 호스트가 공유 버스를 사용하고 있는지 확인해야 하는데 이는 신호 감지(Carrier Sense) 프로토콜 사용해 전송 매체의 신호를 감지해 프레임의 전송 여부를 결정한다.

&#x20; **1.1 1-persistent CSMA**

&#x20;: 일반 신호 감지 프로토콜처럼 프레임을 전송하기 전에 전송 채널이 사용 중인지 확인하고 사용 중이라고 판단하면 유휴 상태가 될 때까지 대기. 유휴 상태가 되면 확률 1의 조건으로 프레임을 무조건 전송한다.(대기 후 전송)

&#x20;

&#x20; **1.2 Non-persistent CSMA**

&#x20;: 전송 채널의 신호를 감지해 채널이 사용 중이라고 판단하면 채널의 유휴 상태를 확인하지 않지만 임의의 시간 동안 기다린 후에 다시 채널 감지를 시작한다. 1-persistent 보다 충돌 발생할 확률이 줄어든다.

&#x20;

&#x20; **1.3 p-persistent CSMA**

&#x20; : 슬롯 채널 방식에서 많이 사용한다. 채널이 유휴 상태이면 p의 확률로 프레임을 전송하고 채널이 사용 중이면 다음 슬롯을 기다린 후 앞의 과정을 반복한다.

&#x20;

&#x20; **1.4 CSMA/CD**

&#x20; \- 둘 이상의 호스트에서 동시에 채널의 유휴 상태를 확인할 가능성이 있어서 여러 호스트가 동시에 채널을 사용할 수 있다고 판단할 수 있고 충돌 발생 확률도 높아진다.

&#x20;\- 충돌이 발생하면 해당 프레임의 내용이 깨지고, 각 호스트에서 전송한 프레임의 내용이 변형되므로 전송하는 것이 의미가 없어 CSMA/CD에서는 충돌 감지(Collision Sense) 기능을 사용해 충돌 여부를 확인한다.

<figure><img src="https://blog.kakaocdn.net/dn/l7u7T/btrcSsXzXcl/CWi6BSywqua9phX4H0ZS2K/img.png" alt="" height="359" width="378"><figcaption></figcaption></figure>

&#x20;\-> 전송 케이블로 된 전송 매체에 트랜시버 장비로 보조선을 연결해 각 호스트를 연결한다.

트랜시버(Transceiver)는 호스트를 전송 케이블에 연결하기 위한 송수신 장치로, 전송 선로의 신호를 감지하는 기능과 함께 충돌 현상을 감지하는 기능도 제공한다.

충돌을 감지하면 특정 신호의 형태로 변환해 전송 케이블에 다시 전송해 충돌 발생을 알려준다.

\-케이블의 길이가 너무 길면 신호 감쇄 현상으로 인한 오류가 발생하는데 이는 리피터로 해결한다.

&#x20;

#### &#x20;2. 프레임 구조

&#x20;\- 상위 계층인 LLC에서 내려온 프레임을 상대 호스트에 전송하려면 MAC 계층에서 정의된 프레임 구조에 맞게 포장해야 한다.

&#x20;MAC 프레임 : MAC 계층 프로토콜에 정의된 MAC 헤더와 트레일러 정보를 추가한 것으로 이더넷 프로토콜에선 이더넷 프레임이라 한다. MAC 프레임은 LLC 계층에서 보낸 모든 정보를 전송 데이터로 취급하며, 데이터 앞에 헤더, 뒤에는 트레일러가 붙는다.

<figure><img src="https://blog.kakaocdn.net/dn/wgLyB/btrcOiIaEY3/XA6JWDR68Trk2jwKclTmKK/img.png" alt="" height="160" width="513"><figcaption></figcaption></figure>

&#x20;\-> 이더넷 프레임(Ethernet Frame) 구조로 바이트 단위이며 Source Address와 Destination Address는 6바이트의 MAC 주소를 사용하며 Data와 Padding 필드는 가변 길이를 지원

* Preamble(프리엠블) - 7바이트 크기로 수신 호스트가 송신 호스트의 클록과 동기를 맞출 수 있도록 시간 여유를 제공하는 것이 목적.
* Start Delimiter(시작 구분자) - 프레임이 시작된다는 의미로 사용되며, Preamble 필드와 구분하기 위해 10101011의 값을 갖는다. 마지막 2비트는 10이지만 Start Delimiter의 값은 11로 끝난다.
* Source Address/Destination Address(송신 호스트 주소/수신 호스트 주소) : MAC 계층에서는 호스트를 구분하는 고유의 MAC 주소를 사용한다. MAC 주소 값은 일반적으로 LAN 카드에 내장되어 제공된다. 두 필드는 전송되는 프레임의 송수신 호스트 주소를 표현한다.

&#x20;  \- 최상위 비트가 1이면 그룹 주소, 0이면 일반 주소

* Length/Type : 필드 값이 1500 이하면 Data 필드의 데이터 크기를 의미하는 Length, 아니면 Type으로 해석.

&#x20; \- Length(길이) : Data 필드에 포함된 가변 길이의 전송 데이터 크기를 나타내며, 최댓값은 1500이다. IP 패킷의 크기가 이 값을 초과하면 먼저 분할 과정이 이뤄져야 하고 Data와 Padding을 합한 데이터의 최소 크기는 46바이트로 길이가 46보다 작으면 Padding 필드에 해당하는 크기만큼 0으로 채운다

&#x20; \- Type(종류) : 이더넷 프레임에 캡슐화된 상위 프로토콜의 패킷 종류를 구분한다.

* Checksum(체크섬) : 데이터 전송 과정에서 데이터 변형 오류의 발생 여부를 수신 호스트가 확인할 수 있도록 송신 호스트가 값을 기록해준다.

&#x20;

#### &#x20;3. LLC 프레임 캡슐화

&#x20;\- 3 계층에서 전송을 요구한 패킷은 LLC 계층으로 내려오면서 LLC헤더 정보를 추가해 LLC 프레임이 된다.

&#x20;  LLC 프레임은 다시 MAC 계층으로 내려오면서 MAC 헤더와 트레일러를 추가한다. Data 필드에 기록

&#x20;  이후 MAC 계층에서는 MAC 프레임을 물리 계층을 사용하여 수신 호스트에 전송

<figure><img src="https://blog.kakaocdn.net/dn/btsVDi/btrcMFEte30/KHjQ2vMs2cx59KLySX2UD1/img.png" alt="" height="357" width="502"><figcaption></figcaption></figure>

&#x20;

#### &#x20;4. 허브와 스위치

&#x20;\- CSMA/CD에선 트랜시버를 사용하지 않고 허브라는 박스 형태의 장비에 잭을 사용해 호스트를 연결하기 때문에 LAN 케이블의 구성이 간단해졌다.

* 허브, 스위치, 버스 차이

<figure><img src="https://blog.kakaocdn.net/dn/FcNQP/btrcQeyY8Gf/v72MKXhvypezVzd2oMy2W0/img.png" alt="" height="394" width="511"><figcaption></figcaption></figure>

&#x20; **4.1 허브**

&#x20;\- 박스 형태의 장비에 호스트를 연결하는 다수의 포트를 지원하므로, 각 호스트는 외형상 허브에 스타형 구조로 연결된다. 내부는 임의의 호스트에서 전송한 프레임을 허브에 연결된 모든 호스트에 전달한다.

&#x20;\- 공유 버스 방식으로 충돌이 발생할 수 있어 LAN에서는 전체 전송 용량이 각 호스트를 연결하는 전송 선로 용량의 제한을 받는다.

&#x20;

&#x20; **4.2 스위치**

&#x20;\- 스위치 허브는 중앙에 위치한 허브에 스위치 기능이 있어 임의의 호스트로부터 수신한 프레임을 모든 호스트에 전송하지 않고 해당 프레임의 목적지로 지정한 호스트에만 전송한다.

&#x20;

* &#x20;장점

&#x20; \- 스위치 허브가 자신에게 연결된 호스트를 모두 수용할 수 있는 충분한 전송 용량을 지원하며 각 호스트는 할당된 LAN 전송 용량을 모두 사용할 수 있다.

&#x20; \- 일반 허브를 스위치 허브로 교체하는 과정에서 연결된 호스트는 하드웨어나 소프트웨어를 교체할 필요 없다.

&#x20;

### 03 토큰 버스

#### &#x20; 1. 프레임 구조

&#x20;\- LLC 계층에서 내려온 프레임을 물리 계층을 통해 수신 호스트에 전달하려면 토큰 버스 프레임에 맞게 만들어야 한다.

<figure><img src="https://blog.kakaocdn.net/dn/dkginB/btrcNMDekI7/xAck35S6pmTdshNs4TgEZk/img.png" alt="" height="230" width="662"><figcaption></figcaption></figure>

CSMA/CD와의 차이점은 데이터 프레임과 토크 프레임을 구분하기 위한 Frame Control 필드가 추가되어 있다.

* Start Delimiter/End Delimiter(시작 구분자/끝 구분자) : 프레임의 시작과 끝을 의미

&#x20; \- 이더넷 프레임은 Length 필드가 있어서 프레임의 전체 크기를 가늠하지만 여기선 End Delimiter 필드가 대신한다.

* Preamble/Source Address/Destination Address/Checksum - 이더넷과 동일
* Frame Control(프레임 제어) - 데이터 프레임이고 제어 프레임을 구분해준다. 데이터 프레임에서는 프레임 우선순위와 수신 호스트의 응답 확인이 필요할 때 사용하고 제어 프레임에서는 토큰의 전달, 링 관리와 같은 용도로 사용.

<figure><img src="https://blog.kakaocdn.net/dn/n3tE4/btrcQeTkKJ8/TKC2w4dcTiDx8kyzm444wk/img.png" alt="" height="259" width="392"><figcaption></figcaption></figure>

&#x20;\-> Frame Control 필드로 첫 번째 2비트 값에 따라 역할이 구분된다. (00,01,10,11)

&#x20;  따라서 토큰 버스 프로토콜에서 상대 호스트에 전송할 데이터 프레임에 해당.

&#x20; 토큰 버스에서 우선순위는 값 0, 2, 4, 6인 네 개의 클래스로 나누며 숫자가 클수록 우선순위가 높다.

&#x20;

#### &#x20; 2. LLC 프레임 캡슐화

&#x20;  \- LLC에서 MAC 계층까지 전달됐다. 이제 프레임의 Data 필드에 캡슐화되어 전송되는데 LLC프레임의 좌우에 토큰 버스 프레임의 헤더와 트레일러 정보가 채워지면 물리계층이 -> 수신 호스트로 전송한다.

&#x20;수신 호스트의 MAC 계층은 토큰 버스 프레임의 헤더와 트레일러 정보를 해석하여 떼어내고, 상위 계층인 LLC 프로토콜에 LLC 프레임만 올려준다.

<figure><img src="https://blog.kakaocdn.net/dn/bW1iw0/btrcYbBmeay/DSSwlp8BxljLniafXqluT0/img.png" alt="" height="403" width="638"><figcaption></figcaption></figure>

### 04 토큰 링

&#x20;\- 점대점으로 연결한 호스트가 순환 구조 형태로 LAN을 구성.

#### &#x20; 1. 프레임 구조

&#x20; \- 토큰 링 프레임은 데이터 프레임과 토큰 프레임으로 구분.

<figure><img src="https://blog.kakaocdn.net/dn/5jec1/btrcOefdDQn/N5vsF13j3Z6A6AMNwuaS51/img.png" alt="" height="277" width="588"><figcaption></figcaption></figure>

&#x20; \-> 모니터(monitor) : 링에 연결된 호스트 중에는 다른 호스트와 구별되는 특별한 기능을 수행하는 관리 호스트.

&#x20;모니터로 지정된 호스트는 네트워크 관리와 관련된 기능을 수행하고 주로 네트워크의 정상 동작을 방해하는 오류를 복구한다.

&#x20;오류 1 -  현재 데이터 프레임을 전송하는 호스트가 없는데 링에 토큰 프레임이 없어지는 오류 -> 이때는 모니터 호스트에서 토큰 프레임을 새로 생성해 다른 호스트가 데이터 프레임을 정상적으로 전송할 수 있도록 해야 한다.

&#x20;오류 2 - 링이 한번 순환하면 송신 호스트에 의해 링이 제거되야하는데 무한정 순환하는 경우 -> 모니터가 관리

&#x20;

#### &#x20; 2. LLC 프레임 캡슐화

&#x20;\- LLC 계층에서 MAC 계층으로 전송 요청이 내려온 LLC 프레임을 토큰 링 프레임의 구조로 캡슐화하는 과정

<figure><img src="https://blog.kakaocdn.net/dn/Jbsyy/btrcSsqgCCW/tJ2JilkkGRIHmqf1u4LqY0/img.png" alt="" height="418" width="666"><figcaption></figcaption></figure>

&#x20; \-> 토큰 링 프레임의 Data 필드에 기록되고, 나머지 토큰 링 프레임의 필드 값을 기록한 후에 물리 계층을 통해 수신 호스트에 전달된다.

&#x20;

#### &#x20; 3. 프레임 필드의 의미

&#x20;   **3.1 Start Delimiter/End Delimiter(SD/ED)**

&#x20; \- 필드 내용 중에서 송수신 호스트 주소, 데이터, 체크섬 등은 이더넷 토큰 버스에서 용도와 같다.

&#x20;   SD/ED 필드는 프레임의 시작과 끝으로  I와 E라는 두 종류의 비트가 정의되어 있다.

&#x20;  \- I비트는 데이터의 처음과 중간 프레임은 I 비트의 값을 1로 지정해 전송하고 마지막 프레임은 값을 0으로 지정해서       수신 호스트가 연속 데이터를 구분하여 수신할 수 있게 해 준다.

&#x20;  \- E 비트는 오류 검출용

&#x20;

&#x20;   **3.2 Access Control**

&#x20;   : 여러 가지 제어 기능을 수행하기 위해 사용된다. 우선순위 비트 / 토큰 비트 / 모니터 비트 / 예약 비트

<figure><img src="https://blog.kakaocdn.net/dn/TRRFr/btrcZtPrMkJ/XpeDWvNF6zSnudsWNRdtR1/img.png" alt="" height="228" width="596"><figcaption></figcaption></figure>

* 우선순위 비트 : 토큰의 우선순위보다 높은 프레임을 전송할 수 있도록 해준다. 000이 가장 낮고 111이 가장 높다.
* 토큰 비트 : 토큰 프레임과 일반 프레임을 구분하는 데, 토큰 프레임은 값이 0이다.
* 모니터 비트 : 네트워크에 오류가 발생하면 특정 프레임이 링 주위를 무한정 순환하는 현상을 방지하기 위해 특정 호스트를 모니터로 지정하여 데이터 프레임이 자신을 지나갈 때 M 비트를 1로 지정한다. M 비트가 1인 프레임이 다시 모니터 호스트를 지나가면 해당 프레임이 한 번 이상 링을 순환했다는 의미가 된다. 따라서 모니터 호스트는 이 프레임을 링에서 제거함으로써, 특정 데이터 프레임이 링을 무한정 순환하는 현상을 방지한다.&#x20;

&#x20;

&#x20;   **3.3 Frame Control**

&#x20;  \- LLC 계층에서 목적지 호스트로 전송해줄 것을 요청한 LLC 프레임과 토큰 링에서 사용하는 제어용 프레임을 구분하는데 사용

<figure><img src="https://blog.kakaocdn.net/dn/bHAm71/btrcQeMATyT/JFhepr6upvTKsjY1VPlQy0/img.png" alt="" height="331" width="548"><figcaption></figcaption></figure>

&#x20;  \-> TT 비트의 값에 의한 프레임 구분

&#x20;   00 - 제어 기능을 수행하기 위한 프레임을 위해 정의 CCCCCC 비트의 코드 값으로 제어 명령의 종류를 구분

&#x20;   01 - 상위 계층인 LLC 계층에서 전송을 요구한 LLC 프레임을 의미

&#x20;

&#x20;   **3.4 Frame Status**

&#x20; \- 토큰 링 프레임의 맨 마지막에 위치하며 프레임의 수신 호스트가 송신 호스트에 응답할 수 있도록 한다.

&#x20;

<figure><img src="https://blog.kakaocdn.net/dn/TMY5T/btrcNmriS8f/KIsbMfbvuYZI3y6Frqr0Kk/img.png" alt="" height="202" width="438"><figcaption></figcaption></figure>

&#x20; \-> A, C 필드로 값이 동일한 경우에만 유효한 응답으로 정의 다르면 0

&#x20; A비트 : 목적지로 지정한 호스트에서 데이터 프레임이 링 인터페이스를 통해 자신에게 전달되면 해당 프레임에 접근했다는 포시로 A 비트를 1로 지정. 데이터 프레임의 수신 호스트 주소가 자신의 주소와 다르면 그냥 통과시키며 목적지 호스트가 네트워크에서 제대로 동작하는지 확인하는 것.

&#x20; C비트 : 입력된 데이터 프레임의 수신 호스트 주소가 자신의 주소와 동일한 프레임이 지나가면 프레임을 내부 버퍼에 보관하고 C 비트를 1로 지정한다. C 비트가 1로 지정된 프레임이 다시 송신 호스트에 돌아가면 A와 C 비트를 점검해 수신 호스트가 데이터 프레임을 제대로 수신했는지 확인할 수 있다.
