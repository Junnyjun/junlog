# Cardinality

중복의 빈도와 반비례하는 지표를 의미한다.

<img src="../../../.gitbook/assets/file.excalidraw (12).svg" alt="" class="gitbook-drawing">

위 같은 값이 존재하는 테이블이 존재하는 경우.\
CLASS 는 A,B,C,D,E 5개 이고, NUMBER 는 1,2,3,5,6,7,8,9 8개이다 \
따라서 중복 된 값이 더 적은 `CLASS가 Cardinality가 높다` 고 표현할 수 있다.

{% hint style="info" %}
Dinstinct (CLASS)  > Dinstinct(NUMBER) 로 카디널리티 비교를 할 수있다.
{% endhint %}

데이터 조회시 최대한 많은 데이터가 걸러져 선택되어야 성능상 이점이 있을것이다.

### 활용

실제 인덱스를 걸지 않고 데이터 수로 비교하여 가독성을 고려하여  설명

#### ID&#x20;

Dinstinct(ID)의  개수는 N개 이므로 가장 카디널리티가 높은 경우이다.

```sql
select * from CLASS C where C.ID=1
```

은 조회시 C.id=1인 케이스만 가져오기 때문에 걸러지는 경우는  (N-1)개로 가장 우월한 성능을 가진다.

#### CLASS

Dinstinct(CLASS)의  중복된 값이 가장 자주 나오는 (Cardinality 가 가장 낮은) 케이스이다.

```sql
select * from CLASS C where C.CLASS='A'
```

은 조회시 가장 많은 중복된 데이터를 가져오기 때문에 가장 낮은 성능을 가진다.

