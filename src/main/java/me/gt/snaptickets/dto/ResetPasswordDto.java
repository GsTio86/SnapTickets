package me.gt.snaptickets.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordDto {
    @Schema(description = "帳號")
    private String username;

    @Schema(description = "舊密碼")
    private String oldPassword;

    @Schema(description = "新密碼")
    private String newPassword;
}
