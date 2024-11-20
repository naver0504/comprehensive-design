package com.example.comprehensivedegisn.adapter;

import com.example.comprehensivedegisn.adapter.domain.ApartmentTransaction;
import com.example.comprehensivedegisn.adapter.order.CustomPageable;
import com.example.comprehensivedegisn.dto.SearchCondition;
import com.example.comprehensivedegisn.dto.SearchResponseRecord;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface ApartmentTransactionAdapter {

    Optional<ApartmentTransaction> findById(Long id);
    Page<SearchResponseRecord> searchApartmentTransactions(Long cachedCount, SearchCondition searchCondition, CustomPageable customPageable);
}
