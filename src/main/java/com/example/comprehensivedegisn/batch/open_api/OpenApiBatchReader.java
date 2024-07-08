package com.example.comprehensivedegisn.batch.open_api;

import com.example.comprehensivedegisn.api.OpenApiClient;
import com.example.comprehensivedegisn.api.OpenApiUtils;
import com.example.comprehensivedegisn.api.dto.ApartmentDetailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;

@RequiredArgsConstructor
@Slf4j
public class OpenApiBatchReader implements ItemStreamReader<ApartmentDetailResponse> {

    private final OpenApiClient openApiClient;

    private int pageNo = 1;
    private LocalDate contractDate = LocalDate.now().minusMonths(1);

    @Value("#{jobParameters[regionalCode]}")
    private String regionalCode;

    @Override
    public ApartmentDetailResponse read()  {
        ApartmentDetailResponse response = openApiClient.request(pageNo, contractDate, regionalCode);

        if(OpenApiUtils.isLimitExceeded(response)) throw new RuntimeException("Limit Exceeded");
        if(OpenApiUtils.isEndOfData(response)) return null;

        if(response.isEndOfPage()){
            contractDate = OpenApiUtils.getPreMonthContractDate(contractDate);
            pageNo = 1;
            return response;
        }
        else{
            pageNo++;
            return response;
        }
    }


    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        if (executionContext.containsKey("lastPageNo")) {
            pageNo = executionContext.getInt("lastPageNo") + 1;
        } else {
            executionContext.putInt("lastPageNo", pageNo);
        }

        if (executionContext.containsKey("lastContractDate")) {
            contractDate = LocalDate.parse(executionContext.getString("lastContractDate"));
        } else {
            executionContext.putString("lastContractDate", contractDate.toString());
        }
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        executionContext.putInt("lastPageNo", pageNo);
        executionContext.putString("lastContractDate", contractDate.toString());
    }
}
