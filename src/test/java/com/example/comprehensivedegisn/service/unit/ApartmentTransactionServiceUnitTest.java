package com.example.comprehensivedegisn.service.unit;

import com.example.comprehensivedegisn.adapter.ApartmentTransactionAdapter;
import com.example.comprehensivedegisn.adapter.domain.Gu;
import com.example.comprehensivedegisn.adapter.order.CustomPageable;
import com.example.comprehensivedegisn.adapter.order.OrderType;
import com.example.comprehensivedegisn.dto.Reliability;
import com.example.comprehensivedegisn.dto.SearchCondition;
import com.example.comprehensivedegisn.dto.SearchResponseRecord;
import com.example.comprehensivedegisn.service.ApartmentTransactionService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ApartmentTransactionServiceUnitTest {

    @InjectMocks
    private ApartmentTransactionService apartmentTransactionService;

    @Mock
    private ApartmentTransactionAdapter apartmentTransactionAdapter;

    @Test
    void searchApartmentTransactions_With_Valid_Input() {
        // given
        Long count = 100L;
        SearchCondition searchCondition = new SearchCondition(Gu.마포구, "아현동", "마포센트럴 아이파크", 111.1083, null, null, Reliability.ALL);
        CustomPageable customPageable = new CustomPageable(OrderType.DEAL_DATE, 3);
        List<SearchResponseRecord> expectedContents = List.of(
                new SearchResponseRecord(1L, "마포센트럴 아이파크", Gu.마포구, "아현동", 111.1083, null, 0, 10L, false),
                new SearchResponseRecord(2L, "마포센트럴 아이파크", Gu.마포구, "아현동", 111.1083, null, 0, 20L, false),
                new SearchResponseRecord(3L, "마포센트럴 아이파크", Gu.마포구, "아현동", 111.1083, null, 0, 30L, false)
        );
        Page<SearchResponseRecord> expectedPage = new PageImpl<>(expectedContents, customPageable.toPageable(), count);
        BDDMockito.given(apartmentTransactionAdapter.searchApartmentTransactions(count, searchCondition, customPageable)).willReturn(expectedPage);

        // when
        Page<SearchResponseRecord> result = apartmentTransactionService.searchApartmentTransactions(count, searchCondition, customPageable);

        // then
        Assertions.assertThat(result.getContent()).isEqualTo(expectedContents);
        BDDMockito.verify(apartmentTransactionAdapter, BDDMockito.times(1)).searchApartmentTransactions(count, searchCondition, customPageable);
    }

    @Test
    void searchApartmentTransactions_With_Not_Valid_Input() {
        // given
        Long count = 100L;
        SearchCondition notValidSearchCondition = new SearchCondition(Gu.마포구, null, "마포센트럴 아이파크", 111.1083, null, null, Reliability.ALL);
        CustomPageable customPageable = new CustomPageable(OrderType.DEAL_DATE, 3);

        // when & then
        Assertions.assertThatThrownBy(() -> apartmentTransactionService.searchApartmentTransactions(count, notValidSearchCondition, customPageable))
                .isInstanceOf(IllegalStateException.class);
        BDDMockito.verify(apartmentTransactionAdapter, BDDMockito.times(0)).searchApartmentTransactions(count, notValidSearchCondition, customPageable);
    }


}