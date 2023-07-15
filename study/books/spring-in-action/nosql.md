# NoSQL

#### MongoDB의 유지성 도큐먼트

MongoDB : 오픈소스 도큐먼트 데이터(비정규화 된  구조의 정보) 베이스

\-> 스프링에서도 사용 가능 - 사용하는 세 가지 방법

* 객체 도큐먼트 매핑을 위한 애너테이션
* MongoTemplate을 사용한 템플릿 기반의  데이터베이스 액세스
* 자동 런타임 저장소 생성

스프링 데이터 JPA와 달리, 스프링 데이터 MongoDB는 자바 객체를 도큐먼트에 매핑하기 위한 애너테이션을 제공. 또한 MongoDB는 다수의 일반 도큐먼트 처리 태스크를 위한 템플릿 기반 데이터 액세스를 제공



**MongoDB 활성화**

```
@Configuration
@EnableMongoRepositories("orders.db") 
public class MongoConfig extends AbstractMongoConfiguration {
    @Override
    protected String getDatabaseName() {
        return "OrdersDB"; // 데이터베이스명을 지정해준다.
    }
    @Override
    public Mongo mongo() throws Exception { // Mongo클라이언트를 생성
        return new MongoClient("mongodbserver", 37017); // 디비 서버 설정과 포트설정(기본 localhost, 27017)
    }
}
```

\=> AbstractMongoConfiguration(최근 버전에서는 depreacted) 을 사용하여 암시적으로 직접 MongoTemplate을 선언하지 않음. MongoFactoryBean을 사용하지 않고 바로 MongoClient() 사용

&#x20;

\* MongoDB에 인증된 서버로 액세스가 필요할 수가 있음

```
@Override
public Mongo mongo() throws Exception {
     
     MongoCredential credential = MongoCredential.createMongoCRCredential(
                             env.getProperty("mongo.username"),"OrdersDB", env.getProperty("mongo.password").toCharArray());
     return new MongoClient(new ServerAddress("localhost", 37017),Arrays.asList(credential));
     // 인증정보가 포함된 MongoClient 만들기
}
```



**MongoDB 퍼시스턴트를  위한 애너테이션 모델 타입**

MongoDB는 객체-도큐먼트 매핑 애너테이션을 가지지  않으며, 단지 갭을 채울 수 있는 기회를 가진다. 자바 타입을 MongoDB 도큐먼트에 매핑한다

<figure><img src="https://blog.kakaocdn.net/dn/vvYmm/btqz8wksPN2/Zoo8AKpjFppBTk25XxIfLK/img.png" alt=""><figcaption><p>매핑 데이터</p></figcaption></figure>

@Document @Id는 JPA의 @Entity, @Id와 비슷하다

```
@Document // 도큐먼트
 public class Order {
 
  @Id // 아이디 선언
  private String id;
  
  @Field("client") // 기본 필드명 오버라이드
  private String customer; // 커스터머 프로퍼틴는 @Feild로 애너테이션 되고, 도큐먼트가  지속 유지될때 field로 명명된 클라이언트로 매핑
  
  private String type;
  
  private Collection<Item> items = new LinkedHashSet<Item>();

  public String getCustomer() {
    return customer;
  }
  
  public void setCustomer(String customer) {
    this.customer = customer;
  }
  
  public String getType() {
    return type;
  }
  
  public void setType(String type) {
    this.type = type;
  }
}
```

\*item의 경우는 JPA에서 @OneToMany로 애너테이션 되어있고, 독립된 테이블로 관리되는 경우이다.(예시)

<figure><img src="https://blog.kakaocdn.net/dn/ZPWjv/btqz8YVj9UB/50EOMw3DTeIHyoyOPzjiEk/img.png" alt=""><figcaption><p>데이터 관계는 가지지만, 비정규화 되어있음</p></figcaption></figure>



**MongoTemplate을 사용하여 MongoDB 액세스**

AbstractAbstractMongoConfiguration을 확장하여 MongoTemplate 빈을 설정하였다

MongoTemplate의 구현 인터페이스인 MongoOperations을 타입으로 프로퍼티를 주입을 해야함

```
@Autowired
MongoOperations mongo;
```

&#x20;

\* MongoOperations는 MongoDB를 사용하기 위한, 여러 유용한 메소드를 제공한다. 아래는 그러한 메소드들의 예시이다.

