# awk

AWK는 텍스트 파일에서 데이터를 검색하고 처리하기 위한 프로그래밍 언어 및 명령어입니다. AWK는 다양한 옵션을 제공하며, 매우 유용하고 강력한 도구로 자주 사용됩니다. 이 문서에서는 AWK 명령어에 대해 자세히 알아보겠습니다.

### Option

AWK는 여러 옵션을 제공합니다. 일부 주요 옵션들은 아래와 같습니다.

```
F : 필드 구분자를 지정합니다.
v : 변수를 선언하고 값을 할당합니다.
f : AWK 스크립트 파일을 지정합니다.
i : 대소문자를 구분하지 않습니다.
W : 현재 문화권을 설정합니다.
```



AWK 명령어의 간단한 예시를 살펴보겠습니다.

<pre class="language-bash"><code class="lang-bash"><strong># file.txt의 각 행의 첫번째 필드를 출력합니다.
</strong><strong>> $ awk '{print $1}' file.txt
</strong><strong>
</strong><strong># file.csv의 각 행의 두번째 필드를 출력합니다.
</strong>> $ awk -F, '{print $2}' file.csv

# file.txt의 첫번째 필드를 모두 더한 값을 출력합니다.
<strong>> $ awk '{sum += $1} END {print sum}' file.txt
</strong></code></pre>

### E.g

#### 1. 파일 내 특정 패턴 검색

특정 문자열을 포함하는 행을 검색하려면 아래와 같이 명령어를 작성합니다.

```bash
> $ awk '/pattern/ {print}' file.txt
```

위 명령은 `file.txt`에서 `pattern`이 포함된 행을 출력합니다.

#### 2. 특정 필드 추출

특정 필드를 추출하려면 `-F` 옵션을 사용하여 필드 구분자를 지정하고, \
`$필드번호`를 사용합니다.

```bash
> $ awk -F, '{print $2}' file.csv
```

위 명령은 `file.csv`에서 두번째 필드를 출력합니다. `,`를 필드 구분자로 사용합니다.

#### 3. 특정 필드 합계 계산

특정 필드의 합계를 계산하려면 아래와 같이 명령어를 작성합니다.

```bash
> $ awk '{sum += $1} END {print sum}' file.txt
```

위 명령은 `file.txt`의 첫번째 필드를 모두 더한 값을 출력합니다.

#### 4. 특정 조건에 따른 처리

특정 조건을 만족하는 경우에만 처리를 수행하려면 `if`문을 사용합니다.

```bash
> $ awk '$1 > 10 {print}' file.txt
```

위 명령은 `file.txt`에서 첫번째 필드가 10보다 큰 경우에 해당하는 행을 출력합니다.

