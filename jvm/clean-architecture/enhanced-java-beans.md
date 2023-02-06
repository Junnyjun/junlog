# Enhanced java beans

개발시 Builder를 지원해주지 않거나, 구 버전을 사용하게 되면 종종 마주치게 되어 \
코드를 지저분 하게하는 자바 빈즈 패턴을 개선할 수 있는 방식이다.

#### Before

```java
    @Bean
    public DataSource dataSource(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(DriverClass);
        dataSource.setPassword(password);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        return dataSource;
    }
```

#### after

```java
    @Bean
    public DataSource dataSource(){
        return new DriverManagerDataSource(){{
            setDriverClassName(DriverClass);
            setPassword(password);
            setUrl(url);
            setUsername(username);
        }};
    }

```

