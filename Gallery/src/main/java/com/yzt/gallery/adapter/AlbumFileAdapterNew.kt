package com.yzt.gallery.adapter

import android.app.Activity
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.yzt.gallery.R
import com.yzt.gallery.bean.AlbumFile
import com.yzt.gallery.key.AlbumFileType
import com.yzt.gallery.repository.LocalMedia

/**
 * 相册文件
 *
 * @author yzt 2020/4/22
 */
class AlbumFileAdapterNew(data: MutableList<LocalMedia>?, activity: Activity?) :
    BaseQuickAdapter<LocalMedia, BaseViewHolder>(R.layout.item_album_file, data), LoadMoreModule {

    private var activity: Activity? = null

    init {
        this.activity = activity
    }

    override fun convert(holder: BaseViewHolder, item: LocalMedia) {
        val itemView = holder.itemView
        val position = holder.layoutPosition
        val iv = holder.getView<AppCompatImageView>(R.id.iv)
        val ivSelected = holder.getView<AppCompatImageView>(R.id.iv_selected)
        val tvSelected = holder.getView<AppCompatTextView>(R.id.tv_selected)
        val ivS = holder.getView<AppCompatImageView>(R.id.iv_s)
        val tvS = holder.getView<AppCompatTextView>(R.id.tv_s)
//        when (item.itemType) {
//            AlbumFileType.SYSTEM_CAMERA.ordinal -> {
//                iv.setBackgroundColor(ContextCompat.getColor(context, R.color.album_white))
//                iv.setImageResource(0)
//                ivSelected.visibility = View.GONE
//                tvSelected.visibility = View.GONE
//                ivS.visibility = View.VISIBLE
//                tvS.visibility = View.VISIBLE
//                ivS.setImageResource(R.drawable.ic_system_camera_green)
//                tvS.setText(R.string.system_camera)
//            }
//            AlbumFileType.SYSTEM_ALBUM.ordinal -> {
//                iv.setBackgroundColor(ContextCompat.getColor(context, R.color.album_white))
//                iv.setImageResource(0)
//                ivSelected.visibility = View.GONE
//                tvSelected.visibility = View.GONE
//                ivS.visibility = View.VISIBLE
//                tvS.visibility = View.VISIBLE
//                ivS.setImageResource(R.drawable.ic_system_album_green)
//                tvS.setText(R.string.system_album)
//            }
//            AlbumFileType.FILE.ordinal -> {
        ivSelected.visibility = View.VISIBLE
//                ivSelected.isSelected = item.isSelected
//                tvSelected.visibility = if (item.isSelected && item.selectedNo > 0) View.VISIBLE else View.GONE
//                tvSelected.text = item.selectedNo.toString()
        ivS.visibility = View.GONE
        tvS.visibility = View.GONE
        item.path?.let {
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
//            }
//            else -> {
//
//            }
//        }
    }

}