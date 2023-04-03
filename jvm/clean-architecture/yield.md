# Yield

Java 13에서 도입된 switch 표현식은 기존의 switch 문보다 코드를 간결하고 가독성 높게 작성할 수 있도록 도와줍니다. 이전에는 switch 문을 사용하여 여러 분기마다 각각 코드 블록을 작성해야 했지만, 이제 switch 표현식에서는 각 분기마다 단일 표현식만으로 값을 반환할 수 있습니다.

이때, 각 분기에서 값을 반환하려면 yield 문을 사용합니다. yield 문은 해당 분기의 실행을 중단하고 값을 반환합니다. 이를 통해 switch 표현식에서는 분기마다 단일 값을 반환하며, 코드의 중복을 줄이고 간결한 코드를 작성할 수 있습니다.

예를 들어, 아래의 코드는 Java 12 이전 버전에서 사용할 수 있는 switch 문의 예입니다.

```java
switch (day) {
    case 1:
        System.out.println("Monday");
        break;
    case 2:
        System.out.println("Tuesday");
        break;
    case 3:
        System.out.println("Wednesday");
        break;
    case 4:
        System.out.println("Thursday");
        break;
    case 5:
        System.out.println("Friday");
        break;
    default:
        System.out.println("Weekend");
        break;
}
```

하지만 Java 13에서는 switch 표현식이 도입되면서, yield 문을 사용하여 각 분기에서 값을 반환할 수 있습니다.

```java
String dayOfWeek = switch (day) {
    case 1 -> "Monday";
    case 2 -> "Tuesday";
    case 3 -> "Wednesday";
    case 4 -> "Thursday";
    case 5 -> "Friday";aa
    default -> "Weekend";
};

System.out.println(dayOfWeek);
```

```java
String dayOfWeek = switch (day) {
    case 1 :
        yield "Monday";
    case 2 :
        yield  "Tuesday";
    case 3 :
        yield  "Wednesday";
...
```

위의 예제에서, 각 케이스는 단일 표현식으로 표현되며, `->` 연산자를 사용하여 해당 표현식이 yield 되도록 표시됩니다. 이렇게하면 각 분기에서 표현식이 실행되고 해당 값을 dayOfWeek 변수에 할당합니다.

즉, yield 문을 사용하여 switch 표현식에서 각 분기에서 값을 반환하면서 코드를 더욱 간결하고 가독성 높게 작성할 수 있습니다. 또한 yield 문을 사용하면 switch 표현식의 분기에서 중복 코드를 줄일 수 있어서 유지 보수 및 코드 개선에도 큰 도움이 됩니다.a
