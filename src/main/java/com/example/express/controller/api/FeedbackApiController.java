package com.example.express.controller.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.express.common.util.StringUtils;
import com.example.express.domain.ResponseResult;
import com.example.express.domain.bean.SysUser;
import com.example.express.domain.bean.UserFeedback;
import com.example.express.domain.enums.FeedbackTypeEnum;
import com.example.express.domain.enums.ResponseErrorCodeEnum;
import com.example.express.domain.vo.BootstrapTableVO;
import com.example.express.domain.vo.UserFeedbackDescVO;
import com.example.express.service.UserFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * API 反馈接口
 * @author xiangsheng.wu
 * @date 2019年04月23日 15:14
 */
@RestController
@RequestMapping("/api/v1/feedback")
public class FeedbackApiController extends BaseApiController {
    @Autowired
    private UserFeedbackService userFeedbackService;

    /**
     * 获取单条记录详情
     */
    @GetMapping("/{id}")
    public ResponseResult getFeedbackById(@PathVariable Integer id) {
        UserFeedbackDescVO feedback = userFeedbackService.getDescVO(id);

        return ResponseResult.success(feedback);
    }

    /**
     * 分页查询当前用户所有反馈记录
     * - 管理员：查询所有
     * - 配送员：查询所有订单反馈
     * - 普通用户：查询个人
     */
    @GetMapping("/list")
    public BootstrapTableVO listFeedback(@RequestParam(required = false, defaultValue = "1") Integer current,
                                       @RequestParam(required = false, defaultValue = "10") Integer size,
                                       Integer type, Integer status, @AuthenticationPrincipal SysUser sysUser) {
        Page<UserFeedback> page = new Page<>(current, size);

        switch (sysUser.getRole()) {
            case ADMIN:
                return userFeedbackService.pageUserFeedback(page, type, status,null);
            case COURIER:
                return userFeedbackService.pageUserFeedback(page, FeedbackTypeEnum.ORDER.getType(), status,null);
            case USER:
                return userFeedbackService.pageUserFeedback(page, type, status, sysUser.getId());
            default:
                return new BootstrapTableVO();
        }
    }

    /**
     * 创建反馈记录
     * - 仅支持 配送员 & 普通用户
     * @author jitwxs
     * @date 2019/4/23 23:13
     */
    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_COURIER')")
    public ResponseResult createFeedback(String type, String orderId, String content, @AuthenticationPrincipal SysUser sysUser) {
        // 校验参数
        Integer feedbackType = StringUtils.toInteger(type, -1);
        FeedbackTypeEnum feedbackTypeEnum = FeedbackTypeEnum.getByType(feedbackType);
        if(feedbackTypeEnum == null) {
            return ResponseResult.failure(ResponseErrorCodeEnum.FEEDBACK_TYPE_ERROR);
        }

        if(StringUtils.isBlank(content)) {
            return ResponseResult.failure(ResponseErrorCodeEnum.FEEDBACK_NOT_EMPTY);
        }
        if(content.length() > CONTENT_MAX_LENGTH) {
            return ResponseResult.failure(ResponseErrorCodeEnum.FEEDBACk_LENGTH_OVER_255);
        }

        boolean isSuccess = userFeedbackService.createFeedback(sysUser.getId(), feedbackTypeEnum, content, orderId);

        return isSuccess ? ResponseResult.success() : ResponseResult.failure(ResponseErrorCodeEnum.OPERATION_ERROR);
    }
}
