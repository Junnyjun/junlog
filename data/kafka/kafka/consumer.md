# Consumer

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
