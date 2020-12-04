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
import com.yzt.gallery.repository.LocalMediaFolder

/**
 * 相册文件夹
 *
 * @author yzt 2020/4/22
 */
class AlbumFolderAdapterNew(data: MutableList<LocalMediaFolder>?, activity: Activity?) :
    BaseQuickAdapter<LocalMediaFolder, BaseViewHolder>(R.layout.item_album_folder, data) {

    private var activity: Activity? = null

    init {
        this.activity = activity
    }

    override fun convert(holder: BaseViewHolder, item: LocalMediaFolder) {
        val itemView = holder.itemView
        val position = holder.layoutPosition
        val vSelected = holder.getView<View>(R.id.v_selected)
        val iv = holder.getView<AppCompatImageView>(R.id.iv)
        val tvName = holder.getView<AppCompatTextView>(R.id.tv_name)
        val tvCount = holder.getView<AppCompatTextView>(R.id.tv_count)
        vSelected.visibility = if (item.isChecked) View.VISIBLE else View.GONE
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