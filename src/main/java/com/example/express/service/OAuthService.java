package com.example.express.service;

public interface OAuthService {

    /**
     * 生成并保存state入缓存
     * @since 2018/5/22 20:57
     */
    String genState();

    /**
     * 校验state
     * @since 2018/5/22 20:58
     */
    boolean checkState(String state);
}
