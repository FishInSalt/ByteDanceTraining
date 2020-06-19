package com.example.photoviewer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.example.ImageInfor.detailImageAdapter;
import com.example.ImageInfor.imageInfor;
import com.photoview.PhotoView;

import java.util.ArrayList;



public class PhotoDetailActivity extends AppCompatActivity
    implements GestureDetector.OnDoubleTapListener
        ,GestureDetector.OnGestureListener
        , ViewSwitcher.ViewFactory {

    private static String DEBUG_TAG = "Gestures";   //调试信息
    private GestureDetectorCompat mDetector;        //手势检测
    private static  boolean WidgetVisible = true;        //判断界面的工具栏和下面的detailRecycler是否可见

    private ArrayList<imageInfor> imageInforArrayList;  //存储图片信息的链表
    private int currentImageIndex;                      //当前显示的图片的索引

    private  int screenWidth ;                          //屏幕宽度
    private  int screenHeight ;                         //屏幕高度
    private ImageSwitcher imageSwitcher;                // 用于图片切换的组件
    private RecyclerView detailRecycler;                //下方的滚动的图片缩略图
    Toolbar toolbar;                                    //工具栏
    detailImageAdapter adapter;                         //下方recyclerView的适配器

    // 按下时和放开时的坐标数据
    private float down_x=0;
    private float down_y = 0;
    private float up_x = 0;
    private float up_y =0;

    private static Context context;     //该上下文用于向detailImageAdapter 传输应用的上下文，以获取应用的尺寸大小


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();

        setContentView(R.layout.photo_detail);

        //工具栏的设置
        toolbar = findViewById(R.id.detail_tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //工具栏的返回键
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
        // 编辑按钮的功能
        TextView editText = findViewById(R.id.edit_text);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhotoDetailActivity.this,PhotoEditActivity.class);
                intent.putExtra("photoPath",imageInforArrayList.get(currentImageIndex).getImagePath());
                startActivity(intent);
            }
        });

        //获取屏幕高度和宽度 用于上下和左右滑动手势检测
        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        screenWidth = outMetrics.widthPixels;
        screenHeight = outMetrics.heightPixels;





        //从主活动传来的图片链表信息
        Intent intent = getIntent();
        //imageInforArrayList = intent.getParcelableArrayListExtra("imageList");
        //final imageInfor currentFruit = intent.getParcelableExtra("currentImage");
         currentImageIndex = intent.getIntExtra("currentImageIndex",0);

        detailRecycler = findViewById(R.id.photo_thumbnail);
        imageSwitcher = findViewById(R.id.imageDetail_switcher);
        initImageList();
        initDetailRecycler();



         imageSwitcher.setFactory(this);
//         imageSwitcher.setOnTouchListener(new View.OnTouchListener() {
//             @Override
//             public boolean onTouch(View v, MotionEvent event) {
//                imageSwitcher.performClick();
//                 Log.d(DEBUG_TAG,"onTouch "+event.toString() );
//                mDetector.onTouchEvent(event);
//
//                 //Log.d(DEBUG_TAG,"onTouchEventbalabala");
//                 if(event.getAction()==MotionEvent.ACTION_DOWN){
//                     down_x=event.getX();
//                     down_y=event.getY();
//                     return true;
//                 }
//                 else if(event.getAction()==MotionEvent.ACTION_UP){
//                     up_x=event.getX();
//                     up_y=event.getY();
//                     if(up_x-down_x>=(screenWidth/2.15))
//                     {
//                         Log.d(DEBUG_TAG,"screen width: "+ screenWidth + " screen height: " +screenHeight);
//                         if(currentImageIndex == 0)
//                             return  true;
//                         else {
//                             --currentImageIndex;
//                             //动画效果
////                index= (index==0?arrayPic.length-1:index-1);
//                             imageSwitcher.setInAnimation(AnimationUtils.loadAnimation( PhotoDetailActivity.this,R.anim.slide_in_left));
//                             imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(PhotoDetailActivity.this,R.anim.slide_out_right));
//                             imageSwitcher.setImageURI(Uri.parse(imageInforArrayList.get(currentImageIndex).getImagePath()));
//                             detailRecycler.scrollToPosition(currentImageIndex);
//                         }
//
//                     }
//                     else if(down_x-up_x>=(screenWidth/2.15))
//                     {
//                         Log.d(DEBUG_TAG,"screen width: "+ screenWidth + " screen height: " +screenHeight);
//                         if(currentImageIndex == imageInforArrayList.size()-1)
//                             return  true;
//                         else {
//                             ++currentImageIndex;
////                index=index==arrayPic.length-1?0:index+1;
//                             imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(PhotoDetailActivity.this,R.anim.slide_in_right));
//                             imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(PhotoDetailActivity.this,R.anim.slide_out_left));
//                             imageSwitcher.setImageURI(Uri.parse(imageInforArrayList.get(currentImageIndex).getImagePath()));
//                             detailRecycler.scrollToPosition(currentImageIndex);
//                         }
//                     }
//                     else if(Math.abs(up_y-down_y) >(screenHeight/3) )
//                     {
//                         Log.d(DEBUG_TAG,"finish ,distanceY = "+ Math.abs(up_y-down_y));
//                         imageSwitcher.setAnimation(AnimationUtils.loadAnimation(PhotoDetailActivity.this,R.anim.exit_down));
////                image_switcher.setImageResource(arrayPic[index]);
////                Intent intent=new Intent(MainActivity.this, ExitPageActivity.class);
////                startActivity(intent);
//                         finish();
//                     }
//                     return true;
//                 }
////        else
////        {
////            mDetector.onTouchEvent(event);
////
////        }
//
//
//
//                 return true;
//             }
//
//         });
         //imageSwitcher.setClickable(true);
