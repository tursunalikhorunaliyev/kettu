package com.khorunaliyev.kettu.entity.place;

import com.khorunaliyev.kettu.entity.audit.AuditEntity;
import com.khorunaliyev.kettu.entity.auth.User;
import com.khorunaliyev.kettu.entity.enums.PlaceStatus;
import com.khorunaliyev.kettu.entity.resources.Feature;
import com.khorunaliyev.kettu.entity.resources.NearbyThings;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "place_history")
public class PlaceHistory extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Target Place
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    // JSON snapshot of the old state
    @Column(name = "place_snapshot_json", columnDefinition = "json", nullable = false)
    private String placeSnapshotJson;

    // Optional reason (e.g., "Updated description")
    @Column(length = 500)
    private String changeReason;

}