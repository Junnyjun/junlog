# 객체 관계형 동작 패턴

#### 1. Unit of Work

**Unit of Work**는 데이터베이스와의 상호작용을 관리하기 위한 중요한 패턴으로, 비즈니스 트랜잭션 동안 객체의 모든 변경 사항을 추적하고, 이를 한꺼번에 데이터베이스에 적용하는 역할을 합니다. 이 패턴은 데이터베이스 호출의 빈도를 줄이고, 트랜잭션의 일관성을 유지하며, 충돌이 발생할 가능성을 줄입니다.

**동작 방식**

**Unit of Work**의 핵심 개념은 트랜잭션 단위로 객체의 변경 사항을 그룹화하여 관리하는 것입니다. 이를 통해, 트랜잭션이 완료될 때까지 모든 변경 사항을 추적하고, 필요한 경우 한꺼번에 데이터베이스에 적용할 수 있습니다. 이 과정은 주로 다음과 같이 이루어집니다:

1. **객체 등록과 변경 사항 관리**:
   * **Unit of Work**는 비즈니스 트랜잭션이 시작될 때 생성됩니다.
   * 이 트랜잭션 동안 생성된 객체는 `newObjects` 리스트에, 수정된 객체는 `dirtyObjects` 리스트에, 삭제된 객체는 `removedObjects` 리스트에 각각 등록됩니다.
   * 각 객체는 이러한 리스트에 등록될 때, 이미 등록된 상태를 확인하여 중복 등록을 방지합니다.
2. **트랜잭션 커밋**:
   * 트랜잭션이 완료될 때, **Unit of Work**는 `commit()` 메서드를 통해 모든 변경 사항을 데이터베이스에 적용합니다.
   * 이 과정에서는 먼저 `newObjects` 리스트에 있는 객체를 데이터베이스에 삽입하고(`insert`), `dirtyObjects` 리스트에 있는 객체를 갱신하며(`update`), `removedObjects` 리스트에 있는 객체를 삭제합니다(`delete`).
   * 이때 각 객체는 그에 상응하는 데이터 매퍼(Data Mapper)와 상호작용하여 데이터베이스에 변경 사항을 반영합니다.
3. **동시성 제어와 일관성 유지**:
   * **Unit of Work**는 트랜잭션이 진행되는 동안 동시성 문제를 처리합니다.
   * 이를 위해 `Optimistic Offline Lock` 또는 `Pessimistic Offline Lock` 같은 동시성 제어 메커니즘을 사용하여 데이터베이스와 객체의 일관성을 유지합니다.
   * 이러한 메커니즘을 통해 동일한 데이터에 대해 여러 트랜잭션이 동시에 발생하는 경우에도 충돌을 최소화하고, 데이터의 무결성을 보장할 수 있습니다.

**객체 등록 방식**

**Unit of Work**에서 객체를 등록하는 방법에는 두 가지가 있습니다:

1. **Caller Registration (명시적 등록)**:
   * 객체를 사용하는 코드는 명시적으로 객체를 **Unit of Work**에 등록해야 합니다.
   * 이는 코드가 명시적으로 `registerNew()`, `registerDirty()` 또는 `registerRemoved()` 메서드를 호출하여 객체의 상태를 **Unit of Work**에 전달해야 함을 의미합니다.
   * 이 방식은 코드가 객체의 상태를 명확하게 관리할 수 있도록 해 주지만, 개발자가 객체를 등록하는 것을 잊어버릴 경우 문제가 발생할 수 있습니다.
2. **Object Registration (자동 등록)**:
   * 객체 자체가 자신의 상태를 **Unit of Work**에 자동으로 등록합니다.
   * 예를 들어, 객체의 상태가 변경될 때마다 객체는 스스로 **Unit of Work**에 등록될 수 있습니다.
   * 이는 개발자의 실수를 줄일 수 있지만, 객체와 **Unit of Work** 간의 강한 결합을 초래할 수 있습니다.

**사용 시기**

**Unit of Work**는 복잡한 비즈니스 로직이 포함된 시스템에서 매우 유용합니다. 특히 다음과 같은 경우에 사용됩니다:

* **트랜잭션 내에서 다수의 객체가 변경되는 경우**:
  * 많은 객체가 수정되고, 생성되며, 삭제되는 복잡한 트랜잭션에서는 각 객체의 변경 사항을 관리하고, 이를 한 번에 커밋하는 것이 중요합니다.
* **일관성 유지가 중요한 경우**:
  * 동시성 문제가 발생할 가능성이 높은 시스템에서는 **Unit of Work**를 사용하여 데이터베이스의 일관성을 유지하고, 충돌을 최소화할 수 있습니다.
