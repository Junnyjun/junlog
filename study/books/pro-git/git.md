# Git 기술

### **Git 저장소**

```
1. 기존 프로젝트나 디렉토리를 Git 저장소로 만드는 방법이 있고,
2. 다른 서버에 있는 저장소를 Clone 하는 방법이 있다.
```

기존 프로젝트를 Git으로 관리하고 싶을 때, 프로젝트의 디렉토리로 이동한다. \
그리고 아래와 같은 명령을 실행한다.

```sh
> git init
```

이 명령은 `.git` 이라는 하위 디렉토리를 만든다. \
`.git` 디렉토리에는 저장소에 필요한 skeleton이 들어있다.\
이 명령만으로는 아직 프로젝트의 어떤 파일도 관리하지 않는다.

Git이 파일을 관리하게 하려면 저장소에 파일을 추가하고 커밋해야 한다.\
git add 명령으로 파일을 추가하고 git commit 명령으로 커밋한다.&#x20;

#### **기존 저장소를 Clone하기**

다른 프로젝트에 참여하려거나(Contribute) Git 저장소를 복사하고 싶을 때 git clone 명령을 사용한다.\
git clone을 실행하면 프로젝트 히스토리를 전부 받아온다.\
실제로 서버의 디스크가 망가져도 클라이언트 저장소 중에서 아무거나 하나 가져다가 복구하면 된다.&#x20;

```bash
> git clone https://github.com/Junnyjun/JunnyJun.git
> git clone https://github.com/Junnyjun/JunnyJun.git newDir
```

만질 수 있는 Git 저장소를 하나 만들었고 워킹 디렉토리에 Checkout도 했다. \
이제는 파일을 수정하고 파일의 스냅샷을 커밋해보자.

파일을 수정하다가 저장하고 싶으면 스냅샷을 커밋한다.

워킹 디렉토리의 모든 파일은 크게 Tracked(관리대상임)와 Untracked(관리 대상이 아님)로 나눈다.

```
Tracked : 이미 스냅샷에 포함돼 있던 파일이다.
Tracked 파일은 또 Unmodified(수정하지 않음)와 Modified(수정함) 그리고 Staged(커밋으로 저장소에 기록할) 상태 중 하나이다.

Untracked : 나머지 파일이다
Untracked 파일은 워킹 디렉토리에 있는 파일 중 스냅샷에서도 Staging Area에도 포함되지 않은 파일이다.
처음 저장소를 Clone하면 모든 파일은 Tracked이면서 Unmodified 상태이다.
파일은 Checkout하고 나서 아무것도 수정하지 않았기 때문이다.
마지막 커밋 이후 아직 아무것도 수정하지 않은 상태에서 어떤 파일을 수정하면 Git은 그 파일을 Modified 상태로 인식한다.
```

#### 파일의 상태 확인하기

&#x20;파일의 상태를 확인하려면 보통 git status 명령을 사용한다.\
Clone 한 후에 바로 이 명령을 실행하면 아래와 같은 메시지를 볼 수 있다.

```bash
> git status
```

프로젝트에 README 파일을 만들어보자.\
README 파일은 새로 만든 파일이기 때문에 git status를 실행하면 Untracked files 에 들어 있다.

```
*바이너리파일(이진 파일)은 텍스트 파일이 아닌 컴퓨터 파일이다.
"바이너리 파일"이라는 용어는 종종 "논-텍스트 파일"을 의미하는 용어로 사용된다.
컴퓨터 파일로 컴퓨터 저장과 처리 목적을 위해 이진 형식으로 인코딩된 데이터를 포함한다 
```

README 파일을 추가해서 직접 Tracked 상태로 만들어보자.

```bash
> git add README
> git status On branch master
Your branch is up-to-date with 'origin/master'.
Changes to be committed:
(use "git reset HEAD <file>..." to unstage)
new file: README
```

git status 명령을 다시 실행하면 README 파일이 Tracked 상태이면서 커밋에 추가될 Staged 상태라는 것을 확인할 수 있다.&#x20;

"Changes to be committed"에 들어있는 파일은 Staged 상태라는 것을 의미한다.\
커밋하면 git add를 실행한 시점의 파일이 커밋되어 저장소 히스토리에 남는다.\
앞에서 git init 명령을 실행한 후, git add (files) 명령을 실행했던 걸 기억할 것이다.\
이 명령을 통해 디렉토리에 있는 파일을 추적하고 관리하도록 한다.\
git add 명령은 파일 또는 디렉토리의 경로를 argument로 받는다.\
디렉토리면 아래에 있는 모든 파일들까지 재귀적으로 추가한다.

&#x20;

이 CONTRIBUTING.md 파일은 Changes not staged for commit 에 있다.\
이것은 수정한 파일이 Tracked 상태이지만 아직 Stage 상태는 아니라는 것이다. \
Staged 상태로 만들려면 git add 명령을 실행해야 한다.\
git add 명령은 파일을 새로 추적할 때도 사용하고 수정한 파일을 Staged 상태로 만들 때도 사용한다.\
Merge할 때 충돌난 상태의 파일을 Resolve 상태로 만들 때도 사용한다.

git add 명령을 실행하면 Git은 바로 파일을 Staged 상태로 만든다.\
지금 이 시점에서 커밋을 하면 git commit 명령을 실행하는 시점의 버전이 커밋되는 것이 아니라\
마지막으로 git add 명령을 실행했을 때의 버전이 커밋된다.\
그러니까 git add 명령을 실행한 후에 또 파일을 수정하면 git add 명령을 다시 실행해서 최신 버전을 Staged 상태로 만들어야 한다.

```bash
> git status -s
   M README
MM Rakefile
A lib/git.rb
M lib/simplegit.rb
?? LICENSE.txt
```

아직 추적하지 않는 새 파일 앞에는 ?? 표시가 붙는다.\
Staged 상태로 추가한 파일 중 새로 생성한 파일 앞에는 A 표시가, 수정한 파일 앞에는 M 표시가 붙는다.\
위 명령의 결과에서 상태정보 컬럼에는 두 가지 정보를 보여준다.\
왼쪽에는 Staging Area 에서의 상태를, 오른쪽에는 Working Tree에서의 상태를 표시한다. \
README 파일 같은 경우 내용을 변경했지만 아직 Staged 상태로 추가하지는 않았다. &#x20;

#### 파일 무시하기

어떤 파일은 Git이 관리할 필요가 없다. 보통 로그파일이나 빌드 시스템이 자동으로 생성한 파일이 그렇다.\
그런 파일을 무시하려면 .gitignore 파일을 만들고 그 안에 무시한 파일 패턴을 적는다.\
아래는 .gitignore 파일의 예이다.

```bash
> cat .gitignore
*.[oa]
*~
```

&#x20;첫번째 라인은 확장자가 ".o"나 ".a"인 파일을 Git이 무시하라는 것이고\
둘째 라인은 \~로 끝나는 모든 파일을 무시하라는 것이다.\
보통 대부분의 텍스트 편집기에서 임시파일로 사용하는 파일 이름이기 때문이다.&#x20;

.gitignore 파일에 입력하는 패턴은 아래 규칙을 따른다.

```
- 아무것도 없는 라인이나, '#'로 시작하는 라인은 무시한다.
- 표준 Glob 패턴을 사용한다.
- 슬래시(/)로 시작하면 하위 디렉토리에 적용되지(Recursivity) 않는다.
- 디렉토리는 슬래시(/)를 끝에 사용하는 것으로 표현한다.
- (!)느낌표로 시작하는 패턴의 파일은 무시하지 않는다. 
```
