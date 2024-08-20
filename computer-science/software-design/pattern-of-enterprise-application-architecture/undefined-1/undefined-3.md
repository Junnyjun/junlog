# 객체 관계형 구조 패턴

### 1. Identity Field

**Identity Field**는 데이터베이스의 행과 메모리 내의 객체 간의 아이덴티티를 유지하기 위해 객체 내에 데이터베이스의 ID 필드를 저장하는 패턴입니다. 이 패턴은 데이터베이스에서 특정 행을 식별할 수 있는 유일한 키(주로 기본 키)를 객체에 포함시켜, 객체가 데이터베이스의 특정 행과 매핑되도록 합니다.

#### 동작 방식

Identity Field의 기본 동작은 간단하지만, 몇 가지 중요한 고려 사항이 있습니다:

1. **키 선택**:
   * 데이터베이스의 기본 키를 선택할 때, 의미 있는 키(예: 주민등록번호)와 무의미한 키(예: 자동 생성된 숫자) 중 하나를 선택할 수 있습니다.
   * 의미 있는 키는 이해하기 쉽지만, 고유성과 불변성을 보장하기 어려울 수 있습니다.
   * 무의미한 키는 일반적으로 더 안전하며, 시스템에서 고유성과 불변성을 더 잘 유지할 수 있습니다.
2. **단일 키와 복합 키**:
   * 단일 키는 하나의 필드만 사용하여 객체를 식별하는 반면, 복합 키는 여러 필드를 결합하여 객체를 식별합니다.
   * 복합 키는 더 많은 정보를 포함할 수 있지만, 코드 복잡성이 증가할 수 있습니다.
3. **키 타입**:
   * 키로 사용되는 필드는 주로 숫자형(long이나 int) 타입이 선호되며, 이는 비교 연산이 빠르고 키 생성이 용이하기 때문입니다.
   * GUID(Globally Unique Identifier)와 같은 고유한 식별자를 사용하는 경우, 매우 큰 문자열이 생성될 수 있으며, 이로 인해 성능 저하가 발생할 수 있습니다.
4. **키 생성**:
   * 키는 자동으로 생성될 수도 있고, 데이터베이스 시퀀스를 사용하여 수동으로 관리할 수도 있습니다.
   * 데이터베이스에서 자동으로 키를 생성하는 방법은 가장 간단하지만, 생성된 키를 미리 알아야 할 경우(예: 외래 키 설정 시) 문제가 발생할 수 있습니다.
   * 데이터베이스 시퀀스를 사용하면 미리 키를 예약할 수 있어 더 복잡한 트랜잭션에서 유용합니다.

#### 사용 시기

**Identity Field**는 객체와 데이터베이스 간의 매핑에서 고유성을 유지해야 할 때 필수적으로 사용됩니다. 특히 다음과 같은 경우에 유용합니다:

* **도메인 모델을 사용하는 경우**: 도메인 모델은 객체의 고유성을 유지해야 하므로 Identity Field를 사용하여 데이터베이스와의 매핑을 관리합니다.
* **복잡한 데이터 구조를 다루는 경우**: 여러 테이블에 걸쳐 데이터를 저장하는 경우, Identity Field를 통해 객체 간의 참조 관계를 명확히 할 수 있습니다.

#### 예제: Java로 구현된 Identity Field

```java
class DomainObject {
    private Long id;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isNew() {
        return this.id == null;
    }
}
```

이 예제에서 `DomainObject` 클래스는 ID 필드를 가지고 있으며, 이 필드는 데이터베이스의 기본 키와 연동됩니다. 객체가 데이터베이스에 새로 삽입될 때, ID 필드는 null로 설정되며, 데이터베이스에 저장되면서 고유한 ID가 할당됩니다.

***

### 2. Foreign Key Mapping

**Foreign Key Mapping**은 객체 간의 연관 관계를 데이터베이스의 외래 키를 통해 매핑하는 패턴입니다. 이 패턴은 두 테이블 간의 관계를 객체 간의 관계로 매핑하여, 데이터베이스와 객체 모델 간의 일관성을 유지합니다.

#### 동작 방식

Foreign Key Mapping은 다음과 같은 방식으로 동작합니다:

