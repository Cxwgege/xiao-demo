package com.xiao;

import cn.dev33.satoken.secure.BCrypt;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class XiaoJavaApplicationTests {

    @Test
    void contextLoads() {
        String password = "123456";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        System.out.println("Generated hash: " + hashedPassword);
        System.out.println("Verification: " + BCrypt.checkpw(password, hashedPassword));
    }

}
