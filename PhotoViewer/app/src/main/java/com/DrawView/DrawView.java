package com.DrawView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
/*
    自定义组件，一个保存涂鸦数据的ImageView
 */

public class DrawView extends androidx.appcompat.widget.AppCompatImageView{
    float downX,downY;
    float upX,upY;
    //路径
    private Path path;
    //画笔
    private Paint paint = null;

    private boolean drawable ;  // 设置组件是否可涂鸦



    //笔迹图像
    Bitmap cacheBitmap = null;
    //画布
    Canvas cacheCanvas = null;

    public DrawView(Context context)
    {
        this(context,null);

    }
    public DrawView(Context context, AttributeSet attrs)
    {
        this(context,attrs,0);
    }

    public DrawView(Context context, AttributeSet attrs,int defStyleAttr)
    {
        super(context,attrs,defStyleAttr);
        init();

    }

    //初始化组件
    private void  init(){
        drawable = false;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        path = new Path();



    }
    //重载setImageBitmap函数，调用时初始化成员cacheBitmap和cacheCanvas
    @Override
    public void setImageBitmap(Bitmap bm) {
        this.cacheBitmap = Bitmap.createBitmap(bm.getWidth(),bm.getHeight(),bm.getConfig());
        this.cacheCanvas = new Canvas(cacheBitmap);
        cacheCanvas.drawBitmap(bm,0,0,paint);

        super.setImageBitmap(bm);
    }
    //
    public void setDrawable(boolean drawable)
    {this.drawable = drawable;}

    public boolean isDrawable(){return drawable;}

    @Override
    protected void onDraw(Canvas canvas) { //该函数在其初始化和每次invalidate时调用
        super.onDraw(canvas);

        if(path!=null&&cacheCanvas!=null) {
            cacheCanvas.drawPath(path, paint);
            cacheCanvas.save();
            cacheCanvas.restore();
            canvas.drawPath(path,paint);

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) { //绘制涂鸦路径
        if (drawable) {


            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downX = event.getX();
                    downY = event.getY();
                    path.moveTo(downX, downY);
                    //invalidate();
                    break;


                case MotionEvent.ACTION_MOVE:
                    upX = event.getX();
                    upY = event.getY();
                    path.quadTo(downX, downY, upX, upY);
                    path.moveTo(upX,upY);
                    downX = upX;
                    downY = upY;
                    break;
                case MotionEvent.ACTION_UP:
                    //cacheCanvas.drawPath(path,paint);
                    //path.reset();
                default:
                    break;
            }
            invalidate();

            return true;
        }
        return false;
    }

    //返回绘制的涂鸦图片
    public Bitmap getCacheBitmap(){

        return cacheBitmap;}
    //清除绘画的路径
    public void clear(){
        path.reset();
    }
}
