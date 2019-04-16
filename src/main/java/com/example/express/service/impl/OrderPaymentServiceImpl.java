package com.example.express.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.express.domain.bean.OrderPayment;
import com.example.express.mapper.OrderPaymentMapper;
import com.example.express.service.OrderPaymentService;
import org.springframework.stereotype.Service;

@Service
public class OrderPaymentServiceImpl extends ServiceImpl<OrderPaymentMapper, OrderPayment> implements OrderPaymentService {
}
