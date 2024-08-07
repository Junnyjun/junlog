# 보조 기억 장치

#### 다양한 보조기억장치

**하드 디스크**

하드 디스크 (HDD, Hard Disk Driver)

* 자기적인 방식으로 데이터를 저장하는 보조기억장치
* 자기 디스크(magnetic disk)의 일종으로 볼 수 있음.

하드 디스크 특징

* 동그란 원판에 데이터를 저장하고, 원판을 회전시켜 뾰족한 리더기를 통해 데이터를 읽는다.
* CD나 LP와 비슷하게 동작한다.

하드 디스크 구조

* 플래터(platter): 하드 디스크에서 데이터가 실질적으로 저장되는 곳. 원판 형태이다.
  * 자기 물질로 덮여 있으며, 내부에 수많은 N극과 S극이 존재한다.
  * 여기서 N극과 S극은 비트 (0과 1) 역할을 한다.
* 스핀들(spinddle): 플래터를 회전시키는 구성 요소

플래터 데이터 저장 원리

플래터는 트랙과 섹터라는 단위로 데이터를 저장함

* 트랙(track): 플래터를 여러 동심원으로 나눌 때 그 중 하나의 원
  * 운동장 달리기 트랙에서 한 라인을 생각
* 섹터(sector): 트랙을 여러 조각으로 나눌 때 한 조각을 의미함
  * 하드 디스크의 가장 작은 전송 단위
  * 하나의 섹터는 일반적으로 512 바이트이나 하드 디스크마다 차이가 있음
  * 블록(block): 하나 이상의 섹터를 묶어서 블록이라 표현함
* 실린더(cylinder): 여러 겹 플래터 상에서 같은 트랙이 위치한 곳을 모아서 연결한 논리적 단위
  * 각 플래터의 트랙들이 모여 원통 모양이 된다.
  * 연속된 정보들은 보통 한 실린더에 기록된다.
    1. ex> 두개의 플래터를 가진 하드 디스크가 네 개 섹터에 데이터를 저장할 때: 첫 번째 플래터의 윗면과 뒷면, 두 번째 플래터의 윗면과 뒷면에 데이터를 저장함
    2. 이를 통해 디스크 암을 움직이지 않고 연속된 정보에 바로 접근할 수 있다.

저장된 데이터에 접근하는 과정

하드 디스크가 데이터에 접근하는 시간은 탐색 시간, 회전 지연, 전송 시간으로 나뉜다.

* 탐색 시간(search time): 접근할 데이터가 저장된 트랙까지 헤드를 이동시키는 시간
* 회전 지연(rotation latency): 헤드가 있는 곳으로 플래터를 회전 시키는 시간
* 전송 시간(transfer time): 하드 디스크와 컴퓨터 사이 데이터를 전송하는 시간

접근 시간과 성능

<figure><img src="https://blog.kakaocdn.net/dn/CWq8y/btrRvaEJJ5K/e7K1q89htSjxla3SjRcNj0/img.png" alt=""><figcaption></figcaption></figure>

* 탐색 시간, 회전 시간, 전송 시간은 성능에 큰 영향을 끼치게 된다.
* 하드 디스크에서 데이터를 이용하는 시간은 매우 크다.

성능 향상 방법

* 플래터를 빨리 돌려 1분당 회전 수(RPM)를 높인다.
* 참조 지역성을 고려하여 데이터를 위치시켜 플래터와 헤드의 이동 시간을 줄인다.

단일 헤드 디스크와 다중 헤드 디스크

* 단일 헤드 디스크(single-head disk)
  * 플래터의 한 면당 헤드가 하나씩 달려 있는 하드 디스크
  * 헤드를 데이터가 있는 곳 까지 이동 시켜야 하기 때문에 이동 헤드 디스크(movable-head disk)라고 부름
* 다중 헤드 디스크(multiple-head disk)
  * 헤드가 트랙별로 여러개 달려 있는 하드 디스크
  * 헤드를 움직일 필요가 없기 때문에 고정 헤드 디스크(fixed-head disk)라고 부름

***

**플래시 메모리**

최근 플래시 메모리 기반의 보조 기억 장치를 많이 사용한다.

* USB 메모리
* SD 카드
* SSD

플래시 메모리(flash memory)

* 전기적으로 데이터를 읽고 쓸 수 있는 반도체 기반 저장 장치
* 보조기억장치 뿐만 아니라 다양한 곳에 활용되고 있음
  * ex> ROM (주기억장치)

플래시 메모리의 종류

* NAND 플래시 메모리
  * NAND 연산을 수행하는 회로(NAND 게이트) 기반으로 만들어진 메모리
  * 대용량 저장 장치로 많이 사용하며 일반적으로 플래시 메모리로 통칭됨
