package me.gt.snaptickets.service;

import me.gt.snaptickets.model.Payment;
import me.gt.snaptickets.model.UserTicket;

import java.util.List;

public interface UserTicketService {

    /**
     * 給予使用者票券
     * @param payment 付款資訊
     */
    void giveUserTicket(Payment payment);

    /**
     * 透過使用者票券編號取得使用者票券
     * @param userticketid
     * @return 使用者票券
     */
    UserTicket getUserTicketByUserTicketId(String userticketid);


    /**
     * 透過使用者帳號取得使用者票券
     * @param username
     * @return 使用者票券
     */
    List<UserTicket> getUserTicketByUsername(String username);

    /**
     * 透過訂單編號取得使用者票券
     * @param orderid
     * @return
     */
    List<UserTicket> getUserTicketByOrderId(String orderid);

    /**
     * 更新使用者票券代碼
     * @param userticketid 使用者票券編號
     * @param code 票券代碼
     * @return 更新筆數
     */
    ActionStatus updateUserTicketCode(String userticketid, String code);

    /**
     * 更新使用者票券狀態
     * @param userticketid 使用者票券編號
     * @param status 使用者票券狀態
     * @return 更新筆數
     */
    ActionStatus updateUserTicketStatus(String userticketid, UserTicket.Status status);

    /**
     * 刪除使用者票券
     * @param userticketid 使用者票券編號
     * @return 刪除筆數
     */
    ActionStatus deleteUserTicket(String userticketid);

    /**
     * 動作狀態
     */
    enum ActionStatus {
        SUCCESS,
        FAIL
    }
}
