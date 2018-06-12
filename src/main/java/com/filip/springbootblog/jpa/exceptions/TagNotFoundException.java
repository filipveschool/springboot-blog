package com.filip.springbootblog.jpa.exceptions;

/**
 * Created by daveburke on 6/15/16.
 */
public class TagNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -9070542392426394574L;
    private String msg;

    public TagNotFoundException() {
        super();
    }

    public TagNotFoundException(String msg) {
        this.msg = System.currentTimeMillis()
                + ": " + msg;
    }

    public String getMsg() {
        return msg;
    }


}
