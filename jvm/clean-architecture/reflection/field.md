# Field

샘플 코드

```java
public class Sample {
    private String data1;
    private String data2;

    public Sample() {
    }

    public Sample(String data1, String data2) {
        this.data1 = data1;
        this.data2 = data2;
    }
    public String data1() {
        return data1;
    }
    public String data1(String raw) {
        return data1 + raw;
    }
}
```

### Field

```java
Sample sample = new Sample("1", "2");
Class<Sample> clazz = Sample.class;
// 전체 조회
Field[] fields = clazz.getFields();

// declared 는 public이 아닌 모든 필드를 조회한다.
Field data1 = clazz.getDeclaredField("data1");
```

필드 값 변경

```java
Sample sample = new Sample("1", "2");

Class<Sample> clazz = Sample.class;
Field data1 = clazz.getDeclaredField("data1");

data1.setAccessible(true);
data1.set(sample, "New");

System.out.println(sample.data1());
// New
```
