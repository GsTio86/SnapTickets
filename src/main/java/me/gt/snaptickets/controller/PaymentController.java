package me.gt.snaptickets.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import ecpay.payment.integration.AllInOne;
import ecpay.payment.integration.domain.QueryTradeInfoObj;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.gt.snaptickets.dto.PaymentSearchDTO;
import me.gt.snaptickets.model.Payment;
import me.gt.snaptickets.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@Tag(name = "付款 API", description = "處理付款完成的金流操作")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;


    @Operation(summary = "查詢付款資料")
    @GetMapping(value = "/admin/payment/info/{id}")
    public ResponseEntity<String> getTradeInfo(@PathVariable("id") String paymentId) {
        AllInOne aio = new AllInOne("");
        QueryTradeInfoObj queryTradeInfoObj = new QueryTradeInfoObj();
        queryTradeInfoObj.setMerchantTradeNo(paymentId);

        String tradeInfo = aio.queryTradeInfo(queryTradeInfoObj);
        try {
            return ResponseEntity.ok(paymentService.tradeInfoFormatJson(tradeInfo));
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("錯誤|" + e.getMessage());
        }
    }

    @Operation(summary = "查詢所有付款資料")
    @GetMapping(value = "/admin/payment/all")
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAll());
    }

    @Operation(summary = "查詢指定狀態的付款資料")
    @GetMapping(value = "/admin/payment/status/{status}")
    public ResponseEntity<List<Payment>> getAllByPaymentStatus(@PathVariable("status") Payment.Status status) {
        return ResponseEntity.ok(paymentService.getAllByPaymentStatus(status.getName()));
    }


    @Operation(summary = "查詢指定付款方式的付款資料")
    @GetMapping(value = "/admin/payment/method/{method}")
    public ResponseEntity<List<Payment>> getAllByPaymentMethod(@PathVariable("method") String method) {
        return ResponseEntity.ok(paymentService.getAllByPaymentMethod(method));
    }

    @Operation(summary = "查詢指定訂單編號的付款資料")
    @GetMapping(value = "/admin/payment/order/{orderId}")
    public ResponseEntity<Payment> getPaymentByOrderId(@PathVariable("orderId") String orderId) {
        return ResponseEntity.ok(paymentService.getPaymentByOrderId(orderId));
    }

    @Operation(summary = "查詢指定條件的付款資料")
    @GetMapping(value = "/admin/payment/search")
    public ResponseEntity<List<Payment>> searchPayments(
            @RequestParam(value = "paymentId", required = false) String paymentId,
            @RequestParam(value = "orderId", required = false) String orderId,
            @RequestParam(value = "method", required = false) String method,
            @RequestParam(value = "status", required = false) Payment.Status status) {
        PaymentSearchDTO searchDTO = new PaymentSearchDTO(paymentId, orderId, method, status);
        return ResponseEntity.ok(paymentService.getPaymentsBySearch(searchDTO));
    }

    @Operation(summary = "更新付款資料狀態")
    @PutMapping(value = "/admin/payment/update/{id}")
    public ResponseEntity<String> updatePaymentStatus(@PathVariable("id") String id, @RequestParam("status") Payment.Status status) {
        try {
            paymentService.updatePaymentStatus(id, status.name());
            return ResponseEntity.ok("付款資料更新成功");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("錯誤|付款資料更新失敗");
        }
    }

    @Operation(summary = "刪除付款資料")
    @DeleteMapping(value = "/admin/payment/delete/{id}")
    public ResponseEntity<String> deletePayment(@PathVariable("id") String id) {
        try {
            paymentService.deletePayment(id);
            return ResponseEntity.ok("付款資料刪除成功");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("錯誤|付款資料刪除失敗");
        }
    }

    @Operation(summary = "處理付款結果")
    @PostMapping(value = "/payment/result", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> doPaymentResult(@RequestParam Map<String, String> result) {
        PaymentService.PaymentResult paymentResult = paymentService.processPaymentResult(new Hashtable<>(result));
        if (paymentResult.statusCode() == 1 || paymentResult.statusCode() == 2) {
            return ResponseEntity.ok("1|OK");
        } else {
            return ResponseEntity.badRequest().body("錯誤|" + paymentResult);
        }
    }


    @Operation(summary = "處理顯示給使用者的付款結果")
    @PostMapping(value = "/payment/client", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView doClientPaymentResult(@RequestParam Map<String, String> result) {
        PaymentService.PaymentResult paymentResult = paymentService.processPaymentResult(new Hashtable<>(result));
        ModelAndView model = new ModelAndView("付款結果");
        model.addObject("statusCode", paymentResult.statusCode());
        model.addObject("message", paymentResult.message());
        if (result.containsKey("MerchantTradeNo")) {
            model.addObject("paymentId", result.get("MerchantTradeNo"));
        }
        if (result.containsKey("CustomField1")) {
            model.addObject("orderId", result.get("CustomField1"));
        }
        return model;
    }

}
