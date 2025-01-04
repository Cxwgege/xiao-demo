package com.xiao.config;

import cn.dev33.satoken.stp.StpInterface;
import com.xiao.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {
    
    private final MenuService permissionService;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 返回此 loginId 拥有的权限列表
        return permissionService.getUserMenus(Long.valueOf(loginId.toString()));
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // 返回此 loginId 拥有的角色列表
        return permissionService.getUserRoles(Long.valueOf(loginId.toString()));
    }
} 