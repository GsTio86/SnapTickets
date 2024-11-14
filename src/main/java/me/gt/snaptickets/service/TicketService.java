package me.gt.snaptickets.service;

import me.gt.snaptickets.model.Ticket;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TicketService {

    /**
     * 新建票券
     *
     * @param ticket 票券
     *
     */
    void createTicket(Ticket ticket);

    /**
     * 透過票券ID查詢票券
     *
     * @param ticketId 票券ID
     * @return 票券
     *
     */
    Ticket getByTicketId(String ticketId);

    /**
     * 取得所有票券
     *
     * @return 票券列表
     *
     */
    List<Ticket> getAllTickets();

    /**
     * 透過票券名稱查詢票券
     *
     * @param name 票券名稱
     * @return 票券列表
     *
     */
    List<Ticket> getTicketsByName(String name);

    /**
     * 取得所有可買票券 (尚未售完)
     *
     * @return 票券列表
     *
     */
    List<Ticket> getAvailableTickets();

    /**
     * 取得所有已售完票券 (已售完)
     *
     * @return 票券列表
     *
     */
    List<Ticket> getSoldOutTickets();

    /**
     * 取得所有未來票券 (活動日期在今天之後)
     *
     * @param eventDate 活動日期 (選填)
     *
     * @return 票券列表
     *
     */
    List<Ticket> getUpcomingTickets(Optional<LocalDateTime> eventDate);

    /**
     * 更新票券
     *
     * @param ticket 票券
     *
     * @return 更新狀態
     */
    ActionStatus updateTicket(Ticket ticket);

    /**
     * 更新票券庫存
     *
     * @param ticketId 票券ID
     * @param quantity 數量
     *
     */
    void updateTicketStock(String ticketId, int quantity);

    /**
     * 增加票券庫存
     *
     * @param ticketId 票券ID
     * @param quantity 數量
     *
     */
    void increaseStock(String ticketId, int quantity);

    /**
     * 減少票券庫存
     *
     * @param ticketId 票券ID
     * @param quantity 數量
     *
     */
    void reduceStock(String ticketId, int quantity);

    /**
     * 刪除票券
     *
     * @param ticketId 票券ID
     *
     * @return 刪除狀態
     */
    ActionStatus deleteTicket(String ticketId);

    /**
     * 更新票券圖片
     *
     * @param id    票券ID
     * @param bytes 圖片位元組
     *
     * @return 更新狀態
     */
    ActionStatus updateImage(String id, byte[] bytes);

    /**
     * 票券狀態
     */
    enum ActionStatus {
        NOT_FOUND,
        AVAILABLE,
        SOLD_OUT,
        CREATE_SUCCESS,
        UPDATE_SUCCESS,
        DELETE_SUCCESS,
        CREATE_FAILED,
        UPDATE_FAILED,
        DELETE_FAILED
    }

}
