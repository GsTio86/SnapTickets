package me.gt.snaptickets.service;

import me.gt.snaptickets.model.User;

import java.util.List;

public interface UserService {

    /**
     * 註冊會員
     *
     * @param user 會員資訊
     * @return 註冊狀態
     */
    RegistrationStatus registerUser(User user);

    /**
     * 會員登入
     *
     * @param identifier 會員 (帳號或信箱)
     * @param password 會員密碼
     * @return 會員資訊
     */
    User loginUser(String identifier, String password);

    /**
     * 會員Token登入
     *
     * @param username 會員名稱
     * @param password 會員密碼
     * @return 驗證狀態 true: 通過 false: 失敗
     */
    boolean verifyTokenLogin(String username, String password);

    /**
     * 透過會員名稱查詢會員
     *
     * @param username 會員名稱
     * @return 會員資訊
     */
    User getByUsername(String username);

    /**
     * 取得所有會員
     *
     * @return 會員列表
     */
    List<User> getAllUsers();

    /**
     * 更新會員資訊
     *
     * @param user 會員資訊
     *
     * @return 更新狀態
     */
    boolean updateUser(User user);

    /**
     * 更新會員密碼
     *
     * @param username 會員名稱
     * @param oldPassword 會員舊密碼
     * @param newPassword 會員新密碼
     *
     * @return 帳號狀態
     */
    AuthStatus updatePassword(String username, String oldPassword, String newPassword);

    /**
     * 刪除會員
     *
     * @param username 會員名稱
     */
    void deleteUser(String username);

    /**
     * 註冊狀態
     */
    enum RegistrationStatus {
        USERNAME_EXISTS,
        EMAIL_EXISTS,
        SUCCESS
    }

    /**
     * 登入狀態
     */
    enum AuthStatus {
        USER_NOT_FOUND,
        PASSWORD_INCORRECT,
        PASSWORD_SAME,
        SUCCESS
    }
}