1. **단일 참조**:
   * 객체가 다른 객체를 참조할 때, 해당 참조는 데이터베이스의 외래 키로 매핑됩니다.
   * 예를 들어, `Album` 객체가 `Artist` 객체를 참조하는 경우, `Album` 테이블에는 `Artist` 테이블의 외래 키가 저장됩니다.
2. **컬렉션 참조**:
   * 한 객체가 여러 객체를 참조하는 경우(예: `Album`이 여러 `Track`을 포함하는 경우), 참조되는 객체의 테이블에 외래 키가 저장됩니다.
   * 이 경우, `Track` 테이블에는 `Album`의 외래 키가 포함되며, 이를 통해 여러 `Track`이 하나의 `Album`에 연결됩니다.
3. **컬렉션 업데이트**:
   * 컬렉션 참조가 업데이트될 때, 기존의 모든 참조를 삭제하고 새로운 참조를 삽입하는 방식으로 처리할 수 있습니다.
   * 또는, 기존의 참조와 새로운 참조 간의 차이를 비교하여 변경된 부분만 업데이트할 수도 있습니다.

#### 사용 시기

**Foreign Key Mapping**은 다음과 같은 경우에 사용됩니다:

* **객체 간의 연관 관계를 유지해야 할 때**: 객체 간의 관계를 데이터베이스의 외래 키로 매핑하여, 관계의 일관성을 유지합니다.
* **일대다, 다대일 관계를 처리할 때**: 컬렉션이나 단일 참조를 매핑할 때 외래 키를 사용하여 관계를 표현합니다.

#### 예제: Java로 구현된 Foreign Key Mapping

```java
class Album {
    private Artist artist;
    
    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }
}

class AlbumMapper {
    public void load(ResultSet rs, Album album) throws SQLException {
        Long artistId = rs.getLong("artist_id");
        Artist artist = ArtistMapper.find(artistId);
        album.setArtist(artist);
    }

    public void update(Album album) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
            "UPDATE albums SET artist_id = ? WHERE id = ?");
        stmt.setLong(1, album.getArtist().getId());
        stmt.setLong(2, album.getId());
        stmt.executeUpdate();
    }
}
```

이 예제에서 `AlbumMapper` 클래스는 `Album` 객체와 데이터베이스 간의 외래 키 매핑을 처리합니다. `Artist` 객체는 `Album` 객체의 `artist` 필드로 참조되며, 이 참조는 `albums` 테이블의 `artist_id` 외래 키로 매핑됩니다.

***

### 3. Association Table Mapping

**Association Table Mapping**은 두 테이블 간의 다대다 관계를 처리하기 위해 별도의 연결 테이블을 사용하는 패턴입니다. 이 패턴은 두 테이블 간의 다대다 관계를 표현하기 위해 중간에 별도의 테이블을 생성하여, 각 테이블의 기본 키를 외래 키로 포함하는 구조를 만듭니다.

#### 동작 방식

Association Table Mapping은 다음과 같은 방식으로 동작합니다:

1. **연결 테이블 생성**:
   * 다대다 관계를 처리하기 위해 두 테이블 간의 연결 테이블을 생성합니다.
   * 이 연결 테이블은 두 테이블의 기본 키를 외래 키로 포함하며, 이 외래 키 조합이 연결 테이블의 기본 키가 됩니다.
2. **데이터 로딩**:
   * 연결 테이블을 조회하여 두 테이블 간의 관계를 로드합니다.
   * 예를 들어, `Employee`와 `Skill` 간의 관계를 표현하기 위해 `EmployeeSkill` 연결 테이블을 사용할 수 있습니다.
3. **데이터 업데이트**:
   * 연결 테이블에 데이터를 삽입하거나 삭제하여 관계를 업데이트합니다.
   * 필요에 따라 연결 테이블의 데이터를 삭제한 후 새 데이터를 삽입하는 방식으로 처리할 수 있습니다.

#### 사용 시기

**Association Table Mapping**은 다음과 같은 경우에 사용됩니다:

