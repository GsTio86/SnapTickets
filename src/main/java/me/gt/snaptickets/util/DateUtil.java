package me.gt.snaptickets.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    public static final DateTimeFormatter standardDataFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    /**
     * 取得當前時間 格式為 yyyy/MM/dd HH:mm:ss
     *
     * @return 當前時間
     */
    public static String getCurrentTime() {
        return standardDataFormat.format(LocalDateTime.now());
    }

}
