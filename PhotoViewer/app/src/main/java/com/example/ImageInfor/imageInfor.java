package com.example.ImageInfor;

import android.os.Parcel;
import android.os.Parcelable;

public final class imageInfor implements Parcelable {
    private  String name;
    private String  imagePath;
    private  long imageId;



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
