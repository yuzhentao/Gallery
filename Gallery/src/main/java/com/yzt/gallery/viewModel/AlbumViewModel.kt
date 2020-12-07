package com.yzt.gallery.viewModel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.yzt.gallery.Album
import com.yzt.gallery.repository.AlbumRepositoryNew
import com.yzt.gallery.bean.AlbumFile
import com.yzt.gallery.bean.AlbumFolder
import com.yzt.gallery.rx.AlbumRxSchedulers
import io.reactivex.Observable

/**
 * 相册ViewModel
 *
 * @author yzt 2020/4/22
 */
class AlbumViewModel(
    hasSystemCamera: Boolean,
    hasSystemAlbum: Boolean,
    files: MutableList<AlbumFile>?
) : ViewModel() {

    private var page = 0
    private val pageSize = 90

    private var hasSystemCamera: Boolean = false
    private var hasSystemAlbum: Boolean = false

    private var filesNew: MutableList<AlbumFile>? = null
    var folderNameLiveDataNew: LiveData<Long>? = null
    var currentFolderLiveDataNew: MutableLiveData<AlbumFolder> =
        MutableLiveData<AlbumFolder>()
    var filesLiveDataNew: MutableLiveData<MutableList<AlbumFile>> =
        MutableLiveData<MutableList<AlbumFile>>()

    init {
        this.hasSystemCamera = hasSystemCamera
        this.hasSystemAlbum = hasSystemAlbum
        this.filesNew = files
        folderNameLiveDataNew =
            Transformations.switchMap(currentFolderLiveDataNew) { input: AlbumFolder ->
                val liveData = MutableLiveData<Long>()
                liveData.value = input.bucketId
                liveData
            }
    }

    @SuppressLint("CheckResult")
    @Suppress("UNCHECKED_CAST")
    fun getFilesNew() {
        Album.get()?.let {
            val observable: Observable<MutableList<AlbumFile>>
            if (page == 0 && folderNameLiveDataNew!!.value == -1L) {
                val observableList: MutableList<Observable<MutableList<AlbumFile>>> =
                    mutableListOf()
                if (hasSystemCamera) {
                    observableList.add(AlbumRepositoryNew.get().getSystemCamera())
                }
                if (hasSystemAlbum) {
                    observableList.add(AlbumRepositoryNew.get().getSystemAlbum())
                }
                filesNew?.let { itt ->
                    if (itt.size > 0) {
                        observableList.add(Observable.just(itt))
                    }
                }
                observableList.add(
                    AlbumRepositoryNew.get()
                        .getFiles(hasSystemCamera, hasSystemAlbum, -1L, page, pageSize)
                )
                observable = Observable.zip(observableList) { objects ->
                    val galleryImages: MutableList<AlbumFile> = ArrayList()
                    for (o in objects) {
                        galleryImages.addAll(o as MutableList<AlbumFile>)
                    }
                    galleryImages
                }
            } else {
                observable = AlbumRepositoryNew().getFiles(
                    hasSystemCamera,
                    hasSystemAlbum,
                    folderNameLiveDataNew!!.value!!,
                    page,
                    pageSize
                )
            }
            observable
                .compose(AlbumRxSchedulers.normalSchedulers())
                .subscribe { files: MutableList<AlbumFile> ->
                    page++
                    filesLiveDataNew.setValue(files)
                }
        }
    }

    fun getFoldersNew(): Observable<MutableList<AlbumFolder>> {
        return AlbumRepositoryNew.get().getFolders()
    }

    fun setCurrentFolderNew(currentFolder: AlbumFolder?) {
        page = 0
        this.currentFolderLiveDataNew.value = currentFolder
    }

}