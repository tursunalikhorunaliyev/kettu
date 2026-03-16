package com.khorunaliyev.kettu.entity.resources;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.MultiPolygon;

@Getter
@Setter
@Entity
@Table(name = "district")
public class District {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;


    @Column(nullable = false, unique = true, length = 100)
    private String name;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;


    @Column(nullable = false)
    private Integer activeItemCount = 0;

    @Column(nullable = false,columnDefinition = "geometry(MultiPolygon, 4326)")
    private MultiPolygon geom;
}