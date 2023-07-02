# Hash Function

임의의 길이의 키값을 고정된 길이의 해시로 변환해주는 맵핑 함수입니다.

<img src="../../../.gitbook/assets/file.excalidraw (2) (1).svg" alt="" class="gitbook-drawing">

<pre><code>Key 
<strong>- 자연수로 가정 ( 만약 자연수가 아닌 character나 string의 형태라면 자연수 형태로 변형하여 사용합니다. )
</strong><strong>
</strong>Hash
- 해시 함수를 통해 작아진 값을 해시(hash), 해시 값(hash value), 해시 코드(hash code)라고 합니다.
- 해시 테이블의 버킷을 가리키는 주소로 사용됩니다.

Hasing
- 키 값을 해시함수를 통해 해시로 변환해주는 작업을 해싱(Hasing)이라고 합니다.
</code></pre>



## Hashing 방법

### 나눗셈 방식

나눗셈 방법(Division method)은 나머지 연산을 이용하여 해시를 만드는 방법입니다.\
검색키 k를 해시 테이블의 크기 m으로 나눈 나머지를 해시로 사용합니다.

`h(k) = k mod m (k: key, m: table size)`

1. Target = 2^n 을 피한다

Target = 2^n 가 되면 해시 함수 h(k)는 키 k의 하위 p비트가 됩니다.

해쉬 함수가 key값 전체에 따라 바뀌지 않고 하위 p 비트에만 영향을 받습니다. \
하위 n비트가 고르게 나온다면 괜찮겠지만 대부분 그렇지 않기 때문에,\
Target = 2^n 인 경우는 피해야 합니다.

<img src="../../../.gitbook/assets/file.excalidraw (8).svg" alt="" class="gitbook-drawing">

2. Target = 2^n  너무 가깝지 않은 소수를 선택합니다.

키의 수가 20인 경우 테이블 크기(m)는 17로 하는 게 좋습니다.



### 이동 접기

해시 테이블 크기의 자릿수로 키를 분해한다.\
분해된 키들을 더한다.\
더한 값이 테이블 크기를 초과한다면 초과한 자릿수는 버린다.

<img src="../../../.gitbook/assets/file.excalidraw (9).svg" alt="" class="gitbook-drawing">

### 경계 접기

해시테이블 크기의 자릿수로 키를 분해한다.\
나누어진 각 부분의 경계 부분 값을 역으로 정렬한다.\
분해된 키들을 더한다.\
더한 값이 테이블 자릿수를 초과한다면 초과한 자릿수는 버린다.

<img src="../../../.gitbook/assets/file.excalidraw (9).svg" alt="" class="gitbook-drawing">

### 중간 제곱법

키 값을 제곱한 후 결과 값의 중간 부분에 있는 몇 비트만 선택하여 해시로 사용하는 방법입니다.\
테이블 크기의 자릿수만큼 중간값을 가져옵니다.

<img src="../../../.gitbook/assets/file.excalidraw (9).svg" alt="" class="gitbook-drawing">
