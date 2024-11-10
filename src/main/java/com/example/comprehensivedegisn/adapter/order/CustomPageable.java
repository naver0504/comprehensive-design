package com.example.comprehensivedegisn.adapter.order;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CustomPageable {

    public CustomPageable(Order order, OrderType orderType) {
        this(Order.ASC, orderType, 0);
    }
    public CustomPageable(OrderType orderType, int page) {
        this(Order.ASC, orderType, page);
    }

    public static final int DEFAULT_SIZE = 10;

    private Order order = Order.ASC;
    private OrderType orderType = OrderType.DEAL_DATE;
    private int page = 0;

    public Pageable toPageable() {
        return PageRequest.of(page, DEFAULT_SIZE);
    }
    public OrderSpecifier<? extends Comparable> orderBy() {
        ComparableExpressionBase<? extends Comparable> expressionBase = orderType.getComparableExpressionBase();
        return order == Order.ASC ? expressionBase.asc() : expressionBase.desc();
    }
}
