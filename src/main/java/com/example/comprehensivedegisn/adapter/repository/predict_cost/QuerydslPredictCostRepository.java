package com.example.comprehensivedegisn.adapter.repository.predict_cost;

import com.example.comprehensivedegisn.adapter.domain.PredictStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.comprehensivedegisn.adapter.domain.QPredictCost.predictCost;

@Repository
@RequiredArgsConstructor
public class QuerydslPredictCostRepository {

    private final JPAQueryFactory queryFactory;

    public void updateStatusToNotRecent(List<Long> ids) {
        queryFactory.update(predictCost)
                .set(predictCost.predictStatus, PredictStatus.NOT_RECENT)
                .where(predictCost.id.in(ids))
                .execute();
    }
}
