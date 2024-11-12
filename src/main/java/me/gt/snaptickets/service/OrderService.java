package me.gt.snaptickets.service;

import me.gt.snaptickets.model.Order;

import java.util.List;

public interface OrderService {

    /**
     * 新建訂單
     *
     * @param order 訂單
     *
     * @return 訂單資料
     */
    ActionStatus createOrder(Order order);


    /**
     * 透過訂單ID查詢訂單
     *
     * @param orderId 訂單ID
     * @return 訂單
     */
    Order getOrderByOrderId(String orderId);


    /**
     * 取得所有訂單
     *
     * @return 訂單列表
     */
    List<Order> getAllOrders();


    /**
     * 透過訂單狀態查詢訂單
     *
     * @param status 訂單狀態
     * @return 訂單列表
     */
    List<Order> getOrdersByStatus(Order.Status status);


    /**
     * 透過使用者名稱查詢訂單
     * @param username
     * @return 訂單列表
     */
    List<Order> getOrdersByUsername(String username);

    /**
     * 更新訂單
     *
     * @param id 訂單ID
     * @param status 訂單狀態
     *
     * @return 更新狀態
     */
    ActionStatus updateOrderStatus(String id, Order.Status status);


    /**
     * 刪除訂單
     *
     * @param orderId 訂單ID
     */
    void deleteOrder(String orderId);

    /**
     * 訂單狀態
     */
    enum ActionStatus {
        NOT_FOUND,
        STOCK_NOT_ENOUGH,
        SERVER_BUSY,
        CREATE_SUCCESS,
        UPDATE_SUCCESS,
        UPDATE_FAILED,
    }

}
