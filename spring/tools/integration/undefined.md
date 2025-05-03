# 채널 어댑터와 메시징 게이트웨이

현대 기업 환경에서는 다양한 시스템과 애플리케이션 간의 통합이 필수적입니다. 이러한 통합 요구는 다음과 같은 여러 요인에서 비롯됩니다:

* 레거시 시스템과 현대 애플리케이션의 공존
* 다양한 벤더 솔루션 사용
* 마이크로서비스 아키텍처에서의 서비스 간 통신
* 클라우드 서비스와의 통합 필요성
* 파트너 시스템과의 데이터 교환

Spring Integration은 이러한 다양한 외부 시스템과의 통합을 위해 채널 어댑터(Channel Adapter)와 메시징 게이트웨이(Messaging Gateway)라는 두 가지 핵심 메커니즘을 제공합니다.

### 채널 어댑터(Channel Adapter)

채널 어댑터는 Spring Integration의 메시징 채널과 외부 시스템 사이의 브리지 역할을 합니다. 채널 어댑터는 크게 두 가지 유형으로 나뉩니다:

#### 인바운드 채널 어댑터(Inbound Channel Adapter)

인바운드 채널 어댑터는 외부 시스템에서 데이터를 가져와 Spring Integration 메시징 채널로 전송합니다. 이는 단방향 통신으로, 외부 시스템으로부터 데이터를 "수신"하는 역할을 담당합니다.

```
@Bean
public IntegrationFlow fileReadingFlow() {
    return IntegrationFlows
        .from(Files.inboundAdapter(new File("/path/to/input"))
              .patternFilter("*.txt")
              .preventDuplicates(true),
              e -> e.poller(Pollers.fixedDelay(1000)))
        .transform(Files.toStringTransformer())
        .channel("fileContentChannel")
        .get();
}
```

또한 어노테이션을 사용해 메서드에 직접 적용할 수도 있습니다: `@InboundChannelAdapter`

```
@InboundChannelAdapter(value = "fileInputChannel", poller = @Poller(fixedRate = "1000"))
public File generateFileMessage() {
    // 파일 객체 생성 또는 참조 로직
    return new File("/path/to/input/newFile.txt");
}
```

#### 아웃바운드 채널 어댑터(Outbound Channel Adapter)

아웃바운드 채널 어댑터는 Spring Integration 메시징 채널에서 데이터를 받아 외부 시스템으로 전송합니다. 이 역시 단방향 통신으로, 메시지를 외부 시스템으로 "전송"하는 역할을 담당합니다.

```
@Bean
public IntegrationFlow jmsOutboundFlow() {
    return IntegrationFlows
        .from("orderProcessedChannel")
        .handle(Jms.outboundAdapter(connectionFactory)
               .destination("processedOrders"))
        .get();
}
```

### 주요 채널 어댑터 유형

Spring Integration은 다양한 프로토콜과 시스템을 위한 풍부한 채널 어댑터 세트를 제공합니다:

#### 파일 시스템 어댑터

파일 시스템에서 파일을 읽거나 파일로 쓰는 작업을 수행합니다.

```
// 인바운드 파일 어댑터 (파일 → 메시지)
@Bean
public IntegrationFlow fileInboundFlow() {
    return IntegrationFlows
        .from(Files.inboundAdapter(new File("/path/to/input"))
              .patternFilter("*.csv"),
              e -> e.poller(Pollers.fixedDelay(5000)))
        .transform(Files.toStringTransformer(StandardCharsets.UTF_8))
        .channel("newCustomersChannel")
        .get();
}

// 아웃바운드 파일 어댑터 (메시지 → 파일)
@Bean
public IntegrationFlow fileOutboundFlow() {
    return IntegrationFlows
        .from("processedOrdersChannel")
        .handle(Files.outboundAdapter(new File("/path/to/output")))
        .get();
}
```

#### JDBC 어댑터

관계형 데이터베이스와의 통합을 지원합니다.

```
// JDBC 인바운드 어댑터 (데이터베이스 → 메시지)
@Bean
public IntegrationFlow jdbcInboundFlow() {
    return IntegrationFlows
        .from(Jdbc.inboundAdapter(dataSource)
              .query("SELECT * FROM orders WHERE status = 'NEW'")
              .rowMapper(new OrderRowMapper()),
              e -> e.poller(Pollers.fixedDelay(10000)))
        .channel("newOrdersChannel")
        .get();
}

// JDBC 아웃바운드 어댑터 (메시지 → 데이터베이스)
@Bean
public IntegrationFlow jdbcOutboundFlow() {
    return IntegrationFlows
        .from("processedOrdersChannel")
        .handle(Jdbc.outboundAdapter(dataSource)
               .sql("UPDATE orders SET status = 'COMPLETED' WHERE id = :id"))
        .get();
}
```

#### HTTP 어댑터

HTTP/REST 서비스와의 통합을 지원합니다.

```
// HTTP 인바운드 어댑터 (HTTP 요청 → 메시지)
@Bean
public IntegrationFlow httpInboundFlow() {
    return IntegrationFlows
        .from(Http.inboundChannelAdapter("/api/orders")
              .requestMapping(m -> m.methods(HttpMethod.POST)))
        .channel("newOrdersChannel")
        .get();
}

// HTTP 아웃바운드 어댑터 (메시지 → HTTP 요청)
@Bean
public IntegrationFlow httpOutboundFlow() {
    return IntegrationFlows
        .from("shippingRequestChannel")
        .handle(Http.outboundGateway("https://shipping-api.example.com/shipments")
               .httpMethod(HttpMethod.POST)
               .expectedResponseType(ShipmentConfirmation.class))
        .channel("shipmentConfirmationChannel")
        .get();
}
```

