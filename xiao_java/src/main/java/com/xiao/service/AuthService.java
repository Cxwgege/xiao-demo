package com.xiao.service;

import com.xiao.domain.dto.LoginDTO;
import com.xiao.domain.vo.LoginVO;

public interface AuthService {
    /**
     * 用户登录
     *
     * @param loginDTO 登录参数
     * @return 登录响应结果
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 用户注销
     */
    void logout();
} 