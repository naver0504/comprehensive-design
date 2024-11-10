package com.example.comprehensivedegisn.adapter.repository.apart;

import com.example.comprehensivedegisn.adapter.domain.ApartmentTransaction;
import com.example.comprehensivedegisn.adapter.domain.Gu;
import com.example.comprehensivedegisn.adapter.order.CustomPageable;
import com.example.comprehensivedegisn.dto.QTransactionResponseRecord;
import com.example.comprehensivedegisn.dto.TransactionResponseRecord;
import com.example.comprehensivedegisn.dto.SearchCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static com.example.comprehensivedegisn.adapter.domain.QApartmentTransaction.apartmentTransaction;
import static com.example.comprehensivedegisn.adapter.domain.QDongEntity.dongEntity;


@Repository
public class QuerydslApartmentTransactionRepository extends QuerydslRepositorySupport {

    public QuerydslApartmentTransactionRepository(JPAQueryFactory querydsl) {
        super(ApartmentTransaction.class);
        this.querydsl = querydsl;
    }

    private final JPAQueryFactory querydsl;

    public Page<TransactionResponseRecord> searchApartmentTransactions(Long cachedCount, SearchCondition searchCondition, CustomPageable customPageable) {
        Pageable pageable = customPageable.toPageable();

        List<TransactionResponseRecord> elements = querydsl().applyPagination(pageable, buildApartmentSearchQuery(searchCondition)
                .select(
                        new QTransactionResponseRecord(
                                apartmentTransaction.id,
                                apartmentTransaction.apartmentName,
                                dongEntity.gu,
                                dongEntity.dongName,
                                apartmentTransaction.areaForExclusiveUse,
                                apartmentTransaction.dealDate,
                                apartmentTransaction.dealAmount
                        )
                )
                .orderBy(customPageable.orderBy())

        ).fetch();

        long totalCount = (cachedCount != null) ? cachedCount : getCount(searchCondition);
        return new PageImpl<>(elements, pageable, totalCount);
    }


    // 한 메소드에서 두 번 JpaQuery를 사용하면 두 번째 JpaQuery에서는
    // 첫 번째 JpaQuery에서 사용한 from, join 등이 초기화되어 있지 않아서 예외 또는 잘못된 결과가 나올 수 있습니다.
    private JPAQuery<?> buildApartmentSearchQuery(SearchCondition searchCondition) {
        return querydsl
                .from(apartmentTransaction)
                .innerJoin(dongEntity).on(apartmentTransaction.dongEntity.id.eq(dongEntity.id))
                .where(
                        eqGu(searchCondition.getGu()),
                        eqDong(searchCondition.getDong()),
                        eqApartmentName(searchCondition.getApartmentName()),
                        eqAreaForExclusiveUse(searchCondition.getAreaForExclusiveUse()),
                        betweenDealDate(searchCondition.getStartDealDate(), searchCondition.getEndDealDate())
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
}
