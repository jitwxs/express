package com.example.express.controller.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.express.common.util.StringUtils;
import com.example.express.domain.ResponseResult;
import com.example.express.domain.bean.OrderInfo;
import com.example.express.domain.bean.SysUser;
import com.example.express.domain.vo.BootstrapTableVO;
import com.example.express.domain.vo.OrderVO;
import com.example.express.service.OrderInfoService;
import com.example.express.service.OrderPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * API订单接口
 * @author jitwxs
 * @date 2019年04月22日 23:54
 */
@RestController
@RequestMapping("/api/v1/order")
public class OrderApiController {
    @Autowired
    private OrderInfoService orderInfoService;
    @Autowired
    private OrderPaymentService orderPaymentService;

    /**
     * 获取个人所有订单
     * @author jitwxs
     * @date 2019/4/24 22:21
     */
    @GetMapping("/list")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_COURIER')")
    public BootstrapTableVO<OrderVO> listSelfOrder(@RequestParam(required = false, defaultValue = "1") Integer current,
                                                   @RequestParam(required = false, defaultValue = "10") Integer size,
                                                   @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                                   @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
                                                   String status, String id,
                                                   @AuthenticationPrincipal SysUser sysUser) {
        Page<OrderInfo> page = new Page<>(current, size);
        Integer orderStatus = StringUtils.toInteger(status, -1);

        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        if(orderStatus != -1) {
            wrapper.eq("status", status);
        }
        if(startDate != null) {
            wrapper.ge("create_date", startDate);
        }
        if(endDate != null) {
            wrapper.le("create_date", endDate);
        }
        if(StringUtils.isNotBlank(id)) {
            wrapper.eq("id", id);
        }
        wrapper.eq("user_id", sysUser.getId());

        return orderInfoService.pageOrderVO(page, wrapper);
    }

    /**
     * 批量删除订单，仅能删除个人订单
     * 状态为订单完成或订单异常
     * @author jitwxs
     * @date 2019/4/24 23:08
     */
    @PostMapping("/batch-delete")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_COURIER')")
    public ResponseResult batchDelete(String[] ids, @AuthenticationPrincipal SysUser sysUser) {
        return orderInfoService.batchDeleteOrder(ids, sysUser.getId());
    }

    /**
     * 批量撤销订单，仅能删除个人订单
     * 状态为未接单
     * @author jitwxs
     * @date 2019/4/25 0:11
     */
    @PostMapping("/batch-cancel")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_COURIER')")
    public ResponseResult batchCancel(String[] ids, @AuthenticationPrincipal SysUser sysUser) {
        return orderInfoService.batchCancelOrder(ids, sysUser.getId());
    }
}
