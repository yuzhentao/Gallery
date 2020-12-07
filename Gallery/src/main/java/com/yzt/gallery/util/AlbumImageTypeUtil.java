package com.yzt.gallery.util;

import android.text.TextUtils;

import java.io.File;

public final class AlbumImageTypeUtil {

    private final static String MIME_TYPE_PNG = "image/png";
    private final static String MIME_TYPE_JPEG = "image/jpeg";
    private final static String MIME_TYPE_JPG = "image/jpg";
    private final static String MIME_TYPE_BMP = "image/bmp";
    private final static String MIME_TYPE_WEBP = "image/webp";
    private final static String MIME_TYPE_GIF = "image/gif";

    public static String ofPNG() {
        return MIME_TYPE_PNG;
    }

    public static String ofJPEG() {
        return MIME_TYPE_JPEG;
    }

    public static String ofJPG() {
        return MIME_TYPE_JPG;
    }

    public static String ofBMP() {
        return MIME_TYPE_BMP;
    }

    public static String ofGIF() {
        return MIME_TYPE_GIF;
    }

    public static String ofWEBP() {
        return MIME_TYPE_WEBP;
    }

    public static boolean isGif(String mimeType) {
        return mimeType != null && (mimeType.equals("image/gif") || mimeType.equals("image/GIF"));
    }

    public static String getImageMimeType(String path) {
        try {
            if (!TextUtils.isEmpty(path)) {
                File file = new File(path);
                String fileName = file.getName();
                int beginIndex = fileName.lastIndexOf(".");
                String temp = beginIndex == -1 ? "jpeg" : fileName.substring(beginIndex + 1);
                return "image/" + temp;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return MIME_TYPE_JPEG;
        }
        return MIME_TYPE_JPEG;
    }

    public static boolean isContent(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        return url.startsWith("content://");
    }

}