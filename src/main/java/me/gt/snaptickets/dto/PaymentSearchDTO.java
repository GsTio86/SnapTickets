package me.gt.snaptickets.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.gt.snaptickets.model.Payment;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSearchDTO {

    @Schema(description = "付款編號")
    private String paymentId;

    @Schema(description = "訂單編號")
    private String orderId;

    @Schema(description = "付款方式")
    private String method;

    @Schema(description = "付款狀態")
    private Payment.Status status;

}
