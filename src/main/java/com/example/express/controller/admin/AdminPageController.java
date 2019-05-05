package com.example.express.controller.admin;

import com.example.express.domain.bean.SysUser;
import com.example.express.domain.vo.user.UserInfoVO;
import com.example.express.service.OrderInfoService;
import com.example.express.service.SysUserService;
import com.example.express.service.UserFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * 管理员页面 Controller
 * @author xiangsheng.wu
 * @date 2019年05月02日 14:38
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminPageController {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private OrderInfoService orderInfoService;
    @Autowired
    private UserFeedbackService feedbackService;

    /**
     * 仪表盘页面
     */
    @RequestMapping("/dashboard")
    public String showDashboardPage(@AuthenticationPrincipal SysUser sysUser, ModelMap map) {
        String frontName = sysUserService.getFrontName(sysUser);
        map.put("frontName", frontName);

        Map<String, Integer> data = sysUserService.getAdminDashboardData();

        String userDesc = "今日注册用数：" + data.get("today") +
                "，总用户数：" + data.get("total") +
                "，其中禁用用户数：" + data.get("disEnable") +
                "，冻结用户数：" + data.get("lock");
        map.put("userDesc", userDesc);

        Map<String, Integer> data1 = orderInfoService.getAdminDashboardData();
        String orderDesc = "今日新增订单数：" + data1.get("today") +
                "，总等待接单数：：" + data1.get("wait") +
                "，正在派送数：" + data1.get("transport");
        map.put("orderDesc", orderDesc);

        Map<String, Integer> data2 = feedbackService.getAdminDashboardData();
        String feedbackDesc = "今日新增反馈数：" + data2.get("today") +
                "，等待处理数：" + data2.get("wait");
        map.put("feedbackDesc", feedbackDesc);

        return "admin/dashboard";
    }

    /**
     * 订单管理页面
     */
    @RequestMapping("/order")
    public String showOrderPage(@AuthenticationPrincipal SysUser sysUser, ModelMap map) {
        map.put("frontName", sysUserService.getFrontName(sysUser));
        return "admin/order";
    }

    /**
     * 订单管理页面
     */
    @RequestMapping("/recycle")
    public String showRecyclePage(@AuthenticationPrincipal SysUser sysUser, ModelMap map) {
        map.put("frontName", sysUserService.getFrontName(sysUser));
        return "admin/recycle";
    }

    /**
     * 反馈管理页面
     */
    @RequestMapping("/feedback")
    public String showFeedbackPage(@AuthenticationPrincipal SysUser sysUser,ModelMap map) {
        map.put("frontName", sysUserService.getFrontName(sysUser));
        return "admin/feedback";
    }

    /**
     * 用户管理页面
     */
    @RequestMapping("/user")
    public String showUser(@AuthenticationPrincipal SysUser sysUser, ModelMap map) {
        map.put("frontName", sysUserService.getFrontName(sysUser));
        return "admin/user";
    }

    /**
     * 收益管理页面
     */
    @RequestMapping("/profit")
    public String showProfit(@AuthenticationPrincipal SysUser sysUser, ModelMap map) {
        map.put("frontName", sysUserService.getFrontName(sysUser));
        return "admin/profit";
    }

    /**
     * 个人中心页面
     */
    @RequestMapping("/info")
    public String showInfoPage(@AuthenticationPrincipal SysUser sysUser,ModelMap map) {
        map.put("frontName", sysUserService.getFrontName(sysUser));
        UserInfoVO userInfo = sysUserService.getUserInfo(sysUser.getId());
        map.put("info", userInfo);
        return "admin/info";
    }

    /**
     * 日志管理页面
     */
    @RequestMapping("/log")
    public String showLogPage(@AuthenticationPrincipal SysUser sysUser,ModelMap map) {
        map.put("frontName", sysUserService.getFrontName(sysUser));
        return "admin/log";
    }
}
