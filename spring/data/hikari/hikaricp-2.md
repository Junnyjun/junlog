# HikariCP의 최적화된 컬렉션

### FastList란?

FastList는 HikariCP에서 사용하는 고성능 리스트 구현체로, 자바의 표준 ArrayList를 대체하기 위해 설계되었습니다. 클래스 선언을 살펴보면 다음과 같습니다:

```
public final class FastList<T> implements List<T>, RandomAccess, Serializable
```

이 클래스는 다음과 같은 특징을 갖고 있습니다:

* `final` 클래스로 선언되어 상속이 불가능합니다.
* 제네릭 타입 를 지원합니다. `<T>`
* , , 인터페이스를 구현합니다. `ListRandomAccessSerializable`

### FastList의 설계 목적

클래스 주석에서 명확하게 드러나듯이, FastList는 "범위 확인(range checking)이 없는 빠른 리스트"를 목표로 합니다. 이는 성능을 극대화하기 위해 안전성 검사를 최소화했다는 의미입니다. 작성자인 Brett Wooldridge가 성능에 초점을 맞추고 설계했음을 알 수 있습니다.

### 내부 구조

FastList의 내부 구조는 매우 단순합니다:

```
private final Class<?> clazz;
private T[] elementData;
private int size;
```

* : 저장할 요소의 클래스 타입 `clazz`
* : 요소를 저장하는 배열 `elementData`
* : 현재 리스트에 있는 요소의 수 `size`

이 단순한 구조는 불필요한 오버헤드를 제거하고 직접적인 배열 접근을 가능하게 합니다.

### 특별한 생성자

FastList는 두 가지 생성자를 제공합니다:

```
public FastList(Class<?> clazz)
public FastList(Class<?> clazz, int capacity)
```

두 생성자 모두 파라미터를 요구하는데, 이는 매우 중요한 차별점입니다. 일반적인 ArrayList는 내부적으로 Object\[] 배열을 사용하지만, FastList는 전달받은 클래스 타입을 사용하여 타입 특화된 배열을 생성합니다: `clazz`

```
this.elementData = (T[]) Array.newInstance(clazz, capacity);
```

이 접근 방식으로 인해 타입 캐스팅 오버헤드를 줄이고 JVM의 최적화 기회를 증가시킵니다.

### 핵심 메서드 분석

#### add() 메서드

```
@Override
public boolean add(T element)
{
   if (size < elementData.length) {
      elementData[size++] = element;
   }
   else {
      // overflow-conscious code
      final var oldCapacity = elementData.length;
      final var newCapacity = oldCapacity << 1;
      @SuppressWarnings("unchecked")
      final var newElementData = (T[]) Array.newInstance(clazz, newCapacity);
      System.arraycopy(elementData, 0, newElementData, 0, oldCapacity);
      newElementData[size++] = element;
      elementData = newElementData;
   }

   return true;
}
```

이 메서드는 다음과 같은 최적화를 포함합니다:

* 배열에 여유 공간이 있으면 단순히 배열에 요소를 추가하고 를 증가시킵니다. `size`
* 배열이 가득 차면 현재 용량의 2배()로 새 배열을 생성합니다. `oldCapacity << 1`
* 새 배열로 요소를 복사할 때 효율적인 `System.arraycopy()`를 사용합니다.

#### get() 메서드

```java
@Override
public T get(int index)
{
   return elementData[index];
}
```

표준 ArrayList의 get() 메서드는 인덱스 범위를 확인하지만, FastList는 그러한 검사를 생략하여 최대한의 성능을 제공합니다. 이는 클래스 주석에서 언급한 "범위 확인이 없는 빠른 리스트"라는 목표와 일치합니다.

#### removeLast() 메서드

```java
public T removeLast()
{
   T element = elementData[--size];
   elementData[size] = null;
   return element;
}
```

리스트의 마지막 요소를 효율적으로 제거하는 특수 목적 메서드입니다. ArrayList에는 없는 이 메서드는 스택처럼 사용될 때 유용합니다.

#### remove(Object) 메서드

