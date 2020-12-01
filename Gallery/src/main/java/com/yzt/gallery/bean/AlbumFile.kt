package com.yzt.gallery.bean

import android.os.Parcel
import android.os.Parcelable

/**
 * 相册文件
 *
 * @author yzt 2020/4/22
 */
class AlbumFile() : Parcelable {

    var position: Int = 0
    var itemType: Int = 0
    var isSelected: Boolean = false
    var selectedNo: Int = 0//选中的号码
    var isCamera: Boolean = false
    var isAlbum: Boolean = false
    var folder: String? = null
    var path: String? = null
    var name: String? = null
    var date: Long = 0
    var type: String? = null
    var size: Long = 0
    var width: Int = 0
    var height: Int = 0

    constructor(parcel: Parcel) : this() {
        position = parcel.readInt()
        itemType = parcel.readInt()
        isSelected = parcel.readByte() != 0.toByte()
        selectedNo = parcel.readInt()
        isCamera = parcel.readByte() != 0.toByte()
        isAlbum = parcel.readByte() != 0.toByte()
        folder = parcel.readString()
        path = parcel.readString()
        name = parcel.readString()
        date = parcel.readLong()
        type = parcel.readString()
        size = parcel.readLong()
        width = parcel.readInt()
        height = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(position)
        parcel.writeInt(itemType)
        parcel.writeByte(if (isSelected) 1 else 0)
        parcel.writeInt(selectedNo)
        parcel.writeByte(if (isCamera) 1 else 0)
        parcel.writeByte(if (isAlbum) 1 else 0)
        parcel.writeString(folder)
        parcel.writeString(path)
        parcel.writeString(name)
        parcel.writeLong(date)
        parcel.writeString(type)
        parcel.writeLong(size)
        parcel.writeInt(width)
        parcel.writeInt(height)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AlbumFile> {
        override fun createFromParcel(parcel: Parcel): AlbumFile {
            return AlbumFile(parcel)
        }

        override fun newArray(size: Int): Array<AlbumFile?> {
            return arrayOfNulls(size)
        }
    }

}