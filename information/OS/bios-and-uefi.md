---
description: what?
---

# BIOS\&UEFI 무슨 차이가 있나요?

## BIOS

* **펌웨어** 입니다 (하드웨어 장치에 영구적인 명령을 제공하며, 플래시 ROM에 저장하고 지우고 다시 쓰기 가)
* 부팅 과정에서 하드웨어 초기화를 돕습니다.

ex ) AMI,  ,PHOENIX..

<img src="../../.gitbook/assets/file.drawing (4).svg" alt="" class="gitbook-drawing">

### Update

바이오스를 업데이트 하는 법

* 플래시(소프트웨어 방식)
* &#x20;EEPROM으로 프로그래밍(하드웨어 방식)

{% hint style="info" %}
MBR ? \
부팅 순위 1순위 저장 장치 드라이브의 최상위 파티션 첫 번째 섹터(최대 2TB)의 첫 번째 바이트부터 512 마지막 바이트까지 부팅 정보를 써 놓고, 바이오스가 이를 읽어들여 부팅하게 만들었다. \
이를 MBR(Master Boot Record)라 불렀다. \
부팅 정보는 부트 코드 + 파티션 테이블 정보 + 서명으로 이루어져 있다.
{% endhint %}

### Limit

* 최대 2TB의 메모리 장치 가능
* 16Bit Proccessor만 실

{% hint style="info" %}
Why?\
컴퓨터가 발달해온 역사와 관련이 있습니다.\
PC에 있던 프로세서는 8088 프로세서였으며, 8비트 외부 데이터 버스가 있는 8086 프로세서의 변형입니다(사용 가능한 모든 하드웨어 설계가 8을 중심으로 했을 때 16비트 프로세서로 작업하기 쉽도록 했습니다. 비트 처리).\
&#x20;8086/8088은 16비트 프로세서였지만 기존 소프트웨어를 새 칩에 쉽게 이식할 수 있도록 이전 8080 8비트 프로세서 제품군과 어셈블리 언어로 호환되도록 설계되었습니다. \
제품군의 각 칩은 이전 제품과 어느 정도 호환되도록 열심히 노력합니다.
{% endhint %}

## UEFI

* UEFI는 통합 확장 가능한 펌웨어 인터페이스를 나타냅니다.
* uefi는 CPU 부팅을 돕는 저수준 소프트웨어
* EFI 서비스 파티션이라는 유효한 부팅 볼륨 목록을 유지 관리합니다.

{% hint style="info" %}
GPT ?

UEFI에서 파티션 테이블의 레이아웃에 대한 표준입니다.\
헤드 섹터(MBR의 섹터) 대신 논리적 블록 주소 지정(LBA)를 사용합니다\
boot loader가 없습니다
{% endhint %}

<img src="../../.gitbook/assets/file.drawing (1).svg" alt="" class="gitbook-drawing">