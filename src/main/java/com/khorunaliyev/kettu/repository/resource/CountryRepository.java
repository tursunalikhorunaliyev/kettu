package com.khorunaliyev.kettu.repository.resource;

import com.khorunaliyev.kettu.entity.resources.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
}