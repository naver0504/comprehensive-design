package com.example.comprehensivedegisn.api_client.predict;

import com.example.comprehensivedegisn.adapter.domain.ApartmentTransaction;
import com.example.comprehensivedegisn.api_client.ApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.List;

@RequiredArgsConstructor
public abstract class PredictApiClient<T  extends  ApartmentTransaction, R> implements ApiClient<T  , R> {

    protected final PredictAiProperties predictAiProperties;

    @Override
    public String createUrl(T t) {
        return createURI(t);
    }

    private String createURI(T t) {
        return predictAiProperties.baseUrl() + ":" + predictAiProperties.port()
                + "/" + createPath()
                + "?gu=" + t.getGu()
                + "&dong=" + t.getDongName()
                + "&exclusiveArea=" + t.getAreaForExclusiveUse()
                + "&floor=" + t.getFloor()
                + "&buildYear=" + t.getBuildYear();
    }

    protected HttpEntity<?> createHttpEntities() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(headers);
    }

    protected abstract String createPath();
}
