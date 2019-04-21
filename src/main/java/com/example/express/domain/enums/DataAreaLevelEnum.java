package com.example.express.domain.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;
import lombok.Getter;

/**
 * @author xiangsheng.wu
 * @date 2019年04月21日 12:49
 */
@Getter
public enum DataAreaLevelEnum implements IEnum<Integer> {
    PROVINCE(1, "省/直辖市"),
    CITY(2, "地级市"),
    COUNTY(3, "区县"),
    TOWN(4, "镇/街");

    private int level;

    private String name;

    DataAreaLevelEnum(int level, String name) {
        this.level = level;
        this.name = name;
    }

    @Override
    public Integer getValue() {
        return this.level;
    }
}
