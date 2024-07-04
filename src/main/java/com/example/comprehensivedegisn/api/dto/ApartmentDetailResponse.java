package com.example.comprehensivedegisn.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "response")
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApartmentDetailResponse(@JacksonXmlProperty(localName = "header") ApartmentHeader header,
                                      @JacksonXmlProperty(localName = "body") ApartmentDetailBody body) {

    public boolean isEndOfPage() {
        return body().items().isEmpty();
    }

    public boolean isLimitExceeded() {
        return header().resultCode() == 99;
    }

    public boolean isEndOfData() {
        return body().totalCount() == 0;
    }
}


