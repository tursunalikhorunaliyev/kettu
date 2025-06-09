package com.khorunaliyev.kettu.entity.resources;

import com.khorunaliyev.kettu.entity.audit.AuditEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "country")
public class Country extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(unique = true, nullable = false, length = 56)
    private String name;

    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Region> regions;


    @Column(nullable = false)
    private Integer activeItemCount = 0;

}