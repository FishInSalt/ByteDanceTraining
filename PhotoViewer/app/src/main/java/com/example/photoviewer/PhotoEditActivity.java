package com.example.photoviewer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.DrawView.DrawView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class PhotoEditActivity extends AppCompatActivity  {


    private boolean photoRotated = false;

    DrawView editImageView;
    Bitmap bitmap;
    final static int rotateAngle = 90;
    final static int NumToSame = 360/rotateAngle;
    int rotateNum = 0;

    // 涂鸦的相关数据
    private boolean isDrawable = false;
    //public  Bitmap drawBitmap;
    private boolean haveDrawn = false;







    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_edit);
        Toolbar toolbar = findViewById(R.id.edit_tool_bar);


        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

        final AlertDialog.Builder saveConfirm = new AlertDialog.Builder(PhotoEditActivity.this);
        saveConfirm.setTitle("提示");
        saveConfirm.setMessage("图片尚未保存，确认保存？");
        saveConfirm.setNegativeButton("不保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_NEGATIVE:
                    case DialogInterface.BUTTON_NEUTRAL:
                        dialog.dismiss();
                        finish();
                        break;
                }
            }
        });
        saveConfirm.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {



                    case DialogInterface.BUTTON_POSITIVE:
                        if(haveDrawn ) {
                            Log.i("drawnPhoto","store drawnPhoto");
                            saveImageToGallery(PhotoEditActivity.this, editImageView.getCacheBitmap());
                        }
                        else if(photoRotated)
                            saveImageToGallery(PhotoEditActivity.this,bitmap);
                        photoRotated = false;
                        dialog.dismiss();
                        finish();
                        break;

                }
            }
        });


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!photoRotated&&!haveDrawn)
                finish();
                else
                {
                    final AlertDialog alertDialog = saveConfirm.create();
                    alertDialog.show();
                }
            }
        });

        Intent intent = getIntent();
        final String currentImagePath = intent.getStringExtra("photoPath");
         editImageView = findViewById(R.id.editImageView);
        final Button rotateBtn = findViewById(R.id.rotate_button);
        final TextView savedBtn = findViewById(R.id.save_btn);
        final Button drawBtn = findViewById(R.id.paint_button);
        editImageView.setImageURI(Uri.parse(currentImagePath));
        bitmap = BitmapFactory.decodeFile(currentImagePath);
        rotateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ++rotateNum;
                 // 旋转回原来需要的次数；


                Matrix matrix = new Matrix(); //旋转图片 动作
                matrix.setRotate(rotateAngle);//顺时针旋转角度
                if(bitmap==null)
                    bitmap = BitmapFactory.decodeFile(currentImagePath);

                int width = bitmap.getWidth();
                int height = bitmap.getHeight(); // 创建新的图片
                Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
                bitmap=resizedBitmap;
                editImageView.setImageBitmap(bitmap);

                if(rotateNum%NumToSame ==0) {
                    editImageView.setImageBitmap(bitmap);

                    photoRotated = false;
                }
                else {

                    photoRotated = true;
                }
            }
        });
        savedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(haveDrawn)
                {
                    Log.i("drawnPhoto","store drawnPhoto");
                    saveImageToGallery(PhotoEditActivity.this,editImageView.getCacheBitmap());
                    haveDrawn = false;

                }

                else if(rotateNum%NumToSame !=0 )
                {
                    saveImageToGallery(PhotoEditActivity.this,bitmap);
                    photoRotated = false;
                    rotateNum=0;
                }
            }
        });

        drawBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isDrawable)      //可涂鸦时按下会还原
                {
                    isDrawable= false;
                    haveDrawn = false;
                    drawBtn.setText("涂鸦");
                    editImageView.setDrawable(isDrawable);
                    editImageView.setImageBitmap(bitmap);
                    rotateBtn.setClickable(true);
                    editImageView.clear();



                }else{      // 不可涂鸦时按下会初始化涂鸦状态
                    isDrawable = true;
                    drawBtn.setText("取消涂鸦");
                    editImageView.setDrawable(isDrawable);
                    rotateBtn.setClickable(false);

                    editImageView.setImageBitmap(bitmap);


                }
            }
        });

        editImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(isDrawable)
                    haveDrawn = true;
                return false;
            }
        });




    }





    //@NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void saveImageToGallery(Context context, Bitmap bmp) {

        // 首先保存图片
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

        File appDir = new File(Environment.getExternalStorageDirectory(),"myphoto");
        if (!appDir.exists()) {
            Log.d("makedir", "making dir: "+appDir.getPath());
            boolean makeDirRes = appDir.mkdir();
            Log.d("makedir", "making dir result: "+makeDirRes);
        }

        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {

            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
//        try {
//
//            MediaStore.Images.Media.insertImage(context.getContentResolver(),
//                    file.getAbsolutePath(), fileName, null);
//        } catch (FileNotFoundException e) {
//
//            e.printStackTrace();
//        }


        // 最后通知图库更新
        // context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(file.getPath()))));
    }


//    private boolean isInView(View view,int x,int y)
//    {
//        if(view == null)
//            return false;
//        int[] location = new int[2];
//        view.getLocationOnScreen(location);
//        int left = location[0];
//        int top = location[1];
//        int right = left +view.getMeasuredWidth();
//        int bottom = top +view.getMeasuredHeight();
//        if(y>=top&&y<=bottom&&x>=left&&x<=right)
//            return true;
//
//        return false;
//    }


}
