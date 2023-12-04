# 컴퓨터 네트워크

## Static Pages와 Dynamic Pages <a href="#static-pages-dynamic-pages" id="static-pages-dynamic-pages"></a>

![](https://velog.velcdn.com/images%2Fiseeu95%2Fpost%2F21ec560f-39e1-42bc-9805-0498468b5344%2Fimage.png)

### Static Pages <a href="#static-pages" id="static-pages"></a>

Web Server는 파일 경로 이름을 받아 경로와 일치하는 file contents를 반환한다.\
항상 동일한 페이지를 반환한다.\
Ex) image, html, css, javascript 파일과 같이 컴퓨터에 저장되어 있는 파일들

### Dynamic Pages <a href="#dynamic-pages" id="dynamic-pages"></a>

인자의 내용에 맞게 동적인 contents를 반환한다.\
즉, 웹 서버에 의해서 실행되는 프로그램을 통해서 만들어진 결과물

`Servlet`: WAS 위에서 돌아가는 Java Program\
개발자는 Servlet에 doGet()을 구현한다.

## Web Server와 WAS의 차이 <a href="#web-server-was" id="web-server-was"></a>

![](https://velog.velcdn.com/images%2Fiseeu95%2Fpost%2F96f0c0bf-fe03-437a-9974-dee4bf8cc748%2Fimage.png)

### Web Server <a href="#web-server" id="web-server"></a>

#### Web Server의 개념 <a href="#web-server" id="web-server"></a>

소프트웨어와 하드웨어로 구분된다.

#### 하드웨어

Web 서버가 설치되어 있는 컴퓨터

#### 소프트웨어

웹 브라우저 클라이언트로부터 HTTP 요청을 받아 정적인 컨텐츠(.html .jpeg .css 등)를 제공하는 컴퓨터 프로그램

### Web Server의 기능 <a href="#web-server" id="web-server"></a>

HTTP 프로토콜을 기반으로 하여 클라이언트(웹 브라우저 또는 웹 크롤러)의 요청을 서비스하는 기능을 담당한다.\
요청에 따라 아래의 두 가지 기능 중 적절하게 선택하여 수행한다.

#### **정적인 컨텐츠 제공**

WAS를 거치지 않고 바로 자원을 제공한다.

#### **동적인 컨텐츠 제공을 위한 요청 전달**

클라이언트의 요청(Request)을 WAS에 보내고, \
WAS가 처리한 결과를 클라이언트에게 전달(응답, Response)한다.

클라이언트는 일반적으로 웹 브라우저를 의미한다.

#### Web Server의 예 <a href="#web-server" id="web-server"></a>

Ex) Apache Server, Nginx, IIS(Windows 전용 Web 서버) 등\
WAS(Web Application Server)

### WAS의 개념 <a href="#was" id="was"></a>

DB 조회나 다양한 로직 처리를 요구하는 **동적인 컨텐츠**를 제공하기 위해 만들어진

HTTP를 통해 컴퓨터나 장치에 애플리케이션을 수행해주는 미들웨어(소프트웨어 엔진)이다.

“웹 컨테이너(Web Container)” 혹은 “서블릿 컨테이너(Servlet Container)”라고도 불린다.\
Container란 JSP, Servlet을 실행시킬 수 있는 소프트웨어를 말한다.

즉, WAS는 JSP, Servlet 구동 환경을 제공한다.

### WAS의 역할 <a href="#was" id="was"></a>

`WAS = Web Server + Web Container`\
Web Server 기능들을 구조적으로 분리하여 처리하고자하는 목적으로 제시되었다.

분산 트랜잭션, 보안, 메시징, 쓰레드 처리 등의 기능을 처리하는 분산 환경에서 사용된다.\
현재는 WAS가 가지고 있는 Web Server도 정적인 컨텐츠를 처리하는 데 있어서 성능상 큰 차이가 없다.

#### WAS의 주요 기능 <a href="#was" id="was"></a>

프로그램 실행 환경과 DB 접속 기능 제공\
여러 개의 트랜잭션(논리적인 작업 단위) 관리 기능\
업무를 처리하는 비즈니스 로직 수행\
WAS의 예\
Ex) Tomcat, JBoss, Jeus, Web Sphere 등

