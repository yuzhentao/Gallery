package com.yzt.gallery.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Bitmap工具
 *
 * @author yzt 2019/12/31
 */
public class AlbumBitmapUtil {

    public static Bitmap rotateBitmap(Bitmap bitmap, float degrees, boolean isRecycle) {
        if (degrees == 0 || null == bitmap) {
            return bitmap;
        }
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees, bitmap.getWidth() / 2.F, bitmap.getHeight() / 2.F);
        Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (isRecycle) {
            bitmap.recycle();
        }
        return bmp;
    }

}