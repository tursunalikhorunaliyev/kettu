package com.khorunaliyev.kettu.repository.place;

import com.khorunaliyev.kettu.dto.projection.PlaceMetaDataInfo;
import com.khorunaliyev.kettu.entity.place.PlaceMetaData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceMetaDataRepository extends JpaRepository<PlaceMetaData, Long> {
    List<PlaceMetaDataInfo> findByCategory_IdAndCategory_SubCategories_Id(@Nullable Long id, @Nullable Long id1);
}