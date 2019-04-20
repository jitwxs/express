package com.example.express.controller;

import com.example.express.common.constant.SecurityConstant;
import com.example.express.common.constant.SessionKeyConstant;
import com.example.express.common.util.HttpClientUtils;
import com.example.express.common.util.JsonUtils;
import com.example.express.common.util.RandomUtils;
import com.example.express.common.util.StringUtils;
import com.example.express.domain.ResponseResult;
import com.example.express.domain.bean.SysUser;
import com.example.express.domain.enums.ResponseErrorCodeEnum;
import com.example.express.domain.enums.SysRoleEnum;
import com.example.express.domain.enums.ThirdLoginTypeEnum;
import com.example.express.domain.vo.RegistryVO;
import com.example.express.security.validate.third.ThirdLoginAuthenticationToken;
import com.example.express.service.OAuthService;
import com.example.express.service.SmsService;
import com.example.express.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@RestController
public class AuthController {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private OAuthService oAuthService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${server.addr}")
    private String serverAddress;
    @Value("${project.third-login.qq.app-id}")
    private String qqAppId;
    @Value("${project.third-login.qq.app-key}")
    private String qqAppKey;
    @Value("${project.sms.interval-min}")
    private String smsIntervalMins;

    /**
     * 获取短信验证码
     * @date 2019/4/17 22:40
     */
    @GetMapping(SecurityConstant.VALIDATE_CODE_URL_PREFIX + "/sms")
    public ResponseResult sendSms(String mobile, HttpSession session) {
        if(StringUtils.isBlank(mobile)) {
            return ResponseResult.failure(ResponseErrorCodeEnum.PARAMETER_ERROR);
        }
        if(!StringUtils.isValidTel(mobile)) {
            return ResponseResult.failure(ResponseErrorCodeEnum.TEL_NOT_LEGAL);
        }

        // 手机号是否注册
        if(!sysUserService.checkExistByTel(mobile)) {
            return ResponseResult.failure(ResponseErrorCodeEnum.TEL_NOT_EXIST);
        }

        // 如果Session中验证信息非空，判断是否超过间隔时间
        Long lastTimestamp = (Long) session.getAttribute(SessionKeyConstant.SMS_TIMESTAMP);
        // 间隔分钟
        int intervalMins = Integer.parseInt(smsIntervalMins);
        if (lastTimestamp != null) {
            long waitSeconds = (System.currentTimeMillis() - lastTimestamp) / 1000;
            if (waitSeconds < intervalMins * 60) {
                ResponseResult.failure(ResponseErrorCodeEnum.SMS_SEND_INTERVAL_TOO_SHORT, new Object[]{smsIntervalMins});
            }
        }

        // 发送验证码
        String verifyCode = RandomUtils.number(6);
        ResponseErrorCodeEnum codeEnum = smsService.send(session, mobile, verifyCode);
        return ResponseResult.failure(codeEnum);
    }

    /**
     * QQ登陆
     */
    @RequestMapping(SecurityConstant.QQ_LOGIN_URL)
    public void qqLogin(HttpServletResponse response) throws Exception {
        // QQ回调URL
        String qqCallbackUrl = serverAddress + SecurityConstant.QQ_CALLBACK_URL;
        // QQ认证服务器地址
        String url = "https://graph.qq.com/oauth2.0/authorize";
        // 生成并保存state，忽略该参数有可能导致CSRF攻击
        String state = oAuthService.genState();
        // 传递参数response_type、client_id、state、redirect_uri
        String param = "response_type=code&" + "client_id=" + qqAppId + "&state=" + state
                + "&redirect_uri=" + qqCallbackUrl;

        // 请求QQ认证服务器
        response.sendRedirect(url + "?" + param);
    }

