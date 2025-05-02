package com.khorunaliyev.kettu.repository;

import com.khorunaliyev.kettu.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
  Optional<Student> findByName(String name);
}