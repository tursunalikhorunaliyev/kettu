package com.khorunaliyev.kettu.repository.auth;

import com.khorunaliyev.kettu.dto.projection.UserInfo;
import com.khorunaliyev.kettu.entity.auth.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {
  Optional<AppUser> findByEmail(String email);

  @Query("SELECT u FROM AppUser u WHERE u.email = :email")
  @Transactional(readOnly = true)
  Optional<UserInfo> findDetachedByEmail(@Param("email") String email);
}