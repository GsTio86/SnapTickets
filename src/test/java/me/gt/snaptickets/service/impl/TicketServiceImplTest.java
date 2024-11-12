package me.gt.snaptickets.service.impl;

import me.gt.snaptickets.model.Ticket;
import me.gt.snaptickets.service.TicketService;
import me.gt.snaptickets.util.DateUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@SpringBootTest
class TicketServiceImplTest {

    @Autowired
    private TicketService service;

    @Test
    void createTicket() {
        Ticket ticket = Ticket.builder()
                .name("測試票券")
                .description("這是一張測試票券")
                .stock(100)
                .startDate(LocalDateTime.parse("2024/11/25 23:59:59", DateUtil.standardDataFormat))
                .price(BigDecimal.valueOf(1000))
                .build();
        service.createTicket(ticket);
        System.out.println("建立票券成功");
    }

    @Test
    void getByTicketId() {
        String ticketId = "TICKET2252056365a612c5d";
        Ticket ticket = service.getByTicketId(ticketId);
        if (ticket != null) {
            System.out.println(ticket);
        } else {
            System.out.println("找不到票券");
        }
    }

    @Test
    void getAllTickets() {
        service.getAllTickets().forEach(System.out::println);
    }

    @Test
    void getTicketsByName() {
        String name = "票券";
        service.getTicketsByName(name).forEach(System.out::println);
    }

    @Test
    void getAvailableTickets() {
        service.getAvailableTickets().forEach(System.out::println);
    }

    @Test
    void getSoldOutTickets() {
        service.getSoldOutTickets().forEach(System.out::println);
    }

    @Test
    void getUpcomingTickets() {
        service.getUpcomingTickets(Optional.empty()).forEach(System.out::println);
    }

    @Test
    void updateTicket() {
        Ticket ticket = service.getByTicketId("TICKET2252056365a612c5d");
        if (ticket == null) {
            System.out.println("找不到票券");
            return;
        }
        ticket.setName("更新票券");
        ticket.setDescription("這是一張測試用的票券啦~");
        ticket.setStock(200);
        ticket.setStartDate(LocalDateTime.parse("2024/11/08 00:59:59", DateUtil.standardDataFormat));
        ticket.setPrice(BigDecimal.valueOf(2000));
        service.updateTicket(ticket);
        System.out.println("更新票券成功");
    }

    @Test
    void deleteTicket() {
        String ticketId = "TICKET2252056365a612c5d";
        service.deleteTicket(ticketId);
        System.out.println("刪除票券成功");
    }
}