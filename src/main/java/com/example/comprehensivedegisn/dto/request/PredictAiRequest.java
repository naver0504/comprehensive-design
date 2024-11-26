package com.example.comprehensivedegisn.dto.request;

import com.example.comprehensivedegisn.adapter.domain.ApartmentTransaction;
import com.example.comprehensivedegisn.adapter.domain.Gu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PredictAiRequest {
    private Gu gu;
    private String dongName;
    private double exclusiveArea;
    private int floor;
    private int buildYear;

    public PredictAiRequest(ApartmentTransaction apartmentTransaction) {
        this.gu = apartmentTransaction.getDongEntity().getGu();
        this.dongName = apartmentTransaction.getDongEntity().getDongName();
        this.exclusiveArea = apartmentTransaction.getAreaForExclusiveUse();
        this.floor = apartmentTransaction.getFloor();
        this.buildYear = apartmentTransaction.getBuildYear();
    }

    @Override
    public String toString() {
        return "gu=" + gu + "&dongName=" + dongName + "&exclusiveArea=" + exclusiveArea + "&floor=" + floor + "&buildYear=" + buildYear;
    }
}