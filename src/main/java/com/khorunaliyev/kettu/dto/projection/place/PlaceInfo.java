package com.khorunaliyev.kettu.dto.projection.place;

import com.khorunaliyev.kettu.dto.projection.CategoryInfo;
import com.khorunaliyev.kettu.dto.projection.TagInfo;
import com.khorunaliyev.kettu.entity.enums.PlaceStatus;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Projection for {@link com.khorunaliyev.kettu.entity.place.Place}
 */
public interface PlaceInfo {

    Long getId();

    String getName();

    String getDescription();

    PlaceStatus getStatus();

    @Value("#{target.likes_count}")
    Integer getLikesCount();

    @Value("#{target.visited_count}")
    Integer getVisitedCount();
    @Value("#{target.likes_count}")

    Integer getPhotoCount();

    CategoryInfo getCategory();

    PlaceLocationInfo getLocation();

    Set<TagInfo> getTags();

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();

}