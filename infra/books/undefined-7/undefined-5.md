# 트랜잭션

## **ACID**

ACID는 데이터베이스 트랜잭션이 안전하게 수행된다는 것을 보장하기 위한 성질을 가리키는 약어이다.

### **원자성(Atomicity)**

원자적이란 더 작은 부분으로 쪼갤 수 없는 것을 가리킨다.

시스템은 연산을 실행하기 전이나 실행한 후의 상태에만 있을 수 있으며 그 중간 상태에는 머물 수 없다.

원자성 덕분에 실패한 트랜잭션은 어떤 것도 변경하지 않았음을 알 수 있으므로 안전하게 재시도 할 수 있다.

### **일관성(Consistency)**

트랜잭션이 실행을 성공적으로 완료하면 언제나 일관성 있는 데이터베이스 상태로 유지하는 것을 말한다.

일관성(C)은 실제로는 ACID에 속하지 않고 애플리케이션의 속성으로 본다.

데이터베이스 자체만으로 불변식을 위반하는 잘못된 데이터를 쓰지 못하도록 막을 수 없기 때문이다.

이러한 것은 애플리케이션의 책임으로 보고 일관성을 달성하기 위해 데이터베이스의 원자성과 격리성 속성에 기댈 수 있다.

### **격리성(Isolation)**

* 동시에 실행되는 트랜잭션은 서로 격리되어 방해할 수 없다.
* 한 트랜잭션이 여러 번 쓴다면 다른 트랜잭션은 그 내용을 전부 볼 수 있든지 아무것도 볼수 없든지 둘 중 하나여야 하고 일부분만 볼 수 있어서는 안된다.

### **지속성(Durability)**

* 데이터베이스 시스템의 목적은 데이터를 잃어버릴 염려가 없는 안전한 저장소를 제공하는 것이다.
* 지속성은 트랜잭션이 성공적으로 커밋됐다면 하드웨어 결함이 발생하거나 데이터베이스가 죽더라도 트랜잭션에서 기록한 모든 데이터는 손실되지 않는다는 보장이다.

## 단일 객체 연산과 다중 객체 연산 <a href="#undefined" id="undefined"></a>

### **단일객체 쓰기**

원자성과 격리성은 단일 객체를 변경하는 경우에도 적용된다.

```
한 객체의 데이터를 끊어 보낼 때 네트워크 연결이 끊기면 조각 데이터를 저장할 것인가?
문서를 쓰고 있을 때 다른 클라이언트에서 그 문서를 읽으면 부분적으로 갱신된 값을 읽게 될까?
```

그렇기에 저장소 엔진들은 거의 보편적으로 한 노드에 존재하는 단일 객체 수준에서 원자성과 격리성을 제공하는 것을 목표로 한다.

### **다중 객체 트랜잭션의 필요성**

다중 객체 트랜잭션은 여러 파티션에 걸쳐서 구현하기가 어렵고 매우 높은 가용성과 성능이 필요한 곳에서는 방해가 되기도 한다.

다중 객체 트랜잭션은 데이터의 여러 조각이 동기화된 상태로 유지돼야 할 때 필요

트랜잭션이 없더라도 복잡한 데이터의 쓰기와 읽기를 수행하는 애플리케이션을 구현할 수 있다.

그러나 원자성이 없으면 오류 처리가 훨씬 더 복잡해지고 격리성이 없으면 동시성 문제가 생길 수 있다.

다중 객체 트랜잭션은 참조가 유효한 상태로 유지되도록 보장해준다.(외래키 등 참조)

비정규화된 데이터가 동기화가 꺠지는 것을 방지(한번에 여러 문서 갱신 시)

트랜잭션 격리성이 없으면 어떤 색인에서는 레코드가 보이지만 다른 색인은 아직 갱신되지 않아서 레코드가 보이지 않을 수 있다.

### 완화된 격리 수준 <a href="#undefined" id="undefined"></a>

#### **커밋 후 읽기 (READ COMMITTED)**

가장 기본적인 수준의 트랜잭션 격리로 이 수준에서는 두 가지를 보장해 준다.

```
데이터베이스에서 읽을 때 커밋된 데이터만 보게 된다(더티 읽기가 없음)
데이터베이스에 쓸 때 커밋된 데이터만 덮어쓰게 된다(더티 쓰기가 없음)
```

**더티 읽기 방지**

더티 읽기(dirty read) : 어떤 트랜잭션에서 처리한 작업이 완료되지 않았는데도 다른 트랜잭션에서 볼 수 있는 현상

