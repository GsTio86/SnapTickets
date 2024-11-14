package me.gt.snaptickets.service.impl;

import me.gt.snaptickets.model.AdminUser;
import me.gt.snaptickets.service.AdminUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AdminUserServiceImplTest {

    @Autowired
    private AdminUserService adminUserService;

    @Test
    void registerAdminUser() {
        AdminUser adminUser = AdminUser.builder()
                .name("管理員帳號")
                .username("admin")
                .password("admin")
                .email("admin1234@gmail.com")
                .permission(AdminUser.Permission.ADMIN)
                .build();
        AdminUserService.RegistrationStatus result = adminUserService.registerAdminUser(adminUser);
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

    }

    @Test
    void getByUsername() {
    }

    @Test
    void updateUser() {
    }

    @Test
    void updatePassword() {
    }

    @Test
    void deleteUser() {
    }
}
