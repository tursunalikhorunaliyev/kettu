package com.khorunaliyev.kettu.dto.projection;

public interface UserInfo {

    Long getId();

    String getName();

    String getEmail();

    String getImage();

    String getBackgroundImage();

    String getBio();

    Integer getCreatedPlacesCount();

    Integer getVisitedPlacesCount();
}
