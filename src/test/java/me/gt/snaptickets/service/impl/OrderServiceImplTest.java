package me.gt.snaptickets.service.impl;

import me.gt.snaptickets.model.Order;
import me.gt.snaptickets.model.Ticket;
import me.gt.snaptickets.service.OrderService;
import me.gt.snaptickets.service.TicketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
class OrderServiceImplTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private TicketService ticketService;

    @Test
    void createOrder() {
        Ticket ticket = ticketService.getByTicketId("TICKET002349793089725c3");
        if (ticket == null) {
            System.out.println("找不到票券");
            return;
        }
        System.out.println("購買票券: " + ticket);
        Order order = Order.builder()
                .username("test")
                .ticketId(ticket.getTicketId())
                .quantity(3)
                .totalPrice(ticket.getPrice().multiply(BigDecimal.valueOf(3)))
                .orderStatus(Order.Status.PENDING)
                .build();
        orderService.createOrder(order);
        System.out.println("建立訂單成功");
    }

    @Test
    void getOrderByOrderId() {
        Order order = orderService.getOrderByOrderId("ORDER0031399573d50a431");
        if (order == null) {
            System.out.println("找不到訂單");
            return;
        }
        System.out.println("訂單資訊: " + order);
        System.out.println("購買帳號: " + order.getUser());
    }

    @Test
    void getAllOrders() {
        orderService.getAllOrders().forEach(System.out::println);
    }

    @Test
    void getOrdersByStatus() {
        orderService.getOrdersByStatus(Order.Status.PENDING).forEach(System.out::println);
    }

    @Test
    void updateOrder() {
        Order order = orderService.getOrderByOrderId("ORDER0031399573d50a431");
        if (order == null) {
            System.out.println("找不到訂單");
            return;
        }
        System.out.println("訂單資訊: " + order);
        orderService.updateOrderStatus(order.getOrderId(), order.getOrderStatus());
        System.out.println("更新訂單成功");
    }

    @Test
    void deleteOrder() {
        orderService.deleteOrder("ORDER0031399573d50a431");
    }
}