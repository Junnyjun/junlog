# 템플릿

확장에는 자유롭게 열려 있고 변경에는 굳게 닫혀 있다는 객체지향 설계의 핵심 원칙인 개방 폐쇄 원칙(OCP)을 다시 한번 생각해보자. 이 원칙은 코드에서 어떤 부분은 변경을 통해 그 기능이 다양해지고 확장하려는 성질이 있고, 어떤 부분은 고정되어 있고 변하지 않으려는 성질이 있음을 말해준다.

3장에서는 스프링에 적용된 템플릿 기법을 살펴보고, 이를 적용해 완성도 있응 DAO 코드를 만드는 방법을 알아본다.

## 다시 보는 초난감 DAO

DB 연결과 관련된 여러 가지 개선 작업은 했지만, 다른 면에서 심각한 문제점이 있다. 바로 예외사항에 대한 처리다.

```java
public void deleteAll() throws SQLException {
    Connection c = dataSource.getConnection();

    PreparedStatement ps = c.preparedStatement("delete from users");
    ps.executeUpdate();     //  여기서 예외가 발생하면 바로 메소드 실행이 중단되면서 DB 커넥션이 반환되지 못한다.

    ps.close();
    c.close();
}
```

DB 풀은 매번 getConnection()으로 가져간 커넥션을 명시적으로 close()해서 돌려줘야지만 다시 풀에 넣었다가 다음 커넥션 요청이 있을 때 재사용할 수 있다. 그런데 이런 식으로 오류가 날 때마다 미처 반환되지 못한 Connection이 계속 쌓이면 어느 순간에 커넥션 풀에 여유가 없어지고 리소스가 모자란다는 심각한 오류를 내며 서버가 중단될 수 있다.

#### 예외사항에서도 리소스를 제대로 반환할 수 있도록 try/catch/finally를 적용해보자

```java
public void deleteAll() throws SQLException {
    Connection c = null;
    PreparedStatement ps = null;
    
    try {
        c = dataSource.getConnection();
        ps = c.prepareStatement("delete from users");
        ps.executeUpdate();     //  예외가 발생할 수 있는 코드를 모두 try 블록으로 묶어준다.
    } catch (SQLException e) {
        throw e;        //  예외가 발생했을 때 부가적인 작업을 해줄 수 있도록 catch 블록을 둔다. 아직은 예외를 메소드 밖으로 던지는 것 밖에 없다.
    } finally {         //  finally이므로 try 블록에서 예외가 발생했을 떄나 안 했을 때나 모두 실행된다.
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {} //  ps.close() 메소드에서도 SQLException이 밣생할 수 있기 때문에 잡아줘야한다.
        }
        if (c != null) {
            try {
                c.close();
            } catch (SQLException e) {}
        }
    }
}
```

어느 시점에서 예외가 발생했는지에 따라서 close()를 사용할 수 있는 변수가 달라질 수 있기 때문에 finally에서는 반드시 c와 ps가 null이 아닌지 먼저 확인한 후에 close() 메소드를 호출해야 한다.

문제는 이 close()도 SQLException이 발생할 수 있는 메소드라는 점이다.

## 변하는 것과 변하지 않는 것

### JDBC try/catch/finally 코드의 문제점

복잡한 try/catch/finally 블록은 2중으로 중첩되어 있고 모든 메소드마다 반복될 것이다. 반복 작업을 하다가 어느 순간 한 줄을 빼먹고 복사했거나, 몇 줄을 잘못 작세했다면 어떻게 될까? 그러면 해당 메소드가 호출되고 나면 커넥션이 하나씩 반환되지 않고 쌓여가게 된다. 실제로 어떤 기업에서 일주일만 지나면 DB 커넥션 풀이 꽉 찼다는 에러를 내면서 중단되는 일이 발생하기도 했다.

DAO 로직을 개선하려고 해도 중복된 모든 부분을 찾아서 수정해야 하고 어느 한 곳이라도 못 찾을경우에는 심각한 오류를 발생할 수도 있다. 또한 테스트도 예외상황을 처리하는 코드는 테스트하기가 매우 어렵고 모든 DAO 메소드에 대해 이런 테스트를 일일이 한다는 건 매우 번거롭다.

이 문제의 핵심은 변하지 않는, 그러나 많은 곳에서 중복되는 코드와 로직에 따라 자꾸 확장되고 자주 변하는 코드를 잘 분리해내는 작업이다.

