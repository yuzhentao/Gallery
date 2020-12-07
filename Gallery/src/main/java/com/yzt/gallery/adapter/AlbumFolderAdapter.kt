package com.yzt.gallery.adapter

import android.app.Activity
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.yzt.gallery.R
import com.yzt.gallery.bean.AlbumFolder

/**
 * 相册文件夹
 *
 * @author yzt 2020/4/22
 */
class AlbumFolderAdapter(data: MutableList<AlbumFolder>?, activity: Activity?) :
    BaseQuickAdapter<AlbumFolder, BaseViewHolder>(R.layout.item_album_folder, data) {

    private var activity: Activity? = null

    init {
        this.activity = activity
    }

    override fun convert(holder: BaseViewHolder, item: AlbumFolder) {
        val vSelected = holder.getView<View>(R.id.v_selected)
        val iv = holder.getView<AppCompatImageView>(R.id.iv)
        val tvName = holder.getView<AppCompatTextView>(R.id.tv_name)
        val tvCount = holder.getView<AppCompatTextView>(R.id.tv_count)
        vSelected.visibility = if (item.isSelected) View.VISIBLE else View.GONE
        item.firstImagePath?.let {
            activity?.let { activity ->
                if (!activity.isFinishing) {
                    Glide
                        .with(context)
                        .load(it)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(iv)
                }
            }
        }
        tvName.text = item.name
        tvCount.text = context.getString(R.string.count, item.imageNum)
    }

}