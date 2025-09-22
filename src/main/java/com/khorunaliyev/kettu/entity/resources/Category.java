package com.khorunaliyev.kettu.entity.resources;

import com.khorunaliyev.kettu.entity.audit.AuditEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;


    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feature_id", nullable = false)
    private Feature feature;


    @Column(nullable = false)
    private Integer activeItemCount = 0;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    private List<SubCategory> subCategories;

    @ManyToMany
    @JoinTable(name = "category_tags", joinColumns = @JoinColumn(name = "tag_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Tag> tags = new HashSet<>();
}