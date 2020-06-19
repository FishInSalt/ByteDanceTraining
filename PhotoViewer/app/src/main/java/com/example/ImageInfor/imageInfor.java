package com.example.ImageInfor;

import android.os.Parcel;
import android.os.Parcelable;

/*
    该类用于存储媒体图片的信息，包括名字，路径等
    实现 parcelable接口，使其可以在不同活动传输
 */


public final class imageInfor implements Parcelable {
    private  String name;       //名称
    private String  imagePath;  //路径
    private  long imageId;      //资源id



    public imageInfor(String name, String imagePath, long imageId)
    {
        this.name = name;
        this.imagePath = imagePath;
        this.imageId = imageId;
 //       this.thumbnail = thumbnail;
    }


    public String getName()
    {
        return  name;
    }
    public  String getImagePath()
    {
        return imagePath;
    }

//    public  Bitmap getThumbnail()
//    {
//        return  thumbnail;
//    }

    public long getImageId()
    {
        return  imageId;
    }



    //parcelable的实现

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(imagePath);
        dest.writeLong(imageId);
    }
    public static final Parcelable.Creator<imageInfor> CREATOR = new Creator<imageInfor>() {
        @Override
        public imageInfor createFromParcel(Parcel source) {
            String name = source.readString();
            String imagePath = source.readString();
            long imageId = source.readLong();
            imageInfor fruit = new imageInfor(name,imagePath,imageId);
            return fruit;
        }

        @Override
        public imageInfor[] newArray(int size) {
            return new imageInfor[size];
        }

    };
}
