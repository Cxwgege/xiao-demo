package com.xiao.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Data
public class UserDTO {
    
    private Long id;

    @NotBlank(message = "用户名不能为空")
    private String username;
    
    private String password;

    @NotBlank(message = "昵称不能为空")
    private String nickname;
    
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    private String avatar;
    
    private Integer status;

    // 用户角色ID列表
    private List<Long> roleIds;
} 