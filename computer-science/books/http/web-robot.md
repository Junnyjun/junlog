# Web Robot

웹 크롤러는, 먼저 웹페이지를 한 개 가져오고, 그 다음 그 페이지가 가리키는 모든 웹페이지를 가져오고, 다시 그 페이지들이 가리키는 모든 웹페이지들을 가져오는 이러한 일을 재귀적으로 반복하는 방식으로 웹을 순화하는 로봇이다

## 크롤러와 크롤링 <a href="#91" id="91"></a>

웹 크롤러는 웹 페이지를 가져오고, 그 페이지들이 가리키는 모든 웹페이지들을 가져오는 일을 재귀적으로 반복하는 방식으로 웹을 순회하는 로봇이다.

```
웹 링크를 재귀적으로 따라가는 로봇 : 크롤러 or 스파이더
HTML 하이퍼링크들로 만들어진 웹을 따라 기어다니기 때문
```

검색엔진은 크롤러를 사용한다. 검색엔진이 크롤링한 문서들이 사용자가 검색 가능한 데이터베이스로 만들어진다.

### 루트 집합 <a href="#911" id="911"></a>

크롤러를 풀어놓기 전에 출발지점을 주어야한다.\
루트 집합(root set) : 크롤러가 방문을 시작하는 URL들의 초기 집합

#### 링크 추출과 상대 링크 정상황 <a href="#912" id="912"></a>

크롤러는 웹을 돌아다니면서 HTML 문서를 검색하고, 검색한 각 페이지 안에 들어있는 URL링크를 파싱해 크롤링할 페이지들의 목록에 추가해야한다.

#### 루프와 중복 <a href="#913" id="913"></a>

```
- 순환은 크롤러를 루프에 빠뜨려서 크롤러가 네트워크 내역폭을 모두 차지하고, 어떤 페이지도 가져올 수 없게 되어버릴 수 있다.
- 크롤러가 같은 페이지를 반복해서 가져오면 웹 서버에 부담이 된다. 크롤러의 네트워크 접근 속도가 충분히 빠르다면, 웹 사이트를 압박하여 어떤 실제 사용자도 사이트에 접근할 수 없도록 막아버리게 될 수도 있다.
- 크롤러는 많은 수에 중복된 페이지들을 가져오게 돼서 크롤러의 애플리케이션이 자신을 쓸모없게 만드는 중복된 컨텐츠로 넘쳐나게 될 수도 있다.
```

### 빵 부스러기의 흔적 <a href="#915" id="915"></a>

수억개의 URL은 많은 공간을 차지한다. \
만약 평균 URL이 40바이트 길이이고, 웹 로봇이 5억개의 URL을 크롤링 했다면, 검색 데이터 구조는 이 URL들을 유지하기 위해 20GB 이상의 메모리를 요구할 것이다.

#### 트리와 해시 테이블 <a href="#undefined" id="undefined"></a>

복잡한 로봇이라면 방문한 URL을 추적하기 위해 검색 트리나 해시 테이블을 사용했을 수도 있다.

#### 느슨한 존재 비트맵 <a href="#undefined" id="undefined"></a>

공간 사용을 최소하하기 위해, 존재 비트 배열(presence bit array)과 같은 느슨한 자료구조를 사용한다.

```
각 URL이 해시 함수에 의해 고정된 크기의 숫자로 변환됨.
배열 안에 대응하는 존재 비트를 갖게됨
URL이 크롤링 되었을 때, 해당하는 존재 비트가 만들어지고, 존재 비트가 이미 존재한다면, 크롤러는 그 URL을 이미 크롤링되었다고 간주함.
```

#### 체크포인트 <a href="#undefined" id="undefined"></a>

로봇 프로그램 중단에 대비해 방문한 URL목록이 디스크에 저장되었는지 확인함

#### 파티셔닝 <a href="#undefined" id="undefined"></a>

한 대의 컴퓨터에서 하나의 로봇이 크롤링을 완수하는 것이 불가능

그래서 대규모 웹들은 각각이 여러 컴퓨터 로봇이 동시에 일하고 있는 농장(fram)을 이용한다. 각 로봇엔 URL들의 특정 한 부분이 할당되어 그에대한 책임을 진다.