    /**
     * QQ回调方法
     * @param code 授权码
     * @param state 应与发送时一致
     */
    @RequestMapping(SecurityConstant.QQ_CALLBACK_URL)
    public void qqCallback(String code, String state, HttpServletResponse response) throws Exception {
        // 验证state，如果不一致，可能被CSRF攻击
        if(!oAuthService.checkState(state)) {
            throw new Exception("State验证失败");
        }

        // 2、向QQ认证服务器申请令牌
        String url = "https://graph.qq.com/oauth2.0/token";
        // QQ回调URL
        String qqCallbackUrl = serverAddress + SecurityConstant.QQ_CALLBACK_URL;
        // 传递参数grant_type、code、redirect_uri、client_id
        String param = String.format("grant_type=authorization_code&code=%s&redirect_uri=%s&client_id=%s&client_secret=%s",
                code, qqCallbackUrl, qqAppId, qqAppKey);

        // 申请令牌，注意此处为post请求
        // QQ获取到的access token具有3个月有效期，用户再次登录时自动刷新。
        String result = HttpClientUtils.sendPostRequest(url, param);

        /*
         * result示例：
         * 成功：access_token=A24B37194E89A0DDF8DDFA7EF8D3E4F8&expires_in=7776000&refresh_token=BD36DADB0FE7B910B4C8BBE1A41F6783
         */
        Map<String, String> resultMap = HttpClientUtils.params2Map(result);
        // 如果返回结果中包含access_token，表示成功
        if(!resultMap.containsKey("access_token")) {
            throw  new Exception("获取token失败");
        }
        // 得到token
        String accessToken = resultMap.get("access_token");

        // 3、使用Access Token来获取用户的OpenID
        String meUrl = "https://graph.qq.com/oauth2.0/me";
        String meParams = "access_token=" + accessToken;
        String meResult = HttpClientUtils.sendGetRequest(meUrl, meParams);
        // 成功返回如下：callback( {"client_id":"YOUR_APPID","openid":"YOUR_OPENID"} );
        // 取出openid（openID为appId+qq号加密得到，对同一appId和QQ号来说是唯一的）
        String openId = getQQOpenid(meResult);

        // 三方登陆
        SysUser sysUser = sysUserService.thirdLogin(openId, ThirdLoginTypeEnum.QQ);
        // 注入框架
        ThirdLoginAuthenticationToken token = new ThirdLoginAuthenticationToken(sysUser.getId());
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 跳转首页
        response.sendRedirect(SecurityConstant.LOGIN_SUCCESS_URL);
    }

    /**
     * 提取Openid
     * @param str 形如：callback( {"client_id":"YOUR_APPID","openid":"YOUR_OPENID"} );
     * @author jitwxs
     * @since 2018/5/22 21:37
     */
    private String getQQOpenid(String str) {
        // 获取花括号内串
        String json = str.substring(str.indexOf("{"), str.indexOf("}") + 1);
        // 转为Map
        Map map = JsonUtils.jsonToObject(json, Map.class);
        return (String)map.get("openid");
    }

    /**
     * 用户注册
     * @date 2019/4/17 0:06
     */
    @PostMapping("/auth/register")
    public ResponseResult register(@RequestBody RegistryVO registryVO) {
        // 校验type
        SysRoleEnum roleEnum = SysRoleEnum.getByName(registryVO.getType());
        if (roleEnum == null) {
            return ResponseResult.failure(ResponseErrorCodeEnum.ROLE_ERROR);
        }
        // 校验用户名是否存在
        if (sysUserService.checkExistByUsername(registryVO.getUsername())) {
            return ResponseResult.failure(ResponseErrorCodeEnum.USERNAME_EXIST_ERROR);
        }
        // 校验type
        if(!StringUtils.isValidTel(registryVO.getTel())) {
            return ResponseResult.failure(ResponseErrorCodeEnum.TEL_NOT_LEGAL);
        }

        SysUser sysUser = SysUser.builder()
                .username(registryVO.getUsername())
                .password(passwordEncoder.encode(registryVO.getPassword()))
                .tel(registryVO.getTel())
                .role(roleEnum).build();

        if(sysUserService.save(sysUser)) {
            return ResponseResult.success();
        } else {
            return ResponseResult.failure(ResponseErrorCodeEnum.OPERATION_ERROR);
        }
    }
}
