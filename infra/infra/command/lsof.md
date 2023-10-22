# lsof

lsof 는 **l**i**s**t **o**pen **f**iles 의 약자로 시스템에서 열린 파일 목록을 알려주고 사용하는 프로세스, 디바이스 정보, 파일의 종류등 상세한 정보를 출력해 준다.

리눅스와 유닉스는 추상화된 파일 시스템(VFS - Virtual File System)을 사용하므로 일반 파일, 디렉터리, 네트워크 소켓, 라이브러리, 심볼릭 링크 등도 모두 파일로 처리되며 lsof 에서 상세한 정보를 확인할 수 있다.

유닉스마다 고유의 lsof 와 비슷한 용도의 명령어가 있지만 명령어와 옵션이 제각각이고 출력 정보가 상이하여 OS 가 바뀌면 사용하기가 힘들다.

## Command

| Column   | Description |
| -------- | ----------- |
| COMMAND  | 실행한 명령어     |
| PID      | process id  |
| USER     | 실행한 사용자     |
| FD       | 파일의 종류      |
| TYPE     | 파일 종류       |
| DEVICE   | 장치 번호       |
| SIZE/OFF | 파일의 크기나 오프셋 |
| NODE     | 노드 번호       |
| NAME     | 파일명         |

### Default

```bash
> lsof
COMMAND PID TID TASKCMD        USER   FD      TYPE DEVICE SIZE/OFF  NODE NAME
init      1                    root  cwd   unknown                       /proc/1/cwd (readlink: Permission denied)
init      1                    root  rtd   unknown                       /proc/1/root (readlink: Permission denied)
init      1                    root  txt   unknown                       /proc/1/exe (readlink: Permission denied)
```

### User

사용자를 지정하여 가져온다.

```bash
> lsof -u root
COMMAND PID USER   FD      TYPE DEVICE SIZE/OFF NODE NAME
init      1 root  cwd   unknown                      /proc/1/cwd (readlink: Permission denied)
init      1 root  rtd   unknown                      /proc/1/root (readlink: Permission denied)
init      1 root  txt   unknown                      /proc/1/exe (readlink: Permission denied)
init      1 root NOFD                                /proc/1/fd (opendir: Permission denied)
init     17 root  cwd   unknown                      /proc/17/cwd (readlink: Permission denied)
init     17 root  rtd   unknown                      /proc/17/root (readlink: Permission denied)
init     17 root  txt   unknown                      /proc/17/exe (readlink: Permission denied)
```

### Port <a href="#lsof-port" id="lsof-port"></a>

포트를 지정하여 가져온다

```bash
> lsof -i TCP:22
 
COMMAND  PID    USER   FD   TYPE DEVICE SIZE/OFF NODE NAME
sshd    1550    root    3u  IPv4  13931      0t0  TCP *:ssh (LISTEN)
sshd    1550    root    4u  IPv6  13933      0t0  TCP *:ssh (LISTEN)
```
