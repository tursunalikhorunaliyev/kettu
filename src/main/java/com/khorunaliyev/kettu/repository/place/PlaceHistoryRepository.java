package com.khorunaliyev.kettu.repository.place;

import com.khorunaliyev.kettu.entity.place.PlaceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceHistoryRepository extends JpaRepository<PlaceHistory, Long> {
    @Query("SELECT ph FROM PlaceHistory ph WHERE JSON_EXTRACT(ph.placeSnapshotJson, '$.status') = :status")
    List<PlaceHistory> findBySnapshotStatus(@Param("status") String status);
}