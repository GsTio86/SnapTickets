package me.gt.snaptickets.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class IdUtil {

    private static final DateTimeFormatter idDataFormat = DateTimeFormatter.ofPattern("HHmmssSSS");

    private static final String TICKET_PREFIX = "TICKET";
    private static final String ORDER_PREFIX = "ORDER";
    private static final String PAYMENT_PREFIX = "PAY";
    private static final String USER_TICKET_PREFIX = "USER_TICKET";

    /*
     * 產生票券編號
     * 票券編號格式為 TICKET-{timestamp}-{uid}
     * @return 票券編號
     */
    public static String generateTicketId() {
        return String.format("%s%s", TICKET_PREFIX, generateUidWithTimestamp());
    }

    /*
     * 產生訂單編號
     * 訂單編號格式為 ORDER-{timestamp}-{uid}
     * @return 訂單編號
     */
    public static String generateOrderId() {
        return String.format("%s%s", ORDER_PREFIX, generateUidWithTimestamp());
    }

    /*
     * 產生付款編號
     * 付款編號格式為 PAYMENT-{timestamp}-{uid}
     * @return 付款編號
     */
    public static String generatePaymentId() {
        return String.format("%s%s", PAYMENT_PREFIX, generateUidWithTimestamp());
    }

    /*
     * 產生使用者票券編號
     * 使用者票券編號格式為 USER_TICKET-{timestamp}-{uid}
     * @return 使用者票券編號
     */
    public static String generateUserTicketId() {
        return String.format("%s%s", USER_TICKET_PREFIX, generateUidWithTimestamp());
    }

    /*
     * 產生票券代碼
     * 票券代碼格式為 12碼英數字 大寫
     * @return 票券代碼
     */
    public static String generateTicketCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    /*
     * 產生唯一識別碼 包含時間戳記
     * 唯一識別碼格式為 {timestamp}-{uid}
     * @return 唯一識別碼
     */
    public static String generateUidWithTimestamp() {
        String timestamp = idDataFormat.format(LocalDateTime.now());
        String uid = generateUid();
        return String.format("%s%s", timestamp, uid);
    }

    /*
     * 產生唯一識別碼 只取前8碼
     * 唯一識別碼格式為 {uid}
     * @return 唯一識別碼
     */
    public static String generateUid() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }
}
