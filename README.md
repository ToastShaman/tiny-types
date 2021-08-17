# tiny-types

Java library for creating tiny/micro types with validation.

The idea is fairly simple - all primitives and strings in our code are wrapped by a class, meaning that we never pass
primitives around.

```java
public class Firstname extends NonBlankStringValue {
    public Firstname(String value) {
        super(value, Validator.MaxLength(60));
    }
}

public class Age extends IntValue {
    public Age(Integer value) {
        super(value, Validator.Min(1).and(Validator.Max(120)));
    }
}

public class Timestamp extends AbstractValueType<BigInteger> {
    public Timestamp(BigInteger value) {
        super(value, Validator.of(v -> v.signum() == 1, String.format("%s must be positive", value)));
    }
}
```

## JSON Support

```java
// Jackson
new ObjectMapper()
    .registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES))
    .registerModule(new ValueTypeModule());
```

```java
// Gson
new GsonBuilder()
    .registerTypeAdapterFactory(new ValueTypeAdapterFactory())
    .create();
```

```java
// Moshi
new Moshi.Builder()
    .add(new ValueTypeAdapterFactory())
    .build();
```
