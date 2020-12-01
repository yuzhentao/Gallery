package com.yzt.gallery.util;

import android.content.Context;
import androidx.annotation.DimenRes;

/**
 * Resources
 */
public class AlbumResourcesUtil {

    public static int getMipmapId(Context context, String name) {
        return context.getResources().getIdentifier(name, "mipmap", context.getPackageName());
    }

    public static int getLayoutId(Context context, String name) {
        return context.getResources().getIdentifier(name, "layout", context.getPackageName());
    }

    public static int getStringId(Context context, String name) {
        return context.getResources().getIdentifier(name, "string", context.getPackageName());
    }

    public static int getDrawableId(Context context, String name) {
        return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
    }

    public static int getStyleId(Context context, String name) {
        return context.getResources().getIdentifier(name, "style", context.getPackageName());
    }

    public static int getId(Context context, String name) {
        return context.getResources().getIdentifier(name, "id", context.getPackageName());
    }

    public static int getColorId(Context context, String name) {
        return context.getResources().getIdentifier(name, "color", context.getPackageName());
    }

    public static int getArrayId(Context context, String name) {
        return context.getResources().getIdentifier(name, "array", context.getPackageName());
    }

    public static float getDimension(Context context, @DimenRes int resId) {
        return context.getResources().getDimension(resId);
    }

    public static int getDimensionPixelOffset(Context context, @DimenRes int resId) {
        return context.getResources().getDimensionPixelOffset(resId);
    }

    public static int getResIDFromName(Context context, String type, String name) {
        return name != null ? context.getResources().getIdentifier(name, type, context.getPackageName()) : 0;
    }

}