```
// 컬렉션을 가지고, 해당 컬렉션의 수를 세는 count()
long orderCount = mongo.getCollection("order").count(); 

// 새 객체를 저장하는 save()
Order order = new Order();
order.set~~~ // 객체 값 설정
~~~
mongo.save(order, "order");

// ID에 따라 검색을 하는 findById()
String orderId = test;
Order order = mongo.findById(orderId, Order.class);

// Query로 고급 검색을 가능할게 하는 find()
List<Order> chucksOrders = mongo.find(Query.query(
    Criteria.where("client").is("Chuck Wagon")), Order.class);
    // 클라이언트필드가 "Chuck Wagon"인 모든 케이스
    
List<Order> chucksWebOrders = mongo.find(Query.query(
    Criteria.where("customer").is("Chuck Wagon")
    .and("type").is("WEB")), Order.class);
    // Crieteria는 한 개의 필드를 체크하는 쿼리를  생성하기 위해 사용. WEB을 통해 Chuck의 모든 Order를 얻음
   
   
// 도큐먼트를 삭제하는 remove()
mongo.remove(order);
```



**MongoDB 저장소 작성**

\* 일반적으로, 만든 저장소 클래스에 MongoOperations를 주입하고 메소드를 구현한다. 그러나 저장소를 작성하는데 애를 쓰고 싶지 않다면, 런타임 시에 자동으로 저장소를 구현하게 한다.

OrderRepository 인터페이스로, Order 도큐먼트를 위한 기본 CRUD 동작을 제공하기 위해 MongoRepository를 확장한다.

```
 public interface OrderRepository extends MongoRepository<Order, String> {}
// MongoRepsoitory< 도큐먼트 , 아이디 >
```

\* 전 장의, JpaRepository확장가 마찬가지로, 런타임 시에 저장소의 구현체를 생성한다.

<figure><img src="https://blog.kakaocdn.net/dn/B0n3P/btqz9s2VLxs/MoVASYXJmjJKc5dap6IdxK/img.png" alt=""><figcaption><p>해당 구현체는, 위의 표에 맞는 구현 메소드를 상속한다</p></figcaption></figure>

맞춤형 쿼리 메소드

\* JPA때와 크게 다르지 않다.

List\<Order> findByCustomer(String c) - 등

\* 동사  - 대상 By - 조건

<figure><img src="https://blog.kakaocdn.net/dn/baMqnP/btqz8xwU0KE/c6y2DcSiLEOZqPc03k3uNK/img.png" alt=""><figcaption></figcaption></figure>



쿼리 지정 하기

@Query 애너테이션 사용

```
@Query("{'customer': 'Chuck Wagon', 'type' : ?0}")
List<Order> findChucksOrders(String t);
```

\-> JPA와 동일



맞춤형 저장소 혼합

... JPA와 동일

```
public interface OrderOperations {
  List<Order> findOrdersByType(String t);
}
// 중간 버전의 인터페이스  정의
```

Impl 사용

```
public class OrderRepositoryImpl implements OrderOperations {

@Autowired
private MongoOperations mongo; // MongoOperation 주입

public List<Order> findOrdersByType(String t) { 
    String type = t.equals("NET") ? "WEB" : t;
    Criteria where = Criteria.where("type").is(t);
    Query query = Query.query(where); 		// 쿼리 생성
    
    return mongo.find(query, Order.class); // 쿼리 수행
  }
}
```

마지막으로 중간버전의 인터페이스 확장을 위한, 저장소 인터페이스 변경

```
public interface OrderRepository
               extends MongoRepository<Order, String>, OrderOperations { ~~~ }
```

&#x20;



#### Neo4j로 그래프 데이터 사용하기

Neo4j : 그래프 데이터베이스 - 관계를 저장하고 탐색하도록 특별히 구축되었습니다. 노드를 사용하여 데이터 엔터티를 저장하고 엣지로는 엔터티 간의 관계를 저장합니다. 엣지는 항상 시작 노드, 끝 노드, 유형과 방향을 가지며, 상-하위 관계, 동작, 소유자 등을 문서화 합니다. 하나의 노드가 가질 수 있는 관계의 수와 종류에는 제한이 없음.

\* MongoDB와 JPA와 동일한 기능을 제공한다



**스프링으로 Neo4j 설정하기**

@EnableNeo4jRepositories : Neo4j 자동저장소 활성화 애너테이션