<figure><img src="https://blog.kakaocdn.net/dn/TgdFY/btrU6YN1GY4/VIKNCUYdfgoLx2p3jO2Wr0/img.png" alt="" height="193" width="680"><figcaption></figcaption></figure>

더티 읽기를 막아야 하는 이유

```
더티 읽기가 생기면 다른 트랜잭션이 일부는 갱신된 값을, 일부는 갱신되지 않은 값을 볼 수 있다.
트랜잭션이 어보트되면 모두 롤백되어야 하나, 더티 읽기를 허용하면 트랜잭션이 나중에 롤백될 데이터를 볼 수 있다.
```

**커밋 후 읽기 구현**

커밋 후 읽기는 Oracle 11g, PostgreSQL, SQL Server 2012, MemSQL 등에서 기본 설정으로 쓰고 있는 격리 수준이다.

더티 쓰기 방지 : 트랜잭션이 커밋되거나 어보트될 때까지 잠금을 보유한다. 이런 잠금은 커밋 후 읽기 모드에서 데이터베이스에 의해 자동으로 실행된다.

더티 읽기 방지: 과거의 커밋된 값/ 현재 쓰고 있는 새로운 값을 모두 기억하게 하여 해당 트랜잭션이 실행 중인 동안 과거의 값을 읽게하여 더티 읽기를 방지 할 수 있다.

<figure><img src="https://blog.kakaocdn.net/dn/L3TSU/btrVeWVyLK2/FtNHkESQ3oW64K3zFnHilK/img.jpg" alt=""><figcaption></figcaption></figure>

더티 읽기 방지: 사용자 2는 사용자 1의 트랜잭션이 커밋된 후에야 x의 새 값을 보게 된다.

**스냅숏 격리와 반복 읽기(Snapshot Isolation)**

<figure><img src="https://blog.kakaocdn.net/dn/5EZgE/btrVebZLXsA/BrmRajtXGvwAkSMpEFyce1/img.jpg" alt=""><figcaption></figcaption></figure>

읽기 스큐: 앨리스는 일관성이 깨진 상태인 데이터베이스를 본다.

&#x20;커밋 후 읽기 격리 수준에서도 동시성 버그가 생길 수 있으며 이런 현상을 비반복 읽기(nonrepeatable read)나 읽기 스큐(read skew)라고 한다.

위와 같은 경우 몇 초 후 새로고침하면 일관성 있는 계좌를 볼 수 있으나 어떤 상황에서는 이런 비일관성을 감내할 수 없는 경우도 있다.

* ex) 백업 (원본과 복사본의 데이터 차이), 분석 질의와 무결성 확인(큰 부분을 스캔하는 질의시 다른 시점의 데이터베이스 일부를 보게되면 잘못된 결과 반환)

스냅숏 격리 : 각 트랜잭션은 데이터베이스의 일관된 스냅숏으로부터 데이터를 읽는다.\
즉 트랜잭션은 시작할 때 데이터베이스에 커밋된 상태였던 모든 데이터를 본다. 데이터가 나중에 다른 트랜잭션에 의해 바뀌더라도 각 트랜잭션은 특정한 시점의 과거 데이터를 볼 뿐이다.

**스냅숏 격리 구현**

다중 버전 동시성 제어(multi-version concurrency control, MVCC) : 데이터베이스가 객체의 여러 버전을 함께 유지하는 기법

핵심 원리 : 읽는 쪽에서 쓰는 쪽을 결코 차단하지 않고, 쓰는 쪽에서는 읽는 쪽을 결코 차단하지 않는다.

**일관된 스냅숏을 보는 가시성 규칙**

트랜잭션은 데이터베이스에서 객체를 읽을 때 트랜잭션 ID를 사용해 어떤 것을 볼 수 있고 어떤 것을 볼 수 없는지 결정한다.

트랜잭션 ID가 더 큰(즉 현재 트랜잭션이 시작한 후에 시작한) 트랜잭션이 쓴 데이터는 그 트랜잭션의 커밋 여부에 관계 없이 모두 무시된다.

**색인과 스냅숏 격리**

다중 버전 데이터베이스에서 색인의 동작

하나의 선택지는 색인이 객체의 모든 버전을 가리키게 하고 색인 질의가 현재 트랜잭션에서 볼 수 없는 버전을 걸러내고, 가비지 컬렉션이 어떤 트랜잭션에게도 더 이상 보이지 않는 오래된 객체 버전을 삭제 할때 대응되는 색인 항목도 삭제

### 갱신 손실 방지 <a href="#undefined" id="undefined"></a>

