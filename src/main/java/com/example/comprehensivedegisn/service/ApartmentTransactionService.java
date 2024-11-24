package com.example.comprehensivedegisn.service;

import com.example.comprehensivedegisn.adapter.ApartmentTransactionAdapter;
import com.example.comprehensivedegisn.adapter.domain.ApartmentTransaction;
import com.example.comprehensivedegisn.adapter.domain.DongEntity;
import com.example.comprehensivedegisn.adapter.order.CustomPageable;
import com.example.comprehensivedegisn.dto.request.SearchApartNameRequest;
import com.example.comprehensivedegisn.dto.request.SearchAreaRequest;
import com.example.comprehensivedegisn.dto.request.SearchCondition;
import com.example.comprehensivedegisn.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApartmentTransactionService {

    private final ApartmentTransactionAdapter apartmentTransactionAdapter;

    public Page<SearchResponseRecord> searchApartmentTransactions(Long cachedCount, SearchCondition searchCondition, CustomPageable customPageable) {
        if(searchCondition.isNotValid()) throw new IllegalArgumentException("검색 조건이 올바르지 않습니다.");
        return apartmentTransactionAdapter.searchApartmentTransactions(cachedCount, searchCondition, customPageable);
    }

    @Cacheable(value = "apartmentTransaction", key = "#root.methodName + ':' +#request.gu + ':' + #request.dong")
    public List<SearchApartNameResponse> findApartmentNames(SearchApartNameRequest request) {
        if(request.isNotValid()) throw new IllegalArgumentException("검색 조건이 올바르지 않습니다.");
        return apartmentTransactionAdapter.findApartmentNames(request.getGu(), request.getDong());
    }

    @Cacheable(value = "apartmentTransaction", key = "#root.methodName + ':' +#request.gu + ':' + #request.dong + ':' + #request.apartmentName")
    public List<SearchAreaResponse> findAreaForExclusive(SearchAreaRequest request) {
        if(request.isNotValid()) throw new IllegalArgumentException("검색 조건이 올바르지 않습니다.");
        return apartmentTransactionAdapter.findAreaForExclusive(request.getGu(), request.getDong(), request.getApartmentName());
    }


    @Cacheable(value = "apartmentTransaction", key = "#root.methodName + ':' +#id")
    public TransactionDetailResponse findTransactionDetail(long id) {
        return apartmentTransactionAdapter.findTransactionDetail(id)
                .orElseThrow(() -> new IllegalArgumentException("잘못 된 거래 Id 입니다."));
    }

    @Cacheable(value = "apartmentTransaction", key = "#root.methodName + ':' +#id")
    public ApartmentTransaction findById(long id) {
        return apartmentTransactionAdapter.findApartmentTransactionById(id)
                .orElseThrow(() -> new IllegalArgumentException("잘못 된 거래 Id 입니다."));
    }

    @Cacheable(value = "apartmentTransaction", key = "#root.methodName + ':' +#apartmentTransaction.id")
    public RealTransactionGraphResponse findApartmentTransactionsForGraph(ApartmentTransaction apartmentTransaction) {
        DongEntity dongEntity = apartmentTransaction.getDongEntity();

        List<ApartmentTransaction> apartmentTransactions = apartmentTransactionAdapter.findApartmentTransactionsForGraph(dongEntity.getGu(), dongEntity.getDongName(),
                apartmentTransaction.getApartmentName(), apartmentTransaction.getAreaForExclusiveUse(), apartmentTransaction.getDealDate().minusMonths(12), apartmentTransaction.getDealDate());

        return new RealTransactionGraphResponse(apartmentTransactions);
    }
}