### 분리와 재사용을 위한 디자인 패턴 적용

#### 메소드 추출

먼저 생각해볼 수 있는 방법은 변하는 부분을 메소드로 빼는 것이다.

```java
public void deleteAll() throws SQLException {
    ...
    try {
        c = dataSource.getConnectin();
        ps = makeStatement(c);      //  변하는 부분을 메소드로 추출하고 변하지 않는 부분에서 호출한다.
        ps.executeUpdate();
    } catch (SQLException e) {...}
}

private PreparedStatement makeStatement(Connection c) throws SQLException {
    PreparedStatement ps;
    ps = c.preparedStatement("delete from users");
    return ps;
}
```

보통 메소드 추출 리팩토링을 적용하는 경우에는 분리시킨 메소드를 다른 곳에서 재사용할 수 있어야 하는데, 이건 반대로 분리시키고 남은 메소드가 재사용이 필요한 부분이고, 분리된 메소드는 DAO 로직마다 새롭게 만들어서 확장돼야 하는 부분이기 때문에 뭔가 반대로 되었다.

#### 템플릿 메소드 패턴의 적용

변하지 않는 부분은 슈퍼클래스에 두고 변하는 부분은 추상 메소드로 정의해둬서 서브클래스에서 오버라이드하여 새롭게 정의해 쓰도록 하는 것이다.

```java
public class UserDaoDeleteAll extends UserDao {
    protected PreparedStatement makeStatement(Connection c) throws SQLException {
        PreparedStatement ps = c.preparedStatement("delete from users");
        return ps;
    }
}
```

이제 UserDao 클래스의 기능을 확장하고 싶을 때마다 상속을 통해 자유롭게 확장할수 있고, 확장 때문에 기존의 상위 DAO 클래스에 불필요한 변화는 생기지 않도록 할 수 있으니 객체지향 설계의 핵심 원리인 개발 폐쇄 원칙(OCP)을 그럭저럭 지키는 구조를 만들어낼 수 있는 것 같다.

![](broken-reference)

#### 전략 패턴의 적용

템플릿 메소드 패턴보다 유연하고 확장성이 뛰어난 것이, 오브젝트를 아예 둘로 분리하고 클래스 레벨에서는 인터페이스를 통해서만 의존하도록 만드는 전략 패턴이다.

![](broken-reference)

전략 패턴의 구조를 따라 이 기능을 인터페이스로 만들어두고 인터페이스의 메소드를 통해 PreparedStatement 생성 전략을 호출해주면 된다.

```java
public void deleteAll() throws SQLException {
    ...
    try {
        c = dataSource.getConnection();

        StatementStrategy strategy = new DeleteAllStatement();  //  전략 클래스가 DeleteAllStatement로 고정됨으로써 OCP 개방 원칙에 맞지 않게 된다.
        ps = starategy.makePreparedStatement(c);

        ps.executeUpdate();
    } catch (SQLException e) {...}
}
```

전략 패턴은 필요에 따라 컨텍스트는 그대로 유지되면서 전략을 바꿔 쓸 수 있다는 것인데, 이렇게 컨텍스트 안에서 이미 구체적인 전략 클래스인 DeleteAllStatement를 사용하도록 고정되어 있다면 뭔가 이상하다

#### DI 적용을 위한 클라이언트/컨텍스트 분리

전략 패턴에 따르면 Context가 어떤 전략을 사용하게 할 것인가는 Context를 사용하는 앞단의 Client가 결정하는 게 일반적이다.

![](broken-reference)

중요한 것은 이 컨텍스트에 해당하는 JDBC try/catch/finally 코드를 클라이언트 코드인 StatementStrategy를 만드는 부분에서 독립시켜야 한다는 점이다.

```java
public void jdbcContextWithStatementStrategy(StatementStrategy stmt) throws SQLException {
    Connection c = null;
    PreparedStatement ps = null;

    try {
        c = dataSource.getConnection();
        ps = stmt.makePreparedStatement(c);
        ps.executeUpdate();
    } catch (SQLException e) {
        throw e;
    } finally {
        if (ps != null) { try { ps.close(); } catch (SQLException e) {}
        if (c != null) { try { c.close(); } catch (SQLException e) {}
    }
}
```

클라이언트로부터 StatementStrategy 타입의 전략 오브젝트를 제공받고 try/catch/finally 구조로 만들어진 컨텍스트 내에서 작업을 수행한다.

