# 가상 메모리

## 가상 메모리 <a href="#ch14" id="ch14"></a>

### 14.1 연속 메모리 할당 <a href="#141" id="141"></a>

연속 메모리 할당은 프로세스 내에 연속적인 메모리 공간을 할당하는 방식이라고 한다.

#### 스와핑 <a href="#undefined" id="undefined"></a>

현재 사용되지 않는 프로세스들을 보조기억장치의 스왑 영역으로 옮기고, 새로 생긴 빈 공간에 실행할 프로세스를 적재하는 것을 말한다.

스와핑을 하게 되면, 프로세스들이 요구하는 메모리 공간의 크기가 실제 메모리 크기보다 크더라도 동시에 실행할 수 있다.

#### 메모리 할당 <a href="#undefined" id="undefined"></a>

프로세스가 실행되기 위해서는 메모리 내의 빈 공간에 적재되어야 한다. 최초 적합, 최적 적합, 최악 적합의 세 가지 적재 방식이 있다.

최초 적합(first-fit)은 운영체제가 메모리 내의 빈 공간을 탐색할 때, 가장 먼저 발견되는 공간에 프로세스를 적재하는 방식이다. 검색을 최소화할 수 있고 할당이 빠르다.\
최적 적합(best-fit)은 운영체제가 매모리 내의 빈 공간을 모두 탐색한 후, 적재 가능한 가장 작은 공간에 할당하는 방식이다\
최악 적합(worst-fit)은 운영체제가 매모리 내의 빈 공간을 모두 탐색한 후, 적재 가능한 가장 큰 공간에 할당하는 방식이다.

#### 외부 단편화 <a href="#undefined" id="undefined"></a>

프로세스를 메모리에 연속적으로 배치하는 연속 메모리 할당은 메모리를 효율적으로 사용하는 방법이 아니다.\
외부 단편화(external fragmentation)라는 메모리 내에 여러 개의 작은 빈 공간이 존재하는 상황이 발생하기 때문이다.

외부 단편화는 빈 공간보다 큰 프로세스를 적재하기 어려운 상황을 초래하고 메모리의 효율성을 떨어뜨린다.

메모리 압축을 통하여 외부 단편화를 해결할 수 있다. 메모리 압축은 프로세스들을 메모리의 시작 주소부터 차례대로 배치하는 방식이다.

#### 확인 문제 <a href="#undefined" id="undefined"></a>

> 1. 메모리 할당 방식에 대한 설명으로 올바른 것을 다음 보기에서 찾아 써 보세요.\
>    최초 적합: 최초로 발견한 적재 가능한 빈 공간에 프로세스를 배치하는 방식\
>    최악 적합: 프로세스가 적재될 수 있는 가장 큰 공간에 프로세스를 배치하는 방식\
>    최적 적합: 프로세스가 적재될 수 있는 가장 작은 공간에 프로세스를 배치하는 방식

### 14.2 페이징을 통한 가상 메모리 관리 <a href="#142" id="142"></a>

페이징은 가상 메모리 관리 기법 중 하나이다.

#### 페이징이란 <a href="#undefined" id="undefined"></a>

프로세스를 일정한 크기로 나누고, 불연속적으로 메모리에 적재하는 방식이다. 프로세스의 논리 주소 공간을 페이지라는 일정 단위로 자르고, 메모리의 물리 주소 공간을 프레임이라는\
일정한 단위로 나눈 뒤 페이지를 프레임에 매핑하는 방식이다.

페이징에서의 스왑은 페이지 인, 페이지 아웃이라고 한다.

#### 페이지 테이블 <a href="#undefined" id="undefined"></a>

프로세스를 이루는 페이지가 어떤 프레임에 적재되어 있는지를 나타내는 테이블이다. 페이지 번호와 프레임 번호를 매핑하는 테이블이다.

하나의 페이지 크기보다 작은 메모리 공간을 요구하는 프로세스로 인해 내부 단편화가 발생할 수 있다. 프로세스마다 페이지 테이블이 있고, 각 페이지 테이블은 CPU 내의 프로세스\
테이블 베이스 레지스터(PTBR)에 저장된다.

페이지 테이블을 메모리에 두게 되면, 메모리 접근 시간이 두 배로 늘어나게 된다. 이를 위해서 TLB(Translation Lookaside Buffer)를 사용한다.

TLB는 페이지 테이블의 캐시로, 페이지 테이블의 일부 내용을 저장한다. TLB에는 최근에 사용된 페이지 위주로 저장하게 된다.

CPU가 접근하려는 논리 주소가 TLB에 있을 경우 이를 TLB 히트, 없을 경우 TLB 미스라고 한다.

#### 페이징에서의 주소 변환 <a href="#undefined" id="undefined"></a>

특정 주소에 접근하기 위해서는 어떤 페이지 혹은 프레임에 접근하고 싶은지, 접근하려는 주소가 그 페이지 혹은 프레임에서 얼마나 떨어져 있는지를 알아야 한다.

페이징 시스템에서의 논리 주소는 페이지 번호(page number)와 변위(offset)로 구성된다. 이는 페이지 테이블을 통해 물리 주소(프레임 번호, 변위)로 변환된다.

