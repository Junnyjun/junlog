# variable

### let

기본적으로 변수는 let 으로 선언하며 불변이며, mut으로 가변으로 선언할 수 있습니다

```rust
fn main() {
    let junny = 1;
    let mut mutableJunny = 2;
    
    junny = 4; // error: cannot assign twice to immutable variable `junny`
    mutableJunny = 4;// ok
}
```
