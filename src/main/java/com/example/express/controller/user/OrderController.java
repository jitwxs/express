package com.example.express.controller.user;

import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.example.express.common.constant.SessionKeyConstant;
import com.example.express.common.util.JsonUtils;
import com.example.express.config.AliPayConfig;
import com.example.express.domain.ResponseResult;
import com.example.express.domain.bean.OrderInfo;
import com.example.express.domain.bean.SysUser;
import com.example.express.domain.enums.PaymentStatusEnum;
import com.example.express.domain.enums.ResponseErrorCodeEnum;
import com.example.express.exception.CustomException;
import com.example.express.service.OrderInfoService;
import com.example.express.service.OrderPaymentService;
import com.example.express.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 订单
 * @author jitwxs
 * @date 2019年04月23日 0:25
 */
@Controller
@RequestMapping("/order")
@PreAuthorize("hasRole('ROLE_USER')")
public class OrderController {
    @Autowired
    private AliPayConfig aliPayConfig;
    @Autowired
    private AlipayClient alipayClient;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private OrderInfoService orderInfoService;
    @Autowired
    private OrderPaymentService paymentService;

    /**
     * 支付宝支付方式
     * @param money 支付金额
     * @author jitwxs
     * @since 2018/5/14 8:53
     */
    @PostMapping("/alipay")
    public void paymentAlipay(Double money, HttpSession session, HttpServletResponse response, @AuthenticationPrincipal SysUser sysUser) throws IOException {
        OrderInfo orderInfo = (OrderInfo)session.getAttribute(SessionKeyConstant.SESSION_LATEST_EXPRESS);

        if(orderInfo == null || money == null) {
            throw new CustomException(ResponseErrorCodeEnum.PARAMETER_ERROR);
        }

        // 金额保留两位
        money = (double) (Math.round(money * 100)) / 100;

        // 生成订单 & 订单支付
        ResponseResult result1 = orderInfoService.createOrder(orderInfo, money, sysUser.getId());
        if(result1.getCode() != ResponseErrorCodeEnum.SUCCESS.getCode()) {
            throw new CustomException(result1);
        }

        String orderId = (String)result1.getData();

        // 1、设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        // 页面跳转同步通知页面路径
        alipayRequest.setReturnUrl(aliPayConfig.getReturnUrl());
        // 服务器异步通知页面路径
        alipayRequest.setNotifyUrl(aliPayConfig.getNotifyUrl());

        // 2、SDK已经封装掉了公共参数，这里只需要传入业务参数，请求参数查阅开头Wiki
        Map<String,String> map = new HashMap<>(16);
        map.put("out_trade_no", String.valueOf(orderId));
        map.put("total_amount", String.valueOf(money));
        map.put("subject", "在线支付");
        map.put("body", "大学校园快递代取管理系统");
        // 销售产品码
        map.put("product_code","FAST_INSTANT_TRADE_PAY");

        alipayRequest.setBizContent(JsonUtils.objectToJson(map));

        response.setContentType("text/html;charset=utf-8");
        try{
            // 3、生成支付表单
            AlipayTradePagePayResponse alipayResponse = alipayClient.pageExecute(alipayRequest);
            if(alipayResponse.isSuccess()) {
                String result = alipayResponse.getBody();
                response.getWriter().write(result);
            } else {
                response.getWriter().write("error");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 支付宝服务器同步回调
     */
    @GetMapping("/alipay/return")
    public String alipayReturn(HttpServletRequest request, @AuthenticationPrincipal SysUser sysUser, ModelMap map) {
        // 获取参数
        Map<String,String> params = getPayParams(request);
        try {
            // 验证订单
            boolean flag = paymentService.validAlipay(params);
            if(flag) {
                // 验证成功后，修改订单状态为已支付
                String orderId = params.get("out_trade_no");
                /*
                 * 订单状态（与官方统一）
                 * WAIT_BUYER_PAY：交易创建，等待买家付款；
                 * TRADE_CLOSED：未付款交易超时关闭，或支付完成后全额退款；
                 * TRADE_SUCCESS：交易支付成功；
                 * TRADE_FINISHED：支付结束，不可退款
                 */
                // 获取支付宝订单号
                String tradeNo = params.get("trade_no");
                // 更新状态
                paymentService.updateStatus(orderId, PaymentStatusEnum.TRADE_SUCCESS, tradeNo);

                HttpSession session = request.getSession();
                // 将支付信息写入session
                session.setAttribute(SessionKeyConstant.SESSION_LATEST_PAYMENT, paymentService.getById(orderId));
                // 支付成功后删除Express的session
                session.removeAttribute(SessionKeyConstant.SESSION_LATEST_EXPRESS);
            } else {
                throw new Exception("支付宝支付验签失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        map.put("frontName", sysUserService.getFrontName(sysUser));
        return "user/paymentResult";
    }

    /**
     * 支付宝服务器异步通知，获取支付宝POST过来反馈信息
     * 该方法无返回值，静默处理
     * 订单的状态已该方法为主，其他的状态修改方法为辅 *
     * （1）程序执行完后必须打印输出“success”（不包含引号）。
     * 如果商户反馈给支付宝的字符不是success这7个字符，支付宝服务器会不断重发通知，直到超过24小时22分钟。
     * （2）程序执行完成后，该页面不能执行页面跳转。
     * 如果执行页面跳转，支付宝会收不到success字符，会被支付宝服务器判定为该页面程序运行出现异常，而重发处理结果通知
     * （3）cookies、session等在此页面会失效，即无法获取这些数据
     * （4）该方式的调试与运行必须在服务器上，即互联网上能访问 *
     * @author jitwxs
     * @since 2018/6/4 14:45
     */
    @PostMapping("/alipay/notify")
    public void alipayNotify(HttpServletRequest request,  HttpServletResponse response){
        /*
         默认只有TRADE_SUCCESS会触发通知，如果需要开通其他通知，请联系客服申请
         触发条件名 	    触发条件描述 	触发条件默认值
        TRADE_FINISHED 	交易完成 	false（不触发通知）
        TRADE_SUCCESS 	支付成功 	true（触发通知）
        WAIT_BUYER_PAY 	交易创建 	false（不触发通知）
        TRADE_CLOSED 	支付关闭 	false（不触发通知）
        来源：https://docs.open.alipay.com/270/105902/#s2
         */
        // 获取参数
        Map<String,String> params = getPayParams(request);
        try{
            // 验证订单
            boolean flag = paymentService.validAlipay(params);
            if(flag) {
                //商户订单号
                String orderId = params.get("out_trade_no");
                //支付宝交易号
                String tradeNo = params.get("trade_no");
                //交易状态
                String tradeStatus = params.get("trade_status");

                switch (tradeStatus) {
                    case "WAIT_BUYER_PAY":
                        paymentService.updateStatus(orderId, PaymentStatusEnum.WAIT_BUYER_PAY);
                        break;
                    /*
                     * 关闭订单
                     * （1)订单已创建，但用户未付款，调用关闭交易接口
                     * （2）付款成功后，订单金额已全部退款【如果没有全部退完，仍是TRADE_SUCCESS状态】
                     */
                    case "TRADE_CLOSED":
                        paymentService.updateStatus(orderId, PaymentStatusEnum.TRADE_CLOSED);
                        break;
                    /*
                     * 订单完成
                     * （1）退款日期超过可退款期限后
                     */
                    case "TRADE_FINISHED" :
                        paymentService.updateStatus(orderId, PaymentStatusEnum.TRADE_FINISHED);
                        break;
                    /*
                     * 订单Success
                     * （1）用户付款成功
                     */
                    case "TRADE_SUCCESS" :
                        paymentService.updateStatus(orderId, PaymentStatusEnum.TRADE_SUCCESS, tradeNo);
                        break;
                    default:break;
                }
                response.getWriter().write("success");
            }else {
                response.getWriter().write("fail");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 获取支付参数
     * @author jitwxs
     * @since 2018/6/4 16:39
     */
    private Map<String,String> getPayParams(HttpServletRequest request) {
        Map<String,String> params = new HashMap<>(16);
        Map<String,String[]> requestParams = request.getParameterMap();

        Iterator<String> iter = requestParams.keySet().iterator();
        while (iter.hasNext()) {
            String name = iter.next();
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
//            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        return params;
    }
}
