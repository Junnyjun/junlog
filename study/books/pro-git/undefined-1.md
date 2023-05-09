# 브런치

Staged 와 Unstaged 상태의 변경 내용을 보기\
단순히 파일이 변경됐다는 사실이 아니라 어떤 내용이 변경됐는지 살펴보려면,\
git status 명령이 아니라 git diff 명령을 사용한다.\
Patch처럼 어떤 라인을 추가했고 삭제했는지가 궁금할 때 사용한다.

&#x20;README 파일을 수정해서 Staged 상태로 만들고 CONTRIBUTING.md 파일은 그냥 수정만 해둔다.\
이 상태에서 git status 명령을 실행하면 아래와 같은 메시지를 볼 수 있다.&#x20;

<figure><img src="https://blog.kakaocdn.net/dn/cl2eQt/btqOdOD1NUv/WR58aolkFq5743iQ9KxgK1/img.png" alt="" width="690"><figcaption></figcaption></figure>

git diff 명령을 실행하면 수정했지만 아직 staged 상태가 아닌 파일을 비교해볼 수 있다. \
이 명령은 워킹 디렉토리에 있는 것과 Staging Area에 있는 것을 비교한다.\
그래서 수정하고 아직 Stage하지 않은 것을 보여준다.\
git diff는 Unstaged 상태인 것들만 보여준다. 이 부분이 조금 헷갈릴 수 있다.\
수정한 파일을 모두 Staging Area에 넣었다면 git diff 명령은 아무것도 출력하지 않는다.&#x20;

<figure><img src="https://blog.kakaocdn.net/dn/pFIgv/btqN9L2HFHp/12JtHSCpACZiiHma0jU7cK/img.png" alt="" width="704"><figcaption></figcaption></figure>

<figure><img src="https://blog.kakaocdn.net/dn/mHuHY/btqOeCi3Mhs/oIxCm696ppRjKogDwShI11/img.png" alt="" width="716"><figcaption></figcaption></figure>

&#x20;만약 커밋하려고 Staging Area에 넣은 파일의 변경 부분을 보고 싶으면 git diff --staged 또는 git diff --cached옵션을 사용한다.\
이 명령은 저장소에 커밋한 것과 Staging Area에 있는 것을 비교한다.

#### 변경사항 커밋하기

수정한 것을 커밋하기 위해 Staging Area에 파일을 정리했다. \
Unstaged 상태의 파일은 커밋되지 않는다는 것을 기억해야 한다.\
Git은 생성하거나 수정하고 나서 git add 명령으로 추가하지 않은 파일은 커밋하지 않는다.\
그 파일은 여전히 Modified 상태로 남아있다. \
커밋하기 전에 git status 명령으로 모든 것이 Staged 상태인지 확인할 수 있다.

<figure><img src="https://blog.kakaocdn.net/dn/bD5f7j/btqOdNZqeyR/BQ1Nf4r2dKBk4cTejZD6jk/img.png" alt="" width="758"><figcaption></figcaption></figure>

자동으로 생성되는 커밋 메시지의 첫 라인은 비어있고 둘째 라인부터 git status 명령의 결과가 채워진다.\
커밋한 내용을 쉽게 기억할 수 있도록 이 메시지를 포함할 수도 있고 메시지를 전부 지우고 새로 작성할 수 있다.

