# 메시지 변환과 라우팅

기업 애플리케이션 통합 과정에서 가장 중요한 두 가지 작업은 메시지 변환(Transformation)과 라우팅(Routing)입니다. 다양한 시스템 간의 통합에서 데이터 형식을 일치시키고, 각 메시지를 적절한 대상으로 전달하는 것은 복잡한 통합 솔루션의 핵심입니다.

### 메시지 변환(Transformation)

메시지 변환은 수신된 메시지를 다른 형식이나 구조로 변환하는 과정입니다. 변환의 목적은 다양합니다:

* 외부 시스템의 데이터 형식을 내부 형식으로 변환(예: XML -> Java 객체)
* 메시지 페이로드 변환(예: 문자열 -> JSON 객체)
* 메시지 보강(Enrichment)을 통한 추가 정보 제공
* 메시지 필터링 또는 내용 수정

#### 기본 트랜스포머 구현

가장 간단한 형태의 트랜스포머는 메시지를 받아 새로운 페이로드로 변환하는 것입니다:

```java
@Bean
public IntegrationFlow simpleTransformFlow() {
    return IntegrationFlows
        .from("inputChannel")
        .transform(payload -> {
            String input = (String) payload;
            return input.toUpperCase();
        })
        .channel("outputChannel")
        .get();
}
```

#### 표준 트랜스포머

Spring Integration은 일반적인 변환 작업을 위한 다양한 표준 트랜스포머를 제공합니다:

**객체-JSON 변환**

```java
@Bean
public IntegrationFlow jsonTransformFlow() {
    return IntegrationFlows
        .from("orderChannel")
        .transform(new ObjectToJsonTransformer())
        .channel("jsonOrdersChannel")
        .get();
}

@Bean
public IntegrationFlow jsonToObjectFlow() {
    return IntegrationFlows
        .from("jsonOrdersChannel")
        .transform(new JsonToObjectTransformer(Order.class))
        .channel("processedOrdersChannel")
        .get();
}
```

**XML 변환**

```java
@Bean
public IntegrationFlow xmlTransformFlow() {
    return IntegrationFlows
        .from("orderChannel")
        .transform(new ObjectToXmlTransformer(marshaller))
        .channel("xmlOrdersChannel")
        .get();
}

@Bean
public IntegrationFlow xmlToObjectFlow() {
    return IntegrationFlows
        .from("xmlOrdersChannel")
        .transform(new XmlToObjectTransformer(unmarshaller))
        .channel("processedOrdersChannel")
        .get();
}
```

**파일 내용 변환**

```java
@Bean
public IntegrationFlow fileContentTransformFlow() {
    return IntegrationFlows
        .from("fileInputChannel")
        .transform(Files.toStringTransformer(StandardCharsets.UTF_8))
        .channel("fileContentChannel")
        .get();
}
```

#### 어노테이션 기반 트랜스포머

자바 메서드에 `@Transformer` 어노테이션을 사용하여 트랜스포머를 구현할 수 있습니다:

```java
@Component
public class OrderTransformer {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Transformer(inputChannel = "rawOrderChannel", outputChannel = "enrichedOrderChannel")
    public EnrichedOrder enrichOrder(Order order) {
        Customer customer = customerRepository.findById(order.getCustomerId())
            .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        return new EnrichedOrder(
            order.getId(),
            order.getItems(),
            order.getTotalAmount(),
            customer.getName(),
            customer.getEmail(),
            customer.getShippingAddress()
        );
    }
}
```

#### 메시지 헤더 조작

트랜스포머는 메시지 페이로드뿐만 아니라 헤더도 변경할 수 있습니다:

```java
@Bean
public IntegrationFlow headerEnrichmentFlow() {
    return IntegrationFlows
        .from("inputChannel")
        .enrichHeaders(h -> h
            .header("processedTimestamp", new Date())
            .headerExpression("uppercasePayload", "payload.toUpperCase()")
            .headerFunction("payloadLength", 
                message -> ((String) message.getPayload()).length()))
        .channel("outputChannel")
        .get();
}
```

#### 콘텐츠 보강기(Content Enricher)

메시지를 외부 시스템에서 가져온 추가 데이터로 보강할 수 있습니다:

