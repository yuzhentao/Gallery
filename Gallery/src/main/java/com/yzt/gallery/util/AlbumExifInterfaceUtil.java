package com.yzt.gallery.util;

import androidx.exifinterface.media.ExifInterface;

import java.io.IOException;

public class AlbumExifInterfaceUtil {

    /**
     * 解决调用系统相机照片会自动旋转的问题
     */
    public static int getDegree(String imagePath) {
        int imageDegree = 0;
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            switch (ori) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    imageDegree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    imageDegree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    imageDegree = 270;
                    break;
                default:
                    imageDegree = 0;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageDegree;
    }

}