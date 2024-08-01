# Class loader

#### Java Class Loader와 Dynamic Loading

Java의 큰 특징 중 하나는 런타임 시에 동적으로 클래스를 읽어오는 Dynamic Loading입니다. 이는 모든 클래스가 참조되는 순간 동적으로 로드 및 링크가 이루어지는 방식입니다. Dynamic Loading은 Load Time Dynamic Loading과 Runtime Dynamic Loading으로 나뉩니다.

#### Load Time Dynamic Loading

Load Time Dynamic Loading은 특정 클래스가 로드될 때 관련된 클래스들을 함께 로드하는 방식을 말합니다. 예를 들어, 아래의 소스 코드에서 `Hello` 클래스는 `String` 객체를 `main()` 메서드의 파라미터로 사용하고 있으며, `System` 객체를 호출하고 있습니다. 이 경우 `Hello` 클래스가 `ClassLoader`에 의해 JVM으로 로드될 때 `java.lang.String` 클래스와 `java.lang.System` 클래스가 동시에 로드됩니다.

```java
public class Hello {
    public static void main(String[] args) {
        System.out.println("Hello EXEM");
    }
}
```

#### Runtime Dynamic Loading

Runtime Dynamic Loading은 객체를 참조하는 순간에 동적으로 로드하는 방식입니다. 아래의 소스 코드는 `Hello` 클래스에서 특정 클래스를 호출하는 내용으로 구성되어 있습니다. 어떤 클래스를 로드해야 할지는 인수로 넘어온 이후에나 알 수 있습니다. 즉, `Class.forName(args[0])`를 호출하는 순간에 `args[0]`에 해당하는 클래스를 로드할 수밖에 없는 상황이 됩니다.

```java
public class Hello {
    public static void main(String[] args) throws ClassNotFoundException {
        Class<?> cl = Class.forName(args[0]);
    }
}
```

#### ClassLoader 역할

ClassLoader는 JVM 내로 클래스를 로드하고 링크를 통해 적절히 배치하는 모듈입니다. JVM은 동일한 클래스를 중복해서 로드하지 않기 때문에 클래스 로드 전에 해당 클래스가 JVM에 이미 로드되어 있는지를 확인하는 과정이 필요합니다. 이 과정에서 클래스를 구별하는 방법은 클래스의 이름입니다.

ClassLoader는 네임스페이스를 사용하여 자신이 로드한 클래스의 정보를 저장합니다. 네임스페이스는 `ClassLoader`가 로드한 클래스의 풀 네임을 저장하며, 이를 통해 JVM은 동일한 클래스라도 서로 다른 `ClassLoader`가 로드한 경우 중복 로드할 수 있습니다.

#### Class Loader Delegation Model

여러 개의 ClassLoader는 계층 구조를 가지며, 각 ClassLoader는 부모 ClassLoader로부터 클래스를 로드하는 작업을 위임받습니다. 예를 들어, `SystemClassLoader`가 클래스를 로드하도록 요청받으면 `ExtensionClassLoader`와 `BootstrapClassLoader`를 통해 클래스를 찾고, 마지막으로 자신이 로드를 시도합니다.

