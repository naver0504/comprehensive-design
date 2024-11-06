package com.example.comprehensivedegisn.repository.order;

import com.querydsl.core.types.dsl.ComparableExpressionBase;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.example.comprehensivedegisn.domain.QApartmentTransaction.*;

@Getter
@RequiredArgsConstructor
public enum OrderType {

    DEAL_AMOUNT(apartmentTransaction.dealAmount),
    AREA_FOR_EXCLUSIVE_USE(apartmentTransaction.areaForExclusiveUse),
    DEAL_DATE(apartmentTransaction.dealDate);

    private final ComparableExpressionBase<? extends Comparable> comparableExpressionBase;
}