* **다대다 관계를 처리할 때**: 두 테이블 간의 다대다 관계를 표현하기 위해 별도의 연결 테이블을 사용합니다.
* **관계에 추가적인 정보를 저장해야 할 때**: 연결 테이블에 추가적인 컬럼을 포함시켜 관계에 대한 추가 정보를 저장할 수 있습니다.

#### 예제: Java로 구현된 Association Table Mapping

```java
class Employee {
    private List<Skill> skills;

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }
}

class EmployeeMapper {
    public void loadSkills(Employee employee) throws SQLException {
        List<Skill> skills = new ArrayList<>();
        PreparedStatement stmt = connection.prepareStatement(
            "SELECT s.* FROM skills s JOIN employee_skills es ON s.id = es.skill_id WHERE es.employee_id = ?");
        stmt.setLong(1, employee.getId());
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Skill skill = SkillMapper.find(rs.getLong("id"));
            skills.add(skill);
        }
        employee.setSkills(skills);
    }

    public void updateSkills(Employee employee) throws SQLException {
        PreparedStatement deleteStmt = connection

.prepareStatement(
            "DELETE FROM employee_skills WHERE employee_id = ?");
        deleteStmt.setLong(1, employee.getId());
        deleteStmt.executeUpdate();

        PreparedStatement insertStmt = connection.prepareStatement(
            "INSERT INTO employee_skills (employee_id, skill_id) VALUES (?, ?)");
        for (Skill skill : employee.getSkills()) {
            insertStmt.setLong(1, employee.getId());
            insertStmt.setLong(2, skill.getId());
            insertStmt.executeUpdate();
        }
    }
}
```

이 예제에서 `EmployeeMapper` 클래스는 `Employee` 객체와 `Skill` 객체 간의 다대다 관계를 처리합니다. `employee_skills` 연결 테이블을 사용하여 `Employee`와 `Skill` 간의 관계를 관리합니다.

***

### 4. Dependent Mapping

**Dependent Mapping**은 자식 클래스의 데이터베이스 매핑을 부모 클래스가 대신 처리하는 패턴입니다. 이 패턴은 특정 객체가 다른 객체에 종속되어 있을 때, 종속된 객체의 매핑을 해당 객체를 소유한 객체가 처리하도록 합니다.

#### 동작 방식

Dependent Mapping은 다음과 같은 방식으로 동작합니다:

1. **종속 객체의 매핑 처리**:
   * 종속된 객체는 독립적인 매핑을 가지지 않으며, 부모 객체가 대신 매핑을 처리합니다.
   * 예를 들어, `Album`이 `Track`을 포함하는 경우, `Track`의 매핑은 `Album`의 매퍼가 처리합니다.
2. **종속 객체의 로딩 및 저장**:
   * 부모 객체가 로딩될 때 종속 객체도 함께 로딩되며, 저장될 때도 함께 저장됩니다.
   * 종속 객체는 데이터베이스에 고유한 ID를 가지지 않으므로, ID 기반의 조회는 불가능합니다.
3. **컬렉션의 업데이트**:
   * 종속 객체가 포함된 컬렉션이 업데이트될 때, 기존의 데이터를 삭제하고 새 데이터를 삽입하는 방식으로 처리할 수 있습니다.

#### 사용 시기

**Dependent Mapping**은 다음과 같은 경우에 사용됩니다:

* **한 객체가 다른 객체에 종속되어 있을 때**: 종속된 객체가 독립적으로 존재하지 않고, 다른 객체에 의해 관리되는 경우 Dependent Mapping을 사용합니다.
* **복잡한 데이터 구조를 간소화할 때**: 종속된 객체의 매핑을 부모 객체가 대신 처리함으로써 매핑 구조를 단순화할 수 있습니다.

#### 예제: Java로 구현된 Dependent Mapping

