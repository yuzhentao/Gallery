package com.yzt.gallery.repository

import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.yzt.gallery.Album
import com.yzt.gallery.R
import com.yzt.gallery.bean.AlbumFile
import com.yzt.gallery.bean.AlbumFolder
import com.yzt.gallery.key.AlbumFileType
import com.yzt.gallery.util.AlbumFileUtil
import io.reactivex.Observable

/**
 * 相册仓库
 *
 * @author yzt 2020/4/22
 */
class AlbumRepository private constructor() {

    companion object {
        @JvmStatic
        fun get(): AlbumRepository {
            return AlbumRepositoryHolder.holder
        }
    }

    private object AlbumRepositoryHolder {
        val holder = AlbumRepository()
    }

    private val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    private val projection = arrayOf(
            MediaStore.Images.ImageColumns.DATA,
            MediaStore.Images.ImageColumns.DISPLAY_NAME,
            MediaStore.Images.ImageColumns.DATE_ADDED,
            MediaStore.Images.ImageColumns.MIME_TYPE,
            MediaStore.Images.ImageColumns.SIZE,
            MediaStore.Images.ImageColumns.WIDTH,
            MediaStore.Images.ImageColumns.HEIGHT,
            MediaStore.Images.ImageColumns._ID)

    fun getFolders(): Observable<MutableList<AlbumFolder>> {
        return Observable.create { emitter ->
            var sdPath = ""
            if (AlbumFileUtil.isSDExists()) {
                sdPath = Environment.getExternalStorageDirectory().toString() + "/Android/data/"
            }
            val folders: MutableList<AlbumFolder> = mutableListOf()
            val folderCount = "count"
            val mediaColumns = arrayOf(
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATA,
                    "COUNT(*) AS $folderCount"
            )
            val selection: String =
                    (
                            projection[0] + " NOT LIKE '%/data/data/%' and "
                                    + projection[0] + " NOT LIKE '%/cache%'"
                                    + ") and ("
                                    + projection[0] + " NOT LIKE '%/data/user/0/%' and "
                                    + projection[0] + " NOT LIKE '%/cache%'"
                                    + ") and ("
                                    + projection[0] + " NOT LIKE '%" + sdPath + "%' and "
                                    + projection[0] + " NOT LIKE '%/cache%'"
                                    + ") and ("
                                    + projection[3] + "='image/webp' or "
                                    + projection[3] + "='image/jpeg' or "
                                    + projection[3] + "='image/png' or "
                                    + projection[3] + "='image/jpg'"
                                    + ")"
                                    + " GROUP BY "
                                    + "("
                                    + MediaStore.Images.Media.BUCKET_DISPLAY_NAME
                            )
            val cursor: Cursor? = Album.get()!!.getContext()!!.contentResolver
                    .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mediaColumns, selection, null, null)
            cursor?.let {
                while (it.moveToNext()) {
                    val path = it.getString(it.getColumnIndex(MediaStore.Images.Media.DATA))
                    if (path.endsWith(".gif"))
                        continue

                    val bucketName = it.getString(it.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                    val count = it.getInt(it.getColumnIndex(folderCount))
                    val folder = AlbumFolder()
                    folder.coverImage = path
                    folder.name = bucketName
                    folder.count = count
                    folders.add(folder)
                }
                it.close()
            }
            var allCount = 0
            for (galleryFolder in folders) {
                allCount += galleryFolder.count
            }
            val allFolder = AlbumFolder()
            if (folders.size > 0) {
                allFolder.coverImage = folders[0].coverImage
            }
            allFolder.name = Album.get()!!.getContext()!!.getString(R.string.all)
            allFolder.count = allCount
            folders.add(0, allFolder)
            emitter.onNext(folders)
        }
    }

