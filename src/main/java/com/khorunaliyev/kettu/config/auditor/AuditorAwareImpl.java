package com.khorunaliyev.kettu.config.auditor;

import com.khorunaliyev.kettu.entity.auth.User;
import com.khorunaliyev.kettu.repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuditorAwareImpl implements AuditorAware<User> {

    private final UserRepository userRepository;

    @Override
    public Optional<User> getCurrentAuditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof User user) {
            System.out.println("USER");
            return Optional.of(user);
        } else if (principal instanceof UserDetails userDetails) {
            System.out.println("USERDETAILS");
            String username = userDetails.getUsername();
            return userRepository.findByEmail(username);
        }

        String username = auth.getName();



        return userRepository.findByEmail(username);

    }
}
