package com.example.express.security.authentication;

import com.example.express.domain.enums.ResponseErrorCodeEnum;
import com.example.express.security.SecurityConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * 默认 Session 过期处理
 * @author jitwxs
 * @since 2019/1/8 23:40
 */
@Slf4j
public class DefaultExpiredSessionStrategy implements SessionInformationExpiredStrategy {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {
        log.error("当前session已过期：{}", event.getSessionInformation().getLastRequest());

        //手动设置异常
        event.getRequest().getSession().setAttribute("SPRING_SECURITY_LAST_EXCEPTION", new DisabledException(ResponseErrorCodeEnum.SESSION_EXPIRE.getMsg()));
        event.getResponse().sendRedirect(SecurityConstants.UN_AUTHENTICATION_URL);
    }
}
