# 개요

Spring Integration은 엔터프라이즈 통합 패턴(Enterprise Integration Patterns)을 구현한 Spring 프레임워크의 확장 모듈입니다. 이 모듈은 Gregor Hohpe와 Bobby Woolf의 "Enterprise Integration Patterns" 책에서 설명된 패턴들을 Java 개발자들이 쉽게 적용할 수 있도록 합니다.

Spring Integration의 주요 목표:

* 시스템 간의 느슨한 결합(loose coupling) 제공
* 비동기 메시징 지원
* 다양한 전송 프로토콜 및 엔드포인트 연결
* 메시지 변환 및 라우팅 단순화
* 선언적 어댑터를 통한 외부 시스템 통합

### 핵심 개념

#### 메시지(Message)

메시지는 Spring Integration의 기본 데이터 전송 단위입니다.

```
public interface Message<T> {
    T getPayload();
    MessageHeaders getHeaders();
}
```

메시지는 두 가지 주요 부분으로 구성됩니다:

* 페이로드(Payload): 실제 전송되는 데이터
* 헤더(Headers): 메타데이터와 라우팅 정보를 담고 있는 이름-값 쌍의 컬렉션

메시지 생성은 MessageBuilder 클래스를 사용하여 수행할 수 있습니다:

```
Message<String> message = MessageBuilder.withPayload("주문 접수됨")
    .setHeader("orderId", "12345")
    .setHeader("timestamp", System.currentTimeMillis())
    .build();
```

#### 메시지 채널(Message Channel)

메시지 채널은 메시지 생산자와 소비자 사이의 파이프라인 역할을 합니다.

```
public interface MessageChannel {
    boolean send(Message<?> message);
    boolean send(Message<?> message, long timeout);
}
```

Spring Integration에서 제공하는 주요 채널 유형:

**DirectChannel**

가장 기본적인 채널 유형으로, 동기식 포인트-투-포인트 메시징을 지원합니다:

```
@Bean
public DirectChannel orderProcessingChannel() {
    return new DirectChannel();
}
```

특징:

* 메시지를 버퍼링하지 않고 즉시 핸들러에게 전달
* 발신자의 스레드에서 실행
* 단일 메시지 핸들러를 호출 (여러 핸들러가 있을 경우 로드 밸런싱)
* 트랜잭션 컨텍스트 전파 지원

**QueueChannel**

메시지를 내부 큐에 저장하는 채널로, 비동기 통신에 적합합니다:

```
@Bean
public QueueChannel orderBacklogChannel() {
    return new QueueChannel(100); // 용량 지정 가능
}
```

특징:

* 내부 메시지 큐를 사용하여 메시지 버퍼링
* 발신자와 수신자 간의 완전한 결합 해제
* 수신자는 명시적으로 메시지를 폴링해야 함
* 선입선출(FIFO) 방식의 메시지 전달

**PublishSubscribeChannel**

메시지를 여러 구독자에게 브로드캐스트하는 채널:

```
@Bean
public PublishSubscribeChannel orderEventChannel() {
    return new PublishSubscribeChannel();
}
```

특징:

* 등록된 모든 구독자에게 메시지 전달
* 기본적으로 발신자의 스레드에서 동기적으로 실행
* 이벤트 기반 시스템에서 상태 변경 알림에 유용

#### 메시지 엔드포인트(Message Endpoint)

메시지 엔드포인트는 메시지를 실제로 처리하는 컴포넌트입니다.

**서비스 액티베이터(Service Activator)**

메시지를 수신하여 비즈니스 로직을 실행합니다:

```
@ServiceActivator(inputChannel = "orderChannel")
public Order processOrder(Order order) {
    // 주문 처리 로직
    order.setStatus(OrderStatus.PROCESSING);
    return order;
}
```

**트랜스포머(Transformer)**

메시지 페이로드를 변환합니다:

```
@Transformer(inputChannel = "newOrderChannel", outputChannel = "enrichedOrderChannel")
public EnrichedOrder enrichOrder(Order order) {
    // 주문 데이터 보강 로직
    return new EnrichedOrder(order);
}
```

**필터(Filter)**

특정 조건에 따라 메시지를 필터링합니다:

