package com.example.express.config;

import com.baidu.aip.face.AipFace;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 百度人脸识别
 * @author xiangsheng.wu
 * @date 2019年05月02日 18:11
 */
@Configuration
public class BaiduAipConfig {
    @Value("${project.baidu.aip.app-id}")
    private String appId;
    @Value("${project.baidu.aip.app-key}")
    private String appKey;
    @Value("${project.baidu.aip.secret-key}")
    private String secretKey;
    @Value("${project.baidu.aip.conn-timeout}")
    private Integer connTimeout;
    @Value("${project.baidu.aip.socket-timeout}")
    private Integer socketTimeout;

    @Bean
    public AipFace aipFace() {
        AipFace client = new AipFace(appId, appKey, secretKey);
        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(connTimeout);
        client.setSocketTimeoutInMillis(socketTimeout);
        return client;
    }
}
