package com.khorunaliyev.kettu.dto.projection.place;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.khorunaliyev.kettu.dto.projection.CategoryInfo;
import com.khorunaliyev.kettu.dto.projection.TagInfo;
import com.khorunaliyev.kettu.dto.projection.UserInfo;
import com.khorunaliyev.kettu.entity.enums.PlaceStatus;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Projection for {@link com.khorunaliyev.kettu.entity.place.Place}
 */
@JsonPropertyOrder({ "id", "name", "description", "category", "location", "tags", "photos", "likes_count", "visited_count", "photos_count", "created_at", "updated_at", "created_by"})
public interface PlaceInfo {

    Long getId();

    String getName();

    String getDescription();

    PlaceStatus getStatus();

    @JsonProperty("likes_count")
    Integer getLikesCount();

    @JsonProperty("visited_count")
    Integer getVisitedCount();

    @JsonProperty("photos_count")
    Integer getPhotoCount();

    CategoryInfo getCategory();

    PlaceLocationInfo getLocation();

    Set<TagInfo> getTags();

    @JsonProperty("created_at")
    LocalDateTime getCreatedAt();

    @JsonProperty("updated_at")
    LocalDateTime getUpdatedAt();

    @JsonProperty("created_by")
    UserInfo getCreatedBy();

    List<PlacePhotoInfo> getPhotos();
}