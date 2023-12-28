# 웹페이지 연결

## 웹페이지 연결

**세션 트래킹** HTTP 프로토콜은 stateless 방식으로 통신한다 \
웹페이지나 서블릿끼리 상태&정보를 공유하려면 웹페이지 연결 (세션 트래킹) 을 이용해야 한다

```
<hidden> 태그 : 웹페이지들 사이의 정보를 공유
브라우저에는 표시되지 않지만 미리 저장된 정보를 전송 할 수 있다

URL rewriting : Get방식으로 URL뒤에 정보를 붙여 다른페이지로 전송
Cookie : Client에 Cookie파일에 정보를 저장한 후 공유
Session : 서버 메모리에 정보를 저장한후 공유
```

### 쿠키

공유정보를 클라이언트 PC에 저장해 놓고 필요할때 웹페이지들이 공유하여 사용할 수 있도록 매개역할을 하는 방법

```
정보가 클라이언트에 저장
용량에 제한 4kb
보안이 취약
클라이언트 브라우저에서 사용유무를 설정 할 수 있다.
도메인당 쿠키가 만들어진다
```

| 속성          | Persistence 쿠키          | session 쿠키                   |
| ----------- | ----------------------- | ---------------------------- |
| 생성위치        | 파일로 생성                  | 브라우저 메모리에 생성                 |
| 종료시기        | 쿠키를 삭제하거나 쿠키설정값이 종료된 경우 | 브라우저를 종료한 경우                 |
| 최초접속 시 전송여부 | 최초접속 시 서버로 전송           | 최초접속 시 서버로 전송 X              |
| 용도          | 로그인 유무 또는 팝업창을 제한할 때    | 사이트 접속 시 session 인증정보를 유지할 때 |

psersistence 쿠키는 클라이언트에 파일로 정보를 저장하는 기능\
session 쿠키는 브라우저가 사용하는 메모리에 생성되는 쿠키&#x20;

**쿠키 기능 실행 과정**

1. 브라우저로 접속
2. 서버는 정보를 저장한 쿠키를 생성
3. 생성된 쿠키를 브라우저로 전송
4. 브라우저는 서버로받은 쿠키 정보를 쿠키 파일에 저장
5. 브라우저가 다시 접속해 서버가 브라우저에게 쿠키전송을 요청하면 브라우저는 쿠키정보를 넘겨준다

#### **쿠키 API**

* javax.servlet.http.Cookie
* HttpServletResponse의 addCookie를 이용해 클라이언트 브라우저에 쿠키를 전송
* HttpServletRequest의 getCookie를 이용해 쿠키를 가져온다

| method     | description          |
| ---------- | -------------------- |
| getComment | 쿠키에 대한 설명을 가져온다      |
| getDomain  | 쿠키의 유효한 도메인 정보를 가져온다 |
| getMaxAge  | 쿠키 유효 기간을 가져온다       |
| getName    | 쿠키 이름을 가져온다          |
| getPath    | 쿠키 디렉터리 정보를 가져온다     |
| getValue   | 쿠키의 설정값을 가져온다        |
| setComment | 쿠키에 대한 설명을 설정        |
| setDomain  | 쿠키의 유효한 도메인을 설정      |
| setMaxAge  | 쿠키의 유효기간 설정          |
| setValue   | 쿠키 값 설정              |
| setPath    | 쿠키의 디렉터리 설정          |

_인자값으로 음수나 setMaxAge를 사용하지않으면 Session쿠키 인자값으로 양수를 지정하면 persistence 쿠키_

***

### **Session**&#x20;

_서버 메모리에 생성되어 정보를 저장_ _보안이 요구되는 경우 session을 이용_

```
정보가 서버 메모리에 저장
세션연동은 세션쿠키를 사용
보안에 유리
서버에 부하를 줄 수 있다
브라우저(사용자) 당 한개의 세션이 생성
유효시간을 가진다
```

#### **Session 실행 과정**&#x20;

_브라우저가 서버에 최초 접속하면 서버의 서블릿은 세션 객체를 생성한 후 세션 객체에 대한 세션 id를 브라우저에 전송한다. 서버로부터 전송된 세션 id도 쿠키이며 (jsessionId) 입니다_

1. 브라우저로 사이트에 접속
2. 서버는 접속한 브라우저에대한 세션 객체를 생성
3. 서버는 생성된 세션 id를 클라이언트 브라우저에 응답
4. 브라우저는 서버로부터 받은 세션 id를 브라우저가 사용하는 메모리의 세션 쿠키에 저장
5. 브라우저가 재 접속하면 브라우저는 세션쿠키에 저장된 세션 id를 서버에 전달
6. 서버는 전송된 세션 id를 이용해 작업을 수행

