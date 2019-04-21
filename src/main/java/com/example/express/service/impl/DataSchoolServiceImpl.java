package com.example.express.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.express.domain.bean.DataArea;
import com.example.express.domain.bean.DataSchool;
import com.example.express.mapper.DataAreaMapper;
import com.example.express.mapper.DataSchoolMapper;
import com.example.express.service.DataAreaService;
import com.example.express.service.DataSchoolService;
import org.springframework.stereotype.Service;

@Service
public class DataSchoolServiceImpl extends ServiceImpl<DataSchoolMapper, DataSchool> implements DataSchoolService {
}
