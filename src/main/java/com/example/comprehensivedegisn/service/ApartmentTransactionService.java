package com.example.comprehensivedegisn.service;

import com.example.comprehensivedegisn.adapter.ApartmentTransactionAdapter;
import com.example.comprehensivedegisn.adapter.order.CustomPageable;
import com.example.comprehensivedegisn.dto.SearchCondition;
import com.example.comprehensivedegisn.dto.SearchResponseRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApartmentTransactionService {

    private final ApartmentTransactionAdapter apartmentTransactionAdapter;

    public Page<SearchResponseRecord> searchApartmentTransactions(Long cachedCount, SearchCondition searchCondition, CustomPageable customPageable) {
        if(searchCondition.isNotValid()) throw new IllegalStateException("검색 조건이 올바르지 않습니다.");
        return apartmentTransactionAdapter.searchApartmentTransactions(cachedCount, searchCondition, customPageable);
    }
}
