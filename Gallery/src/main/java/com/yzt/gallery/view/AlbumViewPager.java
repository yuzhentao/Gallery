package com.yzt.gallery.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

/**
 * 自定义ViewPager
 * 解决android.support.v4.view.ViewPager.performDrag的问题
 * 解决java.lang.IllegalArgumentException: pointerIndex out of range的问题
 * 可设置是否滑动
 */
public class AlbumViewPager extends ViewPager {

    public boolean canScroll = true;//是否可以滑动

    public AlbumViewPager(Context context) {
        super(context);
    }

    public AlbumViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return this.canScroll && (getCurrentItem() != 0 || getChildCount() != 0) && super.onTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return this.canScroll && (getCurrentItem() != 0 || getChildCount() != 0) && super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setCanScroll(boolean canScroll) {
        this.canScroll = canScroll;
    }

}