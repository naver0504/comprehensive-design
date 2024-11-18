package com.example.comprehensivedegisn.adapter.repository.apart;

import com.example.comprehensivedegisn.adapter.domain.Gu;
import com.example.comprehensivedegisn.adapter.order.CustomPageable;
import com.example.comprehensivedegisn.adapter.order.OrderType;
import com.example.comprehensivedegisn.adapter.repository.BaseRepositoryTest;
import com.example.comprehensivedegisn.dto.SearchCondition;
import com.example.comprehensivedegisn.dto.SearchResponseRecord;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@BaseRepositoryTest
class QuerydslApartmentTransactionRepositoryTest {

    @Autowired
    private QuerydslApartmentTransactionRepository querydslApartmentTransactionRepository;

    @ParameterizedTest
    @MethodSource("searchStream")
    void searchTest(Long cachedCount, SearchCondition searchCondition, CustomPageable customPageable) {

        // when
        Page<SearchResponseRecord> result = querydslApartmentTransactionRepository.searchApartmentTransactions(cachedCount, searchCondition, customPageable);
        List<SearchResponseRecord> contents = result.getContent();
        // then
        
        assertThat(result.getSize()).isEqualTo(CustomPageable.DEFAULT_SIZE);
        assertThatGuEq(searchCondition, contents);
        assertThatDongEq(searchCondition, contents);
        asserThatAptNameEq(searchCondition, contents);
        assertThatLocalDateBetween(searchCondition, contents);
        assertThatCacheEq(cachedCount, result);
    }

    private static Stream<Arguments> searchStream() {
        return Stream.of(
                Arguments.of(100L , new SearchCondition(Gu.마포구, null, null, null, LocalDate.of(2021, 1, 1), LocalDate.of(2021, 12, 31), SearchCondition.Reliability.ALL), new CustomPageable(OrderType.DEAL_DATE, 3)),
                Arguments.of(null , new SearchCondition(Gu.마포구, "아현동", "마포센트럴 아이파크", 111.1083, LocalDate.of(2021, 1, 1), LocalDate.of(2021, 12, 31), SearchCondition.Reliability.UNRELIABLE), new CustomPageable(OrderType.DEAL_AMOUNT, 0)),
                Arguments.of(100L , new SearchCondition(Gu.마포구, null, "마포센트럴 아이파크", null, LocalDate.of(2021, 1, 1), null, SearchCondition.Reliability.RELIABLE), new CustomPageable(OrderType.DEAL_AMOUNT, 3)),
                Arguments.of(null , new SearchCondition(Gu.마포구, "아현동", null, 111.1083, null, null, SearchCondition.Reliability.ALL), new CustomPageable(OrderType.DEAL_AMOUNT, 0)),
                Arguments.of(100L , new SearchCondition(Gu.NONE, null, "마포센트럴 아이파크", null, null, null, SearchCondition.Reliability.UNRELIABLE), new CustomPageable(OrderType.DEAL_AMOUNT, 3))

        );
    }

    private static void assertThatCacheEq(Long cachedCount, Page<SearchResponseRecord> result) {
        if(cachedCount != null) {
            assertThat(result.getTotalElements()).isEqualTo(cachedCount);
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


}