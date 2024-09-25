# 빈 정의 상속

Bean Definition Inheritance(빈 정의 상속)는 Spring에서 매우 강력한 기능으로, **부모 Bean** 정의에서 많은 설정 정보를 자식 Bean 정의에 상속할 수 있도록 합니다. 이러한 상속 기능을 사용하면 **생성자 인자**, **프로퍼티 값**, **초기화 메서드**, **스코프** 등의 설정을 **부모 Bean**으로부터 상속받고, 필요한 경우 일부 설정만 **덮어쓰거나 추가**할 수 있습니다.

이 기능은 XML 기반의 Spring 구성에서 특히 유용하며, Bean 설정을 **템플릿**처럼 사용할 수 있게 합니다. 이렇게 하면 코드 중복을 줄이고 구성의 일관성을 유지할 수 있습니다.

#### **Bean 상속의 기본 개념**

부모 Bean 정의에는 다음과 같은 속성들이 포함될 수 있습니다:

* **생성자 인자**
* **프로퍼티 값**
* **초기화 메서드 및 소멸 메서드**
* **스코프**
* **정적 팩토리 메서드 이름**

자식 Bean 정의는 이러한 속성을 상속받아 사용할 수 있으며, **필요한 경우 일부 속성만 재정의**하여 사용할 수 있습니다.

#### **자식 Bean 정의의 특징**

1. **상속된 속성**:
   * 자식 Bean 정의는 부모로부터 생성자 인자, 프로퍼티 값, 메서드 오버라이드, 스코프 등을 상속받습니다.
   * 자식 Bean은 상속받은 값을 추가하거나 덮어쓸 수 있습니다.
2. **독립적인 설정**:
   * 일부 속성은 자식 Bean이 항상 독립적으로 가집니다. 예를 들어, `depends-on`, `autowire mode`, `dependency check`, `singleton`, `lazy-init` 등의 설정은 부모로부터 상속되지 않고, 자식 정의에만 적용됩니다.

#### **XML 구성에서의 Bean 상속 예시**

다음은 XML 기반으로 부모 Bean 정의를 상속하는 방법입니다.

**부모 Bean 정의 (추상 클래스처럼 동작)**

```xml
<bean id="inheritedTestBean" abstract="true" class="org.springframework.beans.TestBean">
    <property name="name" value="parent"/>
    <property name="age" value="1"/>
</bean>
```

* 이 `inheritedTestBean`은 **추상 Bean**으로 설정되었으며, 다른 Bean이 이 정의를 상속하여 사용할 수 있습니다.
* `name` 속성은 "parent"로, `age` 속성은 1로 설정되어 있습니다.

**자식 Bean 정의**

```xml
<bean id="inheritsWithDifferentClass" class="org.springframework.beans.DerivedTestBean"
      parent="inheritedTestBean" init-method="initialize">
    <property name="name" value="override"/>
    <!-- age 속성은 부모 Bean 정의에서 상속받아 1로 설정됨 -->
</bean>
```

* 이 `inheritsWithDifferentClass` Bean은 부모인 `inheritedTestBean`으로부터 **name**과 **age** 속성을 상속받습니다.
* **name** 속성은 `override`로 덮어쓰였지만, **age**는 상속된 값인 1을 사용합니다.
* 또한, 자식 Bean은 새로운 초기화 메서드(`init-method="initialize"`)를 추가했습니다.

#### **추상 Bean 정의**

부모 Bean 정의가 추상(abstract)으로 설정되면, 이 Bean은 **자체적으로는 인스턴스화할 수 없습니다**. 이러한 추상 Bean은 오직 **템플릿**으로만 사용되며, 자식 Bean이 이를 상속하여 사용해야 합니다.

**추상 Bean의 예시:**

```xml
<bean id="inheritedTestBeanWithoutClass" abstract="true">
    <property name="name" value="parent"/>
    <property name="age" value="1"/>
</bean>

<bean id="inheritsWithClass" class="org.springframework.beans.DerivedTestBean"
      parent="inheritedTestBeanWithoutClass" init-method="initialize">
    <property name="name" value="override"/>
    <!-- age는 부모 Bean 정의에서 상속받아 1로 설정됨 -->
</bean>
```

여기서 `inheritedTestBeanWithoutClass`는 **클래스가 명시되지 않은 추상 Bean**으로, 오직 템플릿으로만 사용됩니다.

#### **중요 사항**

1. **abstract 속성**:
   * 부모 Bean 정의가 추상적으로 설정되지 않으면, Spring 컨텍스트는 이 Bean을 인스턴스화하려고 시도합니다. 추상 Bean 정의는 \*\*반드시 `abstract="true"`\*\*로 설정해야 합니다.
   * 추상 Bean은 `getBean()` 메서드를 통해 직접 접근하려고 하면 에러가 발생합니다. 이는 템플릿으로만 사용되기 때문입니다.
2. **자식 Bean의 클래스**:
   * 자식 Bean은 부모 Bean에서 정의된 클래스를 상속할 수도 있고, 필요에 따라 다른 클래스를 정의할 수도 있습니다. 이 경우 자식 클래스는 부모에서 상속된 프로퍼티 값과 호환 가능해야 합니다.
3. **ApplicationContext와 싱글톤**:
   * Spring의 `ApplicationContext`는 기본적으로 모든 싱글톤 Bean을 미리 인스턴스화합니다. 따라서 템플릿으로만 사용될 부모 Bean이 `abstract="true"`로 설정되지 않으면, Spring은 이를 인스턴스화하려고 시도할 수 있습니다.

Spring의 **Bean 정의 상속**은 부모-자식 관계로 설정된 Bean 정의 간에 속성 값을 상속하고, 필요한 경우 자식 Bean에서 재정의할 수 있는 강력한 기능입니다.

이를 통해 **코드 중복을 줄이고** 애플리케이션 설정을 **템플릿 방식**으로 관리할 수 있습니다.

`abstract` 속성은 부모 Bean을 템플릿으로만 사용하기 위해 설정해야 하며, 이를 통해 자식 Bean에서 상속받아 사용합니다
