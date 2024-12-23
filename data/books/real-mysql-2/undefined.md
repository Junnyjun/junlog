# 쿼리 작성 및 최적화

애플리케이션에서 입력된 데이터를 데이터베이스에 저장하거나 데이터베이스로부터 필요한 데이터를 가져오려면 SQL이라는 정형화된 언어를 사용해야 합니다. 데이터베이스나 테이블의 구조를 변경하기 위한 명령어는 DDL(Data Definition Language)이라 하며, 테이블의 데이터를 조작(읽고, 쓰기)하기 위한 명령어는 DML(Data Manipulation Language)이라고 합니다.

애플리케이션에서 데이터를 저장 또는 조회하기 위해 데이터베이스와 통신할 때 데이터베이스 서버로 전달되는 것은 SQL뿐입니다. SQL은 어떠한(What) 데이터를 요청하기 위한 언어이지, 어떻게(How) 데이터를 읽을지를 표현하는 언어는 아니므로 C나 자바 같은 언어와 비교했을 때 상당히 제한적으로 느껴질 수 있습니다. 따라서 쿼리가 빠르게 수행되게 하려면 데이터베이스 서버에서 쿼리가 어떻게 요청을 처리할지 예측할 수 있어야 합니다. 이를 위해 쿼리를 작성하는 방법이나 규칙뿐만 아니라 내부적인 처리 방식(옵티마이저)에 대한 어느 정도의 지식이 필요합니다.

애플리케이션 코드를 튜닝해서 성능을 2배 개선하는 것은 쉽지 않은 일이지만, DBMS에서 몇십 배에서 몇백 배의 성능 향상이 이루어지는 것은 상당히 흔한 일입니다. SQL 처리에서 "어떻게(How)"를 이해하고 쿼리를 작성하는 것이 그만큼 중요하다는 것을 의미합니다. 이번 장에서는 쿼리의 패턴별로 "어떻게 처리되는가?"를 살펴보고, 프로그램 코드를 상당히 줄일 수 있는 유용한 쿼리 패턴도 함께 살펴보겠습니다.

#### 11.1 쿼리 작성과 연관된 시스템 변수

대소문자 구분, 문자열 표기 방법 등과 같은 SQL 작성 규칙은 MySQL 서버의 시스템 설정에 따라 달라집니다. 이번 절에서는 MySQL 서버의 시스템 설정이 쿼리에 어떤 영향을 주는지 살펴보고, MySQL의 예약어는 어떤 것이 있으며 이러한 예약어를 사용할 때 주의해야 할 사항도 함께 다루겠습니다.

**11.1.1 SQL 모드**

MySQL 서버의 `sql_mode`라는 시스템 설정에는 여러 개의 값이 동시에 설정될 수 있습니다. 그중에서 대표적으로 SQL의 작성과 결과에 영향을 미치는 값들은 다음과 같습니다. MySQL 서버의 설정 파일에서 `sql_mode`를 설정할 때는 구분자 `,`를 이용해 다음에 설명되는 키워드를 동시에 설정할 수 있습니다.

**주의:** MySQL 서버의 `sql_mode` 시스템 변수에 설정된 값들은 SQL 문장 작성 규칙뿐만 아니라 MySQL 서버 내부적으로 자동 실행되는 데이터 타입 변환 및 기본값 제어 등과 관련된 옵션도 포함하고 있습니다. 따라서 이미 MySQL 서버에 사용자 테이블을 생성하고 데이터를 저장하기 시작했다면 가능한 한 `sql_mode` 시스템 변수의 내용을 변경하지 않는 것이 좋습니다. 또한 하나의 복제 그룹에 속한 모든 MySQL 서버들은 동일한 `sql_mode` 시스템 변수를 유지하는 것이 바람직합니다. `sql_mode` 시스템 변수를 변경해야 하는 경우 MySQL 서버가 자동으로 실행하는 데이터 타입 변환이나 기본값 설정에 영향을 미치는지 확인한 후 적용하는 것이 좋습니다.

MySQL 서버에 익숙하지 않다면 가능하면 `sql_mode` 시스템 변수를 변경하지 않고 기본값을 그대로 사용하는 것도 좋은 방법입니다. MySQL 8.0 서버의 `sql_mode` 기본값은 다음 옵션들로 구성되어 있습니다:

* `ONLY_FULL_GROUP_BY`
* `STRICT_TRANS_TABLES`
* `NO_ZERO_IN_DATE`
* `NO_ZERO_DATE`
* `ERROR_FOR_DIVISION_BY_ZERO`
* `NO_ENGINE_SUBSTITUTION`

MySQL 8.0 이전 버전에서 MySQL 8.0 버전으로 업그레이드하는 경우라면 기존 버전의 `sql_mode` 시스템 변수에 설정된 값을 그대로 유지하는 것이 호환성 측면에서 좋을 것입니다. 기존 버전에서 `sql_mode` 시스템 변수를 별도로 설정하지 않고 기본값을 사용하는 상태에서 MySQL 8.0으로 업그레이드를 해야 한다면 `sql_mode` 시스템 변수의 `ONLY_FULL_GROUP_BY` 옵션에 주의해야 합니다.

