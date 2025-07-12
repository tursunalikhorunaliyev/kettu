package com.khorunaliyev.kettu.dto.reponse.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NearbyThingsDTO {
    private Long id;
    private String name;
    private String icon;
}
