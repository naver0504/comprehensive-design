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

    private final JPAQueryFactory jpaQueryFactory;

    public QuerydslApartmentTransactionRepository(JPAQueryFactory jpaQueryFactory) {
        super(ApartmentTransaction.class);
        this.jpaQueryFactory = jpaQueryFactory;
    }

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

    private Long getCount(SearchCondition searchCondition) {
        return buildApartmentSearchQuery(searchCondition)
                .select(apartmentTransaction.count())
                .fetchOne();
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

    private Querydsl querydsl() {
        return Objects.requireNonNull(getQuerydsl());
    }

}
