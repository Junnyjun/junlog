---
description: 컴퓨터가 메모리를 일관되게 실행하는 방법에 대해 알아봅시다
---

# 가상 메모리

실제 물리 메모리에만 의존한다면 특정 프로그램은 실행되지 않을 수도 있다.

현재는 물리 메모리 크기와 프로세스의 메모리 위치를 신경 쓰지않고 프로그래밍 할 수 있도록 `가상메모리` 를 지원한다.



### 가상 메모리 ❔

가상 메모리의 프로세스는 0번지 부터 시작되는 메모리 가상 주소를 가진다.

가상 메모리의 크기는 시스템의물리메모리의 최대  크기로 맞춰진다.

<img src="../../../.gitbook/assets/file.drawing (8) (1).svg" alt="" class="gitbook-drawing">

하지만 가상 메모리는 프로세스의 메모리가 스왑영역에서 계속 스왑인&아웃되며 메모리를 훨씬 더 넉넉하게 사용할 수 있다.

메모리 관리자가 물리 메모리와 스왑 영역을 합쳐서 프로세스의 가상 주소를 실제 메모리 주소로 변환 시켜주는 작업은 동적 주소 변환이라 하는데, 프로세스가 메모리를 편하게 사용할 수 있도록 해준다.

#### 메모리 분할

가상 메모리 시스템에서는 운영체제를 제외한 나머지 메모리 영역을 일정한 크기로 나누어 프로세스에 할당 해준다. 이 방식은 가변 분할 방식(세그멘테이션) & 고정 분할 방식 (페이징) 으로 나뉘어 진다.

세그멘 테이션 방식은 외부 단편화의 문제가 있으며,

페이징 방식은 페이지 관리에 어려움이 있다.



메모리를 관리 할 때에는 가상 주소와 물리 주소를 일대일로 매핑하여 테이블로 관리한다.

## 페이징&#x20;

물리 주소 공간을 같은 크기로 나누어 사용하는 방식이며, 프로세스 입장에서는 항상 0번지 부터 시작되게 된다.

가상 주소의 분할된 각 영역은 페이지 라고 부르며, 각각 번호가 부여된다.

물리 메모리의 영역은 프레임 이라 부르며, 페이지와 같이 번호가 부여된다.

페이지와 프레임의 크기는 같다&#x20;

<img src="../../../.gitbook/assets/file.drawing (1) (3) (1).svg" alt="" class="gitbook-drawing">

가상 메모리 Page가 가르키는 값이 Invalid인 경우 스왑 영역에 있다는 의미이다.

### 주소 변환

{% hint style="info" %}
가상 주소는 VA = \<P,D> ( 가상주소 = <페이징, 거리> ) 로 나타낸다

물리 주소는 PA = \<F,D> (물리주소 = <프레임,거리>) 로 나타 낸다.
{% endhint %}

페이지 테이블을 이용하면 간단하게 가상 주소를 물리 주소로 변환 할 수 있다.

페이지 테이블의 페이지 번호로 프레임 번호를 찾아가면 된다.

#### 페이지 테이블 관리

모든 프로세스는 일부 페이지가 물리 메모리의 프레임에 올라와 있고, 프로세스 마다 페이징 테이블로 운영된다.

페이지 테이블은 메모리 관리자가 자주 사용하는 자료 구조이기 때문에 성능이 좋아야 한다.



시스템 내에는 여러 프로세스가 존재하며, 프로세스마다 페이지 테이블이 존재하기 때문에, 프로세스의 수에 따라 비례하여 커진다.

페이지 테이블의 크기를 관리하는 것이 핵심이다 그래서 각 페이지 주소의 시작점을 페이지 테이블 기준 레지스터에 보관한 뒤, 페이지 테이블은 스왑 영역에 보관한다.

#### 페이지 테이블 매핑

