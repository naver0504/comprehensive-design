package com.example.comprehensivedegisn.batch.kakao_map.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public record Document(@JsonProperty("address_name") String addressName,
                       @JsonProperty("address_type") AddressType addressType,
                       String x,
                       String y) {

    public boolean isValid(String roadName) {
        return Objects.equals(addressName(), roadName) && addressType() == AddressType.ROAD;
    }

    private enum AddressType {
        ROAD
    }
}
