package com.xiao.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MenuDTO {
    private Long id;
    
    private Long parentId;
    
    @NotBlank(message = "菜单名称不能为空")
    private String name;
    
    private String path;
    
    private String component;
    
    private String redirect;
    
    private String icon;
    
    @NotBlank(message = "菜单标题不能为空")
    private String title;
    
    private Integer isLink;
    
    private Integer isHide;
    
    private Integer isKeepAlive;
    
    private Integer isAffix;
    
    @NotNull(message = "菜单类型不能为空")
    private Integer type;
    
    private String permission;
    
    private Integer sort;
    
    private Integer status;
} 