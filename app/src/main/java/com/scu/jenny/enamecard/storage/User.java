package com.scu.jenny.enamecard.storage;

/**
 * Created by jenny on 3/9/16.
 */
public class User {
    public User(String firstName, String lastName, String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public final String firstName;
    public final String lastName;
    public final String phoneNumber;
//    public String id;
}
