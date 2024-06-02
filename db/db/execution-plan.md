# Execution Plan

### MySQL 실행 계획

#### **실행 계획이란?**&#x20;

MySQL 실행 계획은 데이터베이스가 SQL 쿼리를 실행할 때 선택하는 경로를 의미합니다. 이는 쿼리 최적화와 데이터베이스 성능 개선에 필수적입니다.&#x20;

실행 계획을 이해하고 최적화하면 데이터베이스 응답 시간을 크게 줄일 수 있습니다.

#### **실행 계획을 확인하는 방법**&#x20;

실행 계획을 확인하려면 `EXPLAIN` 키워드를 사용합니다. 이를 통해 MySQL이 쿼리를 어떻게 실행하는지 알 수 있습니다.&#x20;

또한 `EXPLAIN FORMAT=JSON`을 사용하면 더욱 상세한 정보를 얻을 수 있습니다.

```sql
EXPLAIN select *
FROM 사원
where 사원번호 BETWEEN 10001 and 20000;

1,SIMPLE,사원,,range,PRIMARY,PRIMARY,4,,18728,100,Using where
```

## 실행 계획 읽기

이전 단락에서 `EXPLAIN` 키워드로 실행 계획을 조회하면 `id`, `select_type`, `table`, `type`, `key` 등의 정보가 출력되었습니다. 이때 출력되는 항목의 의미를 명확히 이해해야 합니다.&#x20;

### **ID**

실행 순서를 표시하는 숫자로, 쿼리가 실행되는 차례를 나타내는 것을 `id`로 표기합니다. \
조인을 할 때는 동일한 `id`가 표시됩니다. 즉, `id`의 숫자가 작을수록 먼저 수행된 것이며, `id`가 같은 값이라면 두 테이블의 조인이 이루어졌다고 해석할 수 있습니다.



### SELECT\_TYPE

`select_type`은 SQL 문을 구성하는 `SELECT` 문의 유형을 출력하는 항목입니다. \
`SELECT` 문이 단순히 `FROM` 절에 위치한 것인지, 서브쿼리인지, `UNION` 절로 묶인 `SELECT` 문인지 등의 정보를 제공합니다.

#### SIMPLE

`SIMPLE` 유형은 `UNION`이나 내부 쿼리가 없는 `SELECT` 문을 의미합니다. \
말 그대로 단순한 `SELECT` 구문으로만 작성된 경우를 가리킵니다.

```sql
EXPLAIN SELECT * FROM 사원 WHERE 사원번호 = 100000;
```

#### **PRIMARY**

`PRIMARY`는 서브쿼리가 포함된 SQL 문이 있을 때 첫 번째 `SELECT` 문에 해당하는 구문에 표시되는 유형입니다. 즉, 서브쿼리를 감싸는 외부 쿼리이거나, `UNION`이 포함된 SQL 문에서 첫 번째로 `SELECT` 키워드가 작성된 구문에 표시됩니다.

#### **SUBQUERY**

`DEPENDENT SUBQUERY`는 독립적으로 수행되는 서브쿼리를 의미합니다. \
`SELECT` 절의 스칼라 서브쿼리와 `WHERE` 절의 중첩 서브쿼리일 경우에 해당합니다.

#### **DERIVED**

`DERIVED`는 `FROM` 절에 작성된 서브쿼리라는 의미입니다. \
즉, `FROM` 절의 별도 임시 테이블인 인라인 뷰를 말합니다.&#x20;

다음 쿼리는 메인 쿼리의 `FROM` 절에서 급여 테이블의 데이터를 가져오는 인라인 뷰입니다.&#x20;

```sql
EXPLAIN
SELECT 사원.사원번호, 급여.연봉
FROM 사원,
(
    SELECT 사원번호, MAX(연봉) AS 연봉
    FROM 급여
    WHERE 사원번호 BETWEEN 10001 AND 20000
) AS 급여
WHERE 사원.사원번호 = 급여.사원번호;

```

#### **UNION**

`UNION` 및 `UNION ALL` 구문으로 합쳐진 `SELECT` 문에서 첫 번째 `SELECT` 구문을 제외한 이후의 `SELECT` 구문에 해당한다는 것을 나타냅니다. 이때 `UNION` 구문의 첫 번째 `SELECT` 절은 `PRIMARY` 유형으로 출력됩니다.

