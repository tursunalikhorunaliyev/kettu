package com.khorunaliyev.kettu.entity.place;

import com.khorunaliyev.kettu.entity.auth.AppUser;
import com.khorunaliyev.kettu.entity.enums.PlaceStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "place")
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 500)
    private String description;

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    private List<PlacePhoto> photos = new ArrayList<>();

    @OneToOne(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
    private PlaceLocation location;

    @OneToOne(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
    private PlaceMetaData metaData;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "visited_users", joinColumns = @JoinColumn(name = "place_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<AppUser> visitedKettuUsers = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "liked_users", joinColumns = @JoinColumn(name = "place_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<AppUser> likedKettuUsers = new HashSet<>();

    @Column(nullable = false, columnDefinition = "int default 0")
    private Integer likesCount = 0;

    @Column(nullable = false, columnDefinition = "int default 0")
    private Integer visitedCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlaceStatus status = PlaceStatus.IN_MODERATION;

    @CreatedDate
    @Column(name = "createdAt", updatable = false, nullable = false)
    protected LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updatedAt", nullable = false)
    protected LocalDateTime updatedAt;
}