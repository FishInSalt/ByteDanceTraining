package com.example.photoviewer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.PeriodicSync;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.example.ImageInfor.detailImageAdapter;
import com.example.ImageInfor.imageInfor;
import com.example.ImageInfor.ImageAdapter;
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

    private static Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();

        setContentView(R.layout.photo_detail);

        //Toolbar toolbar =
        //工具栏的设置
        toolbar = findViewById(R.id.detail_tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //工具栏的返回键
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //toolbar.bringToFront();           //将toolbar放在最上层显示
        //toolbar.setAlpha(1f);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        TextView editText = findViewById(R.id.edit_text);

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhotoDetailActivity.this,PhotoEditActivity.class);
                intent.putExtra("photoPath",imageInforArrayList.get(currentImageIndex).getImagePath());
                startActivity(intent);
            }
        });

        //获取屏幕高度和宽度
        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        screenWidth = outMetrics.widthPixels;
        screenHeight = outMetrics.heightPixels;

        detailRecycler = findViewById(R.id.photo_thumbnail);
//        imageSwitcher.onInterceptTouchEvent()

        //从主活动传来的图片链表信息
        Intent intent = getIntent();
        imageInforArrayList = intent.getParcelableArrayListExtra("imageList");
        final imageInfor currentFruit = intent.getParcelableExtra("currentImage");
         currentImageIndex = intent.getIntExtra("currentImageIndex",0);
         imageSwitcher = findViewById(R.id.imageDetail_switcher);
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


        initDetailRecycler();
        //LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        //layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);



//        detailRecycler.setLayoutManager(layoutManager);
//        //detailRecycler.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        //ImageAdapter myAdapter =  new ImageAdapter(imageInforArrayList);
//        detailImageAdapter detailImageAdapter = new detailImageAdapter(imageInforArrayList);

        //传视图下面的缩略图的位置，并修改当前的位置为缩略图位置

        //detailRecycler.setAdapter(myAdapter);
//        detailRecycler.setAdapter(detailImageAdapter);
//
//        detailRecycler.scrollToPosition(currentImageIndex);

