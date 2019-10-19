package com.example.express.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.express.common.cache.CommonDataCache;
import com.example.express.domain.bean.DataArea;
import com.example.express.domain.vo.DataAreaVO;
import com.example.express.mapper.DataAreaMapper;
import com.example.express.service.DataAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataAreaServiceImpl extends ServiceImpl<DataAreaMapper, DataArea> implements DataAreaService {
    @Autowired
    private DataAreaMapper dataAreaMapper;


    @Override
    public List<DataArea> listByParentId(Integer parentId) {
        return dataAreaMapper.selectList(new QueryWrapper<DataArea>().eq("parent_id", parentId).orderByAsc("sort"));
    }

    @Override
    public List<DataAreaVO> listByParentIdByCache(Integer parentId) {
        List<DataArea> areas = CommonDataCache.dataAreaCache.get(parentId);

        return DataAreaVO.convert(areas);
    }
}
