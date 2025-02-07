package com.xiao.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "角色菜单权限请求参数")
public class RoleMenuDTO {
    
    @Schema(description = "角色ID")
    private Long roleId;
    
    @Schema(description = "菜单ID列表")
    private List<Long> menuIds;
} 