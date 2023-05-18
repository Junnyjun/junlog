# Internet ?

## INTERNET

인터넷은 정보를 저장/전송하기 위해 전 세계의 컴퓨터 네트워크를 단일 네트워크로 연결하는 거대한 네트워크

#### 인터넷 작동 방식

<figure><img src="https://github.com/cheatsnake/backend-cheats/raw/master/files/network-internet/Internet.png" alt=""><figcaption></figcaption></figure>

컴퓨터는 인터넷에 직접 액세스할 수 없습니다.\
대신 유선( 이더넷 ) 또는 무선(Wi-Fi) 연결을 통해 다른 장치(미니 컴퓨터 라우터)가 연결된 로컬 네트워크에 액세스할 수 있습니다 . \
이 장치는 사용자를 인터넷 서비스 제공업체(ISP) 에 연결하고 ISP는 다른 상위 수준의 ISP에 연결됩니다. \
따라서 이러한 모든 상호 작용이 인터넷을 구성하고 메시지는 항상 최종 수신자에게 도달하기 전에 다른 네트워크를 통해 전송됩니다.

Host : 모든 네트워크에 있는 모든 장치.\
Server : 다른 컴퓨터의 요청을 처리하는 네트워크의 특수 컴퓨터입니다.

네트워크 토폴로지 : [포인트 투 포인트](https://en.wikipedia.org/wiki/Point-to-point\_\(telecommunications\)) , [데이지 체인](https://en.wikipedia.org/wiki/Daisy\_chain\_\(electrical\_engineering\)) , [버스](https://en.wikipedia.org/wiki/Bus\_network) , [링](https://en.wikipedia.org/wiki/Ring\_network) , [스타](https://en.wikipedia.org/wiki/Star\_network) 및 [메쉬](https://github.com/cheatsnake/backend-cheats/blob/master)  등\
인터넷 자체는 서로 다른 토폴로지가 혼합된 복잡한 시스템이기 때문에 어느 하나의 토폴로지를 참조할 수 없습니다.

## DNS

<figure><img src="https://github.com/cheatsnake/backend-cheats/raw/master/files/network-internet/domain_eng.png" alt=""><figcaption></figcaption></figure>

도메인 이름은 인터넷에서 사용할 수 있는 웹 서버의 사람이 읽을 수 있는 주소입니다. \
그들은 점으로 서로 분리된 부분(레벨)으로 구성됩니다. \
이러한 각 부분은 도메인 이름에 대한 특정 정보를 제공합니다. \
E.g) 국가, 서비스 이름, 현지화 등

*   도메인 이름을 소유한 사람

    > ICANN Corporation은 분산 도메인 등록 시스템의 창시자입니다. 도메인 판매를 원하는 회사에 인증을 제공합니다. 이러한 방식으로 경쟁적인 도메인 시장이 형성됩니다.
*   도메인 이름을 구입하는 방법

    > 도메인 이름은 영원히 살 수 없습니다. 일정기간 임대합니다. 공인 등록 기관 에서 도메인을 구입하는 것이 좋습니다 (거의 모든 국가에서 찾을 수 있음).

### 구조

#### TLD (최상위 도메인).

TLD는 사용자에게 도메인 이름 뒤에 있는 서비스의 일반적인 목적을 알려줍니다. 가장 일반적인 TLD( `.com`, `.org`, `.net`)는 특정 기준을 충족하기 위해 웹 서비스를 요구하지 않지만 일부 TLD는 더 엄격한 정책을 시행하므로 목적이 무엇인지 더 명확합니다.&#x20;

```
.us, .fr또는 같은 로컬 TLD는 .se서비스가 특정 언어로 제공되거나 특정 국가에서 호스팅되도록 요구할 수 있습니다. 특정 언어 또는 국가의 리소스를 나타내야 합니다.
포함하는 TLD는 .gov정부 부서에서만 사용할 수 있습니다.
TLD .edu는 교육 및 학술 기관에서만 사용할 수 있습니다.
```