```
@Configuration
@EnableNeo4jRepositories(basePackages="orders.db") // 자동 저장소 활성화, marker Repository 확장 후, 인터페이스용 db 패키지를 스캔
public class Neo4jConfig extends Neo4jConfiguration { 
  
  public Neo4jConfig() { // 생성자, orders패키지의, 모델 클래스를 찾는다
    setBasePackage("orders"); // 모델 베이스 패키지 세팅
  }
  
  @Bean(destroyMethod="shutdown") 
  public GraphDatabaseService graphDatabaseService() { // 참조하는 그래프데이터베이스
   return new GraphDatabaseFactory()
         .newEmbeddedDatabase("/tmp/graphdb"); // 임베디드 데이터베이스 패키지 설정(인메모리 아님), 애플리케이션의 일부로 JVM 내에서 동작
  }
  /*
   @Bean(destroyMethod="shutdown") 
  public GraphDatabaseService graphDatabaseService() {
   return new SpringRestGraphDatabase(
           "http://grephdb:7575/db/data/", env.getProperty("db.username"), env.getProperty("db.password")
           );
   // 원격서버 + 보안 요소가 있는 경우
  }
  */
}
```



#### 그래프 엔티티 애너테이션

Neo4j에서는, 아래의 애너테이션을 사용하여, 노드와 관계 등을 애너테이션 한다.

<figure><img src="https://blog.kakaocdn.net/dn/bA8nhr/btqAaul23t6/lOSykeazYkpMhvTnfXbeX1/img.png" alt=""><figcaption><p>애너테이션을 사용하여 도메인 타입을, 노드와 간계로 그래프 형식 매핑</p></figcaption></figure>

&#x20;

노드 엔티티는, @NodeEntity.. 관계 엔티티는 @RelationshipEntity로 애너테이션 한다. 단순한 노드끼리의 관계는 @RelationTo 애너테이션으로 정의 가능

```
@NodeEntity // Order는 노드엔티티
public class Order {

  @GraphId // 그래프 아이디
  private Long id;
  private String customer;
  private String type;
 
  // items 프로퍼티는, Order와 Item의 Set에 관계됨
  @RelatedTo(type="HAS_ITEMS")
  private Set<Item> items = new LinkedHashSet<Item>();
  ~~~
}
```

```
@NodeEntity // 아이템 노드
public class Item {
  
  @GraphId // 그래프의 아이디
  private Long id;
  private String product;
  private double price;
  private int quantity;
  ~~~
}
```

<figure><img src="https://blog.kakaocdn.net/dn/bom687/btqz9IY969q/KNCyLuXXL7hMQIvqfDsfW1/img.png" alt=""><figcaption><p>두 노드는, 관계로 엮어있고 관계 자체의 프로퍼티를 가지지는 않음</p></figcaption></figure>

&#x20;

\==>조금 더  복잡한 경우

\* @RelationShipEntity 사용 !!

```
@RelationshipEntity(type="HAS_LINE_ITEM_FOR") // 관계엔티티, 타입 정의
public class LineItem {
  @GraphId // 그래프 아이디
  private Long id;
  
  @StartNode // 시작 노드
  private Order order;
  
  @EndNode // 끝나는 노드
  private Product product;
  
  // 관계엔티티의 자체 프로퍼티
  private int quantity;

  ~~~
}
```

\* LineItem 관계 생성 시, 데이터베이스 내에 유지되는 quanity 프로퍼티를 가진다.

<figure><img src="https://blog.kakaocdn.net/dn/Im0fh/btqAa8W9KHY/Xo7odkkzNPKkCXZpGUiz2k/img.png" alt=""><figcaption><p>두 노드 사이에, 관계 엔티티가 필요한 경우 관계 자체가 프로퍼티를 가짐</p></figcaption></figure>

&#x20;

Neo4jTemplate 사용하기

\* MongoDB의 경우처럼, Neo4jConfiguration을 확장한다면, Neoj4Template빈이 자동으로 생성됨

```
// 마찬가지로, Neo4jOpreations로 직접 오토와이어링
@Autowired
private Neo4Operations neo4j;
```

\* Neo4Operations도 마찬가지로 Neo4j를 사용하기 위한, 여러 유용한 메소드를 제공한다. 아래는 그러한 메소드들의 예시이다.

```
// save() 메소드
Order order = ...;
Order savedOrder = neo4j.save(order);

// findOne() 메소드 - id기반 // 존재하지 않으면 NotFoundException
Order order = neo4j.findOne(42, Order.class);

// 객체 전부 검색 findAll()
EndResult<Order> allOrders = neo4j.findAll(Order.class);

// count()
long orderCount = count(Order.class);

// delete()
neo4j.delete(order);

// 가장 특이한  메소드중 하나 createRelastionshipBetween() 메소드 -- 두 노드간의 관계를 만든다
Order order = ...;
Product prod = ...;
LineItem lineItem = neo4j.createRelationshipBetween(order, prod, LineItem.class, "HAS_LINE_ITEM_FOR", false); // order와 prod간의 LineItem 관계를 생성(type - HAS_LINE_ITEM_FOR)
lineItem.setQuantity(5);
neo4j.save(lineItem);
```



