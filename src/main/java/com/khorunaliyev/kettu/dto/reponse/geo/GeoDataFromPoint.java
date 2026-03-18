package com.khorunaliyev.kettu.dto.reponse.geo;

public record GeoDataFromPoint(IdNameResponse region, IdNameResponse district) {
    public record IdNameResponse(Integer id, String name){}
}
