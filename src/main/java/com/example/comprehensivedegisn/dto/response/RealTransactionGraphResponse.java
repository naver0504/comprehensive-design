package com.example.comprehensivedegisn.dto.response;

import com.example.comprehensivedegisn.adapter.domain.ApartmentTransaction;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record RealTransactionGraphResponse(Map<YearMonth, List<Integer>> realData) {

    public RealTransactionGraphResponse(List<ApartmentTransaction> transactions) {
        this(transactions.stream()
                .collect(
                        Collectors.groupingBy(
                                ApartmentTransaction::getYearMonth,
                                Collectors.mapping(ApartmentTransaction::getDealAmount, Collectors.toList())
                        )));
    }
}
