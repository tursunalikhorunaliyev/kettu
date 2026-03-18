package com.khorunaliyev.kettu.util.query;

public final class DistrictQuery {
    private DistrictQuery(){}

    public static final String FIND_WITH_POINT = """
            select r.id as region_id, r.name as region_name, d.id as district_id, d.name as district_name from district as d\s
            inner join region as r on d.region_id = r.id
            where ST_Contains(d.geom, ST_SetSRID(ST_Point(:lng,:lat),4326))
            """;
}
