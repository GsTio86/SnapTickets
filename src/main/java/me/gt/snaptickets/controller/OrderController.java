package me.gt.snaptickets.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.gt.snaptickets.dto.OrderDto;
import me.gt.snaptickets.exception.CreatePaymentException;
import me.gt.snaptickets.model.Order;
import me.gt.snaptickets.service.OrderService;
import me.gt.snaptickets.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@Tag(name = "訂單 API", description = "處理訂單的操作")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

    @Operation(summary = "結帳")
    @PostMapping("/order/checkout")
    public ResponseEntity<String> createPaymentForm(@RequestBody OrderDto orderDto) {
        try {
            String form = paymentService.createPayment(orderDto);
            return ResponseEntity.ok(form);
        } catch (CreatePaymentException e) {
            return ResponseEntity.badRequest().body("錯誤|訂單建立失敗");
        }
    }

    @Operation(summary = "查詢所有訂單")
    @GetMapping("/admin/order/all")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @Operation(summary = "查詢訂單")
    @GetMapping("/admin/order/info/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable("id") String id) {
        return ResponseEntity.ok(orderService.getOrderByOrderId(id));
    }

    @Operation(summary = "更新訂單狀態")
    @PostMapping("/admin/order/update/{id}")
    public ResponseEntity<String> updateOrderStatus(@PathVariable("id") String id, @RequestBody Order.Status status) {
        OrderService.ActionStatus action = orderService.updateOrderStatus(id, status);
        if (action == OrderService.ActionStatus.UPDATE_SUCCESS) {
            return ResponseEntity.ok("更新成功");
        } else {
            return ResponseEntity.badRequest().body("更新失敗");
        }
    }

    @Operation(summary = "刪除訂單")
    @DeleteMapping("/admin/order/delete/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable("id") String id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok("刪除成功");
    }

}
