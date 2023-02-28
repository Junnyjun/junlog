# Minio

### MinIO란?

MinIO는 객체 스토리지 서버입니다. MinIO는 Amazon S3 API를 사용하여 객체를 저장하고 검색할 수 있습니다. MinIO는 가볍고 빠르며 확장성이 좋습니다.

### 특징

* **객체 스토리지**: MinIO는 객체 스토리지를 제공합니다. 이를 통해 대용량의 데이터를 쉽게 저장하고 검색할 수 있습니다.
* **Amazon S3 호환**: MinIO는 Amazon S3 API를 사용합니다. 따라서 Amazon S3와 호환되는 애플리케이션에서 사용할 수 있습니다.
* **가볍고 빠름**: MinIO는 가볍고 빠릅니다. 이를 통해 애플리케이션을 더욱 빠르게 실행할 수 있습니다.
* **확장성**: MinIO는 확장성이 좋습니다. 이를 통해 대규모 데이터를 처리할 수 있습니다.

### 설치

MinIO는 쉽게 설치하고 사용할 수 있습니다. MinIO를 설치하려면 다음 단계를 수행하면 됩니다.

```bash
# 설치
> wget https://dl.min.io/server/minio/release/linux-arm64/minio
> chmod +x minio
> MINIO_ROOT_USER=admin MINIO_ROOT_PASSWORD=password ./minio server /mnt/data --console-address ":9001"
```

MinIO를 사용하면 객체 스토리지를 쉽게 구축하고 관리할 수 있습니다. \
애플리케이션을 더욱 빠르게 실행할 수 있습니다.

http://{IP}:9000 으로 들어가 관리할 수 있습니다



### Use in code?

`implementation  ('io.minio:minio:8.4.5')`

{% embed url="https://gist.github.com/Junnyjun/29db247b8ad818317711051d1b9bc639" %}
