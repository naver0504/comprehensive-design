package com.example.comprehensivedegisn.adapter.repository.apart;

import com.example.comprehensivedegisn.adapter.domain.*;
import com.example.comprehensivedegisn.adapter.order.CustomPageable;
import com.example.comprehensivedegisn.adapter.order.OrderType;
import com.example.comprehensivedegisn.adapter.repository.BaseRepositoryTest;
import com.example.comprehensivedegisn.adapter.repository.dong.DongRepository;
import com.example.comprehensivedegisn.adapter.repository.predict_cost.PredictCostRepository;
import com.example.comprehensivedegisn.dto.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.example.comprehensivedegisn.adapter.order.CustomPageable.*;
import static org.assertj.core.api.Assertions.*;

@BaseRepositoryTest
public class QuerydslApartmentTransactionRepositoryTest {


    private static final Gu TEST_GU = Gu.마포구;

    public static final String TEST_APT_NAME = "TestAptName";
    public static final String TEST_DONG = "TestDong";
    public static final double TEST_AREA = 111.1083;
    public static final int DEAL_AMOUNT = 1000;
    public static final LocalDate TEST_START_DATE = LocalDate.of(2021, 1, 1);
    public static final LocalDate TEST_END_DATE = LocalDate.of(2021, 12, 31);

    @Autowired
    private QuerydslApartmentTransactionRepository target;

    @Autowired
    private ApartmentTransactionRepository apartmentTransactionRepository;
    @Autowired
    private DongRepository dongRepository;
    @Autowired
    private PredictCostRepository predictCostRepository;

    @Test
    public void findApartmentNamesTest() {
        // given
        int repeat = 3;
        DongEntity dongEntity = dongRepository.save(DongEntity.builder()
                .gu(TEST_GU)
                .dongName(TEST_DONG)
                .build());
        for (int i = 0; i < repeat; i++) {
            for (int j = 0; j < 2; j++) {
                apartmentTransactionRepository.save(ApartmentTransaction.builder()
                        .apartmentName(TEST_APT_NAME + i)
                        .dongEntity(dongEntity)
                        .build());
            }
        }

        // when
        List<SearchApartNameResponse> result = target.findApartmentNames(TEST_GU, TEST_DONG);

        // then
        assertThat(result).hasSize(repeat);
        assertThat(result)
                .extracting(SearchApartNameResponse::apartmentName)
                .allMatch(apartmentName -> apartmentName.startsWith(TEST_APT_NAME));
    }

    @Test
    public void findAreaForExclusiveTest() {
        // given
        int repeat = 3;
        List<Double> expected = new ArrayList<>(repeat);

        DongEntity dongEntity = dongRepository.save(DongEntity.builder()
                .gu(TEST_GU)
                .dongName(TEST_DONG)
                .build());
        for (int i = 1; i <= repeat; i++) {
            double area = TEST_AREA * i;
            expected.add(area);
            for (int j = 0; j < 2; j++) {
                apartmentTransactionRepository.save(ApartmentTransaction.builder()
                        .apartmentName(TEST_APT_NAME)
                        .dongEntity(dongEntity)
                        .areaForExclusiveUse(area)
                        .build());
            }
        }

        // when
        List<SearchAreaResponse> result = target.findAreaForExclusive(TEST_GU, TEST_DONG, TEST_APT_NAME);

        // then
        assertThat(result).hasSize(repeat);
        assertThat(result)
                .extracting(SearchAreaResponse::areaForExclusiveUse)
                .containsExactlyInAnyOrderElementsOf(expected);
    }

    @ParameterizedTest
    @MethodSource("searchStream")
    void searchTest(Long cachedCount, SearchCondition searchCondition, CustomPageable customPageable) {
        // given
        setEntities();

        // when
        Page<SearchResponseRecord> result = target.searchApartmentTransactions(cachedCount, searchCondition, customPageable);
        List<SearchResponseRecord> contents = result.getContent();

        // then
        assertThat(result.getSize()).isEqualTo(DEFAULT_SIZE);
        assertThatGuEq(searchCondition, contents);
        assertThatDongEq(searchCondition, contents);
        asserThatAptNameEq(searchCondition, contents);
        assertThatLocalDateBetween(searchCondition, contents);
        assertThatCacheEq(cachedCount, searchCondition, customPageable, result);
        assertThatAreaEq(searchCondition, contents);
        assertThatReliabilityEq(searchCondition, contents);
    }

