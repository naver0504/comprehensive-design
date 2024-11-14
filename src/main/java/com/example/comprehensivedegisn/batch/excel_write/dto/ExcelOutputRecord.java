package com.example.comprehensivedegisn.batch.excel_write.dto;

import com.example.comprehensivedegisn.adapter.domain.Gu;

import java.time.LocalDate;

public record ExcelOutputRecord(
        LocalDate dealDate,
        double interestRate,
        Gu gu,
        String dong,
        double exclusiveArea,
        int floor,
        int buildYear,
        Double x,
        Double y,
        String dealAmount,
        String jibun
) {

}
