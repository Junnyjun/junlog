# 개요

Spring Integration은 엔터프라이즈 애플리케이션 통합(Enterprise Application Integration, EAI) 패턴을 Spring 프레임워크에 구현한 강력한 확장 모듈입니다. Gregor Hohpe와 Bobby Woolf의 유명한 책 "Enterprise Integration Patterns"에서 설명한 패턴을 기반으로 하며, 이러한 패턴을 Java 개발자들이 쉽게 적용할 수 있도록 합니다.

Spring Integration의 주요 목표는 다음과 같습니다:

* 시스템 간의 느슨한 결합(loose coupling) 제공
* 비동기 메시징 지원
* 다양한 전송 프로토콜 및 엔드포인트 연결
* 메시지 변환 및 라우팅 단순화
* 선언적 어댑터를 통한 외부 시스템 통합

무엇보다 Spring Integration은 POJO(Plain Old Java Objects) 기반 개발을 지원하여 복잡한 통합 시나리오에서도 테스트 용이성과 유지보수성을 높입니다.

### 메시지 기반 아키텍처의 핵심 개념

Spring Integration은 메시지 기반 아키텍처를 채택하고 있습니다. 이 아키텍처는 다음과 같은 핵심 개념을 중심으로 구성됩니다:

#### 메시지(Message)

메시지는 Spring Integration의 기본 데이터 전송 단위입니다. 메시지 인터페이스는 다음과 같이 정의됩니다:

```java
public interface Message<T> {
    T getPayload();
    MessageHeaders getHeaders();
}
```

메시지는 두 가지 주요 부분으로 구성됩니다:

1. **페이로드(Payload)**: 실제 전송되는 데이터 (Java의 모든 유형이 될 수 있음)
2. **헤더(Headers)**: 메타데이터와 라우팅 정보를 담고 있는 이름-값 쌍의 컬렉션

메시지 생성은 클래스를 사용하여 수행할 수 있습니다: `MessageBuilder`

```java
// 문자열 페이로드로 메시지 생성
Message<String> message = MessageBuilder.withPayload("주문 접수됨")
    .setHeader("orderId", "12345")
    .setHeader("timestamp", System.currentTimeMillis())
    .setHeader("priority", 1)
    .build();

// 객체 페이로드로 메시지 생성
Order order = new Order("12345", customer, items);
Message<Order> orderMessage = MessageBuilder.withPayload(order)
    .setHeader("channel", "newOrders")
    .setHeader("region", "APAC")
    .build();
```

#### 메시지 채널(Message Channel)

메시지 채널은 메시지 생산자와 소비자 사이의 파이프라인 역할을 합니다. 채널은 메시지 생산자와 소비자를 분리하여 느슨한 결합을 제공합니다.

인터페이스의 정의: `MessageChannel`

```java
public interface MessageChannel {
    boolean send(Message<?> message);
    boolean send(Message<?> message, long timeout);
}
```

Spring Integration은 다양한 메시지 채널 구현을 제공합니다:

**DirectChannel**

가장 기본적인 채널 유형으로, 동기식 포인트-투-포인트 메시징을 지원합니다:

```java
@Bean
public DirectChannel orderProcessingChannel() {
    DirectChannel channel = new DirectChannel();
    
    // 채널 통계 활성화
    channel.setComponentName("orderProcessingChannel");
    channel.setLoggingEnabled(true);
    
    return channel;
}
```

DirectChannel은 다음과 같은 특징을 가집니다:

* 메시지를 버퍼링하지 않고 즉시 핸들러에게 전달
* 발신자의 스레드에서 실행 (별도 스레드 생성 없음)
* 단일 메시지 핸들러를 호출 (여러 핸들러가 있을 경우 로드 밸런싱)
* 트랜잭션 컨텍스트 전파 지원

**QueueChannel**

메시지를 내부 큐에 저장하는 채널로, 비동기 통신에 적합합니다:

```java
@Bean
public QueueChannel orderBacklogChannel() {
    // 용량이 100인 큐 생성 (옵션)
    return new QueueChannel(100);
}
```

QueueChannel의 특징:

