package com.yzt.gallery.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.ImageView
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.chad.library.adapter.base.BaseQuickAdapter
import com.gyf.immersionbar.ImmersionBar
import com.tbruyelle.rxpermissions2.Permission
import com.tbruyelle.rxpermissions2.RxPermissions
import com.yzt.gallery.Album
import com.yzt.gallery.R
import com.yzt.gallery.adapter.AlbumFileAdapter
import com.yzt.gallery.bean.AlbumFile
import com.yzt.gallery.fragment.AlbumFolderFragment
import com.yzt.gallery.key.AlbumConstants
import com.yzt.gallery.key.AlbumFileType
import com.yzt.gallery.key.AlbumKeys
import com.yzt.gallery.listener.AlbumDoubleClickListener
import com.yzt.gallery.util.*
import com.yzt.gallery.view.AlbumLoadMoreView
import com.yzt.gallery.viewModel.AlbumViewModel
import com.yzt.gallery.viewModel.AlbumViewModelFactory
import io.reactivex.disposables.Disposable
import java.io.File
import java.io.FileOutputStream

/**
 * 相册
 *
 * @author yzt 2020/4/22
 */
class AlbumActivity : AppCompatActivity(), View.OnClickListener {

    private val context by lazy {
        this
    }
    private val activity by lazy {
        this
    }

    private fun <T : View> Activity.bindView(@IdRes viewId: Int): Lazy<T> {
        return lazy { findViewById(viewId) }
    }

    private val dl: androidx.drawerlayout.widget.DrawerLayout by bindView(R.id.dl)
    private val top: ConstraintLayout by bindView(R.id.top)
    private val vLeft: View by bindView(R.id.v_top_left)
    private val tvCenter: AppCompatTextView by bindView(R.id.tv_top_center)
    private val vRight: View by bindView(R.id.v_top_right)
    private val ivRight: AppCompatImageView by bindView(R.id.iv_top_right)
    private val rv: RecyclerView by bindView(R.id.rv)
    private val tvPreview: AppCompatTextView by bindView(R.id.tv_preview)
    private val tvConfirm: AppCompatTextView by bindView(R.id.tv_confirm)

    private val layoutManager by lazy {
        AlbumGridLayoutManager(this@AlbumActivity, 3)
    }

    private var adapter: AlbumFileAdapter? = null

    private var rxPermissions: RxPermissions? = null
    private var permissionsDisposable: Disposable? = null

    private var viewModelFactory: AlbumViewModelFactory? = null
    private var viewModel: AlbumViewModel? = null

    private var selectedFiles: MutableList<AlbumFile>? = mutableListOf()

    private val imageFolder = Environment.getExternalStorageDirectory().absolutePath + "/Gallery/"
    private var filePath: String? = null
    private var fileUri: Uri? = null

