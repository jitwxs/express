package com.example.express.controller.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.express.domain.ResponseResult;
import com.example.express.domain.bean.SysUser;
import com.example.express.domain.bean.UserFeedback;
import com.example.express.domain.vo.UserFeedbackVO;
import com.example.express.service.UserFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * API 反馈接口
 * @author xiangsheng.wu
 * @date 2019年04月23日 15:14
 */
@RestController
@RequestMapping("/api/v1/feedback")
public class FeedbackApiController {
    @Autowired
    private UserFeedbackService userFeedbackService;

    /**
     * 获取单条记录
     */
    @GetMapping("/{id}")
    public ResponseResult getFeedbackById(@PathVariable Integer id) {
        UserFeedback feedback = userFeedbackService.getById(id);

        return ResponseResult.success(feedback);
    }

    /**
     * 获取当前用户所有反馈记录
     */
    @GetMapping("/list")
    public ResponseResult listFeedback(@RequestParam(required = false, defaultValue = "1") Integer current,
                                       @RequestParam(required = false, defaultValue = "10") Integer size,
                                       Integer type, Integer status, @AuthenticationPrincipal SysUser sysUser) {

        Page page = new Page(current, size);

    }
}
