package com.example.express.domain.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author xiangsheng.wu
 * @date 2019年04月23日 16:10
 */
@Data
@Builder
public class UserFeedbackVO implements Serializable {
    private String id;
    /**
     * 用户名
     */
    private String frontName;
    /**
     * 反馈类型
     */
    private Integer type;
    /**
     * 反馈内容
     */
    private String content;
    /**
     * 订单号
     */
    private String orderId;
    /**
     * 反馈状态
     */
    private Integer status;
    /**
     * 处理人
     */
    private String handlerName;
    /**
     * 处理结果
     */
    private String result;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createDate;

    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateDate;
}
