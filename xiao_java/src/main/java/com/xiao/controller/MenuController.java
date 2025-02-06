package com.xiao.controller;

import com.xiao.common.Result;
import com.xiao.domain.dto.MenuDTO;
import com.xiao.domain.entity.Menu;
import com.xiao.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "菜单管理")
@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @Operation(summary = "获取菜单树")
    @GetMapping("/tree")
    public Result<List<Menu>> getMenuTree() {
        return Result.success(menuService.getMenuTree());
    }

    @Operation(summary = "添加菜单")
    @PostMapping
    public Result<Void> addMenu(@Valid @RequestBody MenuDTO menuDTO) {
        menuService.addMenu(menuDTO);
        return Result.success();
    }

    @Operation(summary = "修改菜单")
    @PutMapping
    public Result<Void> updateMenu(@Valid @RequestBody MenuDTO menuDTO) {
        menuService.updateMenu(menuDTO);
        return Result.success();
    }

    @Operation(summary = "删除菜单")
    @DeleteMapping("/{id}")
    public Result<Void> deleteMenu(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return Result.success();
    }
} 