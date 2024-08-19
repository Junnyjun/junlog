# Data Rest

## Data Rest?

스프링 개발 중, 단순 조회를 위해 Service, Controller를 만들 때 많은 보일러플레이트 코드를 작성해야 하는 불편함을 경험한 적이 있을 것이다. 이러한 불편함을 해소하기 위해 Spring Data REST는 리포지토리 인터페이스를 자동으로 RESTful API로 노출하여 개발자의 부담을 줄인다. 리포지토리 인터페이스만 정의하면, Spring Data REST는 기본적으로 CRUD 기능을 갖춘 RESTful 엔드포인트를 자동으로 생성한다.

Spring Data REST는 또한 HATEOAS 원칙을 준수하여 리소스 간의 탐색성을 높인다. 클라이언트는 루트 URL에 GET 요청을 보내어, 서버가 제공하는 모든 리소스에 대한 링크 정보를 포함하는 JSON 응답을 받을 수 있다. 이를 통해 클라이언트는 별도의 문서화 없이도 API의 탐색이 가능하다.

이처럼 Spring Data REST는 기본적인 CRUD 기능을 자동으로 처리해주며, 개발자는 비즈니스 로직에 더욱 집중할 수 있다. 따라서, 단순한 데이터 조회 및 관리를 위해 반복적인 코드를 작성하는 대신, Spring Data REST를 활용하여 더욱 효율적인 개발을 할 수 있을 것이다.

### Paging ?

Spring Data REST는 페이징 및 정렬 기능을 제공하여 대규모 데이터셋을 효율적으로 다룰 수 있게 해줍니다. 이 기능을 활용하면 모든 데이터를 한 번에 반환하지 않고, 클라이언트가 원하는 크기로 데이터를 나눠서 제공할 수 있습니다.

#### Paging

기본적으로 `PagingAndSortingRepository<T, ID>`를 상속받는 리포지토리는 자동으로 페이징 기능을 지원합니다. 특정 URL 파라미터를 사용해 페이지 크기와 시작 페이지 번호를 지정할 수 있습니다.&#x20;

페이징을 직접 정의한 쿼리 메서드에 적용하려면, 메서드 시그니처에 `Pageable` 파라미터를 추가하고, 반환 타입을 `Page` 또는 `Slice`로 변경해야 합니다. 이렇게 하면 Spring Data REST가 자동으로 페이징 링크를 응답에 추가하여, 클라이언트가 다음 페이지 또는 이전 페이지로 쉽게 이동할 수 있게 됩니다.

**이전 및 다음 링크**

각 페이지 응답에는 현재 페이지에 따라 이전 및 다음 페이지의 링크가 포함됩니다. 첫 페이지일 경우 `prev` 링크는 생성되지 않으며, 마지막 페이지일 경우 `next` 링크는 생성되지 않습니다.&#x20;

```bash
curl localhost:8080/people?size=5
```

응답은 다음과 같은 형태를 가집니다:

```json
{
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/persons{&sort,page,size}", 
      "templated" : true
    },
    "next" : {
      "href" : "http://localhost:8080/persons?page=1&size=5{&sort}", 
      "templated" : true
    }
  },
  "_embedded" : {
    … data …
  },
  "page" : { 
    "size" : 5,
    "totalElements" : 50,
    "totalPages" : 10,
    "number" : 0
  }
}
```

이처럼 응답에는 현재 페이지 정보와 전체 페이지 수, 총 엔티티 수 등이 포함되어 있어 UI에서 사용자의 위치를 나타내는 도구를 쉽게 구성할 수 있습니다.

#### Sorting

Spring Data REST는 리포지토리의 정렬 기능을 사용해 결과를 정렬할 수 있는 파라미터를 지원합니다. 특정 속성에 따라 결과를 정렬하려면 `sort` URL 파라미터를 사용합니다.&#x20;

```bash
curl -v "http://localhost:8080/people/search/nameStartsWith?name=K&sort=name,desc"
```

