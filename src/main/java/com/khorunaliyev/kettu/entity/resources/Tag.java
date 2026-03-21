package com.khorunaliyev.kettu.entity.resources;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tag")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @OrderBy("id ASC")
    private Integer id;

    @Column(nullable = false, length = 50, unique = true)
    private String name;
}