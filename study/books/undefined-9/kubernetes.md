# 도커 복습과 Kubernetes

**도커 복습**

도커는 컨테이너를 실행하기 위한 실행 환경(컨테이너 런타임) 및 툴킷\
최근에는 도커 이미지가 아닌 OCI 표준의 컨테이너 이미지를 사용하는 방법이나 다른 도구에서 도커 이미지를 빌드하는 방법 등도 존재

도커 이미지를 제대로 만들어 도커 이미지 보관/배포 서버인 도커 레지스트리로 푸시(업로드)하는 방법은 알아두자

도커 컨테이너는 도커 이미지를 기반으로 실행되는 프로세스\
도커 이미지만 있다면 환경의 영향을 받지 않고 다양한 환경에서 컨테이너를 기동시킬 수 있다

가상 머신에 비해 가볍고 시작과 중지가 빠르다.\
가상 머신은 하이퍼바이저를 이용하여 게스트 OS를 동작시키나, 도커 컨테이너는 호스트 머신의 커널을 이용.

주의점 4가지 (중요도 순서대로)

```
1 컨테이너당 1 프로세스
변경 불가능한 인프라(Immutable Infrastructure) 이미지로 생성한다.
경량의 도커 이미지로 생성한다.
실행 계정은 root 이외의 사용자로 한다.
```

### 실습

```
# 도커 파일 예제 (DockerFile)
# Alpine 3.11 버전 golang 1.14.1 이미지를 기반 이미지로 사용
FROM golang:1.14.1-alpine3.11

# 빌드할 머신에 있는 main.go 파일을 컨테이너에 복사
COPY ./main.go ./

# 빌드 시 컨테이너 내부에서 명령어 실행
RUN go build -o ./go-app ./main.go

# 실행 계정을 nobody로 지정
USER nobody

# 컨테이너가 기동할 때 실행할 명령어 정의
ENTRYPOINT [" ./go-app"]
```

\- HTTP 요청이 오면 'Hello, Kubernetes' 라고 응답하는 Go 언어 애플리케이션을 실행하는 컨테이너 이미지의 생성 방법

<figure><img src="https://blog.kakaocdn.net/dn/drjrPw/btrwIgSh3yR/vqLZH5LPvYHDkifvASK2Lk/img.png" alt=""><figcaption><p>Dockerfile 빌드 및 결과</p></figcaption></figure>

멀티 스테이지 빌드\
여러 컨테이너 이미지를 사용하여 처리하고 결과물만 실행용 컨테이너 이미지에 복사하는 구조로 도커 이미지 사이즈를 줄일 수 있다.

```
# Stage 1 컨테이너(애플리케이션 컴파일)
FROM golang:1.14.1-alpine3.11 as builder
COPY ./main.go ./
RUN go build -o /go-app ./main.go

# Stage 2 컨테이너(컴파일한 바이너리를 포함한 실행용 컨테이너 생성)
FROM alpiine:3.11
# Stage 1에서 컴파일한 결과물을 복사
COPY --from=builder /go-app .
ENTRYPOINT ["./go-app"]
```

\- 멀티 스테이지 빌드 도커 파일 예제

&#x20;

<figure><img src="https://blog.kakaocdn.net/dn/cYX5ZI/btrwIibrrEs/f39A2hbioFkaGfqEyhVLJK/img.png" alt=""><figcaption><p>Docker MultiStage 빌드 및 결과</p></figcaption></figure>

<figure><img src="https://blog.kakaocdn.net/dn/cmaLz7/btrwIg5KYt5/xUrDTCNO4tHOtF1Kc8qmGK/img.png" alt=""><figcaption><p>Scratch로 변경한 빌드 및 결과</p></figcaption></figure>

다이브(Dive)는 도커 이미지를 조사하는 도구. 각각의 레이어에서 어느 파일에 변경이 있어 어느 정도의 용량이 소비되고있는지를 CUI에서 조사할 수 있다.

레이어마다 같은 파일의 변경이 많다면 docker image build 시 --squash 옵션을 사용하면 이미지 축소가 가능하다.

도커 레지스트리를 사용할 땐 신뢰할 수 있는 이미지인지를 확인하고 이용하자. 보안 스캔 도구도 있다.

도커 레지스트리에 이미지를 푸시하는 기본적인 형식은 아래와 같다.\
Docker 레지스트리 호스트명/네임스페이스/저장소:태그

<figure><img src="https://blog.kakaocdn.net/dn/epUKCj/btrwGiQoLcU/6X380GCvjkwKmBLobwb8mk/img.png" alt=""><figcaption><p>컨테이너 기동 및 테스트</p></figcaption></figure>

<figure><img src="https://blog.kakaocdn.net/dn/B49iq/btrwAZv9bIb/JNbv25DUAMhgi40KtQ7JKK/img.png" alt=""><figcaption><p>실행한 도커의 정지 및 확인</p></figcaption></figure>