```java
public void deleteAll() throws SQLException {
    StatementStrategy st = new DeleteAllStatement();    //  선정한 전략 클래스의 오브젝트 생성
    jdbcContextWithStatementStrategy(st);               //  컨텍스트 호출. 전략 오브젝트 전
}
```

## JDBC 전략 패턴의 최적화

### 전략과 클라이언트의 동거

이전의 개선된 코드는 두 가지 문제점이 있다.

* 먼저 DAO 메소드마다 새로운 StatementStrategy 구현 클래스를 만들어야 한다는 점이다.
* DAO 메소드에서 StatementStrategy에 전달할 User와 같은 부가적인 정보가 있는 경우, 이를 전달하고 저장해 둘 생성자와 인스턴스 변수를 번거롭게 만들어야 한다.

#### 로컬 클래스

StatementStrategy 전략 클래스를 매번 독립된 파일로 만들지 말고 UserDao 클래스 안에 내부 클래스로 정의해버리면 클래스 파일이 많아지는 문제는 해결할 수 있다.

```java
public void add(final User user) throws SQLException {
  class AddStatement implements StatementStrategy {   //  add() 메소드 내부에 선언된 로컬 클래
      User user;

      public AddStatement(User user) {
          this.user = user;
      }

      public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
          PreparedStatement ps = c.prepareStatement("insert into users(id, name, password) values(?,?,?)");
          ...
      }

      StatementStrategy st = new AddStatement(user);
      jdbcContextWithStatementStrategy(st);
  }
}
```

로컬 클래스로 만들어두니 장점이 많다.

* AddStatement는 복잡한 클래스가 아니므로 메소드 안에서 정의해도 그다지 복잡해 보이지 않는다.
* 메소드마다 추가해야 했던 클래스 파일을 하나 줄일 수 있다.
* 내부 클래스의 특징을 이용해 로컬 변수를 바로 가져다 사용할 수 있다.

#### 익명 내부 클래스

익명 내부 클래스는 선언과 동시에 오브젝트를 생성한다.

```java
public void add(final User user) throws SQLException {
    jdbcContextWithStatementStrategy(
        new StatementStrategy() {
            public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
                PreparedStatement ps = c.prepareStatement("insert into users(id, name, password) values(?,?,?)");
    
                ps.setString(1, user.getId());
                ps.setString(2, user.getName();
                ...
                return ps;
            }
        }
    );
}
```

이렇게 하면 코드를 더욱 간결해진다.

## 컨텍스트와 DI

전략 패턴의 구조로 보자면 UserDao의 메소드가 클라이언트이고, 익명 내부 클래스로 만들어지는 것이 개별적인 전략이고, jdbcContextWithStatementStrategy() 메소드는 컨텍스트다.

그렇다면 DI를 적용하여 아래 그림과 같은 구조로 개선할 수 있다.

![](broken-reference)

```java
public class JdbcContext {
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {  //  DataSource 타입 빈을 DI 받을 수 있게 준비
        this.dataSource = dataSource;
    }

    public void workWithStatementStrategy(StatementStrategy stmt) throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;

        try {...} 
        catch (SQLException e) {...}
        finally {...}
    }
}
```

```java
public class UserDao {
    ...
    private JdbcContext jdbcContext;

    public void setJdbcContext(JdbcContext jdbcContext) {
        this.jdbcContext = jdbcContext;             //  jdbcContext를 Di받도록 만든다.
    }

    public void add(final User user) throws SQLException {
        this.jdbcContext.workWithStatementStrategy(     //  DI 받은 JdbcContext의 컨텍스트 메소드를 사용하도록 변경한다.
            new StatementStrategy() {...}
        );
    }
}
```

스프링의 DI는 넓게 보자면 객체의 생성과 관계설정에 대한 제어권한을 오브젝트에서 제거하고 외부로 위임했다는 IoC라는 개념을 포괄한다.

#### JdbcContext를 UserDao와 DI 구조로 만들면 어떤 이점이 있을까?

1. JdbcContext가 스프링 컨테이너의 싱글톤 레지스트리에서 관리되는 싱글톤 빈이기 되기 때문이다.
2. JdbcContext가 DI를 통해 다른 빈에 의존하고 있디 때문이다.

## 템플릿과 콜백

