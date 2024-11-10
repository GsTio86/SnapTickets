package me.gt.snaptickets.service.impl;

import me.gt.snaptickets.model.User;
import me.gt.snaptickets.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceImplTest {

    @Autowired
    private UserService service;

    @Test
    void registerUser() {
        User user = User.builder()
                .username("test")
                .password("password")
                .build();
        UserService.RegistrationStatus result = service.registerUser(user);
        switch (result) {
            case USERNAME_EXISTS:
                System.out.println("此帳號已存在");
                break;
            case EMAIL_EXISTS:
                System.out.println("此信箱已被註冊");
                break;
            default:
                System.out.println("註冊成功");
        }
    }
    

    @Test
    void loginUser() {
        String username = "test";
        String password = "daawdawd";
        User result = service.loginUser(username, password);
        if (result != null) {
            System.out.println("登入成功");
        } else {
            System.out.println("帳號或密碼錯誤");
        }
    }
    
    @Test
    void getByUsername() {
        String username = "existingUser";
        User result = service.getByUsername(username);
        if (result != null) {
            System.out.println("會員存在");
        } else {
            System.out.println("會員不存在");
        }
    }

    @Test
    void updateUser() {
        User user = new User();
        user.setUsername("test");
        user.setName("王小明");
        user.setEmail("test123@gmail.com");
        user.setPhone("0912345678");
        user.setAddress("台北市中正區忠孝東路一段1號");
        service.updateUser(user);
    }

    @Test
    void updatePassword_SuccessfulUpdate() {
        String username = "test";
        String password = "newPassword";
        String oldPassword = "testpassword";
        service.updatePassword(username, password, oldPassword);
    }

    @Test
    void deleteUser_SuccessfulDeletion() {
        String username = "test";
        service.deleteUser(username);
    }
}