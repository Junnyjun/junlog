# 파티셔닝

동일한 데이터의 복사본 여러 개를 다른 노드에 저장하는 복제할 때 데이터셋이 매우 크거나 질의 처리량이 매우 높다면 복제만으로는 부족하고 데이터를 파티션으로 쪼갤 필요가 있다. 이를 샤딩이라 한다.

각 데이터 단위(레코드, 로우, 문서)가 하나의 파티션에 속하게 한다. 결과적으로 각 파티션은 그 자체로 작은 데이터베이스가 된다.

데이터 파티션을 원하는 주된 이유는 확장성. 비공유 클러스터에서 다른 파티션은 다른 노드에 저장될 수 있다. 따라서 대용량 데이터셋이 여러 디스크에 분산, 질의 부하는 여러 프로세서에 분산될 수 있다.

&#x20;

파티셔닝과 복제

보통 복제와 파티셔닝을 함께 적용해 각 파티션의 복사본을 여러 노드에 저장한다. 내결함성을 보장할 수 있다는 의미이다. 한 노드에 여러 파티션을 저장할 수 있고, 리더 팔로워 복제 모델을 사용하면 파티셔닝과 복제의 조합. 각 파티션의 리더는 하나의 노드에 할당되고 팔로워들은 다른 노드에 할당된다. 각 노드는 어떤 파티션에게는 리더이면서 다른 파티셔닝에는 팔로워가 될 수 있다.

키-값 데이터 파티셔닝

파티셔닝의 목적은 데이터와 질의 부하를 노드 사이에 고르게 분산시키는 것이다. 고르게 파티셔닝이 이뤄지지 않아 다른 파티션보다 데이터가 많거나 질의를 많이 받는 파티션은 쏠렸다(skewed)고 말한다. 이런 경우 병목이 될 수 있고, 부하가 높은 파티션을 핫스팟이라고 한다. 이를 회피하려면 레코드 할당 노드를 무작위로 선택하는 것이다. 또는 항상 기본키를 통해 레코드에 접근하는 방법이 있다.

&#x20;

키 범위 기준 파티셔닝은 범위 스캔이 매우 유용하다. 그러나 특정한 접근 패턴이 핫스팟을 유발한다. 타임스탬프가 키라면 파티션은 시간 범위에 대응되어서 1일치의 데이터를 파티션 하나가 담당하는 식이다. 쓰기 연산은 모두 동일한 파티션으로 전달되어 해당 파티션만 과부하가 걸리고 나머지 파티션은 유휴 상태로 남아 있을 수 있다. 그래서 첫 번째 요소로 타임스탬프가 아닌 다른 것을 사용해야 한다.

키의 해시 값 기준 파티셔닝은 쏠림과 핫스팟의 위험 때문에 많은 분산 데이터스토어는 키의 파티션을 정하는 데 해시 함수를 사용한다. 키에 적합한 해시 함수를 구했다면 각 파티션에 해시값 범위를 할당하고 해시값이 파티션의 범위에 속하는 모든 그 키를 그 파티션에 할당하면 된다. 이 기법은 키를 파티션 사이에 균일하게 분산시키는 데 좋다. 파티션 경계는 크기가 동일하도록 나눌 수도 있고 무작위에 가깝게 선택할 수도 있다.

그러나 파티셔닝에 키의 해시값을 사용해서 파티셔닝하면 키 범위 파티셔닝의 좋은 속성을 잃어버린다. 범위 질의를 효율적으로 실행할 수 있는 능력을 상실한다. 인접했던 키들이 이제는 모든 파티션에 흩어져서 정렬 순서가 유지되지 않는다.

&#x20;

쏠린 작업부하와 핫스팟 완화

핫스팟을 완벽히 제거할 수는 없다. 현대 데이터 시스템은 대부분 크게 쏠린 작업부하를 자동으로 보정하지 못하므로 애플리케이션에서 쏠림을 완화해야 한다. 요청이 매우 많이 쏠리는 키를 발견했을 때 각 키의 시작이나 끝에 임의의 숫자를 붙이는 방법을 사용하는 경우도 있다.