```java
@Bean
public IntegrationFlow orderEnrichmentFlow() {
    return IntegrationFlows
        .from("newOrderChannel")
        .enrich(e -> e
            // 고객 정보 보강
            .requestChannel("customerLookupChannel")
            .requestPayloadExpression("payload.customerId")
            .replyChannel("customerResponseChannel")
            .propertyFunction("customer", (order, customer) -> {
                Order enrichedOrder = (Order) order;
                enrichedOrder.setCustomerDetails((Customer) customer);
                return enrichedOrder;
            })
            // 배송 정보 보강
            .requestChannel("shippingInfoChannel")
            .requestPayload(Message::getPayload)
            .replyChannel("shippingInfoResponseChannel")
            .propertyFunction("shippingInfo", (order, shippingInfo) -> {
                Order enrichedOrder = (Order) order;
                enrichedOrder.setShippingInfo((ShippingInfo) shippingInfo);
                return enrichedOrder;
            })
            // 헤더 추가
            .header("enriched", true)
            .headerExpression("priority", 
                "payload.totalAmount > 1000 ? 'HIGH' : 'NORMAL'"))
        .channel("enrichedOrderChannel")
        .get();
}
```

#### 분할기(Splitter)와 집계기(Aggregator)

복잡한 메시지를 여러 개의 작은 메시지로 분할하거나, 여러 메시지를 하나로 집계하는 변환 작업도 가능합니다:

```java
// 분할기: 주문을 개별 주문 항목으로 분할
@Bean
public IntegrationFlow orderSplitterFlow() {
    return IntegrationFlows
        .from("bulkOrderChannel")
        .split(Order.class, order -> {
            // 각 주문 항목에 대해 별도의 처리 메시지 생성
            return order.getItems().stream()
                .map(item -> new OrderItemProcessor(
                    order.getId(), 
                    item, 
                    order.getCustomerId()))
                .collect(Collectors.toList());
        })
        .channel("orderItemsChannel")
        .get();
}

// 집계기: 처리된 주문 항목을 원래 주문으로 다시 집계
@Bean
public IntegrationFlow orderAggregatorFlow() {
    return IntegrationFlows
        .from("processedItemsChannel")
        .aggregate(a -> a
            .correlationStrategy(message -> {
                // 주문 ID로 상관관계 설정
                OrderItemProcessor item = (OrderItemProcessor) message.getPayload();
                return item.getOrderId();
            })
            .releaseStrategy(group -> {
                // 모든 항목이 처리되었는지 확인
                OrderItemProcessor firstItem = (OrderItemProcessor) group.getMessages()
                    .get(0).getPayload();
                String orderId = firstItem.getOrderId();
                Order originalOrder = orderRepository.findById(orderId);
                int expectedItems = originalOrder.getItems().size();
                return group.size() >= expectedItems;
            })
            .outputProcessor(group -> {
                // 처리된 항목을 주문으로 변환
                List<Message<?>> messages = group.getMessages();
                List<OrderItemProcessor> processedItems = messages.stream()
                    .map(message -> (OrderItemProcessor) message.getPayload())
                    .collect(Collectors.toList());
                
                String orderId = processedItems.get(0).getOrderId();
                Order originalOrder = orderRepository.findById(orderId);
                
                // 처리 결과를 원래 주문에 반영
                processedItems.forEach(item -> 
                    originalOrder.updateItemStatus(item.getItemId(), item.getStatus()));
                
                return originalOrder;
            }))
        .channel("completedOrdersChannel")
        .get();
}
```

### 메시지 라우팅(Routing)

라우팅은 메시지를 특정 조건에 따라 적절한 채널로 전달하는 과정입니다. Spring Integration은 다양한 라우팅 전략을 지원합니다:

#### 1. 내용 기반 라우터(Content-based Router)

메시지 페이로드의 내용에 따라 라우팅합니다:

```java
@Bean
public IntegrationFlow orderRoutingFlow() {
    return IntegrationFlows
        .from("newOrdersChannel")
        .<Order, OrderType>route(Order::getType,
            mapping -> mapping
                .subFlowMapping(OrderType.RETAIL, sf -> sf.channel("retailOrdersChannel"))
                .subFlowMapping(OrderType.WHOLESALE, sf -> sf.channel("wholesaleOrdersChannel"))
                .subFlowMapping(OrderType.INTERNATIONAL, sf -> sf.channel("internationalOrdersChannel"))
                .defaultSubFlowMapping(sf -> sf.channel("defaultOrdersChannel")))
        .get();
}
```

