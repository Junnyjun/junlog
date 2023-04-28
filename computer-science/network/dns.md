# DNS ?

<figure><img src="https://github.com/cheatsnake/backend-cheats/raw/master/files/network-internet/domain_eng.png" alt=""><figcaption></figcaption></figure>

도메인 이름은 인터넷에서 사용할 수 있는 웹 서버의 사람이 읽을 수 있는 주소입니다. \
그들은 점으로 서로 분리된 부분(레벨)으로 구성됩니다. \
이러한 각 부분은 도메인 이름에 대한 특정 정보를 제공합니다. \
E.g) 국가, 서비스 이름, 현지화 등

*   도메인 이름을 소유한 사람

    > [ICANN Corporation은](https://en.wikipedia.org/wiki/ICANN) 분산 도메인 등록 시스템의 창시자입니다. 도메인 판매를 원하는 회사에 인증을 제공합니다. 이러한 방식으로 경쟁적인 도메인 시장이 형성됩니다.
*   도메인 이름을 구입하는 방법

    > 도메인 이름은 영원히 살 수 없습니다. 일정기간 임대합니다. [공인 등록 기관](https://www.icann.org/en/accredited-registrars?filter-letter=a\&sort-direction=asc\&sort-param=name\&page=1) 에서 도메인을 구입하는 것이 좋습니다 (거의 모든 국가에서 찾을 수 있음).

## 구조

### TLD (최상위 도메인).

TLD는 사용자에게 도메인 이름 뒤에 있는 서비스의 일반적인 목적을 알려줍니다. 가장 일반적인 TLD( `.com`, `.org`, `.net`)는 특정 기준을 충족하기 위해 웹 서비스를 요구하지 않지만 일부 TLD는 더 엄격한 정책을 시행하므로 목적이 무엇인지 더 명확합니다.&#x20;

```
.us, .fr또는 같은 로컬 TLD는 .se서비스가 특정 언어로 제공되거나 특정 국가에서 호스팅되도록 요구할 수 있습니다. 특정 언어 또는 국가의 리소스를 나타내야 합니다.
포함하는 TLD는 .gov정부 부서에서만 사용할 수 있습니다.
TLD .edu는 교육 및 학술 기관에서만 사용할 수 있습니다.
```

TLD에는 특수 문자와 라틴 문자가 포함될 수 있습니다. TLD의 최대 길이는 63자이지만 대부분은 2–3자입니다.

TLD의 전체 목록은 ICANN에서 관리합니다 .

## 작동

브라우저에 웹 페이지를 표시하려는 경우 IP 주소보다 도메인 이름을 입력하는 것이 더 쉽습니다.&#x20;

```
1. JUNNYLAND.COM을 브라우저에 검색
2. 브라우저는 로컬 DNS 캐시를 사용하여 이 도메인 이름으로 식별된 IP 주소를 이미 인식하고 있는지 컴퓨터에 묻습니다.
3. 컴퓨터가 이름 뒤에 어떤 IP가 있는지 모르는 경우 DNS 서버에 요청합니다. DNS 서버의 역할은 등록된 각 도메인 이름과 일치하는 IP 주소를 컴퓨터에 정확하게 알려주는 것입니다.
4.  이제 컴퓨터가 요청된 IP 주소를 알고 있으므로 브라우저가 웹 서버와 콘텐츠를 협상할 수 있습니다.
```

<img src="../../.gitbook/assets/file.excalidraw.svg" alt="" class="gitbook-drawing">
