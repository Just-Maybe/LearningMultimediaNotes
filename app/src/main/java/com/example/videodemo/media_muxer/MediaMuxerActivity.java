package com.example.videodemo.media_muxer;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.example.videodemo.BasePermissionsActivity;
import com.example.videodemo.R;

import java.io.IOException;

/**
 * Created by Miracle on 2020/7/12
 * Email: zhaoqirong96@gmail.com
 * Describe:
 */
public class MediaMuxerActivity extends BasePermissionsActivity implements View.OnClickListener, SurfaceHolder.Callback, Camera.PreviewCallback {
    Camera camera;
    SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;
    private Button btnStart, btnStop;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_muxer);
        initView();
    }

    private void initView() {
        surfaceView = findViewById(R.id.surface_view);
        btnStart = findViewById(R.id.btn_start_muxer);
        btnStart.setOnClickListener(this);
        btnStop = findViewById(R.id.btn_stop_muxer);
        btnStop.setOnClickListener(this);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_muxer:
                startCamera();
                MediaMuxerThread.startMuxer();
                break;
            case R.id.btn_stop_muxer:
                MediaMuxerThread.stopMuxer();
                stopCamera();
                break;
        }
    }

    /**
     * 打开摄像头
     */
    private void startCamera() {
        camera = Camera.open();
        camera.setDisplayOrientation(90);
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewFormat(ImageFormat.NV21);
        // 这个宽高的设置必须和后面编解码的设置一样，否则不能正常处理
        parameters.setPreviewSize(1920, 1080);
        camera.setParameters(parameters);
        camera.setPreviewCallback(this);
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭摄像头
     */
    private void stopCamera() {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.surfaceHolder = holder;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        MediaMuxerThread.stopMuxer();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        MediaMuxerThread.addVideoFrameData(data);
    }
}
