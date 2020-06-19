package com.example.photoviewer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import com.example.ImageInfor.*;

import static android.media.ThumbnailUtils.createImageThumbnail;


public class PhotoViewerMainActivity extends AppCompatActivity {

    private ArrayList<imageInfor> imageInforArrayList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thumbnail_layout);      //缩略图页面


        //工具栏
        Toolbar toolbar = findViewById(R.id.main_tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //初始化图片链表，设置recyclerView
//        initImageList();
//        RecyclerView recyclerView =  findViewById(R.id.recycler_view);
//        StaggeredGridLayoutManager layoutManager = new
//                StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
//        recyclerView.setLayoutManager(layoutManager);
//        ImageAdapter adapter = new ImageAdapter(imageInforArrayList);
//        recyclerView.setAdapter(adapter);





    }

    @Override
    protected void onStart() {

        super.onStart();
        //初始化图片链表，设置recyclerView
        initImageList();
        RecyclerView recyclerView =  findViewById(R.id.recycler_view);
        StaggeredGridLayoutManager layoutManager = new
                StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        ImageAdapter adapter = new ImageAdapter(imageInforArrayList);
        recyclerView.setAdapter(adapter);
    }

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main,menu);
//        return true;
//    }
//    @Override
//    public  boolean onOptionsItemSelected(MenuItem item)
//    {
//        switch (item.getItemId())
//        {
//            case R.id.add_item:
//                Toast.makeText(this,"Click add",Toast.LENGTH_LONG).show();
//                break;
//            case R.id.remove_item:
//                Toast.makeText(this,"Click Remove",Toast.LENGTH_SHORT).show();
//                break;
//            default:
//                break;
//        }
//        return  true;
//    }
    private void initImageList()    //初始化图片链表
    {
        //权限
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        imageInforArrayList = new ArrayList<>();
        //查询手机资源，并按时间先后排序
        final String[] columns = {MediaStore.Images.Media.DATA ,MediaStore.Images.Media._ID};
        final String orderBy = MediaStore.Images.Media.DATE_ADDED + " desc";  //倒叙查询
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy);
        int count = cursor.getCount();

        String[] arrPath = new String[count];
        //讲图片资源信息放入链表
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

    public void waitForAccept(View view){
        Intent intent = new Intent(this, AcceptActivity.class);
        startActivity(intent);
    }

}
