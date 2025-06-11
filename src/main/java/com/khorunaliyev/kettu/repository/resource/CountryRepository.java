package com.khorunaliyev.kettu.repository.resource;

import com.khorunaliyev.kettu.dto.projection.CountryInfo;
import com.khorunaliyev.kettu.entity.resources.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
    List<CountryInfo> findAllBy();
}