```java
class Album {
    private List<Track> tracks;

    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }
}

class AlbumMapper {
    public void load(ResultSet rs, Album album) throws SQLException {
        album.setTitle(rs.getString("title"));
        List<Track> tracks = new ArrayList<>();
        PreparedStatement stmt = connection.prepareStatement(
            "SELECT * FROM tracks WHERE album_id = ?");
        stmt.setLong(1, album.getId());
        ResultSet trackRs = stmt.executeQuery();
        while (trackRs.next()) {
            Track track = new Track(trackRs.getString("title"));
            tracks.add(track);
        }
        album.setTracks(tracks);
    }

    public void update(Album album) throws SQLException {
        PreparedStatement deleteStmt = connection.prepareStatement(
            "DELETE FROM tracks WHERE album_id = ?");
        deleteStmt.setLong(1, album.getId());
        deleteStmt.executeUpdate();

        PreparedStatement insertStmt = connection.prepareStatement(
            "INSERT INTO tracks (album_id, title) VALUES (?, ?)");
        for (Track track : album.getTracks()) {
            insertStmt.setLong(1, album.getId());
            insertStmt.setString(2, track.getTitle());
            insertStmt.executeUpdate();
        }
    }
}
```

이 예제에서 `AlbumMapper` 클래스는 `Album` 객체와 `Track` 객체 간의 종속 관계를 처리합니다. `Track` 객체는 독립적으로 존재하지 않으며, `Album` 객체에 의해 관리됩니다.

***

### 5. Embedded Value

**Embedded Value**는 객체의 값을 다른 객체의 테이블 필드에 매핑하는 패턴입니다. 이 패턴은 작은 객체들이 독립된 테이블로 저장되지 않고, 상위 객체의 테이블에 포함되는 경우에 사용됩니다.

#### 동작 방식

Embedded Value는 다음과 같은 방식으로 동작합니다:

1. **객체 값의 매핑**:
   * 객체의 필드가 상위 객체의 테이블 필드에 매핑됩니다.
   * 예를 들어, `ProductOffering` 클래스가 `Money` 객체를 포함하는 경우, `ProductOffering` 테이블에 `Money` 객체의 필드가 포함됩니다.
2. **객체의 로딩 및 저장**:
   * 상위 객체가 로딩되거나 저장될 때, 포함된 객체도 함께 로딩되거나 저장됩니다.
   * 포함된 객체는 별도의 매핑을 가지지 않으며, 상위 객체의 매퍼가 이를 대신 처리합니다.

#### 사용 시기

**Embedded Value**는 다음과 같은 경우에 사용됩니다:

* **작은 객체들이 독립적으로 존재할 필요가 없을 때**: 작은 객체들이 독립된 테이블로 저장되지 않고, 상위 객체의 테이블에 포함되어도 무방할 때 사용됩니다.
* **값 객체(Value Object)를 매핑할 때**: 값 객체는 식별자가 없고, 독립적으로 존재하지 않으므로 Embedded Value로 매핑됩니다.

#### 예제: Java로 구현된 Embedded Value

```java
class ProductOffering {
    private Money baseCost;

    public Money getBaseCost() {
        return baseCost;
    }

    public void setBaseCost(Money baseCost) {
        this.baseCost = baseCost;
    }
}

class ProductOfferingMapper {
    public void load(ResultSet rs, ProductOffering offering) throws SQLException {
        BigDecimal amount = rs.getBigDecimal("base_cost_amount");
        String currencyCode = rs.getString("base_cost_currency");
        Money baseCost = new Money(amount, Currency.getInstance(currencyCode));
        offering.setBaseCost(baseCost);
    }

    public void update(ProductOffering offering) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
            "UPDATE product_offerings SET base_cost_amount = ?, base_cost_currency = ? WHERE id = ?");
        stmt.setBigDecimal(1, offering.getBaseCost().getAmount());
        stmt.setString(2, offering.getBaseCost().getCurrency().getCurrencyCode());
        stmt.setLong(3, offering.getId());
        stmt.executeUpdate();
    }
}
```

이 예제에서 `ProductOfferingMapper` 클래스는 `ProductOffering` 객체와 `Money` 객체 간의 Embedded Value 관계를 처리합니다. `Money` 객체는 독립적인 테이블에 저장되지 않고, `ProductOffering` 테이블에 포함됩니다.

***

### 6. Serialized LOB

**Serialized LOB**는 객체의 상태를 직렬화하여 데이터베이스의 LOB(Large Object) 필드에 저장하는 패턴입니다. 이 패턴은 복잡한 객체 구조를 하나의 필드에 저장하여, 객체의 상태를 완전히 보존하고자 할 때 사용됩니다.

