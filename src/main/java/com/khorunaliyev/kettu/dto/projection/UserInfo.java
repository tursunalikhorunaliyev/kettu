package com.khorunaliyev.kettu.dto.projection;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.beans.factory.annotation.Value;

@JsonPropertyOrder({ "id", "name", "email", "image", "background_image", "bio", "created_places_count", "visited_places_count" })
public interface UserInfo {

    Long getId();

    String getName();

    String getEmail();

    @Value("#{(target.image != null && !target.image.startsWith('http')) ? 'https://storage.thekettu.com/users/profiles/' + target.image + '.jpg' : target.image}")
    String getImage();

    @JsonProperty("background_image")
    @Value("#{(target.backgroundImage != null && !target.backgroundImage.startsWith('http')) ? 'https://storage.thekettu.com/users/backgrounds/' + target.backgroundImage + '.jpg' : target.backgroundImage}")
    String getBackgroundImage();

    String getBio();

    @JsonProperty("created_places_count")
    Integer getCreatedPlacesCount();

    @JsonProperty("visited_places_count")
    Integer getVisitedPlacesCount();
}
