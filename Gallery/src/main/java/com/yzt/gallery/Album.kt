package com.yzt.gallery

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.core.app.ActivityOptionsCompat
import com.yzt.gallery.activity.AlbumActivity

/**
 * 相册
 *
 * @author yzt 2020/3/5
 */
class Album private constructor() {

    var albumContext: Context? = null
    var maxSelectedCount: Int? = null
    var hasSystemCamera: Boolean? = null
    var hasSystemAlbum: Boolean? = null

    companion object {

        @JvmStatic
        fun get(): Album? {
            return ContextHolder.holder!!

        }

        //        val instance = ContextHolder.holder//TODO 这种写法Kotlin可以使用但是Java不能

    }

    private object ContextHolder {
        var holder: Album? = Album()
    }

    fun setContext(context: Context?): Album {
        albumContext = context
        return ContextHolder.holder!!
    }

    fun getContext(): Context? {
        return albumContext
    }

    fun maxSelectedCount(maxSelectedCount: Int): Album {
        this.maxSelectedCount = maxSelectedCount
        return ContextHolder.holder!!
    }

    fun hasSystemCamera(hasSystemCamera: Boolean): Album {
        this.hasSystemCamera = hasSystemCamera
        return ContextHolder.holder!!
    }

    fun hasSystemAlbum(hasSystemAlbum: Boolean): Album {
        this.hasSystemAlbum = hasSystemAlbum
        return ContextHolder.holder!!
    }

    fun startActivityForResult(activity: Activity, requestCode: Int): Album {
        val intent = Intent(activity, AlbumActivity::class.java)
        val options = ActivityOptionsCompat.makeCustomAnimation(activity, R.anim.album_forward_enter_horizontal, R.anim.album_forward_exit_horizontal)
        activity.startActivityForResult(intent, requestCode, options.toBundle())
        return ContextHolder.holder!!
    }

    fun startActivityForResult(activity: Activity, fragment: androidx.fragment.app.Fragment, requestCode: Int): Album {
        val intent = Intent(activity, AlbumActivity::class.java)
        val options = ActivityOptionsCompat.makeCustomAnimation(activity, R.anim.album_forward_enter_horizontal, R.anim.album_forward_exit_horizontal)
        fragment.startActivityForResult(intent, requestCode, options.toBundle())
        return ContextHolder.holder!!
    }

}