package com.example.express.service;

import com.example.express.domain.ResponseResult;

/**
 * 人脸识别接口
 * @author xiangsheng.wu
 * @date 2019年05月02日 18:18
 */
public interface AipService {
    /**
     * 人脸检测
     * @param isQuality 是否开启质量校验
     */
    ResponseResult faceDetectByBase64(String image, boolean isQuality);
    /**
     * 人脸注册
     * 注：先调用人脸检测，开启质量校验
     */
    ResponseResult faceRegistryByFaceToken(String faceToken, String userId);
    /**
     * 人脸搜索
     */
    ResponseResult faceSearchByBase64(String image);
    /**
     * 人脸更新
     * 注：先调用人脸检测，开启质量校验
     */
    ResponseResult faceUpdateByFaceToken(String faceToken, String userId);
}
