package com.khorunaliyev.kettu.repository.place;

import com.khorunaliyev.kettu.entity.place.UserActiveUploads;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserActiveUploadsRepository extends JpaRepository<UserActiveUploads, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM UserActiveUploads u WHERE u.place.id = :placeId AND u.user.id = :userId")
    void deleteByPlace_IdAndUser_Id(@Param("placeId") Long placeId, @Param("userId") Long userId);

    @Query(value = """
            select
              p.id as place_id,
              p.name,
              max(pp.image) filter (where pp.is_main = true) as main_photo,
              p.description,
              p.photo_count,
              count(*) filter (
                where
                  pp.id is not null
              ) as uploaded_photo_count
            from
              user_active_uploads as uau
              inner join place as p on uau.place_id = p.id
              left join place_photo as pp on p.id = pp.place_id
            where
              uau.user_id = :user_id
            group by
              p.id
            """, nativeQuery = true)
    List<Tuple> getProcessedPlaces(@Param("user_id") Long userId);

}