//         imageSwitcher.setOnClickListener(new View.OnClickListener() {
//             @Override
//             public void onClick(View v) {
//                 if(listView == true) {
//                     detailRecycler.setAlpha(0);
//                     listView = false;
//                 }
//                 else{
//                     detailRecycler.setAlpha(1);
//                     listView = true;
//                 }
//
//             }
//         });


        //StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.HORIZONTAL);





        //recyclerView页滚动优化


        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(detailRecycler);





//        Log.i("fruitlist","list size is "+ imageInforArrayList.size());
//        for (imageInfor fruit: imageInforArrayList
//             ) {
//            Log.i("fruitlist",fruit.getName());
//
//        }

        //imageSwitcher.setOnScrollChangeListener();
        // 手势检测器
        mDetector = new GestureDetectorCompat(this,this);
        mDetector.setOnDoubleTapListener(this);

    }

    public static  Context getContext()
    {
        return context;
    }

    //初始化详情页的缩略图滚动条
    public void initDetailRecycler()
    {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        detailRecycler.setLayoutManager(layoutManager);
        adapter = new detailImageAdapter(imageInforArrayList);
        //设置item的点击监听事件，把点击的位置传给当前位置索引，使得左右切图和缩略图切图位置保持一致
        adapter.setOnItemClickListener(new detailImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d(DEBUG_TAG,"onItemClick:  "+ position );
                currentImageIndex = position;
            }
        });

        detailRecycler.setAdapter(adapter);
        detailRecycler.smoothScrollToPosition(currentImageIndex + detailImageAdapter.getItemNum()/2);
//        adapter.highlightItem(getMiddlePosition());
    }
