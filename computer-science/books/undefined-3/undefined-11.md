# 파일시스템

### 파일과 디렉터리 <a href="#151" id="151"></a>

파일 시스템(file system)은 파일과 디렉터리를 관리하는 운영체제 내의 프로그램이다.

#### 파일 <a href="#undefined" id="undefined"></a>

하드 디스크나 SSD와 같은 보조기억장치에 저장된 관련 정보의 집합을 의미한다.

파일을 이루는 정보에는 파일을 실행하기 위한 정보와 부가 정보가 있다. 확장자를 통해 파일의 유형을 알 수 있다.

#### 디렉터리 <a href="#undefined" id="undefined"></a>

요새는 거의 트리 구조 디렉터리를 사용한다. 트리 구조 디렉터리는 파일과 디렉터리를 계층적으로 관리한다.

최상위 디렉터리를 루트 디렉터리라고 한다. 자연스럽게, 디렉터리를 이용해 파일/디렉터리의 위치, 즉 경로(path)를 표현할 수 있다.

절대 경로: 루트 디렉터리부터 시작하는 경로\
상대 경로: 현재 디렉터리부터 시작하는 경로

디렉터리 엔트리(directory entry)는 디렉터리에 포함된 파일/디렉터리의 이름과 위치를 저장한다.

### 파일 시스템 <a href="#152" id="152"></a>

#### 파티셔닝과 포매팅 <a href="#undefined" id="undefined"></a>

파티셔닝(partitioning)은 저장 장치의 논리적인 영역을 구획하는 작업을 의미한다.

포매팅(formatting)은 파일 시스템을 설정하는 작업으로 어떤 방식으로 파일을 관리할지 결정, 새로운 데이터를 쓸 준비를 하는 작업이다.

#### 파일 할당 방법 <a href="#undefined" id="undefined"></a>

운영체제는 파일/디렉터리를 블록 단위로 읽고 쓴다.

보조기억장치에 할당하는 방법에는 크게 두 가지가 있다. 연속 할당과 불연속 할당이다.

연속할당(Contiguous allocation): 파일을 연속된 블록에 저장하는 방법, 디렉터리 엔트리에는 파일 이름, 첫 번째 블록 주소, 블록 단위 길이 명시가 들어간다.

외부 단편화를 야기한다는 단점이 있다.

불연속 할당에는 두가지가 있는데, 연결 할당과 색인 할당이 있다.

연결 할당(Linked allocation): 파일을 불연속된 블록에 저장하는 방법이다. 각 블록의 일부에 다음 블록의 주소를 저장한다.

반드시 첫 번째 블록부터 하나씩 차례대로 읽어야 하고, 하드웨어 고장이나 오류 발생 시 해당 블록 이후 블록은 접근이 불가능한 단점이 있다.

색인 할당(Indexed allocation): 파일의 모든 블록 주소를 색인 블록이라는 하나의 블록에 모아 관리하는 방식이다. 파일 내 임의의 위치에 접근하기 용이하다.

디렉터리 엔트리에는 파일이름과 색인 블록 주소를 저장한다.

#### 파일 시스템 살펴보기 <a href="#undefined" id="undefined"></a>

FAT 파일 시스템: 불연속 할당 기법의 연결 할당 기법을 이용한 파일 시스템이다. 파일 할당 테이블(FAT)이라는 특수한 블록에 파일의 블록 주소를 저장한다.

FAT 파일 시스템의 디렉터리 엔트리에는 파일 이름, 확장자, 속성, 예약 영역, 생성 시간, 마지막 접근 시간, 마지막 수정 시간, 첫 번째 블록 주소, 파일 크기 등이 들어간다.

유닉스 파일 시스템: 색인 할당 기법을 이용한 파일 시스템이다. 색인 블록(i-node)이라는 특수한 블록에 파일의 블록 주소를 저장한다.

유닉스 파일 시스템의 디렉터리 엔트리에는 i-node 번호와 파일 이름이 들어간다.

파일 속성 정보와 15개의 블록 주소를 저장할 수 있다. 블록 주소 중 12개에는 직접 블록 주소를 저장하고, 13번째에는 단일 간접 블록 주소를 저장하고, 14번째에는 이중 간접 블록 주소를 저장한다.

15번째에는 삼중 간접 블록 주소를 저장한다.
