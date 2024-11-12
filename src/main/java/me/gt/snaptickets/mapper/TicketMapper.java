package me.gt.snaptickets.mapper;

import me.gt.snaptickets.model.Ticket;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TicketMapper {

    @Insert("INSERT INTO tickets (ticketid, name, description, image, price, stock, startDate) " +
            "VALUES(#{ticketId}, #{name}, #{description}, #{image}, #{price}, #{stock}, #{startDate})")
    void createTicket(Ticket ticket);

    @Select("SELECT * FROM tickets WHERE ticketid = #{ticketId}")
    Ticket getById(String ticketId);

    @Select("SELECT * FROM tickets")
    List<Ticket> getAll();

    @Select("SELECT * FROM tickets WHERE name like #{name}")
    List<Ticket> getByName(String name);

    @Select("SELECT * FROM tickets WHERE stock > 0")
    List<Ticket> getAvailable();

    @Select("SELECT * FROM tickets WHERE stock = 0")
    List<Ticket> getSoldOut();

    @Select("SELECT * FROM tickets WHERE tickets.startdate > #{startDate}")
    List<Ticket> getUpcoming(LocalDateTime startDate);

    @Update("UPDATE tickets SET " +
            "name = #{name}, description = #{description}, " +
            "price = #{price}, " +
            "startdate = #{startDate}, " +
            "enddate = #{endDate}" +
            " = CURRENT_TIMESTAMP " +
            "WHERE ticketid = #{ticketId}")
    int updateTicket(Ticket ticket);

    @Update("UPDATE tickets SET image = #{image} WHERE ticketid = #{ticketId}")
    int updateImage(String ticketId, byte[] image);

    @Update("UPDATE tickets SET stock = #{quantity} WHERE ticketid = #{ticketId}")
    void updateTicketStock(String ticketId, int quantity);

    @Update("UPDATE tickets SET startDate = #{startDate} WHERE ticketid = #{ticketId}")
    void updateTicketStartDate(String ticketId, LocalDateTime startDate);

    @Update("UPDATE tickets SET endDate = #{endDate} WHERE ticketid = #{ticketId}")
    void updateTicketEndDate(String ticketId, LocalDateTime endDate);

    @Update("UPDATE tickets SET stock = stock - #{quantity} WHERE ticketid = #{ticketId} AND stock >= #{quantity}")
    int decreaseAvailableStock(String ticketId, int quantity);

    @Delete("DELETE FROM tickets WHERE ticketid = #{ticketId}")
    int deleteTicket(String ticketId);
}
