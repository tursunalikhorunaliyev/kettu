package com.khorunaliyev.kettu.entity.resources;

import com.khorunaliyev.kettu.entity.audit.AuditEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "region")
public class Region extends AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;


    @Column(nullable = false, unique = true, length = 1001)
    private String name;


    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;


    @OneToMany(mappedBy = "region",cascade = CascadeType.ALL, orphanRemoval = true)
    List<City> cities;


    @Column(nullable = false)
    private Integer activeItemCount;

}