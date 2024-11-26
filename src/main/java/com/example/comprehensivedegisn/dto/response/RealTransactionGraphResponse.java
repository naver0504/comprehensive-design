package com.example.comprehensivedegisn.dto.response;

import com.example.comprehensivedegisn.adapter.domain.ApartmentTransaction;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record RealTransactionGraphResponse(Map<String, List<Integer>> realData) {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");

    public RealTransactionGraphResponse(List<ApartmentTransaction> transactions) {
        this(transactions.stream()
                .collect(
                        Collectors.groupingBy(
                                apartmentTransaction -> YearMonth.from(apartmentTransaction.getDealDate()).format(FORMATTER),
                                Collectors.mapping(ApartmentTransaction::getDealAmount, Collectors.toList())
                        )));
    }
}