페이지 테이블을 메모리에서 관리하느냐, 스왑 영역에서 보관하느냐에 따라  가상 주소를 변환하는 방법의 차이가 생긴다.



* 직접 매핑

페이지 테이블 전체가 메모리에 올라가 있는 경우, 바로 변환이 가능하다

* 연관 메핑

페이지 테이블 전체를 스왑 영역에서 관리하는 경우, 스왑 영역의 페이지 테이블을 검색한 뒤 가져온다.

일부 페이지만 무작위로 메모리에 저장되어 있고 일부분을 변환 색인 버퍼, 연관 레지스터라 부른다.



변환 색인 버퍼는 페이지&프레임 번호를 가지고 있다.

주소를 찾기 위해 메모리에 접근할 때 변환 색인 버퍼를 찾는데, 이때 발견되면 TLB히트 반대는 TLB 미스라고 한다.

* 집합 연관 매핑

페이지 테이블을 집합으로 자른뒤 덩어리 단위로 메모리에 가져온다.

일정한 묶음을 디렉터리로 관리한다.



디렉터리 페이지 테이블의 시작 주소는 페이지 테이블 기준 레지스터가 가지고 있으며 프로세스 요청에 따라 디렉터리 테이블을 뒤진다.

* 역매핑

물리 메모리의 프레임 번호를 기준으로 테이블을 구성한다.

테이블은 하나만 존재하게 되며 테이블의 크기를 줄일 수 있는 장점이 있다.

### 세그먼 테이션

가변 분할 방식을 이용한 메모리 관리 기법으로써, 메모리를 프로세스 크기에 따라 나누어 사용한다.

세그먼 테이션 기법도 세그먼테이션 매핑 테이블을 이용하여 변환시킨다.

세그먼 테이션 기법은 각각 할당되는 메모리 크기가 다르기 때문에 크기 정보인 limit이 담겨있다.



세그먼 테이션 기법 또한 매핑 테이블을 사용한다

<img src="../../../.gitbook/assets/file.drawing (2) (2) (3).svg" alt="" class="gitbook-drawing">

세그먼테이션 기법의 limit은 메모리를 보호하는 역할을 한다.  limit보다 큰 메모리를 사용하게 된다면 `trap`이라는 오류가 발생한다



### 세그먼테이션-페이징 혼용

두 기법의 장점만을 취한 가상 메모리 관리 기법이다.

#### 메모리 접근 권한&#x20;

특정 번지에 저장된 데이터를 사용할 수 있는 권한이다.&#x20;

<table><thead><tr><th width="105">space</th><th>description</th></tr></thead><tbody><tr><td>Code</td><td>자기 자신 수정 불가 | Read Execute</td></tr><tr><td>Date</td><td>Read Writre Execute</td></tr></tbody></table>

읽기, 쓰기, 실행, 추가 권한으로 나누어 지게 되며 특정 권한은 다른 권한을 포함하고 있다 ( 추가 권한은 쓰기 권한을 포함, 쓰기  권한은 읽기 권한을 포함 )

가상메모리 - 세그먼테이션 테이블 - 페이징 테이블 으로 운영된다.

세그먼 테이션 테이블에는 권한 정보와, 그룹정보및 기타 권한 정보들이 되어있다.



#### 주소 변환

Process의 데이터를 요청하면 세그먼트 테이블에서 해당 주소와 페이지로 부터 거리를 구한다

세그먼트 번호에서 권한을 확인한다.

현재 위치한 프레임을 확인한다. 물리메모리 인지 스왑영역 인지 확인한다.

물리메모리의 프레임 위치부터 처음 구한 거리에 접근한다.

## 캐시 매핑

캐시는 메로리를 일정 크기로 나눈다. &#x20;

메모리의 페이지 수를 캐시의 페이지 수로 나눈것을 `블록(Block)` 이라고 한다.

캐시에는 블록번호가 명시되는데 이를 `태그(Tag)`라고 한다





