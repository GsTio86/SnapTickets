package me.gt.snaptickets.mapper;

import me.gt.snaptickets.model.Order;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {

    @Insert("INSERT INTO orders (orderid, username, ticketId, quantity, totalprice, orderstatus) " +
            "VALUES(#{orderId}, #{username}, #{ticketId}, #{quantity}, #{totalPrice}, #{orderStatus})")
    void createOrder(Order order);

    @Select("SELECT * FROM orders WHERE orderId = #{orderId}")
    @Results(id = "OrderWithUser", value = {
            @Result(column = "orderid", property = "orderId"),
            @Result(column = "username", property = "username"),
            @Result(column = "ticketid", property = "ticketId"),
            @Result(column = "quantity", property = "quantity"),
            @Result(column = "totalprice", property = "totalPrice"),
            @Result(column = "orderstatus", property = "orderStatus"),
            @Result(column = "createdat", property = "createdAt"),
            @Result(column = "updatedat", property = "updatedAt"),
            @Result(column = "username", property = "user", one = @One(select = "me.gt.snaptickets.mapper.UserMapper.getByUsername")),
            @Result(column = "ticketid", property = "ticket", one = @One(select = "me.gt.snaptickets.mapper.TicketMapper.getById"))
    })
    Order getById(String orderId);

    @Select("SELECT * FROM orders")
    List<Order> getAll();

    @Select("SELECT * FROM orders WHERE username = #{username}")
    @ResultMap("OrderWithUser")
    List<Order> getByUsername(String username);

    @Select("SELECT * FROM orders WHERE orderstatus = #{orderStatus}")
    @ResultMap("OrderWithUser")
    List<Order> getByOrderStatus(Order.Status orderStatus);

    @Select("SELECT * FROM orders WHERE orderstatus = #{status} AND createdat < #{before}")
    @ResultMap("OrderWithUser")
    List<Order> getByOrderStatusAndCreatedAtBefore(Order.Status status, LocalDateTime before);

    @Update("UPDATE orders SET orderstatus = #{orderStatus}, updatedat = CURRENT_TIMESTAMP " + "WHERE orderid = #{orderId}")
    int updateOrderStatus(String orderId, Order.Status orderStatus);

    @Delete("DELETE FROM orders WHERE orderid = #{orderId}")
    void deleteOrder(String orderId);

}
