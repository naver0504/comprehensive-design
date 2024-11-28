package com.example.comprehensivedegisn.adapter.repository.apart;

import com.example.comprehensivedegisn.adapter.domain.ApartmentTransaction;
import com.example.comprehensivedegisn.adapter.domain.Gu;
import com.example.comprehensivedegisn.adapter.domain.PredictStatus;
import com.example.comprehensivedegisn.adapter.order.CustomPageable;
import com.example.comprehensivedegisn.adapter.order.CustomPageImpl;
import com.example.comprehensivedegisn.dto.response.SearchApartNameResponse;
import com.example.comprehensivedegisn.dto.response.SearchAreaResponse;
import com.example.comprehensivedegisn.dto.response.SearchResponseRecord;
import com.example.comprehensivedegisn.dto.request.SearchCondition;
import com.example.comprehensivedegisn.dto.response.TransactionDetailResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.example.comprehensivedegisn.adapter.domain.QApartmentTransaction.apartmentTransaction;
import static com.example.comprehensivedegisn.adapter.domain.QDongEntity.dongEntity;
import static com.example.comprehensivedegisn.adapter.domain.QPredictCost.*;


@Repository
public class QuerydslApartmentTransactionRepository extends QuerydslRepositorySupport {

    private final JPAQueryFactory jpaQueryFactory;

    public QuerydslApartmentTransactionRepository(JPAQueryFactory querydsl, JPAQueryFactory jpaQueryFactory) {
        super(ApartmentTransaction.class);
        this.querydsl = querydsl;
        this.jpaQueryFactory = jpaQueryFactory;
    }

    private final JPAQueryFactory querydsl;

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

    public CustomPageImpl<SearchResponseRecord> searchApartmentTransactions(Long cachedCount, SearchCondition searchCondition, CustomPageable customPageable) {
        Pageable pageable = customPageable.toPageable();
        List<SearchResponseRecord> elements =
                searchCondition.isEmpty() ? getSearchElementsWithEmptyCondition(searchCondition, customPageable) : getSearchElementsWithCondition(searchCondition, customPageable) ;

        long totalCount = (cachedCount != null) ? cachedCount : getCount(searchCondition);
        return new CustomPageImpl<>(elements, pageable, totalCount);
    }

    private List<SearchResponseRecord> getSearchElementsWithCondition(SearchCondition searchCondition, CustomPageable customPageable) {
        JPQLQuery<?> jpqlQuery = querydsl().applyPagination(customPageable.toPageable(), buildApartmentSearchQuery(searchCondition));
        return selectElements((JPAQuery<?>) jpqlQuery).fetch();
    }

    private List<SearchResponseRecord> getSearchElementsWithEmptyCondition(SearchCondition searchCondition, CustomPageable customPageable) {
        return selectElements(buildApartmentSearchQueryWithEmptyCondition(searchCondition, customPageable))
                .fetch();
    }

    private JPAQuery<SearchResponseRecord> selectElements(JPAQuery<?> query) {
        return query
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
                ));
    }


    private JPAQuery<?> buildApartmentsWithDongQuery(Gu gu, String dongName) {
        return querydsl
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
        return querydsl
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

    private JPAQuery<?> buildApartmentSearchQueryWithEmptyCondition(SearchCondition searchCondition, CustomPageable customPageable) {
        List<Long> ids = querydsl().applyPagination(customPageable.toPageable(),
                querydsl.select(apartmentTransaction.id)
                        .from(apartmentTransaction)
                        .orderBy(customPageable.orderBy())
        ).fetch();

        return querydsl
                .from(apartmentTransaction)
                .innerJoin(dongEntity).on(apartmentTransaction.dongEntity.id.eq(dongEntity.id))
                .innerJoin(predictCost).on(apartmentTransaction.id.eq(predictCost.apartmentTransaction.id))
                .where(
                        apartmentTransaction.id.in(ids),
                        betweenDealDate(searchCondition.getStartDealDate(), searchCondition.getEndDealDate()),
                        eqRecentPredictStatus(),
                        searchCondition.toReliabilityEq()
                );
    }


    private Long getCount(SearchCondition searchCondition) {
        return buildApartmentSearchQuery(searchCondition)
                .select(apartmentTransaction.count())
                .fetchOne();
    }

    private Querydsl querydsl() {
        return Objects.requireNonNull(getQuerydsl());
    }

    private BooleanExpression eqGu(Gu gu) {
        return gu == Gu.NONE ? apartmentTransaction.dongEntity.gu.in(Gu.guList) : apartmentTransaction.dongEntity.gu.eq(gu);
    }

    private BooleanExpression eqDong(String dong) {
        return !StringUtils.hasText(dong) ? null : apartmentTransaction.dongEntity.dongName.eq(dong);
    }

    private BooleanExpression eqApartmentName(String apartmentName) {
        return !StringUtils.hasText(apartmentName) ? null : apartmentTransaction.apartmentName.eq(apartmentName);
    }

    private BooleanExpression eqAreaForExclusiveUse(Double areaForExclusiveUse) {
        return areaForExclusiveUse == null ? null : apartmentTransaction.areaForExclusiveUse.eq(areaForExclusiveUse);
    }

    private BooleanExpression betweenDealDate(LocalDate startDealDate, LocalDate endDealDate) {
        if(startDealDate == null && endDealDate == null) {
            return null;
        }
        startDealDate = Objects.isNull(startDealDate) ? LocalDate.of(2006, 1, 1) : startDealDate;
        endDealDate = Objects.isNull(endDealDate) ? LocalDate.now() : endDealDate;
        return apartmentTransaction.dealDate.between(startDealDate, endDealDate);
    }

    private BooleanExpression eqRecentPredictStatus() {
        return predictCost.predictStatus.eq(PredictStatus.RECENT);
    }
}
