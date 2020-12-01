package com.yzt.gallery.viewModel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.yzt.gallery.Album
import com.yzt.gallery.R
import com.yzt.gallery.bean.AlbumFile
import com.yzt.gallery.bean.AlbumFolder
import com.yzt.gallery.repository.AlbumRepository
import com.yzt.gallery.rx.AlbumRxSchedulers
import io.reactivex.Observable

/**
 * 相册ViewModel
 *
 * @author yzt 2020/4/22
 */
class AlbumViewModel(hasSystemCamera: Boolean, hasSystemAlbum: Boolean, files: MutableList<AlbumFile>?) : ViewModel() {

    private var pageNo = 0
    private val pageSize = 90

    private var hasSystemCamera: Boolean = false
    private var hasSystemAlbum: Boolean = false
    private var files: MutableList<AlbumFile>? = null

    var folderNameLiveData: LiveData<String>? = null
    var currentFolderLiveData: MutableLiveData<AlbumFolder> = MutableLiveData<AlbumFolder>()
    var filesLiveData: MutableLiveData<MutableList<AlbumFile>> = MutableLiveData<MutableList<AlbumFile>>()

    init {
        this.hasSystemCamera = hasSystemCamera
        this.hasSystemAlbum = hasSystemAlbum
        this.files = files
        folderNameLiveData = Transformations.switchMap(currentFolderLiveData) { input: AlbumFolder ->
            val liveData = MutableLiveData<String>()
            liveData.value = input.name
            liveData
        }
    }

    @SuppressLint("CheckResult")
    @Suppress("UNCHECKED_CAST")
    fun getFiles() {
        Album.get()?.let {
            val folder: String? = if (folderNameLiveData!!.value == it.getContext()!!.getString(R.string.all)) {
                ""
            } else {
                folderNameLiveData!!.value;
            }
            val observable: Observable<MutableList<AlbumFile>>
            if (pageNo == 0 && folder.isNullOrBlank()) {
                val observableList: MutableList<Observable<MutableList<AlbumFile>>> = mutableListOf()
                if (hasSystemCamera) {
                    observableList.add(AlbumRepository.get().getSystemCamera()!!);
                }
                if (hasSystemAlbum) {
                    observableList.add(AlbumRepository.get().getSystemAlbum()!!);
                }
                files?.let { itt ->
                    if (itt.size > 0) {
                        observableList.add(Observable.just(itt))
                    }
                }
                observableList.add(AlbumRepository.get().getFiles(hasSystemCamera, hasSystemAlbum, "", pageNo, pageSize)!!)
                observable = Observable.zip(observableList) { objects ->
                    val galleryImages: MutableList<AlbumFile> = ArrayList()
                    for (o in objects) {
                        galleryImages.addAll(o as MutableList<AlbumFile>)
                    }
                    galleryImages
                }
            } else {
                observable = AlbumRepository.get().getFiles(hasSystemCamera, hasSystemAlbum, folder!!, pageNo, pageSize)!!
            }
            observable
                    .compose(AlbumRxSchedulers.normalSchedulers())
                    .subscribe { files: MutableList<AlbumFile> ->
                        pageNo++
                        filesLiveData.setValue(files)
                    }
        }
    }

    fun getFolders(): Observable<MutableList<AlbumFolder>> {
        return AlbumRepository.get().getFolders()
    }

    fun setCurrentFolder(currentFolder: AlbumFolder?) {
        pageNo = 0
        this.currentFolderLiveData.value = currentFolder
    }

}