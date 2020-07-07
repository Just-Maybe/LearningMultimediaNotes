package com.example.videodemo;

import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.videodemo.videodecoder.VideoDecoderThread;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private static final String FILEPATH = Environment.getExternalStorageDirectory() + "/video.mp4";
    private VideoDecoderThread mVideoDecoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SurfaceView surfaceView = new SurfaceView(this);
        surfaceView.getHolder().addCallback(this);
        setContentView(surfaceView);

        mVideoDecoder = new VideoDecoderThread();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mVideoDecoder != null) {
            if (mVideoDecoder.init(holder.getSurface(), FILEPATH)) {
                mVideoDecoder.start();
            } else {
                mVideoDecoder = null;
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if(mVideoDecoder!=null){
            mVideoDecoder.close();
        }
    }
}