![Class Loader의 위계 구조](https://i.imgur.com/XP8HTgn.png)

#### Class Loader의 종류

* **Bootstrap ClassLoader**: 가장 상위의 ClassLoader로, JVM 기동 시 가장 먼저 생성되며 기본 Java API를 로드합니다.
* **Extension ClassLoader**: `Bootstrap ClassLoader`를 부모로 하며, 확장 클래스들을 로드합니다.
* **Application ClassLoader**: 사용자 애플리케이션을 위한 ClassLoader로, `CLASSPATH`에 위치한 클래스를 로드합니다.
* **User Defined ClassLoader**: 사용자가 직접 정의하여 사용하는 ClassLoader입니다.

#### Class Loader의 동작 과정

Class Loader의 동작 과정은 크게 세 단계로 나뉩니다: 로드, 링크, 초기화.

1. **로드(Loading)**:
   * 클래스를 파일 시스템이나 네트워크 등에서 가져와 JVM 내로 로드합니다.
   * 예: 파일 형태의 클래스를 여러 가지 방법으로 획득하여 JVM에 로드합니다.
   *   예제:

       ```java
       public class CustomClassLoader extends ClassLoader {
           @Override
           public Class<?> findClass(String name) throws ClassNotFoundException {
               byte[] b = loadClassData(name);
               return defineClass(name, b, 0, b.length);
           }

           private byte[] loadClassData(String name) {
               // 클래스 파일 데이터를 읽어오는 로직 구현
               return new byte[0];
           }
       }
       ```
2.  **링크(Linking)**:

    * **검증(Verification)**: 클래스 파일의 유효성 검증.
      * Final 클래스가 슈퍼클래스가 아닌지 확인.
      * Final 클래스가 오버라이드되지 않았는지 확인.
      * 추상 클래스가 아닌 경우, 구현된 인터페이스의 모든 메서드가 구현되었는지 확인.
      * Constant Pool의 정보가 정합성이 맞는지 검증.
      * 바이트 코드의 정합성 및 완전성을 검증.
    * **준비(Preparation)**: 클래스의 메모리 할당 및 기본 값 설정.
      * 기본 변수 타입별로 초기값을 설정합니다.
      * 예: int는 0, float는 0.0f, 참조형은 null 등.
    * **해결(Resolution)**: 상징적 참조를 실제 메모리 주소로 변환합니다.
      * Constant Pool에 있는 클래스, 인터페이스, 메서드, 필드의 상징적 참조를 직접 참조로 변환합니다.

    ![Class Loader Work](https://i.imgur.com/lVbAX3I.png)
3. **초기화(Initialization)**:
   * 클래스 변수를 적절한 값으로 초기화하고, `<clinit>()` 메서드를 통해 초기화 작업을 수행합니다.
   * 클래스의 초기화 단계는 해당 클래스의 직계 슈퍼클래스가 초기화되지 않았으면 먼저 초기화하고, 그 후 클래스의 `<clinit>()` 메서드를 수행하여 초기화를 완료합니다.
   * 예: `public class Example { static { /* 초기화 블록 */ } }`

#### Class Sharing

Java 5부터 추가된 기능으로, JVM의 각 프로세스 사이에서 로드된 클래스를 공유하는 기능입니다. 이는 JVM의 기동 시간을 줄이고 메모리 사용을 최적화합니다. 예를 들어, 한 JVM이 로드한 클래스를 다른 JVM이 공유하여 재사용합니다.

**Class Sharing의 동작 방식**

* 한 JVM이 `rt.jar`와 같은 기본 클래스 파일들을 미리 로드해 놓으면, 다른 JVM은 이 로드된 클래스 파일들을 공유하여 사용합니다.
* 이는 메모리 사용을 줄이고 JVM 기동 시간을 단축시키는 데 기여합니다.

#### WAS에서의 Class Loader 구성

Web Application Server(WAS)에서 ClassLoader는 독립적이면서도 여러 계층으로 구성됩니다. 이를 통해 애플리케이션 간 클래스가 섞이지 않도록 하며, 모듈 간 클래스 공유를 가능하게 합니다.

![WAS의 Class Loader 구성](https://i.imgur.com/8TzGOWF.png)

**WebLogic과 JEUS의 Class Loader 구조**

* **WebLogic**: SystemClassLoader가 최상위 ClassLoader로, EJB 모듈과 WEB 모듈의 관계를 고립시켜 구성합니다.
  * EJB 모듈은 WEB 모듈보다 상위에 위치하며, 서로 독립적인 구조를 유지합니다.
* **JEUS**: SharedClassLoader와 IsolatedClassLoader 방식을 제공합니다.
  * **SharedClassLoader**: 최대한 클래스를 공유하는 방식으로, EJB 모듈이 Redeploy되면 관련된 모든 WEB 모듈의 ClassLoader를 다시 생성해야 하는 단점이 있습니다.
  * **IsolatedClassLoader**: 각 애플리케이션마다 고립된 ClassLoader 구조를 가지며, 성능상의 이점이 있습니다.

#### Class Loader 옵션

* **-Xbootclasspath**: Bootstrap ClassLoader의 검색 경로 설정.
* **-Djava.ext.dirs**: Extension ClassLoader의 검색 경로 설정.
* **-cp, -classpath**: SystemClassLoader의 검색 경로 설정.
* **-XX:+TraceClassLoading**: 로드된 모든 클래스를 보여주는 디버깅 옵션.
* **-XX:+TraceClassUnloading**: 언로드된 모든 클래스를 보여주는 옵션.
* **-XX:+TraceClassLoadingPreorder**: 참조 순서대로 로드된 모든 클래스를 보여주는 옵션.
* **-XX:+TraceClassResolution**: Constant Pool의 해결 정보를 보여주는 옵션.
* **-XX:+TraceLoaderConstraints**: ClassLoader의 제약 조건에 대한 기록을 보여주는 옵션.
* **-XX:+LazyBootClassLoader**: Bootstrap ClassLoader의 Classpath에 있는 파일들을 Lazy 방식으로 로드하는 옵션.

#### JVM 옵션 예제

*   기본 Java API보다 먼저 읽어들일 경로나 파일을 설정:

    ```shell
    java -Xbootclasspath/p:<경로> MyClass
    ```
*   SystemClassLoader의 검색 경로 설정:

    ```shell
    java -cp <경로> MyClass
    ```
*   클래스 로드 및 언로드 추적:

    ```shell
    java -XX:+TraceClassLoading -XX:+TraceClassUnloading MyClass
    ```

#### User Defined ClassLoader 예제

사용자가 정의한 ClassLoader를 통해 클래스 로드 동작을 커스터마이징할 수 있습니다.

```java
public class CustomClassLoader extends ClassLoader {
    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        byte

[] b = loadClassData(name);
        return defineClass(name, b, 0, b.length);
    }

    private byte[] loadClassData(String name) {
        // 클래스 파일 데이터를 읽어오는 로직 구현
        return new byte[0];
    }
}
```

이와 같이 Java의 ClassLoader와 Dynamic Loading에 대해 이해하고, 이를 활용한 다양한 설정과 예제를 통해 효율적인 애플리케이션 개발이 가능합니다. ClassLoader의 동작 원리와 다양한 옵션을 숙지함으로써, Java 애플리케이션의 성능과 유연성을 높일 수 있습니다.
