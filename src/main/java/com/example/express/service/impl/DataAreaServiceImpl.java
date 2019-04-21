package com.example.express.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.express.domain.bean.DataArea;
import com.example.express.domain.bean.OrderDesc;
import com.example.express.mapper.DataAreaMapper;
import com.example.express.mapper.OrderDescMapper;
import com.example.express.service.DataAreaService;
import com.example.express.service.OrderDescService;
import org.springframework.stereotype.Service;

@Service
public class DataAreaServiceImpl extends ServiceImpl<DataAreaMapper, DataArea> implements DataAreaService {
}
