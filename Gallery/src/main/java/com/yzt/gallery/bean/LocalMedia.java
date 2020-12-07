package com.yzt.gallery.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class LocalMedia implements Parcelable {

    private int itemType;
    private boolean isSelected;
    private int selectedNo;//选中的号码
    private boolean isCamera;
    private boolean isAlbum;

    private long id;
    private String path;//原始路径
    private String realPath;//真实路径，但是无法从Android Q访问
    private String androidQToPath;//路径（此字段仅在Android Q版本中返回）
    private long duration;//视频时长
    public int position;
    private String mimeType;//媒体资源类型
    private int chooseModel;//相册选择模式
    private int width;
    private int height;
    private long size;
    private String fileName;
    private String parentFolderName;
    private int orientation = -1;
    private long bucketId = -1;

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getSelectedNo() {
        return selectedNo;
    }

    public void setSelectedNo(int selectedNo) {
        this.selectedNo = selectedNo;
    }

    public boolean isCamera() {
        return isCamera;
    }

    public void setCamera(boolean camera) {
        isCamera = camera;
    }

    public boolean isAlbum() {
        return isAlbum;
    }

    public void setAlbum(boolean album) {
        isAlbum = album;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRealPath() {
        return realPath;
    }

    public void setRealPath(String realPath) {
        this.realPath = realPath;
    }

    public String getAndroidQToPath() {
        return androidQToPath;
    }

    public void setAndroidQToPath(String androidQToPath) {
        this.androidQToPath = androidQToPath;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getMimeType() {
        return TextUtils.isEmpty(mimeType) ? "image/jpeg" : mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public int getChooseModel() {
        return chooseModel;
    }

    public void setChooseModel(int chooseModel) {
        this.chooseModel = chooseModel;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getParentFolderName() {
        return parentFolderName;
    }

    public void setParentFolderName(String parentFolderName) {
        this.parentFolderName = parentFolderName;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public long getBucketId() {
        return bucketId;
    }

    public void setBucketId(long bucketId) {
        this.bucketId = bucketId;
    }

    public LocalMedia() {

    }

    public LocalMedia(long id, String path, String absolutePath, String fileName, String parentFolderName, long duration, int chooseModel,
                      String mimeType, int width, int height, long size, long bucketId) {
        this.id = id;
        this.path = path;
        this.realPath = absolutePath;
        this.fileName = fileName;
        this.parentFolderName = parentFolderName;
        this.duration = duration;
        this.chooseModel = chooseModel;
        this.mimeType = mimeType;
        this.width = width;
        this.height = height;
        this.size = size;
        this.bucketId = bucketId;
    }

    protected LocalMedia(Parcel in) {
        itemType = in.readInt();
        isSelected = in.readByte() != 0;
        selectedNo = in.readInt();
        isCamera = in.readByte() != 0;
        isAlbum = in.readByte() != 0;
        id = in.readLong();
        path = in.readString();
        realPath = in.readString();
        androidQToPath = in.readString();
        duration = in.readLong();
        position = in.readInt();
        mimeType = in.readString();
        chooseModel = in.readInt();
        width = in.readInt();
        height = in.readInt();
        size = in.readLong();
        fileName = in.readString();
        parentFolderName = in.readString();
        orientation = in.readInt();
        bucketId = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(itemType);
        dest.writeByte((byte) (isSelected ? 1 : 0));
        dest.writeInt(selectedNo);
        dest.writeByte((byte) (isCamera ? 1 : 0));
        dest.writeByte((byte) (isAlbum ? 1 : 0));
        dest.writeLong(id);
        dest.writeString(path);
        dest.writeString(realPath);
        dest.writeString(androidQToPath);
        dest.writeLong(duration);
        dest.writeInt(position);
        dest.writeString(mimeType);
        dest.writeInt(chooseModel);
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeLong(size);
        dest.writeString(fileName);
        dest.writeString(parentFolderName);
        dest.writeInt(orientation);
        dest.writeLong(bucketId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LocalMedia> CREATOR = new Creator<LocalMedia>() {
        @Override
        public LocalMedia createFromParcel(Parcel in) {
            return new LocalMedia(in);
        }

        @Override
        public LocalMedia[] newArray(int size) {
            return new LocalMedia[size];
        }
    };

}