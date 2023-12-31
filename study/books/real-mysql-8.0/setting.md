# SETTING

## 1장 소개

### 1.1 MySQL 소개

* 스웨덴 TcX회사의 라이브러리에서 시작
* \=> 썬 마이크로시스템즈 => 오라클로 인수됨
* 라이센스 정책은 5.5 이전에는 엔터프라이즈와 커뮤니티 에디션으로 나뉘지만 둘의 소스코드는 동일했었다.
  * 그 후부터는 기능과 소스코드가 달라졌다. 핵심기능은 동일
* 5.5, 5.7, 8.0 버전에 이르기까지 오라클에 인수된이후 10년간 많은 변화가 있었다!

### 1.2 왜 MySQL인가?

* 오라클 DB에 비교해서 MySQL의 경쟁력은 가격과 비용이다.
* 최근에는 대형 은행 시스템에서도 MySQL 서버를 사용하고 있을 정도로 MySQL이 안정성 측면에서도 많이 발전했다.
  * 페이스북에서 가진 데이터를 모두 오라클 DB에 저장하면 페이스북은 망할것이다 ㅎㅎ
* DBMS를 선택할 때 고려할 사항
  * 안정성
  * 성능과 기능
  * 커뮤니티나 인지도

## 2장 설치와 설정

### 2.1 설치

* dmg 파일로 설치하면 편리한 앱으로 실행, 종료 및 환경파일을 설정할 수 있다.

### 2.2 MySQL 서버의 시작과 종료

#### 2.2.3 서버 연결 테스트

```
mysql -uroot -p --host=localhost --socket=/tmp/mysql.sock
mysql -uroot -p --host=127.0.0.1 --port=3306
mysql -uroot -p

```

### 2.3 MySQL 서버 업그레이드

* 인플레이스 업그레이드 : MySQL 서버의 데이터 파일을 그대로 두고 업그레이드하는 방법
* 로지컬 업그레이드 : mysqldump 도구 등을 이용해 MySQL 서버의 데이터를 SQL 문장이나 텍스트 파일로 덤프한 후, 새로 업그레이드된 버전의 MySQL 서버에서 덤프된 데이터를 적재하는 방법

### 2.4 서버 설정

* 일반적으로 MySQL은 my.cnf or my.ini 라는 이름의 단 하나의 설정 파일을 사용한다.

```
mysql --help

Default options are read from the following files in the given order:
/etc/my.cnf /etc/mysql/my.cnf /opt/homebrew/etc/my.cnf ~/.my.cnf

```

* mysql --help 명령어를 사용하면 위와 같은 경로의 순서로 my.cnf 파일을 찾아서 처음 발견된 파일을 사용한다.

#### 2.4.1 설정 파일의 구성

* 여러 개의 설정 그룹을 담을 수 있으며, 대체로 실행 프로그램 이름을 그룹명으로 사용한다. \[mysqld] socket = /usr/local/mysql/tmp/mysql.sock port=3306 \[mysql] socket = /usr/local/mysql/tmp/mysql.sock port=3304

#### 2.4.2 MySQL 시스템 변수의 특징

* MySQL 서버는 기동하면서 설정 파일의 내용을 읽어 메모리나 작동 방식을 초기화하고, 접속된 사용자를 제어하기 위해 이러한 값을 별도로 저장해 둔다.
* MySQL 서버에서는 이렇게 저장된 값을 시스템 변수(System Variables)라고 한다.
* 각 시슷템 변수는 다음 예제와 같이 show variables, show global variables라는 명령으로 확인할 수 있다.
* MySQL 서버의 매뉴얼에서 시스템 변수를 설명한 페이지를 보면 모든 변수의 목록과 간단한 설명을 참고할 수 있다.
* 변수가 가지는 속성은 5가지가 있다.
  * Cmd-Line: MySQL 서버의 명령행 인자로 설정될 수 있는지 여부를 나타낸다. 이 값이 Yes 이면 명령행 인자로 변수 값을 변경할 수 있다.
  * Option File: mySQL 설정 파일인 my.cnf로 제어할 수 있는지 여부를 나타낸다.
  * System Var: 시스템 변수인지 아닌지를 나타낸다.
  * Var Scope: 시스템 변수의 적용 범위를 나타낸다. 이 시스템 변수가 영향을 미치는 곳이 서버 전체(Global)를 대상으로 하는지, 아니면 MySQL 서버와 클라이언트 간의 커넥션(Session)만인지 구분한다. 어떤 변수는 세션과 글로벌 모두 적용(Both)되기도 한다.
  * Dynamic: 시스템 변수가 동적인지 정적인지 구분하는 변수이다.

