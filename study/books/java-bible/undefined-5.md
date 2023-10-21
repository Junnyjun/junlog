# 날짜와 시간

### 1. 날짜와 시간 <a href="#1" id="1"></a>

#### Calender와 GregorianCalendar <a href="#calender-gregoriancalendar" id="calender-gregoriancalendar"></a>

Calendar는 추상클래스이기 때문에 직접 객체를 생성할 수 없고, 메서드를 통해서 완전히 구현된 클래스의 인스턴스를 얻어야 한다.

```java
Calendar cal = new Calendar();			// 에러
Calendar cal = Calendar.getInstance();	// OK
```

Calendar를 상속받아 완전히 구현한 클래스로는 GregorianCalendar와 BuddhistCalendar가 있는데 getInstance()는 시스템의 국가와 지역설정을 확인해서 태국인 경우에는 BuddhistCalendar의 인스턴스를 반환하고, 그 외에는 GregorianCalendar의 인스턴스를 반환한다.

#### Date와 Calendar간의 변환 <a href="#date-calendar" id="date-calendar"></a>

1. Calendar를 Date로 변환

```java
Calendar cal = Calendar.getInstance();
...
Date d = new Date(cal.getTimeMillis());
```

2. Date를 Calendar로 변환

```java
Date d = new Date();
...
Calendar cal = Calendar.getInstance();
cal.setTime(d);
```

***

### 2. 형식화 클래스 <a href="#2" id="2"></a>

#### DeciamlFormat <a href="#deciamlformat" id="deciamlformat"></a>

숫자를 형식화 하는데 사용된다.

숫자 데이터를 정수, 부동소수점, 금액 등의 다양한 형식으로 표현할 수 있으며, 반대로 일정한 형식의 텍스트 데이터를 숫자로 쉽게 변환하는 것도 가능하다.

**간단한 사용법**

```java
double number = 1234567.89;
DecimalFormat df = new DecimalFormat("#.#E0");
String result = df.format(number);
```

#### SimpleDateFormat <a href="#simpledateformat" id="simpledateformat"></a>

날짜를 형식화한다.\
**간단한 사용법**

```java
Date today = new Date();
SimpleDateFormat df = new SimpleDateFormat("yyy-MM-dd");
String result = df.format(today);
```

#### ChoiceFormat <a href="#choiceformat" id="choiceformat"></a>

특정 범위에 속하는 값을 문자열로 변환해준다.

#### MessageFormat <a href="#messageformat" id="messageformat"></a>

데이터를 정해진 양식에 맞게 출력할 수 있도록 해준다.

***

### 3. java.time패키지 <a href="#3-javatime" id="3-javatime"></a>

LocalDate + LocalTime -> LocalDateTime\
LocalDateTime + 시간대 -> ZpnedDateTime

#### Period와 Duration <a href="#period-duration" id="period-duration"></a>

날짜 - 날짜 = Period\
시간 - 시간 = Duration

#### 객체 생성하기 - now(), of() <a href="#now-of" id="now-of"></a>

```java
LocalDate date = LocalDate.now();				//2015-11-23
LocalDate date = LocalDate.of(2015, 11, 24); 	//2015-11-24
```

#### Temporal과 TemporalAmount <a href="#temporal-temporalamount" id="temporal-temporalamount"></a>

Temporal, TemporalAccessor, TemporalAdjuster를 구현한 클래스

* LocalDate, LocalTime, LocalDateTime, ZonedDateTime, Instant 등

TemporalAmount를 구현한 클래스

* Period, Duration

#### TemporalUnit과 TemporalField <a href="#temporalunit-temporalfield" id="temporalunit-temporalfield"></a>

TemporalUnit

* 날짜와 시간의 단위를 정의해 놓은 인터페이스

TemporalField

* 년, 월, 일 등 날짜와 시간의 필드를 정의해 놓은 인터페이스

#### Instant <a href="#instant" id="instant"></a>

에포크 타임(EPOCH TIME, 1970-10-01 00:00:00 UTC)부터 경과된 시간을 나노초 단위로 표현한다.

#### DateTimeFormatter <a href="#datetimeformatter" id="datetimeformatter"></a>

자주 쓰이는 다양한 형식들을 기본적으로 정의하고 있으며, 그 외의 형식이 필요하다면 직접 정의해서 사용할 수도 있다.
