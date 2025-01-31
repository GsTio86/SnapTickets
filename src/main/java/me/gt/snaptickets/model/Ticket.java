package me.gt.snaptickets.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Ticket {

    @Schema(description = "編號")
    @Builder.Default
    private String ticketId = IdUtil.generateTicketId();

    @Schema(description = "名稱")
    private String name;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "圖片")
    @JsonIgnore
    private byte[] image;

    @Schema(description = "價格")
    private BigDecimal price;

    @Schema(description = "庫存數量")
    private Integer stock;

    @Schema(description = "開賣日期")
    private LocalDateTime startDate;

    @Schema(description = "結束日期")
    private LocalDateTime endDate;

    @Schema(description = "建立日期")
    private LocalDateTime createdAt;

    @Schema(description = "更新日期")
    private LocalDateTime updatedAt;
}
