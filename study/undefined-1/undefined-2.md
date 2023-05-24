# 명령어

### 소스 코드와 명령어 <a href="#31" id="31"></a>

#### 고급언어와 저급언어 <a href="#undefined" id="undefined"></a>

고급 언어(high-level programming language): 사람이 이해하기 쉬운 언어이다.\
저급 언어(low-level programming language): 컴퓨터가 이해하고 실행하는 언어이다.\
저급 언어에는 기계어와 어셈블리어가 있다.\
기계어(machine code): 0과 1로 이루어진 이진수로 이루어진 언어이다.\
어셈블리어(assembly language): 기계어와 1:1 대응되는 기호로 이루어진 언어이다.

#### 컴파일 언어와 인터프리터 언어 <a href="#undefined" id="undefined"></a>

컴파일 언어(compile language): 컴파일러에 의해 저급 언어로 변환되는 언어이다. \
소스코드가 컴파일러에 의해 목적 코드가 생성된다.\
인터프리터 언어(interpreter language): 소스코드가 인터프리터에 의해 한 줄씩 실행된다.

### 명령어의 구조 <a href="#32" id="32"></a>

#### 연산 코드와 오퍼랜드 <a href="#undefined" id="undefined"></a>

명령어는 연산 코드와 오퍼랜드로 구성된다.

연산 코드(operation code): 명령어의 기능을 나타내는 코드이다. 명령어가 수행할 연산을 나타낸다.\
오퍼랜드(operand): 연산에 사용할 데이터 또는 연산에 사용할 데이터가 저장된 메모리 주소이다.

오퍼랜드 필드를 주소 필드라고도 한다. 오퍼랜드는 0개 또는 1개 이상일 수 있다.\
연산 코드는 CPU의 종류에 따라 다르다. \
대표적으로는 데이터 전송, 산술/논리 연산, 제어 연산, 입출력 연산이 있다.

#### 주소 지정 방식 <a href="#undefined" id="undefined"></a>

유효 주소(effective address): 명령어가 오퍼랜드를 참조하는 주소이다.\
주소 지정 방식(addressing mode): 명령어가 오퍼랜드를 참조하는 방법이다.

* 즉시 주소 지정 방식(immediate addressing mode): 명령어에 직접 오퍼랜드를 기술하는 방법이다.
* 직접 주소 지정 방식(direct addressing mode): 오퍼랜드가 메모리 주소인 경우, 메모리 주소를 직접 기술하는 방법이다.
* 간접 주소 지정 방식(indirect addressing mode): 오퍼랜드가 메모리 주소인 경우, 메모리 주소가 저장된 메모리 주소를 기술하는 방법이다.
* 레지스터 주소 지정 방식(register addressing mode): 오퍼랜드가 레지스터인 경우, 레지스터 번호를 기술하는 방법이다.
* 레지스터 간접 주소 지정 방식(register indirect addressing mode): 오퍼랜드가 레지스터인 경우, 레지스터 번호가 저장된 메모리 주소를 기술하는 방법이다.

### 스택과 큐 <a href="#undefined" id="undefined"></a>

<table><thead><tr><th width="109">종류</th><th>설명</th></tr></thead><tbody><tr><td>스택</td><td>후입선출(LIFO, Last In First Out) 구조이다. <br>데이터를 넣는 것을 푸시(push), 데이터를 꺼내는 것을 팝(pop)이라고 한다.</td></tr><tr><td>큐</td><td>선입선출(FIFO, First In First Out) 구조이다. <br>데이터를 넣는 것을 인큐(enqueue), 데이터를 꺼내는 것을 디큐(dequeue)라고 한다.</td></tr></tbody></table>
