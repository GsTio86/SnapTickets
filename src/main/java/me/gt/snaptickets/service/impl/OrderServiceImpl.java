package me.gt.snaptickets.service.impl;

import me.gt.snaptickets.exception.InsufficientStockException;
import me.gt.snaptickets.mapper.OrderMapper;
import me.gt.snaptickets.model.Order;
import me.gt.snaptickets.model.Ticket;
import me.gt.snaptickets.service.OrderService;
import me.gt.snaptickets.service.RedisService;
import me.gt.snaptickets.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private RedisService redisService;

    private static final String TICKET_LOCK = "ticket_lock:";

    private static final String TICKET_STOCK = "ticket_stock:";

    private static final long LOCK_EXPIRY_TIME = 10;

    @Override
    @Transactional
    public ActionStatus createOrder(Order order) {
        String stockKey = TICKET_STOCK + order.getTicketId(); // 票券庫存key
        String lockKey = TICKET_LOCK + order.getTicketId(); // 票券鎖key
        String lockValue = order.getOrderId(); // 鎖的值 只有持有鎖的人才能解鎖

        if (redisService.tryLock(lockKey, lockValue, LOCK_EXPIRY_TIME)) { // 嘗試鎖定
            try {
                int cacheStock;
                Integer currentStock = (Integer) redisService.get(stockKey).orElse(null);
                if (currentStock == null) {
                    Ticket ticket = ticketService.getByTicketId(order.getTicketId());
                    if (ticket == null) {
                        return ActionStatus.NOT_FOUND;
                    }
                    cacheStock = ticket.getStock();
                    redisService.set(stockKey, cacheStock);
                } else {
                    cacheStock = currentStock;
                }

                if (cacheStock >= order.getQuantity()) { // 確認庫存是否足夠
                    redisService.set(stockKey, cacheStock - order.getQuantity()); // 更新redis 緩存中的庫存

                    ticketService.reduceStock(order.getTicketId(), order.getQuantity()); // 扣除資料庫的庫存數量

                    orderMapper.createOrder(order); // 建立訂單
                    return ActionStatus.CREATE_SUCCESS;
                } else {
                    return ActionStatus.STOCK_NOT_ENOUGH;
                }
            } catch (InsufficientStockException e) {
                return ActionStatus.STOCK_NOT_ENOUGH;
            } finally {
                redisService.unlock(lockKey, lockValue); // 解鎖
            }
        } else {
            return ActionStatus.SERVER_BUSY;
        }
    }

    @Override
    public Order getOrderByOrderId(String orderId) {
        return orderMapper.getById(orderId);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderMapper.getAll();
    }

    @Override
    public List<Order> getOrdersByStatus(Order.Status status) {
        return orderMapper.getByOrderStatus(status);
    }

    @Override
    public List<Order> getOrdersByUsername(String username) {
        return orderMapper.getByUsername(username);
    }

    @Override
    public List<Order> getOrdersByStatusAndCreatedAtBefore(Order.Status status, LocalDateTime before) {
        return orderMapper.getByOrderStatusAndCreatedAtBefore(status, before);
    }

    @Override
    public ActionStatus updateOrderStatus(String id, Order.Status status) {
        if (orderMapper.updateOrderStatus(id, status) == 0) {
            return ActionStatus.UPDATE_FAILED;
        }
        return ActionStatus.UPDATE_SUCCESS;
    }

    @Override
    public void deleteOrder(String orderId) {
        orderMapper.deleteOrder(orderId);
    }
}
