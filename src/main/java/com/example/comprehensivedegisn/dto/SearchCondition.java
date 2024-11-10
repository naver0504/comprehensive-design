package com.example.comprehensivedegisn.dto;

import com.example.comprehensivedegisn.adapter.domain.Gu;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    public boolean validate() {
        return gu != Gu.NONE || dong == null;
    }
}
