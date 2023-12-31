# JSP

## _JSP_

화면 기능을 디자이너가 작업하기 쉽게 하기위해 등장

### **구성 요소**

HTML & CSS & JAVASCRIPT\
JSP 기본&액션 태그

### **JSP 변환 과정**

1. 변환 단계 : 컨테이너는 JSP파일을 자바 파일로 변환
2. 컴파일 단계 : 컨테이너는 변환된 자바 파일을 클래스 파일로 컴파일 합니다.
3. 실행 단계 : 컨테이너는 클래스파일을 실행하여 그 결과를 브라우저로 전송해 출력합니다

**JSP 구성 요소**

* 디렉티브 태그
* 스크립트 요소
* 표현 언어
* 내장 객체
* 액션 태그
* 커스텀 태그

### **디렉티브 태그**&#x20;

_JSP 페이지에 전반적인 설정 정보를 지정할 때 사용하는 태그_

* 페이지 디렉티브 : JSP 페이지의 전반적인 정보를 설정
* 인클루드 디렉티브 : 공통으로 사용하는 JSP 페이지를 다른 JSP페이지에 추가할 때 사용
* 태그라이브 디렉티브 태그 : 개발자나 프레임워크에서 제공하는 태그를 사용할 때 사용

#### **JSP 스크립트 요소**

* 선언문 : jsp에서 변수나 메서드를 선언할때사용
* 스크립트 릿: jsp에서 자바코드를 작성할 때 사용
* 표현식 : jsp에서 변수의 값을 출력할 때 사용

#### **내장 객체 기능**

| 내장객체        | 타입                                     | 설명                   |
| ----------- | -------------------------------------- | -------------------- |
| request     | javax.servlet.http.HttpServletRequest  | 클라이언트의 요청정보 저장       |
| response    | javax.servlet.http.HttpServletResponse | 응답 정보를 저장            |
| out         | javax.servlet.jsp.jspWriter            | JSP 페이지에서 결과를 출력     |
| session     | javax.servlet.http.HttpSession         | 세션 정보를 저장            |
| application | ServletContext                         | 컨텍스트 정보를 저장          |
| pageContext | PageContext                            | JSP 페이지 정보를 저장       |
| page        | java.lang.Object                       | JSP 페이지 서블릿 인스턴스를 저장 |
| config      | javax.servlet.ServletConfig            | JSP 페이지 설정정보 저장      |
| exception   | java.lang.Exception                    | 예외발생시 예외를 처리         |

***

**JSP 예외 처리** _예외처리 담당 JSP를 만들어 예외발생시 예외처리담당 JSP파일을 지정한다_

### **인클루드 액션 태그**

| 형식              | 설명                                           |
| --------------- | -------------------------------------------- |
| jsp:include     | JSP를 현재 JSP에 포함                              |
| JSP:forward     | 서블릿에서 RequestDispatcher 클래스의 포워딩 기능을 대신하는 태그 |
| jsp:useBean     | 객체를 생성하기 위한 new 연산자를 대신하는 태그                 |
| jsp:setProperty | setter를 대신하는                                 |
| jsp:getProperty | getter를 대신하는                                 |

| 항목                  | 인클루드 액션 태그             | 인클루드 디렉티브 태그                           |
| ------------------- | ---------------------- | -------------------------------------- |
| 기능                  | JSP 레이아웃 모듈화           | JSP 레이아웃 모듈화                           |
| 처리 시간               | 요청 시간에 처리              | JSP를 자바 코드로 변환 시 처리                    |
| 데이터 처리 방법           | param 액션 태그를 이용해 동적 처리 | 정적 처리만 가능                              |
| 포함된 JSP 자바 파일 변환 여부 | 포함되는 JSP가 각각 자바 파일로 생성 | 포함되는 JSP가 포함하는 JSP에 합쳐진 후 한개의 자바파일로 생성 |

***

**Bean 사용하기** _Bean 데이터를 저장하거나 전달하는데 사용한다_

