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

public class detailImageAdapter extends RecyclerView.Adapter<detailImageAdapter.ViewHolder> {
    public static final int ITEM_NUM = 5;
    private int start = 0;
    private int end = 0;
    private  int currentIndex = -1;
    private ArrayList<imageInfor>  imageInforArrayList;
    private ViewGroup.LayoutParams lp;

    public detailImageAdapter(ArrayList<imageInfor> list)
    {
        imageInforArrayList = list;
        end = list.size()-1;


    }

    public interface OnItemClickListener{
        void onItemClick(View view,int position);
    }

    private OnItemClickListener mOnItemClickListener;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item,parent,false);

        lp = view.getLayoutParams();
        lp.width = getItemWidth();
        final ViewHolder holder = new ViewHolder(view);
        holder.itemView.getBackground().setAlpha(100);




        return  holder;


    }

    public static int getItemWidth(){
        DisplayMetrics displayMetrics = PhotoDetailActivity.getContext().getResources().getDisplayMetrics();
        return  displayMetrics.widthPixels/ITEM_NUM;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        imageInfor imgInfor = imageInforArrayList.get(position);



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

//                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(lp.width+100,lp.height);
//                v.setLayoutParams(layoutParams);


            }
        });
//        if(isSelected(position))
//        {
//
//            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(lp.width+100,lp.height);
//            holder.itemView.setLayoutParams(layoutParams);
//        }
//        else
//        {
//            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(lp.width,lp.height);
//            holder.itemView.setLayoutParams(layoutParams);
//            ;
//        }



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

//    public void highlightItem(int position){
//        currentIndex = position;
//        int offset = ITEM_NUM/2;
//        for(int i = position-offset;i<position+offset;++i)
//            notifyItemChanged(i);
//    }

    public ViewGroup.LayoutParams getLp(){return lp;}
    public static int  getItemNum(){return ITEM_NUM;}

    @Override
    public int getItemCount() {
        return imageInforArrayList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView photoThumbnail;
        public ViewHolder(View view)
        {
            super(view);
            photoThumbnail = (ImageView) view.findViewById(R.id.image_thumbnail);
            //photoThumbnail.setTag(this);
        }
        public ImageView getPhotoThumbnail(){return  photoThumbnail;}
    }

}
