package com.khorunaliyev.kettu.repository.resource;

import com.khorunaliyev.kettu.dto.projection.FeatureInfo;
import com.khorunaliyev.kettu.entity.resources.Feature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeatureRepository extends JpaRepository<Feature, Long> {
    List<FeatureInfo> findAllBy();
}