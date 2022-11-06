---
description: 메모리 관리의 복잡성을 해결하는 방법
---

# 메모리 관리

## 메모리 관리의 복잡성

메모리 구조는 1B 크기로 나눈다.&#x20;

보통 나눠진 영역은 메모리 주소로 구분하는데 보통 0번지 부터 시작된다.&#x20;

CPU는 메모리에 있는 내용을 가져오거나 작업 결과를 메모리에 저장하기 위해 메모리 주소 레지스터를 사용한다.&#x20;

메모리 주소 레지스터에 필요한 메모리 주소를 넣으면 데이터를 메모리에서 가져오거나 메모리에 데이터를 옮길 수 있다

### 소스코드의 번역과 실행

보통 컴파일러를 사용하여 작성한 프로그램을 실행 가능한 코드로 변경한다.

기계어와 어셈블리어는 컴퓨터의 동작을 가장 직접적으로 표현한 언어로 저급 언어라고 한다

저급언어와 반대되는 이해하기 쉬운 언어는 C언어와 자바가 있다