## Web Server와 WAS를 구분하는 이유 <a href="#web-server-was" id="web-server-was"></a>

![](https://velog.velcdn.com/images%2Fiseeu95%2Fpost%2Fe7d4db2d-70df-40a7-8d6f-2047be0f97bb%2Fimage.png)

### Web Server가 필요한 이유? <a href="#web-server" id="web-server"></a>

클라이언트(웹 브라우저)에 이미지 파일(정적 컨텐츠)을 보내는 과정을 생각해보자.

이미지 파일과 같은 정적인 파일들은 웹 문서(HTML 문서)가 클라이언트로 보내질 때 함께 가는 것이 아니다.

클라이언트는 HTML 문서를 먼저 받고 그에 맞게 필요한 이미지 파일들을 다시 서버로 요청하면 그때서야 이미지 파일을 받아온다.\
Web Server를 통해 정적인 파일들을 Application Server까지 가지 않고 앞단에서 빠르게 보내줄 수 있다.\
따라서 Web Server에서는 정적 컨텐츠만 처리하도록 기능을 분배하여 서버의 부담을 줄일 수 있다.

### WAS가 필요한 이유? <a href="#was" id="was"></a>

웹 페이지는 정적 컨텐츠와 동적 컨텐츠가 모두 존재한다.\
사용자의 요청에 맞게 적절한 동적 컨텐츠를 만들어서 제공해야 한다.

이때, **Web Server만을 이용한다면 사용자가 원하는 요청에 대한 결과값을 모두 미리 만들어 놓고 서비스**를 해야 한다. 하지만 이렇게 수행하기에는 **자원이 절대적으로 부족**하다.

따라서 **WAS를 통해 요청에 맞는 데이터를 DB에서 가져와서 비즈니스 로직에 맞게 그때 그때 결과를 만들어서 제공**함으로써 자원을 효율적으로 사용할 수 있다.

그렇다면 WAS가 Web Server의 기능도 모두 수행하면 되지 않을까?

### **기능을 분리하여 서버 부하 방지**

WAS는 DB 조회나 다양한 로직을 처리하느라 바쁘기 때문에 **단순한 정적 컨텐츠는 Web Server에서 빠르게 클라이언트에 제공**하는 것이 좋다.

**WAS는 기본적으로 동적 컨텐츠를 제공**하기 위해 존재하는 서버이다.

만약 정적 컨텐츠 요청까지 WAS가 처리한다면 정적 데이터 처리로 인해 부하가 커지게 되고, 동적 컨텐츠의 처리가 지연됨에 따라 수행 속도가 느려진다.

#### 물리적으로 분리하여 보안 강화

SSL에 대한 암복호화 처리에 Web Server를 사용

여러 대의 WAS를 연결 가능 Load Balancing을 위해서 Web Server를 사용

특히 대용량 웹 어플리케이션의 경우(여러 개의 서버 사용) Web Server와 WAS를 분리하여 무중단 운영을 위한 장애 극복에 쉽게 대응할 수 있다.

예를 들어, 앞 단의 Web Server에서 오류가 발생한 WAS를 이용하지 못하도록 한 후 WAS를 재시작함으로써 사용자는 오류를 느끼지 못하고 이용할 수 있다.

#### 여러 웹 어플리케이션 서비스 가능

예를 들어, 하나의 서버에서 PHP Application과 Java Application을 함께 사용하는 경우\
기타\
접근 허용 IP 관리, 2대 이상의 서버에서의 세션 관리 등도 Web Server에서 처리하면 효율적이다.\
즉, 자원 이용의 효율성 및 장애 극복, 배포 및 유지보수의 편의성 을 위해 Web Server와 WAS를 분리한다.\
Web Server를 WAS 앞에 두고 필요한 WAS들을 Web Server에 플러그인 형태로 설정하면 더욱 효율적인 분산 처리가 가능하다.

### Web Service Architecture <a href="#web-service-architecture" id="web-service-architecture"></a>

![](https://velog.velcdn.com/images%2Fiseeu95%2Fpost%2Fc10dde73-9733-4d49-be59-a3f9f2af3b6a%2Fimage.png)\
다양한 구조를 가질 수 있다.

> Client -> Web Server -> DB\
> Client -> WAS -> DB\
> Client -> Web Server -> WAS -> DB
