# DNS가 만들어진 배경

## IP주소와 이름의 관계
전 세계에 있는 모든 컴퓨터는 IP 주소를 가지고 있습니다.\
이 IP 주소는 32비트로 구성되어 있으며, 4개의 8비트로 나누어져 있습니다.\
이 4개의 8비트를 각각 `A`, `B`, `C`, `D`라고 부릅니다. \
이 4개의 8비트는 10진수로 변환되어 `A.B.C.D`로 표현됩니다. 

현재 사용되고 있는 IP 주소 체계는 두 종류가 있습니다.
1. IPv4
2. IPv6

IPv4는 32비트로 구성되어 있어서 4,294,967,296개의 주소를 가질 수 있습니다.\
IPv6는 128비트로 구성되어 있어서 340,282,366,920,938,463,463,374,607,431,768,211,456개의 주소를 가질 수 있습니다.

IP 주소는 2진수로 표현되어 있어서 사람이 이해하기 어렵습니다.\
그래서 IP 주소를 사람이 이해하기 쉬운 형태로 변환하는 방법이 있습니다.\
이것을 `DNS(Domain Name System)`이라고 부릅니다.

##  IP 주소와 이름의 대응 관리
DNS는 IP 주소와 이름을 대응시켜주는 시스템입니다.\
DNS는 IP 주소를 사람이 이해하기 쉬운 이름으로 변환하거나, 그 반대로 이름을 IP 주소로 변환해줍니다.

인터넷 초기 시절에는 IP 주소와 이름을 대응시키는 호스트 파일을 사용했습니다.
```bash
# /etc/hosts

127.0.0.1      localhost
8.8.8.8        google.com
...
```
초기 host파일은 SRI-NIC에 접속 신청을 한 뒤, 각 조직의 신청 내용을 확인 한 뒤, 주소를 할당 받아 사용했습니다.

## 집중 관리에서 분산 관리로

인터넷이 발전함에 따라 호스트 파일로 관리하는 것이 어려워졌습니다.  
관리의 어려움으로 인해 `계층화`와 `위임`을 통해 분산 관리 방식으로 전환하게 되었습니다.

### 계층화
인터넷 에서는 네임 스페이스 라고 불리는 하나의 공간을 전체에서 공유하고 있습니다.\
이러한 네임 스페이스의 일부를 분할하고 잘라낸 네임 스페이스를 신뢰할 수 있는 사람에게 위임하는 구조가 채택 되었습니다.

#### 이름을 고유하게 하는 구조

HOSTS 파일은 중복 되지 않도록 하나의 조직이 일원화 관리를 했습니다.
이로써 이름과 IP 주소의 대응 관계가 중복되지 않도록 관리되었습니다.

이렇게 하나의 정점에서 가지가 뻗어나가는 트리구조로 계층화와 위임이 관리되었습니다.

## 도메인 이름의 구성

인터넷에서 분할된 각 네임 스페이스의 범위를 도메인, \
그 범위를 식별 하는 이름을 도메인 이름이라고 합니다.

도메인 이름이란 인터넷에서 사용하는 이름으로, 도메인 이름은 `.`으로 구분되어 있습니다.\
도메인 이름은 `오른쪽에서 왼쪽`으로 읽어야 합니다.

### 도메인 이름의 구성

도메인 각각의 문자열을 `레이블`이라고 부릅니다.\
각 레이블은 `.`으로 구분되어 있습니다.

루트를 기준으로 오른쪽에서 순서대로 읽어나가면서 도메인 이름을 구성합니다.
- `TLD(Top Level Domain)` : 최상위 도메인
- `2LD(Second Level Domain)` : 2차 도메인
- `3LD(Third Level Domain)` : 3차 도메인
...

### DNS란 ?

DNS는 Domain Name System의 약자로, 도메인 이름과 IP 주소를 대응시켜주는 시스템입니다.\
DNS는 IP 주소를 사람이 이해하기 쉬운 이름으로 변환하거나, 그 반대로 이름을 IP 주소로 변환해줍니다.