* NOR 플래시 메모리
  * NOR 연산을 수행하는 회로(NOR 게이트) 기반으로 만들어진 메모리

**플래시 메모리 저장 단위와 종류**

셀(cell)

* 플래시 메모리에서 데이터를 저장하는 가장 작은 단위
* 셀 하나에 몇 비트를 저장할 수 있는지에 따라 플래시 메모리가 나뉜다.
  * 1비트: SLC
  * 2비트: MLC
  * 3비트: TLC
  * 4비트: QLC
  * ...
* 셀에 저장하는 비트량은 수명, 속도, 가격에 큰 영향을 끼친다.
* 셀에 일정 횟수 이상 데이터를 쓰고 지우면 더 이상 데이터를 저장할 수 없다.

SLC(Single Level Cell) 타입

* 한 셀에 1비트 저장 가능. 즉, 0/1 2개 정보를 표현할 수 있음
* 장점
  * MLC나 TLC보다 비트의 빠른 입출력이 가능함
  * MLC나 TLC보다 수명이 길어 더 많은 데이터의 입력/삭제가 가능함
* 단점
  * 용량 대비 가격이 높음
* 기업에서 데이터의 입력/삭제가 많고, 고성능 빠른 저장 장치가 필요할 때 SLC를 주로 사용

MLC (Multiple Level Cell) 타입

* 한 셀에 2비트 저장 가능. 즉, 4개 정보를 표현할 수 있음
* 장점
  * SLC 타입보다 용량대비 가격이 저렴함
  * 한 셀에 두 비트를 저장하기 때문에 SLC보다 대용량화 하기 유리함
* 단점
  * 속도와 수명은 SLC에 비해 떨어짐
* TLC와 함께 시중에서 가장 많이 사용되는 플래시 메모리 저장 타입임

TLC (Triple-Level Cell) 타입

* 한 셀에 3비트 저장 가능. 즉, 8개 정보를 표현할 수 있음
* 장점
  * SLC, MLC보다 가격이 저렴함
  * SLC, MLC보다 대용량화 하기 유리함
* 단점
  * SLC, MLC보다 수명과 속도가 떨어짐

| 구분       | SLC                          | MLC             | TLC               |
| -------- | ---------------------------- | --------------- | ----------------- |
| 셀당 bit   | 1bit                         | 2bit            | 3bit              |
| 수명       | 길다                           | 보통              | 짧다                |
| 읽기/쓰기 속도 | 빠르다                          | 보통              | 느리다               |
| 용량 대비 가격 | 높다                           | 보통              | 낮다                |
| 사용 목적    | 쓰기/삭제가 자주 반복하며, 높은 성능을 원할 경우 | SLC와 TLC의 중간 목적 | 저가의 대용량 장치를 원할 경우 |

셀보다 큰 단위

* 페이지(page): 셀들이 모인 단위
* 블록(block): 페이지가 모인 단위
* 플레인(plane): 블록이 모인 단위
* 다이(die): 플레인이 모인 단위

플래시 메모리의 읽기와 쓰기, 삭제

* 플래시 메모리의 읽기/쓰기 단위와 삭제 단위는 다르다.
* 읽기와 쓰기: 페이지 단위로 이루어짐
* 삭제: 블록 단위로 이루어짐

플래시 메모리 페이지의 상태

페이지는 Free, Valid, Invalid 3가지 상태를 가질 수 있다.

* Free 상태: 어떤 데이터도 저장하지 않아 새로운 데이터를 저장할 수 있는 상태
* Valid 상태: 이미 유효한 데이터를 저장하고 있는 상태
* Invalid 상태: 유효하지 않은 쓰레기 값들을 저장하고 있는 상태

플래시 메모리는 덮어 쓰기가 불가능 하기 떄문에 Valid/Invalid 상태의 페이지에는 새 데이터를 저장할 수 없다.

<figure><img src="https://blog.kakaocdn.net/dn/HSX1S/btrRzgKlCYS/4EU26gKypAZsMihZ3gzvh0/img.png" alt=""><figcaption></figcaption></figure>

* 기존 데이터 A를 수정한 A'을 블록에 저장한다면 A와 A'은 둘 다 남아 있다.
* 여기서 Free 상태인 빈 페이지에 A'가 저장돼 Valid 상태가 된다.
* 기존 A는 쓰레기 값이 되어 A를 저장한 페이지는 Invalid가 된다.
* 쓰레기 값을 저장한 공간들은 사용하지 않는데도 용량을 차지해 낭비가 발생한다.

가비지 컬렉션(garbage collection)

