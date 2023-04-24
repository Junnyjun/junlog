# 애스펙트 지향 스프링

## 4. 애스펙트 지향 스프링

AOP 란?

\- 횡단 관심사의 모듈화, 한 애플리케이션의 여러 부분에 영향을 주는 기능

\- 보안, 로깅, 트랜잭션 등

\- 장점 : 전체 코드기반에 흩어져있는 것을 응집, 주요 관심사 이외의 것을 모듈화하여 코드가 깔끔해짐

## 용어

어드바이스

\- 애스펙트가 해야할 작업

before - 호출 전\*\*, after 실행 후, after-running - 성공 후, after-throwing - 실패 후\*\*, around - 전후로 간단한 기능

조인 포인트

\- 어드바이스 적용 가능한 지점(point)

포인트커트

\- 조인 포인트의 영역을 좁히는 일을 함

\- 어드바이스 = 무엇, 언제 // 포인트 커트 = 어디서

\- 간단하게, 클래스 메소드명 지정부터, 정규표현식 정의 도 가능

애스펙트

\- 어드바이스 + 포인트커트 = 언제 무엇을 어디서 할지 정의한 것

위빙

\- 타깃 객체에, 애스펙트를 적용하여 새로운 프록시 객체를 생성하는 절차

\-> 컴파일 시간, 클래스로드 시간, 실행 시간에 위빙됨

\---

## 포인트 커트를 이용한 조인 포인트 선택

\- 스프링AOP는 Aspetj의 포인트커트 표현식 언어를 이용해 정의

포인트커트 작성 예시

```
package Cafe

public intaerface Coffee {
  public void addShot();
}

==> addShot() 메소드를 트리거링 하는 애스펙트 만들기

execution(* Cafe.Coffee.addShot(..)) && within(Bob.*)
==> 포인트커트로 Bob.Coffee.addShot 메소드를 어드바이스 할 것이며, Bob.*로 클래스 범위제한 

execution(* Cafe.Coffee.addShot(..)) && bean('americano')
==> 포인트커트로 빈 선택
```

애스펙트 정의하기

@Aspect 애너테이션 이용

\==> 이는 POJO가 아닌 애스펙트임을 나타냄

```
@Aspect
public class Order {
  @Before("execution(* Cafe.Coffee.addShot(..))")
  .... addShot 하기전 메소드

  @AfterRunning("execution(* Cafe.Coffee.addShot(..))")
  .... addShot 성공 후 메소드

  @AfterThrowing("execution(* Cafe.Coffee.addShot(..))")
  ... addShot 실패 후 메소드
}

==> 각각 addShot()메소드 실행 전, 실행 성공 후, 실행 실패 후

이외에도 
@After : 성공이든 실패든, 메소드 직후
@Around : 대상 객체의 메서드 실행 전, 후 또는 예외 발생 시점에 공통 기능을 실행(로깅?..)
```

@Pointcut 이용하여 포인트커트 선언하기

```
@Aspect
public class Order {
  @Pointcut("execuetion(* Cafe.Coffee.addShot(..))")
  public void shotPlus();

  @Before("shotPlus()")
  .... addShot 하기전 메소드

  @AfterRunning("shotPlus()")
  .... addShot 성공 후 메소드

  @AfterThrowing("shotPlus()")
  ... addShot 실패 후 메소드
}

==> 이렇게 포인트커트를, 메소드로 지정해 놓으면, 각 어드바이스에 대해 포인트커트의 메소드명으로 사용가능
```

@Around 어드바이스 만들기

```
public class Order {
  @Pointcut("execuetion(** Cafe.Coffee.addShot(..))")
  public void shotPlus();

  @Arround("shotPlus()")
  public void addShotAround(ProceedingJoinPoint jp) {
    try{ 
         .... addShot 하기전 메소드
         jp.proceed();
         .... addShot 성공 후 메소드
    catch(Throwable e) {
         ... addShot 실패 후 메소드
   }
}

===> 이렇게, before, afterRunning, afterThrowing을 하나로 묶을 수 있음
```

## 어드바이스로 파라미터 처리

\* 어드바이스를 이용하여, 파라미터 처리가 가능하다.

\* 예를 들어, 카페에서 샷추가를 하는 전체 잔 수를 기록한다고 할 때, 애스펙트로 이 값을 저장할 수 있다.

```
@Aspect
public class ShotCounter {
  @PointCut("execuetion(* Cafe.Coffee.addShot(int)) && args(shotAmount))
  public void shotAdded(int shotAmount){}

  @Before("shotAdded(int shotAmount))
    ... 추가된 샷 더하는 메소드
```

오토프록싱 : Aspect의 적용대상이 되는 객체에 대한 프록시를 자동으로 만들어 제공

```
@Configuration
@EnableAspectAutoProxy
==> 해당 애너테이션으로 Config단에서 오토프록싱 가능
```
