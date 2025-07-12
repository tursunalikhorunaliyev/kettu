package com.khorunaliyev.kettu.entity.place;

import com.khorunaliyev.kettu.entity.resources.Country;
import com.khorunaliyev.kettu.entity.resources.District;
import com.khorunaliyev.kettu.entity.resources.Region;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.xmlbeans.impl.xb.xsdschema.All;

@Getter
@Setter
@Entity
@Table(name = "place_location")
public class PlaceLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne(mappedBy = "location")
    private Place place;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    @Column(nullable = false)
    private double lat_;

    @Column(nullable = false)
    private double long_;
}