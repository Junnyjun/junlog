# 서블릿 계층 구조

## 서블릿 계층 구조

servlet & servletconfig 인터페이스를 구현하여 제공

```
doDelete(HttpServletRequest req, HttpServletResponse resp)
doGet(HttpServletRequest req, HttpServletResponse resp)
doHead(HttpServletRequest req, HttpServletResponse resp)
doPost(HttpServletRequest req, HttpServletResponse resp)
```

| 메서드명            | 기능                                         |
| --------------- | ------------------------------------------ |
| protect service | request를 public service 전달받아 doXXX 메서드를 호출 |
| public service  | 클라이언트의 request를 protect service 전달합니다      |

### **생명주기**

| 생명주기 단계 | 메서드 특징               |
| ------- | -------------------- |
| 초기화     | init()               |
| 작업 수행   | do get() & do post() |
| 종료      | destroy()            |

#### init()

서블릿 요청 시 맨 처음 한번 만 호출 됩니다

초기화 작업을 주로 수행 합니다.

#### do get() & do post()

서블릿 요청 시 매번 호출 됩니다

클라이언트가 요청하는 작업을 수행

#### destroy()

서블릿이 기능을 수행 하고 메모리에서 소멸될 때 호출

서블릿의 마무리 작업을 주로 수행

## 서블릿 기초

요청과 관련된 API : javax.servlet.http.HttpServletRequest \
응답과 관련된 API : javax.servlet.http.HttpServletResponse

### 응답 & 요청

**ServletRequest의 여러가지 메서드**

| 메서드명           | 기능                                            |
| -------------- | --------------------------------------------- |
| authenticate   | ServletContext 객체에 대한 인증을 위한 컨테이너 로그인 메커니즘 사용 |
| changSessionId | 현재 요청의 세션 id를 변경하여 새 세션 id를 반환                |
| getContextPath | 요청한 컨텍스트의 URI를 반환                             |
| getCookies     | 클라이언트가 보낸 쿠키객체들을 배열로 반환                       |
| getHeaderName  | 요청에 포함된 헤더의 name속성을 enumeration으로 반환          |
| getMethod      | get, Post, put의 HTTP요청 방식을 반환                 |
| getRequestURI  | URL의 컨텍스트 이름과 파일 경로까지 반환                      |
| getSession     | 현재의 요청과 연관된 세션을 반환합니다. 없을땐 새로 만들어서 반환         |

***

### **ServletResponse의 여러가지 메서드**

| 메서드명           | 기능                                             |
| -------------- | ---------------------------------------------- |
| addCookie      | 응답에 쿠키를 추가                                     |
| addHeader      | name & value를 헤더에 추가                           |
| encodeURL      | 클라이언트가 쿠키를 지원하지 않을때 세션 id을 포합한 특정 URL을 인코딩 한다. |
| getHeaderNames | 현재 응답의 헤더에 포함된 naem을 얻어옵니다                     |
| sendRedirect   | 클라이언트에게 리다이렉트 읍답을 보낸 후 특정 URL로 재요청             |
| getPathInfo    | 요청시 보낸 URL과 추가 경로정보를 반환                        |

***

### form

name : form 이름 지정\
method : 전송 방법 지정\
action : 전송할 경로 지정\
encType : encoding 타입 지정

#### 응답 받기

| 메서드명               | 기능                            |
| ------------------ | ----------------------------- |
| getParameter       | name 값을 알고 있을때, name에 값을 받아온다 |
| getParameterValues | 같은 name에 대해 여러개의 값을 얻을때 사용    |
| getParameterNames  | 같은 name에 대해 여러개의 값을 얻을때 사용    |

## 데이터 전송하기

GET & POST 전송 방식

GET ? : URL주소에 데이터를 붙여 전송

```
URL 뒤에 name = value 형태로 전송된다.
여러개의 데이터는 &로 구분
보안이 취약
전송할 수 있는 데이터는 최대 255자
기본 전송방식 이며 사용이 쉽다
웹 브라우저에 직접 입력 가능
```

POST ? : 데이터를 숨겨서 전송

```
Tcp/ip 프로토콜 데이터에 body영역에 숨겨 진 채 전송
보안에 유리
데이터 용량이 넉넉하다
속도가 느리다
```

_ex ) java script_

```
<script  type="text/javascript">
function  fn_validate() {
var  frmLogin = document.frmLogin;
var  user_id = frmLogin.user_id.value;
var  user_pw = frmLogin.user_pw.value;
if ((user_id.length == 0 || user_id == "") 
||(user_pw.length == 0 || user_pw == "")) {
alert("required id & pw");
} else {
frmLogin.method = "post";
frmLogin.action = "login5";
frmLogin.submit();
}
}
</script>
```