갱신 손실 : 두 트랜잭션이 작업을 동시에 하면 두번째 쓰기 작업이 첫 번째 변경을 포함하지 않으므로 변경 중 하나는 손실될 수 있음

```
원자적 쓰기 연산
명시적인 잠금
갱신 손실 자동 감지
Compare-and-set
```

**원자적 쓰기 연산**

```
쓰기 연산에 원자성 (Atomicity) 성질을 부여함으로서 동시성 안전 획득
exclusive lock 을 획득하여 구현 → 갱신이 적용될 때까지 다른 트랜잭션에서 그 객체를 읽지 못함
or 모든 원자적 연산을 단일 스레드에서 실행되도록 강제하는 방법
```

**명시적인 잠금**

애플리케이션에서 갱신할 객체를 명시적으로 잠그는 것

다른 트랜잭션이 동시에 같은 객체를 읽으려고 하면 첫 번째 read-modify-write 주기가 완료될 때까지 기다리도록 강제됨

```
BEGIN TRANSACTION;

SELECT * FROM figures
  WHERE name = 'robot' AND game_id = 222
  FOR UPDATE; (1)

-- 이동이 유효한지 확인한 후
-- 이전의 SELECT에서 반환된 것의 위치를 갱신한다.
UPDATE figures SET position = '4' WHERE id = 1234;

COMMIT;
```

**갱신 손실 자동 감지**

여러 트랜잭션의 병렬 실행을 허용하고 트랜잭션 관리자가 갱신 손실을 발견하면 트랜잭션을 abort 시키고, 재시도하도록 강제하는 방법

&#x20;**Compare-and-set**

값을 마지막으로 읽은 후로 변경되지 않았을 때만 갱신을 허용함으로써 갱신 손실을 회피하는 것

```
-- 데이터베이스 구현에 따라 안전할 수도 안전하지 않을 수도 있다
UPDATE wiki_pages SET content = 'new content'
  WHERE id = 1234 AND content = 'old content';
```

### 쓰기 스큐와 팬텀 <a href="#undefined" id="undefined"></a>

거의 동시에 두 트랜잭션이 시작되었다고 가정

<figure><img src="https://blog.kakaocdn.net/dn/by0TwT/btrU8EVVM6g/DJocXxwnnsjWxVI9ST0Nv0/img.png" alt=""><figcaption></figcaption></figure>

데이터베이스에서 스냅숏 격리를 사용하므로 둘 다 2를 반환해서 두 트랜잭션 모두 다음 단계로 진행함\
최소 한 명의 의사가 호출 대기해야 한다는 요구사항 위반\
이러한 현상을 쓰기 스큐 (wirte skew) 라고 함

**쓰기 스큐를 특정 짓기**

쓰기 스큐는 두 트랜잭션이 같은 객체들을 읽어서 그 중 일부를 갱신할 때 나타날 수 있음

**쓰기 스큐를 유발하는 팬텀**

어떤 트랜잭션에서 실행한 쓰기가 다른 트랜잭션의 검색 질의 결과를 바꾸는 것을 팬텀(Phantom) 이라고함\
스냅숏 격리는 읽기 전용 질의에서는 팬텀을 회피하지만 읽기 쓰기 트랜잭션에서는 팬텀이 발생할 수 있음

**충돌 구체화**

최초의 select 시 잠글 수 있는 객체가 없어 팬텀이 발생한다면 인위적으로 데이터베이스에 잠금 객체를 추가하자

대상 row 를 미리 만들고 lock 을 건다 → 트랜잭션 대상이 되는 특정 범위의 모든 조합에 대해 미리 row 를만들어 둠 (ex, 회의실 예약의 경우 다음 6개월 동안에 해당되는 양)

예약을 하는 트랜잭션은 테이블에서 원하는 대상 row 를 잠글 수 있음 (위에서 미리 생성했기 때문에)\
여기서 생성된 row 는 단지 동시에 변경되는 것을 막기 위한 잠금의 모음일 뿐임 (실제 사용되는 데이터가 아님)

동시성 제어 메커니즘이 애플리케이션 데이터모델로 새어 나오는 것은 보기 좋지 않음, 다른 대안이 불가능할 때 최후의 수단으로 고려

### 직렬성 <a href="#undefined" id="undefined"></a>

직렬성 격리는 보통 가장 강력한 경리 수준이라고 여겨짐

여러 트랜잭션이 병렬로 실행되더라도, 최종 결과는 동시성 없이 한 번에 하나씩 직렬로 실행될 때와 같도록 보장

