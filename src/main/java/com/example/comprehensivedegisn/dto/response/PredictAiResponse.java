package com.example.comprehensivedegisn.dto.response;

import java.time.LocalDate;
import java.util.Map;

public record PredictAiResponse(Map<LocalDate, Long> predictData) {
}