#### 2.4.3 글로벌 변수와 세션 변수

* 글로벌 범위의 시스템 변수는 MySQL 서버 인스턴스에서 전체적으로 영향을 미치는 시스템 변수를 의미한다.
  * 주로 MySQL 서버 자체에 대한 설정일 때가 많다. 예를들어 InnoDB 버퍼 풀 크기, MyISAM의 키 캐시 크기 등이 대표적인 글로벌 영역의 시스템 변수다.
* 세션 범위의 시스템 변수는 mySQL 클라이언트가 서버에 접속할 때 기본적으로 부여하는 옵션의 기본값을 제어하는 데 사용한다.
  * 별도로 그 값을 변경하지 않은 경우에는 그대로 값이 유지되지만, 클라이언트의 필요에 따라 개별 커넥션 단위로 다른 값으로 변경할 수 있는 것이 세션 변수다. 여기서 기본 값은 글로벌 시스템 변수이며, 각 클라이언트가 가지는 값이 세션 시스템 변수다.
  * 각 클라이언트에서 쿼리 단위로 자동 커밋을 수행할지 여부를 결정하는 autocommit 변수가 대표적인 예이다. autocommit을 ON으로 설정해두면 해당 서버에 접속하는 모든 커넥션은 기본적으로 자동 커밋 모드로 시작되지만 각 커넥션에서 autocommit 변수를 OFF로 변경해 자동 커밋 모드를 비활성화할 수도 있다.

#### 2.4.4 정적 변수와 동적 변수

* MySQL 서버가 기동 중인 상태에서 변경 가능한지에 따라 동적 변수와 정적 변수로 구분된다.
* 변경하고자 하는 값이 동적 변수라면 SET 명령으로 간단히 변수값을 변경할 수 있으며, 정적 변수라면 my.cnf 파일을 변경하거나 PERSIST 명령어를 이용하면 된다.
* MySQL 8.0버전부터는 SET PERSIST 명령을 이용하면 실행 중인 MySQL 서버의 시스템 변수를 변경함과 동시에 자동으로 설정 파일로도 기록된다.
* SHOW 나 SET 명령에서 GLOBAL 키워드를 사용하면 글로벌 시스템 변수의 목록과 내용을 읽고 변경할 수 있으며, 빼면 세션 변수를 조회하고 변경한다.

#### 2.4.5 SET PERSIST

* MySQL 서버의 시스템 변수는 동적 변수와 정적변수로 나뉘어지는데, 동적변수는 SET GLOBAL 명령으로 변경하여 서버에 즉시 반영할 수 있다.
* 이렇게 변경했을 때 MySQL 서버의 설정 파일에도 이 내용을 적용해야 하는데 이를 후에 적용하면 잊을 때가 있다.
* 이 문제를 해결하기 위해 MySQL 8.0 버전에서는 SET PERSIST 명령을 지원한다.
  * SET PERSIST 명령을 사용하여 시스템 변수를 변경하면 MySQL 서버는 변경된 값을 즉시 적용함과 동시에 mysqld-auto.cnf에 변경 내용을 추가로 기록해 둔다.
  * 서버가 다시 시작될 때 기본 설정 파일 my.cnf 파일과 mysqld-auto.cnf 파일을 같이 참조해서 시스템 변수에 적용한다.
* SET PERSIST 명령은 세션 변수에는 적용되지 않으며, SET PERSIST 명령으로 시스템 변수를 변경하면 MySQL 서버는 자동으로 GLOBAL 시스템 변수의 변경으로 인식하고 변경한다.
* 현재 실행중인 서버에는 변경 내용을 적용하지 않고 다음 재시작을 위해 mysqld-auto.cnf 파일에만 변경 내용을 기록해두고자 한다면 SET PERSIST\_ONLY 명령을 사용하면 된다.
  * 정적인 변수는 실행중인 서버에서 변경할 수 없기 때문에 mysqld-auto.cnf 에 기록해두고 서버를 재시작 해야한다.
* mysqld-auto.cnf 파일의 내용을 삭제해야 하는 경우에는 RESET PERSIST 명령을 사용하면 된다.

