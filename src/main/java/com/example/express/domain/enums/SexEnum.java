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
    /**
     * 男性
     */
    MALE(1, "male", "男"),
    /**
     * 女性
     */
    FEMALE(0, "female", "女"),
    /**
     * 保密
     */
    CONFIDENTIALITY(-1, "confidentiality", "保密");

    private Integer type;

    private String name;

    private String cname;

    SexEnum(int type, String name, String cname) {
        this.type = type;
        this.name = name;
        this.cname = cname;
    }

    @Override
    public Integer getValue() {
        return this.type;
    }

    public static SexEnum getByType(Integer type) {
        return Arrays.stream(values()).filter(e -> e.getType().equals(type)).findFirst().orElse(null);
    }

    public static SexEnum getByName(String name) {
        return Arrays.stream(values()).filter(e -> e.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
