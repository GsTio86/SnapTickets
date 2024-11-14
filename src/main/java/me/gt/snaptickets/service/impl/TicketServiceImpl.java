package me.gt.snaptickets.service.impl;

import me.gt.snaptickets.exception.InsufficientStockException;
import me.gt.snaptickets.mapper.TicketMapper;
import me.gt.snaptickets.model.Ticket;
import me.gt.snaptickets.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TicketServiceImpl implements TicketService {

    @Autowired
    private TicketMapper ticketMapper;

    @Override
    public void createTicket(Ticket ticket) {
        ticketMapper.createTicket(ticket);
    }

    @Override
    public Ticket getByTicketId(String ticketId) {
        return ticketMapper.getById(ticketId);
    }

    @Override
    public List<Ticket> getAllTickets() {
        return ticketMapper.getAll();
    }

    @Override
    public List<Ticket> getTicketsByName(String name) {
        return ticketMapper.getByName(name);
    }

    @Override
    public List<Ticket> getAvailableTickets() {
        return ticketMapper.getAvailable();
    }

    @Override
    public List<Ticket> getSoldOutTickets() {
        return ticketMapper.getSoldOut();
    }

    @Override
    public List<Ticket> getUpcomingTickets(Optional<LocalDateTime> eventDate) {
        if (eventDate.isEmpty()) {
            return ticketMapper.getUpcoming(LocalDateTime.now());
        }
        return ticketMapper.getUpcoming(eventDate.get());
    }

    @Override
    public ActionStatus updateTicket(Ticket ticket) {
        if (ticketMapper.updateTicket(ticket) == 0) {
            return ActionStatus.UPDATE_FAILED;
        }
        return ActionStatus.UPDATE_SUCCESS;
    }

    @Override
    public void updateTicketStock(String ticketId, int quantity) {
        ticketMapper.updateTicketStock(ticketId, quantity);
    }

    @Override
    public void increaseStock(String ticketId, int quantity) {
        ticketMapper.increaseAvailableStock(ticketId, quantity);
    }

    @Override
    @Transactional
    public void reduceStock(String ticketId, int quantity) {
        int updatedRows = ticketMapper.decreaseAvailableStock(ticketId, quantity); // 扣除庫存數量
        if (updatedRows == 0) {
            throw new InsufficientStockException("此票券庫存不足");
        }
    }

    @Override
    public ActionStatus deleteTicket(String ticketId) {
        if (ticketMapper.deleteTicket(ticketId) == 0) {
            return ActionStatus.DELETE_FAILED;
        }
        return ActionStatus.DELETE_SUCCESS;
    }

    @Override
    public ActionStatus updateImage(String id, byte[] bytes) {
        if (ticketMapper.updateImage(id, bytes) == 0) {
            return ActionStatus.UPDATE_FAILED;
        }
        return ActionStatus.UPDATE_SUCCESS;
    }
}
