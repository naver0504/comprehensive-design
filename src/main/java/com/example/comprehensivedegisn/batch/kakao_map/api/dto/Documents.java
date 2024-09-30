package com.example.comprehensivedegisn.batch.kakao_map.api.dto;

import com.example.comprehensivedegisn.domain.ApartmentTransaction;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record Documents(List<Document> documents, Meta meta) {

    public ApartmentGeoRecord toApartmentGeoRecord(ApartmentTransaction apartmentTransaction) {
        return new ApartmentGeoRecord(apartmentTransaction.getId(), documents.get(0).x(), documents.get(0).y());
    }

    public boolean isValid(String roadName) {
        return documents != null && documents.get(0).isValid(roadName);
    }

    public record Meta(@JsonProperty("is_end") boolean isEnd,
                       @JsonProperty("total_count") int totalCount) {
    }
}