좀 더 복잡한 라우팅 로직을 구현할 수도 있습니다:

```java
@Bean
public IntegrationFlow complexRoutingFlow() {
    return IntegrationFlows
        .from("ordersChannel")
        .<Order, String>route(order -> {
            if (order.getTotalAmount().compareTo(new BigDecimal("10000")) > 0) {
                return "highValue";
            } else if (order.getItems().size() > 10) {
                return "bulkOrder";
            } else if (order.getCustomerType() == CustomerType.VIP) {
                return "vipOrder";
            } else {
                return "standardOrder";
            }
        }, mapping -> mapping
            .subFlowMapping("highValue", sf -> sf
                .enrichHeaders(h -> h.header("priority", "HIGH"))
                .channel("highValueOrdersChannel"))
            .subFlowMapping("bulkOrder", sf -> sf
                .enrichHeaders(h -> h.header("batchProcessing", true))
                .channel("bulkOrdersChannel"))
            .subFlowMapping("vipOrder", sf -> sf
                .enrichHeaders(h -> h.header("expedite", true))
                .channel("vipOrdersChannel"))
            .defaultSubFlowMapping(sf -> sf
                .channel("standardOrdersChannel")))
        .get();
}
```

#### 2. 헤더 값 라우터(Header Value Router)

메시지 헤더의 값을 기준으로 라우팅합니다:

```java
@Bean
public IntegrationFlow headerValueRoutingFlow() {
    return IntegrationFlows
        .from("incomingChannel")
        .<Message<?>, Object>route(message -> message.getHeaders().get("routeKey"),
            mapping -> mapping
                .channelMapping("A", "channelA")
                .channelMapping("B", "channelB")
                .channelMapping("C", "channelC")
                .defaultOutputChannel("defaultChannel"))
        .get();
}
```

#### 3. 페이로드 타입 라우터(Payload Type Router)

메시지 페이로드의 타입에 따라 라우팅합니다:

```java
@Bean
public IntegrationFlow payloadTypeRoutingFlow() {
    return IntegrationFlows
        .from("inputChannel")
        .route(new PayloadTypeRouter()
            .channelMapping(String.class.getName(), "stringChannel")
            .channelMapping(Integer.class.getName(), "integerChannel")
            .channelMapping(Order.class.getName(), "orderChannel")
            .defaultOutputChannel("defaultChannel"))
        .get();
}

// 또는 DSL 사용하여 구현
@Bean
public IntegrationFlow payloadTypeRoutingDslFlow() {
    return IntegrationFlows
        .from("inputChannel")
        .route(Message.class, m -> m.getPayload().getClass().getName(),
            mapping -> mapping
                .channelMapping(String.class.getName(), "stringChannel")
                .channelMapping(Integer.class.getName(), "integerChannel")
                .channelMapping(Order.class.getName(), "orderChannel")
                .defaultOutputChannel("defaultChannel"))
        .get();
}
```

#### 4. 수신자 목록 라우터(Recipient List Router)

하나의 메시지를 여러 채널로 동시에 라우팅합니다:

```java
@Bean
public IntegrationFlow recipientListRoutingFlow() {
    return IntegrationFlows
        .from("inputChannel")
        .routeToRecipients(r -> r
            .recipient("loggingChannel")  // 모든 메시지를
            .recipient("auditChannel")    // 모든 채널로 전송
            .recipient("channelA", m -> m.getHeaders().get("toA") != null)  // 조건부 라우팅
            .recipient("channelB", m -> m.getHeaders().get("toB") != null)
            .recipient("channelC", m -> 
                ((String) m.getPayload()).contains("urgent")))
        .get();
}
```

#### 5. 어노테이션 기반 라우터

`@Router` 어노테이션을 사용하여 라우터를 구현할 수 있습니다:

