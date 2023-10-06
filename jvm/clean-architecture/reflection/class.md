# Class

{% code title="Target" %}
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
}
```
{% endcode %}

### Class

Get Class

```java
// Class.forName("com.example.demo.Sample");
Class<Sample> clazz = Sample.class;
```

내부 값 조회

```java
Field[] fields = clazz.getFields();
Annotation[] annotations = clazz.getAnnotations();
Method[] methods = clazz.getMethods();
Constructor<?>[] constructors = clazz.getConstructors();
```



E.G ) 빈생성자로 인스턴스 생셩

```java
Class<Sample> clazz = Sample.class;
Constructor<?> declaredConstructors = clazz.getDeclaredConstructors()[0];
Sample result =(Sample) declaredConstructors.newInstance();
```