```java
@Override
public boolean remove(Object element)
{
   for (var index = size - 1; index >= 0; index--) {
      if (element == elementData[index]) {
         final var numMoved = size - index - 1;
         if (numMoved > 0) {
            System.arraycopy(elementData, index + 1, elementData, index, numMoved);
         }
         elementData[--size] = null;
         return true;
      }
   }

   return false;
}
```

여기서 주목할 점은:

* 뒤에서부터 검색하여 마지막에 추가된 요소를 먼저 제거하도록 최적화되었습니다.
* 비교는 참조 동일성(reference equality)을 사용합니다. 이는 메서드를 사용하는 ArrayList와 다릅니다. `element == elementData[index]equals()`
* 한 번에 하나의 요소만 제거합니다 (첫 번째 일치 항목).

### 미구현된 메서드들

FastList의 코드를 살펴보면 많은 메서드가 `UnsupportedOperationException`을 던지도록 구현되어 있습니다:&#x20;

```
@Override
public boolean contains(Object o)
{
   throw new UnsupportedOperationException();
}
```

이는 HikariCP가 실제로 사용하지 않는 기능을 제외함으로써 코드 크기를 줄이고 유지보수를 단순화하는 접근 방식입니다.

* 불필요한 기능의 구현과 테스트에 드는 비용을 절약합니다.
* 클래스의 책임을 명확하게 제한합니다.
* 실수로 지원되지 않는 작업을 사용하려고 할 때 명확한 오류를 제공합니다.

### 이터레이터 구현

FastList는 기본 이터레이터를 간결하게 구현했습니다:

```
@Override
public Iterator<T> iterator()
{
   return new Iterator<>() {
      private int index;

      @Override
      public boolean hasNext()
      {
         return index < size;
      }

      @Override
      public T next()
      {
         if (index < size) {
            return elementData[index++];
         }

         throw new NoSuchElementException("No more elements in FastList");
      }
   };
}
```

이 구현은 내부 배열에 직접 접근하므로 매우 효율적입니다. 그러나 더 복잡한 는 지원하지 않습니다. `ListIterator`

### FastList vs ArrayList

FastList와 ArrayList의 주요 차이점을 비교해보겠습니다:

| 기능     | FastList       | ArrayList     |
| ------ | -------------- | ------------- |
| 범위 검사  | 없음 (성능 향상)     | 있음 (안전성 향상)   |
| 요소 비교  | 참조 동일성 (==)    | equals() 메서드  |
| 배열 타입  | 구체적인 타입 배열     | Object\[] 배열  |
| 지원 메서드 | 제한적            | 전체 List 인터페이스 |
| 확장 정책  | 2배 (비트 시프트 사용) | 1.5배          |
| 최적화 대상 | 뒤에서부터 검색/제거    | 일반적인 사용 패턴    |

### 성능 최적화 기법

FastList에 사용된 주요 최적화 기법들을 살펴보겠습니다:

#### 타입 특화 배열

제네릭 타입 소거 문제를 해결하기 위해 클래스 리터럴을 사용하여 특정 타입의 배열을 생성합니다:

```
(T[]) Array.newInstance(clazz, capacity)
```

이는 타입 캐스팅 횟수를 줄이고 JVM이 더 효율적으로 최적화할 수 있게 합니다.

#### 비트 연산을 통한 효율적인 크기 조정

새 용량을 계산할 때 곱셈 대신 비트 시프트를 사용합니다:

```
final var newCapacity = oldCapacity << 1; // 곱하기 2와 동일하지만 더 빠름
```

#### 불필요한 검사 제거

인덱스 범위 검사와 같은 불필요한 검사를 제거하여 실행 경로를 단순화합니다.

#### 직접 배열 접근

중간 메서드 호출 없이 배열 요소에 직접 접근합니다.

#### 메모리 관리 최적화

더 이상 참조하지 않는 요소를 null로 설정하여 메모리 누수를 방지합니다:

```
elementData[--size] = null;
```

### HikariCP에서의 활용

FastList는 HikariCP에서 다음과 같은 용도로 사용됩니다:

* **Statement 객체 관리**: PreparedStatement와 같은 JDBC 객체를 추적하고 관리합니다.
* **자원 관리**: 데이터베이스 연결과 관련된 다양한 자원을 효율적으로 관리합니다.
* **내부 큐 구현**: 특정 작업을 처리하기 위한 경량 큐로 사용됩니다.