&#x20;

파티셔닝과 보조 색인

기존의 키-값 데이터 모델에서는 레코드를 기본키를 통해서만 접근한다면 보조 색인이 연관되면 상황은 복잡해진다. 솔라나 엘라스틱서치 같은 검색 서버에게는 존재의 이유다.

문서 기준 보조 색인 파티셔닝을 사용하면 각 파티션이 완전히 독립적으로 동작한다. 자신의 보조 색인을 유지하며 그 파티션에 속하는 문서만 담당한다. 지역 색인(local index)이라고도 한다. 문서 기준으로 파티셔닝된 색인을 써서 읽을 때는 주의를 기울여야 한다. 모든 파티션으로 질의를 보내서 얻은 결과를 모두 모아야 한다. 이런 식으로 질의를 보내는 방법을 스캐터/개더(scatter/gather)라고도 하는데 이런 질의는 큰 비용이 들 수 있다. 병렬 실행하더라도 꼬리 지연 시간 증폭(하나가 오래 걸리면 먼저 끝난 나머지는 오래 걸리는 프로세스가 끝날 때 까지 대기)이 발생하기 쉽다

용어 기준 보조 색인 파티셔닝. 모든 파티션의 데이터를 담당하는 전역 색인(global index)를 만들 수 있다. 이 색인도 파티셔닝 해야 하지만 기본키 색인과는 다른 식으로 할 수 있다. 이전처럼 색인을 파티셔닝 할 때 용어 자체를 쓸 수도 있고 용어의 해시값을 사용할 수도 있다. 용어 자체로 파티셔닝하면 범위 스캔에 유용한 반면 용어의 해시값을 사용해 파티셔닝하면 부하가 좀 더 고르게 분산된다. 문서 파티셔닝 색인에 비해 전역 색인이 같은 이점은 읽기가 효율적. 모든 파티션에 스캐터/개더를 실행할 필요 없이 원하는 용어를 포함하는 파티션으로만 요청을 보내면 된다. 그러나 쓰기가 느리고 복잡하다는 단점이 있다. 단일 문서를 쓸 때 해당 색인의 여러 파티션에 영향을 줄 수 있기 때문이다. 문서에 있는 모든 용어가 다른 노드에 있는 다른 파티션에 속할 수도 있다. 그래서 현실에서는 전역 보조 색인은 대개 비동기로 갱신된다.

&#x20;

파티션 재균형화

시간이 지나면

질의 처리량이 증가해서 늘어난 부하를 처리하기 위해 CPU를 더 추가

데이터셋 크기가 증가해서 디스크와 램을 추가,

장비에 장애가 발생해서 그 장비가 담당하던 역할을 다른 장비가 넘겨받아야 한다.

클러스터에서 한 노드가 담당하던 부하를 다른 노드로 옮기는 과정을 재균형화(rebalancing)라고 한다.

재균형화 후 최소 요구사항

부하가 클러스터 내에 있는 노드들 사이에 균등하게 분배돼야 한다. 도중에도 데이터베이스는 읽기 쓰기 요청을 받아들여야 한다. 네트워크와 디스크 I/O부하를 최소화할 수 있도록 노드들 사이에 데이터가 필요 이상으로 옮겨져서는 안 된다.

재균형화 전략

쓰면 안 되는 방법 : 해시값에 모듈러 연산을 실행 -> 키가 자주 이동하면 재균형화 비용이 지나치게 커진다. 데이터를 필요 이상으로 이동하지 않는 방법이 필요하다.

파티션 개수 고정

