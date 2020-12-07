package com.yzt.gallery.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class LocalMediaFolder implements Parcelable {

    private long bucketId = -1;
    private String name;
    private String firstImagePath;
    private int imageNum;
    private int checkedNum;
    private boolean isSelected;
    private int ofAllType = -1;
    private boolean isCameraFolder;
    private List<LocalMedia> data = new ArrayList<>();
    private int currentDataPage;
    private boolean isHasMore;

    public long getBucketId() {
        return bucketId;
    }

    public void setBucketId(long bucketId) {
        this.bucketId = bucketId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstImagePath() {
        return firstImagePath;
    }

    public void setFirstImagePath(String firstImagePath) {
        this.firstImagePath = firstImagePath;
    }

    public int getImageNum() {
        return imageNum;
    }

    public void setImageNum(int imageNum) {
        this.imageNum = imageNum;
    }

    public int getCheckedNum() {
        return checkedNum;
    }

    public void setCheckedNum(int checkedNum) {
        this.checkedNum = checkedNum;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getOfAllType() {
        return ofAllType;
    }

    public void setOfAllType(int ofAllType) {
        this.ofAllType = ofAllType;
    }

    public boolean isCameraFolder() {
        return isCameraFolder;
    }

    public void setCameraFolder(boolean cameraFolder) {
        isCameraFolder = cameraFolder;
    }

    public List<LocalMedia> getData() {
        return data;
    }

    public void setData(List<LocalMedia> data) {
        this.data = data;
    }

    public int getCurrentDataPage() {
        return currentDataPage;
    }

    public void setCurrentDataPage(int currentDataPage) {
        this.currentDataPage = currentDataPage;
    }

    public boolean isHasMore() {
        return isHasMore;
    }

    public void setHasMore(boolean hasMore) {
        isHasMore = hasMore;
    }

    public LocalMediaFolder() {

    }

    protected LocalMediaFolder(Parcel in) {
        bucketId = in.readLong();
        name = in.readString();
        firstImagePath = in.readString();
        imageNum = in.readInt();
        checkedNum = in.readInt();
        isSelected = in.readByte() != 0;
        ofAllType = in.readInt();
        isCameraFolder = in.readByte() != 0;
        data = in.createTypedArrayList(LocalMedia.CREATOR);
        currentDataPage = in.readInt();
        isHasMore = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(bucketId);
        dest.writeString(name);
        dest.writeString(firstImagePath);
        dest.writeInt(imageNum);
        dest.writeInt(checkedNum);
        dest.writeByte((byte) (isSelected ? 1 : 0));
        dest.writeInt(ofAllType);
        dest.writeByte((byte) (isCameraFolder ? 1 : 0));
        dest.writeTypedList(data);
        dest.writeInt(currentDataPage);
        dest.writeByte((byte) (isHasMore ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LocalMediaFolder> CREATOR = new Creator<LocalMediaFolder>() {
        @Override
        public LocalMediaFolder createFromParcel(Parcel in) {
            return new LocalMediaFolder(in);
        }

        @Override
        public LocalMediaFolder[] newArray(int size) {
            return new LocalMediaFolder[size];
        }
    };

}