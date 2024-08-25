# 오프라인 동시성 패턴

**Optimistic Offline Lock**

**Optimistic Offline Lock** 패턴은 비즈니스 트랜잭션 간의 충돌 가능성을 낮게 보고, 충돌이 발생했을 때 트랜잭션을 롤백하여 데이터의 일관성을 유지하는 방법입니다. 이 패턴은 비관적 잠금(Pessimistic Offline Lock)과 달리, 데이터에 대한 접근을 제한하지 않고도 일관성을 유지할 수 있습니다.

**개요**

Optimistic Offline Lock은 각 레코드에 버전 번호를 부여하여 데이터를 관리합니다. 트랜잭션이 데이터를 수정하려 할 때, 이 버전 번호를 사용하여 다른 트랜잭션에서 동일한 데이터를 수정했는지 확인합니다. 트랜잭션이 커밋되기 전에 데이터베이스의 현재 버전 번호와 트랜잭션이 가지고 있는 버전 번호를 비교하여 충돌 여부를 판단합니다. 만약 버전 번호가 일치하지 않는다면, 충돌이 발생한 것으로 간주하고 트랜잭션을 롤백합니다.

**작동 방식**

* **버전 관리**: 각 레코드에는 고유한 버전 번호가 부여됩니다. 트랜잭션이 시작될 때 이 버전 번호가 세션에 저장되고, 트랜잭션이 종료될 때까지 유지됩니다.
* **충돌 감지**: 트랜잭션이 데이터를 수정하려 할 때, 저장된 버전 번호와 데이터베이스의 현재 버전 번호를 비교합니다. 두 번호가 일치하지 않으면, 다른 트랜잭션에서 이미 데이터를 수정했음을 의미하며, 트랜잭션은 롤백됩니다.
*   **SQL 문 활용**: 버전 번호는 `UPDATE` 또는 `DELETE` SQL 문에서 사용됩니다. 예를 들어, 다음과 같은 SQL 문을 사용할 수 있습니다:

    ```sql
    UPDATE customer SET name = ?, version = version + 1 WHERE id = ? AND version = ?
    ```

    이 SQL 문은 고객 데이터를 수정할 때, 버전 번호가 일치하는 경우에만 수정이 이루어지도록 합니다. 일치하지 않으면, 수정은 실패하고 트랜잭션은 롤백됩니다.

**실제 사례**

고객 데이터를 수정하는 예제를 통해 이 패턴을 더 잘 이해할 수 있습니다. 트랜잭션이 고객 데이터를 로드할 때, 해당 레코드의 버전 번호도 함께 저장됩니다. 트랜잭션이 완료될 때, 데이터베이스에 저장된 버전 번호와 트랜잭션에 저장된 버전 번호를 비교합니다. 버전 번호가 일치하면, 트랜잭션이 커밋되고, 그렇지 않으면 트랜잭션은 롤백됩니다.

```java
class CustomerService {
    public void updateCustomer(Customer customer) {
        String sql = "UPDATE customer SET name = ?, version = version + 1 WHERE id = ? AND version = ?";
        // SQL 실행 및 충돌 감지
        // 충돌이 발생하면 예외를 던짐
    }
}
```

이와 같은 방식으로 Optimistic Offline Lock은 데이터의 일관성을 유지하며, 충돌을 감지하여 트랜잭션을 안전하게 롤백합니다.

**Pessimistic Offline Lock**

**Pessimistic Offline Lock** 패턴은 충돌 가능성이 높거나 충돌 시 큰 비용이 발생할 수 있는 상황에서 사용됩니다. 이 패턴은 트랜잭션이 데이터를 수정하기 전에 잠금을 획득하여, 다른 트랜잭션이 해당 데이터에 접근하지 못하게 하는 방법입니다.

**개요**

비즈니스 트랜잭션이 시작되기 전에 데이터에 대한 잠금을 획득하며, 트랜잭션이 종료될 때까지 다른 트랜잭션이 해당 데이터에 접근하지 못하게 합니다. 이 방식은 충돌 가능성이 높은 상황에서 효과적이며, 충돌이 발생할 경우 사용자가 이를 인지하고 적절히 대처할 수 있도록 합니다.

**작동 방식**

* **잠금 유형**: 독점 쓰기 잠금과 독점 읽기 잠금을 포함하여, 다양한 잠금 유형이 존재합니다.
  * **독점 쓰기 잠금**: 트랜잭션이 데이터를 수정하기 전에 잠금을 획득하여, 다른 트랜잭션이 데이터를 읽거나 수정하지 못하게 합니다.
  * **독점 읽기 잠금**: 트랜잭션이 데이터를 읽기 전에 잠금을 획득하여, 다른 트랜잭션이 데이터를 수정하지 못하게 합니다.
* **Lock Manager**: 잠금을 관리하는 Lock Manager는 잠금을 요청하거나 해제하는 역할을 합니다.
* **잠금 해제**: 트랜잭션이 종료되면 잠금을 해제하여, 다른 트랜잭션이 데이터에 접근할 수 있도록 합니다.

