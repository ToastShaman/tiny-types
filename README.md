# tiny-types

Java library for creating tiny/micro types with validation.

The idea is fairly simple - all primitives and strings in our code are wrapped by a class, meaning that we never pass
primitives around.

Replacing primitive types with informative domain tiny types brings four main benefits:

* **Domain intention**: The tiny type tells the developer what the item means in the domain.
* **Compiler information**: The compiler can perform stronger and more relevant type-checking.
* **Consolidated validation**: Validation is moved to the constructor/factory, so instances are always valid.
* **Immutable value objects**: Values cannot be changed after construction, and thus are thread-safe.

## Usage

```java
import java.util.UUID;

public final class FixtureId extends UUIDValue {
  public FixtureId(UUID value) {
    super(value);
  }

  public static FixtureId of(String value) {
    return new FixtureId(UUID.fromString(value));
  }
}
```

```java
public final class IsoCountryCode extends NonBlankStringValue {
  public IsoCountryCode(String value) {
    super(value, Validator.MaxLength(3)); // with added validation
  }
}
```

```java
public final class MySecret extends NonBlankSecret {
  public MySecret(String secret) {
    super(secret);
  }
}

public static class Main {
  public static void main(String[] args) {
    System.out.println(new MySecret("my password")); // will print "********"
  }
}
```

```java
public final class Firstname extends NonBlankString {
  public Firstname(String value) {
    super(value, Validator.MaxLength(255));
  }
}

public final class Age extends IntegerValue {
  public Age(Integer value) {
    super(value);
  }
}

public final class MyJsonModule extends ValueTypeModule {
  public MyJsonModule() {
    text(Firstname.class, Firstname::new);
    integer(Age.class, Age::new);
  }
}

public record Person(Firstname firstname, Age age) {
  
}

public final class Main {
  public static void main(String[] args) {
    var mapper = new ObjectMapper().registerModule(new MyJsonModule());
    var person = new Person(new Firstname("Dave"), new Age("49"));
    var json = mapper.writer().writeValueAsString(person);
    
    System.out.println(json); // {firstname: 'Dave', age: 49}
  }
}
```
