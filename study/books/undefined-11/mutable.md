# mutable 컬렉션 사용을 고려하라

**불변 컬렉션과 가변 컬렉션의 성능**

가변 컬렉션이 성능적인 측면에서 조금 더 빠릅니다.

불변 컬렉션에 요소를 추가하는 것이 이해 안 될 수 있지만 요소를 추가하려면 새로운 컬렉션을 만들어서  요소를 추가해야 합니다.

컬렉션을 복제하는 것은 비용이 많이 듭니다.

&#x20;

**안정성**

하지만 아이템1에서 이야기했듯이 불변 컬렉션을 사용하게 되면 동기화와 캡슐화 측면에서 안전합니다.

&#x20;

**결론**

지역변수를 사용할 때는 가변 컬렉션을 사용하는 것이 더 합리적입니다.

표준 라이브러리도 내부적인 처리를 수행할 때는 가변 컬렉션을 사용하도록 구현되어 있습니다.