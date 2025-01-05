package com.xiao.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiao.domain.dto.RoleDTO;
import com.xiao.domain.entity.Role;
import com.xiao.domain.entity.UserRole;
import com.xiao.mapper.RoleMapper;
import com.xiao.mapper.UserRoleMapper;
import com.xiao.service.RoleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.beans.BeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    private final UserRoleMapper userRoleMapper;

    @Override
    public Page<Role> getRolePage(Integer current, Integer size, String keyword) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        // 根据关键字查询角色名称或编码
        wrapper.like(StringUtils.isNotBlank(keyword), Role::getName, keyword)
                .or()
                .like(StringUtils.isNotBlank(keyword), Role::getCode, keyword);
                
        return page(new Page<>(current, size), wrapper);
    }

    @Override
    public void addRole(RoleDTO roleDTO) {
        // 检查角色编码是否存在
        checkRoleCode(roleDTO.getCode(), null);
        
        Role role = new Role();
        BeanUtils.copyProperties(roleDTO, role);
        save(role);
    }

    @Override
    @Transactional
    public void updateRole(RoleDTO roleDTO) {
        // 检查角色编码是否存在
        checkRoleCode(roleDTO.getCode(), roleDTO.getId());
        
        Role role = BeanUtil.copyProperties(roleDTO, Role.class);
        updateById(role);
        
        // 如果角色被禁用，删除相关的用户-角色关联
        if (role.getStatus() == 0) {
            LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserRole::getRoleId, role.getId());
            userRoleMapper.delete(wrapper);
        }
        // 如果角色被启用，可以在这里处理（通常不需要自动恢复关联）
    }

    @Override
    public void deleteRole(Long id) {
        // 检查角色是否被用户使用
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getRoleId, id);
        long count = userRoleMapper.selectCount(wrapper);
        
        if (count > 0) {
            throw new RuntimeException("该角色已被用户使用，无法删除");
        }
        
        removeById(id);
    }

    /**
     * 检查角色编码是否已存在
     */
    private void checkRoleCode(String code, Long id) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getCode, code)
                .ne(id != null, Role::getId, id);
        
        if (count(wrapper) > 0) {
            throw new RuntimeException("角色编码已存在");
        }
    }
} 