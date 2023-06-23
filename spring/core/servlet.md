# Servlet

### Servlet이란

자바를 사용하여 웹페이지를 동적으로 생성하는 서버측 프로그램 혹은 그 사양을 말하며, 흔히 "서블릿"이라 불린다. 자바 서블릿은 웹 서버의 성늘을 향상하기 위해 사용되는 자바 클래스의 일종이다.&#x20;

JSP와 비슷하지만 JSP는 HTML 문서 안에 Java코드를 포함하는 반면, 서블릿은 자바코드안에 HTML을 포함하고 있다는 점에서 차이점이 있다.

<img src="../../.gitbook/assets/file.excalidraw (3) (2).svg" alt="" class="gitbook-drawing">

Servlet Life-Sycle

```
- Init() : 서블릿이 메모리에 로드될 때 한번만 호출 (코드 수정 시 다시 호출)
- doGet(),doPost() : data전송 시 호출 
- service() : 모든 요청은 service()를 통해 메소드로 이동 
- destroy() : 서블릿이 메모리에서 해제되면 호출
```

|    | GET                                                        | POST                                                       |
| -- | ---------------------------------------------------------- | ---------------------------------------------------------- |
| 특징 | 전송되는 데이터가 URL뒤 QueryString으로 전달                            | <p>URL과 별도로 전송<br>HTTP header 뒤 body에 입력 스트림 데이터로 전달</p>   |
| 장점 | <p>간단한 데이터를 빠르게 전송<br>form tag뿐만 아니자 직접 URL에 입력하여 전송가능</p> | <p>데이터의 제한이 없음.<br>최소한의 보안유지 효과를 볼 수 있음</p>                |
| 단점 | <p>데이터양에 제한이 있다<br>(URL창만큼)<br>보안상의 문제</p>                 | <p>전달 데이터의 양이 같은 경우 GET보다 느리다<br>(전송패킷을 body에 구성해야하므로)</p> |

<figure><img src="../../.gitbook/assets/image (3) (1).png" alt=""><figcaption></figcaption></figure>
