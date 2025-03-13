package com.example.comprehensivedegisn.adapter.repository.apart;

import com.example.comprehensivedegisn.adapter.domain.Gu;
import com.example.comprehensivedegisn.adapter.domain.PredictStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Objects;

import static com.example.comprehensivedegisn.adapter.domain.QApartmentTransaction.apartmentTransaction;
import static com.example.comprehensivedegisn.adapter.domain.QPredictCost.predictCost;

public class QuerydslApartmentTransactionSupporter extends QuerydslRepositorySupport {
    public QuerydslApartmentTransactionSupporter(Class<?> domainClass) {
        super(domainClass);
    }

    protected BooleanExpression eqGu(Gu gu) {
        return gu == Gu.NONE ? apartmentTransaction.dongEntity.gu.in(Gu.guList) : apartmentTransaction.dongEntity.gu.eq(gu);
    }

    protected BooleanExpression eqDong(String dong) {
        return !StringUtils.hasText(dong) ? null : apartmentTransaction.dongEntity.dongName.eq(dong);
    }

    protected BooleanExpression eqApartmentName(String apartmentName) {
        return !StringUtils.hasText(apartmentName) ? null : apartmentTransaction.apartmentName.eq(apartmentName);
    }

    protected BooleanExpression eqAreaForExclusiveUse(Double areaForExclusiveUse) {
        return areaForExclusiveUse == null ? null : apartmentTransaction.areaForExclusiveUse.eq(areaForExclusiveUse);
    }

    protected BooleanExpression betweenDealDate(LocalDate startDealDate, LocalDate endDealDate) {
        if(startDealDate == null && endDealDate == null) {
            return null;
        }
        startDealDate = Objects.isNull(startDealDate) ? LocalDate.of(2006, 1, 1) : startDealDate;
        endDealDate = Objects.isNull(endDealDate) ? LocalDate.now() : endDealDate;
        return apartmentTransaction.dealDate.between(startDealDate, endDealDate);
    }

    protected BooleanExpression eqRecentPredictStatus() {
        return predictCost.predictStatus.eq(PredictStatus.RECENT);
    }
}
