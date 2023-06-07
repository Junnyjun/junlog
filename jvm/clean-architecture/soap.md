# SOAP

SOAP(Simple Object Access Protocol)은 웹 서비스를 구현하는 데 사용되는 표준 프로토콜입니다. XML 기반의 메시지 형식을 사용하여, 서로 다른 플랫폼 및 언어로 작성된 애플리케이션 간에 데이터를 교환할 수 있습니다.&#x20;

#### SOAP의 구성 요소

1. **Envelope**: 모든 SOAP 메시지는 Envelope로 시작하며, 이것은 SOAP 메시지의 시작과 끝을 표시하는 데 사용됩니다.
2. **Header**: Header 요소는 선택 사항이며, 애플리케이션에서 처리해야 할 추가 정보가 포함될 수 있습니다. 예를 들어, 인증 정보, 트랜잭션 관리 정보 등이 헤더에 포함될 수 있습니다.
3. **Body**: Body 요소는 실제 메시지 데이터를 포함합니다. 일반적으로 메소드 매개변수와 반환 값이 여기에 표시됩니다.
4. **Fault**: 오류가 발생할 경우, Fault 요소가 사용되어 상세한 오류 정보를 전달할 수 있습니다.

#### SOAP 메시지 예제

아래는 간단한 SOAP 메시지 예제입니다.

```xml
xmlCopy code<soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope">
  <soap:Header>
    <authenticationToken xmlns="http://example.com">TOKEN</authenticationToken>
  </soap:Header>
  <soap:Body>
    <getWeather xmlns="http://example.com">
      <city>New York</city>
    </getWeather>
  </soap:Body>
</soap:Envelope>
```

위 예제에서는 `getWeather` 메소드를 호출하고 "New York"이라는 도시에 대한 날씨 정보를 요청합니다. 또한 인증 토큰이 헤더에 포함되어 있습니다.

#### WSDL (Web Services Description Language)

WSDL은 웹 서비스에 대한 정보를 기술하는 XML 기반의 언어입니다. WSDL 문서는 웹 서비스의 메소드, 매개변수, 반환 값 등에 대한 상세 정보를 포함하며, 클라이언트가 웹 서비스를 이해하고 사용할 수 있도록 돕습니다. 대부분의 경우, 프로그래밍 언어에 특정한 도구를 사용하여 WSDL 문서를 토대로 클라이언트 코드를 자동으로 생성할 수 있습니다.

## wsimport

`wsimport`는 Java 플랫폼에서 웹 서비스를 사용하기 위한 도구입니다. 웹 서비스의 WSDL(Web Services Description Language) 문서를 기반으로 자동으로 Java 웹 서비스 클라이언트 스텁을 생성합니다. 이렇게 생성된 스텁을 사용하여, 개발자는 웹 서비스와 통신하는 데 필요한 로우-레벨의 코드 작성을 건너뛸 수 있습니다.

#### 사용 방법

`wsimport` 도구는 Java Development Kit(JDK)에 포함되어 있으며, 커맨드 라인에서 다음과 같은 형식으로 실행할 수 있습니다.

```css
cssCopy codewsimport [options] <WSDL_URI>
```

여기서 `options`는 다양한 설정 옵션을 나타내고, `<WSDL_URI>`는 웹 서비스의 WSDL 문서 URL을 지정합니다.

#### 주요 옵션

* `-d <directory>`: 생성된 클래스 파일이 저장될 디렉토리를 지정합니다.
* `-s <directory>`: 생성된 소스 파일이 저장될 디렉토리를 지정합니다.
* `-p <package>`: 생성된 클래스 파일에 대한 패키지 이름을 지정합니다.
* `-keep`: 생성된 소스 파일과 함께 클래스 파일도 유지하도록 지시합니다.
* `-verbose`: 도구의 실행 과정에서 자세한 정보를 출력하도록 지시합니다.

#### E.g

```javascript
wsimport -d generated_classes -s generated_sources -p com.example.webservice -keep -verbose http://example.com/yourwebservice?wsdl
```

위 명령어는 다음과 같은 동작을 수행합니다:

```
-d generated_classes: 생성된 클래스 파일을 generated_classes 디렉토리에 저장합니다.
-s generated_sources: 생성된 소스 파일을 generated_sources 디렉토리에 저장합니다.
-p com.example.webservice: 생성된 클래스 파일의 패키지 이름을 com.example.webservice로 지정합니다.
-keep: 생성된 소스 파일과 클래스 파일을 모두 유지합니다.
-verbose: 실행 과정에서 자세한 정보를 출력합니다.
http://example.com/yourwebservice?wsdl: 웹 서비스의 WSDL 문서 URL을 지정합니다.
```
