package com.minhnhat.demo.common.base.response;

public class ErrorResponse {
    private String service;
    private String message;
    private String code;

    public ErrorResponse(String service, String message, String code) {
        this.service = service;
        this.message = message;
        this.code = code;
    }

    public String getService() {
        return service;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }
}
