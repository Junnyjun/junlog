# 컬렉션 프레임웍

### 1. 핵심 인터페이스 <a href="#1" id="1"></a>

#### Collection인터페이스 <a href="#collection" id="collection"></a>

List와 Set의 조상이다.

#### List <a href="#list" id="list"></a>

**순서가 있는** 데이터의 집합, 데이터의 **중복을 허용**한다.

예) 대기자 명단

구현 클래스 : ArrayList, LinkedList, Stack, Vector 등

#### Set <a href="#set" id="set"></a>

**순서를 유지하지 않는** 데이터의 집합. 데이터의 **중복을 허용하지 않는**다.

예) 양의 정수집합, 소수의 집합

구현 클래스 : HashSet, TreeSet 등

#### Map <a href="#map" id="map"></a>

**키(key)와 값(value)의 쌍(pair)**으로 이루어진 데이터의 집합\
순서는 유지되지 않으며, 키는 중복을 허용하지 않고, 값은 중복을 허용한다.

예) 우편번호, 지역번호(전화번호)

구현 클래스 : HashMap, TreeMap, Hashtable, Properties 등

#### Map.Entry <a href="#mapentry" id="mapentry"></a>

key-value쌍을 다루기 위해 Map인터페이스의 내부에 정의되어 있다.

> Vector나 Hashtable과 같은 기존의 컬렉션 클래스들은 호환을 위해, 설계를 변경해서 남겨두었지만 가능하면 사용하지 않는 것이 좋다.



### 2. ArrayList <a href="#2-arraylist" id="2-arraylist"></a>

기존의 Vector를 개선한 것이다.

배열을 이용한 자료구조는 데이터를 읽어오고 저장하는 데는 효율이 좋지만, 용량을 변경해야 할 때는 새로운 배열을 생성한 후 기존의 배열로부터 새로 생성된 배열로 데이터를 복사해야하기 때문에 상당히 효율이 떨어진다.

#### 배열의 단점 <a href="#undefined" id="undefined"></a>

1. 크기를 변경할 수 없다.
2. 비순차적인 데이터의 추가 또는 삭제에 시간이 많이 걸린다.



### 3. LinkedList <a href="#3-linkedlist" id="3-linkedlist"></a>

위의 배열의 단점을 보완하기 위해 링크드 리스트라는 자료구조가 고안되었다.\
또한 링크드 리스트의 단점인 낮은 접근성을 보완하기 위해 더블 링크드 리스트가 고안되었다.\
LinkedList클래스는 더블 링크드 리스트로 구현되었다.

| 컬렉션        | 읽기(접근시간) | 추가 / 삭제 | 비고                            |
| ---------- | -------- | ------- | ----------------------------- |
| ArrayList  | 빠르다      | 느리다     | 순차적인 추가삭제는 더 빠름. 비효율적인 메모리 사용 |
| LinkedList | 느리다      | 빠르다     | 데이터가 많을수록 접근성이 떨어짐            |

> 다루고자 하는 데이터의 개수가 변하지 않는 경우라면, ArrayList가 최상의 선택이 되겠지만, 데이터 개수의 변경이 잦다면 linkedList를 사용하는 것이 더 나은 선택이 될 것이다.



### 4. Stack과 Queue <a href="#4-stack-queue" id="4-stack-queue"></a>

순차적으로 데이터를 추가하고 삭제하는 스택에는 ArrayList와 같은 배열기반의 컬렉션클래스로 구현하는 것이 적합하다.

큐는 데이터의 추가/삭제가 쉬운 LinkedList로 구현하는 것이 적합하다.

자바에서는 스택을 Stack클래스로 구현하여 제공하지만 큐는 Queue인터페이스로만 정의해 놓았기 때문에 Queue인터페이스를 구현한 클래스들을 사용하여야 한다.

> Stack은 컬렉션 프레임웍 이전부터 존재하던 것이기 때문에 Vector로 구현되어 있다.