<figure><img src="https://blog.kakaocdn.net/dn/6jdlt/btrRymSnV4D/ft3FgQrNTDW1PSXQ0lYbx1/img.png" alt=""><figcaption></figcaption></figure>

* 쓰레기 값을 정리하는 기능
* 유효한 페이지들을 새로운 블록에 복사하고, 기존 블록은 삭제한다.

#### 07-2. RAID의 정의와 종류

RAID(Redundant Array of Independent Disks)

* 주로 하드 디스크와 SSD에 사용하는 기술
* 데이터의 안정성과 높은 성능을 위해서 여러 물리적 보조기억장치를 하나처럼 사용하는 기술
* 여기서 물리적 보조기억장치 여러 개를 하나의 논리적 보조기억장치처럼 취급한다.

RAID의 종류

* RAID 레벨: RAID를 구성하는 여러가지 방법을 의미
  * RAID 0\~6
  * RAID 10 -> RAID 1과 0 기술의 결합
  * RAID 50 -> RAID 5와 0 기술의 결합

RAID 0

* 보조기억장치 여러 개에 데이터를 단순히 나누어 저장하는 방식
* 즉, 저장하는 데이터를 모든 하드 디스크에 분산해서 저장하게 된다.
* stripe(스트라입): 분산해 저장한 데이터
* striping(스트라이핑): 분산하여 저장하는 행위

RAID 0 장점

* 데이터를 동시에 읽거나 쓸 수 있게 된다.
  * 스트라이핑을 통해 데이터를 저장하거나 읽는 속도가 빨라진다.
  * 1TB 저장 장치 4개를 쓰면 4TB 저장 장치 하나를 쓸 때보다 이론상 속도가 4배 빨라진다.

RAID 0 단점

* 저장된 정보가 안전하지 않다.
  * 디스크 중 하나에 문제가 발생하면 다른 디스크의 정보를 온전히 읽는데 문제가 발생한다.

RAID 1

* mirroring(미러링)을 통해 저장장치를 원본과 백업본(복사본) 용도로 나눈다.
* 즉, 복사본으로 문제가 발생한 원본 디스크의 데이터 복구가 가능하다.

RAID 1 장점

* 복구 방식이 매우 간단하다.

RAID 1 단점

* 하드 디스크 개수가 한정됐을 때 사용 가능한 총 용량이 감소한다.
  * 복사본으로 사용하는 디스크 용량만큼 총 용량이 감소한다.
* 많은 양의 하드 디스크가 필요해 비용이 증가한다.

RAID 4

* 오류 검출 및 데이터 복구에 사용되는 정보인 패리티 비트를 저장하는 디스크를 따로 구성하는 방식
* 패리티 비트(parity bit): 오류를 검출하고 데이터를 복구하기 위한 정보
* 패리티를 저장한 디스크로 다른 장치의 오류를 검출하고, 오류가 있을 시 복구한다.
* 오류 복구는 패리티 계산법을 통해 이루어진다.

RAID 4 장점

* RAID 1보다 적은 하드 디스크로도 데이터를 안전하게 보관할 수 있다.

RAID 4 단점

* 패리티를 저장하는 장치에 병목 현상이 발생한다.
  * 여러 디스크에 쓰이는 데이터에 대한 패리티가 한 장치에 모두 쓰이기 때문

RAID 5

* 패리티 정보를 하나의 디스크가 아닌 여러 디스크에 분산해 저장하는 방식

RAID 5 장점

* RAID 4에서 발생하는 병목 현상 문제를 해결함

RAID 5 단점

* 특정 데이터에 대한 패리티가 하나만 쓰이기 때문에 정보 안정성이 RAID 6보다는 떨어짐

RAID 6

* 오류를 검출하고 복구할 수 있는 패리티를 두 개 저장하는 방식
* 저장되는 두 패리티는 데이터를 복구하는 방법이 다르기 때문에 한 패리티 방법으로 복구할 수 없을 때도 다른 방법으로 시도가 가능함

RAID 6 장점

* RAID 4나 5보다 데이터를 더 안전하게 보관할 수 있음

RAID 6 단점

* RAID 5보다 느림
  * 한 데이터에 대한 패리티를 두 개 저장해야 하기 때문

Nested RAID

* 여러 RAID 레벨을 혼합한 방식
* RAID 10: RAID 1 + RAID 0
* RAID 50: RAID 5 + RAID 0

***

**RAID 사용**

* 각 RAID 레벨별 장단점이 있어 최적의 RAID는 없다.
  * 상황마다 무엇을 최우선으로 하는지에 따라 최적 RAID는 다르다.
* 따라서 RAID 레벨별 개념과 특성을 아는 것이 중요하다.
