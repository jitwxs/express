package com.example.express.controller.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.express.common.util.StringUtils;
import com.example.express.domain.ResponseResult;
import com.example.express.domain.bean.SysUser;
import com.example.express.domain.enums.OrderStatusEnum;
import com.example.express.domain.enums.PaymentStatusEnum;
import com.example.express.domain.enums.ResponseErrorCodeEnum;
import com.example.express.domain.enums.SysRoleEnum;
import com.example.express.domain.vo.BootstrapTableVO;
import com.example.express.domain.vo.courier.CourierOrderVO;
import com.example.express.domain.vo.OrderDescVO;
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
public class OrderApiController extends BaseApiController {
    @Autowired
    private OrderInfoService orderInfoService;

    /**
     * 获取订单信息
     * @author jitwxs
     * @date 2019/4/25 23:36
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_COURIER') or hasRole('ROLE_ADMIN')")
    public ResponseResult getOrderDesc(@PathVariable String id,
                                       @AuthenticationPrincipal SysUser sysUser) {
        //  普通用户仅允许访问自己订单
        if(sysUser.getRole() == SysRoleEnum.USER && !orderInfoService.isUserOrder(id, sysUser.getId())) {
            return ResponseResult.failure(ResponseErrorCodeEnum.NO_PERMISSION);
        }

        OrderDescVO descVO = orderInfoService.getDescVO(id);
        if(descVO == null) {
            return ResponseResult.failure(ResponseErrorCodeEnum.ORDER_NOT_EXIST);
        }

        return ResponseResult.success(descVO);
    }

    /**
     * 获取所有订单
     * - 普通用户：userId = self
     * - 配送员：courierId = self
     * - 管理员：无限制
     * @param type 0:正常订单；1：已删除订单
     * @author jitwxs
     * @date 2019/4/24 22:21
     */
    @GetMapping("/list")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_COURIER') or hasRole('ROLE_ADMIN')")
    public BootstrapTableVO listSelfOrder(@RequestParam(required = false, defaultValue = "1") Integer current,
                                                       @RequestParam(required = false, defaultValue = "10") Integer size,
                                                       String type, String startDate, String endDate, String status, String id, String payment,
                                                       @AuthenticationPrincipal SysUser sysUser) {
        Integer isDelete = StringUtils.toInteger(type, -1);
        if(isDelete == -1) {
            throw new CustomException(ResponseErrorCodeEnum.PARAMETER_ERROR);
        }

        StringBuilder sql = new StringBuilder();

        OrderStatusEnum orderStatusEnum = OrderStatusEnum.getByStatus(StringUtils.toInteger(status, -1));
        if(orderStatusEnum != null) {
            sql.append(" AND info.status = ").append(orderStatusEnum.getStatus());
        }

        PaymentStatusEnum paymentStatusEnum = PaymentStatusEnum.getByStatus(StringUtils.toInteger(payment, -1));
        if(paymentStatusEnum != null) {
            sql.append(" AND payment.status = ").append(paymentStatusEnum.getStatus());
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

        Page page = new Page<>(current, size);
        switch (sysUser.getRole()) {
            case USER:
                sql.append(" AND info.user_id = '").append(sysUser.getId()).append("'");
                return orderInfoService.pageUserOrderVO(page, sql.toString(), isDelete);
            case COURIER:
                sql.append(" AND info.courier_id = '").append(sysUser.getId()).append("'");
                return orderInfoService.pageCourierOrderVO(page, sql.toString());
            case ADMIN:
                return orderInfoService.pageAdminOrderVO(page, sql.toString());
            default:
                return new BootstrapTableVO();
        }
    }

    /**
     * 获取所有状态为等待派送（支付成功）订单
     */
    @GetMapping("/wait-list")
    @PreAuthorize("hasRole('ROLE_COURIER')")
    public BootstrapTableVO<CourierOrderVO> listWaitDistOrder(@RequestParam(required = false, defaultValue = "1") Integer current,
                                                              @RequestParam(required = false, defaultValue = "10") Integer size,
                                                              String startDate, String endDate, String id) {
        Page<CourierOrderVO> page = new Page<>(current, size);

        StringBuilder sql = new StringBuilder();
        sql.append(" AND info.status = ").append(OrderStatusEnum.WAIT_DIST.getStatus());
        sql.append(" AND payment.status = ").append(PaymentStatusEnum.TRADE_SUCCESS.getStatus());

        if(StringUtils.isNotBlank(startDate)) {
            sql.append(" AND info.create_date > '").append(startDate).append("'");
        }
        if(StringUtils.isNotBlank(endDate)) {
            sql.append(" AND info.create_date < '").append(endDate).append("'");
        }
        if(StringUtils.isNotBlank(id)) {
            sql.append(" AND info.id = ").append(id);
        }

        return orderInfoService.pageCourierOrderVO(page, sql.toString());
    }

    /**
     * 配送员批量接单
     */
    @PostMapping("/batch-accept")
    @PreAuthorize("hasRole('ROLE_COURIER')")
    public ResponseResult batchAccept(String[] ids, @AuthenticationPrincipal SysUser sysUser) {
        return orderInfoService.batchAcceptOrder(ids, sysUser.getId());
    }

    /**
     * 订单异常
     */
    @PostMapping("/error")
    @PreAuthorize("hasRole('ROLE_COURIER')")
    public ResponseResult errorOrder(String id, String remark, @AuthenticationPrincipal SysUser sysUser) {
        if(StringUtils.isAnyBlank(id, remark)) {
            return ResponseResult.failure(ResponseErrorCodeEnum.PARAMETER_ERROR);
        }
        if(remark.length() > CONTENT_MAX_LENGTH) {
            return ResponseResult.failure(ResponseErrorCodeEnum.STR_LENGTH_OVER, new Object[]{"异常信息", CONTENT_MAX_LENGTH});
        }

        if(!orderInfoService.isCourierOrder(id, sysUser.getId())) {
            return ResponseResult.failure(ResponseErrorCodeEnum.NO_PERMISSION);
        }

        return orderInfoService.handleOrder(id, OrderStatusEnum.ERROR, remark);
    }

    /**
     * 管理员批量订单异常
     */
    @PostMapping("/batch-error")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseResult batchErrorOrder(String[] ids, String remark) {
        if(ids.length == 0 || StringUtils.isBlank(remark)) {
            return ResponseResult.failure(ResponseErrorCodeEnum.PARAMETER_ERROR);
        }
        if(remark.length() > CONTENT_MAX_LENGTH) {
            return ResponseResult.failure(ResponseErrorCodeEnum.STR_LENGTH_OVER, new Object[]{"异常信息", CONTENT_MAX_LENGTH});
        }

        return orderInfoService.batchHandleOrder(ids, OrderStatusEnum.ERROR, remark);
    }

    /**
     * 订单完成
     */
    @PostMapping("/complete")
    @PreAuthorize("hasRole('ROLE_COURIER')")
    public ResponseResult completeOrder(String id, String remark, @AuthenticationPrincipal SysUser sysUser) {
        if(StringUtils.isAnyBlank(id, remark)) {
            return ResponseResult.failure(ResponseErrorCodeEnum.PARAMETER_ERROR);
        }
        if(remark.length() > CONTENT_MAX_LENGTH) {
            return ResponseResult.failure(ResponseErrorCodeEnum.STR_LENGTH_OVER, new Object[]{"成功信息", CONTENT_MAX_LENGTH});
        }

        if(!orderInfoService.isCourierOrder(id, sysUser.getId())) {
            return ResponseResult.failure(ResponseErrorCodeEnum.NO_PERMISSION);
        }

        return orderInfoService.handleOrder(id, OrderStatusEnum.COMPLETE, remark);
    }

    /**
     * 管理员批量订单完成
     */
    @PostMapping("/batch-complete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseResult batchCompleteOrder(String[] ids, String remark) {
        if(ids.length == 0 || StringUtils.isBlank(remark)) {
            return ResponseResult.failure(ResponseErrorCodeEnum.PARAMETER_ERROR);
        }
        if(remark.length() > CONTENT_MAX_LENGTH) {
            return ResponseResult.failure(ResponseErrorCodeEnum.STR_LENGTH_OVER, new Object[]{"成功信息", CONTENT_MAX_LENGTH});
        }

        return orderInfoService.batchHandleOrder(ids, OrderStatusEnum.COMPLETE, remark);
    }

    /**
     * 用户批量删除订单，仅能删除个人订单
     * 状态为订单完成或订单异常
     * @author jitwxs
     * @date 2019/4/24 23:08
     */
    @PostMapping("/batch-delete")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseResult batchDelete(String[] ids, @AuthenticationPrincipal SysUser sysUser) {
        return orderInfoService.batchDeleteOrder(ids, sysUser.getId());
    }

    /**
     * 用户批量撤销订单，仅能撤销个人订单
     * 状态为未接单
     * @author jitwxs
     * @date 2019/4/25 0:11
     */
    @PostMapping("/batch-cancel")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseResult batchCancel(String[] ids, @AuthenticationPrincipal SysUser sysUser) {
        return orderInfoService.batchCancelOrder(ids, sysUser.getId());
    }

    /**
     * 用户批量恢复订单，仅能恢复个人订单
     * @author jitwxs
     * @date 2019/4/26 1:58
     */
    @PostMapping("/batch-rollback")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseResult batchRollback(String[] ids, @AuthenticationPrincipal SysUser sysUser) {
        return orderInfoService.batchRollback(ids, sysUser.getId());
    }
}