* 내부 메시지 큐를 사용하여 메시지 버퍼링
* 발신자와 수신자 간의 완전한 결합 해제
* 수신자는 명시적으로 메시지를 폴링해야 함
* 선입선출(FIFO) 방식의 메시지 전달

**PublishSubscribeChannel**

메시지를 여러 구독자에게 브로드캐스트하는 채널:

```java
@Bean
public PublishSubscribeChannel orderEventChannel() {
    PublishSubscribeChannel channel = new PublishSubscribeChannel();
    
    // 비동기 동작을 위한 TaskExecutor 구성 (선택사항)
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(10);
    executor.initialize();
    channel.setTaskExecutor(executor);
    
    return channel;
}
```

PublishSubscribeChannel의 특징:

* 등록된 모든 구독자에게 메시지 전달
* TaskExecutor를 구성하지 않으면 발신자의 스레드에서 동기적으로 실행
* 이벤트 기반 시스템에서 상태 변경 알림에 유용

**PriorityChannel**

메시지 우선순위에 따라 처리하는 채널:

```java
@Bean
public PriorityChannel highPriorityOrders() {
    return new PriorityChannel(100, new MessagePriorityComparator());
}

public class MessagePriorityComparator implements Comparator<Message<?>> {
    @Override
    public int compare(Message<?> message1, Message<?> message2) {
        // 헤더의 'priority' 값을 기준으로 비교 (낮은 숫자가 높은 우선순위)
        Integer priority1 = message1.getHeaders().get("priority", Integer.class);
        Integer priority2 = message2.getHeaders().get("priority", Integer.class);
        
        if (priority1 == null) priority1 = 10; // 기본값
        if (priority2 == null) priority2 = 10;
        
        return priority1.compareTo(priority2);
    }
}
```

#### 메시지 엔드포인트(Message Endpoint)

메시지 엔드포인트는 메시지를 실제로 처리하는 컴포넌트입니다. Spring Integration은 다양한 유형의 엔드포인트를 제공합니다:

**서비스 액티베이터(Service Activator)**

메시지를 수신하여 비즈니스 로직을 실행합니다:

```java
@Bean
public ServiceActivatingHandler orderProcessorActivator() {
    return new ServiceActivatingHandler(orderProcessor(), "processOrder");
}

// 또는 어노테이션 기반 설정
@ServiceActivator(inputChannel = "orderChannel")
public Order processOrder(Order order) {
    // 주문 처리 로직
    order.setStatus(OrderStatus.PROCESSING);
    orderRepository.save(order);
    return order;
}
```

**트랜스포머(Transformer)**

메시지 페이로드를 변환합니다:

```java
@Bean
public TransformerHandler orderEnricher() {
    return new TransformerHandler(new OrderEnricher());
}

// 또는 어노테이션 기반 설정
@Transformer(inputChannel = "newOrderChannel", outputChannel = "enrichedOrderChannel")
public EnrichedOrder enrichOrder(Order order) {
    // 주문 데이터 보강
    Customer customer = customerRepository.findById(order.getCustomerId());
    List<Product> products = productRepository.findAllById(
        order.getItems().stream()
            .map(OrderItem::getProductId)
            .collect(Collectors.toList())
    );
    
    return new EnrichedOrder(order, customer, products);
}
```

**필터(Filter)**

특정 조건에 따라 메시지를 필터링합니다:

```java
@Bean
public FilteringMessageHandler highValueOrderFilter() {
    return new FilteringMessageHandler(message -> {
        Order order = (Order) message.getPayload();
        return order.getTotalAmount().compareTo(new BigDecimal("10000")) > 0;
    });
}

// 또는 어노테이션 기반 설정
@Filter(inputChannel = "allOrdersChannel", outputChannel = "highValueOrdersChannel")
public boolean isHighValueOrder(Order order) {
    return order.getTotalAmount().compareTo(new BigDecimal("10000")) > 0;
}
```

**라우터(Router)**

메시지를 특정 조건에 따라 다른 채널로 라우팅합니다:

