package com.yzt.gallery.util;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * RecyclerView
 *
 * @author yzt 2019/12/5
 */
public class AlbumRecyclerViewUtil {

    public static void config(RecyclerView.LayoutManager llm, RecyclerView rv) {
        if (llm != null && rv != null) {
            if (llm instanceof LinearLayoutManager) {
                ((LinearLayoutManager) llm).setSmoothScrollbarEnabled(true);
            }
            rv.setLayoutManager(llm);
            rv.setHasFixedSize(true);
            rv.setNestedScrollingEnabled(false);
            rv.setOnFlingListener(null);
        }
    }

}