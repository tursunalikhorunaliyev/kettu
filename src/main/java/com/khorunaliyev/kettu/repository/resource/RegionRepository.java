package com.khorunaliyev.kettu.repository.resource;

import com.khorunaliyev.kettu.entity.resources.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {

}