여러 속성을 기준으로 정렬하려면 `sort=PROPERTY` 파라미터를 여러 개 추가할 수 있습니다. 속성 경로 표기법을 사용해 중첩된 속성으로도 정렬할 수 있으며, 이 순서에 따라 `Pageable`에 정렬 정보가 추가됩니다.

Spring Data REST를 사용하면 도메인 모델을 기본적으로 내보낼 수 있습니다. 하지만 때로는 다양한 이유로 해당 모델의 뷰를 변경해야 할 필요가 있습니다. 이 섹션에서는 리소스의 뷰를 단순화하고 축소된 형태로 제공하기 위해 프로젝션과 엑서프츠를 정의하는 방법을 다룹니다.

### Projections?

#### Projections

해당 도메인 객체에 대한 리포지토리를 아래와 같이 생성했다고 가정

```java
interface PersonRepository extends CrudRepository<Person, Long> {}
```

기본적으로 Spring Data REST는 이 도메인 객체를 모든 속성을 포함한 형태로 내보냅니다. `firstName`과 `lastName`은 단순한 데이터 객체로 내보내지고, `address` 속성에 대해서는 두 가지 옵션이 있습니다. 첫 번째 옵션은 `Address` 객체에 대한 리포지토리를 정의하는 것입니다

```java
interface AddressRepository extends CrudRepository<Address, Long> {}
```

이 경우, `Person` 리소스는 `address` 속성을 해당 주소 리소스로 연결되는 URI로 렌더링합니다. 예를 들어, 시스템에서 "Frodo"를 조회하면 다음과 같은 HAL 문서를 볼 수 있습니다:

```json
{
  "firstName" : "Frodo",
  "lastName" : "Baggins",
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/persons/1"
    },
    "address" : {
      "href" : "http://localhost:8080/persons/1/address"
    }
  }
}
```

또 다른 방법으로 `Address` 도메인 객체에 리포지토리를 정의하지 않으면, Spring Data REST는 `Person` 리소스 내에 주소 데이터를 포함하여 다음과 같이 반환합니다:

```json
{
  "firstName" : "Frodo",
  "lastName" : "Baggins",
  "address" : {
    "street": "Bag End",
    "state": "The Shire",
    "country": "Middle Earth"
  },
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/persons/1"
    }
  }
}
```

하지만 주소 정보를 전혀 포함하지 않고자 할 경우, 기본적으로 Spring Data REST는 `id`를 제외한 모든 속성을 내보냅니다. 다음은 주소를 포함하지 않는 프로젝션의 예입니다:

```java
@Projection(name = "noAddresses", types = { Person.class }) 
interface NoAddresses { 

  String getFirstName(); 

  String getLastName(); 
}
```

`@Projection` 어노테이션은 이 인터페이스를 프로젝션으로 표시합니다. `name` 속성은 프로젝션의 이름을 지정하며, `types` 속성은 이 프로젝션이 `Person` 객체에만 적용되도록 합니다. 이 프로젝션은 `firstName`과 `lastName`만을 내보내고, `address` 정보는 제외됩니다.

#### Excerpts

엑서프츠는 컬렉션 리소스에 자동으로 적용되는 프로젝션의 일종으로, 리소스 컬렉션을 단순화된 형태로 제공할 때 유용합니다. 예를 들어, `PersonRepository`에 엑서프츠 프로젝션을 적용하려면 다음과 같이 할 수 있습니다:

```java
@RepositoryRestResource(excerptProjection = NoAddresses.class)
interface PersonRepository extends CrudRepository<Person, Long> {}
```

이렇게 하면 `Person` 리소스가 컬렉션에 포함될 때 `NoAddresses` 프로젝션이 자동으로 적용됩니다.

엑서프츠는 개별 리소스에는 자동으로 적용되지 않으며, 컬렉션 데이터를 기본 미리보기 형태로 제공하는 데 중점을 둡니다.

이렇게 프로젝션과 엑서프츠를 사용하면, 도메인 모델을 다양한 형태로 제공할 수 있으며, 필요에 따라 노출되는 데이터를 조정할 수 있습니다.
