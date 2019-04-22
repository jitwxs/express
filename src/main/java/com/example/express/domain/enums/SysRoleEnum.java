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
    DIS_FORMAL(-1, "DIS_FORMAL", "非正式用户"),
    ADMIN(1, "ROLE_ADMIN", "系统管理员"),
    COURIER(2, "ROLE_COURIER", "配送员"),
    USER(3, "ROLE_USER", "普通用户");

    private int type;

    private String name;

    private String cnName;

    SysRoleEnum(int type, String name, String cnName) {
        this.type = type;
        this.name = name;
        this.cnName = cnName;
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
