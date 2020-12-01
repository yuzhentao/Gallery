package com.yzt.gallery.util;

import java.util.List;

/**
 * NullUtil
 */
public class AlbumNullUtil {

    public static <T> boolean isListNotNull(List<T> list) {
        return list != null && list.size() > 0;
    }

    public static <T> boolean isStringArrayNotNull(String[] array) {
        return array != null && array.length > 0;
    }

    public static <T> boolean isIntArrayNotNull(int[] array) {
        return array != null && array.length > 0;
    }

}