package com.example.comprehensivedegisn.batch.excel_write.dto;

import com.example.comprehensivedegisn.domain.Gu;
import org.locationtech.jts.geom.Point;

public record ExcelTransactionRecord(
        String dealAmount,
        int buildYear,
        int dealYear,
        int dealMonth,
        int dealDay,
        String roadNameAddress,
        String apartmentName,
        double areaForExclusiveUse,
        String jibun,
        int floor,
        double x,
        double y,
        Gu gu,
        String dong
) {

    public ExcelTransactionRecord (
            String dealAmount,
            int buildYear,
            int dealYear,
            int dealMonth,
            int dealDay,
            String roadName,
            int roadNameBonbun,
            int roadNameBubun,
            String apartmentName,
            double areaForExclusiveUse,
            String jibun,
            int floor,
            Point geography,
            Gu gu,
            String dong
    ) {
        this(   dealAmount,
                buildYear,
                dealYear,
                dealMonth,
                dealDay,
                roadName + " " + roadNameBonbun + "-" + (roadNameBubun == 0 ? "" : roadNameBubun),
                apartmentName,
                areaForExclusiveUse,
                jibun,
                floor,
                geography != null ? geography.getX() : 0,
                geography != null ? geography.getY() : 0, gu, dong);

    }
}
