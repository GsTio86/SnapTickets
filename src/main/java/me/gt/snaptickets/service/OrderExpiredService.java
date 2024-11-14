package me.gt.snaptickets.service;

import me.gt.snaptickets.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class OrderExpiredService {

    @Autowired
    private OrderService orderService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private TicketService ticketService;

    private static final String TICKET_STOCK = "TICKET_STOCK:";

    @Scheduled(fixedRate = 3, timeUnit = TimeUnit.MINUTES)  // 每3分鐘執行一次
    public void checkExpiredOrders() {
        LocalDateTime now = LocalDateTime.now();
        List<Order> expiredOrders = orderService.getOrdersByStatusAndCreatedAtBefore(Order.Status.PENDING, now.minusMinutes(15));   // 取得15分鐘前仍未付款的訂單
        for (Order order : expiredOrders) {
            String stockKey = TICKET_STOCK + order.getTicketId();
            int quantity = order.getQuantity();
            orderService.updateOrderStatus(order.getOrderId(), Order.Status.CANCELLED);   // 更新訂單狀態 為取消

            Integer currentStock = (Integer) redisService.get(stockKey).orElse(null);
            if (currentStock != null) {
                redisService.set(stockKey, currentStock + quantity);   // 更新redis 緩存中的庫存
            }
            ticketService.increaseStock(order.getTicketId(), quantity);   // 更新票券庫存
        }
    }
}