#### 동작 방식

Serialized LOB은 다음과 같은 방식으로 동작합니다:

1. **객체의 직렬화**:
   * 객체의 상태가 직렬화되어 데이터베이스의 LOB 필드에 저장됩니다.
   * 직렬화는 주로 XML, JSON, 또는 바이너리 형식으로 수행됩니다.
2. **객체의 역직렬화**:
   * 데이터베이스에서 LOB 필드를 읽어와 객체로 역직렬화합니다.
   * 이를 통해 객체의 상태를 복원할 수 있습니다.
3. **복잡한 객체 구조**:
   * 이 패턴은 복잡한 객체 그래프를 하나의 LOB 필드에 저장할 때 특히 유용합니다.
   * 객체 그래프 전체를 직렬화하여 저장하므로, 객체 간의 복잡한 관계를 관리할 필요가 없습니다.

#### 사용 시기

**Serialized LOB**은 다음과 같은 경우에 사용됩니다:

* **복잡한 객체 구조를 저장해야 할 때**: 객체 그래프를 포함한 복잡한 구조를 하나의 필드에 저장해야 할 때 유용합니다.
* **객체 상태를 완전히 보존해야 할 때**: 객체의 상태를 그대로 유지하면서 데이터베이스에 저장하고자 할 때 사용됩니다.

#### 예제: Java로 구현된 Serialized LOB

```java
class SerializedLOBExample {
    private Connection connection;

    public void saveObject(Long id, Serializable object) throws SQLException, IOException {
        PreparedStatement stmt = connection.prepareStatement(
            "UPDATE objects SET data = ? WHERE id = ?");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(object);
        oos.flush();
        byte[] data = bos.toByteArray();
        stmt.setBytes(1, data);
        stmt.setLong(2, id);
        stmt.executeUpdate();
    }

    public Object loadObject(Long id) throws SQLException, IOException, ClassNotFoundException {
        PreparedStatement stmt = connection.prepareStatement(
            "SELECT data FROM objects WHERE id = ?");
        stmt.setLong(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {


            byte[] data = rs.getBytes("data");
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(bis);
            return ois.readObject();
        }
        return null;
    }
}
```

이 예제에서 `SerializedLOBExample` 클래스는 객체를 직렬화하여 데이터베이스의 LOB 필드에 저장하고, 다시 역직렬화하여 객체로 복원합니다. 이 방법을 통해 복잡한 객체 구조를 하나의 필드에 저장할 수 있습니다.

***

### 7. Single Table Inheritance

**Single Table Inheritance**는 상속 구조를 가진 클래스 계층을 하나의 테이블에 저장하는 패턴입니다. 이 패턴은 클래스 계층의 모든 필드를 하나의 테이블에 포함시키고, 상속된 각 클래스에 대한 데이터를 관리합니다.

#### 동작 방식

Single Table Inheritance는 다음과 같은 방식으로 동작합니다:

1. **단일 테이블 사용**:
   * 클래스 계층의 모든 필드를 포함하는 하나의 테이블을 생성합니다.
   * 각 클래스의 인스턴스는 동일한 테이블의 행에 저장되며, 구분 필드(discriminator column)를 사용하여 클래스 유형을 식별합니다.
2. **구분 필드 사용**:
   * 구분 필드는 각 행이 어떤 클래스에 속하는지 나타내는 값을 저장합니다.
   * 이 필드는 클래스 계층의 최상위 클래스에 정의되며, 상속된 각 클래스에 따라 값이 달라집니다.
3. **필드의 누락 허용**:
   * 특정 클래스에만 해당하는 필드는 다른 클래스의 경우 null로 저장될 수 있습니다.
   * 이는 모든 클래스가 동일한 테이블을 사용하기 때문에 발생하는 현상입니다.

#### 사용 시기

**Single Table Inheritance**는 다음과 같은 경우에 사용됩니다:

* **간단한 상속 구조를 관리할 때**: 상속 구조가 복잡하지 않고, 클래스 간의 차이가 크지 않을 때 유용합니다.
* **성능이 중요한 경우**: 모든 데이터를 하나의 테이블에 저장하므로, 조회 및 관리가 간단하고 성능이 좋습니다.