* **다양한 데이터 매핑 기술을 사용하는 경우**:
  * 여러 데이터 매퍼가 데이터베이스와 상호작용하는 복잡한 시스템에서는 **Unit of Work**가 다양한 매퍼를 조정하여 일관된 데이터베이스 작업을 수행할 수 있습니다.

**예제: Java로 구현된 Unit of Work**

Java로 구현된 **Unit of Work**는 객체의 상태를 추적하고, 이를 데이터베이스에 반영하는 방식으로 동작합니다. 다음은 그 구현 예제입니다:

```java
class UnitOfWork {
    private List<DomainObject> newObjects = new ArrayList<>();
    private List<DomainObject> dirtyObjects = new ArrayList<>();
    private List<DomainObject> removedObjects = new ArrayList<>();
    
    public void registerNew(DomainObject obj) {
        assert obj.getId() != null : "ID cannot be null";
        assert !dirtyObjects.contains(obj) : "Object cannot be both new and dirty";
        assert !removedObjects.contains(obj) : "Object cannot be removed";
        assert !newObjects.contains(obj) : "Object is already registered as new";
        newObjects.add(obj);
    }
    
    public void registerDirty(DomainObject obj) {
        assert obj.getId() != null : "ID cannot be null";
        assert !removedObjects.contains(obj) : "Object cannot be dirty and removed";
        if (!dirtyObjects.contains(obj) && !newObjects.contains(obj)) {
            dirtyObjects.add(obj);
        }
    }
    
    public void registerRemoved(DomainObject obj) {
        assert obj.getId() != null : "ID cannot be null";
        newObjects.remove(obj);
        dirtyObjects.remove(obj);
        if (!removedObjects.contains(obj)) {
            removedObjects.add(obj);
        }
    }
    
    public void commit() {
        insertNew();
        updateDirty();
        deleteRemoved();
    }
    
    private void insertNew() {
        for (DomainObject obj : newObjects) {
            MapperRegistry.getMapper(obj.getClass()).insert(obj);
        }
    }
    
    private void updateDirty() {
        for (DomainObject obj : dirtyObjects) {
            MapperRegistry.getMapper(obj.getClass()).update(obj);
        }
    }
    
    private void deleteRemoved() {
        for (DomainObject obj : removedObjects) {
            MapperRegistry.getMapper(obj.getClass()).delete(obj);
        }
    }
}
```

이 예제에서 **Unit of Work**는 `newObjects`, `dirtyObjects`, `removedObjects` 리스트를 사용하여 객체의 상태를 추적하고, `commit()` 메서드를 통해 데이터베이스에 변경 사항을 반영합니다.

***

#### 2. Identity Map

**Identity Map** 패턴은 동일한 데이터베이스 레코드를 여러 번 로드하지 않도록 보장하며, 객체의 일관성과 데이터베이스 성능을 향상시키는 데 사용됩니다. 이는 객체를 로드할 때마다 고유한 ID를 기준으로 객체를 맵에 저장하여 중복 로드를 방지합니다.

**동작 방식**

**Identity Map**은 객체가 처음 로드될 때 해당 객체를 고유 ID를 사용하여 맵에 저장하고, 이후 동일한 객체를 요청할 때 맵에서 해당 객체를 반환하는 방식으로 작동합니다. 주요 동작 방식은 다음과 같습니다:

1. **객체 저장**:
   * 데이터베이스에서 객체가 로드되면, **Identity Map**은 이 객체를 고유 ID와 함께 맵에 저장합니다.
   * 이때, 객체의 고유 ID는 주로 데이터베이스 테이블의 기본 키를 사용합니다.
2. **객체 조회**:
   * 동일한 객체가 다시 필요할 때, **Identity Map**은 데이터베이스로부터 객체를 다시 로드하는 대신, 맵에서 해당 객체를 반환합니다.
   * 이를 통해 데이터베이스 호출을 최소화하고, 성능을 최적화할 수 있습니다.
3. **맵의 구조**:
   * **Identity Map**은 보통 데이터베이스 테이블마다 별도의 맵을 생성합니다.
   * 이 맵은 객체의 타입에 따라 다르게 구성될 수 있으며, 각 맵은 특정 테이블 또는 객체 타입에 해당하는 객체만을 관리합니다.

**사용 시기**

**Identity Map** 패턴은 다음과 같은 경우에 특히 유용합니다:

* **중복 데이터 로드를 방지할 때**:
  * 동일한 데이터베이스 레코드를 여러 번 로드하지 않도록 보장하고, 메모리와 성능을 최적화할 수 있습니다.
* **객체 일관성을 유지할 때**:
  * 여러 객체가 동일한 데이터베이스 레코드를 참조하는 상황에서, 동일한 객체가 여러 인스턴스로 존재하지 않도록 보장합니다.
* **캐싱을 활용할 때**:
  * 자주 참조되는 데이터를 캐싱하여 데이터베이스 호출

