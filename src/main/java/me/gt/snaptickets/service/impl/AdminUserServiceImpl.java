package me.gt.snaptickets.service.impl;

import me.gt.snaptickets.mapper.AdminUserMapper;
import me.gt.snaptickets.model.AdminUser;
import me.gt.snaptickets.service.AdminUserService;
import me.gt.snaptickets.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    @Autowired
    private AdminUserMapper adminUserMapper;

    @Override
    public RegistrationStatus registerAdminUser(AdminUser adminUser) {
        boolean isUsernameExist = adminUserMapper.getByUsername(adminUser.getUsername()) != null;
        if (isUsernameExist) {
            return RegistrationStatus.USERNAME_EXISTS;
        }
        boolean isEmailExist = adminUserMapper.getByEmail(adminUser.getEmail()) != null;
        if (isEmailExist) {
            return RegistrationStatus.EMAIL_EXISTS;
        }
        adminUser.setPassword(PasswordUtil.encryptPassword(adminUser.getPassword())); // 加密密碼
        adminUserMapper.register(adminUser);
        return RegistrationStatus.SUCCESS;
    }

    @Override
    public AdminUser loginUser(String identifier, String password) {
        AdminUser user;
        if (identifier.contains("@")) {
            user = adminUserMapper.getByEmail(identifier);
        } else {
            user = adminUserMapper.getByUsername(identifier);
        }
        if (user == null) {
            return null;
        }
        if (!PasswordUtil.verifyPassword(password, user.getPassword())) {
            return null;
        }
        return user;
    }

    @Override
    public boolean verifyTokenLogin(String username, String password) {
        AdminUser user = adminUserMapper.getByUsername(username);
        if (user == null) {
            return false;
        }
        if (password.equals(user.getPassword())) {
            return true;
        }
        return false;
    }


    @Override
    public AdminUser getByUsername(String username) {
        return adminUserMapper.getByUsername(username);
    }

    @Override
    public boolean updateUser(AdminUser adminUser) {
        return adminUserMapper.updateUser(adminUser) > 0;
    }

    @Override
    public AuthStatus updatePassword(String username, String oldPassword, String newPassword) {
        AdminUser user = adminUserMapper.getByUsername(username);
        if (user == null) {
            return AuthStatus.USER_NOT_FOUND;
        }
        if (!PasswordUtil.verifyPassword(oldPassword, user.getPassword())) { // 如果舊密碼不正確
            return AuthStatus.PASSWORD_INCORRECT;
        }
        if (oldPassword.equals(newPassword)) { // 如果新密碼與舊密碼相同
            return AuthStatus.PASSWORD_SAME;
        }
        adminUserMapper.updatePassword(username, PasswordUtil.encryptPassword(newPassword)); // 更新密碼
        return AuthStatus.SUCCESS;
    }

    @Override
    public void deleteUser(String username) {
        adminUserMapper.deleteUser(username);
    }
}
