package com.filip.springbootblog.jpa.utils;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Random;

public class SharedUtils {

    public static Long randomNegativeId() {
        Random rand = new Random();
        return -1 * ((long) rand.nextInt(1000));
    }


    public static long timeMark() {
        return new Date().getTime();
    }

    public static String totalTime(long lStartTime, long lEndTime) {
        long duration = lEndTime - lStartTime;
        String totalTime = String.format("Milliseconds: %d", duration);
        return totalTime;
    }

}
