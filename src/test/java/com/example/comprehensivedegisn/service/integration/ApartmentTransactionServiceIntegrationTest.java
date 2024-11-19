package com.example.comprehensivedegisn.service.integration;

import com.example.comprehensivedegisn.adapter.domain.*;
import com.example.comprehensivedegisn.adapter.order.CustomPageable;
import com.example.comprehensivedegisn.adapter.order.OrderType;
import com.example.comprehensivedegisn.adapter.repository.apart.ApartmentTransactionRepository;
import com.example.comprehensivedegisn.adapter.repository.dong.DongRepository;
import com.example.comprehensivedegisn.adapter.repository.predict_cost.PredictCostRepository;
import com.example.comprehensivedegisn.dto.Reliability;
import com.example.comprehensivedegisn.dto.SearchCondition;
import com.example.comprehensivedegisn.dto.SearchResponseRecord;
import com.example.comprehensivedegisn.service.ApartmentTransactionService;
import com.example.comprehensivedegisn.service.integration.config.IntegrationTestForService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

import static com.example.comprehensivedegisn.adapter.repository.apart.QuerydslApartmentTransactionRepositoryTest.*;

@IntegrationTestForService
public class ApartmentTransactionServiceIntegrationTest {

    @Autowired
    private ApartmentTransactionService target;
    @Autowired
    private ApartmentTransactionRepository apartmentTransactionRepository;
    @Autowired
    private DongRepository dongRepository;
    @Autowired
    private PredictCostRepository predictCostRepository;

    @Test
    void searchApartmentTransactions_With_Valid_Input() {
        // given
        Gu gu = Gu.서초구;
        String dong = TEST_DONG;
        String aptName = TEST_APT_NAME;
        LocalDate startDate = TEST_START_DATE;
        int dealAmount = 1000;

        List<SearchResponseRecord> expectedContents = setEntities(dealAmount, aptName, startDate, TEST_AREA, gu, dong);

        SearchCondition searchCondition = new SearchCondition(gu, dong, aptName, null, startDate, null, Reliability.ALL);
        CustomPageable customPageable = new CustomPageable(OrderType.DEAL_AMOUNT, 0);

        // when
        Page<SearchResponseRecord> result = target.searchApartmentTransactions(null, searchCondition, customPageable);
        // then
        List<SearchResponseRecord> contents = result.getContent();
        Assertions.assertThat(contents).isEqualTo(expectedContents);
    }

    @Test
    void searchApartmentTransactions_With_Not_Valid_Input() {
        // given
        Long count = 100L;
        SearchCondition notValidSearchCondition = new SearchCondition(Gu.마포구, "test", null, 111.1083, null, null, Reliability.ALL);
        CustomPageable customPageable = new CustomPageable(OrderType.DEAL_DATE, 3);

        // when
        Assertions.assertThatThrownBy(() -> target.searchApartmentTransactions(count, notValidSearchCondition, customPageable))
                .isInstanceOf(IllegalStateException.class);
    }



    private List<SearchResponseRecord> setEntities(int dealAmount, String aptName, LocalDate startDate, double area, Gu gu, String dong) {
        DongEntity dongEntity = dongRepository.save(DongEntity.builder().gu(gu).dongName(dong).build());

        ApartmentTransaction firstAT = apartmentTransactionRepository.save(ApartmentTransaction.builder()
                .apartmentName(aptName)
                .areaForExclusiveUse(area)
                .dongEntity(dongEntity)
                .dealDate(startDate)
                .dealAmount(dealAmount)
                .build());

        ApartmentTransaction secondAT = apartmentTransactionRepository.save(ApartmentTransaction.builder()
                .apartmentName(aptName)
                .areaForExclusiveUse(area * 2)
                .dongEntity(dongEntity)
                .dealDate(startDate.plusMonths(1))
                .dealAmount(dealAmount * 2)
                .build());

        PredictCost firstPC = predictCostRepository.save(PredictCost.builder()
                .apartmentTransaction(firstAT)
                .predictedCost(1000L)
                .isReliable(false)
                .predictStatus(PredictStatus.RECENT)
                .build());

        PredictCost secondPC = predictCostRepository.save(PredictCost.builder()
                .apartmentTransaction(secondAT)
                .predictedCost(1000L)
                .isReliable(true)
                .predictStatus(PredictStatus.RECENT)
                .build());

        return List.of(
                new SearchResponseRecord(firstAT.getId(), aptName, gu, dong, firstAT.getAreaForExclusiveUse(), firstAT.getDealDate(), firstAT.getDealAmount(), firstPC.getPredictedCost(), firstPC.isReliable()),
                new SearchResponseRecord(secondAT.getId(), aptName, gu, dong, secondAT.getAreaForExclusiveUse(), secondAT.getDealDate(), secondAT.getDealAmount(), secondPC.getPredictedCost(), secondPC.isReliable()));
    }
}
