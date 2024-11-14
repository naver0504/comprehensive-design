package com.example.comprehensivedegisn.adapter.repository.dong;

import com.example.comprehensivedegisn.adapter.domain.Gu;
import com.querydsl.core.group.GroupBy;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Map;

import static com.example.comprehensivedegisn.adapter.domain.QDongEntity.dongEntity;


@Repository
@RequiredArgsConstructor
public class QuerydslDongRepository {

    private final JPAQueryFactory query;

    public Map<String, Integer> findByGuToMap(Gu gu){
        return query.selectFrom(dongEntity)
                .where(dongEntity.gu.eq(gu))
                .transform(GroupBy.groupBy(dongEntity.dongName).as(dongEntity.id));
    }

}
