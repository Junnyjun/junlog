# No child processes

linux에서 process 를 많이 열어두는 서버 ( apm,크롤링 서버 등등 )은 종종 nproc에러가 발생하곤 한다.\
max user process 기본 값인 1024 를 초과한 경우인데, 프로세스의 수를 늘려 해결할 수 있다.

### ulimit 은 무엇인가요?

ulimit는 프로세스의 자원 한도를 설정하는 명령

```
- ulimit [옵션] 값
-a : 모든 제한 사항들을 보여준다.
-c : 최대 코어 파일 사이즈
-d : 프로세스 데이터 세그먼트의 최대 크기
-f : shell에 의해 만들어질 수 있는 파일의 최대 크기
-s : 최대 스택 크기
-p : 파이프 크기
-n : 오픈 파일의 최대수
-u : 오픈파일의 최대수
-v : 최대 가상메모리의 양
-S : soft 한도
-H : hard 한도
```

\# ulimit -a         => Soft 설정\
\# ulimit -aH        =>  Hard 설정

### 세팅값 확인

```bash
> ulimit -a
core file size          (blocks, -c) 0          |   => 코어파일의 최대크기
data seg size           (kbytes, -d) unlimited  |   =>  프로세스의 데이터 세그먼트 최대크기
scheduling priority             (-e) 0
file size               (blocks, -f) unlimited  |   => 쉘에서 생성되는 파일일 최대크기
pending signals                 (-i) 31164
max locked memory       (kbytes, -l) 64
max memory size         (kbytes, -m) unlimited  |   => resident set size의 최대 크기(메모리 최대크기)
open files                      (-n) 1024       |   => 한 프로세스에서 열 수 있는 open file descriptor의 최대 숫자
pipe size            (512 bytes, -p) 8          |   => 512-바이트 블럭의 파이프 크기
POSIX message queues     (bytes, -q) 819200
real-time priority              (-r) 0
stack size              (kbytes, -s) 8192
cpu time               (seconds, -t) unlimited  |   => 총 누적된 CPU 시간(초)
max user processes              (-u) 1024
virtual memory          (kbytes, -v) unlimited  |   => 쉘에서 사용가능 한 가상 메모리의 최대 용량
file locks                      (-x) unlimited

```

### 세팅값 변경

```bash
# 변경
> ulimit -u 4096
# 확
> ulimit -a
max user processes              (-u) 1024
```
