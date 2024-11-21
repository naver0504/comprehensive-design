package com.example.comprehensivedegisn.service.unit;

import com.example.comprehensivedegisn.adapter.ApartmentTransactionAdapter;
import com.example.comprehensivedegisn.adapter.domain.Gu;
import com.example.comprehensivedegisn.adapter.order.CustomPageable;
import com.example.comprehensivedegisn.adapter.order.OrderType;
import com.example.comprehensivedegisn.dto.*;
import com.example.comprehensivedegisn.dto.request.SearchApartNameRequest;
import com.example.comprehensivedegisn.dto.request.SearchAreaRequest;
import com.example.comprehensivedegisn.dto.request.SearchCondition;
import com.example.comprehensivedegisn.dto.response.SearchApartNameResponse;
import com.example.comprehensivedegisn.dto.response.SearchAreaResponse;
import com.example.comprehensivedegisn.dto.response.SearchResponseRecord;
import com.example.comprehensivedegisn.service.ApartmentTransactionService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.stream.Stream;

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

    @ParameterizedTest
    @MethodSource("provideNotValidSearchCondition")
    void searchApartmentTransactions_With_Not_Valid_Input(Long count, SearchCondition notValidSearchCondition) {
        // given
        CustomPageable customPageable = new CustomPageable(OrderType.DEAL_DATE, 3);
        // when & then
        Assertions.assertThatThrownBy(() -> apartmentTransactionService.searchApartmentTransactions(count, notValidSearchCondition, customPageable))
                .isInstanceOf(IllegalArgumentException.class);
        BDDMockito.verify(apartmentTransactionAdapter, BDDMockito.times(0)).searchApartmentTransactions(count, notValidSearchCondition, customPageable);
    }

    @Test
    void findApartmentNames_With_Valid_Input() {
        // given
        String dongName = "아현동";
        SearchApartNameRequest request = new SearchApartNameRequest(Gu.마포구, dongName);
        List<SearchApartNameResponse> expected = List.of(
                new SearchApartNameResponse("마포센트럴 아이파크"),
                new SearchApartNameResponse("마포센트럴 아이파크2")
        );
        BDDMockito.given(apartmentTransactionAdapter.findApartmentNames(request.getGu(), request.getDong())).willReturn(expected);

        // when
        List<SearchApartNameResponse> result = apartmentTransactionService.findApartmentNames(request);

        // then
        Assertions.assertThat(result).isEqualTo(expected);
        BDDMockito.verify(apartmentTransactionAdapter, BDDMockito.times(1)).findApartmentNames(request.getGu(), request.getDong());
    }

    @ParameterizedTest
    @MethodSource("provideNotValidSearchApartNameRequest")
    void findApartmentNames_With_Not_Valid_Input(SearchApartNameRequest request) {
        // when & then
        Assertions.assertThatThrownBy(() -> apartmentTransactionService.findApartmentNames(request))
                .isInstanceOf(IllegalArgumentException.class);
        BDDMockito.verify(apartmentTransactionAdapter, BDDMockito.times(0)).findApartmentNames(request.getGu(), request.getDong());
    }

    @Test
    void findAreaForExclusive_With_Valid_Input() {
        // given
        SearchAreaRequest request = new SearchAreaRequest(Gu.마포구, "아현동", "마포센트럴 아이파크");
        List<SearchAreaResponse> expected = List.of(
                new SearchAreaResponse(100.1),
                new SearchAreaResponse(100.2)
        );
        BDDMockito.given(apartmentTransactionAdapter.findAreaForExclusive(request.getGu(), request.getDong(), request.getApartmentName())).willReturn(expected);

        // when
        List<SearchAreaResponse> result = apartmentTransactionService.findAreaForExclusive(request);

        // then
        Assertions.assertThat(result).isEqualTo(expected);
        BDDMockito.verify(apartmentTransactionAdapter, BDDMockito.times(1)).findAreaForExclusive(request.getGu(), request.getDong(), request.getApartmentName());
    }

    @ParameterizedTest
    @MethodSource("provideNotValidSearchAreaRequest")
    void findAreaForExclusive_With_Not_Valid_Input(SearchAreaRequest request) {
        // when & then
        Assertions.assertThatThrownBy(() -> apartmentTransactionService.findAreaForExclusive(request))
                .isInstanceOf(IllegalArgumentException.class);
        BDDMockito.verify(apartmentTransactionAdapter, BDDMockito.times(0)).findAreaForExclusive(request.getGu(), request.getDong(), request.getApartmentName());
    }


    public static Stream<Arguments> provideNotValidSearchCondition() {
        return Stream.of(
                Arguments.of(100L, new SearchCondition(Gu.마포구, null, "마포센트럴 아이파크", 111.1083, null, null, Reliability.ALL)),
                Arguments.of(100L, new SearchCondition(Gu.마포구, "아현동", null, 111.1083, null, null, Reliability.ALL)),
                Arguments.of(200L, new SearchCondition(Gu.NONE, "아현동", "마포센트럴 아이파크", null, null, null, Reliability.ALL)),
                Arguments.of(300L, new SearchCondition(Gu.NONE, "아현동", "마포센트럴 아이파크", 111.1083, null, null, null))
        );
    }

    public static Stream<Arguments> provideNotValidSearchApartNameRequest() {
        return Stream.of(
                Arguments.of(new SearchApartNameRequest(Gu.마포구, null)),
                Arguments.of(new SearchApartNameRequest(Gu.마포구, " ")),
                Arguments.of(new SearchApartNameRequest(null, "아현동")),
                Arguments.of(new SearchApartNameRequest(Gu.NONE, null))
        );
    }

    public static Stream<Arguments> provideNotValidSearchAreaRequest() {
        return Stream.of(
                Arguments.of(new SearchAreaRequest(Gu.마포구, null, "마포센트럴 아이파크")),
                Arguments.of(new SearchAreaRequest(Gu.마포구, "아현동", null)),
                Arguments.of(new SearchAreaRequest(null, "아현동", "마포센트럴 아이파크")),
                Arguments.of(new SearchAreaRequest(Gu.NONE, "아현동", "마포센트럴 아이파크"))
        );
    }
}