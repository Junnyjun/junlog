# Thread Local

Java Thread Local은 멀티스레드 환경에서 각 스레드에서 고유한 값을 가지도록 하는 기능입니다. 이를 통해 여러 스레드에서 공유되는 객체에 대한 동시 접근 문제를 방지할 수 있습니다.

### 사용 방법

Thread Local을 사용하려면 java.lang 패키지의 ThreadLocal 클래스를 이용해야 합니다. 각 스레드에서 고유한 값을 가지도록 하려는 객체를 생성한 다음, ThreadLocal 객체를 생성하여 해당 객체를 저장하면 됩니다.

```
public class MyThreadSafeClass {
    private ThreadLocal<Integer> threadLocalValue = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return 0;
        }
    };

    public void incrementValue() {
        threadLocalValue.set(threadLocalValue.get() + 1);
    }

    public int getValue() {
        return threadLocalValue.get();
    }
}

```

위의 코드에서는 MyThreadSafeClass 클래스 내부에서 ThreadLocal 객체를 생성하여 Integer 값을 저장합니다. initialValue() 메소드를 오버라이드하여, 스레드가 값에 처음으로 접근할 때 0을 반환하도록 설정합니다.

incrementValue() 메소드는 Thread Local에 저장된 값을 증가시키고, getValue() 메소드는 Thread Local에 저장된 값을 반환합니다. 이렇게 구현하면 여러 스레드에서 MyThreadSafeClass 객체를 공유하더라도 각 스레드에서는 고유한 값을 가지게 됩니다.

### 활용 예시

Thread Local은 멀티스레드 환경에서 많이 사용되는 기능 중 하나입니다. 예를 들어, 웹 애플리케이션에서 각 요청마다 로그인한 사용자 정보를 유지하고 싶을 때 Thread Local을 활용할 수 있습니다.

```
public class UserContext {
    private static final ThreadLocal<User> userThreadLocal = new ThreadLocal<>();

    public static void setUser(User user) {
        userThreadLocal.set(user);
    }

    public static User getUser() {
        return userThreadLocal.get();
    }
}

```

위의 코드에서는 UserContext 클래스 내부에서 Thread Local을 이용하여 로그인한 사용자 정보를 저장합니다. 각 요청마다 UserContext.setUser() 메소드를 호출하여 로그인한 사용자 정보를 저장하면, 해당 요청에서는 UserContext.getUser() 메소드를 호출하여 로그인한 사용자 정보를 얻을 수 있습니다.

Java Thread Local은 멀티스레드 환경에서 공유 변수에 대한 동시 접근 문제를 해결에에 유용
