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
     */
    ResponseResult faceDetectByBase64(String image);
    /**
     * 人脸搜索
     */
    ResponseResult faceSearchByBase64(String image);
    /**
     * 人脸注册
     */
    ResponseResult addFaceByBase64(String image, String userId);
    /**
     * 人脸更新
     */

}