#### 별칭(alias)과 로봇 순환 <a href="#916-alias" id="916-alias"></a>

올바른 자료구조에 방문 처리를 하더라도 URL이 별칭을 가질 수 있기 때문에 어떤 페이지를 이전에 방문했는지 알기 쉽지 않을 때도 있다.

#### 같은 문서를 가리키는 다른 URL들 <a href="#url" id="url"></a>

URL을 표준 형식으로 정규화해서 다른 URL과 같은 리소스를 가리키고 있음이 확실한 것들을 미리 제거하려고 시도한다.

```
포트 번호가 명시되지 않았다면, 호스트 명에 :80 을 추가함.
모든 %xx 이스케이핑된 문자들을 대응되는 문자로 변환한다.
태그들을 제거한다
```

## 로봇의 HTTP <a href="#92-http" id="92-http"></a>

로봇들은 다른 클라이언트 프로그램과 다르지 않음. 웹로봇도 HTTP요청 헤더를 사용해야 한다.

#### 요청 헤더 식별하기 <a href="#921" id="921"></a>

웹로봇은 신원 식별 헤더를 전송한다.\
잘못된 크롤러의 소유자를 찾아낼 때와 서버에게 로봇이 어떤 종류의 콘텐츠를 다룰 수 있는지에 대한 약간의 정보를 줄 때 유용하기 때문

#### 가상 호스팅 <a href="#922" id="922"></a>

로봇 구현자들은 Host 헤더를 지원할 필요가 있다. 요청에 Host 헤더를 포함하지 않으면 로봇이 어떤 URL에 대해 잘못된 콘텐츠를 찾게 만든다.

조건부 요청 : 캐시 사본이 유효한지 검사할 때 사용하는 조건부 GET을 사용해서 변경되었을 때만 콘텐츠를 가져오도록 하낟.

#### 부적절하게 동작하는 로봇들 <a href="#93" id="93"></a>

`폭주하는 로봇` : 로봇은 웹 서핑을 하는 사람보다 빠르게 HTTP요청을 만들 수 있고, 빠른 네트워크 연결을 갖춘 컴퓨터 위에서 동작한다. 그러기 때문에 로봇이 논리적인 에러를 갖고 있거나 순환에 빠졌다면 웹 서버에 극심한 부하를 안겨줄 수 있다. 그래서 로봇 저자들은 폭주 방지를 위한 보호 장치를 반드시 신경써야한다.

`오래된 URL` : 로봇은 url의 목록을 방문하는데 이 목록이 오래돼어 콘텐츠가 바뀌었다면 로봇은 존재하지 않는 url에 대한 요청을 많이 보낼 수 있다.

`길고 잘못된 url` : 순환이나 프로그래밍상 오류로 로봇은 웹 사이트에게 크고 의미없는 url을 요청할 수 있다. 만약 url이 충분히 길다면 웹 서버의 처리능력에 영향을 주고, 로그를 어지럽히고 장애를 발생시킬 수 있다.

### 검색엔진 <a href="#96" id="96"></a>

웹 로봇이 제일 많이 사용되는 곳이 검색엔진

#### 검색엔진의 아키텍쳐 <a href="#962" id="962"></a>

오늘날 검색엔진들은 ‘풀 텍스트 색인'이라고 하는 로컬 데이터베이스를 생성한다.

* 검색 엔진 동작
  1. 검색엔진 크롤러가 웹페이지들을 수집하여 풀 텍스트 색인에 추가한다.
  2. 구글같은 웹 검색 게이트웨이를 통해 풀 텍스트 색인에 대한 질의를 보낸다.

#### 풀 텍스트 색인 <a href="#963" id="963"></a>

단어 하나를 입력받아 그 단어를 포함하고 있는 문서를 즉각 알려줄 수 있는 데이터베이스

### 질의 보내기 <a href="#964" id="964"></a>

사용자가 질의를 웹 검색엔진 게이트웨이로 보내는 방법

```
html 폼을 사용자가 채워넣음
브라우저가 그 폼을 http 요청을 이용해서 게이트웨이에 보냄
게이트웨이 프로그램은 검색 질의를 추출하고 웹 ui 질의를 풀 텍스트 색인을 검색할 때 사용되는 표현식으로 변환한다
```

