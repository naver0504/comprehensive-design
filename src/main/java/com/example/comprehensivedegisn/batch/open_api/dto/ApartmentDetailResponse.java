package com.example.comprehensivedegisn.batch.open_api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "response")
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApartmentDetailResponse(@JacksonXmlProperty(localName = "header") ApartmentHeader header,
                                      @JacksonXmlProperty(localName = "body") ApartmentDetailBody body) {

    public boolean isLimitExceeded() {
        return header().resultCode() == 99;
    }

    public boolean isEndOfData() {
        return body().totalCount() == 0;
    }

    public int getTotalCount() {
        return body().totalCount();
    }
}


