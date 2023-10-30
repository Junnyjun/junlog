# 네트워크 관련 기초 용어

#### &#x20;1. 네트워크 기초 용어  <a href="#chapter-eb-a-ed-a-b-ec-b-c-ed-ac-ea-b-eb-a-a-ea-b-b-ec-b-ec-a-a-ec-b" id="chapter-eb-a-ed-a-b-ec-b-c-ed-ac-ea-b-eb-a-a-ea-b-b-ec-b-ec-a-a-ec-b"></a>

* 네트워크 : 전송 매체로 연결된 시스템의 모음

<figure><img src="https://blog.kakaocdn.net/dn/PyUvL/btrhxrN82mZ/ha4MRpR0FQZxMBmSe98RT1/img.png" alt=""><figcaption></figcaption></figure>

* 시스템 : 내부 규칙에 따라 능동적으로 동작하는 대상 ex) 컴퓨터, 자동차, 자판기 등
* 인터페이스 : 시스템과 전송 매체의 연결 지점에 대한 약속 ex) GUI, HCI, API, NAI 등
* 전송매체 : 시스템끼리 데이터를 전달하기 위한 물리적인 전송 수단 ex) 인터넷케이블, 무선, 광케이블 등
* 프로토콜 : 전송매체를 통해 데이터를 교환할 때의 임의의 통신 규칙
* 인터넷 : 전세계의 네트워크가 유기적으로 연결되어 동작하는 통합 네트워크
* 표준화 : 서로 다른 시스템이 상호 연동해 동작하기 위한 통일된 연동 형식(실제 제품간의 약속)

2\. 시스템 기초 용어

* 노드 : 인터넷에 연결된 시스템의 가장 일반적인 용어
* 호스트 : 컴퓨팅 기능이 있는 시스템
* 클라이언트 : 서비스를 요청하는 시스템
* 서버 : 서비스를 제공하는 시스템

&#x20;

**2 네트워크의 기능**

1\. 계층 모델

\- ISO의 OSI(Open System interconnection) 7계층 모델&#x20;

<figure><img src="https://blog.kakaocdn.net/dn/pHiO3/btrhtXT0cJ9/Sdk5PCA0zBoLKKk3Tkgd5K/img.png" alt=""><figcaption></figcaption></figure>

1\) 특징

\- H/W : 1계층  S/W : 2\~7계층

\- 일반 사용자는 응용계층을 통해 데이터의 송수신 요청하고, 순차적으로 하위계층으로 내려가 전송된다.

\- 반대로 호스트(1계층) 반대방향으로 처리가 이루어진다.&#x20;

\- Kernel-mode : 1\~4  User-mode : 5\~7

&#x20;

✅ OSI 7계층 모델의 계층별 기능&#x20;

1. 물리 계층: 물리적으로 데이터를 전송하는 역할을 수행
2. 데이터 링크 계층: 물리적 전송 오류를 해결 (오류 감지 / 재전송 기능)&#x20;
3. 네트워크 계층: 올바른 전송 경로를 선택 (혼잡 제어 포함)&#x20;
4. 전송 계층: 송수신 프로세스 사이의 연결 기능을 지원&#x20;
5. 세션 계층: 대화 개념을 지원하는 상위의 논리적 연결을 지원&#x20;
6. 표현 계층: 데이터의 표현 방법&#x20;
7. 응용 계층: 다양한 응용 환경을 지원

&#x20;

2\. 프로토콜과 인터페이스

1\) 프로토콜 : 서로 다른 호스트에 위치한 동일 계층끼리의 통신 규칙

2\) 인터페이스 : 같은 호스트에 위치한 상하위 계층 사이의 규칙

<figure><img src="https://blog.kakaocdn.net/dn/bZrg8s/btrhvPu7BGw/G75wYh8NcQZS5WUwR1mHtK/img.png" alt=""><figcaption></figcaption></figure>

&#x20;

3\) 인터넷 계층 구조

\- 네트워크 계층(3), 전송계층(4)으로 되어 있음

ex) FTP 서비스

FTB 클라이언트 <-> TCP <-> IP <-> LAN카드 드라이버 <-> 이더넷

FTB 서버 <-> TCP <-> IP <-> LAN카드 드라이버 <-> 이더넷

&#x20;

3\. 인터 네트워킹

1\) 정의 : 네트워크와 네트워크의 연결을 의미한다.

\- 게이트 웨이 : 인터네트워킹 기능을  수행하는 시스템

* 리피터 : 물리계층을 지원(신호를 증폭해줌)
* 브리지 : 물리계층과 데이터 링크 계층을 지원(물리계층에서 발생한 오류 해결)
* 라우터 : 물리계층, 데이터링크계층, 네트워크 계층 지원 (판단하는 역할)

4\. 데이터 단위

* APDU: 응용 계층의 데이터 단위
* PPDU: 표현 계층의 데이터 단위
* SPDU: 세션 계층의 데이터 단위
* TPDU: 전송 계층의 데이터 단위 세그먼트 − 세그먼트(TC), 데이터그램(UDP)
* NPDU: 네트워크 계층의 데이터 단위 − 패킷
* DPDU: 데이터 링크 계층의 데이터 단위 − 프레임

&#x20;

**3 네트워크 주소의 표현**

1\. 주소와 이름

1\) IP주소

\- IPv4 프로토콜 사용 (32BIT)

\- IPv6에서는 128 비트 주소 체계로 확장

<figure><img src="https://blog.kakaocdn.net/dn/bltpRm/btrhsgGIy1l/koM8wm6c5qPBQ5zh3h5skK/img.png" alt=""><figcaption></figcaption></figure>

2\) 호스트 이름

\- 호스트이름과 IP주소의 변환

\* 일반 사용자가 호스트 이름(naver.com)으로 상위계층에 서비스를 요청하면, dns에서 호스트 이름에 대한 ip 주소를 받아오고, 네트워크 계층으로 전송한다.

\- 호스트 이름 구조 : <호스트>.<단체 이름>.<단체 종류>.<국가 도메인> ex) zebra.korea.co.kr

<figure><img src="https://blog.kakaocdn.net/dn/J47ST/btrhAfF3xBv/rYkob8lE5hBifa6Btcn7kK/img.png" alt=""><figcaption></figcaption></figure>

&#x20;

2\. 주소 정보의 관리

\- 호스트 파일 : 호스트 이름과 IP 주소의 조합을 텍스트 파일로 관리
