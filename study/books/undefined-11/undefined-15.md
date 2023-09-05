# 타입 파라미터의 섀도잉을 피하라

```
class Forest(val name: String) {
    fun addTree(name: String){  }
}
```

* 위의 코드 처럼 프로퍼티와 파라미터가 같은 이름을 가질 수 있다.
* 이렇게 되면 지역 파라미터가 외부 스코프에 있는 프로퍼티를 가림. 이를 섀도잉 이라 부른다.

섀도잉 현상은 클래스 타입 파라미터와 함수 타입 파라미터 사이에서도 발생

```
interface Tree
class Birch: Tree
class Spruce: Tree

class Forest<T: Tree> {
    fun <T: Tree> addTree(tree: T) {
        println("adding tree ...")
    }
}

fun main() {
    val forest = Forest<Birch>()
    forest.addTree(Birch())
    forest.addTree(Spruce()) //정상동작...
}

-----------------------------------------

class Forest<T: Tree> {
    fun addTree(tree: T) {
        println("adding tree ...")
    }
}

fun main() {
    val forest = Forest<Birch>()
    forest.addTree(Birch())
    forest.addTree(Spruce()) // error
}
```

만약 독릭적인 타입 파라미터를 의도했다면, 타입 파라미터의 이름을 다르게 하는 것이 좋다.

```
class Forest<T: Tree> {
    fun <ST: Tree> addTree(tree: ST) {
        println("adding tree ...")
    }
    
    // 다른 타입 파라미터에 제한을 준 경우
    fun <ST: T> addTypeTree(tree: ST) {
        println("adding tree ...")
    }
}
```