* 속성의 접근제한자는 private
* 각 속성은 setter/getter
* setter와getter의 첫글자는 소문자
* 인자 없는 생성자를 반드시 가지며 다른생성자도 추가 할 수 있다

## 표현언어

기존 표현식보다 편리하게 값을 출력

변수와 여러가지 연산자를 포함할 수 있다

JSP의 내장객체에 저장된 속성 및 자바 빈 속성도 표현언어 에서 출력할 수 있다

표현언어 자체 내장 객체도 제공

JSP페이지 생성 시 기본설정은 표현언어를 사용할 수 없다

페이지 디렉티브 태그에서는 반드시 isElIgnored=false로 설정해야 한다

### _`${표현식 & 값}`_

***

**표현 언어 내장 객체** **내장 객체 기능**

| 내장객체        | 타입                                     | 설명                   |
| ----------- | -------------------------------------- | -------------------- |
| request     | javax.servlet.http.HttpServletRequest  | 클라이언트의 요청정보 저장       |
| response    | javax.servlet.http.HttpServletResponse | 응답 정보를 저장            |
| out         | javax.servlet.jsp.jspWriter            | JSP 페이지에서 결과를 출력     |
| session     | javax.servlet.http.HttpSession         | 세션 정보를 저장            |
| application | ServletContext                         | 컨텍스트 정보를 저장          |
| pageContext | PageContext                            | JSP 페이지 정보를 저장       |
| page        | java.lang.Object                       | JSP 페이지 서블릿 인스턴스를 저장 |
| config      | javax.servlet.ServletConfig            | JSP 페이지 설정정보 저장      |
| exception   | java.lang.Exception                    | 예외발생시 예외를 처리         |

***

**JSP 예외 처리** _예외처리 담당 JSP를 만들어 예외발생시 예외처리담당 JSP파일을 지정한다_

### **내장객체 종류와 기능**

#### 스코프

* PageScope : page와 같은 기능을 하고 page영역에 바인딩된 객체를 참조
* requestScope : request와 같은 기능을 하고 request에 바인딩된 객체를 참조
* sessionScopre : session와 같은 기능을 하고 session에 바인딩된 객체를 참조
* applicationScopre : application와 같은 기능을 하고 application에 바인딩된 객체를 참조

#### 요청 매개 변수

* param : request.getParameter를 호출한것과 같이 한개의 값을 전달하는 요청 매개변수
* paramValue : request.getParameterValues 를 호출한 것과 같이 여러개의 값을 전달하는 요청 매개변수

헤더값

* header : request.getHeader 와 같이 요청 헤더이름의 정보를 단일값으로 반환
* headerValues : request.getHeader 와 같이 요청 헤더이름의 정보를 배열로 반환

쿠키 값

* cookie : 쿠키이름의 값을 반환
* pageContext : pageContext객체를 참조할 때 사용

**커스텀 태그** _JSTL : JSP 페이지에서 가장 많이 사용하는 기능을 태그로 제공하며 JSTL 라이브러리를 따로 설치해서 사용_

## 라이브러리 태그

**Core** 자바의 import문 처럼 코어 태그 라이브러리를 사용하려면 \
반드시 jsp 페이지 상단에 taglib 디렉티브 태그를 추가해야한다

#### 변수 지원

\<c:set> : JSP 페이지에서 변수를 지정

\<c:remove> : 지정된 변수를 제거

#### 흐름 제어

\<c:if> : 조건문을 사용합니다

\<c:choose> : switch문을 사용합니다

\<c:forEach> : 반복문을 사용

\<c:forTokens> : 구분자로 분리된 각각의 토큰을 처리할 때 사용

#### URL 처리

\<c:import> : URL을 이용해 다른 자원을 JSP 페이지에 추가

\<c:redirect> response.sendRedirect의 기능을 수행

\<c:url> : 요청 매개변수로부터 URL을 생성
