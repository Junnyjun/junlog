# Hash Table

### Direct Address Table <a href="#direct_address_table" id="direct_address_table"></a>

키 값을 배열의 인덱스로 환산해서 데이터에 접근하는 자료이다

<img src="../../.gitbook/assets/file.excalidraw.svg" alt="" class="gitbook-drawing">

Table의 크기는 전체 키들의 숫자와 같습니다.\
사용하지 않는 키는 비워둡니다.

| 종류     | 연산                      |
| ------ | ----------------------- |
| SEARCH |  Table\[key]            |
| INSERT |  Table\[key] = value    |
| DELETE |  Table\[key] = ~~NULL~~ |

전체 키를 예측해야 테이블 사이즈를 정할 수 있습니다.\
전체 키 대비 사용하는 키가 적다면 비실용적입니다

### Hash Table
