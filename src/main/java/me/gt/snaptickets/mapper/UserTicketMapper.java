package me.gt.snaptickets.mapper;

import me.gt.snaptickets.model.UserTicket;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserTicketMapper {

    @Insert("INSERT INTO user_ticket (userticketid, username, orderid, ticketid, code, status, expiredat) " +
            "VALUES (#{userTicketId}, #{username}, #{orderId}, #{ticketId}, #{code}, #{status}, #{expiredAt})")
    void giveUserTicket(UserTicket userTicket);

    @Select("SELECT * FROM user_ticket WHERE userticketid = #{userTicketId}")
    @Results(id = "TicketWithUser", value = {
            @Result(column = "userticketid", property = "userTicketId"),
            @Result(column = "username", property = "username"),
            @Result(column = "orderid", property = "orderId"),
            @Result(column = "ticketid", property = "ticketId"),
            @Result(column = "code", property = "code"),
            @Result(column = "status", property = "status"),
            @Result(column = "issuedat", property = "issuedAt"),
            @Result(column = "usedat", property = "usedAt"),
            @Result(column = "expiredat", property = "expiredAt"),
            @Result(column = "ticketid", property = "ticket", one = @One(select = "me.gt.snaptickets.mapper.TicketMapper.getById"))
    })
    UserTicket getUserTicketByUserTicketId(String userticketid);

    @Select("SELECT * FROM user_ticket WHERE orderid = #{orderId}")
    List<UserTicket> getUserTicketByOrderId(String orderId);

    @Select("SELECT * FROM user_ticket WHERE username = #{username}")
    @ResultMap("TicketWithUser")
    List<UserTicket> getUserTicketByUsername(String username);

    @Update("UPDATE user_ticket SET code = #{code} WHERE userticketid = #{userTicketId}")
    int updateUserTicketCode(String userticketid, String code);

    @Update("UPDATE user_ticket SET status = #{status} WHERE userticketid = #{userTicketId}")
    int updateUserTicketStatus(String userticketid, UserTicket.Status status);

    @Update("UPDATE user_ticket SET status = #{status}, usedat = CURRENT_TIMESTAMP WHERE userticketid = #{userTicketId}")
    int updateUserTicketStatusWithUsedAt(String userticketid, UserTicket.Status status);

    @Delete("DELETE FROM user_ticket WHERE userticketid = #{userTicketId}")
    int deleteUserTicket(String userticketid);
}
