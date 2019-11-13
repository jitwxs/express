package com.example.express.domain.bean;

import com.baomidou.mybatisplus.annotation.*;
import com.example.express.domain.enums.FeedbackStatusEnum;
import com.example.express.domain.enums.FeedbackTypeEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户反馈
 * @date 2019年04月16日 23:10
 */
@Data
@Builder
public class UserFeedback {
        @TableId(type = IdType.INPUT)
    private String id;

    private String userId;
    /**
     * 反馈类型
     */
    @TableField("type")
    private FeedbackTypeEnum feedbackType;
    /**
     * 订单ID
     */
    private String orderId;
    /**
     * 反馈内容
     */
    private String content;
    /**
     * 反馈状态
     */
    @TableField("status")
    private FeedbackStatusEnum feedbackStatus;
    /**
     * 处理人
     */
    private String handler;
    /**
     * 处理结果
     */
    private String result;

    @Version
    private Integer version;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createDate;

    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateDate;
}