#### 예제: Java로 구현된 Single Table Inheritance

```java
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "vehicle_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String manufacturer;

    // getters and setters
}

@Entity
@DiscriminatorValue("CAR")
public class Car extends Vehicle {
    private int seatingCapacity;

    // getters and setters
}

@Entity
@DiscriminatorValue("TRUCK")
public class Truck extends Vehicle {
    private double payloadCapacity;

    // getters and setters
}
```

이 예제에서 `Vehicle`, `Car`, `Truck` 클래스는 Single Table Inheritance 전략을 사용하여 하나의 테이블에 저장됩니다. `vehicle_type` 구분 필드를 사용하여 각 행이 어떤 클래스에 속하는지 식별합니다.

***

### 8. Class Table Inheritance

**Class Table Inheritance**는 상속 구조를 가진 클래스 계층을 각각의 클래스마다 별도의 테이블로 저장하는 패턴입니다. 이 패턴은 각 클래스가 독립된 테이블을 가지며, 상위 클래스의 테이블이 하위 클래스의 테이블과 연결되는 구조를 가집니다.

#### 동작 방식

Class Table Inheritance는 다음과 같은 방식으로 동작합니다:

1. **클래스별 테이블 생성**:
   * 상위 클래스와 하위 클래스 각각에 대해 별도의 테이블을 생성합니다.
   * 상위 클래스의 필드는 상위 클래스 테이블에 저장되며, 하위 클래스의 필드는 하위 클래스 테이블에 저장됩니다.
2. **외래 키 사용**:
   * 하위 클래스의 테이블은 상위 클래스의 테이블과 외래 키로 연결됩니다.
   * 이 외래 키는 상위 클래스의 기본 키를 참조합니다.
3. **데이터 조회**:
   * 특정 하위 클래스의 데이터를 조회할 때는 상위 클래스와 하위 클래스의 테이블을 조인하여 데이터를 가져옵니다.

#### 사용 시기

**Class Table Inheritance**는 다음과 같은 경우에 사용됩니다:

* **클래스 간의 필드 차이가 클 때**: 각 클래스가 많은 고유 필드를 가지고 있을 때, Class Table Inheritance를 사용하여 데이터베이스 구조를 효율적으로 관리할 수 있습니다.
* **테이블의 데이터 무결성이 중요할 때**: 각 클래스별로 테이블을 나누어 관리함으로써, 데이터 무결성을 유지할 수 있습니다.

#### 예제: Java로 구현된 Class Table Inheritance

```java
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String manufacturer;

    // getters and setters
}

@Entity
public class Car extends Vehicle {
    private int seatingCapacity;

    // getters and setters
}

@Entity
public class Truck extends Vehicle {
    private double payloadCapacity;

    // getters and setters
}
```

이 예제에서 `Vehicle`, `Car`, `Truck` 클래스는 Class Table Inheritance 전략을 사용하여 각각의 테이블에 저장됩니다. `Vehicle` 테이블은 상위 클래스의 필드를 포함하며, `Car`와 `Truck` 테이블은 각 클래스의 고유 필드를 포함하고 상위 클래스와 외래 키로 연결됩니다.

***

### 9. Concrete Table Inheritance

**Concrete Table Inheritance**는 각 하위 클래스가 독립적인 테이블을 가지며, 상위 클래스의 필드를 포함하는 패턴입니다. 이 패턴은 각 하위 클래스가 상위 클래스의 모든 필드를 포함하여, 하나의 테이블에 모든 데이터를 저장합니다.

#### 동작 방식

Concrete Table Inheritance는 다음과 같은 방식으로 동작합니다:

1. **하위 클래스별 테이블 생성**:
   * 각 하위 클래스에 대해 독립적인 테이블을 생성합니다.
   * 하위 클래스의 테이블에는 상위 클래스의 모든 필드와 하위 클래스의 고유 필드가 포함됩니다.
2. **상위 클래스의 필드 포함**:
   * 각 하위 클래스의 테이블은 상위 클래스의 모든 필드를 포함하여, 상속 관계를 유지합니다.
   * 상위 클래스의 필드는 각 하위 클래스의 테이블에 중복 저장됩니다.
