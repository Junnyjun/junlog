# Introduce

## Spring IoC 컨테이너와 빈 소개

Spring Framework는 제어의 역전(Inversion of Control, IoC) 원칙을 구현합니다.&#x20;

IoC의 특수한 형태로서 의존성 주입(Dependency Injection, DI)은 객체가 생성자 인자, 팩토리 메서드의 인자, 또는 객체 인스턴스가 생성되거나 팩토리 메서드에서 반환된 후에 설정되는 속성을 통해서만 의존성을 정의하는 방식입니다.&#x20;

IoC 컨테이너는 빈을 생성할 때 이러한 의존성을 주입합니다.

`org.springframework.beans`와 `org.springframework.context` 패키지는 Spring Framework의 IoC 컨테이너의 기초를 이룹니다.&#x20;

`BeanFactory` 인터페이스는 어떤 종류의 객체든 관리할 수 있는 고급 구성 메커니즘을 제공합니다.&#x20;

* Spring의 AOP 기능과의 쉬운 통합
* 국제화를 위한 메시지 리소스 처리
* 이벤트 발행
* 웹 애플리케이션에서 사용할 수 있는 `WebApplicationContext`와 같은 애플리케이션 계층별 특정 컨텍스트

간단히 말해서, `BeanFactory`는 구성 프레임워크와 기본 기능을 제공하고, `ApplicationContext`는 더 많은 엔터프라이즈 특정 기능을 추가합니다.&#x20;

`ApplicationContext`는 `BeanFactory`의 완전한 상위 집합이며, 이 장에서는 Spring의 IoC 컨테이너 설명에 `ApplicationContext`만 사용됩니다.&#x20;

Spring에서 애플리케이션의 중추를 형성하고 Spring IoC 컨테이너에 의해 관리되는 객체를 빈(bean)이라고 합니다.&#x20;

빈은 Spring IoC 컨테이너에 의해 인스턴스화되고 조립되며 관리되는 객체입니다. 빈은 단순히 애플리케이션의 많은 객체 중 하나일 뿐입니다. 빈과 그들 간의 의존성은 컨테이너에서 사용되는 구성 메타데이터에 반영됩니다.
