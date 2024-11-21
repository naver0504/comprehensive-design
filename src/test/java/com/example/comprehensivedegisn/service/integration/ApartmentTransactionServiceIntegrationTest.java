package com.example.comprehensivedegisn.service.integration;

import com.example.comprehensivedegisn.adapter.domain.*;
import com.example.comprehensivedegisn.adapter.order.CustomPageable;
import com.example.comprehensivedegisn.adapter.order.OrderType;
import com.example.comprehensivedegisn.adapter.repository.apart.ApartmentTransactionRepository;
import com.example.comprehensivedegisn.adapter.repository.dong.DongRepository;
import com.example.comprehensivedegisn.adapter.repository.predict_cost.PredictCostRepository;
import com.example.comprehensivedegisn.dto.*;
import com.example.comprehensivedegisn.service.ApartmentTransactionService;
import com.example.comprehensivedegisn.service.integration.config.IntegrationTestForService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.ArrayList;
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

    @ParameterizedTest
    @MethodSource("com.example.comprehensivedegisn.service.unit.ApartmentTransactionServiceUnitTest#provideNotValidSearchCondition")
    void searchApartmentTransactions_With_Not_Valid_Input(Long count, SearchCondition notValidSearchCondition) {
        // given
       CustomPageable customPageable = new CustomPageable(OrderType.DEAL_DATE, 3);

        // when
        Assertions.assertThatThrownBy(() -> target.searchApartmentTransactions(count, notValidSearchCondition, customPageable))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findApartmentNames_With_Valid_Input() {
        // given
        Gu gu = Gu.서초구;
        String dong = TEST_DONG;
        String aptName = TEST_APT_NAME;

        int repeat = 3;
        DongEntity dongEntity = dongRepository.save(DongEntity.builder()
                .gu(gu)
                .dongName(dong)
                .build());
        for (int i = 0; i < repeat; i++) {
            apartmentTransactionRepository.save(ApartmentTransaction.builder()
                    .apartmentName(aptName + i)
                    .dongEntity(dongEntity)
                    .build());
        }

        SearchApartNameRequest request = new SearchApartNameRequest(gu, dong);

        // when
        List<SearchApartNameResponse> result = target.findApartmentNames(request);
        // then
        Assertions.assertThat(result).hasSize(repeat);
        Assertions.assertThat(result)
                .extracting(SearchApartNameResponse::apartmentName)
                .allMatch(name -> name.startsWith(aptName));
    }

    @ParameterizedTest
    @MethodSource("com.example.comprehensivedegisn.service.unit.ApartmentTransactionServiceUnitTest#provideNotValidSearchApartNameRequest")
    void findApartmentNames_With_Not_Valid_Input(SearchApartNameRequest request) {
        Assertions.assertThatThrownBy(() -> target.findApartmentNames(request))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    void findAreaForExclusive_With_Valid_Input() {
        // given
        Gu gu = Gu.서초구;
        String dong = TEST_DONG;
        String aptName = TEST_APT_NAME;
        double area = TEST_AREA;

        int repeat = 3;
        List<Double> expected = new ArrayList<>(repeat);

        DongEntity dongEntity = dongRepository.save(DongEntity.builder()
                .gu(gu)
                .dongName(dong)
                .build());
        for (int i = 0; i < repeat; i++) {
            double areaForExclusiveUse = area * i;
            expected.add(areaForExclusiveUse);
            apartmentTransactionRepository.save(ApartmentTransaction.builder()
                    .apartmentName(aptName)
                    .areaForExclusiveUse(areaForExclusiveUse)
                    .dongEntity(dongEntity)
                    .build());
        }

        SearchAreaRequest request = new SearchAreaRequest(gu, dong, aptName);

        // when
        List<SearchAreaResponse> result = target.findAreaForExclusive(request);
        // then
        Assertions.assertThat(result).hasSize(repeat);
        Assertions.assertThat(result)
                .extracting(SearchAreaResponse::areaForExclusiveUse)
                .containsExactlyElementsOf(expected);
    }

    @ParameterizedTest
    @MethodSource("com.example.comprehensivedegisn.service.unit.ApartmentTransactionServiceUnitTest#provideNotValidSearchAreaRequest")
    void findAreaForExclusive_With_Not_Valid_Input(SearchAreaRequest request) {
        Assertions.assertThatThrownBy(() -> target.findAreaForExclusive(request))
                .isInstanceOf(IllegalArgumentException.class);
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