#### PriorityQueue <a href="#priorityqueue" id="priorityqueue"></a>

저장한 순서에 관계없이 우선순위가 높은 것부터 꺼낸다.

저장공간으로 배열을 사용하며, 각 요소를 '힙(heap)'이라는 자료구조의 형태로 저장하다.

#### Deque <a href="#deque" id="deque"></a>

한 쪽 끝으로만 추가/삭제할 수 있는 Queue와 달리, 양쪽 끝에 추가/삭제가 가능하다.\
구현체로는 ArrayDeque와 LinkedList가 있다.

덱은 스택과 큐를 하나로 합쳐놓은 것과 같다.



### 5. Iterator, ListIterator, Enumeration <a href="#5-iterator-listiterator-enumeration" id="5-iterator-listiterator-enumeration"></a>

컬렉션에 저장된 요소를 접근하는데 사용되는 인터페이스이다.\
Enumration은 Iterator의 구버전이며, ListIterator는 Iterator의 기능을 향상 시킨 것이다.

#### Iterator <a href="#iterator" id="iterator"></a>

컬렉션에 저장된 각 요소에 접근하는 기능을 가진 Iterator인터페이스를 정의하고, Collection인터페이스에는 Iterator를 반환하는 iterator()를 정의하고 있다.

iterator()는 Collection인터페이스에 정의된 메서드이므로 Collection인터페이스의 자손인 List와 Set에도 포함되어 있다.

Map인터페이스는 iterator()를 직접 호출할 수 ㅇ벗고, 그 대신 keySet()이나 entrySet()과 같은 메서드를 통해 Set의 형태로 얻어 온 후 iterator()를 호출해야 한다.

#### Enumeration <a href="#enumeration" id="enumeration"></a>

이전 버전으로 작성된 소스와의 호환을 위해서 남겨 두고 있다.

#### ListIterator <a href="#listiterator" id="listiterator"></a>

컬렉션의 요소에 접근할 때 Iterator는 단방향으로만 이동할 수 있는데 ListItertor는 양방향으로의 이동이 가능하다.

다만 ArrayList나 LinkedList와 같이 List인터페이스를 구현한 컬렉션에서만 사용가능하다.



### 6. Arrays <a href="#6-arrays" id="6-arrays"></a>

배열을 다루는데 유용한 메서드가 정의되어 있다.

* 배열의 복사 - copyOf(), copyOfRange()
* 배열 채우기 - fill(), setAll()
* 배열의 정렬과 검색 - sort(), binarySearch()
* 문자열의 비교와 출력 - equals(), toString()
* 배열을 List로 변환 - asList(Object... a)
* parallelXXX(), spliterator(), stream()



### 7. Comparator와 Comparable <a href="#7-comparator-comparable" id="7-comparator-comparable"></a>

Comparable : 기본 정렬기준을 구현하는데 사용\
Comparator : 기본 정렬기준 외에 다른 기준으로 정렬하고자할 때 사용



### 8. HashSet & HashMap(Hashtable) <a href="#8-hashset--hashmaphashtable" id="8-hashset--hashmaphashtable"></a>

해싱을 이용해서 구현했다.

Hashtable과 HashMap의 관계는 Vector와 ArrayList의 관계와 같다.\
HashMap을 사용하자.



### 9. TreeSet & TreeMap <a href="#9-treeset--treemap" id="9-treeset--treemap"></a>

Red-Black tree를 이용해서 구현했다.

검색에 관한한 대부분의 경우에서 Hash가 Tree보다 더 뛰어나다.\
다만 범위검색이나 정령이 필요한 경우 Tree를 사용하자.



### 10. Properties <a href="#10-properties" id="10-properties"></a>

Hashtable을 상속받아 구현한 것이다.\
키와 값을 (String, String)의 형태로 저장하는 컬렉션 클래스이다.

