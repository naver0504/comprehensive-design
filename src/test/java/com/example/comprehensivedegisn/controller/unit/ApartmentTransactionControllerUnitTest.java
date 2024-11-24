package com.example.comprehensivedegisn.controller.unit;

import com.example.comprehensivedegisn.adapter.domain.ApartmentTransaction;
import com.example.comprehensivedegisn.adapter.domain.DealingGbn;
import com.example.comprehensivedegisn.adapter.domain.DongEntity;
import com.example.comprehensivedegisn.adapter.domain.Gu;
import com.example.comprehensivedegisn.adapter.order.CustomPageable;
import com.example.comprehensivedegisn.adapter.order.OrderType;
import com.example.comprehensivedegisn.api_client.predict.PredictAiApiClient;
import com.example.comprehensivedegisn.config.error.ControllerAdvice;
import com.example.comprehensivedegisn.config.error.CustomHttpDetail;
import com.example.comprehensivedegisn.config.error.CustomHttpExceptionResponse;
import com.example.comprehensivedegisn.controller.ApartmentTransactionController;
import com.example.comprehensivedegisn.adapter.order.CustomPageImpl;
import com.example.comprehensivedegisn.dto.request.SearchApartNameRequest;
import com.example.comprehensivedegisn.dto.request.SearchAreaRequest;
import com.example.comprehensivedegisn.dto.request.SearchCondition;
import com.example.comprehensivedegisn.dto.response.*;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.comprehensivedegisn.adapter.repository.apart.QuerydslApartmentTransactionRepositoryTest.*;
import static com.example.comprehensivedegisn.adapter.repository.apart.QuerydslApartmentTransactionRepositoryTest.TEST_END_DATE;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ApartmentTransactionControllerUnitTest {

    private static final Logger log = LoggerFactory.getLogger(ApartmentTransactionControllerUnitTest.class);
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @InjectMocks
    private ApartmentTransactionController target;

    @Mock
    private ApartmentTransactionService apartmentTransactionService;

    @Mock
    private PredictAiApiClient predictAiApiClient;

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
        CustomPageImpl<SearchResponseRecord> resultPageImpl = objectMapper.readValue(result, new TypeReference<>() {
        });
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
        List<SearchApartNameResponse> content = objectMapper.readValue(result, new TypeReference<>() {
        });
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

    @Test
    void findTransactionDetail_With_Valid_Input() throws Exception {
        // given
        long id = 100L;
        String url = "/apartment-transactions/" + id;

        TransactionDetailResponse expected = new TransactionDetailResponse(LocalDate.of(2021, 1, 1), 1990, 100.1, DealingGbn.중개거래, "testAptName", 10000, 1000L, null);
        BDDMockito.given(apartmentTransactionService.findTransactionDetail(id))
                .willReturn(expected);

        // when
        ResultActions resultActions = mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk());
        String result = resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    void findTransactionDetail_With_Not_Valid_Input() throws Exception {
        // given
        long id = -100L;
        String url = "/apartment-transactions/" + id;

        CustomHttpExceptionResponse expectedError = new CustomHttpExceptionResponse(CustomHttpDetail.BAD_REQUEST.getStatusCode(), "잘못 된 거래 Id 입니다.");
        BDDMockito.given(apartmentTransactionService.findTransactionDetail(id))
                .willThrow(new IllegalArgumentException("잘못 된 거래 Id 입니다."));
        // when
        ResultActions resultActions = mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isBadRequest());
        String result = resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        CustomHttpExceptionResponse content = objectMapper.readValue(result, CustomHttpExceptionResponse.class);
        Assertions.assertThat(content).isEqualTo(expectedError);
    }

    @Test
    void findApartmentTransactionsForGraph_With_Valid_Input() throws Exception {
        // given
        Long id = 5L;
        String url = "/apartment-transactions/" + id + "/graph";
        Gu gu = Gu.성북구;
        String dong = TEST_DONG;
        String aptName = TEST_APT_NAME;
        double area = TEST_AREA;

        int repeatCount = 12;
        LocalDate dealDate = TEST_END_DATE;

        DongEntity dongEntity = DongEntity.builder().gu(gu).dongName(dong).build();
        List<ApartmentTransaction> apartmentTransactions = new ArrayList<>();
        Map<LocalDate, Long> predictData = new HashMap<>();
        for (int i = 0; i < repeatCount; i++) {
            predictData.put(dealDate.minusMonths(i), 1000L * i);
            apartmentTransactions.add(ApartmentTransaction.builder()
                    .dongEntity(dongEntity)
                    .apartmentName(aptName)
                    .areaForExclusiveUse(area)
                    .dealDate(dealDate.minusMonths(i))
                    .build());
        }

        ApartmentTransaction apartmentTransaction = apartmentTransactions.get(0);
        RealTransactionGraphResponse realTransactionGraphResponse = new RealTransactionGraphResponse(apartmentTransactions);
        PredictAiResponse predictAiResponse = new PredictAiResponse(predictData);
        GraphResponse expected = new GraphResponse(realTransactionGraphResponse, predictAiResponse);
        BDDMockito.given(apartmentTransactionService.findById(any(Long.class)))
                .willReturn(apartmentTransaction);
        BDDMockito.given(apartmentTransactionService.findApartmentTransactionsForGraph(any(ApartmentTransaction.class)))
                .willReturn(realTransactionGraphResponse);
        BDDMockito.given(predictAiApiClient.callApi(any(ApartmentTransaction.class)))
                .willReturn(predictAiResponse);

        // when
        ResultActions resultActions = mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON));
        // then
        resultActions.andExpect(status().isOk());
        String result = resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        GraphResponse content = objectMapper.readValue(result, GraphResponse.class);
        Assertions.assertThat(content).isEqualTo(expected);
    }

    @Test
    void findApartmentTransactionsForGraph_With_Not_Valid_Input() throws Exception {
        // given
        long id = -5L;
        String url = "/apartment-transactions/" + id + "/graph";


        BDDMockito.given(apartmentTransactionService.findById(any(Long.class)))
                .willThrow(new IllegalArgumentException("잘못 된 거래 Id 입니다."));

        CustomHttpExceptionResponse expectedError = new CustomHttpExceptionResponse(CustomHttpDetail.BAD_REQUEST.getStatusCode(), "잘못 된 거래 Id 입니다.");

        // when
        ResultActions resultActions = mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON));
        // then
        resultActions.andExpect(status().isBadRequest());
        String result = resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        CustomHttpExceptionResponse content = objectMapper.readValue(result, CustomHttpExceptionResponse.class);
        Assertions.assertThat(content).isEqualTo(expectedError);
    }

}