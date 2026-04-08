package com.minhnhat.demo.common.base.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private T data;
    private Message message;

    public static <T> ApiResponseBuilder<T> builder() {
        return new ApiResponseBuilder<>();
    }

    public static class ApiResponseBuilder<T> {
        private T data;
        private Message message;

        public ApiResponseBuilder<T> data(T data) {
            this.data = data;
            return this;
        }

        public ApiResponseBuilder<T> message(String code, String messageText, String service) {
            this.message = Message.builder()
                    .code(code)
                    .message(messageText)
                    .service(service)
                    .build();
            return this;
        }

        public ApiResponse<T> build() {
            return new ApiResponse<>(data, message);
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Message {
        private String code;
        private String message;
        private String service;
    }
}
