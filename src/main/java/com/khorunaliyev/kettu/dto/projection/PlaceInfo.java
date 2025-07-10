package com.khorunaliyev.kettu.dto.projection;

import com.khorunaliyev.kettu.entity.enums.PlaceStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Projection for {@link com.khorunaliyev.kettu.entity.place.Place}
 */
public interface PlaceInfo {
    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();

    Long getId();

    String getName();

    String getDescription();

    Integer getLikesCount();

    PlaceStatus getStatus();

    List<PlacePhotoInfo> getPhotos();

    PlaceLocationInfo getLocation();

    PlaceMetaDataInfo getMetaData();

    Set<UserInfo> getVisitedUsers();

    Set<NearbyThingsInfo> getNearbyThings();

    /**
     * Projection for {@link com.khorunaliyev.kettu.entity.place.PlacePhoto}
     */
    interface PlacePhotoInfo {
        Long getId();

        String getImage();

        boolean isIsMain();
    }

    /**
     * Projection for {@link com.khorunaliyev.kettu.entity.place.PlaceLocation}
     */
    interface PlaceLocationInfo {
        Long getId();

        double getLat_();

        double getLong_();

        CountryInfo getCountry();

        RegionInfo getRegion();

        DistrictInfo getDistrict();
    }

    /**
     * Projection for {@link com.khorunaliyev.kettu.entity.place.PlaceMetaData}
     */
    interface PlaceMetaDataInfo {
        Long getId();

        FeatureInfo getFeature();

        CategoryInfo getCategory();

        SubCategoryInfo getSubCategory();
    }

    /**
     * Projection for {@link com.khorunaliyev.kettu.entity.auth.User}
     */
    interface UserInfo {
        Long getId();

        String getName();

        String getEmail();
    }
}