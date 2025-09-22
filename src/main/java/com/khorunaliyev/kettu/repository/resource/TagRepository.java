package com.khorunaliyev.kettu.repository.resource;

import com.khorunaliyev.kettu.entity.resources.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}