package com.yzt.gallery.listener;

import android.view.View;

/**
 * 双击事件
 *
 * @author yzt 2020/2/22
 */
public abstract class AlbumDoubleClickListener implements View.OnClickListener {

    private static final long DOUBLE_TIME = 1000;
    private static long lastClickTime = 0;

    @Override
    public void onClick(View v) {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - lastClickTime < DOUBLE_TIME) {
            onDoubleClick(v);
        }
        lastClickTime = currentTimeMillis;
    }

    public abstract void onDoubleClick(View v);

}