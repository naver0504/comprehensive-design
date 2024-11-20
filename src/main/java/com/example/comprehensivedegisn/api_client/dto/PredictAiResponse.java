package com.example.comprehensivedegisn.api_client.dto;

import java.time.LocalDate;
import java.util.List;

public record PredictAiResponse(List<PredictAiData> elements) {

    public record PredictAiData(LocalDate localDate, int predictPrice) {
    }
}
