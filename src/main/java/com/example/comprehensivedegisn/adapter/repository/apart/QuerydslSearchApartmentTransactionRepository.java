package com.example.comprehensivedegisn.adapter.repository.apart;

import com.example.comprehensivedegisn.adapter.domain.ApartmentTransaction;
import com.example.comprehensivedegisn.adapter.order.CustomPageable;
import com.example.comprehensivedegisn.dto.request.SearchCondition;
import com.example.comprehensivedegisn.dto.response.SearchResponseRecord;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;


import java.util.List;
import java.util.Objects;

import static com.example.comprehensivedegisn.adapter.domain.QApartmentTransaction.apartmentTransaction;
import static com.example.comprehensivedegisn.adapter.domain.QDongEntity.dongEntity;
import static com.example.comprehensivedegisn.adapter.domain.QPredictCost.predictCost;

@Repository
@Slf4j
public class QuerydslSearchApartmentTransactionRepository extends QuerydslApartmentTransactionSupporter {


    private final JPAQueryFactory jpaQueryFactory;

    public QuerydslSearchApartmentTransactionRepository(JPAQueryFactory jpaQueryFactory) {
        super(ApartmentTransaction.class);
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public List<SearchResponseRecord> searchApartmentTransactions(SearchCondition searchCondition, CustomPageable customPageable) {
        List<Long> ids = querydsl().applyPagination(customPageable.toPageable(), buildSearchPaginationQuery(searchCondition))
                .select(apartmentTransaction.id)
                .orderBy(customPageable.orderBy())
                .fetch();

        if(CollectionUtils.isEmpty(ids)) return List.of();

        return jpaQueryFactory.select(Projections.constructor(SearchResponseRecord.class,
                        apartmentTransaction.id,
                        apartmentTransaction.apartmentName,
                        dongEntity.gu,
                        dongEntity.dongName,
                        apartmentTransaction.areaForExclusiveUse,
                        apartmentTransaction.dealDate,
                        apartmentTransaction.dealAmount,
                        predictCost.predictedCost,
                        predictCost.isReliable
                ))
                .from(apartmentTransaction)
                .innerJoin(dongEntity).on(apartmentTransaction.dongEntity.id.eq(dongEntity.id))
                .innerJoin(predictCost).on(apartmentTransaction.id.eq(predictCost.apartmentTransaction.id))
                .where(apartmentTransaction.id.in(ids))
                .orderBy(customPageable.orderBy())
                .fetch();
    }

    public Long getSearchCount(SearchCondition searchCondition) {
        return buildSearchCountQuery(searchCondition).fetchOne();
    }

    public JPAQuery<?> buildSearchPaginationQuery(SearchCondition searchCondition) {
        return searchCondition.isGuEmpty() ? buildQueryWithEmptyGuCondition(searchCondition) : buildQueryWithGuCondition(searchCondition);
    }

    public JPAQuery<Long> buildSearchCountQuery(SearchCondition searchCondition) {
        return searchCondition.isGuEmpty()? getCountWithEmptyGuCondition(searchCondition) : getCountWithGuCondition(searchCondition);
    }

    private JPAQuery<Long> getCountWithGuCondition(SearchCondition searchCondition) {
        return buildQueryWithGuCondition(searchCondition).select(apartmentTransaction.count());
    }

    private JPAQuery<Long> getCountWithEmptyGuCondition(SearchCondition searchCondition) {
        return buildCountQueryWithEmptyGuCondition(searchCondition);
    }

    private JPAQuery<?> buildQueryWithGuCondition(SearchCondition searchCondition) {
        JPAQuery<?> query = jpaQueryFactory
                .from(apartmentTransaction)
                .innerJoin(apartmentTransaction.dongEntity, dongEntity)
                .where(
                        eqGu(searchCondition.getGu()),
                        eqDong(searchCondition.getDong()),
                        betweenDealDate(searchCondition.getStartDealDate(), searchCondition.getEndDealDate()),
                        eqApartmentName(searchCondition.getApartmentName()),
                        eqAreaForExclusiveUse(searchCondition.getAreaForExclusiveUse())
                );
        return joinWithPredictCost(query, searchCondition);
    }

    /***
     *
     * 1. searchCondition.isReliabilityEmpty() -> PredictCost 테이블은 조인할 필요 없음.
     * 2. Objects.isNull(betweenDealDate) -> 모든 ApartmentTransaction 조회 -> 최근 PredictCost만 조회하면 됨.
     * 3. 그 이외의 경우는 날짜 조건과 Reliability 조건이 주어졌으므로 둘을 조인해서 쿼리 (시간이 제일 오래 걸림)
     */
    private JPAQuery<Long> buildCountQueryWithEmptyGuCondition(SearchCondition searchCondition) {
        BooleanExpression betweenDealDate = betweenDealDate(searchCondition.getStartDealDate(), searchCondition.getEndDealDate());

        if(searchCondition.isReliabilityEmpty()) return jpaQueryFactory
                .select(apartmentTransaction.dealDate.count())
                .from(apartmentTransaction)
                .where(betweenDealDate);

        if(Objects.isNull(betweenDealDate)) return jpaQueryFactory
                .select(predictCost.id.count())
                .from(predictCost)
                .where(eqRecentPredictStatus(), searchCondition.toReliabilityEq());

        return buildQueryWithEmptyGuCondition(searchCondition)
                .select(predictCost.count());
    }

    private JPAQuery<?> buildQueryWithEmptyGuCondition(SearchCondition searchCondition) {
        JPAQuery<?> query = jpaQueryFactory
                .from(apartmentTransaction)
                .where(betweenDealDate(searchCondition.getStartDealDate(), searchCondition.getEndDealDate()));

        return joinWithPredictCost(query, searchCondition);
    }

    private JPAQuery<?> joinWithPredictCost(JPAQuery<?> query, SearchCondition searchCondition) {
        if(searchCondition.isReliabilityEmpty()) return query;
        else return query
                .innerJoin(predictCost)
                .on(
                        apartmentTransaction.id.eq(predictCost.apartmentTransaction.id),
                        eqRecentPredictStatus(),
                        searchCondition.toReliabilityEq()
                );
    }
}
