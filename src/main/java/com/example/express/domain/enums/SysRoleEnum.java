package com.example.express.domain.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;
import lombok.Getter;

import java.util.Arrays;

/**
 * 角色枚举
 * @date 2019年04月17日 0:11
 */
@Getter
public enum SysRoleEnum implements IEnum<Integer> {
    DIS_FORMAL(-1, "DIS_FORMAL"),
    ADMIN(1, "ROLE_ADMIN"),
    COURIER(2, "ROLE_COURIER"),
    USER(3, "ROLE_USER");

    private int type;

    private String name;

    SysRoleEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public Integer getValue() {
        return this.type;
    }

    public static SysRoleEnum getByType(int type) {
        return Arrays.stream(values()).filter(e -> e.getType() == type).findFirst().orElse(null);
    }

    public static SysRoleEnum getByName(String name) {
        return Arrays.stream(values()).filter(e -> e.getName().equals(name)).findFirst().orElse(null);
    }
}