    private void setEntities() {
        DongEntity dongEntity = dongRepository.save(DongEntity.builder()
                .gu(TEST_GU)
                .dongName(TEST_DONG)
                .build());
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < DEFAULT_SIZE; j++) {
                ApartmentTransaction apartmentTransaction = apartmentTransactionRepository.save(ApartmentTransaction.builder()
                        .apartmentName(TEST_APT_NAME)
                        .areaForExclusiveUse(TEST_AREA * i)
                        .dealAmount(DEAL_AMOUNT * i)
                        .dongEntity(dongEntity)
                        .dealDate(TEST_START_DATE.plusMonths(i))
                        .build());

                if(j % 2 == 0) {
                    predictCostRepository.save(PredictCost.builder()
                            .apartmentTransaction(apartmentTransaction)
                            .predictedCost(1000L)
                            .isReliable(true)
                            .predictStatus(PredictStatus.RECENT)
                            .build());
                } else {
                    predictCostRepository.save(PredictCost.builder()
                            .apartmentTransaction(apartmentTransaction)
                            .predictedCost(1000L)
                            .isReliable(false)
                            .predictStatus(PredictStatus.RECENT)
                            .build());
                }
            }
        }
    }

    public static Stream<Arguments> searchStream() {
        return Stream.of(
                Arguments.of(100L , new SearchCondition(TEST_GU, null, null, null, TEST_START_DATE, TEST_END_DATE, Reliability.ALL), new CustomPageable(OrderType.DEAL_DATE, 1)),
                Arguments.of(null , new SearchCondition(TEST_GU, TEST_DONG, TEST_APT_NAME, TEST_AREA, TEST_START_DATE, TEST_END_DATE, Reliability.UNRELIABLE), new CustomPageable(OrderType.DEAL_AMOUNT, 0)),
                Arguments.of(100L , new SearchCondition(TEST_GU, TEST_DONG, TEST_APT_NAME, null, TEST_START_DATE, null, Reliability.RELIABLE), new CustomPageable(OrderType.DEAL_AMOUNT, 3)),
                Arguments.of(null , new SearchCondition(TEST_GU, TEST_DONG, TEST_APT_NAME, TEST_AREA, null, null, Reliability.ALL), new CustomPageable(OrderType.DEAL_AMOUNT, 0)),
                Arguments.of(100L , new SearchCondition(Gu.NONE, null, null, null, null, null, Reliability.UNRELIABLE), new CustomPageable(OrderType.DEAL_AMOUNT, 2))

        );
    }

    private void assertThatCacheEq(Long cachedCount, SearchCondition searchCondition, CustomPageable customPageable, Page<SearchResponseRecord> result) {
        if(cachedCount != null) {
            assertThat(result.getTotalElements()).isEqualTo(cachedCount);
        } else {
            assertThat(result.getTotalElements()).isEqualTo(target.searchApartmentTransactions(null, searchCondition, customPageable).getTotalElements());
        }
    }

    private void assertThatLocalDateBetween(SearchCondition searchCondition, List<SearchResponseRecord> contents) {
        if(searchCondition.getStartDealDate() != null && searchCondition.getEndDealDate() != null) {
            assertThat(contents)
                    .extracting(SearchResponseRecord::dealDate)
                    .allMatch(dealDate -> dealDate.isAfter(searchCondition.getStartDealDate()) || dealDate.isEqual(searchCondition.getStartDealDate()))
                    .allMatch(dealDate -> dealDate.isBefore(searchCondition.getEndDealDate()) || dealDate.isEqual(searchCondition.getEndDealDate()));;
        }
    }

    private void asserThatAptNameEq(SearchCondition searchCondition, List<SearchResponseRecord> contents) {
        if(searchCondition.getApartmentName() != null) {
            assertThat(contents)
                    .extracting(SearchResponseRecord::apartmentName)
                    .allMatch(apartmentName -> apartmentName.contains(searchCondition.getApartmentName()));
        }
    }

    private void assertThatDongEq(SearchCondition searchCondition, List<SearchResponseRecord> contents) {
        if(searchCondition.getDong() != null) {
            assertThat(contents)
                    .extracting(SearchResponseRecord::region)
                    .allMatch(region -> region.contains(searchCondition.getDong()));
        }
    }

    private void assertThatGuEq(SearchCondition searchCondition, List<SearchResponseRecord> contents) {
        if(searchCondition.getGu() != Gu.NONE) {
            assertThat(contents)
                    .extracting(SearchResponseRecord::region)
                    .allMatch(region -> region.contains(searchCondition.getGu().toString()));
        }
    }

    private void assertThatAreaEq(SearchCondition searchCondition, List<SearchResponseRecord> contents) {
        if(searchCondition.getAreaForExclusiveUse() != null) {
            assertThat(contents)
                    .extracting(SearchResponseRecord::areaForExclusiveUse)
                    .allMatch(area -> area.equals(searchCondition.getAreaForExclusiveUse()));
        }
    }

    private void assertThatReliabilityEq(SearchCondition searchCondition, List<SearchResponseRecord> contents) {
        if(searchCondition.getReliability() != Reliability.ALL) {
            assertThat(contents)
                    .extracting(SearchResponseRecord::isReliable)
                    .allMatch(isReliable -> {
                        if(searchCondition.getReliability() == Reliability.RELIABLE) {
                            return isReliable;
                        } else {
                            return !isReliable;
                        }
                    });
        }
    }


}