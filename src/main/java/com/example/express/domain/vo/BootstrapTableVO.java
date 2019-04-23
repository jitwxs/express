package com.example.express.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author xiangsheng.wu
 * @date 2019年04月23日 18:11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BootstrapTableVO implements Serializable {
    /**
     * 总记录数
     */
    private Long total;
    /**
     * 记录
     */
    private Object rows;
}
