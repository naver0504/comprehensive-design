package com.example.comprehensivedegisn.batch.kakao_map.dto;


public record ApartmentGeoRecord (Long id, String x, String y) {

    public boolean isNotEmpty() {
        return x != null && y != null;
    }

    public String toPoint() {
        if(x == null || y == null) {
            return String.format("POINT(%s %s)", "0", "0");
        } else {
            return String.format("POINT(%s %s)", x, y);
        }
    }
}
