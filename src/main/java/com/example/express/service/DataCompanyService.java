package com.example.express.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.express.domain.bean.DataCompany;

import java.util.List;

public interface DataCompanyService extends IService<DataCompany> {
    List<DataCompany> listAll();

    List<DataCompany> listAllByCache();

    DataCompany getByCache(Integer id);
}
