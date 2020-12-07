package com.yzt.gallery.util;

import android.os.Build;

public class AlbumSdkVersionUtil {

    public static boolean checkedAndroid_Q() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    }

    public static boolean checkedAndroid_R() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R;
    }

}