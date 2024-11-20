package com.example.comprehensivedegisn.dto;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.Getter;

import static com.example.comprehensivedegisn.adapter.domain.QPredictCost.predictCost;

@Getter
public enum Reliability {
        ALL(predictCost.isReliable.in(true, false)),
        RELIABLE(predictCost.isReliable.isTrue()),
        UNRELIABLE(predictCost.isReliable.isFalse());

        private final BooleanExpression reliabilityExpression;

        Reliability(BooleanExpression reliabilityExpression) {
            this.reliabilityExpression = reliabilityExpression;
        }

    }