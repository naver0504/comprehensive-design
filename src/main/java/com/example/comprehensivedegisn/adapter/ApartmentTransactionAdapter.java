package com.example.comprehensivedegisn.adapter;

import com.example.comprehensivedegisn.adapter.domain.ApartmentTransaction;
import com.example.comprehensivedegisn.adapter.domain.Gu;
import com.example.comprehensivedegisn.adapter.order.CustomPageable;
import com.example.comprehensivedegisn.dto.response.SearchApartNameResponse;
import com.example.comprehensivedegisn.dto.response.SearchAreaResponse;
import com.example.comprehensivedegisn.dto.request.SearchCondition;
import com.example.comprehensivedegisn.dto.response.SearchResponseRecord;
import com.example.comprehensivedegisn.dto.response.TransactionDetailResponse;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ApartmentTransactionAdapter {

    Optional<ApartmentTransaction> findApartmentTransactionById(Long id);
    Page<SearchResponseRecord> searchApartmentTransactions(Long cachedCount, SearchCondition searchCondition, CustomPageable customPageable);
    List<SearchApartNameResponse> findApartmentNames(Gu gu, String dongName);
    List<SearchAreaResponse> findAreaForExclusive(Gu gu, String dongName, String apartmentName);
    Optional<TransactionDetailResponse> findTransactionDetail(long id);
    List<ApartmentTransaction> findApartmentTransactionsForGraph(Gu gu, String dongName, String apartmentName, double areaForExclusiveUse, LocalDate startDate, LocalDate endDate);
}
