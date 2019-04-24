package com.example.express.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.express.domain.bean.DataArea;
import com.example.express.domain.bean.DataCompany;
import com.example.express.domain.vo.DataAreaVO;

import java.util.List;

public interface DataCompanyService extends IService<DataCompany> {
    List<DataCompany> listAll();
    List<DataCompany> listAllByCache();
}
