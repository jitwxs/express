package com.example.express.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.express.domain.bean.OrderInfo;
import com.example.express.mapper.OrderInfoMapper;
import com.example.express.service.OrderInfoService;
import org.springframework.stereotype.Service;

@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {
}
