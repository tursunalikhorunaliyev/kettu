package com.khorunaliyev.kettu.component;

import com.khorunaliyev.kettu.entity.auth.AppUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserContext {

    public AppUser getUser() {
        return (AppUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }

    public Long getUserId() {
        return getUser().getId();
    }
}