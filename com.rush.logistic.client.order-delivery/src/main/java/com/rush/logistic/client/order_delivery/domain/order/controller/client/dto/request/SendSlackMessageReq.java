package com.rush.logistic.client.order_delivery.domain.order.controller.client.dto.request;

import lombok.Builder;

@Builder
public record SendSlackMessageReq(

) {
    public static SendSlackMessageReq toDto() {
        return SendSlackMessageReq.builder()

                .build();
    }
}
