package com.example.comprehensivedegisn.batch.kakao_map.dto;

import com.example.comprehensivedegisn.domain.ApartmentTransaction;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record Documents(List<Document> documents, Meta meta) {

    public RoadNameLocationRecord toRoadNameLocationRecord() {
        if(documents.isEmpty()) {
            return RoadNameLocationRecord.EMPTY;
        }
        return new RoadNameLocationRecord(documents.get(0).x(), documents.get(0).y());
    }

    public record Meta(@JsonProperty("is_end") boolean isEnd,
                       @JsonProperty("total_count") int totalCount) {
    }
}
