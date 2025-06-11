package com.khorunaliyev.kettu.repository.auth;

import com.khorunaliyev.kettu.entity.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);

  @Query("SELECT u FROM User u WHERE u.email = :email")
  @Transactional(readOnly = true)
  Optional<User> findDetachedByEmail(@Param("email") String email);
}