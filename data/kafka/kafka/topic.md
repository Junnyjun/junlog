# Topic

**토픽**은 Kafka에서 메시지들이 저장되는 **논리적 카테고리**입니다. Kafka에서 프로듀서는 메시지를 특정 토픽으로 보내고, 컨슈머는 그 토픽을 구독하여 데이터를 소비합니다.

* **데이터 카테고리**: 토픽은 데이터를 분류하는 단위로 사용됩니다.&#x20;
* **다수의 컨슈머**: 하나의 토픽은 여러 컨슈머가 구독할 수 있습니다. 여러 컨슈머가 동시에 같은 토픽의 데이터를 읽어 갈 수 있어 확장성이 뛰어납니다.
* **영속성**: Kafka는 데이터를 토픽에 저장하며, 설정된 보존 기간 동안 데이터를 보관합니다. 기본적으로 데이터는 삭제되지 않으며, 설정에 따라 데이터를 일정 기간 동안 유지하거나 특정 크기 이상이 되면 삭제합니다.

토픽의 이름은 `<ENV>.<TEAM>.<SERVICE>.<MESSAGE TYPE | EVENT NAME>` 으로 짓는 방식을 권장한다.



데이터 베이스에서 테이블과 유사하며, 데이터를 구분하는 가장 기본적인 단위이다

`파티션` 토픽에는 파티션이 존재하는데, 최소 1개 이상을 가진다, 파티션을 통해 한번에 처리할 수 있는 데이터양이 늘어나고, 토픽 내부에서도 파티션을 통해 데이터를 분류할 수 있다.

<pre class="language-bash"><code class="lang-bash"><strong>> topic.sh \
</strong><strong> --create \
</strong> --bootstrap-server localhost:9092 \
  --replication-factor 1 \ # 복제본의 수를 지정
  --partitions 1 \ # 파티션의 수를 지정
  --config retention.ms=1000 \ # 메시지 보관 시간을 지정
  --topic test
</code></pre>

토픽에 넣는 데이터는 레코드라고 부르며, 키 + 값 으로 이루어져 있다

<img src="../../../.gitbook/assets/file.excalidraw (54).svg" alt="" class="gitbook-drawing">

메시지 키가 없을 경우, 라운드 로빈으로 전송한다.\
메시지 키가 존재하는 경우, 키의 해시값을 가진 파티션중 한 개에 할당 된다.
