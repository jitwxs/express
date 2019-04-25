package com.example.express.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.express.domain.ResponseResult;
import com.example.express.domain.bean.OrderInfo;
import com.example.express.domain.enums.SysRoleEnum;
import com.example.express.domain.vo.BootstrapTableVO;
import com.example.express.domain.vo.OrderDescVO;
import com.example.express.domain.vo.OrderVO;

public interface OrderInfoService extends IService<OrderInfo> {
    /**
     * 检查是否有未完成的订单
     * 订单状态为 WAIT_DIST 或 TRANSPORT 的订单
     * @param roleEnum ROLE_USER: order表userId；ROLE_COURIER:order表courierId
     */
    boolean isExistUnfinishedOrder(String userId, SysRoleEnum roleEnum);

    boolean isExist(String orderId);
    /**
     * 手动删除
     * @author jitwxs
     * @date 2019/4/25 0:13
     */
    boolean manualDelete(String orderId, int hasDelete, int deleteType);

    /**
     * 是否是某位用户的订单
     * @author jitwxs
     * @date 2019/4/26 0:53
     */
    boolean isUserOrder(String orderId, String userId);
    /**
     * 生成订单 & 订单支付
     * @author jitwxs
     * @date 2019/4/23 0:40
     */
    ResponseResult createOrder(OrderInfo orderInfo, double money, String uid);

    OrderDescVO getDescVO(String orderId);

    /**
     * 分页查询订单
     * @param isDelete 0：未删除；1：已删除
     */
    BootstrapTableVO<OrderVO> pageOrderVO(Page<OrderVO> page, String selectSql, int isDelete);
    /**
     * 批量删除订单
     */
    ResponseResult batchDeleteOrder(String[] ids, String userId);
    /**
     * 批量撤销订单
     */
    ResponseResult batchCancelOrder(String[] ids, String userId);
    /**
     * 批量恢复订单
     */
    ResponseResult batchRollback(String[] ids, String id);
}
