package com.example.express.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.express.domain.bean.DataSchool;

import java.util.List;

public interface DataSchoolService extends IService<DataSchool> {
    boolean isExist(Integer id);

    List<DataSchool> listByProvinceId(Integer provinceId);

    List<DataSchool> listByProvinceIdByCache(Integer provinceId);
}