    fun getFiles(hasSystemCamera: Boolean, hasSystemAlbum: Boolean, folderName: String?, pageNo: Int, pageSize: Int): Observable<MutableList<AlbumFile>>? {
        return Observable.create { emitter ->
            var sdPath = ""
            if (AlbumFileUtil.isSDExists()) {
                sdPath = Environment.getExternalStorageDirectory().toString() + "/Android/data/"
            }
            val folderNameSelection: String =
                    if (folderName.isNullOrBlank()) {
                        MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME + " != '" + "" + "' and "
                    } else {
                        MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME + " = '" + folderName + "' and "
                    }
            val selection: String =
                    (
                            folderNameSelection
                                    + "("
                                    + projection[0] + " NOT LIKE '%/data/data/%' and "
                                    + projection[0] + " NOT LIKE '%/cache%'"
                                    + ") and ("
                                    + projection[0] + " NOT LIKE '%/data/user/0/%' and "
                                    + projection[0] + " NOT LIKE '%/cache%'"
                                    + ") and ("
                                    + projection[0] + " NOT LIKE '%" + sdPath + "%' and "
                                    + projection[0] + " NOT LIKE '%/cache%'"
                                    + ") and ("
                                    + projection[3] + "='image/webp' or "
                                    + projection[3] + "='image/jpeg' or "
                                    + projection[3] + "='image/png' or "
                                    + projection[3] + "='image/jpg'"
                                    + ")"
                            )
            val sortOrder = MediaStore.Images.Media.DATE_TAKEN + " DESC limit " + pageSize + " offset " + pageNo * pageSize
            val cursor: Cursor? = Album.get()!!.getContext()!!.contentResolver
                    .query(uri, projection, selection, null, sortOrder)
            val files: MutableList<AlbumFile> = ArrayList()
            cursor?.let {
                while (it.moveToNext()) {
                    val path: String? = it.getString(it.getColumnIndexOrThrow(projection[0]))
                    val name: String? = it.getString(it.getColumnIndexOrThrow(projection[1]))
                    val date: Long = it.getLong(it.getColumnIndexOrThrow(projection[2]))
                    val type: String? = it.getString(it.getColumnIndex(projection[3]))
                    val size: Long = it.getLong(it.getColumnIndex(projection[4]))
                    val width: Int = it.getInt(it.getColumnIndex(projection[5]))
                    val height: Int = it.getInt(it.getColumnIndex(projection[6]))
                    if (path.isNullOrBlank())
                        continue

                    var file: AlbumFile
                    if (!name.isNullOrBlank()) {
                        file = AlbumFile()
                        file.itemType = AlbumFileType.FILE.ordinal
                        file.isSelected = false
                        file.isCamera = false
                        file.isAlbum = false
                        if (path.contains("/")) {
                            file.folder = path.substring(0, path.lastIndexOf("/"))
                        }
                        file.path = path
                        file.name = name
                        file.date = date
                        file.type = type
                        file.size = size
                        file.width = width
                        file.height = height
                        files.add(file)
                    }
                }
                it.close()
            }
            for (index in files.indices) {
                if (hasSystemCamera && hasSystemAlbum) {
                    files[index].position = index + 2
                } else if (hasSystemCamera && !hasSystemAlbum || !hasSystemCamera && hasSystemAlbum) {
                    files[index].position = index + 1
                } else {
                    files[index].position = index
                }
            }
            emitter.onNext(files)
        }
    }

    fun getSystemCamera(): Observable<MutableList<AlbumFile>> {
        return Observable.create { emitter ->
            val file = AlbumFile()
            file.isCamera = true
            file.itemType = AlbumFileType.SYSTEM_CAMERA.ordinal
            val files: MutableList<AlbumFile> = ArrayList()
            files.add(file)
            emitter.onNext(files)
        }
    }

    fun getSystemAlbum(): Observable<MutableList<AlbumFile>> {
        return Observable.create { emitter ->
            val file = AlbumFile()
            file.isCamera = true
            file.itemType = AlbumFileType.SYSTEM_ALBUM.ordinal
            val files: MutableList<AlbumFile> = ArrayList()
            files.add(file)
            emitter.onNext(files)
        }
    }

}