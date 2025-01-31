package me.gt.snaptickets.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import me.gt.snaptickets.model.Ticket;
import me.gt.snaptickets.util.DateUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TicketDto {

    @Schema(description = "票券名稱")
    private String name;

    @Schema(description = "票券描述")
    private String description;

    @Schema(description = "價格", example = "100")
    private BigDecimal price;

    @Schema(description = "庫存", example = "100")
    private Integer stock;

    @Schema(description = "開賣日期", example = "2024/12/31 12:00:00")
    private String startDate;

    @Schema(description = "結束日期", example = "2024/12/31 12:00:00")
    private String endDate;

    /**
     * 將 TicketDto 轉換為 Ticket
     *
     * @return Ticket
     */
    public Ticket convertToTicket() {
        return Ticket.builder()
                .name(name)
                .description(description)
                .price(price)
                .stock(stock)
                .startDate(parseEventDate(startDate))
                .endDate(parseEventDate(endDate))
                .build();
    }

    /**
     * 將 TicketDto 轉換為 Ticket
     *
     * @param ticketId
     *
     * @return TicketDto
     */
    public Ticket convertToTicket(String ticketId) {
        return Ticket.builder()
                .ticketId(ticketId)
                .name(name)
                .description(description)
                .price(price)
                .stock(stock)
                .startDate(parseEventDate(startDate))
                .endDate(parseEventDate(endDate))
                .build();
    }

    private LocalDateTime parseEventDate(String date) {
        try {
            return LocalDateTime.parse(date, DateUtil.standardDataFormat);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("日期格式不正確，應為 " + DateUtil.standardDataFormat);
        }
    }
}
