package com.khorunaliyev.kettu.dto.reponse.place;

import com.khorunaliyev.kettu.dto.reponse.resource.NearbyThingsDTO;
import com.khorunaliyev.kettu.entity.auth.User;
import com.khorunaliyev.kettu.entity.enums.PlaceStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class PlaceHistoryDTO {
    private Long placeID;
    private String name;
    private String description;
    private PlaceStatus status;
    private PlacePhotoDTO photo;
    private PlaceMetaDataDTO metaData;
    private PlaceLocationDTO location;
    private Set<NearbyThingsDTO> nearbyThings;
    private Set<UserDTO> visitedUsers;
    private Set<UserDTO> likedUsers;
    private Integer likes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserDTO createdBy;
    private UserDTO updatedBy;
}