**실제 사례**

고객 데이터를 읽거나 수정할 때 Pessimistic Offline Lock을 사용하는 예제는 다음과 같습니다:

```java
class LockManager {
    private Map<Long, String> lockTable = new HashMap<>();

    public synchronized void acquireLock(Long lockableId, String owner) {
        if (lockTable.containsKey(lockableId)) {
            throw new ConcurrencyException("Lock already acquired by another owner");
        }
        lockTable.put(lockableId, owner);
    }

    public synchronized void releaseLock(Long lockableId, String owner) {
        if (!lockTable.get(lockableId).equals(owner)) {
            throw new ConcurrencyException("Cannot release lock owned by another");
        }
        lockTable.remove(lockableId);
    }
}
```

이 예제에서는 고객 데이터를 로드하거나 수정하려는 트랜잭션이 먼저 `LockManager`를 통해 잠금을 획득합니다. 다른 트랜잭션은 잠금이 해제될 때까지 해당 데이터에 접근할 수 없습니다.

**Coarse-Grained Lock**

**Coarse-Grained Lock** 패턴은 여러 개의 관련 객체를 단일 잠금으로 관리하여 잠금 관리의 복잡성을 줄이고 성능을 최적화하는 방법입니다.

**개요**

이 패턴은 여러 개의 관련된 객체들을 하나의 그룹으로 묶어 단일 잠금으로 관리합니다. 예를 들어, 고객과 그 주소들을 개별적으로 잠그는 대신, 전체 객체 그룹을 하나의 잠금으로 관리하여 복잡성을 줄이고 성능을 향상시킬 수 있습니다.

**작동 방식**

* **공유 버전 사용**: 관련 객체들이 동일한 버전 번호를 공유하게 설정하여, 그룹 전체가 단일 잠금으로 관리되도록 합니다.
* **루트 잠금**: 객체 그룹의 루트 객체에 잠금을 설정하여, 그룹 전체를 잠그는 방법도 사용할 수 있습니다.

**실제 사례**

고객 객체와 그에 연결된 주소 객체들이 동일한 버전 객체를 공유하는 시스템을 설계할 수 있습니다. 고객 객체 또는 주소 객체 중 하나가 수정될 때, 해당 버전 객체의 버전 번호가 증가되며, 이로 인해 전체 그룹이 잠기게 됩니다.

```java
class Version {
    private Long id;
    private long value;

    public synchronized void increment() {
        this.value++;
    }
}

class Customer {
    private Version version;
    private List<Address> addresses;

    public void addAddress(Address address) {
        address.setVersion(this.version);
        this.addresses.add(address);
    }

    public void update() {
        this.version.increment();
    }
}
```

이 코드에서는 `Customer`와 그에 연결된 `Address` 객체들이 동일한 `Version` 객체를 공유하며, `Customer` 또는 `Address` 객체가 수정될 때마다 버전 번호가 증가됩니다. 이를 통해 그룹 전체를 단일 잠금으로 관리할 수 있습니다.

**Implicit Lock**

**Implicit Lock** 패턴은 개발자가 명시적으로 잠금을 처리하지 않아도 시스템이 자동으로 잠금을 관리하도록 하는 방법입니다. 이를 통해 개발자가 잠금을 설정하거나 해제하는 작업을 잊어버리는 것을 방지하고, 일관된 잠금 관리가 가능하도록 합니다.

**개요**

이 패턴은 시스템이 잠금 메커니즘을 자동으로 처리하도록 하여, 개발자가 잠금을 수동으로 설정하거나 해제할 필요가 없도록 합니다. 이는 잠금을 잊어버리거나 실수로 생략하는 것을 방지하며, 시스템의 안정성을 높입니다.

**작동 방식**

* **잠금 획득 자동화**: 데이터를 로드하거나 수정할 때, 시스템이 자동으로 필요한 잠금을 획득합니다.
* **잠금 해제 자동화**: 트랜잭션이 종료되면 시스템이 자동으로 잠금을 해제합니다.
* **잠금 검증**: 트랜잭션이 커밋되기 전에 필요한 모든 잠금이 획득되었는지 검증합니다.

**실제 사례**

도메인 객체

를 조회하거나 수정할 때 자동으로 잠금을 설정하는 매퍼(mapper)를 사용하는 예제입니다:

```java
class LockingMapper extends Mapper {
    private Mapper impl;

    public LockingMapper(Mapper impl) {
        this.impl = impl;
    }

    public DomainObject find(Long id) {
        ExclusiveReadLockManager.INSTANCE.acquireLock(id, AppSessionManager.getSession().getId());
        return impl.find(id);
    }

    public void update(DomainObject obj) {
        impl.update(obj);
    }

    public void delete(DomainObject obj) {
        impl.delete(obj);
    }
}
```

이 예제에서는 `LockingMapper`가 객체를 조회하거나 수정하기 전에 자동으로 잠금을 설정하여, 개발자가 잠금을 설정하거나 해제하는 작업을 실수로 누락하지 않도록 보장합니다.
