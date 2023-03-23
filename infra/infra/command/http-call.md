# http call

## curl, wget 사용법

### curl

`curl`은 URL을 통해 데이터를 전송하거나 받는 명령줄 도구입니다.

#### GET 요청

```bash
# curl {URL}
$ curl https://www.google.com

# curl -X POST {URL} -d '{data}'
$ curl -X POST <https://example.com/login> -d 'username=foo&password=bar'

# curl -O {URL} > download
$ curl -O https://example.com/image.jpg
```

### wget

`wget`은 파일을 다운로드하는 명령줄 도구입니다.

#### 파일 다운로드

```bash
# wget {URL}
$ wget https://example.com/file.zip

# wget -O {새로운 파일 이름} {URL}
$ wget -O new_file.zip https://example.com/file.zip

# wget -b {URL} > background
$ wget -b https://example.com/large_file.zip
```
