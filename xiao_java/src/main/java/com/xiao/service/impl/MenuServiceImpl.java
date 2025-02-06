package com.xiao.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiao.common.BusinessException;
import com.xiao.domain.dto.MenuDTO;
import com.xiao.domain.entity.Menu;
import com.xiao.domain.entity.Role;
import com.xiao.domain.entity.UserRole;
import com.xiao.mapper.MenuMapper;
import com.xiao.mapper.RoleMapper;
import com.xiao.mapper.UserRoleMapper;
import com.xiao.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

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
            .map(Role::getCode)
            .collect(Collectors.toList());
    }

    @Override
    public List<String> getUserMenus(Long userId) {
        // 查询用户角色关联
        List<UserRole> userRoles = userRoleMapper.selectList(
            new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, userId)
        );
        
        if (userRoles.isEmpty()) {
            return List.of();
        }
        
        // 查询角色对应的菜单权限
        return menuMapper.selectMenusByRoleIds(
            userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList())
        );
    }

    @Override
    public List<Menu> getMenuTree() {
        // 获取所有菜单列表
        List<Menu> menuList = menuMapper.selectList(new LambdaQueryWrapper<Menu>()
                .eq(Menu::getIsDeleted, 0)
                .orderByAsc(Menu::getSort));

        // 构建树形结构
        return buildMenuTree(menuList, 0L);
    }

    @Override
    @Transactional
    public void addMenu(MenuDTO menuDTO) {
        // 检查同级菜单名称是否重复
        checkMenuName(menuDTO.getName(), menuDTO.getParentId(), null);
        
        Menu menu = new Menu();
        BeanUtil.copyProperties(menuDTO, menu);
        menuMapper.insert(menu);
    }

    @Override
    @Transactional
    public void updateMenu(MenuDTO menuDTO) {
        // 检查同级菜单名称是否重复
        checkMenuName(menuDTO.getName(), menuDTO.getParentId(), menuDTO.getId());
        
        // 不能将菜单的父ID设置为自己或自己的子菜单
        checkParentId(menuDTO.getId(), menuDTO.getParentId());
        
        Menu menu = new Menu();
        BeanUtil.copyProperties(menuDTO, menu);
        menuMapper.updateById(menu);
    }

    @Override
    @Transactional
    public void deleteMenu(Long id) {
        // 检查是否有子菜单
        Long count = menuMapper.selectCount(new LambdaQueryWrapper<Menu>()
                .eq(Menu::getParentId, id)
                .eq(Menu::getIsDeleted, 0));
                
        if (count > 0) {
            throw new BusinessException("该菜单下有子菜单，无法删除");
        }
        
        menuMapper.deleteById(id);
    }

    /**
     * 构建菜单树
     */
    private List<Menu> buildMenuTree(List<Menu> menuList, Long parentId) {
        return menuList.stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .peek(menu -> menu.setChildren(buildMenuTree(menuList, menu.getId())))
                .collect(Collectors.toList());
    }

    /**
     * 检查同级菜单名称是否重复
     */
    private void checkMenuName(String name, Long parentId, Long id) {
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<Menu>()
                .eq(Menu::getName, name)
                .eq(Menu::getParentId, parentId)
                .eq(Menu::getIsDeleted, 0);
                
        if (id != null) {
            wrapper.ne(Menu::getId, id);
        }
        
        Long count = menuMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("同级菜单下已存在相同名称的菜单");
        }
    }

    /**
     * 检查父级ID是否合法
     */
    private void checkParentId(Long id, Long parentId) {
        if (id.equals(parentId)) {
            throw new BusinessException("父级菜单不能选择自己");
        }
        
        // 获取所有子菜单ID
        List<Long> childIds = getChildIds(id);
        if (childIds.contains(parentId)) {
            throw new BusinessException("父级菜单不能选择自己的子菜单");
        }
    }

    /**
     * 获取所有子菜单ID
     */
    private List<Long> getChildIds(Long id) {
        List<Long> ids = new ArrayList<>();
        List<Menu> children = menuMapper.selectList(new LambdaQueryWrapper<Menu>()
                .eq(Menu::getParentId, id)
                .eq(Menu::getIsDeleted, 0));
                
        children.forEach(child -> {
            ids.add(child.getId());
            ids.addAll(getChildIds(child.getId()));
        });
        
        return ids;
    }
} 