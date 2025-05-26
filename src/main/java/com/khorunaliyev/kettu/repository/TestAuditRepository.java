package com.khorunaliyev.kettu.repository;

import com.khorunaliyev.kettu.entity.TestAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestAuditRepository extends JpaRepository<TestAudit, Long> {
}