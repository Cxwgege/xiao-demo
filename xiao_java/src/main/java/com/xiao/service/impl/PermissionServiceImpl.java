package com.xiao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiao.domain.entity.Menu;
import com.xiao.domain.entity.Role;
import com.xiao.domain.entity.UserRole;
import com.xiao.mapper.MenuMapper;
import com.xiao.mapper.RoleMapper;
import com.xiao.mapper.UserRoleMapper;
import com.xiao.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final MenuMapper menuMapper;

    @Override
    public List<String> getUserRoles(Long userId) {
        // 查询用户角色关联
        List<UserRole> userRoles = userRoleMapper.selectList(
            new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, userId)
        );
        
        if (userRoles.isEmpty()) {
            return List.of();
        }
        
        // 获取角色列表
        List<Role> roles = roleMapper.selectList(
            new LambdaQueryWrapper<Role>()
                .in(Role::getId, userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toList()))
                .eq(Role::getStatus, 1)
                .eq(Role::getIsDeleted, 0)
        );
        
        return roles.stream()
            .map(Role::getRoleCode)
            .collect(Collectors.toList());
    }

    @Override
    public List<String> getUserPermissions(Long userId) {
        // 查询用户角色关联
        List<UserRole> userRoles = userRoleMapper.selectList(
            new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, userId)
        );
        
        if (userRoles.isEmpty()) {
            return List.of();
        }
        
        // 查询角色对应的菜单权限
        return menuMapper.selectPermissionsByRoleIds(
            userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList())
        );
    }
} 