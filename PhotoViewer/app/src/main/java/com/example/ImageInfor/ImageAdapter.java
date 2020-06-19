package com.example.ImageInfor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewGroupCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photoviewer.PhotoDetailActivity;
import com.example.photoviewer.PhotoViewerMainActivity;
import com.example.photoviewer.R;

import java.util.ArrayList;




public  class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder>  {
    private ArrayList<imageInfor> imageInforList; // 存储图片资源的数据信息链表
    private int currentIndex=0;     //对应运行时被选中的组件的索引



    public interface OnItemClickListener{
        void onItemClick(View view,int position);
    }

    private  OnItemClickListener mOnItemClickListener;  //该成员用于通过设置监听器传递当前被选中的索引图的索引


    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;

        public ViewHolder(View view)
        {
            super(view);
            image = (ImageView) view.findViewById(R.id.image_thumbnail);


        }

        public ImageView getImage(){return image;}


    }


    public ImageAdapter(ArrayList<imageInfor> objects)
    {

        imageInforList = objects;

    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.photo_item, parent, false);


        final ViewHolder holder = new ViewHolder(view);
        holder.itemView.getBackground().setAlpha(100);          //设置背景的透明度







        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        imageInfor photoInfor = imageInforList.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {//设置点击事件，用于创建并传递数据给详情页活动
            @Override
            public void onClick(View v) {

                //if(v.getContext().getClass().getSimpleName().equals( PhotoViewerMainActivity.class.getSimpleName())) {


                    //点击开始详情页活动
                    imageInfor fruit = imageInforList.get(position);
                    Intent intent = new Intent("com.example.photoviewer.ACTION_START");
                    //intent.putParcelableArrayListExtra("imageList", imageInforList);
                    //intent.putExtra("currentImage", fruit);
                    intent.putExtra("currentImageIndex",position);
                    v.getContext().startActivity(intent);
                //}



//                    else
//                    {
//                        if(mOnItemClickListener!=null&& currentIndex!=position)
//                        {
//                        // View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_detail,parent,false);
//
//                        imageInfor imageInfor = imageInforList.get(position);
//                        ImageSwitcher imageSwitcher = v.getRootView().findViewById(R.id.imageDetail_switcher);
//                        imageSwitcher.setImageURI(Uri.parse( imageInfor.getImagePath()));
//                        //传当前的缩略图位置
//                        mOnItemClickListener.onItemClick(v,position );
//                        currentIndex = position;
//                    }
//                }

            }
        });
        //设置holder的内容，即缩略图
        Bitmap thumbnail = android.provider.MediaStore.Images.Thumbnails.getThumbnail(holder.image.getContext().getContentResolver(),photoInfor.getImageId(),
                MediaStore.Images.Thumbnails.MINI_KIND,new BitmapFactory.Options());
        holder.image.setImageBitmap(thumbnail);

    }

    @Override
    public int getItemCount() {
        return imageInforList.size();
    }

    // 设置缩略图的item监听器，用于传输当前item的位置
    public void setOnItemClickListener(OnItemClickListener listener)
    {
        this.mOnItemClickListener = listener;
    }

    public void setCurrentIndex(int index)
    {
        currentIndex = index;
    }


}
