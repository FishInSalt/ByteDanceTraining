package com.example.ImageInfor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageSwitcher;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photoviewer.PhotoDetailActivity;
import com.example.photoviewer.R;

import java.security.spec.PSSParameterSpec;
import java.util.ArrayList;

/*
    该adapter用于显示详情页中的缩略图滚动条
 */

public class detailImageAdapter extends RecyclerView.Adapter<detailImageAdapter.ViewHolder> {
    public static final int ITEM_NUM = 5;           //缩略图滚动条能显示个数
    private  int currentIndex = -1;                 //当前的索引值
    private ArrayList<imageInfor>  imageInforArrayList;  // 图片资源的数链
    //private ViewGroup.LayoutParams lp;

    public detailImageAdapter(ArrayList<imageInfor> list)
    {
        imageInforArrayList = list;



    }
    //用于监听viewholder的点击事件,然后传当前点击的位置position到详情页
    public interface OnItemClickListener{
        void onItemClick(View view,int position);
    }

    private OnItemClickListener mOnItemClickListener;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item,parent,false);

        ViewGroup.LayoutParams lp = view.getLayoutParams();
        lp.width = getItemWidth();
        final ViewHolder holder = new ViewHolder(view);
        holder.itemView.getBackground().setAlpha(100);




        return  holder;


    }
    //缩略图滚动条每个item的宽度
    public static int getItemWidth(){
        DisplayMetrics displayMetrics = PhotoDetailActivity.getContext().getResources().getDisplayMetrics();
        return  displayMetrics.widthPixels/ITEM_NUM;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        imageInfor imgInfor = imageInforArrayList.get(position);


        //获取缩略图资源
        Bitmap thumbnail = android.provider.MediaStore.Images.Thumbnails.getThumbnail(holder.getPhotoThumbnail().getContext().getContentResolver(),imgInfor.getImageId(),
                MediaStore.Images.Thumbnails.MINI_KIND,new BitmapFactory.Options());
        holder.getPhotoThumbnail().setImageBitmap(thumbnail);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnItemClickListener!=null&& currentIndex!=position)
                {
                    // View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_detail,parent,false);

                    imageInfor imageInfor = imageInforArrayList.get(position);
                    ImageSwitcher imageSwitcher = v.getRootView().findViewById(R.id.imageDetail_switcher);
                    imageSwitcher.setImageURI(Uri.parse( imageInfor.getImagePath()));
                    //传当前的缩略图位置
                    mOnItemClickListener.onItemClick(v,position );
                    currentIndex = position;
                }




            }
        });




    }
    public void setCurrentIndex(int index)
    {
        currentIndex = index;
    }
    public void setOnItemClickListener(OnItemClickListener listener)
    {
        this.mOnItemClickListener = listener;
    }

    //判断是否被选中
    public boolean isSelected(int position){
        return  currentIndex == position;
    }



   // public ViewGroup.LayoutParams getLp(){return lp;}
    public static int  getItemNum(){return ITEM_NUM;}

    @Override
    public int getItemCount() {
        return imageInforArrayList.size();
    }
    //内部类
    static class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView photoThumbnail;
        public ViewHolder(View view)
        {
            super(view);
            photoThumbnail = (ImageView) view.findViewById(R.id.image_thumbnail);

        }
        public ImageView getPhotoThumbnail(){return  photoThumbnail;}
    }

}