#### 자동 저장소 만들기

MongoDB와 동일.. 겹치는 내용이 많아서 코드와 간단한 설명정도만 남김.

\* GraphRepsitory 인터페이스 확장

```
public interface OrderRepository extends GraphRepository<Order> {}
```

&#x20;

\* 확장으로 얻는 메소드

<figure><img src="https://blog.kakaocdn.net/dn/13RyD/btqAcbMGDRV/dU3x0RKxm4tgP2F0HiOrRK/img.png" alt=""><figcaption></figcaption></figure>

<figure><img src="https://blog.kakaocdn.net/dn/d522yJ/btqz8YBuYLt/mw8ugO8UYIf7HzCcwyPT4k/img.png" alt=""><figcaption></figcaption></figure>

\* Order saveOrder = orderRepository.save(order) 등, 간단하게 사용 가능



**맞춤형 쿼리 메소드**

\=> MongoDB나 JPA와 명명규칙은 같지만, 앞의 둘과는 달리 OrderRepository에 쿼리 메소드를 따로 지정해주어야함

```
public interface OrderRepository extends GraphRepository<Order> {

  List<Order> findByCustomer(String customer);
  List<Order> findByCustomerAndType(String customer, String type); // 다른 메소드와 유사하며, 주어진 타입의 type 프로퍼티를 가진다.

}
```

맞춤형 쿼리 지정 : 마찬가지로 @Query 애너테이션을 사용한다

```
@Query("match (o:Order)-[:HAS_ITEMS]->(i:Item) " +
       "where i.product='Spring in Action' return o")
List<Order> findSiAOrders();
```



**맞춤형 저장소 동작 혼합**

MongoDB와 거의 동일하다

\* OrderOperation 중간 인터페이스 생성

```
public interface OrderOperations {
  List<Order> findSiAOrders();
}
```

&#x20;

\* OrderRepository 확장

```
public interface OrderRepository
       extends GraphRepository<Order>, OrderOperations {
   ~~~
}
```

\--> 마찬가지로, Impl 클래스로 구현체 클래스를 검색함

\* OrderRepositoryImpl

```
public class OrderRepositoryImpl implements OrderOperations {
  private final Neo4jOperations neo4j;
  
  @Autowired
  public OrderRepositoryImpl(Neo4jOperations neo4j) {
    this.neo4j = neo4j;
  }
  
  public List<Order> findSiAOrders() {
    Result<Map<String, Object>> result = neo4j.query(
            "match (o:Order)-[:HAS_ITEMS]->(i:Item) " +
            "where i.product='Spring in Action' return o",
            EndResult<Order> endResult = result.to(Order.class);
            
    return IteratorUtil.asList(endResult);
  }
}
```



#### Redis에서 키-값 데이터 사용

\* 키-값 데이터베이스 :  딕셔너리, 해시 맵과 같은, 단순한 데이터 처리를 위한 데이터 스토리지 패러다임 - Redis

<figure><img src="https://blog.kakaocdn.net/dn/dsw7gJ/btqAaWbEyMe/FzvIYUJCfzh6TXkkUprnHK/img.png" alt=""><figcaption></figcaption></figure>

\* 스프링 데이터 Redis는, 데이터베이스에서  데이터를 가져오고, 저장하기 위한 템플릿을 구현



**Redis에 연결하기**

스프링데이터는 Redis를 위한 4가지 Connection Factory를 제공한다&#x20;

\*\* JedisConnectionFactory \*\*JredisConnectionFactory \*\* LettuceConnectionFactory \*\* SrpConnectionFactory

\-> 책에서는 JedisConnectionFactory를 사용

```
@Bean
public RedisConnectionFactory redisCF() {
  JedisConnectionFactory cf = new JedisConnectionFactory();
  cf.setHostName("redis-server"); // 호스트 서버 설정
  cf.setPort(7379); // 포트 설정
  cf.setPassword("foobared");  // 비밀번호 설정
  return cf; 
}
```



**RedisTemplate 사용하기**

ConnectionFactory는 기본적으로 byte 배열을 가져올 수 있다.

