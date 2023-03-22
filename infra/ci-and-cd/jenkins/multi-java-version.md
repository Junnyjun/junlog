# Multi Java version

jenkins 관리 - Global Tool Configuration - JDK에 `JDK installations` 버튼을 눌러 활성화 시킨다

ADD JDK 후 Installer를 Extract \*.zip/\*.tar.gz로 변경해준뒤 아래와 같이 설정해준다.\
※ 아래는 Linux 예시

{% code title="JAVA 8" overflow="wrap" %}
```
NAME : JAVA_8
Label : JAVA_8
Download URL for binary archive : https://github.com/adoptium/temurin8-binaries/releases/download/jdk8u362-b09/OpenJDK8U-jdk_x64_linux_hotspot_8u362b09.tar.gz
Subdirectory of extracted archive : jdk8u362-b09
```
{% endcode %}

{% code title="JAVA 11" overflow="wrap" %}
```
NAME : JAVA_11
Label : JAVA_11
Download URL for binary archive : https://github.com/adoptium/temurin11-binaries/releases/download/jdk-11.0.18%2B10/OpenJDK11U-jdk_x64_linux_hotspot_11.0.18_10.tar.gz
Subdirectory of extracted archive : jdk-11.0.18+10
```
{% endcode %}

{% code title="JAVA 17 " overflow="wrap" %}
```
NAME : JAVA_17
Label : JAVA_17
Download URL for binary archive : https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.6%2B10/OpenJDK17U-jdk_x64_linux_hotspot_17.0.6_10.tar.gz
Subdirectory of extracted archive : jdk-17.0.6+10
```
{% endcode %}

본인의 OS환경이 조금 다르다면 [https://adoptium.net/temurin/releases/](https://adoptium.net/temurin/releases/) 에서 환경을 맞춰주면 된다



저장 후 Item의 구성에 들어가면, JDK 설정이 나오는걸 볼 수 있다.