```
@Filter(inputChannel = "allOrdersChannel", outputChannel = "highValueOrdersChannel")
public boolean isHighValueOrder(Order order) {
    return order.getTotalAmount().compareTo(new BigDecimal("10000")) > 0;
}
```

**라우터(Router)**

메시지를 특정 조건에 따라 다른 채널로 라우팅합니다:

```
@Router(inputChannel = "newOrdersChannel")
public String routeByOrderType(Order order) {
    switch (order.getType()) {
        case RETAIL: return "retailOrdersChannel";
        case WHOLESALE: return "wholesaleOrdersChannel";
        default: return "defaultOrdersChannel";
    }
}
```

**스플리터(Splitter)**

하나의 메시지를 여러 개로 분할합니다:

```
@Splitter(inputChannel = "bulkOrderChannel", outputChannel = "orderItemsChannel")
public List<OrderItem> splitOrderIntoItems(Order order) {
    return order.getItems();
}
```

**어그리게이터(Aggregator)**

여러 개의 관련 메시지를 하나로 결합합니다:

```
@Aggregator(inputChannel = "processedItemsChannel", outputChannel = "shipmentsChannel")
public Shipment createShipment(List<ProcessedItem> items) {
    return new Shipment(items);
}

@CorrelationStrategy
public Object correlateByOrderId(ProcessedItem item) {
    return item.getOrderId();
}

@ReleaseStrategy
public boolean isComplete(List<Message<?>> messages) {
    if (messages.isEmpty()) return false;
    
    Message<?> first = messages.get(0);
    Integer expectedItems = first.getHeaders().get("expectedItemCount", Integer.class);
    
    return expectedItems != null && messages.size() >= expectedItems;
}
```

#### 채널 어댑터와 게이트웨이

**채널 어댑터(Channel Adapter)**

채널 어댑터는 메시지 채널과 외부 시스템 사이의 단방향 통신을 제공합니다:

* 인바운드 채널 어댑터(Inbound Channel Adapter): 외부 시스템에서 메시지를 수신하여 채널로 전송
* 아웃바운드 채널 어댑터(Outbound Channel Adapter): 채널의 메시지를 외부 시스템으로 전송

예: 파일 시스템 어댑터

```
@Bean
public MessageSource<File> fileReadingMessageSource() {
    FileReadingMessageSource source = new FileReadingMessageSource();
    source.setDirectory(new File("/path/to/input"));
    source.setFilter(new SimplePatternFileListFilter("*.csv"));
    return source;
}

@Bean
public IntegrationFlow fileReadingFlow() {
    return IntegrationFlows
        .from(fileReadingMessageSource(), 
              c -> c.poller(Pollers.fixedDelay(5000)))
        .channel("fileContentChannel")
        .get();
}
```

**메시징 게이트웨이(Messaging Gateway)**

게이트웨이는 양방향 통신(요청-응답)을 위한 인터페이스를 제공합니다:

```
@MessagingGateway(defaultRequestChannel = "orderProcessingChannel", 
                 defaultReplyChannel = "orderConfirmationChannel")
public interface OrderService {
    OrderConfirmation processOrder(Order order);
    
    @Gateway(requestChannel = "priorityOrdersChannel")
    OrderConfirmation processHighPriorityOrder(Order order);
    
    @Async
    Future<OrderConfirmation> processOrderAsync(Order order);
}
```

#### Java DSL을 사용한 통합 플로우 구성

Spring Integration 5.0부터는 Java DSL을 사용하여 통합 플로우를 정의할 수 있습니다:

```
@Configuration
@EnableIntegration
public class OrderProcessingIntegrationConfig {

    @Bean
    public IntegrationFlow orderProcessingFlow() {
        return IntegrationFlows
            .from("inputChannel")
            .filter(this::isValidOrder)
            .<Order, OrderType>route(Order::getType,
                mapping -> mapping
                    .subFlowMapping(OrderType.RETAIL, sf -> sf
                        .channel("retailOrdersChannel")
                        .handle(orderService(), "processRetailOrder"))
                    .subFlowMapping(OrderType.WHOLESALE, sf -> sf
                        .channel("wholesaleOrdersChannel")
                        .handle(orderService(), "processWholesaleOrder")))
            .get();
    }
    
    private boolean isValidOrder(Order order) {
        return order != null && !order.getItems().isEmpty();
    }
}
```
