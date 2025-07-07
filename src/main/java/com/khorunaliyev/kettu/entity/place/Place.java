package com.khorunaliyev.kettu.entity.place;

import com.khorunaliyev.kettu.entity.audit.AuditEntity;
import com.khorunaliyev.kettu.entity.auth.User;
import com.khorunaliyev.kettu.entity.enums.PlaceStatus;
import com.khorunaliyev.kettu.entity.resources.NearbyThings;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "place")
public class Place extends AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 500)
    private String description;

    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PlacePhoto> photos;

    @OneToOne(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private PlaceLocation location;

    @OneToOne(fetch = FetchType.EAGER)
    private PlaceMetaData metaData;

    @ManyToMany
    @JoinTable(name = "place_nearby_things", joinColumns = @JoinColumn(name = "place_id"), inverseJoinColumns = @JoinColumn(name = "thing_id"))
    private Set<NearbyThings> nearbyThings = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "visited_users", joinColumns = @JoinColumn(name = "place_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> visitedUsers = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "liked_users", joinColumns = @JoinColumn(name = "place_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> likedUsers = new HashSet<>();

    @Column(nullable = false)
    private Integer likesCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlaceStatus status = PlaceStatus.MODERATION;

}