package com.example.express.domain.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 用户注册VO
 * @date 2019年04月17日 0:03
 */
@Data
public class RegistryVO {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String tel;
    @NotBlank
    private String type;
}
