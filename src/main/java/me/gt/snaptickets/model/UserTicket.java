package me.gt.snaptickets.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import me.gt.snaptickets.util.IdUtil;
import org.springframework.data.annotation.Transient;

import java.time.LocalDateTime;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserTicket {

    @Schema(description = "使用者票券編號")
    @Builder.Default
    private String userTicketId = IdUtil.generateUserTicketId();

    @Schema(description = "帳號")
    private String username;

    @Schema(description = "訂單編號")
    private String orderId;

    @Schema(description = "票券編號")
    private String ticketId;

    @Schema(description = "票券代碼")
    @Builder.Default
    private String code = IdUtil.generateTicketCode();

    @Schema(description = "使用狀態")
    @Builder.Default
    private Status status = Status.NOT_USED;

    @Schema(description = "獲得時間")
    @Builder.Default
    private LocalDateTime issuedAt = LocalDateTime.now();

    @Schema(description = "使用時間")
    private LocalDateTime usedAt;

    @Schema(description = "使用期限")
    @Builder.Default
    private LocalDateTime expiredAt = LocalDateTime.now().plusDays(5);

    @Transient
    private Ticket ticket;

    @Schema(description = "票券使用狀態")
    public enum Status {
        NOT_USED("未使用"),
        USED("已使用");

        String name;
        Status(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
