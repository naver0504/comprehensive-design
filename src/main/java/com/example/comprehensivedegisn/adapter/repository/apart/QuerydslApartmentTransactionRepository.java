package com.example.comprehensivedegisn.adapter.repository.apart;

import com.example.comprehensivedegisn.adapter.domain.ApartmentTransaction;
import com.example.comprehensivedegisn.adapter.domain.Gu;
import com.example.comprehensivedegisn.adapter.domain.PredictStatus;
import com.example.comprehensivedegisn.adapter.order.CustomPageable;
import com.example.comprehensivedegisn.dto.response.SearchApartNameResponse;
import com.example.comprehensivedegisn.dto.response.SearchAreaResponse;
import com.example.comprehensivedegisn.dto.response.SearchResponseRecord;
import com.example.comprehensivedegisn.dto.request.SearchCondition;
import com.example.comprehensivedegisn.dto.response.TransactionDetailResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.example.comprehensivedegisn.adapter.domain.QApartmentTransaction.apartmentTransaction;
import static com.example.comprehensivedegisn.adapter.domain.QDongEntity.dongEntity;
import static com.example.comprehensivedegisn.adapter.domain.QPredictCost.*;


@Repository
public class QuerydslApartmentTransactionRepository extends QuerydslApartmentTransactionSupporter {

    public QuerydslApartmentTransactionRepository(JPAQueryFactory jpaQueryFactory) {
        super(ApartmentTransaction.class);
        this.jpaQueryFactory = jpaQueryFactory;
    }

    private final JPAQueryFactory jpaQueryFactory;

    public List<SearchApartNameResponse> findApartmentNames(Gu gu, String dongName) {
        return buildApartmentsWithDongQuery(gu, dongName)
                .select(Projections.constructor(SearchApartNameResponse.class, apartmentTransaction.apartmentName))
                .groupBy(apartmentTransaction.apartmentName)
                .fetch();
    }

    public List<SearchAreaResponse> findAreaForExclusive(Gu gu, String dongName, String apartmentName) {
        return  buildApartmentsWithDongQuery(gu, dongName)
                .where(apartmentTransaction.apartmentName.eq(apartmentName))
                .select(Projections.constructor(SearchAreaResponse.class, apartmentTransaction.areaForExclusiveUse))
                .groupBy(apartmentTransaction.areaForExclusiveUse)
                .fetch();
    }

    public Optional<TransactionDetailResponse> findTransactionDetail(long id) {
        return Optional.ofNullable(jpaQueryFactory.from(apartmentTransaction)
                .innerJoin(dongEntity).on(apartmentTransaction.dongEntity.id.eq(dongEntity.id))
                .innerJoin(predictCost).on(apartmentTransaction.id.eq(predictCost.apartmentTransaction.id))
                .where(
                        apartmentTransaction.id.eq(id),
                        eqRecentPredictStatus()
                )
                .select(Projections.constructor(TransactionDetailResponse.class,
                        apartmentTransaction.dealDate,
                        apartmentTransaction.buildYear,
                        apartmentTransaction.areaForExclusiveUse,
                        apartmentTransaction.dealingGbn,
                        apartmentTransaction.apartmentName,
                        apartmentTransaction.dealAmount,
                        predictCost.predictedCost,
                        apartmentTransaction.geography
                ))
                .fetchOne());
    }

    public List<SearchResponseRecord> searchApartmentTransactions(SearchCondition searchCondition, CustomPageable customPageable) {
        return querydsl().applyPagination(customPageable.toPageable(), buildApartmentSearchQuery(searchCondition))
                .select(Projections.constructor(SearchResponseRecord.class,
                        apartmentTransaction.id,
                        apartmentTransaction.apartmentName,
                        dongEntity.gu,
                        dongEntity.dongName,
                        apartmentTransaction.areaForExclusiveUse,
                        apartmentTransaction.dealDate,
                        apartmentTransaction.dealAmount,
                        predictCost.predictedCost,
                        predictCost.isReliable
                )).orderBy(customPageable.orderBy())
                .fetch();
    }



