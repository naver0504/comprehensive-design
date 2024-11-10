package com.example.comprehensivedegisn.adapter.repository.apart;

import com.example.comprehensivedegisn.adapter.domain.Gu;
import com.example.comprehensivedegisn.adapter.order.CustomPageable;
import com.example.comprehensivedegisn.adapter.order.OrderType;
import com.example.comprehensivedegisn.adapter.repository.BaseRepositoryTest;
import com.example.comprehensivedegisn.dto.SearchCondition;
import com.example.comprehensivedegisn.dto.TransactionResponseRecord;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@BaseRepositoryTest
class QuerydslApartmentTransactionRepositoryTest {

    @Autowired
    private QuerydslApartmentTransactionRepository querydslApartmentTransactionRepository;

    @ParameterizedTest
    @MethodSource("searchStream")
    void searchTest(Long cachedCount, SearchCondition searchCondition, CustomPageable customPageable) {

        // when
        Page<TransactionResponseRecord> result = querydslApartmentTransactionRepository.searchApartmentTransactions(cachedCount, searchCondition, customPageable);
        // then
        Assertions.assertThat(result.getSize()).isEqualTo(CustomPageable.DEFAULT_SIZE);
        if(searchCondition.getStartDealDate() != null && searchCondition.getEndDealDate() != null) {
            Assertions.assertThat(result.getContent())
                    .extracting(TransactionResponseRecord::dealDate)
                    .allMatch(dealDate -> dealDate.isAfter(searchCondition.getStartDealDate()) || dealDate.isEqual(searchCondition.getStartDealDate()))
                    .allMatch(dealDate -> dealDate.isBefore(searchCondition.getEndDealDate()) || dealDate.isEqual(searchCondition.getEndDealDate()));;
        }
        if(cachedCount != null) {
            Assertions.assertThat(result.getTotalElements()).isEqualTo(cachedCount);
        }
    }

    private static Stream<Arguments> searchStream() {
        return Stream.of(
                Arguments.of(100L , new SearchCondition(Gu.마포구, null, null, null, LocalDate.of(2021, 1, 1), LocalDate.of(2021, 12, 31)), new CustomPageable(OrderType.DEAL_DATE, 3)),
                Arguments.of(null , new SearchCondition(Gu.마포구, "아현동", "마포센트럴 아이파크", 111.1083, LocalDate.of(2021, 1, 1), LocalDate.of(2021, 12, 31)), new CustomPageable(OrderType.DEAL_AMOUNT, 0)),
                Arguments.of(100L , new SearchCondition(Gu.마포구, null, "마포센트럴 아이파크", null, LocalDate.of(2021, 1, 1), null), new CustomPageable(OrderType.DEAL_AMOUNT, 3)),
                Arguments.of(null , new SearchCondition(Gu.마포구, "아현동", null, 111.1083, null, null), new CustomPageable(OrderType.DEAL_AMOUNT, 0)),
                Arguments.of(100L , new SearchCondition(Gu.NONE, null, "마포센트럴 아이파크", null, null, null), new CustomPageable(OrderType.DEAL_AMOUNT, 3))

        );
    }
}