package me.gt.snaptickets.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import me.gt.snaptickets.model.User;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Date;
import java.util.regex.Pattern;

public class PasswordUtil {

    /**
     * JWT 密鑰過期時間 (一天)
     */
    private static final long jwtExpirationMs = 86400000;

    /**
     * 密碼安全性正則表達式
     */
    private static final String PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    /**
     * 密碼安全性檢查
     */
    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);


    /**
     * 生成 JWT Token
     * @param user
     * @return JWT Token
     */
    public static String generateJwtToken(User user, String jwtSecret) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("email", user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    /**
     * 生成密碼鹽值
     *
     * @return 密碼鹽值
     */
    public static String generateSalt() {
        return BCrypt.gensalt();
    }

    /**
     * 加密密碼
     *
     * @param password 密碼
     * @return 加密後的密碼
     */
    public static String encryptPassword(String password) {
        return BCrypt.hashpw(password, generateSalt());
    }

    /**
     * 驗證密碼
     *
     * @param password      密碼
     * @param hashedPassword 加密後的密碼
     * @return 是否驗證成功
     */
    public static boolean verifyPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }

    /**
     * 驗證密碼是否符合安全性要求
     *
     * @param password 密碼
     * @return 是否符合安全性要求
     */
    public static boolean isValid(String password) {
        return password != null && pattern.matcher(password).matches();
    }

    /**
     * 驗證密碼是否符合安全性要求
     *
     * @param password 密碼
     * @return 錯誤訊息
     */
    public static String validatePassword(String password) {
        if (password == null || password.length() < 8 || !isValid(password)) {
            return "密碼需包含大小寫字母、數字、特殊字元，且至少8位";
        }
        return null;
    }

}
