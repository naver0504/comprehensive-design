package com.example.comprehensivedegisn.dto;

import com.example.comprehensivedegisn.adapter.domain.Gu;

import java.time.LocalDate;
public record SearchResponseRecord(
        Long id,
        String apartmentName,
        String region,
        Double areaForExclusiveUse,
        LocalDate dealDate,
        String dealAmount,
        long predictedCost,
        boolean isReliable
) {

    public SearchResponseRecord(Long id, String apartmentName, Gu gu, String dongName, Double areaForExclusiveUse,
                                LocalDate dealDate, String dealAmount, long predictedCost, boolean isReliable) {
        this(id, apartmentName, createRegion(gu, dongName), areaForExclusiveUse, dealDate, dealAmount, predictedCost, isReliable);
    }

    public static String createRegion(Gu gu, String dongName) {
        return gu + " " + dongName;
    }

}
