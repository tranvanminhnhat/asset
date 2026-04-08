package com.minhnhat.demo.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    @Autowired
    private MessageSource messageSource;

    public String get(String code) {
        return messageSource.getMessage(
                code,
                null,
                LocaleContextHolder.getLocale()
        );
    }

    public String get(String code, Object[] args) {
        return messageSource.getMessage(
                code,
                args,
                LocaleContextHolder.getLocale()
        );
    }
}
