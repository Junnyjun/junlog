# 내용 협상& 트랜스 코딩

#### 내용 협상 기법

서버에 있는 페이지들 중 어떤 것이 클라이언트에게 맞는지 판단하는 세 가지 다른 방법이 있다.&#x20;

```
클라이언트 주도 -> 클라이언트가 요청을 보내면, 서버는 클라이언트에게 선택지를 보내주고 클라이언트가 선택
서버 주도 협상 -> 서버가 클라이언트의 요청 헤더를 검증해서 어떤 버전을 제공할지 결정한다
투명한 협상 -> 투명한 중간 장치(주로 프락시캐시)가 서버를 대신하여 협상한다
```

#### 클라이언트 주도 협상

여러 가지 버전에 대한 링크와 각각에 대한 설명이 담긴 HTML 페이지를 돌려주거나, 300 Multiple Choices 응답 코드로 HTTP/1.1 응답을 돌려주는 것이다.&#x20;

첫번째 메서드의 결과로, 클라이언트 브라우저는 이러한 응답을 받아 링크와 함께 페이지를 보여주거나 혹은 사용자가 결정을 하도록 하기 위해 대화창을 띄울 것이다.

이 경우, 결정은 브라우저 사용자에 의해 수동으로 클라이언트 쪽에서 행해지기 때문에 증가된 대기시간과 페이지당 여러 번이 필요하다는 단점이 있다.

### 서버 주도 협상&#x20;

#### 내용 협상 헤더

어떤 두 클라이언트가 자신이 이해할 수 없는 언어를 지정한 Accept-Language 헤더 정보를 보낸다면, 서버는 www.joes-hardware.com 의 어떤 사본을 각 클라이언트에게 돌려줘야 할 지 판단할 수 있을 것이다.&#x20;

#### 내용 협상 헤더의 품질값

HTTP 프로토콜은 클라이언트가 각 선호의 카테고리마다 여러 선택 가능한 항목을 선호도와 함께 나열할 수 있도록 품질값을 정의하였다.

```xml
Accept-Language: en;q=0.5, fr;q=0.0, nl;q=1.0
```

q값은 0.0부터 1.0까지 가질 수 있다. 위의 헤더는 클라이언트가 네덜란드어로 된 문서를 받기를 원하고 있으나 영어로 된 문서라도 받아들일것을 의미하고 있다.

#### 그 외의 헤더들에 의해 결정

서버는 또한 User-Agent와 같은 클라이언트의 다른 요청 헤더들을 이용해 알맞은 요청을 만들어내려고 시도할 수 있다.캐시는 반드시 캐시된 문서의 올바른 최신버전을 제공해주려 하기 때문에 HTTP 프로토콜은 서버가 응답에 넣어 보낼 수 있는 Vary 헤더를 정의한다.&#x20;

#### 아파치의 내용 협상

* 웹 사이트 디렉터리에서, 배리언트(variant)를 갖는 웹 사이트의 각 URI를 위한 type-map파일을 만든다. 그 type-map파일은 모든 배리언트와 그들 각각에 대응하는 내용 협상 헤더들을 나열한다.
* 아파치가 그 디렉터리에 대해 자동으로 type-map 파일을 생성하도록 하는 MultiViews 지시어를 켠다

#### 투명 협상

* 투명 협상은 클라이언트 입장에서 협상하는 중개자 프락시를 둠으로써 클라이언트와의 메시지 교환을 최소화하는 동시에 서버 주도 협상으로 인한 부하를 서버에서 제고한다.
* 캐시 프락시는 단일한 URL을 통해 접근할 수 있는 문서의 여러 다른 사본을 저장할 수 있다. 만약 서버가 그들의 캐시에 대한 의사결정 프로세스를 캐시에게 알려 주었다면, 캐시는 서버의 입장에서 클라이언트와 협상할 수 있다.
* 캐시는 올바른 응답을 클라이언트에게 돌려주기 위해 내용 협상 헤더를 사용한다
* 서버가 특정 요청 헤더에 따라 다르게 응답한다면, 캐시된 응답을 돌려보내기 전에 캐시는 반드시 일반적인 내용 협상 헤더들 뿐 아니라 이들 요청 헤더들도 맞춰보아야 한다.
* 캐시는 각 배리언트마다 알맞은 문서 버전을 저장해야 한다. 캐시가 검색을 할 때, 먼저 내용 협상 헤더로 적절한 콘텐츠를 맞춰보고, 다음에 요청의 배리언트를 캐시된 배리언트와 맞춰본다.&#x20;

#### 트랜스 코딩

* 포맷 변환 - 데이터를 클라이언트가 볼 수 있도록 한 포맷에서 다른 포맷으로 변환한다
* 정보 합성 - 문서에서 정보의 요점을 추출한다
* 콘텐츠 주입 - 오히려 양을 늘리는 또 다른 종류의 변환인 내용 주입 트랜스코딩이다.