전략 패턴의 기본 구조에 익명 내부 클래스를 활용한 방식을 스프링에서는 템플릿/콜백 패턴이라고 부른다. 전략 패턴의 컨텍스트를 템플릿이라 부르고, 익명 내부 클래스로 만들어지는 오브젝트를 콜백이라고 부른다.

### 템플릿/콜백의 동작원리

템플릿/콜백 패턴의 콜백은 보통 단일 메소드 인터페이스를 사용한다.

![](broken-reference)

* 클라이언트의 역할은 템플릿 안에서 실행될 로직을 담은 콜백 오브젝트를 만들고, 콜백이 참조할 정보를 제공하는 것이다. 만들어진 콜백은 클라이언트가 템플릿의 메소드를 호출할 때 파라미터로 전달된다.
* 템플릿은 정해진 작업 흐름을 따라 작업을 진행하다가 내부에서 생성한 참조정보를 가지고 콜백 오브젝트의 메소드를 호출한다. 콜백은 클라이언트 메소드에 있는 정보와 템플릿이 제공한 참조정보를 이용해서 작업을 수행하고 그 결과를 다시 템플릿에 돌려준다,
* 템플릿은 콜백이 돌려준 정보를 사용해서 작업을 마저 수행한다. 경우에 따라 최종 결과를 클라이언트에 다시 돌려주기도 한다.

### 편리한 콜백의 재활용

복잡한 익명 내부 클래스의 사용을 최소화 할 수 있는 방법으로 JDBC의 try/catch/finally 에 적용한 방법을 적용해볼 수 있다.

* 익명 내부 클래스를 사용한 클라이언트 코드

```java
public void deleteAll() throws SQLException {
    this.jdbcContext.workWithStatementStrategy(
        new StatementStrategy() {   //  변하지 않는 콜백 클래스 정의와 오브젝트 생성
            public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
                return c.preparedStatement("delete from users");    //  변하는 SQL 문장
            }
        }
    );
}
```

* 변하지 않는 부분을 분리시킨 deleteAll() 메소드

```java
public void deleteAll() throws SQLException {
    executeSql("delete from users");
}

private void executeSql(final String query) throws SQLException {
    this.jdbcContext.workWithStatementStrategy(
        new StatementStrategy() {   //  변하지 않는 콜백 클래스 정의와 오브젝트 생성
            public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
                return c.preparedStatement(query);
            }
        }
    );
}
```

sql을 담은 파라미터를 final로 선언하여 익명 내부 클래스인 콜백 안에서 직접 사용할 수 있게 하는 부분만 주의하면 된다.

### 템플릿/콜백의 응용

스프링은 단지 이를 편리하게 사용할 수 있도록 도와주는 컨테이너를 제공하고, 이런 패턴의 사용 방법을 지지해주는 것뿐이다. 스프링을 사용하는 개발자라면 당연히 스프링이 제공하는 템플릿/콜백 기능을 잘 사용할 수 있어야 한다. 스프링에 내장된 것을 원리도 알지 못한 채로 기계적으로 사용하는 경우와 적용된 패턴을 이해하고 사용하는 경우는 큰 차이가 있다.

고정된 작업 흐름을 갖고 있으면서 여기저기서 자주 반복되는 코드가 있다면, 중복되는 코드를 분리할 방법을 생각해보는 습관을 기르자

UserDao에서만 사용되기 아까운 재사용 가능한 콜백을 담고있는 executeSql() 메소드를 템플릿 클래스 안으로 옮길 수 있다.

```java
public class JdbcContext {
    ...
    public void executeSql(final String query) throws SQLException {
        workWithStatementStrategy(
            new StatementStrategy() {...}
        );
    }
}
```

이로써 모든 DAO 메소드에서 executeSql() 메소드를 사용할 수 있게 됐다.

![https://leejaedoo.github.io/assets/img/콜백재활용.jpeg](https://leejaedoo.github.io/assets/img/%EC%BD%9C%EB%B0%B1%EC%9E%AC%ED%99%9C%EC%9A%A9.jpeg)

결국 JdbcContext 안에 클라이언트와 템플릿, 콜백이 모두 함께 공존하여 동작하는 구조가 되었다.

일반적으로 성격이 다른 코드들은 가능한 한 분리하는 것이 낫지만, 이 경우는 하나의 목적을 위해 서로 긴밀하게 연관되어 동작하는 응집력이 강한 코드들이기 때문에 한 군데 모여있는 것이 유리하다.
