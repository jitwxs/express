package com.example.express.controller.courier;

import com.example.express.domain.bean.SysUser;
import com.example.express.domain.enums.SysRoleEnum;
import com.example.express.domain.vo.user.UserInfoVO;
import com.example.express.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * 配送员页面 Controller
 * @author xiangsheng.wu
 * @date 2019年04月23日 14:38
 */
@Controller
@RequestMapping("/courier")
@PreAuthorize("hasRole('ROLE_COURIER')")
public class CourierPageController {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private OrderInfoService orderInfoService;
    @Autowired
    private OrderEvaluateService orderEvaluateService;
    @Autowired
    private UserFeedbackService feedbackService;
    @Autowired
    private UserEvaluateService userEvaluateService;

    /**
     * 仪表盘页面
     */
    @RequestMapping("/dashboard")
    public String showDashboardPage(@AuthenticationPrincipal SysUser sysUser, ModelMap map) {
        map.put("frontName", sysUserService.getFrontName(sysUser));

        String score = userEvaluateService.getScoreFromCache(sysUser.getId());
        int evaluateCount = orderEvaluateService.countEvaluate(sysUser.getId(), SysRoleEnum.COURIER);

        String userDesc = "您共收到：" + evaluateCount + "条评价，您的综合评分为：" + score + "分";
        map.put("evaluateDesc", userDesc);


        Map<String, Integer> data1 = orderInfoService.getCourierDashboardData(sysUser.getId());
        String orderDesc = "可以接单数：" + data1.get("wait") +
                "，需要派送订单数：" + data1.get("transport");
        map.put("orderDesc", orderDesc);

        Map<String, Integer> data2 = feedbackService.getCourierDashboardData();
        String feedbackDesc = "今日系统新增反馈数：" + data2.get("today") +
                "，系统等待处理数：" + data2.get("wait");
        map.put("feedbackDesc", feedbackDesc);

        return "courier/dashboard";
    }
    /**
     * 接单大厅页面
     */
    @RequestMapping("/order")
    public String showOrderPage(@AuthenticationPrincipal SysUser sysUser, ModelMap map) {
        map.put("frontName", sysUserService.getFrontName(sysUser));
        return "courier/order";
    }

    /**
     * 订单列表页面
     */
    @RequestMapping("/history")
    public String showHistory(@AuthenticationPrincipal SysUser sysUser, ModelMap map) {
        map.put("frontName", sysUserService.getFrontName(sysUser));
        return "courier/history";
    }

    /**
     * 评价中心页面
     */
    @RequestMapping("/evaluate")
    public String showEvaluate(@AuthenticationPrincipal SysUser sysUser,ModelMap map) {
        map.put("frontName", sysUserService.getFrontName(sysUser));
        map.put("score", userEvaluateService.getScoreFromCache(sysUser.getId()));
        return "courier/evaluate";
    }

    /**
     * 个人中心页面
     */
    @RequestMapping("/info")
    public String showInfoPage(@AuthenticationPrincipal SysUser sysUser,ModelMap map) {
        map.put("frontName", sysUserService.getFrontName(sysUser));
        UserInfoVO userInfo = sysUserService.getUserInfo(sysUser.getId());
        map.put("info", userInfo);
        return "courier/info";
    }

    /**
     * 操作日志页面
     */
    @RequestMapping("/log")
    public String showLogPage(@AuthenticationPrincipal SysUser sysUser,ModelMap map) {
        map.put("frontName", sysUserService.getFrontName(sysUser));
        return "courier/log";
    }

    /**
     * 反馈建议页面
     */
    @RequestMapping("/feedback")
    public String showFeedbackPage(@AuthenticationPrincipal SysUser sysUser,ModelMap map) {
        map.put("frontName", sysUserService.getFrontName(sysUser));
        return "courier/feedback";
    }
}
