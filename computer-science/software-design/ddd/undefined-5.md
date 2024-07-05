# 도메인 객체 생명 주기

#### 도메인 객체의 생명주기

모든 객체는 생명주기를 가지고 있습니다. 객체는 생성되고, 다양한 상태를 거치며, 결국 삭제되거나 보관됩니다. 간단하고 일시적인 객체는 생성자 호출을 통해 쉽게 생성되고 사용된 후 가비지 컬렉터에 의해 제거됩니다. 하지만 대부분의 경우, 우리는 더 복잡하고 긴 생명주기를 가진 객체에 대해 작업하게 됩니다. 이러한 지속적인 객체들을 관리하는 것은 모델 기반 설계(Model-Driven Design)를 실현하는 데 있어서 큰 도전 과제가 됩니다.

#### 생명주기 관리의 문제점

객체의 생명주기 관리는 두 가지 주요 문제를 야기합니다:

1. 생명주기 전반에 걸쳐 객체의 무결성을 유지하는 것.
2. 생명주기 관리를 복잡하게 만드는 것을 방지하는 것.

#### 생명주기 문제 해결을 위한 패턴

이 문제들을 해결하기 위해 세 가지 패턴이 사용됩니다: AGGREGATES, FACTORIES, 그리고 REPOSITORIES입니다.

**AGGREGATES**

AGGREGATES는 객체 간의 관계를 간소화하고 무결성을 유지하기 위한 방법입니다. AGGREGATE는 관련된 객체의 집합으로, 데이터 변경의 단위로 취급됩니다. 각 AGGREGATE는 루트 엔티티와 경계를 가집니다. AGGREGATE 내의 객체는 서로 참조할 수 있지만, 외부 객체는 루트 엔티티를 통해서만 참조할 수 있습니다.

**AGGREGATE의 예시**

자동차 모델은 AGGREGATE의 좋은 예시입니다. 자동차는 전역 식별성을 가지며, 타이어와 같은 부품은 지역 식별성을 가집니다. 자동차는 루트 엔티티로서, 외부 객체는 자동차를 통해서만 타이어에 접근할 수 있습니다.

**AGGREGATE 규칙**

1. 루트 엔티티는 전역 식별성을 가지며, 내부 엔티티는 지역 식별성을 가집니다.
2. AGGREGATE 내부 객체 간의 참조는 허용되지만, 외부 객체는 루트 엔티티에만 참조할 수 있습니다.
3. 데이터베이스 쿼리를 통해 직접 접근할 수 있는 것은 AGGREGATE의 루트 엔티티뿐입니다.
4. 삭제 작업은 AGGREGATE 경계 내의 모든 객체를 한 번에 제거해야 합니다.
5. AGGREGATE 경계 내의 모든 객체가 커밋될 때, 모든 불변 조건이 충족되어야 합니다.

**코드 예시**

```java
public class Car {
    private String vehicleIdentificationNumber;
    private List<Tire> tires;

    public Car(String vin) {
        this.vehicleIdentificationNumber = vin;
        this.tires = new ArrayList<>();
    }

    public void addTire(Tire tire) {
        tires.add(tire);
    }

    // AGGREGATE 내의 타이어에 접근
    public Tire getTire(int index) {
        return tires.get(index);
    }
}

public class Tire {
    private String position;
    private int mileage;

    public Tire(String position) {
        this.position = position;
        this.mileage = 0;
    }

    public void addMileage(int miles) {
        this.mileage += miles;
    }
}
```

**FACTORIES**

FACTORIES는 객체의 생성과 관련된 복잡성을 캡슐화하여 클라이언트가 객체 생성의 내부 구조를 알 필요 없게 합니다.

**FACTORY의 역할**

FACTORY는 객체 또는 AGGREGATE를 생성할 때 사용됩니다. FACTORY는 객체의 내부 구조를 숨기고, 클라이언트가 단순한 인터페이스를 통해 객체를 생성할 수 있도록 합니다. FACTORY는 객체의 초기 상태를 설정하고, 생성 과정에서 모든 불변 조건이 충족되도록 합니다.

