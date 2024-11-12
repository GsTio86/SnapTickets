package me.gt.snaptickets.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import me.gt.snaptickets.dto.AdminUserDto;
import me.gt.snaptickets.model.AdminUser;
import me.gt.snaptickets.service.AdminUserService;
import me.gt.snaptickets.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@Hidden
@RequestMapping("/admin")
public class AdminUserController {

    @Autowired
    private AdminUserService userService;

    @Value("${jwt.admin.secret}")
    private String jwtSecret;

    @Operation(summary = "註冊管理員帳號")
    @PostMapping("/auth/register")
    public ResponseEntity<String> registerUser(@RequestBody AdminUserDto adminUserDto) {
        String validationMessage = PasswordUtil.validatePassword(adminUserDto.getPassword()); // 驗證密碼是否符合安全性要求
        if (validationMessage != null) {
            return ResponseEntity.badRequest().body(validationMessage);
        }
        AdminUserService.RegistrationStatus status = userService.registerAdminUser(adminUserDto.convertToUser());
        return switch (status) {
            case USERNAME_EXISTS -> ResponseEntity.badRequest().body("此帳號已存在");
            case EMAIL_EXISTS -> ResponseEntity.badRequest().body("此信箱已被註冊");
            default -> ResponseEntity.ok("註冊成功");
        };
    }

    @Operation(summary = "管理員帳號登入")
    @PostMapping("/auth/login")
    public ResponseEntity<Object> loginUser(@RequestParam String identifier, @RequestParam String password) {
        AdminUser user = userService.loginUser(identifier, password);
        if (user == null) {
            return ResponseEntity.badRequest().body("帳號或密碼錯誤");
        }
        String token = PasswordUtil.generateJwtToken(user,jwtSecret);
        return ResponseEntity.ok().body(Map.of("username", user.getUsername(), "token", token));
    }

    @Operation(summary = "透過帳號查詢資料")
    @GetMapping("/user/info/{username}")
    public ResponseEntity<Object> getUserByUsername(@PathVariable String username) {
        AdminUser user = userService.getByUsername(username);
        if (user == null) {
            return ResponseEntity.badRequest().body("查無此帳號");
        }
        return ResponseEntity.ok(AdminUserDto.fromUser(user));
    }

    @Operation(summary = "更新帳號資訊")
    @PutMapping("/user")
    public ResponseEntity<Object> updateUser(@RequestBody AdminUserDto user) {
        boolean success = userService.updateUser(user.convertToUser());
        if (success) {
            return ResponseEntity.ok("更改資料成功");
        }
        return ResponseEntity.badRequest().body("更改資料失敗");
    }

    @Operation(summary = "更改帳號密碼")
    @PutMapping("/change-password/{username}")
    public ResponseEntity<String> updatePassword(@PathVariable String username, @RequestParam String oldPassword, @RequestParam String newPassword) {
        String validationMessage = PasswordUtil.validatePassword(newPassword); // 驗證新密碼是否符合安全性要求
        if (validationMessage != null) {
            return ResponseEntity.badRequest().body(validationMessage);
        }
        AdminUserService.AuthStatus status = userService.updatePassword(username, oldPassword, newPassword);
        return switch (status) {
            case USER_NOT_FOUND -> ResponseEntity.badRequest().body("帳號不存在");
            case PASSWORD_INCORRECT -> ResponseEntity.badRequest().body("舊密碼不正確");
            case PASSWORD_SAME -> ResponseEntity.badRequest().body("新密碼與舊密碼相同");
            default -> ResponseEntity.ok("更改密碼成功");
        };
    }

    @Operation(summary = "刪除帳號")
    @DeleteMapping("/user/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.ok("帳號已刪除");
    }

}
