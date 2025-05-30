package com.khorunaliyev.kettu.repository.resource;

import com.khorunaliyev.kettu.entity.resources.Feature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeatureRepository extends JpaRepository<Feature, Long> {
}