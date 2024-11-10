package me.gt.snaptickets.service.impl;

import me.gt.snaptickets.model.Order;
import me.gt.snaptickets.model.Payment;
import me.gt.snaptickets.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PaymentServiceImplTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private OrderServiceImpl orderService;

    @Test
    void createPayment() {
        Order order = orderService.getOrderByOrderId("ORDER0031399573d50a431");
        if (order == null) {
            System.out.println("找不到訂單");
            return;
        }
        System.out.println("訂單資訊: " + order);
        Payment payment = Payment.builder()
                .orderId(order.getOrderId())
                .paymentMethod(Payment.Method.CREDIT)
                .paymentAmount(order.getTotalPrice())
                .build();
        paymentService.createPayment(null);
        System.out.println("成功建立付款: " + payment);
    }

    @Test
    void getByPaymentId() {
        Payment payment = paymentService.getByPaymentId("PAY011225810d3c1f5f1");
        if (payment == null) {
            System.out.println("找不到付款");
            return;
        }
        System.out.println("付款資訊: " + payment);
        System.out.println("付款訂單資訊: " + payment.getOrder());
    }

    @Test
    void getAll() {
        paymentService.getAll().forEach(System.out::println);
    }

    @Test
    void getAllByPaymentStatus() {
        paymentService.getAllByPaymentStatus(Payment.Status.PENDING.name()).forEach(System.out::println);
    }

    @Test
    void updatePaymentStatus() {
        paymentService.updatePaymentStatus("PAY011225810d3c1f5f1", Payment.Status.COMPLETED.name());
        System.out.println("更新付款狀態成功");
    }
}