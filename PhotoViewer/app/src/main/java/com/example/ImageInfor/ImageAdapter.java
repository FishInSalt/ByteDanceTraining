package com.example.ImageInfor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photoviewer.PhotoViewerMainActivity;
import com.example.photoviewer.R;

import java.util.ArrayList;




public  class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder>  {
    private ArrayList<imageInfor> imageInforList;
    private int currentIndex=0;

    public interface OnItemClickListener{
        void onItemClick(View view,int position);
    }

    private  OnItemClickListener mOnItemClickListener;
    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        //TextView fruitName;
        //Context context;

        View fruitView;
        public ViewHolder(View view)
        {
            super(view);
            fruitView = view;
            image = (ImageView) view.findViewById(R.id.image_thumbnail);
            //fruitName = (TextView) view.findViewById(R.id.fruit_name);

        }


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





//        holder.image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if(v.getContext().getClass().getSimpleName().equals( PhotoViewerMainActivity.class.getSimpleName())) {
//
//
//                    int position = holder.getAdapterPosition();
//                    imageInfor fruit = imageInforList.get(position);
//                    Intent intent = new Intent("com.example.photoviewer.ACTION_START");
//                    intent.putParcelableArrayListExtra("imageList", imageInforList);
//                    intent.putExtra("currentImage", fruit);
//                    intent.putExtra("currentImageIndex",position);
//                    v.getContext().startActivity(intent);
//                }
//                else
//                {
//                //    View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_detail,parent,false);
//                    int position = holder.getAdapterPosition();
//                    imageInfor imageInfor = imageInforList.get(position);
//                    ImageSwitcher fruitImage = v.getRootView().findViewById(R.id.imageDetail_switcher);
//                    fruitImage.setImageURI(Uri.parse( imageInfor.getImagePath()));
//                    currentIndex = position;
//                }
//            }
//
//        });

        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        imageInfor fruit = imageInforList.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(v.getContext().getClass().getSimpleName().equals( PhotoViewerMainActivity.class.getSimpleName())) {



                    imageInfor fruit = imageInforList.get(position);
                    Intent intent = new Intent("com.example.photoviewer.ACTION_START");
                    intent.putParcelableArrayListExtra("imageList", imageInforList);
                    intent.putExtra("currentImage", fruit);
                    intent.putExtra("currentImageIndex",position);
                    v.getContext().startActivity(intent);
                }



                    else
                    {
                        if(mOnItemClickListener!=null&& currentIndex!=position)
                        {
                        // View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_detail,parent,false);

                        imageInfor imageInfor = imageInforList.get(position);
                        ImageSwitcher imageSwitcher = v.getRootView().findViewById(R.id.imageDetail_switcher);
                        imageSwitcher.setImageURI(Uri.parse( imageInfor.getImagePath()));
                        mOnItemClickListener.onItemClick(v,position );
                        currentIndex = position;
                    }
                }

            }
        });
        // holder.fruitName.setText(fruit.getName());
        Bitmap thumbnail = android.provider.MediaStore.Images.Thumbnails.getThumbnail(holder.fruitView.getContext().getContentResolver(),fruit.getImageId(),
                MediaStore.Images.Thumbnails.MINI_KIND,new BitmapFactory.Options());
        holder.image.setImageBitmap(thumbnail);
        //holder.image.setTag(position);
        //holder.fruitImage.setBackgroundColor(Color.RED);
    }

    @Override
    public int getItemCount() {
        return imageInforList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        this.mOnItemClickListener = listener;
    }

    public void setCurrentIndex(int index)
    {
        currentIndex = index;
    }


}
