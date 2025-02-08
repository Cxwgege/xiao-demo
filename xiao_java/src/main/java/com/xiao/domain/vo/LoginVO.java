package com.xiao.domain.vo;

import lombok.Data;
import java.util.List;

@Data
public class LoginVO {
    private String token;
    private UserInfo userInfo;
    
    @Data
    public static class UserInfo {
        private Long userId;
        private String username;
        private String nickname;
        private String avatar;
        private List<String> roles;
        private List<String> permissions;
    }
} 