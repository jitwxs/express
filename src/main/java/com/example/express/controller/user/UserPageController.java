package com.example.express.controller.user;

import com.example.express.common.constant.SessionKeyConstant;
import com.example.express.domain.bean.OrderInfo;
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

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 普通用户页面 Controller
 * @date 2019年04月20日 23:29
 */
@Controller
@RequestMapping("/user")
@PreAuthorize("hasRole('ROLE_USER')")
public class UserPageController {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private OrderInfoService orderInfoService;
    @Autowired
    private UserFeedbackService feedbackService;
    @Autowired
    private UserEvaluateService userEvaluateService;
    @Autowired
    private OrderEvaluateService orderEvaluateService;
    @Autowired
    private DataCompanyService dataCompanyService;

    /**
     * 仪表盘页面
     */
    @RequestMapping("/dashboard")
    public String showDashboardPage(@AuthenticationPrincipal SysUser sysUser, ModelMap map) {
        map.put("frontName", sysUserService.getFrontName(sysUser));

        String score = userEvaluateService.getScoreFromCache(sysUser.getId());
        int evaluateCount = orderEvaluateService.countEvaluate(sysUser.getId(), SysRoleEnum.USER);

        String userDesc = "您共收到：" + evaluateCount + "条评价，您的综合评分为：" + score + "分";
        map.put("evaluateDesc", userDesc);

        Map<String, Integer> data1 = orderInfoService.getUserDashboardData(sysUser.getId());
        String orderDesc = "未支付订单数：：" + data1.get("waitPayment") +
                "，等待接单数：：" + data1.get("wait") +
                "，正在派送数：" + data1.get("transport");
        map.put("orderDesc", orderDesc);

        Map<String, Integer> data2 = feedbackService.getUserDashboardData(sysUser.getId());
        String feedbackDesc = "正在处理的反馈数：" + data2.get("process") +
                "，未处理的反馈数：" + data2.get("wait");
        map.put("feedbackDesc", feedbackDesc);

        return "user/dashboard";
    }
    /**
     * 下单页面
     */
    @RequestMapping("/order")
    public String showOrderPage(@AuthenticationPrincipal SysUser sysUser, ModelMap map) {
        map.put("frontName", sysUserService.getFrontName(sysUser));
        return "user/order";
    }

    /**
     * 支付页面
     * @author jitwxs
     * @date 2019/4/23 0:00
     */
    @RequestMapping("/order/place")
    public String placeOrder(OrderInfo orderInfo, ModelMap map, HttpSession session, @AuthenticationPrincipal SysUser sysUser) {
        map.put("frontName", sysUserService.getFrontName(sysUser));
        map.put("order", orderInfo);
        map.put("company", dataCompanyService.getByCache(orderInfo.getCompany()).getName());
        session.setAttribute(SessionKeyConstant.SESSION_LATEST_EXPRESS, orderInfo);
        return "user/payment";
    }

    /**
     * 订单列表页面
     */
    @RequestMapping("/history")
    public String showHistory(@AuthenticationPrincipal SysUser sysUser, ModelMap map) {
        map.put("frontName", sysUserService.getFrontName(sysUser));
        return "user/history";
    }

    /**
     * 评价中心页面
     */
    @RequestMapping("/evaluate")
    public String showEvaluate(@AuthenticationPrincipal SysUser sysUser,ModelMap map) {
        map.put("frontName", sysUserService.getFrontName(sysUser));
        map.put("score", userEvaluateService.getScoreFromCache(sysUser.getId()));
        return "user/evaluate";
    }

    /**
     * 个人中心页面
     */
    @RequestMapping("/info")
    public String showInfoPage(@AuthenticationPrincipal SysUser sysUser,ModelMap map) {
        map.put("frontName", sysUserService.getFrontName(sysUser));
        UserInfoVO userInfo = sysUserService.getUserInfo(sysUser.getId());
        map.put("info", userInfo);
        return "user/info";
    }

    /**
     * 回收站页面
     */
    @RequestMapping("/recycle")
    public String showRecyclePage(@AuthenticationPrincipal SysUser sysUser,ModelMap map) {
        map.put("frontName", sysUserService.getFrontName(sysUser));
        return "user/recycle";
    }

    /**
     * 操作日志页面
     */
    @RequestMapping("/log")
    public String showLogPage(@AuthenticationPrincipal SysUser sysUser,ModelMap map) {
        map.put("frontName", sysUserService.getFrontName(sysUser));
        return "user/log";
    }

    /**
     * 反馈建议页面
     */
    @RequestMapping("/feedback")
    public String showFeedbackPage(@AuthenticationPrincipal SysUser sysUser,ModelMap map) {
        map.put("frontName", sysUserService.getFrontName(sysUser));
        return "user/feedback";
    }

    /**
     * 收货地址页面
     */
    @RequestMapping("/address")
    public String showAddressPage(@AuthenticationPrincipal SysUser sysUser,ModelMap map) {
        map.put("frontName", sysUserService.getFrontName(sysUser));
        return "user/address";
    }
}
