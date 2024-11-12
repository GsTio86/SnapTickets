package me.gt.snaptickets.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import me.gt.snaptickets.dto.OrderDto;
import me.gt.snaptickets.dto.PaymentSearchDTO;
import me.gt.snaptickets.model.Payment;

import java.util.Hashtable;
import java.util.List;

public interface PaymentService {

    /**
     * 建立付款
     *
     * @param orderDto 訂單
     *
     * @return 付款物件
     */
    String createPayment(OrderDto orderDto);


    /**
     * 透過付款ID查詢付款
     *
     * @param paymentId 付款ID
     * @return 付款
     */
    Payment getByPaymentId(String paymentId);

    /**
     * 透過訂單ID查詢付款
     *
     * @param orderId 訂單ID
     * @return 付款
     */
    Payment getPaymentByOrderId(String orderId);

    /**
     * 取得所有付款
     *
     * @return 付款列表
     */
    List<Payment> getAll();


    /**
     * 透過付款狀態查詢付款
     *
     * @param status 付款狀態
     * @return 付款列表
     */
    List<Payment> getAllByPaymentStatus(String status);

    /**
     * 透過付款方式查詢付款
     *
     * @param type 付款方式
     * @return 付款列表
     */
    List<Payment> getAllByPaymentMethod(String type);

    /**
     * 更新付款狀態
     *
     * @param paymentId 付款編號
     * @param status 付款狀態
     */
    void updatePaymentStatus(String paymentId, Payment.Status status);

    /**
     * 更新付款
     *
     * @param paymentId 付款編號
     * @param status 付款狀態
     */
    void updatePaymentStatus(String paymentId, String status);

    /**
     * 將交易資訊格式化成JSON
     *
     * @param tradeInfo 交易資訊
     *
     * @return JSON格式的交易資訊
     */
    String tradeInfoFormatJson(String tradeInfo) throws JsonProcessingException;

    /**
     * 透過搜尋條件查詢付款
     *
     * @param searchDTO 搜尋條件
     * @return 付款列表
     */
    List<Payment> getPaymentsBySearch(PaymentSearchDTO searchDTO);

    /**
     * 刪除付款
     *
     * @param id 付款ID
     */
    void deletePayment(String id);

    /**
     * 處理付款結果
     *
     * @param hashtable 付款結果
     * @return 付款結果
     */
    PaymentResult processPaymentResult(Hashtable<String, String> hashtable);

    /**
     * 付款結果
     *
     * @param statusCode 狀態碼
     * @param message 訊息
     */
    record PaymentResult(long statusCode, String message) {}
}
