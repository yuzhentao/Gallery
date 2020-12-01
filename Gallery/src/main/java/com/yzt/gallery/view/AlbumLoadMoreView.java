package com.yzt.gallery.view;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IntDef;

import com.chad.library.adapter.base.loadmore.BaseLoadMoreView;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.yzt.gallery.R;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 加载更多
 *
 * @author yzt 2020/6/18
 */
public class AlbumLoadMoreView extends BaseLoadMoreView {

    public static final int LOAD_MORE_VERTICAL = 0;
    public static final int LOAD_MORE_HORIZONTAL = 1;

    @IntDef({
            LOAD_MORE_VERTICAL,
            LOAD_MORE_HORIZONTAL}
    )
    @Retention(RetentionPolicy.SOURCE)
    @interface LOAD_MORE_ORIENTATION {

    }

    private static final int[] LOAD_MORE_LAYOUT_ID = new int[]{
            R.layout.layout_album_load_more_vertical,
            R.layout.layout_album_load_more_horizontal};

    private int orientation;

    public AlbumLoadMoreView(@LOAD_MORE_ORIENTATION int orientation) {
        this.orientation = orientation;
    }

    @NotNull
    @Override
    public View getLoadComplete(@NotNull BaseViewHolder baseViewHolder) {
        return baseViewHolder.findView(R.id.cl_complete);
    }

    @NotNull
    @Override
    public View getLoadEndView(@NotNull BaseViewHolder baseViewHolder) {
        return baseViewHolder.findView(R.id.cl_end);
    }

    @NotNull
    @Override
    public View getLoadFailView(@NotNull BaseViewHolder baseViewHolder) {
        return baseViewHolder.findView(R.id.cl_fail);
    }

    @NotNull
    @Override
    public View getLoadingView(@NotNull BaseViewHolder baseViewHolder) {
        return baseViewHolder.findView(R.id.cl_loading);
    }

    @NotNull
    @Override
    public View getRootView(@NotNull ViewGroup viewGroup) {
        return View.inflate(viewGroup.getContext(), LOAD_MORE_LAYOUT_ID[orientation], null);
    }

}