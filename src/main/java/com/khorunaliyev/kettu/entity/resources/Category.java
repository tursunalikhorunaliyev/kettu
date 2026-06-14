package com.khorunaliyev.kettu.entity.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.khorunaliyev.kettu.entity.auditing.FullAuditing;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "active_item_count",nullable = false, columnDefinition = "integer default 0")
    @JsonProperty("item_count")
    private Integer activeItemCount = 0;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<SubCategory> subCategories = new ArrayList<>();
}