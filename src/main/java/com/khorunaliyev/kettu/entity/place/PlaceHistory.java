package com.khorunaliyev.kettu.entity.place;

import com.khorunaliyev.kettu.entity.audit.AuditEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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