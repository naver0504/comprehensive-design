package com.example.comprehensivedegisn.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName = "response")
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApartmentDetailRootElement(@JacksonXmlProperty(localName = "header") ApartmentHeader header,
                                         @JacksonXmlProperty(localName = "body") List<ApartmentDetail> body) { }


