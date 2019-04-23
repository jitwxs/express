package com.example.express.domain.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;
import lombok.Getter;

import java.util.Arrays;

/**
 * 反馈状态枚举
 * @date 2019年04月17日 0:11
 */
@Getter
public enum FeedbackStatusEnum implements IEnum<Integer> {
    WAIT(1, "等待处理"),
    PROCESS(2, "处理中"),
    COMPLETE(3, "处理完毕");

    private int status;

    private String name;

    FeedbackStatusEnum(int status, String name) {
        this.status = status;
        this.name = name;
    }

    @Override
    public Integer getValue() {
        return this.status;
    }

    public static FeedbackStatusEnum getByStatus(int status) {
        return Arrays.stream(values()).filter(e -> e.getStatus() == status).findFirst().orElse(null);
    }
}
