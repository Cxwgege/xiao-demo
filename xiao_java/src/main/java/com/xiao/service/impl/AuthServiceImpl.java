package com.xiao.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiao.common.BusinessException;
import com.xiao.domain.dto.LoginDTO;
import com.xiao.domain.entity.Menu;
import com.xiao.domain.entity.User;
import com.xiao.domain.vo.LoginVO;
import com.xiao.mapper.UserMapper;
import com.xiao.mapper.MenuMapper;
import com.xiao.service.AuthService;
import com.xiao.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final UserMapper userMapper;
    private final MenuService permissionService;
    private final MenuMapper menuMapper;
    
    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 查询用户
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, loginDTO.getUsername())
                .eq(User::getIsDeleted, 0));
                
        if (user == null) {
            throw new BusinessException("用户名错误");
        }
        
        // 校验密码
        if (!BCrypt.checkpw(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException("密码错误");
        }
        
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }
        
        // 更新登录时间
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(updateUser);
        
        // 执行登录
        StpUtil.login(user.getId());
        
        // 获取权限信息
        List<String> roles = StpUtil.getRoleList();
        List<String> perms = StpUtil.getPermissionList();
        
        // 获取用户菜单
        List<Menu> menus = menuMapper.selectMenusByUserId(user.getId());
        
        // 构建登录响应
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(StpUtil.getTokenValue());
        
        LoginVO.UserInfo userInfo = new LoginVO.UserInfo();
        BeanUtil.copyProperties(user, userInfo);
        userInfo.setUserId(user.getId());
        userInfo.setRoles(roles);
        userInfo.setPerms(perms);
        userInfo.setMenus(menus);
        
        loginVO.setUserInfo(userInfo);
        return loginVO;
    }
    
    @Override
    public void logout() {
        StpUtil.logout();
    }
} 