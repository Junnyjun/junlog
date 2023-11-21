# Pipe filter pattern

이 패턴은 데이터 스트림을 생성하고 처리하는 시스템에서 사용할 수 있다.&#x20;

각 처리 과정은 **필터 (filter)** 컴포넌트에서 이루어지며, 처리되는 데이터는 **파이프 (pipes)**를 통해 흐른다. 이 파이프는 버퍼링 또는 동기화 목적으로 사용될 수 있다.

### 활용 <a href="#3" id="3"></a>

컴파일러. 연속한 필터들은 어휘 분석, 파싱, 의미 분석 그리고 코드 생성을 수행한다.

<img src="../../.gitbook/assets/file.excalidraw (26).svg" alt="" class="gitbook-drawing">
