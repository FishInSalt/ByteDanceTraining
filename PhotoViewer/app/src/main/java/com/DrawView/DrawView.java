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

public class DrawView extends androidx.appcompat.widget.AppCompatImageView{
    float downX,downY;
    float upX,upY;
    //路径
    private Path path;
    //画笔
    private Paint paint = null;

    private boolean drawable ;



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
    private void  init(){
        drawable = false;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        path = new Path();



    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        this.cacheBitmap = Bitmap.createBitmap(bm.getWidth(),bm.getHeight(),bm.getConfig());
        this.cacheCanvas = new Canvas(cacheBitmap);
        cacheCanvas.drawBitmap(bm,0,0,paint);

        super.setImageBitmap(bm);
    }

    public void setDrawable(boolean drawable)
    {this.drawable = drawable;}

    public boolean isDrawable(){return drawable;}

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        if(cacheBitmap!=null)
//            canvas.drawBitmap(cacheBitmap,0,0,paint);
        if(path!=null&&cacheCanvas!=null) {
            cacheCanvas.drawPath(path, paint);
            cacheCanvas.save();
            cacheCanvas.restore();
            canvas.drawPath(path,paint);

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
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

    public Bitmap getCacheBitmap(){

        return cacheBitmap;}
    public void clear(){
        path.reset();
    }
}
