package me.gt.snaptickets.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import me.gt.snaptickets.model.Order;
import me.gt.snaptickets.model.Payment;

import java.math.BigDecimal;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    @Schema(description = "購買的使用者")
    private String username;

    @Schema(description = "購買的票券")
    private String ticketId;

    @Schema(description = "購買數量")
    private Integer quantity;

    @Schema(description = "總金額")
    private BigDecimal totalPrice;

    @Schema(description = "付款方式")
    private Payment.Method paymentMethod;

    @Schema(description = "付款狀態")
    private Payment.Status paymentStatus;

    public Order convertToOrder() {
        return Order.builder()
                .username(username)
                .ticketId(ticketId)
                .quantity(quantity)
                .totalPrice(totalPrice)
                .build();
    }

}
