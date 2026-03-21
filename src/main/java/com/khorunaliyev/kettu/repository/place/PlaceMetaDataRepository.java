package com.khorunaliyev.kettu.repository.place;

import com.khorunaliyev.kettu.dto.projection.PlaceMetaDataInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceMetaDataRepository extends JpaRepository<PlaceMetaData, Long> {
    List<PlaceMetaDataInfo> findByCategory_Id(@Nullable Long id);
}