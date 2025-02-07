package com.xiao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiao.domain.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface MenuMapper extends BaseMapper<Menu> {
    /**
     * 根据角色ID列表查询权限列表
     */
    List<String> selectMenusByRoleIds(@Param("roleIds") List<Long> roleIds);

    /**
     * 根据用户ID查询菜单列表
     */
    List<Menu> selectMenusByUserId(@Param("userId") Long userId);

    /**
     * 获取角色的菜单ID列表
     */
    List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 删除角色的所有菜单关联
     */
    void deleteRoleMenus(@Param("roleId") Long roleId);

    /**
     * 批量插入角色菜单关联
     */
    void insertRoleMenus(@Param("roleId") Long roleId, @Param("menuIds") List<Long> menuIds);
} 