```java
@Component
public class OrderRouter {
    
    @Router(inputChannel = "newOrdersChannel")
    public String routeOrder(Order order) {
        switch (order.getRegion()) {
            case "NORTH_AMERICA":
                return "naOrdersChannel";
            case "EUROPE":
                return "euOrdersChannel";
            case "ASIA_PACIFIC":
                return "apacOrdersChannel";
            default:
                return "internationalOrdersChannel";
        }
    }
    
    // 또는 여러 채널을 반환
    @Router(inputChannel = "criticalOrdersChannel")
    public List<String> routeCriticalOrder(Order order) {
        List<String> channels = new ArrayList<>();
        
        // 모든 중요 주문은 로깅 및 알림
        channels.add("loggingChannel");
        channels.add("notificationChannel");
        
        // 금액에 따라 추가 채널 선택
        if (order.getTotalAmount().compareTo(new BigDecimal("50000")) > 0) {
            channels.add("managerApprovalChannel");
            channels.add("financeNotificationChannel");
        }
        
        return channels;
    }
}
```

### 고급 메시지 변환 패턴

실제 기업 환경에서는 더 복잡한 변환 및 라우팅 패턴이 필요합니다. 몇 가지 고급 패턴을 살펴보겠습니다:

#### 1. 정규화기(Normalizer)

다양한 형식의 입력을 표준 형식으로 변환합니다:

```java
@Bean
public IntegrationFlow orderNormalizerFlow() {
    return IntegrationFlows
        .from("multiFormatOrdersChannel")
        .route(Message.class, message -> {
            Object payload = message.getPayload();
            if (payload instanceof String) {
                // 문자열 페이로드 검사
                String content = (String) payload;
                if (content.startsWith("<")) {
                    return "xmlOrdersChannel";
                } else if (content.startsWith("{")) {
                    return "jsonOrdersChannel";
                } else if (content.contains(",")) {
                    return "csvOrdersChannel";
                }
            } else if (payload instanceof byte[]) {
                return "binaryOrdersChannel";
            } else if (payload instanceof Order) {
                return "standardOrdersChannel";
            }
            return "unknownFormatChannel";
        })
        // XML 형식 처리
        .subFlowMapping("xmlOrdersChannel", sf -> sf
            .transform(new XmlToObjectTransformer(orderUnmarshaller))
            .channel("standardOrdersChannel"))
        // JSON 형식 처리
        .subFlowMapping("jsonOrdersChannel", sf -> sf
            .transform(new JsonToObjectTransformer(Order.class))
            .channel("standardOrdersChannel"))
        // CSV 형식 처리
        .subFlowMapping("csvOrdersChannel", sf -> sf
            .transform(this::convertCsvToOrder)
            .channel("standardOrdersChannel"))
        // 바이너리 형식 처리
        .subFlowMapping("binaryOrdersChannel", sf -> sf
            .transform(this::deserializeBinaryOrder)
            .channel("standardOrdersChannel"))
        // 알 수 없는 형식 처리
        .subFlowMapping("unknownFormatChannel", sf -> sf
            .handle(message -> {
                log.error("Unknown order format: {}", message.getPayload());
                throw new IllegalArgumentException("Unsupported order format");
            }))
        .get();
}

private Order convertCsvToOrder(String csv) {
    // CSV 문자열을 주문 객체로 변환하는 로직
    String[] fields = csv.split(",");
    Order order = new Order();
    order.setId(fields[0]);
    // ... 나머지 필드 처리
    return order;
}

private Order deserializeBinaryOrder(byte[] data) {
    // 바이너리 데이터를 주문 객체로 역직렬화하는 로직
    try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
         ObjectInputStream ois = new ObjectInputStream(bis)) {
        return (Order) ois.readObject();
    } catch (Exception e) {
        throw new RuntimeException("Failed to deserialize order", e);
    }
}
```

#### 2. 클레임 체크 트랜스포머(Claim Check Transformer)

큰 페이로드를 일시적으로 저장소에 보관하고 참조만 전달합니다:

```java
@Bean
public IntegrationFlow claimCheckFlow() {
    return IntegrationFlows
        .from("largePayloadChannel")
        // 체크인: 페이로드 저장 및 ID 반환
        .claimCheckIn(messageStore())
        // 저장된 메시지 ID만 가지고 경량 처리
        .handle((payload, headers) -> {
            String messageId = (String) payload;
            log.info("처리 중인 메시지 ID: {}", messageId);
            // 가벼운 처리 로직
            return messageId;
        })
        // 체크아웃: ID로 원본 페이로드 복원
        .claimCheckOut(messageStore())
        .channel("processedChannel")
        .get();
}

@Bean
public MessageStore messageStore() {
    return new SimpleMessageStore(100); // 최대 100개 메시지 저장
}
```

#### 3. 컨텐츠 필터(Content Filter)

