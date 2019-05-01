package com.example.express.controller.courier;

import com.example.express.controller.BaseController;
import com.example.express.domain.bean.SysUser;
import com.example.express.domain.vo.UserInfoVO;
import com.example.express.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 配送员页面 Controller
 * @author xiangsheng.wu
 * @date 2019年04月23日 14:38
 */
@Controller
@RequestMapping("/courier")
@PreAuthorize("hasRole('ROLE_COURIER')")
public class CourierPageController extends BaseController {
    @Autowired
    private SysUserService sysUserService;

    /**
     * 仪表盘页面
     */
    @RequestMapping("/dashboard")
    public String showDashboardPage(@AuthenticationPrincipal SysUser sysUser, ModelMap map) {
        initModelMap(map, sysUser);
        return "courier/dashboard";
    }
    /**
     * 接单大厅页面
     */
    @RequestMapping("/order")
    public String showOrderPage(@AuthenticationPrincipal SysUser sysUser, ModelMap map) {
        initModelMap(map, sysUser);
        return "courier/order";
    }

    /**
     * 订单列表页面
     */
    @RequestMapping("/history")
    public String showHistory(@AuthenticationPrincipal SysUser sysUser, ModelMap map) {
        initModelMap(map, sysUser);
        return "courier/history";
    }

    /**
     * 个人中心页面
     */
    @RequestMapping("/info")
    public String showInfoPage(@AuthenticationPrincipal SysUser sysUser,ModelMap map) {
        initModelMap(map, sysUser);
        UserInfoVO userInfo = sysUserService.getUserInfo(sysUser.getId());
        map.put("info", userInfo);
        return "courier/info";
    }

    /**
     * 操作日志页面
     */
    @RequestMapping("/log")
    public String showLogPage(@AuthenticationPrincipal SysUser sysUser,ModelMap map) {
        initModelMap(map, sysUser);
        return "courier/log";
    }

    /**
     * 反馈建议页面
     */
    @RequestMapping("/feedback")
    public String showFeedbackPage(@AuthenticationPrincipal SysUser sysUser,ModelMap map) {
        initModelMap(map, sysUser);
        return "courier/feedback";
    }
}