#### **Session API**

getSession : 기존의 세션객체가 존재하면 반환 없으면 새로 생성(false일때는 null)

| method                 | description                       |
| ---------------------- | --------------------------------- |
| getAttribute           | 속성이름이 name인 값을 반환                 |
| getAttributeNames      | 세션 속성 이름들을 Enumeration 객체 타입으로 반환 |
| getCreationTime        | 세션이 생성된 시간을 반환                    |
| getId                  | 세션의 고유 식별자를 반환                    |
| getMaxInactiveInterval | 세션유지시간을 반환                        |
| invalidate             | 현재 생성된 세션을 소멸                     |
| isNew                  | 세션의 새로움을 판별                       |
| removeAttribute        | 세션 제거                             |
| setAttribute           | 세션에 value 할당                      |

***

encodeURL 브라우저에서 쿠키&세션 기능을 사용할 수 없게 설정했을때 사용

## 서블릿 속성과 스코프

### **서블릿 속성**&#x20;

세가지 서블릿 API 클래스에 저장되는 객체

* servletContext
* HttpSession
* HttpServletRequest

### **서블릿 스코프**&#x20;

서블릿 API에 바인딩된 속성에 대한 접근 범위

* 로그인 상태 유지기능
* 장바구니 기능
* MVC에 Model\&View 데이터 전달 기능

| Scope 종류         | Servlet API        | Attribute\&Scope       |
| ---------------- | ------------------ | ---------------------- |
| Applicatio Scope | ServletContext     | 속성은 어플리케이션 전체에 접근      |
| Session Scope    | HttpSession        | 속성은 브라우저 에서만 접근        |
| Request Scope    | HttpSerlvetRequest | 속성은 해당 요청/응답 사이클에서만 접근 |

**URL 패턴** 실제 서블릿의 매핑 이름 서블릿 매핑시 가상의 이름으로 클라이언트가 브라우저에서 요청할 때 사용되며 반드시 / 로 시작

## 필터 API

브라우저에서 서블릿에 요청하거나 응답할 때 미리 요청이나 응답과 관련해 여러가지 작업을 처리하는 기능

### **요청 필터**

사용자 인증 및 권한 검사\
요청 시 요청 관련 로그 작업\
인코딩

### **응답 필터**

응답 결과에 대한 암호과 작업\
서비스 시간 측정

| 메서드     | 기능                                                                         |
| ------- | -------------------------------------------------------------------------- |
| destroy | 필터 소명 시 컨테이너에 의해 호출되어 종료 작업을 수행\| \|doFilter\|요청/응답 시 컨테이너에 의해 호출되어 기능을 수행 |
| init    | 필터 생성 시 컨테이너에 의해 호출되어 초기화 수행                                               |

* getFilterName : 필터이름 반환
* getInitParameter : 매개변수 name에 대한 값 반환
* getServletContext : 서블릿 콘텍스트 객체를 반환

#### **Servlet Listener API**

| Listener                        |                                    |
| ------------------------------- | ---------------------------------- |
| ServletContextAttributeListener | context 객체의 속성 추가/제거/수정 이벤트 발생시 처리 |

* attributeAdd() :
* attributeRemoved()
* attributeReplaced

|                     |                          |
| ------------------- | ------------------------ |
| HttpSessionListener | 세션 객체의 생성/소멸 이벤트 발생 시 처리 |

* sessionCreated()
* sessionDestroyed()

|                        |                      |
| ---------------------- | -------------------- |
| ServletRequestListener | 클라이언트의 요청 이벤트 발생시 처리 |

* requestInitialized
* requestDestroyed

|                                 |                              |
| ------------------------------- | ---------------------------- |
| ServletRequestAttributeListener | 요청객체에 속성 추가/제거/수정 이벤트 발생시 처리 |

* attributesAdded()
* attributesRemoved()
* attributesReplaced()

|                            |                                     |
| -------------------------- | ----------------------------------- |
| HttpSessionBindingListener | 세션에 바인딩/언바인딩 된 객체를 알려주는 이벤트 발생 시 처리 |

* valueBound()
* valueUnbound()

|                        |                            |
| ---------------------- | -------------------------- |
| ServletContextListener | 컨텍스트 객체의 생성/소멸 이벤트 발생 시 처리 |

* contextInitialized()
* contextDestroyed()

|                                |                        |
| ------------------------------ | ---------------------- |
| HttpSerssionActivationListener | 세션 활성화/비활성화 비엔트 발생시 처리 |

* sessionDidActivated()
* sessionWillPassivated()
