package me.gt.snaptickets.service.impl;

import me.gt.snaptickets.mapper.UserTicketMapper;
import me.gt.snaptickets.model.Order;
import me.gt.snaptickets.model.Payment;
import me.gt.snaptickets.model.UserTicket;
import me.gt.snaptickets.service.OrderService;
import me.gt.snaptickets.service.UserTicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserTicketServiceImpl implements UserTicketService {

    @Autowired
    private UserTicketMapper userTicketMapper;

    @Autowired
    private OrderService orderService;

    @Override
    @Transactional
    public void giveUserTicket(Payment payment) {
        Order order = payment.getOrder();
        if (order.getOrderStatus() == Order.Status.COMPLETED) {
            return;
        }
        for (int i = 0; i < order.getQuantity(); i++) {
            UserTicket userTicket = UserTicket.builder()
                    .username(payment.getOrder().getUsername())
                    .orderId(payment.getOrder().getOrderId())
                    .ticketId(payment.getOrder().getTicketId())
                    .build();
            userTicketMapper.giveUserTicket(userTicket);
        }
        orderService.updateOrderStatus(order.getOrderId(), Order.Status.COMPLETED); // 更新訂單狀態為已完成
    }

    @Override
    public UserTicket getUserTicketByUserTicketId(String userticketid) {
        return userTicketMapper.getUserTicketByUserTicketId(userticketid);
    }

    @Override
    public List<UserTicket> getUserTicketByUsername(String username) {
        return userTicketMapper.getUserTicketByUsername(username);
    }

    @Override
    public List<UserTicket> getUserTicketByOrderId(String orderid) {
        return userTicketMapper.getUserTicketByOrderId(orderid);
    }

    @Override
    public ActionStatus updateUserTicketCode(String userticketid, String code) {
        if (userTicketMapper.updateUserTicketCode(userticketid, code) > 0) {
            return ActionStatus.SUCCESS;
        }
        return ActionStatus.FAIL;
    }

    @Override
    public ActionStatus updateUserTicketStatus(String userticketid, UserTicket.Status status) {
        if (status == UserTicket.Status.USED) {
            if (userTicketMapper.updateUserTicketStatusWithUsedAt(userticketid, status) > 0) {
                return ActionStatus.SUCCESS;
            }
        } else {
            if (userTicketMapper.updateUserTicketStatus(userticketid, status) > 0) {
                return ActionStatus.SUCCESS;
            }
        }
        return ActionStatus.FAIL;
    }

    @Override
    public ActionStatus deleteUserTicket(String userticketid) {
        if (userTicketMapper.deleteUserTicket(userticketid) > 0) {
            return ActionStatus.SUCCESS;
        }
        return ActionStatus.FAIL;
    }
}
