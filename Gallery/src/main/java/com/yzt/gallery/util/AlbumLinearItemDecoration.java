package com.yzt.gallery.util;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AlbumLinearItemDecoration extends RecyclerView.ItemDecoration {

    private int spacing;
    private boolean includeEdge;//包不包含边缘

    public AlbumLinearItemDecoration(int spacing, boolean includeEdge) {
        this.spacing = spacing;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        if (parent.getAdapter() != null) {
            int count = parent.getAdapter().getItemCount();
            if (includeEdge) {
                outRect.bottom = spacing;
            } else {
                if (position != count - 1) {
                    outRect.bottom = spacing;
                }
            }
        }
    }
}