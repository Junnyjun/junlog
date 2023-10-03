# Binary Search

이진 탐색이란 데이터가 정렬돼 있는 배열에서 특정한 값을 찾아내는 알고리즘이다.&#x20;

배열의 중간에 있는 임의의 값을 선택하여 찾고자 하는 값 X와 비교한다. \
X가 중간 값보다 작으면 중간 값을 기준으로 좌측의 데이터들을 대상으로, X가 중간값보다 크면 배열의 우측을 대상으로 다시 탐색한다.&#x20;

동일한 방법으로 다시 중간의 값을 임의로 선택하고 비교한다. 해당 값을 찾을 때까지 이 과정을 반복한다.

<img src="../../.gitbook/assets/file.excalidraw (14).svg" alt="" class="gitbook-drawing">

## How do code?

{% embed url="https://gist.github.com/Junnyjun/74ce11cb9412d9f10fc44262d6070e60" %}
