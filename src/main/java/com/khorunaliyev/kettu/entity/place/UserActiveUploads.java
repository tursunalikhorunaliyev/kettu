package com.khorunaliyev.kettu.entity.place;

import com.khorunaliyev.kettu.entity.auth.AppUser;
import com.khorunaliyev.kettu.entity.resources.Category;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_active_uploads")
public class UserActiveUploads {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @OneToOne()
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;
}