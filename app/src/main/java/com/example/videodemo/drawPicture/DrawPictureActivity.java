package com.example.videodemo.drawPicture;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.videodemo.R;

/**
 * Created by Miracle on 2020/7/8
 * Email: zhaoqirong96@gmail.com
 * Describe:
 */
public class DrawPictureActivity extends AppCompatActivity {
    private SurfaceView surfaceView;
    private ImageView imageView;
    private CustomView customView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_picture);
        initView();
    }

    private void initView() {
        surfaceView = findViewById(R.id.surface);
        imageView = findViewById(R.id.imageView);
        customView = findViewById(R.id.custom);

        surfaceViewDrawBitmap();
        ImageViewDrawBitmap();
    }


    private void surfaceViewDrawBitmap() {
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (holder == null) {
                    return;
                }
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setStyle(Paint.Style.STROKE);

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.timg);
                Canvas canvas = holder.lockCanvas();// 先锁定当前surfaceView的画布
                canvas.drawBitmap(bitmap, 0, 0, paint);//执行绘制操作
                holder.unlockCanvasAndPost(canvas);// 解除锁定并显示在界面上
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }

    private void ImageViewDrawBitmap() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.timg);
        imageView.setImageBitmap(bitmap);
    }
}