#### 2.4.6 my.cnf파일

* MySQL 서버를 제대로 사용하려면 시스템 변수에 대한 이해가 상당히 많이 필요하다.

## 3장 사용자 및 권한

* MySQl의 사용자 계정은 단순히 사용자의 아이디 뿐 아니라 해당 사용자가 어느 IP에서 접속하고 있는지도 확인한다
* MySQL 8.0부터는 권한을 묶어서 관리하는 역할(Role)의 개념이 도입됐기 때문에 각 사용자의 권한으로 미리 준비된 권한 세트(Role)을 부여하는 것도 가능하다.

### 3.1 사용자 식별

* 사용자의 계정뿐 아니라 사용자의 접속 지점도 계정의 일부가 된다.
  * 따라서 MySQL에서 계정을 언급할 때는 다음과 같이 항상 아이디와 호스트를 함께 명시해야 한다.

```
'svc_id'@'127.0.0.1'

```

### 3.2 사용자 계정 관리

#### 3.2.1 시스템 계쩡과 일반 계정

* MySQL 8.0부터 계정은 SYSTEM\_USER 권한을 가지고 있느냐에 따라 시스템 계저과 일반 계정으로 구분된다.
* 시스템 계정은 시스템 계정과 일반 계정을 관리(생성 삭제 및 변경)할 수 있지만 일반 계정은 시스템 계정을 관리할 수 없다.
* 시스템 계정만 가능한 작업
  * 계정 관리
  * 다른 세션 또는 그 세션에서 실행 중인 쿼리를 강제종료
  * 스토어드 프로그램 생성 시 DEFINER를 타 사용자로 설정
* 내장된 계정 : 아래의 3개의 계정은 처음부터 잠겨(account\_locked) 상태이다.
  * 'mysql.sys'@'localhost': MySQL 8.0부터 기본으로 내장된 sys 스키마의 객체(뷰, 함수, 프로시저)들의 DEFINER로 사용되는 계정
  * 'mysql.session'@'localhost': MySQL 프러그인이 서버로 접근할 때 사용되는 계정
  * 'mysql.infoschema'@'localhost': information\_schema에 정의된 뷰의 DEFINER로 사용되는 계정

#### 3.2.2 계정 생성

* MySQL 8.0부터는 계정 생성은 CREATE USER 명령으로, 권한 부여는 GRANT 명령으로 구분해서 실행한다.(이전에는 한 번에 가능했음)
* 계정 생성할 때는 다양한 옵션을 추가할 수 있다.
  * 계정의 인증 방식과 비밀번호
  * 비밀번호 관련 옵션(비밀번호 유효기간, 비밀번호 이력 개수, 비밀번호 재사용 불가 기간)
  * 기본 역할
  * SSL 옵션
  * 계정 잠그므 여부

### 3.3 비밀번호 관리

* MySQL 서버에서 비밀번호 유효성 체크 규칙을 적용하려면 validate\_password 컴포넌트를 설치해야 한다.
  * INSTALL COMPONENT 'file://component\_validate\_password';
* 비밀번호 정책은 크게 3가지 중에 선택할 수 있다.
  * LOW : 길이만 검증
  * MEDIUM: default
  * STRONG: MEDIUM + 금칙어 지정가능

### 3.4 권한

* 글로벌 권한: 데이터베이스나 테이블 이외의 객체에 적용되는 권한
* 객체 권한: 데이터베이스나 테이블을 제어하는 데 필요한 권한
  * DB 권한
  * 테이블 권한
* 정적권한: MySQL 서버의 소스코드에 고정적으로 명시돼 있는 권한
* 동적 권한: MySQL 서버가 시작되면서 동적으로 생성하는 권한

### 3.5 역할

* MySQL 8.0버전부터 권한을 묶어서 역할을 사용할 수 있게 됐다.
* 실제 MySQL 서버 내부적으로 역할은 계정과 같은 모습을 하고 있다.
* 역할을 생성하고 계정에 부여를 한 후 이를 사용할 수 있게 하려면 SET ROLE 명령을 실행해서 해당 역할을 활성화해야 한다.
  * activate\_all\_roles\_on\_login 변수가 ON으로 되어있으면 역할을 활성화하지 않아도 로그인ㅇ과 동시에 부여된 역할이 자동으로 활성화된다.
