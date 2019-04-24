package com.example.express.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.express.domain.bean.OrderInfo;
import org.apache.ibatis.annotations.Param;

public interface OrderInfoMapper extends BaseMapper<OrderInfo> {
    boolean manualDelete(@Param("id") String orderId,@Param("deleteType") Integer deleteType);
}
