package com.khorunaliyev.kettu.controller;

import com.khorunaliyev.kettu.entity.TestAudit;
import com.khorunaliyev.kettu.repository.TestAuditRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/test")
@RequiredArgsConstructor
public class TestAuditController {

    private final TestAuditRepository testAuditRepository;

    @PostMapping("/audit")
    public void createAudit(@RequestParam(name = "name") String name){
        TestAudit testAudit = new TestAudit();
        testAudit.setName(name);
        testAuditRepository.save(testAudit);
    }


    @PostMapping("/audit/update")
    @Transactional
    public void updateAudit(@RequestParam(name = "id") Long id, @RequestParam(name = "name") String name){
        Optional<TestAudit> testAudit = testAuditRepository.findById(id);
        TestAudit realTestAudit = testAudit.get();
        realTestAudit.setName(name);
        testAuditRepository.save(realTestAudit);
    }

}
