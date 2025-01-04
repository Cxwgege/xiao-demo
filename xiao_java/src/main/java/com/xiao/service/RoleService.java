package com.xiao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiao.domain.dto.RoleDTO;
import com.xiao.domain.entity.Role;

public interface RoleService extends IService<Role> {
    
    /**
     * 分页查询角色列表
     */
    Page<Role> getRolePage(Integer current, Integer size, String keyword);
    
    /**
     * 添加角色
     */
    void addRole(RoleDTO roleDTO);
    
    /**
     * 修改角色
     */
    void updateRole(RoleDTO roleDTO);
    
    /**
     * 删除角色
     */
    void deleteRole(Long id);
} 