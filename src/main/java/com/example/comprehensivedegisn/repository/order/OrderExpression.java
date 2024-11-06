package com.example.comprehensivedegisn.repository.order;

import com.querydsl.core.types.OrderSpecifier;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OrderExpression {

    private Order order = Order.ASC;
    private OrderType orderType = OrderType.DEAL_DATE;

    public OrderSpecifier<? extends Comparable> orderBy() {
        if (order == Order.ASC) {
            return orderType.getComparableExpressionBase().asc();
        } else {
            return orderType.getComparableExpressionBase().desc();
        }
    }
}
