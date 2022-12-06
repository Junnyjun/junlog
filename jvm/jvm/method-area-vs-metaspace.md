# Method area vs Metaspace

JVM엔 Method aread와 Metaspace가 존재합니다.\
두 영역 전부 Class 정보를 가지고 있다고 설명이 되어있는데, 정확히 서로 어떻게 달라 분리하였는지 설명합니다.

### Method area

프로그램의 클래스 정보를 저장하는 영역 \
JVM은 자바 프로그램을 실행할 때 필요한 정보를 Method area에 로드하여 사용합니다.&#x20;

Method area 안에는 클래스의 이름, 메서드와 변수의 정보, 상수 풀 등이 저장됩니다. \
Method area는 프로그램 전체에서 공유되는 영역이므로, 모든 스레드가 공유하고 접근할 수 있습니다.

### Metaspace

Java Virtual Machine (JVM)의 는 클래스 메타데이터를 저장하는 메모리 영역\
JVM은 클래스를 로드할 때, 해당 클래스의 메타데이터를 metaspace에 저장합니다. \
메타데이터는 클래스의 이름, 메서드와 변수의 정보, 상수 풀 등을 포함합니다.

metaspace는 Method area와 달리 크기가 제한되어 있지 않습니다. \
이는 JVM이 클래스를 로드할 때 메모리를 동적으로 할당하기 때문입니다. \
따라서, metaspace는 Method area보다 더 많은 클래스 메타데이터를 저장할 수 있습니다.

metaspace의 용량은 JVM의 옵션에 의해 조절될 수 있으며, 만약 metaspace가 부족해지면 JVM은 자동으로 용량을 증설하거나 GC(Garbage Collection)을 수행하여 메모리를 정리합니다.

{% hint style="warning" %}
Java 8 이전의 JVM에서는 metaspace가 존재하지 않습니다. \
대신 Method area라는 메모리 영역을 사용합니다. Method area는 클래스 메타데이터를 저장하는 역할을 합니다. Method area는 프로그램 전체에서 공유되는 영역이므로, 모든 스레드가 접근할 수 있습니다.

Method area의 크기는 JVM의 옵션에 의해 조절될 수 있으며, 기본적으로 JVM은 메모리 절약을 위해 적은 용량의 Method area를 사용합니다. \
만약 Method area가 부족해지면 JVM은 OutOfMemoryError를 발생시키고 프로그램을 종료합니다.

Java 8 이후의 JVM에서는 Method area 대신 metaspace를 사용합니다. \
metaspace는 Method area와 달리 크기가 제한되어 있지 않으며, JVM이 클래스를 로드할 때 메모리를 동적으로 할당하기 때문입니다. \
따라서, metaspace는 Method area보다 더 많은 클래스 메타데이터를 저장할 수 있습니다.
{% endhint %}



