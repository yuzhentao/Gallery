package com.yzt.gallery.util;

import java.text.SimpleDateFormat;

public class AlbumDateUtil {

    private static final SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd_HHmmssSS");

    /**
     * 判断两个时间戳相差多少秒
     */
    public static int dateDiffer(long d) {
        try {
            long l1 = ValueOf.toLong(String.valueOf(System.currentTimeMillis()).substring(0, 10));
            long interval = l1 - d;
            return (int) Math.abs(interval);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


    /**
     * 根据时间戳创建文件名
     */
    public static String getCreateFileName(String prefix) {
        long millis = System.currentTimeMillis();
        return prefix + sf.format(millis);
    }

}