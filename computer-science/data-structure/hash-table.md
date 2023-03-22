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

Key를 해쉬하여 배열에 넣고 사용하는 자료구조입니다.

<details>

<summary>※ hash는 동일 문자열에 항상 같은 응답을 합니다.</summary>

```java
String hased(String raw) throws NoSuchAlgorithmException {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    StringBuilder hexString = new StringBuilder();
    for (byte b : digest.digest(raw.getBytes(StandardCharsets.UTF_8))) {
        String hex = Integer.toHexString(0xff & b);
        if (hex.length() == 1) {
            hexString.append('0');
        }
        hexString.append(hex);
    }
    return hexString.toString();
}
```

</details>

| hash key         | key       |
| ---------------- | --------- |
| bc72337a7af5 ... | firstKey  |
| 0b41b20f3772 ... | secondKey |
| 4c44f766cb3c ... | thirdKey  |

해시 테이블은 해시함수를 사용하여 배열 인덱스의 범위를 줄일 수 있기 때문에 전체 테이블의 크기가 줄어듭니다

### 충돌

해시 테이블 크기보다 키의 개수가 많기 때문에 두 개의 키가 동일한 slot에 해시될 수 있습니다. 이것을 해시 충돌(hash collison)이라고 합니다.

<details>

<summary>적재율 (Load Factor)</summary>

해시 테이블의 크기 대비, 키의 개수

```
적재율(a) = n/m (n: 키의 개수, m: 테이블의 크기)
```

</details>

Direct address table은 키 값을 인덱스로 사용하는 구조이기 때문에 적재율이 1 이하이며 적재율이 1 초과인 해시 테이블의 경우는 반드시 충돌이 발생하게 됩니다.

해시 테이블은 테이블의 크기가 키의 개수보다 작기 때문에 적재율이 1이 초과합니다. 그렇기 때문에 해시 충돌이 발생할 수밖에 없습니다.

만약, 충돌이 발생하지 않는다면 테이블의 탐색, 삽입, 삭제 연산은 모두 O(1)에 수행되지만 충돌이 발생할 경우 충돌에 대한 추가 작업이 필요하게 됩니다.

충돌은 해시 테이블 연산 효율을 떨어트리기 때문에 충돌을 줄이는 것이 해시 테이블의 핵심입니다.

해시 함수를 개선하여 충돌을 줄일 수 있습니다.
