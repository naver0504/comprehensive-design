package com.example.comprehensivedegisn.adapter.order;

import com.example.comprehensivedegisn.adapter.domain.ApartmentTransaction;

import com.example.comprehensivedegisn.adapter.repository.BaseRepositoryTest;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.Querydsl;

import java.util.List;

import static com.example.comprehensivedegisn.adapter.domain.QApartmentTransaction.apartmentTransaction;
@BaseRepositoryTest
class CustomPageableTest {


    @Autowired
    private EntityManager entityManager;

    private JPAQueryFactory queryFactory;
    private Querydsl querydsl;

    @BeforeEach
    void setUp(){
        queryFactory = new JPAQueryFactory(entityManager);
        querydsl = new Querydsl(entityManager, (new PathBuilderFactory()).create(ApartmentTransaction.class));
    }
    @Test

    void DEAL_AMOUNT_TEST() {
        // given
        OrderType orderType = OrderType.DEAL_AMOUNT;
        Order asc = Order.ASC;
        int pageId = 3;

        CustomPageable customPageable = new CustomPageable(asc, orderType, pageId);
        Pageable pageable = customPageable.toPageable();

        // when
        List<ApartmentTransaction> apartmentTransactions =
                querydsl
                        .applyPagination(pageable,
                                queryFactory.selectFrom(apartmentTransaction)
                                .orderBy(customPageable.orderBy()))
                        .fetch();

        // then
        Assertions.assertThat(orderType.getComparableExpressionBase()).isNotNull();
        Assertions.assertThat(apartmentTransactions.size()).isEqualTo(pageable.getPageSize());
        Assertions.assertThat(apartmentTransactions)
                .extracting(ApartmentTransaction::getDealAmount)
                .isSorted();

    }

    @Test
    public void AREA_FOR_EXCLUSIVE_USE_TEST() {
        // given
        OrderType orderType = OrderType.AREA_FOR_EXCLUSIVE_USE;
        Order asc = Order.ASC;
        int pageId = 1;

        CustomPageable customPageable = new CustomPageable(asc, orderType, pageId);
        Pageable pageable = customPageable.toPageable();

        // when
        List<ApartmentTransaction> apartmentTransactions =  querydsl
                .applyPagination(pageable,
                        queryFactory.selectFrom(apartmentTransaction)
                                .orderBy(customPageable.orderBy()))
                .fetch();
        // then
        Assertions.assertThat(orderType.getComparableExpressionBase()).isNotNull();
        Assertions.assertThat(apartmentTransactions.size()).isEqualTo(pageable.getPageSize());

        Assertions.assertThat(apartmentTransactions)
                .extracting(ApartmentTransaction::getAreaForExclusiveUse)
                .isSorted();
    }

    @Test
    public void DEAL_DATE_TEST() {
        // given
        OrderType orderType = OrderType.DEAL_DATE;
        Order asc = Order.ASC;
        int pageId = 2;

        CustomPageable customPageable = new CustomPageable(asc, orderType, pageId);
        Pageable pageable = customPageable.toPageable();

        // when
        List<ApartmentTransaction> apartmentTransactions =  querydsl
                .applyPagination(pageable,
                        queryFactory.selectFrom(apartmentTransaction)
                                .orderBy(customPageable.orderBy()))
                .fetch();
        // then
        Assertions.assertThat(orderType.getComparableExpressionBase()).isNotNull();
        Assertions.assertThat(apartmentTransactions.size()).isEqualTo(pageable.getPageSize());
        Assertions.assertThat(apartmentTransactions)
                .extracting(ApartmentTransaction::getDealDate)
                .isSorted();
    }

}