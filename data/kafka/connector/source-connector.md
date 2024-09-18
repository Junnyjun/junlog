# Source Connector

소스 커넥터 샘플 API입니다.

## Create Source Connector

<mark style="color:green;">`POST`</mark> `/connectors`

소스 커넥터를 등록 합니다.

**Headers**

| Name         | Value              |
| ------------ | ------------------ |
| Content-Type | `application/json` |

**Body**

```json
{
  "name" : "name",
  "config" :{
    "connector.class": "io.debezium.connector.mysql.MySqlConnector",
    "transforms.unwrap.delete.handling.mode": "drop",
    "database.history.kafka.topic": "schema-changes.employees",
    "transforms": "unwrap",
    "bootstrap.servers": "kafka00:9092,kafka01:9092,kafka02:9092",
    "include.schema.changes": "false",
    "topic.prefix": "insert-only",
    "schema.history.internal.kafka.topic": "history-never",
    "transforms.unwrap.drop.tombstones": "false",
    "transforms.unwrap.type": "io.debezium.transforms.ExtractNewRecordState",
    "database.allowPublicKeyRetrieval": "true",
    "database.user": "root",
    "database.server.id": "184054",
    "database.name": "employees",
    "database.history.kafka.bootstrap.servers": "kafka00:9092",
    "schema.history.internal.kafka.bootstrap.servers": "kafka00:9092,kafka01:9092,kafka02:9092",
    "transforms.unwrap.add.headers": "operation,source.ts_ms",
    "database.port": "3308",
    "database.useSSL": "false",
    "database.hostname": "host ip",
    "database.connectionTimeZone": "UTC",
    "database.password": 1234,
    "table.include.list": ".*",
    "snapshot.mode": "never"
  }
}
```

**Response**

{% tabs %}
{% tab title="200" %}
```json
{
  "name": "insertOnly-connect2",
  "config": {
    "connector.class": "io.debezium.connector.mysql.MySqlConnector",
    "transforms.unwrap.delete.handling.mode": "drop",
    "database.history.kafka.topic": "schema-changes.employees",
 ...
}
```
{% endtab %}
{% endtabs %}

