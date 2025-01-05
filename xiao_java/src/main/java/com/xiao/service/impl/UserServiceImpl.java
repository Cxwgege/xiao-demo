package com.xiao.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiao.domain.dto.UserDTO;
import com.xiao.domain.entity.User;
import com.xiao.domain.entity.UserRole;
import com.xiao.domain.entity.Role;
import com.xiao.domain.vo.UserVO;
import com.xiao.mapper.UserMapper;
import com.xiao.mapper.UserRoleMapper;
import com.xiao.mapper.RoleMapper;
import com.xiao.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;
    private static final String DEFAULT_PASSWORD = "123456";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUser(UserDTO userDTO) {
        // 检查用户名是否已存在
        checkUsername(userDTO.getUsername(), null);
        
        User user = new User();
        BeanUtil.copyProperties(userDTO, user);
        // 设置默认密码并加密
        user.setPassword(BCrypt.hashpw(DEFAULT_PASSWORD));
        
        save(user);
        
        // 保存用户角色关联
        if (userDTO.getRoleIds() != null && !userDTO.getRoleIds().isEmpty()) {
            updateUserRoles(user.getId(), userDTO.getRoleIds());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(UserDTO userDTO) {
        // 检查用户名是否已存在
        checkUsername(userDTO.getUsername(), userDTO.getId());
        
        User user = getById(userDTO.getId());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 使用BeanUtil进行属性拷贝，忽略password字段
        BeanUtil.copyProperties(userDTO, user, "password");
        updateById(user);
        
        // 更新用户角色关联
        if (userDTO.getRoleIds() != null) {
            updateUserRoles(user.getId(), userDTO.getRoleIds());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        // 删除用户角色关联
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, id);
        userRoleMapper.delete(wrapper);
        
        // 删除用户
        removeById(id);
    }

    @Override
    public List<Long> getUserRoleIds(Long userId) {
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId);
        return userRoleMapper.selectList(wrapper)
                .stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserRoles(Long userId, List<Long> roleIds) {
        // 删除原有角色关联
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId);
        userRoleMapper.delete(wrapper);
        
        // 添加新的角色关联
        if (roleIds != null && !roleIds.isEmpty()) {
            List<UserRole> userRoles = roleIds.stream()
                    .map(roleId -> {
                        UserRole userRole = new UserRole();
                        userRole.setUserId(userId);
                        userRole.setRoleId(roleId);
                        return userRole;
                    })
                    .toList();
            
            userRoles.forEach(userRoleMapper::insert);
        }
    }

    @Override
    public Page<UserVO> getUserPage(Integer current, Integer size, String keyword) {
        // 查询用户分页数据
        Page<User> userPage = new Page<>(current, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(keyword), User::getUsername, keyword)
                .or()
                .like(StringUtils.isNotBlank(keyword), User::getNickname, keyword)
                .or()
                .like(StringUtils.isNotBlank(keyword), User::getPhone, keyword)
                .orderByDesc(User::getCreateTime);
        
        page(userPage, wrapper);
        
        // 转换为VO对象
        Page<UserVO> voPage = new Page<>(current, size, userPage.getTotal());
        List<UserVO> voList = userPage.getRecords().stream()
                .map(user -> {
                    UserVO vo = new UserVO();
                    BeanUtil.copyProperties(user, vo);
                    
                    // 查询用户角色
                    List<Role> roles = getUserRoles(user.getId());
                    vo.setRoles(roles);
                    
                    return vo;
                })
                .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public void resetPassword(Long id) {
        User user = getById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 使用BCrypt加密重置密码
        user.setPassword(BCrypt.hashpw(DEFAULT_PASSWORD));
        updateById(user);
    }
    
    /**
     * 检查用户名是否已存在
     */
    private void checkUsername(String username, Long id) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username)
                .ne(id != null, User::getId, id);
        
        if (count(wrapper) > 0) {
            throw new RuntimeException("用户名已存在");
        }
    }
    
    /**
     * 获取用户角色列表
     */
    private List<Role> getUserRoles(Long userId) {
        // 查询用户角色关联
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId);
        List<UserRole> userRoles = userRoleMapper.selectList(wrapper);
        
        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 查询角色信息，只返回状态正常的角色
        List<Long> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());
                
        LambdaQueryWrapper<Role> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.in(Role::getId, roleIds)
                  .eq(Role::getStatus, 1); // 只查询状态正常的角色
                
        return roleMapper.selectList(roleWrapper);
    }
} 