메시지에서 불필요한 정보를 제거합니다:

```java
@Bean
public IntegrationFlow contentFilterFlow() {
    return IntegrationFlows
        .from("rawOrderChannel")
        .transform(order -> {
            Order filteredOrder = (Order) ((Order) order).clone();
            
            // 민감한 정보 제거
            filteredOrder.getCustomer().setCardDetails(null);
            filteredOrder.getCustomer().setSsn(null);
            
            // 파트너에게 불필요한 내부 필드 제거
            filteredOrder.setInternalNotes(null);
            filteredOrder.setProfitMargin(null);
            
            return filteredOrder;
        })
        .channel("partnerOrderChannel")
        .get();
}
```

### 통합 주문 처리 파이프라인

이제 복잡한 주문 처리 시스템의 변환 및 라우팅 로직을 구현해 보겠습니다.

```java
@Configuration
@EnableIntegration
public class OrderProcessingIntegrationConfig {

    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private InventoryService inventoryService;
    
    @Autowired
    private PricingService pricingService;
    
    // 1. 주문 입력 및 정규화
    @Bean
    public IntegrationFlow orderInputFlow() {
        return IntegrationFlows
            .from("rawOrdersChannel")
            // 형식에 따라 라우팅
            .<Object, String>route(payload -> {
                if (payload instanceof WebOrderDTO) {
                    return "webOrder";
                } else if (payload instanceof MobileOrderDTO) {
                    return "mobileOrder";
                } else if (payload instanceof String) {
                    String content = (String) payload;
                    if (content.startsWith("<")) return "xmlOrder";
                    if (content.startsWith("{")) return "jsonOrder";
                }
                return "unknownOrder";
            }, mapping -> mapping
                .subFlowMapping("webOrder", sf -> sf
                    .transform(this::convertWebOrder))
                .subFlowMapping("mobileOrder", sf -> sf
                    .transform(this::convertMobileOrder))
                .subFlowMapping("xmlOrder", sf -> sf
                    .transform(new XmlToObjectTransformer(orderUnmarshaller)))
                .subFlowMapping("jsonOrder", sf -> sf
                    .transform(new JsonToObjectTransformer(PartnerOrderDTO.class))
                    .transform(this::convertPartnerOrder))
                .defaultSubFlowMapping(sf -> sf
                    .handle(message -> {
                        throw new IllegalArgumentException("Unknown order format");
                    })))
            .channel("normalizedOrderChannel")
            .get();
    }
    
    // 2. 주문 보강 및 검증
    @Bean
    public IntegrationFlow orderEnrichmentFlow() {
        return IntegrationFlows
            .from("normalizedOrderChannel")
            // 고객 정보 보강
            .enrich(e -> e
                .requestChannel("customerLookupChannel")
                .requestPayloadExpression("payload.customerId")
                .replyChannel("customerResponseChannel")
                .propertyFunction("customer", (order, customer) -> {
                    Order enrichedOrder = (Order) order;
                    enrichedOrder.setCustomer((Customer) customer);
                    return enrichedOrder;
                }))
            // 제품 정보 보강
            .enrich(e -> e
                .requestChannel("productLookupChannel")
                .requestPayload(message -> {
                    Order order = (Order) message.getPayload();
                    return order.getItems().stream()
                        .map(OrderItem::getProductId)
                        .collect(Collectors.toList());
                })
                .replyChannel("productResponseChannel")
                .propertyFunction("products", (order, products) -> {
                    Order enrichedOrder = (Order) order;
                    Map<String, Product> productMap = ((List<Product>) products).stream()
                        .collect(Collectors.toMap(Product::getId, p -> p));
                    
                    // 제품 정보로 주문 항목 보강
                    enrichedOrder.getItems().forEach(item -> {
                        Product product = productMap.get(item.getProductId());
                        item.setProductName(product.getName());
                        item.setUnitPrice(product.getPrice());
                    });
                    
                    return enrichedOrder;
                }))
            // 주문 유효성 검사
            .filter(this::validateOrder, f -> f.discardChannel("invalidOrdersChannel"))
            // 주문 가격 계산
            .transform(order -> {
                Order validatedOrder = (Order) order;
                // 상품 가격 합계 계산
                BigDecimal subtotal = validatedOrder.getItems().stream()
                    .map(item -> item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                // 세금 및 배송비 추가
                BigDecimal tax = pricingService.calculateTax(subtotal, validatedOrder.getShippingAddress());
                BigDecimal shippingCost = pricingService.calculateShippingCost(
                    validatedOrder.getItems(), validatedOrder.getShippingAddress());
                
                validatedOrder.setSubtotal(subtotal);
                validatedOrder.setTax(tax);
                validatedOrder.setShippingCost(shippingCost);
                validatedOrder.setTotalAmount(subtotal.add(tax).add(shippingCost));
                
                return validatedOrder;
            })
            .channel("validatedOrderChannel")
            .get();
    }
    
    // 3. 주문 라우팅 및 처리
    @Bean
    public IntegrationFlow orderRoutingFlow() {
        return IntegrationFlows
            .from("validatedOrderChannel")
            .<Order, String>route(order -> {
                // 재고 확인
                boolean allItemsInStock = order.getItems().stream()
                    .allMatch(item -> inventoryService.checkAvailability(
                        item.getProductId(), item.getQuantity()));
                
                if (!allItemsInStock) {
                    return "backorderFlow";
                }
                
                // 주문 금액에 따른 처리
                if (order.getTotalAmount().compareTo(new BigDecimal("10000")) > 0) {
                    return "highValueFlow";
                }
                
                // 고객 유형에 따른 처리
                if (order.getCustomer().getType() == CustomerType.VIP) {
                    return "vipFlow";
                }
                
                // 국제 주문 처리
                if (!order.getShippingAddress().getCountry().equals("KR")) {
                    return "internationalFlow";
                }
                
                // 표준 주문 처리
                return "standardFlow";
            }, mapping -> mapping
                .subFlowMapping("backorderFlow", sf -> sf
                    .transform(this::prepareBackorder)
                    .channel("backorderChannel"))
                .subFlowMapping("highValueFlow", sf -> sf
                    .enrichHeaders(h -> h.header("requiresApproval", true))
                    .channel("approvalRequiredChannel"))
                .subFlowMapping("vipFlow", sf -> sf
                    .enrichHeaders(h -> h.header("expediteDelivery", true))
                    .channel("vipOrderChannel"))
                .subFlowMapping("internationalFlow", sf -> sf
                    .transform(this::prepareInternationalOrder)
                    .channel("internationalOrderChannel"))
                .defaultSubFlowMapping(sf -> sf
                    .channel("standardOrderChannel")))
            .get();
    }
    
    // 4. 고객 조회 서브플로우
    @Bean
    public IntegrationFlow customerLookupFlow() {
        return IntegrationFlows
            .from("customerLookupChannel")
            .handle(message -> {
                String customerId = (String) message.getPayload();
                return customerRepository.findById(customerId)
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));
            })
            .channel("customerResponseChannel")
            .get();
    }
    
    // 5. 제품 조회 서브플로우
    @Bean
    public IntegrationFlow productLookupFlow() {
        return IntegrationFlows
            .from("productLookupChannel")
            .handle(message -> {
                @SuppressWarnings("unchecked")
                List<String> productIds = (List<String>) message.getPayload();
                return productRepository.findAllById(productIds);
            })
            .channel("productResponseChannel")
            .get();
    }
    
    // 6. 유효하지 않은 주문 처리 플로우
    @Bean
    public IntegrationFlow invalidOrderFlow() {
        return IntegrationFlows
            .from("invalidOrdersChannel")
            .transform(order -> {
                Order invalidOrder = (Order) order;
                // 오류 세부 정보 수집
                List<String> errors = validateOrderWithDetails(invalidOrder);
                
                // 오류 보고서 생성
                ValidationReport report = new ValidationReport();
                report.setOrderId(invalidOrder.getId());
                report.setCustomerId(invalidOrder.getCustomerId());
                report.setErrors(errors);
                report.setTimestamp(new Date());
                
                return report;
            })
            .routeToRecipients(r -> r
                // 오류 로깅
                .recipient("validationErrorLogChannel")
                // 고객 알림
                .recipient("customerNotificationChannel", 
                    m -> ((ValidationReport) m.getPayload()).getErrors().size() <= 3)
                // 관리자 검토가 필요한 심각한 오류
                .recipient("manualReviewChannel", 
                    m -> ((ValidationReport) m.getPayload()).getErrors().size() > 3))
            .get();
    }
```
