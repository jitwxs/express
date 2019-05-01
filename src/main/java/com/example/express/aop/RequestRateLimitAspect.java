package com.example.express.aop;

import com.example.express.domain.ResponseResult;
import com.example.express.domain.bean.SysUser;
import com.example.express.domain.enums.ResponseErrorCodeEnum;
import com.example.express.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
public class RequestRateLimitAspect {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;
    @Autowired
    private RedisService redisService;

    @Pointcut(value = "@annotation(com.example.express.aop.RequestRateLimit)")
    public void pointcut() {
    }

    @Around(value = "pointcut() && @annotation(control)")
    public Object around(final ProceedingJoinPoint point, final RequestRateLimit control) throws Throwable {
        // 获取请求方法
        final Method method = ((MethodSignature) point.getSignature()).getMethod();
        // 获取请求URI
        String uri = request.getRequestURI();
        // 用户代理
        String userAgent = request.getHeader("user-agent");

        // 用户Key。登陆状态取用户ID，未登录取IP
        String userKey;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication instanceof AnonymousAuthenticationToken) {
            userKey = getIpAddress(request);
        } else {
            SysUser user = (SysUser) authentication.getPrincipal();
            userKey = user.getId();
        }

        // 取得配置的限速
        final int[] limit = limitMaxAndExpire(control);
        // 拼接限速 key
        String limitKey = String.format("express_api_request_limit_rate_%s_%s_%s", userKey, method.getName(), uri);
        if(redisService.checkRequestRateLimit(limitKey, limit[1], limit[0], TimeUnit.SECONDS, userAgent)) {
//            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            return ResponseResult.failure(ResponseErrorCodeEnum.REQUEST_TOO_HIGH);
        }

        // 接口请求相应的耗时ms
        long startTime = System.currentTimeMillis();
        Object results = point.proceed();
        long endTime = System.currentTimeMillis();
        log.info("接口{}, 耗时：{} ms", method.getName(), endTime - startTime);

        return results;
    }

    private static int[] limitMaxAndExpire(final RequestRateLimit control) {
        String limit = control.limit().limit();
        int[] result = new int[2];
        String[] limits = limit.split("/");
        result[0] = Integer.parseInt(limits[0]);
        result[1] = Integer.parseInt(limits[1]);
        return result;
    }

    /**
     * 获取用户真实IP地址，不使用request.getRemoteAddr()的原因是有可能用户使用了代理软件方式避免真实IP地址,
     * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值
     *
     * @return ip
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        log.info("x-forwarded-for ip: " + ip);
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            if( ip.indexOf(",")!=-1 ){
                ip = ip.split(",")[0];
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
            log.info("Proxy-Client-IP ip: " + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
            log.info("WL-Proxy-Client-IP ip: " + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
            log.info("HTTP_CLIENT_IP ip: " + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            log.info("HTTP_X_FORWARDED_FOR ip: " + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
            log.info("X-Real-IP ip: " + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            log.info("getRemoteAddr ip: " + ip);
        }
        log.info("获取客户端ip: " + ip);
        return ip;
    }
}