을 줄이고, 시스템의 전반적인 성능을 향상시킬 수 있습니다.

**예제: Java로 구현된 Identity Map**

Java로 구현된 **Identity Map**은 다음과 같이 객체를 관리합니다:

```java
public class IdentityMap {
    private static Map<Long, Person> people = new HashMap<>();

    public static void addPerson(Person person) {
        people.put(person.getId(), person);
    }

    public static Person getPerson(Long id) {
        return people.get(id);
    }

    public static Person getPerson(long id) {
        return getPerson(Long.valueOf(id));
    }
}
```

이 예제에서 **Identity Map**은 `people` 맵을 사용하여 `Person` 객체를 고유 ID와 함께 관리합니다. `getPerson()` 메서드를 통해 동일한 `Person` 객체가 여러 번 로드되는 것을 방지합니다.

***

#### 3. Lazy Load

**Lazy Load**는 객체가 필요로 하는 데이터를 초기화할 때 즉시 로드하지 않고, 실제로 데이터가 필요해졌을 때 로드하는 패턴입니다. 이 패턴은 메모리 사용량을 최적화하고, 초기 로딩 시간을 줄이는 데 유용합니다.

**동작 방식**

**Lazy Load** 패턴은 다음과 같은 다양한 방법으로 구현될 수 있습니다:

1. **지연 초기화 (Lazy Initialization)**:
   * 필드가 처음으로 접근될 때 데이터를 로드하는 가장 단순한 형태입니다.
   * 객체의 필드가 `null`인지 확인하고, `null`인 경우 데이터를 로드하여 초기화합니다.
2. **가상 프록시 (Virtual Proxy)**:
   * 실제 객체 대신 프록시 객체를 반환하고, 이 프록시 객체가 필요해질 때 실제 데이터를 로드합니다.
   * 이 방식은 메모리 사용을 줄이고 초기 로딩 시간을 단축할 수 있지만, 객체 식별 문제를 발생시킬 수 있습니다.
3. **유령 객체 (Ghost Object)**:
   * 유령 객체는 데이터베이스에서 최소한의 정보만 로드된 상태로 존재하다가, 필요한 데이터가 참조될 때 전체 데이터를 로드합니다.
   * 유령 객체는 메모리 사용량을 줄이는 데 유용하지만, 모든 액세스 시 데이터 로딩을 수행해야 하므로 주의가 필요합니다.
4. **값 홀더 (Value Holder)**:
   * 값 홀더는 객체의 데이터를 지연 로딩할 수 있는 래퍼 객체로, 데이터가 실제로 필요할 때만 로드하여 반환합니다.
   * 이는 객체의 필드를 직접 관리하지 않고, 값을 지연 초기화할 수 있도록 지원합니다.

**사용 시기**

**Lazy Load** 패턴은 다음과 같은 경우에 사용됩니다:

* **대용량 데이터 로드**:
  * 초기 로딩 시 모든 데이터를 메모리에 로드하는 것이 비효율적일 때 **Lazy Load**를 사용하여 필요한 데이터만 로드합니다.
* **성능 최적화**:
  * 데이터베이스 호출을 줄이고, 초기 로딩 시간을 단축하기 위해 **Lazy Load**를 사용하여 성능을 최적화합니다.
* **메모리 절약**:
  * 메모리 사용량을 줄이기 위해 필요할 때만 데이터를 로드하는 **Lazy Load**를 활용할 수 있습니다.

**예제: Java로 구현된 Lazy Load**

Java로 구현된 **Lazy Load**의 예제는 다음과 같습니다:

```java
class Supplier {
    private List<Product> products;

    public List<Product> getProducts() {
        if (products == null) {
            products = Product.findForSupplier(getId());
        }
        return products;
    }
}
```

이 예제에서 `Supplier` 클래스는 `getProducts()` 메서드를 통해 `products` 리스트를 처음으로 접근할 때 데이터를 로드합니다. 이를 통해 불필요한 초기 로딩을 방지하고, 필요할 때만 데이터를 로드할 수 있습니다.

또 다른 방법으로는 가상 프록시를 사용하는 것입니다:

```java
class VirtualList implements List<Product> {
    private List<Product> source;
    private VirtualListLoader loader;

    public VirtualList(VirtualListLoader loader) {
        this.loader = loader;
    }

    private List<Product> getSource() {
        if (source == null) {
            source = loader.load();
        }
        return source;
    }

    // List 인터페이스의 메서드들을 구현하여 getSource()에 위임합니다.
}
```

이 가상 프록시 예제에서는 `VirtualList`가 실제 데이터를 로드할 때 `loader`를 사용합니다. 리스트의 메서드들이 호출될 때 `getSource()`를 통해 필요한 시점에 데이터를 로드합니다.