#### 페이지 테이블 엔트리 <a href="#undefined" id="undefined"></a>

페이지 테이블 엔트리는 페이지 테이블의 각 행을 의미한다. 페이지 번호와 프레임 번호 외의 정보도 포함한다.

유효 비트: 현재 해당 페이지에 접근 가능한지 여부를 알려준다. 유효비트가 0이면 해당 페이지는 메모리에 적재되어 있지 않다. 이를 페이지 폴트라고 한다.

보호 비트: 페이지 보호를 위한 비트이다. 보호 비트가 0이면 읽기 전용, 1이면 읽기/쓰기가 가능하다.

참조 비트: CPU가 해당 페이지에 접근했는지 여부를 알려준다. 참조 비트가 0이면 해당 페이지에 접근하지 않았다.

수정 비트: CPU가 해당 페이지에 쓰기 접근했는지 여부를 알려준다. 수정 비트가 0이면 해당 페이지에 쓰기 접근하지 않았다.

### 추가 강의 <a href="#undefined" id="undefined"></a>

#### 페이징의 이점 - 쓰기 시 복사 <a href="#undefined" id="undefined"></a>

프로세스는 기본적으로 자원을 공유하지 않는다. 이론적인 fork()는 부모 프로세스의 메모리를 그대로 복사하게 되고, 이는 프로세스 생성 시간 지연과 메모리 낭비등의 문제를\
낳는다.

쓰기 시 복사는 자식 프로세스가 부모 프로세스와 동일한 프레임을 가리키고 있다가, 쓰기 작업 수행시 해당 페이지를 별도의 공간으로 복제한다.

#### 계층적 페이징 <a href="#undefined" id="undefined"></a>

페이지 테이블을 페이징하여 여러 단계의 페이지를 두는 방식이다.

바깥 페이지 번호, 안쪽 페이지 번호, 변위 세 가지로 이루어진 논리 주소를 사용한다.

페이지 폴트가 발생했을 경우, 메모리 참조 횟수가 증가하여 성능 저하가 발생할 수 있다.

### 14.3 페이지 교체와 프레임 할당 <a href="#143" id="143"></a>

#### 요구 페이징 <a href="#undefined" id="undefined"></a>

요구 페이징(demand paging)은 요구되는 페이지만 적재하는 기법이다. 페이지 교체와 프레임 할당이 필요하다.

#### 페이지 교체 알고리즘 <a href="#undefined" id="undefined"></a>

좋은 페이지 교체 알고리즘은 일반적으로 페이지 폴트가 적은 알고리즘을 의미한다.

FIFO 페이지 교체 알고리즘(First In First Out)은 가장 오래 전에 적재된 페이지를 교체하는 알고리즘이다.

2차 기회 페이지 교체 알고리즘은 FIFO 알고리즘의 단점을 보완하기 위해 만들어졌다. 2차 기회 알고리즘은 참조 비트를 추가하여, 참조 비트가 0인 페이지를 교체한다.

최적 페이지 교체 알고리즘은 CPU에 의해 참조되는 횟수를 고려한 알고리즘이다. 메모리에 오래 남아야 할 페이지는 참조 횟수가 많을 것이고, 이를 고려하여 페이지를 교체한다.

LRU 페이지 교체 알고리즘(Least Recently Used)은 가장 오랫동안 참조되지 않은 페이지를 교체하는 알고리즘이다.

#### 스래싱과 프레임 할당 <a href="#undefined" id="undefined"></a>

페이지 폴트가 자주 발생하는 상황은 나쁜 페이지 교체 알고리즘을 사용해서만이 아닌 프로세스가 사용할 수 있는 프레임 자체가 적어서 일수도 있다.

프로세스가 실제 실행되는 시간보다 페이징에 더 많은 시간을 소요하여 성능이 저해되는 문제를 스래싱(thrashing)이라고 한다.

스래싱은 각 프로세스가 필요로 하는 최소한의 프레임 수를 파악하고, 프로세스들에게 적절한 프레임을 할당하는 것으로 해결할 수 있다.

프레임 할당 방식에 대해 알아보자. 정적 할당 방식은 다음과 같다.

균등 할당(equal allocation)은 각 프로세스에게 동일한 프레임 수를 할당하는 방식이다. 비합리적인 느낌이다.

비례 할당(proportional allocation)은 각 프로세스에게 프레임 수를 프로세스 크기에 비례하여 할당하는 방식이다.

동적 할당 방식은 다음과 같다.

작업 집합 모델(workset model)은 CPU가 특정 시간동안 주로 참조한 페이지 개수만큼만을 프레임으로 할당하는 방식이다.

프로세스가 참조한 페이지와 시간 간격을 통해 작업 집합을 구할 수 있다.

페이지 폴트 빈도 모델(page fault frequency model)은 페이지 폴트가 발생한 횟수를 기준으로 프레임을 할당하는 방식이다.

페이지 폴트율이 너무 높으면 그 프로세스는 너무 적은 프레임을 갖고 있다, 페이지 폴트율이 너무 낮으면 그 프로세스는 너무 많은 프레임을 갖고 있다의 가정에서 시작한다.

상한선과 하한선을 설정하여, 그 범위 내에서 프레임을 할당한다.