3. **데이터의 독립성**:
   * 각 하위 클래스의 테이블은 상위 클래스의 테이블과 독립적이며, 별도의 외래 키 참조가 필요하지 않습니다.

#### 사용 시기

**Concrete Table Inheritance**는 다음과 같은 경우에 사용됩니다:

* **클래스 간의 관계가 단순할 때**: 상위 클래스와 하위 클래스 간의 관계가 단순하고, 데이터베이스 구조가 복잡하지 않을 때 유용합니다.
* **독립적인 테이블이 필요한 경우**: 각 하위 클래스의 데이터가 독립적으로 관리되어야 할 때 사용됩니다.

#### 예제: Java로 구현된 Concrete Table Inheritance

```java
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String manufacturer;

    // getters and setters
}

@Entity
public class Car extends Vehicle {
    private int seatingCapacity;

    // getters and setters
}

@Entity
public class Truck extends Vehicle {
    private double payloadCapacity;

    // getters and setters
}
```

이 예제에서 `Vehicle`, `Car`, `Truck` 클래스는 Concrete Table Inheritance 전략을 사용하여 각각의 독립된 테이블에 저장됩니다. 각 테이블은 상위 클래스의 필드와 하위 클래스의 고유 필드를 모두 포함합니다.

***

### 10. Inheritance Mappers

**Inheritance Mappers**는 상속 구조를 가진 객체를 데이터베이스에 매핑하는 다양한 전략을 포괄하는 용어입니다. 이 패턴은 상속 구조를 가진 클래스 계층을 데이터베이스에 효율적으로 매핑하기 위한 여러 가지 방법을 제공합니다.

#### 동작 방식

Inheritance Mappers는 다음과 같은 방식으로 동작합니다:

1. **상속 매핑 전략 선택**:
   * 상속 구조를 데이터베이스에 매핑할 때, Single Table Inheritance, Class Table Inheritance, Concrete Table Inheritance 중 적절한 전략을 선택합니다.
   * 각 전략은 상속 구조를 처리하는 방식이 다르며, 특정 상황에 맞게 선택해야 합니다.
2. **클래스 계층 매핑**:
   * 선택된 전략에 따라 클래스 계층을 데이터베이스에 매핑합니다.
   * 상속 관계를 유지하면서, 각 클래스의 데이터를 효율적으로 관리할 수 있도록 매핑합니다.
3. **데이터베이스 설계**:
   * 상속 매핑 전략에 따라 데이터베이스 테이블 구조를 설계합니다.
   * 각 전략에 맞는 테이블 구조를 설계하여, 상속 관계를 유지하면서 성능과 무결성을 고려합니다.

#### 사용 시기

**Inheritance Mappers**는 다음과 같은 경우에 사용됩니다:

* **상속 구조를 가진 객체를 매핑할 때**: 상속 구조를

가진 클래스 계층을 데이터베이스에 매핑해야 할 때 적절한 전략을 선택하여 사용합니다.

* **데이터베이스 설계를 최적화할 때**: 상속 매핑 전략을 통해 데이터베이스 구조를 최적화하고, 성능과 무결성을 유지합니다.

#### 예제: Java로 구현된 Inheritance Mappers

Inheritance Mappers는 위에서 설명한 **Single Table Inheritance**, **Class Table Inheritance**, **Concrete Table Inheritance**를 포함한 모든 상속 매핑 전략을 포괄하는 개념입니다. 따라서 특정 예제 없이 각 전략에 따라 적절한 방법을 선택하여 구현할 수 있습니다.

위 텍스트는 "Serialized LOB" 패턴에 대한 내용을 설명하고 있습니다. 이 패턴은 객체 그래프를 직렬화하여 데이터베이스에 저장하는 방법 중 하나로, 주로 복잡한 객체 모델을 하나의 필드에 저장할 때 사용됩니다. 아래는 Serialized LOB 패턴에 대한 간략한 요약과 Java로 구현한 예제입니다.

#### Serialized LOB 패턴 요약

