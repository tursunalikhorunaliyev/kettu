package com.khorunaliyev.kettu.dto.reponse.place;

import com.khorunaliyev.kettu.dto.reponse.resource.IDNameDTO;
import com.khorunaliyev.kettu.dto.reponse.resource.IDNameItemCountDTO;
import com.khorunaliyev.kettu.entity.resources.Feature;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PlaceMetaDataDTO {
    private Long id;
    private IDNameDTO feature;
    private IDNameItemCountDTO category;
}
