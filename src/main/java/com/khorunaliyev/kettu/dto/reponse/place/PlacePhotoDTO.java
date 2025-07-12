package com.khorunaliyev.kettu.dto.reponse.place;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PlacePhotoDTO {
    private String mainImage;
    private List<String> images;
}
