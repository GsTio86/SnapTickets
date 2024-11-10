package me.gt.snaptickets.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import me.gt.snaptickets.util.IdUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Schema(description = "付款編號")
    @Builder.Default
    private String paymentId = IdUtil.generatePaymentId();

    @Schema(description = "訂單編號")
    private String orderId;

    @Schema(description = "付款方式")
    private Method paymentMethod;

    @Schema(description = "付款狀態")
    @Builder.Default
    private Status paymentStatus = Status.PENDING;

    @Schema(description = "付款金額")
    private BigDecimal paymentAmount;

    @Schema(description = "付款建立時間")
    private LocalDateTime createdAt;

    @Schema(description = "付款更新時間")
    private LocalDateTime updatedAt;

    private Order order;

    public enum Status {
        PENDING("待付款"),
        CHECKING("待確認"),
        SIMULATING("模擬付款"),
        REFUND("已退款"),
        COMPLETED("已付款"),
        FAILED("付款失敗");

        private String name;

        Status(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum Method {
        ALL("ALL", "不指定"),
        CREDIT("Credit","信用卡"),
        WEBATM("WebATM","網路轉帳"),
        ATM("ATM","ATM轉帳"),
        CVS("CVS","超商代碼"),
        BARCODE("BARCODE","超商條碼"),
        APPLEPAY("ApplePay","ApplePay");

        private String id;
        private String name;

        Method(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

}