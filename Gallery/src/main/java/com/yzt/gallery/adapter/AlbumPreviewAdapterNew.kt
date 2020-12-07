package com.yzt.gallery.adapter

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.yzt.gallery.R
import com.yzt.gallery.bean.LocalMedia
import com.yzt.gallery.view.photoview.PhotoView

/**
 * 相册预览
 *
 * @author yzt 2020/4/23
 */
class AlbumPreviewAdapterNew(context: Context?, activity: Activity?, beans: MutableList<LocalMedia>?) : PagerAdapter() {

    private var context: Context? = null
    private var activity: Activity? = null
    private var beans: MutableList<LocalMedia>? = null

    init {
        this.context = context
        this.activity = activity
        this.beans = beans
    }

    override fun getCount(): Int {
        beans?.let {
            if (it.size > 0) {
                return it.size
            }
        }
        return 0
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val bean: LocalMedia? = beans!![position]
        val view = View.inflate(context, R.layout.item_album_preview, null)
        val pv: PhotoView = view!!.findViewById(R.id.pv)
        bean?.let {
            if (!it.path.isNullOrBlank()) {
                activity?.let { activity ->
                    if (!activity.isFinishing) {
                        Glide
                                .with(context!!)
                                .load(it.path)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(pv)
                    }
                }
            }
        }
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

}