```java
@Bean
public RouterMessageHandler orderTypeRouter() {
    return new RouterMessageHandler(message -> {
        Order order = (Order) message.getPayload();
        if (order.getType() == OrderType.RETAIL) {
            return "retailOrdersChannel";
        } else if (order.getType() == OrderType.WHOLESALE) {
            return "wholesaleOrdersChannel";
        } else {
            return "defaultOrdersChannel";
        }
    });
}

// 또는 어노테이션 기반 설정
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

```java
@Bean
public SplitterMessageHandler orderItemSplitter() {
    return new SplitterMessageHandler(message -> {
        Order order = (Order) message.getPayload();
        
        // 주문의 각 항목에 대해 별도의 메시지 생성
        return order.getItems().stream()
            .map(item -> MessageBuilder
                    .withPayload(item)
                    .copyHeadersIfAbsent(message.getHeaders())
                    .setHeader("orderId", order.getId())
                    .build())
            .collect(Collectors.toList());
    });
}

// 또는 어노테이션 기반 설정
@Splitter(inputChannel = "bulkOrderChannel", outputChannel = "orderItemsChannel")
public List<OrderItem> splitOrderIntoItems(Order order) {
    return order.getItems();
}
```

**어그리게이터(Aggregator)**

여러 개의 관련 메시지를 하나로 결합합니다:

```java
@Bean
public AggregatingMessageHandler shipmentAggregator() {
    return new AggregatingMessageHandler(
        // 처리된 항목을 배송으로 집계
        new DefaultAggregatingMessageGroupProcessor(),
        // 상관관계 전략: 동일한 주문 ID를 가진 메시지를 그룹화
        message -> message.getHeaders().get("orderId")
    );
}

// 또는 어노테이션 기반 설정
@Aggregator(inputChannel = "processedItemsChannel", outputChannel = "shipmentsChannel")
public Shipment createShipment(List<ProcessedItem> items) {
    // 처리된 항목을 배송 객체로 집계
    return new Shipment(items);
}

// 집계할 메시지를 식별하는 상관관계 전략
@CorrelationStrategy
public Object correlateByOrderId(ProcessedItem item) {
    return item.getOrderId();
}

// 그룹이 완료되었는지 결정하는 릴리스 전략
@ReleaseStrategy
public boolean isComplete(List<Message<?>> messages) {
    // 예: 모든 주문 항목이 처리되었는지 확인
    if (messages.isEmpty()) return false;
    
    // 첫 번째 메시지에서 예상되는 항목 수를 확인
    Message<?> first = messages.get(0);
    Integer expectedItems = first.getHeaders().get("expectedItemCount", Integer.class);
    
    return expectedItems != null && messages.size() >= expectedItems;
}
```

### 채널 어댑터와 게이트웨이

Spring Integration은 다양한 외부 시스템과의 통합을 위한 채널 어댑터와 게이트웨이를 제공합니다:

#### 채널 어댑터(Channel Adapter)

채널 어댑터는 메시지 채널과 외부 시스템 사이의 단방향 통신을 제공합니다:

* **인바운드 채널 어댑터(Inbound Channel Adapter)**: 외부 시스템에서 메시지를 수신하여 채널로 전송
* **아웃바운드 채널 어댑터(Outbound Channel Adapter)**: 채널의 메시지를 외부 시스템으로 전송

파일 시스템 어댑터 예제:

```java
// 파일 인바운드 채널 어댑터 (파일 시스템 -> 채널)
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
        .transform(Files.toStringTransformer())
        .channel("fileContentChannel")
        .get();
}

// 파일 아웃바운드 채널 어댑터 (채널 -> 파일 시스템)
@Bean
public MessageHandler fileWritingMessageHandler() {
    FileWritingMessageHandler handler = new FileWritingMessageHandler(
        new File("/path/to/output"));
    handler.setFileNameGenerator(message -> 
        "order_" + message.getHeaders().get("orderId") + ".json");
    handler.setExpectReply(false);
    return handler;
}

