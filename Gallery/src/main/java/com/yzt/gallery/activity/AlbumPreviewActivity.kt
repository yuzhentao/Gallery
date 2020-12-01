package com.yzt.gallery.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityOptionsCompat
import com.gyf.immersionbar.ImmersionBar
import com.yzt.gallery.R
import com.yzt.gallery.adapter.AlbumPreviewAdapter
import com.yzt.gallery.bean.AlbumFile
import com.yzt.gallery.key.AlbumKeys
import com.yzt.gallery.util.AlbumViewUtil
import com.yzt.gallery.view.AlbumViewPager
import java.util.*

/**
 * 相册预览
 *
 * @author yzt 2020/4/23
 */
class AlbumPreviewActivity : AppCompatActivity(), View.OnClickListener {

    private val context by lazy {
        this
    }
    private val activity by lazy {
        this
    }

    private fun <T : View> Activity.bindView(@IdRes viewId: Int): Lazy<T> {
        return lazy { findViewById<T>(viewId) }
    }

    private val vp: AlbumViewPager by bindView(R.id.vp)
    private val vSelected: View by bindView(R.id.v_selected)
    private val ivSelected: AppCompatImageView by bindView(R.id.iv_selected)
    private val tvSelected: AppCompatTextView by bindView(R.id.tv_selected)
    private val tvBack: AppCompatTextView by bindView(R.id.tv_back)
    private val tvConfirm: AppCompatTextView by bindView(R.id.tv_confirm)

    private var adapter: AlbumPreviewAdapter? = null
    private var files: MutableList<AlbumFile>? = mutableListOf()
    private var currentFile: AlbumFile? = null

    private var selectedCount = 0
    private var unSelectedNos: MutableList<Int>? = mutableListOf()

    private var showConfirm: Boolean = true

    companion object {

        @JvmStatic
        fun startActivityForResult(activity: Activity, files: MutableList<AlbumFile>, showConfirm: Boolean, requestCode: Int) {
            val intent = Intent(activity, AlbumPreviewActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelableArrayList(AlbumKeys.BUNDLE_BEANS, files as ArrayList<out Parcelable>)
            intent.putExtra(AlbumKeys.INTENT_BUNDLE, bundle)
            intent.putExtra(AlbumKeys.INTENT_SHOW_CONFIRM, showConfirm)
            val options = ActivityOptionsCompat.makeCustomAnimation(activity, R.anim.album_forward_enter_horizontal, R.anim.album_forward_exit_horizontal)
            activity.startActivityForResult(intent, requestCode, options.toBundle())
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album_preview)
        ImmersionBar.with(activity).statusBarDarkFont(true).statusBarColor(R.color.album_black).init()
        initView()
        initData()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.v_selected -> {
                currentFile?.let {
                    it.isSelected = !it.isSelected
                    unSelectedNos!!.clear()
                    var newIndex = 0
                    for (index in files!!.indices) {
                        val file = files!![index]
                        if (file.isSelected) {
                            file.selectedNo = ++newIndex
                        } else {
                            unSelectedNos!!.add(index)
                        }
                    }
                    ivSelected.isSelected = it.isSelected
                    tvSelected.visibility = if (it.isSelected) View.VISIBLE else View.GONE
                    tvSelected.text = it.selectedNo.toString()
                    if (it.isSelected) {
                        selectedCount++
                    } else {
                        selectedCount--
                    }
                    tvConfirm.isEnabled = selectedCount > 0
                    tvConfirm.text = if (selectedCount > 0) getString(R.string.confirm_selected_count, selectedCount) else getString(R.string.confirm)
                }
            }
            R.id.tv_back -> {
                back(false)
            }
            R.id.tv_confirm -> {
                if (showConfirm) {
                    back(true)
                }
            }
        }
    }

    private fun initView() {
        AlbumViewUtil.setMargins(vp, 0, ImmersionBar.getStatusBarHeight(activity), 0, 0)
        vSelected.setOnClickListener(this)
        tvBack.setOnClickListener(this)
        tvConfirm.setOnClickListener(this)
    }

    private fun initData() {
        intent?.let {
            val bundle = it.getBundleExtra(AlbumKeys.INTENT_BUNDLE)
            files = bundle?.getParcelableArrayList(AlbumKeys.BUNDLE_BEANS)
            showConfirm = it.getBooleanExtra(AlbumKeys.INTENT_SHOW_CONFIRM, true)
            tvConfirm.visibility = if (showConfirm) View.VISIBLE else View.GONE
        }
        files?.let {
            selectedCount = it.size
            currentFile = it[0]
            currentFile?.let { itt ->
                ivSelected.isSelected = itt.isSelected
                tvSelected.visibility = if (itt.isSelected) View.VISIBLE else View.GONE
                tvSelected.text = itt.selectedNo.toString()
            }
            adapter = AlbumPreviewAdapter(context, activity, it)
            vp.adapter = adapter
            vp.offscreenPageLimit = it.size
            vp.addOnPageChangeListener(object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(position: Int) {

                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

                }

                override fun onPageSelected(position: Int) {
                    currentFile = it[position]
                    currentFile?.let { itt ->
                        ivSelected.isSelected = itt.isSelected
                        tvSelected.visibility = if (itt.isSelected) View.VISIBLE else View.GONE
                        tvSelected.text = itt.selectedNo.toString()
                    }
                }
            })
            tvConfirm.isEnabled = selectedCount > 0
            tvConfirm.text = if (selectedCount > 0) getString(R.string.confirm_selected_count, selectedCount) else getString(R.string.confirm)
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.album_backward_enter_horizontal, R.anim.album_backward_exit_horizontal)
    }

    private fun back(isConfirm: Boolean) {
        val intent = Intent()
        val bundle = Bundle()
        bundle.putParcelableArrayList(AlbumKeys.BUNDLE_BEANS, files as ArrayList<out Parcelable>)
        intent.putExtra(AlbumKeys.INTENT_BUNDLE, bundle)
        if (showConfirm) {
            intent.putExtra(AlbumKeys.INTENT_IS_CONFIRM, isConfirm)
        }
        setResult(Activity.RESULT_OK, intent)
        onBackPressed()
    }

}