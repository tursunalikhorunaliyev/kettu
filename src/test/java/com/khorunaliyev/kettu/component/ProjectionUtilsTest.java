package com.khorunaliyev.kettu.component;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectionUtilsTest {

    private final ProjectionUtils utils = new ProjectionUtils();
    private final GeometryFactory geometryFactory = new GeometryFactory();

    @Test
    void convertPointReturnsNullForNullInput() {
        assertThat(utils.convertPoint(null)).isNull();
    }

    @Test
    void convertPointMapsXtoLongAndYtoLat() {
        // JTS: X = longitude, Y = latitude.
        Point point = geometryFactory.createPoint(new org.locationtech.jts.geom.Coordinate(69.24, 41.31));

        Map<String, Double> result = utils.convertPoint(point);

        assertThat(result).containsEntry("lat", 41.31).containsEntry("long", 69.24);
    }

    @Test
    void generateQualitiesReturnsNullForNullFileName() {
        assertThat(utils.generateQualities(null)).isNull();
    }

    @Test
    void generateQualitiesBuildsAllThreeQualityUrls() {
        Map<String, String> result = utils.generateQualities("abc.jpg");

        assertThat(result)
                .containsEntry("high", "https://storage.thekettu.com/high/abc.jpg")
                .containsEntry("medium", "https://storage.thekettu.com/medium/abc.jpg")
                .containsEntry("low", "https://storage.thekettu.com/low/abc.jpg");
    }
}
