package com.example.express.domain.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;
import lombok.Getter;

import java.util.Arrays;

/**
 * 角色枚举
 * @date 2019年04月17日 0:11
 */
@Getter
public enum SexEnum implements IEnum<Integer> {
    MALE(1, "男"),
    FEMALE(0, "女");

    private int type;

    private String name;

    SexEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public Integer getValue() {
        return this.type;
    }

    public static SexEnum getByType(int type) {
        return Arrays.stream(values()).filter(e -> e.getType() == type).findFirst().orElse(null);
    }
}
