package com.example.express.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author jitwxs
 * @date 2019年04月23日 23:37
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFeedbackDescVO implements Serializable {
    private String id;

    private String frontName;
    /**
     * 反馈类型
     */
    private String feedbackType;
    /**
     * 反馈内容
     */
    private String content;
    /**
     * 反馈状态
     */
    private String feedbackStatus;
    /**
     * 处理人
     */
    private String handler;
    /**
     * 处理结果
     */
    private String result;

    private OrderDescVO order;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateDate;
}
