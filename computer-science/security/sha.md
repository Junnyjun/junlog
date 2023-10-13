# SHA

SHA (Secure Hash Algorithm, 안전한 해시 알고리즘) 함수들은 **암호학적 해시 함수**들의 모음입니다.

해시 함수의 버전은 0\~3버전 까지 존재하며, 작성 시점엔 SHA2가 가장 범용적이며, \
현재 SHA3가 권장되고 있습니다.

## 특징

A. 해시 함수는 두 값이 다르면 데이터도 다르다 ( 같을 경우 해시 충돌 )\
B. 해싱된 값을 복호화 할수 없다.\
C. 결과물은 고정된 길이의 숫자를 가진다.

| Version | Name          | Bit |
| ------- | ------------- | --- |
| SHA-0/1 | SHA-0/1       | 160 |
| SHA-2   | SHA-224       | 224 |
| SHA-2   | SHA-256       | 256 |
| SHA-2   | SHA-384       | 384 |
| SHA-2   | SHA-512       | 512 |
| SHA-2   | SHA-512/224   | 224 |
| SHA-2   | SHA-512/256   | 256 |
| SHA-3   | SHA-224       | 224 |
| SHA-3   | SHA-256       | 256 |
| SHA-3   | SHA-384       | 384 |
| SHA-3   | SHA-512       | 512 |
| SHA-3   | SHAKE 128/256 | 임의  |

## SHA-0/SHA-1

sha-0에 압축 함수에 비트 회전 연산을 하나 추가한 알고리즘 입니다.

SHA-1은 SHA 함수들 중 가장 많이 쓰이며, TLS, SSL, PGP, SSH, IPSec 등 많은 보안 프로토콜과 프로그램에서 사용되고 있다. SHA-1은 이전에 널리 사용되던 MD5를 대신해서 쓰이기도 한다.

SHA-0과 SHA-1는 해시 충돌에 대한 이슈가 생겨 중요한 데이터는 이 이상의 알고리즘을 사용할 것을 권장하고 있습니다.

### 동작 원리

<img src="../../.gitbook/assets/file.excalidraw (19).svg" alt="" class="gitbook-drawing">

#### A 패딩

INPUT MESSAGE를 512bit의 블록으로 나눕니다.\
이때 512 비트는 448bit ( message + padding ) + 64bit ( message length )로 나눕니다.

#### B 32Bit 분리

<table data-header-hidden><thead><tr><th width="125">WORD</th><th>VALUE</th></tr></thead><tbody><tr><td>A</td><td>0x67452301</td></tr><tr><td>B</td><td>0xefcdab89</td></tr><tr><td>C</td><td>0x98badcfe</td></tr><tr><td>D</td><td>0x10325476</td></tr><tr><td>E</td><td>0xc3d2e1f0</td></tr></tbody></table>

W0 - W79는 위 고정된 Word Buffer와 특정 연산을 거쳐 160Bit를 만들어냅니다.

<figure><img src="../../.gitbook/assets/image (12).png" alt=""><figcaption></figcaption></figure>

#### C Hash 생성

80개의 WT는 다음과 같은 공식을 거쳐 생성됩니다.

| 0<= t <= 19   | f (t, B, C, D) = (B & C) \| (\~B & D)           |
| ------------- | ----------------------------------------------- |
| 20<= t <= 39  | f (t, B, C, D) = B ^ C ^ D                      |
| 40<= t <= 59  | f (t, B, C, D) = (B & C ) \| (B & D) \| (C & D) |
| 60 <= t <= 79 | f (t, B, C, D) = B ^ C ^ D                      |

`Wt = (Wt-16 XOR Wt-14 XOR Wt-8 XOR Wt-3) <<< 1bit 레프트 로테이션`

## SHA2
