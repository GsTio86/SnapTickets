package me.gt.snaptickets.mapper;

import me.gt.snaptickets.model.Payment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PaymentMapper {

    @Insert("INSERT INTO payments (paymentid, orderid, paymentmethod, paymentstatus, paymentamount) " +
            "VALUES(#{paymentId}, #{orderId}, #{paymentMethod}, #{paymentStatus}, #{paymentAmount})")
    void createPayment(Payment payment);

    @Select("SELECT * FROM payments WHERE paymentid = #{paymentId}")
    @Results(id = "PaymentWithOrder", value = {
            @Result(column = "paymentid", property = "paymentId"),
            @Result(column = "orderid", property = "orderId"),
            @Result(column = "paymentmethod", property = "paymentMethod"),
            @Result(column = "paymentstatus", property = "paymentStatus"),
            @Result(column = "paymentamount", property = "paymentAmount"),
            @Result(column = "createdat", property = "createdAt"),
            @Result(column = "updatedat", property = "updatedAt"),
            @Result(column = "orderid", property = "order", one = @One(select = "me.gt.snaptickets.mapper.OrderMapper.getById"))
    })
    Payment getByPaymentId(String paymentId);

    @Select("SELECT * FROM payments WHERE orderid = #{orderId}")
    Payment getByOrderId(String orderId);

    @Select("SELECT * FROM payments")
    List<Payment> getAllPayments();

    @Select("SELECT * FROM payments WHERE paymentstatus = #{paymentStatus}")
    List<Payment> getByPaymentStatus(String paymentStatus);

    @Select("SELECT * FROM payments WHERE paymentmethod = #{paymentMethod}")
    List<Payment> getByPaymentMethod(String paymentMethod);

    @Update("UPDATE payments SET paymentstatus = #{paymentStatus}, updatedat = CURRENT_TIMESTAMP WHERE paymentid = #{paymentId}")
    void updatePayment(String paymentId, String paymentStatus);

    @Delete("DELETE FROM payments WHERE paymentid = #{paymentId}")
    void deletePayment(String id);
}
