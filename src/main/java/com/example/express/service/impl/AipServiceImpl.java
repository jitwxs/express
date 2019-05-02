package com.example.express.service.impl;

import com.baidu.aip.face.AipFace;
import com.example.express.domain.ResponseResult;
import com.example.express.domain.bean.SysUser;
import com.example.express.domain.enums.ResponseErrorCodeEnum;
import com.example.express.service.AipService;
import com.example.express.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xiangsheng.wu
 * @date 2019年05月02日 21:28
 */
@Slf4j
@Service
public class AipServiceImpl implements AipService {
    @Autowired
    private AipFace client;
    @Autowired
    private SysUserService sysUserService;

    private static String GROUP_ID = "1";
    private static String BASE_64 = "BASE64";
    /**
     * 最小被接收的人脸分数
     */
    private static double MIN_ACCEPT_SCORE = 90;

    @Override
    public ResponseResult faceDetectByBase64(String image) {
        HashMap<String, String> options = new HashMap<>();
        options.put("face_field", "gender,face_type");
        options.put("max_face_num", "1");
        options.put("face_type", "LIVE");

        // 人脸检测
        JSONObject res = client.detect(image, BASE_64, options);
        log.info("res: {}", res);
        // 校验错误
        Integer errorCode = (Integer)res.get("error_code");
        if(errorCode != 0) {
            log.info("错误码：{}，错误信息：{}", errorCode, res.get("error_msg"));
            return ResponseResult.failure(ResponseErrorCodeEnum.OPERATION_ERROR.getCode(), (String)res.get("error_msg"));
        }

        // 取第一条记录
        JSONArray faceList = (JSONArray) ((JSONObject)res.get("result")).get("face_list");
        if(faceList.length() == 0) {
            return ResponseResult.failure(ResponseErrorCodeEnum.NOT_MATCH_FACE);
        }
        JSONObject faceMap = (JSONObject)faceList.get(0);

        // 人脸置信度，0~1
        Integer faceProbability = (Integer) faceMap.get("face_probability");
        if(faceProbability != 1) {
            return ResponseResult.failure(ResponseErrorCodeEnum.NOT_DETECT_FACE);
        }

        // 判断真人、卡通
        String faceTpe = (String)((JSONObject) faceMap.get("face_type")).get("type");
        if(!"human".equals(faceTpe)) {
            return ResponseResult.failure(ResponseErrorCodeEnum.NOT_REAL_FACE);
        }

        Map<String, String> resultMap = new HashMap<>();
        // faceToken
        resultMap.put("face_token", (String)faceMap.get("face_token"));
        // 性别
        resultMap.put("gender", (String)((JSONObject) faceMap.get("gender")).get("type"));

        return ResponseResult.success(resultMap);
    }

    @Override
    public ResponseResult faceSearchByBase64(String image) {
        HashMap<String, String> options = new HashMap<>();
        // 图片质量控制 NONE: 不进行控制 LOW:较低的质量要求 NORMAL: 一般的质量要求 HIGH: 较高的质量要求
        options.put("quality_control", "NORMAL");
        // 活体检测控制 NONE: 不进行控制 LOW:较低的活体要求(高通过率 低攻击拒绝率)
        // NORMAL: 一般的活体要求(平衡的攻击拒绝率, 通过率) HIGH: 较高的活体要求(高攻击拒绝率 低通过率)
        options.put("liveness_control", "NORMAL");
        // 查找后返回的用户数量。返回相似度最高的几个用户，默认为1，最多返回20个。
        options.put("max_user_num", "1");

        // 人脸搜索
        JSONObject res = client.search(image, BASE_64, GROUP_ID, options);
        // 校验错误
        Integer errorCode = (Integer)res.get("error_code");
        if(errorCode != 0) {
            log.info("错误码：{}，错误信息：{}", errorCode, res.get("error_msg"));
            return ResponseResult.failure(ResponseErrorCodeEnum.OPERATION_ERROR.getCode(), (String)res.get("error_msg"));
        }
        /*
         {
          "face_token": "fid",
          "user_list": [
             {
                "group_id" : "test1",
                "user_id": "u333333",
                "user_info": "Test User",
                "score": 99.3
            }
          ]
        }
         */
        // 取第一条记录
        JSONArray userList = (JSONArray) ((JSONObject)res.get("result")).get("user_list");
        if(userList.length() == 0) {
            return ResponseResult.failure(ResponseErrorCodeEnum.NOT_MATCH_FACE);
        }
        JSONObject userMap = (JSONObject)userList.get(0);

        Double score = (Double) userMap.get("score");
        if(score < MIN_ACCEPT_SCORE) {
            return ResponseResult.failure(ResponseErrorCodeEnum.NOT_ACCORD_WITH_MIN_REQUIREMENT);
        }

        String userId = (String) userMap.get("user_id");
        SysUser sysUser = sysUserService.getById(userId);
        return ResponseResult.success(sysUser);
    }

    @Override
    public ResponseResult addFaceByBase64(String image, String userId) {
        HashMap<String, String> options = new HashMap<>();
        options.put("quality_control", "NORMAL");
        options.put("liveness_control", "NORMAL");

        SysUser sysUser = sysUserService.getById(userId);
        // 人脸注册
        JSONObject res = client.addUser(image, BASE_64, GROUP_ID, userId, options);
        // 校验错误
        Integer errorCode = (Integer)res.get("error_code");
        if(errorCode != 0) {
            log.info("错误码：{}，错误信息：{}", errorCode, res.get("error_msg"));
            return ResponseResult.failure(ResponseErrorCodeEnum.OPERATION_ERROR.getCode(), (String)res.get("error_msg"));
        }
        /*
         {
          "face_token": "2fa64a88a9d5118916f9a303782a97d3",
          "location": {
              "left": 117,
              "top": 131,
              "width": 172,
              "height": 170,
              "rotation": 4
          }
        }
         */
        sysUser.setFaceToken((String) res.get("face_token"));
        boolean isUpdate = sysUserService.updateById(sysUser);

        return isUpdate ? ResponseResult.success() : ResponseResult.failure(ResponseErrorCodeEnum.FACE_ADD_ERROR);
    }
}
