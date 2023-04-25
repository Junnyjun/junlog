# Method

샘플 코드 작성

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

### Method

```java
Class<Sample> clazz = Sample.class;

// 전체 조회
Method[] methods = clazz.getMethods();

// 단일 조회
// Method data1 = clazz.getDeclaredMethod("data1");
// Method data2 = clazz.getDeclaredMethod("data1", String.class);
Method data1 = clazz.getMethod("data1");
Method data2 = clazz.getMethod("data1", String.class); ...
```

method 실행

```java
Sample sample = new Sample("1", "2");
Class<? extends Sample> clazz = sample.getClass();

// 인자 X
Method data1 = clazz.getMethod("data1");
Object invoke = data1.invoke(sample);

// 인자 O
Method data2 = clazz.getMethod("data3", String.class);
Object invoke1 = data2.invoke(sample, "3");
```
