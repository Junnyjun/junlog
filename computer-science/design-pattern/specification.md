# Specification

Specification 패턴은 도메인 객체에 대한 조건이나 규칙을 캡슐화하여 재사용 및 조합 가능한 형태로 표현하는 디자인 패턴입니다.\
즉, 비즈니스 규칙이나 조건을 별도의 객체(스펙)로 분리하여, 여러 곳에서 재사용하거나 복합적인 조건을 구성할 수 있게 해줍니다.

***

### How do Code❔

{% tabs %}
{% tab title="JAVA" %}
```java
public interface Specification<T> {
    boolean isSatisfiedBy(T candidate);

    default Specification<T> and(Specification<T> other) {
        return candidate -> this.isSatisfiedBy(candidate) && other.isSatisfiedBy(candidate);
    }

    default Specification<T> or(Specification<T> other) {
        return candidate -> this.isSatisfiedBy(candidate) || other.isSatisfiedBy(candidate);
    }

    default Specification<T> not() {
        return candidate -> !this.isSatisfiedBy(candidate);
    }
}

public class Product {
    private String name;
    private double price;
    private boolean inStock;

    public Product(String name, double price, boolean inStock) {
        this.name = name;
        this.price = price;
        this.inStock = inStock;
    }

    // Getter 메서드들
    public String getName() { return name; }
    public double getPrice() { return price; }
    public boolean isInStock() { return inStock; }
}

public class PriceSpecification implements Specification<Product> {
    private double minPrice;
    private double maxPrice;

    public PriceSpecification(double minPrice, double maxPrice) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    @Override
    public boolean isSatisfiedBy(Product candidate) {
        return candidate.getPrice() >= minPrice && candidate.getPrice() <= maxPrice;
    }
}
```
{% endtab %}

{% tab title="KOTLIN" %}
```kotlin
interface Specification<T> {
    fun isSatisfiedBy(candidate: T): Boolean

    infix fun and(other: Specification<T>): Specification<T> = object : Specification<T> {
        override fun isSatisfiedBy(candidate: T): Boolean =
            this@Specification.isSatisfiedBy(candidate) && other.isSatisfiedBy(candidate)
    }

    infix fun or(other: Specification<T>): Specification<T> = object : Specification<T> {
        override fun isSatisfiedBy(candidate: T): Boolean =
            this@Specification.isSatisfiedBy(candidate) || other.isSatisfiedBy(candidate)
    }

    operator fun not(): Specification<T> = object : Specification<T> {
        override fun isSatisfiedBy(candidate: T): Boolean =
            !this@Specification.isSatisfiedBy(candidate)
    }
}
// Product.kt
data class Product(
    val name: String,
    val price: Double,
    val inStock: Boolean
)

class PriceSpecification(private val minPrice: Double, private val maxPrice: Double) : Specification<Product> {
    override fun isSatisfiedBy(candidate: Product): Boolean {
        return candidate.price in minPrice..maxPrice
    }
}
```
{% endtab %}
{% endtabs %}