### 메시징 게이트웨이(Messaging Gateway)

메시징 게이트웨이는 외부 시스템과의 양방향 통신을 가능하게 하는 구성 요소입니다. 특히 중요한 것은 게이트웨이가 Spring Integration의 메시징 인프라와 애플리케이션 코드 사이의 "깔끔한" 인터페이스 역할을 한다는 점입니다.

게이트웨이를 사용하면:

* 애플리케이션 코드가 Spring Integration의 메시징 API에 직접 의존하지 않음
* 쉽게 테스트할 수 있는 깔끔한 POJO 기반 인터페이스 제공
* 동기 및 비동기 통신 패턴 모두 지원

#### 게이트웨이 기본 사용법

가장 기본적인 형태의 게이트웨이는 인터페이스를 정의하고 `@MessagingGateway` 어노테이션을 붙이는 것입니다:

```
@MessagingGateway(defaultRequestChannel = "orderProcessingChannel")
public interface OrderService {
    void processOrder(Order order);
}
```

위의 간단한 인터페이스를 통해 Spring Integration은 런타임에 프록시 구현체를 생성합니다. 이 구현체는 `processOrder` 메서드가 호출될 때 Order 객체를 메시지 페이로드로 변환하고, 이를 `orderProcessingChannel`로 전송합니다.

#### 응답이 있는 게이트웨이

응답이 필요한 경우 메서드의 반환 타입을 명시적으로 정의할 수 있습니다:

```
@MessagingGateway(defaultRequestChannel = "orderProcessingChannel",
                  defaultReplyChannel = "orderConfirmationChannel")
public interface OrderService {
    OrderConfirmation processOrder(Order order);
}
```

이 경우, Spring Integration은:

* Order 객체를 메시지로 변환하여 orderProcessingChannel로 전송
* orderConfirmationChannel에서 응답 메시지를 기다림
* 응답 메시지의 페이로드를 OrderConfirmation 타입으로 변환하여 반환

#### 고급 게이트웨이 기능

게이트웨이는 다양한 고급 기능을 제공합니다:

**1. 특정 메서드에 대한 채널 오버라이드**

```
@MessagingGateway(defaultRequestChannel = "standardOrdersChannel")
public interface OrderService {
    OrderConfirmation processOrder(Order order);
    
    @Gateway(requestChannel = "priorityOrdersChannel",
             replyTimeout = 10000)
    OrderConfirmation processPriorityOrder(Order order);
}
```

**2. 비동기 처리**

```
@MessagingGateway(defaultRequestChannel = "orderProcessingChannel")
public interface OrderService {
    // Java의 Future 사용
    Future<OrderConfirmation> processOrderAsync(Order order);
    
    // CompletableFuture 사용
    CompletableFuture<OrderConfirmation> processOrderWithCompletableFuture(Order order);
}
```

**3. 헤더 조작**

```
@MessagingGateway(defaultRequestChannel = "orderProcessingChannel")
public interface OrderService {
    // 매개변수를 헤더로 매핑
    @Gateway(requestChannel = "orderProcessingChannel")
    OrderConfirmation processOrder(
        Order order, 
        @Header("region") String region,
        @Header("priority") int priority);
}
```

### 통합 예제: 주문 처리 시스템

다양한 채널을 통해 들어오는 주문을 처리하고, 여러 외부 시스템과 통합하는 주문 처리 시스템을 설계해 보겠습니다.

```
@Configuration
@EnableIntegration
public class OrderProcessingIntegrationConfig {

    // 1. HTTP를 통한 주문 수신
    @Bean
    public IntegrationFlow webOrdersFlow() {
        return IntegrationFlows
            .from(Http.inboundGateway("/api/orders")
                  .requestMapping(m -> m.methods(HttpMethod.POST))
                  .requestPayloadType(WebOrderDTO.class))
            .transform(webOrderTransformer())
            .channel("newOrdersChannel")
            .get();
    }
    
    // 2. 주문 검증
    @Bean
    public IntegrationFlow orderValidationFlow() {
        return IntegrationFlows
            .from("newOrdersChannel")
            .filter(this::validateOrder)
            .channel("validatedOrdersChannel")
            .get();
    }
    
    // 3. 게이트웨이 정의
    @MessagingGateway(defaultRequestChannel = "inventoryRequestChannel",
                      defaultReplyChannel = "inventoryResponseChannel")
    public interface InventoryGateway {
        boolean checkInventory(Order order);
    }
    
    // 4. 재고 확인 플로우
    @Bean
    public IntegrationFlow inventoryFlow() {
        return IntegrationFlows
            .from("inventoryRequestChannel")
            .handle(Http.outboundGateway("http://inventory-service/api/check")
                   .httpMethod(HttpMethod.POST)
                   .expectedResponseType(InventoryResponse.class))
            .transform(response -> ((InventoryResponse) response).isAvailable())
            .channel("inventoryResponseChannel")
            .get();
    }
}
```

이러한 구성을 통해 Spring Integration은 다양한 시스템 간의 통합을 위한 강력하고 유연한 프레임워크를 제공합니다.
