package com.khorunaliyev.kettu.component;

import com.khorunaliyev.kettu.dto.projection.PlaceInfo;
import com.khorunaliyev.kettu.dto.reponse.place.*;
import com.khorunaliyev.kettu.dto.reponse.resource.IDNameDTO;
import com.khorunaliyev.kettu.dto.reponse.resource.IDNameItemCountDTO;
import com.khorunaliyev.kettu.dto.reponse.resource.NearbyThingsDTO;
import com.khorunaliyev.kettu.entity.place.Place;
import com.khorunaliyev.kettu.entity.place.PlacePhoto;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
@Component
public class PlaceMappers {
    public PlaceDTO toDto(PlaceInfo p) {
        return new PlaceDTO(p.getId(),
                p.getName(),
                p.getDescription(),
                p.getStatus(),
                new PlacePhotoDTO(p.getPhotos().stream()
                        .filter(PlaceInfo.PlacePhotoInfo::isIsMain)
                        .map(PlaceInfo.PlacePhotoInfo::getImage)
                        .findFirst().orElse(null), p.getPhotos().stream().filter(placePhotoInfo -> !placePhotoInfo.isIsMain()).map(PlaceInfo.PlacePhotoInfo::getImage).toList()),
                new PlaceMetaDataDTO(p.getMetaData().getId(),
                        new IDNameDTO(p.getMetaData().getFeature().getId(), p.getMetaData().getFeature().getName()),
                        new IDNameItemCountDTO(p.getMetaData().getCategory().getId(), p.getMetaData().getCategory().getName(), p.getMetaData().getCategory().getActiveItemCount()),
                        new IDNameItemCountDTO(p.getMetaData().getSubCategory().getId(), p.getMetaData().getSubCategory().getName(), p.getMetaData().getSubCategory().getActiveItemCount())),
                new PlaceLocationDTO(p.getLocation().getId(),
                        new IDNameItemCountDTO(p.getLocation().getCountry().getId(), p.getLocation().getCountry().getName(), p.getLocation().getCountry().getActiveItemCount()),
                        new IDNameItemCountDTO(p.getLocation().getRegion().getId(), p.getLocation().getRegion().getName(), p.getLocation().getRegion().getActiveItemCount()),
                        new IDNameDTO(p.getLocation().getDistrict().getId(), p.getLocation().getDistrict().getName())),
                p.getNearbyThings().stream().map(ns -> new NearbyThingsDTO(ns.getId(), ns.getName(), "https://storage.thekettu.com/"+ns.getIcon())).collect(Collectors.toSet()),
                p.getVisitedUsers().stream().map(vu -> new UserDTO(vu.getId(), vu.getName(), vu.getEmail())).collect(Collectors.toSet()),
                p.getLikedUsers().stream().map(lu -> new UserDTO(lu.getId(), lu.getName(), lu.getEmail())).collect(Collectors.toSet()),
                p.getLikesCount(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }
    public PlaceHistoryDTO placeHistoryDTO(Place p) {
        return new PlaceHistoryDTO(p.getId(),
                p.getName(),
                p.getDescription(),
                p.getStatus(),
                new PlacePhotoDTO(p.getPhotos().stream()
                        .filter(PlacePhoto::isMain)
                        .map(PlacePhoto::getImage)
                        .findFirst().orElse(null), p.getPhotos().stream().filter(placePhotoInfo -> !placePhotoInfo.isMain()).map(PlacePhoto::getImage).toList()),
                new PlaceMetaDataDTO(p.getMetaData().getId(),
                        new IDNameDTO(p.getMetaData().getFeature().getId(), p.getMetaData().getFeature().getName()),
                        new IDNameItemCountDTO(p.getMetaData().getCategory().getId(), p.getMetaData().getCategory().getName(), p.getMetaData().getCategory().getActiveItemCount()),
                        new IDNameItemCountDTO(p.getMetaData().getSubCategory().getId(), p.getMetaData().getSubCategory().getName(), p.getMetaData().getSubCategory().getActiveItemCount())),
                new PlaceLocationDTO(p.getLocation().getId(),
                        new IDNameItemCountDTO(p.getLocation().getCountry().getId(), p.getLocation().getCountry().getName(), p.getLocation().getCountry().getActiveItemCount()),
                        new IDNameItemCountDTO(p.getLocation().getRegion().getId(), p.getLocation().getRegion().getName(), p.getLocation().getRegion().getActiveItemCount()),
                        new IDNameDTO(p.getLocation().getDistrict().getId(), p.getLocation().getDistrict().getName())),
                p.getNearbyThings().stream().map(ns -> new NearbyThingsDTO(ns.getId(), ns.getName(), ns.getIcon())).collect(Collectors.toSet()),
                p.getVisitedUsers().stream().map(vu -> new UserDTO(vu.getId(), vu.getName(), vu.getEmail())).collect(Collectors.toSet()),
                p.getLikedUsers().stream().map(lu -> new UserDTO(lu.getId(), lu.getName(), lu.getEmail())).collect(Collectors.toSet()),
                p.getLikesCount(),
                p.getCreatedAt(),
                p.getUpdatedAt(),
                new UserDTO(p.getCreatedBy().getId(), p.getCreatedBy().getName(), p.getCreatedBy().getEmail()),
                new UserDTO(p.getUpdatedBy().getId(), p.getUpdatedBy().getName(), p.getUpdatedBy().getEmail())
        );
    }
}