@Bean
public IntegrationFlow fileWritingFlow() {
    return IntegrationFlows
        .from("processedOrdersChannel")
        .handle(fileWritingMessageHandler())
        .get();
}
```

#### 메시징 게이트웨이(Messaging Gateway)

게이트웨이는 양방향 통신(요청-응답)을 위한 인터페이스를 제공합니다:

```java
// 게이트웨이 인터페이스 정의
@MessagingGateway(defaultRequestChannel = "orderProcessingChannel", 
                 defaultReplyChannel = "orderConfirmationChannel")
public interface OrderService {
    @Gateway(requestChannel = "priorityOrdersChannel", 
             replyTimeout = 5000)
    OrderConfirmation processHighPriorityOrder(Order order);
    
    OrderConfirmation processOrder(Order order);
    
    @Async
    @Gateway(requestChannel = "asyncOrdersChannel")
    Future<OrderConfirmation> processOrderAsync(Order order);
}

// 사용 예시
@Service
public class OrderFacade {
    @Autowired
    private OrderService orderService;
    
    public void submitOrder(Order order) {
        if (order.isPriority()) {
            OrderConfirmation confirmation = orderService.processHighPriorityOrder(order);
            // 처리 로직...
        } else {
            try {
                Future<OrderConfirmation> future = orderService.processOrderAsync(order);
                // 필요한 경우 future로부터 결과 기다림
                // OrderConfirmation confirmation = future.get(30, TimeUnit.SECONDS);
            } catch (Exception e) {
                // 예외 처리
            }
        }
    }
}
```

### Java DSL을 사용한 통합 플로우 구성

Spring Integration 5.0부터는 Java DSL을 사용하여 좀 더 유연하고 가독성 높은 통합 플로우를 정의할 수 있습니다:

```java
@Configuration
@EnableIntegration
public class OrderProcessingIntegrationConfig {

    @Bean
    public IntegrationFlow orderProcessingFlow() {
        return IntegrationFlows
            // 1. HTTP 요청에서 주문 수신
            .from(Http.inboundGateway("/orders")
                  .requestMapping(m -> m.methods(HttpMethod.POST))
                  .requestPayloadType(Order.class))
            // 2. 주문 유효성 검사
            .filter(this::isValidOrder,
                   e -> e.discardChannel("invalidOrdersChannel"))
            // 3. 주문 유형에 따라 다른 처리 경로로 라우팅
            .<Order, OrderType>route(Order::getType,
                mapping -> mapping
                    .subFlowMapping(OrderType.RETAIL, sf -> sf
                        // 3.1 소매 주문 처리 서브플로우
                        .transform(retailOrderTransformer())
                        .channel("retailOrdersChannel")
                        .handle(retailOrderService(), "processOrder"))
                    .subFlowMapping(OrderType.WHOLESALE, sf -> sf
                        // 3.2 도매 주문 처리 서브플로우
                        .enrichHeaders(h -> h.header("discount", "10%"))
                        .channel(c -> c.queue("wholesaleOrdersQueue"))
                        .handle(wholesaleOrderService(), "processOrder"))
                    .defaultSubFlowMapping(sf -> sf
                        // 3.3 기타 주문 유형 처리 서브플로우
                        .channel("defaultOrdersChannel")
                        .handle(orderService(), "processGenericOrder")))
            // 4. 주문 처리 결과 변환
            .transform(orderConfirmationTransformer())
            // 5. 알림 전송 (와이어탭 사용하여 메인 플로우에 영향 없이 처리)
            .wireTap(flow -> flow
                    .channel(c -> c.executor(notificationExecutor()))
                    .handle(m -> notificationService().sendConfirmation(m.getPayload())))
            // 6. 응답 반환
            .get();
    }
    
    // 주문 유효성 검사 로직
    private boolean isValidOrder(Order order) {
        return order != null && 
               order.getCustomerId() != null && 
               !order.getItems().isEmpty() &&
               order.getTotalAmount().compareTo(BigDecimal.ZERO) > 0;
    }
    
    // 유효하지 않은 주문 처리 플로우
    @Bean
    public IntegrationFlow invalidOrdersFlow() {
        return IntegrationFlows.from("invalidOrdersChannel")
                .handle(message -> {
                    Order invalidOrder = (Order) message.getPayload();
                    log.error("Invalid order received: {}", invalidOrder);
                    // 오류 처리 및 알림
                })
                .get();
    }
    
