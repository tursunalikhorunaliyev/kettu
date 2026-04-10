package com.khorunaliyev.kettu.dto.projection.place;

import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

/**
 * Projection for {@link com.khorunaliyev.kettu.entity.place.PlacePhoto}
 */
public interface PlacePhotoInfo {
    Long getId();


    boolean isIsMain();


    boolean isIsProcessing();

    @Value("#{@projectionUtils.generateQualities(target.image)}")
    Map<String, String> getQualities();
}