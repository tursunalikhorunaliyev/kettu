package com.khorunaliyev.kettu.config.auditor;

import com.khorunaliyev.kettu.entity.User;
import com.khorunaliyev.kettu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuditorAwareImpl implements AuditorAware<User> {
    private final UserRepository userRepository;

    @Override
    public Optional<User> getCurrentAuditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(!auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser"))return Optional.empty();

        String username = auth.getName();
        return userRepository.findByEmail(username);
    }
}
