package com.example.express.controller;

import com.example.express.aop.RequestRateLimit;
import com.example.express.common.constant.RedisKeyConstant;
import com.example.express.common.constant.SecurityConstant;
import com.example.express.common.constant.SessionKeyConstant;
import com.example.express.common.util.*;
import com.example.express.domain.ResponseResult;
import com.example.express.domain.bean.SysUser;
import com.example.express.domain.enums.*;
import com.example.express.security.validate.third.ThirdLoginAuthenticationToken;
import com.example.express.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@RestController
public class AuthController {
    @Autowired
    private AipService aipService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private OAuthService oAuthService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private DataSchoolService dataSchoolService;

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Value("${server.addr}")
    private String serverAddress;
    @Value("${project.third-login.qq.app-id}")
    private String qqAppId;
    @Value("${project.third-login.qq.app-key}")
    private String qqAppKey;
    @Value("${project.sms.interval-min}")
    private String smsIntervalMins;

    /**
     * 验证图形验证码
     * @author jitwxs
     * @since 2018/5/2 0:02
     */
    @PostMapping(SecurityConstant.VALIDATE_CODE_URL_PREFIX + "/check-img")
    public ResponseResult checkVerifyCode(String code, HttpSession session) {
        if(StringUtils.isBlank(code)) {
            return ResponseResult.failure(ResponseErrorCodeEnum.PARAMETER_ERROR);
        }

        Object systemCode = session.getAttribute("validateCode");
        if(systemCode == null) {
            return ResponseResult.failure(ResponseErrorCodeEnum.SYSTEM_ERROR);
        }

        String validateCode = ((String)systemCode).toLowerCase();

        if(!validateCode.equals(code.toLowerCase())) {
            return ResponseResult.failure(ResponseErrorCodeEnum.VERIFY_CODE_ERROR);
        }

        return ResponseResult.success();
    }

