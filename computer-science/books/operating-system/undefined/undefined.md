# 컴퓨터 시스템 조직

운영체제는 하드웨어 위에서 동작하며, 하드웨어 자원(CPU, 메모리, I/O 장치)을 제어·관리함으로써 사용자 프로그램이 복잡한 기계 동작을 의식하지 않고도 컴퓨터를 사용할 수 있게 한다.&#x20;

### 하드웨어 구성 요소

#### 중앙처리장치(CPU)

* **기능**: 메모리에서 명령어를 불러(fetch)와 해석(decode), 실행(execute)을 반복하며 프로그램을 수행
* **구성**:
  * **연산 논리 장치(ALU)**: 산술·논리 연산
  * **제어 장치(Control Unit)**: 명령어 해석 및 동기 제어
  * **레지스터(Register)**: 즉시 사용되는 임시 저장소(프로그램 카운터, 스택 포인터, 일반 레지스터 등)
* **클럭 속도(Clock Rate)**: GHz 단위로, 클럭 주기에 따라 한 사이클에 한 단계씩 진행

#### 주기억장치(Main Memory, RAM)

* **역할**: 실행 중인 프로그램과 데이터를 저장
* **특성**:
  * 휘발성(전원 차단 시 데이터 소실)
  * 접근 속도(수십 ns)
  * 용량(수 GB\~수십 GB)
* **구조**: 다중 계층 캐시(L1/L2/L3)와 연결, CPU와 직접 통신

#### 보조 기억장치(Secondary Storage)

* **종류**: HDD, SSD, NVMe 드라이브 등
* **특성**:
  * 비휘발성
  * 대용량(백 기가바이트\~수 테라바이트)
  * 접근 속도(수 ms, SSD는 수십 μs)
* **역할**: 영구 데이터 보관, 프로그램 로딩, 가상 메모리 백업 스페이스

#### 입출력(I/O) 장치

* **입력**: 키보드, 마우스, 터치스크린, 센서 등
* **출력**: 디스플레이, 프린터, 스피커, 모터 등
* **통신**: 네트워크 카드(이더넷, Wi-Fi), USB 컨트롤러 등

***

### 장치 컨트롤러(Device Controller)와 드라이버(Driver)

#### 장치 컨트롤러의 역할

각 I/O 장치는 전용 컨트롤러 칩(또는 보드)을 통해 시스템 버스에 연결된다. 컨트롤러는 다음을 담당한다.

1. **신호 변환**: 디지털 신호⇄아날로그 신호(예: 사운드 카드)
2. **데이터 버퍼링**: 장치 속도와 메모리 속도 차이 완충
3. **명령 해석**: 메모리에 적재된 제어 레지스터(command register) 값을 읽어 필요한 동작 수행
4. **상태 보고**: 동작 완료, 오류 발생 시 인터럽트 발생

#### 장치 드라이버의 역할

운영체제는 장치 컨트롤러별로 **드라이버**를 제공하여, 상위 계층(커널 코드)이 일관된 방식으로 장치를 제어하도록 한다.

* **추상화**: 컨트롤러별 저수준 레지스터 조작을 캡슐화
* **인터페이스**: read(), write(), ioctl() 같은 표준 함수로 제어
* **에러 처리**: 타임아웃, 재시도, 오류 로그 기록
* **동기화**: 멀티스레드/멀티프로세스 환경에서 안전한 접근 보장

***

### 시스템 버스(System Bus)와 동기화

#### 버스 구조

* **단일 버스**: CPU, 메모리, I/O 컨트롤러를 모두 직렬 연결
* **분리 버스**: 주소 버스, 데이터 버스, 제어 버스를 분리하여 병목 완화
* **버스 속도 및 폭**: 버스 폭(비트 수), 클럭 속도에 따라 대역폭 결정

#### 버스 접근 동기화

여러 컴포넌트가 동시에 버스를 사용하려 할 때 충돌 방지 메커니즘:

1. **버스 마스터십(Bus Mastering)**
   * DMA(Direct Memory Access) 컨트롤러가 CPU 개입 없이 메모리 직접 접근
2. **버스 아비터(Bus Arbiter)**
   * 중앙 집중형 또는 분산형 아비터가 우선순위에 따라 버스 사용 권한 할당
3. **메모리 컨트롤러 동기화**
   * 다중 요청 발생 시 순서 보장 및 데이터 무결성 확보

***

### 메모리 계층 구조(Memory Hierarchy)

#### 계층별 특성 비교

| 계층               | 속도      | 용량        | 비용 비율 | 휘발성 여부 |
| ---------------- | ------- | --------- | ----- | ------ |
| 레지스터(Register)   | 수 ns    | 몇 바이트     | 매우 높음 | 휘발성    |
| 캐시(Cache)        | 수 ns    | 수십\~수백 KB | 높음    | 휘발성    |
| 메인 메모리(RAM)      | 수십 ns   | 수 GB      | 중간    | 휘발성    |
| 보조 저장장치(SSD/HDD) | µs\~ms  | 수백 GB\~TB | 낮음    | 비휘발성   |
| 테르셔리 스토리지        | ms\~수 초 | TB 이상     | 매우 낮음 | 비휘발성   |

#### 운영체제의 관리 기법

1. **캐시 관리**
   * **라인 크기** 결정, **교체 정책** (LRU, LFU 등)
2. **버퍼링과 스풀링**
   * I/O 요청 집합화, 디스크 쓰기/읽기 순서 최적화
3. **가상 메모리(Virtual Memory)**
   * **페이징(Paging)**, **세그멘테이션(Segmentation)**
   * 스왑 공간을 통한 메모리 확장
4. **메모리 할당 전략**
   * **연속 할당** (첫 적합법, 최적 적합법 등)
   * **비연속 할당** (프레임 기반, 블록 기반)