//

    @Override
    public  boolean onTouchEvent(MotionEvent event)
    {
        mDetector.onTouchEvent(event);
        //得到当前的缩放情况
        PhotoView imageView = (PhotoView) imageSwitcher.getCurrentView();
        float currentScale = imageView.getScale();

        Log.d(DEBUG_TAG,"onTouchEvent"+event.toString());


        if(event.getAction()==MotionEvent.ACTION_DOWN){
            down_x=event.getX();
            down_y=event.getY();
            return false;
        }
        else if(event.getAction()==MotionEvent.ACTION_UP) {
            up_x = event.getX();
            up_y = event.getY();
            if (currentScale == 1f) {
                if (up_x - down_x >= (screenWidth / 3)) {
                    if (currentImageIndex == 0)
                        return true;
                    else {
                        --currentImageIndex;
                        //动画效果
                        imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_left));
                        imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_right));
                        imageSwitcher.setImageURI(Uri.parse(imageInforArrayList.get(currentImageIndex).getImagePath()));
                        //处理两端的情况
                        if (currentImageIndex>=(detailImageAdapter.getItemNum()/2))
                            detailRecycler.scrollToPosition(currentImageIndex - detailImageAdapter.getItemNum()/2);
                        adapter.setCurrentIndex(currentImageIndex);

                    }

                } else if (down_x - up_x >= (screenWidth / 3)) {
                    Log.d(DEBUG_TAG, "screen width: " + screenWidth + " screen height: " + screenHeight);
                    if (currentImageIndex == imageInforArrayList.size() - 1)
                        return true;
                    else {
                        ++currentImageIndex;
                        imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_right));
                        imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_left));
                        imageSwitcher.setImageURI(Uri.parse(imageInforArrayList.get(currentImageIndex).getImagePath()));
                        //处理两端的情况
                        if(currentImageIndex<(imageInforArrayList.size()-detailImageAdapter.getItemNum()/2))
                            detailRecycler.scrollToPosition(currentImageIndex + detailImageAdapter.getItemNum()/2);
                        adapter.setCurrentIndex(currentImageIndex);

                    }
                } else if (Math.abs(up_y - down_y) > screenHeight / 4) {
                    Log.d(DEBUG_TAG, "distanceY"+Math.abs(up_y - down_y));
                    imageSwitcher.setAnimation(AnimationUtils.loadAnimation(this, R.anim.exit_down));
                    finish();
                }
            }
            return true;
        }


        return  super.onTouchEvent(event);
    }







    @Override
    public boolean onDown(MotionEvent e) {
        //Log.d(DEBUG_TAG,"onDown: " + e.toString());
        return true;                            //返回true使程序能继续检测后续的动作
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.d(DEBUG_TAG,"onLongPress: " + e.toString());
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d(DEBUG_TAG,"onFling: " + e1.toString()+e2.toString());

        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.d(DEBUG_TAG,"onScroll: " + e1.toString()+e2.toString());
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        Log.d(DEBUG_TAG,"onShowPress: " + e.toString());

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.d(DEBUG_TAG,"onSingleTapUp: " + e.toString());
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Log.d(DEBUG_TAG,"onDoubleTap: " + e.toString());

        //工具栏和缩略图列表与大图的交互，双击时大图放大并且工具栏和缩略图设为不可见
        if(WidgetVisible == true && !isInView(detailRecycler,(int)e.getX(),(int)e.getY())&&!isInView(toolbar,(int)e.getX(),(int)e.getY())
        ) {
            //detailRecycler.setAlpha(0);
            detailRecycler.setVisibility(View.INVISIBLE);

            toolbar.setVisibility(View.INVISIBLE);
            WidgetVisible = false;

        }


        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        Log.d(DEBUG_TAG,"onDoubleTapEvent: " + e.toString());
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        Log.d(DEBUG_TAG,"onSingleTapConfirmed: " + e.toString());

        //单击图片时使缩略图和隐形或者显形
        if(WidgetVisible == true && !isInView(detailRecycler,(int)e.getX(),(int)e.getY())&&!isInView(toolbar,(int)e.getX(),(int)e.getY())) {
            WidgetVisible = false;
            detailRecycler.setVisibility(View.INVISIBLE);

            toolbar.setVisibility(View.INVISIBLE);
        }
        else{
            WidgetVisible = true;
            detailRecycler.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.VISIBLE);

        }


        return true;
    }

    int x = 0;
    int y = 0;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) { //拦截手势信号并处理


        //this.mDetector.onTouchEvent(ev);

        if(ev.getAction() == MotionEvent.ACTION_DOWN)
        {
            x = (int)ev.getRawX();
            y = (int)ev.getRawY();
        }

            if (isInView(detailRecycler, x, y)) {
                if(WidgetVisible)           //仅当recycler可见时能处理
                    detailRecycler.onTouchEvent(ev);
                mDetector.onTouchEvent(ev);
            }
            else
                onTouchEvent(ev);

        super.dispatchTouchEvent(ev);


        return false;
    }


    @Override
    public View makeView() {
        PhotoView i = new PhotoView(imageSwitcher.getContext());
        i.setLayoutParams(new ImageSwitcher.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        i.setImageURI(Uri.parse(imageInforArrayList.get(currentImageIndex).getImagePath()));
        return  i;
    }
    //检测触摸事件是否发生在某个组件
    private boolean isInView(View view,int x,int y)
    {
        if(view == null)
            return false;
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left +view.getMeasuredWidth();
        int bottom = top +view.getMeasuredHeight();
        if(y>=top&&y<=bottom&&x>=left&&x<=right)
            return true;

        return false;
    }



    private void initImageList()
    {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        imageInforArrayList = new ArrayList<>();

        final String[] columns = {MediaStore.Images.Media.DATA ,MediaStore.Images.Media._ID};
        final String orderBy = MediaStore.Images.Media.DATE_ADDED + " desc";  //倒叙查询
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy);
        int count = cursor.getCount();
        String[] arrPath = new String[count];

        for (int i = 0; i < count; i++) {
            cursor.moveToPosition(i);
            int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            int idColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            arrPath[i] = cursor.getString(dataColumnIndex);
            Uri fruitUri  = Uri.parse(arrPath[i]);
            String[] fruitInfor = arrPath[i].split("/");
            String fruitName = fruitInfor[fruitInfor.length-1];

            imageInfor fruitItem = new imageInfor(fruitName,arrPath[i],cursor.getLong(idColumnIndex));
            imageInforArrayList.add(fruitItem);
            //Log.i("PATH", arrPath[i]);
        }
        cursor.close();


    }
    //当编辑功能中保存了新的图片，这里的缩略图链表应该更新
    @Override
    protected void onStart() {
        super.onStart();
        initImageList();
        initDetailRecycler();
    }


    public void wifip2p(View view){
        Intent intent = new Intent(this, WifiActivity.class);
        intent.putExtra("photoPath",imageInforArrayList.get(currentImageIndex).getImagePath());
        startActivity(intent);
    }
}
