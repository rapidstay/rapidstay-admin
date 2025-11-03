package com.rapidstay.xap.admin.common.exception;

public class GlobalException extends RuntimeException {
    private final String code;

    public GlobalException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
