# Component

`Spring Integration` 은 프로그래밍 모델을 메시지 처리 도메인으로 확장하여 엔터프라이즈 통합 패턴을 구현하는데 사용할 수 있는 프레임워크입니다.

메시지 기반 아키텍처를 지원하여, 특정 비즈니스 로직의 실행, 라우팅, 변환을 지원합니다.

`Spring Integration` 은 다음과 같은 특징을 가지고 있습니다.

* 메시지 기반 아키텍처
* 엔터프라이즈 통합 패턴
* 비동기 메시징
* 이벤트 기반 프로그래밍

## Main Components

전형적인 스프링 기반은 계층구조로 설계하고, 메시징 기반은 조금더 수평적인 관점을 더하긴 하지만\
수직적인 관점에서 관심사를 분리할수 있게 해준다.

{% hint style="info" %}
Message

스프링에서 메시지란 객체를 처리하는 동안 사용하는 메타 데이터를 감싼 `Wrapper 이다`

페이로드와 여러가지 헤더로 구성된다.
{% endhint %}

## Message Channel

Pipe and Filter 패턴을 구현하기 위한 인터페이스로, 메시지를 전달하는 역할을 한다.

<img src="../../../.gitbook/assets/file.excalidraw (45).svg" alt="" class="gitbook-drawing">

Producer는 메시지를 생성하고, Consumer는 메시지를 소비한다.\
`point-to-point`, `publish-subscribe`등 다양한 패턴을 지원한다.

### Message Endpoint

Spring Integration의 주요 목표는 제어의 역전을 통해 통합 솔루션을 만들어 주는 일이다.\
사용자는 컨슈머와 프로듀서를 직접 구현할 필요가 없으며, 메시지를 만들거나 연산을 호출하지 않아도 된다.

순수 객체 기반으로 구현을 이어가고 도메인 모델에 집중할 수 있어야 한다.

메시지 엔드 포인트는 `pipe and filter` 에서 filter를 나타낸다.\
엔드 포인트는 어플리케이션 코드를 메시지 처리 프레임워크에 비침투적으로 연결해주는 역할을 한다
