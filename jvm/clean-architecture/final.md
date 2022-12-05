# Final

## Final , Finalize , Finally

### Final

#### In class : 상속을 받을 수 없다

<pre class="language-java"><code class="lang-java">public final class JunnyLand {
    public void joinus(){}
}
<strong>class sample extends JunnyLand{ } &#x3C;&#x3C; X
</strong></code></pre>

#### In Method : 오버라이딩 할 수 없다

```java
public class JunnyLand {
    public final void joinus(){}
}

class sample extends JunnyLand{
    @Override
    public void joinus() {} << X
}
```

#### In Field : 재할당 할 수 없다&#x20;

```java
public class JunnyLand {
    private final Long hello= 1L;
    public void joinus(){
        hello = 2L; << X
    }
}

```

### Finally

try-catch이후에 항상 실행되는 블록을 지정해줍니다.

```java
public class JunnyLand {
    public void joinus(){
        try {
            System.out.println("Hello, JunnyLand!");
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            System.out.println("Bye, JunnyLand!");
        }
    }
}
```

### Finalize

GC가 더이상 참조하지 않는 객체를 메모리에서 참조 해제할 때 호출된다.\
GC시 하는일을 커스텀하는 경우 사용하지만, Deprecated 되었으며,\
AutoCloseable, try-catch-resource를 사용하는 방식을 추천한다

```java
public class JunnyLand extends Throwable{
    public JunnyLand() {
        super.finalize();
    }
}
```

