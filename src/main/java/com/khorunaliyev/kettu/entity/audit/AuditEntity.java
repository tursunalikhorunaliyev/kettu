package com.khorunaliyev.kettu.entity.audit;

import com.khorunaliyev.kettu.entity.auth.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class AuditEntity {

    @CreatedDate
    @Column(name = "createdAt", updatable = false)
    protected LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updatedAt")
    protected LocalDateTime updatedAt;

    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by",updatable = false)
    protected User createdBy;

    @LastModifiedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    protected User updatedBy;

}
