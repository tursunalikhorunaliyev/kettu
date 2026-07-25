package com.khorunaliyev.kettu.entity.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "sub_category")
public class SubCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, columnDefinition = "integer default 0")
    @JsonProperty("item_count")
    private Integer activeItemCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "subcategory_tags", joinColumns = @JoinColumn(name = "subcategory_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"), uniqueConstraints = @UniqueConstraint(
            name = "uk_subcategory_tag",
            columnNames = {"subcategory_id", "tag_id"}
    ))
    @OrderBy("id ASC")
    private Set<Tag> tags = new HashSet<>();
}