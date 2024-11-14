package me.gt.snaptickets.service;

import me.gt.snaptickets.model.AdminUser;

import java.util.List;

public interface AdminUserService {

    /**
     * 註冊會員
     *
     * @param adminUser 管理員帳號
     * @return 註冊狀態
     */
    RegistrationStatus registerAdminUser(AdminUser adminUser);

    /**
     * 透過管理員名稱查詢管理員
     *
     * @param identifier 管理員帳號(帳號或信箱)
     * @return 管理員資訊
     */
    AdminUser loginUser(String identifier, String password);


    /**
     * 管理員Token登入
     *
     * @param username 會員名稱
     * @param password 會員密碼
     * @return 驗證狀態 true: 通過 false: 失敗
     */
    boolean verifyTokenLogin(String username, String password);

    /**
     * 透過管理員名稱查詢管理員
     *
     * @param username 管理員名稱
     * @return 管理員資訊
     */
    AdminUser getByUsername(String username);


    /**
     * 取得所有管理員
     *
     * @return 管理員帳號列表
     */
    List<AdminUser> getAllUsers();

    /**
     * 更新管理員資訊
     *
     * @param adminUser 管理員資訊
     *
     * @return 更新狀態
     */
    boolean updateUser(AdminUser adminUser);

    /**
     * 更新管理員密碼
     *
     * @param username 管理員名稱
     * @param oldPassword 管理員舊密碼
     * @param newPassword 管理員新密碼
     *
     * @return 帳號狀態
     */
    AuthStatus updatePassword(String username, String oldPassword, String newPassword);

    /**
     * 刪除管理員
     *
     * @param username 管理員名稱
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
