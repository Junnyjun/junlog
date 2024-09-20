# Topic

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

## Consumer

토픽으로 전송된 데이터는 아래와 같은 명령어로 확인할 수 있다

컨슈머 그룹은 1개 이상의 컨슈머로 이루어져있다. 이 그룹을 통해 메시지는 레코드의 커서까지 커밋하고 이를 `__consumer_offsets`로 저장한다

```bash
consumer.sh \
 --bootstrap-server localhost:9092 \ # kafka 서버 지정
 --topic test \ # 읽어올 topic 지정
 --group test-group \ # consumer group 지정
# --property print.key=true \ # key 출력 여부
# --property key.separator=, \ # key와 value 사이의 구분자
  --from-beginning # 처음부터 읽어오기
```
