package com.example.comprehensivedegisn.dto;

import com.example.comprehensivedegisn.adapter.domain.Gu;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.*;

import java.time.LocalDate;

/***
 * public으로 선언된 생성자를 찾는다.
 * 없다면, public이 아닌 생성자 중에 매개변수 개수가 제일 적은 생성자를 선택한다. (보통 기본 생성자)
 * 찾은 생성자가 고유하다면, 해당 생성자를 선택한다.
 * 찾은 생성자가 여러개라면, 매개변수가 제일 적은 생성자를 선택한다.
 *
 */
@NoArgsConstructor
@Getter
@AllArgsConstructor
@ToString
public class SearchCondition {

    private Gu gu = Gu.NONE;
    private String dong;
    private String apartmentName;
    private Double areaForExclusiveUse;

    private LocalDate startDealDate;
    private LocalDate endDealDate;

    private Reliability reliability = Reliability.ALL;

    public BooleanExpression toReliabilityEq() {
        return reliability.getReliabilityExpression();
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
}
