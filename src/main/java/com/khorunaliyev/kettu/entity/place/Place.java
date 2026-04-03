package com.khorunaliyev.kettu.entity.place;

import com.khorunaliyev.kettu.entity.auditing.FullAuditing;
import com.khorunaliyev.kettu.entity.auth.AppUser;
import com.khorunaliyev.kettu.entity.enums.PlaceStatus;
import com.khorunaliyev.kettu.entity.resources.Category;
import com.khorunaliyev.kettu.entity.resources.Tag;
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
public class Place extends FullAuditing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    private List<PlacePhoto> photos = new ArrayList<>();

    @OneToOne(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true)
    private PlaceLocation location;

    @ManyToMany(fetch =  FetchType.LAZY)
    @JoinTable(name = "place_tags", joinColumns = @JoinColumn(name = "place_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 13)
    private PlaceStatus status = PlaceStatus.IN_MODERATION;

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

    @Column(nullable = false)
    private Integer photoCount = 0;
}