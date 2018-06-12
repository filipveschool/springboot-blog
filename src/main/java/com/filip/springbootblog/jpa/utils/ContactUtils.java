package com.filip.springbootblog.jpa.utils;

import com.filip.springbootblog.jpa.models.User;
import com.filip.springbootblog.jpa.models.UserConnection;

import java.util.Collection;
import java.util.Random;

public class ContactUtils {

    // region Properties

    public static void printProperty(String header, String property) {
        System.out.println("\r\n" + header + " ------------------------------ ");
        System.out.println(property);
    }

    // endregion

    // region Users

    public static void listUsersWithDetail(Collection<User> users) {
        System.out.println("LISTING ENTITIES WITH DETAILS ---------------------------------");
        System.out.println();
        for (User user : users) {
            System.out.println(user);
            if (user.getAuthorities() != null) {
                user.getAuthorities().forEach(System.out::println);
            }
            // if (user.getUserProfile() != null) {
            // System.out.println(user.getUserProfile());
            // }
            System.out.println();
        }
    }

    public static void listUser(String header, User user) {
        System.out.println("\r\n" + header + " ------------------------------ ");
        System.out.println();
        System.out.println(user);
        System.out.println();
    }

    public static void listUserConnection(String header, UserConnection user) {
        System.out.println("\r\n" + header + " ------------------------------ ");
        System.out.println();
        System.out.println(user);
        System.out.println();
    }

    // endregion


    // region Random IDs

    public static Long randomContactId() {
        Random rand = new Random();
        return (long) (rand.nextInt(10) + 1);
    }

    // endregion
}
