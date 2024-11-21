package com.example.comprehensivedegisn.dto.response;

import com.example.comprehensivedegisn.adapter.domain.DealingGbn;
import org.locationtech.jts.geom.Point;

import java.time.LocalDate;

public record TransactionDetailResponse(LocalDate dealDate, int buildYear, double areaForExclusiveUse, DealingGbn dealingGbn,
                                        String apartmentName, int dealAmount, long predictCost, Double x, Double y) {

    public TransactionDetailResponse(LocalDate dealDate, int buildYear, double areaForExclusiveUse, DealingGbn dealingGbn,
                                     String apartmentName, int dealAmount, long predictCost, Point point) {
            this(dealDate, buildYear, areaForExclusiveUse, dealingGbn, apartmentName, dealAmount,
                    predictCost, point != null ? point.getX() : null, point != null ? point.getY() : null);
    }
}
