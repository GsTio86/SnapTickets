package me.gt.snaptickets.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import me.gt.snaptickets.model.AdminUser;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserDto {

    @Schema(description = "帳號")
    private String username;

    @Schema(description = "密碼 (需包含大小寫字母、數字、特殊字元，且長度至少8)", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "電子郵件")
    private String email;

    /**
     * 轉換成 AdminUser 物件
     * @return AdminUser
     */
    public AdminUser convertToUser() {
        return AdminUser.builder()
                .username(username)
                .password(password)
                .name(name)
                .email(email)
                .build();
    }

    /**
     * 從 User 物件轉換成 AdminUserDto 物件
     *
     * @param user
     *
     * @return AdminUserDto
     */
    public static AdminUserDto fromUser(AdminUser user) {
        return AdminUserDto.builder()
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
