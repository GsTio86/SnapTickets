package me.gt.snaptickets.service.impl;

import me.gt.snaptickets.mapper.UserMapper;
import me.gt.snaptickets.model.User;
import me.gt.snaptickets.service.UserService;
import me.gt.snaptickets.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public RegistrationStatus registerUser(User user) {
        boolean isUsernameExist = userMapper.getByUsername(user.getUsername()) != null;
        if (isUsernameExist) {
            return RegistrationStatus.USERNAME_EXISTS;
        }
        boolean isEmailExist = userMapper.getByEmail(user.getEmail()) != null;
        if (isEmailExist) {
            return RegistrationStatus.EMAIL_EXISTS;
        }
        user.setPassword(PasswordUtil.encryptPassword(user.getPassword())); // 加密密碼
        userMapper.register(user);
        return RegistrationStatus.SUCCESS;
    }

    @Override
    public User loginUser(String identifier, String password) {
        User user;
        if (identifier.contains("@")) {
            user = userMapper.getByEmail(identifier);
        } else {
            user = userMapper.getByUsername(identifier);
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
    public User getByUsername(String username) {
        return userMapper.getByUsername(username);
    }

    @Override
    public boolean updateUser(User user) {
        return userMapper.updateUser(user) > 0;
    }

    @Override
    public AuthStatus updatePassword(String username, String oldPassword, String newPassword) {
        User user = userMapper.getByUsername(username);
        if (user == null) {
            return AuthStatus.USER_NOT_FOUND;
        }
        if (!PasswordUtil.verifyPassword(oldPassword, user.getPassword())) { // 如果舊密碼不正確
            return AuthStatus.PASSWORD_INCORRECT;
        }
        if (oldPassword.equals(newPassword)) { // 如果新密碼與舊密碼相同
            return AuthStatus.PASSWORD_SAME;
        }
        userMapper.updatePassword(username, PasswordUtil.encryptPassword(newPassword)); // 更新密碼
        return AuthStatus.SUCCESS;
    }

    @Override
    public void deleteUser(String username) {
        userMapper.deleteUser(username);
    }
}