주로 애플리케이션의 환경설정과 관련된 속성(property)을 저장하는데 사용되며 데이터를 파일로부터 읽고 쓰는 편리한 기능을 제공한다.



### 11. Collections <a href="#11-collections" id="11-collections"></a>

컬렉션과 관련된 메서드를 제공한다.

#### 컬렉션의 동기화 <a href="#undefined" id="undefined"></a>

멀티 쓰레드 프로그래밍에서는 데이터의 일관성을 유지하기 위해서는 공유되는 객체의 동기화가 필요하다.

Vector와 Hashtable같은 구버전의 클래스들은 자체적으로 동기화 처리가 되어 있는데, 멀타쓰레드 프로그래밍이 아닌 경우 성능저하의 원인이 된다.

Collections클래스에는 아래와 같은 동기화 메서드를 제공하고 있으므로, 필요할 때 해당하는 것을 사용하면 된다.

```java
static Collection	synchronizedCollection(Collection c);
static List			synchronizedList(List list);
static Set			synchronizedSet(Set s);
static Map			synchronizedMap(Map m);
static SortedSet	synchronizedSortedSet(SortedSet s);
static SortedMap	synchronizedSortedMap(SortedMap m);
```

#### 변경불가 컬렉션 만들기 <a href="#undefined" id="undefined"></a>

컬렉션에 저장된 데이터를 보호하기 위해서 컬렉션을 변경할 수 없게, 즉 읽기전용으로 만들어야할 때 사용된다.

```java
static Collection	unmodifiableCollection(Collection c);
static List			unmodifiableList(List list);
static Set			unmodifiableSet(Set s);
static Map			unmodifiableMap(Map m);
static SortedSet	unmodifiableSortedSet(SortedSet s);
static SortedMap	unmodifiableSortedMap(SortedMap m);
```

#### 싱글톤 컬렉션 만들기 <a href="#undefined" id="undefined"></a>

매개변수로 저장할 요소를 지정하면, 해당 요소를 저장하는 컬렉션을 반환한다. 그리고 반환된 컬렉션은 변경할 수 없다.

```java
static List			singletonList(Object o);
static Set			singletonSet(Object o)
static Map			singletonMap(Object key, Object value);
```

#### 한종류의 객체만 저장하는 컬렉션 만들기 <a href="#undefined" id="undefined"></a>

컬렉션에 저장할 요소의 타입을 제한하는 것은 제네릭스로 간단히 처리할 수 있는데도 메서드들을 제공하는 이유는 구버전에 작성된 코드들과의 호환성 때문이다. 여기서는 따로 적지 않겠다.



### 12. 컬렉션 클래스 요약 <a href="#12" id="12"></a>

| 컬렉션                | 특징                                                          |
| ------------------ | ----------------------------------------------------------- |
| ArrayList          | 배열기반. 데이터의 추가와 삭제에 불리. 순차적인 추가/삭제는 제일 빠름. 임의의 요소에 접근성이 뛰어남. |
| LinkedList         | 연결기반. 데이터의 추가와 삭제에 유리. 임의의 요소에 대한 접근성이 좋지 않다.               |
| HashMap            | 배열과 연결이 결합된 형태. 추가, 삭제, 검색, 접근성이 모두 뛰어남. 검색에는 최고성능을 보인다.    |
| TreeMap            | 연결기반. 정렬과 검색(특히 범위검색)에 적합. 검색 성능은 HashMap보다 떨어짐.            |
| Stack              | Vector를 상속받아 구현. FILO                                       |
| Queue              | ㅣLinkedListrk Queue인터페이스를 구현. FIFO                          |
| Properties         | Hashtable을 상속받아 구현                                          |
| HashSet            | HashMap을 이용해서 구현                                            |
| TreeSet            | TreeMap을 이용해서 구현                                            |
| LinkedHashMap(Set) | HashMap(Set)에 저장순서 유지기능을 추가                                 |
