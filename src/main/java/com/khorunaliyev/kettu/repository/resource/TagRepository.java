package com.khorunaliyev.kettu.repository.resource;

import com.khorunaliyev.kettu.entity.resources.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {
    @Query(value = "select t.* from tag as t inner join category_tags as ct on t.id = ct.tag_id where ct.category_id = :category_id", nativeQuery = true)
    List<Tag> findAllByCategoryId(@Param("category_id") Integer category_id);
}