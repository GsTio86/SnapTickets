package me.gt.snaptickets.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.gt.snaptickets.dto.ResetPasswordDto;
import me.gt.snaptickets.dto.UserDto;
import me.gt.snaptickets.model.Order;
import me.gt.snaptickets.model.User;
import me.gt.snaptickets.model.UserTicket;
import me.gt.snaptickets.service.OrderService;
import me.gt.snaptickets.service.UserService;
import me.gt.snaptickets.service.UserTicketService;
import me.gt.snaptickets.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@Tag(name = "會員 API", description = "處理會員帳號操作")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserTicketService userTicketService;


    @Value("${jwt.user.secret}")
    private String jwtSecret;

    @Operation(summary = "註冊帳號")
    @PostMapping("/auth/register")
    public ResponseEntity<String> registerUser(@RequestBody UserDto userDto) {
        String validationMessage = PasswordUtil.validatePassword(userDto.getPassword()); // 驗證密碼是否符合安全性要求
        if (validationMessage != null) {
            return ResponseEntity.badRequest().body(validationMessage);
        }
        UserService.RegistrationStatus status = userService.registerUser(userDto.convertToUser());
        return switch (status) {
            case USERNAME_EXISTS -> ResponseEntity.badRequest().body("此帳號已存在");
            case EMAIL_EXISTS -> ResponseEntity.badRequest().body("此信箱已被註冊");
            default -> ResponseEntity.ok("註冊成功");
        };
    }

    @Operation(summary = "帳號登入")
    @PostMapping("/auth/login")
    public ResponseEntity<Object> loginUser(@RequestParam String identifier, @RequestParam String password) {
        User user = userService.loginUser(identifier, password);
        if (user == null) {
            return ResponseEntity.badRequest().body("帳號或密碼錯誤");
        }
        String token = PasswordUtil.generateJwtToken(user,jwtSecret);
        return ResponseEntity.ok().body(Map.of("username", user.getUsername(), "token", token));
    }

    @Operation(summary = "重設密碼")
    @PutMapping("/auth/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        return updatePassword(resetPasswordDto.getUsername(), resetPasswordDto.getOldPassword(), resetPasswordDto.getNewPassword());
    }

    @Operation(summary = "查詢帳號資料")
    @GetMapping("/user/info/{username}")
    public ResponseEntity<Object> getUserByUsername(@PathVariable String username) {
        User user = userService.getByUsername(username);
        if (user == null) {
            return ResponseEntity.badRequest().body("查無此帳號");
        }
        return ResponseEntity.ok(UserDto.fromUser(user));
    }

    @Operation(summary = "查詢訂單")
    @GetMapping("/user/order/{username}")
    public ResponseEntity<List<Order>> getOrdersByUsername(@PathVariable("username") String username) {
        return ResponseEntity.ok(orderService.getOrdersByUsername(username));
    }

    @Operation(summary = "查詢擁有的票券")
    @GetMapping("/user/ticket/{username}")
    public ResponseEntity<List<UserTicket>> getTicketByUsername(@PathVariable("username") String username) {
        return ResponseEntity.ok(userTicketService.getUserTicketByUsername(username));
    }

    @Operation(summary = "查詢擁有的票券資料")
    @GetMapping("/user/ticket/info/{orderid}")
    public ResponseEntity<List<UserTicket>> getTicketByUsernameAndOrderId(@PathVariable("orderid") String orderid) {
        return ResponseEntity.ok(userTicketService.getUserTicketByOrderId(orderid));
    }

    @Operation(summary = "更新帳號資訊")
    @PutMapping("/user/update/{username}")
    public ResponseEntity<Object> updateUser(@PathVariable("username") String username, @RequestBody UserDto user) {
        if (!username.equals(user.getUsername())) {
            return ResponseEntity.badRequest().body("更改資料失敗");
        }
        return updateUser(user);
    }

    @Operation(summary = "更改帳號密碼")
    @PutMapping("/user/change-password/{username}")
    public ResponseEntity<String> updatePassword(@PathVariable String username, @RequestParam String oldPassword, @RequestParam String newPassword) {
        String validationMessage = PasswordUtil.validatePassword(newPassword); // 驗證新密碼是否符合安全性要求
        if (validationMessage != null) {
            return ResponseEntity.badRequest().body(validationMessage);
        }
        UserService.AuthStatus status = userService.updatePassword(username, oldPassword, newPassword);
        return switch (status) {
            case USER_NOT_FOUND -> ResponseEntity.badRequest().body("帳號不存在");
            case PASSWORD_INCORRECT -> ResponseEntity.badRequest().body("舊密碼不正確");
            case PASSWORD_SAME -> ResponseEntity.badRequest().body("新密碼與舊密碼相同");
            default -> ResponseEntity.ok("更改密碼成功");
        };
    }

    @Operation(summary = "查詢帳號資料")
    @GetMapping("/admin/user/info")
    public ResponseEntity<Object> getUserDataByUsername(@RequestParam String username) {
        return getUserByUsername(username);
    }

    @Operation(summary = "獲取所有帳號資料")
    @GetMapping("/admin/user/all")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "更新帳號資訊")
    @PutMapping("/admin/user/update")
    public ResponseEntity<Object> updateUser(@RequestBody UserDto user) {
        boolean success = userService.updateUser(user.convertToUser());
        if (success) {
            return ResponseEntity.ok("更改資料成功");
        }
        return ResponseEntity.badRequest().body("更改資料失敗");
    }

    @Operation(summary = "刪除帳號")
    @DeleteMapping("/admin/user/delete/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.ok("帳號已刪除");
    }

}