정확히 뭘 수정했는지도 보여줄 수 있는데, git commit에 -v 옵션을 추가하면 편집기에 diff메시지도 추가된다. \
내용을 저장하고 편집기를 종료하면 Git은 입력된 내용(#로 시작하는 내용을 제외한)으로 새 커밋을 하나 완성한다.

&#x20;메시지를 인라인으로 첨부할 수도 있다. commit 명령을 실행할 때 -m 옵션을 사용한다.

<figure><img src="https://blog.kakaocdn.net/dn/87vlc/btqOcQCiY77/vB3FQhhjFFE3nIONHlpR0K/img.png" alt="" width="770"><figcaption></figcaption></figure>

commit 명령은 몇 가지 정보를 출력하는데 위 예제는 (master)브랜치에 커밋했고 체크섬은 (463dc4f)라고 알려준다.\
그리고 수정한 파일이 몇 개이고 삭제됐거나 추가된 라인이 몇 라인인지 알려준다.

&#x20;Git은 Staging Area에 속한 스냅샷을 커밋한다는 것을 기억해야 한다.\
수정은 했지만, 아직 Staging Area에 넣지 않은 것은 다음에 커밋할 수 있다. \
커밋할 때마다 프로젝트의 스냅샷을 기록하기 때문에 나중에 스냅샷끼리 비교하거나 예전 스냅샷으로 되돌릴 수 있다.&#x20;

### Staging Area 생략하기

Staging Area는 커밋할 파일을 정리한다는 점에서 매우 유용하지만 복잡하기만 하고 필요하지 않은 때도 있다.\
git commit 명령을 실행할 때 -a 옵션을 추가하면 Git은 Tracked 상태의 파일을 자동으로 Staging Area에 넣는다.\
그래서 git add 명령을 실행하는 수고를 덜 수 있다.&#x20;

#### 파일 삭제하기

Git에서 파일을 제거하려면 git rm 명령으로 Tracked 상태의 파일을 삭제한 후에 커밋해야 한다. \
이 명령은 워킹 디렉토리에 있는 파일도 삭제하기 때문에 실제로 파일도 지워진다.\
Git 명령을 사용하지 않고 단순히 워킹 디렉토리에서 파일을 삭제하고 git status 명령으로 상태를 확인

<figure><img src="https://blog.kakaocdn.net/dn/vjKLs/btqOdMTLNGU/6NU7Vho669yw8x4nqtsHtK/img.png" alt="" width="706"><figcaption></figcaption></figure>

그리고 git rm 명령을 실행하면 삭제한 파일은 staged 상태가 된다.

<figure><img src="https://blog.kakaocdn.net/dn/cXJtaz/btqOgtTFlu8/uUbcJH0jYtyz1C74P6j371/img.png" alt="" width="694"><figcaption></figcaption></figure>

커밋하면 파일은 삭제되고 Git은 이 파일을 더는 추적하지 않는다.\
이미 파일을 수정했거나 Staging Area에 추가했다면 -f 옵션을 주어 강제로 삭제해야 한다.\
이 점은 실수로 데이터를 삭제하지 못하도록 하는 안전장치다.\
커밋하지 않고 수정한 데이터는 Git으로 복구할 수 없기 때문이다.

또, Staging Area에서만 제거하고 워킹 디렉토리에 있는 파일은 지우지 않고 남겨둘 수 있다.\
다시 말해서 하드디스크에 있는 파일은 그대로 두고 Git만 추적하지 않게 한다.\
이것은 .gitignore 파일에 추가하는 것을 빼먹었거나 대용량 로그 파일이나 컴파일된 파일인 .a 파일 같은 것을 실수로 추가했을 때 쓴다.

\--cached 옵션을 사용하여 명령을 실행한다.

```bash
> git rm --cached README 
```

여러 개의 파일이나 디렉토리를 한꺼번에 삭제할 수도 있다.\
아래와 같이 git rm 명령에 file-glob 패턴을 사용한다.

```bash
> git rm log/\*.log
```

\* 앞에 \을 사용한 것을 기억하자. \
파일명 확장 기능은 쉘에만 있는 것이 아니라 Git 자체에도 있기 때문에 필요하다.\
이 명령은 log/ 디렉토리에 있는 .log 파일을 모두 삭제한다. 아래의 예제처럼 할 수도 있다.

```bash
> git rm \*~
```

이 명령은 \~로 끝나는 파일은 모두 삭제한다.

#### &#x20;커밋 히스토리 조회하기

&#x20;Git에는 히스토리를 조회하는 명령어인 git log가 있다.\
특별한 argument 없이 git log 명령을 실행하면 저장소의 커밋 히스토리를 시간순으로 보여준다.\
즉, 가장 최근의 커밋이 가장 먼저 나온다. \
그리고 이어서 각 커밋의 SHA-1 체크섬, 저자 이름, 저자 이메일, 커밋한 날짜, 커밋 메시지를 보여준다.

&#x20;원하는 히스토리를 검색할 수 있도록 git log 명령은 매우 다양한 옵션을 지원한다. \
여기에서는 자주 사용하는 옵션을 설명한다.

&#x20;\-p는 각 커밋의 diff 결과를 보여준다. 다른 유용한 옵션으로 \`-2\`가 있는데 최근 두 개의 결과만 보여주는 옵션이다

```bash
> git log -p -2
```

이 옵션은 직접 diff를 실행한 것과 같은 결과를 출력하기 때문에 동료가 무엇을 커밋했는지 리뷰하고 빨리 조회하는데 유용하다.

또 git log 명령에는 히스토리의 통계를 보여주는 옵션도 있다. --stat 옵션으로 각 커밋의 통계정보를 조회할 수 있다.

<figure><img src="https://blog.kakaocdn.net/dn/JOAJ6/btqOdaN3W1w/r9FBq83mFcUDTWs4m5biL1/img.png" alt="" width="691"><figcaption></figcaption></figure>

이 결과에서 --stat 옵션은 어떤 파일이 수정됐는지,얼마나 많은 파일이 변경됐는지, 또 얼마나 많은 라인을 추가하거나 삭제했는지 보여준다.&#x20;

또 다른 유용한 옵션은 --pretty 옵션이다.\
이 옵션을 통해 히스토리 내용을 보여줄 때 기본 형식 이외에 여러 가지 중에 하나를 선택할 수 있다.\
oneline 옵션은 각 커밋을 한 라인으로 보여준다.&#x20;

oneline option과 format 옵션은 --graph 옵션과 함께 사용할 때 더 빛난다.\
이 명령은 브랜치와 머지 히스토리를 보여주는 아스키 그래프를 출력한다.\
다음 장에서 살펴볼 브랜치나 Merge 결과의 히스토리를 이런 식으로 살펴보면 훨씬 흥미롭다.

git log 명령의 기본적인 옵션과 출력물의 형식에 관련된 옵션을 살펴보았다.\
git log 명령은 앞서 살펴본 것보다 더 많은 옵션을 지원한다.

<figure><img src="https://blog.kakaocdn.net/dn/b0MsrC/btqN7UeWsK4/r4k1KVl7VIU8931Phf0Yq0/img.png" alt="" width="746"><figcaption></figcaption></figure>

#### 조회 제한조건

출력 형식과 관련된 옵션을 살펴봤지만 git log 명령은 조회 범위를 제한하는 옵션들도 있다.\
히스토리 전부가 아니라 부분만 조회한다.\
\--since 나 --until 같은 시간을 기준으로 조회하는 옵션은 매우 유용하다. \
지난 2주 동안 만들어진 커밋들만 조회하는 명령은 아래와 같다.

```bash
> git log --since=2.weeks
```

이 옵션은 다양한 형식을 지원한다.\
"2020-08-15"같이 정확한 날짜도 사용할 수 있고 "2 years 1 day 3 minutes ago" 같이 상대적인 기간을 사용할 수도 있다.

\-S 옵션은 코드에서 추가되거나 제거된 내용 중에 특정 텍스트가 포함되어 있는지를 검색한다.

```bash
> git log --Sfunction_name
```

디렉토리나 파일 이름을 사용하여 그 파일이 변경된 log의 결과를 검색할 수도 있다.\
\--와 함께 경로 이름을 명령어 끝 부분에 쓴다.

```bash
> git log -- path1 path2 
```

<figure><img src="https://blog.kakaocdn.net/dn/bcNZNo/btqOcRH2kkf/DGum6qiRpLCCxl8PLe2ek0/img.png" alt="" width="718"><figcaption></figcaption></figure>
