# 컬렉션 처리 단계 수를 제한하라

**컬렉션의 비용**

모든 컬렉션 처리 메서드는 비용이 많이 듭니다.

내부적으로 요소들을 활용해 반복을 돌며, 추가적인 컬렉션을 만들어 사용하기도 합니다.

시퀀스도 시퀀스 전체를 랩 하는 객체가 만들어지며, 조작을 위해 또 다른 추가적인 객체를 만들어냅니다.

&#x20;

**컬렉션의 처리의 단계 수**

컬렉션과 시퀀스의 처리 수가 많다면 꽤 큰 비용이 들어갑니다.

어떤 메서드를 사용하는지에 따라서 컬렉션 처리의 단계 수가 달라집니다.

따라서 적절한 메서드를 활용해서 컬렉션 처리 단계 수를 적절하게 제한하는 것이 좋습니다.

&#x20;

| 이 코드보다는                                                                                                 | 이 코드가 좋습니다.                                                             |
| ------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------- |
| <p>.filter { it != null }<br>.map { it }</p>                                                            | .filterNotNull()                                                        |
| <p>.map { Transformation }<br>.filterNotNull()</p>                                                      | .mapNotNull { Transformation }                                          |
| <p>.map { Transformation }<br>.joinToString()</p>                                                       | .joinToString { Transformation }                                        |
| <p>.filter { Predicate1 }<br>.filter{ Predicate2 }</p>                                                  | .filter { Predicate1 && Predicate2 }                                    |
| <p>.filter { it is Type }<br>.map { it as Type }</p>                                                    | .filterIsInstance()                                                     |
| <p>.sortedBy { Key2 }<br>.sortedBy { Key1 }</p>                                                         | <p>.sortedWith(<br>compareBy({ Key1 }, { Key2 })<br>)</p>               |
| <p>.listOf(…)<br>.filterNotNull()</p>                                                                   | .listOfNotNull(…)                                                       |
| <p>.withIndex()<br>.filter { (index, element) -><br>Predicate using index<br>}<br>.map { it.value }</p> | <p>.filterIndexed { index, element -><br>Predicate using index<br>}</p> |
