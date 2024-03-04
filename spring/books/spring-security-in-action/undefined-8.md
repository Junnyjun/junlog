# 필터 구현

스프링 시큐리티에서 HTTP필터는 HTTP요청에 적용되는 다양한 책임을 위임한다.

<img src="../../../.gitbook/assets/file.excalidraw.svg" alt="" class="gitbook-drawing">

FilterChain을 통해 다양한 요구사항별 필터를 적용할 수 있다.

## Filter Chain 구현
필터를 만드려면 일반적으로 Servlet Filter를 구현한다.
다른 필터와 마찬가지로 `doFilter`메서드를 구현한다.

- serveltRequest : HTTP요청
- servletResponse : HTTP응답
- filterChain : 다음 필터로 요청을 전달하는데 사용

필터 체인은 필터가 작동하는 순서가 정의된 필터의 모음을 나타낸다.

- BasicAuthenticationFilter : HTTP Basic 인증을 처리하는 필터
- CsrfFilter : CSRF 공격을 방어하는 필터
- CorsFilter : CORS를 처리하는 필터

## 기존 필터 앞 필터 추가

