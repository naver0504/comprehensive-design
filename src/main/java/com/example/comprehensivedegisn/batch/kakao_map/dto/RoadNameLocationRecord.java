package com.example.comprehensivedegisn.batch.kakao_map.dto;


public record RoadNameLocationRecord(String x, String y) {

    public static final RoadNameLocationRecord EMPTY = new RoadNameLocationRecord(null, null);

    public ApartmentGeoRecord toApartmentGeoRecord(long id) {
        return new ApartmentGeoRecord(id, x, y);
    }
}
