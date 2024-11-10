package me.gt.snaptickets.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import me.gt.snaptickets.model.User;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @Schema(description = "帳號")
    private String username;

    @Schema(description = "密碼 (需包含大小寫字母、數字、特殊字元，且長度至少8)", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "電子郵件")
    private String email;

    @Schema(description = "手機號碼")
    private String phone;

    @Schema(description = "地址")
    private String address;


    /**
     * 轉換成 User 物件
     * @return User
     */
    public User convertToUser() {
        return User.builder()
                .username(username)
                .password(password)
                .name(name)
                .email(email)
                .phone(phone)
                .address(address)
                .build();
    }

    /**
     * 從 User 物件轉換成 UserDto 物件
     *
     * @param user
     *
     * @return UserDto
     */
    public static UserDto fromUser(User user) {
        return UserDto.builder()
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .build();
    }
}
