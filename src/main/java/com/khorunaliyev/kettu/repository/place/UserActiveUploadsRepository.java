package com.khorunaliyev.kettu.repository.place;

import com.khorunaliyev.kettu.entity.place.UserActiveUploads;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserActiveUploadsRepository extends JpaRepository<UserActiveUploads, Long> {
}