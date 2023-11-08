# 입출력 I/O

### 1. 자바에서의 입출력 <a href="#1" id="1"></a>

컴퓨터 내부 또는 외부의 장치와 프로그램간의 데이터를 주고받는 것을 말한다.

#### 스트림 <a href="#undefined" id="undefined"></a>

데이터를 운반하는데 사용되는 연결통로

단방향 통신만 가능하기 때문에 하나의 스트림으로 입력과 출력을 동시에 처리할 수 없다.

#### 바이트기반 스트림 - InputStream, OutputStream <a href="#inputstream-outputstream" id="inputstream-outputstream"></a>

| 입력스트림                | 출력스트림                 | 입출력 대상의 종류 |
| -------------------- | --------------------- | ---------- |
| FileInputStream      | FileOutputStream      | 파일         |
| ByteArrayInputStream | ByteArrayOutputStream | 메모리        |
| PipedInputStream     | pipedOutputStream     | 프로세스간의 통신  |
| AudioInputStream     | AudioOutputStream     | 오디오장치      |

#### 보조 스트림 <a href="#undefined" id="undefined"></a>

스트림의 기능을 보완하기 위한 보조스트림이 제공된다.

보조스트림만으로는 입출력을 처리할 수 없고, 스트림을 먼저 생성한 다음에 이를 이요해서 보조스트림을 생성해야한다.

```java
FileInputStream fis = new FileInputStream("test.txt");

BufferedInputStream bis = new BufferedInputStream(fis);

bis.read();
```

| 입력                    | 출력                   | 설명                                              |
| --------------------- | -------------------- | ----------------------------------------------- |
| FilterInputStream     | FilterOutputStream   | 필터를 이용한 입출력 처리                                  |
| BufferedInputStream   | BufferedOutputStream | 버퍼를 이용한 입출력 성능 향상                               |
| DataInputStream       | DataOutputStream     | int, float와 같은 기본형 단위로 데이터를 처리하는 기능             |
| SequenceInputStream   | 없음                   | 두 개의 스트림을 하나로 연결                                |
| LineNumberInputStream | 없음                   | 읽어 온 데이터의 라인 번호를 카운트                            |
| ObjectInputStream     | ObjectOutputStream   | 데이터를 객체 단위로 읽고 쓰는데 사용. 주로 파일을 이용하며 객체 직렬화와 관련있음 |
| 없음                    | PrintOutputStream    | 버퍼를 이용하며, 추가적인 print관련 기능                       |
| PushbackInputStream   | 없음                   | 버퍼를 이용해서 읽어 온 데이터를 다시 되돌리는 기능                   |

#### 문자 기반 스트림 - Reader, Writer <a href="#reader-writer" id="reader-writer"></a>

Java에서는 char가 2byte이기 때문에 바이트 기반의 스트림으로 문자를 처리하는데 어려움이 있다.

문자데이터를 입출력할 때는 바이트기반 스트림 대신 문자기반 스트림을 사용하자.

보조스트림 역시 이름만 다를 뿐 사용목적과 방식은 바이트기반과 다르지 않다.



### 2. 표준입출력과 File <a href="#2-file" id="2-file"></a>

#### 표준 입출력 - System.in, System.out, System.err <a href="#systemin-systemout-systemerr" id="systemin-systemout-systemerr"></a>

* System.in\
  콘솔로부터 데이터를 입력받는데 사용
* System.out\
  콘솔로 데이터를 출력하는데 사용
* System.err\
  콘솔로 데이터를 출력하는데 사용

#### 표준입출력 대상변경 - setOut(), setErr(), setIn() <a href="#setout-seterr-setin" id="setout-seterr-setin"></a>

입출력을 콘솔 이외에 다른 입출력 대상으로 변경하는 것이 가능하다.

#### RandomAccessFile <a href="#randomaccessfile" id="randomaccessfile"></a>

하나의 클래스로 파일의 입력과 출력을 모두 할 수 있도록 되어 있다.

파일의 어느 위치에나 읽기/쓰기가 가능하다.

#### File <a href="#file" id="file"></a>

File클래스를 통해서 파일과 디렉토리를 다룰 수 있다.



### 3. 직렬화(Serialization) <a href="#3-serialization" id="3-serialization"></a>

객체를 데이터 스트림으로 만드는 것

스트림으로부터 데이터를 읽어서 객체를 만드는 것을 역직렬화라고 한다.

#### Serializable <a href="#serializable" id="serializable"></a>

Serializable인터페이스를 구현한 클래스는 직렬화가 가능하다.

조상클래스가 Serializable을 구현했다면 해당 클래스는 직렬화가 가능하다.

조상클래스가 Serializable을 구현하지 않았다면 자손클래스를 직렬화할 때 조상클래스에 정의된 인스턴스변수는 직렬화 대상에서 제외된다. 따라서, 모든 클래스의 조상인 Object는 Serializble을 구현하지 않았기 때문에 직렬화할 수 없다.

#### transient <a href="#transient" id="transient"></a>

직렬화하고자 하는 객체의 클래스에 직렬화가 안 되는 객체에 대한 참조를 포함하고 있다면, 제어자 transient를 붙여서 직렬화 대상에서 제외할 수 있다.

#### 직렬화가능한 클래스의 버전관리 <a href="#undefined" id="undefined"></a>

직렬화된 객체를 역직렬화할 때 직렬화 했을 때와 같은 클래스를 사용해야 한다.

객테가 직렬화될 때 클래스에 정의된 멤버들의 정보를 이용해서 serialVersionUID라는 클래스의 버전을 자동생성해서 직렬화 내용에 포함한다.

클래스의 버전을 수동으로 관리할 수 있다.\
클래스 내에 serialVersionUID를 정의해주면, 클래스의 내용이 바뀌어도 클래스의 버전이 자동생성된 값으로 변경되지 않는다.

serialVersionUID의 값은 어떠한 값으로도 지정할 수 있지만 중복되지 않기 위해 serialver.exe를 사용해서 생성된 값을 사용하는 것이 일반적이다.
