package com.khorunaliyev.kettu.config.auditor;

import com.khorunaliyev.kettu.entity.User;
import com.khorunaliyev.kettu.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuditorAwareImpl implements AuditorAware<User> {

    private final EntityManager entityManager;
    private final UserRepository userRepository;

    @Override
    public Optional<User> getCurrentAuditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) return Optional.empty();

        String username = auth.getName();

        // NOTE: we avoid findByEmail to prevent flush issues
        // You must ensure email is unique and user is already persisted
        // Here we're assuming email == username
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                    "SELECT u.id FROM User u WHERE u.email = :email", Long.class);
            Long userId = query.setParameter("email", username).getSingleResult();

            User reference = entityManager.getReference(User.class, userId);
            return Optional.of(reference);
        } catch (NoResultException e) {
            return Optional.empty();
        }

    }
}