처음부터 파티션을 노드 대수보다 많이 만들고 각 노드에 여러 파티션을 할당하는 방법. 예를 들면 노드가 10개이면 처음부터 1000개로 나눠서 각 노드마다 100개씩 할당을 하고, 노드가 추가되면 파티션이 다시 균일하게 분배될 때까지 기존 노드에서 파티션 몇개를 뺏어올 수 있다. 이론상 성능이 좋은 노드에 파티션을 더 할당함으로써 더 많은 부하를 담당하게 할 수 있다. 처음 설정된 파티션 개수가 사용 가능한 노드 대수의 최대치가 되므로 미래에 증가될 것을 수용하기에 충분히 높은 값으로 선택해야 한다. 그래도 너무 큰 수를 선택하면 개별 파티션 관리 오버헤드가 발생하므로 잘 선택해야한다.

동적 파티셔닝

키 범위 파티셔닝을 사용하는 데이터베이스에서는 파티션 경계와 개수가 고정돼 있는 게 매우 불편하다. 잘못 지정하면 모든 데이터가 한 파티션에 지정되고 나머지 파티션은 텅 빌 수도 있다. (Skewed현상) 이런 이유로 키 범위 파티셔닝을 사용하는 DB에서는 파티션을 동적으로 만든다. 파티션 크기가 설정된 값을 넘어서면 두개로 쪼개 각각에 원래 파티션의 절반 정도의 데이터가 포함되게 하고, 데이터가 많이 삭제되어 파티션 크기가 임곗값 아래로 떨어지면 인접한 파티션과 합쳐질 수 있다.

처음에 파티션이 하나일 때의 문제.

노드 비례 파티셔닝

동적 파티셔닝에서는 파티션 분할과 병합을 통해 개별 파티션 크기가 어떤 고정된 최솟값과 최댓값 사이에 유지되게 하므로 파티션 개수가 데이터셋 크기에 비례한다. 반면 파티션 개수를 고정하면 개별 파티션의 크기가 데이터셋 크기에 비례한다. 두 경우 모두 파티션 개수는 노드 대수와 독립적이다. 노드당 할당되는 파티션 개수를 고정한다.

운영: 자동 재균형화와 수동 재균형화

완전 자동 재균형화는 일상적인 유지보수에 솔이 덜 가므로 편리할 수 있지만 예측하기 어렵기도 하다. 요청 경로를 재설정해야 하고 대량의 데이터를 노드 사이에 이동해야 하므로 비용이 큰 연산이다. 주의 깊게 처리하지 않으면 네트워크나 노드에 과부화가 걸릴 수 있고 재균형화가 진행 중인 동안에 실행되는 다른 요청의 성능이 저하될 수 있다. 이런 자동화는 자동 장애 감지와 조합되면 위험해질 수 있다.

이런 이유로 재균형화 과정에 사람이 개입하는 게 좋을 수 있다. 완전 자동 처리보다는 느릴 수 있지만 운영상 예상치 못한 일을 방지하는 데 도움될 수 있다.

&#x20;

요청 라우팅

클라이언트에서 요청을 보내려고 할 대 어느 노드로 접속해야 하는지 어떻게 결정하는가? 데이터베이스에 국한되지 않은 더욱 일반적인 서비스 찾기 문제의 일종이다. 고가용성(여러 장비에서 이중화 설정이 된 상태로 실행되는)을 지향하는 소프트웨어라면 모두 이 문제가 있다.

상위수준에서는 몇 가지 접근법이 있다.

1\. 클라이언트가 아무 노드에나 접속하게 한다.(라운드로빈 로드 밸런서를 통해) 요청을 적용할 파티션이 있다면 직접 처리하고 아니면 올바른 노드로 전달해서 클라이언트에 그 응답을 전달.

2\. 모든 요청을 라우팅 계층으로 먼저 보내고 각 요청을 처리할 노드를 알아내고 그에 따라 해당 노드로 요청을 전달한다. 라우팅 계층 자체에서는 아무 요청도 처리하지 않고, 파티션 인지 로드 밸런서로 동작한다.

3\. 클라이언트가 파티셔닝 방법과 파티션이 어떤 노드에 할당됐는지를 알고 있게 한다. 중재자 없이 올바른 노드로 직접 접속할 수 있다.