**FACTORY 규칙**

1. 생성 메서드는 원자적이어야 하며, 생성된 객체 또는 AGGREGATE의 모든 불변 조건을 만족시켜야 합니다.
2. FACTORY는 구체적인 클래스가 아닌, 추상 타입을 반환해야 합니다.

**코드 예시**

```java
public class CarFactory {
    public Car createCar(String vin) {
        Car car = new Car(vin);
        car.addTire(new Tire("Front Left"));
        car.addTire(new Tire("Front Right"));
        car.addTire(new Tire("Rear Left"));
        car.addTire(new Tire("Rear Right"));
        return car;
    }
}
```

**REPOSITORIES**

REPOSITORIES는 객체의 저장, 검색, 삭제와 관련된 복잡성을 캡슐화하여 클라이언트가 간단한 인터페이스를 통해 객체를 조작할 수 있도록 합니다.

**REPOSITORY의 역할**

REPOSITORY는 특정 타입의 모든 객체를 개념적으로 모은 집합체로, 객체를 저장하고 검색하는 기능을 제공합니다. REPOSITORY는 데이터베이스 쿼리 및 매핑의 복잡성을 숨기고, 클라이언트가 도메인 모델의 용어로 객체를 조작할 수 있도록 합니다.

**REPOSITORY 규칙**

1. REPOSITORY는 객체를 추가하고 제거하는 메서드를 제공해야 합니다.
2. REPOSITORY는 특정 기준에 따라 객체를 검색하는 메서드를 제공해야 합니다.
3. REPOSITORY는 AGGREGATE 루트에 대해서만 제공해야 합니다.

**코드 예시**

```java
public class CarRepository {
    private Map<String, Car> carStorage = new HashMap<>();

    public void addCar(Car car) {
        carStorage.put(car.getVehicleIdentificationNumber(), car);
    }

    public Car getCar(String vin) {
        return carStorage.get(vin);
    }

    public void removeCar(String vin) {
        carStorage.remove(vin);
    }
}
```

#### 생명주기의 예제

도메인 객체의 생명주기를 보다 명확히 이해하기 위해 브로커리지 계좌 시스템을 예로 들어 설명합니다.

**브로커리지 계좌 시스템**

브로커리지 계좌는 고객과 투자 객체와 연관됩니다. 각 브로커리지 계좌는 AGGREGATE로 취급되며, 고객과 투자는 AGGREGATE 내의 객체로 간주됩니다.

**초기 모델**

브로커리지 계좌는 고객과 투자 객체와 연관됩니다. 이 연관 관계는 객체 포인터나 데이터베이스 조회를 통해 구현될 수 있습니다.

**코드 예시**

```java
public class BrokerageAccount {
    private String accountNumber;
    private Customer customer;
    private Map<String, Investment> investments;

    public BrokerageAccount(String accountNumber, Customer customer) {
        this.accountNumber = accountNumber;
        this.customer = customer;
        this.investments = new HashMap<>();
    }

    public Customer getCustomer() {
        return customer;
    }

    public Investment getInvestment(String stockSymbol) {
        return investments.get(stockSymbol);
    }

    public void addInvestment(String stockSymbol, Investment investment) {
        investments.put(stockSymbol, investment);
    }
}
```

#### AGGREGATES의 활용

브로커리지 계좌 시스템에서 AGGREGATE 패턴을 적용하여 복잡성을 줄이고 객체 간의 관계를 명확히 합니다.

**AGGREGATE 규칙 적용**

1. 브로커리지 계좌는 AGGREGATE 루트로, 고객과 투자 객체는 AGGREGATE 내의 객체로 간주됩니다.
2. 외부 객체는 브로커리지 계좌를 통해서만 고객과 투자 객체에 접근할 수 있습니다.

**코드 예시**

