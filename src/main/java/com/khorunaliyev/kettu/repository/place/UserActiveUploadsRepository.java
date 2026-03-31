package com.khorunaliyev.kettu.repository.place;

import com.khorunaliyev.kettu.entity.place.UserActiveUploads;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserActiveUploadsRepository extends JpaRepository<UserActiveUploads, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM UserActiveUploads u WHERE u.place.id = :placeId AND u.user.id = :userId")
    void deleteByPlace_IdAndUser_Id(@Param("placeId") Long placeId, @Param("userId") Long userId);

}