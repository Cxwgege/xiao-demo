package com.xiao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiao.common.Result;
import com.xiao.domain.dto.UserDTO;
import com.xiao.domain.entity.User;
import com.xiao.domain.vo.UserVO;
import com.xiao.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "获取用户分页列表")
    @GetMapping("/page")
    public Result<Page<UserVO>> getUserPage(
            @RequestParam Integer current,
            @RequestParam Integer size,
            @RequestParam(required = false) String keyword) {
        return Result.success(userService.getUserPage(current, size, keyword));
    }

    @Operation(summary = "添加用户")
    @PostMapping
    public Result<Void> addUser(@Valid @RequestBody UserDTO userDTO) {
        userService.addUser(userDTO);
        return Result.success();
    }

    @Operation(summary = "修改用户")
    @PutMapping
    public Result<Void> updateUser(@Valid @RequestBody UserDTO userDTO) {
        userService.updateUser(userDTO);
        return Result.success();
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    @Operation(summary = "重置密码")
    @PutMapping("/{id}/password/reset")
    public Result<Void> resetPassword(@PathVariable Long id) {
        userService.resetPassword(id);
        return Result.success();
    }
} 