package com.khorunaliyev.kettu.entity.resources;

import com.khorunaliyev.kettu.entity.audit.AuditEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "category")
public class Category extends AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;


    @Column(nullable = false, unique = true, length = 100)
    private String name;


    @ManyToOne
    @JoinColumn(name = "feature_id")
    private Feature feature;


    @Column(nullable = false)
    private Integer activeItemCount;
}