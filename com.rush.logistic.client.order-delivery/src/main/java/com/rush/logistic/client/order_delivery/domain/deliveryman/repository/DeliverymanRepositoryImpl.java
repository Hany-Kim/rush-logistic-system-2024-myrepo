package com.rush.logistic.client.order_delivery.domain.deliveryman.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeliverymanRepositoryImpl implements DeliverymanRepositoryCustom {
    private final JPAQueryFactory queryFactory;


}
