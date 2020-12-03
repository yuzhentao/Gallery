package com.yzt.gallery.repository

import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.yzt.gallery.Album
import com.yzt.gallery.R
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

    private val SELECTION_NOT_GIF = ("(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " AND " + MediaStore.MediaColumns.MIME_TYPE + NOT_GIF + ") AND " + MediaStore.MediaColumns.SIZE + ">0)" + GROUP_BY_BUCKET_Id)

    private val SELECTION_NOT_GIF_29 = (MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " AND " + MediaStore.MediaColumns.MIME_TYPE + NOT_GIF + " AND " + MediaStore.MediaColumns.SIZE + ">0")

    /**
     * Queries for images with the specified suffix
     */
    private val SELECTION_SPECIFIED_FORMAT = ("(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " AND " + MediaStore.MediaColumns.MIME_TYPE)

    /**
     * Queries for images with the specified suffix targetSdk>=29
     */
    private val SELECTION_SPECIFIED_FORMAT_29 = (MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " AND " + MediaStore.MediaColumns.MIME_TYPE)

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
                        if (SdkVersionUtils.checkedAndroid_Q()) { //API>=29
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
                        //                    allMediaFolder.ofAllType = config.chooseMode
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

    /**
     * 获取查询条件
     */
    private fun getSelection(): String {
        return if (SdkVersionUtils.checkedAndroid_Q()) SELECTION_NOT_GIF_29 else SELECTION_NOT_GIF
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

}