package com.khorunaliyev.kettu.entity.resources;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

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

    @Column(nullable = false)
    @JsonProperty("item_count")
    private Integer activeItemCount = 0;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "category_tags", joinColumns = @JoinColumn(name = "category_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"), uniqueConstraints = @UniqueConstraint(
            name = "uk_category_tag",
            columnNames = {"category_id", "tag_id"}
    ))
    @OrderBy("id ASC")
    private Set<Tag> tags = new HashSet<>();
}