**Serialized LOB** 패턴은 객체 그래프를 하나의 큰 객체(LOB, Large Object)로 직렬화하여 데이터베이스에 저장하는 방법입니다. 이 패턴을 사용할 때는 주로 다음과 같은 시나리오에 적합합니다:

1. **복잡한 객체 그래프**: 객체 모델이 복잡하고, 이를 하나의 테이블 필드에 저장하고자 할 때 유용합니다.
2. **SQL 쿼리로 접근 불필요**: 객체 그래프의 구조에 대해 SQL 쿼리로 직접 접근할 필요가 없는 경우에 적합합니다.
3. **버전 관리 어려움**: 객체의 클래스 구조가 변경될 때 버전 관리가 어려울 수 있습니다.

#### 장점

* 구현이 상대적으로 간단하고, 데이터베이스 공간을 최소화할 수 있습니다.
* XML을 사용하면 사람이 읽을 수 있는 형식으로 데이터를 저장할 수 있습니다.

#### 단점

* SQL 쿼리를 통해 객체 그래프 내부를 탐색할 수 없습니다.
* 클래스 구조 변경 시 데이터 복구가 어려울 수 있습니다.
* 대용량의 XML 데이터를 처리할 때 성능 문제가 발생할 수 있습니다.

#### Java 구현 예제

```java
import java.io.*;
import java.sql.*;
import java.util.*;
import org.jdom2.Element;

public class Customer {
    private String name;
    private List<Department> departments = new ArrayList<>();

    public Long insert() throws SQLException, IOException {
        PreparedStatement insertStatement = null;
        try {
            insertStatement = DB.prepare("INSERT INTO customers (id, name, departments) VALUES (?, ?, ?)");
            long id = findNextDatabaseId();
            insertStatement.setLong(1, id);
            insertStatement.setString(2, name);
            insertStatement.setString(3, XmlStringer.write(departmentsToXmlElement()));
            insertStatement.execute();
            return id;
        } finally {
            DB.cleanUp(insertStatement);
        }
    }

    public static Customer load(ResultSet rs) throws SQLException, IOException {
        String name = rs.getString("name");
        String departmentLob = rs.getString("departments");
        Customer result = new Customer(name);
        result.readDepartments(XmlStringer.read(departmentLob));
        return result;
    }

    private Element departmentsToXmlElement() {
        Element root = new Element("departmentList");
        for (Department dep : departments) {
            root.addContent(dep.toXmlElement());
        }
        return root;
    }

    private void readDepartments(Element source) {
        departments.clear();
        for (Element deptElement : source.getChildren("department")) {
            departments.add(Department.readXml(deptElement));
        }
    }
}

class Department {
    private String name;
    private List<Department> subsidiaries = new ArrayList<>();

    public Element toXmlElement() {
        Element root = new Element("department");
        root.setAttribute("name", name);
        for (Department sub : subsidiaries) {
            root.addContent(sub.toXmlElement());
        }
        return root;
    }

    public static Department readXml(Element source) {
        String name = source.getAttributeValue("name");
        Department result = new Department();
        result.name = name;
        for (Element subElement : source.getChildren("department")) {
            result.subsidiaries.add(readXml(subElement));
        }
        return result;
    }
}

class DB {
    public static PreparedStatement prepare(String query) throws SQLException {
        // SQL 연결과 Statement 생성 로직
        return null;
    }

    public static void cleanUp(PreparedStatement ps) {
        // 리소스 정리 로직
    }
}

class XmlStringer {
    public static String write(Element element) {
        // XML Element를 문자열로 변환하는 로직
        return element.toString();
    }

    public static Element read(String xmlString) {
        // 문자열을 XML Element로 변환하는 로직
        return null;
    }
}
```

#### 주요 포인트:

1. **데이터 저장**: `Customer` 클래스는 부서 정보(`departments`)를 XML 문자열로 직렬화하여 데이터베이스에 저장합니다.
2. **데이터 로드**: 데이터베이스에서 가져온 XML 문자열을 다시 객체로 복원합니다.
3. **LOB 필드 사용**: `departments` 필드는 LOB로 저장되며, 이는 XML 형태로 데이터베이스에 기록됩니다.