* **STRICT\_ALL\_TABLES & STRICT\_TRANS\_TABLES:** MySQL 서버에서 `INSERT`나 `UPDATE` 문장으로 데이터를 변경하는 경우, 칼럼의 타입과 저장되는 값의 타입이 다를 때 자동으로 타입 변경을 수행합니다. 이때 타입이 적절히 변환되기 어렵거나 칼럼에 저장될 값이 없거나 값의 길이가 칼럼의 최대 길이보다 큰 경우 MySQL 서버가 `INSERT`나 `UPDATE` 문장을 계속 실행할지, 아니면 에러를 발생시킬지를 결정합니다. `STRICT_TRANS_TABLES` 옵션은 InnoDB 같은 트랜잭션을 지원하는 스토리지 엔진에만 엄격한 모드(Strict Mode)를 적용하며, `STRICT_ALL_TABLES` 옵션은 트랜잭션 지원 여부와 무관하게 모든 스토리지 엔진에 대해 엄격한 모드(Strict Mode)를 적용합니다. 이 옵션들은 사용자가 원하지 않는 방향으로 값의 자동 변환이 유발될 수도 있으므로 MySQL 서버를 서비스에 적용하기 전에 반드시 활성화할 것을 권장합니다. 서비스 도중에 이 두 옵션을 변경해야 한다면 응용 프로그램에서 사용하는 `INSERT`와 `DELETE` 문장을 검토해서 의도하지 않은 결과가 발생하지 않도록 주의해야 합니다.
* **ANSI\_QUOTES:** MySQL에서는 문자열 값(리터럴)을 표현하기 위해 홑따옴표와 쌍따옴표를 동시에 사용할 수 있습니다. 하지만 오라클 같은 DBMS에서는 홑따옴표를 문자열 값을 표기하는 데 사용하고, 쌍따옴표는 칼럼명이나 테이블명과 같은 식별자를 구분하는 용도로만 사용합니다. MySQL에 익숙하지 않은 사용자에게는 혼란스러울 수 있으며, 하나의 SQL 문장에서 홑따옴표와 쌍따옴표가 엉켜 있으면 가독성이 떨어질 수 있습니다. `sql_mode` 시스템 변수에 `ANSI_QUOTES`를 설정하면 홑따옴표만 문자열 값 표기로 사용할 수 있고, 쌍따옴표는 칼럼명이나 테이블명과 같은 식별자를 표기하는 데만 사용할 수 있습니다.
* **ONLY\_FULL\_GROUP\_BY:** MySQL의 쿼리에서는 `GROUP BY` 절에 포함되지 않은 칼럼이더라도 집합 함수의 사용 없이 그대로 `SELECT` 절이나 `HAVING` 절에 사용할 수 있습니다. 이는 SQL 표준이나 다른 DBMS와는 다른 동작 방식인데, `sql_mode` 시스템 변수에 `ONLY_FULL_GROUP_BY`를 설정해서 SQL 문법에 좀 더 엄격한 규칙을 적용할 수 있습니다. MySQL 5.7 버전까지는 이 옵션이 기본값으로 비활성화되어 있었지만 MySQL 8.0 버전부터는 기본적으로 활성화되어 있습니다. 따라서 MySQL 8.0으로 업그레이드하는 경우라면 `sql_mode` 시스템 변수의 `ONLY_FULL_GROUP_BY` 옵션에 특별히 주의해야 합니다. `ONLY_FULL_GROUP_BY` 옵션이 활성화되면 `GROUP BY` 절이 사용된 문장의 `SELECT` 절에는 `GROUP BY` 절에 명시된 칼럼과 집계 함수만 사용할 수 있습니다. `SELECT` 절에 집계 함수가 사용되는 경우 `GROUP BY` 절에 명시되지 않은 칼럼도 집계 함수의 인자로 사용할 수 있습니다.
* **PIPE\_AS\_CONCAT:** MySQL에서 `"|"`는 `OR` 연산자와 같은 의미로 사용됩니다. 하지만 `sql_mode` 시스템 변수에 `PIPE_AS_CONCAT` 값을 설정하면 오라클과 같이 문자열 연결 연산자(`CONCAT`)로 사용할 수 있습니다. 이 설정을 활성화하면 불리언 표현식의 결합 연산자로 `&&` 연산자를 사용할 수 있지만, `||` 연산자는 사용할 수 없습니다. SQL의 가독성을 높이기 위해 다른 용도로 사용될 수 있는 `&&` 연산자와 `||` 연산자는 사용을 자제하는 것이 좋습니다.
* **PAD\_CHAR\_TO\_FULL\_LENGTH:** MySQL에서는 `CHAR` 타입이라고 하더라도 `VARCHAR`처럼 유효 문자열 뒤의 공백 문자는 제거되어 반환됩니다. 이는 주로 애플리케이션 개발자에게 민감한 부분인데, 자동으로 불필요한 공백 문자를 제거하는 방식이 더 편리할 수 있습니다. 하지만 `CHAR` 타입의 칼럼값을 가져올 때 뒤쪽의 공백이 제거되지 않고 반환돼야 한다면 `sql_mode` 시스템 설정에 `PAD_CHAR_TO_FULL_LENGTH`를 추가하면 됩니다.
* **NO\_BACKSLASH\_ESCAPES:** MySQL에서도 일반적인 프로그래밍 언어에서처럼 역슬래시 문자를 이스케이프 문자로 사용할 수 있습니다. `sql_mode` 시스템 설정에 `NO_BACKSLASH_ESCAPES`를 추가하면 역슬래시를 문자의 이스케이프 용도로 사용할 수 없게 됩니다. 이 설정을 활성화하면 역슬래시 문자도 다른 문자와 동일하게 취급됩니다.
* **REAL\_AS\_FLOAT:** MySQL 서버에서는 부동 소수점 타입으로 `FLOAT`과 `DOUBLE` 타입을 지원하며, `REAL` 타입은 `DOUBLE` 타입의 동의어로 사용됩니다. 하지만 `REAL_AS_FLOAT` 모드가 활성화되면 MySQL 서버는 `REAL` 타입을 `FLOAT` 타입의 동의어로 바꿉니다.

