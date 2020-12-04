package com.yzt.gallery.viewModel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.yzt.gallery.Album
import com.yzt.gallery.repository.AlbumRepositoryNew
import com.yzt.gallery.repository.LocalMedia
import com.yzt.gallery.repository.LocalMediaFolder
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
    files: MutableList<LocalMedia>?
) : ViewModel() {

    private var page = 0
    private val pageSize = 90

    private var hasSystemCamera: Boolean = false
    private var hasSystemAlbum: Boolean = false

    private var filesNew: MutableList<LocalMedia>? = null
    var folderNameLiveDataNew: LiveData<Long>? = null
    var currentFolderLiveDataNew: MutableLiveData<LocalMediaFolder> =
        MutableLiveData<LocalMediaFolder>()
    var filesLiveDataNew: MutableLiveData<MutableList<LocalMedia>> =
        MutableLiveData<MutableList<LocalMedia>>()

    init {
        this.hasSystemCamera = hasSystemCamera
        this.hasSystemAlbum = hasSystemAlbum
        this.filesNew = files
        folderNameLiveDataNew =
            Transformations.switchMap(currentFolderLiveDataNew) { input: LocalMediaFolder ->
                val liveData = MutableLiveData<Long>()
                liveData.value = input.bucketId
                liveData
            }
    }

    @SuppressLint("CheckResult")
    @Suppress("UNCHECKED_CAST")
    fun getFilesNew() {
        Album.get()?.let {
            val observable: Observable<MutableList<LocalMedia>>
            if (page == 0 && folderNameLiveDataNew!!.value == -1L) {
                val observableList: MutableList<Observable<MutableList<LocalMedia>>> =
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
                    val galleryImages: MutableList<LocalMedia> = ArrayList()
                    for (o in objects) {
                        galleryImages.addAll(o as MutableList<LocalMedia>)
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
                .subscribe { files: MutableList<LocalMedia> ->
                    page++
                    filesLiveDataNew.setValue(files)
                }
        }
    }

    fun getFoldersNew(): Observable<MutableList<LocalMediaFolder>> {
        return AlbumRepositoryNew.get().getFolders()
    }

    fun setCurrentFolderNew(currentFolder: LocalMediaFolder?) {
        page = 0
        this.currentFolderLiveDataNew.value = currentFolder
    }

}