    /**
     * 获取短信验证码
     * @date 2019/4/17 22:40
     */
    @RequestRateLimit(limit = RateLimitEnum.RRLimit_1_60)
    @GetMapping(SecurityConstant.VALIDATE_CODE_URL_PREFIX + "/sms")
    public ResponseResult sendSms(String mobile, HttpSession session) {
        if(StringUtils.isBlank(mobile)) {
            return ResponseResult.failure(ResponseErrorCodeEnum.PARAMETER_ERROR);
        }
        if(!StringUtils.isValidTel(mobile)) {
            return ResponseResult.failure(ResponseErrorCodeEnum.TEL_INVALID);
        }

        // 如果Session中验证信息非空，判断是否超过间隔时间
        Object lastTimestamp = session.getAttribute(SessionKeyConstant.SMS_TIMESTAMP);
        // 间隔分钟
        int intervalMins = Integer.parseInt(smsIntervalMins);
        if (lastTimestamp != null) {
            long waitSeconds = (System.currentTimeMillis() - Long.parseLong((String)lastTimestamp)) / 1000;
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
    public void qqCallback(String code, String state, HttpSession session, HttpServletResponse response) throws Exception {
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
            throw new Exception("获取token失败");
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
        ResponseResult result1 = sysUserService.thirdLogin(openId, ThirdLoginTypeEnum.QQ);
        if(result1.getCode() != ResponseErrorCodeEnum.SUCCESS.getCode()) {
            session.setAttribute(SecurityConstant.LAST_EXCEPTION, result1);
            response.sendRedirect(SecurityConstant.UN_AUTHENTICATION_URL);
            return;
        }

        // 注入框架
        SysUser sysUser = (SysUser) result1.getData();
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
     * 人脸登录
     * @author jitwxs
     * @date 2019/5/3 0:47
     */
    @SuppressWarnings("Duplicates")
    @PostMapping("/auth/face-login")
    public ResponseResult faceLogin(String data) {
        String base64Prefix = "data:image/png;base64,";
        if(StringUtils.isBlank(data)) {
            return ResponseResult.failure(ResponseErrorCodeEnum.PARAMETER_ERROR);
        }
        if(data.startsWith(base64Prefix)) {
            data = data.substring(base64Prefix.length());
        }

        ResponseResult result = aipService.faceSearchByBase64(data);
        if(result.getCode() != ResponseErrorCodeEnum.SUCCESS.getCode()) {
            return result;
        }

        // 人脸登录和三方登录一样，无需鉴权，因此使用三方登录的方式注入框架即可
        SysUser sysUser = (SysUser) result.getData();
        ThirdLoginAuthenticationToken token = new ThirdLoginAuthenticationToken(sysUser.getId());
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return ResponseResult.success();
    }

    /**
     * 人脸校验
     * 未登录：存session
     * 已登录：存redis
     * @author jitwxs
     * @date 2019/5/3 0:23
     */
    @SuppressWarnings("unchecked")
    @PostMapping("/auth/face-check")
    public ResponseResult faceCheck(HttpSession session, String data, @AuthenticationPrincipal SysUser sysUser) {
        String base64Prefix = "data:image/png;base64,";
        if(StringUtils.isBlank(data)) {
            return ResponseResult.failure(ResponseErrorCodeEnum.PARAMETER_ERROR);
        }
        if(data.startsWith(base64Prefix)) {
            data = data.substring(base64Prefix.length());
        }
        ResponseResult result = aipService.faceDetectByBase64(data, true);
        if(result.getCode() != ResponseErrorCodeEnum.SUCCESS.getCode()) {
            return result;
        }

        if (sysUser == null) {
            // 暂存face_token于session，用于人脸注册
            session.setAttribute(SessionKeyConstant.REGISTER_FACE_TOKEN, result.getData());
        } else {
            try {
                // 暂存face_token于redis，用于人脸绑定和人脸更新
                Map<String, String> resultData = (Map<String, String>) result.getData();
                String faceToken = resultData.get("face_token");

                redisTemplate.opsForHash().put(RedisKeyConstant.LAST_FACE_TOKEN, sysUser.getId(), faceToken);
            } catch (Exception e) {
                return ResponseResult.failure(ResponseErrorCodeEnum.REDIS_ERROR);
            }
        }

        return ResponseResult.success();
    }

    /**
     * 用户注册
     * @param type 注册类型 1：用户名密码；2：短信验证码；3：人脸
     * @date 2019/4/17 0:06
     */
    @SuppressWarnings("unchecked")
    @PostMapping("/auth/register")
    public ResponseResult register(Integer type, String username, String password,
                                   String tel, String code, HttpSession session) {
        if(type == null) {
            return ResponseResult.failure(ResponseErrorCodeEnum.PARAMETER_ERROR);
        }
        switch (type) {
            case 1:
                if(StringUtils.isAnyBlank(username, password)) {
                    return ResponseResult.failure(ResponseErrorCodeEnum.PARAMETER_ERROR);
                }
                return sysUserService.registryByUsername(username, password);
            case 2:
                if(StringUtils.isAnyBlank(tel, code)) {
                    return ResponseResult.failure(ResponseErrorCodeEnum.PARAMETER_ERROR);
                }
                if(!StringUtils.isValidTel(tel)) {
                    return ResponseResult.failure(ResponseErrorCodeEnum.TEL_INVALID);
                }
                return sysUserService.registryByTel(tel, code, session);
            case 3:
                // 读取face_token
                Map<String, String> map = (Map<String, String>) session.getAttribute(SessionKeyConstant.REGISTER_FACE_TOKEN);
                String faceToken = map.get("face_token");
                String gender = map.get("gender");
                if(StringUtils.isAnyBlank(faceToken, gender)) {
                    return ResponseResult.failure(ResponseErrorCodeEnum.NOT_FACE_TO_REGISTRY);
                }

                return sysUserService.registryByFace(faceToken, gender);
            default:
                return ResponseResult.failure(ResponseErrorCodeEnum.PARAMETER_ERROR);
        }
    }

    /**
     * 信息补全
     * @author jitwxs
     * @date 2019/4/21 21:07
     */
    @PostMapping("/auth/complete-info")
    public ResponseResult completeInfo(Integer role, Integer school, Integer sex, String studentIdCard, String realName, String idCard,
                                       @AuthenticationPrincipal SysUser sysUser) {
        if(role == null || school == null || sex == null || StringUtils.isBlank(studentIdCard)) {
            return ResponseResult.failure(ResponseErrorCodeEnum.PARAMETER_ERROR);
        }

        // 仅支持申请普通用户、配送员
        SysRoleEnum roleEnum = SysRoleEnum.getByType(role);
        if(roleEnum != SysRoleEnum.USER && roleEnum != SysRoleEnum.COURIER) {
            return ResponseResult.failure(ResponseErrorCodeEnum.PARAMETER_ERROR);
        }
        sysUser.setRole(roleEnum);

        // 校验 schoolId
        if(!dataSchoolService.isExist(school)) {
            return ResponseResult.failure(ResponseErrorCodeEnum.SCHOOL_NOT_EXIST);
        }
        // 校验 studentIdCard
        if(!StringUtils.isNumeric(studentIdCard)) {
            return ResponseResult.failure(ResponseErrorCodeEnum.STUDENT_IDCARD_NOT_NUMBER);
        }

        sysUser.setSchoolId(school);
        sysUser.setStudentIdCard(studentIdCard);

        // 校验性别
        SexEnum sexEnum = SexEnum.getByType(sex);
        if(sexEnum == null) {
            return ResponseResult.failure(ResponseErrorCodeEnum.PARAMETER_ERROR);
        }
        sysUser.setSex(sexEnum);

        // 配送员必填真实姓名、身份证号
        if(roleEnum == SysRoleEnum.COURIER) {
            if(StringUtils.isAnyBlank(realName, idCard)) {
                return ResponseResult.failure(ResponseErrorCodeEnum.PARAMETER_ERROR);
            }
            if(!IDValidateUtils.check(idCard)) {
                return ResponseResult.failure(ResponseErrorCodeEnum.ID_CARD_INVALID);
            }
            if(StringUtils.containsSpecial(realName) || StringUtils.containsNumber(realName)) {
                return ResponseResult.failure(ResponseErrorCodeEnum.REAL_NAME_INVALID);
            }

            sysUser.setIdCard(idCard);
            sysUser.setRealName(realName);
        }

        if(!sysUserService.updateById(sysUser)) {
            return ResponseResult.failure(ResponseErrorCodeEnum.OPERATION_ERROR);
        }

        return ResponseResult.success();
    }
}
