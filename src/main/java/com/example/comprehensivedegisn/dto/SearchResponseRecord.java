package com.example.comprehensivedegisn.dto;

import com.example.comprehensivedegisn.adapter.domain.Gu;

import java.time.LocalDate;
public record SearchResponseRecord(
        long id,
        String apartmentName,
        String region,
        double areaForExclusiveUse,
        LocalDate dealDate,
        int dealAmount,
        long predictedCost,
        boolean isReliable
) {

    public SearchResponseRecord(long id, String apartmentName, Gu gu, String dongName, double areaForExclusiveUse,
                                LocalDate dealDate, int dealAmount, long predictedCost, boolean isReliable) {
        this(id, apartmentName, createRegion(gu, dongName), areaForExclusiveUse, dealDate, dealAmount, predictedCost, isReliable);
    }

    public static String createRegion(Gu gu, String dongName) {
        return gu + " " + dongName;
    }

}
