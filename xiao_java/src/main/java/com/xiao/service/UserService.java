package com.xiao.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiao.domain.dto.UserDTO;
import com.xiao.domain.entity.User;
import com.xiao.domain.vo.UserVO;
import java.util.List;

public interface UserService extends IService<User> {
    
    /**
     * 分页查询用户列表
     */
    Page<UserVO> getUserPage(Integer current, Integer size, String keyword);
    
    /**
     * 添加用户
     */
    void addUser(UserDTO userDTO);
    
    /**
     * 修改用户
     */
    void updateUser(UserDTO userDTO);
    
    /**
     * 删除用户
     */
    void deleteUser(Long id);
    
    /**
     * 重置密码
     */
    void resetPassword(Long id);
    
    /**
     * 获取用户角色ID列表
     */
    List<Long> getUserRoleIds(Long userId);
    
    /**
     * 更新用户角色
     */
    void updateUserRoles(Long userId, List<Long> roleIds);
} 