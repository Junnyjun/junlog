# Container Extension Points

Spring 컨테이너 확장 포인트는 일반적으로 개발자가 `ApplicationContext` 클래스를 서브클래싱하지 않고도 Spring IoC 컨테이너를 확장할 수 있는 기능을 제공합니다. 애플리케이션을 확장할 수 있는 여러 통합 인터페이스가 있으며, 이 중에서 `BeanPostProcessor`, `BeanFactoryPostProcessor`, 그리고 `FactoryBean` 같은 인터페이스가 자주 사용됩니다.&#x20;

## **Customizing Beans by Using a BeanPostProcessor**

`BeanPostProcessor` 인터페이스는 Spring 컨테이너가 Bean을 인스턴스화하고, 설정하고, 초기화한 후에 호출되는 콜백 메서드를 제공합니다. 이를 통해 컨테이너의 기본 인스턴스화 로직이나 의존성 해결 로직을 덮어쓰거나 사용자 정의 로직을 추가할 수 있습니다.&#x20;

### **구성 및 실행 순서 제어**

여러 `BeanPostProcessor` 인스턴스를 구성할 수 있으며, 이때 실행 순서를 `order` 속성을 통해 제어할 수 있습니다. 단, 이 속성은 `Ordered` 인터페이스를 구현한 경우에만 설정할 수 있습니다.&#x20;

직접 `BeanPostProcessor`를 작성하는 경우에도 `Ordered` 인터페이스 구현을 고려하는 것이 좋습니다.&#x20;

### **작동 원리**

`BeanPostProcessor` 인스턴스는 Spring IoC 컨테이너가 Bean 인스턴스를 생성한 후 해당 Bean에 대해 후처리 작업을 수행합니다. 이 과정에서 두 가지 주요 콜백 메서드가 실행됩니다:

* `postProcessBeforeInitialization`: 컨테이너의 초기화 메서드가 호출되기 전, 컨테이너가 이 메서드를 호출합니다.
* `postProcessAfterInitialization`: 초기화 메서드 호출 후, 이 메서드를 통해 추가 작업을 할 수 있습니다.

### **BeanPostProcessor의 범위**

`BeanPostProcessor` 인스턴스는 **컨테이너 단위**로 스코프가 지정됩니다. 이는 컨테이너 계층을 사용할 때만 관련이 있습니다.&#x20;

한 컨테이너에 정의된 `BeanPostProcessor`는 해당 컨테이너의 Bean만 후처리하며, 다른 컨테이너에 정의된 Bean은 처리하지 않습니다. 두 컨테이너가 동일한 계층의 일부이더라도 후처리되지 않습니다.

### **Bean 정의 변경 방법**

실제 Bean 정의(즉, Bean을 정의하는 청사진)를 변경하려면 `BeanFactoryPostProcessor`를 사용해야 합니다. 이 인터페이스는 설정 메타데이터를 조정하여 Bean이 인스턴스화되기 전에 Bean의 정의를 수정할 수 있습니다.&#x20;

#### **콜백 메서드**

`BeanPostProcessor` 인터페이스는 정확히 두 개의 콜백 메서드로 구성됩니다. 이 클래스가 후처리기로 컨테이너에 등록되면, 컨테이너에서 생성된 각 Bean 인스턴스에 대해 후처리기가 두 번 호출됩니다.&#x20;

첫 번째는 Bean 초기화 메서드 호출 전, 두 번째는 초기화 후입니다. 후처리기는 Bean 인스턴스에 대해 아무 작업도 수행하지 않거나, Bean 인스턴스를 프록시로 감싸는 등의 작업을 할 수 있습니다.&#x20;

Spring AOP 인프라 클래스 중 일부는 이 후처리기를 사용하여 프록시 래핑 로직을 제공합니다.

#### **자동 감지 및 등록**

`ApplicationContext`는 `BeanPostProcessor` 인터페이스를 구현하는 Bean을 자동으로 감지합니다. 그런 다음 Bean이 생성될 때 이를 호출할 수 있도록 `BeanPostProcessor`를 등록합니다.&#x20;

Bean 후처리기는 컨테이너 내에서 다른 Bean과 동일한 방식으로 배포될 수 있습니다.

#### **BeanPostProcessor의 프로그램 방식 등록**

`BeanPostProcessor` 등록을 위한 권장 방법은 `ApplicationContext` 자동 감지를 통해 이루어지지만, 프로그래밍 방식으로 `ConfigurableBeanFactory`의 `addBeanPostProcessor` 메서드를 사용하여 등록할 수도 있습니다.&#x20;

이를 통해 등록 전에 조건부 로직을 평가하거나 컨테이너 계층에서 BeanPostProcessor를 복사할 수 있습니다.&#x20;

그러나 이 방식으로 등록된 BeanPostProcessor는 `Ordered` 인터페이스를 존중하지 않으며, 등록된 순서가 곧 실행 순서를 결정합니다.

**AOP 자동 프록싱과 BeanPostProcessor**

`BeanPostProcessor` 인터페이스를 구현하는 클래스는 특별하며, 컨테이너에서 다른 방식으로 처리됩니다.&#x20;

모든 `BeanPostProcessor` 인스턴스와 직접 참조되는 Bean은 애플리케이션 컨텍스트의 **특별한 시작 단계**에서 인스턴스화됩니다.&#x20;

그런 다음 모든 `BeanPostProcessor` 인스턴스가 정렬된 방식으로 등록되고, 이후 생성된 Bean에 대해 적용됩니다. AOP 자동 프록싱은 `BeanPostProcessor`로 구현되기 때문에, `BeanPostProcessor` 인스턴스나 그것이 직접 참조하는 Bean은 자동 프록싱 대상이 아니며, 따라서 애스펙트가 적용되지 않습니다.

만약 `BeanPostProcessor`에 `@Autowired` 또는 `@Resource`로 연결된 Bean이 있다면, Spring이 타입 매칭 의존성 후보를 찾는 과정에서 예상치 못한 Bean에 접근할 수 있습니다.&#x20;

이로 인해 해당 Bean이 자동 프록싱 또는 다른 후처리 작업에 적합하지 않게 될 수 있습니다.&#x20;

***

### **Customizing Configuration Metadata with a BeanFactoryPostProcessor**

`BeanFactoryPostProcessor` 인터페이스는 Spring IoC 컨테이너가 Bean을 인스턴스화하기 전에 **Bean 설정 메타데이터**를 읽고 변경할 수 있는 확장 포인트입니다.&#x20;

이를 통해 컨테이너가 설정 메타데이터를 기반으로 Bean을 인스턴스화하기 전에 메타데이터를 동적으로 변경할 수 있습니다.

#### **동작 방식**

* `BeanFactoryPostProcessor`는 설정 메타데이터에 대해 작동하므로, Bean이 인스턴스화되기 전 Bean 정의에 접근할 수 있습니다.
* 여러 `BeanFactoryPostProcessor` 인스턴스를 구성할 수 있으며, `order` 속성을 사용해 실행 순서를 제어할 수 있습니다. 단, 이 속성은 `Ordered` 인터페이스를 구현한 경우에만 설정할 수 있습니다.

`BeanFactoryPostProcessor`는 메타데이터를 조작하는 데 주로 사용되며, Bean 인스턴스를 직접 수정하려면 `BeanPostProcessor`를 사용하는 것이 적합합니다.
