package com.yzt.gallery.repository

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import com.yzt.gallery.Album
import com.yzt.gallery.R
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

    private val ORDER_BY = MediaStore.Files.FileColumns._ID + " DESC" //排序规则
    private val NOT_GIF_UNKNOWN = "!='image/*'"
    private val NOT_GIF = "!='image/gif' AND " + MediaStore.MediaColumns.MIME_TYPE + NOT_GIF_UNKNOWN
    private val GROUP_BY_BUCKET_Id = " GROUP BY (bucket_id"
    private val COLUMN_COUNT = "count"
    private val COLUMN_BUCKET_ID = "bucket_id"
    private val COLUMN_BUCKET_DISPLAY_NAME = "bucket_display_name"

    /**
     * unit
     */
    private val FILE_SIZE_UNIT = 1024 * 1024L

    /**
     * Image
     */
    private val SELECTION = ("(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=? )"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0)" + GROUP_BY_BUCKET_Id)

    private val SELECTION_29 = (MediaStore.Files.FileColumns.MEDIA_TYPE + "=? "
            + " AND " + MediaStore.MediaColumns.SIZE + ">0")

    private val uri: Uri = MediaStore.Files.getContentUri("external")//统一资源标志符
    private val projection_29 = arrayOf(
        MediaStore.Files.FileColumns._ID,
        COLUMN_BUCKET_ID,
        COLUMN_BUCKET_DISPLAY_NAME,
        MediaStore.MediaColumns.MIME_TYPE
    )
    private val projection = arrayOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.MediaColumns.DATA,
        COLUMN_BUCKET_ID,
        COLUMN_BUCKET_DISPLAY_NAME,
        MediaStore.MediaColumns.MIME_TYPE,
        "COUNT(*) AS $COLUMN_COUNT"
    )

    private val projection_page = arrayOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.MediaColumns.DATA,
        MediaStore.MediaColumns.MIME_TYPE,
        MediaStore.MediaColumns.WIDTH,
        MediaStore.MediaColumns.HEIGHT,
        MediaStore.MediaColumns.DURATION,
        MediaStore.MediaColumns.SIZE,
        MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
        MediaStore.MediaColumns.DISPLAY_NAME,
        COLUMN_BUCKET_ID
    )

    fun getFolders(): Observable<MutableList<LocalMediaFolder>> {
        return Observable.create { emitter ->
            val mediaFolders: MutableList<LocalMediaFolder> = ArrayList()

            val data: Cursor? = Album.get()!!.getContext()!!.contentResolver.query(
                uri,
                if (SdkVersionUtils.checkedAndroid_Q()) projection_29 else projection,
                getSelection(),
                getSelectionArgs(),
                ORDER_BY
            )

            try {
                if (data != null) {
                    val count = data.count
                    var totalCount = 0
                    if (count > 0) {
                        if (SdkVersionUtils.checkedAndroid_Q()) {//API>=29
                            val countMap: MutableMap<Long, Long> = HashMap()
                            while (data.moveToNext()) {
                                val bucketId =
                                    data.getLong(data.getColumnIndex(COLUMN_BUCKET_ID))
                                var newCount = countMap[bucketId]
                                if (newCount == null) {
                                    newCount = 1L
                                } else {
                                    newCount++
                                }
                                countMap[bucketId] = newCount
                            }
                            if (data.moveToFirst()) {
                                val hashSet: MutableSet<Long> = HashSet()
                                do {
                                    val bucketId =
                                        data.getLong(data.getColumnIndex(COLUMN_BUCKET_ID))
                                    if (hashSet.contains(bucketId)) {
                                        continue
                                    }
                                    val bucketDisplayName =
                                        data.getString(
                                            data.getColumnIndex(
                                                COLUMN_BUCKET_DISPLAY_NAME
                                            )
                                        )
                                    val size = countMap[bucketId]!!
                                    val id =
                                        data.getLong(data.getColumnIndex(MediaStore.Files.FileColumns._ID))
                                    val mediaFolder = LocalMediaFolder()
                                    mediaFolder.bucketId = bucketId
                                    mediaFolder.firstImagePath =
                                        getRealPathAndroid_Q(
                                            id
                                        )
                                    mediaFolder.name = bucketDisplayName
                                    mediaFolder.imageNum = ValueOf.toInt(size)
                                    mediaFolders.add(mediaFolder)
                                    hashSet.add(bucketId)
                                    totalCount += size.toInt()
                                } while (data.moveToNext())
                            }
                        } else {
                            data.moveToFirst()
                            do {
                                val bucketId =
                                    data.getLong(data.getColumnIndex(COLUMN_BUCKET_ID))
                                val url =
                                    data.getString(data.getColumnIndex(MediaStore.MediaColumns.DATA))
                                val bucketDisplayName =
                                    data.getString(data.getColumnIndex(COLUMN_BUCKET_DISPLAY_NAME))
                                val size =
                                    data.getInt(data.getColumnIndex(COLUMN_COUNT))
                                val mediaFolder = LocalMediaFolder()
                                mediaFolder.bucketId = bucketId
                                mediaFolder.firstImagePath = url
                                mediaFolder.name = bucketDisplayName
                                mediaFolder.imageNum = size
                                mediaFolders.add(mediaFolder)
                                totalCount += size
                            } while (data.moveToNext())
                        }

                        sortFolder(mediaFolders)

                        // 相机胶卷
                        val allMediaFolder = LocalMediaFolder()
                        allMediaFolder.bucketId = -1
                        if (data.moveToFirst()) {
                            val firstUrl: String =
                                if (SdkVersionUtils.checkedAndroid_Q()) getFirstUri(
                                    data
                                ) else getFirstUrl(data)
                            allMediaFolder.firstImagePath = firstUrl
                        }
                        val bucketDisplayName: String =
                            Album.get()!!.getContext()!!.getString(R.string.picture_camera_roll)
                        allMediaFolder.name = bucketDisplayName
                        allMediaFolder.imageNum = totalCount
//                        allMediaFolder.ofAllType = config.chooseMode
                        allMediaFolder.ofAllType = PictureMimeType.ofImage()
                        allMediaFolder.isChecked = true
                        allMediaFolder.isCameraFolder = true
                        mediaFolders.add(0, allMediaFolder)
                    }
                }
            } catch (e: Exception) {
                AlbumLogUtil.e(e.message)
            } finally {
                if (data != null && !data.isClosed) {
                    data.close()
                }
            }

            emitter.onNext(mediaFolders)
        }
    }

    fun getFiles(
        hasSystemCamera: Boolean,
        hasSystemAlbum: Boolean,
        bucketId: Long,
        page: Int,
        pageSize: Int
    ): Observable<MutableList<LocalMedia>> {
        return Observable.create { emitter ->
            val files: MutableList<LocalMedia> = ArrayList()
            var data: Cursor? = null

            try {
                data = if (SdkVersionUtils.checkedAndroid_R()) {
                    val queryArgs: Bundle = MediaUtils.createQueryArgsBundle(
                        getPageSelection(bucketId),
                        getPageSelectionArgs(bucketId),
                        pageSize,
                        (page - 1) * pageSize
                    )
                    Album.get()!!.getContext()!!.contentResolver.query(
                        uri,
                        projection_page,
                        queryArgs,
                        null
                    )
                } else {
                    val orderBy =
                        if (page == -1) MediaStore.Files.FileColumns._ID + " DESC" else MediaStore.Files.FileColumns._ID + " DESC limit " + pageSize + " offset " + (page - 1) * pageSize
                    Album.get()!!.getContext()!!.contentResolver.query(
                        uri,
                        projection_page,
                        getPageSelection(bucketId),
                        getPageSelectionArgs(bucketId),
                        orderBy
                    )
                }
                if (data != null) {
                    if (data.count > 0) {
                        val idColumn = data.getColumnIndexOrThrow(
                            projection_page[0]
                        )
                        val dataColumn = data.getColumnIndexOrThrow(
                            projection_page[1]
                        )
                        val mimeTypeColumn = data.getColumnIndexOrThrow(
                            projection_page[2]
                        )
                        val widthColumn = data.getColumnIndexOrThrow(
                            projection_page[3]
                        )
                        val heightColumn = data.getColumnIndexOrThrow(
                            projection_page[4]
                        )
                        val durationColumn = data.getColumnIndexOrThrow(
                            projection_page[5]
                        )
                        val sizeColumn = data.getColumnIndexOrThrow(
                            projection_page[6]
                        )
                        val folderNameColumn = data.getColumnIndexOrThrow(
                            projection_page[7]
                        )
                        val fileNameColumn = data.getColumnIndexOrThrow(
                            projection_page[8]
                        )
                        val bucketIdColumn = data.getColumnIndexOrThrow(
                            projection_page[9]
                        )
                        data.moveToFirst()
                        do {
                            val id = data.getLong(idColumn)
                            val absolutePath = data.getString(dataColumn)
                            val url =
                                if (SdkVersionUtils.checkedAndroid_Q()) getRealPathAndroid_Q(
                                    id
                                ) else absolutePath
//                            if (config.isFilterInvalidFile) {
//                                if (!PictureFileUtils.isFileExists(absolutePath)) {
//                                    continue
//                                }
//                            }
                            var mimeType = data.getString(mimeTypeColumn)
                            mimeType =
                                if (TextUtils.isEmpty(mimeType)) PictureMimeType.ofJPEG() else mimeType
                            // Here, it is solved that some models obtain mimeType and return the format of image / *,
                            // which makes it impossible to distinguish the specific type, such as mi 8,9,10 and other models
                            if (mimeType.endsWith("image/*")) {
                                mimeType = if (PictureMimeType.isContent(url)) {
                                    PictureMimeType.getImageMimeType(absolutePath)
                                } else {
                                    PictureMimeType.getImageMimeType(url)
                                }
//                                if (!config.isGif) {
//                                    if (PictureMimeType.isGif(mimeType)) {
//                                        continue
//                                    }
//                                }
                            }
//                            if (!config.isWebp) {
//                                if (mimeType.startsWith(PictureMimeType.ofWEBP())) {
//                                    continue
//                                }
//                            }
//                            if (!config.isBmp) {
//                                if (mimeType.startsWith(PictureMimeType.ofBMP())) {
//                                    continue
//                                }
//                            }
                            val width = data.getInt(widthColumn)
                            val height = data.getInt(heightColumn)
                            val duration = data.getLong(durationColumn)
                            val size = data.getLong(sizeColumn)
                            val folderName = data.getString(folderNameColumn)
                            val fileName = data.getString(fileNameColumn)
                            val bucket_id = data.getLong(bucketIdColumn)
//                            if (config.filterFileSize > 0) {
//                                if (size > config.filterFileSize * FILE_SIZE_UNIT) {
//                                    continue
//                                }
//                            }
//                            if (PictureMimeType.isHasVideo(mimeType)) {
//                                if (config.videoMinSecond > 0 && duration < config.videoMinSecond) {
//                                    // If you set the minimum number of seconds of video to display
//                                    continue
//                                }
//                                if (config.videoMaxSecond > 0 && duration > config.videoMaxSecond) {
//                                    // If you set the maximum number of seconds of video to display
//                                    continue
//                                }
//                                if (duration == 0L) {
//                                    //If the length is 0, the corrupted video is processed and filtered out
//                                    continue
//                                }
//                                if (size <= 0) {
//                                    // The video size is 0 to filter out
//                                    continue
//                                }
//                            }
                            val image = LocalMedia(
                                id,
                                url,
                                absolutePath,
                                fileName,
                                folderName,
                                duration,
//                                config.chooseMode,
                                PictureMimeType.ofImage(),
                                mimeType,
                                width,
                                height,
                                size,
                                bucket_id
                            )
                            image.itemType = AlbumFileType.FILE.ordinal
                            image.isSelected = false
                            image.isCamera = false
                            image.isAlbum = false
                            files.add(image)
                        } while (data.moveToNext())
                    }
//                    return MediaData(data.count > 0, result)
                }
            } catch (e: Exception) {
                AlbumLogUtil.e(e.message)
            } finally {
                if (data != null && !data.isClosed) {
                    data.close()
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

    /**
     * 获取查询条件
     */
    private fun getSelection(): String {
        return if (SdkVersionUtils.checkedAndroid_Q()) SELECTION_29 else SELECTION
    }

    /**
     * 获取查询条件属性值
     */
    private fun getSelectionArgs(): Array<String> {
        return arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString()
        )
    }

    private fun sortFolder(imageFolders: List<LocalMediaFolder>) {
        Collections.sort(imageFolders, Comparator { lhs, rhs ->
            if (lhs.data == null || rhs.data == null) {
                return@Comparator 0
            }
            val lSize: Int = lhs.imageNum
            val rSize: Int = rhs.imageNum
            rSize.compareTo(lSize)
        })
    }

    private fun getRealPathAndroid_Q(id: Long): String {
        return uri.buildUpon().appendPath(ValueOf.toString(id)).build().toString()
    }

    private fun getFirstUri(cursor: Cursor): String {
        val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID))
        return getRealPathAndroid_Q(id)
    }

    private fun getFirstUrl(cursor: Cursor): String {
        return cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA))
    }

    private fun getPageSelectionArgs(bucketId: Long): Array<String> {
        return getSelectionArgsForPageSingleMediaType(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE,
            bucketId
        )
    }

    private fun getPageSelection(bucketId: Long): String {
        if (bucketId == -1L) {
            return ("(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND " + MediaStore.MediaColumns.MIME_TYPE + NOT_GIF
                    + ") AND " + MediaStore.MediaColumns.SIZE + ">0")
        }
        return ("(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                + " AND " + MediaStore.MediaColumns.MIME_TYPE + NOT_GIF
                + ") AND " + COLUMN_BUCKET_ID + "=? AND " + MediaStore.MediaColumns.SIZE + ">0")
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

    fun getSystemCamera(): Observable<MutableList<LocalMedia>> {
        return Observable.create { emitter ->
            val file = LocalMedia()
            file.isCamera = true
            file.itemType = AlbumFileType.SYSTEM_CAMERA.ordinal
            val files: MutableList<LocalMedia> = ArrayList()
            files.add(file)
            emitter.onNext(files)
        }
    }

    fun getSystemAlbum(): Observable<MutableList<LocalMedia>> {
        return Observable.create { emitter ->
            val file = LocalMedia()
            file.isAlbum = true
            file.itemType = AlbumFileType.SYSTEM_ALBUM.ordinal
            val files: MutableList<LocalMedia> = ArrayList()
            files.add(file)
            emitter.onNext(files)
        }
    }

}