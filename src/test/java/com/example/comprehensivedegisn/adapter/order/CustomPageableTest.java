package com.example.comprehensivedegisn.adapter.order;

import com.example.comprehensivedegisn.adapter.domain.ApartmentTransaction;
import com.example.comprehensivedegisn.adapter.domain.Gu;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static com.example.comprehensivedegisn.adapter.domain.QApartmentTransaction.apartmentTransaction;
import static com.example.comprehensivedegisn.adapter.domain.QDongEntity.dongEntity;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomPageableTest {


    @Autowired
    private EntityManager entityManager;

    private JPAQueryFactory queryFactory;

    @BeforeEach
    void setUp(){
        queryFactory = new JPAQueryFactory(entityManager);
    }

    @Test
    void queryFactoryTest(){
        Assertions.assertThat(queryFactory).isNotNull();
    }

    @Test
    void DEAL_AMOUNT_TEST() {
        // given
        OrderType orderType = OrderType.DEAL_AMOUNT;
        Order asc = Order.ASC;

        CustomPageable orderExpression = new CustomPageable(asc, orderType);
        // when
        List<ApartmentTransaction> apartmentTransactions = queryFactory.selectFrom(apartmentTransaction)
                .orderBy(orderExpression.orderBy())
                .limit(10)
                .fetch();
        // then
        Assertions.assertThat(orderType.getComparableExpressionBase()).isNotNull();
        Assertions.assertThat(apartmentTransactions)
                .extracting(ApartmentTransaction::getDealAmount)
                .isSorted();
    }

    @Test
    public void AREA_FOR_EXCLUSIVE_USE_TEST() {
        // given
        OrderType orderType = OrderType.AREA_FOR_EXCLUSIVE_USE;
        Order asc = Order.ASC;

        CustomPageable orderExpression = new CustomPageable(asc, orderType);
        // when
        List<ApartmentTransaction> apartmentTransactions = queryFactory.selectFrom(apartmentTransaction)
                .orderBy(orderExpression.orderBy())
                .limit(10)
                .fetch();
        // then
        Assertions.assertThat(orderType.getComparableExpressionBase()).isNotNull();
        Assertions.assertThat(apartmentTransactions)
                .extracting(ApartmentTransaction::getAreaForExclusiveUse)
                .isSorted();
    }

    @Test
    public void DEAL_DATE_TEST() {
        // given
        OrderType orderType = OrderType.DEAL_DATE;
        Order asc = Order.ASC;

        CustomPageable orderExpression = new CustomPageable(asc, orderType);
        // when
        List<ApartmentTransaction> apartmentTransactions = queryFactory.selectFrom(apartmentTransaction)
                .innerJoin(dongEntity).on(apartmentTransaction.dongEntity.eq(dongEntity))
                .where(dongEntity.gu.eq(Gu.마포구))
                .orderBy(orderExpression.orderBy())
                .limit(10)
                .offset(2)
                .fetch();
        // then
        Assertions.assertThat(orderType.getComparableExpressionBase()).isNotNull();
        Assertions.assertThat(apartmentTransactions)
                .extracting(ApartmentTransaction::getDealDate)
                .isSorted();
    }

}