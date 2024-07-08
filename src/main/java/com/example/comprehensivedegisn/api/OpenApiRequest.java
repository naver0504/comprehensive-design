package com.example.comprehensivedegisn.api;

import lombok.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OpenApiRequest {

    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");

    private int numOfRows;
    private String LAWD_CD;
    private int pageNo;
    private String contractDate;

    @Builder
    public OpenApiRequest(int numOfRows, String LAWD_CD, int pageNo, LocalDate contractDate) {
        this.numOfRows = numOfRows;
        this.LAWD_CD = LAWD_CD;
        this.pageNo = pageNo;
        this.contractDate = convertLocalDateToString(contractDate);
    }

    public String convertLocalDateToString(LocalDate localDate) {
        return formatter.format(localDate);
    }
}
