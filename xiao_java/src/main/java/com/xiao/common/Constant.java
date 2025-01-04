package com.xiao.common;

public class Constant {
    String USER_LOGIN_TOKEN = "user_login_token:";


    //  请求白名单，请求会放行
    public static final String[] WHITE_LIST = {
            "/auth/register",
            "/auth/login",
            "/auth/logout",
            "/swagger-ui/**",
            "/v3/**",
            "/files/{fileName}",
    };

}
