# Monitor

자바의 모든 객체는 모니터를 가지고 있습니다.\
모니터는 여러 스레드가 동시에 접근하는 것을 막아주는 기능을 합니다.

```java
public class HelloMonitor implements Runnable {

    @Override
    public void run() {
        String hello = "hello";
        synchronized (hello) {
            hello += " world";
        }
    }
}
------------------------------------------------------------------------------
.... 
    MONITORENTER < 모니터 시작
   L0
    LINENUMBER 9 L0
    NEW java/lang/StringBuilder
    DUP
    INVOKESPECIAL java/lang/StringBuilder.<init> ()V
    ALOAD 1
    INVOKEVIRTUAL java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
    LDC " world"
    INVOKEVIRTUAL java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
    INVOKEVIRTUAL java/lang/StringBuilder.toString ()Ljava/lang/String;
    ASTORE 1
   L6
    LINENUMBER 10 L6
    ALOAD 2
    MONITOREXIT < 모니터 종료
....
```

이처럼 모니터를 가진 스레드는 해당 모니터를 가지는 객체에 락을 걸 수 있다.

<img src="../../../.gitbook/assets/file.excalidraw (29).svg" alt="" class="gitbook-drawing">

해당 monitor의 라이프사이클은 `synchronized`에 의존한다
