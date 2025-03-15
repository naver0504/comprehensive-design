package com.example.comprehensivedegisn.adapter.impl;

import com.example.comprehensivedegisn.adapter.ApartmentTransactionAdapter;
import com.example.comprehensivedegisn.adapter.domain.ApartmentTransaction;
import com.example.comprehensivedegisn.adapter.domain.Gu;
import com.example.comprehensivedegisn.adapter.order.CustomPageImpl;
import com.example.comprehensivedegisn.adapter.order.CustomPageable;
import com.example.comprehensivedegisn.adapter.repository.apart.ApartmentTransactionRepository;
import com.example.comprehensivedegisn.adapter.repository.apart.QuerydslApartmentTransactionRepository;
import com.example.comprehensivedegisn.adapter.repository.apart.QuerydslSearchApartmentTransactionRepository;
import com.example.comprehensivedegisn.dto.response.SearchApartNameResponse;
import com.example.comprehensivedegisn.dto.response.SearchAreaResponse;
import com.example.comprehensivedegisn.dto.request.SearchCondition;
import com.example.comprehensivedegisn.dto.response.SearchResponseRecord;
import com.example.comprehensivedegisn.dto.response.TransactionDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class ApartmentTransactionAdapterImpl implements ApartmentTransactionAdapter {

    private final ApartmentTransactionRepository apartmentTransactionRepository;
    private final QuerydslApartmentTransactionRepository querydslApartmentTransactionRepository;
    private final QuerydslSearchApartmentTransactionRepository querydslSearchApartmentTransactionRepository;

    @Override
    public Optional<ApartmentTransaction> findApartmentTransactionById(Long id) {
        return apartmentTransactionRepository.findApartmentTransactionById(id);
    }

    @Override
    public Page<SearchResponseRecord> searchApartmentTransactions(Long cachedCount, SearchCondition searchCondition, CustomPageable customPageable) {
        List<SearchResponseRecord> elements = querydslSearchApartmentTransactionRepository.searchApartmentTransactions(searchCondition, customPageable);
        long count = getCount(cachedCount, searchCondition);
        return new CustomPageImpl<>(elements, customPageable.toPageable(), count);
    }

    @Override
    public List<SearchApartNameResponse> findApartmentNames(Gu gu, String dongName) {
        return querydslApartmentTransactionRepository.findApartmentNames(gu, dongName);
    }

    @Override
    public List<SearchAreaResponse> findAreaForExclusive(Gu gu, String dongName, String apartmentName) {
        return querydslApartmentTransactionRepository.findAreaForExclusive(gu, dongName, apartmentName);
    }

    @Override
    public Optional<TransactionDetailResponse> findTransactionDetail(long id) {
        return querydslApartmentTransactionRepository.findTransactionDetail(id);
    }

    @Override
    public List<ApartmentTransaction> findApartmentTransactionsForGraph(Gu gu, String dongName, String apartmentName, double areaForExclusiveUse, LocalDate startDate, LocalDate endDate) {
        return apartmentTransactionRepository.findApartmentTransactionsForGraph(gu, dongName, apartmentName, areaForExclusiveUse, startDate, endDate);
    }

    private long getCount(Long cachedCount, SearchCondition searchCondition) {
        if (cachedCount != null) return cachedCount;
        return querydslSearchApartmentTransactionRepository.getSearchCount(searchCondition);
    }
}
