package com.yzt.gallery.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

/**
 * 尺寸工具
 *
 * @author yzt 2020/2/3
 */
public class AlbumDimenUtil {

    public static int px2dp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5F);
    }

    public static int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5F);
    }

    public static int px2sp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (px / scale + 0.5F);
    }

    public static int sp2px(Context context, float sp) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * scale + 0.5F);
    }

    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static int sp2px(Context context, int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    public static int getViewHeight(View view, boolean isHeight) {
        int result;
        if (view == null) {
            return 0;
        }
        if (isHeight) {
            int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            view.measure(h, 0);
            result = view.getMeasuredHeight();
        } else {
            int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            view.measure(0, w);
            result = view.getMeasuredWidth();
        }
        return result;
    }

    public static int getWidthOrHeight(Activity activity, boolean isWidth) {
        DisplayMetrics dm = new DisplayMetrics();
        if (activity != null
                && activity.getWindowManager() != null
                && activity.getWindowManager().getDefaultDisplay() != null) {
            activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
            if (isWidth) {
                return dm.widthPixels;
            } else {
                return dm.heightPixels;
            }
        } else {
            return 0;
        }
    }

    public static int getWidthOrHeight(Context context, boolean isWidth) {
        if (context != null
                && context.getResources() != null
                && context.getResources().getDisplayMetrics() != null) {
            Resources resources = context.getResources();
            DisplayMetrics dm = resources.getDisplayMetrics();
            if (isWidth) {
                return dm.widthPixels;
            } else {
                return dm.heightPixels;
            }
        } else {
            return 0;
        }
    }

    public static int getDpi(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        if (activity != null
                && activity.getWindowManager() != null
                && activity.getWindowManager().getDefaultDisplay() != null) {
            activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
            return dm.densityDpi;
        } else {
            return 0;
        }
    }

}