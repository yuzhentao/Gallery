package com.yzt.gallery.repository

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import com.yzt.gallery.Album
import com.yzt.gallery.R
import com.yzt.gallery.bean.AlbumFile
import com.yzt.gallery.bean.AlbumFolder
import com.yzt.gallery.key.AlbumFileType
import com.yzt.gallery.util.AlbumLogUtil
import io.reactivex.Observable
import java.util.*

/**
 * 相册仓库（新
 *
 * @author yzt 2020/12/3
 */
class AlbumRepositoryNew {

    companion object {
        @JvmStatic
        fun get(): AlbumRepositoryNew {
            return AlbumRepositoryNewHolder.holder
        }
    }

    private object AlbumRepositoryNewHolder {
        val holder = AlbumRepositoryNew()
    }

    private val notGifUnknown = "!='image/*'"
    private val notGif = "!='image/gif' AND " + MediaStore.MediaColumns.MIME_TYPE + notGifUnknown
    private val groupByBucketId = " GROUP BY (bucket_id"
    private val columnCount = "count"
    private val columnBucketId = "bucket_id"
    private val columnBucketDisplayName = "bucket_display_name"

    private val fileSizeUnit = 1024 * 1024L

    private val uri: Uri = MediaStore.Files.getContentUri("external")//统一资源标志符
    private val projection = arrayOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.MediaColumns.DATA,
        columnBucketId,
        columnBucketDisplayName,
        MediaStore.MediaColumns.MIME_TYPE,
        "COUNT(*) AS $columnCount"
    )
    private val projection29 = arrayOf(
        MediaStore.Files.FileColumns._ID,
        columnBucketId,
        columnBucketDisplayName,
        MediaStore.MediaColumns.MIME_TYPE
    )
    private val selection =
        ("(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=? )" + " AND " + MediaStore.MediaColumns.SIZE + ">0)" + groupByBucketId)
    private val selection29 =
        (MediaStore.Files.FileColumns.MEDIA_TYPE + "=? " + " AND " + MediaStore.MediaColumns.SIZE + ">0")
    private val sortOrder = MediaStore.Files.FileColumns._ID + " DESC"

    private val projectionFile = arrayOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.MediaColumns.DATA,
        MediaStore.MediaColumns.MIME_TYPE,
        MediaStore.MediaColumns.WIDTH,
        MediaStore.MediaColumns.HEIGHT,
        MediaStore.MediaColumns.DURATION,
        MediaStore.MediaColumns.SIZE,
        MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
        MediaStore.MediaColumns.DISPLAY_NAME,
        columnBucketId
    )

    /**
     * 获取文件夹
     */
    fun getFolders(): Observable<MutableList<AlbumFolder>> {
        return Observable.create { emitter ->
            val folders: MutableList<AlbumFolder> = mutableListOf()
            val cursor: Cursor? = Album.get()!!.getContext()!!.contentResolver.query(
                uri,
                getProjection(),
                getSelection(),
                getSelectionArgs(),
                sortOrder
            )

            try {
                if (cursor != null) {
                    val count = cursor.count
                    var totalCount = 0
                    if (count > 0) {
                        if (SdkVersionUtils.checkedAndroid_Q()) {//API>=29
                            val countMap: MutableMap<Long, Long> = mutableMapOf()
                            while (cursor.moveToNext()) {
                                val bucketId =
                                    cursor.getLong(cursor.getColumnIndex(columnBucketId))
                                var newCount = countMap[bucketId]
                                if (newCount == null) {
                                    newCount = 1L
                                } else {
                                    newCount++
                                }
                                countMap[bucketId] = newCount
                            }
                            if (cursor.moveToFirst()) {
                                val set: MutableSet<Long> = mutableSetOf()
                                do {
                                    val bucketId =
                                        cursor.getLong(cursor.getColumnIndex(columnBucketId))
                                    if (set.contains(bucketId)) {
                                        continue
                                    }

                                    val id =
                                        cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID))
                                    val bucketDisplayName =
                                        cursor.getString(
                                            cursor.getColumnIndex(
                                                columnBucketDisplayName
                                            )
                                        )
                                    val size = countMap[bucketId]!!
                                    val folder =
                                        AlbumFolder()
                                    folder.bucketId = bucketId
                                    folder.firstImagePath =
                                        getRealPathAndroidQ(
                                            id
                                        )
                                    folder.name = bucketDisplayName
                                    folder.imageNum = ValueOf.toInt(size)
                                    folders.add(folder)
                                    set.add(bucketId)
                                    totalCount += size.toInt()
                                } while (cursor.moveToNext())
                            }
                        } else {
                            cursor.moveToFirst()
                            do {
                                val bucketId =
                                    cursor.getLong(cursor.getColumnIndex(columnBucketId))
                                val url =
                                    cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA))
                                val bucketDisplayName =
                                    cursor.getString(cursor.getColumnIndex(columnBucketDisplayName))
                                val size =
                                    cursor.getInt(cursor.getColumnIndex(columnCount))
                                val folder =
                                    AlbumFolder()
                                folder.bucketId = bucketId
                                folder.firstImagePath = url
                                folder.name = bucketDisplayName
                                folder.imageNum = size
                                folders.add(folder)
                                totalCount += size
                            } while (cursor.moveToNext())
                        }

                        sortFolders(folders)

                        val bucketDisplayName: String =
                            Album.get()!!.getContext()!!.getString(R.string.picture_camera_roll)
                        val allFolder =
                            AlbumFolder()//所有照片
                        allFolder.bucketId = -1
                        if (cursor.moveToFirst()) {
                            val firstUrl: String =
                                if (SdkVersionUtils.checkedAndroid_Q()) getFirstUri(
                                    cursor
                                ) else getFirstUrl(cursor)
                            allFolder.firstImagePath = firstUrl
                        }
                        allFolder.name = bucketDisplayName
                        allFolder.imageNum = totalCount
                        allFolder.ofAllType = PictureMimeType.ofImage()
                        allFolder.isSelected = true
                        allFolder.isCameraFolder = true
                        folders.add(0, allFolder)
                    }
                }
            } catch (e: Exception) {
                AlbumLogUtil.e(e.message)
            } finally {
                if (cursor != null && !cursor.isClosed) {
                    cursor.close()
                }
            }
            emitter.onNext(folders)
        }
    }

    /**
     * 获取文件
     */
    fun getFiles(
        hasSystemCamera: Boolean,
        hasSystemAlbum: Boolean,
        bucketId: Long,
        page: Int,
        pageSize: Int
    ): Observable<MutableList<AlbumFile>> {
        return Observable.create { emitter ->
            val files: MutableList<AlbumFile> = mutableListOf()
            var cursor: Cursor? = null

            try {
                cursor = if (SdkVersionUtils.checkedAndroid_R()) {//API>=30
                    val queryArgsFile: Bundle = MediaUtils.createQueryArgsBundle(
                        getSelectionFile(bucketId),
                        getSelectionArgsFile(bucketId),
                        pageSize,
                        (page - 1) * pageSize
                    )
                    Album.get()!!.getContext()!!.contentResolver.query(
                        uri,
                        projectionFile,
                        queryArgsFile,
                        null
                    )
                } else {
                    val sortOrderFile =
                        if (page == -1) MediaStore.Files.FileColumns._ID + " DESC" else MediaStore.Files.FileColumns._ID + " DESC limit " + pageSize + " offset " + (page - 1) * pageSize
                    Album.get()!!.getContext()!!.contentResolver.query(
                        uri,
                        projectionFile,
                        getSelectionFile(bucketId),
                        getSelectionArgsFile(bucketId),
                        sortOrderFile
                    )
                }
                if (cursor != null) {
                    if (cursor.count > 0) {
                        val idColumn = cursor.getColumnIndexOrThrow(
                            projectionFile[0]
                        )
                        val dataColumn = cursor.getColumnIndexOrThrow(
                            projectionFile[1]
                        )
                        val mimeTypeColumn = cursor.getColumnIndexOrThrow(
                            projectionFile[2]
                        )
                        val widthColumn = cursor.getColumnIndexOrThrow(
                            projectionFile[3]
                        )
                        val heightColumn = cursor.getColumnIndexOrThrow(
                            projectionFile[4]
                        )
                        val durationColumn = cursor.getColumnIndexOrThrow(
                            projectionFile[5]
                        )
                        val sizeColumn = cursor.getColumnIndexOrThrow(
                            projectionFile[6]
                        )
                        val folderNameColumn = cursor.getColumnIndexOrThrow(
                            projectionFile[7]
                        )
                        val fileNameColumn = cursor.getColumnIndexOrThrow(
                            projectionFile[8]
                        )
                        val bucketIdColumn = cursor.getColumnIndexOrThrow(
                            projectionFile[9]
                        )
                        cursor.moveToFirst()
                        do {
                            val id = cursor.getLong(idColumn)
                            val absolutePath = cursor.getString(dataColumn)
                            val url =
                                if (SdkVersionUtils.checkedAndroid_Q()) getRealPathAndroidQ(
                                    id
                                ) else absolutePath
                            if (!PictureFileUtils.isFileExists(absolutePath)) {//过滤无效文件
                                continue
                            }

                            var mimeType = cursor.getString(mimeTypeColumn)
                            mimeType =
                                if (TextUtils.isEmpty(mimeType)) PictureMimeType.ofJPEG() else mimeType
                            if (mimeType.endsWith("image/*")) {
                                mimeType = if (PictureMimeType.isContent(url)) {
                                    PictureMimeType.getImageMimeType(absolutePath)
                                } else {
                                    PictureMimeType.getImageMimeType(url)
                                }
                                if (PictureMimeType.isGif(mimeType)) {//过滤GIF
                                    continue
                                }
                            }
                            if (mimeType.startsWith(PictureMimeType.ofWEBP())) {//过滤WEBP
                                continue
                            }
                            if (mimeType.startsWith(PictureMimeType.ofBMP())) {//过滤BMP
                                continue
                            }
                            val width = cursor.getInt(widthColumn)
                            val height = cursor.getInt(heightColumn)
                            val duration = cursor.getLong(durationColumn)
                            val size = cursor.getLong(sizeColumn)
                            val folderName = cursor.getString(folderNameColumn)
                            val fileName = cursor.getString(fileNameColumn)
                            val bucketId = cursor.getLong(bucketIdColumn)
                            val image = AlbumFile(
                                id,
                                url,
                                absolutePath,
                                fileName,
                                folderName,
                                duration,
                                PictureMimeType.ofImage(),
                                mimeType,
                                width,
                                height,
                                size,
                                bucketId
                            )
                            image.itemType = AlbumFileType.FILE.ordinal
                            image.isSelected = false
                            image.isCamera = false
                            image.isAlbum = false
                            files.add(image)
                        } while (cursor.moveToNext())
                    }
                }
            } catch (e: Exception) {
                AlbumLogUtil.e(e.message)
            } finally {
                if (cursor != null && !cursor.isClosed) {
                    cursor.close()
                }
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
            val files: MutableList<AlbumFile> = mutableListOf()
            files.add(file)
            emitter.onNext(files)
        }
    }

    fun getSystemAlbum(): Observable<MutableList<AlbumFile>> {
        return Observable.create { emitter ->
            val file = AlbumFile()
            file.isAlbum = true
            file.itemType = AlbumFileType.SYSTEM_ALBUM.ordinal
            val files: MutableList<AlbumFile> = mutableListOf()
            files.add(file)
            emitter.onNext(files)
        }
    }

    /**
     * 由需要查询的列名组成的数组，如果为null则表示查询全部列
     */
    private fun getProjection() =
        if (SdkVersionUtils.checkedAndroid_Q()) projection29 else projection

    /**
     * 类似SQL中的where子句，用于增加条件来完成数据过滤
     */
    private fun getSelection(): String {
        return if (SdkVersionUtils.checkedAndroid_Q()) selection29 else selection
    }

    /**
     * 用于替换selection中可以使用?表示的变量值
     */
    private fun getSelectionArgs(): Array<String> {
        return arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString()
        )
    }

    /**
     * 对文件夹进行排序
     */
    private fun sortFolders(folders: MutableList<AlbumFolder>) {
        Collections.sort(folders, Comparator { lhs, rhs ->
            if (lhs.data == null || rhs.data == null) {
                return@Comparator 0
            }
            val lSize: Int = lhs.imageNum
            val rSize: Int = rhs.imageNum
            rSize.compareTo(lSize)
        })
    }

    private fun getSelectionFile(bucketId: Long): String {
        if (bucketId == -1L) {
            return ("(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND " + MediaStore.MediaColumns.MIME_TYPE + notGif
                    + ") AND " + MediaStore.MediaColumns.SIZE + ">0")
        }
        return ("(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                + " AND " + MediaStore.MediaColumns.MIME_TYPE + notGif
                + ") AND " + columnBucketId + "=? AND " + MediaStore.MediaColumns.SIZE + ">0")
    }

    private fun getSelectionArgsFile(bucketId: Long): Array<String> {
        return getSelectionArgsForPageSingleMediaType(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE,
            bucketId
        )
    }

    private fun getSelectionArgsForPageSingleMediaType(
        mediaType: Int,
        bucketId: Long
    ): Array<String> {
        return if (bucketId == -1L) arrayOf(mediaType.toString()) else arrayOf(
            mediaType.toString(),
            ValueOf.toString(bucketId)
        )
    }

    private fun getRealPathAndroidQ(id: Long): String {
        return uri.buildUpon().appendPath(ValueOf.toString(id)).build().toString()
    }

    private fun getFirstUrl(cursor: Cursor): String {
        return cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA))
    }

    private fun getFirstUri(cursor: Cursor): String {
        val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID))
        return getRealPathAndroidQ(id)
    }

}