**11.1.2 예약어 사용 시 주의사항**

MySQL의 예약어는 특정 기능을 수행하기 위해 이미 사용되고 있는 단어들입니다. 이러한 예약어를 테이블명이나 칼럼명으로 사용할 때는 특별한 주의가 필요합니다. 예약어를 테이블명이나 칼럼명으로 사용하려면 역따옴표(`)로 감싸야 합니다. 예를 들어,` SELECT\`라는 칼럼명을 사용하고 싶다면 다음과 같이 작성해야 합니다:

```sql
SELECT `SELECT` FROM `TABLE`;
```

예약어를 사용하지 않는 것이 가장 안전하지만, 불가피하게 사용해야 할 경우에는 역따옴표를 활용하여 혼동을 방지해야 합니다.

#### 11.2 매뉴얼의 SQL 문법 표기를 읽는 방법

MySQL 매뉴얼에서 제공하는 SQL 문법 표기를 이해하는 것은 효과적인 쿼리 작성과 최적화에 매우 중요합니다. 매뉴얼에서는 다양한 SQL 명령어와 옵션을 다음과 같은 방식으로 표기합니다:

* **대괄호(\[]):** 선택적인 요소를 나타냅니다. 예를 들어, `ALTER TABLE table_name [option]`에서 `option`은 선택적으로 사용할 수 있는 부분입니다.
* **꺾쇠괄호(<>):** 필수적인 요소를 나타냅니다. 예를 들어, `CREATE TABLE <table_name>`에서 `<table_name>`은 반드시 제공해야 하는 부분입니다.
* **파이프(|):** 여러 개의 선택지 중 하나를 나타냅니다. 예를 들어, `INT | VARCHAR`는 `INT`나 `VARCHAR` 중 하나를 선택할 수 있음을 의미합니다.
* **별표(\*):** 0개 이상의 반복을 의미합니다. 예를 들어, `column_name*`은 `column_name`이 0번 이상 반복될 수 있음을 의미합니다.

매뉴얼의 이러한 표기법을 이해하면 복잡한 SQL 명령어도 쉽게 해석하고 사용할 수 있습니다.

#### 11.3 MySQL 연산자와 내장 함수

MySQL에서 제공하는 다양한 연산자와 내장 함수는 데이터베이스 쿼리를 작성하고 최적화하는 데 필수적입니다. 이번 절에서는 MySQL에서 자주 사용되는 연산자와 내장 함수에 대해 살펴보겠습니다.

**11.3.1 동등(Equal) 비교 (=, <=>)**

동등 비교는 다른 DBMS와 마찬가지로 `=` 기호를 사용하여 비교를 수행할 수 있습니다. 하지만 MySQL에서는 동등 비교를 위해 `<=>` 연산자도 제공합니다. `<=>` 연산자는 `=` 연산자와 유사하지만, `NULL` 값에 대한 비교를 처리할 수 있는 점이 다릅니다. 이를 "NULL-Safe" 비교 연산자라고 합니다. 예를 들어:

```sql
SELECT 1 = 1, NULL = NULL, 1 = NULL;
```

위의 쿼리 결과는 다음과 같습니다:

```
+---------+---------+----------+
| 1 = 1   | NULL = NULL | 1 = NULL |
+---------+---------+----------+
|       1 |    NULL |     NULL |
+---------+---------+----------+
```

반면, `<=>` 연산자를 사용하면 `NULL` 값을 안전하게 비교할 수 있습니다:

```sql
SELECT 1 <=> 1, NULL <=> NULL, 1 <=> NULL;
```

결과는 다음과 같습니다:

```
+------------+------------+------------+
| 1 <=> 1    | NULL <=> NULL | 1 <=> NULL |
+------------+------------+------------+
|          1 |          1 |          0 |
+------------+------------+------------+
```

즉, `<=>` 연산자는 양쪽 비교 대상이 모두 `NULL`인 경우 `TRUE(1)`를 반환하고, 한쪽만 `NULL`인 경우 `FALSE(0)`를 반환합니다.

**11.3.2 부정(Not-Equal) 비교 (!=, <>)**

"같지 않다" 비교를 위한 연산자는 `<>`와 `!=` 두 가지가 있습니다. 두 연산자는 기능적으로 동일하며, 어느 것을 사용하든 특별한 문제가 되지는 않습니다. 다만, 가독성을 위해 하나의 연산자를 통일해서 사용하는 것이 좋습니다. 예를 들어:

```sql
SELECT 1 <> 2, 1 != 2;
```

결과는 다음과 같습니다:

```
+-------+-------+
| 1 <> 2 | 1 != 2 |
+-------+-------+
|     1 |     1 |
+-------+-------+
```

두 연산자 모두 `TRUE(1)`를 반환합니다.

**11.3.3 NOT 연산자 (!)**

`NOT` 연산자는 `TRUE` 또는 `FALSE` 연산의 결과를 반대로 만드는 연산자입니다. MySQL에서는 `NOT` 뿐만 아니라 `!` 연산자도 같은 목적으로 사용할 수 있습니다. 예를 들어:

```sql
SELECT !1, !FALSE;
SELECT NOT 1, NOT 0, NOT (1=1);
```

결과는 다음과 같습니다:

```
+------+---------+
| !1   | !FALSE  |
+------+---------+
|    0 |       1 |
+------+---------+

+-------+-------+-------------+
| NOT 1 | NOT 0 | NOT (1=1)    |
+-------+-------+-------------+
|     0 |     1 |           0 |
+-------+-------+-------------+
```

`!1`은 `FALSE(0)`를 반환하고, `!FALSE`는 `TRUE(1)`를 반환합니다. `NOT` 연산자는 불리언 값뿐만 아니라 숫자나 문자열 표현식에서도 사용할 수 있지만, 부정의 결과를 정확히 예측할 수 없는 경우에는 사용을 자제하는 것이 좋습니다.

**11.3.4 AND(&&) 와 OR(||) 연산자**

일반적으로 DBMS에서는 불리언 표현식의 결과를 결합하기 위해 `AND`와 `OR`를 사용합니다. MySQL에서는 `AND`와 `OR` 뿐만 아니라 `&&`와 `||`의 사용도 허용됩니다. `&&`는 `AND` 연산자와 같으며, `||`는 `OR` 연산자와 같습니다. 그러나 SQL의 가독성을 높이기 위해 이러한 단축 연산자보다는 `AND`와 `OR`를 사용하는 것이 좋습니다. 예를 들어:

```sql
SELECT 'abcdef' LIKE 'abc%';
SELECT 'abcdef' LIKE '%abc';
SELECT 'abcdef' LIKE '%ef';

SELECT 'abcdef' && 'ghijkl'; -- 논리적 의미로 해석하기 어려움
```

첫 번째 쿼리는 `abcdef`가 `abc`로 시작하는지 확인하여 `TRUE(1)`를 반환합니다. 두 번째 쿼리는 `abcdef`가 `abc`를 포함하지 않으므로 `FALSE(0)`를 반환합니다. 세 번째 쿼리는 `abcdef`가 `ef`로 끝나므로 `TRUE(1)`를 반환합니다.

단축 연산자인 `&&`와 `||`는 혼용될 경우 가독성이 떨어질 수 있으므로, 가능하면 `AND`와 `OR`를 사용하는 것이 좋습니다.

**11.3.5 비교 연산자**

MySQL에서는 다양한 비교 연산자를 제공하여 데이터를 비교할 수 있습니다. 그 중에서도 `BETWEEN`과 `IN` 연산자는 자주 사용됩니다.

*   **BETWEEN:** 특정 범위 내에 있는지 확인할 때 사용합니다.

    ```sql
    SELECT * FROM dept_emp
    WHERE dept_no BETWEEN 'd003' AND 'd005' AND emp_no = 10001;
    ```
*   **IN:** 여러 값 중 하나와 일치하는지 확인할 때 사용합니다.

    ```sql
    SELECT * FROM dept_emp
    WHERE dept_no IN ('d003', 'd004', 'd005') AND emp_no = 10001;
    ```

`BETWEEN`과 `IN` 연산자의 성능 차이는 쿼리의 실행 계획을 통해 확인할 수 있습니다. 일반적으로 `IN` 연산자는 여러 번의 동등 비교를 수행하는 것과 같은 효과가 있기 때문에 인덱스를 최적으로 사용할 수 있습니다. 반면, `BETWEEN` 연산자는 범위 검색을 수행하게 되어 레인지 스캔이 필요할 수 있습니다.

**11.3.6 정규 표현식 연산자(REGEXP)**

MySQL에서는 정규 표현식을 사용하여 문자열을 패턴 매칭할 수 있는 `REGEXP` 연산자를 제공합니다. 정규 표현식은 복잡한 패턴을 정의할 수 있으며, 이를 통해 특정 조건을 만족하는 데이터를 효율적으로 검색할 수 있습니다. 예를 들어:

```sql
SELECT * FROM employees
WHERE first_name REGEXP '^A.*e$';
```

위의 쿼리는 `first_name`이 'A'로 시작하고 'e'로 끝나는 모든 직원의 데이터를 조회합니다.

**주의:** `REGEXP` 연산자를 사용할 때는 인덱스 레인지 스캔을 사용할 수 없기 때문에 성능에 유의해야 합니다. 가능하다면 데이터 조회 범위를 줄일 수 있는 조건과 함께 사용하는 것이 좋습니다.

#### 11.4 SELECT

웹 서비스와 같은 일반적인 온라인 트랜잭션 처리 환경의 데이터베이스에서는 `INSERT`나 `UPDATE` 같은 작업은 거의 레코드 단위로 발생하므로 성능상 문제가 되는 경우는 별로 없습니다. 하지만 `SELECT`는 여러 개의 테이블로부터 데이터를 조합해서 빠르게 가져와야 하기 때문에 여러 개의 테이블을 어떻게 읽을 것인가에 많은 주의를 기울여야 합니다. 하나의 애플리케이션에서 사용되는 쿼리 중에서도 `SELECT` 쿼리의 비율이 높습니다. 이번 절에서는 `SELECT` 쿼리의 각 부분에 사용될 수 있는 기능을 성능 위주로 살펴보겠습니다.

**11.4.1 SELECT 절의 처리 순서**

`SELECT` 문장이라고 하면 SQL 전체를 의미합니다. `SELECT` 키워드와 실제 가져올 칼럼을 명시한 부분만 언급할 때는 `SELECT` 절이라고 표현하겠습니다. 여기서 절이란 주로 알고 있는 키워드(`SELECT`, `FROM`, `JOIN`, `WHERE`, `GROUP BY`, `HAVING`, `ORDER BY`, `LIMIT`)와 그 뒤에 기술된 표현식을 묶어서 말합니다.

다음 예제는 여러 가지 절이 포함된 쿼리입니다:

```sql
SELECT s.emp_no, COUNT(DISTINCT e.first_name) AS cnt
FROM salaries s
INNER JOIN employees e ON e.emp_no = s.emp_no
WHERE s.emp_no IN (10001, 10002)
GROUP BY s.emp_no
HAVING AVG(s.salary) > 1000
ORDER BY AVG(s.salary)
LIMIT 10;
```

이 쿼리 예제를 각 절로 나눠보면 다음과 같습니다:

* **SELECT 절:** `SELECT s.emp_no, COUNT(DISTINCT e.first_name) AS cnt`
* **FROM 절:** `FROM salaries s INNER JOIN employees e ON e.emp_no = s.emp_no`
* **WHERE 절:** `WHERE s.emp_no IN (10001, 10002)`
* **GROUP BY 절:** `GROUP BY s.emp_no`
* **HAVING 절:** `HAVING AVG(s.salary) > 1000`
* **ORDER BY 절:** `ORDER BY AVG(s.salary)`
* **LIMIT 절:** `LIMIT 10`

위의 예제 쿼리는 `SELECT` 문장에 지정할 수 있는 대부분의 절이 포함되어 있습니다. 때로는 이런 쿼리에서 어느 절이 먼저 실행될지 예측하지 못할 때가 있는데, 어느 절이 먼저 실행되는지를 모르면 처리 내용이나 처리 결과를 예측할 수 없습니다.

**11.4.2 SELECT 절의 최적화**

`SELECT` 절은 데이터를 조회하는 데 있어 가장 중요한 부분 중 하나입니다. 효율적인 `SELECT` 절 작성은 쿼리의 성능을 크게 향상시킬 수 있습니다. 다음과 같은 최적화 기법을 활용할 수 있습니다:

*   **필요한 칼럼만 선택:** `SELECT *` 대신 필요한 칼럼만 선택하여 불필요한 데이터 전송을 줄입니다.

    ```sql
    SELECT emp_no, first_name, last_name FROM employees WHERE emp_no = 10001;
    ```
*   **인덱스 활용:** `WHERE` 절과 `JOIN` 조건에 인덱스를 적절히 활용하여 검색 속도를 향상시킵니다.

    ```sql
    SELECT e.emp_no, e.first_name, d.dept_name
    FROM employees e
    INNER JOIN dept_emp de ON e.emp_no = de.emp_no
    INNER JOIN departments d ON de.dept_no = d.dept_no
    WHERE d.dept_no = 'd001';
    ```
*   **집계 함수의 효율적 사용:** `COUNT`, `SUM`, `AVG` 등의 집계 함수를 효율적으로 사용하여 불필요한 연산을 줄입니다.

    ```sql
    SELECT dept_no, COUNT(*) AS num_employees
    FROM dept_emp
    GROUP BY dept_no;
    ```

**11.4.3 WHERE 절의 최적화**

`WHERE` 절은 데이터베이스에서 데이터를 필터링하는 중요한 부분입니다. 효율적인 `WHERE` 절 작성은 쿼리의 성능을 크게 향상시킬 수 있습니다. 다음과 같은 최적화 기법을 활용할 수 있습니다:

*   **인덱스 사용:** `WHERE` 절의 조건에 인덱스를 활용하여 검색 속도를 향상시킵니다.

    ```sql
    SELECT * FROM employees WHERE last_name = 'Smith';
    ```
*   **함수 사용 최소화:** `WHERE` 절에서 함수 사용을 최소화하여 인덱스 활용을 극대화합니다. 함수가 사용되면 인덱스가 무시될 수 있습니다.

    ```sql
    -- 비효율적인 예
    SELECT * FROM employees WHERE YEAR(hire_date) = 2020;

    -- 효율적인 예
    SELECT * FROM employees WHERE hire_date BETWEEN '2020-01-01' AND '2020-12-31';
    ```
*   **논리 연산자 최적화:** `AND`, `OR` 연산자를 효율적으로 사용하여 검색 범위를 줄입니다.

    ```sql
    SELECT * FROM employees
    WHERE dept_no IN ('d001', 'd002') AND salary > 50000;
    ```

**11.4.4 JOIN 절의 최적화**

`JOIN` 절은 여러 테이블 간의 관계를 맺고 데이터를 조합하는 데 사용됩니다. 효율적인 `JOIN` 절 작성은 쿼리의 성능을 크게 향상시킬 수 있습니다. 다음과 같은 최적화 기법을 활용할 수 있습니다:

*   **적절한 JOIN 타입 선택:** `INNER JOIN`, `LEFT JOIN`, `RIGHT JOIN` 등 적절한 JOIN 타입을 선택하여 불필요한 데이터를 제외합니다.

    ```sql
    SELECT e.emp_no, e.first_name, d.dept_name
    FROM employees e
    INNER JOIN dept_emp de ON e.emp_no = de.emp_no
    INNER JOIN departments d ON de.dept_no = d.dept_no;
    ```
*   **인덱스 활용:** `JOIN` 조건에 인덱스를 활용하여 검색 속도를 향상시킵니다.

    ```sql
    SELECT e.emp_no, e.first_name, d.dept_name
    FROM employees e
    INNER JOIN dept_emp de ON e.emp_no = de.emp_no
    INNER JOIN departments d ON de.dept_no = d.dept_no
    WHERE d.dept_no = 'd001';
    ```
* **불필요한 테이블 제외:** `JOIN`할 필요가 없는 테이블은 제외하여 쿼리의 복잡성을 줄입니다.

**11.4.5 GROUP BY 절의 최적화**

`GROUP BY` 절은 특정 칼럼의 값으로 데이터를 그룹화하여 집계된 결과를 얻는 데 사용됩니다. 효율적인 `GROUP BY` 절 작성은 쿼리의 성능을 크게 향상시킬 수 있습니다. 다음과 같은 최적화 기법을 활용할 수 있습니다:

*   **적절한 인덱스 사용:** `GROUP BY` 절의 칼럼에 인덱스를 사용하여 그룹화를 빠르게 수행합니다.

    ```sql
    SELECT dept_no, COUNT(*) AS num_employees
    FROM dept_emp
    GROUP BY dept_no;
    ```
*   **집계 함수의 효율적 사용:** 집계 함수는 필요한 경우에만 사용하여 불필요한 연산을 줄입니다.

    ```sql
    SELECT dept_no, AVG(salary) AS average_salary
    FROM dept_emp
    GROUP BY dept_no
    HAVING AVG(salary) > 50000;
    ```
*   **ROLLUP 사용:** `GROUP BY` 절과 함께 `WITH ROLLUP`을 사용하여 소계와 총계를 함께 조회할 수 있습니다.

    ```sql
    SELECT dept_no, COUNT(*) AS num_employees
    FROM dept_emp
    GROUP BY dept_no WITH ROLLUP;
    ```

**11.4.6 ORDER BY 절의 최적화**

`ORDER BY` 절은 쿼리 결과를 특정 칼럼의 값에 따라 정렬하는 데 사용됩니다. 효율적인 `ORDER BY` 절 작성은 쿼리의 성능을 크게 향상시킬 수 있습니다. 다음과 같은 최적화 기법을 활용할 수 있습니다:

*   **인덱스 활용:** `ORDER BY` 절의 칼럼에 인덱스를 사용하여 정렬 속도를 향상시킵니다.

    ```sql
    SELECT * FROM employees
    WHERE dept_no = 'd001'
    ORDER BY hire_date DESC;
    ```
*   **불필요한 정렬 제거:** 이미 정렬된 데이터를 사용하거나 불필요한 정렬을 제거하여 쿼리의 성능을 향상시킵니다.

    ```sql
    -- 불필요한 정렬
    SELECT * FROM employees
    ORDER BY emp_no, first_name, last_name;

    -- 필요 없는 경우 제거
    SELECT * FROM employees;
    ```
*   **LIMIT와 함께 사용:** `ORDER BY` 절과 `LIMIT`을 함께 사용하여 필요한 만큼의 데이터만 정렬하여 성능을 향상시킵니다.

    ```sql
    SELECT * FROM employees
    ORDER BY hire_date DESC
    LIMIT 10;
    ```

**11.4.7 LIMIT 절의 최적화**

`LIMIT` 절은 쿼리 결과에서 특정 범위의 데이터만 조회하는 데 사용됩니다. 효율적인 `LIMIT` 절 작성은 쿼리의 성능을 크게 향상시킬 수 있습니다. 다음과 같은 최적화 기법을 활용할 수 있습니다:

*   **적절한 인덱스 사용:** `LIMIT` 절과 함께 `ORDER BY` 절을 사용할 때 인덱스를 활용하여 빠르게 데이터를 조회합니다.

    ```sql
    SELECT * FROM employees
    WHERE dept_no = 'd001'
    ORDER BY hire_date DESC
    LIMIT 10;
    ```
*   **OFFSET 사용 최소화:** `OFFSET`을 사용하면 특정 위치부터 데이터를 조회할 수 있지만, 큰 `OFFSET`을 사용하면 성능이 저하될 수 있습니다. 가능하면 `OFFSET`을 사용하지 않고 `WHERE` 절을 활용하여 필요한 데이터를 조회합니다.

    ```sql
    -- 비효율적인 OFFSET 사용
    SELECT * FROM employees
    ORDER BY hire_date DESC
    LIMIT 10000, 10;

    -- 효율적인 WHERE 절 사용
    SELECT * FROM employees
    WHERE hire_date < '2020-01-01'
    ORDER BY hire_date DESC
    LIMIT 10;
    ```

#### 11.5 실습: 화이트보드 작업

실제 프로젝트에서 도메인, 서브도메인, 바운디드 컨텍스트를 이해하고 정의하기 위해 화이트보드 작업을 시도해 보세요:

1. **도메인 식별:** 자신이 속한 조직이나 프로젝트의 도메인을 명확히 정의합니다.
2. **서브도메인 목록 작성:** 일상 업무에서 인식되는 모든 서브도메인을 목록화합니다.
3. **바운디드 컨텍스트 분리:** 각 서브도메인을 독립적인 바운디드 컨텍스트로 분리하여 그 경계를 설정합니다.
4. **컨텍스트 맵 작성:** 바운디드 컨텍스트 간의 관계와 통합 방식을 시각화합니다.

이 과정을 통해 자신의 프로젝트에 맞는 DDD 모델을 구축할 수 있습니다. 초기에는 어려울 수 있으나, 반복적인 연습을 통해 점차 명확한 도메인 모델을 설계할 수 있게 됩니다.

#### 11.6 SaaSOvation의 DDD 적용 사례

SaaSOvation은 두 가지 주요 SaaS 제품, **CollabOvation**과 **ProjectOvation**을 개발하는 회사입니다. 이 두 제품을 통해 DDD의 개념을 실제로 어떻게 적용하는지 살펴보겠습니다.

**도메인과 서브도메인의 분리**

SaaSOvation의 **e-Commerce System**을 예로 들어보겠습니다. 이 시스템은 다음과 같은 서브도메인으로 구성됩니다:

1. **Product Catalog:** 제품 목록을 관리하고, 제품 정보를 제공.
2. **Orders:** 주문을 처리하고, 주문 상태를 관리.
3. **Invoicing:** 청구서 발행과 결제 처리를 담당.
4. **Shipping:** 제품 배송과 관련된 모든 활동을 관리.
5. **Inventory:** 재고를 관리하고, 재고 수준을 유지.
6. **External Forecasting:** 판매 예측을 통해 재고 및 판매 전략을 최적화.

![도메인, 서브도메인, 바운디드 컨텍스트 예시](https://www.ebooksworld.ir/Chapter2_Figure2.1.png) _Figure 2.1 도메인, 서브도메인, 바운디드 컨텍스트 예시_

이 예시에서, **e-Commerce System**은 전체 도메인을 나타내며, 각 서브도메인은 독립적인 바운디드 컨텍스트로 분리됩니다. 예를 들어, **Inventory System**은 재고 관리에 집중하는 독립된 바운디드 컨텍스트로, **e-Commerce System**의 다른 서브도메인들과는 명확히 구분됩니다. 이러한 분리를 통해 각 서브도메인은 독립적으로 발전할 수 있으며, 시스템 간의 결합도를 낮출 수 있습니다.

**바운디드 컨텍스트의 역할**

**바운디드 컨텍스트**는 도메인 모델의 경계를 명확히 함으로써, 동일한 용어가 다른 의미로 사용되는 것을 방지합니다. 예를 들어, **e-Commerce System** 내에서의 "Customer"는 주문과 관련된 의미를 가지지만, **Inventory System** 내에서는 재고 관리와 관련된 의미를 가질 수 있습니다. 이러한 용어의 혼동을 피하기 위해 각 컨텍스트는 독립적인 유비쿼터스 언어를 사용합니다.

또한, 바운디드 컨텍스트는 서로 다른 서브도메인 간의 상호작용을 명확히 정의하여, 시스템 간의 의존성을 줄이고 유지보수를 용이하게 합니다. SaaSOvation의 경우, **Inventory System**과 **External Forecasting System** 간의 통합 관계는 명확히 정의된 바운디드 컨텍스트 간의 상호작용을 통해 관리됩니다.

#### 11.7 스키마 조작 (DDL)

DBMS 서버의 모든 오브젝트를 생성하거나 변경하는 쿼리를 DDL(Data Definition Language)이라고 합니다. 스토어드 프로시저나 함수, DB나 테이블 등을 생성하거나 변경하는 대부분의 명령이 DDL에 해당합니다. MySQL 서버가 업그레이드되면서 많은 DDL이 온라인 모드로 처리될 수 있게 개선되었지만, 여전히 스키마를 변경하는 작업 중에는 상당히 오랜 시간이 걸리고 MySQL 서버에 많은 부하를 발생시키는 작업들이 있으므로 주의가 필요합니다. 여기서는 중요 DDL 문의 문법과 함께 어떤 DDL 문이 특히 느리고 큰 부하를 유발하는지도 함께 살펴보겠습니다.

**11.7.1 온라인 DDL**

MySQL 5.5 이전 버전까지는 MySQL 서버에서 테이블의 구조를 변경하는 동안에는 다른 커넥션에서 DML을 실행할 수 없었습니다. 이러한 문제점을 해결하기 위해 Percona에서 개발한 `pt-online-schema-change`라는 도구를 사용했습니다. 하지만 MySQL 8.0 버전으로 업그레이드되면서 대부분의 스키마 변경 작업은 MySQL 서버에 내장된 온라인 DDL 기능으로 처리 가능해졌습니다. 따라서 MySQL 8.0에서는 `pt-online-schema-change`와 같은 도구는 이제 거의 사용되지 않습니다.

**11.7.1.1 온라인 DDL 알고리즘**

온라인 DDL은 스키마를 변경하는 작업 도중에도 다른 커넥션에서 해당 테이블의 데이터를 변경하거나 조회하는 작업을 가능하게 해줍니다. 온라인 DDL은 다음과 같은 알고리즘과 함께 잠금 수준을 설정할 수 있습니다: `ALGORITHM`과 `LOCK` 옵션.

MySQL 서버에서는 `ALTER TABLE` 명령이 온라인 DDL로 작동할지, 아니면 기존 방식으로 처리할지를 결정하기 위해 다음과 같은 순서로 알고리즘을 선택합니다:

1. **INSTANT 알고리즘:** 테이블의 데이터는 전혀 변경하지 않고, 메타데이터만 변경하여 작업을 완료합니다. 테이블이 가진 레코드 건수와 무관하게 작업 시간은 매우 짧습니다.
2. **INPLACE 알고리즘:** 임시 테이블로 데이터를 복사하지 않고 스키마 변경을 실행합니다. 테이블의 모든 레코드를 리빌드해야 하기 때문에 테이블의 크기에 따라 많은 시간이 소요될 수 있습니다. 하지만 스키마 변경 중에도 테이블의 읽기와 쓰기 모두 가능합니다.
3. **COPY 알고리즘:** 변경된 스키마를 적용한 임시 테이블을 생성하고, 테이블의 레코드를 모두 임시 테이블로 복사한 후 최종적으로 임시 테이블을 `RENAME`하여 스키마 변경을 완료합니다.

```sql
ALTER TABLE salaries CHANGE to_date end_date DATE NOT NULL,
ALGORITHM=INPLACE, LOCK=NONE;
```

위의 예제는 `salaries` 테이블의 `to_date` 칼럼을 `end_date`로 변경하면서 `INPLACE` 알고리즘과 `NONE` 잠금 수준을 지정하여 온라인으로 스키마를 변경하는 방법을 보여줍니다.

**11.7.1.2 온라인 DDL 진행 상황 모니터링**

온라인 DDL을 포함한 모든 `ALTER TABLE` 명령은 `performance_schema`를 통해 진행 상황을 모니터링할 수 있습니다. `performance_schema`를 이용해 `ALTER TABLE`의 진행 상황을 모니터링하려면 다음과 같이 설정해야 합니다:

1.  **`performance_schema` 활성화:**

    ```sql
    SET GLOBAL performance_schema = ON;
    ```
2.  **인스트루먼트 활성화:**

    ```sql
    UPDATE performance_schema.setup_instruments
    SET ENABLED = 'YES', TIMED = 'YES'
    WHERE NAME LIKE 'stage/innodb/alter%';
    ```
3.  **컨슈머 활성화:**

    ```sql
    UPDATE performance_schema.setup_consumers
    SET ENABLED = 'YES'
    WHERE NAME LIKE '%stages%';
    ```

진행 상황은 `performance_schema.events_stages_current` 테이블을 통해 확인할 수 있습니다. 예를 들어, `INPLACE` 알고리즘으로 스키마 변경을 실행하는 경우 다음과 같이 조회할 수 있습니다:

```sql
SELECT EVENT_NAME, WORK_COMPLETED, WORK_ESTIMATED
FROM performance_schema.events_stages_current;
```

#### 11.8 쿼리 성능 테스트

쿼리의 성능을 테스트하고 최적화하기 위해서는 다양한 방법을 활용할 수 있습니다. MySQL에서는 `EXPLAIN` 명령어를 사용하여 쿼리의 실행 계획을 확인할 수 있으며, 이를 통해 쿼리가 인덱스를 적절히 활용하고 있는지, 어떤 방식으로 테이블을 스캔하고 있는지를 파악할 수 있습니다.

```sql
EXPLAIN SELECT * FROM employees WHERE emp_no = 10001;
```

위의 쿼리는 `employees` 테이블에서 `emp_no`가 `10001`인 레코드를 조회하는 쿼리의 실행 계획을 보여줍니다. 실행 계획을 통해 인덱스가 사용되고 있는지, 전체 테이블 스캔이 이루어지고 있는지 등을 확인할 수 있습니다.

**성능 테스트 도구:**

*   **`BENCHMARK()` 함수:** 특정 표현식을 여러 번 반복 실행하여 성능을 측정할 수 있습니다.

    ```sql
    SELECT BENCHMARK(1000000, MD5('test'));
    ```
*   **`SLEEP()` 함수:** 쿼리 실행 도중 대기 시간을 설정하여 성능 테스트를 할 수 있습니다.

    ```sql
    SELECT SLEEP(1.5);
    ```
