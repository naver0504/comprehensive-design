package com.example.comprehensivedegisn.api;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class OpenApiRequest {

    @Builder
    public OpenApiRequest(int pageNo, LocalDate contractDate) {
        this.pageNo = pageNo;
        this.contractDate = contractDate;
    }

    private int numOfRows = 100;
    private String LAWD_CD = "11000";

    private int pageNo;
    private LocalDate contractDate;

    public String getFormattedContractDate() {
        return contractDate.toString().replace("-", "");
    }
}