```
RedisConnectionFactory cf = ...;
RedisConnection conn = cf.getConnection();
// 세팅
conn.set("greeting".getBytes(), "Hello World".getBytes())

// 검색
byte[] greetingBytes = conn.get("greeting".getBytes());
String greeting = new String(greetingBytes);
```

하지만,  실제로 byte배열보다는, 상위 레벨의 데이터 액세스가 필요하다. 스프링데이터 Redis 이럴 때 사용할 수 있는 2가지의 Template을 제공한다.

\*\* RedisTemplate  \*\* StringRedisTemplate(키, 밸류가 모두 String일 경우 권장)

```
RedisConnectionFactory cf = ~~;
RedisTemplate<String, Product> redis = new RedisTemplate<String, Product>();
redis.setConnectionFactory(cf); // 일반 RedisTemplate의 경우엔 지정을 해주어야함

=====
/// 자주 사용할 경우 빈 설정
@Bean
public RedisTemplate<String, Product> redisTemplate(RedisConnectionFactory cf) {
  RedisTemplate<String, Product> redis = new RedisTemplate<String, Product>();
  redis.setConnectionFactory(cf);
  return redis;
}
```

```
RedisConnectionFactory cf = ...;
StringRedisTemplate redis = new StringRedisTemplate(cf); // RedisConnectionFactory를 바로 허용

====
//자주 사용할 경우 빈 설정
public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory cf) {
  return new StringRedisTemplate(cf);
}
```

&#x20;

\* RedisTemplate가 지원하는 다양한 메소드

예시..&#x20;

```
///// 단순 Value 메소드
// sku 프로퍼티에 값을 가져오고 싶을 때 설정
redis.opsForValue().set(product.getSku(), product);
// sku가 123456일 때,
Product product = redis.opsForValue().get("123456");

//// 리스트 메소드 left(시작점) right(종단)
// 리스트의 종단(오른쪽)에 값 추가
redis.opsForList().rightPush("cart", product);

// 종단 pop 메소드
Product last = redis.opsForList().rightPop("cart");

// 단순 값 추출
List<Product> products = redis.opsForList().range("cart", 2, 12);

//// 세트 메소드
// 세트에 아이템 추가
redis.opsForSet().add("cart", product);

// difference, union, intersec
List<Product> diff = redis.opsForSet().difference("cart1", "cart2");
List<Product> union = redis.opsForSet().union("cart1", "cart2");
List<Product> intersect = redis.opsForSet().intersect("cart1", "cart2");

//// 키 바인딩.. 주어진 키에 집중한다
// cart라는 키가 제공하는 리스트 엔트리의 Product 객체
BoundOperations<String, Product> cart = redis.boundListOps("cart"); 
Product popped = cart.rightPop();
cart.rightPush(product1);
cart.rightPush(product2);
```

&#x20;

<figure><img src="https://blog.kakaocdn.net/dn/7nnen/btqz9t8869a/qkFrZXSa4V912lidx1NKkK/img.png" alt=""><figcaption><p>redisTemplate의 다양한  메소드</p></figcaption></figure>



**키와 값의 직렬 변환 설정**

엔트리가 Redis에 키-값으로 저장된 것을 직렬화하는 직렬 변환기를, 스프링 데이터 Redis가 지원한다

* GenericToStringSerializer - 일반적인 스프링 변환 서비스
* JacksonJsonRedisSerializer - Jackson1를 이용하여 JSON 직렬 변환
* Jackson2JsonRedisSerializer - Jackson2를 이용하여 JSON 직렬 변환
* JdkSerializationRedisSerializer - 자바 직렬 변환
* OxmSerializer - XML 직력별환 용, 스프링 O/X 매핑 진행자/비진행자를  이용한 변환
* StringRedisSerializer - String 키와 값의 직렬변환

&#x20;

RedisTemplate은 JdkSerializationRedisSerializer를 사용.. StringRedisTemplate은 StringRedisSerializer를 사용 => 다른 직렬 변환도 당연히 사용 가능하다

\==> Jackson2JsonRedisSerializer를 사용하여, JSON으로 직렬 변환하고 싶을 때의 예시

```
@Bean
public RedisTemplate<String, Product> redisTemplate(RedisConnectionFactory cf) {
  RedisTemplate<String, Product> redis = new RedisTemplate<String, Product>();
  redis.setConnectionFactory(cf);
  
  // 키는 String으로
  redis.setKeySerializer(new StringRedisSerializer());
  
  // 값은 JSON으로 직렬화
  redis.setValueSerializer(new Jackson2JsonRedisSerializer<Product>(Product.class));
  
  return redis;
}
```