직렬성을 제공하는 3가지 기법

```
말 그대로 트랜잭션을 순차적으로 실행하기
2단계 잠금
직렬성 스냅숏 격리 같은 낙관적 동시성 제어 기법
```

**실제적인 직렬 실행**

동시성 문제를 피하는 가장 간단한 방법은 동시성을 완전히 제거하는 것\
한 번에 트랜잭션 하나씩만 직렬로 단일 스레드에서 실행하면 됨\
단점 → 성능

**직렬 실행의 제약 사항**

트랜잭션 직렬 실행은 몇 가지 제약 사항 안에서 직렬성 격리를 획득하는 시용적인 방법이 됐음

```
모든 트랜잭션은 작고 빨라야 한다. 느린 트랜잭션 하나가 전체 처리를 지연시킬 수 있기 때문.
활성화된 데이터셋이 메모리에 적재될 수 있는 경우로 사용이 제한됨, 단일 스레드 트랜잭션에서 디스크에 접근한다면 시스템이 매우 느려짐
쓰기 처리량이 단일 CPU 코어에서 처리할 수 있을 정도로 충분히 낮아야 함
여러 파티션에 걸친 트랜잭션도 쓸 수 있지만, 이것을 사용할 수 있는 정도에는 엄격한 제한이 있음
```

**2단계 잠금(2PL)**

트랜잭션 A가 객체 하나를 읽고 트랜잭션 B가 그 객체에 쓰기를 원한다면 B는 진행하기 전에 A가 커밋되거나 어보트될 때까지 기다려야 한다(이렇게 하면 B가 A 몰래 갑자기 객체를 변경하지 못하도록 보장된다).

트랜잭션 A가 객체에 썼고 트랜잭션 B가 그 객체를 읽기 원한다면 B는 진행하기 전에 A가 커밋되거나 어보트될 때까지 기다려야 한다

vs 스냅숏 격리(읽는 쪽은 결코 쓰는 쪽을 막지 않으며, 쓰는 쪽도 결코 읽는 쪽을 막지 않음)

2PL은 직렬성을 제공하므로 스냅숏 격리에서 발생할 수 있는 갱신 손실이나 쓰기 스큐를 포함한 모든 경쟁 조건으로부터 자유롭다.

**2단계 잠금 구현**

MySQL(InnoDB), SQL Server 에서 직렬성 격리 수준을 구현하는데 사용됨

잠금은 공유 모드 (shared mode) 나 독점 모드 (exclusive mode) 로 사용될 수 있음\
잠금이 아주 많이 사용되므로 교착 상태(두 개의 트랜잭션이 서로 기다리는 것)가 매우 쉽게 발생할 수 있음

**2단계 잠금의 성능**

가장 큰 약점이 성능\
잠금을 획득하고 해제하는 오버헤드 때문에 느린 것\
더 중요한 원인은 동시성이 줄어들기 때문 (동시성과 성능은 반비례)

### 직렬성 스냅숏 격리 (Serializable Snapshot Isolation, SSI) <a href="#serializable-snapshot-isolation-ssi" id="serializable-snapshot-isolation-ssi"></a>

직렬성 격리와 좋은 성능은 공존할 수 있을까? - 직렬성 스냅숏 격리

현재 최고로 유망한 것이 직렬성 스냅숏 격리임\
스냅숏 격리에 비해 약간의 성능 손해만 있을 뿐임

**비관적 동시성 제어 vs 낙관적 동시성 제어**

2단계 잠금은 비관적 동시성 제어 메커니즘\
뭔가 잘못될 가능성이 있으면 뭔가를 하기 전에 상황이 다시 안전해질 때 까지 기다리는게 낫다는 원칙

직렬성 스냅숏 격리는 낙관적 동시성 제어 메커니즘

```
뭔가 위험한 상황이 발생할 가능성이 있을 때 트랜잭션을 막는 대신 모든 것이 괜찮아질 거라는 희망을 갖고 계속 진행한다는 뜻
트랜잭션이 커밋되기를 원할 때 데이터베이스는 나쁜 상황이 발생했는지 확인함
발생했다면 abort 되고 재시도함
경쟁이 심하면 abort 비율이 높아지므로 성능 떨어짐
예비 용량이 충분하고 트랜잭션 사이의 경쟁이 너무 심하지 않으면, 낙관적 동시성 제어 기법이 성능이 좋은 경향이 있음
SSI = 스냅숏 격리 + 직렬성 충돌 감지 및 abort 시킬 트랜잭션 결정하는 알고리즘
```
