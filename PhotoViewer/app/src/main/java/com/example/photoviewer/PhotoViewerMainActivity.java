package com.example.photoviewer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
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
        setContentView(R.layout.thumbnail_layout);

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

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        //ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.MEDIA_CONTENT_CONTROL},1);
        //ListPhoto();
        initFruits();
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
    private void initFruits()
    {
        final String[] columns = {MediaStore.Images.Media.DATA ,MediaStore.Images.Media._ID};
        final String orderBy = MediaStore.Images.Media._ID;
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
//    public void ListPhoto()
//    {
//        if(ContextCompat.checkSelfPermission(PhotoViewerMainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE )
//                                                != PackageManager.PERMISSION_GRANTED)
//        {
//            Log.i("PATH","get photo false");
//        }
//        else {
//            Log.i("PATH","get photo true");
//            final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
//            final String orderBy = MediaStore.Images.Media._ID;
////Stores all the images from the gallery in Cursor
//            Cursor cursor = getContentResolver().query(
//                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
//                    null, orderBy);
////Total number of images
//            int count = cursor.getCount();
//
////Create an array to store path to all the images
//            String[] arrPath = new String[count];
//
//            for (int i = 0; i < count; i++) {
//                cursor.moveToPosition(i);
//                int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
//                //Store the path of the image
//                arrPath[i] = cursor.getString(dataColumnIndex);
//                Log.i("PATH", arrPath[i]);
//            }
//// The cursor should be freed up after use with close()
//            cursor.close();
//
//        }
//    }

}
