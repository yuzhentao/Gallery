package com.yzt.gallery.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yzt.gallery.bean.AlbumFile

/**
 * 相册ViewModelFactory
 *
 * @author yzt 2020/4/22
 */
class AlbumViewModelFactory(hasSystemCamera: Boolean, hasSystemAlbum: Boolean, files: MutableList<AlbumFile>?) : ViewModelProvider.NewInstanceFactory() {

    private var hasSystemCamera: Boolean = false
    private var hasSystemAlbum: Boolean = false
    private var files: MutableList<AlbumFile>? = null

    init {
        this.hasSystemCamera = hasSystemCamera
        this.hasSystemAlbum = hasSystemAlbum
        this.files = files
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AlbumViewModel(hasSystemCamera, hasSystemAlbum, files) as T
    }

}