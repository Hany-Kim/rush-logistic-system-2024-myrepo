package com.rush.logistic.client.slack.domain.client;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRoleEnum {
    MASTER("ROLE_MASTER"),
    HUB("ROLE_HUB"),
    DELIVERY("ROLE_DELIVERY"),
    COMPANY("ROLE_COMPANY");

    private final String role;
}
