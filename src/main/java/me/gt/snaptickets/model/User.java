package me.gt.snaptickets.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Schema(description = "帳號")
    private String username;

    @Schema(description = "密碼")
    @JsonIgnore
    private String password;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "電子郵件")
    private String email;

    @Schema(description = "手機號碼")
    private String phone;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "建立日期")
    @Builder.Default
    @JsonIgnore
    private LocalDateTime createdAt = LocalDateTime.now();



}
