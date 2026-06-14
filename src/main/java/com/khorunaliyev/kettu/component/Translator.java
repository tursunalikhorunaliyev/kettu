package com.khorunaliyev.kettu.component;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class Translator {
    private static MessageSource messageSource;

    public Translator(MessageSource messageSource) {
        Translator.messageSource = messageSource;
    }

    public static String translate(String key) {
        if (key == null) return "";
        return messageSource.getMessage(key, null, key, LocaleContextHolder.getLocale());
    }
}