    private var selectedCount = 0
    private var maxSelectedCount = 1
    private var hasSystemCamera: Boolean = false
    private var hasSystemAlbum: Boolean = false

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                AlbumConstants.REQUEST_CODE_PREVIEW -> {
                    val intent = data ?: return
                    val bundle = intent.getBundleExtra(AlbumKeys.INTENT_BUNDLE) ?: return
                    val backFiles: MutableList<AlbumFile> =
                        bundle.getParcelableArrayList(AlbumKeys.BUNDLE_BEANS)
                            ?: return
                    for (i in selectedFiles!!.indices) {
                        val selectFile = selectedFiles!![i]
                        for (j in backFiles.indices) {
                            val backFile = backFiles[i]
                            if (selectFile.isSelected && !backFile.isSelected) {
                                clickItemNew(selectFile, selectFile.position)
                                break
                            }
                        }
                    }
                    if (intent.getBooleanExtra(AlbumKeys.INTENT_IS_CONFIRM, false)) {
                        backPaths()
                    }
                }
                AlbumConstants.REQUEST_CODE_SYSTEM_CAMERA -> {
                    fileUri = fileUri ?: return
                    if (fileUri!!.path.isNullOrBlank() || filePath.isNullOrBlank())
                        return

                    var bitmap: Bitmap? = BitmapFactory.decodeFile(filePath)
                    if (bitmap == null || bitmap.isRecycled || bitmap.width == 0 || bitmap.height == 0)
                        return

                    val imageDegree: Int = AlbumExifInterfaceUtil.getDegree(filePath)
                    if (imageDegree > 0) {
                        bitmap = AlbumBitmapUtil.rotateBitmap(bitmap, imageDegree.toFloat(), false)
                    }
                    val file = File(filePath!!)
                    val fos = FileOutputStream(file)
                    bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                    context.sendBroadcast(
                        Intent(
                            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                            Uri.parse("file://$filePath")
                        )
                    )
                    backPath()
                    fos.flush()
                    fos.close()
                }
                AlbumConstants.REQUEST_CODE_SYSTEM_ALBUM -> {
                    val intent = data ?: return
                    fileUri = intent.data ?: return
                    if (fileUri!!.path.isNullOrBlank())
                        return

                    if (intent.clipData != null) {
                        intent.clipData?.let {
                            val item: ClipData.Item = it.getItemAt(0)
                            fileUri = item.uri
                        }
                    }
                    filePath = AlbumFileUtil.getPath(context, fileUri)
                    if (filePath.isNullOrBlank())
                        return

                    backPath()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album)
        ImmersionBar.with(activity).statusBarDarkFont(true).init()
        checkPermission()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(
            R.anim.album_backward_enter_horizontal,
            R.anim.album_backward_exit_horizontal
        )
    }

    override fun onDestroy() {
        if (permissionsDisposable != null && !permissionsDisposable!!.isDisposed) {
            permissionsDisposable!!.dispose()
        }
        Album.get()?.let {
            it.albumContext = null
            it.maxSelectedCount = null
            it.hasSystemCamera = null
            it.hasSystemAlbum = null
        }
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (dl.isDrawerOpen(GravityCompat.END)) {
            closeDrawer()
        } else {
            super.onBackPressed()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.v_top_left -> {
                onBackPressed()
            }
            R.id.v_top_right -> {
                dl.openDrawer(GravityCompat.END)
            }
            R.id.tv_preview -> {
                if (AlbumClickUtil.isFastDoubleClick(v.id))
                    return

                getSelectedFiles()
                AlbumPreviewActivity.startActivityForResult(
                    activity,
                    selectedFiles!!,
                    true,
                    AlbumConstants.REQUEST_CODE_PREVIEW
                )
            }
            R.id.tv_confirm -> {
                getSelectedFiles()
                val paths = ArrayList<String>()
                for (index in selectedFiles!!.indices) {
                    paths.add(selectedFiles!![index].path!!)
                }
                if (paths.size == 1) {
                    filePath = paths[0]
                    backPath()
                } else if (paths.size > 1) {
                    backPaths()
                }
            }
        }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            rxPermissions = RxPermissions(activity)
            permissionsDisposable = rxPermissions!!.requestEachCombined(
                AlbumConstants.PERMISSION_CAMERA,
                AlbumConstants.PERMISSION_STORAGE
            )
                .subscribe { permission: Permission ->
                    when {
                        permission.granted -> {
                            initView()
                            initData()
                        }
                        permission.shouldShowRequestPermissionRationale -> {
                            checkPermission()
                        }
                        else -> {
                            AlbumToastUtil.longCenter(R.string.permission_sd)
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            startActivityForResult(intent, AlbumConstants.REQUEST_CODE_SETTINGS)
                        }
                    }
                }
        } else {
            initView()
            initData()
        }
    }

    private fun initView() {
        AlbumViewUtil.setPadding(top, 0, ImmersionBar.getStatusBarHeight(activity), 0, 0)
        top.setOnClickListener(object : AlbumDoubleClickListener() {
            override fun onDoubleClick(v: View?) {
                layoutManager.scrollToPositionWithOffset(0, 0)
            }
        })
        AlbumViewUtil.gone(tvCenter)
        AlbumViewUtil.visible(vRight)
        AlbumViewUtil.visible(ivRight)
        ivRight.setImageResource(R.drawable.ic_album_menu_green)
        ivRight.scaleType = ImageView.ScaleType.FIT_CENTER
        tvPreview.isEnabled = false
        tvConfirm.isEnabled = false
        vLeft.setOnClickListener(this)
        vRight.setOnClickListener(this)
        tvPreview.setOnClickListener(this)
        tvConfirm.setOnClickListener(this)
        AlbumRecyclerViewUtil.config(layoutManager, rv)
        val itemDecoration = AlbumGridItemDecoration(3, AlbumDimenUtil.dp2px(context, 2), false)
        rv.addItemDecoration(itemDecoration)
        (rv.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        adapter = AlbumFileAdapter(null, activity)
        rv.adapter = adapter
        adapter!!.setAnimationWithDefault(BaseQuickAdapter.AnimationType.AlphaIn)
        adapter!!.loadMoreModule.isEnableLoadMore = true
        adapter!!.loadMoreModule.loadMoreView =
            AlbumLoadMoreView(AlbumLoadMoreView.LOAD_MORE_VERTICAL)
        adapter!!.loadMoreModule.setOnLoadMoreListener {
            viewModel?.getFilesNew()
        }
        adapter!!.setOnItemClickListener { adapter, _, position ->
            val file = adapter.data[position] as AlbumFile?
            file?.let {
                when (it.itemType) {
                    AlbumFileType.SYSTEM_CAMERA.ordinal -> {
                        openSystemCamera()
                    }
                    AlbumFileType.SYSTEM_ALBUM.ordinal -> {
                        openSystemGallery()
                    }
                    AlbumFileType.FILE.ordinal -> {
                        clickItemNew(it, position)
                    }
                }
            }
        }
        val folderFragment = AlbumFolderFragment()
        folderFragment.setOnBackClickListener { dl.closeDrawer(GravityCompat.END) }
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.cl_folder, folderFragment)
            .commitAllowingStateLoss()
    }

    private fun initData() {
        Album.get()?.let {
            maxSelectedCount = it.maxSelectedCount!!
            hasSystemCamera = it.hasSystemCamera!!
            hasSystemAlbum = it.hasSystemAlbum!!
        }
        viewModelFactory = AlbumViewModelFactory(hasSystemCamera, hasSystemAlbum, null)
        viewModel = ViewModelProvider(activity, viewModelFactory!!).get(AlbumViewModel::class.java)
        viewModel?.let {
            it.folderNameLiveDataNew!!.observe(this, Observer { _ ->
                adapter!!.setList(null)
                it.getFilesNew()
                selectedCount = 0
            })
            it.filesLiveDataNew.observe(this, Observer { files ->
                if (files != null && files.size > 0) {
                    adapter!!.addData(files)
                    adapter!!.loadMoreModule.loadMoreComplete()
                } else {
                    adapter!!.loadMoreModule.loadMoreEnd()
                }
            })
        }
    }

    fun closeDrawer() {
        dl.closeDrawer(GravityCompat.END)
    }

    @Suppress("UNCHECKED_CAST")
    private fun clickItemNew(file: AlbumFile?, position: Int) {
        file?.let {
            if (selectedCount >= maxSelectedCount && !it.isSelected) {
                AlbumToastUtil.shortCenter(getString(R.string.max_selected_count, maxSelectedCount))
                return
            }
            it.isSelected = !it.isSelected
            if (it.isSelected) {
                selectedCount++
            } else {
                selectedCount--
                if (selectedCount < 0) {
                    selectedCount = 0
                }
                for (index in adapter!!.data.indices) {
                    val tempFile = adapter!!.data[index]
                    if (tempFile.isCamera || tempFile.isAlbum)
                        continue

                    if (tempFile.isSelected && tempFile.selectedNo > it.selectedNo) {
                        tempFile.selectedNo = tempFile.selectedNo - 1
                        adapter!!.notifyItemChanged(index)
                    }
                    if (!tempFile.isSelected && tempFile.selectedNo == 1) {
                        tempFile.selectedNo = 0
                        adapter!!.notifyItemChanged(index)
                    }
                }
            }
            it.selectedNo = selectedCount
            adapter!!.notifyItemChanged(position)
            tvPreview.isEnabled = selectedCount > 0
            tvConfirm.isEnabled = selectedCount > 0
            tvConfirm.text = if (selectedCount > 0) getString(
                R.string.confirm_selected_count,
                selectedCount
            ) else getString(R.string.confirm)
        }
    }

    private fun openSystemCamera() {
        if (!AlbumFileUtil.createFolder(imageFolder))
            return

        val fileName = System.currentTimeMillis().toString() + ".jpg"
        filePath = imageFolder + fileName
        val file = File(imageFolder, fileName)
        fileUri = FileProvider.getUriForFile(context, "com.yzt.gallery.fileprovider", file)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
        startActivityForResult(intent, AlbumConstants.REQUEST_CODE_SYSTEM_CAMERA)
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun openSystemGallery() {
        val intent = Intent()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.action = Intent.ACTION_PICK
        } else {
            intent.action = Intent.ACTION_GET_CONTENT
        }
        intent.data = Uri.parse("content://media/internal/images/media")
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        if (intent.resolveActivity(activity.packageManager) != null) {
            activity.startActivityForResult(
                Intent.createChooser(
                    intent,
                    getString(R.string.open_with)
                ), AlbumConstants.REQUEST_CODE_SYSTEM_ALBUM
            )
        } else {
            AlbumToastUtil.longCenter(R.string.no_activity_found)
        }
    }

    private fun getSelectedFiles() {
        selectedFiles!!.clear()
        for (index in adapter!!.data.indices) {
            val file = adapter!!.data[index]
            if (file.isSelected) {
                selectedFiles!!.add(file)
            }
        }
        if (selectedFiles!!.size > 0) {
            selectedFiles!!.sortBy {
                it.selectedNo
            }
        }
    }

    private fun backPath() {
        val backIntent = Intent()
        backIntent.putExtra(AlbumKeys.INTENT_PATH, filePath)
        setResult(Activity.RESULT_OK, backIntent)
        onBackPressed()
    }

    private fun backPaths() {
        getSelectedFiles()
        val paths = ArrayList<String>()
        for (index in selectedFiles!!.indices) {
            paths.add(selectedFiles!![index].path!!)
        }
        val backIntent = Intent()
        backIntent.putStringArrayListExtra(AlbumKeys.INTENT_PATHS, paths)
        setResult(Activity.RESULT_OK, backIntent)
        onBackPressed()
    }

}