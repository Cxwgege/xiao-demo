package com.xiao.service;

import java.util.List;

public interface PermissionService {
    /**
     * 获取用户角色列表
     */
    List<String> getUserRoles(Long userId);
    
    /**
     * 获取用户权限列表
     */
    List<String> getUserPermissions(Long userId);
} 