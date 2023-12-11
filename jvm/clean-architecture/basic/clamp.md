# Clamp

자바의 Clamp는 특정 값의 범위를 지정합니다.\
주어진 값이 특정 하한 및 상한 경계를 벗어나지 않도록 보장해주는 기능 입니다.

## Java 21 ↓

JAVA21 이전에는 Java에 값을 고정하는 내장 함수가 존재하지 않아 \
클램프함수를 직접 작성해야 했습니다.

<img src="../../../.gitbook/assets/file.excalidraw (2) (1).svg" alt="" class="gitbook-drawing">

`최소값`보다 작은값은 `최소값`으로 `최대값`보다 큰 값은 `최대값`으로 설정됩니다\
이 외는 스스로의 값을 가집니다

{% code title="Sample.java" overflow="wrap" %}
```java
class Clamp {
    int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
```
{% endcode %}

## Java 21 ↑

21에 새롭게 도입된 Math.clamp는 위와 같은 설정을 하지 않아도 값을 고정해줄 수 있습니다.

```java
class Clamp {
    int clamp2(int value, int min, int max) {
        return Math.clamp(value, min, max);
    }
}
```
