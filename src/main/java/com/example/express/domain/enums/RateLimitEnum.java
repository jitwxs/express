package com.example.express.domain.enums;

public enum RateLimitEnum {

    RRLimit_2_1("2/1"),
    RRLimit_5_1("5/1"),
    RRLimit_5_2("5/2"),
    RRLimit_1_5("1/5"),
    RRLimit_10_1("10/1"),
    RRLimit_1_10("1/10"),
    RRLimit_1_60("1/60"),
    ;

    private String limit;

    RateLimitEnum(final String limit) {
        this.limit = limit;
    }

    public String limit() {
        return this.limit;
    }
}
