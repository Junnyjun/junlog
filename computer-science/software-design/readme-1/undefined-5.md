# 컴포넌트

SOLID 원칙이 벽과 방에 벽돌을 배치하는 방법을 알려준다면, 컴포넌트 원칙은 빌딩에 방을 배치하는 방법을 설명해준다.

큰 빌딩과 마찬가지로 대규모 소프트웨어 시스템은 작은 컴포넌트들로 만들어진다.

&#x20;

> 컴포넌트는 배포 단위다.

컴포넌트는 시스템의 구성 요소로 배포할 수 있는 가장 작은 단위다.&#x20;

&#x20;

예시)

* 자바 - jar
* 루비 - gem
* 닷넷 - DLL

&#x20;

여러 컴포넌트를 서로 링크하여 실행 가능한 단일 파일로 생성할 수 있다.

&#x20;

또는 여러 컴포넌트를 서로 묶어서 .war 파일과 같은 단일 아카이브로 만들 수도 있다.

또는 컴포넌트 각각을 .jar나 .dll 같이 동적으로 로드할 수 있는 플러그인이나 .exe 파일로 만들어서 독립적으로 배포할 수도 있다.

&#x20;

> 잘 설계된 컴포넌트는 반드시 독립적으로 배포 가능한, 따라서 독립적으로 개발 가능한 능력을 갖춰야 한다.

### &#x20;

### 컴포넌트의 역사

&#x20;

* 개발 초창기\
  \
  라이브러리 함수의 소스 코드를 애플리케이션 코드에 직접 포함시켜 단일 프로그램으로 컴파일했다.\
  이 때 라이브러리는 바이너리가 아니라 소스 코드 형태로 유지되었다\
  하지만, 이 시대 장치는 느리고 메모리가 한정적이었기 때문에, 이러한 방식은 문제가 있었다.\
  \
  따라서 함수 라이브러리의 소스 코드를 애플리케이션 코드로부터 분리하고, 함수 라이브러리를 개별적으로 컴파일해, 컴파일된 바이너리를 메모리의 특정 위치에 로드했다.\
  \
  하지만, 이 방식도 초기에 할당된 메모리보다 애플리케이션이 더 커지게 되자, 애플리케이션을 두 개의 세그먼트로 분리하여 함수 라이브러리 공간을 사이에 두고 오가며 동작하게 배치해야 했다.\
  이것은 분명 지속 가능한 방법이 아니었다.\
  \


<figure><img src="https://blog.kakaocdn.net/dn/ee10iG/btq0Y4U6M9o/DJp1sLCCmk5VP94XoeS2M1/img.png" alt=""><figcaption><p>애플리케이션을 두 개의 주소 세그먼트로 분리</p></figcaption></figure>

&#x20;

&#x20;

### 재배치성

위 문제의 해결책은 재배치가 가능한 바이너리였다.

로더를 이용하여 메모리에 재배치할 수 있는 형태의 바이너리를 생성하도록 컴파일러를 수정하였다.

\
로더는 재배치 코드가 자리할 위치 정보를 전달받고, 재배치 코드에는 로드한 데이터에서 어느 부분을 수정해야 정해진 주소에 로드할 수 있는지를 알려주는 플래그가 삽입되었다.

&#x20;

프로그래머는 함수 라이브러리를 로드할 위치와 애플리케이션을 로드할 위치를 로더에게 지시할 수 있게 되었다.

로더는 바이너리르 입력받은 후, 단순히 하나씩 차례로 메모리로 로드하면서 재배치하는 작업을 처리하였고, 프로그래머는 필요한 함수만을 로드할 수 있게 되었다

&#x20;

* 링킹 로더 \
  컴파일러는 재배치 가능한 바이너리 안의 함수 이름을 메타데이터 형태로 생성하도록 수정되었다.\
  \
  프로그램이 라이브러리 함수를 호출한다면 외부 참조(external reference)로 생성했다.\
  프로그램이 라이브러리 함수를 정의한다면 외부 정의(external definition)로 생성했다.\
  \
  이렇게 함으로써 외부 정의를 로드할 위치가 정해지기만 하면 로더가 외부 참조를 외부 정의에 링크시킬 수 있게 된다. 이를 링킹 로더라고 한다.

### 링커

1970년대가 되자 프로그램이 커지게되고, 링킹 로더는 그 프로그램을 로드하는 데 매우 긴 시간이 걸렸다.

따라서, 로드와 링크가 분리되었다.

&#x20;

프로그래머는 링커라는 별도의 애플리케이션으로 이 작업을 처리하도록 만들었다.

링커는 링크가 완료된 재배치 코드를 만들어 주었고, 한 번 만들어준 실행 파일은 언제라도 빠르게 로드할 수 있게 되었다.\
\
이후 디스크의 속도가 증가하고 액티브X와 공유 라이브러리, jar 파일이 등장했으며,

다수의 .jar 또는 공유 라이브러리를 순식간에 서로 링크한 후, 링크가 끝난 프로그램을 실행할 수 있게 되었다.

이렇게 컴포넌트 플러그인 아키텍처가 탄생했다.

&#x20;

결과적으로 여기 말하는 소프트웨어 컴포넌트는 런타임에 플러그인 형태로 결합할 수 있는 동적 링크파일을 말한다.