    private JPAQuery<?> buildApartmentsWithDongQuery(Gu gu, String dongName) {
        return jpaQueryFactory
                .from(apartmentTransaction)
                .distinct()
                .innerJoin(dongEntity).on(apartmentTransaction.dongEntity.id.eq(dongEntity.id))
                .where(
                        eqGu(gu),
                        eqDong(dongName)
                );
    }

    // 한 메소드에서 두 번 JpaQuery를 사용하면 두 번째 JpaQuery에서는
    // 첫 번째 JpaQuery에서 사용한 from, join 등이 초기화되어 있지 않아서 예외 또는 잘못된 결과가 나올 수 있습니다.
    private JPAQuery<?> buildApartmentSearchQuery(SearchCondition searchCondition) {
        return jpaQueryFactory
                .from(apartmentTransaction)
                .innerJoin(dongEntity).on(apartmentTransaction.dongEntity.id.eq(dongEntity.id))
                .innerJoin(predictCost).on(apartmentTransaction.id.eq(predictCost.apartmentTransaction.id))
                .where(
                        eqGu(searchCondition.getGu()),
                        eqDong(searchCondition.getDong()),
                        eqApartmentName(searchCondition.getApartmentName()),
                        eqAreaForExclusiveUse(searchCondition.getAreaForExclusiveUse()),
                        betweenDealDate(searchCondition.getStartDealDate(), searchCondition.getEndDealDate()),
                        eqRecentPredictStatus(),
                        searchCondition.toReliabilityEq()
                );
    }


    public Long getSearchCount(SearchCondition searchCondition) {
        return searchCondition.isGuEmpty()? getCountWithEmptyGuCondition(searchCondition) : getCount(searchCondition);
    }

    public Long enhancedGetSearchCount(SearchCondition searchCondition) {
        return searchCondition.isGuEmpty()? enhancedGetCountWithEmptyGuCondition(searchCondition) : enhancedGetCount(searchCondition);
    }

    private Long getCount(SearchCondition searchCondition) {
        return buildApartmentSearchQuery(searchCondition)
                .select(apartmentTransaction.count())
                .fetchOne();
    }

    private Long enhancedGetCount(SearchCondition searchCondition) {
        return setQueryWithReliability(searchCondition)
                .select(dongEntity.count())
                .from(dongEntity)
                .innerJoin(apartmentTransaction).on(
                        dongEntity.id.eq(apartmentTransaction.dongEntity.id),
                        eqApartmentName(searchCondition.getApartmentName()),
                        eqAreaForExclusiveUse(searchCondition.getAreaForExclusiveUse())
                )
                .where(
                        eqGu(searchCondition.getGu()),
                        eqDong(searchCondition.getDong())
                ).fetchOne();
    }

    private Long getCountWithEmptyGuCondition(SearchCondition searchCondition) {
        return jpaQueryFactory.select(predictCost.count())
                .from(predictCost)
                .where(
                        eqRecentPredictStatus(),
                        searchCondition.toReliabilityEq()
                )
                .fetchOne();
    }

    private Long enhancedGetCountWithEmptyGuCondition(SearchCondition searchCondition) {
        /***
         *  predictCost와 apartmentTransaction을 innerJoin하면
         *  count 쿼리 성능이 너무 떨어진다. 날짜 범위를 1년 씩 늘렸을 때
         *  3년 단위로 쿼리 수행 시간이 2배 이상 증가한다.
         */
        return jpaQueryFactory
                .select(apartmentTransaction.count())
                .from(apartmentTransaction)
                .where(betweenDealDate(searchCondition.getStartDealDate(), searchCondition.getEndDealDate())).fetchOne();
    }

    private JPAQuery<?> setQueryWithReliability(SearchCondition searchCondition) {
        JPAQuery<?> query = jpaQueryFactory.query();
        if(searchCondition.isReliabilityEmpty()) return query;
        return query
                .innerJoin(predictCost).on(
                        predictCost.apartmentTransaction.id.eq(apartmentTransaction.id),
                        eqRecentPredictStatus(),
                        searchCondition.toReliabilityEq()
                );
    }

    private Querydsl querydsl() {
        return Objects.requireNonNull(getQuerydsl());
    }

}
