package com.leading.demo2.exception;

public class SmsDemoException extends RuntimeException {
    private String code;
    private String message;

    public SmsDemoException(String code, String message, Throwable cause){
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    public SmsDemoException(String code, String message){
        super(message);
        this.code = code;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }
}
