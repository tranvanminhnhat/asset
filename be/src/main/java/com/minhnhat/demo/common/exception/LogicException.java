package com.minhnhat.demo.common.exception;

public class LogicException extends RuntimeException{

    private final String serviceName;
    private final String messageId;   // id message lỗi

    public LogicException(String serviceName, String message, String messageId) {
        super(message);
        this.serviceName = serviceName;
        this.messageId = messageId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getMessageId() {
        return messageId;
    }
}