#### UNION RESULT

`UNION RESULT`는 `UNION ALL`이 아닌 `UNION` 구문으로 `SELECT` 절을 결합했을 때 출력됩니다. `UNION`은 출력 결과에 중복이 없는 유일한 속성을 가지므로 각 `SELECT` 절에서 데이터를 가져와 정렬하여 중복 체크하는 과정을 거칩니다.&#x20;

따라서 `UNION RESULT`는 별도의 메모리 또는 디스크에 임시 테이블을 만들어 중복을 제거하겠다는 의미로 해석할 수 있습니다.&#x20;

한편 `UNION` 구문으로 결합되기 전의 각 `SELECT` 문이 중복되지 않는 결과가 보장될 때는 `UNION` 구문보다는 `UNION ALL` 구문으로 변경하는 SQL 튜닝을 수행합니다.

#### DEPENDENT SUBQUERY

`DEPENDENT SUBQUERY`는 `UNION` 또는 `UNION ALL`을 사용하는 서브쿼리가 메인 테이블의 영향을 받는 경우를 의미하며, `UNION`으로 연결된 단위 쿼리들 중 처음으로 작성한 단위 쿼리에 해당합니다.&#x20;

즉, `UNION`으로 연결되는 첫 번째 단위 쿼리가 독립적으로 수행하지 못하고 메인 테이블로부터 값을 하나씩 공급받는 구조 (`AND 사원.사원번호 = 관리자.사원번호`)이므로 성능적으로 불리하여 SQL 문이 튜닝 대상이 됩니다.

#### DEPENDENT UNION

`DEPENDENT UNION`은 `UNION` 또는 `UNION ALL`을 사용하는 서브쿼리가 메인 테이블의 영향을 받는 경우로, `UNION`으로 연결된 단위 쿼리 중 첫 번째 단위 쿼리를 제외하고 두 번째 단위 쿼리에 해당됩니다.&#x20;

즉, `UNION`으로 연결되는 두 번째 이후의 단위 쿼리가 독립적으로 수행하지 못하고 메인 테이블로부터 값을 하나씩 공급받는 구조 (`AND 사원2.사원번호 = 관리자.사원번호`)이므로 성능적으로 불리하여 SQL 문이 튜닝 대상이 됩니다.

#### UNCACHEABLE SUBQUERY

`UNCACHEABLE SUBQUERY`는 말 그대로 메모리에 상주하여 재활용되어야 할 서브쿼리가 재사용되지 못할 때 출력되는 유형입니다.&#x20;

해당 서브쿼리 안에 사용자 정의 함수나 사용자 변수가 포함되거나 `RAND()`, `UUID()` 함수 등을 사용하여 매번 조회 시마다 결과가 달라지는 경우에 해당됩니다. \
만약 자주 호출되는 SQL 문이라면 메모리에 서브쿼리 결과가 상주할 수 있도록 변경하는 방향으로 SQL 튜닝을 검토해볼 수 있습니다.

#### MATERIALIZED

`MATERIALIZED`는 `IN` 절 구문에 연결된 서브쿼리가 임시 테이블을 생성한 뒤, 조인이나 가공 작업을 수행할 때 출력되는 유형입니다. 즉, `IN` 절의 서브쿼리를 임시 테이블로 만들어서 조인 작업을 수행하는 것을 의미합니다.

다음 SQL 문에서는 `IN` 절 구문의 서브쿼리 (`SELECT 사원번호 FROM 급여 WHERE 시작일자 > '2020-01-01'`)가 임시 테이블을 생성하고, 이후 사원 테이블과 조인을 수행함을 확인할 수 있습니다.&#x20;

```sql
EXPLAIN
SELECT 사원.*
FROM 사원
WHERE 사원번호 IN (
    SELECT 사원번호
    FROM 급여
    WHERE 시작일자 > '2020-01-01'
);
```

### TABLE

`table`은 말 그대로 테이블명을 표시하는 항목입니다. 실행 계획 정보에서 테이블명이나 테이블 별칭(alias)을 출력하며, 서브쿼리나 임시 테이블을 만들어서 별도의 작업을 수행할 때는 `<subquery#>`나 `<derived#>`라고 출력됩니다.&#x20;

