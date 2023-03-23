# Swap

처리할 사이즈가 커지면 종종 메모리문제를 뱉곤한다.

```
OpenJDK 64-Bit Server VM warning: INFO: os::commit_memory(0x00000000e7380000, 17825792, 0) failed; error='Cannot allocate memory' (errno=12)
#
# There is insufficient memory for the Java Runtime Environment to continue.
# Native memory allocation (mmap) failed to map 17825792 bytes for committing reserved memory.
# An error report file with more information is saved as:
# /root/hs_err_pid****.log
```

JVM 로그를 확인해본다.

```
processor       : **
vendor_id       : **
... ...
cache_alignment : 64
address sizes   : 43 bits physical, 48 bits virtual

... ...

Memory: 4k page, physical 8006856k(115112k free), swap 0k(0k free)

... ... 

```

가용 메모리 크기를 현재는 늘려줄 수 없으니, 스왑 메모리를 잡아 임시방편으로 처리하려 한다



기동시 메모리 상태 및 Swap 상태를 확인해줍니다

```bash
[junny@batch home] free -m
              total        used        free      shared  buff/cache   available
Mem:           7819        6447         993          10         378        5085
Swap:             0           0           0

[junny@batch home] swapon -s
-
```



빈 용량 파일을 생성하여 스왑 사이즈를 설정합니다.\
여기서는 2048 \* 2MB ( 4GB) 를 지정해 주었습니다.

```bash
[junny@batch home] dd if=/dev/zero of=/swapfile count=2048 bs=2M
2048+0 records in
2048+0 records out
4294967296 bytes (4.3 GB) copied, 19.6152 s, 219 MB/s
```

권한 및 Swap 설정해줍니다.

```bash
[junny@batch home] chmod 600 /swapfile
[junny@batch home] mkswap /swapfile
Setting up swapspace version 1, size = 4194300 KiB
no label, UUID=218218ad-63b4-40be-8d71-c52a8ddcbf72

[junny@batch home] swapon /swapfile
[junny@batch home] echo "/swapfile swap swap defaults 0 0" >> /etc/fstab
```



설정이 잘되었는지 확인해 줍니다.

```bash
[junny@batch home] free -m
              total        used        free      shared  buff/cache   available
Mem:           7819        5244         189          10        2385        2251
Swap:          4095         100        3995
[junny@batch home] swapon -s
Filename                                Type            Size    Used    Priority
/swapfile                               file    4194300 102656  -2
```
