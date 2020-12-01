package com.yzt.gallery.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Method;
import java.util.Locale;

/**
 * View
 */
public class AlbumViewUtil {

    public static SparseIntArray widthSA = new SparseIntArray();
    public static SparseIntArray heightSA = new SparseIntArray();

    public static void visible(View view) {
        if (view == null) {
            return;
        }
        view.setVisibility(View.VISIBLE);
    }

    public static void invisible(View view) {
        if (view == null) {
            return;
        }
        view.setVisibility(View.INVISIBLE);
    }

    public static void gone(View view) {
        if (view == null) {
            return;
        }
        view.setVisibility(View.GONE);
    }

    /**
     * 获取顶部状态栏的高度
     *
     * @param context：Context
     * @return int
     */
    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourcesId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourcesId);
    }

    /**
     * 获取底部导航栏的高度
     *
     * @param context：Context
     * @return int
     */
    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    /**
     * 是否存在虚拟导航栏
     *
     * @param context：Context
     * @return boolean
     */
    @SuppressLint("PrivateApi")
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hasNavigationBar;
    }

    /**
     * 判断是否平板设备
     *
     * @param context Context
     * @return boolean
     */
    public static boolean isTabletDevice(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static void setMargins(View view, int l, int t, int r, int b) {
        if (view != null
                && view.getLayoutParams() != null
                && view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            if (isRtl()) {
                mlp.setMargins(r, t, l, b);
            } else {
                mlp.setMargins(l, t, r, b);
            }
            view.requestLayout();
        }
    }

    public static void setMargins(Context context, View view, int l, int t, int r, int b) {
        int newL = l == 0 ? 0 : AlbumResourcesUtil.getDimensionPixelOffset(context, l);
        int newT = t == 0 ? 0 : AlbumResourcesUtil.getDimensionPixelOffset(context, t);
        int newR = r == 0 ? 0 : AlbumResourcesUtil.getDimensionPixelOffset(context, r);
        int newB = b == 0 ? 0 : AlbumResourcesUtil.getDimensionPixelOffset(context, b);
        if (view != null
                && view.getLayoutParams() != null
                && view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            if (isRtl()) {
                mlp.setMargins(newR, newT, newL, newB);
            } else {
                mlp.setMargins(newL, newT, newR, newB);
            }
            view.requestLayout();
        }
    }

    public static void setPadding(View view, int l, int t, int r, int b) {
        if (view != null) {
            if (isRtl()) {
                view.setPadding(r, t, l, b);
            } else {
                view.setPadding(l, t, r, b);
            }
        }
    }

    public static void setPadding(Context context, View view, int l, int t, int r, int b) {
        int newL = l == 0 ? 0 : AlbumResourcesUtil.getDimensionPixelOffset(context, l);
        int newT = t == 0 ? 0 : AlbumResourcesUtil.getDimensionPixelOffset(context, t);
        int newR = r == 0 ? 0 : AlbumResourcesUtil.getDimensionPixelOffset(context, r);
        int newB = b == 0 ? 0 : AlbumResourcesUtil.getDimensionPixelOffset(context, b);
        if (view != null) {
            if (isRtl()) {
                view.setPadding(newR, newT, newL, newB);
            } else {
                view.setPadding(newL, newT, newR, newB);
            }
        }
    }

    public static void setWidthAndHeight(View view, int w, int h) {
        if (view != null
                && view.getLayoutParams() != null) {
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            lp.width = w;
            lp.height = h;
            view.requestLayout();
        }
    }

    public static void getWidthAndHeight(final View view, final int viewId) {
        if (view != null) {
            view.post(() -> {
                widthSA.put(viewId, view.getWidth());
                heightSA.put(viewId, view.getHeight());
            });
        }
    }

    private static boolean isRtl() {
        return TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == View.LAYOUT_DIRECTION_RTL;
    }

}