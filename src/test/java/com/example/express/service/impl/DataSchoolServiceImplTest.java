package com.example.express.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.express.BaseTests;
import com.example.express.domain.bean.DataArea;
import com.example.express.domain.enums.DataAreaLevelEnum;
import com.example.express.mapper.DataAreaMapper;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DataSchoolServiceImplTest extends BaseTests {
    @Autowired
    private DataAreaMapper dataAreaMapper;

    @Test
    public void initProvinceId() {
        List<DataArea> list = dataAreaMapper.selectList(new QueryWrapper<DataArea>().eq("level", DataAreaLevelEnum.PROVINCE));
        Assert.assertEquals(34, list.size());
    }
}