```sql
EXPLAIN
SELECT 사원.사원번호, 급여.연봉
FROM 사원,
(
    SELECT 사원번호, MAX(연봉) AS 연봉
    FROM 급여
    WHERE 사원번호 BETWEEN 10001 AND 20000
) AS 급여
WHERE 사원.사원번호 = 급여.사원번호;

```

#### 실행 계획 예시

```
+----+-------------+-----------+---------+-------+--------------+------+----------+-------------+
| id | select_type | table     | type    | rows  | filtered     | Extra                               |
+----+-------------+-----------+---------+-------+--------------+------+----------+-------------+
|  1 | PRIMARY     | <derived2>| system  | NULL  | NULL         |                                      |
|  1 | PRIMARY     | 사원      | const   | NULL  | NULL         | Using index                        |
|  2 | DERIVED     | 급여      | range   | 3     | 100.00       | Using where                        |
+----+-------------+-----------+---------+-------+--------------+------+----------+-------------+
```

#### partitions

는 실행 계획의 부가 정보로, 데이터가 저장된 논리적인 영역을 표시하는 항목입니다. \
사전에 정의한 전체 파티션 중 특정 파티션에 선택적으로 접근하는 것이 SQL 성능 측면에서 유리합니다. \
만약 너무 많은 영역의 파티션에 접근하는 것으로 출력된다면 파티션 정의를 튜닝해봐야 할 것입니다.

### TYPE

`type`은 테이블의 데이터를 어떻게 찾을지에 관한 정보를 제공하는 항목입니다. \
테이블을 처음부터 끝까지 전부 확인할지, 아니면 인덱스를 통해 바로 데이터를 찾아갈지 등을 해석할 수 있습니다.

* `type`: 조인 유형 (예: ALL, index, range, ref, eq\_ref, const, system, NULL).
* `possible_keys`: 사용할 수 있는 인덱스.
* `key`: 실제로 선택된 인덱스.
* `key_len`: 선택된 인덱스의 길이.
* `ref`: 조인 조건.
* `rows`: 예상 스캔 행 수.
* `filtered`: 필터링된 비율.
* `Extra`: 추가 정보 (예: Using index, Using temporary, Using filesort).

#### 3. 실행 계획 최적화

**3.1. 인덱스 사용** 인덱스는 쿼리 성능에 큰 영향을 미칩니다. 인덱스를 적절하게 사용하면 데이터 검색 속도를 크게 향상시킬 수 있습니다.

```sql
CREATE INDEX idx_department_id ON employees(department_id);
```

**3.2. 조인 최적화** 조인 순서와 전략을 최적화하면 쿼리 성능을 개선할 수 있습니다. MySQL은 Nested Loop 조인을 기본으로 사용하지만, 필요한 경우 조인 순서를 변경하여 성능을 향상시킬 수 있습니다.

```sql
SELECT e.name, d.department_name
FROM employees e
JOIN departments d ON e.department_id = d.department_id;
```

**3.3. 쿼리 재작성** 쿼리 구조를 개선하여 실행 계획을 최적화할 수 있습니다. 서브쿼리를 조인으로 변경하거나, 불필요한 컬럼을 제거하여 쿼리 성능을 향상시킬 수 있습니다.

```sql
-- 서브쿼리 예시
SELECT name FROM employees WHERE department_id IN (SELECT department_id FROM departments WHERE location = 'New York');

-- 조인으로 변경
SELECT e.name
FROM employees e
JOIN departments d ON e.department_id = d.department_id
WHERE d.location = 'New York';
```

#### 4. 고급 주제

**4.1. 실행 계획 캐시** MySQL은 실행 계획을 캐시하여 반복적인 쿼리의 성능을 개선합니다. 이를 통해 동일한 쿼리에 대해 실행 계획을 재사용할 수 있습니다.

**4.2. 성능 모니터링 도구** MySQL Workbench, Percona Toolkit, pt-query-digest 등의 도구를 사용하여 쿼리 성능을 모니터링하고 최적화할 수 있습니다.

**4.3. MySQL 최신 버전 기능** MySQL 최신 버전에서는 실행 계획과 관련된 여러 개선 사항이 도입되었습니다. 이를 활용하여 더 나은 성능을 달성할 수 있습니다.

