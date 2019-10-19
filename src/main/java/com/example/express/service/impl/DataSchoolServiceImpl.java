package com.example.express.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.express.common.cache.CommonDataCache;
import com.example.express.common.constant.RedisKeyConstant;
import com.example.express.domain.bean.DataSchool;
import com.example.express.mapper.DataSchoolMapper;
import com.example.express.service.DataSchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service
public class DataSchoolServiceImpl extends ServiceImpl<DataSchoolMapper, DataSchool> implements DataSchoolService {
    @Autowired
    private DataSchoolMapper dataSchoolMapper;
    @Autowired
    private RedisTemplate<String, DataSchool> redisTemplate;

    @Override
    public DataSchool getById(Serializable id) {
        DataSchool entity = (DataSchool) redisTemplate.opsForHash().get(RedisKeyConstant.DATA_SCHOOL, id);
        if(entity != null) {
            return entity;
        }
        entity = super.getById(id);

        redisTemplate.opsForHash().put(RedisKeyConstant.DATA_SCHOOL, id, entity);
        return entity;
    }

    @Override
    public boolean updateById(DataSchool entity) {
        boolean update = super.updateById(entity);
        if(update) {
            redisTemplate.opsForHash().delete(RedisKeyConstant.DATA_SCHOOL, entity.getId());
        }

        return update;
    }

    @Override
    public boolean isExist(Integer id) {
        return dataSchoolMapper.selectById(id) != null;
    }

    @Override
    public List<DataSchool> listByProvinceId(Integer provinceId) {
        return dataSchoolMapper.selectList(new QueryWrapper<DataSchool>().eq("province_id", provinceId));
    }

    @Override
    public List<DataSchool> listByProvinceIdByCache(Integer provinceId) {
        return CommonDataCache.dataSchoolCache.get(provinceId);
    }
}
