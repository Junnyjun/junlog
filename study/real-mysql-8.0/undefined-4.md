# 인덱스

인덱스는 책 끝에 색인으로 설명할 수 있다.

데이터 베이스가 모든 데이터를 가져오려면 시간이 오래 걸리는데,\
이러한 값들을 키&밸류 형태로 인덱스를 만들어 정렬하여 빠르게 찾아가는 방식을 뜻한다.

데이터를 저장하는데에 있어, 정렬 및 찾아보기(색인)을 만들어야 하여,\
데이터 변경(update, insert ... )작업의 처리속도를 희생하여 데이터를 빠르게 읽어오는 방법이다

인덱스는 크게 Primary key, Secondary key로 구분되어 진다.\
primary는 식별자를 기준으로 한 인덱스 방식이며 나머지는 secondary로 통칭한다

### B-tree

가장 범용적인 목적으로 사용되는 인덱스 알고리즘 이다.\
대개 B+ tree와 B- Tree를 사용하며 B- tree로 대부분의 인덱스를 사용할 수 있다.

<img src="../../.gitbook/assets/file.excalidraw (1).svg" alt="" class="gitbook-drawing">

B-tree는최상위에 루트 노드가 존재하며 브런치 노드, 리프 노드 순으로 관리된다\
인덱스의 리프노드는 항상 실제 레코드를 찾아가기 위한 주소값을 가지고 있게 된다.

인덱스에 새로운 키를 추가하기 위해선 저장될 적절한 키값의 위치를 찾아야한다.\
저장될 위치가 정해지면 레코드의 키값과 대상 레코드의 주소 정보를 B-tree의 리프노드에 저장한다.

인덱스의 존재하는 키를 제거하기 위해서는 기존에 마킹된 key를 제거하기만 하면 된다.\
여기서 수정된 데이터를 추가하면 존재하는 키의 수정이 된다.

#### 인덱스 단위

InnoDb의 storage engine은 디스크에 저장하는 가장 기본 단위를 page & block이라고 하며\
가장 작은 디스크IO 작업 단위이고 버퍼풀에서 데이터를 버퍼링 하는 기본 단위이기도 하다\
B-tree는 하나의 인덱스 페이지에 최대 585개의 자식노드를 가질 수 있다

인덱스에서의 선택도&기수성은 같은 의미로 사용되며, 인덱스 내부 유니크한 값의 수를 의미한다.\
유니크한 값의 수가 많을 수록 검색대상의 수가 줄어들어 성능이 향상된다

또한 옵티마이저는 전체 테이블 레코드의 20\~25%를 넘어서면 인덱스를 이용하지 않고 모두 직접 읽어 처리하는데, 기존 레코드를 읽을때 인덱스를 사용하면 4\~5배 가량의 비용이 발생하기 때문이다.

#### 데이터 조회

인덱스 레인지 스캔은 가장 대표적인 방식으로 인덱스의 범위가 결정 되었을때 사용하는 방식이다.\
루트노드에서부터 탐색을 시작하여 리프노드까지 데이터의 링크를 찾아 스캔한다.

인덱스 풀스캔은 인덱스를 처음부터 끝까지 모두 읽는 방식이다.\
쿼리의 조건절에 사용된 칼럼들이 인덱스의 첫 번째 칼럼이 아닌 경우 사용한다.

루스 인덱스 스캔은 듬성듬성하게 인덱스를 읽어오는 방식이다.\
중간에 필요하지 않은 인덱스 키값은 무시하고 넘어가는 방식이다.

인덱스 스킵 스캔은 꼭 필요한 부분만 읽어오는 방식으로 루스 인덱스 스캔과 비슷하지만\
필요한 방식을 선점하여 처리하는 차이가 있다.

#### Multi Index

두개이상의 칼럼으로 구성된 인덱스는 다중 칼럼 인덱스라고 한다.\
첫번째  칼럼은 정렬의 대상이 되고 두번째 칼럼은 이후 데이터의 정렬의 대상이된다.\
인덱스를 생성할 때 설정한 정렬 규칙에 따라 인덱스의 키값은 항상 오름&내림차순으로 정렬된다.

### R-tree&#x20;

공간인덱스로 2차원 데이터를 인덱싱하고 검색하는 목적의 인덱스이다.\
최근 GPS와 GIS에 기반을 둔 서비스가 늘어나면서 주로 사용하게 되었다.\
일반적으로 위도&경도(wgs84) 좌표 저장에 주로 사용한다.