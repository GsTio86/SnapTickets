package me.gt.snaptickets.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import me.gt.snaptickets.util.IdUtil;
import org.springframework.data.annotation.Transient;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Schema(description = "訂單編號")
    @Builder.Default
    private String orderId = IdUtil.generateOrderId();

    @Schema(description = "購買的使用者")
    private String username;

    @Schema(description = "購買的票券")
    private String ticketId;

    @Schema(description = "購買數量")
    private Integer quantity;

    @Schema(description = "總金額")
    private BigDecimal totalPrice;

    @Schema(description = "訂單狀態")
    @Builder.Default
    private Status orderStatus = Status.PENDING;

    @Schema(description = "訂單建立時間")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Schema(description = "訂單更新時間")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Transient
    private User user;

    @Transient
    private Ticket ticket;

    @Schema(description = "訂單處理狀態")
    public enum Status {
        PENDING("待付款"),
        PAID("已付款"),
        COMPLETED("已完成"),
        CANCELLED("已取消");

        String name;
        Status(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}