package com.example.express.controller.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.express.common.util.StringUtils;
import com.example.express.domain.ResponseResult;
import com.example.express.domain.bean.SysUser;
import com.example.express.domain.enums.ResponseErrorCodeEnum;
import com.example.express.domain.vo.BootstrapTableVO;
import com.example.express.domain.vo.OrderDescVO;
import com.example.express.domain.vo.OrderVO;
import com.example.express.exception.CustomException;
import com.example.express.service.OrderInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 获取订单信息
     * @author jitwxs
     * @date 2019/4/25 23:36
     */
    @GetMapping("/{id}")
    public ResponseResult getOrderDesc(@PathVariable String id) {
        OrderDescVO descVO = orderInfoService.getDescVO(id);
        if(descVO == null) {
            return ResponseResult.failure(ResponseErrorCodeEnum.ORDER_NOT_EXIT);
        }

        return ResponseResult.success(descVO);
    }

    /**
     * 获取个人所有订单
     * @param type 0:正常订单；1：已删除订单
     * @author jitwxs
     * @date 2019/4/24 22:21
     */
    @GetMapping("/list")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_COURIER')")
    public BootstrapTableVO<OrderVO> listSelfOrder(@RequestParam(required = false, defaultValue = "1") Integer current,
                                                   @RequestParam(required = false, defaultValue = "10") Integer size,
                                                   String type, String startDate, String endDate, String status, String id,
                                                   @AuthenticationPrincipal SysUser sysUser) {
        Integer isDelete = StringUtils.toInteger(type, -1);
        if(isDelete == -1) {
            throw new CustomException(ResponseErrorCodeEnum.PARAMETER_ERROR);
        }

        Page<OrderVO> page = new Page<>(current, size);
        Integer orderStatus = StringUtils.toInteger(status, -1);

        StringBuilder sql = new StringBuilder();
        if(orderStatus != -1) {
            sql.append(" AND info.status = ").append(orderStatus);
        }
        if(StringUtils.isNotBlank(startDate)) {
            sql.append(" AND info.create_date > '").append(startDate).append("'");
        }
        if(StringUtils.isNotBlank(endDate)) {
            sql.append(" AND info.create_date < '").append(endDate).append("'");
        }
        if(StringUtils.isNotBlank(id)) {
            sql.append(" AND info.id = ").append(id);

        }
        sql.append(" AND info.user_id = ").append(sysUser.getId());

        return orderInfoService.pageOrderVO(page, sql.toString(), isDelete);
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

    /**
     * 批量恢复订单，仅能恢复个人订单
     * @author jitwxs
     * @date 2019/4/26 1:58
     */
    @PostMapping("/batch-rollback")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_COURIER')")
    public ResponseResult batchRollback(String[] ids, @AuthenticationPrincipal SysUser sysUser) {
        return orderInfoService.batchRollback(ids, sysUser.getId());
    }
}
