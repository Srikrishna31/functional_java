package com.io;

class Person {
    private static final String FORMAT =
            "ID: %s, First name: %s, Last name: %s";
    public final int id;
    public final String firstName;
    public final String lastName;

    private Person(int id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public static Person apply(int id, String firstName, String lastName) {
        return new Person(id, firstName, lastName);
    }

    @Override
    public String toString() {
        return String.format(FORMAT, id, firstName, lastName);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Person)) {
            return false;
        }
        var that = (Person)obj;

        return that.id == id && that.firstName.equals(firstName) && that.lastName.equals(lastName);
    }

    @Override
    public int hashCode() {
        return firstName.hashCode();
    }
}
