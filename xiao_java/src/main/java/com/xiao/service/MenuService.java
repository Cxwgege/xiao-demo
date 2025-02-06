package com.xiao.service;

import com.xiao.domain.dto.MenuDTO;
import com.xiao.domain.entity.Menu;
import java.util.List;

public interface MenuService {
    /**
     * 获取用户角色列表
     */
    List<String> getUserRoles(Long userId);
    
    /**
     * 获取用户权限列表
     */
    List<String> getUserMenus(Long userId);

    /**
     * 获取菜单树
     */
    List<Menu> getMenuTree();

    /**
     * 添加菜单
     */
    void addMenu(MenuDTO menuDTO);

    /**
     * 修改菜单
     */
    void updateMenu(MenuDTO menuDTO);

    /**
     * 删除菜单
     */
    void deleteMenu(Long id);
} 