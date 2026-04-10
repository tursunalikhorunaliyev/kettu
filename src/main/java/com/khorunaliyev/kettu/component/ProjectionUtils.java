package com.khorunaliyev.kettu.component;

import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("projectionUtils")
public class ProjectionUtils {
    public Map<String, Double> convertPoint(Point point) {
        if (point == null) return null;
        return Map.of("lat", point.getY(), "long", point.getX());
    }

    public Map<String, String> generateQualities(String fileName) {
        if (fileName == null) return null;
        String base = "https://storage.thekettu.com/";
        return Map.of(
                "high", base + "high/" + fileName,
                "medium", base + "medium/" + fileName,
                "low", base + "low/" + fileName);

    }
}
