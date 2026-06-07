package com.khorunaliyev.kettu.dto.reponse.geo;

import java.util.Map;

public record GeoDataFromPoint(Point point ,IdSlugName region, IdSlugName district) {
    public record IdSlugName(Integer id,String slug, String name){}
    public record Point(double latitude, double longitude){};
}
