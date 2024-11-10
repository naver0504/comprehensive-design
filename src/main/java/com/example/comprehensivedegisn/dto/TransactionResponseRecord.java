package com.example.comprehensivedegisn.dto;

import com.example.comprehensivedegisn.adapter.domain.Gu;
import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDate;
public record TransactionResponseRecord(
        Long id,
        String apartmentName,
        String region,
        Double areaForExclusiveUse,
        LocalDate dealDate,
        String dealAmount
) {

    @QueryProjection
    public TransactionResponseRecord(Long id, String apartmentName, Gu gu, String dongName, Double areaForExclusiveUse, LocalDate dealDate, String dealAmount) {
        this(id, apartmentName, createRegion(gu, dongName), areaForExclusiveUse, dealDate, dealAmount);
    }

    public static String createRegion(Gu gu, String dongName) {
        return gu + " " + dongName;
    }

}
