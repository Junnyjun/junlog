# Resources



리눅스에서 메모리 사용량을 확인하는 방법이다.

```bash
$ free -m
              total        used        free      shared  buff/cache   available
Mem:          11940        4961         167           6        6811        6778
Swap:             0           0           0
```

사용이 가능한 메모리는 free + buffers + cached



상세 정보

```bash
$ sudo apt install sysstat
$ sar -r 
_aarch64_       (2 CPU)

10시 34분 11초 kbmemfree   kbavail kbmemused  %memused kbbuffers  kbcached  kbcommit   %commit  kbactive   kbinact   kbdirty
10시 34분 12초    198308   6899236   5005832     40.94    237212   6313544  10514948     86.00   1967432   9447804     43208
10시 34분 13초    198308   6899236   5005832     40.94    237212   6313544  10515208     86.00   1967432   9448120     42020
10시 34분 14초    198308   6899144   5005840     40.94    237212   6313544  10514720     86.00   1967432   9447640     42080
10시 34분 15초    197816   6898784   5006332     40.95    237212   6313544  10514900     86.00   1967432   9447884     42080
10시 34분 16초    197564   6898464   5006576     40.95    237212   6313552  10515168     86.00   1967432   9448056     42100
10시 34분 17초    197564   6898464   5006576     40.95    237212   6313552  10516208     86.01   1967432   9449088     42100

Average:       197978   6898888   5006165     40.94    237212   6313547  10515192     86.00   1967432   9448099     42265
```

```bash
$ free -k; cat /proc/meminfo | grep Mem; top -n1 | grep "KiB Mem"; vmstat -s | grep memory;
              total        used        free      shared  buff/cache   available
Mem:       12226856     5123808      196236        6816     6906812     6897148
Swap:             0           0           0
MemTotal:       12226856 kB
MemFree:          196236 kB
MemAvailable:    6897148 kB
     12226856 K total memory
      5123660 K used memory
      1967524 K active memory
      9447296 K inactive memory
       196236 K free memory
       237228 K buffer memory

```

```bash
$ dstat --time --cpu --mem --net --disk
----system---- --total-cpu-usage-- ------memory-usage----- -net/total- -dsk/total-
     time     |usr sys idl wai stl| used  free  buff  cach| recv  send| read  writ
23-03 10:38:18|  1   1  97   0   0|5015M  185M  232M 6167M|   0     0 |  68k   51k
23-03 10:38:19|  1   1  98   0   0|5016M  185M  232M 6167M|1208B 2010B|   0     0
23-03 10:38:20|  1   2  96   1   0|5016M  185M  232M 6167M|1612B 1994B|   0   192k
23-03 10:38:21|  1   2  98   0   0|5016M  185M  232M 6167M|1040B 1552B|   0     0
23-03 10:38:22|  1   2  97   0   0|5016M  185M  232M 6167M| 666B  506B|   0     0
23-03 10:38:23|  1   2  97   0   0|5016M  185M  232M 6167M| 890B 1450B|   0     0
23-03 10:38:24|  1   2  97   0   0|5016M  185M  232M 6167M|1096B 1570B|   0     0 

```



#### 기타 유용한 명령어

```bash
$ top
$ ps 
$ df -h
```

#### &#x20;Modern

```bash
> snap install bottom
> bottom
```
