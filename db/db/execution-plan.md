# Execution Plan

### MySQL 실행 계획 이해 및 최적화

#### 1. MySQL 실행 계획 개요

**1.1. 실행 계획이란?** MySQL 실행 계획은 데이터베이스가 SQL 쿼리를 실행할 때 선택하는 경로를 의미합니다. 이는 쿼리 최적화와 데이터베이스 성능 개선에 필수적입니다.&#x20;

실행 계획을 이해하고 최적화하면 데이터베이스 응답 시간을 크게 줄일 수 있습니다.

**1.2. 실행 계획을 확인하는 방법** 실행 계획을 확인하려면 `EXPLAIN` 키워드를 사용합니다. 이를 통해 MySQL이 쿼리를 어떻게 실행하는지 알 수 있습니다.&#x20;

또한 `EXPLAIN FORMAT=JSON`을 사용하면 더욱 상세한 정보를 얻을 수 있습니다.

```sql
EXPLAIN SELECT * FROM employees WHERE department_id = 10;
```

#### 2. 실행 계획 읽기

**`EXPLAIN` 출력 이해하기** `EXPLAIN` 명령어를 사용하면 다음과 같은 필드가 출력됩니다

```
id: 쿼리의 실행 순서 및 단계.
select_type: 쿼리의 유형 (예: SIMPLE, PRIMARY, SUBQUERY).
table: 접근하는 테이블.
type: 조인 유형 (예: ALL, index, range, ref, eq_ref, const, system, NULL).
possible_keys: 사용할 수 있는 인덱스.
key: 실제로 선택된 인덱스.
key_len: 선택된 인덱스의 길이.
ref: 조인 조건.
rows: 예상 스캔 행 수.
filtered: 필터링된 비율.
Extra: 추가 정보 (예: Using index, Using temporary, Using filesort).
```

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

