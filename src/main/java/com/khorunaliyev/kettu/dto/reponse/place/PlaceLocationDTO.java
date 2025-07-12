package com.khorunaliyev.kettu.dto.reponse.place;

import com.khorunaliyev.kettu.dto.reponse.resource.IDNameDTO;
import com.khorunaliyev.kettu.dto.reponse.resource.IDNameItemCountDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PlaceLocationDTO {
    private Long id;
    private IDNameItemCountDTO country, region;
    private IDNameDTO district;
}