```java
public class BrokerageAccount {
    private String accountNumber;
    private Customer customer;
    private Map<String, Investment> investments;

    public BrokerageAccount(String accountNumber, Customer customer) {
        this.accountNumber = accountNumber;
        this.customer = customer;
        this.investments = new HashMap<>();
    }

    public Customer getCustomer() {
        return customer;
    }

    public Investment getInvestment(String stockSymbol) {
        return investments.get(stockSymbol);
    }

    public void addInvestment(String stockSymbol, Investment investment) {
        investments.put(stockSymbol, investment);
    }

    // AGGREGATE 내의 투자 객체에 접근
    public void updateInvestment(String stockSymbol, Investment investment) {
        if (investments.containsKey(stockSymbol)) {
            investments.put(stockSymbol, investment);
        }
    }

    // AGGREGATE 내의 모든 객체 삭제
    public void clearInvestments() {
        investments.clear();
    }
}
```

#### FACTORIES의 활용

브로커리지 계좌 시스템에서 FACTORY 패턴을 적용하여 객체 생성의 복잡성을 줄입니다.

**FACTORY 규칙 적용**

1. FACTORY는 브로커리지 계좌와 관련된 모든 객체의 초기 상태를 설정하고, 생성 과정에서 모든 불변 조건이 충족되도록 합니다.
2. FACTORY는 추상 타입을 반환하여 클라이언트가 구체적인 클래스에 의존하지 않도록 합니다.

**코드 예시**

```java
public class BrokerageAccountFactory {
    public BrokerageAccount createAccount(String accountNumber, Customer customer) {
        BrokerageAccount account = new BrokerageAccount(accountNumber, customer);
        // 초기 투자를 추가할 수 있음
        account.addInvestment("AAPL", new Investment("AAPL", 100));
        account.addInvestment("GOOG

", new Investment("GOOG", 150));
        return account;
    }
}
```

#### REPOSITORIES의 활용

브로커리지 계좌 시스템에서 REPOSITORY 패턴을 적용하여 객체의 저장, 검색, 삭제와 관련된 복잡성을 줄입니다.

**REPOSITORY 규칙 적용**

1. REPOSITORY는 브로커리지 계좌를 추가하고 제거하는 메서드를 제공합니다.
2. REPOSITORY는 특정 기준에 따라 브로커리지 계좌를 검색하는 메서드를 제공합니다.
3. REPOSITORY는 브로커리지 계좌의 AGGREGATE 루트에 대해서만 제공됩니다.

**코드 예시**

```java
public class BrokerageAccountRepository {
    private Map<String, BrokerageAccount> accountStorage = new HashMap<>();

    public void addAccount(BrokerageAccount account) {
        accountStorage.put(account.getAccountNumber(), account);
    }

    public BrokerageAccount getAccount(String accountNumber) {
        return accountStorage.get(accountNumber);
    }

    public void removeAccount(String accountNumber) {
        accountStorage.remove(accountNumber);
    }
}
```

#### AGGREGATES와 REPOSITORIES의 상호작용

브로커리지 계좌 시스템에서 AGGREGATE와 REPOSITORY의 상호작용을 통해 객체의 생명주기를 관리합니다.

**코드 예시**

```java
public class Main {
    public static void main(String[] args) {
        BrokerageAccountRepository repository = new BrokerageAccountRepository();
        BrokerageAccountFactory factory = new BrokerageAccountFactory();
        
        // 새로운 브로커리지 계좌 생성
        Customer customer = new Customer("cust123", "John Doe");
        BrokerageAccount account = factory.createAccount("acc123", customer);
        
        // 계좌 저장
        repository.addAccount(account);
        
        // 계좌 검색
        BrokerageAccount retrievedAccount = repository.getAccount("acc123");
        
        // 계좌 업데이트
        retrievedAccount.addInvestment("TSLA", new Investment("TSLA", 50));
        
        // 계좌 저장소 업데이트
        repository.addAccount(retrievedAccount);
        
        // 계좌 삭제
        repository.removeAccount("acc123");
    }
}
```
