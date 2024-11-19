package com.example.comprehensivedegisn.controller.unit;

import com.example.comprehensivedegisn.adapter.domain.Gu;
import com.example.comprehensivedegisn.adapter.order.CustomPageable;
import com.example.comprehensivedegisn.adapter.order.OrderType;
import com.example.comprehensivedegisn.config.ControllerAdvice;
import com.example.comprehensivedegisn.controller.ApartmentTransactionController;
import com.example.comprehensivedegisn.adapter.order.CustomPageImpl;
import com.example.comprehensivedegisn.dto.SearchCondition;
import com.example.comprehensivedegisn.dto.SearchResponseRecord;
import com.example.comprehensivedegisn.service.ApartmentTransactionService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ApartmentTransactionControllerUnitTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @InjectMocks
    private ApartmentTransactionController target;

    @Mock
    private ApartmentTransactionService apartmentTransactionService;

    @BeforeEach
    void setUp() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());

        this.mockMvc = MockMvcBuilders
                .standaloneSetup(target)
                .setControllerAdvice(new ControllerAdvice())
                .build();
    }

    @Test
    void searchApartmentTransactions_With_Valid_Input() throws Exception {
        // given
        String url = "/apartment-transactions";
        long count = 100L;
        Gu gu = Gu.마포구;
        String dong = "testDong";
        String apartmentName = "testApartmentName";

        OrderType orderType = OrderType.AREA_FOR_EXCLUSIVE_USE;
        int page = 1;

        List<SearchResponseRecord> expectedContents = List.of(
                new SearchResponseRecord(1L, apartmentName, gu, dong, 100.1, LocalDate.of(2021, 1, 1), 10300, 1000L, true),
                new SearchResponseRecord(2L, apartmentName, gu, dong, 100.2, LocalDate.of(2021, 1, 2), 10200, 1000L, true)
                );

        PageImpl<SearchResponseRecord> expectedResult = new PageImpl<>(expectedContents, new CustomPageable(orderType, 1).toPageable(), count);
        BDDMockito.given(apartmentTransactionService.searchApartmentTransactions(any(Long.class), any(SearchCondition.class), any(CustomPageable.class)))
                .willReturn(expectedResult);

        // when
        ResultActions resultActions = mockMvc.perform(get(url)
                .param("cachedCount", Long.toString(count))
                .param("dong", dong)
                .param("gu", gu.name())
                .param("apartmentName", apartmentName)
                .param("orderType", orderType.name())
                .param("page", Integer.toString(page))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isOk());
        String result = resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println(result);
        CustomPageImpl<SearchResponseRecord> resultPageImpl = objectMapper.readValue(result, new TypeReference<>() {});
        List<SearchResponseRecord> resultContents = resultPageImpl.getContent();

        Assertions.assertThat(resultContents).isEqualTo(expectedContents);

        BDDMockito.verify(apartmentTransactionService, BDDMockito.times(1))
                .searchApartmentTransactions(any(Long.class), any(SearchCondition.class), any(CustomPageable.class));

    }

    @Test
    void searchApartmentTransactions_With_Not_Valid_Input() throws Exception {
        // given
        String url = "/apartment-transactions";
        long count = 100L;
        Gu gu = Gu.마포구;
        String dong = null;
        String apartmentName = "testApartmentName";

        BDDMockito.given(apartmentTransactionService.searchApartmentTransactions(any(Long.class), any(SearchCondition.class), any(CustomPageable.class)))
                .willThrow(new IllegalStateException("검색 조건이 올바르지 않습니다."));

        // when
        ResultActions resultActions = mockMvc.perform(get(url)
                .param("cachedCount", Long.toString(count))
                .param("dong", dong)
                .param("gu", gu.name())
                .param("apartmentName", apartmentName));

        // then
        resultActions.andExpect(status().isBadRequest());
        BDDMockito.verify(apartmentTransactionService, BDDMockito.times(1))
                .searchApartmentTransactions(any(Long.class), any(SearchCondition.class), any(CustomPageable.class));
    }
}
