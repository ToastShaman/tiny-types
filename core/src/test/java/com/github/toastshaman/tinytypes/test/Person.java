package com.github.toastshaman.tinytypes.test;

import java.util.List;
import java.util.Objects;

public class Person {
    public final Firstname firstname;
    public final Lastname lastname;
    public final Age age;
    public final List<Hobby> hobbies;
    public final Timestamp timestamp;
    public final Pin pin;

    public Person(Firstname firstname,
                  Lastname lastname,
                  Age age,
                  List<Hobby> hobbies,
                  Timestamp timestamp,
                  Pin pin) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.age = age;
        this.hobbies = hobbies;
        this.timestamp = timestamp;
        this.pin = pin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(firstname, person.firstname) && Objects.equals(lastname, person.lastname) && Objects.equals(age, person.age) && Objects.equals(hobbies, person.hobbies) && Objects.equals(timestamp, person.timestamp) && Objects.equals(pin, person.pin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstname, lastname, age, hobbies, timestamp, pin);
    }

    @Override
    public String toString() {
        return "Person{" +
                "firstname=" + firstname +
                ", lastname=" + lastname +
                ", age=" + age +
                ", hobbies=" + hobbies +
                ", timestamp=" + timestamp +
                ", pin=" + pin +
                '}';
    }
}
