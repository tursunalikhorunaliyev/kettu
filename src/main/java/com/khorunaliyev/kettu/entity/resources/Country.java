package com.khorunaliyev.kettu.entity.resources;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "country")
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(unique = true, nullable = false, length = 56)
    private String name;

    @Column(nullable = false)
    private Integer activeItemCount = 0;
}