TLD에는 특수 문자와 라틴 문자가 포함될 수 있습니다. TLD의 최대 길이는 63자이지만 대부분은 2–3자입니다.

TLD의 전체 목록은 ICANN에서 관리합니다 .

<figure><img src="https://github.com/cheatsnake/backend-cheats/raw/master/files/network-internet/dns.png" alt=""><figcaption></figcaption></figure>

DNS(도메인 이름 시스템)는 컴퓨터에서 사용하는 숫자 IP 주소 에 해당하는 사람이 읽을 수 있는 알파벳 이름(도메인 이름)을 만들 수 있는 분산형 인터넷 주소 명명 시스템입니다 .

*   DNS의 구조

    > DNS는 많은 독립적인 노드로 구성되며 각 노드는 책임 영역에 속하는 데이터만 저장합니다.
*   DNS 리졸버

    > 인터넷 서비스 공급자와 가까운 곳에 위치한 서버입니다. \
    > 도메인 이름으로 주소를 검색하고 캐시하는 서버입니다(향후 요청 시 빠른 검색을 위해 일시적으로 저장)
*   DNS 레코드 유형

    > * 레코드 - 도메인 이름을 IPv4 주소와 연결합니다.
    > * AAAA 레코드 - 도메인 이름을 IPv6 주소와 연결합니다.
    > * CNAME 레코드 - 다른 도메인 이름으로 리디렉션합니다.
    > * 기타 - MX 레코드, NS 레코드, PTR 레코드, SOA 레코드.

### 작동

브라우저에 웹 페이지를 표시하려는 경우 IP 주소보다 도메인 이름을 입력하는 것이 더 쉽습니다.&#x20;

```
1. JUNNYLAND.COM을 브라우저에 검색
2. 브라우저는 로컬 DNS 캐시를 사용하여 이 도메인 이름으로 식별된 IP 주소를 이미 인식하고 있는지 컴퓨터에 묻습니다.
3. 컴퓨터가 이름 뒤에 어떤 IP가 있는지 모르는 경우 DNS 서버에 요청합니다. DNS 서버의 역할은 등록된 각 도메인 이름과 일치하는 IP 주소를 컴퓨터에 정확하게 알려주는 것입니다.
4.  이제 컴퓨터가 요청된 IP 주소를 알고 있으므로 브라우저가 웹 서버와 콘텐츠를 협상할 수 있습니다.
```

<img src="../../../.gitbook/assets/file.excalidraw (7).svg" alt="" class="gitbook-drawing">

## BROWSER

브라우저 및 작동 방식

<figure><img src="https://github.com/cheatsnake/backend-cheats/raw/master/files/network-internet/browser_eng.png" alt=""><figcaption></figcaption></figure>

브라우저는 웹 페이지를 렌더링하는 데 사용할 수 있는 파일에 대한 요청을 서버에 보내는 데 사용할 수 있는 클라이언트입니다. 간단히 말해서 브라우저는 인터넷에서 검색하고 다운로드할 수 있는 HTML 파일을 보기 위한 프로그램으로 생각할 수 있습니다.

작동 원리

> 쿼리 처리, 페이지 렌더링 및 탭 기능(각 탭에는 한 탭의 내용이 다른 탭의 내용에 영향을 미치지 않도록 하는 자체 프로세스가 있음).

확장 프로그램

> 브라우저의 사용자 인터페이스를 변경하고 웹 페이지의 내용을 수정하며 브라우저의 네트워크 요청을 수정할 수 있습니다.

개발자 도구

> 모든 웹 개발자에게 없어서는 안될 도구입니다. \
> 이를 통해 웹 페이지와 관련된 가능한 모든 정보를 분석하고 성능, 로그를 모니터링하고 가장 중요한 것은 네트워크 요청에 대한 정보를 추적할 수 있습니다.
