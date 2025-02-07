package com.xiao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiao.common.Result;
import com.xiao.domain.dto.RoleDTO;
import com.xiao.domain.dto.RoleMenuDTO;
import com.xiao.domain.entity.Role;
import com.xiao.service.MenuService;
import com.xiao.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "角色管理")
@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final MenuService menuService;

    @Operation(summary = "获取角色分页列表")
    @GetMapping("/page")
    public Result<Page<Role>> getRolePage(
            @RequestParam Integer current,
            @RequestParam Integer size,
            @RequestParam(required = false) String keyword) {
        return Result.success(roleService.getRolePage(current, size, keyword));
    }

    @Operation(summary = "添加角色")
    @PostMapping
    public Result<Void> addRole(@Valid @RequestBody RoleDTO roleDTO) {
        roleService.addRole(roleDTO);
        return Result.success();
    }

    @Operation(summary = "修改角色")
    @PutMapping
    public Result<Void> updateRole(@Valid @RequestBody RoleDTO roleDTO) {
        roleService.updateRole(roleDTO);
        return Result.success();
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    public Result<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return Result.success();
    }

    @Operation(summary = "获取角色菜单ID列表")
    @GetMapping("/menus/{roleId}")
    public Result<List<Long>> getRoleMenuIds(@PathVariable Long roleId) {
        return Result.success(menuService.getRoleMenuIds(roleId));
    }

    @Operation(summary = "更新角色菜单权限")
    @PutMapping("/menus")
    public Result<Void> updateRoleMenus(@RequestBody RoleMenuDTO roleMenuDTO) {
        menuService.updateRoleMenus(roleMenuDTO.getRoleId(), roleMenuDTO.getMenuIds());
        return Result.success();
    }
} 