//        detailRecycler.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(listView == false)
//                {
//                    listView = true;
//                    detailRecycler.setAlpha(1);
//                    detailRecycler.setClickable(true);
//
//                }
//
//            }
//        });





        Log.i("fruitlist","list size is "+ imageInforArrayList.size());
        for (imageInfor fruit: imageInforArrayList
             ) {
            Log.i("fruitlist",fruit.getName());

        }

        //imageSwitcher.setOnScrollChangeListener();
        // for testing gestures Detector;
        mDetector = new GestureDetectorCompat(this,this);
        mDetector.setOnDoubleTapListener(this);

    }

    public static  Context getContext()
    {
        return context;
    }

    public void initDetailRecycler()
    {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        detailRecycler.setLayoutManager(layoutManager);
        adapter = new detailImageAdapter(imageInforArrayList);
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
    private int getMiddlePosition(){
        return getScrollPosition()+(detailImageAdapter.getItemNum()/2);
    }


    private int getScrollPosition(){
        return (int)((double)detailRecycler.computeHorizontalScrollOffset()/ (double)detailImageAdapter.getItemWidth());
    }

    @Override
    public  boolean onTouchEvent(MotionEvent event)
    {
        mDetector.onTouchEvent(event);

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
//                index= (index==0?arrayPic.length-1:index-1);
                        imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_left));
                        imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_right));
                        imageSwitcher.setImageURI(Uri.parse(imageInforArrayList.get(currentImageIndex).getImagePath()));
                        //处理两端的情况
                        if (currentImageIndex>=(detailImageAdapter.getItemNum()/2))
                            detailRecycler.scrollToPosition(currentImageIndex - detailImageAdapter.getItemNum()/2);
                        //ImageAdapter adapter = (ImageAdapter) detailRecycler.getAdapter();
                        adapter.setCurrentIndex(currentImageIndex);

                    }

                } else if (down_x - up_x >= (screenWidth / 3)) {
                    Log.d(DEBUG_TAG, "screen width: " + screenWidth + " screen height: " + screenHeight);
                    if (currentImageIndex == imageInforArrayList.size() - 1)
                        return true;
                    else {
                        ++currentImageIndex;
//                index=index==arrayPic.length-1?0:index+1;
                        imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_right));
                        imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_left));
                        imageSwitcher.setImageURI(Uri.parse(imageInforArrayList.get(currentImageIndex).getImagePath()));
                        //处理两端的情况
                        if(currentImageIndex<(imageInforArrayList.size()-detailImageAdapter.getItemNum()/2))
                            detailRecycler.scrollToPosition(currentImageIndex + detailImageAdapter.getItemNum()/2);
                        //detailImageAdapter adapter = (detailImageAdapter) detailRecycler.getAdapter();
                        adapter.setCurrentIndex(currentImageIndex);

                    }
                } else if (Math.abs(up_y - down_y) > screenHeight / 4) {
                    Log.d(DEBUG_TAG, "distanceY"+Math.abs(up_y - down_y));
                    imageSwitcher.setAnimation(AnimationUtils.loadAnimation(this, R.anim.exit_down));
//                image_switcher.setImageResource(arrayPic[index]);
//                Intent intent=new Intent(MainActivity.this, ExitPageActivity.class);
//                startActivity(intent);
                    finish();
                }
                //return true;
            }
            return true;
        }





        return  super.onTouchEvent(event);
    }







    @Override
    public boolean onDown(MotionEvent e) {
        Log.d(DEBUG_TAG,"onDown: " + e.toString());
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


//        Log.d(DEBUG_TAG,"onScroll: " + "distanceX"+distanceX+"   distanceY"+distanceY);
//        if(distanceX>=(screenWidth/2))
//        {
//            ++currentImageIndex;
//            imageSwitcher.setImageURI(Uri.parse(imageInforArrayList.get(currentImageIndex).getImagePath()));
//        }

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
        //RecyclerView detailRecycler = findViewById(R.id.photo_thumbnail);
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

        //使缩略图隐形
        //RecyclerView detailRecycler = findViewById(R.id.photo_thumbnail);
        if(WidgetVisible == true && !isInView(detailRecycler,(int)e.getX(),(int)e.getY())&&!isInView(toolbar,(int)e.getX(),(int)e.getY())) {
            //detailRecycler.setAlpha(0);
            WidgetVisible = false;
            detailRecycler.setVisibility(View.INVISIBLE);

            toolbar.setVisibility(View.INVISIBLE);
        }
        else{
            //detailRecycler.setAlpha(1);
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
        //ImageView i = new ImageView(imageSwitcher.getContext());
        PhotoView i = new PhotoView(imageSwitcher.getContext());
        //PinchImageView i = new PinchImageView(this);
//        i.enable();
//        i.setScaleType(ImageView.ScaleType.CENTER);
        i.setLayoutParams(new ImageSwitcher.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));

        i.setImageURI(Uri.parse(imageInforArrayList.get(currentImageIndex).getImagePath()));
        //i.setAdjustViewBounds(true);
        //i.setScaleType(ImageView.ScaleType.FIT_XY);    //解决imageView 左右留有白边的问题
        //i.setMaximumScale(2.5f);
//        PhotoView photoView = new PhotoView(imageSwitcher.getContext());
//        photoView.getScale();

        return  i;
    }

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
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main,menu);
//        return true;
//    }


    private void initFruits()
    {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        imageInforArrayList = new ArrayList<>();

        final String[] columns = {MediaStore.Images.Media.DATA ,MediaStore.Images.Media._ID};
        final String orderBy = MediaStore.Images.Media.DATE_ADDED + " desc";  //倒叙查询
//Stores all the images from the gallery in Cursor
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy);
//Total number of images
        int count = cursor.getCount();

//Create an array to store path to all the images
        String[] arrPath = new String[count];

        for (int i = 0; i < count; i++) {
            cursor.moveToPosition(i);
            int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            int idColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            //Store the path of the image
            arrPath[i] = cursor.getString(dataColumnIndex);
            Uri fruitUri  = Uri.parse(arrPath[i]);
            String[] fruitInfor = arrPath[i].split("/");
            String fruitName = fruitInfor[fruitInfor.length-1];

            imageInfor fruitItem = new imageInfor(fruitName,arrPath[i],cursor.getLong(idColumnIndex));
            imageInforArrayList.add(fruitItem);
            Log.i("PATH", arrPath[i]);
        }
// The cursor should be freed up after use with close()
        cursor.close();


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initFruits();
        initDetailRecycler();
    }
}
