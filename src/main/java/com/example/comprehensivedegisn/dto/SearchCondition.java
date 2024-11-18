package com.example.comprehensivedegisn.dto;

import com.example.comprehensivedegisn.adapter.domain.Gu;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.*;

import java.time.LocalDate;

import static com.example.comprehensivedegisn.adapter.domain.QPredictCost.predictCost;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
public class SearchCondition {

    private Gu gu = Gu.NONE;
    private String dong;
    private String apartmentName;
    private Double areaForExclusiveUse;

    private LocalDate startDealDate;
    private LocalDate endDealDate;

    private Reliability reliability;

    public BooleanExpression toReliabilityEq() {
        return reliability.reliabilityExpression;
    }

    public boolean isNotValid() {
        return isDongNotValidate() || isApartmentNameNotValid() || isAreaNotValid();
    }

    private boolean isAreaNotValid() {
        return apartmentName == null && areaForExclusiveUse != null;
    }

    private boolean isApartmentNameNotValid() {
        return dong == null && apartmentName != null;
    }

    private boolean isDongNotValidate() {
        return gu == Gu.NONE && dong != null;
    }


    @Getter
    public enum Reliability {
        ALL(predictCost.isReliable.in(true, false)),
        RELIABLE(predictCost.isReliable.isTrue()),
        UNRELIABLE(predictCost.isReliable.isFalse());

        private final BooleanExpression reliabilityExpression;

        Reliability(BooleanExpression reliabilityExpression) {
            this.reliabilityExpression = reliabilityExpression;
        }

    }
}
