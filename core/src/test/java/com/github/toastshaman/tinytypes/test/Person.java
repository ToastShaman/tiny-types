package com.github.toastshaman.tinytypes.test;

import java.util.List;
import java.util.Objects;

public class Person {
    public final Firstname firstname;
    public final Lastname lastname;
    public final Age age;
    public final List<Hobby> hobbies;
    public final Timestamp timestamp;

    public Person(Firstname firstname,
                  Lastname lastname,
                  Age age,
                  List<Hobby> hobbies,
                  Timestamp timestamp) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.age = age;
        this.hobbies = hobbies;
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(firstname, person.firstname) && Objects.equals(lastname, person.lastname) && Objects.equals(age, person.age) && Objects.equals(hobbies, person.hobbies) && Objects.equals(timestamp, person.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstname, lastname, age, hobbies, timestamp);
    }
}
