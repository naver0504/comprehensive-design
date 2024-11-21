package com.example.comprehensivedegisn.controller.unit;

import com.example.comprehensivedegisn.adapter.domain.Gu;
import com.example.comprehensivedegisn.adapter.order.CustomPageable;
import com.example.comprehensivedegisn.adapter.order.OrderType;
import com.example.comprehensivedegisn.config.error.ControllerAdvice;
import com.example.comprehensivedegisn.config.error.CustomHttpDetail;
import com.example.comprehensivedegisn.config.error.CustomHttpExceptionResponse;
import com.example.comprehensivedegisn.controller.ApartmentTransactionController;
import com.example.comprehensivedegisn.adapter.order.CustomPageImpl;
import com.example.comprehensivedegisn.dto.request.SearchApartNameRequest;
import com.example.comprehensivedegisn.dto.request.SearchAreaRequest;
import com.example.comprehensivedegisn.dto.request.SearchCondition;
import com.example.comprehensivedegisn.dto.response.SearchApartNameResponse;
import com.example.comprehensivedegisn.dto.response.SearchAreaResponse;
import com.example.comprehensivedegisn.dto.response.SearchResponseRecord;
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

        CustomHttpExceptionResponse expectedError = new CustomHttpExceptionResponse(CustomHttpDetail.BAD_REQUEST.getStatusCode(), "검색 조건이 올바르지 않습니다.");
        BDDMockito.given(apartmentTransactionService.searchApartmentTransactions(any(Long.class), any(SearchCondition.class), any(CustomPageable.class)))
                .willThrow(new IllegalArgumentException("검색 조건이 올바르지 않습니다."));

        // when
        ResultActions resultActions = mockMvc.perform(get(url)
                .param("cachedCount", Long.toString(count))
                .param("dong", dong)
                .param("gu", gu.name())
                .param("apartmentName", apartmentName)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isBadRequest());
        String result = resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        CustomHttpExceptionResponse content = objectMapper.readValue(result, CustomHttpExceptionResponse.class);
        Assertions.assertThat(content).isEqualTo(expectedError);
        BDDMockito.verify(apartmentTransactionService, BDDMockito.times(1))
                .searchApartmentTransactions(any(Long.class), any(SearchCondition.class), any(CustomPageable.class));
    }

    @Test
    void findApartmentNames_With_Valid_Input() throws Exception {
        // given
        String url = "/apartment-transactions/apartment-name";
        Gu gu = Gu.강동구;
        String dong = "testDong";

        List<SearchApartNameResponse> expected = List.of(
                new SearchApartNameResponse("testAptName1"),
                new SearchApartNameResponse("testAptName2")
        );

        BDDMockito.given(apartmentTransactionService.findApartmentNames(any(SearchApartNameRequest.class)))
                .willReturn(expected);

        // when
        ResultActions resultActions = mockMvc.perform(get(url)
                .param("gu", gu.name())
                .param("dong", dong)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk());
        String result = resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        List<SearchApartNameResponse> content = objectMapper.readValue(result, new TypeReference<>() {});
        Assertions.assertThat(content).isEqualTo(expected);
    }

    @Test
    void findApartmentNames_With_Not_Valid_Input() throws Exception {
        // given
        String url = "/apartment-transactions/apartment-name";
        Gu gu = Gu.강동구;
        String dong = null;

        CustomHttpExceptionResponse expectedError = new CustomHttpExceptionResponse(CustomHttpDetail.BAD_REQUEST.getStatusCode(), "검색 조건이 올바르지 않습니다.");
        BDDMockito.given(apartmentTransactionService.findApartmentNames(any(SearchApartNameRequest.class)))
                .willThrow(new IllegalArgumentException("검색 조건이 올바르지 않습니다."));

        // when
        ResultActions resultActions = mockMvc.perform(get(url)
                .param("gu", gu.name())
                .param("dong", dong)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isBadRequest());
        String result = resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        CustomHttpExceptionResponse content = objectMapper.readValue(result, CustomHttpExceptionResponse.class);
        Assertions.assertThat(content).isEqualTo(expectedError);
    }

    @Test
    void findAreaForExclusive_With_Valid_Input() throws Exception {
        // given
        String url = "/apartment-transactions/area";
        Gu gu = Gu.강남구;
        String dong = "testDong";
        String apartmentName = "testAptName";

        List<SearchAreaResponse> expected = List.of(
                new SearchAreaResponse(100.1),
                new SearchAreaResponse(100.2)
        );

        BDDMockito.given(apartmentTransactionService.findAreaForExclusive(any(SearchAreaRequest.class)))
                .willReturn(expected);

        // when
        ResultActions resultActions = mockMvc.perform(get("/apartment-transactions/area")
                .param("gu", gu.name())
                .param("dong", dong)
                .param("apartmentName", apartmentName)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk());
        String result = resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        List<SearchAreaResponse> content = objectMapper.readValue(result, new TypeReference<>() {
        });
        Assertions.assertThat(content).isEqualTo(expected);
    }

    @Test
    void findAreaForExclusive_With_Not_Valid_Input() throws Exception {
        // given
        String url = "/apartment-transactions/area";
        Gu gu = Gu.강남구;
        String dong = null;
        String apartmentName = "testAptName";

        CustomHttpExceptionResponse expectedError = new CustomHttpExceptionResponse(CustomHttpDetail.BAD_REQUEST.getStatusCode(), "검색 조건이 올바르지 않습니다.");
        BDDMockito.given(apartmentTransactionService.findAreaForExclusive(any(SearchAreaRequest.class)))
                .willThrow(new IllegalArgumentException("검색 조건이 올바르지 않습니다."));

        // when
        ResultActions resultActions = mockMvc.perform(get(url)
                .param("gu", gu.name())
                .param("dong", dong)
                .param("apartmentName", apartmentName)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isBadRequest());
        String result = resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        CustomHttpExceptionResponse content = objectMapper.readValue(result, CustomHttpExceptionResponse.class);
        Assertions.assertThat(content).isEqualTo(expectedError);
    }
}
