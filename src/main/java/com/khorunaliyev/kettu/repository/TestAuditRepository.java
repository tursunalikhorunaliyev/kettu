package com.khorunaliyev.kettu.repository;

import com.khorunaliyev.kettu.entity.TestAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestAuditRepository extends JpaRepository<TestAudit, Long> {
}