    // 나머지 필요한 빈 정의...
}
```

### 실제 프로젝트 응용 사례

쇼핑몰 주문 시스템에서 Spring Integration을 활용한 실제 응용 사례를 살펴보겠습니다:

#### 다양한 주문 입력 채널 통합

* 웹사이트를 통한 주문
* 모바일 앱을 통한 주문
* 외부 마켓플레이스를 통한 주문
* 파트너 API를 통한 주문

#### 주문 처리 워크플로우

* 재고 확인 및 업데이트
* 결제 처리
* 배송 준비 및 추적
* 고객 알림

```java
@Configuration
@EnableIntegration
public class ECommerceIntegrationConfig {

    // 다양한 주문 소스로부터 통합 (인바운드 엔드포인트)
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
    
    @Bean
    public IntegrationFlow mobileOrdersFlow() {
        return IntegrationFlows
            .from(Http.inboundGateway("/api/mobile/orders")
                  .requestMapping(m -> m.methods(HttpMethod.POST))
                  .requestPayloadType(MobileOrderDTO.class))
            .transform(mobileOrderTransformer())
            .channel("newOrdersChannel")
            .get();
    }
    
    @Bean
    public IntegrationFlow partnerOrdersFlow() {
        return IntegrationFlows
            .from(Sftp.inboundAdapter(sftpSessionFactory())
                  .remoteDirectory("/orders")
                  .filter(new SftpSimplePatternFileListFilter("*.xml"))
                  .localDirectory(new File("/tmp/partner-orders"))
                  .autoCreateLocalDirectory(true))
            .transform(Transformers.fromXml(Order.class))
            .channel("newOrdersChannel")
            .get();
    }
    
    // 공통 주문 처리 통합 플로우 
    @Bean
    public IntegrationFlow orderProcessingFlow() {
        return IntegrationFlows
            .from("newOrdersChannel")
            // 주문 검증
            .filter(this::validateOrder, f -> f.discardChannel("invalidOrdersChannel"))
            // 중복 주문 확인
            .handle(idempotentReceiver())
            // 트랜잭션 시작
            .gateway(transactionalFlow())
            // 주문 완료 후 처리
            .split(Order.class, this::createFulfillmentTasks)
            .channel(c -> c.executor(fulfillmentExecutor()))
            .get();
    }
    
    // 트랜잭션 처리 서브플로우
    @Bean
    public IntegrationFlow transactionalFlow() {
        return flow -> flow
            // 트랜잭션 경계 설정
            .transactional(transactionManager())
            // 재고 확인
            .handle(inventoryService(), "checkAndReserveStock")
            // 결제 처리
            .handle(paymentService(), "processPayment")
            // 주문 저장
            .handle(orderRepository(), "save")
            // 이벤트 발행
            .publishSubscribeChannel(c -> c
                // 로그 기록
                .subscribe(sf -> sf.handle(auditService(), "logOrderCreated"))
                // 분석 데이터 전송
                .subscribe(sf -> sf.handle(analyticsService(), "trackOrder")));
    }
    
    // 오류 처리 및 복구 전략
    @Bean
    public IntegrationFlow errorHandlingFlow() {
        return IntegrationFlows
            .from(IntegrationContextUtils.ERROR_CHANNEL_BEAN_NAME)
            .handle(message -> {
                MessagingException exception = (MessagingException) message.getPayload();
                Message<?> failedMessage = exception.getFailedMessage();
                
                log.error("Error processing message: {}", failedMessage, exception);
                
                // 오류의 특성에 따라 다른 복구 전략 적용
                if (isRecoverable(exception)) {
                    // 일시적인 오류: 재시도 큐로 전송
                    retryChannel().send(failedMessage);
                } else {
                    // 영구적인 오류: 인적 개입이 필요한 큐로 전송
                    manualResolutionChannel().send(
                        MessageBuilder.fromMessage(failedMessage)
                            .setHeader("error", exception.getMessage())
                            .build());
                }
            })
            .get();
    }
    
    // 필요한 빈 메서드 정의...
}
```
