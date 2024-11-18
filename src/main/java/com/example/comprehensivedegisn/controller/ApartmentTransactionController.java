package com.example.comprehensivedegisn.controller;

import com.example.comprehensivedegisn.adapter.order.CustomPageable;
import com.example.comprehensivedegisn.dto.SearchCondition;
import com.example.comprehensivedegisn.dto.SearchResponseRecord;
import com.example.comprehensivedegisn.service.ApartmentTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ApartmentTransactionController {

    private final ApartmentTransactionService apartmentTransactionService;

    @GetMapping("/apartment-transactions")
    public ResponseEntity<Page<SearchResponseRecord>> searchApartmentTransactions(@RequestParam(required = false) Long cachedCount,
                                                                                  @ModelAttribute SearchCondition searchCondition,
                                                                                  @ModelAttribute CustomPageable customPageable) {
        return ResponseEntity.ok(apartmentTransactionService.searchApartmentTransactions(cachedCount, searchCondition, customPageable));
    }


}
