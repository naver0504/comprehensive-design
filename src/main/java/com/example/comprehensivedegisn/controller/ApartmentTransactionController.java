package com.example.comprehensivedegisn.controller;

import com.example.comprehensivedegisn.adapter.domain.ApartmentTransaction;
import com.example.comprehensivedegisn.adapter.order.CustomPageable;
import com.example.comprehensivedegisn.api_client.predict.PredictApiClientForGraph;
import com.example.comprehensivedegisn.dto.request.SearchApartNameRequest;
import com.example.comprehensivedegisn.dto.request.SearchAreaRequest;
import com.example.comprehensivedegisn.dto.request.SearchCondition;
import com.example.comprehensivedegisn.dto.response.*;
import com.example.comprehensivedegisn.service.ApartmentTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ApartmentTransactionController {

    /**
     * WebDataBinder 통해 직접 필드에 접근할 수 있도록 설정
     * Setter 통해 바인딩하지 않고 필드에 직접 접근하여 바인딩
     *
     */

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        dataBinder.initDirectFieldAccess();
    }

    private final ApartmentTransactionService apartmentTransactionService;
    private final PredictApiClientForGraph predictAiApiClient;

    @GetMapping("/apartment-transactions")
    public ResponseEntity<Page<SearchResponseRecord>> searchApartmentTransactions(@RequestParam(required = false) Long cachedCount,
                                                                                  @ModelAttribute SearchCondition searchCondition,
                                                                                  @ModelAttribute CustomPageable customPageable) {

        return ResponseEntity.ok(apartmentTransactionService.searchApartmentTransactions(cachedCount, searchCondition, customPageable));
    }

    @GetMapping("/apartment-transactions/apartment-name")
    public ResponseEntity<List<SearchApartNameResponse>> findApartmentNames(SearchApartNameRequest request) {
        return ResponseEntity.ok(apartmentTransactionService.findApartmentNames(request));
    }

    @GetMapping("/apartment-transactions/area")
    public ResponseEntity<List<SearchAreaResponse>> findAreaForExclusiveUses(SearchAreaRequest request) {
        return ResponseEntity.ok(apartmentTransactionService.findAreaForExclusive(request));
    }

    @GetMapping("/apartment-transactions/{id}")
    public ResponseEntity<TransactionDetailResponse> findTransactionDetail(@PathVariable long id) {
        return ResponseEntity.ok(apartmentTransactionService.findTransactionDetail(id));
    }

    @GetMapping("/apartment-transactions/{id}/graph")
    public ResponseEntity<GraphResponse> findApartmentTransactionsForGraph(@PathVariable long id) {
        ApartmentTransaction apartmentTransaction = apartmentTransactionService.findById(id);
        RealTransactionGraphResponse realData = apartmentTransactionService.findApartmentTransactionsForGraph(apartmentTransaction);
        PredictAiResponse predictData = predictAiApiClient.callApi(apartmentTransaction);
        return ResponseEntity.ok(new GraphResponse(realData, predictData));
    }
}
