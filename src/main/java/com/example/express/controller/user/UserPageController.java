package com.example.express.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 普通用户页面 Controller
 * @date 2019年04月20日 23:29
 */
@Controller
@RequestMapping("/user")
public class UserPageController {
    /**
     * 仪表盘页面
     */
    @RequestMapping("/dashboard")
    public String showDashboardPage() {
        return "user/dashboard";
    }
    /**
     * 下单页面
     */
    @RequestMapping("/order")
    public String showOrderPaage() {
        return "user/order";
    }
    /**
     * 订单列表页面
     */
    @RequestMapping("/history")
    public String showHistory() {
        return "user/history";
    }
    /**
     * 个人信息页面
     */
    @RequestMapping("/info")
    public String showInfoPage() {
        return "user/info";
    }
    /**
     * 设置页面
     */
    @RequestMapping("/setting")
    public String showSettingPage() {
        return "user/setting";
    }
}