모든 경우에 라우팅 결정을 내리는 구성요소가 노드에 할당된 파티션의 변경 사항을 어떻게 확인하는지가 중요하다. 참여하는 모든 곳에서 정보가 일치해야 하므로 다루기 어렵다.

많은 분산 데이터 시스템은 클러스터 메타데이터를 추적하기 위해 주키퍼(ZooKeeper)같은 별도의 코디네이션 서비스를 사용한다. 각 노드는 여기에 자신을 등록하고 주키퍼는 파티션과 노드 사이의 신뢰성 있는 할당 정보를 관리한다. 다른 구성요소들은 주키퍼에 있는 정보를 구독할 수 있다.

&#x20;

병렬 질의 실행

지금까지 단일 키를 읽거나 쓰는 매우 간단한 질의였지만 분석용으로 자주 사용되는 대규모 병렬 처리(Massively parallel processing, MPP) 관계형 데이터베이스 제품은 훨씬 더 복잡한 종류의 질의를 지원한다. 전형적인 데이터 웨어하우스 질의는 조인, 필터링, 그룹화, 집계 연산을 포함한다. MPP 질의 최적화기는 복잡한 질의를 여러 실행 단계와 파티션으로 분해하며 이들 중 다수는 데이터베이스 클러스터 내의 서로 다른 노드에서 병결적으로 실행될 수 있다. 데이터셋의 많은 부분을 스캔하는 연산을 포함하는 질의는 특히 병렬 실행의 혜택을 받는다.

&#x20;

정리

저장하고 처리할 데이터가 너무 많아서 장비 한 대로 처리하는 게 불가능해지면 파티셔닝이 필요하다. 파티셔닝의 목적은 핫스팟이 생기지 않게 하면서 데이터와 질의 부하를 어려 장비에 균일하게 분배하는 것이다. 그렇게 하려면 데이터에 적합한 파티셔닝 방식을 선택해야 하고 클러스터에 노드가 추가되거나 제거될 때 파티션 재균형화를 실행해야 한다.

&#x20;

키 범위 파티셔닝 키가 정렬돼 있고 개별 파티션은 어떤 최솟값과 최댓값 사이에 속하는 모든 키를 담당한다. 키가 정렬돼 있어 범위 질의가 효율적이지만 정렬 순서가 서로 가까운 키에 자주 접근하면 핫스팟이 생길 위험이 있다. 한 파티션이 너무 커지면 키 범위를 두 개로 쪼개 동적으로 재균형화를 실행한다.

해시 파티셔닝 각 키에 해시 함수를 적용하고 개별 파티션은 특정 범위의 해시값을 담당한다. 키 순서가 보장되지 않아 범위 질의가 비효율적이지만 부하를 균일하게 분산할 수 있다. 보통 고정된 개수의 파티션을 미리 만들어 각 노드에 몇 개씩의 파티션을 할당하며 노드가 추가되거나 제거되면 파티션을 통째로 노드 사이에서 이동한다.

두 가지 방법을 섞어 쓸 수도 있다. 일부분은 파티션 식별용으로 나머지는 정렬 순서용으로 만든 복합 키를 사용.

&#x20;

문서 파티셔닝 색인(local index): 보조 색인을 기본키와 값이 저장된 파티션에 저장한다. 쓸 때는 파티션 하나만 갱신하면 되지만 보조 색인을 읽으려면 모든 파티션에 걸쳐서 스캐터/개더를 실행해야 한다.

용어 파티셔닝 색인(global index): 색인된 값을 사용해서 보조 색인을 별도로 파티셔닝 한다. 보조 색인 항목은 기본키의 모든 파티션에 있는 레코드를 포함할 수도 있다. 문서를 쓸 때는 보조 색인 여러 개를 갱신해야 하지만 읽기는 단일 파티션에서 실행될 수 있다.

&#x20;

설계상 모든 파티션은 대부분 독립적으로 동작한다. 여러 장비로 확장될 수 있다. 그러나 여러 파티션에 기록해야 하는 연산은 따져 보기 어려울 수 있다.